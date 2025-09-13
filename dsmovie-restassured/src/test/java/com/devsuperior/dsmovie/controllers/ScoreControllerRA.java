package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ScoreControllerRA {

	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String clientToken, adminToken;

	private Map<String, Object> putScoreInstance;

	@BeforeEach
	void setUp() throws JSONException {
		baseURI = "http://localhost:8080";

		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";

		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);

		putScoreInstance = new HashMap<>();
		putScoreInstance.put("movieId", 2L);
		putScoreInstance.put("score", 4.0);
	}

	// deve retornar 404 quando id do filme não existir
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() {

		putScoreInstance.put("movieId", 100L);
		JSONObject score = new JSONObject(putScoreInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(score)
				.when()
				.put("/scores")
				.then()
				.statusCode(404)
				.body("error", equalTo("Recurso não encontrado"))
				.body("status", equalTo(404));
	}

	// deve retornar 422 quando id do filme não for informado
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() {

		putScoreInstance.put("movieId", null);
		JSONObject score = new JSONObject(putScoreInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(score)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.statusCode(422)
				.body("errors.fieldName[0]", equalTo("movieId"))
				.body("errors.message[0]", equalTo("Campo requerido"));
	}

	// deve retornar 422 quando valor do score for menor que zero
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() {

		putScoreInstance.put("score", -2.0);
		putScoreInstance.put("movieId", 2L);
		JSONObject score = new JSONObject(putScoreInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(score)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.statusCode(422)
				.body("errors.fieldName[0]", equalTo("score"))
				.body("errors.message[0]", equalTo("Valor mínimo 0"));
	}
}

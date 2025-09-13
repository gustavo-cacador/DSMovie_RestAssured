package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class MovieControllerRA {

	private String clientUsername, clientPassword, adminUsername, adminPassword, adminOnlyUsername, adminOnlyPassword;
	private String clientToken, adminToken, adminOnlyToken, invalidToken;
	private Long existingMovieId, nonExistingMovieId;
	private String movieTitle;

	private Map<String, Object> postProductInstance;

	@BeforeEach
	void setUp() throws JSONException {
		baseURI = "http://localhost:8080";

		existingMovieId = 1L;
		nonExistingMovieId = 100L;

		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		adminOnlyUsername = "ana@gmail.com";
		adminOnlyPassword = "123456";

		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		adminOnlyToken = TokenUtil.obtainAccessToken(adminOnlyUsername, adminOnlyPassword);
		invalidToken = adminToken + "xpto";

		movieTitle = "The Witcher";

		postProductInstance = new HashMap<>();
		postProductInstance.put("title", "Como treinar seu Dragão");
		postProductInstance.put("score", 8.2);
		postProductInstance.put("count", 8);
		postProductInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/vIgyYkXkg6NC2whRbYjBD7eb3Er.jpg");
	}

	// buscar todos deve retornar 200 ok qnd n passar nenhum parametro
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {

		given()
				.get("movies")
				.then()
				.statusCode(200)
				.body("content.id[0]", is(1))
				.body("content.title[0]", equalTo("The Witcher"))
				.body("content.score[0]", is(4.5F))
				.body("content.count[0]", is(2))
				.body("content.image[0]", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
	}

	// buscar todos deve retornar busca paginada 200 ok qnd parametro title for passado
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {

		movieTitle = "Venom: Tempo de Carnificina";

		given()
				.get("movies?title={movieTitle}", movieTitle)
				.then()
				.statusCode(200)
				.body("content.id[0]", is(2))
				.body("content.title[0]", equalTo("Venom: Tempo de Carnificina"))
				.body("content.score[0]", is(3.3F))
				.body("content.count[0]", is(3))
				.body("content.image[0]", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/vIgyYkXkg6NC2whRbYjBD7eb3Er.jpg"));
	}

	// busca por id retorna 200 ok qnd id do movie existir
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {

		existingMovieId = 3L;

		given()
				.get("movies/{id}", existingMovieId)
				.then()
				.statusCode(200)
				.body("id", is(3))
				.body("title", equalTo("O Espetacular Homem-Aranha 2: A Ameaça de Electro"))
				.body("score", is(0.0F))
				.body("count", is(0))
				.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/u7SeO6Y42P7VCTWLhpnL96cyOqd.jpg"));
	}

	// busca por id retorna notfound 404 qnd id do movie n existir
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {

		nonExistingMovieId = 100L;

		given()
				.get("movies/{id}", nonExistingMovieId)
				.then()
				.statusCode(404)
				.body("error", equalTo("Recurso não encontrado"))
				.body("status", equalTo(404));
	}

	// insert retorna unprocessableentity 422 ao tentar inserir um novo filme qnd estiver como admin mas title for vazio ou nulo
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() {

		postProductInstance.put("title", "   ");
		JSONObject newMovie = new JSONObject(postProductInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(newMovie)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("movies")
				.then()
				.statusCode(422)
				.body("errors.message", hasItems("Tamanho deve ser entre 5 e 80 caracteres", "Campo requerido"));
	}

	// insert retorna forbidden 403 ao tentar inserir um novo filme como cliente
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() {

		JSONObject newMovie = new JSONObject(postProductInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(newMovie)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("movies")
				.then()
				.statusCode(403);
	}

	// insert retorna unauthorized 401 ao tentar inserir um novo filme com token invalido
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() {

		JSONObject newMovie = new JSONObject(postProductInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + invalidToken)
				.body(newMovie)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("movies")
				.then()
				.statusCode(401);
	}
}

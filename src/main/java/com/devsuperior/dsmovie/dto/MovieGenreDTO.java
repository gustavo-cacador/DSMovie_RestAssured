package com.devsuperior.dsmovie.dto;

import com.devsuperior.dsmovie.entities.MovieEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MovieGenreDTO {

	private static final DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

	@Schema(description = "Database generated movie ID")
	private Long id;

	@NotBlank(message = "Campo requerido")
	@Size(min = 5, max = 80, message = "Tamanho deve ser entre 5 e 80 caracteres")
	@Schema(description = "Movie title")
	private String title;

	private Double score;

	private Integer count;

	private String image;

	private String genre;

	public MovieGenreDTO() {

	}

	public MovieGenreDTO(Long id, String title, Double score, Integer count, String image, String genre) {
		this.id = id;
		this.title = title;
		this.score = Double.valueOf(df.format(score));
		this.count = count;
		this.image = image;
		this.genre = genre;
	}

	public MovieGenreDTO(MovieEntity movie) {
		this(movie.getId(), movie.getTitle(), movie.getScore(), movie.getCount(), movie.getImage(), movie.getGenre().getName());
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Double getScore() {
		return score;
	}
	
	public Integer getCount() {
		return count;
	}

	public String getImage() {
		return image;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	@Override
	public String toString() {
		return "MovieDTO [id=" + id + ", title=" + title + ", score=" + score + ", count=" + count + ", image=" + image
				+ "]";
	}
}

package movies;

import java.io.InputStream;

import javax.ws.rs.core.Response;

import com.google.protobuf.util.JsonFormat;

import cinema.services.Cinema;
import cinema.services.CinemaResult;
import cinema.services.MovieArray;
import cinema.services.MovieEntity;
import hello.Hello;
import hello.Hello.HelloRequest;
import hello.Hello.HelloResponse;
import movies.Movies.Movie;
import movies.Movies.MovieId;
import movies.Movies.MovieList;
import movies.Movies.MovieIdList;
import movies.Movies.MovieList.Builder;

public class MovieWrapper implements IMovieWrapper {

	private static final boolean isTestlog = true;
	
	private void printDetailedLog(String text){
		if(isTestlog) System.out.println("- info: " + text);
	}
	
	public HelloResponse hello(HelloRequest request){
		try { 
			System.out.println("TEST METHOD IS WORKING");   
			// Deserializing the protobuf request:
			String name = request.getName(); 
			
			
			// Calculating the result:
			String result = "Hello from backend: " + name; 
			
			
			// Serializing the protobuf response:
			HelloResponse response = HelloResponse.newBuilder().setResult(result).build();
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} // Return server error if there was an error:
	}

	
	private MovieEntity convertMovieToEntity(Movies.Movie movie) {
		MovieEntity entity = new MovieEntity();
		entity.director = movie.getDirector();
		entity.title = movie.getTitle();
		entity.year = movie.getYear();
		entity.actor = new String[movie.getActorCount()];
		for (int i = 0; i < movie.getActorCount(); i++) {
			entity.actor[i] = movie.getActor(i);
		}
		return entity;
	}

	private Movies.Movie buildMovie(MovieEntity entity) {
		movies.Movies.Movie.Builder builder = Movies.Movie.getDefaultInstance().toBuilder();
		builder.setDirector(entity.director).setTitle(entity.title).setYear(entity.year);
		for (int i = 0; i < entity.actor.length; i++) {
			builder.addActor(entity.actor[i]);
		}
		return builder.build();
	}

	private Movies.MovieList buildMovieList(MovieArray list) {
		movies.Movies.MovieList.Builder builder = Movies.MovieList.getDefaultInstance().toBuilder();
		for (int i = 0; i < list.movie.size(); i++) {
			builder.addMovie(buildMovie(list.movie.get(i)));
		}
		return builder.build();
	}

	private Movies.MovieId buildMovieId(CinemaResult id) {
		movies.Movies.MovieId.Builder builder = Movies.MovieId.getDefaultInstance().toBuilder();
		builder.setId(id.id);
		return builder.build();
	}

	private MovieIdList buildMovieIdList(cinema.services.MovieIdList ids) {
		movies.Movies.MovieIdList.Builder builder = Movies.MovieIdList.getDefaultInstance().toBuilder();
		for (int i = 0; i < ids.id.size(); i++) {
			builder.addId(ids.id.get(i));
		}
		return builder.build();
	}

	@Override
	public MovieList getMovies() {
		System.out.println("Get Movies starts");
		try {
			// Calculating the result:
			Cinema movieHandler = new Cinema();
			MovieArray result = movieHandler.getMovies();

			// Serializing the protobuf response:
			MovieList response = buildMovieList(result);

			return response;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} // Return server error if there was an error:
		return null;
	}

	@Override
	public Movie getMovie(String id) {
		System.out.println("Get Movie starts");
		try {
			// Calculating the result:
			Cinema movieHandler = new Cinema();
			MovieEntity result = movieHandler.getMovie(id);
			printDetailedLog("Movie found");
			// Serializing the protobuf response:
			Movie response = buildMovie(result);
			printDetailedLog("Result created");
			
			return response;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} // Return server error if there was an error:
		return null;
	}

	@Override
	public MovieId addMovie(Movie movie) {
		System.out.println("Add Movie starts");
		try {			
			// Deserializing the protobuf request:
			MovieEntity entity = convertMovieToEntity(movie);
			printDetailedLog("Mesage converted");

			// Calculating the result:
			Cinema movieHandler = new Cinema();

			CinemaResult result = movieHandler.addMovie(entity);
			printDetailedLog("Movie added");

			// Serializing the protobuf response:
			Movies.MovieId response = buildMovieId(result);
			printDetailedLog("Result created");

			//JsonFormat.printer().print(response);

			return response;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@Override
	public void updateMovie(String id, Movie movie) {
		System.out.println("Update Movie starts");
		try {
			// Deserializing the protobuf request:
			MovieEntity entity = convertMovieToEntity(movie);
			printDetailedLog("Movie converted");		
			
			// Calculating the result:
			Cinema movieHandler = new Cinema();
			movieHandler.updateMovie(id, entity);
			printDetailedLog("Movie updated");
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} // Return server error if there was an error:
	}

	@Override
	public void deleteMovie(String id) {
		System.out.println("Delete Movie starts");
		try {
			// Calculating the result:
			Cinema movieHandler = new Cinema();
			movieHandler.deleteMovie(id);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} // Return server error if there was an error:
	}

	@Override
	public MovieIdList findMovie(String year, String field) {
		System.out.println("Find Movie starts");
		try {
			// Calculating the result:
			Cinema movieHandler = new Cinema();
			cinema.services.MovieIdList result = movieHandler.findMovie(year, field);
			printDetailedLog("Movies found");		

			// Serializing the protobuf response:
			MovieIdList response = buildMovieIdList(result);
			printDetailedLog("Movies converted");		

			return response;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} // Return server error if there was an error:
		return null;
	}
/*
	@Override
	public String hello(String name) {
		return name;
	}*/
}
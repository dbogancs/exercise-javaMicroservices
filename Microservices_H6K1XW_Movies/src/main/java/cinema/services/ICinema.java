package cinema.services;

/*
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType; 
*/
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

//@Path("MovieDatabase")
public abstract interface ICinema {	 
	 
	//@GET
	//@Path("movies")
	//@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public MovieArray getMovies();

	//@GET
	//@Path("movies/{id}")
	//@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public MovieEntity getMovie(@PathParam("id") String id);
	
	//@POST
	//@Path("movies")
	//@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@Consumes(MediaType.APPLICATION_JSON)
	public CinemaResult addMovie(MovieEntity movie);
	
	//@PUT
	//@Path("movies/{id}")
	//@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@Consumes(MediaType.APPLICATION_JSON)
	public void updateMovie(@PathParam("id") String id, MovieEntity movie);
	
	//@DELETE
	//@Path("movies/{id}")
	public void deleteMovie(@PathParam("id") String id);
	
	//@GET
	//@Path("movies/find")
	//@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public MovieIdList findMovie(@QueryParam("year") String year, @QueryParam("orderby") String field);
	
} 
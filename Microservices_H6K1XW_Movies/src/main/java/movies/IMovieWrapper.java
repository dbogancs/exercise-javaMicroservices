package movies;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import hello.Hello.HelloRequest;
import hello.Hello.HelloResponse;
import movies.Movies.Movie;
import movies.Movies.MovieId;
import movies.Movies.MovieIdList;
import movies.Movies.MovieList;


@Path("movies")
//@Consumes(MediaType.TEXT_PLAIN)
//@Produces(MediaType.TEXT_PLAIN)
//@Consumes("application/x-protobuf")
//@Produces("application/x-protobuf")
//@Consumes(MediaType.APPLICATION_JSON)
//@Produces(MediaType.APPLICATION_JSON)
@Consumes({MediaType.APPLICATION_JSON,"application/x-protobuf"})
@Produces({MediaType.APPLICATION_JSON,"application/x-protobuf"})
public interface IMovieWrapper {


	@POST
	@Path("test")
	public HelloResponse hello(HelloRequest movie);
	
	@GET
	public MovieList getMovies();

	@GET
	@Path("{id}")
	public Movie getMovie(@PathParam("id") String id);
	
	@POST
	//@Consumes(MediaType.APPLICATION_JSON)
	public MovieId addMovie(Movie movie);
	
	@PUT
	@Path("{id}")
	//@Consumes(MediaType.APPLICATION_JSON)
	public void updateMovie(@PathParam("id") String id, Movie movie);
	
	@DELETE
	@Path("{id}")
	public void deleteMovie(@PathParam("id") String id);
	
	@GET
	@Path("find")
	public MovieIdList findMovie(@QueryParam("year") String year, @QueryParam("orderby") String field);
	
}

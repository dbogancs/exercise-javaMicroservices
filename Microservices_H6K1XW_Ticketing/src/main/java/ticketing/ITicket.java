package ticketing;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ticketing.Ticketing.BuyTicketsRequest;
import ticketing.Ticketing.BuyTicketsResponse;
import ticketing.Ticketing.GetMoviesRequest;
import ticketing.Ticketing.GetMoviesResponse;
import ticketing.Ticketing.GetTicketsRequest;
import ticketing.Ticketing.GetTicketsResponse;

@Path("ticketing")
@Consumes({MediaType.APPLICATION_JSON, "application/x-protobuf"})
@Produces({MediaType.APPLICATION_JSON, "application/x-protobuf"})
public interface ITicket {

	@POST
	@Path("GetMovies")
	GetMoviesResponse GetMovies(GetMoviesRequest request); // int year

	@POST
	@Path("BuyTickets")
	BuyTicketsResponse BuyTickets(BuyTicketsRequest request); // int movieId, int count, string cardNumber

	@POST
	@Path("GetTickets")
	GetTicketsResponse GetTickets(GetTicketsRequest request);
}

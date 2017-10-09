package ticketing;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import banking.Banking;
import banking.Banking.ChargeCardRequest;
import banking.Banking.ChargeCardResponse;
import banking.IBank;
import movies.IMovieWrapper;
import movies.Movies;
import movies.Movies.Movie;
import movies.Movies.MovieIdList;
import movies.Movies.MovieList;
import ticketing.Ticketing.BuyTicketsRequest;
import ticketing.Ticketing.BuyTicketsResponse;
import ticketing.Ticketing.GetMoviesRequest;
import ticketing.Ticketing.GetMoviesResponse;
import ticketing.Ticketing.GetTicketsRequest;
import ticketing.Ticketing.GetTicketsResponse;

public class Ticket implements ITicket {

	private static String MovieAddress;
	private static String BankAddress;
	private static List<Ticketing.Ticket> tickets = new ArrayList<ticketing.Ticketing.Ticket>();
	private static final boolean isTestlog = true;
	


	private void printDetailedLog(String text){
		if(isTestlog) System.out.println("- info: " + text);
	}
	
	
	private static String getMovieAddress() {
		if (MovieAddress == null) {
			// Load the address of the backend service from the configuration
			// file:
			MovieAddress = System.getProperty("microservices.movies.url");
		}
		return MovieAddress;
	}

	private static String getBankAddress() {
		if (BankAddress == null) {
			// Load the address of the backend service from the configuration
			// file:
			BankAddress = System.getProperty("microservices.banking.url");
		}
		return BankAddress;
	}

	private IMovieWrapper getMovieBackend() {
		String movieAddress = Ticket.getMovieAddress();
		if (movieAddress == null)
			return null;
		try {
			// Create the Resteasy provider:
			ResteasyProviderFactory instance = ResteasyProviderFactory.getInstance();
			ResteasyClient client = new ResteasyClientBuilder().providerFactory(instance).build();
			ResteasyWebTarget target = client.target(getMovieAddress());
			// Get a typed interface:
			IMovieWrapper backend = target.proxy(IMovieWrapper.class);
			return backend;
		} catch (

		Exception ex)

		{
			ex.printStackTrace();
		}
		return null;
	}

	private IBank getBankBackend() {
		String movieAddress = Ticket.getMovieAddress();
		if (movieAddress == null)
			return null;
		try {
			// Create the Resteasy provider:
			ResteasyProviderFactory instance = ResteasyProviderFactory.getInstance();
			ResteasyClient client = new ResteasyClientBuilder().providerFactory(instance).build();
			ResteasyWebTarget target = client.target(getBankAddress());
			// Get a typed interface:
			IBank backend = target.proxy(IBank.class);
			return backend;
		} catch (

		Exception ex)

		{
			ex.printStackTrace();
		}
		return null;
	}

	private Ticketing.Movie convertMoviesMovieToTicketingMovie(int id, Movies.Movie movie) {
		Ticketing.Movie.Builder entity = Ticketing.Movie.getDefaultInstance().toBuilder();
		entity.setId(id).setTitle(movie.getTitle());
		return entity.build();
	}

	private Banking.ChargeCardRequest convertBuyToChagre(int price, BuyTicketsRequest buyInfo) {
		Banking.ChargeCardRequest.Builder entity = Banking.ChargeCardRequest.getDefaultInstance().toBuilder();
		entity.setAmount(buyInfo.getCount() * price);
		entity.setCardNumber(buyInfo.getCardNumber());
		return entity.build();
	}

	private BuyTicketsResponse convertChargeToSucces(ChargeCardResponse chargeInfo) {
		BuyTicketsResponse.Builder entity = BuyTicketsResponse.getDefaultInstance().toBuilder();
		entity.setSuccess(chargeInfo.getSuccess());
		return entity.build();
	}

	private GetTicketsResponse buildTickets(List<Ticketing.Ticket> tickets) {
		GetTicketsResponse.Builder list = GetTicketsResponse.getDefaultInstance().toBuilder();
		for (int i = 0; i < tickets.size(); i++) {
			if(tickets.get(i).getCount() > 0) list.addTicket(tickets.get(i));
		}
		return list.build();
	}

	@Override
	public GetMoviesResponse GetMovies(GetMoviesRequest request) {
		System.out.println("GetMovies starts");
		try {
			// Calculating the result:
			IMovieWrapper movieHandler = this.getMovieBackend();
			printDetailedLog("Backend loaded");
			if (movieHandler == null){
				printDetailedLog("...but Backend not found");
				return null;
			}
			// Call the backend service:
			MovieIdList response = movieHandler.findMovie(new Integer(request.getYear()).toString(), "Title");
			printDetailedLog("MovieIds found. Count: " + response.getIdCount());
			
			// Process the result:
			Ticketing.GetMoviesResponse.Builder movies = Ticketing.GetMoviesResponse.getDefaultInstance().toBuilder();
			for (int i = 0; i < response.getIdCount(); i++) {
				int id = response.getId(i);
				Movie resp = movieHandler.getMovie(new Integer(id).toString());
				Ticketing.Movie tMovie = convertMoviesMovieToTicketingMovie(id, resp);
				movies.addMovie(tMovie);
				printDetailedLog("Movie added to List: " + tMovie.getTitle());
			}

			// Serializing the protobuf response:
			Ticketing.GetMoviesResponse selectedMovies = movies.build();
			printDetailedLog("Movies collected. Count: " + selectedMovies.getMovieCount());
			
			return selectedMovies;
		} catch (Exception e) {
			e.printStackTrace();
		} // Return server error if there was an error:
		return null;
	}

	@Override
	public BuyTicketsResponse BuyTickets(BuyTicketsRequest request) {
		System.out.println("BuyTickets starts");
		boolean isNewTicketType = true;
		try {
			// Calculating the result:
			IBank bankHandler = this.getBankBackend();
			if (bankHandler == null)
				return null;
			ChargeCardRequest chargeInfo = convertBuyToChagre(10, request);
			printDetailedLog("Request converted");

			// Call the backend service:

			ChargeCardResponse result = bankHandler.ChargeCard(chargeInfo);
			printDetailedLog("Tickets bought: " + result.getSuccess());
			printDetailedLog("Ticket Type Count: " + tickets.size());
			// Process the result:
			if (result.getSuccess()) {
				for (int i = 0; i < tickets.size(); i++) {
					if (tickets.get(i).getMovieId() == request.getMovieId()) {
						isNewTicketType = false;
						Ticketing.Ticket updatedTicket = tickets.get(i).toBuilder()
								.setCount(tickets.get(i).getCount() + request.getCount()).build();
						tickets.set(i, updatedTicket);
						printDetailedLog("Ticket added to List. Count: " + tickets.get(i).getCount() + " Id: " + tickets.get(i).getMovieId());
					}
				}
				if(isNewTicketType) {
					tickets.add(ticketing.Ticketing.Ticket.newBuilder()
						.setCount(request.getCount())
						.setMovieId(request.getMovieId())
						.build());
					printDetailedLog("Ticket added to List. Count: " + tickets.get(tickets.size()-1).getCount() + " Id: " + tickets.get(tickets.size()-1).getMovieId());
				}
				Ticketing.Ticket.Builder ticket = Ticketing.Ticket.getDefaultInstance().toBuilder();
			}

			// Serializing the protobuf response:
			BuyTicketsResponse success = convertChargeToSucces(result);

			return success;
		} catch (Exception e) {
			e.printStackTrace();
		} // Return server error if there was an error:
		return null;
	}

	@Override
	public GetTicketsResponse GetTickets(GetTicketsRequest request) {
		System.out.println("GetTickets starts");
		try {
			GetTicketsResponse success = buildTickets(tickets);

			return success;
		} catch (Exception e) {
			e.printStackTrace();
		} // Return server error if there was an error:
		return null;
	}

}

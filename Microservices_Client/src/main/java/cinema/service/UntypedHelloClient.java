package cinema.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import com.google.protobuf.Message;

import banking.Banking.ChargeCardRequest;
import banking.Banking.ChargeCardResponse;
import hello.Hello;
import hello.Hello.HelloRequest;
import hello.Hello.HelloResponse;
import movies.Movies.Movie;
import movies.Movies.MovieId;
import movies.Movies.MovieIdList;
import ticketing.Ticketing.BuyTicketsRequest;
import ticketing.Ticketing.BuyTicketsResponse;
import ticketing.Ticketing.GetMoviesRequest;
import ticketing.Ticketing.GetMoviesResponse;
import ticketing.Ticketing.GetTicketsRequest;
import ticketing.Ticketing.GetTicketsRequestOrBuilder;
import ticketing.Ticketing.GetTicketsResponse;

public class UntypedHelloClient {

	private static final String post = "POST";
	private static final String put = "PUT";
	private static final String delete = "DELETE";
	private static final String get = "GET";

	private static void sendProto(HttpURLConnection urlc, Message msg) throws IOException {
		urlc.setDoInput(true);
		urlc.setDoOutput(true);
		if (msg != null)
			msg.writeTo(urlc.getOutputStream());
	}

	private static void sendJson(HttpURLConnection urlc, String msg) throws IOException {
		urlc.setDoInput(true);
		urlc.setDoOutput(true);
		if (msg != null)
			urlc.getOutputStream().write(msg.getBytes());
	}

	private static <T extends Message> T receiveProto(HttpURLConnection urlc, Class cls) throws IOException {
		try {
			Method parseFromMethod = cls.getMethod("parseFrom", InputStream.class);
			return (T) parseFromMethod.invoke(null, urlc.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String receiveJson(HttpURLConnection urlc) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(urlc.getInputStream(), writer, "UTF-8");
		return writer.toString();
	}

	private static <T extends Message> T callMethodPP(String method, String sUrl, Message msg, Class cls)
			throws IOException {
		URL url = new URL(sUrl);
		HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
		urlc.setRequestMethod(method);
		urlc.setRequestProperty("Accept", "application/x-protobuf");
		urlc.setRequestProperty("Content-Type", "application/x-protobuf");
		sendProto(urlc, msg);
		Message response = receiveProto(urlc, cls);
		return (T) response;
	}

	private static String callMethodPJ(String method, String sUrl, Message msg) throws IOException {
		URL url = new URL(sUrl);
		HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
		urlc.setRequestMethod(method);
		urlc.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
		urlc.setRequestProperty("Content-Type", "application/x-protobuf");
		sendProto(urlc, msg);
		return receiveJson(urlc);
	}

	private static <T extends Message> T callMethodJP(String method, String sUrl, String msg, Class cls)
			throws IOException {
		URL url = new URL(sUrl);
		HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
		urlc.setRequestMethod(method);
		urlc.setRequestProperty("Accept", "application/x-protobuf");
		urlc.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
		sendJson(urlc, msg);
		return (T) receiveProto(urlc, cls);
	}

	private static String callMethodJJ(String method, String sUrl, String msg) throws IOException {
		URL url = new URL(sUrl);
		HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
		urlc.setRequestMethod(method);
		urlc.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
		urlc.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
		sendJson(urlc, msg);
		return receiveJson(urlc);
	}

	public static void main(String[] args) {
		try {
			{
				HelloRequest req = HelloRequest.newBuilder().setName("Dani").build();
				HelloResponse response = null;
				String responseStr = null;
				String url = "http://localhost:8081/movies/test";
				String reqStr = "{\"name\":\"Dani\"}";

				response = callMethodPP(post, url, req, HelloResponse.class);
				System.out.println("Test1 - PP: " + response.getResult());

				responseStr = callMethodPJ(post, url, req);
				System.out.println("Test2 - PJ: " + responseStr);

				response = callMethodJP(post, url, reqStr, HelloResponse.class);
				System.out.println("Test3 - JP: " + response.getResult());

				responseStr = callMethodJJ(post, url, reqStr);
				System.out.println("Test4 - JJ: " + responseStr);
			}

			{
				String responseStr = null;
				String requestStr = null;
				String url = null;
				url = "http://localhost:8081/movies";

				Movie request1 = Movie.newBuilder().addActor("Dumb Actor 1").addActor("Dumb Actor 2")
						.setDirector("Dumb Director").setTitle("Dumb Title").setYear(2003).build();
				MovieId response1 = callMethodPP(post, url, request1, MovieId.class);
				System.out.println("POST Add Movie - id: " + response1.getId());

				Movie response2 = callMethodPP(get, url + "/" + new Integer(response1.getId()).toString(), null,
						Movie.class);
				System.out.println("GET Find Movie - id(" + response1.getId() + "): " + response2.getTitle());

				String response3 = callMethodPJ(get, url + "/" + new Integer(response1.getId()).toString(), null);
				System.out.println("GET Find Movie JSON - id(" + response1.getId() + "): " + response3);

				Movie request4 = Movie.newBuilder().addActor("Idiot Actor 1").addActor("Idiot Actor 2")
						.setDirector("Idiot Director").setTitle("Idiot Title").setYear(2003).build();
				MovieId response4 = callMethodPP(post, url, request4, MovieId.class);
				System.out.println("POST Add Movie - id: " + response4);

				String request5 = "{\"title\":\"Retard Title\",\"year\": 2003,\"director\":\"Retard Director\",\"actor\":[\"Retard Actor 1\",\"Retard Actor 2\"]}";
				String response5 = callMethodJJ(post, url, request5);
				System.out.println("POST Add Movie - id: " + response5);

				String response6 = callMethodPJ(get, url, null);
				System.out.println("GET All Movie JSON: " + response6);

				Movie response7 = callMethodPP(get, url + "/" + new Integer(response4.getId()).toString(), null,
						Movie.class);
				System.out.println("GET Movie - id(" + response4.getId() + "): " + response7.getTitle());

				String response8 = callMethodPJ(get, url + "/" + new Integer(response4.getId()).toString(), null);
				System.out.println("GET Movie JSON - id(" + response4.getId() + "): " + response8);

				Movie request9 = response7.toBuilder().setDirector("Very Idiot Director").setTitle("Idiot Title").build();
				String response9 = callMethodPJ(put, url + "/" + new Integer(response4.getId()).toString(), request9);
				String response10 = callMethodJJ(get, url + "/" + new Integer(response4.getId()).toString(), null);
				System.out.println("GET Find updated Movie - id(" + response4.getId() + "): " + response10);

				String request11 = "{\"title\":\"Moron Title\",\"year\": 2008,\"director\":\"Moron Director\",\"actor\":[\"Moron Actor 1\",\"Moron Actor 2\"]}";
				MovieId response11 = callMethodJP(post, url, request11, MovieId.class);
				System.out.println("POST Add Movie - id: " + response11);

				String response12 = callMethodJJ(get, url + "/" + new Integer(response11.getId()).toString(), null);
				System.out.println("GET Add Movie - id(" + response11.getId() + "): " + response12);

				String response13 = callMethodJJ(get,
						url + "/find?orderby=Director&year=2003", null);
				System.out.println("GET Find Movies 2003 - order by director: " + response13);
				
				String response14 = callMethodJJ(get,
						url + "/find?orderby=Title&year=2003", null);
				System.out.println("GET Find Movies 2003 - order by title: " + response14);

				String response15 = callMethodJJ(get,
						url + "/find?orderby=Director&year=2008", null);
				System.out.println("GET Find Movies 2008 - order by director: " + response15);
			
			}
			
			{
				String responseStr = null;
				String requestStr = null;
				String url = null;
				url = "http://localhost:8082/banking/ChargeCard";
				
				ChargeCardRequest request1 = ChargeCardRequest.newBuilder().setAmount(25).setCardNumber("even").build();
				ChargeCardResponse response1 = callMethodPP(post, url, request1, ChargeCardResponse.class);
				System.out.println("POST Charge Try: " + response1.getSuccess());

				ChargeCardRequest request2 = ChargeCardRequest.newBuilder().setAmount(10).setCardNumber("odd").build();
				ChargeCardResponse response2 = callMethodPP(post, url, request2, ChargeCardResponse.class);
				System.out.println("POST Charge Try: " + response2.getSuccess());

				ChargeCardRequest request3 = ChargeCardRequest.newBuilder().setAmount(-5).setCardNumber("even").build();
				ChargeCardResponse response3 = callMethodPP(post, url, request3, ChargeCardResponse.class);
				System.out.println("POST Charge Try: " + response3.getSuccess());
			}
			
			{
				String responseStr = null;
				String requestStr = null;
				String url = null;
				url = "http://localhost:8080/ticketing/GetMovies";

				GetMoviesRequest request1 = GetMoviesRequest.newBuilder().setYear(2003).build();
				String response1 = callMethodPJ(post, url, request1);
				System.out.println("POST Get Movies 2003: " + response1);

				url = "http://localhost:8080/ticketing/BuyTickets";
				
				BuyTicketsRequest request2 = BuyTicketsRequest.newBuilder().setCardNumber("even").setCount(10).setMovieId(0).build();
				BuyTicketsResponse response2 = callMethodPP(post, url, request2, BuyTicketsResponse.class);
				System.out.println("POST Buy Tickets - id(0): " + response2.getSuccess());

				BuyTicketsRequest request3 = BuyTicketsRequest.newBuilder().setCardNumber("even").setCount(20).setMovieId(2).build();
				BuyTicketsResponse response3 = callMethodPP(post, url, request3, BuyTicketsResponse.class);
				System.out.println("POST Buy Tickets - id(2): " + response3.getSuccess());

				BuyTicketsRequest request4 = BuyTicketsRequest.newBuilder().setCardNumber("even").setCount(20).setMovieId(2).build();
				BuyTicketsResponse response4 = callMethodPP(post, url, request4, BuyTicketsResponse.class);
				System.out.println("POST Buy Tickets - id(2): " + response4.getSuccess());

				BuyTicketsRequest request5 = BuyTicketsRequest.newBuilder().setCardNumber("even").setCount(-10).setMovieId(1).build();
				BuyTicketsResponse response5 = callMethodPP(post, url, request5, BuyTicketsResponse.class);
				System.out.println("POST Buy Tickets - id(1): " + response5.getSuccess());

				url = "http://localhost:8080/ticketing/GetTickets";
				
				GetTicketsRequest request7 = GetTicketsRequest.newBuilder().build();
				String response7 = callMethodPJ(post, url, request7);
				System.out.println("POST All bought Tickets: " + response7);

				GetTicketsRequest request8 = GetTicketsRequest.newBuilder().build();
				GetTicketsResponse response8 = callMethodPP(post, url, request8, GetTicketsResponse.class);
				System.out.println("POST First Ticket id: " + response8.getTicket(0).getMovieId());

			}

			/*
			// id movie
			response = movie.path("0").request().get();
			result = response.readEntity(String.class);
			System.out.println("GET movie: " + result);
			
			
			// add movie
			response = movie.request()
					.post(Entity.json("{\"title\":\"A Valami cim\",\"year\":2005,\"director\":\"Z Valami rendez�\"}"));
			result = response.readEntity(String.class);
			System.out.println("POST add: " + result);
			
			// add movie
			response = movie.request()
					.post(Entity.json("{\"title\":\"Valami cim\",\"year\":2030,\"director\":\"Valami rendez�\"}"));
			result = response.readEntity(String.class);
			System.out.println("POST add: " + result);
			
			// add movie
			response = movie.request()
					.post(Entity.json("{\"title\":\"A Valami cim\",\"year\":2005,\"director\":\"Z Valami rendez�\"}"));
			result = response.readEntity(String.class);
			System.out.println("POST add: " + result);
			
			// add movie
			response = movie.request()
					.post(Entity.json("{\"title\":\"Z Valami cim\",\"year\":2005,\"director\":\"A Valami rendez�\"}"));
			result = response.readEntity(String.class);
			System.out.println("POST add: " + result);
			
			// add movie
			response = movie.request()
					.post(Entity.json("{\"title\":\"A Valami cim\",\"year\":2003,\"director\":\"Z Valami rendez�\"}"));
			result = response.readEntity(String.class);
			System.out.println("POST add: " + result);
			
			// add movie
			response = movie.request()
					.post(Entity.json("{\"title\":\"Z Valami cim\",\"year\":2003,\"director\":\"A Valami rendez�\"}"));
			result = response.readEntity(String.class);
			System.out.println("POST add: " + result);
			
			// add movie
			response = movie.request().post(Entity.json(
					"{\"title\":\"Batman Begins\",\"year\":2005,\"director\":\"Cristopher Nolan\",\"actor\":[\"Christian Bale\",\"Michael Caine\"]}"));
			result = response.readEntity(String.class);
			System.out.println("POST add: " + result);
			
			// add movie
			response = movie.request().post(Entity.json(
					"{\"title\":\"Batman Begins\",\"year\":2005,\"director\":\"Cristopher Nolan\",\"actor\":[\"Christian Bale\",\"Michael Caine\"]}"));
			result = response.readEntity(String.class);
			System.out.println("POST add: " + result);
			
			// add movie
			response = movie.request().post(Entity.json(
					"{\"title\":\"Batman Begins\",\"year\":2005,\"director\":\"Cristopher Nolan\",\"actor\":[\"Christian Bale\",\"Michael Caine\"]}"));
			result = response.readEntity(String.class);
			System.out.println("POST add: " + result);
			
			// all movie
			response = movie.request().get();
			result = response.readEntity(String.class);
			System.out.println("GET all movie: " + result);
			
			// id movie
			response = movie.path("1").request().get();
			result = response.readEntity(String.class);
			System.out.println("GET movie: " + result);
			
			// update movie
			response = movie.path("1").request().put(Entity.json(
					"{\"title\":\"Pirates of the Caribbean: The Curse of the Black Pearl\",\"year\":2003,\"director\":\"Gore Verbinski\"}"));
			result = response.readEntity(String.class);
			System.out.println("PUT update: " + result);
			
			// id movie
			response = movie.path("1").request().get();
			result = response.readEntity(String.class);
			System.out.println("GET movie: " + result);
			
			// update movie
			response = movie.path("1").request().put(Entity.json(
					"{\"title\":\"Pirates of the Caribbean: The Curse of the Black Pearl\",\"year\":2003,\"director\":\"Gore Verbinski\",\"actor\":[\"Christian Bale\",\"Michael Caine\"]}"));
			result = response.readEntity(String.class);
			System.out.println("PUT update: " + result);
			
			// id movie
			response = movie.path("1").request().get();
			result = response.readEntity(String.class);
			System.out.println("GET movie: " + result);
			
			// delete movie
			response = movie.path("0").request().delete();
			result = response.readEntity(String.class);
			System.out.println("DELETE movie: " + result);
			
			// all movie
			response = movie.request().get();
			result = response.readEntity(String.class);
			System.out.println("GET all movie: " + result);
			
			// find movie
			response = movie.path("find").queryParam("year", "2003").queryParam("orderby", "Title").request().get();
			result = response.readEntity(String.class);
			System.out.println("GET find: " + result);
			
			// find movie
			response = movie.path("find").queryParam("year", "2003").queryParam("orderby", "Director").request().get();
			result = response.readEntity(String.class);
			System.out.println("GET find: " + result);
			
			// find movie
			response = movie.path("find").queryParam("year", "2005").queryParam("orderby", "Title").request().get();
			result = response.readEntity(String.class);
			System.out.println("GET find: " + result);
			
			// find movie
			response = movie.path("find").queryParam("year", "2005").queryParam("orderby", "Director").request().get();
			result = response.readEntity(String.class);
			System.out.println("GET find: " + result);
			// response.close();
			
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
package cinema.services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;

public class Cinema implements ICinema {

	private static ArrayList<MovieEntity> movies = new ArrayList<MovieEntity>();
	private static Integer idCounter = 0;

	public Cinema() {
	}

	@Override
	public MovieArray getMovies() {
		return new MovieArray(movies);
	}

	@Override
	public MovieEntity getMovie(String id) {
		int movieId = Integer.parseInt(id);
		for (int i = 0; i < movies.size(); i++) {
			MovieEntity movie = movies.get(i);
			if (movie.id == movieId)
				return movie;
		}
		throw new NotFoundException();
	}

	@Override
	public CinemaResult addMovie(MovieEntity movie) {
		MovieEntity newMovie = new MovieEntity();
		newMovie.actor = movie.actor;
		newMovie.director = movie.director;
		newMovie.title = movie.title;
		newMovie.year = movie.year;
		newMovie.id = idCounter++;
		movies.add(newMovie);
		return new CinemaResult(newMovie.id);
	}

	@Override
	public void updateMovie(String id, MovieEntity movie) {
		movie.id = Integer.parseInt(id);
		MovieEntity oldMovie = null;
		try {
			oldMovie = getMovie(id);
		} catch (NotFoundException e) {
			movies.add(movie);
		}
		oldMovie.actor = movie.actor;
		oldMovie.director = movie.director;
		oldMovie.title = movie.title;
		oldMovie.year = movie.year;

	}

	@Override
	public void deleteMovie(String id) {
		int movieId = Integer.parseInt(id);
		for (int i = 0; i < movies.size(); i++) {
			MovieEntity movie = movies.get(i);
			if (movie.id == movieId)
				movies.remove(i);
		}
	}

	@Override
	public MovieIdList findMovie(String year, String field) {
		List<Integer> sortedMovieIds = new ArrayList<Integer>();
		List<MovieEntity> restMovies = new ArrayList<MovieEntity>();
		restMovies.addAll(movies);

		MovieEntity selectedMovie = null;
		MovieEntity movie = null;
		int selectedIndex;

		while (restMovies.size() > 0) {
			selectedIndex = -1;

			for (int i = 0; i < restMovies.size(); i++) {
				movie = restMovies.get(i);

				switch (field) {
				case "Title":
					if (selectedMovie == null || selectedMovie.title.compareTo(movie.title) >= 0)
						selectedIndex = i;
					break;

				case "Director":
					if (selectedMovie == null || selectedMovie.director.compareTo(movie.director) >= 0)
						selectedIndex = i;
					break;

				default:
					return null;
				}

				if (selectedIndex >= 0)
					selectedMovie = restMovies.get(selectedIndex);
				else
					selectedMovie = restMovies.get(0);
			}

			if (selectedMovie.year == Integer.parseInt(year))
				sortedMovieIds.add(selectedMovie.id);
			restMovies.remove(selectedIndex);
			selectedMovie = null;
		}

		return new MovieIdList(sortedMovieIds);
	}

}

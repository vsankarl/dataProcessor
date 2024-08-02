package com.demo.matcher;
import com.demo.matcher.beans.Movie;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovieDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieDatabase.class);

    // Fundamental assumption - a movie is always with a title. 
    // So title is used as an index.
    // Also, since a movie can have the same title released on
    // same or different years we store value as a list of movie objects.
    private Map<String, List<Movie>> movieDb = new HashMap<>();

    public void addMovie(Movie movie) {
        String title = movie.getTitle();
        List<Movie> movieList = movieDb.get(title);
        if (movieList != null) {
            for (Movie m : movieList) {
                if (m != null && m.equals(movie)) {
                    LOGGER.info("Duplicate movie found, skip {}", title);
                    return;
                }
            }
        }
        // no duplicate found
        movieDb.computeIfAbsent(title, k -> new ArrayList<>()).add(movie);
    }

    public List<Movie> getMoviesByTitle(String title) {
        return movieDb.getOrDefault(title, Collections.emptyList());
    }

    public List<Movie> getMoviesByTitleAndYear(String title, int year) {
        List<Movie> movieList = movieDb.get(title);
        List<Movie> movieResult = new ArrayList<>();
        if (movieList == null) {
            return movieResult;
        }

        for (Movie m: movieList) {
            if (m != null && m.getYear() == year) {
                movieResult.add(m);
            }
        }
        return movieResult;
    }

    public List<Movie> getMoviesByTitleAndNames(String title, Multimap<String, Movie.Role> names) {
        List<Movie> movieList = movieDb.get(title);
        List<Movie> movieResult = new ArrayList<>();
        if (movieList == null) {
            return movieResult;
        }

        for (Movie m : movieList) {
            if (checkForPlayersInMovie(m, names)) {
                    movieResult.add(m);
             }
        }
        return movieResult;
    }

    private Boolean checkForPlayersInMovie(Movie movie, Multimap<String, Movie.Role> queryCast) {
        Multimap<String, Movie.Role> dbMoviePlayers = movie.getPlayers(); 
        for (String s : queryCast.keySet()) {
            for (Movie.Role r : queryCast.get(s)) {
                if (dbMoviePlayers.containsEntry(s, r) == false) {
                   // Commenting out for performance
                   // LOGGER.debug("NF - Id {} db->movie->cast-role {}   q->cast {} q->role {}", 
                   //             movie.getId(), dbMoviePlayers.entries().toString(), s, r.toString());
                   return false;
                }
            }
        }
        return true;
    }

    public List<Movie> getMoviesByTitleAndYearAndNames(String title, int year, 
                                                       Multimap<String, Movie.Role> names) {
        List<Movie> movieList = movieDb.get(title);
        List<Movie> movieResult = new ArrayList<>();
        if (movieList == null) {
            return movieResult;
        }

        for (Movie m: movieList) {
            if (m != null && m.getYear() == year && 
                checkForPlayersInMovie(m, names)) {
                    movieResult.add(m);
            }
        }
        return movieResult;
    }
}

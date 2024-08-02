package com.demo.matcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.matcher.beans.Movie;

public class MatcherImpl implements Matcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(MatcherImpl.class);

  private MovieDatabase movieDatabase = new MovieDatabase();

  private Map<Integer, Movie> iterateActorAndDirectorStream(CsvStream actorAndDirectorStreamDb) {

    Map<Integer, Movie> tempMovieDb = new HashMap<>();

    Stream<String> castInfoStream = actorAndDirectorStreamDb.getDataRows();
    castInfoStream.forEach(row -> {
        String[] parts = Util.tokenize(row);
        int id = Util.getIntOrDefault(parts[0], -1);
        String player = parts[1].replace("\"", "").trim();
        String strRole = parts[2].toUpperCase().replace(" ", "_");
        Movie.Role role = Movie.Role.INVALID;
        if (Movie.Role.isStringInEnum(strRole)) {
           role = Movie.Role.valueOf(strRole);
        }
        Movie movie = tempMovieDb.get(id);
        if (movie != null) {
          movie.updatePlayerAndRole(player, role);
        } else {
          movie = new Movie(id, player, role);
          tempMovieDb.put(id, movie);
        }
    });
    return tempMovieDb;
  }

  private void iterateMovieStream(CsvStream movieStreamDb, Map<Integer, Movie> tempMovieDb) {
      Stream<String> movieInfoStream = movieStreamDb.getDataRows();
      movieInfoStream.forEach(row -> {
        String[] parts = Util.tokenize(row);
        int id = Integer.parseInt(parts[0]);
        String title = parts[1].replace("\"", "");
        int year = Util.getIntOrDefault(parts[2], -1);
        Movie movie = tempMovieDb.get(id);

        if (movie != null) {
          movie.updateTitleAndYear(title, year);
        } else {
          movie = new Movie(id, title, year);
        }
        movieDatabase.addMovie(movie);
        // optimize the memory footprint as we process the record.
        tempMovieDb.remove(id);
       });
  }

  public MatcherImpl(CsvStream movieStreamDb, CsvStream actorAndDirectorStreamDb) {
    LOGGER.info("importing database");
    // Overview : Index movies based on title. 
    // Using actorAndDirectorStreamDb, partially(only some fields are available)
    // create the movie object, key it based of id and store it tempMovieDb.
    // Using id from movieStreamDb find the partial movie object from tempMovieDb,
    // update the movie object with rest of the field. Now
    // the movie object is complete with all the fields, add it to the
    // movieDatabase indexed on title.
    // Use the id to remove the record from the tempMovieDb for optimizing memory.
    try {
      Map<Integer, Movie> tempMovieDb = iterateActorAndDirectorStream(actorAndDirectorStreamDb);
      iterateMovieStream(movieStreamDb, tempMovieDb);
    } catch (Exception e) {
      LOGGER.error("Exception in importing database {}", e);  
      throw e ;
    }
    LOGGER.info("database imported");
  }

  @Override
  public List<IdMapping> match(DatabaseType databaseType, CsvStream externalDb) {
    try {
      MovieDataFeed dataFeed = MovieDataFeedFactory.createMovieDataFeed(databaseType);
      return dataFeed.match(movieDatabase, externalDb);
    } catch(Exception e) {
      LOGGER.error("Exception occurred {}", e);
    }
    return null;
  }
}
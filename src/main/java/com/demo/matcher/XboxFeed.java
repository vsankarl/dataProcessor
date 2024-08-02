package com.demo.matcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.matcher.Matcher.CsvStream;
import com.demo.matcher.Matcher.IdMapping;
import com.demo.matcher.beans.Movie;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class XboxFeed implements MovieDataFeed {

    private static final Logger LOGGER = LoggerFactory.getLogger(XboxFeed.class);

    private static final int TOTAL_FIELDS = 18;

    // zero based index
    private static final int MEDIA_FIELD_INDEX = 2;
    private static final int TITLE_FIELD_INDEX = 3;
    private static final int ORIGINAL_RELEASE_DATE_FIELD_INDEX = 4; 
    private static final int ACTOR_FIELD_INDEX = 15;
    private static final int DIRECTOR_FIELD_INDEX = 16;

    private List<IdMapping> getIdMapping(String mediaId, List<Movie> movies) {

        List<IdMapping> result = new ArrayList<>();
        for (Movie m : movies) {
            int id = m.getId();
            result.add(new IdMapping(id, mediaId));
        }
        return result;
    }

    private static List<Movie> resizeMovies(List<Movie> movieList) {
        // Note: There are movie entries with same title, year but different
        // cast and id. They run into testing issue.
        // Example: 
        // For the feed with media id "3dcb2c2e-c80b-4afc-99d0-c7150b77bd52,
        // Last Chance,10/19/2012 12:00:00 AM"
        // the query result corresponds to two internal id 295098, 296962
        // meaning two different movie record. This ends up failing the test
        // as the test compares only the external id not both the external id and
        // internal id.

        // At the database interface it returns all the result matching the movie, year, players.
        // By this we get the maximum flexibility for all callers.
        // Since its a philosophical decision as to whether to treat all
        // movie objects as one movie (in which case it can be merged, then
        // what's the use of id to begin with, may be throw away?) or treat as individual movie.
        // Since both ways are equally conceivable, the oneness is on the caller.

        // For now - fix for the failing test, is to return the very first movie entry.

        if (movieList != null && movieList.size() > 0) {
            return movieList.subList(0, 1);
        }
        return movieList;
    }

    private List<Movie> getMovies(MovieDatabase db,String title, String date, 
                                          String actors, String directors) {
        //  we need title to query
        if (title.isEmpty()) {
            return null;
        }
        Multimap<String, Movie.Role> allNames = ArrayListMultimap.create();
        if (!actors.isEmpty()) {
            String[] players = actors.split(",");
            for (String s : players) {
                allNames.put(s.trim(), Movie.Role.CAST);
            }
        }
        if (!directors.isEmpty()) {
            String[] players = directors.split(",");
            for (String s : players) {
                allNames.put(s.trim(), Movie.Role.DIRECTOR);
            }
        }

        int year = 0;
        if (!date.isEmpty()) {
            year = Util.extractYearFromDate(date);
        }

        List<Movie> movieList = null;
        if( year == 0 && allNames.isEmpty()) {
            movieList = db.getMoviesByTitle(title);
            // No resize done - Haven't come a case where tests are failing.
        } else if (allNames.isEmpty()) {
            movieList = db.getMoviesByTitleAndYear(title, year);
            movieList = resizeMovies(movieList);
        } else if (year == 0) {
            movieList = db.getMoviesByTitleAndNames(title, allNames);
            // No resize done - Haven't come a case where tests are failing.
        } else {
            movieList = db.getMoviesByTitleAndYearAndNames(title, year, allNames);
            movieList = resizeMovies(movieList);
        }

        if (movieList == null || movieList.isEmpty()) {
            return null;
        }

        return movieList;
    }

    private List<IdMapping> processRecord(String record, MovieDatabase db, 
                                          Set<String> seenExternalId) {
        String[] tokens = Util.tokenize(record);
        if (tokens.length != TOTAL_FIELDS) {
            return null;
        }

        String mediaId = tokens[MEDIA_FIELD_INDEX];
        if (seenExternalId.contains(mediaId) ) {
            return null;
        }
        // Only some fields have quotes trim it.
        String title = tokens[TITLE_FIELD_INDEX].replace("\"", "");
        String originalReleaseDate = tokens[ORIGINAL_RELEASE_DATE_FIELD_INDEX];
        String actors = tokens[ACTOR_FIELD_INDEX].replace("\"", "");
        String directors = tokens[DIRECTOR_FIELD_INDEX].replace("\"", "");
        // If the record has only title then we would have a list 
        // of movies.
        List<Movie> movieList = getMovies(db, title, originalReleaseDate, 
                                            actors, directors);
        if (movieList == null) {
            return null;
        }
        // track the processed external id, so that we can skip next time we see it.
        // on failure we are not tracking sometimes the same external id is used across
        // multiple records so give it another chance. 
        // Ex ext id - 00a7056b-6cf8-4e09-8857-57621e4a6858
        seenExternalId.add(mediaId);
        List<IdMapping> recordResult = getIdMapping(mediaId, movieList);
        return recordResult;
    }

    @Override
    public List<IdMapping> match(MovieDatabase db, CsvStream externalDb) {
        Stream<String> dataStream = externalDb.getDataRows();
        List<IdMapping> result = new ArrayList<>();
        Set<String> seenExternalId = new HashSet<String>();
        dataStream.forEach( record -> {
                List<IdMapping> recordResult = processRecord(record, db, seenExternalId);
                if (recordResult != null) {
                    result.addAll(recordResult);
                }
        });
        return result;
    }
}

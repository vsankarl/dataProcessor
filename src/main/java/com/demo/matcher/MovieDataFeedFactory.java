package com.demo.matcher;

import com.demo.matcher.Matcher.DatabaseType;

public class MovieDataFeedFactory {
      public static MovieDataFeed createMovieDataFeed(DatabaseType databaseType) {
        switch (databaseType) {
            case XBOX:
                return new XboxFeed();
            case GOOGLE_PLAY:
                return new GooglePlayFeed();
            case VUDU:
                return new VuduFeed();
            case AMAZON_INSTANT:
                return new AmazonInstantFeed();
            default:
                throw new IllegalArgumentException("Unsupported DatabaseType: " + databaseType);
        }
    } 
}

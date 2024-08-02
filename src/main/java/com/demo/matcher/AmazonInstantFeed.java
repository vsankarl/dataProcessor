package com.demo.matcher;

import java.util.List;

import com.demo.matcher.Matcher.CsvStream;
import com.demo.matcher.Matcher.IdMapping;

public class AmazonInstantFeed implements MovieDataFeed {

    @Override
    public List<IdMapping> match(MovieDatabase db, CsvStream externalDb) {
       return null;
    }
}
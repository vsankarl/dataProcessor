package com.demo.matcher;

import java.util.List;

import com.demo.matcher.Matcher.CsvStream;
import com.demo.matcher.Matcher.IdMapping;
public interface MovieDataFeed {

    public List<IdMapping> match(MovieDatabase db, CsvStream externalDb);

}

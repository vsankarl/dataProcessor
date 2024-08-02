## Goal

The objective of this project is to showcase data processing mechanism using streams.

In this exercise, we use a movie database and various provider feeds. Providers can include any number of services, such as Google Play, VUDU, and Amazon Instant. The movie database is stored in CSV files: movies.csv and actors_and_directors.csv.

The aim is to develop a matching algorithm that takes the provider feed (e.g., xbox_feed.csv) and matches the records with attributes in the movie database.

The results of the matching process are saved in output/results/com.demo.matcher.MatcherImplTest.txt. These results are generated during test phase.

## Provider feeds

Provider feeds include information about the availability of a given movie or TV show. They typically contain metadata such as the title, director, and release year, along with availability information like price and the fulfillment URL. Different feeds may include varying types of metadata, leading to inconsistencies between data sources.

## Data Files ( found in test/resources )

**movies.csv** : a comma-separated file of around 200,000 movies, with the following schema:

| id | title | year |
| ------------ | ----- | ---- |
| 1            | Finding Nemo | 2006 |

**actors_and_directors.csv**: a comma-separated file of actors and directors associated with each movie. It has the following schema:

| movie_id | name | role |
| ------------ | ---- | -------- |
| 1            | Leonardo DiCaprio | cast |
| 2            | Martin Scorsese | director|

**xbox.csv**: a comma-separated file of the official provider feed for Xbox. It has the following schema:

| MediaId | Title | OriginalReleaseDate | MediaType | Actors | Director | XboxLiveURL |
| ------- | ----- | ------------------- | --------- | ------ | -------- | ----------- |
| 531b964f-0cb9-4968-9b77-e547f2435225| Furious 7 | 4/13/2015 | Movie | Vin Diesel, Paul Walker, Jason Statham | James Wan | video.xbox.com  

# How to run?
   < Some directory >\dataProcessor> mvn clean
   
   < Some directory >\dataProcessor> mvn package
   
   See the results under < Some directory >\dataProcessor\output\test\com.demo.matcher.MatcherImplTest-output.txt


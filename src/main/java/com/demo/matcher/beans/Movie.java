package com.demo.matcher.beans;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


public class Movie {

    public enum Role {
        INVALID,
        CAST, 
        DIRECTOR, 
        WRITER,
        SCREENWRITER, 
        EDITOR,
        PRODUCER, 
        COMPOSER,
        EXECUTIVE_PRODUCER,
        BASED_ON_A_STORY_BY; // Note: this doesn't fit the general roles in the movie industry
                             // having it to make it for easy handling

        public static boolean isStringInEnum(String str) {
            for (Role role : Role.values()) {
                if (role.name().equals(str)) {
                    return true;
                }
            }
            return false;
        }
    }

    private int id;
    private String title;
    private int year;
    // An artist can be both as cast and a director, hence storing
    // it as multimap.
    private Multimap<String, Role> players;

    public Movie(int id, String player, Role role) {
        this.id = id;
        this.title = "";
        this.year = 0;
        this.players = ArrayListMultimap.create();;
        this.players.put(player, role);
    }

    public Movie(int id, String title, int year) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.players = ArrayListMultimap.create();;
    }

    public void updateTitleAndYear(String title, int year) {
        this.title = title;
        this.year = year;
    }

    public void updatePlayerAndRole(String player, Role role) {
        this.players.put(player, role);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public Multimap<String, Role> getPlayers() {
        return players;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Movie other = (Movie) obj;
        // Do a partial comparison of checking the id, year and title
        // to optimize the performance
        return id == other.id && year == other.year && 
               (title == null ? other.title == null : title.equals(other.title));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Movie ID: ").append(id).append(",");
        sb.append("Title: ").append(title).append(",");
        sb.append("Year: ").append(year).append(",");

        if (players != null) {
            sb.append("Names: ");
            for (String player : players.keySet()) {
                sb.append("  ").append(player).append(" - ");
                sb.append(players.get(player)).append(";");
            }
        }

        return sb.toString();
    }
}


package com.example.personalproject;

import java.util.HashMap;
import java.util.Map;

public class MovieInfo {
    public String moviename;
    public String director;
    public String date;

    MovieInfo() {}
    MovieInfo(String mn, String dr, String dt) {
        this.moviename = mn;
        this.director = dr;
        this.date = dt;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("moviename", moviename);
        result.put("director", director);
        result.put("date", date);

        return result;
    }

    public String getMoviename(){
        return moviename;
    }
    public String getDirector() { return director; }
    public String getDate() { return date; }
}

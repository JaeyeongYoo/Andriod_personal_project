package com.example.personalproject;

import java.util.HashMap;
import java.util.Map;

public class AnnounceInfo {
    String title;
    String content;
    String date;

    AnnounceInfo() {}

    AnnounceInfo(String tt, String ct, String dt){
        this.title = tt;
        this.content = ct;
        this.date = dt;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("content", content);
        result.put("date", date);

        return result;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }
}

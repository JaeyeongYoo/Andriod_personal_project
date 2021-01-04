package com.example.personalproject;

import android.content.pm.PackageInfo;

import java.util.HashMap;
import java.util.Map;

public class PostContentInfo {
    String key;
    String moviename;
    String title;
    String content;
    String tag;
    String writer;
    //int like;

    PostContentInfo() {}
    PostContentInfo(String mn, String tt, String ct, String tg, String writer){
        this.moviename = mn;
        this.title = tt;
        this.content = ct;
        this.tag = tg;
        this.writer = writer;
        //this.like = 0;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("moviename", moviename);
        result.put("title", title);
        result.put("content", content);
        result.put("tag", tag);
        result.put("writer", writer);
        //result.put("like", like);

        return result;
    }


    public String getMoviename() {
        return moviename;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTag() {
        return tag;
    }

    public String getWriter() {
        return writer;
    }

}

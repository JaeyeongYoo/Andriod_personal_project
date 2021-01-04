package com.example.personalproject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUserInfo {
    public String useremail;
    public String password;
    public String fullname;
    public String birthday;


    public FirebaseUserInfo () {
        //default constructor
    }

    public FirebaseUserInfo(String ue, String pw, String fn, String bd) {
        this.useremail = ue;
        this.password = pw;
        this.fullname = fn;
        this.birthday = bd;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("useremail", useremail);
        result.put("password", password);
        result.put("fullname", fullname);
        result.put("birthday", birthday);

        return result;
    }

    public String getUseremail(){
        return useremail;
    }
}

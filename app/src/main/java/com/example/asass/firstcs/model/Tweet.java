package com.example.asass.firstcs.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tweet {

    @SerializedName("head")
    @Expose
    private String head;
    @SerializedName("body")
    @Expose
    private String body;
   /* @SerializedName("login")
    @Expose
    private String login;*/

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    /*public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }*/

}


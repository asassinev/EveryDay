package com.example.asass.firstcs.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Tweet {

    @SerializedName("head")
    @Expose
    private String head;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("textCreatedAt")
    @Expose
    private String textCreatedAt;

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

    public String getTextCreatedAt() {
        return textCreatedAt;
    }

    public void setTextCreatedAt(String textCreatedAt) {
        this.textCreatedAt = textCreatedAt;
    }

}


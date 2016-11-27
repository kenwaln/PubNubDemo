package com.kwaln.pubnubdemo;

/**
 * Created by Ken on 11/25/2016.
 */

public class ChatMsg {
    String poster;
    String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    ChatMsg(String p, String b) {
        poster = p;
        body = b;
    }

    @Override
    public String toString() {
        return poster + ": " + body;
    }
}

package com.lyos.simpleclientxmpp.model;

/**
 * Created by lyos2210 on 28/07/14.
 */
public class ChatMessage {

    private String from;
    private String body;

    public ChatMessage() {
    }

    public ChatMessage(String from, String body) {
        this.from = from;
        this.body = body;
    }

    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
}

package com.example.asal.morsechatproject.Model;

/**
 * Created by asal on 2018-01-02.
 */

public class Message
{
    private String message;
    private boolean seen;
    private long time;
    private String type;

    public Message()
    {
    }

    public Message(String message, boolean seen, long time, String type) {
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

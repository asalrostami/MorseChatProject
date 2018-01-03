package com.example.asal.morsechatproject.Model;

/**
 * Created by asal on 2018-01-03.
 */

public class Requests
{
    private String user_name;
    private String user_status;
    private String user_tumb_image;

    public Requests()
    {
    }

    public Requests(String user_name, String user_status, String user_tumb_image) {
        this.user_name = user_name;
        this.user_status = user_status;
        this.user_tumb_image = user_tumb_image;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_tumb_image() {
        return user_tumb_image;
    }

    public void setUser_tumb_image(String user_tumb_image) {
        this.user_tumb_image = user_tumb_image;
    }
}

package com.example.nemanja.mychat;

/**
 * Created by nemanja on 3/21/2018.
 */

public class Chats
{
    private String user_status;

    public Chats()
    {

    }

    public Chats(String user_status)
    {
        this.user_status = user_status;
    }

    public String getUser_status()
    {
        return user_status;
    }

    public void setUser_status(String user_status)
    {
        this.user_status = user_status;
    }
}

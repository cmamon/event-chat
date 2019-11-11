package com.example.eventchat;

import android.app.Application;

import com.example.eventchat.model.Message;

public class EventChat extends Application {
    private Message mMessage;

    public Message getMessage()
    {
        return mMessage;
    }

    public void setMessage(Message message)
    {
        mMessage = message;
    }
}

package com.example.eventchat.activity;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import com.example.eventchat.R;
import com.example.eventchat.model.Message;
import com.example.eventchat.utils.EventChat;

public class MessageDetailActivity extends WearableActivity {

    private Message mMessage;
    private TextView mTVMessageContent;
    private TextView mTVSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        mTVMessageContent = (TextView) findViewById(R.id.message_content);
        mTVSender = (TextView) findViewById(R.id.sender);

        EventChat app = (EventChat) getApplicationContext();
        mMessage = app.getMessage();

        mTVSender.setText(String.format("From %s", String.valueOf(mMessage.getStudentId())));
        mTVMessageContent.setText(mMessage.getStudentMessage());

        // Enables Always-on
        setAmbientEnabled();
    }
}

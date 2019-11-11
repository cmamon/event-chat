package com.example.eventchat;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.eventchat.model.Message;

public class MessageDetailActivity extends WearableActivity {

    private Message mMessage;
    private TextView mTVMessageContent;
    private TextView mTVSender;
    private TextClock mClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        mTVMessageContent = findViewById(R.id.message_content);
        mTVSender = findViewById(R.id.sender);
        mClock = findViewById(R.id.clock);

        // Hide clock first
        mClock.setVisibility(View.INVISIBLE);

        EventChat app = (EventChat) getApplicationContext();
        mMessage = app.getMessage();

        if (mMessage != null) {
            mTVSender.setText(String.valueOf(mMessage.getStudentId()));
            mTVMessageContent.setText(mMessage.getStudentMessage());
        }

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails)
    {
        super.onEnterAmbient(ambientDetails);

        mClock.setFormat12Hour(null);
        mClock.setFormat24Hour("HH:mm:ss");

        mTVMessageContent.setVisibility(View.INVISIBLE);
        mTVSender.setVisibility(View.INVISIBLE);
        mClock.setVisibility(View.VISIBLE);
    }

    @Override
    public void onExitAmbient()
    {
        super.onExitAmbient();
        // Stop clock

        mClock.setVisibility(View.INVISIBLE);
        mTVMessageContent.setVisibility(View.VISIBLE);
        mTVSender.setVisibility(View.VISIBLE);
    }
}

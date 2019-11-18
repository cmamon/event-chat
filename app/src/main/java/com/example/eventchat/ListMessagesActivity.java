package com.example.eventchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventchat.model.Message;
import com.example.eventchat.utils.CustomScrollingLayoutCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ListMessagesActivity extends WearableActivity
        implements RecyclerViewClickListener, SensorEventListener
{
    private final String serverURL = "https://hmin309-embedded-systems.herokuapp.com/message-exchange/messages/";

    private WearableRecyclerView mWearableRecyclerView;
    private TextView mClock;
    private CountDownTimer mTimer;

    private RecyclerView.Adapter mAdapter;
    private ArrayList<Message> mMessages;

    private SensorManager sensorManager;
    private Sensor mAccelerometer;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_messages);

        mWearableRecyclerView = findViewById(R.id.message_list);
        mWearableRecyclerView.setCircularScrollingGestureEnabled(true);
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);
        mWearableRecyclerView.setAdapter(null);

        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        mWearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback)
        );

        mMessages = new ArrayList<>();

        // Make HTTP request to get messages from the server
        getMessages();

        // Parameter accelerometer sensor to update message list when shaking the watch
        parameterAccelerometer();

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        /* Display clock when entering in ambient mode */

        mTimer = new CountDownTimer(1000000000, 1000) {

            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                Calendar c = Calendar.getInstance();
                mClock.setText(String.format(
                        "%d:%d:%d",
                        c.get(Calendar.HOUR),
                        c.get(Calendar.MINUTE),
                        c.get(Calendar.SECOND)
                ));
            }
            public void onFinish() {

            }
        };
        mTimer.start();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        // Stop clock
        mTimer.cancel();

        /* Update message list*/
        mMessages.clear();
        getMessages();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float[] mGravity = event.values.clone();

            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];

            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;

            // If the device as been shaken (strong motion), update message list
            if (mAccel > 2) {
                mMessages.clear();
                getMessages();

                Toast.makeText(ListMessagesActivity.this, "Message list updated",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // required method
    }

    public void parameterAccelerometer() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    public void getMessages() {
        /* Make HTTP request to get the list of messages form the server */
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, serverURL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray messages) {
                        createRecycleView(messages);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG", error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    public void createRecycleView(JSONArray messages)
    {
        for (int i = 0; i < messages.length(); i++) {
            try {
                JSONObject messageJson = messages.getJSONObject(i);

                Message message = new Message(
                    Integer.parseInt(messageJson.getString("id")),
                    Integer.parseInt(messageJson.getString("student_id")),
                    Double.parseDouble(messageJson.getString("gps_lat")),
                    Double.parseDouble(messageJson.getString("gps_long")),
                    messageJson.getString("student_message")
                );

                mMessages.add(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /* Pass the message list to the adapter */
        mAdapter = new MessageAdapter(mMessages, this);
        mWearableRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        /* Save clicked message and pass it to the MessageDetailActivity */

        EventChat app = (EventChat) getApplicationContext();
        app.setMessage(mMessages.get(position));

        Intent intent = new Intent(ListMessagesActivity.this, MessageDetailActivity.class);
        startActivity(intent);
    }
}

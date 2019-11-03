package com.example.eventchat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

public class SendMessageActivity extends WearableActivity implements LocationListener
{
    private static final String TAG = SendMessageActivity.class.getName();
    private Location mLocation;
    private LocationManager locationManager;
    private final String serverURL = "https://hmin309-embedded-systems.herokuapp.com/message-exchange/messages/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        // Enables Always-on
        setAmbientEnabled();
        sendMessage();
    }

    public boolean wifiIsActive()
    {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();

    }

    public boolean locationAccessIsGranted()
    {
        return
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED;
    }

    public void sendMessage()
    {
        if (!wifiIsActive()) {
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            if (locationAccessIsGranted()) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 0, this
                );

                mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION },
                        1337);
            }

        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }

        if (!locationAccessIsGranted()) {
            return;
        }

        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            String message = "WESH LA DETAILLE";
            String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do " +
                    "eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim" +
                    " veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea ";

            DecimalFormat df = new DecimalFormat("0.00");

            double latitude = Double.parseDouble(df.format(mLocation.getLatitude()));
            double longitude = Double.parseDouble(df.format(mLocation.getLongitude()));

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("student_id", 12345);
            jsonBody.put("gps_lat", latitude);
            jsonBody.put("gps_long", longitude);
            jsonBody.put("student_message", lorem);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(SendMessageActivity.this,
                                    "Message sent successfully",
                                    Toast.LENGTH_LONG
                            ).show();
//                            if (response.code() == 200) {
//                                // Do awesome stuff
//                            } else if(response.code() == 500){
//                                Toast.makeText(this, "Error: internal server error", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            NetworkResponse response = error.networkResponse;
                            if (error instanceof ServerError && response != null) {
                                try {
                                    String res = new String(response.data,
                                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                    // Now you can use any deserializer to make sense of data
                                    JSONObject obj = new JSONObject(res);
                                } catch (UnsupportedEncodingException e1) {
                                    // Couldn't properly decode data to string
                                    e1.printStackTrace();
                                } catch (JSONException e2) {
                                    // returned data is not JSONObject?
                                    e2.printStackTrace();
                                }
                            }
                        }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            queue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.d("Latitude","enable");
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.d("Latitude","disable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.d("Latitude","status");
    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLocation = location;
    }
}

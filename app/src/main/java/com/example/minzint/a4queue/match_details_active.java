package com.example.minzint.a4queue;

import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class match_details_active extends AppCompatActivity {

    // ===================================================================
    // VARIABLES

    private String userID_matched;
    private String userName_matched;
    private String times;
    private String Description;
    private String fee;

    private String user_id;
    private String name;
    private String username;
    private String email;
    private String profile;
    private int rating;
    private String register_date;
    boolean logged_in = false;
    private String matched_user_name;
    private String matched_user_id;
    private String message;
    private String mobile_booking_location_lat;
    private String mobile_booking_location_lng;
    private String mobile_booking_address;

    private RequestQueue myRequestQueue;
    private StringRequest request;

    private boolean view = false;

    private String isStarted = "";

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details_active);

        // REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        Bundle bundle = getIntent().getExtras();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);

        user_id =pref.getString("user_id", null);
        name = pref.getString("name", null);
        username = pref.getString("username", null);
        email = pref.getString("email", null);
        profile = pref.getString("profile", null);
        rating = pref.getInt("rating", 0);
        register_date = pref.getString("register_date", null);
        logged_in = pref.getBoolean("logged_in", false);
        mobile_booking_location_lat = bundle.getString("mobile_booking_location_lat");
        mobile_booking_location_lng = bundle.getString("mobile_booking_location_lng");
        mobile_booking_address = bundle.getString("mobile_booking_address");
        view =  Boolean.parseBoolean(bundle.getString("view"));

        // GET INFORMATION AND DISPLAY ON THE ACTIVITY

        getDetailsFromGlobalVariable();

        String[] myArray = getDataToGlobalVariableQueue();

        TextView matchDetails = findViewById(R.id.matchDetails);

        matchDetails.setText(userName_matched + " has requested your assistance!");

        TextView userNameLink = findViewById(R.id.userNameLink);

        userNameLink.setText(userName_matched);

        TextView userLocationLink = findViewById(R.id.userLocationLink);

        userLocationLink.setText(myArray[2]);

        TextView userTimeLink = findViewById(R.id.userTimeLink);

        userTimeLink.setText(times);

        TextView UserFeeAmountLink = findViewById(R.id.UserFeeAmountLink);

        UserFeeAmountLink.setText("R" + fee + "/hr");

        TextView UserMessage = findViewById(R.id.UserMessage);

        UserMessage.setText(Description);

        Button QueueProgressButton = findViewById(R.id.QueueProgressButton);

        // create a new event listener
        QueueProgressButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                checkMatchedStatus();

            }
        });

        Button EndQueueButton = findViewById(R.id.EndQueueButton);


        // create a new event listener
        EndQueueButton.setOnClickListener(new View.OnClickListener() {

            private  boolean alreadyPressed = false;

            @Override
            public void onClick(View view) {

                if (!alreadyPressed) {
                    // UPDATE DATABASE
                    notifyEndQueue();

                    // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                    //Toast.makeText(getApplicationContext(), userName_matched + " has been notified.", Toast.LENGTH_SHORT).show();

                    // CLEAR THE SHARED PREFERENCES FOR THE NEXT USE
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("queue_progress_details", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();

                    alreadyPressed = true;

                    // MOVE TO NEXT ACTIVITY
                    Intent intent = new Intent(getApplicationContext(), queue_end_details.class);
                    intent.putExtra("time", "3:00");
                    startActivity(intent);

                }else{

                    Toast.makeText(getApplicationContext(), "Pending " + userName_matched + " acceptance", Toast.LENGTH_SHORT).show();

                }

            }
        });

        // CUSTOM VIEWS
        if (view){

            QueueProgressButton.setVisibility(View.INVISIBLE);
            EndQueueButton.setVisibility(View.INVISIBLE);
        }


    }

    /**
     * GET INFORMATION FROM GLOBAL VARIABLE
     */
    public void getDetailsFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("matched_user_details", 0);
        userID_matched = pref.getString("matched_user_id", null);
        userName_matched = pref.getString("matched_user_name", null);
        times = pref.getString("times", null);
        Description = pref.getString("matched_user_description", null);
        fee = pref.getString("fee", null);

    }

    /**
     *SEND NOTIFICATION OF THE END OF THE QUEUE SESSION
     */
    public void notifyEndQueue()
    {

        String[] myArray = getDataToGlobalVariableQueue();

        //Toast.makeText(getApplicationContext(), myArray[2], Toast.LENGTH_SHORT).show();

        // CREATE NEW REQUEST
        request = new StringRequest(Request.Method.POST, ServerDetails.QUEUE_UPDATE_END_QUEUE, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);

                    if (jsonObject.names().get(0).equals("success")) {

                        // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                        //makeText(getApplicationContext(), "User notified" , Toast.LENGTH_SHORT).show();

                    } else {

                        // DISPLAY ERROR RESPONSE TO THE ANDROID ACTIVITY SCREEN
                        //Toast.makeText(getApplicationContext(), "Error" + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    // CATCH ERRORS
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                String[] myArray = getDataToGlobalVariableQueue();

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("update", "");
                hashMap.put("user_id", userID_matched);
                hashMap.put("matched_id", user_id);
                hashMap.put("notification_end", "1");
                hashMap.put("isCompleted", 1 + "");

                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }


    /**
     * CHECK IF A MATCH EXISTS IN THE DATABASE
     */
    public void checkMatchedStatus() {

            request = new StringRequest(Request.Method.POST, ServerDetails.MATCHING_CHECK_URL, new Response.Listener<String>() {

                // HANDLE RESPONSES
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.names().get(0).equals("match_success")) {

                            isStarted = jsonObject.getString("isStarted");

                            if (isStarted.equals("1")){

                                // START A QUEUE SESSION
                                Intent intent = new Intent(getApplicationContext(), StartQueue.class);
                                intent.putExtra("user_id", user_id);
                                intent.putExtra("name", name);
                                intent.putExtra("username", username);
                                intent.putExtra("email", email);
                                intent.putExtra("profile", profile);
                                intent.putExtra("rating", rating);
                                intent.putExtra("register_date", register_date);
                                intent.putExtra("logged_in", logged_in);
                                intent.putExtra("matched_username", userName_matched);
                                intent.putExtra("matched_user_id", userID_matched);
                                intent.putExtra("times", times);
                                intent.putExtra("message", Description);
                                intent.putExtra("fee", fee);
                                intent.putExtra("mobile_booking_location_lat", mobile_booking_location_lat);
                                intent.putExtra("mobile_booking_location_lng", mobile_booking_location_lng);
                                intent.putExtra("mobile_booking_address", mobile_booking_address);
                                startActivity(intent);

                            }else{

                                // want to go the next activity for the time being
                                Intent nextActivity = new Intent(getApplicationContext(), QueueProgress.class);
                                nextActivity.putExtra("user_id", user_id);
                                nextActivity.putExtra("name", name);
                                nextActivity.putExtra("username", username);
                                nextActivity.putExtra("email", email);
                                nextActivity.putExtra("profile", profile);
                                nextActivity.putExtra("rating", rating);
                                nextActivity.putExtra("register_date", register_date);
                                nextActivity.putExtra("logged_in", logged_in);
                                nextActivity.putExtra("matched_username", userName_matched);
                                nextActivity.putExtra("matched_user_id", matched_user_id);
                                nextActivity.putExtra("times", times);
                                nextActivity.putExtra("message", message);
                                nextActivity.putExtra("fee", fee);
                                // NEED TO GET THIS VALUE FROM QUEUE RECORDS
                                nextActivity.putExtra("number_people_queue", "0");

                                startActivity(nextActivity);

                            }

                        } else {

                            // DISPLAY ERROR RESPONSE TO THE ANDROID ACTIVITY SCREEN
                            //Toast.makeText(getApplicationContext(), "Error" + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        // CATCH ERRORS
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(match_details_active.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("check_match_status", "");
                    hashMap.put("matched_id", user_id);
                    return hashMap;

                }
            };

            myRequestQueue.add(request);

    }


    /**
     * UPDATE A MATCH IN THE DATABASE
     */
    public void updateMatchFromDatabase()
    {

        // CREATE NEW REQUEST
        request = new StringRequest(Request.Method.POST, ServerDetails.MATCHING_UPDATE, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);

                    if(jsonObject == null){

                        Toast.makeText(match_details_active.this, "Server not responding. Registration failed!", Toast.LENGTH_SHORT).show();

                    }

                    if (jsonObject.names().get(0).equals("success")) {

                        // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                        Toast.makeText(getApplicationContext(), "Match updated" , Toast.LENGTH_SHORT).show();

                    } else {

                        // DISPLAY ERROR RESPONSE TO THE ANDROID ACTIVITY SCREEN
                        Toast.makeText(getApplicationContext(), "Error" + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    // CATCH ERRORS
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("update_match", "");
                hashMap.put("user_id", userID_matched);

                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

    /**
     * SAVE DATA TO GLOBAL VARIABLES
     * @param user_selected_lat
     * @param user_selected_long
     * @param user_selected_address
     */
    public void SaveDataToGlobalVariableQueue(String user_selected_lat, String user_selected_long, String user_selected_address) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("queue_selected", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("user_selected_lat",user_selected_lat+"");
        editor.putString("user_selected_long",user_selected_long+"");
        editor.putString("user_selected_address",user_selected_address);

        editor.commit();

    }

    /**
     * GET DATA FROM GLOBAL VARIABLES
     * @return
     */
    public String[] getDataToGlobalVariableQueue()
    {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("queue_selected", 0);
        String user_selected_lat = pref.getString("user_selected_lat", null);
        String user_selected_long = pref.getString("user_selected_long", null);
        String user_selected_address = pref.getString("user_selected_address", null);

        String[] myArray = {user_selected_lat, user_selected_long, user_selected_address};

        return myArray;

    }

}

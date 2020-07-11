package com.example.minzint.a4queue;

import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;

public class match_details extends AppCompatActivity {

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

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

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

        // FIND INFORMATON AND DISPLAY IN THE ACTIVITY

        getDetailsFromGlobalVariable();

       final String[] myArray = getDataToGlobalVariableQueue();

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

        // DECLINE A MATCH BUTTON

        Button declineMatch = findViewById(R.id.declineMatch);

        declineMatch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // MUST UPDATE THE DATABASE
                removeMatchFromDatabase();

                Intent nextActivity = new Intent(getApplicationContext(), Statistics.class);
                startActivity(nextActivity);



            }
        });

        // ACCEPT A MATCH BUTTON

        Button acceptMatch = findViewById(R.id.acceptMatch);

        acceptMatch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);

                updateMatchFromDatabase();

                Intent nextActivity = new Intent(getApplicationContext(), Statistics.class);
                nextActivity.putExtra("latitude", myArray[0]);
                nextActivity.putExtra("longitude", myArray[1]);
                nextActivity.putExtra("address", myArray[2]);
                nextActivity.putExtra("context", "match_details");
                startActivity(nextActivity);
            }
        });

    }

    /**
     * GET GLOBAL INFORMATION
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
     * REMOVE A MATCH FROM THE DATABASE
     */
    public void removeMatchFromDatabase()
    {

            // CREATE NEW REQUEST
            request = new StringRequest(Request.Method.POST, ServerDetails.MATCHING_DELETE, new Response.Listener<String>() {

                // HANDLE RESPONSES
                @Override
                public void onResponse(String response) {

                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response);

                        Toast.makeText(getApplicationContext(), "Match removed" , Toast.LENGTH_SHORT).show();

                        if(jsonObject == null){

                            Toast.makeText(match_details.this, "Server not responding. Registration failed!", Toast.LENGTH_SHORT).show();

                        }

                        if (jsonObject.names().get(0).equals("success")) {

                            // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                            Toast.makeText(getApplicationContext(), "Match removed" , Toast.LENGTH_SHORT).show();

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
                    hashMap.put("remove_match_status", "");
                    hashMap.put("user_id", userID_matched);

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

                        Toast.makeText(match_details.this, "Server not responding. Registration failed!", Toast.LENGTH_SHORT).show();

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
     * GET DATA FROM GLOBAL VARIABLE
     * @return - STRING ARRAY OF INFORMATION
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

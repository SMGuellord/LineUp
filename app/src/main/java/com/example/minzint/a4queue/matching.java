package com.example.minzint.a4queue;

import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class matching extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Button findUsers = (Button) findViewById(R.id.matchButton);

        findUsers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                TextView work = findViewById(R.id.work);
                work.setText("");

                // FIND ALL THE USERS

                TextView userInput = findViewById(R.id.UserDetails);

                String id = userInput.getText().toString();


            }
        });



        final Button bookings = (Button) findViewById(R.id.bookings);

        bookings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                TextView work = findViewById(R.id.work);
                work.setText("");

                // FIND ALL THE USERS

                TextView userInput = findViewById(R.id.bookingDetails);

                String id = userInput.getText().toString();

                FindBookingDetails(id, "Dont Know");

            }
        });

    }

    /**
     * FIND USER DETAILS IN THE DATABASE
     * @return
     */
    public ArrayList<String> FindUserDetails()
    {

        // Create array list
        final ArrayList<String> userDetails = new ArrayList<>();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        //String url ="http://10.0.2.2/TYP/api.php/users/" + getIDFromGlobalVariable();
        String url = ServerDetails.FIND_USERS+"/api.php/users/" + getIDFromGlobalVariable();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONArray resposeJSON = null;

                        if (response != null) {
                            try {

                                JSONObject jsonObj = new JSONObject(response);

                                String id = jsonObj.getString("id");
                                String name = jsonObj.getString("name");
                                userDetails.add(name);
                                String username = jsonObj.getString("username");
                                userDetails.add(username);
                                String email = jsonObj.getString("email");
                                userDetails.add(email);
                                String longatude = jsonObj.getString("longatude");
                                userDetails.add(longatude.toString());
                                String latitude = jsonObj.getString("latitude");
                                userDetails.add(latitude.toString());

                                TextView work = findViewById(R.id.work);
                                work.setText("The user details are: "+ id + name + " " + username + " " +  email + " " +  longatude + " " +  latitude);

                            } catch (final JSONException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                                "Json parsing error: " + e.getMessage(),
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });
                            }

                        } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                                "Couldn't get json from server. Check LogCat for possible errors!",
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });

                            }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return userDetails;

    }

    /**
     * FIND BOOKING DETAILS IN THE DATABASE
     * @param id
     * @param name
     */
    public void FindBookingDetails(String id, String name)
    {

        // Create array list
        final ArrayList<String> userBookingDetails = new ArrayList<>();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.2.2/TYP/api.php/bookings/" + id;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONArray resposeJSON = null;

                        if (response != null) {
                            try {

                                JSONObject jsonObj = new JSONObject(response);

                                String name = jsonObj.getString("username");
                                String address = jsonObj.getString("address");
                                String lat = jsonObj.getString("lat");
                                String lng = jsonObj.getString("lng");

                                TextView work = findViewById(R.id.work);
                                work.setText("The user bookings are: " + name + " " + address + " " +  lat + " " +  lng);

                            } catch (final JSONException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                                "Json parsing error: " + e.getMessage(),
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });
                            }

                        } else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Couldn't get json from server. Check LogCat for possible errors!",
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            });

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    /**
     * GET GLOBAL VARIABLES
     * @return
     */
    public String getNameFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);
        String username = pref.getString("user_name", null);
        return username;

    }

    /**
     * GET GLOBAL VARIABLES
     * @return
     */
    public String getIDFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);
        String user_id = pref.getString("user_id", null);
        return user_id;

    }

}

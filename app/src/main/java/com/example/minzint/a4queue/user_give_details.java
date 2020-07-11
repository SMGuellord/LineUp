package com.example.minzint.a4queue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.minzint.a4queue.utilities.NavigationOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class user_give_details extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    // ===================================================================
    // VARIABLES

    private EditText AMessage;
    private EditText EditTimeMatch;
    private int feeVAlue = 100;

    private String user_id;
    private String name;
    private String username;
    private String email;
    private String profile;
    private int rating;
    private String register_date;
    boolean logged_in = false;

    TextView textViewUsername;
    TextView textViewUserEmail;
    ImageView profile_Image;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private RequestQueue myRequestQueue;
    private StringRequest request;

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_give_details);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // GET DETAILS FROM PREVIOUS ACTIVITY
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);

        user_id =pref.getString("user_id", null);
        name = pref.getString("name", null);
        username = pref.getString("username", null);
        email = pref.getString("email", null);
        profile = pref.getString("profile", null);
        rating = pref.getInt("rating", 0);
        register_date = pref.getString("register_date", null);
        logged_in = pref.getBoolean("logged_in", false);

        //Instantiate toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //instantiate drawer.
        drawer = findViewById(R.id.drawer_layout);
        //
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        textViewUsername = headerView.findViewById(R.id.TextViewUsername);
        textViewUserEmail = headerView.findViewById(R.id.textViewEmail);
        profile_Image = headerView.findViewById(R.id.navProfilePicture);
        textViewUsername.setText(name);
        textViewUserEmail.setText(email);

        /*Set profile image*/
        if (profile != "") {

            Picasso.with(this)
                    .load(ServerDetails.PROFILE_DIR_URL + "" + profile)
                    .resize(300, 300).into(profile_Image);
        }

        //Add menu toggle to toolbar.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        //Sync the rotation of the hamburger icon with the drawer.
        toggle.syncState();

        // REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        EditTimeMatch = findViewById(R.id.EditTimeMatchs);
        AMessage = findViewById(R.id.AMessage);

        final Button confirmMatch = findViewById(R.id.confirmMatch);

        // create a new event listener
        confirmMatch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // actions on event

                boolean formatTimeCondition = formatTimeGiven();

                if (formatTimeCondition == true) {

                    if (confirmMatch.getText().equals("Match")) {
                        confirmMatch.setText("Unmatch");
                        SaveMatchToDatabase();

                        String[] myArray = getQueueInformationFromGlobalVariable();

                        // DISPLAY MATCH DETAILS
                        Intent nextActivity = new Intent(getApplicationContext(), post_match_deetails.class);
                        nextActivity.putExtra("time", EditTimeMatch.getText().toString());
                        nextActivity.putExtra("mobile_booking_location_lat", myArray[0]);
                        nextActivity.putExtra("mobile_booking_location_lng", myArray[1]);
                        nextActivity.putExtra("mobile_booking_address", myArray[2]);
                        startActivity(nextActivity);

                    }

                }else{

                        // THE TIME WAS NOT ENTERED CORRECTLY
                    TextView ErrorTimeTitle = findViewById(R.id.ErrorTimeTitle);
                    ErrorTimeTitle.setVisibility(View.VISIBLE);


                }
            }
        });

        EditTimeMatch.setText("12:00");

        // REMOVE A PREVIOUS MATCH IF IT IS NOT USED
        removeMatchFromDatabase();

    }

    /**
     * CHECKBOX MANAGEMENT
     * @param view
     */
    public void onCheckboxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        CheckBox checkboxmin = findViewById(R.id.checkboxmin);
        CheckBox checkboxave = findViewById(R.id.checkboxave);
        CheckBox checkboxmax = findViewById(R.id.checkboxmax);

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkboxmin: {
                if (checked) {

                    checkboxave.setChecked(false);
                    checkboxmax.setChecked(false);

                    feeVAlue = 50;


                }
                break;
            }
            case R.id.checkboxave: {
                if (checked) {

                    checkboxmin.setChecked(false);
                    checkboxmax.setChecked(false);

                    feeVAlue = 100;


                }
                break;
            }
            case R.id.checkboxmax:
            {
                if (checked){

                    checkboxmin.setChecked(false);
                    checkboxave.setChecked(false);

                    feeVAlue = 150;
                }
                break;
            }
        }
    }

    /**
     * FORMAT THE MESSAGE RECEIVED FROM USER
     * @return
     */
    public String formatMessageGiven()
    {
        // TAKE STEPS HERE TO FORMAT THE TIME GIVEN BY THE USER
        return AMessage.getText().toString();

    }

    /**
     * FORMAT THE TIME
     * @return
     */
    public boolean formatTimeGiven(){

        boolean conditionHours = false;
        boolean conditionMinutes = false;

        String time = EditTimeMatch.getText().toString();

        String[] segments = time.split(":");

        if (Integer.parseInt(segments[0]) <= 24){

            conditionHours = true;

        }

        if (Integer.parseInt(segments[1]) < 60){

            conditionMinutes = true;

        }

        if (conditionHours && conditionMinutes){

            return true;

        }else{

            return false;
        }

    }

    /**
     * SAVE A MATCH TO THE DATABASE
     */
    private void SaveMatchToDatabase()
    {




                // CREATE NEW REQUEST
                request = new StringRequest(Request.Method.POST, ServerDetails.MATCHING_URL, new Response.Listener<String>() {

                    // HANDLE RESPONSES
                    @Override
                    public void onResponse(String response) {
                        Log.i("onResponse: ",response);
                        JSONObject jsonObject;

                        //Toast.makeText(getApplicationContext(), "Notification sent to " + getMatchedNameFromGlobalVariable(), Toast.LENGTH_SHORT).show();

                        try {
                            jsonObject = new JSONObject(response);



                            if(jsonObject == null){

                                Toast.makeText(user_give_details.this, "Server not responding. Registration failed!", Toast.LENGTH_SHORT).show();
                            }

                            if (jsonObject.names().get(0).equals("success")) {

                                // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN

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

                        Date currentTime = Calendar.getInstance().getTime();

                        // SAVE THE GENERAL VARIABLES
                        SaveDataToGlobalVariable(getMatchedIDFromGlobalVariable(), getMatchedNameFromGlobalVariable(), currentTime.getHours() + ":" + currentTime.getMinutes(), formatMessageGiven(), feeVAlue + "");

                        String[] myArray = getQueueInformationFromGlobalVariable();

                        Date now = new Date();
                         String pattern = "yyyy-MM-dd";
                         SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                         String mysqlDateString = formatter.format(now);

                        HashMap<String, String> hashMap = new HashMap<String, String>();
//                        hashMap.put("submit", "");
                        hashMap.put("user_id", getIDFromGlobalVariable());
                        hashMap.put("username", name);
                        hashMap.put("matched_id", getMatchedIDFromGlobalVariable());
                        hashMap.put("matched_username", getMatchedNameFromGlobalVariable());
                        hashMap.put("dates", mysqlDateString + "");
                        hashMap.put("times", EditTimeMatch.getText().toString());
                        hashMap.put("mobile_times", EditTimeMatch.getText().toString());
                        hashMap.put("message", formatMessageGiven());
                        hashMap.put("lat", Float.parseFloat(myArray[0]) + "");
                        hashMap.put("lng", Float.parseFloat(myArray[1]) + "");
                        hashMap.put("user_selected_address", myArray[2]);
                        hashMap.put("Fee", feeVAlue + "");
                        return hashMap;

                    }
                };

                myRequestQueue.add(request);

            }

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

                    ///Toast.makeText(getApplicationContext(), "Match removed" , Toast.LENGTH_SHORT).show();

                    if(jsonObject == null){

                        // Toast.makeText(user_review.this, "Server not responding. Registration failed!", Toast.LENGTH_SHORT).show();

                    }

                    if (jsonObject.names().get(0).equals("success")) {

                        // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                        // Toast.makeText(getApplicationContext(), "Match removed" , Toast.LENGTH_SHORT).show();

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
                hashMap.put("user_id", user_id);

                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

    /**
     * GET DATA FROM GLOBAL VARIABLE
     * @return
     */
    public String getIDFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);
        String user_id = pref.getString("user_id", null);
        return user_id;

    }

    /**
     * GET DATA FROM GLOBAL VARIABLE
     * @return
     */
    public String getNameFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);
        String userName = pref.getString("user_name", null);
        return userName;

    }

    /**
     * GET DATA FROM GLOBAL VARIABLE
     * @return
     */
    public String getMatchedIDFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);
        String matched_user_id = pref.getString("matched_user_id", null);
        return matched_user_id;

    }

    /**
     * GET DATA FROM GLOBAL VARIABLE
     * @return
     */
    public String getMatchedNameFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);
        String matched_user_name = pref.getString("matched_user_name", null);
        return matched_user_name;

    }

    /**
     * GET DATA FROM GLOBAL VARIABLE
     * @return
     */
    public String[] getQueueInformationFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("queue_selected", 0);
        String user_selected_lat = pref.getString("user_selected_lat", null);
        String user_selected_long = pref.getString("user_selected_long", null);
        String user_selected_address = pref.getString("user_selected_address", null);

        String[] myArray = {user_selected_lat, user_selected_long, user_selected_address};

        return myArray;

    }

    /**
     * GET DATA FROM GLOBAL VARIABLE
     * @return
     */
    public void SaveDataToGlobalVariable(String matched_user_id, String matched_user_name, String times,  String matched_user_description, String fee) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("matched_user_details", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("matched_user_id", matched_user_id);
        editor.putString("matched_user_name", matched_user_name);
        editor.putString("times", times);
        editor.putString("matched_user_description", matched_user_description);
        editor.putString("fee", fee);

        editor.commit();

    }

    //Adding navigation item listener.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationOptions.getNavigationOption(getApplicationContext(), item);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBackPressed() {

        //Opening and Closing drawer
        ////////////////////////////////////////////
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



}

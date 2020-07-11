package com.example.minzint.a4queue;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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

import java.util.HashMap;
import java.util.Map;

public class user_own_profile  extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    private String RequestedUesrname;
    TextView UserNameText;

    private RequestQueue myRequestQueue;
    private StringRequest request;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_own_profile);

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

        // INITIALISE REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        try {

            RequestedUesrname = bundle.getString("RequestedUesrname");


        }catch (NullPointerException ex){ // IF THE GIVEN VALUES ARE NULL

            ex.printStackTrace();

        }

        FindUserDetails(RequestedUesrname);

        Button specifyDetails = findViewById(R.id.specifyDetails);

        specifyDetails.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // GO TO THE MATCHES ACTIVITY
                Intent nextActivity = new Intent(getApplicationContext(), user_give_details.class);
                startActivity(nextActivity);

            }

        });

    }

    public void FindUserDetails(final String name) {

        request = new StringRequest(Request.Method.POST, ServerDetails.GET_USER_DETAILS, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("find_success")) {

                        final String id = jsonObject.getString("id");
                        final String name = jsonObject.getString("name");
                        final String username = jsonObject.getString("username");
                        final String email = jsonObject.getString("email");
                        final String register_date = jsonObject.getString("register_date");
                        final String rating = jsonObject.getString("rating");
                        final String isAdmin = jsonObject.getString("isAdmin");
                        final String details = jsonObject.getString("bio");
                        final String profile = jsonObject.getString("profile");

                        // Toast.makeText(getApplicationContext(), username, Toast.LENGTH_SHORT).show();

                        // UPDATE ACTIVITY
                        TextView UserNameText = findViewById(R.id.UserName);
                        UserNameText.setText(name);

                        TextView userDescript = findViewById(R.id.userDescript);
                        userDescript.setText(details + "");

                        ImageView myImageProfile = findViewById(R.id.profilePictures);

                        RatingBar userRating = findViewById(R.id.userRating);
                        userRating.setNumStars(10);
                        userRating.setRating(Integer.parseInt(rating));

                        /*Set profile image*/
                        if (profile != "") {
                            Picasso.with(user_own_profile.this)
                                    .load(ServerDetails.PROFILE_DIR_URL + "" + profile)
                                    .resize(300, 300).into(myImageProfile);
                        }

                        SaveDataToGlobalVariable(id, username);


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
                Toast.makeText(user_own_profile.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("GET_USER_DETAILS", "");
                hashMap.put("name", name);
                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }




    public String getIDFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);
        String user_id = pref.getString("user_id", null);
        return user_id;

    }

    public String getNameFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);
        String userName = pref.getString("user_name", null);
        return userName;

    }

    public void SaveDataToGlobalVariable(String matched_user_id, String matched_user_name)
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("matched_user_id", matched_user_id);
        editor.putString("matched_user_name", matched_user_name);

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

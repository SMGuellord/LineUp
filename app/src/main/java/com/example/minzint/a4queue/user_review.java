package com.example.minzint.a4queue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class user_review extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

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


    private String matched_id;
    private String matched_username;
    private String message;
    private float ratingSession;

    private RequestQueue myRequestQueue;
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review);

        Bundle bundle = getIntent().getExtras();
        matched_id = bundle.getString("matched_id");
        matched_username  = bundle.getString("matched_username");

        // INITIALISE REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);

        user_id = pref.getString("user_id", null);
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

        TextView userNameTitleReview = findViewById(R.id.userNameTitleReview);

        userNameTitleReview.setText(matched_username);

        Button CompleteReviewLink = findViewById(R.id.CompleteReviewLink);

        final EditText UserReviewMessageLink = findViewById(R.id.UserReviewMessageLink);
        final RatingBar ratingBarUser = findViewById(R.id.ratingBarUser);
        ratingBarUser.setNumStars(5);



        CompleteReviewLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                message = UserReviewMessageLink.getText().toString();
                ratingSession = ratingBarUser.getRating();
                int ratingInt = (int)rating;

                new ManageAsyncAndChange().execute();

            }
        });

    }

    // ASYNC BECAUSE THE HTTP REQUESTS TAKE TIME AND NEED TO WAIT FOR
    // ONE TO FINISH BEFORE MOVING ON TO THE NEXT
    private class ManageAsyncAndChange extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {

            // UPDATE THE DATABASE
            insertUserReview();
            removeMatchFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // MOVE TO STATISTICS
            Intent intent = new Intent(getApplicationContext(), Statistics.class);
            startActivity(intent);

        }
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


    public void insertUserReview()
    {

        request = new StringRequest(Request.Method.POST, ServerDetails.INSERT_USER_REVIEWS, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("success")) {

                        // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN


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
               // Toast.makeText(user_review.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id", matched_id);
                hashMap.put("username", matched_username + "");
                hashMap.put("rating", ratingSession + "");
                hashMap.put("message", message + "");
                return hashMap;

            }
        };

        myRequestQueue.add(request);
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

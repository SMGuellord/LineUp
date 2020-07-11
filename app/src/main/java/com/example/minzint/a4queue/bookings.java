package com.example.minzint.a4queue;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.design.widget.NavigationView;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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

public class bookings extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    // ===================================================================
    // VARIABLES

    private String user_id;
    private String name;
    private String username;
    private String email;
    private String profile;
    private int rating;
    private String register_date;
    boolean logged_in = false;

    private String userID_matched;
    private String userName_matched;
    private String times;
    private String Description;
    private String fee;

    TextView textViewUsername;
    TextView textViewUserEmail;
    ImageView profile_Image;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    ListView lView;

    ListAdapter lAdapter;

    private RequestQueue myRequestQueue;
    private StringRequest request;

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        // GET VARIABLES PAST ON FROM THE PREVIOUS ACTIVITY
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

        // SET ITEMS TO BE INVISIBLE
        TextView ActiveQueueTitle = findViewById(R.id.ActiveQueueTitle);

        ActiveQueueTitle.setText("You have no active queues or unmatched bookings currently");

        TextView UnmatchedBookings = findViewById(R.id.UnmatchedBookings);
        UnmatchedBookings.setVisibility(View.INVISIBLE);

        // INITIALISE REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        drawer = findViewById(R.id.drawer_layout);

        getDetailsFromGlobalVariable();

        checkMatchedStatus();

        final TextView ServicesAnimation = findViewById(R.id.ServicesAnimation);

        //  TITLE ANIMATION
        Integer colorFrom = getResources().getColor(R.color.brightRed);
        Integer colorTo = getResources().getColor(R.color.brightBlue);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                ServicesAnimation.setTextColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(10000);
        colorAnimation.start();

        // LINK TO BROWSE AVAILABLE BOOKINGS
        Button BrowseBookings  = findViewById(R.id.BrowseBookings);

        BrowseBookings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // GO TO THE BROWSER BOOKINGS PAGE
                Intent intent = new Intent(getApplicationContext(), BrowseBookings.class);
                startActivity(intent);

            }
        });


    }

    /**
     * CHECK FOR MATCHES AND ACTIVE QUEUES
     */
    public void checkMatchedStatus() {

            request = new StringRequest(Request.Method.POST, ServerDetails.MATCHING_CHECK_URL, new Response.Listener<String>() {

                // HANDLE RESPONSES
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);


                        if (jsonObject.names().get(0).equals("match_success")) {

                            // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                            // Toast.makeText(getApplicationContext(), "You have a match", Toast.LENGTH_SHORT).show();

                            String isMatched = jsonObject.getString("isMatched");

                                // ONLY RUN IF THE USER HAS ACCEPTED THE MATCH
                                if (isMatched.equals("1")) {

                                    TextView UnmatchedBookings = findViewById(R.id.UnmatchedBookings);
                                    UnmatchedBookings.setVisibility(View.INVISIBLE);

                                    // SET ITEMS TO BE INVISIBLE
                                    TextView ActiveQueueTitle = findViewById(R.id.ActiveQueueTitle);
                                    ActiveQueueTitle.setText("Active Queue");
                                    ActiveQueueTitle.setVisibility(View.VISIBLE);

                                    lView = (ListView) findViewById(R.id.ActiveQueues);

                                    String[] myQueueDetailsArray = getDataToGlobalVariableQueue();

                                    final int[] images = {R.drawable.pinlarge};
                                    final String[] version = {userName_matched};
                                    final String[] versionNumber = {myQueueDetailsArray[2]};

                                    lAdapter = new ListAdapter(bookings.this, version, versionNumber, images);

                                    lView.setAdapter(lAdapter);

                                    lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                            // START THE NEXT ACTIVITY
                                            // SHOW THE USER THAT THEY HAVE A MATCH
                                            Intent intent = new Intent(getApplicationContext(), match_details_active.class);
                                            intent.putExtra("matched_username", userName_matched);
                                            intent.putExtra("matched_user_id", userID_matched);
                                            intent.putExtra("times", times);
                                            intent.putExtra("message", Description);
                                            intent.putExtra("fee", fee);
                                            intent.putExtra("view", "false");
                                            startActivity(intent);

                                        }
                                    });

                                } else if (!isMatched.equals("1")){

                                    TextView ActiveQueueTitle = findViewById(R.id.ActiveQueueTitle);
                                    ActiveQueueTitle.setVisibility(View.INVISIBLE);

                                    TextView UnmatchedBookings = findViewById(R.id.UnmatchedBookings);
                                    UnmatchedBookings.setVisibility(View.VISIBLE);

                                    lView = (ListView) findViewById(R.id.UnmatchedBookingsList);

                                    String[] myQueueDetailsArray = getDataToGlobalVariableQueue();

                                    final int[] images = {R.drawable.pinlarge};
                                    final String[] version = {userName_matched};
                                    final String[] versionNumber = {myQueueDetailsArray[2]};

                                    lAdapter = new ListAdapter(bookings.this, version, versionNumber, images);

                                    lView.setAdapter(lAdapter);

                                    lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                            // START THE NEXT ACTIVITY
                                            // SHOW THE USER THAT THEY HAVE A MATCH
                                            Intent intent = new Intent(getApplicationContext(), match_details.class);
                                            intent.putExtra("matched_username", userName_matched);
                                            intent.putExtra("matched_user_id", userID_matched);
                                            intent.putExtra("times", times);
                                            intent.putExtra("message", Description);
                                            intent.putExtra("fee", fee);
                                            intent.putExtra("matched", "no");
                                            startActivity(intent);

                                        }
                                    });

                                }else{

                                    // SET BOTH TO BE INVISIBLE

                                    TextView ActiveQueueTitle = findViewById(R.id.ActiveQueueTitle);
                                    ActiveQueueTitle.setVisibility(View.INVISIBLE);

                                    TextView UnmatchedBookings = findViewById(R.id.UnmatchedBookings);
                                    UnmatchedBookings.setVisibility(View.INVISIBLE);


                            }

                        } else {

                            // DISPLAY ERROR RESPONSE TO THE ANDROID ACTIVITY SCREEN
                            //Toast.makeText(getApplicationContext(), "Error" + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        // CATCH ERRORS
                        e.printStackTrace();

                        TextView ActiveQueueTitle = findViewById(R.id.ActiveQueueTitle);
                        ActiveQueueTitle.setVisibility(View.INVISIBLE);

                        TextView UnmatchedBookings = findViewById(R.id.UnmatchedBookings);
                        UnmatchedBookings.setVisibility(View.INVISIBLE);

                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(bookings.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("check_match_status", "");
                    hashMap.put("matched_id", getIDFromGlobalVariable());
                    return hashMap;

                }
            };

            myRequestQueue.add(request);

        }

    /**
     * GET GLOBAL VARIABLES
     * @return - STRING OF GLOBAL VARIABLES
     */
    public String getIDFromGlobalVariable() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);
        String user_id = pref.getString("user_id", null);
        return user_id;

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

    /**
     * GET GLOBAL VARIABLES
     * @return - STRING OF GLOBAL VARIABLES
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

    /**
     * GET GLOBAL VARIABLES
     * @return - STRING OF GLOBAL VARIABLES
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



}

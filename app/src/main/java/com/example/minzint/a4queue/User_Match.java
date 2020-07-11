package com.example.minzint.a4queue;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
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
import android.view.WindowManager;
import android.widget.AdapterView;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User_Match extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    private MenuItem searchMenuItem;

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

    private RequestQueue myRequestQueue;
    private StringRequest request;

    private ArrayList<String> userNames = new ArrayList<>();
    ListAdapter lAdapter;
    private ListView lView;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    TextView NearbyUsersAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__match);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // INITIALISE REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

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

        lView = (ListView) findViewById(R.id.ListViewUsers);

        DeleteImagesTask myDeleteImagesTask = new DeleteImagesTask();
        // ASYNC CLASS
        myDeleteImagesTask.execute();

        NearbyUsersAnimate = findViewById(R.id.NearbyUsersAnimate);

        animate();

    }

    // ASYNC BECAUSE THE HTTP REQUESTS TAKE TIME AND NEED TO WAIT FOR
    // ONE TO FINISH BEFORE MOVING ON TO THE NEXT

    // THIS DOES NOT DELETE NAMES!!!
    private class DeleteImagesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            GET_USER_DETAILS();

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {

            displayNames();

        }
    }

    // THE SIZE OF THESE ARRAYS MUST BE INCREASED WHEN MORE USERS ARE ADDED
    private String[] userNamesList = new String[10];
    private String[] userDetailList = new String[10];
    private int[] userImageList = new int[10];
    private String[] userImageStringList = new String[10];
    private int count = 0;

    public void displayNames(){

        lAdapter = new ListAdapter(User_Match.this, userNamesList, userDetailList, userImageList);
        lView.setAdapter(lAdapter);


    }

    public void animate(){

        //  TITLE ANIMATION
        Integer colorFrom = getResources().getColor(R.color.brightRed);
        Integer colorTo = getResources().getColor(R.color.brightBlue);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                NearbyUsersAnimate.setTextColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(10000);
        colorAnimation.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimation.start();

    }

    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void GET_USER_DETAILS() {

        request = new StringRequest(Request.Method.POST, ServerDetails.GET_USERS_NEW, new Response.Listener<String>() {

            final String[] myQueueDetailsArray = getQueueInformationFromGlobalVariable();

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray JsonArray = new JSONArray(response);

                    String userRadius = "";

                    // GET USER RADIUS
                    for (int i = 0; i < JsonArray.length(); i++){

                        String user_name =  JsonArray.getJSONObject(i).get("name") + "";
                        String bio =  JsonArray.getJSONObject(i).get("bio").toString();
                        String rating =  JsonArray.getJSONObject(i).get("rating").toString();
                        String user_lat =  JsonArray.getJSONObject(i).get("user_lat").toString();
                        String user_lng =  JsonArray.getJSONObject(i).get("user_lng").toString();
                        String bin_searching =  JsonArray.getJSONObject(i).get("bin_searching").toString();
                        String radius =  JsonArray.getJSONObject(i).get("radius").toString();

                        // DONT SHOW THE CURRENT USER
                        if (name.equals(user_name)){

                            userRadius = radius;

                        }
                    }

                    // GOT THROUGH EACH USER
                    for (int i = 0; i < JsonArray.length(); i++){

                       String user_name =  JsonArray.getJSONObject(i).get("name") + "";
                       String bio =  JsonArray.getJSONObject(i).get("bio").toString();
                       String rating =  JsonArray.getJSONObject(i).get("rating").toString();
                       String user_lat =  JsonArray.getJSONObject(i).get("user_lat").toString();
                       String user_lng =  JsonArray.getJSONObject(i).get("user_lng").toString();
                       String bin_searching =  JsonArray.getJSONObject(i).get("bin_searching").toString();
                       String radius =  JsonArray.getJSONObject(i).get("radius").toString();

                       // DONT SHOW THE CURRENT USER
                       if (!name.equals(user_name)){

                           // IF THE USER IS FINDABLE
                           if (bin_searching.equals("1")) {

                               // GET THE DISTANCE THAT THE USER IS FROM THE QUEUE
                               double distanceApartKM = distance(Double.parseDouble(myQueueDetailsArray[0]), Double.parseDouble(myQueueDetailsArray[1]), Double.parseDouble(user_lat), Double.parseDouble(user_lng), 'K');

                               int radiusInt = Integer.parseInt(userRadius);

                               // ONLY SHOW THE USER IF THE DISTANCE IS WITHIN 500 KILOMETERS
                               // THIS MUST CHANGE TO SETTINGS WHEN READY
                               if (distanceApartKM < radiusInt) {
                                
                                   // USER NAME
                                   userNamesList[count] = user_name;
                                   // USER BIO
                                   userDetailList[count] = bio;
                                   // USER IMAGE
                                   //userImageStringList[count] = segments[13];

                                   String userRating = rating;

                                   if (Integer.parseInt(userRating) < 3) {
                                       userImageList[count] = R.drawable.usericonred;

                                   } else if ((Integer.parseInt(userRating) >= 3) && (Integer.parseInt(userRating) < 7)) {

                                       userImageList[count] = R.drawable.usericonyellow;

                                   } else if ((Integer.parseInt(userRating) >= 7) && (Integer.parseInt(userRating) < 10)) {

                                       userImageList[count] = R.drawable.usericongreen;
                                   }

                                   count++;

                               }
                           }
                       }


                    }

                    lAdapter.notifyDataSetChanged();

                } catch (JSONException e) {

                    // CATCH ERRORS
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("GET_USER_DETAILS", "");
                return hashMap;

            }
        };

        myRequestQueue.add(request);

        // ACTION LISTENER FOR EACH INDIVIDUAL PERSON
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent nextActivity = new Intent(getApplicationContext(), user_own_profile.class);
                nextActivity.putExtra("RequestedUesrname", userNamesList[i]);
                startActivity(nextActivity);

            }
        });

    }

    public String[] getQueueInformationFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("queue_selected", 0);
        String user_selected_lat = pref.getString("user_selected_lat", null);
        String user_selected_long = pref.getString("user_selected_long", null);
        String user_selected_address = pref.getString("user_selected_address", null);

        String[] myArray = {user_selected_lat, user_selected_long, user_selected_address};

        return myArray;

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

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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

public class Settings extends AppCompatActivity  implements  NavigationView.OnNavigationItemSelectedListener{

    // ===================================================================
    // VARIABLES

    private String name;
    private String email;
    private String username;

    private String user_id;
    private String profile;
    private int rating;
    private String register_date;
    boolean logged_in = false;

    ImageView profile_Image;
    TextView textViewUsername;
    TextView userEmail;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private TextView DisplayQueueLoverLocationInputText;
    private SeekBar seekBarDisplayUsers;

    private int distanceDisplay;
    private float appRating;

    private RatingBar ratingBar;
    private Switch SearchingAvailableSwitch;
    private int switchValue;

    ToggleButton QueueUpdateToggleButton;
    ToggleButton MatchedUsersToggleButton;

    private CharSequence QueueNotificationStatus;
    private CharSequence MatchNotificationStatus;

    private RequestQueue myRequestQueue;
    private StringRequest request;

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Instantiate toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //instantiate drawer.
        drawer = findViewById(R.id.drawer_layoutSettings);
        //
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        // GET ATTRIBUTES FROM PREVIOUS ACTIVITY
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);

        user_id =pref.getString("user_id", null);
        name = pref.getString("name", null);
        username = pref.getString("username", null);
        email = pref.getString("email", null);
        profile = pref.getString("profile", null);
        rating = pref.getInt("rating", 0);
        register_date = pref.getString("register_date", null);
        logged_in = pref.getBoolean("logged_in", false);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        textViewUsername = headerView.findViewById(R.id.TextViewUsername);
        userEmail = headerView.findViewById(R.id.textViewEmail);
        textViewUsername.setText(name);
        userEmail.setText(email);
        profile_Image = headerView.findViewById(R.id.navProfilePicture);

        /*Set profile image*/
        if(profile !=""){
            Picasso.with(this)
                    .load(ServerDetails.PROFILE_DIR_URL+""+profile)
                    .resize(300,300).into(profile_Image);
        }

        //Add menu toggle to toolbar.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        //Sync the rotation of the hanburger icon with the drawer.
        toggle.syncState();

        // inialize rating bar
        ratingBar = findViewById(R.id.ratingBar);
        QueueUpdateToggleButton = findViewById(R.id.QueueUpdateToggleButton);
        MatchedUsersToggleButton = findViewById(R.id.MatchedUsersToggleButton);
        SearchingAvailableSwitch = findViewById(R.id.SearchingAvailableSwitch);
        SearchingAvailableSwitch.setChecked(true);

        // SET ACTION LISTENER FOR THE SWITCH
        SearchingAvailableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchValue = 1;
                } else {
                    switchValue = 0;
                }
            }
        });

        QueueUpdateToggleButton.setChecked(true);
        MatchedUsersToggleButton.setChecked(true);

       /* if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_QueueLover);
        }*/

        // INITIALISE REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        // GET ANY SETTINGS
        GetSettingsRow();

        DisplayQueueLoverLocationInputText = findViewById(R.id.DisplayQueueLoverLocationInputText);
        seekBarDisplayUsers =(SeekBar) findViewById(R.id.seekBarDisplayUsers);
        seekBarDisplayUsers.setMax(150);
        seekBarDisplayUsers.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub

                DisplayQueueLoverLocationInputText.setText(progress + "KM");
                distanceDisplay = progress;

            }
        });

        ImageView myimageView4 = findViewById(R.id.imageView4);

        myimageView4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // actions on event

                // manually set the location

                updateUserLocation(-26.202636,28.032607);


            }
        });


    }

    /**
     * UPDATE THE USER CURRENT LOCATION IN THE DATABASE
     * @param lat - LATITUDE
     * @param lng - LONGATUDE
     */
    public void updateUserLocation(final double lat, final double lng)
    {

        request = new StringRequest(Request.Method.POST, ServerDetails.UPDATE_USER_DETAILS, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(Statistics.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("update", "");
                hashMap.put("id", user_id);
                hashMap.put("user_lat", lat + "");
                hashMap.put("user_lng", lng + "");
                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        QueueNotificationStatus = QueueUpdateToggleButton.getText();
        MatchNotificationStatus = MatchedUsersToggleButton.getText();

        appRating = ratingBar.getRating();

        UpdateSettings(distanceDisplay);

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
     * GET SETTINGS FROM THE DATABASE
     */
    public void GetSettingsRow() {

        request = new StringRequest(Request.Method.POST, ServerDetails.GET_SETTINGS, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("find_success")) {

                        // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                        // Toast.makeText(getApplicationContext(), "You have a match", Toast.LENGTH_SHORT).show();

                        // GET THE DETAILS
                        String bin_searching = jsonObject.getString("bin_searching");
                        String bin_location = jsonObject.getString("bin_location");
                        String bin_custom = jsonObject.getString("bin_custom");
                        String lat = jsonObject.getString("lat");
                        String lng = jsonObject.getString("lng");
                        String radius = jsonObject.getString("radius");
                        String address = jsonObject.getString("address");
                        String plat = jsonObject.getString("plat");
                        String plng = jsonObject.getString("plng");
                        String app_rating = jsonObject.getString("app_rating");
                        String notification_queue = jsonObject.getString("notification_queue");
                        String notification_matches = jsonObject.getString("notification_matches");

                        // SET TOGGLE BUTTONS
                        if (notification_queue.equals("ON")){
                            QueueUpdateToggleButton.setChecked(true);

                        }else{

                            QueueUpdateToggleButton.setChecked(false);
                        }

                        if (notification_matches.equals("ON")){

                            MatchedUsersToggleButton.setChecked(true);

                        }else{

                            MatchedUsersToggleButton.setChecked(false);
                        }

                        // SET THE SEARCHING SWITCH
                        if (bin_searching.equals("1")){
                            // SEARCHING ACTIVE
                            SearchingAvailableSwitch.setChecked(true);

                        }else{

                            SearchingAvailableSwitch.setChecked(false);

                        }

                        // SET THE APPLICATION RATING BAR
                        ratingBar.setRating(Float.parseFloat(app_rating));

                        // UPDATE RADIUS INTERFACE
                        DisplayQueueLoverLocationInputText.setText(radius + "KM");
                        seekBarDisplayUsers.setProgress(Integer.parseInt(radius));

                    } else {

                        // CreateSettingsRow();

                    }

                } catch (JSONException e) {
                    // CATCH ERRORS
                    e.printStackTrace();

                   //CreateSettingsRow();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Settings.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("GET_SETTINGS", "");
                hashMap.put("user_id", user_id);
                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

    /**
     * CREATE A SETTINGS ROW IF USER DOES NOT HAVE ONE, MOVED TO STATISTICS
     */
    public void CreateSettingsRow() {

        request = new StringRequest(Request.Method.POST, ServerDetails.CREATE_SETTINGS_ROW, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);


                } catch (JSONException e) {
                    // CATCH ERRORS
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Settings.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id", user_id);
                hashMap.put("username", username + "");
                hashMap.put("bin_searching", 1 + "");
                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

    /**
     * UPDATE THE USER SETTINGS
     * @param progress
     */
    public void UpdateSettings(final int progress) {

        request = new StringRequest(Request.Method.POST, ServerDetails.UPDATE_SETTINGS, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("match_success")) {

                        // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                        // Toast.makeText(getApplicationContext(), "You have a match", Toast.LENGTH_SHORT).show()

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
                Toast.makeText(Settings.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("update", "");
                hashMap.put("user_id", user_id);
                hashMap.put("bin_searching", switchValue + "");
                hashMap.put("radius", progress + "");
                hashMap.put("app_rating", appRating + "");
                hashMap.put("notification_queue", QueueNotificationStatus + "");
                hashMap.put("notification_matches", MatchNotificationStatus + "");
                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }
}

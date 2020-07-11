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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.minzint.a4queue.utilities.NavigationOptions;
import com.squareup.picasso.Picasso;

public class automatch extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

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

    TextView textViewUsername;
    TextView textViewUserEmail;
    ImageView profile_Image;

    private RequestQueue myRequestQueue;
    private StringRequest request;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatch);

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

        // instantiate toolbar
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

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

import com.example.minzint.a4queue.models.FutureBookingsDetails;
import com.example.minzint.a4queue.utilities.NavigationOptions;
import com.squareup.picasso.Picasso;

public class Future_Booking_desc extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    // ===================================================================
    // VARIABLES

    private FutureBookingsDetails futureBookingsDetails;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private String user_id;
    private String name;
    private String username;
    private String email;
    private String profile;
    private int rating;
    private String register_date;
    boolean logged_in;

    private final String TAG = "Future_Booking_desc";

    TextView textViewUsername;
    TextView textViewUserEmail;
    ImageView profile_Image;

    private TextView textViewAddress;
    private TextView textViewTime;
    private TextView textViewDate;
    private TextView textViewFee;
    private TextView textViewDetails;
    private TextView textViewCreatedOn;

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future__booking_desc);

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

        Log.d(TAG, "Future booking description");
        Log.d(TAG, "user_id:---------- "+user_id);
        Log.d(TAG,"name:------------- "+name);
        Log.d(TAG,"username:--------- "+ username);
        Log.d(TAG,"email: -----------"+email);
        Log.d(TAG,"profile:---------- "+ profile);
        Log.d(TAG,"rating:----------- "+ String.valueOf(rating));
        Log.d(TAG,"register:---------- "+ register_date);

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
        if(profile !=""){
            Picasso.with(this)
                    .load(ServerDetails.PROFILE_DIR_URL+""+profile)
                    .resize(300,300).into(profile_Image);
        }

        //instantiate drawer.
        drawer = findViewById(R.id.drawer_layoutBooking);
        //
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        futureBookingsDetails = getIntent().getExtras().getParcelable("FutureBookingsDetails");

        // FIND ACTIVITY ATTRIBUTES
        textViewAddress = findViewById(R.id.textViewAddressVal);
        textViewTime = findViewById(R.id.textViewTimeVal);
        textViewDate = findViewById(R.id.textViewDateVal);
        textViewDetails = findViewById(R.id.textViewDetailsVal);
        textViewCreatedOn = findViewById(R.id.textViewCreatedOnVal);
        textViewFee = findViewById(R.id.textViewFeePerHour);


       /* if (futureBookingsDetails.getProfile() != "") {

            Picasso.with(this)
                    .load(ServerDetails.PROFILE_DIR_URL + "" + futureBookingsDetails.getProfile() )
                    .resize(100, 100).into(imageViewProfile);
        }*/

        textViewAddress.setText(futureBookingsDetails.getAddress());

        // FIELD IN THE DATABASE CALLED MOBILE TIMES PLEASE CHANGE
        textViewTime.setText(futureBookingsDetails.getTimes());

        // IF MOBILE BOOKING THEN JUST DISPLAYS THE DAY IT WAS CREATED SINCE THAT WAS THE DAY IT WAS USED
        if (futureBookingsDetails.getDate().equals("0000-00-00")){

            String[] segments = futureBookingsDetails.getDate_created().split(" ");

            // DISPLAY ONLY THE DATE
            textViewDate.setText(segments[0]);

        }else{

            textViewDate.setText(futureBookingsDetails.getDate());
        }

        textViewDetails.setText(futureBookingsDetails.getDetails());
        textViewCreatedOn.setText(futureBookingsDetails.getDate_created());


        // IF MOBILE BOOKING WILL RATHER USE THE MOBILE VERSION OF FEE
        if (futureBookingsDetails.getDate_created().equals("0.0")){

            // GET AND DISPLAY THE FEE

        }else{

            textViewFee.setText(String.valueOf(futureBookingsDetails.getFee_per_hour()));

        }

        //Add menu toggle to toolbar.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        //Sync the rotation of the hamburger icon with the drawer.
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationOptions.getNavigationOption(getApplicationContext(), item);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

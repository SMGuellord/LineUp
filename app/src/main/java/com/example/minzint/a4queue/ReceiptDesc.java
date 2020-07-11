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

import com.example.minzint.a4queue.models.ReceiptInfo;
import com.example.minzint.a4queue.utilities.NavigationOptions;
import com.squareup.picasso.Picasso;

public class ReceiptDesc extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // ===================================================================
    // VARIABLES

    private TextView textViewAddress;
    private TextView textViewDate;
    private TextView textViewMatchedWith;
    private TextView textViewFee;
    private TextView textViewTotal;
    private TextView textViewTime;


    private ReceiptInfo receiptInfo;
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

    TextView textViewUsername;
    TextView textViewUserEmail;
    ImageView profile_pic, profile_Image;

    private final String TAG="ReceiptDesc";

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_desc);

        //instantiate drawer.
        drawer = findViewById(R.id.drawer_layoutReceipt);
        //
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);

        user_id =pref.getString("user_id", null);
        name = pref.getString("name", null);
        username = pref.getString("username", null);
        email = pref.getString("email", null);
        profile = pref.getString("profile", null);
        rating = pref.getInt("rating", 0);
        register_date = pref.getString("register_date", null);
        logged_in = pref.getBoolean("logged_in", false);

        Log.d(TAG, "Receipt Description");
        Log.d(TAG, "user_id:---------- "+user_id);
        Log.d(TAG,"name:------------- "+name);
        Log.d(TAG,"username:--------- "+ username);
        Log.d(TAG,"email: -----------"+email);
        Log.d(TAG,"profile:---------- "+ profile);
        Log.d(TAG,"rating:----------- "+ String.valueOf(rating));
        Log.d(TAG,"register:---------- "+ register_date);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        profile_pic = findViewById(R.id.profilePicture);
        textViewUsername = headerView.findViewById(R.id.TextViewUsername);
        textViewUserEmail = headerView.findViewById(R.id.textViewEmail);
        textViewUsername.setText(name);
        textViewUserEmail.setText(email);

        profile_Image = headerView.findViewById(R.id.navProfilePicture);
        /*Set profile image*/
        if(profile !=""){
            Picasso.with(this)
                    .load(ServerDetails.PROFILE_DIR_URL+""+profile)
                    .resize(300,300).into(profile_Image);
        }


        receiptInfo = getIntent().getExtras().getParcelable("ReceiptInfo");

        // FIND ATTRIBUTES

        textViewAddress = findViewById(R.id.textViewAddressReceiptVal);
        textViewDate = findViewById(R.id.textViewDateReceiptVal);
        textViewMatchedWith = findViewById(R.id.textViewMatched_username_val);
        textViewTime = findViewById(R.id.textViewTimeQueuedVal);
        textViewFee = findViewById(R.id.textViewFeePerHourReceipt);
        textViewTotal = findViewById(R.id.textViewTotalReceiptDescVal);

        textViewAddress.setText(receiptInfo.getAddress());
        textViewDate.setText(receiptInfo.getDate());
        textViewMatchedWith.setText(receiptInfo.getMatched_user_name());
        textViewTime.setText(receiptInfo.getTime_queued());
        textViewFee.setText(String.valueOf(receiptInfo.getFee()));
        textViewTotal.setText(String.valueOf(receiptInfo.getTotal_paid()));


        //Instantiate toolbar.
        Toolbar toolbar = findViewById(R.id.toolbarReceipt);
        setSupportActionBar(toolbar);

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

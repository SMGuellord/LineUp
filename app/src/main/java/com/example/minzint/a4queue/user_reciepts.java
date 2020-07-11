package com.example.minzint.a4queue;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minzint.a4queue.models.ReceiptInfo;
import com.example.minzint.a4queue.utilities.NavigationOptions;
import com.example.minzint.a4queue.utilities.ReceiptAdapter;
import com.example.minzint.a4queue.utilities.RetrofitApiClient;
import com.example.minzint.a4queue.utilities.RetrofitApiInterface;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class user_reciepts extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

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

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<ReceiptInfo> receiptInfos;
    private ReceiptAdapter adapter;
    private RetrofitApiInterface apiInterface;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reciepts);

        final TextView ReceiptsAnimation = findViewById(R.id.ReceiptsAnimation);

        //  TITLE ANIMATION
        Integer colorFrom = getResources().getColor(R.color.brightRed);
        Integer colorTo = getResources().getColor(R.color.brightBlue);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                ReceiptsAnimation.setTextColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(10000);
        colorAnimation.start();


        setupLayoutManager();
        recyclerView = findViewById(R.id.recycleViewReceipt);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

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

        fetchData(user_id);
    }

    /**
     * Set layout manager for the recycler view.
     * */
    private void setupLayoutManager(){
        layoutManager = new LinearLayoutManager(user_reciepts.this);
    }

    /**
     * fetch data from the external database.
     * */
    private void fetchData(String request){
        apiInterface = RetrofitApiClient.getApiClient().create(RetrofitApiInterface.class);
        Call<List<ReceiptInfo>> call = apiInterface.getReceiptInfo(request);

        call.enqueue(new Callback<List<ReceiptInfo>>() {
            @Override
            public void onResponse(Call<List<ReceiptInfo>> call, Response<List<ReceiptInfo>> response) {
                Log.d("user_receipts", "response: "+ response.toString());
                if(response != null){
                    receiptInfos = response.body();
                    Log.d("user_receipts", "Receipt: "+receiptInfos.get(0).toString());
                    Toast.makeText(user_reciepts.this, "You have "+receiptInfos.size()+" receipt(s)", Toast.LENGTH_LONG).show();

                    adapter = new ReceiptAdapter(receiptInfos, user_reciepts.this);
                    recyclerView.setAdapter(adapter);
                }else{

                    // DISPLAY NO BOOKING AVAILABLE ON FAIL
                    LinearLayout noReceiptsNotification = findViewById(R.id.noReceiptsNotification);
                    noReceiptsNotification.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onFailure(Call<List<ReceiptInfo>> call, Throwable t) {

                // DISPLAY NO BOOKING AVAILABLE ON FAIL
                LinearLayout noReceiptsNotification = findViewById(R.id.noReceiptsNotification);
                noReceiptsNotification.setVisibility(View.VISIBLE);

            }
        });
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

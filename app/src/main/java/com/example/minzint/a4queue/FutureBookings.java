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

import com.example.minzint.a4queue.models.FutureBookingsDetails;
import com.example.minzint.a4queue.utilities.NavigationOptions;
import com.example.minzint.a4queue.utilities.RetrofitApiClient;
import com.example.minzint.a4queue.utilities.RetrofitApiInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FutureBookings extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

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

    private final String TAG = "FutureBookings";

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<FutureBookingsDetails> futureBookingsDetails;
    private BookingListAdapter adapter;
    private RetrofitApiInterface apiInterface;

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_bookings);

        setupLayoutManager();
        recyclerView = findViewById(R.id.recycleViewBookings);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        //Instantiate toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //instantiate drawer.
        drawer = findViewById(R.id.drawer_layoutFutureBookings);
        //
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(FutureBookings.this);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);

        user_id =pref.getString("user_id", null);
        name = pref.getString("name", null);
        username = pref.getString("username", null);
        email = pref.getString("email", null);
        profile = pref.getString("profile", null);
        rating = pref.getInt("rating", 0);
        register_date = pref.getString("register_date", null);
        logged_in = pref.getBoolean("logged_in", false);

        // LOGGING INFORMATION
        Log.d(TAG, "Future Bookings");
        Log.d(TAG, "user_id:---------- "+user_id);
        Log.d(TAG,"name:------------- "+name);
        Log.d(TAG,"username:--------- "+ username);
        Log.d(TAG,"email: -----------"+email);
        Log.d(TAG,"profile:---------- "+ profile);
        Log.d(TAG,"rating:----------- "+ String.valueOf(rating));
        Log.d(TAG,"register:---------- "+ register_date);
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

       /* if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_QueueLover);
        }*/

        fetchData(user_id);

        final TextView BookingsAnimation = findViewById(R.id.BookingsAnimation);

        //  TITLE ANIMATION
        Integer colorFrom = getResources().getColor(R.color.brightRed);
        Integer colorTo = getResources().getColor(R.color.brightBlue);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                BookingsAnimation.setTextColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(10000);
        colorAnimation.start();

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
     * Set layout manager for the recycler view.
     * */
    private void setupLayoutManager(){
        layoutManager = new LinearLayoutManager(FutureBookings.this);
    }

    /**
     * fetch data from the external database.
     * */
    private void fetchData(String request){
        apiInterface = RetrofitApiClient.getApiClient().create(RetrofitApiInterface.class);
        Call<List<FutureBookingsDetails>> call = apiInterface.getBookings(request);

        call.enqueue(new Callback<List<FutureBookingsDetails>>() {

            @Override
            public void onResponse(Call<List<FutureBookingsDetails>> call, Response<List<FutureBookingsDetails>> response) {
                Log.d("FutureBookings", "Bookings: "+response.toString());
                if(response != null){
                    futureBookingsDetails = response.body();

                    Toast.makeText(FutureBookings.this, "You have "+futureBookingsDetails.size()+" booking(s)", Toast.LENGTH_LONG).show();

                    adapter = new BookingListAdapter(futureBookingsDetails, FutureBookings.this);
                    recyclerView.setAdapter(adapter);
                }else{

                    // DISPLAY NO BOOKING AVAILABLE ON FAIL
                    LinearLayout noBookingsNotification = findViewById(R.id.noBookingsNotification);
                    noBookingsNotification.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onFailure(Call<List<FutureBookingsDetails>> call, Throwable t) {

                // DISPLAY NO BOOKING AVAILABLE ON FAIL
                LinearLayout noBookingsNotification = findViewById(R.id.noBookingsNotification);
                noBookingsNotification.setVisibility(View.VISIBLE);

                Log.d("FutureBookings", "Error on bookings"+t.toString());
            }
        });
    }
}

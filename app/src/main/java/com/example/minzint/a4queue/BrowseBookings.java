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

public class BrowseBookings extends AppCompatActivity {

    // ===================================================================
    // VARIABLES

    private final String TAG = "FutureBookings";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<FutureBookingsDetails> FutureBookingMatchUser;
    private MatchBookingsAdapter adapter;
    private RetrofitApiInterface apiInterface;

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

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_bookings);

        // GET VARIABLES PAST FROM THE PREVIOUS ACTIVITY
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);

        user_id =pref.getString("user_id", null);
        name = pref.getString("name", null);
        username = pref.getString("username", null);
        email = pref.getString("email", null);
        profile = pref.getString("profile", null);
        rating = pref.getInt("rating", 0);
        register_date = pref.getString("register_date", null);
        logged_in = pref.getBoolean("logged_in", false);

        // SETUP THE RECYCLER VIEW
        setupLayoutManager();
        recyclerView = findViewById(R.id.recycleViewBrowseBookings);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        final TextView BrowseBookingsTitle = findViewById(R.id.BrowseBookingsTitle);

        //  TITLE ANIMATION
        Integer colorFrom = getResources().getColor(R.color.brightRed);
        Integer colorTo = getResources().getColor(R.color.brightBlue);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                BrowseBookingsTitle.setTextColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(10000);
        colorAnimation.start();

        fetchData("retrieve");

    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.setAdapter(null);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        recyclerView.setAdapter(null);

    }

    /**
     * fetch data from the external database.
     * */
    private void fetchData(String request){
        apiInterface = RetrofitApiClient.getApiClient().create(RetrofitApiInterface.class);
        Call<List<FutureBookingsDetails>> call = apiInterface.getFutureBookings(request);

        call.enqueue(new Callback<List<FutureBookingsDetails>>() {

            @Override
            public void onResponse(Call<List<FutureBookingsDetails>> call, Response<List<FutureBookingsDetails>> response) {
                Log.d("FutureBookings", "Bookings: "+response.toString());
                if(response != null){
                    FutureBookingMatchUser = response.body();

                    MatchBookingsAdapter adapter = null;
                    adapter = new MatchBookingsAdapter(FutureBookingMatchUser, BrowseBookings.this);
                    recyclerView.setAdapter(adapter);

                }else{

                }

            }

            @Override
            public void onFailure(Call<List<FutureBookingsDetails>> call, Throwable t) {
                Log.d("FutureBookings", "Error on bookings"+t.toString());
            }
        });
    }

    /**
     * Set layout manager for the recycler view.
     * */
    private void setupLayoutManager(){
        layoutManager = new LinearLayoutManager(BrowseBookings.this);
    }

}

package com.example.minzint.a4queue;

import android.content.DialogInterface;
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
import android.app.AlertDialog;
import android.text.InputType;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
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
import com.example.minzint.a4queue.models.AccountStat;
import com.example.minzint.a4queue.utilities.NavigationOptions;
import com.example.minzint.a4queue.utilities.RetrofitApiClient;
import com.example.minzint.a4queue.utilities.RetrofitApiInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class User_Profile extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{


    private String user_id;
    private String name;
    private String username;
    private String email;
    private String profile;
    private int rating;
    private String register_date;
    boolean logged_in;

    private List<AccountStat> mAccountStat;

    private RetrofitApiInterface apiInterface;

    private final String TAG = "User_Profile";
    TextView textViewUsername;
    TextView textViewUserEmail;
    ImageView profile_pic, profile_Image;
    Button btnBookings;
    Button btnServices;
    Button btnReceipts;

    TextView textViewFootsteps_pm;
    TextView textViewTimeQueuing_pm;
    TextView textViewTimeSaved_pm;
    TextView textViewBookings_completed_pm;
    TextView textViewServicesProvided_pm;
    TextView textViewMoneyMade_pm;
    TextView textViewMoneySpent_pm;

    TextView textViewFootsteps_allTime;
    TextView textViewTimeQueuing_allTime;
    TextView textViewTimeSaved_allTime;
    TextView textViewBookings_completed_allTime;
    TextView textViewServicesProvided_allTime;
    TextView textViewMoneyMade_allTime;
    TextView textViewMoneySpent_allTime;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private RequestQueue myRequestQueue;
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile);

        //Instantiate toolbar.
        Toolbar toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);

        //instantiate drawer.
        drawer = findViewById(R.id.drawer_layoutProfile);
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

        Log.d(TAG, "User Profile details");
        Log.d(TAG, "user_id:---------- "+user_id);
        Log.d(TAG,"name:------------- "+name);
        Log.d(TAG,"username:--------- "+ username);
        Log.d(TAG,"email: -----------"+email);
        Log.d(TAG,"profile:---------- "+ profile);
        Log.d(TAG,"rating:----------- "+ String.valueOf(rating));
        Log.d(TAG,"register:---------- "+ register_date);

        //Rating bar.
        RatingBar simpleRatingBar = findViewById(R.id.simpleRatingBar); // initiate a rating bar
        simpleRatingBar.setRating((float) 3.5); // set default rating
        simpleRatingBar.setIsIndicator(true); //Set the rating bar to view only.

        TextView profile_name = findViewById(R.id.textViewNameValue);
        profile_name.setText(name);
        Log.d("User_Profile: ","Name:"+ name);
        TextView profile_email = findViewById(R.id.textViewEmailValue);
        profile_email.setText(email);
        Log.d("User_Profile: ","email: "+ email);
        TextView register = findViewById(R.id.textViewRegisterDateValue);
        register.setText(register_date);
        Log.d("User_Profile", "register: "+ register_date);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        profile_pic = findViewById(R.id.profilePicture);
        textViewUsername = headerView.findViewById(R.id.TextViewUsername);
        textViewUserEmail = headerView.findViewById(R.id.textViewEmail);
        textViewUsername.setText(name);
        textViewUserEmail.setText(email);

        btnBookings = findViewById(R.id.btnBookings);
        btnServices = findViewById(R.id.btnServices);
        btnReceipts = findViewById(R.id.btnReceipts);
        /*Set on click listener to button btnBookings*/
        btnBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent future_bookings = new Intent(getApplicationContext(), FutureBookings.class);
                future_bookings.putExtra("user_id", user_id);
                startActivity(future_bookings);
            }
        });

        /*Set on click lister to button btnServices.*/
        btnServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBookings = new Intent(getApplicationContext(), bookings.class);
                startActivity(intentBookings);
            }
        });

        btnReceipts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReceipt = new Intent(getApplicationContext(), user_reciepts.class);
                startActivity(intentReceipt);
            }
        });

        profile_Image = headerView.findViewById(R.id.navProfilePicture);
        /*Set profile image*/
        if(profile !=""){
            Picasso.with(this)
                    .load(ServerDetails.PROFILE_DIR_URL+""+profile)
                    .resize(300,300).into(profile_Image);
        }

        /*Set profile image*/

        if(profile !=""){
            Picasso.with(User_Profile.this)
                    .load(ServerDetails.PROFILE_DIR_URL+""+profile)
                    .resize(120,120).into(profile_pic);
        }


        //Add menu toggle to toolbar.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        //Sync the rotation of the hanburger icon with the drawer.
        toggle.syncState();

       /* if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_QueueLover);
        }*/

        final Button btnDeleteAccount = findViewById(R.id.btn_delete_account);

        // REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        //Add action Listener to btnDeleteAccount.
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            String pWord = "";
            @Override
            public void onClick(View v) {

                /*Confirmation message box*/
                AlertDialog.Builder builder1 = new AlertDialog.Builder(User_Profile.this);
                builder1.setTitle("Confirmation Message");
                builder1.setMessage("Are you sure you want to deactivate your account?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(User_Profile.this);
                                builder.setTitle("Enter Password");


                                // Set up the input
                                final EditText input = new EditText(User_Profile.this);
                                // Specify the type of input expected;
                                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                builder.setView(input);

                                // Set up the buttons
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pWord = input.getText().toString();
                                        // CREATE NEW REQUEST
                                        request = new StringRequest(Request.Method.POST, ServerDetails.DELETE_ACCOUNT_URL, new Response.Listener<String>() {


                                            // HANDLE RESPONSES
                                            @Override
                                            public void onResponse(String response) {
                                                Log.i("tagconvertstr", "["+response+"]");
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);


                                                    if (jsonObject.names().get(0).equals("account_deactivated")) {

                                                        // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                                                        Toast.makeText(getApplicationContext(), jsonObject.getString("account_deactivated"), Toast.LENGTH_SHORT).show();
                                                        logged_in = false;
                                                        Intent redirect_to_main = new Intent(getApplicationContext(), MainActivity.class);
                                                        startActivity(redirect_to_main);

                                                    } else if(jsonObject.names().get(0).equals("query_fail")) {
                                                        Toast.makeText(getApplicationContext(), jsonObject.getString("query_fail"), Toast.LENGTH_SHORT).show();
                                                    } else if (jsonObject.names().get(0).equals("wrong_password")){

                                                        // DISPLAY ERROR RESPONSE TO THE ANDROID ACTIVITY SCREEN
                                                        Toast.makeText(getApplicationContext(), "Error" + jsonObject.getString("wrong_password"), Toast.LENGTH_SHORT).show();

                                                    }  else if (jsonObject.names().get(0).equals("error")){

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
                                                Toast.makeText(User_Profile.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
                                            }
                                        }) {

                                            @Override
                                            protected Map<String, String> getParams() {

                                                HashMap<String, String> hashMap = new HashMap<>();
                                                hashMap.put("delete", "");
                                                hashMap.put("user_id", user_id);
                                                hashMap.put("password", input.getText().toString());
                                                return hashMap;

                                            }
                                        };

                                        myRequestQueue.add(request);
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();

                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        final Button btnChangeDetails = findViewById(R.id.btn_change_details);

        btnChangeDetails.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intentUpdate = new Intent(getApplicationContext(), Update_User_Details.class);
                startActivity(intentUpdate);
            }
        });

        mAccountStat = new ArrayList<>();
        getStatistics(user_id);

         textViewFootsteps_pm = findViewById(R.id.textView_footsteps_past_month);
         textViewTimeQueuing_pm = findViewById(R.id.textView_time_queueing_past_month);
         textViewTimeSaved_pm = findViewById(R.id.textView_time_saved_past_month);
         textViewBookings_completed_pm = findViewById(R.id.textView_bookings_made_past_month);
         textViewServicesProvided_pm = findViewById(R.id.textView_services_provided_past_month);
         textViewMoneyMade_pm = findViewById(R.id.textView_money_made_past_month);
         textViewMoneySpent_pm = findViewById(R.id.textView_money_paid_past_month);

         textViewFootsteps_allTime = findViewById(R.id.textView_footsteps_all_time);
         textViewTimeQueuing_allTime = findViewById(R.id.textView_time_queueing_all_time);
         textViewTimeSaved_allTime = findViewById(R.id.textView_time_saved_all_time);
         textViewBookings_completed_allTime = findViewById(R.id.textView_bookings_made_all_time);
         textViewServicesProvided_allTime = findViewById(R.id.textView_services_provided_all_time);
         textViewMoneyMade_allTime  = findViewById(R.id.textView_money_made_all_time);
         textViewMoneySpent_allTime = findViewById(R.id.textView_money_paid_all_time);

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

    public void getStatistics(String user_id){
        Log.d("User_Profile", "Status: get statistics running...");
        apiInterface = RetrofitApiClient.getApiClient().create(RetrofitApiInterface.class);
        Call<List<AccountStat>> call = apiInterface.getStatistics(user_id);

        call.enqueue(new Callback<List<AccountStat>>() {

            @Override
            public void onResponse(Call<List<AccountStat>> call, retrofit2.Response<List<AccountStat>> response) {
                Log.d("User_Profile", "Account stats: "+response.toString());
                mAccountStat = response.body();
                Log.d("User_Profile", "Account stats : "+mAccountStat.toString());

                textViewFootsteps_pm.setText(String.valueOf(mAccountStat.get(0).getFootsteps_pm()));
                textViewTimeQueuing_pm.setText(mAccountStat.get(0).getTime_queuing_pm()+" hrs");
                textViewTimeSaved_pm.setText(mAccountStat.get(0).getTime_saved_pm()+" hrs");
                textViewBookings_completed_pm.setText(String.valueOf(mAccountStat.get(0).getBookings_completed()));
                textViewServicesProvided_pm.setText(String.valueOf(mAccountStat.get(0).getServices_provided_pm()));
                textViewMoneyMade_pm.setText("R"+String.valueOf(mAccountStat.get(0).getMoney_made_pm()));
                textViewMoneySpent_pm.setText("R"+String.valueOf(mAccountStat.get(0).getMoney_spent_pm()));

                textViewFootsteps_allTime.setText(String.valueOf(mAccountStat.get(0).getFootsteps_all_time()));
                textViewTimeQueuing_allTime.setText(mAccountStat.get(0).getTime_queuing_all_time()+" hrs");
                textViewTimeSaved_allTime.setText(mAccountStat.get(0).getTime_saved_all_time()+" hrs");
                textViewBookings_completed_allTime.setText(String.valueOf(mAccountStat.get(0).getBookings_completed_all_time()));
                textViewServicesProvided_allTime.setText(String.valueOf(mAccountStat.get(0).getServices_provided_all_time()));
                textViewMoneyMade_allTime.setText("R"+String.valueOf(mAccountStat.get(0).getMoney_made_all_time()));
                textViewMoneySpent_allTime.setText("R"+String.valueOf(mAccountStat.get(0).getMoney_spent_all_time()));
            }

            @Override
            public void onFailure(Call<List<AccountStat>> call, Throwable t) {
                Log.d("User_Profile", "Error on Account stats "+t.toString());
            }
        });
    }
}

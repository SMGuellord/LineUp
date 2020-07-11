package com.example.minzint.a4queue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class StartQueue extends AppCompatActivity {

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
    private String matched_username;
    private String matched_user_id;
    private String times;
    private String message;
    private String fee;

    private AdView mAdView;

    // ===================================================================
    // END OF VARIABLES


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_queue);

        // GET ATTRIBUTES FROM PREVIOUS ACTIVITIY
        Bundle bundle = getIntent().getExtras();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);

        user_id =pref.getString("user_id", null);
        name = pref.getString("name", null);
        username = pref.getString("username", null);
        email = pref.getString("email", null);
        profile = pref.getString("profile", null);
        rating = pref.getInt("rating", 0);
        register_date = pref.getString("register_date", null);
        logged_in = pref.getBoolean("logged_in", false);
        matched_username = bundle.getString("matched_username");
        matched_user_id = bundle.getString("matched_user_id");
        times = bundle.getString("times");
        message = bundle.getString("message");
        fee = bundle.getString("fee");

        final SeekBar numberUsers = findViewById(R.id.numberPeopleQueue);
        final TextView numberPeopleLink = findViewById(R.id.numberPeopleLink);

        // GET THE NUMBER OF PEOPLE IN THE QUEUE
        numberUsers.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                numberPeopleLink.setText(progressChangedValue + "");

            }
        });

        ImageView infoLinkPeopleQueue = findViewById(R.id.infoLinkPeopleQueue);

        // INFORMATION LINK
        infoLinkPeopleQueue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartQueue.this);
                alertDialogBuilder.setMessage("This represents the number of people currently standing in the queue. This information will be used to provide further statistics.");
                alertDialogBuilder.setPositiveButton("Got it",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        ImageView infoLinkStartQueue = findViewById(R.id.infoLinkStartQueue);

        // INFORMATION LINK
        infoLinkStartQueue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartQueue.this);
                alertDialogBuilder.setMessage("Pressing this button will officially begin the queue process.");
                alertDialogBuilder.setPositiveButton("Got it",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        Button startQueue = findViewById(R.id.startQueue);

        startQueue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // want to go the next activity for the time being
                Intent nextActivity = new Intent(getApplicationContext(), QueueProgress.class);
                nextActivity.putExtra("matched_user_name", matched_username);
                nextActivity.putExtra("matched_user_id", matched_user_id);
                nextActivity.putExtra("times", times);
                nextActivity.putExtra("message", message);
                nextActivity.putExtra("fee", fee);
                nextActivity.putExtra("number_people_queue", numberPeopleLink.getText().toString());

                startActivity(nextActivity);

            }
        });

        // mobile adverts
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    /**
     * SAVE INFORMATION TO GLOBAL VARIABLE
     * @param user_id
     * @param username
     */
    public void SaveDataToGlobalVariable(String user_id, String username)
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("user_name", username);
        editor.putString("user_id", user_id);

        editor.commit();

    }
}

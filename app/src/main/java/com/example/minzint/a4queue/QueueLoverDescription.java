package com.example.minzint.a4queue;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class QueueLoverDescription extends AppCompatActivity  implements  NavigationView.OnNavigationItemSelectedListener{

    // ===================================================================
    // VARIABLES

    private String name;
    private String email;
    private String isAvailable;
    private String userUsername;

    TextView username;
    TextView userEmail;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_lover_description);

        //Instantiate toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //instantiate drawer.
        drawer = findViewById(R.id.drawer_layoutQueueLoverDesc);
        //
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        userUsername  = bundle.getString("username");
        email = bundle.getString("email");
        isAvailable = bundle.getString("available");

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        username = headerView.findViewById(R.id.TextViewUsername);
        userEmail = headerView.findViewById(R.id.textViewEmail);
        username.setText(name);
        userEmail.setText(email);

        //Add menu toggle to toolbar.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        //Sync the rotation of the hanburger icon with the drawer.
        toggle.syncState();

       /* if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_QueueLover);
        }*/


        TextView textViewUserName = findViewById(R.id.textViewUserName);
        //Get the place name from the intent.
        final String Username = getIntent().getStringExtra("Name");
        final String cardDesc = getIntent().getStringExtra("Description");
        //Set the place name
        textViewUserName.setText(Username);

        EditText editTextDesc = findViewById(R.id.editTextQueueLoverDescription);
        //Set booking card description.
        editTextDesc.setText(cardDesc);

        //Set editTextDesc to readOnly.
        editTextDesc.setEnabled(false);

        Button btnSelect = findViewById(R.id.btnSelectQueueLover);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Build confirmation dialog.
                final AlertDialog.Builder builder = new AlertDialog.Builder(QueueLoverDescription.this);
                builder.setMessage("Book for "+Username +"?");
                builder.setCancelable(true);

                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(QueueLoverDescription.this, SelectQueueLover.class);
                        //Redirect to the booking card activity.
                        startActivity(intent);

                        //Display confirmation message.
                        Toast.makeText(QueueLoverDescription.this, Username+" booked successfully!", Toast.LENGTH_LONG).show();
                    }
                });

                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                //Display confirmation dialog.
                alertDialog.show();
            }
        });

    }

    private void putExtraValues(Intent intent){
        intent.putExtra("name", name);
        intent.putExtra("username", userUsername);
        intent.putExtra("email", email);
        intent.putExtra("available", isAvailable);
    }

    //Adding navigation item listener.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.nav_QueueLover:
                Intent intentQueueLover = new Intent(getApplicationContext(), QueueLover.class);
                putExtraValues(intentQueueLover);
                startActivity(intentQueueLover);
                break;
            case R.id.nav_QueueHater:
                Intent intentQueueHater = new Intent(getApplicationContext(), QueueHater.class);
                putExtraValues(intentQueueHater);
                startActivity(intentQueueHater);
                break;*/
            case R.id.nav_Profile:
                Intent intentProfile = new Intent(getApplicationContext(), User_Profile.class);
                putExtraValues(intentProfile);
                startActivity(intentProfile);
                break;
            case R.id.nav_Statistics:
                Intent intentStatistics = new Intent(getApplicationContext(), Statistics.class);
                putExtraValues(intentStatistics);
                startActivity(intentStatistics);
                break;
            case R.id.nav_Settings:
                Intent intentSettings = new Intent(getApplicationContext(), Settings.class);
                putExtraValues(intentSettings);
                startActivity(intentSettings);
                break;
            case R.id.nav_Help:
                Intent intentHelp = new Intent(getApplicationContext(), help_introduction.class);
                putExtraValues(intentHelp);
                startActivity(intentHelp);
                break;
            case R.id.nav_Logout:
                Intent intentLogout = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intentLogout);
                break;

        }

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

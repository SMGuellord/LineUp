package com.example.minzint.a4queue;

import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static java.util.Arrays.asList;

public class SelectQueueLover extends AppCompatActivity  implements  NavigationView.OnNavigationItemSelectedListener{

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
        setContentView(R.layout.activity_select_queue_lover);

        //Instantiate toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //instantiate drawer.
        drawer = findViewById(R.id.drawer_layoutSelectQueueLover);
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

        ListView listView = findViewById(R.id.listViewQueueLovers);
        final ArrayList<String> queueLovers = new ArrayList<>(asList("Queue Lover 1", "Queue Lover 2", "Queue Lover 3", "Queue Lover 4","Queue Lover 5","Queue Lover 6","Queue Lover 7","Queue Lover 8","Queue Lover 9", "Queue Lover 10"));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_expandable_list_item_1, queueLovers);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SelectQueueLover.this, QueueLoverDescription.class);
                intent.putExtra("Name", queueLovers.get(i));
                intent.putExtra("Description", queueLovers.get(i)+" Description goes here.");
                startActivity(intent);
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

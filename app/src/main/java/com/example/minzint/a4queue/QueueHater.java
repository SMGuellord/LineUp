package com.example.minzint.a4queue;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueueHater extends AppCompatActivity  implements  NavigationView.OnNavigationItemSelectedListener{

    // ===================================================================
    // VARIABLES

    private String name;
    private String email;
    private String isAvailable;
    private String userUsername;
    private ArrayList<String> uName;
    private ArrayList<String> uUsername;
    private ArrayList<String> uEmail;
    TextView username;
    TextView userEmail;

    private RequestQueue myRequestQueue;
    private static final String URL = "http://"+ServerDetails.SERVER_ADDRESS+":"+ServerDetails.PORT;
    private StringRequest request;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_hater);

        //Instantiate toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        uName = new ArrayList<>();
        uUsername = new ArrayList<>();
        uEmail = new ArrayList<>();
        // REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        //instantiate drawer.
        drawer = findViewById(R.id.drawer_layoutQueueHater);
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

       final ListView listView = findViewById(R.id.listViewQueueLovers);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }

        });

        Button btnSelectQueueLover = findViewById(R.id.btnSelectQueueLover);

        btnSelectQueueLover.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Toast.makeText(QueueHater.this, "Retrieving queue lovers", Toast.LENGTH_SHORT).show();
                // CREATE NEW REQUEST
                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                    // HANDLE RESPONSES
                    @Override
                    public void onResponse(String response) {

                        JSONArray jsonArray;
                        try {
                            jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {

                                String strName = jsonArray.getJSONObject(i).getString("Name");
                                Log.i("name", strName);
                                String strUsername = jsonArray.getJSONObject(i).getString("Username");
                                Log.i("username", strUsername);
                                String strEmail = jsonArray.getJSONObject(i).getString("Email");
                                Log.i("Email", strEmail);
                                uName.add(strName);
                                uUsername.add(strUsername);
                                uEmail.add(strEmail);
                            }

                            ArrayAdapter<String> arrayAdapter;
                            arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_expandable_list_item_1, uName );
                            listView.setAdapter(arrayAdapter);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {

                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("submit", "");

                        return hashMap;

                    }
                };

                myRequestQueue.add(request);

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

package com.example.minzint.a4queue;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.minzint.a4queue.models.AddressDetails;
import com.example.minzint.a4queue.models.PlaceInfo;
import com.example.minzint.a4queue.models.QueueInfo;
import com.example.minzint.a4queue.utilities.NavigationOptions;
import com.example.minzint.a4queue.utilities.RetrofitApiClient;
import com.example.minzint.a4queue.utilities.RetrofitApiInterface;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class Statistics extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, AdapterView.OnItemSelectedListener {

    // USER DETAILS VARIABLES
    private String user_id;
    private String name;
    private String username;
    private String email;
    private String profile;
    private int rating;
    private String register_date;
    boolean logged_in = false;
    private Double latMatched;
    private Double lngMatched;
    private String addrMatched;

    // NAVIGATION BAR DETAILS
    TextView textViewUsername;
    TextView textViewUserEmail;
    ImageView profile_Image;

    // NAVIGATION BAR DETAILS
    private DrawerLayout drawer;
    private NavigationView navigationView;

    // LOGGING AND PERMISSIONS
    private static final String TAG = "Statistics";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //South Africa South-West to North-East latitude and longitude range.
    private static final LatLngBounds BOUNDS_GREATER_SOUTH_AFRICA = new LatLngBounds(
            new LatLng(-33.918861, 18.423300), new LatLng(-25.731340, 28.218370));
    private static final int PLACE_PICKER_REQUEST = 1;

    //application Widgets
    private AutoCompleteTextView mSearchText;

    //private ImageView mGps;
    //private ImageView mInfo;
    private ImageView cancel;
    private Button mPlacePicker;

    // LOCATION AND MAPPING VARIABLES
    private boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutoCompleteAdapter mPlaceAutocompleteAdapter;

    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private Marker mMarker;
    private List<Marker> markerList;

    // BUTTON FUNCTIONALITY
    private Button matchUsers;
    private Button AutoMatch;
    private Button btnFindBookins;

    // AUTO MATCH FUNCTIONALITY
    private String[] AutoMatchuserDetails;

   /* GeoDataClient wraps our service connection to Google Play services and provides access
    to the Google Places API for Android.*/

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // USER LOCATION ATTRIBUTES
    private double userLongitude;
    private double userLatitute;
    private String userAddress;

    // API INTERFACING
    private List<QueueInfo> queueInfo;
    private RetrofitApiInterface apiInterface;
    private String context;

    // VARIABLES FOR THE DROP DOWN MENU
//    private Spinner spinner;
//    private static final String[] paths = {"Current Location", "Information"};

    /////////////////////////////////////////////////////////////////////
    // VARIABLES FOR THE BOTTOM UP SLIDER

    @BindView(R.id.place_picker)
    Button btnPlacePicker;

    @BindView(R.id.AutoMatch)
    Button btnAutomatch;

    @BindView(R.id.matchUser)
    Button btnMatchUser;

    @BindView(R.id.bottom_sheet_Text)
    TextView textViewBottomSheet;

    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;

    // API ATTRIBUTES

    private RequestQueue myRequestQueue;
    private StringRequest request;

    // BOOKING ATTRIBUTES

    private String booking_id = "";
    private String matched_user_id = "";
    private String matched_username = "";
    private String message = "";
    private String fee = "";
    private double booking_location_lat = -32.684412;
    private double booking_location_lng = 26.236719;
    private String mobile_booking_address  = "";
    private String times = "";
    private String isMatched = "";

    private String notification_end = "";
    private String isEnded = "";

    private Place place;

    // LOCATION VARIABLES
    private double user_selected_lat;
    private double user_selected_long;
    private String user_selected_address;

    // USER LOCATION
    private double user_latitude;
    private double user_longatude;

    private Timer timerAsyncAndLocation;
    TimerTask CHECK_DETAILS = null;

    final ManageAsyncAndLocation myManageAsyncAndLocation = new ManageAsyncAndLocation();

    /////////////////////////////////////////////////////////////////////
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // CHECK FOR INTERNET CONNECTION
        boolean isNetworkAvailable = isNetworkAvailable();

        //Instantiate markerList.
        markerList = new ArrayList<>();

        //  HANDLE NO NETWORK CONNECTION
        if (isNetworkAvailable==false){

            // MOVE TO NEXT ACTIVITY
            Intent nextActivity = new Intent(getApplicationContext(), Offline_Notify.class);
            startActivity(nextActivity);

        }

        //Add bottom sheet behavior.
        ButterKnife.bind(this);

        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        textViewBottomSheet.setText("Close");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        textViewBottomSheet.setText("Find user");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        btnAutomatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    // SAVE THE MATCH
                    SaveMatchToDatabase();

                    String[] myArray = getQueueInformationFromGlobalVariable();

                    // DISPLAY MATCH DETAILS
                    Intent nextActivity = new Intent(getApplicationContext(), post_match_deetails.class);
                    nextActivity.putExtra("mobile_booking_location_lat", myArray[0]);
                    nextActivity.putExtra("mobile_booking_location_lng", myArray[1]);
                    nextActivity.putExtra("mobile_booking_address", myArray[2]);
                    Date currentTime = Calendar.getInstance().getTime();
                    nextActivity.putExtra("time", currentTime.getHours() + ":" + currentTime.getMinutes());
                    startActivity(nextActivity);

                    // FIND USER BEFORE THE PERSON EVEN ASKS FOR IT
                    // FIND EXTRA DETAILS IN CASE THE USER REQUESTS AGAIN
                    AutoMatchuserDetails = FindRandomUserDetails();

                }
        });

        btnPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(Statistics.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d(TAG, "onClick: googlePlayServicesRepairableException: " + e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d(TAG, "onClick: googlePlayServicesRepairableException: " + e.getMessage());
                    Log.d(TAG, "onClick: googlePlayServicesRepairableException: " + e.getMessage());
                }
            }
        });

        //hide keyboard after searching
        //hideSoftKeyboard();

        btnMatchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Update user location in the database
                updateUserLocation(user_latitude, user_longatude);

                // CREATE QUEUE
                // createQueue();

                // GO TO THE MATCHES ACTIVITY
                Intent nextActivity = new Intent(getApplicationContext(), User_Match.class);
                startActivity(nextActivity);

            }
        });

        // REMOVE THE SEARCH FUNCTIONALITY
        cancel = findViewById(R.id.ic_cancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
            }
        });

        // INITIALISE REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        //=================================================================================
        // THE DROP DOWN MENU

     /*   spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Statistics.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);*/

        //=================================================================================

        //=================================================================================
        // ATTRIBUTE DEFINTION

        mSearchText = findViewById(R.id.input_search);
        mSearchText.setTextIsSelectable(true);
        mSearchText.setFocusable(true);
        mSearchText.setSingleLine();
        //mGps = findViewById(R.id.ic_gps);
        //mInfo = findViewById(R.id.place_info);
        mPlacePicker = findViewById(R.id.place_picker);

        //=================================================================================

        // GET PERMISSION FOR LOCATION SERVICES
        getLocationPermission();

        //=================================================================================
        // GET THE DETAILS PASSED THROUGH FROM PREVIOUS ACTIVITY

        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);

        user_id =pref.getString("user_id", null);
        name = pref.getString("name", null);
        username = pref.getString("username", null);
        email = pref.getString("email", null);
        profile = pref.getString("profile", null);
        rating = pref.getInt("rating", 0);
        register_date = pref.getString("register_date", null);
        logged_in = pref.getBoolean("logged_in", false);
        Bundle bundle;

        //=================================================================================

        Intent intent = getIntent();
        if(intent.hasExtra("context")){
            bundle = getIntent().getExtras();
            context = bundle.getString("context");
            if(context != null && context.equals("match_details")){
                latMatched = Double.parseDouble(bundle.getString("latitude"));
                lngMatched = Double.parseDouble(bundle.getString("longitude"));
                addrMatched = bundle.getString("address");
            }

        }

        //=================================================================================
        // LOGGING

        Log.d(TAG, "user_id:---------- " + user_id);
        Log.d(TAG, "name:------------- " + name);
        Log.d(TAG, "username:--------- " + username);
        Log.d(TAG, "email: -----------" + email);
        Log.d(TAG, "profile:---------- " + profile);
        Log.d(TAG, "rating:----------- " + String.valueOf(rating));
        Log.d(TAG, "register:---------- " + register_date);

        //=================================================================================

        //=================================================================================
        //Instantiate toolbar and drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // define navigation bar attributes
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        // set navigation bar attributes
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

       /* if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_QueueLover);
        }*/

        //=================================================================================

        //=================================================================================
        // HANDLE MATCH FUNCTIONALITY

        matchUsers = findViewById(R.id.matchUser);
        matchUsers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Update user location in the database
                updateUserLocation(user_latitude, user_longatude);

                // GO TO THE MATCHES ACTIVITY
                Intent nextActivity = new Intent(getApplicationContext(), User_Match.class);
                startActivity(nextActivity);

            }

        });

        //=================================================================================
        // HANDLE AUTOMATCH FUNCTIONALITY

        // FIND USER BEFORE THE PERSON EVEN ASKS FOR IT
        AutoMatchuserDetails = FindRandomUserDetails();

        AutoMatch = findViewById(R.id.AutoMatch);

        AutoMatch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                    // SAVE THE MATCH
                    // MUST BE DONE THIS WAY BECAUSE OF ASYC ISSUES
                    SaveMatchToDatabase();

                    String[] myArray = getQueueInformationFromGlobalVariable();

                    // DISPLAY MATCH DETAILS
                    Intent nextActivity = new Intent(getApplicationContext(), post_match_deetails.class);
                    nextActivity.putExtra("mobile_booking_location_lat", myArray[0]);
                    nextActivity.putExtra("mobile_booking_location_lng", myArray[1]);
                    nextActivity.putExtra("mobile_booking_address", myArray[2]);
                    Date currentTime = Calendar.getInstance().getTime();
                    nextActivity.putExtra("time", currentTime.getHours() + ":" + currentTime.getMinutes());
                    startActivity(nextActivity);

                    // FIND USER BEFORE THE PERSON EVEN ASKS FOR IT
                    // FIND EXTRA DETAILS IN CASE THE USER REQUESTS AGAIN
                    AutoMatchuserDetails = FindRandomUserDetails();

                }

        });

        //=================================================================================

        //=================================================================================
        // REFRESH THE ACTIVITY

        myManageAsyncAndLocation.execute();

        // WANT THIS TO RUN EVERY THREE MINUTES

        timerAsyncAndLocation = new Timer();
        CHECK_DETAILS = new TimerTask() {
            @Override
            public void run() {

                // ASYNC CLASS
                checkNotitficationStatus();


            }
        };

        timerAsyncAndLocation.schedule(CHECK_DETAILS, 60000, 60000);


        //=================================================================================

        //=================================================================================
        // MODAL POPUPS FOR ADDITIONAL INFORMATION

        ImageView infoLinkLocation = findViewById(R.id.infoLinkLocation);

        // INFORMATION LINK
        infoLinkLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Statistics.this);
                alertDialogBuilder.setMessage("Select the queuing location.");
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

        ImageView infoLinkAutoMatch = findViewById(R.id.infoLinkAutoMatch);

        // INFORMATION LINK
        infoLinkAutoMatch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Statistics.this);
                alertDialogBuilder.setMessage("Automatically match with another user that will stand in the queue on your behalf.");
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

        ImageView infoLinkMatchUser = findViewById(R.id.infoLinkMatchUser);

        // INFORMATION LINK
        infoLinkMatchUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Statistics.this);
                alertDialogBuilder.setMessage("Find a specific user to stand in the queue on your behalf.");
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

        //=================================================================================


        //=================================================================================
        // DEFINE A SETTINGS ROW IN THE DATABASE FOR FIRST TIME USER
        CreateSettingsRow();

        //=================================================================================

        matchUsers.setEnabled(false);
        AutoMatch.setEnabled(false);
        matchUsers.setBackgroundColor(getResources().getColor(R.color.light_gray));
        AutoMatch.setBackgroundColor(getResources().getColor(R.color.light_gray));

    }


    /**
     * CHECK IF USER IS ONLINE
     * @return  boolean - true or false
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * SAVE A USER MATCH TO THE DATABASE FOR PERSISTENCE
     */
    private void SaveMatchToDatabase()
    {

        // CREATE NEW REQUEST
        request = new StringRequest(Request.Method.POST, ServerDetails.MATCHING_URL, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {
                Log.i("onResponse: ",response);
                JSONObject jsonObject;

                try {
                    jsonObject = new JSONObject(response);

                    if(jsonObject == null){

                        Toast.makeText(Statistics.this, "Server not responding. Registration failed!", Toast.LENGTH_SHORT).show();
                    }

                    if (jsonObject.names().get(0).equals("success")) {

                        // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                        // Toast.makeText(getApplicationContext(), "Notification sent to " + AutoMatchuserDetails[2] + " ,Please stand by for his response", Toast.LENGTH_SHORT).show();

                    } else {

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

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                String[] myArray = getQueueInformationFromGlobalVariable();
                Date currentTime = Calendar.getInstance().getTime();

                Date now = new Date();
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                String mysqlDateString = formatter.format(now);

                SaveDataToGlobalVariable(AutoMatchuserDetails[1], AutoMatchuserDetails[2], currentTime.getHours() + ":" + currentTime.getMinutes(), "I need your assistance", 100 + "");

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("user_id", user_id);
                hashMap.put("username", username);
                hashMap.put("matched_id", AutoMatchuserDetails[1]);
                hashMap.put("matched_username", AutoMatchuserDetails[2]);
                hashMap.put("dates", mysqlDateString + "");
                hashMap.put("times", currentTime.getHours() + ":" + currentTime.getMinutes());
                hashMap.put("mobile_times", currentTime.getHours() + ":" + currentTime.getMinutes());
                hashMap.put("message", "I need your assistance");
                hashMap.put("lat", Float.parseFloat(myArray[0]) + "");
                hashMap.put("lng", Float.parseFloat(myArray[1]) + "");
                hashMap.put("user_selected_address", myArray[2]);
                hashMap.put("Fee", 100 + "");
                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

    /**
     * FINDS A RANDOM USER WITHIN GIVEN BOUNDS FOR THE AUTOMATCH FEATURE
     * @return
     */
    public String[] FindRandomUserDetails() {

            final String[] AutoMatchuserDetails = new String[5];

            request = new StringRequest(Request.Method.POST, ServerDetails.FIND_RANDOM_USER, new Response.Listener<String>() {

                // HANDLE RESPONSES
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);


                        if (jsonObject.names().get(0).equals("find_success")) {

                            final String id = jsonObject.getString("id");
                            final String name = jsonObject.getString("name");

                            // search again if found current user
                            if (name.equals(username)) {

                                FindRandomUserDetails();

                            }

                            // SAVE TO ARRAY
                            AutoMatchuserDetails[1] = id;
                            AutoMatchuserDetails[2] = name;

                        } else {

                            // DISPLAY ERROR RESPONSE TO THE ANDROID ACTIVITY SCREEN
                            //Toast.makeText(getApplicationContext(), "Error" + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        // CATCH ERRORS
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // DO NOTHING
                    //Toast.makeText(Statistics.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("GET_USER_DETAILS", "");
                    return hashMap;

                }
            };

            myRequestQueue.add(request);

            return AutoMatchuserDetails;

        }

    private boolean acceptedNoti = false;

    public void showNotificationAccepted()
    {

        final Snackbar snackbar = Snackbar
                .make(drawer, "Have you started queuing for " + matched_username + " yet?", Snackbar.LENGTH_LONG);

        // Set an action on it, and a handler
        snackbar.setAction("Start Queue", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // START THE NEXT ACTIVITY
                // START A QUEUE SESSION
                Intent intent = new Intent(getApplicationContext(), StartQueue.class);
                intent.putExtra("matched_username", matched_username);
                intent.putExtra("matched_user_id", matched_user_id);
                intent.putExtra("times", times);
                intent.putExtra("message", message);
                intent.putExtra("fee", fee);
                intent.putExtra("mobile_booking_address", mobile_booking_address);
                startActivity(intent);

            }
        });

        snackbar.setActionTextColor(Color.GREEN);
        snackbar.show();

    }

    /**
     * GET THE CURRENT MATCH STATUS AND TAKE APPROPRIATE ACTIONS IF NEEDED
     */
    public void checkMatchedStatus() {

            request = new StringRequest(Request.Method.POST, ServerDetails.MATCHING_CHECK_URL, new Response.Listener<String>() {

                // HANDLE RESPONSES
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);


                        if (jsonObject.names().get(0).equals("match_success")) {

                            // GET VALUES
                            booking_id = jsonObject.getString("id");
                            matched_user_id = jsonObject.getString("user_id");
                            matched_username = jsonObject.getString("username");
                            message = jsonObject.getString("details");
                            fee = jsonObject.getString("fee_per_hr");
                            times = jsonObject.getString("mobile_times");
                            booking_location_lat = Double.parseDouble(jsonObject.getString("mobile_booking_location_lat"));
                            booking_location_lng = Double.parseDouble(jsonObject.getString("mobile_booking_location_lng"));
                            mobile_booking_address = jsonObject.getString("address");
                            isMatched = jsonObject.getString("isMatched");
                            String isStarted = jsonObject.getString("isStarted");
                            String isCompleted = jsonObject.getString("isCompleted");
                            String date_created = jsonObject.getString("date_created");
                            String isSwap = jsonObject.getString("isSwap");
                            String isEnded = jsonObject.getString("isEnded");

                            // SAVE DETAILS TO GLOBAL VARIABLES
                            SaveBookingToGlobal(booking_id, mobile_booking_address, date_created, isMatched, isCompleted, isStarted, isEnded, isSwap, fee, times);
                            SaveDataToGlobalVariableQueue(booking_location_lat + "", booking_location_lng + "", mobile_booking_address);

                            // FORMAT TIME
                            String[] segments = times.split(":");
                            String hour = segments[0];
                            String minute = segments[1];

                            // GET THE USERS DATE
                            Date myDate;

                            if ((segments[0] != null) && (segments[1] != null)) {

                                int hourInt = Integer.parseInt(hour);
                                int minuteInt = Integer.parseInt(minute);

                                DecimalFormat myFormatter = new DecimalFormat("#00.###");
                                myFormatter.setDecimalSeparatorAlwaysShown(false);
                                myFormatter.format(hourInt);
                                myFormatter.format(minuteInt);

                                // THE BOOKING DATE
                                myDate = new Date(2018, 07, 15, hourInt, minuteInt);

                            } else {

                                myDate = new Date(2018, 07, 15, 6, 0);

                            }

                            // GET THE CURRENT DATE AND TIME
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date currentDate = new Date();

                                // FOR TESTING
                                // Toast.makeText(getApplicationContext(), currentDate.toString(), Toast.LENGTH_SHORT).show();

                                // ONLY RUN THIS IF THE BOOKING IS WITHIN 1KM FROM THE USER CURRENT LOCATION
                                //double distanceApartKM = distance(booking_location_lat, booking_location_lng, mobile_location_lat, mobile_location_lng, 'K');
                                int distanceApartKMInteger = 100;

                                // ONLY RUN IF THE USER HAS ACCEPTED THE MATCH
                                if (isMatched.equals("1")) {

                                    if (distanceApartKMInteger < 100000) {

                                        if (isStarted.equals("1")) {

                                         acceptedNoti = true;

                                        }else { // IF ALREADY STARTED

                                            if (isCompleted.equals("0")){

                                                // STOP THE TIMER
                                                timerAsyncAndLocation.cancel();
                                                timerAsyncAndLocation.purge();

                                                CHECK_DETAILS.cancel();

                                                    // want to go the next activity for the time being
                                                    Intent nextActivity = new Intent(getApplicationContext(), QueueProgress.class);
                                                    nextActivity.putExtra("matched_username", matched_username);
                                                    nextActivity.putExtra("matched_user_id", matched_user_id);
                                                    nextActivity.putExtra("times", times);
                                                    nextActivity.putExtra("message", message);
                                                    nextActivity.putExtra("fee", fee);
                                                    // NEED TO GET THIS VALUE FROM QUEUE RECORDS
                                                    nextActivity.putExtra("number_people_queue", "0");

                                                    startActivity(nextActivity);


                                            }
                                        }
                                    }

                                } else {

                                    // FOR TESTING
                                    // Toast.makeText(getApplicationContext(), "I am suppoed to run match details", Toast.LENGTH_SHORT).show();

                                        // START THE NEXT ACTIVITY
                                        // SHOW THE USER THAT THEY HAVE A MATCH
                                        Intent intent = new Intent(getApplicationContext(), match_details.class);
                                        intent.putExtra("matched_username", matched_username);
                                        intent.putExtra("matched_user_id", matched_user_id);
                                        intent.putExtra("times", times);
                                        intent.putExtra("message", message);
                                        intent.putExtra("fee", fee);
                                        intent.putExtra("matched", "no");
                                        startActivity(intent);


                                }

                            // SAVE DATE TO A GLOBAL VARIABLE
                            SaveDataToGlobalVariable(matched_user_id, matched_username, times, message, fee);

                        } else {

                            // DISPLAY ERROR RESPONSE TO THE ANDROID ACTIVITY SCREEN
                            //Toast.makeText(getApplicationContext(), "Error" + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        // CATCH ERRORS
                        e.printStackTrace();
                    }

                    if (acceptedNoti == true){

                        showNotificationAccepted();

                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    // DO NOTHING
                    //Toast.makeText(Statistics.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("check_match_status", "");
                    hashMap.put("matched_id", user_id);
                    return hashMap;

                }
            };

            myRequestQueue.add(request);

    }

    private boolean noti50 = false;
    private boolean noti75 = false;
    private boolean noti90 = false;
    private boolean notiAccept = false;
    private boolean notiEnd = false;

    public void showNotifications(final String matched_usernamee, final String matched_id){


        if (noti50 == true){

            final Snackbar snackbar = Snackbar
                    .make(drawer, matched_usernamee + " is now 50% through the queue", Snackbar.LENGTH_INDEFINITE);

            // Set an action on it, and a handler
            snackbar.setAction("Got it", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            snackbar.setActionTextColor(Color.GREEN);
            snackbar.show();


        }else if (noti75 == true){

            final Snackbar snackbar = Snackbar
                    .make(drawer, matched_usernamee + " is now 75% through the queue", Snackbar.LENGTH_INDEFINITE);

            // Set an action on it, and a handler
            snackbar.setAction("Got it", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            snackbar.setActionTextColor(Color.GREEN);
            snackbar.show();


        }else if (noti90 == true){

            final Snackbar snackbar = Snackbar
                    .make(drawer, matched_usernamee + " is now 90% through the queue, please go meet up!", Snackbar.LENGTH_INDEFINITE);

            // Set an action on it, and a handler
            snackbar.setAction("Got it", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            snackbar.setActionTextColor(Color.GREEN);
            snackbar.show();


        }else if (notiEnd == true){

            final Snackbar snackbar = Snackbar
                    .make(drawer, matched_usernamee + " has requested to end the queue session", Snackbar.LENGTH_INDEFINITE);

            // Set an action on it, and a handler
            snackbar.setAction("Review", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), user_review.class);
                    intent.putExtra("matched_id", matched_id);
                    intent.putExtra("matched_username", matched_usernamee);
                    startActivity(intent);


                }
            });

            snackbar.setActionTextColor(Color.GREEN);
            snackbar.show();


        }else if (notiAccept == true){

            // NOTIFY THE USER THAT THE MATCH HAS BEEN ACCEPTED
            final Snackbar snackbar = Snackbar
                    .make(drawer, matched_usernamee + " has accepted your queue request!", Snackbar.LENGTH_INDEFINITE);

            // Set an action on it, and a handler
            snackbar.setAction("Got it", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            snackbar.setActionTextColor(Color.GREEN);
            snackbar.show();

        }

    }

    /**
     * CHECK IF THERE ARE ANY NOTIFICATIONS AVAILABLE FOR THE USER
     */
    public void checkNotitficationStatus() {

            request = new StringRequest(Request.Method.POST, ServerDetails.CHECK_NOTIFICATION, new Response.Listener<String>() {

                // HANDLE RESPONSES
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);


                        if (jsonObject.names().get(0).equals("success")) {

                            // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                            // Toast.makeText(getApplicationContext(), "You have a match", Toast.LENGTH_SHORT).show();

                            final String matched_id = jsonObject.getString("matched_id");
                            final String matched_usernamee = jsonObject.getString("matched_username");
                            String isMatched = jsonObject.getString("isMatched");
                            final String mobile_notification_50 = jsonObject.getString("mobile_notification_50");
                            final String mobile_notification_75 = jsonObject.getString("mobile_notification_75");
                            final String mobile_notification_90 = jsonObject.getString("mobile_notification_90");
                            notification_end = jsonObject.getString("notification_end");
                            isEnded = jsonObject.getString("isEnded");

                            boolean update50 = false;
                            boolean update75 = false;
                            boolean update90 = false;
                            boolean updateAccepted = false;
                            boolean updateQueueEnd = false;

                            if (isMatched.equals("1")) {
                                if (isEnded.equals("1")) {

                                    update50 = false;
                                    update75 = false;
                                    update90 = false;
                                    updateAccepted = true;

                                    if ((mobile_notification_50.equals("1"))) {
                                        updateAccepted = false;
                                        update50 = true;
                                    }
                                    if (mobile_notification_75.equals("1")) {
                                        updateAccepted = false;
                                        update50 = false;
                                        update75 = true;
                                    }
                                    if (mobile_notification_90.equals("1")) {
                                        updateAccepted = false;
                                        update50 = false;
                                        update75 = false;
                                        update90 = true;
                                    }

                                    // MOST IMPORTANT MUST BE LAST TO BE EXECUTED IN CODE BLOCK
                                    if (notification_end.equals("1")) {
                                        updateAccepted = false;
                                        update50 = false;
                                        update75 = false;
                                        update90 = false;
                                        updateQueueEnd = true;
                                    }

                                if ((update50) && (!updateQueueEnd)){

                                  noti50 = true;

                                }
                                if ((update75) && (!updateQueueEnd)){

                                    noti75 = true;


                                }
                                if ((update90) && (!updateQueueEnd)){

                                        noti90 = true;

                                }
                                if ((updateAccepted) && (!updateQueueEnd)){

                                   notiAccept = true;

                                }else if (updateQueueEnd){

                                         notiEnd = true;

                                }

                                }
                            }

                            // WAIT FOR EVERYTHING TO LOAD PROPERLY
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            showNotifications(matched_usernamee, matched_id);

                            } else {

                                // DISPLAY ERROR RESPONSE TO THE ANDROID ACTIVITY SCREEN
                                //Toast.makeText(getApplicationContext(), "Error" + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();

                            }

                    } catch (JSONException e) {
                        // CATCH ERRORS
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // DO NOTHING
                    //Toast.makeText(Statistics.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("check_notification_status", "");
                    hashMap.put("user_id", user_id);
                    return hashMap;

                }
            };

            myRequestQueue.add(request);

    }

    /**
     * INITIALIZE THE MAP
     */
    private void init() {
        Log.d(TAG, "init: initializing");

        // Instantiate google api client for place detection.
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        //Add item click listener to mSearchText.
        mSearchText.setOnItemClickListener(mAutoCompleteClickListner);

        mGeoDataClient = Places.getGeoDataClient(this);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);

        //Auto complete place adapter to inflate place suggestions list.
        mPlaceAutocompleteAdapter = new PlaceAutoCompleteAdapter(Statistics.this, mGeoDataClient, BOUNDS_GREATER_SOUTH_AFRICA, null);
        mPlaceAutocompleteAdapter.setBounds(BOUNDS_GREATER_SOUTH_AFRICA);
        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                // execute searching process.
//                 geoLocate();

                return false;
            }
        });


        /* NO LONGER USED HERE

        //Set on click action listener to gps icon.
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                //Redirect to current device location
                getDeviceLocation();
            }
        });

        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked place info");
                try {
                    if (mMarker.isInfoWindowShown()) {
                        mMarker.hideInfoWindow();
                    } else {
                        if(mMarker.getTitle() != null){
                            Log.d(TAG, "onclick: place info: " + mPlace.toString());

                            mMarker.showInfoWindow();
                        }


                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "onClick: NullPointerException: " + e.getMessage());
                }
            }
        });

        */

        /*
        ---------------------------Nearby place picker--------------------------------------------
         */
        //Add onclick action listener to mPlacePicker widget.
        mPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(Statistics.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d(TAG, "onClick: googlePlayServicesRepairableException: " + e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d(TAG, "onClick: googlePlayServicesRepairableException: " + e.getMessage());
                    Log.d(TAG, "onClick: googlePlayServicesRepairableException: " + e.getMessage());
                }

            }
        });
        //hide keyboard after searching
        //hideSoftKeyboard();
    }

    /**
     * GET DETAILS FROM USER INTERACTION
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(Statistics.this, data);


                user_selected_lat = (float) place.getLatLng().latitude;
                user_selected_long = (float) place.getLatLng().longitude;
                user_selected_address = place.getAddress().toString();

                SaveDataToGlobalVariableQueue(user_selected_lat + "",user_selected_long + "", user_selected_address);

                // CREATE QUEUE
                createQueue();

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallBack);
            }
        }
    }

    /**
     * get geo Location.
     */
    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(Statistics.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);

        } catch (IOException e) {
            Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location " + address.toString());
            //move camera to the search input location.

            user_selected_lat = address.getLatitude();
            user_selected_long = address.getLongitude();
            user_selected_address = address.getAddressLine(0);

            SaveDataToGlobalVariableQueue(user_selected_lat + "", user_selected_long + "", user_selected_address);

            Log.d(TAG, "user lat "+user_latitude);
            Log.d(TAG, "user lng "+user_longatude);
            Log.d(TAG, "user lat "+user_selected_address);
            createQueue();

            textViewBottomSheet.setText("Find user at " + user_selected_address + "?");

            Log.d("Statistics", " Place details: "+ address.getLatitude() +" "+address.getLongitude()+" "+address.getAddressLine(0));
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
            AddressDetails addressDetails = new AddressDetails(address.getAddressLine(0), new LatLng(address.getLatitude(), address.getLongitude()));
            Log.d(TAG, "Address details: " + address.getAddressLine(0));
            retrieveQueueInfo(mMap, addressDetails);

            AutoMatch.setEnabled(true);
            matchUsers.setEnabled(true);
            AutoMatch.setBackgroundColor(getResources().getColor(R.color.brownGreen));
            matchUsers.setBackgroundColor(getResources().getColor(R.color.brownGreen));

        }
    }

    /**
     * CREATE QUEUE IF NONE EXISTS IN THE DATABASE WITH THE SAME ADDRESS
     */
    private void createQueue()
    {

        Log.d(TAG, "Create queue running...");

        // CREATE NEW REQUEST
        request = new StringRequest(Request.Method.POST, ServerDetails.CREATE_QUEUE, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {
                Log.i("onResponse: ",response);
                JSONObject jsonObject;

                try {

                    jsonObject = new JSONObject(response);




                    if(jsonObject == null){

                        Toast.makeText(Statistics.this, "Server not responding. Registration failed!", Toast.LENGTH_SHORT).show();
                    }

                    if (jsonObject.names().get(0).equals("success")) {

                        // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN

                    } else {

                        // DISPLAY ERROR RESPONSE TO THE ANDROID ACTIVITY SCREEN
                        //Toast.makeText(getApplicationContext(), "Error" + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    // CATCH ERRORS
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();


            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("length", 0 + "");
                hashMap.put("eta", 0 + "");
                hashMap.put("speed", 0 + "");
                hashMap.put("traffic", "Light");
                hashMap.put("lat", user_selected_lat + "");
                hashMap.put("lng", user_selected_long + "");
                hashMap.put("address", user_selected_address + "");
                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

    /**
     * get current device location
     */
    private void getDeviceLocation() {
        Log.d(TAG, "getLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            //Check if permission is granted
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            //Set user lat, lng to userLogitude and userLatitude attribute.
                            userLongitude = currentLocation.getLongitude();
                            userLatitute = currentLocation.getLatitude();

                            // MATTHEWS USE
                            user_latitude = currentLocation.getLatitude();
                            user_longatude = currentLocation.getLongitude();

                            //position the camera at the current location
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");

                        } else {
                            Log.d(TAG, "onComplete: current locations null");

                            moveCamera(new LatLng(-25.950019, 28.219530), DEFAULT_ZOOM, "My Location");
                            //Toast.makeText(Statistics.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException " + e.getMessage());
        }
    }

    /**
     * UPDATE THE USER CURRENT LOCATION IN THE DATABASE
     * @param lat - LATITUDE
     * @param lng - LONGATUDE
     */
    public void updateUserLocation(final double lat, final double lng)
    {

        request = new StringRequest(Request.Method.POST, ServerDetails.UPDATE_USER_DETAILS, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(Statistics.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("update", "");
                hashMap.put("id", getIDFromGlobalVariable());
                hashMap.put("user_lat", lat + "");
                hashMap.put("user_lng", lng + "");
                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

    /**
     * UPDATE THE MAP ONCE THE USER HAS RECEIVED A MATCH
     * @param mMap - THE MAP
     */
    private void plotMatchedCoordinates(GoogleMap mMap){
        LatLng latLng = new LatLng(latMatched, lngMatched);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        mMap.clear();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(Statistics.this));
        mMap.setOnMarkerClickListener(this);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Intent intent = new Intent(getApplicationContext(), GraphTesting.class);
                intent.putExtra("address", addrMatched);
                startActivity(intent);

            }
        });

        try {
            String snippet = "Address: " + addrMatched + "\n";
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title("Queuing destination")
                    .snippet(snippet);
            mMarker = mMap.addMarker(options);

        } catch (NullPointerException e) {
            Log.d(TAG, "moveCamera: NullPointerException: " + e.getMessage());
        }
    }

    /**
     * Position camera to current location
     *
     * @param latLng latitude and longitude
     * @param zoom   camera zoom level
     */
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
       /* //drawer queue markers
        retrieveQueueInfo();*/
        //only put the marker if it's not the device current location.
        if(context != null && context.equals("match_details")){
            plotMatchedCoordinates(mMap);
        }else{
            if (!title.equals("My Location")) {
                //Instantiate a marker(pin)
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(title);
                mMap.addMarker(markerOptions);
            }
        }
        //hideSoftKeyboard();
    }

    //Move camera and add place's info to the marker.
    private void moveCamera(LatLng latLng, float zoom, final PlaceInfo placeInfo) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.clear();

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(Statistics.this));



        if (placeInfo != null) {
            try {
                Log.d(TAG, "PlaceInfo"+placeInfo.toString());
                String snippet =placeInfo.getAddress();
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);
                    mMarker = mMap.addMarker(options);

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        Intent intent = new Intent(getApplicationContext(), GraphTesting.class);
                        String address = null;
                        try{
                            address = placeInfo.getAddress();
                            intent.putExtra("address", address);
                            Log.d(TAG, "address "+ address);
                        }catch(NullPointerException e){
                            e.printStackTrace();
                        }finally{
                            if(address == null){
                                address = placeInfo.getName();
                                Log.d(TAG, "new address "+ address);
                                intent.putExtra("address", address);
                            }


                        }
                        Log.d(TAG, "Drawing graph for address: "+ address );
                        startActivity(intent);

                    }
                });


            } catch (NullPointerException e) {
                Log.d(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }
        //hideSoftKeyboard();
    }

    /*Retrieve queue info*/
    private void retrieveQueueInfo(final GoogleMap mMap, final AddressDetails address){
        apiInterface = RetrofitApiClient.getApiClient().create(RetrofitApiInterface.class);
        Call<List<QueueInfo>> call = apiInterface.getQueueInfo("retrieve");
        call.enqueue(new Callback<List<QueueInfo>>() {
            @Override
            public void onResponse(Call<List<QueueInfo>> call, retrofit2.Response<List<QueueInfo>> response) {
                queueInfo = response.body();
                if(queueInfo != null) {

                    try {
                        for (QueueInfo q : queueInfo) {
                            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(Statistics.this));

                            Log.d("Statistics ", "drawMarker: " + q.toString());
                            String snippet = q.toString() + "\n";
                            LatLng latLng = new LatLng(q.getLat(), q.getLng());
                            MarkerOptions options = new MarkerOptions()
                                    .position(latLng)
                                    .title(q.getAddress())
                                    .snippet(snippet)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                            Marker currentMarker = mMap.addMarker(options);
                            currentMarker.setTag(0);
                            markerList.add(currentMarker);
                        }

                        drawMarker(mMap, markerList, address);

                    } catch (NullPointerException e) {
                        Log.d(TAG, "moveCamera: NullPointerException: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<QueueInfo>> call, Throwable t) {
                Log.d("Statistics","Error QueueInfo "+t.toString());
            }
        });
    }

    /**
     * CREATE AND DRAW MARKER ON THE MAP
     * @param mMap
     * @param markerList
     * @param address
     */
    private void drawMarker(GoogleMap mMap, List<Marker> markerList, AddressDetails address){
        boolean found = false;

        for(Marker m : markerList){
            Log.d("Address: ", "Compare "+m.getTitle() +" vs "+address.getAddress());
            if(m.getTitle().equals(address.getAddress())) {
                LatLng latLng = new LatLng(m.getPosition().latitude, m.getPosition().longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title(m.getTitle()).snippet(m.getSnippet()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                Log.d("Statistics", "Address line found in queue records");
                found = true;
            }
            break;
        }

        if(!found){
            Log.d("Statistics", "Address line not found in queue records");
//            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
            moveCamera(new LatLng(address.getLatLng().latitude, address.getLatLng().longitude), DEFAULT_ZOOM, mPlace);
        }
    }

    /**
     * Initialize the map
     */
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * get location permissions from the user.
     */
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Check if there is any outstanding permission.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: was called");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialise out map.
                    initMap();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            //put a blue dot on the map to show current location.
            //Display location button on the map.
            mMap.setMyLocationEnabled(true);

            //Remove the location button from the map.
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            //initialize search function.
            init();
        }
    }

    /**
     * hide keyboard.
     */
    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*
     * --------------------------------google places API autocomplete suggestions and place picker-----------
     * */

    private AdapterView.OnItemClickListener mAutoCompleteClickListner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //hideSoftKeyboard();

            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), 0);

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallBack);

            geoLocate();

        }


    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallBack = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
            }

            final Place place = places.get(0);

            try {
                //Instantiate place's info
                mPlace = new PlaceInfo();

                mPlace.setId(place.getId());
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setAttributions(place.getAttributions().toString());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                mPlace.setRating(place.getRating());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                mPlace.setLatLngl(place.getLatLng());

                Log.d(TAG, "onResult: place details: " + mPlace.toString());

            } catch (NullPointerException e) {
                Log.d(TAG, "onResult: NullPointerException: " + e.getMessage());
            }

            AddressDetails address = new AddressDetails(mPlace.getAddress(), mPlace.getLatLngl());
            Log.d(TAG, "original address "+ address.getAddress());
            //Free up places data.
            places.release();
            retrieveQueueInfo(mMap, address);
//            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

        }
    };


    /**
     * SAVE DATA TO GLOBAL VARIABLES
     * @param matched_user_id
     * @param matched_user_name
     * @param times
     * @param matched_user_description
     * @param fee
     */
    public void SaveDataToGlobalVariable(String matched_user_id, String matched_user_name, String times,  String matched_user_description, String fee) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("matched_user_details", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("matched_user_id", matched_user_id);
        editor.putString("matched_user_name", matched_user_name);
        editor.putString("times", times);
        editor.putString("matched_user_description", matched_user_description);
        editor.putString("fee", fee);

        editor.commit();

    }

    /**
     * SAVE DATA TO GLOBAL VARIABLES
     * @param user_selected_lat
     * @param user_selected_long
     * @param user_selected_address
     */
    public void SaveDataToGlobalVariableQueue(String user_selected_lat, String user_selected_long, String user_selected_address) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("queue_selected", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("user_selected_lat",user_selected_lat+"");
        editor.putString("user_selected_long",user_selected_long+"");
        editor.putString("user_selected_address",user_selected_address);

        editor.commit();

    }

    /**
     * SAVE DATA TO GLOBAL VARIABLES
     * @param id
     * @param address
     * @param date_created
     * @param isMatched
     * @param isCompleted
     * @param isStarted
     * @param isEnded
     * @param isSwap
     * @param fee_per_hr
     * @param times
     */
    public void SaveBookingToGlobal(String id, String address, String date_created, String isMatched, String isCompleted, String isStarted, String isEnded, String isSwap, String fee_per_hr, String times) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("booking_information", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("id",id);
        editor.putString("address",address);
        editor.putString("date_created", date_created + "");
        editor.putString("isMatched", isMatched + "");
        editor.putString("isCompleted", isCompleted + "");
        editor.putString("isStarted", isStarted + "");
        editor.putString("isEnded", isEnded + "");
        editor.putString("isSwap", isSwap + "");
        editor.putString("fee_per_hr", fee_per_hr + "");
        editor.putString("times", times + "");


        editor.commit();

    }

    /**
     * GET DATA FROM GLOBAL VARIABLES
     * @return
     */
    public String[] getQueueInformationFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("queue_selected", 0);
        String user_selected_lat = pref.getString("user_selected_lat", null);
        String user_selected_long = pref.getString("user_selected_long", null);
        String user_selected_address = pref.getString("user_selected_address", null);

        String[] myArray = {user_selected_lat, user_selected_long, user_selected_address};

        return myArray;

    }

    /**
     * GET ID FROM GLOBAL VARIABLE
     * @return
     */
    public String getIDFromGlobalVariable() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);
        return pref.getString("user_id", null);
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
     * GET DISTANCE BETWEEN TWO POINTS
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @param unit
     * @return
     */
    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /**
     * CONVERT DEGREES TO RADIUNS
     * @param deg
     * @return
     */
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /**
     * CREATE SETTINGS ROW IF THE USER DOES NOT HAVE ONE IN THE DATABASE
     */
    public void CreateSettingsRow() {

        request = new StringRequest(Request.Method.POST, ServerDetails.CREATE_SETTINGS_ROW, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);


                } catch (JSONException e) {
                    // CATCH ERRORS
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(Statistics.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id", user_id);
                hashMap.put("username", username + "");
                hashMap.put("bin_searching", 1 + "");
                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.isInfoWindowShown()){
            marker.hideInfoWindow();
        }else{

            if(marker.getTitle() != null && !marker.getSnippet().equals("")){
                marker.showInfoWindow();

            }
        }

        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:

                Log.d(TAG, "onClick: clicked gps icon");

                //Redirect to current device location
                getDeviceLocation();


                break;
            case 1:

                Log.d(TAG, "onClick: clicked place info");
                try {
                    if (mMarker.isInfoWindowShown()) {
                        mMarker.hideInfoWindow();
                    } else {
                        if(mMarker.getTitle() != null){
                            Log.d(TAG, "onclick: place info: " + mPlace.toString());

                            mMarker.showInfoWindow();
                        }


                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "onClick: NullPointerException: " + e.getMessage());
                }

                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // ASYNC BECAUSE THE HTTP REQUESTS TAKE TIME AND NEED TO WAIT FOR
    // ONE TO FINISH BEFORE MOVING ON TO THE NEXT
    private class ManageAsyncAndLocation extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getDeviceLocation();

            // CHECK FOR NOTIFICATIONS
            checkNotitficationStatus();

        }

        @Override
        protected Void doInBackground(Void... params) {



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // CHECK IF THE USER HAS A POTENTIAL MATCH
            checkMatchedStatus();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        timerAsyncAndLocation.purge();
        timerAsyncAndLocation.cancel();

    }

    @Override
    protected void onStop() {
        super.onStop();

        timerAsyncAndLocation.purge();
        timerAsyncAndLocation.cancel();

    }

    @Override
    public void onResume(){
        super.onResume();

        // WANT THIS TO RUN EVERY THREE MINUTES
        timerAsyncAndLocation = new Timer();
        CHECK_DETAILS = new TimerTask() {
            @Override
            public void run() {
                // ASYNC CLASS
                new ManageAsyncAndLocation().execute();

            }
        };

        timerAsyncAndLocation.schedule(CHECK_DETAILS, 01, 180000);

    }

}
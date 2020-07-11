package com.example.minzint.a4queue;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.minzint.a4queue.utilities.NavigationOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class QueueProgress extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,SensorEventListener {

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
    private String number_people_queue;

    TextView textViewUsername;
    TextView textViewUserEmail;
    ImageView profile_Image;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    // THIS IS FOR THE FOOTSTEP COUNTER
    private SensorManager sensorManager;
    private TextView footstepCount;
    boolean activityRunning;
    private long steps = 0;

    private TextView counts;
    private TextView QueueProgressLink;

    private ProgressBar progressBarDistanceMoved;
    private ProgressBar progressBarQueueProgresss;

    private long QueueDistanceMeters = 0;
    private long DistanceWalkedMeters = 0;
    private long stepCount = 0;
    private int number_people_queue_int = 0;

    private RequestQueue myRequestQueue;
    private StringRequest request;

    public int seconds = 0;
    public int minutes = 0;

    private TextView WaitingTimeLink;

    private long startTime = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;

    long timeSwapBuff = 0L;

    long updatedTime = 0L;


    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_progress);

        // INITIALISE REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

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
        // THIS IS WHEN THE APPLICATION STARTS
        number_people_queue = bundle.getString("number_people_queue");

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

        // SETUP THE ACTIVITY
        SetupActivity();

        // DETERMINE HOW THE PAGE WAS STARTED
        if (number_people_queue.equals("0")){

            String[] myArray = getDatafromGlobalVariable();
            number_people_queue = myArray[0] + "";
            stepCount = Long.parseLong(myArray[1]);

            String time = myArray[2];
            String[] segments = time.split(":");
            minutes = Integer.parseInt(segments[0]);
            seconds = Integer.parseInt(segments[1]);
            WaitingTimeLink.setText(time);

        }

        // GET DETAILS
        getDetailsFromGlobalVariable();

        // UPDATE THE ACTIVITY
        UpdateActivity();

        // ALLOW THE TIMER TO WORK
        // ManageTimer();

        // UPDATE THE DATABASE
        updateQueueStarted();

        // GET PREVIOUS RESULT
        getQueueDetailsDB();

        // HANDLE THE STATS
        final TextView StatisticsHeading = findViewById(R.id.StatisticsHeading);
        final TextView QueueName = findViewById(R.id.QueueNameTitle);

        //  TITLE ANIMATION
        Integer colorFrom = getResources().getColor(R.color.brightRed);
        Integer colorTo = getResources().getColor(R.color.brightBlue);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                StatisticsHeading.setTextColor((Integer)animator.getAnimatedValue());
                QueueName.setTextColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(10000);
        colorAnimation.start();

        // INITIALISE REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        getBookingDates();

        TextView locationHeading = findViewById(R.id.locationHeading);

        String[] myArray = getDataToGlobalVariableQueue();
        locationHeading.setText(myArray[2]);

        getBookingTimes();

        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

    }

    /**
     * GET DETAILS FROM GLOBAL VARIABLE
     */
    public void getDetailsFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("matched_user_details", 0);
        matched_user_id = pref.getString("matched_user_id", null);
        matched_username = pref.getString("matched_user_name", null);
        times = pref.getString("times", null);
        message = pref.getString("matched_user_description", null);
        fee = pref.getString("fee", null);

    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {


            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);

            int mins = secs / 60;

            secs = secs % 60;

            int milliseconds = (int) (updatedTime % 1000);

            if (mins < 9){

                WaitingTimeLink.setText("0" + mins + ":"

                        + String.format("%02d", secs));
                customHandler.postDelayed(this, 0);

            }else {

                WaitingTimeLink.setText("" + mins + ":"

                        + String.format("%02d", secs));
                customHandler.postDelayed(this, 0);

            }

        }
    };

    /**
     * SETUP THE ACTIVITY WHEN STARTING
     */
    public void SetupActivity()
    {

        String[] myArray = getDataToGlobalVariableQueue();

        TextView QueueNameTitle = findViewById(R.id.QueueNameTitle);

        QueueNameTitle.setText(myArray[2]);

        ImageView infoLinkDistance = findViewById(R.id.infoLinkDistance);

        // INFORMATION LINK
        infoLinkDistance.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QueueProgress.this);
                alertDialogBuilder.setMessage("This value represents the total distance you have moved through the queue.");
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

        ImageView infoLinkProgress = findViewById(R.id.infoLinkProgress);

        // INFORMATION LINK
        infoLinkProgress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QueueProgress.this);
                alertDialogBuilder.setMessage("This value represents the percentage of the queue you have already completed.");
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

        ImageView infoLinkTime = findViewById(R.id.infoLinkTime);

        // INFORMATION LINK
        infoLinkTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QueueProgress.this);
                alertDialogBuilder.setMessage("This value represents the total time you have spent in the queue.");
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


        // INIALISATION
        WaitingTimeLink = findViewById(R.id.WaitingTimeLink);
        WaitingTimeLink.setText("00:00");

        // GET THE TEXT VIEW
        footstepCount = findViewById(R.id.counts);

        // GET THE SYSTEM SERVICE
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        progressBarDistanceMoved = findViewById(R.id.progressBarDistanceMoved);
        progressBarQueueProgresss = findViewById(R.id.progressBarQueueProgresss);
        counts = findViewById(R.id.counts);
        QueueProgressLink = findViewById(R.id.QueueProgressLink);

        // SET PROGRESS BAR PROPERTIES
        // MUST BE IN PERCENTAGE
        progressBarQueueProgresss.setMax(100);
        progressBarQueueProgresss.setProgress(0);

        // QUEUE DETAILS BUTTON
        Button QueueDetails = findViewById(R.id.QueueDetails);

        QueueDetails.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), match_details_active.class);
                intent.putExtra("matched_username", matched_username);
                intent.putExtra("matched_user_id", matched_user_id);
                intent.putExtra("times", times);
                intent.putExtra("message", message);
                intent.putExtra("fee", fee);
                intent.putExtra("view", "true");
                startActivity(intent);

            }
        });

        // NOTIFY OF PROGRESS BUTTON
        Button NotifyProgress = findViewById(R.id.NotifyProgress);

        final CharSequence[] items = {"Queue 50% complete", "Queue 75% Complete", " Queue 90% Complete", "I can no longer queue"};

        NotifyProgress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QueueProgress.this);
                alertDialogBuilder.setTitle("Send Notification");

                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0){

                            // UPDATE THE DB
                            updateUserNotificationLabel50();
                            // NOTIFY OF SUCCESS
                            Toast.makeText(getApplicationContext(), matched_username + " has been notified", Toast.LENGTH_SHORT).show();

                        }else  if (which == 1){

                            // UPDATE THE DB
                            updateUserNotificationLabel75();
                            // NOTIFY OF SUCCESS
                            Toast.makeText(getApplicationContext(), matched_username + " has been notified", Toast.LENGTH_SHORT).show();

                        }else if (which == 2){

                            // UPDATE THE DB
                            updateUserNotificationLabel90();
                            // NOTIFY OF SUCCESS
                            Toast.makeText(getApplicationContext(), matched_username + " has been notified", Toast.LENGTH_SHORT).show();

                        }else if (which == 3){

                            // UPDATE DATABASE
                            notifyEndQueue();
                            deleteQueueState();

                            // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                            Toast.makeText(getApplicationContext(), matched_username + " has been notified.", Toast.LENGTH_SHORT).show();

                            // CLEAR THE SHARED PREFERENCES FOR THE NEXT USE
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("queue_progress_details", 0);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();
                            editor.apply();

                            // MOVE TO NEXT ACTIVITY
                            Intent intent = new Intent(getApplicationContext(), queue_end_details.class);
                            intent.putExtra("time", WaitingTimeLink.getText().toString());
                            startActivity(intent);

                        }
                    }

                });

                alertDialogBuilder.setPositiveButton("Select",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });

        // END QUEUE BUTTON
        Button EndQueueButton = findViewById(R.id.EndQueueButton);

        // create a new event listener
        EndQueueButton.setOnClickListener(new View.OnClickListener() {

            private  boolean alreadyPressed = false;

            @Override
            public void onClick(View view) {

                if (!alreadyPressed) {
                    // UPDATE DATABASE
                    notifyEndQueue();

                    deleteQueueState();

                    // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                    Toast.makeText(getApplicationContext(), matched_username + " has been notified.", Toast.LENGTH_SHORT).show();

                    // CLEAR THE SHARED PREFERENCES FOR THE NEXT USE
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("queue_progress_details", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.apply();

                    alreadyPressed = true;

                    // MOVE TO NEXT ACTIVITY
                    Intent intent = new Intent(getApplicationContext(), queue_end_details.class);
                    intent.putExtra("time", WaitingTimeLink.getText().toString());
                    startActivity(intent);

                }else{

                    Toast.makeText(getApplicationContext(), "Pending " + matched_username + " acceptance", Toast.LENGTH_SHORT).show();

                }

            }
        });

        Button ManualAdjust = findViewById(R.id.ManualAdjust);

        final CharSequence[] choice = {"5 Minute Delay", "10 Minute Delay", "30 Minute Delay"};

        // ALLOW THE USER TO MANUALLY ADJUST THE QUEUE PROGRESS
        ManualAdjust.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QueueProgress.this);
                alertDialogBuilder.setTitle("Queue Delays");

                alertDialogBuilder.setItems(choice, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {


                        if (which == 0){

                            number_people_queue_int += 20;
                            number_people_queue = number_people_queue_int + "";

                        }else if (which == 1){

                            number_people_queue_int += 50;
                            number_people_queue = number_people_queue_int + "";


                        }else if (which == 2){

                            number_people_queue_int += 150;
                            number_people_queue = number_people_queue_int + "";


                        }

                        UpdateActivity();

                    }

                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });
    }

    private int count;
    private String userTotal = "";

    /**
     * UPDATE THE ACTIVITY ON FOOTSTEP COUNT
     */
    public void UpdateActivity()
    {

        // CONFIGURE THE DISTANCE OF THE QUEUE
        // IF 100 PEOPLE THEN THE QUEUE IS ASSUEMED TO BE 50M LONG

        // CALCULATE VALUES // QUEUE DISTANCE
        ///////////////////////////////////////////////////////////////
        number_people_queue_int = Integer.parseInt(number_people_queue);

        // GET VALUE
        QueueDistanceMeters = (long)number_people_queue_int/2;

        // SET PROGRESS BAR PROPERTIES
        progressBarDistanceMoved.setMax(1000);
        progressBarDistanceMoved.setProgress(0);


        // DISTANCE WALKED METERS
        ////////////////////////////////////////////////////////////////

        DistanceWalkedMeters = stepCount / 2;
        manageDistanceMovedProgressbar(DistanceWalkedMeters);

        // DISPLAY THE DISTANCE THE USER HAS WALKED
        counts.setText(DistanceWalkedMeters + "m");

        /////////////////////////////////////////////////////////////////

        // GET THE PERCENTAGE COMPLETE
        ////////////////////////////////////////////////////////////////

        // GET THE PERCENTAGE COMPLETE
        double percentageValue = (((double)DistanceWalkedMeters/(double)QueueDistanceMeters) * 100);
        int percentageValueInt = (int)percentageValue;

        // UPDATE THE PROGRESS BAR
        manageQueueProgressProgressbar(percentageValueInt);
        // UPDATE THE TEXT
        QueueProgressLink.setText(percentageValueInt + "%");

        if ((percentageValueInt > 0) && (percentageValueInt <= 25)) {

            if ((percentageValueInt > 10) && (percentageValueInt <= 20)) {
                // PUSH QUEUE INFORMATION
                pushQueueDetailsDB(percentageValueInt);

            }

        }else if ((percentageValueInt > 25) && (percentageValueInt <= 50)) {

            if ((percentageValueInt > 40)) {

                // PUSH QUEUE INFORMATION
                pushQueueDetailsDB(percentageValueInt);

            }

        }else if ((percentageValueInt > 50) && (percentageValueInt <= 75)){

            if ((percentageValueInt > 60) && (percentageValueInt <= 70)) {
                // PUSH QUEUE INFORMATION
                pushQueueDetailsDB(percentageValueInt);

            }

            // NOTIFICATION UPDATE
            updateUserNotificationLabel50();

        }else if ((percentageValueInt > 75) && (percentageValueInt <= 90)){

            if ((percentageValueInt > 80) && (percentageValueInt <= 85)) {

                // PUSH QUEUE INFORMATION
                pushQueueDetailsDB(percentageValueInt);

            }

            // NOTIFICATION UPDATE
            updateUserNotificationLabel75();

        }else if ((percentageValueInt > 90) && (percentageValueInt <= 99)){

            if ((percentageValueInt > 95)) {
                // PUSH QUEUE INFORMATION
                pushQueueDetailsDB(percentageValueInt);
            }

            // NOTIFICATION UPDATE
            updateUserNotificationLabel90();

        }

        saveQueueState(percentageValueInt);
        SaveDataToGlobalVariable(number_people_queue_int,stepCount, WaitingTimeLink.getText().toString());

        double dblTot = calTotalPaid();
        userTotal = String.valueOf(dblTot);

        TextView myQueueEarningsTotal = findViewById(R.id.QueueEarningsTotal);
        myQueueEarningsTotal.setText("R"+userTotal);

    }

    private String[] getTime(){
        String[] time = WaitingTimeLink.getText().toString().split(":");
        return time;
    }

    //    Calculate total amount to be paid
    public double calTotalPaid(){

        double total;
        String[] timeQ = getTime();
        String strFee = fee;
        if(timeQ.length == 3){
            int hours = Integer.parseInt(timeQ[0]);
            int min = Integer.parseInt(timeQ[1]);
            int sec = Integer.parseInt(timeQ[2]);
//            total = (hours + min/60 + sec/3600) * Double.parseDouble(strFee)/60;
            total = (hours*60 + min + sec/60) * Double.parseDouble(strFee)/60;
        }else{
            int min = Integer.parseInt(timeQ[0]);
            int sec = Integer.parseInt(timeQ[1]);
//            total = (min/60 + sec/3600) * Double.parseDouble(strFee);
            total = (min + sec/60) * Double.parseDouble(strFee)/60;
        }


       /* if(total < 20){
            total = 20; // Base price.
        }*/
        return total;
    }

    /**
     * DETERMINE THE AMOUNT OF METERS WALKED
     * @param StepCount
     * @return
     */
    public long DetermineMetersWalked(long StepCount){

        long value = StepCount / 2;
        manageDistanceMovedProgressbar(value);
        return value;

    }

    /**
     * DETERMINE HOW LONG THE QUEUE IS IN METERS AND DISPLAY IN ACTIVITY
     * @return
     */
    public long DetermineQueueDistanceMeters(){

        number_people_queue_int = Integer.parseInt(number_people_queue);

        // GET VALUE
        long value = (long)number_people_queue_int/2;

        // SET PROGRESS BAR PROPERTIES
        progressBarDistanceMoved.setMax(1000);
        progressBarDistanceMoved.setProgress(0);

        // RETURN
        return value;

    }

    /**
     * MANAGE THE PROGRESS BARS
     * @param StepCount
     */
    public void manageDistanceMovedProgressbar(long StepCount)
    {

        if (StepCount < 400)
        {

            progressBarDistanceMoved.setProgress((int)StepCount);
            progressBarDistanceMoved.getProgressDrawable().setColorFilter(
                    Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);

        }else if ((StepCount > 400) && (StepCount <= 700)){

            progressBarDistanceMoved.setProgress((int)StepCount);
            progressBarDistanceMoved.getProgressDrawable().setColorFilter(
                    Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);

        }else if ((StepCount > 700) && (StepCount < 2000)){

            progressBarDistanceMoved.setProgress((int)StepCount);
            progressBarDistanceMoved.getProgressDrawable().setColorFilter(
                    Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);

        }

    }

    /**
     * MANAGE THE PROGRESS BARS
     * @param percentage
     */
    public void manageQueueProgressProgressbar(int percentage)
    {
        if (percentage < 25)
        {

            progressBarQueueProgresss.setProgress(percentage);
            progressBarQueueProgresss.getProgressDrawable().setColorFilter(
                    Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);

        }else if ((percentage > 25) && (percentage <= 75)){


            progressBarQueueProgresss.setProgress(percentage);
            progressBarQueueProgresss.getProgressDrawable().setColorFilter(
                    Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);

        }else if ((percentage > 75) && (percentage <= 100)){

            progressBarQueueProgresss.setProgress(percentage);
            progressBarQueueProgresss.getProgressDrawable().setColorFilter(
                    Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (countSensor != null) {

            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);

        }

        String[] myArray = new String[5];
        myArray = getDatafromGlobalVariable();
        number_people_queue = myArray[0] + "";

        // GET THE PREVIOUSLY STORED VALUES
        ManageAsyncAndUpdate myManageAsyncAndUpdate = new ManageAsyncAndUpdate();
        myManageAsyncAndUpdate.execute();


    }

    // ASYNC BECAUSE THE HTTP REQUESTS TAKE TIME AND NEED TO WAIT FOR
    // ONE TO FINISH BEFORE MOVING ON TO THE NEXT
    private class ManageAsyncAndUpdate extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // GET THE QUEUE DETAILS
            getQueueDetailsDB();

        }

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // UPDATE THE ACTIVITY DISPLAY
            UpdateActivity();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        // STOP THE PEDOMETER
        activityRunning = false;
        sensorManager.unregisterListener(this);

        // STOP THE TIMER
        //myTimer.purge();
        //myTimer.cancel();

    }

    @Override
    protected void onStop() {
        super.onStop();

        // STOP THE TIMER
        //myTimer.purge();
        //myTimer.cancel();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;

        if (activityRunning) {
            if (footstepCount != null) {

                if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                    steps++;
                }

                // UPDATE THE STEP COUNT
                stepCount = steps;
                UpdateActivity();

            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * GET THE QUEUE DETAILS FROM THE DATABASE
     */
    public void getQueueDetailsDB()
    {

        request = new StringRequest(Request.Method.POST, ServerDetails.GET_QUEUE_DETAILS, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("find_success")) {

                        stepCount = Long.parseLong(jsonObject.getString("footsteps"));
                        steps =  Long.parseLong(jsonObject.getString("footsteps"));
                        number_people_queue = jsonObject.getString("queue_distance");
                        int percentageComplete = Integer.parseInt(jsonObject.getString("queue_percentage_complete"));
                        String queue_elapsed_time = jsonObject.getString("queue_elapsed_time");

                        WaitingTimeLink.setText(queue_elapsed_time);

                        String[] segments = queue_elapsed_time.split(":");
                        minutes = Integer.parseInt(segments[0]);
                        seconds = Integer.parseInt(segments[1]);

                        // CONFIGURE THE DISTANCE OF THE QUEUE
                        // IF 100 PEOPLE THEN THE QUEUE IS ASSUEMED TO BE 50M LONG

                        // CALCULATE VALUES
                        QueueDistanceMeters = DetermineQueueDistanceMeters();
                        DistanceWalkedMeters = DetermineMetersWalked(stepCount);

                        // DISPLAY THE DISTANCE THE USER HAS WALKED
                        counts.setText(DistanceWalkedMeters + "m");

                        // GET THE PERCENTAGE COMPLETE
                        double percentageValue = (((double)DistanceWalkedMeters/(double)QueueDistanceMeters) * 100);

                        // UPDATE THE PROGRESS BAR
                        manageQueueProgressProgressbar(percentageComplete);
                        // UPDATE THE TEXT
                        QueueProgressLink.setText(percentageComplete + "%");

                        if ((percentageComplete > 0) && (percentageComplete <= 25)) {

                            if ((percentageComplete > 10) && (percentageComplete <= 20)) {
                                // PUSH QUEUE INFORMATION
                                pushQueueDetailsDB(percentageComplete);
                            }

                        }else if ((percentageComplete > 25) && (percentageComplete <= 50)) {

                            if ((percentageComplete > 40)) {

                                // PUSH QUEUE INFORMATION
                                pushQueueDetailsDB(percentageComplete);

                            }

                        }else if ((percentageComplete > 50) && (percentageComplete <= 75)){

                            if ((percentageComplete > 60) && (percentageComplete <= 70)) {
                                // PUSH QUEUE INFORMATION
                                pushQueueDetailsDB(percentageComplete);

                            }

                            // NOTIFICATION UPDATE
                            updateUserNotificationLabel50();

                        }else if ((percentageComplete > 75) && (percentageComplete <= 90)){

                            if ((percentageComplete > 80) && (percentageComplete <= 85)) {

                                // PUSH QUEUE INFORMATION
                                pushQueueDetailsDB(percentageComplete);

                            }

                            // NOTIFICATION UPDATE
                            updateUserNotificationLabel75();

                        }else if ((percentageComplete > 90) && (percentageComplete <= 99)){

                            if ((percentageComplete > 95)) {
                                // PUSH QUEUE INFORMATION
                                pushQueueDetailsDB(percentageComplete);
                            }

                            // NOTIFICATION UPDATE
                            updateUserNotificationLabel90();

                        }

                        SaveDataToGlobalVariable(number_people_queue_int,stepCount, WaitingTimeLink.getText().toString());


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

                // IF NO RESPOSE IT MEANS THAT THERE WAS NO RECORD IN THE DATABASE

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                String[] myQueueDetails = getDataToGlobalVariableQueue();

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("RESUME_QUEUE", "");
                hashMap.put("booking_id",  getBookingIDFromGlobalVariable() + "");
                return hashMap;

            }
        };

        myRequestQueue.add(request);
    }

    /**
     * PUSH QUEUE RECORDS TO THE DATABASE
     * @param percentageComplete
     */
    public void pushQueueDetailsDB(final int percentageComplete)
    {

        request = new StringRequest(Request.Method.POST, ServerDetails.QUEUE_UPDATE, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("success")) {

                        // NO RESPONSE


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
                //Toast.makeText(QueueProgress.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                String[] myQueueDetails = getDataToGlobalVariableQueue();

                // TIME
                // currentDate.getHours()+":"+currentDate.getMinutes()
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("booking_id", getBookingIDFromGlobalVariable());
                hashMap.put("footsteps", stepCount + "");
                hashMap.put("lat", myQueueDetails[0]);
                hashMap.put("lng", myQueueDetails[1]);
                hashMap.put("queue_distance", QueueDistanceMeters + "");
                hashMap.put("distance_moved", DistanceWalkedMeters + "");
                hashMap.put("queue_percentage_complete", percentageComplete + "");
                hashMap.put("queue_elapsed_time", WaitingTimeLink.getText().toString());
                return hashMap;

            }
        };

        myRequestQueue.add(request);
    }

    /**
     * DELETE THE QUEUE STATE
     */
    public void deleteQueueState()
    {

        request = new StringRequest(Request.Method.POST, ServerDetails.DELETE_QUEUE_STATE, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("success")) {

                        // NO RESPONSE


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
                //Toast.makeText(QueueProgress.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                String[] myQueueDetails = getDataToGlobalVariableQueue();

                // TIME
                // currentDate.getHours()+":"+currentDate.getMinutes()
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("remove", "");
                hashMap.put("booking_id", getBookingIDFromGlobalVariable());
                return hashMap;

            }
        };

        myRequestQueue.add(request);
    }

    /**
     * SAVE THE QUEUE STATE, USED WHEN THE USER EXISTS THE ACTIVITY
     * @param percentageComplete
     */
    public void saveQueueState(final int percentageComplete)
    {

        request = new StringRequest(Request.Method.POST, ServerDetails.SAVE_QUEUE_STATE, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("success")) {

                        // NO RESPONSE


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
               //Toast.makeText(QueueProgress.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                String[] myQueueDetails = getDataToGlobalVariableQueue();

                // TIME
                // currentDate.getHours()+":"+currentDate.getMinutes()
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("booking_id", getBookingIDFromGlobalVariable());
                hashMap.put("footsteps", stepCount + "");
                hashMap.put("queue_distance", QueueDistanceMeters + "");
                hashMap.put("distance_moved", DistanceWalkedMeters + "");
                hashMap.put("queue_percentage_complete", percentageComplete + "");
                hashMap.put("queue_elapsed_time", WaitingTimeLink.getText().toString());
                return hashMap;

            }
        };

        myRequestQueue.add(request);
    }

    /**
     * ENSURE THE OTHER USER KNOWS THE QUEUE HAS STARTED
     */
    public void updateQueueStarted()
    {

        request = new StringRequest(Request.Method.POST, ServerDetails.UPDATE_QUEUE_STARTED, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);


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
                //Toast.makeText(QueueProgress.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("update", "");
                hashMap.put("matched_id", user_id);
                hashMap.put("isStarted", 0 +"");
                return hashMap;

            }
        };

        myRequestQueue.add(request);
    }

    private boolean update50 = true;
    private boolean update75 = true;
    private boolean update90 = true;

    /**
     * SEND NOTIFICATION PROGRESS 50
     */
    public void updateUserNotificationLabel50()
    {

        // ONLY WANT TO RUN THIS ONCE
        if (update50 == true) {
            request = new StringRequest(Request.Method.POST, ServerDetails.UPDATE_NOTIFICATION_DETAILS, new Response.Listener<String>() {

                // HANDLE RESPONSES
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);


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
                    //Toast.makeText(QueueProgress.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("update", "");
                    hashMap.put("matched_id", user_id);
                    hashMap.put("user_id", matched_user_id);
                    hashMap.put("mobile_notification_50", "1");
                    return hashMap;

                }
            };

            myRequestQueue.add(request);

            update50 = false;
        }

    }

    /**
     * SEND NOTIFICATION PROGRESS 75
     */
    public void updateUserNotificationLabel75()
    {

        // ONLY WANT TO RUN THIS ONCE
        if (update75 == true) {


            request = new StringRequest(Request.Method.POST, ServerDetails.UPDATE_NOTIFICATION_DETAILS_75, new Response.Listener<String>() {

                // HANDLE RESPONSES
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);


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
                    //Toast.makeText(QueueProgress.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("update", "");
                    hashMap.put("matched_id", user_id);
                    hashMap.put("user_id", matched_user_id);
                    hashMap.put("mobile_notification_75", "1");
                    return hashMap;

                }
            };

            myRequestQueue.add(request);

            update75 = false;

        }
    }

    /**
     * SEND NOTIFICATION PROGRESS 90
     */
    public void updateUserNotificationLabel90()
    {
        // ONLY WANT TO RUN THIS ONCE
        if (update90 == true) {

            request = new StringRequest(Request.Method.POST, ServerDetails.UPDATE_NOTIFICATION_DETAILS_90, new Response.Listener<String>() {

                // HANDLE RESPONSES
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);


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
                   // Toast.makeText(QueueProgress.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("update", "");
                    hashMap.put("matched_id", user_id);
                    hashMap.put("user_id", matched_user_id);
                    hashMap.put("mobile_notification_90", "1");
                    return hashMap;

                }
            };

            myRequestQueue.add(request);

            update90 = false;
        }
    }

    /**
     * GET DETAILS FROM GLOBAL VARIABLE
     * @return
     */
    public String getBookingIDFromGlobalVariable()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("booking_information", 0);
        return pref.getString("id", null);


    }

    /**
     * GET DETAILS FROM GLOBAL VARIABLE
     * @return
     */
    public String[] getDataToGlobalVariableQueue()
    {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("queue_selected", 0);
        String user_selected_lat = pref.getString("user_selected_lat", null);
        String user_selected_long = pref.getString("user_selected_long", null);
        String user_selected_address = pref.getString("user_selected_address", null);

        String[] myArray = {user_selected_lat, user_selected_long, user_selected_address};

        return myArray;

    }

    /**
     * SAVE DETAILS TO GLOBAL VARIABLE
     * @param number_people_queue_int
     * @param StepCount
     * @param time
     */
    public void SaveDataToGlobalVariable(int number_people_queue_int, long StepCount, String time)
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("queue_progress_details", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("number_people_queue_int", number_people_queue_int + "");
        editor.putString("StepCount", StepCount + "");
        editor.putString("time", time + "");

        editor.commit();

    }

    /**
     * GET DETAILS FROM GLOBAL VARIABLE
     * @return
     */
    public String[] getDatafromGlobalVariable()
    {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("queue_progress_details", 0);

                 boolean check = pref.contains("number_people_queue_int");

                 if (check!= false){

                     String number_people_queue_int = pref.getString("number_people_queue_int", null);
                     String StepCount = pref.getString("StepCount", null);
                     String time = pref.getString("time", null);

                     String[] myArray = {number_people_queue_int, StepCount, time};
                     return myArray;

                 }

           return null;
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
     * SEND NOTIFICATION QUEUE END
     */
    public void notifyEndQueue()
    {

        String[] myArray = getDataToGlobalVariableQueue();

        //Toast.makeText(getApplicationContext(), myArray[2], Toast.LENGTH_SHORT).show();

        // CREATE NEW REQUEST
        request = new StringRequest(Request.Method.POST, ServerDetails.QUEUE_UPDATE_END_QUEUE, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);

                    if (jsonObject.names().get(0).equals("success")) {

                        // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                        Toast.makeText(getApplicationContext(), "User notified" , Toast.LENGTH_SHORT).show();

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

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                String[] myArray = getDataToGlobalVariableQueue();

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("update", "");
                hashMap.put("user_id", matched_user_id);
                hashMap.put("address", myArray[2]);
                hashMap.put("matched_id", user_id);
                hashMap.put("notification_end", "1");
                hashMap.put("isCompleted", 1 + "");

                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

    private ArrayList<String> dates = new ArrayList<>();

    public void getBookingDates() {

        request = new StringRequest(Request.Method.POST, ServerDetails.GET_BOOKING_DATES, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray JsonArray = new JSONArray(response);

                    // LOOP THROUGH
                    for (int i = 0; i < JsonArray.length(); i++) {

                        // GET ALL THE DATES
                        dates.add(JsonArray.getJSONObject(i).get("date") + "");

                    }

                    int countMonday = 0;
                    int countTuesday = 0;
                    int countWednesday = 0;
                    int countThursday = 0;
                    int countFriday = 0;
                    int countSaturday = 0;
                    int countSunday = 0;

                    // LOOP THROUGH EACH DATE
                    for (String date : dates) {

                        // FIRST FORMAT THE STRING
                        String[] segments = date.split("-");

                        // CONSTANTS FOR THE CALCULATION
                        String year = segments[0];
                        int DateNumber = Integer.parseInt(segments[2]);
                        String month = segments[1];

                        // FIND THE DAY THAT A DATE FALLS ON
                        // TEST DATE
                        // YEAR 2018
                        // MONTH 08
                        // DAY 20

                        // FIRST NEED TO CALCULATE THE YEAR CODE

                        String lastTwoDigits = year.substring(2);

                        int IntLastTwoDigits = Integer.parseInt(lastTwoDigits);

                        // FORMULA FOR THE YEAR CODE: (YY + (YY div 4)) mod 7

                        int YearCode = ((IntLastTwoDigits + (IntLastTwoDigits / 4)) % 7);

                        // FOR TESTING
                        // Toast.makeText(getApplicationContext(), "Year Code: " + YearCode, Toast.LENGTH_SHORT).show();

                        // DATE NUMBER
                        // THE ACTUAL DAY


                        /* MONTH CODES
                        January = 0
                        February = 3
                        March = 3
                        April = 6
                        May = 1
                        June = 4
                        July = 6
                        August = 2
                        September = 5
                        October = 0
                        November = 3
                        December = 5
                        */

                        int MonthCode = 0;

                        if (month.equals("01")) {

                            MonthCode = 0;

                        } else if (month.equals("02")) {

                            MonthCode = 3;

                        } else if (month.equals("03")) {

                            MonthCode = 3;

                        } else if (month.equals("04")) {

                            MonthCode = 6;

                        } else if (month.equals("05")) {

                            MonthCode = 1;

                        } else if (month.equals("06")) {

                            MonthCode = 4;

                        } else if (month.equals("07")) {

                            MonthCode = 6;

                        } else if (month.equals("08")) {

                            MonthCode = 2;

                        } else if (month.equals("09")) {

                            MonthCode = 5;

                        } else if (month.equals("10")) {

                            MonthCode = 0;

                        } else if (month.equals("11")) {

                            MonthCode = 3;

                        } else if (month.equals("12")) {

                            MonthCode = 5;

                        }

                        // CENTURY CODE
                        // 2000s = 6
                        int CenturyCode = 6;

                        // LEAP YEAR CODE
                        // NOT A LEAP YEAR
                        int LeapYearCode = 0;

                        // FINALLY CALCULATING THE DAY
                        // FORMULA (Year Code + Month Code + Century Code + Date Number – Leap Year Code) mod 7

                        /*  DAY CODES
                        0 = Sunday
                        1 = Monday
                        2 = Tuesday
                        3 = Wednesday
                        4 = Thursday
                        5 = Friday
                        6 = Saturday
                        */

                        int DayCode = (YearCode + MonthCode + CenturyCode + DateNumber - LeapYearCode) % 7;

                        String Day = "";

                        if (DayCode == 0) {  // SUNDAY

                            Day = "Sunday";
                            countSunday++;

                        } else if (DayCode == 1) { // MONDAY

                            Day = "Monday";
                            countMonday++;

                        } else if (DayCode == 2) { // TUESDAY

                            Day = "Tuesday";
                            countTuesday++;

                        } else if (DayCode == 3) { // WEDNESDAY

                            Day = "Wednesday";
                            countWednesday++;

                        } else if (DayCode == 4) { // THURSDAY

                            Day = "Thurday";
                            countThursday++;

                        } else if (DayCode == 5) { // FRIDAY

                            Day = "Friday";
                            countFriday++;

                        } else if (DayCode == 6) { // SATURDAY

                            Day = "Saturday";
                            countSaturday++;

                        }

                        // FOR TESTING
                        // Toast.makeText(getApplicationContext(), countTuesday + countWednesday + countThursday + countFriday + "", Toast.LENGTH_SHORT).show();


                    }

                    // Toast.makeText(getApplicationContext(), saturday, Toast.LENGTH_SHORT).show();

                    PieChart pieChart = (PieChart) findViewById(R.id.piechart);

                    pieChart.setUsePercentValues(true);

                    ArrayList<Entry> yvalues = new ArrayList<Entry>();
                    if (countMonday != 0){
                        yvalues.add(new Entry(countMonday, 0));
                    }
                    if (countTuesday > 0){
                        yvalues.add(new Entry(countTuesday, 1));
                    }
                    if (countWednesday > 0){
                        yvalues.add(new Entry(countWednesday, 2));
                    }
                    if (countThursday > 0){
                        yvalues.add(new Entry(countThursday, 3));
                    }
                    if (countFriday > 0){
                        yvalues.add(new Entry(countFriday, 4));
                    }
                    if (countSaturday > 0){
                        yvalues.add(new Entry(countSaturday, 5));
                    }
                    if (countSunday > 0){
                        yvalues.add(new Entry(countSunday, 6));
                    }

                    PieDataSet dataSet = new PieDataSet(yvalues, "");

                    ArrayList<Integer> colors = new ArrayList<Integer>();
                    colors.add(ContextCompat.getColor(getApplicationContext(), R.color.brightBlue));
                    colors.add(ContextCompat.getColor(getApplicationContext(), R.color.brightRed));
                    colors.add(ContextCompat.getColor(getApplicationContext(), R.color.brownGreen));
                    colors.add(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent2));
                    colors.add(ContextCompat.getColor(getApplicationContext(), R.color.cyan));

                    dataSet.setColors(colors);

                    ArrayList<String> xVals = new ArrayList<String>();

                    if (countMonday > 0){
                        xVals.add("Mondays");
                    }
                    if (countTuesday > 0){
                        xVals.add("Tuesdays");
                    }
                    if (countWednesday > 0){
                        xVals.add("Wednesdays");
                    }
                    if (countThursday > 0){
                        xVals.add("Thursdays");
                    }
                    if (countFriday > 0){
                        xVals.add("Fridays");
                    }
                    if (countSaturday > 0){
                        xVals.add("Saturdays");
                    }
                    if (countSunday > 0){
                        xVals.add("Sundays");
                    }


                    PieData data = new PieData(xVals, dataSet);
                    data.setValueTextSize(12);
                    data.setValueFormatter(new PercentFormatter());

                    pieChart.setData(data);

                    pieChart.setVisibility(View.VISIBLE);

                } catch (JSONException e) {

                    // CATCH ERRORS
                    e.printStackTrace();

                    TextView noInformationHeading = findViewById(R.id.noInformationHeading);
                    noInformationHeading.setText("No information available for this queue");
                    noInformationHeading.setTextColor(getResources().getColor(R.color.brightRed));
                    noInformationHeading.setVisibility(View.VISIBLE);


                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

                // display error message
                TextView noInformationHeading = findViewById(R.id.noInformationHeading);
                noInformationHeading.setText("No information available for this queue");
                noInformationHeading.setVisibility(View.VISIBLE);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                String[] myArray = getDataToGlobalVariableQueue();

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("retrieve", "");
                hashMap.put("address", myArray[2]);
                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

    private ArrayList<String> timess = new ArrayList<>();

    public void getBookingTimes() {

        request = new StringRequest(Request.Method.POST, ServerDetails.GET_BOOKING_TIMES, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray JsonArray = new JSONArray(response);

                    // LOOP THROUGH
                    for (int i = 0; i < JsonArray.length(); i++) {

                        // GET ALL THE DATES
                        timess.add(JsonArray.getJSONObject(i).get("times") + "");

                    }

                    ArrayList<BarEntry> bargroup2 = new ArrayList<>();

                    int counter02 = 0;
                    int counter24 = 0;
                    int counter46 = 0;
                    int counter68 = 0;
                    int counter810 = 0;
                    int counter1012 = 0;
                    int counter1214 = 0;
                    int counter1416 = 0;
                    int counter1618 = 0;
                    int counter1820 = 0;
                    int counter2022 = 0;
                    int counter2224 = 0;

                    for (String s : timess){

                        String[] segments = s.split(":");


                        if ((segments[0].equals("00")) || (segments[0].equals("01"))){

                            counter02++;

                        }
                        else if ((segments[0].equals("02")) || (segments[0].equals("03"))){

                            counter24++;

                        }
                        else if ((segments[0].equals("04")) || (segments[0].equals("05"))){

                            counter46++;

                        }
                        else if ((segments[0].equals("06")) || (segments[0].equals("07"))){

                            counter68++;

                        }
                        else if ((segments[0].equals("08")) || (segments[0].equals("09"))){

                            counter810++;

                        }
                        else if ((segments[0].equals("10")) || (segments[0].equals("11"))){

                            counter1012++;

                        }
                        else if ((segments[0].equals("12")) || (segments[0].equals("13"))){

                            counter1214++;

                        }
                        else if ((segments[0].equals("14")) || (segments[0].equals("15"))){

                            counter1416++;

                        }
                        else if ((segments[0].equals("16")) || (segments[0].equals("17"))){

                            counter1618++;

                        }
                        else if ((segments[0].equals("18")) || (segments[0].equals("19"))){

                            counter1820++;

                        }
                        else if ((segments[0].equals("20")) || (segments[0].equals("21"))){

                            counter2022++;

                        }
                        else if ((segments[0].equals("22")) || (segments[0].equals("23"))){

                            counter2224++;

                        }

                        //Toast.makeText(getApplicationContext(), "14:00 count " + counter1416, Toast.LENGTH_SHORT).show();

                        BarChart barChart = (BarChart) findViewById(R.id.barchart);

                        ArrayList<String> labels = new ArrayList<>();
                        labels.add("00:00-02:00");
                        labels.add("02:00-04:00");
                        labels.add("04:00-06:00");
                        labels.add("06:00-08:00");
                        labels.add("08:00-10:00");
                        labels.add("10:00-12:00");
                        labels.add("12:00-14:00");
                        labels.add("14:00-16:00");
                        labels.add("16:00-18:00");
                        labels.add("18:00-20:00");
                        labels.add("20:00-22:00");
                        labels.add("22:00-24:00");

                        if (counter02 > 0){

                            bargroup2.add(new BarEntry(counter02, 0));

                        }

                        else if (counter24 > 0){

                            bargroup2.add(new BarEntry(counter24, 1));

                        }

                        else if (counter46 > 0){

                            bargroup2.add(new BarEntry(counter46, 2));

                        }

                        else if (counter68 > 0){

                            bargroup2.add(new BarEntry(counter68, 3));

                        }

                        else if (counter810 > 0){

                            bargroup2.add(new BarEntry(counter810, 4));

                        }

                        else if (counter1012 > 0){

                            bargroup2.add(new BarEntry(counter1012, 5));

                        }

                        else if (counter1214 > 0){

                            bargroup2.add(new BarEntry(counter1214, 6));

                        }

                        else if (counter1416 > 0){

                            bargroup2.add(new BarEntry(counter1416, 7));

                        }

                        else if (counter1618 > 0){

                            bargroup2.add(new BarEntry(counter1618, 8));

                        }

                        else if (counter1820 > 0){

                            bargroup2.add(new BarEntry(counter1820, 9));

                        }

                        else if (counter2022 > 0){

                            bargroup2.add(new BarEntry(counter2022, 10));

                        }

                        else if (counter2224 > 0){

                            bargroup2.add(new BarEntry(counter2224, 11));

                        }

                        // HANDLE ODD CASES
                        if (bargroup2.size() > 11){

                            bargroup2.remove(11);
                            bargroup2.remove(10);

                        }

                        // handle odd cases

                        if (labels.size() >= bargroup2.size()) {


                            BarDataSet bardataset = new BarDataSet(bargroup2, "Times");
                            BarData data = new BarData(labels, bardataset);
                            barChart.setData(data);

                            barChart.setVisibility(View.VISIBLE);

                        }

                    }


                } catch (JSONException e) {

                    // CATCH ERRORS
                    e.printStackTrace();

                    TextView noInformationHeading = findViewById(R.id.noInformationHeading);
                    noInformationHeading.setText("No information available for this queue");
                    noInformationHeading.setVisibility(View.VISIBLE);

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

                TextView noInformationHeading = findViewById(R.id.noInformationHeading);
                noInformationHeading.setText("No information available for this queue");
                noInformationHeading.setVisibility(View.VISIBLE);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                String[] myArray = getDataToGlobalVariableQueue();

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("retrieve", "");
                hashMap.put("address", myArray[2]);
                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

}

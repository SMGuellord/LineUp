package com.example.minzint.a4queue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class queue_end_details extends AppCompatActivity {

    // ===================================================================
    // VARIABLES

    private RequestQueue myRequestQueue;
    private StringRequest request;

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
    private String timeInQueue;
    private String total;

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_end_details);

        // REQUEST QUEUE
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
        timeInQueue = bundle.getString("time");
        Log.d("queue_end_details", "time in queue "+ timeInQueue);

        // h:min:sec

        double dblTot = calTotalPaid();
        total = String.valueOf(dblTot);
        Log.d("queue_end_details", "total to be paid: "+ total);
        getDetailsFromGlobalVariable();

        String[] QueueDetailsArray =  getDataToGlobalVariableQueue();

        // SET THE VALUES

        TextView PageDescriptionLink = findViewById(R.id.PageDescriptionLink);
        PageDescriptionLink.setText("The user has been notified, here are a few details about the queue session.");

        TextView InvolvedUsersLink = findViewById(R.id.InvolvedUsersLink);
        InvolvedUsersLink.setText(name + ", " + matched_username);

        // DISPLAY THE LOCATION
        TextView userLocationLink = findViewById(R.id.userLocationLink);
        userLocationLink.setText(QueueDetailsArray[2]);

        // GET THE TIME
        TextView TimeLink = findViewById(R.id.TimeLink);
        TimeLink.setText(timeInQueue);

        // GET THE AMOUNT AGREED UPON
        TextView UserFeeAmountLink = findViewById(R.id.UserFeeAmountLink);
        UserFeeAmountLink.setText("R" + fee + "");

        TextView TotalDueLink = findViewById(R.id.TotalDueLink);
        TotalDueLink.setText("R"+total);

        // create a button element
        Button QueueDoneButton = findViewById(R.id.QueueDoneButton);

        // create a new event listener
        QueueDoneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                addReciept();

                Intent intent = new Intent(getApplicationContext(), Statistics.class);
                startActivity(intent);


            }
        });

        ImageView infoLinkDone = findViewById(R.id.infoLinkDone);

        // INFORMATION LINK
        infoLinkDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(queue_end_details.this);
                alertDialogBuilder.setMessage("Information about this queue session can be found again later in the receipts section of the menu.");
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

    }

    /**
     * GET AND FORMAT TIME
     * @return
     */
    private String[] getTime(){
        String[] time = timeInQueue.split(":");
        return time;
    }
//    Calculate total amount to be paid
    public double calTotalPaid(){

        Toast.makeText(queue_end_details.this, "Calculating total...", Toast.LENGTH_LONG);
        double total;
        String[] timeQ = getTime();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("matched_user_details", 0);
        String strFee = pref.getString("fee", null);
        if(timeQ.length == 3){
            int hours = Integer.parseInt(timeQ[0]);
            Log.d("queue_end_details",  "hours: "+hours);
            int min = Integer.parseInt(timeQ[1]);
            Log.d("queue_end_details",  "min: "+min);
            int sec = Integer.parseInt(timeQ[2]);
            Log.d("queue_end_details",  "sec: "+sec);
            total = (hours + min/60 + sec/3600) * Integer.parseInt(fee);
        }else{
            int min = Integer.parseInt(timeQ[0]);
            Log.d("queue_end_details",  "min: "+min);
            int sec = Integer.parseInt(timeQ[1]);
            Log.d("queue_end_details",  "sec: "+sec);
            total = (min/60 + sec/3600) * Double.parseDouble(strFee);
        }


        if(total < 20){
            total = 20; // Base price.
        }
        return total;
    }

    /**
     * ADD A RECEIPT TO THE DATABASE
     */
    public void addReciept()
    {
        // CREATE NEW REQUEST
        request = new StringRequest(Request.Method.POST, ServerDetails.ADD_RECIEPT, new Response.Listener<String>() {

            // HANDLE RESPONSES
            @Override
            public void onResponse(String response) {
                Log.i("onResponse: ",response);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);

                    if(jsonObject == null){
                        Toast.makeText(queue_end_details.this, "Server not responding. Add Failed failed!", Toast.LENGTH_SHORT).show();
                    }

                    if (jsonObject.names().get(0).equals("success")) {



                    } else {

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

                String[] QueueDetailsArray =  getDataToGlobalVariableQueue();

                String[] myBookingInformation = getBookingInformation();

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("user_id", user_id);
                hashMap.put("user_name", username);
                hashMap.put("matched_user_id", matched_user_id);
                hashMap.put("matched_user_name", matched_username);
                hashMap.put("address", QueueDetailsArray[2]);
                hashMap.put("time_queued", timeInQueue);
                hashMap.put("fee", fee);
                hashMap.put("total_paid", total);
                hashMap.put("date", dateFormat.format(date));
                hashMap.put("dates", dateFormat.format(date));
                hashMap.put("lat", Float.parseFloat(QueueDetailsArray[0]) + "");
                hashMap.put("lng", Float.parseFloat(QueueDetailsArray[1]) + "");
                hashMap.put("date_created", myBookingInformation[0]);
                hashMap.put("isMatched", myBookingInformation[1]);
                hashMap.put("isCompleted", myBookingInformation[2]);
                hashMap.put("isStarted", myBookingInformation[3]);
                hashMap.put("isEnded", myBookingInformation[4]);
                hashMap.put("isSwap", myBookingInformation[5]);
                hashMap.put("fee_per_hr", myBookingInformation[6]);
                hashMap.put("times", myBookingInformation[7]);
                hashMap.put("booking_id", myBookingInformation[8]);

                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

    /**
     * GET DATA FROM GLOBAL VARIABLES
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
     * GET DETAILS FROM GLOBAL VARIABLES
     * @return
     */
    public String[] getBookingInformation()
    {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("booking_information", 0);
        String id = pref.getString("id", null);
        String date_created = pref.getString("date_created", null);
        String isMatched = pref.getString("isMatched", null);
        String isCompleted = pref.getString("isCompleted", null);
        String isStarted = pref.getString("isStarted", null);
        String isEnded = pref.getString("isEnded", null);
        String isSwap = pref.getString("isSwap", null);
        String fee_per_hr = pref.getString("fee_per_hr", null);
        String times = pref.getString("times", null);

        String[] myArray = {date_created, isMatched, isCompleted, isStarted, isEnded, isSwap, fee_per_hr, times, id};

        return myArray;

    }

    /**
     * GET DETAILS FROM GLOBAL VARIABLES
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
}

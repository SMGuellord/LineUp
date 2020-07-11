package com.example.minzint.a4queue;


import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Update_User_Details extends AppCompatActivity {

    // ===================================================================
    // VARIABLES

    //INSTANCE VARIABLES
    private EditText nameUp;
    private EditText usernameUp;
    private EditText UserEmailAddress;
    private EditText UserPassword;
    private EditText UserPasswordRetype;

    private RequestQueue myRequestQueue;
    private StringRequest request;

    private final String TAG = "Update_User_Details";
    private String user_id;
    private String name;
    private String username;
    private String email;
    private String profile;
    private int rating;
    private String register_date;
    boolean logged_in;

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update__user__details);

        // GET THE CONTENTS OF THE TEXT AREA
        nameUp = findViewById(R.id.nameUpdate);
        usernameUp = findViewById(R.id.UserNameUpdate);
        UserEmailAddress = findViewById(R.id.UserEmailAddressUpdate);
        UserPassword = findViewById(R.id.UserPasswordUpdate);
        UserPasswordRetype = findViewById(R.id.UserPasswordUpdateRetype);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);

        user_id =pref.getString("user_id", null);
        name = pref.getString("name", null);
        username = pref.getString("username", null);
        email = pref.getString("email", null);
        profile = pref.getString("profile", null);
        rating = pref.getInt("rating", 0);
        register_date = pref.getString("register_date", null);
        logged_in = pref.getBoolean("logged_in", false);

        Log.d(TAG, "Update user details");
        Log.d(TAG, "user_id:---------- "+user_id);
        Log.d(TAG,"name:------------- "+name);
        Log.d(TAG,"username:--------- "+ username);
        Log.d(TAG,"email: -----------"+email);
        Log.d(TAG,"profile:---------- "+ profile);
        Log.d(TAG,"rating:----------- "+ String.valueOf(rating));
        Log.d(TAG,"register:---------- "+ register_date);

        nameUp.setText(name);
        usernameUp.setText(username);
        UserEmailAddress.setText(email);

        // create a button element
       final Button btnUpdate = findViewById(R.id.btn_update_details);

        // REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        // BUTTON ON CLICK LISTENER
        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                Toast.makeText(Update_User_Details.this, "Update in progress...", Toast.LENGTH_SHORT).show();

                // CREATE NEW REQUEST
                request = new StringRequest(Request.Method.POST, ServerDetails.UPDATE_DETAILS_URL, new Response.Listener<String>() {

                    // HANDLE RESPONSES
                    @Override
                    public void onResponse(String response) {
                        Log.i("onResponse: ",response);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);

                            if(jsonObject == null){
                                Toast.makeText(Update_User_Details.this, "Server not responding. Update failed!", Toast.LENGTH_SHORT).show();
                            }

                            if (jsonObject.names().get(0).equals("account_updated")) {

                                user_id = jsonObject.getString("user_id");
                                name = jsonObject.getString("name");
                                username = jsonObject.getString("username");
                                email = jsonObject.getString("email");
                                profile = jsonObject.getString("profile");
                                rating = Integer.parseInt(jsonObject.getString("rating"));
                                register_date = jsonObject.getString("register_date");
                                logged_in = true;

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetailsPref", 0);
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putString("user_id", user_id);
                                editor.putString("name", name);
                                editor.putString("username", username);
                                editor.putString("email", email);
                                editor.putString("profile", profile);
                                editor.putInt("rating", rating);
                                editor.putString("register_date", register_date);
                                editor.putBoolean("logged_in", logged_in);

                                editor.apply();
                                // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                                Toast.makeText(getApplicationContext(), jsonObject.getString("account_updated"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), User_Profile.class);
                                startActivity(intent);

                            }else if(jsonObject.names().get(0).equals("query_fail")){
                                Toast.makeText(getApplicationContext(), jsonObject.getString("query_fail"), Toast.LENGTH_SHORT).show();
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

                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("update", "");
                        hashMap.put("user_id", user_id);
                        hashMap.put("name", nameUp.getText().toString());
                        hashMap.put("email", UserEmailAddress.getText().toString());
                        hashMap.put("username", usernameUp.getText().toString());
                        hashMap.put("password", UserPassword.getText().toString());
                        hashMap.put("confirm", UserPasswordRetype.getText().toString());
                        return hashMap;

                    }
                };

                myRequestQueue.add(request);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}


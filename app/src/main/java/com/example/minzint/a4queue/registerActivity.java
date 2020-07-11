package com.example.minzint.a4queue;


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

public class registerActivity extends AppCompatActivity {

    // ===================================================================
    // VARIABLES

    private String isAvailable;

    //INSTANCE VARIABLES
    private EditText name;
    private EditText username;
    private EditText UserEmailAddress;
    private EditText UserPassword;
    private EditText UserPasswordRetype;

    private RequestQueue myRequestQueue;
    private StringRequest request;

    // ===================================================================
    // END OF VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // GET THE CONTENTS OF THE TEXT AREA
        name = findViewById(R.id.nameRegister);
        username = findViewById(R.id.UserNameRegister);
        UserEmailAddress = findViewById(R.id.UserEmailAddressRegister);
        UserPassword = findViewById(R.id.UserPasswordRegister);
        UserPasswordRetype = findViewById(R.id.UserPasswordRegisterRetype);
        isAvailable = "no";//Default mode.
        // create a button element
        Button btnRegister = findViewById(R.id.CreateAccountButton);

        // REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        // BUTTON ON CLICK LISTENER
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Toast.makeText(registerActivity.this, "Registration in progress...", Toast.LENGTH_SHORT).show();

                // CREATE NEW REQUEST
                request = new StringRequest(Request.Method.POST, ServerDetails.REGISTER_URL, new Response.Listener<String>() {

                    // HANDLE RESPONSES
                    @Override
                    public void onResponse(String response) {
                        Log.i("onResponse: ",response);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);

                            if(jsonObject == null){
                                Toast.makeText(registerActivity.this, "Server not responding. Registration failed!", Toast.LENGTH_SHORT).show();
                            }

                            if (jsonObject.names().get(0).equals("success")) {

                                // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                                Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                // INTENTIONALLY MOVED THIS TO THE LOGIN ACTIVITY - DONT CHANGE
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

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
//                        hashMap.put("submit", "");
                        hashMap.put("name", formatUserInputName());
                        hashMap.put("email", formatUserInputEmail());
                        hashMap.put("username", formatUserInputUsername());
                        hashMap.put("password", formatUserInputUserPassword());
                        hashMap.put("confirm", formatUserInputUserPasswordReenter());
                        return hashMap;

                    }
                };

                myRequestQueue.add(request);

            }
        });
    }

    /**
     * PREVENT SQL INJECTION
     * @return
     */
    public String formatUserInputEmail(){

        String userEmail = UserEmailAddress.getText().toString();

        String[] segments = userEmail.split(" ");

        // PREVENT THE USER FROM ADDING THEIR OWN SQL
        userEmail = segments[0];

        return userEmail;

    }

    /**
     * PREVENT SQL INJECTION
     * @return
     */
    public String formatUserInputName(){

        String userName = name.getText().toString();

        String[] segments = userName.split(" ");

        // PREVENT THE USER FROM ADDING THEIR OWN SQL
        userName = segments[0];

        return userName;

    }

    /**
     * PREVENT SQL INJECTION
     * @return
     */
    public String formatUserInputUsername(){

        String userName = username.getText().toString();

        String[] segments = userName.split(" ");

        // PREVENT THE USER FROM ADDING THEIR OWN SQL
        userName = segments[0];

        return userName;

    }

    /**
     * PREVENT SQL INJECTION
     * @return
     */
    public String formatUserInputUserPassword(){

        String userPassword = UserPassword.getText().toString();

        String[] segments = userPassword.split(" ");

        // PREVENT THE USER FROM ADDING THEIR OWN SQL
        userPassword = segments[0];

        return userPassword;

    }

    /**
     * PREVENT SQL INJECTION
     * @return
     */
    public String formatUserInputUserPasswordReenter(){

        String userPasswordReenter = UserPasswordRetype.getText().toString();

        String[] segments = userPasswordReenter.split(" ");

        // PREVENT THE USER FROM ADDING THEIR OWN SQL
        userPasswordReenter = segments[0];

        return userPasswordReenter;

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}


package com.example.minzint.a4queue;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

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

public class Reactivate_account extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirm_Password;

    private String user_id;
    private String name;
    private String username;
    private String email;
    private String profile;
    private int rating;
    private String register_date;
    boolean logged_in;

    private RequestQueue myRequestQueue;

    // STRING TO THE URL OF THE PHP SCRIPT

    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reactivate_account);

        // GET THE CONTENTS OF THE TEXT AREA
        editTextUsername = findViewById(R.id.UserNameEditText);
        editTextPassword = findViewById(R.id.UserPasswordEditText);
        editTextConfirm_Password = findViewById(R.id.ConfirmPasswordEditText);


        // create a button element
        Button btnReactivate = findViewById(R.id.btnReactivate);


        // REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        // BUTTON ON CLICK LISTENER
        btnReactivate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if( editTextPassword.getText().toString().equals(editTextConfirm_Password.getText().toString())){
                    // CREATE NEW REQUEST
                    request = new StringRequest(Request.Method.POST, ServerDetails.REACTIVATE_ACCOUNT_URL, new Response.Listener<String>() {

                        // HANDLE RESPONSES
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);


                                if (jsonObject.names().get(0).equals("account_reactivated")) {

                                    // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("account_reactivated"), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);

                                } else if (jsonObject.names().get(0).equals("query_fail")) {
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("query_fail"), Toast.LENGTH_SHORT).show();
                                } else if (jsonObject.names().get(0).equals("wrong_password")) {
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("wrong_password"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Reactivate_account.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("activate", "");
                            hashMap.put("username", editTextUsername.getText().toString());
                            hashMap.put("password", editTextPassword.getText().toString());
                            return hashMap;

                        }
                    };

                    myRequestQueue.add(request);
                }else{
                    Toast.makeText(Reactivate_account.this, "Password did not match!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

}



package com.example.minzint.a4queue;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.minzint.a4queue.utilities.RetrofitApiInterface;



import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // ===================================================================
    // VARIABLES

    private EditText editTextUsername;
    private EditText editTextPassword;

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

    TextView SignInTitle;

    // ===================================================================
    // END OF VARIABLES

    /**
     * PREVENT SQL INJECTION
     * @return
     */
    public String formatUserInputName(){

        String username = editTextUsername.getText().toString();

        String[] segments = username.split(" ");

        // PREVENT THE USER FROM ADDING THEIR OWN SQL
        username = segments[0];

        return username;

    }

    /**
     * PREVENT SQL INJECTION
     * @return
     */
    public String formatUserInputPasword(){

        String userPassword = editTextPassword.getText().toString();

        String[] segments = userPassword.split(" ");

        // PREVENT THE USER FROM ADDING THEIR OWN SQL
        userPassword = segments[0];

        return userPassword;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // GET THE CONTENTS OF THE TEXT AREA
        editTextUsername = findViewById(R.id.UserNameEditText);
        editTextPassword = findViewById(R.id.UserPasswordEditText);

        // create a button element
        Button btnLogin = findViewById(R.id.loginButton);

        // REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        // BUTTON ON CLICK LISTENER
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                // CREATE NEW REQUEST
                request = new StringRequest(Request.Method.POST, ServerDetails.LOGIN_URL, new Response.Listener<String>() {

                    // HANDLE RESPONSES
                    @Override
                    public void onResponse(String response) {

                        Log.d("Statistics", "Login Response"+ response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            if (jsonObject.names().get(0).equals("login_success")) {

                                // DISPLAY THE RESPONSE TO THE ANDROID ACTIVITY SCREEN
                                // Toast.makeText(getApplicationContext(), jsonObject.getString("login_success"), Toast.LENGTH_SHORT).show();
                                //Read user's details.
                                user_id = jsonObject.getString("user_id");
                                name = jsonObject.getString("name");
                                username = jsonObject.getString("username");
                                email = jsonObject.getString("email");
                                profile = jsonObject.getString("profile");
                                rating = Integer.parseInt(jsonObject.getString("rating"));
                                register_date = jsonObject.getString("register_date");
                                logged_in = true;

                                Log.d("user_id:---------- ", user_id);
                                Log.d("name:------------- ", name);
                                Log.d("username:--------- ", username);
                                Log.d("email: -----------", email);
                                Log.d("profile:---------- ", profile);
                                Log.d("rating:----------- ", String.valueOf(rating));
                                Log.d("register:---------- ", register_date);
                                SaveDataToGlobalVariable(user_id, name, username, email, profile, rating, register_date, logged_in);


                                Intent intent = new Intent(getApplicationContext(), Statistics.class);
                                startActivity(intent);

                            } else if(jsonObject.names().get(0).equals("account_deactivated")) {
//                                Toast.makeText(getApplicationContext(), jsonObject.getString("account_deactivated"), Toast.LENGTH_SHORT).show();

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                                builder1.setTitle(jsonObject.getString("account_deactivated"));
                                builder1.setMessage("Would you like to re-activate your account?");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                builder1.setNegativeButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(getApplicationContext(), Reactivate_account.class);
                                                startActivity(intent);
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
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
                        Toast.makeText(MainActivity.this, "Oops!!! Something went wrong. Server must be offline", Toast.LENGTH_SHORT).show();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("submit", "");
                        hashMap.put("username", formatUserInputName());
                        hashMap.put("password", formatUserInputPasword());
                        return hashMap;

                    }
                };

                myRequestQueue.add(request);


            }
        });
        //////////////////////////////////////////////////////////////////////////////////


        //////////////////////////////////////////////////////////////////////////////////

        // create a button element
        TextView myTextViewRegister = findViewById(R.id.registerLink);

        // create a new event listener
        myTextViewRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // actions on event

                // want to go the next activity for the time being
                Intent nextActivity = new Intent(getApplicationContext(), registerActivity.class);
                startActivity(nextActivity);

            }
        });

        SignInTitle = findViewById(R.id.SignInTitle);

        animate();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /*Save user details to sharedPreferences file*/
    public void SaveDataToGlobalVariable(String user_id, String name, String username, String email, String profile, int rating, String register_date, Boolean logged_in)
//    public void SaveDataToGlobalVariable(List<UserDetails> userDetails)
    {
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

        editor.commit();

    }

    /**
     * HEADING ANIMATIONS
     */
    public void animate(){

        //  TITLE ANIMATION
        Integer colorFrom = getResources().getColor(R.color.brightRed);
        Integer colorTo = getResources().getColor(R.color.brightBlue);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                SignInTitle.setTextColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(30000);
        colorAnimation.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimation.start();

    }

}



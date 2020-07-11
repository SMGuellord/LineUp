package com.example.minzint.a4queue;

import android.os.Bundle;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NotificationWorker extends AppCompatActivity {

    private TextView NotificationTitle;
    private TextView NotificationMessage;
    private Button NotificationSend;

    private NotificationHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_worker);

        NotificationTitle =  findViewById(R.id.notificationTitle);
        NotificationMessage = findViewById(R.id.notificationMessage);
        NotificationSend = findViewById(R.id.NotificationButton);

        helper = new NotificationHelper(this);

        NotificationSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendOnChannel(NotificationTitle.getText().toString(), NotificationMessage.getText().toString());

            }
        });
    }

    public void SendOnChannel(String title, String message){

        NotificationCompat.Builder nb = helper.getChannelNotification(title, message);
        helper.getManager().notify(1, nb.build());
    }

}

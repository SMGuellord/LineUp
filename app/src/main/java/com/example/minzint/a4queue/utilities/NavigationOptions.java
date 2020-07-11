package com.example.minzint.a4queue.utilities;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.example.minzint.a4queue.FutureBookings;
import com.example.minzint.a4queue.MainActivity;
import com.example.minzint.a4queue.R;
import com.example.minzint.a4queue.Settings;
import com.example.minzint.a4queue.Statistics;
import com.example.minzint.a4queue.User_Profile;
import com.example.minzint.a4queue.bookings;
import com.example.minzint.a4queue.help_introduction;
import com.example.minzint.a4queue.user_reciepts;

public class NavigationOptions {

    public static void getNavigationOption(Context context, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_Profile:
                Intent intentProfile = new Intent(context, User_Profile.class);
                context.startActivity(intentProfile);
                break;
            case R.id.nav_Bookings:
                Intent future_bookings = new Intent(context, FutureBookings.class);
                context.startActivity(future_bookings);
                break;
            case R.id.nav_Services:
                Intent intentBookings = new Intent(context, bookings.class);
                context.startActivity(intentBookings);
                break;
            case R.id.nav_Statistics:
                Intent intentStatistics = new Intent(context, Statistics.class);
                context.startActivity(intentStatistics);
                break;
            case R.id.nav_Receipt:
                Intent intentReceipt = new Intent(context, user_reciepts.class);
                context.startActivity(intentReceipt);
                break;
            case R.id.nav_Settings:
                Intent intentSettings = new Intent(context, Settings.class);
                context.startActivity(intentSettings);
                break;
            case R.id.nav_Help:
                Intent intentHelp = new Intent(context, help_introduction.class);
                context.startActivity(intentHelp);
                break;
            case R.id.nav_Logout:
                Intent intentLogout = new Intent(context, MainActivity.class);
                context.startActivity(intentLogout);
                break;

        }
    }
}

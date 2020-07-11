package com.example.minzint.a4queue;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class GraphTesting extends AppCompatActivity {

    private RequestQueue myRequestQueue;
    private StringRequest request;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_testing);

        Bundle bundle = getIntent().getExtras();
        address = bundle.getString("address");

        final TextView StatisticsHeading = findViewById(R.id.StatisticsHeading);

        //  TITLE ANIMATION
        Integer colorFrom = getResources().getColor(R.color.brightRed);
        Integer colorTo = getResources().getColor(R.color.brightBlue);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                StatisticsHeading.setTextColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(10000);
        colorAnimation.start();

        // INITIALISE REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(this);

        getBookingDates();

        TextView locationHeading = findViewById(R.id.locationHeading);
        locationHeading.setText(address);

        getBookingTimes();

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
                    colors.add(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    colors.add(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                    colors.add(ContextCompat.getColor(getApplicationContext(), R.color.brownGreen));

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
                    pieChart.setDrawHoleEnabled(false);
                    pieChart.setDescription(" ");
                    pieChart.setDrawSliceText(false);
                    pieChart.animateXY(1500, 1500);

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

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("retrieve", "");
                hashMap.put("address", address);
                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

    private ArrayList<String> times = new ArrayList<>();

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
                        times.add(JsonArray.getJSONObject(i).get("times") + "");

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

                    for (String s : times) {

                        String[] segments = s.split(":");


                        if ((segments[0].equals("00")) || (segments[0].equals("01"))) {

                            counter02++;

                        } else if ((segments[0].equals("02")) || (segments[0].equals("03"))) {

                            counter24++;

                        } else if ((segments[0].equals("04")) || (segments[0].equals("05"))) {

                            counter46++;

                        } else if ((segments[0].equals("06")) || (segments[0].equals("07"))) {

                            counter68++;

                        } else if ((segments[0].equals("08")) || (segments[0].equals("09"))) {

                            counter810++;

                        } else if ((segments[0].equals("10")) || (segments[0].equals("11"))) {

                            counter1012++;

                        } else if ((segments[0].equals("12")) || (segments[0].equals("13"))) {

                            counter1214++;

                        } else if ((segments[0].equals("14")) || (segments[0].equals("15"))) {

                            counter1416++;

                        } else if ((segments[0].equals("16")) || (segments[0].equals("17"))) {

                            counter1618++;

                        } else if ((segments[0].equals("18")) || (segments[0].equals("19"))) {

                            counter1820++;

                        } else if ((segments[0].equals("20")) || (segments[0].equals("21"))) {

                            counter2022++;

                        } else if ((segments[0].equals("22")) || (segments[0].equals("23"))) {

                            counter2224++;

                        }

                    }

                        //Toast.makeText(getApplicationContext(), "10:00 count " + counter1012, Toast.LENGTH_SHORT).show();

                        BarChart barChart = (BarChart) findViewById(R.id.barchart);

                        ArrayList<String> labels = new ArrayList<>();
                        labels.add("00-02");
                        labels.add("02-04");
                        labels.add("04-06");
                        labels.add("06-08");
                        labels.add("08-10");
                        labels.add("10-12");
                        labels.add("12-14");
                        labels.add("14-16");
                        labels.add("16-18");
                        labels.add("18-20");
                        labels.add("20-22");
                        labels.add("22-24");

                        if (counter02 > 0){

                            bargroup2.add(new BarEntry(counter02, 0));

                        }

                        if (counter24 > 0){

                            bargroup2.add(new BarEntry(counter24, 1));

                        }

                        if (counter46 > 0){

                            bargroup2.add(new BarEntry(counter46, 2));

                        }

                        if (counter68 > 0){

                            bargroup2.add(new BarEntry(counter68, 3));

                        }

                        if (counter810 > 0){

                            bargroup2.add(new BarEntry(counter810, 4));

                        }

                        if (counter1012 > 0){

                            bargroup2.add(new BarEntry(counter1012, 5));

                        }

                        if (counter1214 > 0){

                            bargroup2.add(new BarEntry(counter1214, 6));

                        }

                        if (counter1416 > 0){

                            bargroup2.add(new BarEntry(counter1416, 7));

                        }

                        if (counter1618 > 0){

                            bargroup2.add(new BarEntry(counter1618, 8));

                        }

                        if (counter1820 > 0){

                            bargroup2.add(new BarEntry(counter1820, 9));

                        }

                        if (counter2022 > 0){

                            bargroup2.add(new BarEntry(counter2022, 10));

                        }

                        if (counter2224 > 0){

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
                            bardataset.setColors(new int[]{getResources().getColor(R.color.brightBlue), getResources().getColor(R.color.brightGreen),getResources().getColor(R.color.brightRed),getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.colorPrimary)});
                            barChart.setData(data);
                            barChart.setDescription(" ");
                            barChart.setVisibility(View.VISIBLE);
                            barChart.animateXY(1500, 1500);


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

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("retrieve", "");
                hashMap.put("address", address);
                return hashMap;

            }
        };

        myRequestQueue.add(request);

    }

}

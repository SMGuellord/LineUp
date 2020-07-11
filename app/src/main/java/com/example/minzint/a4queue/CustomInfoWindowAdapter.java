package com.example.minzint.a4queue;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.minzint.a4queue.models.FutureBookingsDetails;
import com.example.minzint.a4queue.models.QueueStatByDayTime;
import com.example.minzint.a4queue.utilities.RetrofitApiClient;
import com.example.minzint.a4queue.utilities.RetrofitApiInterface;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    // ===================================================================
    // VARIABLES

    private final View mWindow;
    private Context mContext;
    private List<QueueStatByDayTime> statByTimes;
    private RetrofitApiInterface apiInterface;
    private int count8To10;
    private int count10To12;
    private int count12To14;
    private int count14To16;
    private int count16To18;
    private int count18To20;

    private  ArrayList<String> labels;
    List<BarEntry> dataEntry;
    BarChart barChart;

    private RequestQueue myRequestQueue;
    private StringRequest request;
    private String address;

    PieChart pieChart;

    // ===================================================================
    // END OF VARIABLES

    public CustomInfoWindowAdapter(Context mContext) {
        this.mContext = mContext;
        this.mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window, null);
        mWindow.setClickable(true);
        mWindow.bringToFront();

        // INITIALISE REQUEST QUEUE
        myRequestQueue = Volley.newRequestQueue(mContext);

    }

    /**
     * CREATE A MARKER WITH THE REQUIRED INFORMATION
     * @param marker - MAP MARKER
     * @param view - THE VIEW REQUIRED
     */
    private void renderWindowText(Marker marker, View view){

        // FIND AND INITIALIZE ATTRIBUTES

        /*String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);
        if(!title.equals("")){
            tvTitle.setText(title);
        }
        String snippet = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.snippet);
        Log.d("CustomInfoWindowAdapter", "title value: "+title);
        Log.d("CustomInfoWindowAdapter", "snippet value: "+snippet);*/

        String title = null;
        String snippet = null;
        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvSnippet = view.findViewById(R.id.snippet);

        try{
            title = marker.getTitle();
            tvTitle.setText(title);
            snippet = marker.getSnippet();
            Log.d("CustomInfoWindowAdapter", "title value: "+title);
            Log.d("CustomInfoWindowAdapter", "snippet value: "+snippet);
            tvSnippet.setText(snippet);
            address = title;
        }catch(NullPointerException e){
            e.printStackTrace();
        }finally {

            if(snippet == null){
                snippet = marker.getTitle();
                tvSnippet.setText(snippet);
                title = snippet.split(",")[0];
                tvTitle.setText(title);
                address = snippet;

                Log.d("CustomInfoWindowAdapter", "new title value: "+title);
                Log.d("CustomInfoWindowAdapter", "new snippet value: "+snippet);

            }
        }
        /*if(snippet == null){
            tvSnippet.setText("Could not extract address for this location!");
        }
        else if(!snippet.equals("")){
            tvSnippet.setText(snippet);
        }*/


        /*
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);
        barChart = view.findViewById(R.id.barchartTimes);
        pieChart = view.findViewById(R.id.piechart);
        pieChart.setDescription("");
        labels = new ArrayList<>();
        labels.add("8-10");
        labels.add("10-12");
        labels.add("12-14");
        labels.add("14-16");
        labels.add("16-18");
        labels.add("18:20");

        this.count8To10 = 0;
        this.count10To12 = 0;
        this.count12To14 = 0;
        this.count14To16 = 0;
        this.count16To18 = 0;
        this.count18To20 = 0;

        if(!title.equals("")){
            tvTitle.setText(title);
        }
        String snippet = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.snippet);
        Log.d("CustomInfoWindowAdapter", "title value: "+title);
        Log.d("CustomInfoWindowAdapter", "snippet value: "+snippet);
        if(!snippet.equals("")){
            tvSnippet.setText(snippet);
        }
        address = title;

        // CREATE THE BAR GRAPH FOR BUSY TIMES
       getStatsTimes(address, barChart);

       // CREATE A PIE CHART FOR BUSY DAYS
       getBookingDates();

       */

    }


    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    private ArrayList<String> dates = new ArrayList<>();

    /**
     * GET BUSY DAY INFORMATION FROM DATABASE, POPULATE PIE CHART
     */
    public void getBookingDates() {

        request = new StringRequest(Request.Method.POST, ServerDetails.GET_BOOKING_DATES, new com.android.volley.Response.Listener<String>() {

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
                    colors.add(Color.rgb(66, 134, 244));
                    colors.add(Color.rgb(206, 62, 26));
                    colors.add(Color.rgb(32, 206, 25));
                    colors.add(Color.rgb(239, 117, 237));
                    colors.add(Color.rgb(228, 247, 143));

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

                    pieChart.recomputeViewAttributes(mWindow);

                } catch (JSONException e) {

                    // CATCH ERRORS
                    e.printStackTrace();

                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

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


    /**
     * CREATE BAR CHART
     * @param dataEntry
     * @param barChart
     */
    private void generateBarChart( List<BarEntry> dataEntry, BarChart barChart ){

        BarDataSet bardataset = new BarDataSet(dataEntry, "Time ranges");
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(labels, bardataset);
        barChart.setDrawValueAboveBar(true);
        barChart.setData(data);
        barChart.setDescription("");
        barChart.getAxisLeft().setAxisMinValue(0);
        barChart.getAxisRight().setAxisMinValue(0);
    }

    /*Count times in a given range*/
    private void generateTimeStat(List<QueueStatByDayTime> statByTimes, BarChart barChart){
        if(!statByTimes.isEmpty()){
            for(QueueStatByDayTime q : statByTimes){
                String times = q.getTimes();
                Log.d("CustomInfoWindowAdapter", "Time "+times);
                String[] token = times.split(":");
                int hours = Integer.parseInt(token[0]);
                Log.d("CustomInfoWindowAdapter", "hour "+hours);
                switch (hours){
                    case 8 :
                    case 9:
                        count8To10++;
                        break;
                    case 10 :
                    case 11:
                        count10To12++;
                        break;
                    case 12 :
                    case 13:
                        count12To14++;
                        break;
                    case 14 :
                    case 15:
                        count14To16++;
                        break;
                    case 16 :
                    case 17:
                        count16To18++;
                        break;
                    case 18 :
                    case 20:
                        count18To20++;
                        break;
                }
            }
            dataEntry = new ArrayList<>();
            dataEntry.add(new BarEntry(count8To10, 0));
            dataEntry.add(new BarEntry(count10To12, 1));
            dataEntry.add(new BarEntry(count12To14, 2));
            dataEntry.add(new BarEntry(count14To16, 3));
            dataEntry.add(new BarEntry(count16To18, 4));
            dataEntry.add(new BarEntry(count18To20, 5));
            generateBarChart(dataEntry, barChart);
            Log.d("CustomInfoWindowAdapter", "count values: "+toString());
        }
    }

    /*Retrieve bookings statistics histories by times. */
    private void getStatsTimes(final String request, final BarChart barChart){
        statByTimes = new ArrayList<>();
        apiInterface = RetrofitApiClient.getApiClient().create(RetrofitApiInterface.class);

        Log.d("CustomInfoWindowAdapter", "get stats times running ");

       Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Call<List<QueueStatByDayTime>> call = apiInterface.getStatByTimes(request);
                call.enqueue(new Callback<List<QueueStatByDayTime>>() {

                    @Override
                    public void onResponse(Call<List<QueueStatByDayTime>> call, Response<List<QueueStatByDayTime>> response) {
                        Log.d("CustomInfoWindowAdapter", "response stat times: "+response.toString());
                        if(response != null){
                            statByTimes = response.body();
                            generateTimeStat(statByTimes, barChart);

                        }else{

                        }

                    }

                    @Override
                    public void onFailure(Call<List<QueueStatByDayTime>> call, Throwable t) {
                        Log.d("CustomInfoWindowAdapter", "Error times stats "+t.toString());
                        t.printStackTrace();
                    }
                });
            }
        });

        try {
            t.join(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.start();
    }

    @Override
    public String toString() {
        return "CustomInfoWindowAdapter{" +
                "count8To10=" + count8To10 +
                ", count10To12=" + count10To12 +
                ", count12To14=" + count12To14 +
                ", count14To16=" + count14To16 +
                ", count16To18=" + count16To18 +
                ", count18To20=" + count18To20 +
                '}';
    }
}

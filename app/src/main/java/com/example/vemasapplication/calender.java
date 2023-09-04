package com.example.vemasapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class calender extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        String accessToken = getIntent().getStringExtra("accessToken");
        Log.d("accesstoken", accessToken);

        CalendarView calendarView = findViewById(R.id.calendarView);
        LinearLayout bookingDetailsLayout = findViewById(R.id.linearLayoutContainer);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Create a Calendar object for the selected date
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(Calendar.YEAR, year);
                selectedCalendar.set(Calendar.MONTH, month);
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Set the time to midnight (00:00) for startDate
                Calendar startDate = (Calendar) selectedCalendar.clone();
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);

                // Set the time to 23:59 for endDate
                Calendar endDate = (Calendar) selectedCalendar.clone();
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);

                // Format the startDate and endDate in "yyyy-MM-dd HH:mm" format
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String formattedStartDate = dateFormat.format(startDate.getTime());
                String formattedEndDate = dateFormat.format(endDate.getTime());

                ApiClient.getBookings(accessToken, "99", "1", formattedStartDate, formattedEndDate, "", "", "", new ApiClient.ApiResponseListener() {
                    @Override
                    public void onResponse(final String response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Response", "JSON Response: " + response);

                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    JSONObject resultObject = jsonResponse.optJSONObject("result");

                                    if (resultObject != null) {
                                        JSONArray resultArray = resultObject.optJSONArray("result");
                                        Log.d("Response", "Result Array Length: " + (resultArray != null ? resultArray.length() : 0));

                                        // Clear any previous data in the layout
                                        bookingDetailsLayout.removeAllViews();

                                        if (resultArray != null) {
                                            for (int i = 0; i < resultArray.length(); i++) {
                                                JSONObject item = resultArray.optJSONObject(i);

                                                if (item != null) {
                                                    String vehicleNumber = item.optString("vehicleRegistrationNumber", "");
                                                    String ownerName = item.optString("customerName", "");
                                                    String time = item.optString("requestedDate", "");

                                                    Log.d("Response", "Vehicle Number: " + vehicleNumber);
                                                    Log.d("Response", "Owner's Name: " + ownerName);
                                                    Log.d("Response", "Time: " + time);

                                                    // Inflate the booking details item layout
                                                    View bookingDetailsItem = LayoutInflater.from(calender.this).inflate(R.layout.booking_details_layout, null);

                                                    TextView vehicleNumberTextView = bookingDetailsItem.findViewById(R.id.vehicleNumberTextView);
                                                    TextView ownerNameTextView = bookingDetailsItem.findViewById(R.id.ownerNameTextView);
                                                    TextView timeTextView = bookingDetailsItem.findViewById(R.id.timeTextView);

                                                    vehicleNumberTextView.setText("Vehicle Number: " + vehicleNumber);
                                                    ownerNameTextView.setText("Owner's Name: " + ownerName);
                                                    timeTextView.setText("Time: " + time);

                                                    // Add the booking details item to the layout
                                                    bookingDetailsLayout.addView(bookingDetailsItem);
                                                }
                                            }
                                        }
                                    } else {
                                        Log.d("Response", "No 'result' object found in the JSON response.");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final Exception e) {
                        // Handle API error here
                    }
                });
            }
        });
    }
}

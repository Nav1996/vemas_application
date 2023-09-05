package com.example.vemasapplication;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.vemasapplication.ApiClient;
import com.example.vemasapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class bookingform extends Activity {

    private EditText dateEditText;
    private EditText timeEditText;
    private Calendar calendar;
    private Spinner countrySpinner;
    private EditText vehicleRegistrationEditText;
    private EditText customerNameEditText;
    private EditText emailAddressEditText;
    private EditText phoneNumberEditText;
    private EditText notesEditText;
    String accessToken = "";
    String id = "";
    String formattedStartDate = "";
    String formattedEndDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookingform);
        accessToken = getIntent().getStringExtra("accessToken");
        id = getIntent().getStringExtra("id");
        formattedStartDate = getIntent().getStringExtra("startdate");
        formattedEndDate = getIntent().getStringExtra("enddate");
        fetchDataAndPopulateForm(accessToken,id);

        Spinner countrySpinner = findViewById(R.id.countrySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.countries, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        // Initialize references to your date and time EditText fields
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);

        // Initialize the Calendar instance
        calendar = Calendar.getInstance();

        // Set initial date and time in the EditText fields
        updateDateAndTimeFields();

        // Initialize other UI elements
        countrySpinner = findViewById(R.id.countrySpinner);
        vehicleRegistrationEditText = findViewById(R.id.vehicleRegistrationEditText);
        customerNameEditText = findViewById(R.id.customerNameEditText);
        emailAddressEditText = findViewById(R.id.emailAddressEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        notesEditText = findViewById(R.id.notesEditText);

        // Find the ImageButton by its ID
        ImageButton saveButton = findViewById(R.id.saveButton);

// Set an OnClickListener for the ImageButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the createBooking function here
                updateBooking();
            }
        });

    }

    public void openDatePicker(View view) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        updateDateAndTimeFields();
                    }
                },
                year,
                month,
                dayOfMonth
        );
        datePickerDialog.show();
    }

    public void openTimePicker(View view) {
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        updateDateAndTimeFields();
                    }
                },
                hourOfDay,
                minute,
                true // 24-hour format
        );
        timePickerDialog.show();
    }

    private void updateDateAndTimeFields() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        dateEditText.setText(dateFormat.format(calendar.getTime()));
        timeEditText.setText(timeFormat.format(calendar.getTime()));
    }


    private void createBooking() {

        String vehicleId = "0"; // Replace with the actual vehicle ID
        String requestedDate = dateEditText.getText().toString();
        String customerId = "0"; // Replace with the actual customer ID
        String customerName = customerNameEditText.getText().toString();
        String customerEmail = emailAddressEditText.getText().toString();
        String customerMobile = phoneNumberEditText.getText().toString();
        String notes = notesEditText.getText().toString();
        String customerPhone = phoneNumberEditText.getText().toString();
        String objectId = "BOOKING";
        String vehicleRegistrationNumber = vehicleRegistrationEditText.getText().toString();
        String status = "50";

        // Call the createBooking function from ApiClient
        ApiClient.createBooking(accessToken, vehicleId, requestedDate, customerId,
                customerName, customerEmail, customerMobile, notes,
                customerPhone, objectId, vehicleRegistrationNumber, status,
                new ApiClient.ApiResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        // Parse the JSON response
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int statusCode = jsonResponse.getInt("statusCode");


                            if (statusCode == 201) {
                                // Success, show a toast with the success message
                                showToast("Success");
                                // Create an Intent to start the 'calender' activity
                                Intent intent = new Intent(bookingform.this, calender.class);

                                // Pass the access token as an extra to the 'calender' activity
                                intent.putExtra("accessToken", accessToken);
                            } else {
                                // Error, show a toast with the error message
                                showToast("Error");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                            showToast("Error: Invalid JSON response");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        showToast("Error: " + e.getMessage());
                    }
                });
    }
    // Helper method to show a toast on the main UI thread
    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(bookingform.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Inside your bookingform activity
    // Inside your bookingform activity
    private void fetchDataAndPopulateForm(String accessToken, String bookingId) {
        Log.d("fetchDataAndPopulateForm", "Called with accessToken: " + accessToken + " and bookingId: " + bookingId);

        // Get the list of bookings
        ApiClient.getBookings(accessToken, "10", "1", formattedStartDate, formattedEndDate, "", "", "", new ApiClient.ApiResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject resultObject = jsonResponse.optJSONObject("result");

                    if (resultObject != null) {
                        JSONArray resultArray = resultObject.optJSONArray("result");
                        Log.d("Response", "Result Array Length: " + (resultArray != null ? resultArray.length() : 0));

                        if (resultArray != null) {
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject bookingData = resultArray.getJSONObject(i);
                                String bookingIdResponse = bookingData.optString("id");
                                Log.d("Booking ID in Response", bookingIdResponse);

                                if (bookingIdResponse.equals(bookingId)) {
                                    // Found the matching booking, populate the form
                                    Log.d("Matching Booking Found", bookingData.toString());
                                    populateFormWithData(bookingData.toString());
                                    return;
                                }
                            }
                        } else {
                            // Handle the case where no bookings are found
                            showToast("No bookings found");
                        }
                    } else {
                        // Handle the case where 'resultObject' is null
                        showToast("No 'result' object found in the JSON response");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Error: Invalid JSON response");
                }
            }

            @Override
            public void onError(Exception e) {
                // Handle API errors here
                showToast("Error: " + e.getMessage());
            }
        });
    }





    private void populateFormWithData(String response) {
        try {
            // Parse the JSON response
            Log.d("response",response);
            JSONObject jsonResponse = new JSONObject(response);

            // Extract data from the JSON response and populate your form fields
            String vehicleRegistrationNumber = jsonResponse.optString("vehicleRegistrationNumber", "");
            String customerName = jsonResponse.optString("customerName", "");
            String customerEmail = jsonResponse.optString("customerEmail", "");
            String customerPhone = jsonResponse.optString("customerPhone", "");
            String notes = jsonResponse.optString("notes", "");

            // Populate your form fields
            vehicleRegistrationEditText.setText(vehicleRegistrationNumber);
            customerNameEditText.setText(customerName);
            emailAddressEditText.setText(customerEmail);
            phoneNumberEditText.setText(customerPhone);
            notesEditText.setText(notes);

            // You can populate other fields in a similar manner
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Error: Invalid JSON response");
        }
    }

    private void updateBooking() {
        String bookingId = id; // Replace with the actual booking ID

        // Get input data
        String requestedDate = dateEditText.getText().toString();
        String customerName = customerNameEditText.getText().toString();
        String customerEmail = emailAddressEditText.getText().toString();
        String customerMobile = phoneNumberEditText.getText().toString();
        String notes = notesEditText.getText().toString();
        String customerPhone = phoneNumberEditText.getText().toString();
        String vehicleRegistrationNumber = vehicleRegistrationEditText.getText().toString();
        String status = "50"; // Replace with the desired status code



        // Perform input validation
        if (TextUtils.isEmpty(requestedDate) || TextUtils.isEmpty(customerName) ||
                TextUtils.isEmpty(customerEmail) || TextUtils.isEmpty(customerMobile) ||
                TextUtils.isEmpty(customerPhone) || TextUtils.isEmpty(vehicleRegistrationNumber)) {
            showToast("Please fill in all required fields");
            return;
        }

        // Format the date in the desired format
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US); // Specify the input date format
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            Date date = inputFormat.parse(requestedDate);
            requestedDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            showToast("Error: Invalid date format");
            return;
        }

        Log.d("date",requestedDate);

        // Call the updateBooking function from ApiClient
        ApiClient.updateBooking(accessToken, bookingId, "0","0", requestedDate, customerName, customerEmail, customerMobile, notes, customerPhone, vehicleRegistrationNumber, status, new ApiClient.ApiResponseListener() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int statusCode = jsonResponse.getInt("statusCode");

                    if (statusCode == 200) {
                        // Success, show a toast with the success message
                        showToast("Booking updated successfully");
                    } else {
                        // Error, show a toast with the error message
                        showToast("Error: " + jsonResponse.optString("errorMessage", "Unknown error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                    showToast("Error: Invalid JSON response");
                }
            }

            @Override
            public void onError(Exception e) {
                showToast("Error: " + e.getMessage());
            }
        });
    }





}

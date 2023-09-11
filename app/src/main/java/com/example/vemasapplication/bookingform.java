package com.example.vemasapplication;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
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
    private Button saveButton,cancelButton,updateButton;
    private ImageButton deleteButton,arrowButton;
    String accessToken = "";
    String id = "";
    String formattedStartDate = "";
    String formattedEndDate = "";
    String update = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookingform);
        accessToken = getIntent().getStringExtra("accessToken");
        id = getIntent().getStringExtra("id");
        formattedStartDate = getIntent().getStringExtra("startdate");
        formattedEndDate = getIntent().getStringExtra("enddate");
        update =  getIntent().getStringExtra("update");
        fetchDataAndPopulateForm(accessToken,id);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        arrowButton = findViewById(R.id.arrowButton);


        arrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an Intent to start the 'calendar' activity
                Intent intent = new Intent(bookingform.this, calender.class);

                // Pass the access token as an extra to the 'calendar' activity
                intent.putExtra("accessToken", accessToken);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });
        if(update != null && update.equals("update")){
            saveButton.setVisibility(View.GONE);

        }else{
            deleteButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.GONE);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the confirmation dialog
                deleteBooking();
            }
        });

// Method to show the confirmation dialog

        countrySpinner = findViewById(R.id.countrySpinner);
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

        vehicleRegistrationEditText = findViewById(R.id.vehicleRegistrationEditText);
        customerNameEditText = findViewById(R.id.customerNameEditText);
        emailAddressEditText = findViewById(R.id.emailAddressEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        notesEditText = findViewById(R.id.notesEditText);



        // Find the ImageButton by its ID
        Button saveButton = findViewById(R.id.saveButton);

// Set an OnClickListener for the ImageButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the createBooking function here
                createBooking();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBooking();
            }
        });

    }
    private void showConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.confirmation_dialog, null);
        builder.setView(dialogView);

        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        Button buttonYes = dialogView.findViewById(R.id.buttonYes);
        Button buttonNo = dialogView.findViewById(R.id.buttonNo);

        dialogMessage.setText("Are you sure you want to delete this item?");

        AlertDialog dialog = builder.create();

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBooking();
                dialog.dismiss();
                Intent intent = new Intent(bookingform.this, calender.class);

                // Pass the access token as an extra to the 'calendar' activity
                intent.putExtra("accessToken", accessToken);
                intent.putExtra("delete", "delete");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                // Start the 'calendar' activity
                startActivity(intent);// Dismiss the dialog
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog when "No" is clicked
            }
        });

        // Show the dialog
        dialog.show();
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
        String selectedDate = dateEditText.getText().toString();
        String selectedTime = timeEditText.getText().toString();
        String customerId = "0"; // Replace with the actual customer ID
        String customerName = customerNameEditText.getText().toString();
        String customerEmail = emailAddressEditText.getText().toString();
        String customerMobile = countrySpinner.getSelectedItem().toString() + phoneNumberEditText.getText().toString();
        String notes = notesEditText.getText().toString();
        String customerPhone = countrySpinner.getSelectedItem().toString() + phoneNumberEditText.getText().toString();
        String objectId = "BOOKING";
        String vehicleRegistrationNumber = vehicleRegistrationEditText.getText().toString();
        String status = "50";
        String dateTimeString = selectedDate + "T" + selectedTime;

        // Validate the date format
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        inputDateFormat.setLenient(false);

        try {
            Date date = inputDateFormat.parse(selectedDate);
            if (!inputDateFormat.format(date).equals(selectedDate)) {
                // The date format is invalid
                showToast("Invalid date format. Please use dd MMM yyyy format.");
                return;
            }
        } catch (ParseException e) {
            // Invalid date format
            showToast("Invalid date format. Please use dd MMM yyyy format.");
            return;
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy'T'HH:mm", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String requestedDate;


        if (TextUtils.isEmpty(customerName) || TextUtils.isEmpty(customerEmail) ||
                TextUtils.isEmpty(customerMobile) || TextUtils.isEmpty(vehicleRegistrationNumber)) {
            showToast("Please fill in all required fields");
            return;
        }

        try {
            Date date = inputFormat.parse(dateTimeString);
            requestedDate = outputFormat.format(date);

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
                                    showToast("Booking added successfully");

                                    // Create an Intent to start the 'calendar' activity
                                    Intent intent = new Intent(bookingform.this, calender.class);

                                    // Pass the access token as an extra to the 'calendar' activity
                                    intent.putExtra("accessToken", accessToken);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("operationResult", "success");

                                    startActivity(intent);
                                    finish();
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
        } catch (ParseException e) {
            e.printStackTrace();
            showToast("Error: Invalid date or time format");
        }
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
            Log.d("response", response);
            JSONObject jsonResponse = new JSONObject(response);

            // Extract data from the JSON response and populate your form fields
            String vehicleRegistrationNumber = jsonResponse.optString("vehicleRegistrationNumber", "");
            String customerName = jsonResponse.optString("customerName", "");
            String customerEmail = jsonResponse.optString("customerEmail", "");
            String customerPhone = jsonResponse.optString("customerPhone", "");
            String notes = jsonResponse.optString("notes", "");
            String requestedDateAndTime = jsonResponse.optString("requestedDate", ""); // Replace "requestedDate" with the actual field name in your JSON response for date and time

            // Split the requestedDateAndTime into date and time parts
            String[] dateAndTimeParts = requestedDateAndTime.split("T");
            String datePart = dateAndTimeParts[0];
            String timePart = dateAndTimeParts[1];

            // Format datePart into the desired format "dd MMM yyyy"
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);

            try {
                Date date = inputFormat.parse(datePart);
                datePart = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                showToast("Error: Invalid date format");
                return;
            }

            // Use runOnUiThread to update UI elements
            final String finalDatePart = datePart; // Declare datePart as final

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Populate your form fields
                    vehicleRegistrationEditText.setText(vehicleRegistrationNumber);
                    customerNameEditText.setText(customerName);
                    emailAddressEditText.setText(customerEmail);

                    // Extract the country code from the phone number
                    String countryCode = getCountryCodeFromPhoneNumber(customerPhone);

                    // Set the value of the country spinner
                    if (!TextUtils.isEmpty(countryCode)) {
                        int position = getCountryPosition(countryCode);
                        if (position != -1) {
                            countrySpinner.setSelection(position);
                        }
                    }

                    // Set the remaining part of the phone number (excluding country code) in phoneNumberEditText
                    String remainingPhoneNumber = getRemainingPhoneNumber(customerPhone);
                    phoneNumberEditText.setText(remainingPhoneNumber);

                    notesEditText.setText(notes);

                    // Set formatted date and time fields
                    dateEditText.setText(finalDatePart);
                    timeEditText.setText(timePart);

                    // You can populate other fields in a similar manner
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Error: Invalid JSON response");
        }
    }



    // Helper method to extract the country code from the phone number
    private String getCountryCodeFromPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && phoneNumber.length() >= 2) {
            return phoneNumber.substring(0, 2);
        }
        return "";
    }
    private int getCountryPosition(String countryCode) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) countrySpinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().startsWith(countryCode)) {
                    return i;
                }
            }
        }
        return -1;
    }


    // Helper method to get the remaining part of the phone number (excluding country code)
    private String getRemainingPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && phoneNumber.length() >= 2) {
            return phoneNumber.substring(2);
        }
        return "";
    }

    private void deleteBooking() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this booking?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Yes, proceed with the deletion
                performDeleteBooking();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked No, do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void performDeleteBooking() {
        ApiClient.deleteBooking(accessToken, id, new ApiClient.ApiResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    // Check if the response is a JSON object
                    if (response.startsWith("{")) {
                        // Parse the JSON response
                        JSONObject jsonResponse = new JSONObject(response);
                        int statusCode = jsonResponse.getInt("statusCode");
                        String message = jsonResponse.getString("message");

                        if (statusCode == 200) {
                            // Success, show the message from the response
                            showToast(message);
                        } else {
                            // Error, show a toast with the error message
                            showToast("Error: " + message);
                        }
                    } else {
                        // The response is not a JSON object, treat it as a plain message
                        showToast(response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                    showToast("Error: Invalid JSON response");
                }


                Intent intent = new Intent(bookingform.this, calender.class);

                // Pass the access token as an extra to the 'calendar' activity
                intent.putExtra("accessToken", accessToken);
                intent.putExtra("delete", "delete");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                // Start the 'calendar' activity
                startActivity(intent);

                // Optionally, navigate to another activity or perform any additional actions here
            }

            @Override
            public void onError(Exception e) {
                showToast("Error: " + e.getMessage());
            }
        });
    }




    private void updateBooking() {
        String bookingId = id; // Replace with the actual booking ID

        // Get input data
        String requestedDate = dateEditText.getText().toString();
        String requestedTime = timeEditText.getText().toString();
        String customerName = customerNameEditText.getText().toString();
        String customerEmail = emailAddressEditText.getText().toString();
        String customerMobile = phoneNumberEditText.getText().toString();
        String notes = notesEditText.getText().toString();
        String customerPhone = phoneNumberEditText.getText().toString();
        String vehicleRegistrationNumber = vehicleRegistrationEditText.getText().toString();
        String status = "50"; // Replace with the desired status code
        String dateTimeString = requestedDate + "T" + requestedTime;



        // Perform input validation
        if (TextUtils.isEmpty(requestedDate) || TextUtils.isEmpty(customerName) ||
                TextUtils.isEmpty(customerEmail) || TextUtils.isEmpty(customerMobile) ||
                TextUtils.isEmpty(customerPhone) || TextUtils.isEmpty(vehicleRegistrationNumber)) {
            showToast("Please fill in all required fields");
            return;
        }
        Log.d("selectedTime", requestedTime);
        Log.d("selectedDate", requestedDate);

        // Format the date in the desired format
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US); // Specify the input date format
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


        try {
            Date date = inputFormat.parse(dateTimeString);
            requestedDate = outputFormat.format(date);
            requestedDate = requestedDate.substring(0, 11) + requestedTime + requestedDate.substring(16);
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
                        Intent intent = new Intent(bookingform.this, calender.class);

                        // Pass the access token as an extra to the 'calendar' activity
                        intent.putExtra("accessToken", accessToken);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        // Start the 'calendar' activity
                        startActivity(intent);
                        finish();

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

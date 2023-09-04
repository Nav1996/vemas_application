package com.example.vemasapplication;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vemasapplication.ApiClient;
import com.example.vemasapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

public class login extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        ImageButton loginImageButton = findViewById(R.id.loginButton);

        loginImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the email and password from the EditText fields
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Check if email and password are not empty
                if (email.isEmpty() || password.isEmpty()) {
                    // Show an error message if either field is empty
                    Toast.makeText(login.this, "Email and password are required.", Toast.LENGTH_SHORT).show();
                } else {
                    // Call the signIn function with the email and password
                    ApiClient.signIn(email, password, new ApiClient.ApiResponseListener() {
                        @Override
                        public void onResponse(final String response) {
                            // Display a Toast message on the main UI thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("API Response", response);

                                    // Check if the response contains an access token
                                    if (response.contains("accessToken")) {
                                        try {
                                            // Parse the response as a JSON object
                                            JSONObject jsonResponse = new JSONObject(response);

                                            // Extract the access token
                                            String accessToken = jsonResponse.optString("accessToken");

                                            if (!accessToken.isEmpty()) {
                                                // Create an Intent to start the 'calender' activity
                                                Intent intent = new Intent(login.this, bookingform.class);

                                                // Pass the access token as an extra to the 'calender' activity
                                                intent.putExtra("accessToken", accessToken);

                                                // Start the 'calender' activity
                                                startActivity(intent);
                                            } else {
                                                // Show an error message if accessToken is empty or not present
                                                Toast.makeText(login.this, "Login failed. Invalid credentials.", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            // Handle JSON parsing error
                                            e.printStackTrace();
                                            Toast.makeText(login.this, "Error parsing API response.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Show an error message
                                        Toast.makeText(login.this, "Login failed. Invalid credentials.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(final Exception e) {
                            // Display a Toast message on the main UI thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(login.this, "Login failed. Invalid credentials.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

    }
}

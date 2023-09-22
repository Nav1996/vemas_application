package com.example.vemasapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class login extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressDialog progressDialog;
    private View keyboardPlaceholder;
    final int initialMarginTop = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        ImageButton loginImageButton = findViewById(R.id.loginButton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        ImageView logoImage = findViewById(R.id.logoImage);


        keyboardPlaceholder = findViewById(R.id.keyboardPlaceholder);

        // Add a global layout listener to detect keyboard visibility changes
        View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);

                int screenHeight = rootView.getHeight();
                int keypadHeight = screenHeight - rect.bottom;

                // Calculate the new margin top value based on keyboard visibility
                int newMarginTop;
                if (keypadHeight > screenHeight * 0.15) {
                    // Keyboard is shown, adjust margin top
                    newMarginTop = initialMarginTop - keypadHeight;
                } else {
                    // Keyboard is hidden, restore initial margin top
                    newMarginTop = initialMarginTop;
                }

                // Update the margin top of the logoImage
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) logoImage.getLayoutParams();
                params.setMargins(params.leftMargin, newMarginTop, params.rightMargin, params.bottomMargin);
                logoImage.setLayoutParams(params);
            }
        });

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
                    progressDialog.show();
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
                                                Intent intent = new Intent(login.this, calender.class);

                                                // Pass the access token as an extra to the 'calender' activity
                                                intent.putExtra("accessToken", accessToken);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

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
                                    progressDialog.dismiss();
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
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

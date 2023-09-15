package com.example.vemasapplication;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

    public class ApiClient {

    public interface ApiResponseListener {
        void onResponse(String response);
        void onError(Exception e);
    }



        public static void createBooking(String accessToken, String vehicleId, String requestedDate, String customerId,
                                         String customerName, String customerEmail, String customerMobile, String notes,
                                         String customerPhone, String objectId, String vehicleRegistrationNumber,
                                         String status, ApiResponseListener listener) {
            AsyncTask.execute(() -> {
                try {
                    // Create URL
                    URL url = new URL("https://dev.vemas.com.au/api/bookings");

                    // Create connection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", accessToken);
                    connection.setDoOutput(true);

                    // Create JSON payload
                    JSONObject jsonPayload = new JSONObject();
                    jsonPayload.put("VehicleId", vehicleId);
                    jsonPayload.put("RequestedDate", requestedDate);
                    jsonPayload.put("CustomerId", customerId);
                    jsonPayload.put("CustomerName", customerName);
                    jsonPayload.put("CustomerEmail", customerEmail);
                    jsonPayload.put("CustomerMobile", customerMobile);
                    jsonPayload.put("Notes", notes);
                    jsonPayload.put("CustomerPhone", customerPhone);
                    jsonPayload.put("ObjectId", objectId);
                    jsonPayload.put("VehicleRegistrationNumber", vehicleRegistrationNumber);
                    jsonPayload.put("Status", status);

                    // Write payload to the connection's output stream
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonPayload.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Get response
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // Notify the listener with the response
                        listener.onResponse(response.toString());
                    } else {
                        // Handle the error
                        listener.onError(new Exception("HTTP Error: " + responseCode));
                    }

                } catch (IOException | JSONException e) {
                    // Handle the exception
                    listener.onError(e);
                }
            });
        }

        public static void getBookings(String accessToken, String pageSize, String pageNumber, String fromDate, String toDate, String regNo, String customerName, String commonSearch, ApiResponseListener listener) {
            AsyncTask.execute(() -> {
                try {
                    // Create URL
                    String apiUrl = "https://dev.vemas.com.au/api/bookings?" +
                            "pageSize=" + pageSize +
                            "&pageNumber=" + pageNumber +
                            "&fromDate=" + fromDate +
                            "&toDate=" + toDate +
                            "&regNo=" + regNo +
                            "&customerName=" + customerName +
                            "&commonSearch=" + commonSearch;

                    URL url = new URL(apiUrl);

                    // Create connection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json");

                    // Set the Authorization header with the access token
                    connection.setRequestProperty("authorization",accessToken);

                    // Get response
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // Notify the listener with the response
                        listener.onResponse(response.toString());
                    } else {
                        // Handle the error
                        listener.onError(new Exception("HTTP Error: " + responseCode));
                    }

                } catch (IOException e) {
                    // Handle the exception
                    listener.onError(e);
                }
            });
        }


        public static void signIn(String userName, String password, ApiResponseListener listener) {
        AsyncTask.execute(() -> {
            try {
                // Create URL
                URL url = new URL("https://dev.vemas.com.au/api/auth/SignIn");

                // Create connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create JSON payload
                String jsonInputString = "{\"UserName\":\"" + userName + "\",\"Password\":\"" + password + "\"}";

                // Write payload to the connection's output stream
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Get response
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Notify the listener with the response
                    listener.onResponse(response.toString());
                } else {
                    // Handle the error
                    listener.onError(new Exception("HTTP Error: " + responseCode));
                }

            } catch (IOException e) {
                // Handle the exception
                listener.onError(e);
            }
        });
    }

        public static void getCustomerInfo(String accessToken, String apiUrl, ApiResponseListener listener) {
            AsyncTask.execute(() -> {
                try {
                    // Create URL
                    URL url = new URL(apiUrl);

                    // Create connection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json");

                    // Set the Authorization header with the access token
                    connection.setRequestProperty("Authorization", accessToken);

                    // Get response
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // Notify the listener with the response
                        listener.onResponse(response.toString());
                    } else {
                        // Handle the error
                        listener.onError(new Exception("HTTP Error: " + responseCode));
                    }

                } catch (IOException e) {
                    // Handle the exception
                    listener.onError(e);
                }
            });
        }


        public static void searchVehicles(String accessToken, String pageSize, String pageNumber, String customerId,
                                          String makeId, String modelId, String regNo, String status, String commonSearch,
                                          ApiResponseListener listener) {
            AsyncTask.execute(() -> {
                try {
                    // Create URL
                    String apiUrl = "https://dev.vemas.com.au/api/vehicles?" +
                            "pageSize=" + pageSize +
                            "&pageNumber=" + pageNumber +
                            "&CustomerId=" + customerId +
                            "&MakeId=" + makeId +
                            "&ModelId=" + modelId +
                            "&RegNo=" + regNo +
                            "&StatusId=" + status +
                            "&commonSearch=" + commonSearch;

                    URL url = new URL(apiUrl);

                    // Create connection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json");

                    // Set the Authorization header with the access token
                    connection.setRequestProperty("Authorization", accessToken);

                    // Get response
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // Notify the listener with the response
                        listener.onResponse(response.toString());
                    } else {
                        // Handle the error
                        listener.onError(new Exception("HTTP Error: " + responseCode));
                    }

                } catch (IOException e) {
                    // Handle the exception
                    listener.onError(e);
                }
            });
        }


        public static void deleteBooking(String accessToken, String bookingId, ApiResponseListener listener) {
            AsyncTask.execute(() -> {
                try {
                    // Create URL
                    URL url = new URL("https://dev.vemas.com.au/api/bookings/" + bookingId);

                    // Create connection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("DELETE");
                    connection.setRequestProperty("Authorization", accessToken);

                    // Get response code
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // The booking was successfully deleted
                        listener.onResponse("Booking deleted successfully");
                    } else {
                        // Handle the error
                        listener.onError(new Exception("HTTP Error: " + responseCode));
                    }
                } catch (IOException e) {
                    // Handle the exception
                    listener.onError(e);
                }
            });
        }


        public static void updateBooking(String accessToken, String bookingId, String vehicleId, String CustomerId, String requestedDate, String customerName,
                                         String customerEmail, String customerMobile, String notes, String customerPhone,
                                         String vehicleRegistrationNumber, String status, ApiResponseListener listener) {
            AsyncTask.execute(() -> {
                try {
                    // Log all the parameters for debugging
                    Log.d("updateBooking", "accessToken: " + accessToken);
                    Log.d("updateBooking", "bookingId: " + bookingId);
                    Log.d("updateBooking", "vehicleId: " + vehicleId);
                    Log.d("updateBooking", "CustomerId: " + CustomerId);
                    Log.d("updateBooking", "requestedDate: " + requestedDate);
                    Log.d("updateBooking", "customerName: " + customerName);
                    Log.d("updateBooking", "customerEmail: " + customerEmail);
                    Log.d("updateBooking", "customerMobile: " + customerMobile);
                    Log.d("updateBooking", "notes: " + notes);
                    Log.d("updateBooking", "customerPhone: " + customerPhone);
                    Log.d("updateBooking", "vehicleRegistrationNumber: " + vehicleRegistrationNumber);
                    Log.d("updateBooking", "status: " + status);

                    // Create URL
                    URL url = new URL("https://dev.vemas.com.au/api/bookings/" + bookingId);

                    // Create connection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PATCH");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", accessToken);
                    connection.setDoOutput(true);

                    // Create JSON payload
                    JSONObject jsonPayload = new JSONObject();
                    jsonPayload.put("vehicleId", vehicleId);
                    jsonPayload.put("CustomerId", CustomerId);
                    jsonPayload.put("RequestedDate", requestedDate);
                    jsonPayload.put("CustomerName", customerName);
                    jsonPayload.put("CustomerEmail", customerEmail);
                    jsonPayload.put("CustomerMobile", customerMobile);
                    jsonPayload.put("ObjectId", "BOOKING");
                    jsonPayload.put("Notes", notes);
                    jsonPayload.put("CustomerPhone", customerPhone);
                    jsonPayload.put("VehicleRegistrationNumber", vehicleRegistrationNumber);
                    jsonPayload.put("Status", status);

                    // Write payload to the connection's output stream
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonPayload.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Get response
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // Notify the listener with the response
                        listener.onResponse(response.toString());
                    } else {
                        // Handle the error
                        listener.onError(new Exception("HTTP Error: " + responseCode));
                    }

                } catch (IOException | JSONException e) {
                    // Handle the exception
                    listener.onError(e);
                }
            });
        }


    }

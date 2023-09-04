package com.example.vemasapplication;
import android.os.AsyncTask;
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
}

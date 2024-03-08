package wildepizza.com.github.blizzity;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class API {
    // Replace with the actual server address
     String serverUrl;
    API(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    public void test() {

        // Replace with the actual authorization token
        String authToken = "correct_auth_token";

        // Replace with the actual name parameter
        String name = "YourName";

        // Construct the API endpoint URL
        String apiUrl = serverUrl + "/api/welcome?name=" + name;

        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Create an HTTP request with authorization header
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", authToken)
                .build();

        try {
            // Send the HTTP request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print the response status code and body
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean login(String username, String password) {
        // Construct the API endpoint URL
        String apiUrl = serverUrl + "/login?username=" + username + "&password=" + password;

        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Create an HTTP request with authorization header
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        try {
            // Send the HTTP request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print the response status code and body
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            return response.body().equals("Successfully logged in");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean register(String username, String password) {
        // Construct the API endpoint URL
        String apiUrl = serverUrl + "/register?username=" + username + "&password=" + password;

        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Create an HTTP request with authorization header
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        try {
            // Send the HTTP request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print the response status code and body
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            return response.body().equals("Successfully registered");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean verify(String username) {
        // Construct the API endpoint URL
        String apiUrl = serverUrl + "/verify?username=" + username;

        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Create an HTTP request with authorization header
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        try {
            // Send the HTTP request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print the response status code and body
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            return response.body().equals("Username is available");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean video(String authToken, String language, int length) {
        String apiUrl = serverUrl + "/api/video?language=" + language + "&length=" + length;

        try {
            // Create URL object
            URL url = new URL(apiUrl);

            // Create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("GET");

            // Set request headers
            connection.setRequestProperty("Authorization", authToken);
            System.out.println(authToken);

            // Get response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Get input stream from the connection
                InputStream inputStream = connection.getInputStream();

                // Create a file output stream to save the video file
                FileOutputStream outputStream = new FileOutputStream("received_video.mp4");

                // Read data from input stream and write to output stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Close streams
                outputStream.close();
                inputStream.close();

                System.out.println("Video file received successfully.");
                connection.disconnect();
                return true;
            } else {
                connection.disconnect();
                System.out.println(connection.getResponseMessage());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public String usages(String key) {
        String apiUrl = serverUrl + "/api/usages?key=" + URLEncoder.encode(key);

        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Create an HTTP request with authorization header
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        try {
            // Send the HTTP request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print the response status code and body
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
    public String credits(String key) {
        String apiUrl = serverUrl + "/api/credits?key=" + URLEncoder.encode(key);

        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Create an HTTP request with authorization header
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        try {
            // Send the HTTP request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print the response status code and body
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}

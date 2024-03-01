package wildepizza.com.github.blizzity;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
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
            return !response.body().equals("wrong password");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void video() {
        String authToken = "correct_auth_token";

        String name = "english";
        int length = 10;
        String apiUrl = serverUrl + "/api/video?language=" + name + "&length=" + length;

        try {
            // Create URL object
            URL url = new URL(apiUrl);

            // Create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("GET");

            // Set request headers
            connection.setRequestProperty("Authorization", authToken);

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
            } else {
                System.out.println("Failed to receive video file. Response code: " + responseCode);
            }

            // Disconnect the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String usages(String key) {
        String apiUrl = serverUrl + "/api/usages?key=" + key;

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

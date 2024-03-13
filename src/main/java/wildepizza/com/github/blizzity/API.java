package wildepizza.com.github.blizzity;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicReference;

public class API {
    // Replace with the actual server address
     String serverUrl;
    API(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    public boolean login(String username, String password) {
        String apiUrl = serverUrl + "/login?username=" + username + "&password=" + password;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body().equals("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean register(String username, String password) {
        String apiUrl = serverUrl + "/register?username=" + username + "&password=" + password;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body().equals("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean verify(String username) {
        String apiUrl = serverUrl + "/api/username/available?username=" + username;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body().equals("true");
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
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean verify(String space, String key) {
        String apiUrl = serverUrl + "/api/verify/" + space + "?key=" + URLEncoder.encode(key);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                if (response.body().equals("true")) {
                    return true;
                } else {
                    AtomicReference<String> code = new AtomicReference<>();
                    spark.Spark.get("/google/callback", (req, res) -> {
                        code.set(req.queryParams("code"));
                        res.redirect("https://blizzity.de?close=true");
                        return "";
                    });
                    Desktop.getDesktop().browse(URI.create(response.body()));
                    while (code.get() == null)
                        Thread.sleep(1);
                    apiUrl = serverUrl + "/api/verify/" + space + "/key?key=" + URLEncoder.encode(key) + "&code=" + code.get();
                    client = HttpClient.newHttpClient();
                    request = HttpRequest.newBuilder()
                            .uri(URI.create(apiUrl))
                            .build();
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    return response.body().equals("success");
                }
            } else {
                System.out.println("Response Code: " + response.statusCode());
                System.out.println("Response Body: " + response.body());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public String credits(String key) {
        String apiUrl = serverUrl + "/api/credits?key=" + URLEncoder.encode(key);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

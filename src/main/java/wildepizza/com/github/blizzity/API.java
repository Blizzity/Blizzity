package wildepizza.com.github.blizzity;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import spark.Spark;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicReference;

public class API {
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
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return response.body().equals("success");
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
    public boolean register(String username, String password) {
        String apiUrl = serverUrl + "/register?username=" + username + "&password=" + password;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return response.body().equals("success");
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
    public boolean verify(String username) {
        String apiUrl = serverUrl + "/api/username/available?username=" + username;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return response.body().equals("true");
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
    public File video(String authToken, String language, int length) {
        String apiUrl = serverUrl + "/api/video?language=" + language + "&length=" + length;

        try {
            // Create URL object
            URL url = new URL(apiUrl);

            // Create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("GET");

            // Set request headers
            connection.setRequestProperty("Authorization", URLEncoder.encode(authToken));
            System.out.println(authToken);

            // Get response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Get input stream from the connection
                InputStream inputStream = connection.getInputStream();

                File outputFile = File.createTempFile("received_video", ".mp4");
                // Create a file output stream to save the video file
                FileOutputStream outputStream = new FileOutputStream(outputFile);

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
                return outputFile;
            } else {
                System.out.println("Response Code: " + connection.getResponseCode());
                System.out.println("Response Body: " + connection.getResponseMessage());
                connection.disconnect();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String usages(String key) {
        String apiUrl = serverUrl + "/api/usages?key=" + URLEncoder.encode(key);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            assert response != null;
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            return null;
        }
    }
    public String credits(String key) {
        String apiUrl = serverUrl + "/api/credits?key=" + URLEncoder.encode(key);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            assert response != null;
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
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
                    Wait wait = new Wait();
                    AtomicReference<String> code = new AtomicReference<>();
                    if (space.equals("youtube")) {
                        Spark.get("/google/callback", (req, res) -> {
                            code.set(req.queryParams("code"));
                            res.redirect("https://blizzity.de?close=true");
                            wait.setVariable(true);
                            if (code.get() != null) {
                                return "Received code: " + code.get();
                            } else {
                                return "No code parameter found in the URL";
                            }
                        });
                    } else if (space.equals("tiktok")) {
                        Spark.get("/tiktok/callback", (req, res) -> {
                            code.set(req.queryParams("code"));
                            res.redirect("https://blizzity.de?close=true");
                            wait.setVariable(true);
                            if (code.get() != null) {
                                return "Received code: " + code.get();
                            } else {
                                return "No code parameter found in the URL";
                            }
                        });
                    } else
                        return false;

                    String url = response.body();
                    openURLInNewWindow(url, 400, 600);

                    wait.waitForVariable();
                    Spark.stop();
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
    public static void openURLInNewWindow(String url, int width, int height) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    URI uri = new URI(url);
                    desktop.browse(uri);

                    // Get screen size
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                    // Calculate position for the window to be centered
                    int x = (screenSize.width - width) / 2;
                    int y = (screenSize.height - height) / 2;

                    // Create and set the custom size for the frame
                    Frame frame = new Frame();
                    frame.setSize(width, height);
                    frame.setLocation(x, y);
                    frame.setVisible(true);
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}

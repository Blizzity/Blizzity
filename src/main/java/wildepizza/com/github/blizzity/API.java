package wildepizza.com.github.blizzity;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public List<Map<String, String>> info(String space, String authToken) {
        List<Map<String, String>> result = new ArrayList<>();
        String apiUrl = serverUrl + "/api/info/" + space + "?key=" + URLEncoder.encode(authToken);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            Map<String, String> info = new HashMap<>();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                for (String body : response.body().split(";")) {
                    info.put("display_name", jsonPart(body, "\"display_name\":\""));
                    info.put("avatar", jsonPart(body, "\"avatar_url\":\"").replace("\\u0026", "&"));
                    info.put("username", jsonPart(body, "\"username\":\""));
                    result.add(info);
                }
                return result;
            } else {
                System.out.println("Response Code: " + response.statusCode());
                System.out.println("Response Body: " + response.body());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String jsonPart(String json, String start) {
        int fromIndex = json.indexOf(start) + start.length();
        return json.substring(fromIndex, json.indexOf("\"", fromIndex));
    }
    public Double<File, String> video(String authToken, String language, int length) {

        String apiUrl = serverUrl + "/api/generate?language=" + language + "&length=" + length;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", URLEncoder.encode(authToken))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Get response code
            int responseCode = response.statusCode();
            String video = response.body();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                apiUrl = serverUrl + "/api/video?video=" + video;

                // Create URL object
                URL url = new URL(apiUrl);

                // Create connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set request method
                connection.setRequestMethod("GET");

                // Set request headers
                connection.setRequestProperty("Authorization", URLEncoder.encode(authToken));

                // Get response code
                responseCode = connection.getResponseCode();


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
                    return new Double<>(outputFile, video);
                } else {
                    System.out.println("Response Code: " + connection.getResponseCode());
                    System.out.println("Response Body: " + connection.getResponseMessage());
                    connection.disconnect();
                    return null;
                }
            } else {
                System.out.println("Response Code: " + response.statusCode());
                System.out.println("Response Body: " + response.body());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void youtubePost(String authToken, String video, String title, String description, String privacy_level) {
        String apiUrl = serverUrl + "/api/upload/youtube?video=" + video + "&key=" + URLEncoder.encode(authToken) + "&title=" + title + "&description=" + description + "&privacy_level=" + privacy_level;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Get response code
            int responseCode = response.statusCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Successfully posted video: " + apiUrl);
            } else {
                System.out.println("Response Code: " + response.statusCode());
                System.out.println("Response Body: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void tiktokPost(String authToken, String video, String title, String privacy_level, boolean disable_duet, boolean disable_comment, boolean disable_stitch, int video_cover_timestamp_ms) {
        String apiUrl = serverUrl + "/api/upload/tiktok?video=" + video + "&key=" + URLEncoder.encode(authToken) + "&title=" + title + "&privacy_level=" + privacy_level + "&disable_duet=" + disable_duet + "&disable_comment=" + disable_comment + "&disable_stitch=" + disable_stitch + "&video_cover_timestamp_ms=" + video_cover_timestamp_ms;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
//                    .header("Authorization", URLEncoder.encode(authToken))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Get response code
            int responseCode = response.statusCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Successfully posted video: " + apiUrl);
            } else {
                System.out.println("Response Code: " + response.statusCode());
                System.out.println("Response Body: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        spark.Spark.get("/google/callback", (req, res) -> {
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
                        spark.Spark.get("/tiktok/callback", (req, res) -> {
                            code.set(req.queryParams("code"));
                            res.redirect("https://blizzity.de?close=true");
                            wait.setVariable(true);
                            if (code.get() != null) {
                                return "Received code: " + code.get();
                            } else {
                                return "No code parameter found in the URL";
                            }
                        });
                    } else if (space.equals("snapchat")) {
                        spark.Spark.get("/snapchat/callback", (req, res) -> {
                            code.set(req.queryParams("code"));
                            res.redirect("https://blizzity.de?close=true");
                            wait.setVariable(true);
                            if (code.get() != null) {
                                return "Received code: " + code.get();
                            } else {
                                return "No code parameter found in the URL";
                            }
                        });
                    } else if (space.equals("facebook")) {
                        spark.Spark.get("/facebook/callback", (req, res) -> {
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
                    /*Engine engine = Engine.newInstance(RenderingMode.HARDWARE_ACCELERATED);
                    Browser browser = engine.newBrowser();
                    browser.navigation().loadUrl(url);
                    BrowserView view = BrowserView.newInstance(browser);
                    BorderPane root = new BorderPane(view);
                    Scene scene = new Scene(root, 800, 600);
                    JFXPanel panel = new JFXPanel();
                    panel.setScene(scene);
                    frame.add(panel);
                    frame.pack();*/

                    System.out.println(url);
                    Desktop.getDesktop().browse(URI.create(url));

                    wait.waitForVariable();
                    System.out.println(code.get());
                    spark.Spark.stop();
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

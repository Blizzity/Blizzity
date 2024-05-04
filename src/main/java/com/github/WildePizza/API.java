package com.github.WildePizza;

import spark.Spark;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("deprecation")
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
                Logger.response(response);
                return false;
            }
        } catch (Exception e) {
            Logger.exception(e);
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
                Logger.response(response);
                return false;
            }
        } catch (Exception e) {
            Logger.exception(e);
            return false;
        }
    }
    public boolean connect(String username) {
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
                Logger.response(response);
                return false;
            }
        } catch (Exception e) {
            Logger.exception(e);
            return false;
        }
    }
    private final Map<String, List<Map<String, String>>> info = new HashMap<>();
    public List<Map<String, String>> info(String space, String authToken) {
        return info(space, authToken, false);
    }
    public List<Map<String, String>> info(String space, String authToken, boolean refresh) {
        if (info.get(space) == null || info.get(space).isEmpty() || refresh) {
            List<Map<String, String>> result = new ArrayList<>();
            String apiUrl = serverUrl + "/api/social/info?key=" + URLEncoder.encode(authToken) + "&space=" + space;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                    for (String body : response.body().split(";")) {
                        Map<String, String> info = new HashMap<>();
                        info.put("display_name", jsonPart(body, "\"display_name\":\""));
                        info.put("avatar", jsonPart(body, "\"avatar_url\":\"").replace("\\u0026", "&"));
                        info.put("username", jsonPart(body, "\"username\":\""));
                        info.put("id", jsonPart(body, "\"id\":\""));
                        info.put("selected", jsonPart(body, "\"selected\":\""));
                        result.add(info);
                    }
                    info.put(space, result);
                    return result;
                } else {
                    Logger.response(response);
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                Logger.exception(e);
                return null;
            }
        } else
            return info.get(space);
    }
    public static String jsonPart(String json, String start) {
        int fromIndex = json.indexOf(start) + start.length();
        return json.substring(fromIndex, json.indexOf("\"", fromIndex));
    }
    public Double<File, String> video(String authToken, String language, int length) {
        String apiUrl = serverUrl + "/api/generate?language=" + language + "&length=" + length;
        HttpResponse<String> response = null;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", URLEncoder.encode(authToken))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int responseCode = response.statusCode();
            String video = response.body();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                apiUrl = serverUrl + "/api/video?video=" + video;
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", URLEncoder.encode(authToken));
                responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    File outputFile = File.createTempFile("received_video", ".mp4");
                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    inputStream.close();
                    connection.disconnect();
                    return new Double<>(outputFile, video);
                } else {
                    Logger.response(response);
                    connection.disconnect();
                    return null;
                }
            } else {
                Logger.response(response);
                return null;
            }
        } catch (Exception e) {
            if (response != null)
                Logger.response(response);
            Logger.exception(e);
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
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Logger.response(response);
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }
    public void snapchatPost(String authToken, String video, String title) {
        String apiUrl = serverUrl + "/api/upload/snapchat?video=" + video + "&key=" + URLEncoder.encode(authToken) + "&title=" + title;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Get response code
            int responseCode = response.statusCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Logger.response(response);
            }
        } catch (Exception e) {
            Logger.exception(e);
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
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Logger.response(response);
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }
    public boolean admin(String key) {
        String apiUrl = serverUrl + "/api/admin?key=" + URLEncoder.encode(key);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Boolean.parseBoolean(response.body());
        } catch (Exception e) {
            assert response != null;
            Logger.response(response);
            Logger.exception(e);
            return false;
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
            Logger.exception(e);
            assert response != null;
            Logger.response(response);
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
            Logger.exception(e);
            assert response != null;
            Logger.response(response);
            return null;
        }
    }
    public boolean check(String space, String key) {
        String apiUrl = serverUrl + "/api/social/check?key=" + URLEncoder.encode(key) + "&space=" + space;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return Boolean.parseBoolean(response.body());
            }
            return false;
        } catch (Exception e) {
            Logger.exception(e);
            return false;
        }
    }
    public boolean select(String space, String key, String id) {
        String apiUrl = serverUrl + "/api/social/select?key=" + URLEncoder.encode(key) + "&space=" + space + "&id=" + id;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return response.body().equals("success");
            }
            return false;
        } catch (Exception e) {
            Logger.exception(e);
            return false;
        }
    }
    public Map<String, String> adminCheck(String space, String language, String key) {
        String apiUrl = serverUrl + "/api/admin/check?key=" + URLEncoder.encode(key) + "&language=" + language + "&space=" + space;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                if (!response.body().isEmpty()) {
                    Map<String, String> info = new HashMap<>();
                    info.put("display_name", jsonPart(response.body(), "\"display_name\":\""));
                    info.put("avatar", jsonPart(response.body(), "\"avatar_url\":\"").replace("\\u0026", "&"));
                    info.put("username", jsonPart(response.body(), "\"username\":\""));
                    info.put("id", jsonPart(response.body(), "\"id\":\""));
                    info.put("selected", jsonPart(response.body(), "\"selected\":\""));
                    return info;
                }
            } else {
                Logger.response(response);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean admin(String space, String language, String key, String channel) {
        String apiUrl = serverUrl + "/api/admin/add?key=" + URLEncoder.encode(key) + "&language=" + language + "&space=" + space + "&channel=" + channel;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK || response.body().equals("success")) {
                return response.body().equals("success");
            } else {
                Logger.response(response);
            }
            return false;
        } catch (Exception e) {
            Logger.exception(e);
            return false;
        }
    }
    public boolean connect(String space, String key) {
        String apiUrl = serverUrl + "/api/social/url?key=" + URLEncoder.encode(key) + "&space=" + space;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                Wait wait = new Wait();
                AtomicReference<String> verifier = new AtomicReference<>();
                AtomicReference<String> code = new AtomicReference<>();
                switch (space) {
                    case "youtube":
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
                        break;
                    case "tiktok":
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
                        break;
                    case "snapchat":
                        Spark.get("/snapchat/callback", (req, res) -> {
                            code.set(req.queryParams("code"));
                            res.redirect("https://blizzity.de?close=true");
                            wait.setVariable(true);
                            if (code.get() != null) {
                                return "Received code: " + code.get();
                            } else {
                                return "No code parameter found in the URL";
                            }
                        });
                        break;
                    case "facebook":
                        Spark.get("/facebook/callback", (req, res) -> {
                            code.set(req.queryParams("code"));
                            res.redirect("https://blizzity.de?close=true");
                            wait.setVariable(true);
                            if (code.get() != null) {
                                return "Received code: " + code.get();
                            } else {
                                return "No code parameter found in the URL";
                            }
                        });
                        break;
                    case "x":
                        Spark.get("/x/callback", (req, res) -> {
                            code.set(req.queryParams("oauth_token"));
                            verifier.set(req.queryParams("oauth_verifier"));
                            res.redirect("https://blizzity.de?close=true");
                            wait.setVariable(true);
                            if (code.get() != null) {
                                return "Received code: " + code.get();
                            } else {
                                return "No code parameter found in the URL";
                            }
                        });
                        break;
                    default: {
                        return false;
                    }
                }

                String url = response.body();
                Desktop.getDesktop().browse(URI.create(url));

                wait.waitForVariable();
                Spark.stop();
                apiUrl = serverUrl + "/api/social/verify?key=" + URLEncoder.encode(key) + "&space=" + space + "&code=" + code.get() + (verifier.get() == null ? "" : "&verifier=" + verifier.get());
                client = HttpClient.newHttpClient();
                request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body().equals("success");
            } else {
                Logger.response(response);
                return false;
            }
        } catch (Exception e) {
            Logger.exception(e);
            return false;
        }
    }
}

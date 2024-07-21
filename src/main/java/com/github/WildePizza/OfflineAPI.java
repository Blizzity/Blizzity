package com.github.WildePizza;

import com.github.WildePizza.utils.LoggerUtils;
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

public class OfflineAPI extends API {
    OfflineAPI() {
        super(null);
    }
    @Override
    public List<Map<String, String>> info(String space, String authToken) {
        return info(space, authToken, false);
    }
    @Override
    public List<Map<String, String>> info(String space, String authToken, boolean refresh) {
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> info = new HashMap<>();
        info.put("display_name", "User");
        info.put("avatar", "");
        info.put("username", "username");
        info.put("id", "0");
        info.put("selected", "true");
        result.add(info);
        return result;
    }
    @Override
    public Double<File, String> video(String authToken, String language, int length) {
        return null;
    }
    @Override
    public void youtubePost(String authToken, String video, String title, String description, String privacy_level) {}
    @Override
    public void snapchatPost(String authToken, String video, String title) {}
    @Override
    public void tiktokPost(String authToken, String video, String title, String privacy_level, boolean disable_duet, boolean disable_comment, boolean disable_stitch, int video_cover_timestamp_ms) {}
    @Override
    public boolean admin(String key) {
        return true;
    }
    @Override
    public String usages(String key) {
        return "0";
    }
    @Override
    public String credits(String key) {
        return "0";
    }
    @Override
    public boolean check(String space, String key) {
        return false;
    }
    @Override
    public boolean select(String space, String key, String id) {
        return false;
    }
    @Override
    public Map<String, String> adminCheck(String space, String language, String key) {
        Map<String, String> info = new HashMap<>();
        info.put("display_name", "User");
        info.put("avatar", "");
        info.put("username", "username");
        info.put("id", "0");
        info.put("selected", "true");
        return info;
    }
    @Override
    public boolean admin(String space, String language, String key, String channel) {
        return false;
    }
    @Override
    public boolean connect(String space, String key) {
        return false;
    }
}

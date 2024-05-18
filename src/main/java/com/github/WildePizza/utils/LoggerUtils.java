package com.github.WildePizza.utils;

import com.github.WildePizza.SimpleLogger;

import java.net.http.HttpResponse;

public class LoggerUtils {
    public static void response(SimpleLogger logger, HttpResponse<String> response) {
        logger.note("Response Code: " + response.statusCode());
        logger.note("Response Body: " + response.body());
    }
}

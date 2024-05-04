package com.github.WildePizza;

import java.net.http.HttpResponse;
import java.util.Arrays;

public class Logger {
    public static void exception(Exception exception) {
        System.out.println(
                "Cause:" + exception.getCause().toString()
                + "\nMessage" + exception.getMessage()
                + "\nStacktrace:" + Arrays.toString(exception.getStackTrace())
        );
    }
    public static void response(HttpResponse<String> response) {
        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }
}

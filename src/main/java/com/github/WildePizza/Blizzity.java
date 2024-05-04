package com.github.WildePizza;

public class Blizzity {
    // Replace with the actual server address
    static String serverUrl = "http://localhost:8080";
    public static void main(String[] args) {
        Spark.run();
        API api = new API(serverUrl);
        GUI gui = new GUI(api);
        gui.open();
    }
}

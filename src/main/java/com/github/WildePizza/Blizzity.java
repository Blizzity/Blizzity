package com.github.WildePizza;

public class Blizzity {
    static final boolean offlineMode = true;
    static final boolean debug = false;
    static String serverUrl = "http://localhost:8443";
    public static void main(String[] args) {
        String publicIP = (String) new VariablesBuilder().withGithubFile("https://github.com/Blizzity/BlizzityVariables/blob/main/variables.dat").build().getVariable("publicIP");
        Spark.run();
        API api = offlineMode ? new OfflineAPI() : new API(debug ? serverUrl : serverUrl.replace("localhost", publicIP));
        GUI gui = new GUI(api);
        gui.open();
    }
}

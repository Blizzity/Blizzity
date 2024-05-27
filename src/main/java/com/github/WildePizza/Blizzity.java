package com.github.WildePizza;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

public class Blizzity {
    static final boolean debug = false;
    static String serverUrl = "http://localhost:8443";
    public static void main(String[] args) throws GitAPIException, IOException {
        String publicIP = (String) new VariablesBuilder().withGithubFile("https://github.com/Blizzity/BlizzityVariables/blob/main/variables.dat").build().getVariable("publicIP");
        Spark.run();
        API api = new API(debug ? serverUrl : serverUrl.replace("localhost", publicIP));
        GUI gui = new GUI(api);
        gui.open();
    }
}

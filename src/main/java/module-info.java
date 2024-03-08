module wildepizza.com.github.blizzity {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.net.http;
    requires java.desktop;
    requires javafx.swing;


    opens wildepizza.com.github.blizzity to javafx.fxml;
    exports wildepizza.com.github.blizzity;
}
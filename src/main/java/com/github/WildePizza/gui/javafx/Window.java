package com.github.WildePizza.gui.javafx;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public class Window {
    double sizeMultiplier = 1;
    double height;
    double width;
    public Window() {
        this(300, 200);
    }
    Window(double width, double height) {
        this.width = width;
        this.height = height;
    }
    public void open() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);
        Pane root = new Pane();
        Container title = new Container(width, 40*sizeMultiplier);
        Double[] point = new Double[2];
        title.setOnMousePressed(event -> {
            point[0] = event.getX();
            point[1] = event.getY();
        });
        title.setOnMouseDragged(event -> {
            stage.setX(stage.getX() - (point[0] - event.getX()));
            stage.setY(stage.getY() - (point[1] - event.getY()));
            point[0] = event.getX();
            point[1] = event.getY();
        });
        root.getChildren().add(title);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(19, 19, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.showAndWait();
    }
}

package com.github.WildePizza.gui.javafx;

import javafx.scene.layout.Pane;

public class HoverPane extends Pane {
    boolean hovering = false;
    HoverPane() {
        hoverProperty().addListener((observable, oldValue, newValue) -> {
            hovering = newValue;
        });
    }
}

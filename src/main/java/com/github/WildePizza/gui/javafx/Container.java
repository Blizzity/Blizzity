package com.github.WildePizza.gui.javafx;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Container extends Pane {
    double width;
    double height;
    boolean resizable = false;
    double outlineWidth = 1;
    Rectangle bg1;
    Rectangle bg2;
    Container(double width, double height) {
        this.width = width;
        this.height = height;
        drawContainer();
    }
    private void drawContainer() {
        bg1 = new Rectangle(width, height);
        bg1.setFill(Color.rgb(30,31,34));
        bg2 = new Rectangle(width-outlineWidth*2, height-outlineWidth*2);
        bg2.setLayoutX(outlineWidth);
        bg2.setLayoutY(outlineWidth);
        bg2.setFill(Color.rgb(43,45,48));
        getChildren().addAll(bg1, bg2);
    }
}

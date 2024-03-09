package wildepizza.com.github.blizzity.gui;

import javafx.scene.layout.Region;
import javafx.scene.shape.Shape;

public class RoundedRect extends Region {

    private double width, height, arcWidth, arcHeight;

    public RoundedRect(double width, double height, double arcWidth, double arcHeight) {
        this.width = width;
        this.height = height;
        this.arcWidth = arcWidth;
        this.arcWidth = arcHeight;
    }

   /* @Override
    protected Shape getShape() {
        return new RoundRectangle(width, height, arcWidth, arcHeight);
    }*/
}
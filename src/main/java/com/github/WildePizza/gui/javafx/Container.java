package com.github.WildePizza.gui.javafx;

import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Container extends HoverPane {
    public static final int TOP=1, RIGHT=2, BOTTOM=3, LEFT=4;
    Color outlineColor = Color.rgb(30,31,34);
    Color color = Color.rgb(43,45,48);
    double width;
    double height;
    boolean resizable = false;
    boolean outlineTop = false, outlineBottom = false, outlineRight = false, outlineLeft = false;
    double outlineWidth = 1;
    double outlineHitboxSize = 5;
    Rectangle bg1, bg2, outlineTopHitbox, outlineBottomHitbox, outlineRightHitbox, outlineLeftHitbox;
    double x;
    double y;

    public Container(double width, double height) {
        setWidth(width);
        setHeight(height);
        drawContainer();
    }

    @Override
    public void setHeight(double height) {
        this.height = height;
        super.setHeight(height);
    }

    @Override
    public void setWidth(double width) {
        this.width = width;
        super.setWidth(width);
    }

    public Container setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
        drawContainer();
        return this;
    }

    public Container setColor(Color color) {
        this.color = color;
        drawContainer();
        return this;
    }

    public Container setOutline(int side, boolean outline) {
        switch (side) {
            case TOP:
                if (outline) {
                    outlineTopHitbox = new Rectangle(width, outlineHitboxSize);
                    outlineTopHitbox.setLayoutX(x);
                    outlineTopHitbox.setLayoutY(y - Math.floor(outlineHitboxSize / 2));
                    outlineTopHitbox.setCursor(Cursor.S_RESIZE);
                    outlineTopHitbox.setFill(Color.TRANSPARENT);
                    outlineTopHitbox.setStroke(Color.TRANSPARENT);
                    getChildren().add(outlineTopHitbox);
                } else
                    getChildren().remove(outlineTopHitbox);
                this.outlineTop = outline;
                break;
            case RIGHT:
                this.outlineRight = outline;
                break;
            case BOTTOM:
                this.outlineBottom = outline;
                break;
            case LEFT:
                this.outlineLeft = outline;
                break;
        }
        drawContainer();
        return this;
    }

    public boolean getOutline(int side) {
        switch (side) {
            case TOP:
                return outlineTop;
            case RIGHT:
                return outlineRight;
            case BOTTOM:
                return outlineBottom;
            case LEFT:
                return outlineLeft;
            default:
                return false;
        }
    }

    public Container setX(double x) {
        this.x = x;
        drawContainer();
        return this;
    }

    public double getX() {
        return x;
    }

    public Container setY(double y) {
        this.y = y;
        drawContainer();
        return this;
    }

    public double getY() {
        return y;
    }

    public double getOutlineWidth() {
        return outlineWidth;
    }

    private void drawContainer() {
        int offsetX = 0;
        int offsetY = 0;
        int offsetWidth = 0;
        int offsetHeight = 0;
        super.setLayoutX(x);
        super.setLayoutY(y);
        super.setWidth(width);
        super.setHeight(height);
        bg1 = new Rectangle(width, height);
        bg1.setFill(outlineColor);
        if (outlineTop) {
            offsetY++;
            offsetHeight--;
        }
        if (outlineRight) {
            offsetWidth--;
        }
        if (outlineBottom) {
            offsetHeight--;
        }
        if (outlineLeft) {
            offsetX++;
            offsetWidth--;
        }
        bg2 = new Rectangle(width+offsetWidth, height+offsetHeight);
        bg2.setLayoutX(offsetX);
        bg2.setLayoutY(offsetY);
        bg2.setFill(color);
        if (getChildren().isEmpty())
            getChildren().addAll(bg1, bg2);
        else {
            getChildren().set(0, bg1);
            getChildren().set(1, bg2);
        }
    }
}

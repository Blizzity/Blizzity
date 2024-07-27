package com.github.WildePizza.gui.javafx;

import com.github.WildePizza.GUI;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class ResizablePanel extends JFXPanel {
    protected Rectangle topLeftHitbox, topRightHitbox, topHitbox, rightHitbox, bottomRightHitbox, bottomHitbox, bottomLeftHitbox, leftHitbox;
    protected double tempWidth, tempHeight, height, width;
    double blurRadius = 10 * GUI.sizeMultiplier;
    double outlineRadius = 1 * GUI.sizeMultiplier;
    double hitboxRadius = 5 * GUI.sizeMultiplier;
    JDialog dialog;
    JFXPanel panel;
    ResizablePanel(JDialog dialog) {
        this();
        this.dialog = dialog;
    }
    ResizablePanel(JFXPanel panel) {
        this();
        this.panel = panel;
    }
    ResizablePanel() {
        setOpaque(false);
        setBackground(new java.awt.Color(0, 0, 0, 0));
        Pane root = new Pane();
        root.setStyle("-fx-background-color: transparent;");
        moveResizeHitboxes();
        Scene scene = new Scene(root, width, height);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        setScene(scene);
    }
    private void setSize(double width, double height) {
        setSize(width, height, true);
    }
    private void setSize(double width, double height, boolean refresh) {
        this.width = width;
        this.height = height;
        moveResizeHitboxes();
        if (refresh)
            dialog.setSize((int) (width + blurRadius * 2), (int) (height + blurRadius * 2));
        else {
            if (tempWidth == 0 || Math.abs(tempWidth - width) <= 20) {
                tempWidth = width + 300;
                dialog.setSize((int) tempWidth, (int) tempHeight);
            }
            if (tempHeight == 0 || Math.abs(tempHeight - height) <= 20) {
                tempHeight = height + 300;
                dialog.setSize((int) tempWidth, (int) tempHeight);
            }
        }
    }
    private void makeDraggable(Rectangle rectangle, Cursor cursor) {
        rectangle.setCursor(cursor);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.TRANSPARENT);
        final Point[] clickPoint = new Point[1];
        AtomicReference<Double> initialWidth = new AtomicReference<>(0D);
        AtomicReference<Double> initialHeight = new AtomicReference<>(0D);
        rectangle.setOnMousePressed(event -> {
            initialWidth.set(width);
            initialHeight.set(height);
            clickPoint[0] = new Point((int) event.getX(), (int) event.getY());
            tempWidth = 0;
            tempHeight = 0;
        });
        rectangle.setOnMouseReleased(event -> {
            dialog.setSize((int) (width + blurRadius * 2), (int) (height + blurRadius * 2));
        });
        rectangle.setOnMouseDragged(event -> {
            int yOffset = (int) (clickPoint[0].y - event.getY());
            int xOffset = (int) (clickPoint[0].x - event.getX());
            Platform.runLater(() -> {
                if (cursor.equals(Cursor.NW_RESIZE)) {
                    setSize(width + xOffset, height + yOffset, false);
                    dialog.setLocation(dialog.getLocation().x - xOffset, dialog.getLocation().y - yOffset); // TODO fix glitchy movement
                } else if (cursor.equals(Cursor.N_RESIZE)) {
                    setSize(width, height + yOffset, false);
                    dialog.setLocation(dialog.getLocation().x, dialog.getLocation().y - yOffset);
                } else if (cursor.equals(Cursor.NE_RESIZE)) {
                    setSize(initialWidth.get() - xOffset, height + yOffset, false);
                    dialog.setLocation(dialog.getLocation().x, dialog.getLocation().y - yOffset);
                } else if (cursor.equals(Cursor.E_RESIZE)) {
                    setSize(initialWidth.get() - xOffset, height, false);
                } else if (cursor.equals(Cursor.SE_RESIZE)) {
                    setSize(initialWidth.get() - xOffset, initialHeight.get() - yOffset, false);
                } else if (cursor.equals(Cursor.S_RESIZE)) {
                    setSize(width, initialHeight.get() - yOffset, false);
                } else if (cursor.equals(Cursor.SW_RESIZE)) {
                    setSize(width + xOffset, initialHeight.get() - yOffset, false);
                    dialog.setLocation(dialog.getLocation().x - xOffset, dialog.getLocation().y);
                } else if (cursor.equals(Cursor.W_RESIZE)) {
                    setSize(width + xOffset, height, false);
                    dialog.setLocation(dialog.getLocation().x - xOffset, dialog.getLocation().y);
                }
            });
        });
    }
    public Node[] getResizeHitbox() {
        topLeftHitbox = new Rectangle();
        makeDraggable(topLeftHitbox, Cursor.NW_RESIZE);
        topHitbox = new Rectangle();
        makeDraggable(topHitbox, Cursor.N_RESIZE);
        topRightHitbox = new Rectangle();
        makeDraggable(topRightHitbox, Cursor.NE_RESIZE);
        rightHitbox = new Rectangle();
        makeDraggable(rightHitbox, Cursor.E_RESIZE);
        bottomRightHitbox = new Rectangle();
        makeDraggable(bottomRightHitbox, Cursor.SE_RESIZE);
        bottomHitbox = new Rectangle();
        makeDraggable(bottomHitbox, Cursor.S_RESIZE);
        bottomLeftHitbox = new Rectangle();
        makeDraggable(bottomLeftHitbox, Cursor.SW_RESIZE);
        leftHitbox = new Rectangle();
        makeDraggable(leftHitbox, Cursor.W_RESIZE);
        return new Node[]{topLeftHitbox, topHitbox, topRightHitbox, rightHitbox, bottomRightHitbox, bottomHitbox, bottomLeftHitbox, leftHitbox};
    }
    public void moveResizeHitboxes() {
        topLeftHitbox.setX(blurRadius- hitboxRadius);
        topLeftHitbox.setY(blurRadius- hitboxRadius);
        topLeftHitbox.setWidth(hitboxRadius *2);
        topLeftHitbox.setHeight(hitboxRadius *2);
        topHitbox.setX(blurRadius+ hitboxRadius);
        topHitbox.setY(blurRadius- hitboxRadius);
        topHitbox.setWidth(width- hitboxRadius *2);
        topHitbox.setHeight(hitboxRadius *2);
        topRightHitbox.setX(width+blurRadius- hitboxRadius);
        topRightHitbox.setY(blurRadius- hitboxRadius);
        topRightHitbox.setWidth(hitboxRadius *2);
        topRightHitbox.setHeight(hitboxRadius *2);
        rightHitbox.setX(width+blurRadius- hitboxRadius);
        rightHitbox.setY(blurRadius+ hitboxRadius);
        rightHitbox.setWidth(hitboxRadius *2);
        rightHitbox.setHeight(height- hitboxRadius *2);
        bottomRightHitbox.setX(width+blurRadius- hitboxRadius);
        bottomRightHitbox.setY(height+blurRadius- hitboxRadius);
        bottomRightHitbox.setWidth(hitboxRadius *2);
        bottomRightHitbox.setHeight(hitboxRadius *2);
        bottomHitbox.setX(blurRadius+ hitboxRadius);
        bottomHitbox.setY(height+blurRadius- hitboxRadius);
        bottomHitbox.setWidth(width- hitboxRadius *2);
        bottomHitbox.setHeight(hitboxRadius *2);
        bottomLeftHitbox.setX(blurRadius- hitboxRadius);
        bottomLeftHitbox.setY(height+blurRadius- hitboxRadius);
        bottomLeftHitbox.setWidth(hitboxRadius *2);
        bottomLeftHitbox.setHeight(hitboxRadius *2);
        leftHitbox.setX(blurRadius- hitboxRadius);
        leftHitbox.setY(blurRadius+ hitboxRadius);
        leftHitbox.setWidth(hitboxRadius *2);
        leftHitbox.setHeight(height- hitboxRadius *2);
    }
}

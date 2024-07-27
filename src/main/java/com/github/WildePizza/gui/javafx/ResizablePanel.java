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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ResizablePanel extends MappedJFXPanel {
    protected double tempWidth, tempHeight, height, width;
    double offsetRadius;
    double outlineRadius = 1 * GUI.sizeMultiplier;
    double hitboxRadius = 5 * GUI.sizeMultiplier;
    public List<Interface> resizeEvent = new ArrayList<>();
    JDialog dialog;
    public ResizablePanel(double width, double height, double offsetRadius, JDialog dialog) {
        this(width, height, offsetRadius);
        this.dialog = dialog;
    }
    public ResizablePanel(double width, double height) {
        this(width, height, 0);
    }
    public ResizablePanel() {
        this(0, 0, 0);
    }
    public ResizablePanel(double width, double height, double offsetRadius) {
        super();
        Platform.runLater(() -> {
            this.width = width;
            this.height = height;
            this.offsetRadius = offsetRadius;
            setOpaque(false);
            setBackground(new java.awt.Color(0, 0, 0, 0));
            Pane root = new Pane();
            root.setStyle("-fx-background-color: transparent;");
            getMappedParent().add("cursor.nw_resize", newDraggable(Cursor.NW_RESIZE));
            getMappedParent().add("cursor.n_resize", newDraggable(Cursor.N_RESIZE));
            getMappedParent().add("cursor.ne_resize", newDraggable(Cursor.NE_RESIZE));
            getMappedParent().add("cursor.e_resize", newDraggable(Cursor.E_RESIZE));
            getMappedParent().add("cursor.se_resize", newDraggable(Cursor.SE_RESIZE));
            getMappedParent().add("cursor.s_resize", newDraggable(Cursor.S_RESIZE));
            getMappedParent().add("cursor.sw_resize", newDraggable(Cursor.SW_RESIZE));
            getMappedParent().add("cursor.w_resize", newDraggable(Cursor.W_RESIZE));
            moveResizeHitboxes();
            Scene scene = new Scene(root, width, height);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            setScene(scene);
        });
    }
    public void setSize(double width, double height) {
        setSize(width, height, true);
    }
    public void setSize(double width, double height, boolean refresh) {
        this.width = width;
        this.height = height;
        moveResizeHitboxes();
        callResize(width, height);
        if (dialog != null) {
            if (refresh)
                dialog.setSize((int) (width + offsetRadius * 2), (int) (height + offsetRadius * 2));
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
        } else {
            setPreferredSize(new Dimension((int) (width + offsetRadius * 2), (int) (height + offsetRadius * 2)));
        }
    }
    private Rectangle newDraggable(Cursor cursor) {
        Rectangle rectangle = new Rectangle();
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
        rectangle.setOnMouseReleased(event -> dialog.setSize((int) (width + offsetRadius * 2), (int) (height + offsetRadius * 2)));
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
        return rectangle;
    }
    public void moveResizeHitboxes() {
        Rectangle topLeftHitbox = (Rectangle) getMappedParent().get("cursor.nw_resize");
        topLeftHitbox.setX(offsetRadius - hitboxRadius);
        topLeftHitbox.setY(offsetRadius - hitboxRadius);
        topLeftHitbox.setWidth(hitboxRadius *2);
        topLeftHitbox.setHeight(hitboxRadius *2);
        Rectangle topHitbox = (Rectangle) getMappedParent().get("cursor.n_resize");
        topHitbox.setX(offsetRadius + hitboxRadius);
        topHitbox.setY(offsetRadius - hitboxRadius);
        topHitbox.setWidth(width- hitboxRadius *2);
        topHitbox.setHeight(hitboxRadius *2);
        Rectangle topRightHitbox = (Rectangle) getMappedParent().get("cursor.ne_resize");
        topRightHitbox.setX(width+ offsetRadius - hitboxRadius);
        topRightHitbox.setY(offsetRadius - hitboxRadius);
        topRightHitbox.setWidth(hitboxRadius *2);
        topRightHitbox.setHeight(hitboxRadius *2);
        Rectangle rightHitbox = (Rectangle) getMappedParent().get("cursor.e_resize");
        rightHitbox.setX(width+ offsetRadius - hitboxRadius);
        rightHitbox.setY(offsetRadius + hitboxRadius);
        rightHitbox.setWidth(hitboxRadius *2);
        rightHitbox.setHeight(height- hitboxRadius *2);
        Rectangle bottomRightHitbox = (Rectangle) getMappedParent().get("cursor.se_resize");
        bottomRightHitbox.setX(width+ offsetRadius - hitboxRadius);
        bottomRightHitbox.setY(height+ offsetRadius - hitboxRadius);
        bottomRightHitbox.setWidth(hitboxRadius *2);
        bottomRightHitbox.setHeight(hitboxRadius *2);
        Rectangle bottomHitbox = (Rectangle) getMappedParent().get("cursor.s_resize");
        bottomHitbox.setX(offsetRadius + hitboxRadius);
        bottomHitbox.setY(height+ offsetRadius - hitboxRadius);
        bottomHitbox.setWidth(width- hitboxRadius *2);
        bottomHitbox.setHeight(hitboxRadius *2);
        Rectangle bottomLeftHitbox = (Rectangle) getMappedParent().get("cursor.sw_resize");
        bottomLeftHitbox.setX(offsetRadius - hitboxRadius);
        bottomLeftHitbox.setY(height+ offsetRadius - hitboxRadius);
        bottomLeftHitbox.setWidth(hitboxRadius *2);
        bottomLeftHitbox.setHeight(hitboxRadius *2);
        Rectangle leftHitbox = (Rectangle) getMappedParent().get("cursor.w_resize");
        leftHitbox.setX(offsetRadius - hitboxRadius);
        leftHitbox.setY(offsetRadius + hitboxRadius);
        leftHitbox.setWidth(hitboxRadius *2);
        leftHitbox.setHeight(height- hitboxRadius *2);
    }
    public void callResize(double width, double height) {
        for (Interface action : resizeEvent) {
            action.execute(width, height);
        }
    }
    @FunctionalInterface
    public interface Interface {
        void execute(double width, double height);
    }
}

package com.github.WildePizza.gui.javafx;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

import javax.swing.*;
import java.awt.*;

public class Window {
    double sizeMultiplier = 1;
    double blurRadius = 10;
    double outlineRadius = 5;
    double height;
    double width;
    Rectangle rectangle, topLeftHitbox, topRightHitbox, topHitbox, rightHitbox, bottomRightHitbox, bottomHitbox, bottomLeftHitbox, leftHitbox;
    JDialog dialog;
    Container title, main;
    JFXPanel fxPanel;
    SimpleSVGButton jfxCloseButton;
    public Window() {
        this(300, 200);
    }
    Window(double width, double height) {
        this.width = width;
        this.height = height;
    }
    public void open(JFrame frame) {
        SwingUtilities.invokeLater(() -> {
            dialog = new JDialog(frame, "", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setUndecorated(true);
            dialog.setSize((int) (width+blurRadius*2), (int) (height+blurRadius*2));
            dialog.setLocationRelativeTo(frame);
            dialog.setBackground(new java.awt.Color(0, 0, 0, 0));
            dialog.getContentPane().setBackground(new java.awt.Color(0, 0, 0, 0));
            dialog.getRootPane().setOpaque(false);
            dialog.getRootPane().setBackground(new java.awt.Color(0, 0, 0, 0));
            fxPanel = new JFXPanel();
            fxPanel.setOpaque(false);
            fxPanel.setBackground(new java.awt.Color(0, 0, 0, 0));
            dialog.add(fxPanel);
            Pane root = new Pane();
            root.setStyle("-fx-background-color: transparent;");
            root.getChildren().addAll(getContent(dialog));
            root.getChildren().addAll(getResizeHitbox());
            moveResizeHitboxes();
            Scene scene = new Scene(root, width, height);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            fxPanel.setScene(scene);
            dialog.setVisible(true);
        });
    }
    public Node[] getContent(JDialog dialog) {
        rectangle = new Rectangle(blurRadius, blurRadius, width, height);
        rectangle.setFill(Color.BLACK);
        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(blurRadius);
        rectangle.setEffect(blur);
        title = new Container(width, 40*sizeMultiplier)
                .setY(blurRadius)
                .setX(blurRadius)
                .setColor(Color.rgb(60,63,65));
        SVGPath path1 = new SVGPath();
        path1.setContent("M7 17L16.8995 7.10051");
        path1.setStrokeWidth(0.7);
        path1.setStroke(javafx.scene.paint.Color.WHITE);
        SVGPath path2 = new SVGPath();
        path2.setContent("M7 7.00001L16.8995 16.8995");
        path2.setStrokeWidth(0.7);
        path2.setStroke(javafx.scene.paint.Color.WHITE);
        Group svgGroup = new Group(path1, path2);
        svgGroup.setScaleX(1*sizeMultiplier);
        svgGroup.setScaleY(1*sizeMultiplier);
        svgGroup.setLayoutX(30*sizeMultiplier/2-svgGroup.getLayoutBounds().getCenterX());
        svgGroup.setLayoutY(40*sizeMultiplier/2-svgGroup.getLayoutBounds().getCenterY());
        jfxCloseButton = new SimpleSVGButton(svgGroup, 30*sizeMultiplier, 40*sizeMultiplier);
        jfxCloseButton.setBackgroundColor(javafx.scene.paint.Color.rgb(201,79,79));
        jfxCloseButton.setLayoutX((width - 30)*sizeMultiplier);
        jfxCloseButton.setOnAction(actionEvent -> dialog.dispose());
        title.getChildren().add(jfxCloseButton);
        final Point[] clickPoint = new Point[1];
        title.setOnMousePressed(event -> clickPoint[0] = new Point((int) event.getX(), (int) event.getY()));
        title.setOnMouseDragged(event -> {
            int xOffset = (int) (dialog.getLocation().x - clickPoint[0].x + event.getX());
            int yOffset = (int) (dialog.getLocation().y - clickPoint[0].y + event.getY());
            dialog.setLocation(xOffset, yOffset);
        });
        main = new Container(width, height-40*sizeMultiplier).setY(40*sizeMultiplier+blurRadius).setX(blurRadius);
        return new Node[]{rectangle, title, main};
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
        topLeftHitbox.setX(blurRadius-outlineRadius);
        topLeftHitbox.setY(blurRadius-outlineRadius);
        topLeftHitbox.setWidth(outlineRadius*2);
        topLeftHitbox.setHeight(outlineRadius*2);
        topHitbox.setX(blurRadius+outlineRadius);
        topHitbox.setY(blurRadius-outlineRadius);
        topHitbox.setWidth(width-outlineRadius*2);
        topHitbox.setHeight(outlineRadius*2);
        topRightHitbox.setX(width+blurRadius-outlineRadius);
        topRightHitbox.setY(blurRadius-outlineRadius);
        topRightHitbox.setWidth(outlineRadius*2);
        topRightHitbox.setHeight(outlineRadius*2);
        rightHitbox.setX(width+blurRadius-outlineRadius);
        rightHitbox.setY(blurRadius+outlineRadius);
        rightHitbox.setWidth(outlineRadius*2);
        rightHitbox.setHeight(height-outlineRadius*2);
        bottomRightHitbox.setX(width+blurRadius-outlineRadius);
        bottomRightHitbox.setY(height+blurRadius-outlineRadius);
        bottomRightHitbox.setWidth(outlineRadius*2);
        bottomRightHitbox.setHeight(outlineRadius*2);
        bottomHitbox.setX(blurRadius+outlineRadius);
        bottomHitbox.setY(height+blurRadius-outlineRadius);
        bottomHitbox.setWidth(width-outlineRadius*2);
        bottomHitbox.setHeight(outlineRadius*2);
        bottomLeftHitbox.setX(blurRadius-outlineRadius);
        bottomLeftHitbox.setY(height+blurRadius-outlineRadius);
        bottomLeftHitbox.setWidth(outlineRadius*2);
        bottomLeftHitbox.setHeight(outlineRadius*2);
        leftHitbox.setX(blurRadius-outlineRadius);
        leftHitbox.setY(blurRadius+outlineRadius);
        leftHitbox.setWidth(outlineRadius*2);
        leftHitbox.setHeight(height-outlineRadius*2);
    }
    private void makeDraggable(Rectangle rectangle, Cursor cursor) {
        rectangle.setCursor(cursor);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.TRANSPARENT);
        final Point[] clickPoint = new Point[1];
        rectangle.setOnMousePressed(event -> clickPoint[0] = new Point((int) event.getX(), (int) event.getY()));
        rectangle.setOnMouseReleased(event -> {
            dialog.setSize((int) (width + blurRadius * 2), (int) (height + blurRadius * 2));
        });
        rectangle.setOnMouseDragged(event -> {
            int yOffset = (int) (clickPoint[0].y - event.getY());
            int xOffset = (int) (clickPoint[0].x - event.getX());
            if (cursor.equals(Cursor.NW_RESIZE)) {
                Platform.runLater(() -> {
                    setSize(width + xOffset, height + yOffset, false);
                    dialog.setLocation(dialog.getLocation().x - xOffset, dialog.getLocation().y - yOffset);
                });
            }
        });
    }
    protected double tempWidth, tempHeight;
    private void setSize(double width, double height) {
        setSize(width, height, true);
    }
    private void setSize(double width, double height, boolean refresh) {
        this.width = width;
        this.height = height;
        rectangle.setHeight(height);
        rectangle.setWidth(width);
        rectangle.toBack();
        main.setCurrentHeight(height - 40 * sizeMultiplier, true);
        main.setCurrentWidth(width, true);
        title.setCurrentWidth(width, true);
        jfxCloseButton.setLayoutX((width - 30)*sizeMultiplier);
        moveResizeHitboxes();
        if (refresh)
            dialog.setSize((int) (width + blurRadius * 2), (int) (height + blurRadius * 2));
        else {
            if (tempWidth == 0 || Math.abs(tempWidth-width) <= 50) {
                tempWidth = width + 300;
                dialog.setSize((int) tempWidth, (int) tempHeight);
            }
            if (tempHeight == 0 || Math.abs(tempHeight-height) <= 50) {
                tempHeight = height + 300;
                dialog.setSize((int) tempWidth, (int) tempHeight);
            }
        }
    }
}

package com.github.WildePizza.gui.javafx;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Window {
    double sizeMultiplier = 1;
    double blurRadius = 10 * sizeMultiplier;
    double outlineRadius = 1;
    double hitboxRadius = 5;
    double height;
    double width;
    Rectangle rectangle, topLeftHitbox, topRightHitbox, topHitbox, rightHitbox, bottomRightHitbox, bottomHitbox, bottomLeftHitbox, leftHitbox, outline, divider;
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
        outline = new Rectangle(blurRadius-outlineRadius, blurRadius-outlineRadius, width+outlineRadius*2, height+outlineRadius*2);
        divider = new Rectangle(blurRadius, blurRadius+40*sizeMultiplier, width, 1);
        divider.setFill(Color.rgb(60,63,65));
        outline.setFill(Color.rgb(60,63,65));
        rectangle = new Rectangle(blurRadius, blurRadius, width, height);
        rectangle.setFill(Color.BLACK);
        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(blurRadius);
        rectangle.setEffect(blur);
        title = new Container(width, 40*sizeMultiplier)
                .setY(blurRadius)
                .setX(blurRadius)
                .setColor(Color.rgb(43, 45, 48));
        dialog.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                Platform.runLater(() -> {
                    title.setColor(Color.rgb(43, 45, 48));
                    ((Pane) (fxPanel.getScene().getRoot())).getChildren().remove(outline);
                });
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                Platform.runLater(() -> {
                    title.setColor(Color.rgb(60,63,65));
                    ((Pane) (fxPanel.getScene().getRoot())).getChildren().add(outline);
                    outline.toBack();
                    rectangle.toBack();
                });
            }
        });
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
        jfxCloseButton.setOnAction(actionEvent -> {
            dialog.dispose();
            callInterface();
        });
        title.getChildren().add(jfxCloseButton);
        final Point[] clickPoint = new Point[1];
        title.setOnMousePressed(event -> clickPoint[0] = new Point((int) event.getX(), (int) event.getY()));
        title.setOnMouseDragged(event -> {
            int xOffset = (int) (dialog.getLocation().x - clickPoint[0].x + event.getX());
            int yOffset = (int) (dialog.getLocation().y - clickPoint[0].y + event.getY());
            dialog.setLocation(xOffset, yOffset);
        });
        main = new Container(width, height-41*sizeMultiplier).setY(41*sizeMultiplier+blurRadius).setX(blurRadius);
        return new Node[] {rectangle, title, main, divider};
    }
    public ObservableList<Node> getChildren() {
        return main.getChildren();
    }
    public Container getContainer() {
        return main;
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
    public List<Interface> actions = new ArrayList<>();
    public void callInterface() {
        for (Interface action : actions) {
            action.execute();
        }
    }

    @FunctionalInterface
    public interface Interface {
        void execute();
    }
}

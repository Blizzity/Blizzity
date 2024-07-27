package com.github.WildePizza.gui.javafx;

import com.github.WildePizza.GUI;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
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

public class Window {
    double blurRadius = 10 * GUI.sizeMultiplier;
    double outlineRadius = 1;
    double hitboxRadius = 5;
    double height;
    double width;
    Rectangle rectangle, topLeftHitbox, topRightHitbox, topHitbox, rightHitbox, bottomRightHitbox, bottomHitbox, bottomLeftHitbox, leftHitbox, outline, divider;
    JDialog dialog;
    Container title, main;
    ResizablePanel fxPanel;
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
            fxPanel = new ResizablePanel(width, height, blurRadius, dialog);
            fxPanel.resizeEvent.add((width, height) -> {
                divider.setWidth(width);
                outline.setWidth(width+outlineRadius*2);
                outline.setHeight(height+outlineRadius*2);
                rectangle.setHeight(height);
                rectangle.setWidth(width);
                rectangle.toBack();
                main.setCurrentHeight(height - 40 * GUI.sizeMultiplier, true);
                main.setCurrentWidth(width, true);
                title.setCurrentWidth(width, true);
                jfxCloseButton.setLayoutX((width - 30)*GUI.sizeMultiplier);
            });
            Platform.runLater(() -> ((Pane) (fxPanel.getScene().getRoot())).getChildren().addAll(getContent(dialog)));
            dialog.add(fxPanel);
            dialog.setVisible(true);
        });
    }
    public Node[] getContent(JDialog dialog) {
        outline = new Rectangle(blurRadius-outlineRadius, blurRadius-outlineRadius, width+outlineRadius*2, height+outlineRadius*2);
        outline.setFill(Color.rgb(60,63,65));
        divider = new Rectangle(blurRadius, blurRadius+40*GUI.sizeMultiplier, width, 1);
        divider.setFill(Color.rgb(60,63,65));
        rectangle = new Rectangle(blurRadius, blurRadius, width, height);
        rectangle.setFill(Color.BLACK);
        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(blurRadius);
        rectangle.setEffect(blur);
        title = new Container(width, 40*GUI.sizeMultiplier)
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
        svgGroup.setScaleX(1*GUI.sizeMultiplier);
        svgGroup.setScaleY(1*GUI.sizeMultiplier);
        svgGroup.setLayoutX(30*GUI.sizeMultiplier/2-svgGroup.getLayoutBounds().getCenterX());
        svgGroup.setLayoutY(40*GUI.sizeMultiplier/2-svgGroup.getLayoutBounds().getCenterY());
        jfxCloseButton = new SimpleSVGButton(svgGroup, 30*GUI.sizeMultiplier, 40*GUI.sizeMultiplier);
        jfxCloseButton.setBackgroundColor(javafx.scene.paint.Color.rgb(201,79,79));
        jfxCloseButton.setLayoutX((width - 30)*GUI.sizeMultiplier);
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
        main = new Container(width, height-41*GUI.sizeMultiplier).setY(41*GUI.sizeMultiplier+blurRadius).setX(blurRadius);
        return new Node[] {rectangle, title, main, divider};
    }
    public ObservableList<Node> getChildren() {
        return main.getChildren();
    }
    public Container getContainer() {
        return main;
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

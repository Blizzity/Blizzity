package wildepizza.com.github.blizzity.gui.javafx;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import wildepizza.com.github.blizzity.gui.listeners.ScreenListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SimpleButton extends Pane {
    double height;
    double width;
    private Color strokeColor = Color.WHITE;
    private Color selectedStrokeColor = Color.BLUE;
    private Color textFillColor = Color.BLACK;
    private boolean selected;
    private Rectangle rectangle;
    public SimpleButton(String text) {
        initializeComboBox(text, 100, 30);
    }
    public SimpleButton() {
        initializeComboBox("", 100, 30);
    }
    public SimpleButton(String text, int width, int height) {
        initializeComboBox(text, width, height);
    }
    private final ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
        @Override protected void invalidated() {
            setEventHandler(ActionEvent.ACTION, get());
        }

        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "onAction";
        }
    };
    private void initializeComboBox(String text, int width, int height) {
        this.height = height;
        this.width = width;
        rectangle = new Rectangle(width, height);
        rectangle.setFill(Color.WHITE);
        rectangle.setArcWidth(10);
        rectangle.setArcHeight(10);
        rectangle.setStroke(strokeColor);
        rectangle.setStrokeWidth(1);

        Label label = new Label(text);
        label.setTextFill(Color.BLACK);
        label.setLayoutY(7);
        label.setLayoutX(10);

        getChildren().addAll(rectangle, label);
        ScreenListener.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!isHover()) {
                    Platform.runLater(() -> {
                        hide();
                    });
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        setOnMouseClicked(event -> {
            if (selected) {
                hide();
            } else {
                rectangle.setStroke(selectedStrokeColor);
                selected = true;
            }
            onAction.getValue().handle(new ActionEvent());
        });
    }
    public void hide() {
        rectangle.setStroke(strokeColor);
        selected = false;
    }
    public final void setOnAction(EventHandler<ActionEvent> var1) {
        this.onAction.set(var1);
    }
}

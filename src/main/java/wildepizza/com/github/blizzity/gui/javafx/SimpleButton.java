package wildepizza.com.github.blizzity.gui.javafx;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import wildepizza.com.github.blizzity.gui.listeners.ScreenListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@SuppressWarnings("unused")
public class SimpleButton extends Pane {
    double height;
    double width;
    private Color strokeColor = Color.WHITE;
    private Color selectedStrokeColor = Color.BLUE;
    private Color textFillColor = Color.BLACK;
    private boolean selected;
    private Rectangle rectangle;
    private Label label;
    public SimpleButton(String text) {
        initializeComboBox(text, 100, 30);
    }
    public SimpleButton() {
        initializeComboBox("", 100, 30);
    }
    public SimpleButton(String text, int width, int height) {
        initializeComboBox(text, width, height);
    }
    public void setTextFill(Color color) {
        label.setTextFill(color);
        textFillColor = color;
    }
    public void setBackgroundColor(Color color) {
        rectangle.setFill(color);
    }
    public void setStrokeColor(Color color) {
        strokeColor = color;
        rectangle.setStroke(color);
    }
    public void setSelectedStrokeColor(Color color) {
        selectedStrokeColor = color;
    }
    private final ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<>() {
        @Override
        protected void invalidated() {
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

        label = new Label(text);
        label.setTextFill(textFillColor);
        Text font = new Text(text);
        font.setFont(label.getFont());
        label.setLayoutY((height - font.getBoundsInLocal().getHeight()) /2);
        label.setLayoutX((width - font.getBoundsInLocal().getWidth()) /2);

        getChildren().addAll(rectangle, label);
        ScreenListener.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!isHover()) {
                    Platform.runLater(() -> hide());
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
                rectangle.setStrokeWidth(3);
                rectangle.setStroke(selectedStrokeColor);
                selected = true;
            }
            onAction.getValue().handle(new ActionEvent());
        });
    }
    public void hide() {
        rectangle.setStroke(strokeColor);
        rectangle.setStrokeWidth(1);
        selected = false;
    }
    public final void setOnAction(EventHandler<ActionEvent> var1) {
        this.onAction.set(var1);
    }
}

package wildepizza.com.github.blizzity.gui.javafx;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import wildepizza.com.github.blizzity.gui.listeners.ScreenListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@SuppressWarnings("unused")
public class SVGButton extends Pane {
    double height;
    double width;
    private Color svgColor = Color.WHITE;
    private Color selectedSvgColor = Color.BLUE;
    private Color textFillColor = Color.BLACK;
    private boolean selected;
    private Rectangle rectangle;
    public void setBackgroundColor(Color color) {
        rectangle.setFill(color);
    }
    public void setSVGColor(Color color) {
        svgColor = color;
    }
    public void setSelectedSVGColor(Color color) {
        selectedSvgColor = color;
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
    private void initializeComboBox(Group group, int width, int height) {
        this.height = height;
        this.width = width;
        Rectangle generateSectionButton = new Rectangle(0, 0, 60, 40);
        generateSectionButton.setFill(javafx.scene.paint.Color.TRANSPARENT);

        rectangle = new Rectangle(width, height);
        rectangle.setFill(Color.WHITE);
        rectangle.setArcWidth(10);
        rectangle.setArcHeight(10);

        getChildren().addAll(generateSectionButton, group);
        setOnMouseEntered(event -> {

        });
        setOnMouseExited(event -> {

        });
        setOnMouseClicked(event -> {
            changeSvgColor(group, selectedSvgColor);
            onAction.getValue().handle(new ActionEvent());
        });
    }
    public void changeSvgColor(Group group, Color color) {
        if (((SVGPath) group.getChildren().get(0)).getFill().equals(Color.TRANSPARENT))
            for (Node node : group.getChildren()) {
                ((SVGPath) node).setStroke(color);
            }
        else
            ((SVGPath) group.getChildren().get(0)).setFill(javafx.scene.paint.Color.WHITE);
    }
    public final void setOnAction(EventHandler<ActionEvent> var1) {
        this.onAction.set(var1);
    }
}

package wildepizza.com.github.blizzity.gui.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

@SuppressWarnings("unused")
public class SVGButton extends Pane {
    double height;
    double width;
    public boolean selected = false;
    private final Rectangle rectangle;
    private final Group group;
    public void setBackgroundColor(Color color) {
        rectangle.setFill(color);
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
    public SVGButton(Group group, int width, int height) {
        this.group = group;
        this.height = height;
        this.width = width;
        Rectangle generateSectionButton = new Rectangle(0, 0, width, height);
        generateSectionButton.setFill(javafx.scene.paint.Color.TRANSPARENT);

        rectangle = new Rectangle(width-10, height-10);
        rectangle.setFill(Color.WHITE);
        rectangle.setArcWidth(10);
        rectangle.setArcHeight(10);
        rectangle.setLayoutX(5);
        rectangle.setLayoutY(5);

        getChildren().addAll(generateSectionButton, group);
        setOnMouseEntered(event -> {
            if (!selected) {
                getChildren().add(rectangle);
                group.toFront();
            }
        });
        setOnMouseExited(event -> {
            if (!selected) {
                getChildren().remove(rectangle);
                group.toFront();
            }
        });
        setOnMouseClicked(event -> {
            onAction.getValue().handle(new ActionEvent());
            selected = true;
        });
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            if (!getChildren().contains(rectangle)) {
                getChildren().add(rectangle);
                group.toFront();
            }
        } else {
            getChildren().remove(rectangle);
        }

    }

    public final void setOnAction(EventHandler<ActionEvent> var1) {
        this.onAction.set(var1);
    }
}

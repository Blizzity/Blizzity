package com.github.WildePizza.gui.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.MappedNode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

@SuppressWarnings("unused")
public class SimpleSVGButton extends Pane {
    double height;
    double width;
    private final Rectangle rectangle;
    private final MappedNode group;
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
    public SimpleSVGButton(MappedNode group, double width, double height) {
        this.group = group;
        this.height = height;
        this.width = width;
        Rectangle hitbox = new Rectangle(0, 0, width, height);
        hitbox.setFill(javafx.scene.paint.Color.TRANSPARENT);
        double multiplier = Math.min(height, width)/40;
        rectangle = new Rectangle(width, height);
        rectangle.setFill(Color.WHITE);
        rectangle.setLayoutX(0);
        rectangle.setLayoutY(0);
        getChildren().addAll(group, hitbox);
        hitbox.setOnMouseEntered(event -> {
            if (!getChildren().contains(rectangle)) {
                getChildren().add(rectangle);
                group.toFront();
                hitbox.toFront();
            }
        });
        hitbox.setOnMouseExited(event -> {
            if (getChildren().contains(rectangle) && (!group.isHover() || !hitbox.isHover() || !rectangle.isHover())) {
                getChildren().remove(rectangle);
                group.toFront();
                hitbox.toFront();
            }

        });
        hitbox.setOnMouseClicked(event -> onAction.getValue().handle(new ActionEvent()));
    }

    public final void setOnAction(EventHandler<ActionEvent> var1) {
        this.onAction.set(var1);
    }
}

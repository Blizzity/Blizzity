package com.github.WildePizza.gui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Container extends HoverPane {
    public static final int TOP=1, TOP_RIGHT=2, RIGHT=3, BOTTOM_RIGHT=4, BOTTOM=5, BOTTOM_LEFT=6, LEFT=7, TOP_LEFT=8;
    Color outlineColor = Color.rgb(30,31,34);
    Color color = Color.rgb(43,45,48);
    double width;
    double height;
    Pane children = new Pane();
    public boolean resizable = false;
    boolean outlineTop = false, outlineBottom = false, outlineRight = false, outlineLeft = false;
    double outlineWidth = 1;
    double outlineHitboxSize = 5;
    public Rectangle bg1, bg2, outlineTopHitbox, outlineRightHitbox;
    double x;
    double y;

    public Container setResizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    public Container(double width, double height) {
        setMaxWidth(width);
        setMinWidth(width);
        setCurrentWidth(width);
        setMaxHeight(height);
        setMinHeight(height);
        setCurrentHeight(height);
        drawContainer();
    }

    public boolean setCurrentHeight(double height) {
        if ((height >= getMinHeight() || getMinHeight() == -1) && ((height <= getMaxHeight()) || getMaxHeight() == -1)) {
            this.height = height;
            super.setHeight(height);
            drawContainer();
            return true;
        }
        return false;
    }

    public double getCurrentHeight() {
        return height;
    }

    public double getCurrentWidth() {
        return width;
    }

    public boolean setCurrentWidth(double width) {
        if ((width >= getMinWidth() || getMinWidth() == -1) && ((width <= getMaxWidth()) || getMaxWidth() == -1)) {
            this.width = width;
            super.setWidth(width);
            drawContainer();
            return true;
        }
        return false;
    }

    public Container setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
        drawContainer();
        return this;
    }

    public Container setColor(Color color) {
        this.color = color;
        drawContainer();
        return this;
    }
    public List<Container> getRelatedTreeY(MappedParent children) {
        return getRelatedTreeY(children, false, new ArrayList<>());
    }
    public List<Container> getRelatedTreeY(MappedParent children, boolean inverted, List<Container> containers) {
        containers.add(this);
        children.getChildrenMap().forEach((name, child) -> {
            if (child instanceof Container) {
                if (hasRelatedY((Container) child, inverted)) {
                    if (!containers.contains(child)) {
                        ((Container) child).getRelatedTreeY(children, !inverted, containers).forEach(c -> {
                            if (!containers.contains(c)) {
                                containers.add(c);
                            }
                        });
                    }
                }
            }
        });
        return containers;
    }
    public List<Container> getRelatedTreeX(MappedParent children) {
        return getRelatedTreeX(children, false, new ArrayList<>());
    }
    public List<Container> getRelatedTreeX(MappedParent children, boolean inverted, List<Container> containers) {
        containers.add(this);
        children.getChildrenMap().forEach((name, child) -> {
            if (child instanceof Container) {
                if (hasRelatedX((Container) child, inverted)) {
                    if (!containers.contains(child)) {
                        ((Container) child).getRelatedTreeX(children, !inverted, containers).forEach(c -> {
                            if (!containers.contains(c)) {
                                containers.add(c);
                            }
                        });
                    }
                }
            }
        });
        return containers;
    }
    public boolean isBetween(double key1, double key2, double value) {
        return key1 <= value && key2 >= value;
    }
    public boolean hasRelatedY(Container container) {
        return hasRelatedY(container, false);
    }
    public boolean hasRelatedY(Container container, boolean inverted) {
        if (getY() + (inverted ? getCurrentHeight() : 0) == container.getY() + (inverted ? 0 : container.getCurrentHeight())) {
            return isBetween(getX(), getX() + getCurrentWidth(), container.getX()) ||
                    isBetween(getX(), getX() + getCurrentWidth(), container.getX() + container.getCurrentWidth()) ||
                    isBetween(container.getX(), container.getX() + container.getCurrentWidth(), getX()) ||
                    isBetween(container.getX(), container.getX() + container.getCurrentWidth(), getX() + getCurrentWidth());
        }
        return false;
    }
    public boolean hasRelatedX(Container container) {
        return hasRelatedX(container, false);
    }
    public boolean hasRelatedX(Container container, boolean inverted) {
        if (getX() + (inverted ? 0 : getCurrentWidth()) == container.getX() + (inverted ? container.getCurrentWidth() : 0)) {
            return isBetween(getY(), getY() + getCurrentHeight(), container.getY()) ||
                    isBetween(getY(), getY() + getCurrentHeight(), container.getY() + container.getCurrentHeight()) ||
                    isBetween(container.getY(), container.getY() + container.getCurrentHeight(), getY()) ||
                    isBetween(container.getY(), container.getY() + container.getCurrentHeight(), getY() + getCurrentHeight());
        }
        return false;
    }
    public Container setOutline(int side, boolean outline, String name, MappedParent children, boolean resizable) {
        switch (side) {
            case TOP:
                if (resizable && this.resizable) {
                    if (outline) {
                        if (children.get(name + ".hitbox.top") == null) {
                            outlineTopHitbox = new Rectangle(width, outlineHitboxSize);
                            outlineTopHitbox.setLayoutX(x);
                            outlineTopHitbox.setLayoutY(y - Math.floor(outlineHitboxSize / 2));
                            outlineTopHitbox.setCursor(Cursor.N_RESIZE);
                            outlineTopHitbox.setFill(Color.TRANSPARENT);
                            outlineTopHitbox.setStroke(Color.TRANSPARENT);
                            final Point[] clickPoint = new Point[1];
                            outlineTopHitbox.setOnMousePressed(event -> clickPoint[0] = new Point((int) event.getX(), (int) event.getY()));
                            outlineTopHitbox.setOnMouseDragged(event -> {
                                int yOffset = (int) (clickPoint[0].y - event.getY());
                                clickPoint[0] = new Point((int) event.getX(), (int) event.getY());
                                outlineTopHitbox.setY(outlineTopHitbox.getY() - yOffset);
                                List<Container> relatedChildren = getRelatedTreeY(children);
                                int stop = -1;
                                int index = 0;
                                for (Container child : relatedChildren) {
                                    if (child == this) {
                                        if (!child.setCurrentHeight(child.getCurrentHeight() + yOffset)) {
                                            stop=index;
                                            break;
                                        } else
                                            child.setY(child.getY() - yOffset);
                                    } else {
                                        if (!child.setCurrentHeight(child.getCurrentHeight() - yOffset)) {
                                            stop=index;
                                            break;
                                        }
                                    }
                                    index++;
                                }
                                index = 0;
                                if (stop != -1) {
                                    outlineTopHitbox.setY(outlineTopHitbox.getY() + yOffset);
                                    for (Container child : relatedChildren) {
                                        if (index == stop)
                                            break;
                                        if (child == this) {
                                            relatedChildren.get(index).setCurrentHeight(child.getCurrentHeight() - yOffset);
                                            child.setY(child.getY() + yOffset);
                                        } else
                                            relatedChildren.get(index).setCurrentHeight(child.getCurrentHeight() + yOffset);
                                        index++;
                                    }
                                }
                            });
                            children.add(name + ".hitbox.top", outlineTopHitbox);
                        }
                    } else
                        children.remove(name + ".hitbox.top");
                    outlineTopHitbox.toFront();
                }
                this.outlineTop = outline;
                break;
            case RIGHT:
                if (resizable && this.resizable) {
                    if (outline) {
                        if (children.get(name + ".hitbox.right") == null) {
                            outlineRightHitbox = new Rectangle(outlineHitboxSize, height);
                            outlineRightHitbox.setLayoutX(x + width - Math.floor(outlineHitboxSize / 2));
                            outlineRightHitbox.setLayoutY(y);
                            outlineRightHitbox.setCursor(Cursor.E_RESIZE);
                            outlineRightHitbox.setFill(Color.TRANSPARENT);
                            outlineRightHitbox.setStroke(Color.TRANSPARENT);
                            final Point[] clickPoint = new Point[1];
                            outlineRightHitbox.setOnMousePressed(event -> clickPoint[0] = new Point((int) event.getX(), (int) event.getY()));
                            outlineRightHitbox.setOnMouseDragged(event -> {
                                int xOffset = (int) (clickPoint[0].x - event.getX());
                                clickPoint[0] = new Point((int) event.getX(), (int) event.getX());
                                outlineRightHitbox.setX(outlineRightHitbox.getX() - xOffset);
                                List<Container> relatedChildren = getRelatedTreeX(children);
                                int stop = -1;
                                int index = 0;
                                for (Container child : relatedChildren) {
                                    if (child == this) {
                                        if (!child.setCurrentWidth(child.getCurrentWidth() + xOffset)) {
                                            stop=index;
                                            break;
                                        } child.setX(child.getX() + xOffset);
                                    } else {
                                        if (!child.setCurrentWidth(child.getCurrentWidth() - xOffset)) {
                                            stop=index;
                                            break;
                                        }
                                    }
                                    index++;
                                }
                                index = 0;
                                if (stop != -1) {
                                    outlineRightHitbox.setX(outlineRightHitbox.getX() + xOffset);
                                    for (Container child : relatedChildren) {
                                        if (index == stop)
                                            break;
                                        if (child == this) {
                                            relatedChildren.get(index).setCurrentWidth(child.getCurrentWidth() - xOffset);
                                            child.setX(child.getX() - xOffset);
                                        } else
                                            relatedChildren.get(index).setCurrentWidth(child.getCurrentWidth() + xOffset);
                                        index++;
                                    }
                                }
                            });
                            children.add(name + ".hitbox.right", outlineRightHitbox);
                        }
                    } else
                        children.remove(name + ".hitbox.right");
                    outlineRightHitbox.toFront();
                }
                this.outlineRight = outline;
                break;
            case BOTTOM: // not needed
                this.outlineBottom = outline;
                break;
            case LEFT:
                this.outlineLeft = outline;
                break;
        }
        drawContainer();
        return this;
    }

    public boolean getOutline(int side) {
        switch (side) {
            case TOP:
                return outlineTop;
            case RIGHT:
                return outlineRight;
            case BOTTOM:
                return outlineBottom;
            case LEFT:
                return outlineLeft;
            default:
                return false;
        }
    }

    public Container setX(double x) {
        this.x = x;
        drawContainer();
        return this;
    }

    public double getX() {
        return x;
    }

    public Container setY(double y) {
        this.y = y;
        drawContainer();
        return this;
    }

    public double getY() {
        return y;
    }

    public double getOutlineWidth() {
        return outlineWidth;
    }

    private void drawContainer() {
        int offsetX = 0;
        int offsetY = 0;
        int offsetWidth = 0;
        int offsetHeight = 0;
        super.setLayoutX(x);
        super.setLayoutY(y);
        bg1 = new Rectangle(width, height);
        bg1.setFill(outlineColor);
        if (outlineTop) {
            offsetY++;
            offsetHeight--;
        }
        if (outlineRight) {
            offsetWidth--;
        }
        if (outlineBottom) {
            offsetHeight--;
        }
        if (outlineLeft) {
            offsetX++;
            offsetWidth--;
        }
        bg2 = new Rectangle(width+offsetWidth, height+offsetHeight);
        bg2.setLayoutX(offsetX);
        bg2.setLayoutY(offsetY);
        bg2.setFill(color);
        if (super.getChildren().isEmpty())
            super.getChildren().addAll(bg1, bg2, children);
        else {
            super.getChildren().set(0, bg1);
            super.getChildren().set(1, bg2);
            super.getChildren().set(2, children);
        }
    }
    @Override
    public ObservableList<javafx.scene.Node> getChildren() {
        return children.getChildren();
    }
}

package com.github.WildePizza.gui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.*;

public class MappedParent extends Parent {

    private final ObservableMap<String, Node> children = FXCollections.observableHashMap();
    public MappedParent() {
        children.addListener((MapChangeListener<String, Node>) change -> {
            if (change.wasAdded()) {
                super.getChildren().add(change.getValueAdded());
            }
            if (change.wasRemoved()) {
                super.getChildren().remove(change.getValueRemoved());
            }
        });
    }

    public void add(String key, Node child) {
        if (children.containsKey(key)) {
            throw new IllegalArgumentException("Key already exists: " + key);
        }
        children.put(key, child);
        children.forEach((name1, child1) -> {
            if (child1 instanceof Container) {
                children.forEach((name2, child2) -> {
                    if (child2 instanceof Container) {
                        if (((Container) child1).getY() == 0) {
                            ((Container) child1).setOutline(Container.TOP, true);
                        }
                        if (((Container) child1).getY() - ((Container) child1).getHeight() == ((Container) child2).getY()) {
                            ((Container) child1).setOutline(Container.TOP, true);
                        }
                        if (((Container) child1).getX() + ((Container) child1).getWidth() == ((Container) child2).getX()) {
                            ((Container) child1).setOutline(Container.RIGHT, true);
                        }
                    }
                });
            }
        });
    }

    public void addAll(Object... objects) {
        for (int i = 0; true; i+=2) {
            if (i >= objects.length)
                break;
            add((String) objects[i], (Node) objects[i+1]);
        }
    }

    public void remove(String key) {
        Node child = children.remove(key);
    }

    public Node get(String key) {
        return children.get(key);
    }
    @Deprecated
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    public Map<String, Node> getChildrenMap() {
        return children;
    }

    @Override
    public ObservableList<Node> getChildrenUnmodifiable() {
        return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(children.values()));
    }
}

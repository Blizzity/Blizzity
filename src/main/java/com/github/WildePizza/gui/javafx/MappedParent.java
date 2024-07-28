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
        ObservableMap<String, Node> childrenClone = FXCollections.observableHashMap();
        childrenClone.putAll(children);
        childrenClone.forEach((name1, child1) -> {
            if (child1 instanceof Container) {
                childrenClone.forEach((name2, child2) -> {
                    if (child2 instanceof Container) {
                        if (((Container) child1).hasRelatedY((Container) child2)) {
                            ((Container) child1).setOutline(Container.TOP, true, name1, this, ((Container) child2).resizable);
                        }
                        if (((Container) child1).hasRelatedX((Container) child2)) {
                            ((Container) child1).setOutline(Container.RIGHT, true, name1, this, ((Container) child2).resizable);
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

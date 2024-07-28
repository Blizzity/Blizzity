package com.github.WildePizza.gui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;

import java.util.*;

public class MappedParent extends LayeredParent {
    private final Map<Integer, ObservableMap<String, Node>> children = new HashMap<>();

    public void add(String key, Node child) {
        add(1, key, child);
    }
    public void add(int layer, String key, Node child) {
        ObservableMap<String, Node> children;
        if (this.children.get(layer) == null) {
            children = FXCollections.observableHashMap();
            children.addListener((MapChangeListener<String, Node>) change -> {
                if (change.wasAdded()) {
                    super.add(change.getValueAdded(), layer);
                }
                if (change.wasRemoved()) {
                    super.remove(change.getValueRemoved(), layer);
                }
            });
            this.children.put(layer, children);
        } else
            children = this.children.get(layer);
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
        remove(1, key);
    }
    public void remove(int layer, String key) {
        Node child = children.get(layer).remove(key);
    }

    public Node get(String key) {
        return get(1, key);
    }
    public Node get(int layer, String key) {
        return children.get(layer).get(key);
    }
    @Deprecated
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    public Map<String, Node> getChildrenMap() {
        Map<Integer, ObservableMap<String, Node>> sortedMap = new TreeMap<>(Comparator.reverseOrder());
        sortedMap.putAll(children);
        Map<String, Node> values = new HashMap<>();
        sortedMap.forEach((name, value) -> values.putAll(value));
        return values;
    }

    public Map<String, Node> getChildrenMap(int layer) {
        return children.get(layer);
    }

    @Override
    public ObservableList<Node> getChildrenUnmodifiable() {
        Map<Integer, ObservableMap<String, Node>> sortedMap = new TreeMap<>(Comparator.reverseOrder());
        sortedMap.putAll(children);
        List<Node> values = new ArrayList<>();
        sortedMap.forEach((name, value) -> values.addAll(value.values()));
        return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(values));
    }

    public ObservableList<Node> getChildrenUnmodifiable(int layer) {
        return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(children.get(layer).values()));
    }
}

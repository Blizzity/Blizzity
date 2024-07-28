package com.github.WildePizza.gui.javafx;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LayeredParent extends Parent {
    Map<Integer, Pane> children = new HashMap<>();

    public void add(Node node, int layer) {
        if (!children.containsKey(layer)) {
            Pane pane = new Pane();
            children.put(layer, pane);
            getChildren().clear();
            Map<Integer, Pane> sortedMap = new TreeMap<>(Comparator.reverseOrder());
            sortedMap.putAll(children);
            for (Node child : sortedMap.values()) {
                getChildren().add(child);
            }
        }
        children.get(layer).getChildren().add(node);
    }
    public void remove(Node node, int layer) {
        children.get(layer).getChildren().remove(node);
    }
    public void remove(Node node) {
        for (Pane pane : children.values()) {
            pane.getChildren().remove(node);
        }
    }
}

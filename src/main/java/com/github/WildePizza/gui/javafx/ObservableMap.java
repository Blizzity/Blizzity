package com.github.WildePizza.gui.javafx;

public interface ObservableMap<K, V> extends javafx.collections.ObservableMap<K, V> {
    public void putAll(Object... values);
}

package com.github.WildePizza.gui.javafx.mapped;

import java.util.ArrayList;
import java.util.List;

public class MappedNodePath {
    private List<MappedNGNode> path = new ArrayList<>();
    private int position;

    public MappedNGNode last() {
        return path.isEmpty() ? null : path.get(path.size() - 1);
    }

    // ITERATION methods

    public MappedNGNode getCurrentNode() {
        return path.get(position);
    }

    public boolean hasNext() {
        return position < path.size() -1 && !isEmpty();
    }

    public void next() {
        if (!hasNext()) {
            throw new IllegalStateException();
        }
        position++;
    }

    public void reset() {
        position = path.isEmpty() ? -1 : 0;
    }

    // MODIFICATION methods

    public void clear() {
        position = -1;
        path.clear();
    }

    public void add(MappedNGNode n) {
        path.add(0, n);
        if (position == -1) position = 0;
    }

    public int size() {
        return path.size();
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    @Override public String toString() {
        return path.toString();
    }
}

package com.github.WildePizza.gui.javafx.mapped;

import com.sun.scenario.effect.Filterable;

public interface MappedPoolFilterable extends Filterable {
    public void setImagePool(MappedImagePool pool);
    public MappedImagePool getImagePool();
}

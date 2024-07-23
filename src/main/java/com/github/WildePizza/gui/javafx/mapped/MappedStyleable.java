package com.github.WildePizza.gui.javafx.mapped;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.scene.MappedNode;


public interface MappedStyleable {

    
    String getTypeSelector();

    
    String getId();

    
    ObservableList<String> getStyleClass();

    
    String getStyle();

    
    List<CssMetaData<? extends MappedStyleable, ?>> getCssMetaData();

    
    MappedStyleable getStyleableParent();

    
    ObservableSet<PseudoClass> getPseudoClassStates();

    
    default MappedNode getStyleableNode() {
        return null;
    }
}

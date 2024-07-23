package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.shape.PathUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.css.StyleableProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Collection;
import java.util.List;
import javafx.scene.shape.FillRule;

public class MappedPath extends MappedShape {
    static {
        MappedPathHelper.setPathAccessor(new MappedPathHelper.PathAccessor() {
            @Override
            public MappedNGNode doCreatePeer(MappedNode node) {
                return ((MappedPath) node).doCreatePeer();
            }

            @Override
            public void doUpdatePeer(MappedNode node) {
                ((MappedPath) node).doUpdatePeer();
            }

            @Override
            public Bounds doComputeLayoutBounds(MappedNode node) {
                return ((MappedPath) node).doComputeLayoutBounds();
            }

            @Override
            public Paint doCssGetFillInitialValue(MappedShape shape) {
                return ((MappedPath) shape).doCssGetFillInitialValue();
            }

            @Override
            public Paint doCssGetStrokeInitialValue(MappedShape shape) {
                return ((MappedPath) shape).doCssGetStrokeInitialValue();
            }

            @Override
            public Shape doConfigShape(MappedShape shape) {
                return ((MappedPath) shape).doConfigShape();
            }

        });
    }

    private MappedPath2D path2d = null;

    {
        // To initialize the class helper at the begining each constructor of this class
        MappedPathHelper.initHelper(this);

        // overriding default values for fill and stroke
        // Set through CSS property so that it appears to be a UA style rather
        // that a USER style so that fill and stroke can still be set from CSS.
        ((StyleableProperty)fillProperty()).applyStyle(null, null);
        ((StyleableProperty)strokeProperty()).applyStyle(null, Color.BLACK);
    }

    
    public MappedPath() {
    }

    
    public MappedPath(MappedPathElement... elements) {
        if (elements != null) {
            this.elements.addAll(elements);
        }
    }

    
    public MappedPath(Collection<? extends MappedPathElement> elements) {
        if (elements != null) {
            this.elements.addAll(elements);
        }
    }

    void markPathDirty() {
        path2d = null;
        MappedNodeHelper.markDirty(this, DirtyBits.NODE_CONTENTS);
        MappedNodeHelper.geomChanged(this);
    }

    
    private ObjectProperty<FillRule> fillRule;

    public final void setFillRule(FillRule value) {
        if (fillRule != null || value != FillRule.NON_ZERO) {
            fillRuleProperty().set(value);
        }
    }

    public final FillRule getFillRule() {
        return fillRule == null ? FillRule.NON_ZERO : fillRule.get();
    }

    public final ObjectProperty<FillRule> fillRuleProperty() {
        if (fillRule == null) {
            fillRule = new ObjectPropertyBase<FillRule>(FillRule.NON_ZERO) {

                @Override
                public void invalidated() {
                    MappedNodeHelper.markDirty(MappedPath.this, DirtyBits.NODE_CONTENTS);
                    MappedNodeHelper.geomChanged(MappedPath.this);
                }

                @Override
                public Object getBean() {
                    return MappedPath.this;
                }

                @Override
                public String getName() {
                    return "fillRule";
                }
            };
        }
        return fillRule;
    }

    private boolean isPathValid;
    
    private final ObservableList<MappedPathElement> elements = new TrackableObservableList<MappedPathElement>() {
        @Override
        protected void onChanged(Change<MappedPathElement> c) {
            List<MappedPathElement> list = c.getList();
            boolean firstElementChanged = false;
            while (c.next()) {
                List<MappedPathElement> removed = c.getRemoved();
                for (int i = 0; i < c.getRemovedSize(); ++i) {
                    removed.get(i).removeNode(MappedPath.this);
                }
                for (int i = c.getFrom(); i < c.getTo(); ++i) {
                    list.get(i).addNode(MappedPath.this);
                }
                firstElementChanged |= c.getFrom() == 0;
            }

            //Note: as ArcTo may create a various number of PathElements,
            // we cannot count the number of PathElements removed (fast enough).
            // Thus we can optimize only if some elements were added to the end
            if (path2d != null) {
                c.reset();
                c.next();
                // we just have to check the first change, as more changes cannot come after such change
                if (c.getFrom() == c.getList().size() && !c.wasRemoved() && c.wasAdded()) {
                    // some elements added
                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        MappedPathElementHelper.addTo(list.get(i), path2d);
                    }
                } else {
                    path2d = null;
                }
            }
            if (firstElementChanged) {
                isPathValid = isFirstPathElementValid();
            }

            MappedNodeHelper.markDirty(MappedPath.this, DirtyBits.NODE_CONTENTS);
            MappedNodeHelper.geomChanged(MappedPath.this);
        }
    };

    
    public final ObservableList<MappedPathElement> getElements() { return elements; }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private MappedNGNode doCreatePeer() {
        return new MappedNGPath();
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private MappedPath2D doConfigShape() {
        if (isPathValid) {
            if (path2d == null) {
                path2d = PathUtils.configShape(getElements(), getFillRule() == FillRule.EVEN_ODD);
            } else {
                path2d.setWindingRule(getFillRule() == FillRule.NON_ZERO ?
                        MappedPath2D.WIND_NON_ZERO : MappedPath2D.WIND_EVEN_ODD);
            }
            return path2d;
        } else {
            return new MappedPath2D();
        }
    }

    private Bounds doComputeLayoutBounds() {
        if (isPathValid) {
            return null; // Helper will need to call its super's compute layout bounds
        }
        return new BoundingBox(0, 0, -1, -1); //create empty bounds
    }

    private boolean isFirstPathElementValid() {
        ObservableList<MappedPathElement> _elements = getElements();
        if (_elements != null && _elements.size() > 0) {
            MappedPathElement firstElement = _elements.get(0);
            if (!firstElement.isAbsolute()) {
                System.err.printf("First element of the path can not be relative. MappedPath: %s\n", this);
                return false;
            } else if (firstElement instanceof MoveTo) {
                return true;
            } else {
                System.err.printf("Missing initial moveto in path definition. MappedPath: %s\n", this);
                return false;
            }
        }
        return true;
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private void doUpdatePeer() {
        if (MappedNodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
            MappedNGPath peer = MappedNodeHelper.getPeer(this);
            if (peer.acceptsPath2dOnUpdate()) {
                peer.updateWithPath2d((MappedPath2D) MappedShapeHelper.configShape(this));
            } else {
                peer.reset();
                if (isPathValid) {
                    peer.setFillRule(getFillRule());
                    for (final MappedPathElement elt : getElements()) {
                        elt.addTo(peer);
                    }
                    peer.update();
                }
            }
        }
    }

    /* *************************************************************************
     *                                                                         *
     *                         Stylesheet Handling                             *
     *                                                                         *
     **************************************************************************/

    /*
     * Some sub-class of MappedShape, such as {@link Line}, override the
     * default value for the {@link MappedShape#fill} property. This allows
     * CSS to get the correct initial value.
     *
     * Note: This method MUST only be called via its accessor method.
     */
    private Paint doCssGetFillInitialValue() {
        return null;
    }

    /*
     * Some sub-class of MappedShape, such as {@link Line}, override the
     * default value for the {@link MappedShape#stroke} property. This allows
     * CSS to get the correct initial value.
     *
     * Note: This method MUST only be called via its accessor method.
     */
    private Paint doCssGetStrokeInitialValue() {
        return Color.BLACK;
    }

    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MappedPath[");

        String id = getId();
        if (id != null) {
            sb.append("id=").append(id).append(", ");
        }

        sb.append("elements=").append(getElements());

        sb.append(", fill=").append(getFill());
        sb.append(", fillRule=").append(getFillRule());

        Paint stroke = getStroke();
        if (stroke != null) {
            sb.append(", stroke=").append(stroke);
            sb.append(", strokeWidth=").append(getStrokeWidth());
        }

        return sb.append("]").toString();
    }
}

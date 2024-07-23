package com.github.WildePizza.gui.javafx.mapped;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.MappedStyleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sun.javafx.util.Utils;
import com.sun.javafx.beans.event.AbstractNotifyListener;
import com.sun.javafx.collections.TrackableObservableList;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.sg.prism.NGShape;
import javafx.scene.shape.*;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public abstract class MappedShape extends MappedNode {

    static {
        // This is used by classes in different packages to get access to
        // private and package private methods.
        MappedShapeHelper.setShapeAccessor(new MappedShapeHelper.ShapeAccessor() {
            @Override
            public void doUpdatePeer(MappedNode node) {
                ((MappedShape) node).doUpdatePeer();
            }

            @Override
            public void doMarkDirty(MappedNode node, DirtyBits dirtyBit) {
                ((MappedShape) node).doMarkDirty(dirtyBit);
            }

            @Override
            public BaseBounds doComputeGeomBounds(MappedNode node,
                                                  BaseBounds bounds, BaseTransform tx) {
                return ((MappedShape) node).doComputeGeomBounds(bounds, tx);
            }

            @Override
            public boolean doComputeContains(MappedNode node, double localX, double localY) {
                return ((MappedShape) node).doComputeContains(localX, localY);
            }

            @Override
            public Paint doCssGetFillInitialValue(MappedShape shape) {
                return shape.doCssGetFillInitialValue();
            }

            @Override
            public Paint doCssGetStrokeInitialValue(MappedShape shape) {
                return shape.doCssGetStrokeInitialValue();
            }

            @Override
            public NGShape.Mode getMode(MappedShape shape) {
                return shape.getMode();
            }

            @Override
            public void setMode(MappedShape shape, NGShape.Mode mode) {
                shape.setMode(mode);
            }

            @Override
            public void setShapeChangeListener(MappedShape shape, Runnable listener) {
                shape.setShapeChangeListener(listener);
            }
        });
    }

    
    public MappedShape() {
    }

    StrokeLineJoin convertLineJoin(StrokeLineJoin t) {
        return t;
    }

    public final void setStrokeType(StrokeType value) {
        strokeTypeProperty().set(value);
    }

    public final StrokeType getStrokeType() {
        return (strokeAttributes == null) ? DEFAULT_STROKE_TYPE
                : strokeAttributes.getType();
    }

    
    public final ObjectProperty<StrokeType> strokeTypeProperty() {
        return getStrokeAttributes().typeProperty();
    }

    public final void setStrokeWidth(double value) {
        strokeWidthProperty().set(value);
    }

    public final double getStrokeWidth() {
        return (strokeAttributes == null) ? DEFAULT_STROKE_WIDTH
                : strokeAttributes.getWidth();
    }

    
    public final DoubleProperty strokeWidthProperty() {
        return getStrokeAttributes().widthProperty();
    }

    public final void setStrokeLineJoin(StrokeLineJoin value) {
        strokeLineJoinProperty().set(value);
    }

    public final StrokeLineJoin getStrokeLineJoin() {
        return (strokeAttributes == null)
                ? DEFAULT_STROKE_LINE_JOIN
                : strokeAttributes.getLineJoin();
    }

    
    public final ObjectProperty<StrokeLineJoin> strokeLineJoinProperty() {
        return getStrokeAttributes().lineJoinProperty();
    }

    public final void setStrokeLineCap(StrokeLineCap value) {
        strokeLineCapProperty().set(value);
    }

    public final StrokeLineCap getStrokeLineCap() {
        return (strokeAttributes == null) ? DEFAULT_STROKE_LINE_CAP
                : strokeAttributes.getLineCap();
    }

    
    public final ObjectProperty<StrokeLineCap> strokeLineCapProperty() {
        return getStrokeAttributes().lineCapProperty();
    }

    public final void setStrokeMiterLimit(double value) {
        strokeMiterLimitProperty().set(value);
    }

    public final double getStrokeMiterLimit() {
        return (strokeAttributes == null) ? DEFAULT_STROKE_MITER_LIMIT
                : strokeAttributes.getMiterLimit();
    }

    
    public final DoubleProperty strokeMiterLimitProperty() {
        return getStrokeAttributes().miterLimitProperty();
    }

    public final void setStrokeDashOffset(double value) {
        strokeDashOffsetProperty().set(value);
    }

    public final double getStrokeDashOffset() {
        return (strokeAttributes == null) ? DEFAULT_STROKE_DASH_OFFSET
                : strokeAttributes.getDashOffset();
    }

    
    public final DoubleProperty strokeDashOffsetProperty() {
        return getStrokeAttributes().dashOffsetProperty();
    }

    
    public final ObservableList<Double> getStrokeDashArray() {
        return getStrokeAttributes().dashArrayProperty();
    }

    private NGShape.Mode computeMode() {
        if (getFill() != null && getStroke() != null) {
            return NGShape.Mode.STROKE_FILL;
        } else if (getFill() != null) {
            return NGShape.Mode.FILL;
        } else if (getStroke() != null) {
            return NGShape.Mode.STROKE;
        } else {
            return NGShape.Mode.EMPTY;
        }
    }

    NGShape.Mode getMode() {
        return mode;
    }

    void setMode(NGShape.Mode mode) {
        mode = mode;
    }

    private NGShape.Mode mode = NGShape.Mode.FILL;

    private void checkModeChanged() {
        NGShape.Mode newMode = computeMode();
        if (mode != newMode) {
            mode = newMode;

            MappedNodeHelper.markDirty(this, DirtyBits.SHAPE_MODE);
            MappedNodeHelper.geomChanged(this);
        }
    }

    
    private ObjectProperty<Paint> fill;


    public final void setFill(Paint value) {
        fillProperty().set(value);
    }

    public final Paint getFill() {
        return fill == null ? Color.BLACK : fill.get();
    }

    Paint old_fill;
    public final ObjectProperty<Paint> fillProperty() {
        if (fill == null) {
            fill = new StyleableObjectProperty<Paint>(Color.BLACK) {

                boolean needsListener = false;

                @Override public void invalidated() {

                    Paint _fill = get();

                    if (needsListener) {
                        MappedToolkit.getPaintAccessor().
                                removeListener(old_fill, platformImageChangeListener);
                    }
                    needsListener = _fill != null &&
                            MappedToolkit.getPaintAccessor().isMutable(_fill);
                    old_fill = _fill;

                    if (needsListener) {
                        MappedToolkit.getPaintAccessor().
                                addListener(_fill, platformImageChangeListener);
                    }

                    MappedNodeHelper.markDirty(MappedShape.this, DirtyBits.SHAPE_FILL);
                    checkModeChanged();
                }

                @Override
                public CssMetaData<MappedShape,Paint> getCssMetaData() {
                    return StyleableProperties.FILL;
                }

                @Override
                public Object getBean() {
                    return MappedShape.this;
                }

                @Override
                public String getName() {
                    return "fill";
                }
            };
        }
        return fill;
    }

    
    private ObjectProperty<Paint> stroke;


    public final void setStroke(Paint value) {
        strokeProperty().set(value);
    }

    private final AbstractNotifyListener platformImageChangeListener =
            new AbstractNotifyListener() {
                @Override
                public void invalidated(Observable valueModel) {
                    MappedNodeHelper.markDirty(MappedShape.this, DirtyBits.SHAPE_FILL);
                    MappedNodeHelper.markDirty(MappedShape.this, DirtyBits.SHAPE_STROKE);
                    MappedNodeHelper.geomChanged(MappedShape.this);
                    checkModeChanged();
                }
            };

    public final Paint getStroke() {
        return stroke == null ? null : stroke.get();
    }

    Paint old_stroke;
    public final ObjectProperty<Paint> strokeProperty() {
        if (stroke == null) {
            stroke = new StyleableObjectProperty<Paint>() {

                boolean needsListener = false;

                @Override public void invalidated() {

                    Paint _stroke = get();

                    if (needsListener) {
                        MappedToolkit.getPaintAccessor().
                                removeListener(old_stroke, platformImageChangeListener);
                    }
                    needsListener = _stroke != null &&
                            MappedToolkit.getPaintAccessor().isMutable(_stroke);
                    old_stroke = _stroke;

                    if (needsListener) {
                        MappedToolkit.getPaintAccessor().
                                addListener(_stroke, platformImageChangeListener);
                    }

                    MappedNodeHelper.markDirty(MappedShape.this, DirtyBits.SHAPE_STROKE);
                    checkModeChanged();
                }

                @Override
                public CssMetaData<MappedShape,Paint> getCssMetaData() {
                    return StyleableProperties.STROKE;
                }

                @Override
                public Object getBean() {
                    return MappedShape.this;
                }

                @Override
                public String getName() {
                    return "stroke";
                }
            };
        }
        return stroke;
    }

    
    private BooleanProperty smooth;


    public final void setSmooth(boolean value) {
        smoothProperty().set(value);
    }

    public final boolean isSmooth() {
        return smooth == null ? true : smooth.get();
    }

    public final BooleanProperty smoothProperty() {
        if (smooth == null) {
            smooth = new StyleableBooleanProperty(true) {

                @Override
                public void invalidated() {
                    MappedNodeHelper.markDirty(MappedShape.this, DirtyBits.NODE_SMOOTH);
                }

                @Override
                public CssMetaData<MappedShape,Boolean> getCssMetaData() {
                    return StyleableProperties.SMOOTH;
                }

                @Override
                public Object getBean() {
                    return MappedShape.this;
                }

                @Override
                public String getName() {
                    return "smooth";
                }
            };
        }
        return smooth;
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
        return Color.BLACK;
    }

    /*
     * Some sub-class of MappedShape, such as {@link Line}, override the
     * default value for the {@link MappedShape#stroke} property. This allows
     * CSS to get the correct initial value.
     *
     * Note: This method MUST only be called via its accessor method.
     */
    private Paint doCssGetStrokeInitialValue() {
        return null;
    }


    /*
     * Super-lazy instantiation pattern from Bill Pugh.
     */
    private static class StyleableProperties {

        
        private static final CssMetaData<MappedShape,Paint> FILL =
                new CssMetaData<MappedShape,Paint>("-fx-fill",
                        PaintConverter.getInstance(), Color.BLACK) {

                    @Override
                    public boolean isSettable(MappedShape node) {
                        return node.fill == null || !node.fill.isBound();
                    }

                    @Override
                    public StyleableProperty<Paint> getStyleableProperty(MappedShape node) {
                        return (StyleableProperty<Paint>)node.fillProperty();
                    }

                    @Override
                    public Paint getInitialValue(MappedShape node) {
                        // Some shapes have a different initial value for fill.
                        // Give a way to have them return the correct initial value.
                        return MappedShapeHelper.cssGetFillInitialValue(node);
                    }

                };

        
        private static final CssMetaData<MappedShape,Boolean> SMOOTH =
                new CssMetaData<MappedShape,Boolean>("-fx-smooth",
                        BooleanConverter.getInstance(), Boolean.TRUE) {

                    @Override
                    public boolean isSettable(MappedShape node) {
                        return node.smooth == null || !node.smooth.isBound();
                    }

                    @Override
                    public StyleableProperty<Boolean> getStyleableProperty(MappedShape node) {
                        return (StyleableProperty<Boolean>)node.smoothProperty();
                    }

                };

        
        private static final CssMetaData<MappedShape,Paint> STROKE =
                new CssMetaData<MappedShape,Paint>("-fx-stroke",
                        PaintConverter.getInstance()) {

                    @Override
                    public boolean isSettable(MappedShape node) {
                        return node.stroke == null || !node.stroke.isBound();
                    }

                    @Override
                    public StyleableProperty<Paint> getStyleableProperty(MappedShape node) {
                        return (StyleableProperty<Paint>)node.strokeProperty();
                    }

                    @Override
                    public Paint getInitialValue(MappedShape node) {
                        // Some shapes have a different initial value for stroke.
                        // Give a way to have them return the correct initial value.
                        return MappedShapeHelper.cssGetStrokeInitialValue(node);
                    }


                };

        
        private static final CssMetaData<MappedShape,Number[]> STROKE_DASH_ARRAY =
                new CssMetaData<MappedShape,Number[]>("-fx-stroke-dash-array",
                        SizeConverter.SequenceConverter.getInstance(),
                        new Double[0]) {

                    @Override
                    public boolean isSettable(MappedShape node) {
                        return true;
                    }

                    @Override
                    public StyleableProperty<Number[]> getStyleableProperty(final MappedShape node) {
                        return (StyleableProperty<Number[]>)node.getStrokeAttributes().cssDashArrayProperty();
                    }

                };

        
        private static final CssMetaData<MappedShape,Number> STROKE_DASH_OFFSET =
                new CssMetaData<MappedShape,Number>("-fx-stroke-dash-offset",
                        SizeConverter.getInstance(), 0.0) {

                    @Override
                    public boolean isSettable(MappedShape node) {
                        return node.strokeAttributes == null ||
                                node.strokeAttributes.canSetDashOffset();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MappedShape node) {
                        return (StyleableProperty<Number>)node.strokeDashOffsetProperty();
                    }

                };

        
        private static final CssMetaData<MappedShape,StrokeLineCap> STROKE_LINE_CAP =
                new CssMetaData<MappedShape,StrokeLineCap>("-fx-stroke-line-cap",
                        new EnumConverter<StrokeLineCap>(StrokeLineCap.class),
                        StrokeLineCap.SQUARE) {

                    @Override
                    public boolean isSettable(MappedShape node) {
                        return node.strokeAttributes == null ||
                                node.strokeAttributes.canSetLineCap();
                    }

                    @Override
                    public StyleableProperty<StrokeLineCap> getStyleableProperty(MappedShape node) {
                        return (StyleableProperty<StrokeLineCap>)node.strokeLineCapProperty();
                    }

                };

        
        private static final CssMetaData<MappedShape,StrokeLineJoin> STROKE_LINE_JOIN =
                new CssMetaData<MappedShape,StrokeLineJoin>("-fx-stroke-line-join",
                        new EnumConverter<StrokeLineJoin>(StrokeLineJoin.class),
                        StrokeLineJoin.MITER) {

                    @Override
                    public boolean isSettable(MappedShape node) {
                        return node.strokeAttributes == null ||
                                node.strokeAttributes.canSetLineJoin();
                    }

                    @Override
                    public StyleableProperty<StrokeLineJoin> getStyleableProperty(MappedShape node) {
                        return (StyleableProperty<StrokeLineJoin>)node.strokeLineJoinProperty();
                    }

                };

        
        private static final CssMetaData<MappedShape,StrokeType> STROKE_TYPE =
                new CssMetaData<MappedShape,StrokeType>("-fx-stroke-type",
                        new EnumConverter<StrokeType>(StrokeType.class),
                        StrokeType.CENTERED) {

                    @Override
                    public boolean isSettable(MappedShape node) {
                        return node.strokeAttributes == null ||
                                node.strokeAttributes.canSetType();
                    }

                    @Override
                    public StyleableProperty<StrokeType> getStyleableProperty(MappedShape node) {
                        return (StyleableProperty<StrokeType>)node.strokeTypeProperty();
                    }


                };

        
        private static final CssMetaData<MappedShape,Number> STROKE_MITER_LIMIT =
                new CssMetaData<MappedShape,Number>("-fx-stroke-miter-limit",
                        SizeConverter.getInstance(), 10.0) {

                    @Override
                    public boolean isSettable(MappedShape node) {
                        return node.strokeAttributes == null ||
                                node.strokeAttributes.canSetMiterLimit();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MappedShape node) {
                        return (StyleableProperty<Number>)node.strokeMiterLimitProperty();
                    }

                };

        
        private static final CssMetaData<MappedShape,Number> STROKE_WIDTH =
                new CssMetaData<MappedShape,Number>("-fx-stroke-width",
                        SizeConverter.getInstance(), 1.0) {

                    @Override
                    public boolean isSettable(MappedShape node) {
                        return node.strokeAttributes == null ||
                                node.strokeAttributes.canSetWidth();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MappedShape node) {
                        return (StyleableProperty<Number>)node.strokeWidthProperty();
                    }

                };
        private static final List<CssMetaData<? extends MappedStyleable, ?>> STYLEABLES;
        static {

            final List<CssMetaData<? extends MappedStyleable, ?>> styleables =
                    new ArrayList<CssMetaData<? extends MappedStyleable, ?>>(MappedNode.getClassCssMetaData());
            styleables.add(FILL);
            styleables.add(SMOOTH);
            styleables.add(STROKE);
            styleables.add(STROKE_DASH_ARRAY);
            styleables.add(STROKE_DASH_OFFSET);
            styleables.add(STROKE_LINE_CAP);
            styleables.add(STROKE_LINE_JOIN);
            styleables.add(STROKE_TYPE);
            styleables.add(STROKE_MITER_LIMIT);
            styleables.add(STROKE_WIDTH);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    
    public static List<CssMetaData<? extends MappedStyleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    


    @Override
    public List<CssMetaData<? extends MappedStyleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private BaseBounds doComputeGeomBounds(BaseBounds bounds,
                                           BaseTransform tx) {
        return computeShapeBounds(bounds, tx, MappedShapeHelper.configShape(this));
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private boolean doComputeContains(double localX, double localY) {
        return computeShapeContains(localX, localY, MappedShapeHelper.configShape(this));
    }

    private static final double MIN_STROKE_WIDTH = 0.0f;
    private static final double MIN_STROKE_MITER_LIMIT = 1.0f;

    private void updatePGShape() {
        final NGShape peer = MappedNodeHelper.getPeer(this);
        if (strokeAttributesDirty && (getStroke() != null)) {
            // set attributes of stroke only when stroke paint is not null
            final float[] pgDashArray =
                    (hasStrokeDashArray())
                            ? toPGDashArray(getStrokeDashArray())
                            : DEFAULT_PG_STROKE_DASH_ARRAY;

            peer.setDrawStroke(
                    (float)Utils.clampMin(getStrokeWidth(),
                            MIN_STROKE_WIDTH),
                    getStrokeType(),
                    getStrokeLineCap(),
                    convertLineJoin(getStrokeLineJoin()),
                    (float)Utils.clampMin(getStrokeMiterLimit(),
                            MIN_STROKE_MITER_LIMIT),
                    pgDashArray, (float)getStrokeDashOffset());

            strokeAttributesDirty = false;
        }

        if (MappedNodeHelper.isDirty(this, DirtyBits.SHAPE_MODE)) {
            peer.setMode(mode);
        }

        if (MappedNodeHelper.isDirty(this, DirtyBits.SHAPE_FILL)) {
            Paint localFill = getFill();
            peer.setFillPaint(localFill == null ? null :
                    MappedToolkit.getPaintAccessor().getPlatformPaint(localFill));
        }

        if (MappedNodeHelper.isDirty(this, DirtyBits.SHAPE_STROKE)) {
            Paint localStroke = getStroke();
            peer.setDrawPaint(localStroke == null ? null :
                    MappedToolkit.getPaintAccessor().getPlatformPaint(localStroke));
        }

        if (MappedNodeHelper.isDirty(this, DirtyBits.NODE_SMOOTH)) {
            peer.setSmooth(isSmooth());
        }
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private void doMarkDirty(DirtyBits dirtyBits) {
        final Runnable listener = shapeChangeListener != null ? shapeChangeListener.get() : null;
        if (listener != null && MappedNodeHelper.isDirtyEmpty(this)) {
            listener.run();
        }
    }

    private Reference<Runnable> shapeChangeListener;

    void setShapeChangeListener(Runnable listener) {
        if (shapeChangeListener != null) shapeChangeListener.clear();
        shapeChangeListener = listener != null ? new WeakReference(listener) : null;
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private void doUpdatePeer() {
        updatePGShape();
    }

    
    BaseBounds computeBounds(BaseBounds bounds, BaseTransform tx,
                             double upad, double dpad,
                             double x, double y,
                             double w, double h)
    {
        // if the w or h is < 0 then bounds is empty
        if (w < 0.0f || h < 0.0f) return bounds.makeEmpty();

        double x0 = x;
        double y0 = y;
        double x1 = w;
        double y1 = h;
        double _dpad = dpad;
        if (tx.isTranslateOrIdentity()) {
            x1 += x0;
            y1 += y0;
            if (tx.getType() == BaseTransform.TYPE_TRANSLATION) {
                final double dx = tx.getMxt();
                final double dy = tx.getMyt();
                x0 += dx;
                y0 += dy;
                x1 += dx;
                y1 += dy;
            }
            _dpad += upad;
        } else {
            x0 -= upad;
            y0 -= upad;
            x1 += upad*2;
            y1 += upad*2;
            // Each corner is transformed by an equation similar to:
            //     x' = x * mxx + y * mxy + mxt
            //     y' = x * myx + y * myy + myt
            // Since all of the corners are translated by mxt,myt we
            // can ignore them when doing the min/max calculations
            // and add them in once when we are done.  We then have
            // to do min/max operations on 4 points defined as:
            //     x' = x * mxx + y * mxy
            //     y' = x * myx + y * myy
            // Furthermore, the four corners that we will be transforming
            // are not four independent coordinates, they are in a
            // rectangular formation.  To that end, if we translated
            // the transform to x,y and scaled it by width,height then
            // we could compute the min/max of the unit rectangle 0,0,1x1.
            // The transform would then be adjusted as follows:
            // First, the translation to x,y only affects the mxt,myt
            // components of the transform which we can hold off on adding
            // until we are done with the min/max.  The adjusted translation
            // components would be:
            //     mxt' = x * mxx + y * mxy + mxt
            //     myt' = x * myx + y * myy + myt
            // Second, the scale affects the components as follows:
            //     mxx' = mxx * width
            //     mxy' = mxy * height
            //     myx' = myx * width
            //     myy' = myy * height
            // The min/max of that rectangle then degenerates to:
            //     x00' = 0 * mxx' + 0 * mxy' = 0
            //     y00' = 0 * myx' + 0 * myy' = 0
            //     x01' = 0 * mxx' + 1 * mxy' = mxy'
            //     y01' = 0 * myx' + 1 * myy' = myy'
            //     x10' = 1 * mxx' + 0 * mxy' = mxx'
            //     y10' = 1 * myx' + 0 * myy' = myx'
            //     x11' = 1 * mxx' + 1 * mxy' = mxx' + mxy'
            //     y11' = 1 * myx' + 1 * myy' = myx' + myy'
            double mxx = tx.getMxx();
            double mxy = tx.getMxy();
            double myx = tx.getMyx();
            double myy = tx.getMyy();
            // Computed translated translation components
            final double mxt = (x0 * mxx + y0 * mxy + tx.getMxt());
            final double myt = (x0 * myx + y0 * myy + tx.getMyt());
            // Scale non-translation components by w/h
            mxx *= x1;
            mxy *= y1;
            myx *= x1;
            myy *= y1;
            x0 = (Math.min(Math.min(0,mxx),Math.min(mxy,mxx+mxy)))+mxt;
            y0 = (Math.min(Math.min(0,myx),Math.min(myy,myx+myy)))+myt;
            x1 = (Math.max(Math.max(0,mxx),Math.max(mxy,mxx+mxy)))+mxt;
            y1 = (Math.max(Math.max(0,myx),Math.max(myy,myx+myy)))+myt;
        }
        x0 -= _dpad;
        y0 -= _dpad;
        x1 += _dpad;
        y1 += _dpad;

        bounds = bounds.deriveWithNewBounds((float)x0, (float)y0, 0.0f,
                (float)x1, (float)y1, 0.0f);
        return bounds;
    }

    BaseBounds computeShapeBounds(BaseBounds bounds, BaseTransform tx,
                                  Shape s)
    {
        // empty mode means no bounds!
        if (mode == NGShape.Mode.EMPTY) {
            return bounds.makeEmpty();
        }

        float[] bbox = {
                Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
                Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
        };
        boolean includeShape = (mode != NGShape.Mode.STROKE);
        boolean includeStroke = (mode != NGShape.Mode.FILL);
        if (includeStroke && (getStrokeType() == StrokeType.INSIDE)) {
            includeShape = true;
            includeStroke = false;
        }

        if (includeStroke) {
            final StrokeType type = getStrokeType();
            double sw = Utils.clampMin(getStrokeWidth(), MIN_STROKE_WIDTH);
            StrokeLineCap cap = getStrokeLineCap();
            StrokeLineJoin join = convertLineJoin(getStrokeLineJoin());
            float miterlimit =
                    (float) Utils.clampMin(getStrokeMiterLimit(), MIN_STROKE_MITER_LIMIT);
            // Note that we ignore dashing for computing bounds and testing
            // point containment, both to save time in bounds calculations
            // and so that animated dashing does not keep perturbing the bounds...
            MappedToolkit.getToolkit().accumulateStrokeBounds(
                    s,
                    bbox, type, sw,
                    cap, join, miterlimit, tx);
            // Account for "minimum pen size" by expanding by 0.5 device
            // pixels all around...
            bbox[0] -= 0.5;
            bbox[1] -= 0.5;
            bbox[2] += 0.5;
            bbox[3] += 0.5;
        } else if (includeShape) {
            Shape.accumulate(bbox, s, tx);
        }

        if (bbox[2] < bbox[0] || bbox[3] < bbox[1]) {
            // They are probably +/-INFINITY which would yield NaN if subtracted
            // Let's just return a "safe" empty bbox..
            return bounds.makeEmpty();
        }
        bounds = bounds.deriveWithNewBounds(bbox[0], bbox[1], 0.0f,
                bbox[2], bbox[3], 0.0f);
        return bounds;
    }

    boolean computeShapeContains(double localX, double localY,
                                 Shape s) {
        if (mode == NGShape.Mode.EMPTY) {
            return false;
        }

        boolean includeShape = (mode != NGShape.Mode.STROKE);
        boolean includeStroke = (mode != NGShape.Mode.FILL);
        if (includeStroke && includeShape &&
                (getStrokeType() == StrokeType.INSIDE))
        {
            includeStroke = false;
        }

        if (includeShape) {
            if (s.contains((float)localX, (float)localY)) {
                return true;
            }
        }

        if (includeStroke) {
            StrokeType type = getStrokeType();
            double sw = Utils.clampMin(getStrokeWidth(), MIN_STROKE_WIDTH);
            StrokeLineCap cap = getStrokeLineCap();
            StrokeLineJoin join = convertLineJoin(getStrokeLineJoin());
            float miterlimit =
                    (float) Utils.clampMin(getStrokeMiterLimit(), MIN_STROKE_MITER_LIMIT);
            // Note that we ignore dashing for computing bounds and testing
            // point containment, both to save time in bounds calculations
            // and so that animated dashing does not keep perturbing the bounds...
            return MappedToolkit.getToolkit().strokeContains(s, localX, localY,
                    type, sw, cap,
                    join, miterlimit);
        }

        return false;
    }

    private boolean strokeAttributesDirty = true;

    private StrokeAttributes strokeAttributes;

    private StrokeAttributes getStrokeAttributes() {
        if (strokeAttributes == null) {
            strokeAttributes = new StrokeAttributes();
        }

        return strokeAttributes;
    }

    private boolean hasStrokeDashArray() {
        return (strokeAttributes != null) && strokeAttributes.hasDashArray();
    }

    private static float[] toPGDashArray(final List<Double> dashArray) {
        final int size = dashArray.size();
        final float[] pgDashArray = new float[size];
        for (int i = 0; i < size; i++) {
            pgDashArray[i] = dashArray.get(i).floatValue();
        }

        return pgDashArray;
    }

    private static final StrokeType DEFAULT_STROKE_TYPE = StrokeType.CENTERED;
    private static final double DEFAULT_STROKE_WIDTH = 1.0;
    private static final StrokeLineJoin DEFAULT_STROKE_LINE_JOIN =
            StrokeLineJoin.MITER;
    private static final StrokeLineCap DEFAULT_STROKE_LINE_CAP =
            StrokeLineCap.SQUARE;
    private static final double DEFAULT_STROKE_MITER_LIMIT = 10.0;
    private static final double DEFAULT_STROKE_DASH_OFFSET = 0;
    private static final float[] DEFAULT_PG_STROKE_DASH_ARRAY = new float[0];

    private final class StrokeAttributes {
        private ObjectProperty<StrokeType> type;
        private DoubleProperty width;
        private ObjectProperty<StrokeLineJoin> lineJoin;
        private ObjectProperty<StrokeLineCap> lineCap;
        private DoubleProperty miterLimit;
        private DoubleProperty dashOffset;
        private ObservableList<Double> dashArray;

        public final StrokeType getType() {
            return (type == null) ? DEFAULT_STROKE_TYPE : type.get();
        }

        public final ObjectProperty<StrokeType> typeProperty() {
            if (type == null) {
                type = new StyleableObjectProperty<StrokeType>(DEFAULT_STROKE_TYPE) {

                    @Override
                    public void invalidated() {
                        StrokeAttributes.this.invalidated(
                                StyleableProperties.STROKE_TYPE);
                    }

                    @Override
                    public CssMetaData<MappedShape,StrokeType> getCssMetaData() {
                        return StyleableProperties.STROKE_TYPE;
                    }

                    @Override
                    public Object getBean() {
                        return MappedShape.this;
                    }

                    @Override
                    public String getName() {
                        return "strokeType";
                    }
                };
            }
            return type;
        }

        public double getWidth() {
            return (width == null) ? DEFAULT_STROKE_WIDTH : width.get();
        }

        public final DoubleProperty widthProperty() {
            if (width == null) {
                width = new StyleableDoubleProperty(DEFAULT_STROKE_WIDTH) {

                    @Override
                    public void invalidated() {
                        StrokeAttributes.this.invalidated(
                                StyleableProperties.STROKE_WIDTH);
                    }

                    @Override
                    public CssMetaData<MappedShape,Number> getCssMetaData() {
                        return StyleableProperties.STROKE_WIDTH;
                    }

                    @Override
                    public Object getBean() {
                        return MappedShape.this;
                    }

                    @Override
                    public String getName() {
                        return "strokeWidth";
                    }
                };
            }
            return width;
        }

        public StrokeLineJoin getLineJoin() {
            return (lineJoin == null) ? DEFAULT_STROKE_LINE_JOIN
                    : lineJoin.get();
        }

        public final ObjectProperty<StrokeLineJoin> lineJoinProperty() {
            if (lineJoin == null) {
                lineJoin = new StyleableObjectProperty<StrokeLineJoin>(
                        DEFAULT_STROKE_LINE_JOIN) {

                    @Override
                    public void invalidated() {
                        StrokeAttributes.this.invalidated(
                                StyleableProperties.STROKE_LINE_JOIN);
                    }

                    @Override
                    public CssMetaData<MappedShape,StrokeLineJoin> getCssMetaData() {
                        return StyleableProperties.STROKE_LINE_JOIN;
                    }

                    @Override
                    public Object getBean() {
                        return MappedShape.this;
                    }

                    @Override
                    public String getName() {
                        return "strokeLineJoin";
                    }
                };
            }
            return lineJoin;
        }

        public StrokeLineCap getLineCap() {
            return (lineCap == null) ? DEFAULT_STROKE_LINE_CAP
                    : lineCap.get();
        }

        public final ObjectProperty<StrokeLineCap> lineCapProperty() {
            if (lineCap == null) {
                lineCap = new StyleableObjectProperty<StrokeLineCap>(
                        DEFAULT_STROKE_LINE_CAP) {

                    @Override
                    public void invalidated() {
                        StrokeAttributes.this.invalidated(
                                StyleableProperties.STROKE_LINE_CAP);
                    }

                    @Override
                    public CssMetaData<MappedShape,StrokeLineCap> getCssMetaData() {
                        return StyleableProperties.STROKE_LINE_CAP;
                    }

                    @Override
                    public Object getBean() {
                        return MappedShape.this;
                    }

                    @Override
                    public String getName() {
                        return "strokeLineCap";
                    }
                };
            }

            return lineCap;
        }

        public double getMiterLimit() {
            return (miterLimit == null) ? DEFAULT_STROKE_MITER_LIMIT
                    : miterLimit.get();
        }

        public final DoubleProperty miterLimitProperty() {
            if (miterLimit == null) {
                miterLimit = new StyleableDoubleProperty(
                        DEFAULT_STROKE_MITER_LIMIT) {
                    @Override
                    public void invalidated() {
                        StrokeAttributes.this.invalidated(
                                StyleableProperties.STROKE_MITER_LIMIT);
                    }

                    @Override
                    public CssMetaData<MappedShape,Number> getCssMetaData() {
                        return StyleableProperties.STROKE_MITER_LIMIT;
                    }

                    @Override
                    public Object getBean() {
                        return MappedShape.this;
                    }

                    @Override
                    public String getName() {
                        return "strokeMiterLimit";
                    }
                };
            }

            return miterLimit;
        }

        public double getDashOffset() {
            return (dashOffset == null) ? DEFAULT_STROKE_DASH_OFFSET
                    : dashOffset.get();
        }

        public final DoubleProperty dashOffsetProperty() {
            if (dashOffset == null) {
                dashOffset = new StyleableDoubleProperty(
                        DEFAULT_STROKE_DASH_OFFSET) {

                    @Override
                    public void invalidated() {
                        StrokeAttributes.this.invalidated(
                                StyleableProperties.STROKE_DASH_OFFSET);
                    }

                    @Override
                    public CssMetaData<MappedShape,Number> getCssMetaData() {
                        return StyleableProperties.STROKE_DASH_OFFSET;
                    }

                    @Override
                    public Object getBean() {
                        return MappedShape.this;
                    }

                    @Override
                    public String getName() {
                        return "strokeDashOffset";
                    }
                };
            }

            return dashOffset;
        }

        // TODO: Need to handle set from css - should clear array and add all.
        public ObservableList<Double> dashArrayProperty() {
            if (dashArray == null) {
                dashArray = new TrackableObservableList<Double>() {
                    @Override
                    protected void onChanged(Change<Double> c) {
                        StrokeAttributes.this.invalidated(
                                StyleableProperties.STROKE_DASH_ARRAY);
                    }
                };
            }
            return dashArray;
        }

        private ObjectProperty<Number[]> cssDashArray = null;
        private ObjectProperty<Number[]> cssDashArrayProperty() {
            if (cssDashArray == null) {
                cssDashArray = new StyleableObjectProperty<Number[]>()
                {

                    @Override
                    public void set(Number[] v) {

                        ObservableList<Double> list = dashArrayProperty();
                        list.clear();
                        if (v != null && v.length > 0) {
                            for (int n=0; n<v.length; n++) {
                                list.add(v[n].doubleValue());
                            }
                        }

                        // no need to hold onto the array
                    }

                    @Override
                    public Double[] get() {
                        List<Double> list = dashArrayProperty();
                        return list.toArray(new Double[list.size()]);
                    }

                    @Override
                    public Object getBean() {
                        return MappedShape.this;
                    }

                    @Override
                    public String getName() {
                        return "cssDashArray";
                    }

                    @Override
                    public CssMetaData<MappedShape,Number[]> getCssMetaData() {
                        return StyleableProperties.STROKE_DASH_ARRAY;
                    }
                };
            }

            return cssDashArray;
        }

        public boolean canSetType() {
            return (type == null) || !type.isBound();
        }

        public boolean canSetWidth() {
            return (width == null) || !width.isBound();
        }

        public boolean canSetLineJoin() {
            return (lineJoin == null) || !lineJoin.isBound();
        }

        public boolean canSetLineCap() {
            return (lineCap == null) || !lineCap.isBound();
        }

        public boolean canSetMiterLimit() {
            return (miterLimit == null) || !miterLimit.isBound();
        }

        public boolean canSetDashOffset() {
            return (dashOffset == null) || !dashOffset.isBound();
        }

        public boolean hasDashArray() {
            return (dashArray != null);
        }

        private void invalidated(final CssMetaData<MappedShape, ?> propertyCssKey) {
            MappedNodeHelper.markDirty(MappedShape.this, DirtyBits.SHAPE_STROKEATTRS);
            strokeAttributesDirty = true;
            if (propertyCssKey != StyleableProperties.STROKE_DASH_OFFSET) {
                // all stroke attributes change geometry except for the
                // stroke dash offset
                MappedNodeHelper.geomChanged(MappedShape.this);
            }
        }
    }

    // PENDING_DOC_REVIEW
    
    public static MappedShape union(final MappedShape shape1, final MappedShape shape2) {
        final MappedArea result = shape1.getTransformedArea();
        result.add(shape2.getTransformedArea());
        return createFromGeomShape(result);
    }

    // PENDING_DOC_REVIEW
    
    public static MappedShape subtract(final MappedShape shape1, final MappedShape shape2) {
        final MappedArea result = shape1.getTransformedArea();
        result.subtract(shape2.getTransformedArea());
        return createFromGeomShape(result);
    }

    // PENDING_DOC_REVIEW
    
    public static MappedShape intersect(final MappedShape shape1, final MappedShape shape2) {
        final MappedArea result = shape1.getTransformedArea();
        result.intersect(shape2.getTransformedArea());
        return createFromGeomShape(result);
    }

    private MappedArea getTransformedArea() {
        return getTransformedArea(calculateNodeToSceneTransform(this));
    }

    private MappedArea getTransformedArea(final BaseTransform transform) {
        if (mode == NGShape.Mode.EMPTY) {
            return new MappedArea();
        }

        final Shape fillShape = MappedShapeHelper.configShape(this);
        if ((mode == NGShape.Mode.FILL)
                || (mode == NGShape.Mode.STROKE_FILL)
                && (getStrokeType() == StrokeType.INSIDE)) {
            return createTransformedArea(fillShape, transform);
        }

        final StrokeType strokeType = getStrokeType();
        final double strokeWidth =
                Utils.clampMin(getStrokeWidth(), MIN_STROKE_WIDTH);
        final StrokeLineCap strokeLineCap = getStrokeLineCap();
        final StrokeLineJoin strokeLineJoin = convertLineJoin(getStrokeLineJoin());
        final float strokeMiterLimit =
                (float) Utils.clampMin(getStrokeMiterLimit(),
                        MIN_STROKE_MITER_LIMIT);
        final float[] dashArray =
                (hasStrokeDashArray())
                        ? toPGDashArray(getStrokeDashArray())
                        : DEFAULT_PG_STROKE_DASH_ARRAY;

        final Shape strokeShape =
                MappedToolkit.getToolkit().createStrokedShape(
                        fillShape, strokeType, strokeWidth, strokeLineCap,
                        strokeLineJoin, strokeMiterLimit,
                        dashArray, (float) getStrokeDashOffset());

        if (mode == NGShape.Mode.STROKE) {
            return createTransformedArea(strokeShape, transform);
        }

        // fill and stroke
        final MappedArea combinedArea = new MappedArea(fillShape);
        combinedArea.add(new MappedArea(strokeShape));

        return createTransformedArea(combinedArea, transform);
    }

    private static BaseTransform calculateNodeToSceneTransform(MappedNode node) {
        final Affine3D cumulativeTransformation = new Affine3D();

        do {
            cumulativeTransformation.preConcatenate(
                    MappedNodeHelper.getLeafTransform(node));
            node = node.getParent();
        } while (node != null);

        return cumulativeTransformation;
    }

    private static MappedArea createTransformedArea(
            final Shape geomShape,
            final BaseTransform transform) {
        return transform.isIdentity()
                ? new MappedArea(geomShape)
                : new MappedArea(geomShape.getPathIterator(transform));
    }

    private static MappedPath createFromGeomShape(
            final Shape geomShape) {
        final MappedPath path = new MappedPath();
        final ObservableList<MappedPathElement> elements = path.getElements();

        final PathIterator iterator = geomShape.getPathIterator(null);
        final float coords[] = new float[6];

        while (!iterator.isDone()) {
            final int segmentType = iterator.currentSegment(coords);
            switch (segmentType) {
                case PathIterator.SEG_MOVETO:
                    elements.add(new MoveTo(coords[0], coords[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    elements.add(new LineTo(coords[0], coords[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    elements.add(new QuadCurveTo(coords[0], coords[1],
                            coords[2], coords[3]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    elements.add(new CubicCurveTo(coords[0], coords[1],
                            coords[2], coords[3],
                            coords[4], coords[5]));
                    break;
                case PathIterator.SEG_CLOSE:
                    elements.add(new ClosePath());
                    break;
            }

            iterator.next();
        }

        path.setFillRule((iterator.getWindingRule()
                == PathIterator.WIND_EVEN_ODD)
                ? FillRule.EVEN_ODD
                : FillRule.NON_ZERO);

        path.setFill(Color.BLACK);
        path.setStroke(null);

        return path;
    }
}

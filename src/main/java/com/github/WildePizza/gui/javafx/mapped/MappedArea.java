package com.github.WildePizza.gui.javafx.mapped;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.sun.javafx.geom.*;
import com.sun.javafx.geom.transform.BaseTransform;



public class MappedArea extends Shape {

    private static final Vector EmptyCurves = new Vector();

    private Vector curves;

    
    public MappedArea() {
        curves = EmptyCurves;
    }

    
    public MappedArea(Shape s) {
        if (s instanceof MappedArea) {
            curves = ((MappedArea) s).curves;
        } else {
            curves = pathToCurves(s.getPathIterator(null));
        }
    }

    public MappedArea(PathIterator iter) {
        curves = pathToCurves(iter);
    }

    private static Vector pathToCurves(PathIterator pi) {
        Vector curves = new Vector();
        int windingRule = pi.getWindingRule();
        // coords array is big enough for holding:
        //     coordinates returned from currentSegment (6)
        //     OR
        //         two subdivided quadratic curves (2+4+4=10)
        //         AND
        //             0-1 horizontal splitting parameters
        //             OR
        //             2 parametric equation derivative coefficients
        //     OR
        //         three subdivided cubic curves (2+6+6+6=20)
        //         AND
        //             0-2 horizontal splitting parameters
        //             OR
        //             3 parametric equation derivative coefficients
        float coords[] = new float[6];
        double tmp[] = new double[23];
        double movx = 0, movy = 0;
        double curx = 0, cury = 0;
        double newx, newy;
        while (!pi.isDone()) {
            switch (pi.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    Curve.insertLine(curves, curx, cury, movx, movy);
                    curx = movx = coords[0];
                    cury = movy = coords[1];
                    Curve.insertMove(curves, movx, movy);
                    break;
                case PathIterator.SEG_LINETO:
                    newx = coords[0];
                    newy = coords[1];
                    Curve.insertLine(curves, curx, cury, newx, newy);
                    curx = newx;
                    cury = newy;
                    break;
                case PathIterator.SEG_QUADTO:
                    newx = coords[2];
                    newy = coords[3];
                    Curve.insertQuad(curves, tmp,
                            curx, cury,
                            coords[0], coords[1],
                            coords[2], coords[3]);
                    curx = newx;
                    cury = newy;
                    break;
                case PathIterator.SEG_CUBICTO:
                    newx = coords[4];
                    newy = coords[5];
                    Curve.insertCubic(curves, tmp,
                            curx, cury,
                            coords[0], coords[1],
                            coords[2], coords[3],
                            coords[4], coords[5]);
                    curx = newx;
                    cury = newy;
                    break;
                case PathIterator.SEG_CLOSE:
                    Curve.insertLine(curves, curx, cury, movx, movy);
                    curx = movx;
                    cury = movy;
                    break;
            }
            pi.next();
        }
        Curve.insertLine(curves, curx, cury, movx, movy);
        AreaOp operator;
        if (windingRule == PathIterator.WIND_EVEN_ODD) {
            operator = new AreaOp.EOWindOp();
        } else {
            operator = new AreaOp.NZWindOp();
        }
        return operator.calculate(curves, EmptyCurves);
    }

    
    public void add(MappedArea rhs) {
        curves = new AreaOp.AddOp().calculate(this.curves, rhs.curves);
        invalidateBounds();
    }

    
    public void subtract(MappedArea rhs) {
        curves = new AreaOp.SubOp().calculate(this.curves, rhs.curves);
        invalidateBounds();
    }

    
    public void intersect(MappedArea rhs) {
        curves = new AreaOp.IntOp().calculate(this.curves, rhs.curves);
        invalidateBounds();
    }

    
    public void exclusiveOr(MappedArea rhs) {
        curves = new AreaOp.XorOp().calculate(this.curves, rhs.curves);
        invalidateBounds();
    }

    
    public void reset() {
        curves = new Vector();
        invalidateBounds();
    }

    
    public boolean isEmpty() {
        return (curves.size() == 0);
    }

    
    public boolean isPolygonal() {
        Enumeration enum_ = curves.elements();
        while (enum_.hasMoreElements()) {
            if (((Curve) enum_.nextElement()).getOrder() > 1) {
                return false;
            }
        }
        return true;
    }

    
    public boolean isRectangular() {
        int size = curves.size();
        if (size == 0) {
            return true;
        }
        if (size > 3) {
            return false;
        }
        Curve c1 = (Curve) curves.get(1);
        Curve c2 = (Curve) curves.get(2);
        if (c1.getOrder() != 1 || c2.getOrder() != 1) {
            return false;
        }
        if (c1.getXTop() != c1.getXBot() || c2.getXTop() != c2.getXBot()) {
            return false;
        }
        if (c1.getYTop() != c2.getYTop() || c1.getYBot() != c2.getYBot()) {
            // One might be able to prove that this is impossible...
            return false;
        }
        return true;
    }

    
    public boolean isSingular() {
        if (curves.size() < 3) {
            return true;
        }
        Enumeration enum_ = curves.elements();
        enum_.nextElement(); // First Order0 "moveto"
        while (enum_.hasMoreElements()) {
            if (((Curve) enum_.nextElement()).getOrder() == 0) {
                return false;
            }
        }
        return true;
    }

    private RectBounds cachedBounds;
    private void invalidateBounds() {
        cachedBounds = null;
    }
    private RectBounds getCachedBounds() {
        if (cachedBounds != null) {
            return cachedBounds;
        }
        RectBounds r = new RectBounds();
        if (curves.size() > 0) {
            Curve c = (Curve) curves.get(0);
            // First point is always an order 0 curve (moveto)
            r.setBounds((float) c.getX0(), (float) c.getY0(), 0, 0);
            for (int i = 1; i < curves.size(); i++) {
                ((Curve) curves.get(i)).enlarge(r);
            }
        }
        return (cachedBounds = r);
    }

    
    public RectBounds getBounds() {
        return new RectBounds(getCachedBounds());
    }

    
    public boolean isEquivalent(MappedArea other) {
        // REMIND: A *much* simpler operation should be possible...
        // Should be able to do a curve-wise comparison since all Areas
        // should evaluate their curves in the same top-down order.
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        Vector c = new AreaOp.XorOp().calculate(this.curves, other.curves);
        return c.isEmpty();
    }

    
    public void transform(BaseTransform tx) {
        if (tx == null) {
            throw new NullPointerException("transform must not be null");
        }
        // REMIND: A simpler operation can be performed for some types
        // of transform.
        curves = pathToCurves(getPathIterator(tx));
        invalidateBounds();
    }

    
    public MappedArea createTransformedArea(BaseTransform tx) {
        MappedArea a = new MappedArea(this);
        a.transform(tx);
        return a;
    }

    
    public boolean contains(float x, float y) {
        if (!getCachedBounds().contains(x, y)) {
            return false;
        }
        Enumeration enum_ = curves.elements();
        int crossings = 0;
        while (enum_.hasMoreElements()) {
            Curve c = (Curve) enum_.nextElement();
            crossings += c.crossingsFor(x, y);
        }
        return ((crossings & 1) == 1);
    }

    
    @Override
    public boolean contains(Point2D p) {
        return contains(p.x, p.y);
    }

    
    public boolean contains(float x, float y, float w, float h) {
        if (w < 0 || h < 0) {
            return false;
        }
        if (!getCachedBounds().contains(x, y) || !getCachedBounds().contains(x+w, y+h)) {
            return false;
        }
        Crossings c = Crossings.findCrossings(curves, x, y, x+w, y+h);
        return (c != null && c.covers(y, y+h));
    }

    
    public boolean intersects(float x, float y, float w, float h) {
        if (w < 0 || h < 0) {
            return false;
        }
        if (!getCachedBounds().intersects(x, y, w, h)) {
            return false;
        }
        Crossings c = Crossings.findCrossings(curves, x, y, x+w, y+h);
        return (c == null || !c.isEmpty());
    }

    
    public PathIterator getPathIterator(BaseTransform tx) {
        return new AreaIterator(curves, tx);
    }

    
    public PathIterator getPathIterator(BaseTransform tx, float flatness) {
        return new FlatteningPathIterator(getPathIterator(tx), flatness);
    }

    @Override
    public MappedArea copy() {
        return new MappedArea(this);
    }
}

class AreaIterator implements PathIterator {
    private BaseTransform transform;
    private Vector curves;
    private int index;
    private Curve prevcurve;
    private Curve thiscurve;

    public AreaIterator(Vector curves, BaseTransform tx) {
        this.curves = curves;
        this.transform = tx;
        if (curves.size() >= 1) {
            thiscurve = (Curve) curves.get(0);
        }
    }

    public int getWindingRule() {
        // REMIND: Which is better, EVEN_ODD or NON_ZERO?
        //         The paths calculated could be classified either way.
        //return WIND_EVEN_ODD;
        return WIND_NON_ZERO;
    }

    public boolean isDone() {
        return (prevcurve == null && thiscurve == null);
    }

    public void next() {
        if (prevcurve != null) {
            prevcurve = null;
        } else {
            prevcurve = thiscurve;
            index++;
            if (index < curves.size()) {
                thiscurve = (Curve) curves.get(index);
                if (thiscurve.getOrder() != 0 &&
                        prevcurve.getX1() == thiscurve.getX0() &&
                        prevcurve.getY1() == thiscurve.getY0())
                {
                    prevcurve = null;
                }
            } else {
                thiscurve = null;
            }
        }
    }

    public int currentSegment(float coords[]) {
        int segtype;
        int numpoints;
        if (prevcurve != null) {
            // Need to finish off junction between curves
            if (thiscurve == null || thiscurve.getOrder() == 0) {
                return SEG_CLOSE;
            }
            coords[0] = (float) thiscurve.getX0();
            coords[1] = (float) thiscurve.getY0();
            segtype = SEG_LINETO;
            numpoints = 1;
        } else if (thiscurve == null) {
            throw new NoSuchElementException("area iterator out of bounds");
        } else {
            segtype = thiscurve.getSegment(coords);
            numpoints = thiscurve.getOrder();
            if (numpoints == 0) {
                numpoints = 1;
            }
        }
        if (transform != null) {
            transform.transform(coords, 0, coords, 0, numpoints);
        }
        return segtype;
    }
}

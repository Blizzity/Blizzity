package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.RectangularShape;
import com.sun.javafx.geom.transform.BaseTransform;


public class MappedRoundRectangle2D extends RectangularShape {
    
    public float x;

    
    public float y;

    
    public float width;

    
    public float height;

    
    public float arcWidth;

    
    public float arcHeight;

    
    public MappedRoundRectangle2D() {
    }

    
    public MappedRoundRectangle2D(float x, float y, float w, float h, float arcw, float arch) {
        setRoundRect(x, y, w, h, arcw, arch);
    }

    
    @Override
    public float getX() {
        return x;
    }

    
    @Override
    public float getY() {
        return y;
    }

    
    @Override
    public float getWidth() {
        return width;
    }

    
    @Override
    public float getHeight() {
        return height;
    }

    
    @Override
    public boolean isEmpty() {
        return (width <= 0.0f) || (height <= 0.0f);
    }

    
    public void setRoundRect(float x, float y, float w, float h,
                             float arcw, float arch)
    {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.arcWidth = arcw;
        this.arcHeight = arch;
    }

    
    @Override
    public RectBounds getBounds() {
        return new RectBounds(x, y, x + width, y + height);
    }

    
    public void setRoundRect(MappedRoundRectangle2D rr) {
        setRoundRect(rr.x, rr.y, rr.width, rr.height, rr.arcWidth, rr.arcHeight);
    }

    
    @Override
    public void setFrame(float x, float y, float w, float h) {
        setRoundRect(x, y, w, h, this.arcWidth, this.arcHeight);
    }

    
    @Override
    public boolean contains(float x, float y) {
        if (isEmpty()) { return false; }
        float rrx0 = this.x;
        float rry0 = this.y;
        float rrx1 = rrx0 + this.width;
        float rry1 = rry0 + this.height;
        // Check for trivial rejection - point is outside bounding rectangle
        if (x < rrx0 || y < rry0 || x >= rrx1 || y >= rry1) {
            return false;
        }
        float aw = Math.min(this.width, Math.abs(this.arcWidth)) / 2f;
        float ah = Math.min(this.height, Math.abs(this.arcHeight)) / 2f;
        // Check which corner point is in and do circular containment
        // test - otherwise simple acceptance
        if (x >= (rrx0 += aw) && x < (rrx0 = rrx1 - aw)) {
            return true;
        }
        if (y >= (rry0 += ah) && y < (rry0 = rry1 - ah)) {
            return true;
        }
        x = (x - rrx0) / aw;
        y = (y - rry0) / ah;
        return (x * x + y * y <= 1.0);
    }

    private int classify(float coord, float left, float right, float arcsize) {
        if (coord < left) {
            return 0;
        } else if (coord < left + arcsize) {
            return 1;
        } else if (coord < right - arcsize) {
            return 2;
        } else if (coord < right) {
            return 3;
        } else {
            return 4;
        }
    }

    
    @Override
    public boolean intersects(float x, float y, float w, float h) {
        if (isEmpty() || w <= 0 || h <= 0) {
            return false;
        }
        float rrx0 = this.x;
        float rry0 = this.y;
        float rrx1 = rrx0 + this.width;
        float rry1 = rry0 + this.height;
        // Check for trivial rejection - bounding rectangles do not intersect
        if (x + w <= rrx0 || x >= rrx1 || y + h <= rry0 || y >= rry1) {
            return false;
        }
        float aw = Math.min(this.width, Math.abs(this.arcWidth)) / 2f;
        float ah = Math.min(this.height, Math.abs(this.arcHeight)) / 2f;
        int x0class = classify(x, rrx0, rrx1, aw);
        int x1class = classify(x + w, rrx0, rrx1, aw);
        int y0class = classify(y, rry0, rry1, ah);
        int y1class = classify(y + h, rry0, rry1, ah);
        // Trivially accept if any point is inside inner rectangle
        if (x0class == 2 || x1class == 2 || y0class == 2 || y1class == 2) {
            return true;
        }
        // Trivially accept if either edge spans inner rectangle
        if ((x0class < 2 && x1class > 2) || (y0class < 2 && y1class > 2)) {
            return true;
        }
        // Since neither edge spans the center, then one of the corners
        // must be in one of the rounded edges.  We detect this case if
        // a [xy]0class is 3 or a [xy]1class is 1.  One of those two cases
        // must be true for each direction.
        // We now find a "nearest point" to test for being inside a rounded
        // corner.
        x = (x1class == 1) ? (x = x + w - (rrx0 + aw)) : (x = x - (rrx1 - aw));
        y = (y1class == 1) ? (y = y + h - (rry0 + ah)) : (y = y - (rry1 - ah));
        x = x / aw;
        y = y / ah;
        return (x * x + y * y <= 1f);
    }

    
    @Override
    public boolean contains(float x, float y, float w, float h) {
        if (isEmpty() || w <= 0 || h <= 0) {
            return false;
        }
        return (contains(x, y) &&
                contains(x + w, y) &&
                contains(x, y + h) &&
                contains(x + w, y + h));
    }

    
    @Override
    public PathIterator getPathIterator(BaseTransform tx) {
        return new MappedRoundRectIterator(this, tx);
    }

    @Override
    public MappedRoundRectangle2D copy() {
        return new MappedRoundRectangle2D(x, y, width, height, arcWidth, arcHeight);
    }

    
    @Override
    public int hashCode() {
        int bits = java.lang.Float.floatToIntBits(x);
        bits += java.lang.Float.floatToIntBits(y) * 37;
        bits += java.lang.Float.floatToIntBits(width) * 43;
        bits += java.lang.Float.floatToIntBits(height) * 47;
        bits += java.lang.Float.floatToIntBits(arcWidth) * 53;
        bits += java.lang.Float.floatToIntBits(arcHeight) * 59;
        return bits;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof MappedRoundRectangle2D) {
            MappedRoundRectangle2D rr2d = (MappedRoundRectangle2D) obj;
            return ((x == rr2d.x) &&
                    (y == rr2d.y) &&
                    (width == rr2d.width) &&
                    (height == rr2d.height) &&
                    (arcWidth == rr2d.arcWidth) &&
                    (arcHeight == rr2d.arcHeight));
        }
        return false;
    }
}

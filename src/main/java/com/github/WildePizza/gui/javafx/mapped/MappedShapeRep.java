/*
 * Copyright (c) 2009, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.BaseBounds;
import com.sun.prism.MappedGraphics;

public interface MappedShapeRep {
    public enum InvalidationType {
        
        LOCATION,
        
        LOCATION_AND_GEOMETRY
        // NOTE: potentially have more specific (stroke/shape changed), at which
        // point we might as well use bits
    }

    public boolean is3DCapable();
    public void invalidate(InvalidationType type);
    public void fill(MappedGraphics g, Shape shape, BaseBounds bounds);
    public void draw(MappedGraphics g, Shape shape, BaseBounds bounds);
    public void dispose();
}

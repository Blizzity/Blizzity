package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.collections.VetoableListDecorator;
import com.sun.javafx.css.MappedStyleManager;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.javafx.scene.*;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.stage.WindowHelper;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.TempState;
import com.sun.javafx.util.Utils;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.stage.MappedWindow;

import java.util.*;

public class MappedParent extends MappedNode {
    static final int DIRTY_CHILDREN_THRESHOLD = 10;
    private static final boolean warnOnAutoMove = PropertyHelper.getBooleanProperty("javafx.sg.warn");
    private static final int REMOVED_CHILDREN_THRESHOLD = 20;
    private boolean removedChildrenOptimizationDisabled = false;
    Map<String, MappedNode> children = new HashMap<>();

    static {
        MappedParentHelper.setParentAccessor(new MappedParentHelper.ParentAccessor() {
            @Override
            public MappedNGNode doCreatePeer(MappedNode node) {
                return ((MappedParent) node).doCreatePeer();
            }

            @Override
            public void doUpdatePeer(MappedNode node) {
                ((MappedParent) node).doUpdatePeer();
            }

            @Override
            public BaseBounds doComputeGeomBounds(MappedNode node,
                                                  BaseBounds bounds, BaseTransform tx) {
                return ((MappedParent) node).doComputeGeomBounds(bounds, tx);
            }

            @Override
            public boolean doComputeContains(MappedNode node, double localX, double localY) {
                return ((MappedParent) node).doComputeContains(localX, localY);
            }

            @Override
            public void doProcessCSS(MappedNode node) {
                ((MappedParent) node).doProcessCSS();
            }

            @Override
            public void doPickNodeLocal(MappedNode node, PickRay localPickRay,
                                        PickResultChooser result) {
                ((MappedParent) node).doPickNodeLocal(localPickRay, result);
            }

            @Override
            public boolean pickChildrenNode(MappedParent parent, PickRay pickRay, PickResultChooser result) {
                return parent.pickChildrenNode(pickRay, result);
            }

            @Override
            public void setTraversalEngine(MappedParent parent, ParentTraversalEngine value) {
                parent.setTraversalEngine(value);
            }

            @Override
            public ParentTraversalEngine getTraversalEngine(MappedParent parent) {
                return parent.getTraversalEngine();
            }

            @Override
            public List<String> doGetAllParentStylesheets(MappedParent parent) {
                return parent.doGetAllParentStylesheets();
            }
        });
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private void doUpdatePeer() {
        final MappedNGGroup peer = getPeer();

        if (Utils.assertionEnabled()) {
            List<MappedNGNode> pgnodes = peer.getChildren();
            if (pgnodes.size() != pgChildrenSize) {
                java.lang.System.err.println("*** pgnodes.size() [" + pgnodes.size() + "] != pgChildrenSize [" + pgChildrenSize + "]");
            }
        }

        if (isDirty(DirtyBits.PARENT_CHILDREN)) {
            // Whether a permutation, or children having been added or
            // removed, we'll want to clear out the PG side starting
            // from startIdx. We know that everything up to but not
            // including startIdx is identical between the FX and PG
            // sides, so we only need to update the remaining portion.
            peer.clearFrom(startIdx);
            for (int idx = startIdx; idx < children.size(); idx++) {
                peer.add(idx, children.get(idx).getPeer());
            }
            if (removedChildrenOptimizationDisabled) {
                peer.markDirty();
                removedChildrenOptimizationDisabled = false;
            } else {
                if (removed != null && !removed.isEmpty()) {
                    for(int i = 0; i < removed.size(); i++) {
                        peer.addToRemoved(removed.get(i).getPeer());
                    }
                }
            }
            if (removed != null) {
                removed.clear();
            }
            pgChildrenSize = children.size();
            startIdx = pgChildrenSize;
        }

        if (isDirty(DirtyBits.PARENT_CHILDREN_VIEW_ORDER)) {
            computeViewOrderChildren();
            peer.setViewOrderChildren(viewOrderChildren);
        }

        if (Utils.assertionEnabled()) validatePG();
    }


    /* *********************************************************************
     *                        Scenegraph Structure                         *
     *                                                                     *
     *  Functions and variables related to the scenegraph structure,       *
     *  modifying the structure, and walking the structure.                *
     *                                                                     *
     **********************************************************************/

    // Used to check for duplicate nodes
    private final Set<MappedNode> childSet = new HashSet<MappedNode>();

    // starting child index from which we need to send the children to the PGGroup
    private int startIdx = 0;

    // double of children in the PGGroup as of the last update
    private int pgChildrenSize = 0;

    void validatePG() {
        boolean assertionFailed = false;
        final MappedNGGroup peer = getPeer();
        List<MappedNGNode> pgnodes = peer.getChildren();
        if (pgnodes.size() != children.size()) {
            java.lang.System.err.println("*** pgnodes.size validatePG() [" + pgnodes.size() + "] != children.size() [" + children.size() + "]");
            assertionFailed = true;
        } else {
            for (int idx = 0; idx < children.size(); idx++) {
                MappedNode n = children.get(idx);
                if (n.getParent() != this) {
                    java.lang.System.err.println("*** this=" + this + " validatePG children[" + idx + "].parent= " + n.getParent());
                    assertionFailed = true;
                }
                if (n.getPeer() != pgnodes.get(idx)) {
                    java.lang.System.err.println("*** pgnodes[" + idx + "] validatePG != children[" + idx + "]");
                    assertionFailed = true;
                }
            }
        }
        if (assertionFailed) {
            throw new java.lang.AssertionError("validation of PGGroup children failed");
        }

    }

    void printSeq(String prefix, List<MappedNode> nodes) {
        String str = prefix;
        for (MappedNode nn : nodes) {
            str += nn + " ";
        }
        System.out.println(str);
    }

    
    private final List<MappedNode> viewOrderChildren = new ArrayList(1);

    void markViewOrderChildrenDirty() {
        viewOrderChildren.clear();
        MappedNodeHelper.markDirty(this, DirtyBits.PARENT_CHILDREN_VIEW_ORDER);
    }

    private void computeViewOrderChildren() {
        boolean viewOrderSet = false;
        for (MappedNode child : children.values()) {
            double vo = child.getViewOrder();

            if (!viewOrderSet && vo != 0) {
                viewOrderSet = true;
            }
        }

        viewOrderChildren.clear();
        if (viewOrderSet) {
            viewOrderChildren.addAll(children.values());

            // Sort in descending order (or big-to-small order)
            Collections.sort(viewOrderChildren, (MappedNode a, MappedNode b)
                    -> a.getViewOrder() < b.getViewOrder() ? 1
                    : a.getViewOrder() == b.getViewOrder() ? 0 : -1);
        }
    }

    // Call this method if children view order is needed for picking.
    // The returned list should be treated as read only.
    private List<MappedNode> getOrderedChildren() {
        if (isDirty(DirtyBits.PARENT_CHILDREN_VIEW_ORDER)) {
            //Fix for JDK-8205092
            computeViewOrderChildren();
        }
        if (!viewOrderChildren.isEmpty()) {
            return viewOrderChildren;
        }
        return new ArrayList<>(children.values());
    }

    // Variable used to indicate that the change to the children ObservableList is
    // a simple permutation as the result of a toFront or toBack operation.
    // We can avoid almost all of the processing of the on replace trigger in
    // this case.
    private boolean childrenTriggerPermutation = false;

    //accumulates all removed nodes between pulses, for dirty area calculation.
    private List<MappedNode> removed;

    // set to true if either childRemoved or childAdded returns
    // true. These functions will indicate whether the geom
    // bounds for the parent have changed
    private boolean geomChanged;
    private boolean childSetModified;
    private final ObservableList<MappedNode> children = new VetoableListDecorator<MappedNode>(new TrackableObservableList<MappedNode>() {


        protected void onChanged(ListChangeListener.Change<MappedNode> c) {
            // proceed with updating the scene graph
            unmodifiableManagedChildren = null;
            boolean relayout = false;
            boolean viewOrderChildrenDirty = false;

            if (childSetModified) {
                while (c.next()) {
                    int from = c.getFrom();
                    int to = c.getTo();
                    for (int i = from; i < to; ++i) {
                        MappedNode n = children.get(i);
                        if (n.getParent() != null && n.getParent() != MappedParent.this) {
                            if (warnOnAutoMove) {
                                java.lang.System.err.println("WARNING added to a new parent without first removing it from its current");
                                java.lang.System.err.println("    parent. It will be automatically removed from its current parent.");
                                java.lang.System.err.println("    node=" + n + " oldparent= " + n.getParent() + " newparent=" + this);
                            }
                            n.getParent().children.remove(n);
                            if (warnOnAutoMove) {
                                Thread.dumpStack();
                            }
                        }
                    }

                    List<MappedNode> removed = c.getRemoved();
                    int removedSize = removed.size();
                    for (int i = 0; i < removedSize; ++i) {
                        final MappedNode n = removed.get(i);
                        if (n.isManaged()) {
                            relayout = true;
                        }
                    }

                    // Mark viewOrderChildrenDirty if there is modification to children list
                    // and view order was set on one or more of the children prior to this change
                    if (((removedSize > 0) || (to - from) > 0) && !viewOrderChildren.isEmpty()) {
                        viewOrderChildrenDirty = true;
                    }
                    // update the parent and scene for each new node
                    for (int i = from; i < to; ++i) {
                        MappedNode node = children.get(i);

                        // Newly added node has view order set.
                        if (node.getViewOrder() != 0) {
                            viewOrderChildrenDirty = true;
                        }
                        if (node.isManaged() || (node instanceof MappedParent && ((MappedParent) node).layoutFlag != LayoutFlags.CLEAN)) {
                            relayout = true;
                        }
                        node.setParent(MappedParent.this);
                        node.setScenes(getScene(), getSubScene());
                        // assert !node.boundsChanged;
                        if (node.isVisible()) {
                            geomChanged = true;
                            childIncluded(node);
                        }
                    }
                }

                // check to see if the number of children exceeds
                // DIRTY_CHILDREN_THRESHOLD and dirtyChildren is null.
                // If so, then we need to create dirtyChildren and
                // populate it.
                if (dirtyChildren == null && children.size() > DIRTY_CHILDREN_THRESHOLD) {
                    dirtyChildren
                            = new ArrayList<MappedNode>(2 * DIRTY_CHILDREN_THRESHOLD);
                    // only bother populating children if geom has
                    // changed, otherwise there is no need
                    if (dirtyChildrenCount > 0) {
                        int size = children.size();
                        for (int i = 0; i < size; ++i) {
                            MappedNode ch = children.get(i);
                            if (ch.isVisible() && ch.boundsChanged) {
                                dirtyChildren.add(ch);
                            }
                        }
                    }
                }
            } else {
                // If childSet was not modified, we still need to check whether the permutation
                // did change the layout
                layout_loop:while (c.next()) {
                    List<MappedNode> removed = c.getRemoved();
                    for (int i = 0, removedSize = removed.size(); i < removedSize; ++i) {
                        if (removed.get(i).isManaged()) {
                            relayout = true;
                            break layout_loop;
                        }
                    }

                    for (int i = c.getFrom(), to = c.getTo(); i < to; ++i) {
                        if (children.get(i).isManaged()) {
                            relayout = true;
                            break layout_loop;
                        }
                    }
                }
            }


            //
            // Note that the styles of a child do not affect the parent or
            // its siblings. Thus, it is only necessary to reapply css to
            // the MappedNode just added and not to this parent and all of its
            // children. So the following call to reapplyCSS was moved
            // to MappedNode.parentProperty. The original comment and code were
            // purposely left here as documentation should there be any
            // question about how the code used to work and why the change
            // was made.
            //
            // if children have changed then I need to reapply
            // CSS from this node on down
//                reapplyCSS();
            //

            // request layout if a Group subclass has overridden doLayout OR
            // if one of the new children needs layout, in which case need to ensure
            // the needsLayout flag is set all the way to the root so the next layout
            // pass will reach the child.
            if (relayout) {
                requestLayout();
            }

            if (geomChanged) {
                MappedNodeHelper.geomChanged(MappedParent.this);
            }

            // Note the starting index at which we need to update the
            // PGGroup on the next update, and mark the children dirty
            c.reset();
            c.next();
            if (startIdx > c.getFrom()) {
                startIdx = c.getFrom();
            }

            MappedNodeHelper.markDirty(MappedParent.this, DirtyBits.PARENT_CHILDREN);
            // Force synchronization to include the handling of invisible node
            // so that removed list will get cleanup to prevent memory leak.
            MappedNodeHelper.markDirty(MappedParent.this, DirtyBits.NODE_FORCE_SYNC);

            if (viewOrderChildrenDirty) {
                markViewOrderChildrenDirty();
            }
        }

    }) {
        @Override
        protected void onProposedChange(final List<MappedNode> newNodes, int[] toBeRemoved) {
            final MappedScene scene = getScene();
            if (scene != null) {
                MappedWindow w = scene.getWindow();
                if (w != null && WindowHelper.getPeer(w) != null) {
                    Toolkit.getToolkit().checkFxUserThread();
                }
            }
            geomChanged = false;

            long newLength = children.size() + newNodes.size();
            int removedLength = 0;
            for (int i = 0; i < toBeRemoved.length; i += 2) {
                removedLength += toBeRemoved[i + 1] - toBeRemoved[i];
            }
            newLength -= removedLength;

            // If the childrenTriggerPermutation flag is set, then we know it
            // is a simple permutation and no further checking is needed.
            if (childrenTriggerPermutation) {
                childSetModified = false;
                return;
            }

            // If the childrenTriggerPermutation flag is not set, then we will
            // check to see whether any element in the ObservableList has changed,
            // or whether the new ObservableList is a permutation on the existing
            // ObservableList. Note that even if the childrenModified flag is false,
            // we still have to check for duplicates. If it is a simple
            // permutation, we can avoid checking for cycles or other parents.
            childSetModified = true;
            if (newLength == childSet.size()) {
                childSetModified = false;
                for (int i = newNodes.size() - 1; i >= 0; --i ) {
                    MappedNode n = newNodes.get(i);
                    if (!childSet.contains(n)) {
                        childSetModified = true;
                        break;
                    }
                }
            }

            // Enforce scene graph invariants, and check for structural errors.
            //
            // 1. If a child has been added to this parent more than once,
            // then it is an error
            //
            // 2. If a child is a target of a clip, then it is an error.
            //
            // 3. If a node would cause a cycle, then it is an error.
            //
            // 4. If a node is null
            //
            // Note that if a node is the child of another parent, we will
            // implicitly remove the node from its former MappedParent after first
            // checking for errors.

            // iterate over the nodes that were removed and remove them from
            // the hash set.
            for (int i = 0; i < toBeRemoved.length; i += 2) {
                for (int j = toBeRemoved[i]; j < toBeRemoved[i + 1]; j++) {
                    childSet.remove(children.get(j));
                }
            }

            try {
                if (childSetModified) {
                    // check individual children before duplication test
                    // if done in this order, the exception is more specific
                    for (int i = newNodes.size() - 1; i >= 0; --i ) {
                        MappedNode node = newNodes.get(i);
                        if (node == null) {
                            throw new NullPointerException(
                                    constructExceptionMessage(
                                            "child node is null", null));
                        }
                        if (node.getClipParent() != null) {
                            throw new IllegalArgumentException(
                                    constructExceptionMessage(
                                            "node already used as a clip", node));
                        }
                        if (wouldCreateCycle(MappedParent.this, node)) {
                            throw new IllegalArgumentException(
                                    constructExceptionMessage(
                                            "cycle detected", node));
                        }
                    }
                }

                childSet.addAll(newNodes);
                if (childSet.size() != newLength) {
                    throw new IllegalArgumentException(
                            constructExceptionMessage(
                                    "duplicate children added", null));
                }
            } catch (RuntimeException e) {
                //Return children to it's original state
                childSet.clear();
                childSet.addAll(children.values());

                // rethrow
                throw e;
            }

            // Done with error checking

            if (!childSetModified) {
                return;
            }

            // iterate over the nodes that were removed and clear their
            // parent and scene. Add to them also to removed list for further
            // dirty regions calculation.
            if (removed == null) {
                removed = new ArrayList<MappedNode>();
            }
            if (removed.size() + removedLength > REMOVED_CHILDREN_THRESHOLD || !isTreeVisible()) {
                //do not populate too many children in removed list
                removedChildrenOptimizationDisabled = true;
            }
            for (int i = 0; i < toBeRemoved.length; i += 2) {
                for (int j = toBeRemoved[i]; j < toBeRemoved[i + 1]; j++) {
                    MappedNode old = children.get(j);
                    final MappedScene oldScene = old.getScene();
                    if (oldScene != null) {
                        oldScene.generateMouseExited(old);
                    }
                    if (dirtyChildren != null) {
                        dirtyChildren.remove(old);
                    }
                    if (old.isVisible()) {
                        geomChanged = true;
                        childExcluded(old);
                    }
                    if (old.getParent() == MappedParent.this) {
                        old.setParent(null);
                        old.setScenes(null, null);
                    }
                    // Do not add node with null scene to the removed list.
                    // It will not be processed in the list and its memory
                    // will not be freed.
                    if (scene != null && !removedChildrenOptimizationDisabled) {
                        removed.add(old);
                    }
                }
            }
        }

        private String constructExceptionMessage(
                String cause, MappedNode offendingNode) {
            final StringBuilder sb = new StringBuilder("Children: ");
            sb.append(cause);
            sb.append(": parent = ").append(MappedParent.this);
            if (offendingNode != null) {
                sb.append(", node = ").append(offendingNode);
            }

            return sb.toString();
        }
    };

    
    private final ObservableList<MappedNode> unmodifiableChildren =
            FXCollections.unmodifiableObservableList(children);

    
    private List<MappedNode> unmodifiableManagedChildren = null;

    
    protected ObservableList<MappedNode> getChildren() {
        return children;
    }

    
    public ObservableList<MappedNode> getChildrenUnmodifiable() {
        return unmodifiableChildren;
    }

    
    protected <E extends MappedNode> List<E> getManagedChildren() {
        if (unmodifiableManagedChildren == null) {
            unmodifiableManagedChildren = new ArrayList<MappedNode>();
            for (int i=0, max=children.size(); i<max; i++) {
                MappedNode e = children.get(i);
                if (e.isManaged()) {
                    unmodifiableManagedChildren.add(e);
                }
            }
        }
        return (List<E>)unmodifiableManagedChildren;
    }

    
    final void managedChildChanged() {
        requestLayout();
        unmodifiableManagedChildren = null;
    }

    // implementation of MappedNode.toFront function
    final void toFront(MappedNode node) {
        if (Utils.assertionEnabled()) {
            if (!childSet.contains(node)) {
                throw new java.lang.AssertionError(
                        "specified node is not in the list of children");
            }
        }

        if (children.get(children.size() - 1) != node) {
            childrenTriggerPermutation = true;
            try {
                children.remove(node);
                children.add(node);
            } finally {
                childrenTriggerPermutation = false;
            }
        }
    }

    // implementation of MappedNode.toBack function
    final void toBack(MappedNode node) {
        if (Utils.assertionEnabled()) {
            if (!childSet.contains(node)) {
                throw new java.lang.AssertionError(
                        "specified node is not in the list of children");
            }
        }

        if (children.get(0) != node) {
            childrenTriggerPermutation = true;
            try {
                children.remove(node);
                children.add(0, node);
            } finally {
                childrenTriggerPermutation = false;
            }
        }
    }

    @Override
    void scenesChanged(final MappedScene newScene, final MappedSubScene newSubScene,
                       final MappedScene oldScene, final MappedSubScene oldSubScene) {

        if (oldScene != null && newScene == null) {
            // RT-34863 - clean up CSS cache when MappedParent is removed from scene-graph
            MappedStyleManager.getInstance().forget(this);

            // Clear removed list on parent who is no longer in a scene
            if (removed != null) {
                removed.clear();
            }
        }

        for (int i=0; i<children.size(); i++) {
            children.get(i).setScenes(newScene, newSubScene);
        }

        final boolean awaitingLayout = layoutFlag != LayoutFlags.CLEAN;

        sceneRoot = (newSubScene != null && newSubScene.getRoot() == this) ||
                (newScene != null && newScene.getRoot() == this);
        layoutRoot = !isManaged() || sceneRoot;


        if (awaitingLayout) {
            // If this node is dirty and the new scene or subScene is not null
            // then add this node to the new scene's dirty list
            if (newScene != null && layoutRoot) {
                if (newSubScene != null) {
                    newSubScene.setDirtyLayout(this);
                }
            }
        }
    }

    @Override
    void setDerivedDepthTest(boolean value) {
        super.setDerivedDepthTest(value);

        for (int i=0, max=children.size(); i<max; i++) {
            final MappedNode node = children.get(i);
            node.computeDerivedDepthTest();
        }
    }

    boolean pickChildrenNode(PickRay pickRay, PickResultChooser result) {
        List<MappedNode> orderedChildren = getOrderedChildren();
        for (int i = orderedChildren.size() - 1; i >= 0; i--) {
            orderedChildren.get(i).pickNode(pickRay, result);
            if (result.isClosed()) {
                return false;
            }
        }
        return true;
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private void doPickNodeLocal(PickRay pickRay, PickResultChooser result) {
        double boundsDistance = intersectsBounds(pickRay);

        if (!Double.isNaN(boundsDistance) && pickChildrenNode(pickRay, result)) {
            if (isPickOnBounds()) {
                result.offer(this, boundsDistance, PickResultChooser.computePoint(pickRay, boundsDistance));
            }
        }
    }

    @Override boolean isConnected() {
        return super.isConnected() || sceneRoot;
    }

    @Override public MappedNode lookup(String selector) {
        MappedNode n = super.lookup(selector);
        if (n == null) {
            for (int i=0, max=children.size(); i<max; i++) {
                final MappedNode node = children.get(i);
                n = node.lookup(selector);
                if (n != null) return n;
            }
        }
        return n;
    }

    
    @Override List<MappedNode> lookupAll(MappedSelector selector, List<MappedNode> results) {
        results = super.lookupAll(selector, results);
        for (int i=0, max=children.size(); i<max; i++) {
            final MappedNode node = children.get(i);
            results = node.lookupAll(selector, results);
        }
        return results;
    }

    private ParentTraversalEngine traversalEngine;

    private final void setTraversalEngine(ParentTraversalEngine value) {
        this.traversalEngine = value;
    }

    private final ParentTraversalEngine getTraversalEngine() {
        return traversalEngine;
    }

    /* *********************************************************************
     *                               Layout                                *
     *                                                                     *
     *  Functions and variables related to the layout scheme used by       *
     *  JavaFX. Includes both public and private API.                      *
     *                                                                     *
     **********************************************************************/
    
    private ReadOnlyBooleanWrapper needsLayout;
    LayoutFlags layoutFlag = LayoutFlags.CLEAN;

    protected final void setNeedsLayout(boolean value) {
        if (value) {
            markDirtyLayout(true, false);
        } else if (layoutFlag == LayoutFlags.NEEDS_LAYOUT) {
            boolean hasBranch = false;
            for (int i = 0, max = children.size(); i < max; i++) {
                final MappedNode child = children.get(i);
                if (child instanceof MappedParent) {
                    if (((MappedParent)child).layoutFlag != LayoutFlags.CLEAN) {
                        hasBranch = true;
                        break;
                    }

                }
            }
            setLayoutFlag(hasBranch ? LayoutFlags.DIRTY_BRANCH : LayoutFlags.CLEAN);
        }
    }

    public final boolean isNeedsLayout() {
        return layoutFlag == LayoutFlags.NEEDS_LAYOUT;
    }

    public final ReadOnlyBooleanProperty needsLayoutProperty() {
        if (needsLayout == null) {
            needsLayout = new ReadOnlyBooleanWrapper(this, "needsLayout", layoutFlag == LayoutFlags.NEEDS_LAYOUT);
        }
        return needsLayout;
    }

    
    private boolean performingLayout = false;

    boolean isPerformingLayout() {
        return performingLayout;
    }

    private boolean sizeCacheClear = true;
    private double prefWidthCache = -1;
    private double prefHeightCache = -1;
    private double minWidthCache = -1;
    private double minHeightCache = -1;

    void setLayoutFlag(LayoutFlags flag) {
        if (needsLayout != null) {
            needsLayout.set(flag == LayoutFlags.NEEDS_LAYOUT);
        }
        layoutFlag = flag;
    }

    private void markDirtyLayout(boolean local, boolean forceParentLayout) {
        setLayoutFlag(LayoutFlags.NEEDS_LAYOUT);
        if (local || layoutRoot) {
            if (sceneRoot) {
                Toolkit.getToolkit().requestNextPulse();
                if (getSubScene() != null) {
                    getSubScene().setDirtyLayout(this);
                }
            } else {
                markDirtyLayoutBranch();
            }
        } else {
            requestParentLayout(forceParentLayout);
        }
    }

    
    public void requestLayout() {
        clearSizeCache();
        markDirtyLayout(false, forceParentLayout);
    }

    private boolean forceParentLayout = false;
    
    void requestLayout(boolean forceParentLayout) {
        boolean savedForceParentLayout = this.forceParentLayout;
        this.forceParentLayout = forceParentLayout;
        requestLayout();
        this.forceParentLayout = savedForceParentLayout;
    }

    
    protected final void requestParentLayout() {
        requestParentLayout(false);
    }

    
    void requestParentLayout(boolean forceParentLayout) {
        if (!layoutRoot) {
            final MappedParent p = getParent();
            if (p != null && (!p.performingLayout || forceParentLayout)) {
                p.requestLayout();
            }
        }
    }

    void clearSizeCache() {
        if (sizeCacheClear) {
            return;
        }
        sizeCacheClear = true;
        prefWidthCache = -1;
        prefHeightCache = -1;
        minWidthCache = -1;
        minHeightCache = -1;
    }

    @Override public double prefWidth(double height) {
        if (height == -1) {
            if (prefWidthCache == -1) {
                prefWidthCache = computePrefWidth(-1);
                if (Double.isNaN(prefWidthCache) || prefWidthCache < 0) prefWidthCache = 0;
                sizeCacheClear = false;
            }
            return prefWidthCache;
        } else {
            double result = computePrefWidth(height);
            return Double.isNaN(result) || result < 0 ? 0 : result;
        }
    }

    @Override public double prefHeight(double width) {
        if (width == -1) {
            if (prefHeightCache == -1) {
                prefHeightCache = computePrefHeight(-1);
                if (Double.isNaN(prefHeightCache) || prefHeightCache < 0) prefHeightCache = 0;
                sizeCacheClear = false;
            }
            return prefHeightCache;
        } else {
            double result = computePrefHeight(width);
            return Double.isNaN(result) || result < 0 ? 0 : result;
        }
    }

    @Override public double minWidth(double height) {
        if (height == -1) {
            if (minWidthCache == -1) {
                minWidthCache = computeMinWidth(-1);
                if (Double.isNaN(minWidthCache) || minWidthCache < 0) minWidthCache = 0;
                sizeCacheClear = false;
            }
            return minWidthCache;
        } else {
            double result = computeMinWidth(height);
            return Double.isNaN(result) || result < 0 ? 0 : result;
        }
    }

    @Override public double minHeight(double width) {
        if (width == -1) {
            if (minHeightCache == -1) {
                minHeightCache = computeMinHeight(-1);
                if (Double.isNaN(minHeightCache) || minHeightCache < 0) minHeightCache = 0;
                sizeCacheClear = false;
            }
            return minHeightCache;
        } else {
            double result = computeMinHeight(width);
            return Double.isNaN(result) || result < 0 ? 0 : result;
        }
    }

    // PENDING_DOC_REVIEW
    
    protected double computePrefWidth(double height) {
        double minX = 0;
        double maxX = 0;
        for (int i=0, max=children.size(); i<max; i++) {
            MappedNode node = children.get(i);
            if (node.isManaged()) {
                final double x = node.getLayoutBounds().getMinX() + node.getLayoutX();
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x + boundedSize(node.prefWidth(-1), node.minWidth(-1), node.maxWidth(-1)));
            }
        }
        return maxX - minX;
    }

    // PENDING_DOC_REVIEW
    
    protected double computePrefHeight(double width) {
        double minY = 0;
        double maxY = 0;
        for (int i=0, max=children.size(); i<max; i++) {
            MappedNode node = children.get(i);
            if (node.isManaged()) {
                final double y = node.getLayoutBounds().getMinY() + node.getLayoutY();
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y + boundedSize(node.prefHeight(-1), node.minHeight(-1), node.maxHeight(-1)));
            }
        }
        return maxY - minY;
    }

    
    protected double computeMinWidth(double height) {
        return prefWidth(height);
    }

    // PENDING_DOC_REVIEW
    
    protected double computeMinHeight(double width) {
        return prefHeight(width);
    }

    
    @Override public double getBaselineOffset() {
        for (int i=0, max=children.size(); i<max; i++) {
            final MappedNode child = children.get(i);
            if (child.isManaged()) {
                double offset = child.getBaselineOffset();
                if (offset == BASELINE_OFFSET_SAME_AS_HEIGHT) {
                    continue;
                }
                return child.getLayoutBounds().getMinY() + child.getLayoutY() + offset;
            }
        }
        return super.getBaselineOffset();
    }

    
    private MappedNode currentLayoutChild = null;

    boolean isCurrentLayoutChild(MappedNode node) {
        return node == currentLayoutChild;
    }

    
    public final void layout() {
        // layoutFlag can be accessed or changed during layout processing.
        // Hence we need to cache and reset it before performing layout.
        LayoutFlags flag = layoutFlag;
        setLayoutFlag(LayoutFlags.CLEAN);
        switch(flag) {
            case CLEAN:
                break;
            case NEEDS_LAYOUT:
                if (performingLayout) {
                    /* This code is here mainly to avoid infinite loops as layout() is public and the call might be (indirectly) invoked accidentally
                     * while doing the layout.
                     * One example might be an invocation from Group layout bounds recalculation
                     *  (e.g. during the localToScene/localToParent calculation).
                     * The layout bounds will thus return layout bounds that are "old" (i.e. before the layout changes, that are just being done),
                     * which is likely what the code would expect.
                     * The changes will invalidate the layout bounds again however, so the layout bounds query after layout pass will return correct answer.
                     */
                    break;
                }
                performingLayout = true;
                layoutChildren();
                // Intended fall-through
            case DIRTY_BRANCH:
                for (int i = 0, max = children.size(); i < max; i++) {
                    final MappedNode child = children.get(i);
                    currentLayoutChild = child;
                    if (child instanceof MappedParent) {
                        ((MappedParent)child).layout();
                    } else if (child instanceof MappedSubScene) {
                        ((MappedSubScene)child).layoutPass();
                    }
                }
                currentLayoutChild = null;
                performingLayout = false;
                break;
        }
    }

    
    protected void layoutChildren() {
        for (int i=0, max=children.size(); i<max; i++) {
            final MappedNode node = children.get(i);
            currentLayoutChild = node;
            if (node.isResizable() && node.isManaged()) {
                node.autosize();
            }
        }
        currentLayoutChild = null;
    }

    
    private boolean sceneRoot = false;

    
    boolean layoutRoot = false;
    @Override final void notifyManagedChanged() {
        layoutRoot = !isManaged() || sceneRoot;
    }

    final boolean isSceneRoot() {
        return sceneRoot;
    }

    /* *********************************************************************
     *                                                                     *
     *                         Stylesheet Handling                         *
     *                                                                     *
     **********************************************************************/


    
    private final ObservableList<String> stylesheets = new TrackableObservableList<String>() {
        @Override
        protected void onChanged(ListChangeListener.Change<String> c) {
            final MappedScene scene = getScene();
            if (scene != null) {

                // Notify the MappedStyleManager if stylesheets change. This MappedParent's
                // styleManager will get recreated in MappedNodeHelper.processCSS.
                MappedStyleManager.getInstance().stylesheetsChanged(MappedParent.this, c);

                // RT-9784 - if stylesheet is removed, reset styled properties to
                // their initial value.
                c.reset();
                while(c.next()) {
                    if (c.wasRemoved() == false) {
                        continue;
                    }
                    break; // no point in resetting more than once...
                }

                reapplyCSS();
            }
        }
    };

    
    public final ObservableList<String> getStylesheets() { return stylesheets; }

    /*
     * This method recurses up the parent chain until parent is null. As the
     * stack unwinds, if the MappedParent has stylesheets, they are added to the
     * list.
     *
     * It is possible to override this method to stop the recursion. This allows
     * a MappedParent to have a set of stylesheets distinct from its MappedParent.
     *
     * Note: This method MUST only be called via its accessor method.
     */
    // SB-dependency: RT-21247 has been filed to track this
    private List<String> doGetAllParentStylesheets() {

        List<String> list = null;
        final MappedParent myParent = getParent();
        if (myParent != null) {

            //
            // recurse so that stylesheets of Parents closest to the root are
            // added to the list first. The ensures that declarations for
            // stylesheets further down the tree (closer to the leaf) have
            // a higer ordinal in the cascade.
            //
            list = MappedParentHelper.getAllParentStylesheets(myParent);
        }

        if (stylesheets != null && stylesheets.isEmpty() == false) {
            if (list == null) {
                list = new ArrayList<String>(stylesheets.size());
            }
            for (int n=0,nMax=stylesheets.size(); n<nMax; n++) {
                list.add(stylesheets.get(n));
            }
        }

        return list;

    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private void doProcessCSS() {

        // Nothing to do...
        if (cssFlag == CssFlags.CLEAN) return;

        // RT-29254 - If DIRTY_BRANCH, pass control to MappedNode#processCSS. This avoids calling MappedNodeHelper.processCSS on
        // this node and all of its children when css doesn't need updated, recalculated, or reapplied.
        if (cssFlag == CssFlags.DIRTY_BRANCH) {
            super.processCSS();
            return;
        }

        // Let the super implementation handle CSS for this node
        MappedParentHelper.superProcessCSS(this);

        // avoid the following call to children.toArray if there are no children
        if (children.isEmpty()) return;

        //
        // RT-33103
        //
        // It is possible for a child to be removed from children in the middle of
        // the following loop. Iterating over the children may result in an IndexOutOfBoundsException.
        // So a copy is made and the copy is iterated over.
        //
        // Note that we don't want the fail-fast feature of an iterator, not to mention the general iterator overhead.
        //
        final MappedNode[] childArray = children.toArray(new MappedNode[children.size()]);

        // For each child, process CSS
        for (int i=0; i<childArray.length; i++) {

            final MappedNode child = childArray[i];

            //  If a child no longer has this as its parent, then it is skipped.
            final MappedParent childParent = child.getParent();
            if (childParent == null || childParent != this) continue;

            // If the parent styles are being updated, recalculated or
            // reapplied, then make sure the children get the same treatment.
            // Unless the child is already more dirty than this parent (RT-29074).
            if(CssFlags.UPDATE.compareTo(child.cssFlag) > 0) {
                child.cssFlag = CssFlags.UPDATE;
            }
            MappedNodeHelper.processCSS(child);
        }
    }

    /* *********************************************************************
     *                               Misc                                  *
     *                                                                     *
     *  Initialization and other functions                                 *
     *                                                                     *
     **********************************************************************/
    {
        // To initialize the class helper at the begining each constructor of this class
        MappedParentHelper.initHelper(this);
    }

    
    protected MappedParent() {
        layoutFlag = LayoutFlags.NEEDS_LAYOUT;
        setAccessibleRole(AccessibleRole.PARENT);
    }

    private MappedNGNode doCreatePeer() {
        return new MappedNGGroup();
    }

    @Override
    void nodeResolvedOrientationChanged() {
        for (int i = 0, max = children.size(); i < max; ++i) {
            children.get(i).parentResolvedOrientationInvalidated();
        }
    }

    /* *************************************************************************
     *                                                                         *
     *                         Bounds Computations                             *
     *                                                                         *
     *  This code originated in GroupBoundsHelper (part of javafx-sg-common)   *
     *  but has been ported here to the FX side since we cannot rely on the PG *
     *  side for computing the bounds (due to the decoupling of the two        *
     *  scenegraphs for threading and other purposes).                         *
     *                                                                         *
     *  Unfortunately, we cannot simply reuse GroupBoundsHelper without some  *
     *  major (and hacky) modification due to the fact that GroupBoundsHelper  *
     *  relies on PG state and we need to do similar things here that rely on  *
     *  core scenegraph state. Unfortunately, that means we made a port.       *
     *                                                                         *
     **************************************************************************/

    private BaseBounds tmp = new RectBounds();

    
    private BaseBounds cachedBounds = new RectBounds();

    
    private boolean cachedBoundsInvalid;

    
    private int dirtyChildrenCount;

    
    private ArrayList<MappedNode> dirtyChildren;

    private MappedNode top;
    private MappedNode left;
    private MappedNode bottom;
    private MappedNode right;
    private MappedNode near;
    private MappedNode far;

    private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
        // If we have no children, our bounds are invalid
        if (children.isEmpty()) {
            return bounds.makeEmpty();
        }

        if (tx.isTranslateOrIdentity()) {
            // this is a transform which is only doing translations, or nothing
            // at all (no scales, rotates, or shears)
            // so in this case we can easily use the cached bounds
            if (cachedBoundsInvalid) {
                recomputeBounds();

                if (dirtyChildren != null) {
                    dirtyChildren.clear();
                }
                cachedBoundsInvalid = false;
                dirtyChildrenCount = 0;
            }
            if (!tx.isIdentity()) {
                bounds = bounds.deriveWithNewBounds((float)(cachedBounds.getMinX() + tx.getMxt()),
                        (float)(cachedBounds.getMinY() + tx.getMyt()),
                        (float)(cachedBounds.getMinZ() + tx.getMzt()),
                        (float)(cachedBounds.getMaxX() + tx.getMxt()),
                        (float)(cachedBounds.getMaxY() + tx.getMyt()),
                        (float)(cachedBounds.getMaxZ() + tx.getMzt()));
            } else {
                bounds = bounds.deriveWithNewBounds(cachedBounds);
            }

            return bounds;
        } else {
            // there is a scale, shear, or rotation happening, so need to
            // do the full transform!
            double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE, maxZ = Double.MIN_VALUE;
            boolean first = true;
            for (int i=0, max=children.size(); i<max; i++) {
                final MappedNode node = children.get(i);
                if (node.isVisible()) {
                    bounds = getChildTransformedBounds(node, tx, bounds);
                    // if the bounds of the child are invalid, we don't want
                    // to use those in the remaining computations.
                    if (bounds.isEmpty()) continue;
                    if (first) {
                        minX = bounds.getMinX();
                        minY = bounds.getMinY();
                        minZ = bounds.getMinZ();
                        maxX = bounds.getMaxX();
                        maxY = bounds.getMaxY();
                        maxZ = bounds.getMaxZ();
                        first = false;
                    } else {
                        minX = Math.min(bounds.getMinX(), minX);
                        minY = Math.min(bounds.getMinY(), minY);
                        minZ = Math.min(bounds.getMinZ(), minZ);
                        maxX = Math.max(bounds.getMaxX(), maxX);
                        maxY = Math.max(bounds.getMaxY(), maxY);
                        maxZ = Math.max(bounds.getMaxZ(), maxZ);
                    }
                }
            }
            // if "first" is still true, then we didn't have any children with
            // non-empty bounds and thus we must return an empty bounds,
            // otherwise we have non-empty bounds so go for it.
            if (first)
                bounds.makeEmpty();
            else
                bounds = bounds.deriveWithNewBounds((float)minX, (float)minY, (float)minZ,
                        (float)maxX, (float)maxY, (float)maxZ);

            return bounds;
        }
    }

    private void setChildDirty(final MappedNode node, final boolean dirty) {
        if (node.boundsChanged == dirty) {
            return;
        }

        node.boundsChanged = dirty;
        if (dirty) {
            if (dirtyChildren != null) {
                dirtyChildren.add(node);
            }
            ++dirtyChildrenCount;
        } else {
            if (dirtyChildren != null) {
                dirtyChildren.remove(node);
            }
            --dirtyChildrenCount;
        }
    }

    private void childIncluded(final MappedNode node) {
        // assert node.isVisible();
        cachedBoundsInvalid = true;
        setChildDirty(node, true);
    }

    // This is called when either the child is actually removed, OR IF IT IS
    // TOGGLED TO BE INVISIBLE. This is because in both cases it needs to be
    // cleared from the state which manages bounds.
    private void childExcluded(final MappedNode node) {
        if (node == left) {
            left = null;
            cachedBoundsInvalid = true;
        }
        if (node == top) {
            top = null;
            cachedBoundsInvalid = true;
        }
        if (node == near) {
            near = null;
            cachedBoundsInvalid = true;
        }
        if (node == right) {
            right = null;
            cachedBoundsInvalid = true;
        }
        if (node == bottom) {
            bottom = null;
            cachedBoundsInvalid = true;
        }
        if (node == far) {
            far = null;
            cachedBoundsInvalid = true;
        }

        setChildDirty(node, false);
    }

    
    private void recomputeBounds() {
        // fast path for case of no children
        if (children.isEmpty()) {
            cachedBounds.makeEmpty();
            return;
        }

        // fast path for case of 1 child
        if (children.size() == 1) {
            MappedNode node = children.get(0);
            node.boundsChanged = false;
            if (node.isVisible()) {
                cachedBounds = getChildTransformedBounds(node, BaseTransform.IDENTITY_TRANSFORM, cachedBounds);
                top = left = bottom = right = near = far = node;
            } else {
                cachedBounds.makeEmpty();
                // no need to null edge nodes here, it was done in childExcluded
                // top = left = bottom = right = near = far = null;
            }
            return;
        }

        if ((dirtyChildrenCount == 0) ||
                !updateCachedBounds(dirtyChildren != null
                                ? dirtyChildren : children,
                        dirtyChildrenCount)) {
            // failed to update cached bounds, recreate them
            createCachedBounds(children);
        }
    }

    private final int LEFT_INVALID = 1;
    private final int TOP_INVALID = 1 << 1;
    private final int NEAR_INVALID = 1 << 2;
    private final int RIGHT_INVALID = 1 << 3;
    private final int BOTTOM_INVALID = 1 << 4;
    private final int FAR_INVALID = 1 << 5;

    private boolean updateCachedBounds(final List<MappedNode> dirtyNodes,
                                       int remainingDirtyNodes) {
        // fast path for untransformed bounds calculation
        if (cachedBounds.isEmpty()) {
            createCachedBounds(dirtyNodes);
            return true;
        }

        int invalidEdges = 0;

        if ((left == null) || left.boundsChanged) {
            invalidEdges |= LEFT_INVALID;
        }
        if ((top == null) || top.boundsChanged) {
            invalidEdges |= TOP_INVALID;
        }
        if ((near == null) || near.boundsChanged) {
            invalidEdges |= NEAR_INVALID;
        }
        if ((right == null) || right.boundsChanged) {
            invalidEdges |= RIGHT_INVALID;
        }
        if ((bottom == null) || bottom.boundsChanged) {
            invalidEdges |= BOTTOM_INVALID;
        }
        if ((far == null) || far.boundsChanged) {
            invalidEdges |= FAR_INVALID;
        }

        // These indicate the bounds of the Group as computed by this
        // function
        float minX = cachedBounds.getMinX();
        float minY = cachedBounds.getMinY();
        float minZ = cachedBounds.getMinZ();
        float maxX = cachedBounds.getMaxX();
        float maxY = cachedBounds.getMaxY();
        float maxZ = cachedBounds.getMaxZ();

        // this checks the newly added nodes first, so if dirtyNodes is the
        // whole children list, we can end early
        for (int i = dirtyNodes.size() - 1; remainingDirtyNodes > 0; --i) {
            final MappedNode node = dirtyNodes.get(i);
            if (node.boundsChanged) {
                // assert node.isVisible();
                node.boundsChanged = false;
                --remainingDirtyNodes;
                tmp = getChildTransformedBounds(node, BaseTransform.IDENTITY_TRANSFORM, tmp);
                if (!tmp.isEmpty()) {
                    float tmpx = tmp.getMinX();
                    float tmpy = tmp.getMinY();
                    float tmpz = tmp.getMinZ();
                    float tmpx2 = tmp.getMaxX();
                    float tmpy2 = tmp.getMaxY();
                    float tmpz2 = tmp.getMaxZ();

                    // If this node forms an edge, then we will set it to be the
                    // node for this edge and update the min/max values
                    if (tmpx <= minX) {
                        minX = tmpx;
                        left = node;
                        invalidEdges &= ~LEFT_INVALID;
                    }
                    if (tmpy <= minY) {
                        minY = tmpy;
                        top = node;
                        invalidEdges &= ~TOP_INVALID;
                    }
                    if (tmpz <= minZ) {
                        minZ = tmpz;
                        near = node;
                        invalidEdges &= ~NEAR_INVALID;
                    }
                    if (tmpx2 >= maxX) {
                        maxX = tmpx2;
                        right = node;
                        invalidEdges &= ~RIGHT_INVALID;
                    }
                    if (tmpy2 >= maxY) {
                        maxY = tmpy2;
                        bottom = node;
                        invalidEdges &= ~BOTTOM_INVALID;
                    }
                    if (tmpz2 >= maxZ) {
                        maxZ = tmpz2;
                        far = node;
                        invalidEdges &= ~FAR_INVALID;
                    }
                }
            }
        }

        if (invalidEdges != 0) {
            // failed to validate some edges
            return false;
        }

        cachedBounds = cachedBounds.deriveWithNewBounds(minX, minY, minZ,
                maxX, maxY, maxZ);
        return true;
    }

    private void createCachedBounds(final List<MappedNode> fromNodes) {
        // These indicate the bounds of the Group as computed by this function
        float minX, minY, minZ;
        float maxX, maxY, maxZ;

        final int nodeCount = fromNodes.size();
        int i;

        // handle first visible non-empty node
        for (i = 0; i < nodeCount; ++i) {
            final MappedNode node = fromNodes.get(i);
            node.boundsChanged = false;
            if (node.isVisible()) {
                tmp = node.getTransformedBounds(
                        tmp, BaseTransform.IDENTITY_TRANSFORM);
                if (!tmp.isEmpty()) {
                    left = top = near = right = bottom = far = node;
                    break;
                }
            }
        }

        if (i == nodeCount) {
            left = top = near = right = bottom = far = null;
            cachedBounds.makeEmpty();
            return;
        }

        minX = tmp.getMinX();
        minY = tmp.getMinY();
        minZ = tmp.getMinZ();
        maxX = tmp.getMaxX();
        maxY = tmp.getMaxY();
        maxZ = tmp.getMaxZ();

        // handle remaining visible non-empty nodes
        for (++i; i < nodeCount; ++i) {
            final MappedNode node = fromNodes.get(i);
            node.boundsChanged = false;
            if (node.isVisible()) {
                tmp = node.getTransformedBounds(
                        tmp, BaseTransform.IDENTITY_TRANSFORM);
                if (!tmp.isEmpty()) {
                    final float tmpx = tmp.getMinX();
                    final float tmpy = tmp.getMinY();
                    final float tmpz = tmp.getMinZ();
                    final float tmpx2 = tmp.getMaxX();
                    final float tmpy2 = tmp.getMaxY();
                    final float tmpz2 = tmp.getMaxZ();

                    if (tmpx < minX) { minX = tmpx; left = node; }
                    if (tmpy < minY) { minY = tmpy; top = node; }
                    if (tmpz < minZ) { minZ = tmpz; near = node; }
                    if (tmpx2 > maxX) { maxX = tmpx2; right = node; }
                    if (tmpy2 > maxY) { maxY = tmpy2; bottom = node; }
                    if (tmpz2 > maxZ) { maxZ = tmpz2; far = node; }
                }
            }
        }

        cachedBounds = cachedBounds.deriveWithNewBounds(minX, minY, minZ,
                maxX, maxY, maxZ);
    }

    
    @Override protected void updateBounds() {
        for (int i=0, max=children.size(); i<max; i++) {
            children.get(i).updateBounds();
        }
        super.updateBounds();
    }

    // Note: this marks the currently processed child in terms of transformed bounds. In rare situations like
    // in RT-37879, it might happen that the child bounds will be marked as invalid. Due to optimizations,
    // the invalidation must *always* be propagated to the parent, because the parent with some transformation
    // calls child's getTransformedBounds non-idenitity transform and the child's transformed bounds are thus not validated.
    // This does not apply to the call itself however, because the call will yield the correct result even if something
    // was invalidated during the computation. We can safely ignore such invalidations from that MappedNode in this case
    private MappedNode currentlyProcessedChild;

    private BaseBounds getChildTransformedBounds(MappedNode node, BaseTransform tx, BaseBounds bounds) {
        currentlyProcessedChild = node;
        bounds = node.getTransformedBounds(bounds, tx);
        currentlyProcessedChild = null;
        return bounds;
    }

    
    void childBoundsChanged(MappedNode node) {
        // See comment above at "currentlyProcessedChild" field
        if (node == currentlyProcessedChild) {
            return;
        }

        cachedBoundsInvalid = true;

        // mark the node such that the parent knows that the child's bounds
        // are not in sync with this parent. In this way, when the bounds
        // need to be computed, we'll come back and figure out the new bounds
        // for all the children which have boundsChanged set to true
        setChildDirty(node, true);

        // go ahead and indicate that the geom has changed for this parent,
        // even though once we figure it all out it may be that the bounds
        // have not changed
        MappedNodeHelper.geomChanged(this);
    }

    
    void childVisibilityChanged(MappedNode node) {
        if (node.isVisible()) {
            childIncluded(node);
        } else {
            childExcluded(node);
        }

        MappedNodeHelper.geomChanged(this);
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private boolean doComputeContains(double localX, double localY) {
        final Point2D tempPt = TempState.getInstance().point;
        for (int i=0, max=children.size(); i<max; i++) {
            final MappedNode node = children.get(i);
            tempPt.x = (float)localX;
            tempPt.y = (float)localY;
            try {
                node.parentToLocal(tempPt);
            } catch (NoninvertibleTransformException e) {
                continue;
            }
            if (node.contains(tempPt.x, tempPt.y)) {
                return true;
            }
        }
        return false;
    }

    
    @Override
    public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
        switch (attribute) {
            case CHILDREN: return getChildrenUnmodifiable();
            default: return super.queryAccessibleAttribute(attribute, parameters);
        }
    }

    void releaseAccessible() {
        for (int i=0, max=children.size(); i<max; i++) {
            final MappedNode node = children.get(i);
            node.releaseAccessible();
        }
        super.releaseAccessible();
    }

    
    List<MappedNode> test_getRemoved() {
        return removed;
    }

    
    List<MappedNode> test_getViewOrderChildren() {
        return viewOrderChildren;
    }
}

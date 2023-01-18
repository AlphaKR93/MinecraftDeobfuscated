/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.phys.Vec3;

public class Path {
    private final List<Node> nodes;
    private Node[] openSet = new Node[0];
    private Node[] closedSet = new Node[0];
    @Nullable
    private Set<Target> targetNodes;
    private int nextNodeIndex;
    private final BlockPos target;
    private final float distToTarget;
    private final boolean reached;

    public Path(List<Node> $$0, BlockPos $$1, boolean $$2) {
        this.nodes = $$0;
        this.target = $$1;
        this.distToTarget = $$0.isEmpty() ? Float.MAX_VALUE : ((Node)this.nodes.get(this.nodes.size() - 1)).distanceManhattan(this.target);
        this.reached = $$2;
    }

    public void advance() {
        ++this.nextNodeIndex;
    }

    public boolean notStarted() {
        return this.nextNodeIndex <= 0;
    }

    public boolean isDone() {
        return this.nextNodeIndex >= this.nodes.size();
    }

    @Nullable
    public Node getEndNode() {
        if (!this.nodes.isEmpty()) {
            return (Node)this.nodes.get(this.nodes.size() - 1);
        }
        return null;
    }

    public Node getNode(int $$0) {
        return (Node)this.nodes.get($$0);
    }

    public void truncateNodes(int $$0) {
        if (this.nodes.size() > $$0) {
            this.nodes.subList($$0, this.nodes.size()).clear();
        }
    }

    public void replaceNode(int $$0, Node $$1) {
        this.nodes.set($$0, (Object)$$1);
    }

    public int getNodeCount() {
        return this.nodes.size();
    }

    public int getNextNodeIndex() {
        return this.nextNodeIndex;
    }

    public void setNextNodeIndex(int $$0) {
        this.nextNodeIndex = $$0;
    }

    public Vec3 getEntityPosAtNode(Entity $$0, int $$1) {
        Node $$2 = (Node)this.nodes.get($$1);
        double $$3 = (double)$$2.x + (double)((int)($$0.getBbWidth() + 1.0f)) * 0.5;
        double $$4 = $$2.y;
        double $$5 = (double)$$2.z + (double)((int)($$0.getBbWidth() + 1.0f)) * 0.5;
        return new Vec3($$3, $$4, $$5);
    }

    public BlockPos getNodePos(int $$0) {
        return ((Node)this.nodes.get($$0)).asBlockPos();
    }

    public Vec3 getNextEntityPos(Entity $$0) {
        return this.getEntityPosAtNode($$0, this.nextNodeIndex);
    }

    public BlockPos getNextNodePos() {
        return ((Node)this.nodes.get(this.nextNodeIndex)).asBlockPos();
    }

    public Node getNextNode() {
        return (Node)this.nodes.get(this.nextNodeIndex);
    }

    @Nullable
    public Node getPreviousNode() {
        return this.nextNodeIndex > 0 ? (Node)this.nodes.get(this.nextNodeIndex - 1) : null;
    }

    public boolean sameAs(@Nullable Path $$0) {
        if ($$0 == null) {
            return false;
        }
        if ($$0.nodes.size() != this.nodes.size()) {
            return false;
        }
        for (int $$1 = 0; $$1 < this.nodes.size(); ++$$1) {
            Node $$2 = (Node)this.nodes.get($$1);
            Node $$3 = (Node)$$0.nodes.get($$1);
            if ($$2.x == $$3.x && $$2.y == $$3.y && $$2.z == $$3.z) continue;
            return false;
        }
        return true;
    }

    public boolean canReach() {
        return this.reached;
    }

    @VisibleForDebug
    void setDebug(Node[] $$0, Node[] $$1, Set<Target> $$2) {
        this.openSet = $$0;
        this.closedSet = $$1;
        this.targetNodes = $$2;
    }

    @VisibleForDebug
    public Node[] getOpenSet() {
        return this.openSet;
    }

    @VisibleForDebug
    public Node[] getClosedSet() {
        return this.closedSet;
    }

    public void writeToStream(FriendlyByteBuf $$0) {
        if (this.targetNodes == null || this.targetNodes.isEmpty()) {
            return;
        }
        $$0.writeBoolean(this.reached);
        $$0.writeInt(this.nextNodeIndex);
        $$0.writeInt(this.targetNodes.size());
        this.targetNodes.forEach($$1 -> $$1.writeToStream($$0));
        $$0.writeInt(this.target.getX());
        $$0.writeInt(this.target.getY());
        $$0.writeInt(this.target.getZ());
        $$0.writeInt(this.nodes.size());
        for (Node $$12 : this.nodes) {
            $$12.writeToStream($$0);
        }
        $$0.writeInt(this.openSet.length);
        for (Node $$2 : this.openSet) {
            $$2.writeToStream($$0);
        }
        $$0.writeInt(this.closedSet.length);
        for (Node $$3 : this.closedSet) {
            $$3.writeToStream($$0);
        }
    }

    public static Path createFromStream(FriendlyByteBuf $$0) {
        boolean $$1 = $$0.readBoolean();
        int $$2 = $$0.readInt();
        int $$3 = $$0.readInt();
        HashSet $$4 = Sets.newHashSet();
        for (int $$5 = 0; $$5 < $$3; ++$$5) {
            $$4.add((Object)Target.createFromStream($$0));
        }
        BlockPos $$6 = new BlockPos($$0.readInt(), $$0.readInt(), $$0.readInt());
        ArrayList $$7 = Lists.newArrayList();
        int $$8 = $$0.readInt();
        for (int $$9 = 0; $$9 < $$8; ++$$9) {
            $$7.add((Object)Node.createFromStream($$0));
        }
        Node[] $$10 = new Node[$$0.readInt()];
        for (int $$11 = 0; $$11 < $$10.length; ++$$11) {
            $$10[$$11] = Node.createFromStream($$0);
        }
        Node[] $$12 = new Node[$$0.readInt()];
        for (int $$13 = 0; $$13 < $$12.length; ++$$13) {
            $$12[$$13] = Node.createFromStream($$0);
        }
        Path $$14 = new Path((List<Node>)$$7, $$6, $$1);
        $$14.openSet = $$10;
        $$14.closedSet = $$12;
        $$14.targetNodes = $$4;
        $$14.nextNodeIndex = $$2;
        return $$14;
    }

    public String toString() {
        return "Path(length=" + this.nodes.size() + ")";
    }

    public BlockPos getTarget() {
        return this.target;
    }

    public float getDistToTarget() {
        return this.distToTarget;
    }
}
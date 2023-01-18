/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Float
 *  java.lang.Object
 */
package net.minecraft.world.level.pathfinder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.pathfinder.Node;

public class Target
extends Node {
    private float bestHeuristic = Float.MAX_VALUE;
    private Node bestNode;
    private boolean reached;

    public Target(Node $$0) {
        super($$0.x, $$0.y, $$0.z);
    }

    public Target(int $$0, int $$1, int $$2) {
        super($$0, $$1, $$2);
    }

    public void updateBest(float $$0, Node $$1) {
        if ($$0 < this.bestHeuristic) {
            this.bestHeuristic = $$0;
            this.bestNode = $$1;
        }
    }

    public Node getBestNode() {
        return this.bestNode;
    }

    public void setReached() {
        this.reached = true;
    }

    public boolean isReached() {
        return this.reached;
    }

    public static Target createFromStream(FriendlyByteBuf $$0) {
        Target $$1 = new Target($$0.readInt(), $$0.readInt(), $$0.readInt());
        Target.readContents($$0, $$1);
        return $$1;
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.Object
 */
package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Target;

public abstract class NodeEvaluator {
    protected PathNavigationRegion level;
    protected Mob mob;
    protected final Int2ObjectMap<Node> nodes = new Int2ObjectOpenHashMap();
    protected int entityWidth;
    protected int entityHeight;
    protected int entityDepth;
    protected boolean canPassDoors;
    protected boolean canOpenDoors;
    protected boolean canFloat;
    protected boolean canWalkOverFences;

    public void prepare(PathNavigationRegion $$0, Mob $$1) {
        this.level = $$0;
        this.mob = $$1;
        this.nodes.clear();
        this.entityWidth = Mth.floor($$1.getBbWidth() + 1.0f);
        this.entityHeight = Mth.floor($$1.getBbHeight() + 1.0f);
        this.entityDepth = Mth.floor($$1.getBbWidth() + 1.0f);
    }

    public void done() {
        this.level = null;
        this.mob = null;
    }

    protected Node getNode(BlockPos $$0) {
        return this.getNode($$0.getX(), $$0.getY(), $$0.getZ());
    }

    protected Node getNode(int $$0, int $$1, int $$2) {
        return (Node)this.nodes.computeIfAbsent(Node.createHash($$0, $$1, $$2), $$3 -> new Node($$0, $$1, $$2));
    }

    public abstract Node getStart();

    public abstract Target getGoal(double var1, double var3, double var5);

    protected Target getTargetFromNode(Node $$0) {
        return new Target($$0);
    }

    public abstract int getNeighbors(Node[] var1, Node var2);

    public abstract BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4, Mob var5);

    public abstract BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4);

    public void setCanPassDoors(boolean $$0) {
        this.canPassDoors = $$0;
    }

    public void setCanOpenDoors(boolean $$0) {
        this.canOpenDoors = $$0;
    }

    public void setCanFloat(boolean $$0) {
        this.canFloat = $$0;
    }

    public void setCanWalkOverFences(boolean $$0) {
        this.canWalkOverFences = $$0;
    }

    public boolean canPassDoors() {
        return this.canPassDoors;
    }

    public boolean canOpenDoors() {
        return this.canOpenDoors;
    }

    public boolean canFloat() {
        return this.canFloat;
    }

    public boolean canWalkOverFences() {
        return this.canWalkOverFences;
    }
}
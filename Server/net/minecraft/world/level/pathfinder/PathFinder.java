/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.Comparator
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.Mob
 */
package net.minecraft.world.level.pathfinder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.BinaryHeap;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;

public class PathFinder {
    private static final float FUDGING = 1.5f;
    private final Node[] neighbors = new Node[32];
    private final int maxVisitedNodes;
    private final NodeEvaluator nodeEvaluator;
    private static final boolean DEBUG = false;
    private final BinaryHeap openSet = new BinaryHeap();

    public PathFinder(NodeEvaluator $$0, int $$1) {
        this.nodeEvaluator = $$0;
        this.maxVisitedNodes = $$1;
    }

    @Nullable
    public Path findPath(PathNavigationRegion $$02, Mob $$1, Set<BlockPos> $$2, float $$3, int $$4, float $$5) {
        this.openSet.clear();
        this.nodeEvaluator.prepare($$02, $$1);
        Node $$6 = this.nodeEvaluator.getStart();
        if ($$6 == null) {
            return null;
        }
        Map $$7 = (Map)$$2.stream().collect(Collectors.toMap($$0 -> this.nodeEvaluator.getGoal($$0.getX(), $$0.getY(), $$0.getZ()), (Function)Function.identity()));
        Path $$8 = this.findPath($$02.getProfiler(), $$6, (Map<Target, BlockPos>)$$7, $$3, $$4, $$5);
        this.nodeEvaluator.done();
        return $$8;
    }

    @Nullable
    private Path findPath(ProfilerFiller $$0, Node $$12, Map<Target, BlockPos> $$2, float $$3, int $$4, float $$5) {
        $$0.push("find_path");
        $$0.markForCharting(MetricCategory.PATH_FINDING);
        Set $$6 = $$2.keySet();
        $$12.g = 0.0f;
        $$12.f = $$12.h = this.getBestH($$12, (Set<Target>)$$6);
        this.openSet.clear();
        this.openSet.insert($$12);
        ImmutableSet $$7 = ImmutableSet.of();
        int $$8 = 0;
        HashSet $$9 = Sets.newHashSetWithExpectedSize((int)$$6.size());
        int $$10 = (int)((float)this.maxVisitedNodes * $$5);
        while (!this.openSet.isEmpty() && ++$$8 < $$10) {
            Node $$11 = this.openSet.pop();
            $$11.closed = true;
            for (Target $$122 : $$6) {
                if (!($$11.distanceManhattan($$122) <= (float)$$4)) continue;
                $$122.setReached();
                $$9.add((Object)$$122);
            }
            if (!$$9.isEmpty()) break;
            if ($$11.distanceTo($$12) >= $$3) continue;
            int $$13 = this.nodeEvaluator.getNeighbors(this.neighbors, $$11);
            for (int $$14 = 0; $$14 < $$13; ++$$14) {
                Node $$15 = this.neighbors[$$14];
                float $$16 = this.distance($$11, $$15);
                $$15.walkedDistance = $$11.walkedDistance + $$16;
                float $$17 = $$11.g + $$16 + $$15.costMalus;
                if (!($$15.walkedDistance < $$3) || $$15.inOpenSet() && !($$17 < $$15.g)) continue;
                $$15.cameFrom = $$11;
                $$15.g = $$17;
                $$15.h = this.getBestH($$15, (Set<Target>)$$6) * 1.5f;
                if ($$15.inOpenSet()) {
                    this.openSet.changeCost($$15, $$15.g + $$15.h);
                    continue;
                }
                $$15.f = $$15.g + $$15.h;
                this.openSet.insert($$15);
            }
        }
        Optional $$18 = !$$9.isEmpty() ? $$9.stream().map($$1 -> this.reconstructPath($$1.getBestNode(), (BlockPos)$$2.get($$1), true)).min(Comparator.comparingInt(Path::getNodeCount)) : $$6.stream().map($$1 -> this.reconstructPath($$1.getBestNode(), (BlockPos)$$2.get($$1), false)).min(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getNodeCount));
        $$0.pop();
        if (!$$18.isPresent()) {
            return null;
        }
        Path $$19 = (Path)$$18.get();
        return $$19;
    }

    protected float distance(Node $$0, Node $$1) {
        return $$0.distanceTo($$1);
    }

    private float getBestH(Node $$0, Set<Target> $$1) {
        float $$2 = Float.MAX_VALUE;
        for (Target $$3 : $$1) {
            float $$4 = $$0.distanceTo($$3);
            $$3.updateBest($$4, $$0);
            $$2 = Math.min((float)$$4, (float)$$2);
        }
        return $$2;
    }

    private Path reconstructPath(Node $$0, BlockPos $$1, boolean $$2) {
        ArrayList $$3 = Lists.newArrayList();
        Node $$4 = $$0;
        $$3.add(0, (Object)$$4);
        while ($$4.cameFrom != null) {
            $$4 = $$4.cameFrom;
            $$3.add(0, (Object)$$4);
        }
        return new Path((List<Node>)$$3, $$1, $$2);
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  java.lang.Double
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class Beardifier
implements DensityFunctions.BeardifierOrMarker {
    public static final int BEARD_KERNEL_RADIUS = 12;
    private static final int BEARD_KERNEL_SIZE = 24;
    private static final float[] BEARD_KERNEL = Util.make(new float[13824], $$0 -> {
        for (int $$1 = 0; $$1 < 24; ++$$1) {
            for (int $$2 = 0; $$2 < 24; ++$$2) {
                for (int $$3 = 0; $$3 < 24; ++$$3) {
                    $$0[$$1 * 24 * 24 + $$2 * 24 + $$3] = (float)Beardifier.computeBeardContribution($$2 - 12, $$3 - 12, $$1 - 12);
                }
            }
        }
    });
    private final ObjectListIterator<Rigid> pieceIterator;
    private final ObjectListIterator<JigsawJunction> junctionIterator;

    public static Beardifier forStructuresInChunk(StructureManager $$02, ChunkPos $$1) {
        int $$2 = $$1.getMinBlockX();
        int $$3 = $$1.getMinBlockZ();
        ObjectArrayList $$4 = new ObjectArrayList(10);
        ObjectArrayList $$5 = new ObjectArrayList(32);
        $$02.startsForStructure($$1, (Predicate<Structure>)((Predicate)$$0 -> $$0.terrainAdaptation() != TerrainAdjustment.NONE)).forEach(arg_0 -> Beardifier.lambda$forStructuresInChunk$2($$1, (ObjectList)$$4, $$2, $$3, (ObjectList)$$5, arg_0));
        return new Beardifier((ObjectListIterator<Rigid>)$$4.iterator(), (ObjectListIterator<JigsawJunction>)$$5.iterator());
    }

    @VisibleForTesting
    public Beardifier(ObjectListIterator<Rigid> $$0, ObjectListIterator<JigsawJunction> $$1) {
        this.pieceIterator = $$0;
        this.junctionIterator = $$1;
    }

    @Override
    public double compute(DensityFunction.FunctionContext $$0) {
        int $$1 = $$0.blockX();
        int $$2 = $$0.blockY();
        int $$3 = $$0.blockZ();
        double $$4 = 0.0;
        while (this.pieceIterator.hasNext()) {
            Rigid $$5 = (Rigid)((Object)this.pieceIterator.next());
            BoundingBox $$6 = $$5.box();
            int $$7 = $$5.groundLevelDelta();
            int $$8 = Math.max((int)0, (int)Math.max((int)($$6.minX() - $$1), (int)($$1 - $$6.maxX())));
            int $$9 = Math.max((int)0, (int)Math.max((int)($$6.minZ() - $$3), (int)($$3 - $$6.maxZ())));
            int $$10 = $$6.minY() + $$7;
            int $$11 = $$2 - $$10;
            int $$12 = switch ($$5.terrainAdjustment()) {
                default -> throw new IncompatibleClassChangeError();
                case TerrainAdjustment.NONE -> 0;
                case TerrainAdjustment.BURY, TerrainAdjustment.BEARD_THIN -> $$11;
                case TerrainAdjustment.BEARD_BOX -> Math.max((int)0, (int)Math.max((int)($$10 - $$2), (int)($$2 - $$6.maxY())));
            };
            $$4 += (switch ($$5.terrainAdjustment()) {
                default -> throw new IncompatibleClassChangeError();
                case TerrainAdjustment.NONE -> 0.0;
                case TerrainAdjustment.BURY -> Beardifier.getBuryContribution($$8, $$12, $$9);
                case TerrainAdjustment.BEARD_THIN, TerrainAdjustment.BEARD_BOX -> Beardifier.getBeardContribution($$8, $$12, $$9, $$11) * 0.8;
            });
        }
        this.pieceIterator.back(Integer.MAX_VALUE);
        while (this.junctionIterator.hasNext()) {
            JigsawJunction $$13 = (JigsawJunction)this.junctionIterator.next();
            int $$14 = $$1 - $$13.getSourceX();
            int $$15 = $$2 - $$13.getSourceGroundY();
            int $$16 = $$3 - $$13.getSourceZ();
            $$4 += Beardifier.getBeardContribution($$14, $$15, $$16, $$15) * 0.4;
        }
        this.junctionIterator.back(Integer.MAX_VALUE);
        return $$4;
    }

    @Override
    public double minValue() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double maxValue() {
        return Double.POSITIVE_INFINITY;
    }

    private static double getBuryContribution(int $$0, int $$1, int $$2) {
        double $$3 = Mth.length($$0, (double)$$1 / 2.0, $$2);
        return Mth.clampedMap($$3, 0.0, 6.0, 1.0, 0.0);
    }

    private static double getBeardContribution(int $$0, int $$1, int $$2, int $$3) {
        int $$4 = $$0 + 12;
        int $$5 = $$1 + 12;
        int $$6 = $$2 + 12;
        if (!(Beardifier.isInKernelRange($$4) && Beardifier.isInKernelRange($$5) && Beardifier.isInKernelRange($$6))) {
            return 0.0;
        }
        double $$7 = (double)$$3 + 0.5;
        double $$8 = Mth.lengthSquared($$0, $$7, $$2);
        double $$9 = -$$7 * Mth.fastInvSqrt($$8 / 2.0) / 2.0;
        return $$9 * (double)BEARD_KERNEL[$$6 * 24 * 24 + $$4 * 24 + $$5];
    }

    private static boolean isInKernelRange(int $$0) {
        return $$0 >= 0 && $$0 < 24;
    }

    private static double computeBeardContribution(int $$0, int $$1, int $$2) {
        return Beardifier.computeBeardContribution($$0, (double)$$1 + 0.5, $$2);
    }

    private static double computeBeardContribution(int $$0, double $$1, int $$2) {
        double $$3 = Mth.lengthSquared($$0, $$1, $$2);
        double $$4 = Math.pow((double)Math.E, (double)(-$$3 / 16.0));
        return $$4;
    }

    private static /* synthetic */ void lambda$forStructuresInChunk$2(ChunkPos $$0, ObjectList $$1, int $$2, int $$3, ObjectList $$4, StructureStart $$5) {
        TerrainAdjustment $$6 = $$5.getStructure().terrainAdaptation();
        for (StructurePiece $$7 : $$5.getPieces()) {
            if (!$$7.isCloseToChunk($$0, 12)) continue;
            if ($$7 instanceof PoolElementStructurePiece) {
                PoolElementStructurePiece $$8 = (PoolElementStructurePiece)$$7;
                StructureTemplatePool.Projection $$9 = $$8.getElement().getProjection();
                if ($$9 == StructureTemplatePool.Projection.RIGID) {
                    $$1.add((Object)new Rigid($$8.getBoundingBox(), $$6, $$8.getGroundLevelDelta()));
                }
                for (JigsawJunction $$10 : $$8.getJunctions()) {
                    int $$11 = $$10.getSourceX();
                    int $$12 = $$10.getSourceZ();
                    if ($$11 <= $$2 - 12 || $$12 <= $$3 - 12 || $$11 >= $$2 + 15 + 12 || $$12 >= $$3 + 15 + 12) continue;
                    $$4.add((Object)$$10);
                }
                continue;
            }
            $$1.add((Object)new Rigid($$7.getBoundingBox(), $$6, 0));
        }
    }

    @VisibleForTesting
    public record Rigid(BoundingBox box, TerrainAdjustment terrainAdjustment, int groundLevelDelta) {
    }
}
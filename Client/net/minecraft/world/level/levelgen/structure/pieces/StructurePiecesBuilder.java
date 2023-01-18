/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure.pieces;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

public class StructurePiecesBuilder
implements StructurePieceAccessor {
    private final List<StructurePiece> pieces = Lists.newArrayList();

    @Override
    public void addPiece(StructurePiece $$0) {
        this.pieces.add((Object)$$0);
    }

    @Override
    @Nullable
    public StructurePiece findCollisionPiece(BoundingBox $$0) {
        return StructurePiece.findCollisionPiece(this.pieces, $$0);
    }

    @Deprecated
    public void offsetPiecesVertically(int $$0) {
        for (StructurePiece $$1 : this.pieces) {
            $$1.move(0, $$0, 0);
        }
    }

    @Deprecated
    public int moveBelowSeaLevel(int $$0, int $$1, RandomSource $$2, int $$3) {
        int $$4 = $$0 - $$3;
        BoundingBox $$5 = this.getBoundingBox();
        int $$6 = $$5.getYSpan() + $$1 + 1;
        if ($$6 < $$4) {
            $$6 += $$2.nextInt($$4 - $$6);
        }
        int $$7 = $$6 - $$5.maxY();
        this.offsetPiecesVertically($$7);
        return $$7;
    }

    public void moveInsideHeights(RandomSource $$0, int $$1, int $$2) {
        int $$6;
        BoundingBox $$3 = this.getBoundingBox();
        int $$4 = $$2 - $$1 + 1 - $$3.getYSpan();
        if ($$4 > 1) {
            int $$5 = $$1 + $$0.nextInt($$4);
        } else {
            $$6 = $$1;
        }
        int $$7 = $$6 - $$3.minY();
        this.offsetPiecesVertically($$7);
    }

    public PiecesContainer build() {
        return new PiecesContainer(this.pieces);
    }

    public void clear() {
        this.pieces.clear();
    }

    public boolean isEmpty() {
        return this.pieces.isEmpty();
    }

    public BoundingBox getBoundingBox() {
        return StructurePiece.createBoundingBox((Stream<StructurePiece>)this.pieces.stream());
    }
}
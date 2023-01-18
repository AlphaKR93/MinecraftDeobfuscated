/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure;

import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

public interface StructurePieceAccessor {
    public void addPiece(StructurePiece var1);

    @Nullable
    public StructurePiece findCollisionPiece(BoundingBox var1);
}
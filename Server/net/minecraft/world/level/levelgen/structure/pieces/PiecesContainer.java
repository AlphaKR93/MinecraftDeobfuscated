/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.stream.Stream
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen.structure.pieces;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import org.slf4j.Logger;

public record PiecesContainer(List<StructurePiece> pieces) {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation JIGSAW_RENAME = new ResourceLocation("jigsaw");
    private static final Map<ResourceLocation, ResourceLocation> RENAMES = ImmutableMap.builder().put((Object)new ResourceLocation("nvi"), (Object)JIGSAW_RENAME).put((Object)new ResourceLocation("pcp"), (Object)JIGSAW_RENAME).put((Object)new ResourceLocation("bastionremnant"), (Object)JIGSAW_RENAME).put((Object)new ResourceLocation("runtime"), (Object)JIGSAW_RENAME).build();

    public PiecesContainer(List<StructurePiece> $$0) {
        this.pieces = List.copyOf($$0);
    }

    public boolean isEmpty() {
        return this.pieces.isEmpty();
    }

    public boolean isInsidePiece(BlockPos $$0) {
        for (StructurePiece $$1 : this.pieces) {
            if (!$$1.getBoundingBox().isInside($$0)) continue;
            return true;
        }
        return false;
    }

    public Tag save(StructurePieceSerializationContext $$0) {
        ListTag $$1 = new ListTag();
        for (StructurePiece $$2 : this.pieces) {
            $$1.add($$2.createTag($$0));
        }
        return $$1;
    }

    public static PiecesContainer load(ListTag $$0, StructurePieceSerializationContext $$1) {
        ArrayList $$2 = Lists.newArrayList();
        for (int $$3 = 0; $$3 < $$0.size(); ++$$3) {
            CompoundTag $$4 = $$0.getCompound($$3);
            String $$5 = $$4.getString("id").toLowerCase(Locale.ROOT);
            ResourceLocation $$6 = new ResourceLocation($$5);
            ResourceLocation $$7 = (ResourceLocation)RENAMES.getOrDefault((Object)$$6, (Object)$$6);
            StructurePieceType $$8 = BuiltInRegistries.STRUCTURE_PIECE.get($$7);
            if ($$8 == null) {
                LOGGER.error("Unknown structure piece id: {}", (Object)$$7);
                continue;
            }
            try {
                StructurePiece $$9 = $$8.load($$1, $$4);
                $$2.add((Object)$$9);
                continue;
            }
            catch (Exception $$10) {
                LOGGER.error("Exception loading structure piece with id {}", (Object)$$7, (Object)$$10);
            }
        }
        return new PiecesContainer((List<StructurePiece>)$$2);
    }

    public BoundingBox calculateBoundingBox() {
        return StructurePiece.createBoundingBox((Stream<StructurePiece>)this.pieces.stream());
    }
}
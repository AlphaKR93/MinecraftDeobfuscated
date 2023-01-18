/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Map
 *  java.util.function.Function
 */
package net.minecraft.world.level.levelgen.structure.structures;

import java.util.Map;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class ShipwreckPieces {
    static final BlockPos PIVOT = new BlockPos(4, 0, 15);
    private static final ResourceLocation[] STRUCTURE_LOCATION_BEACHED = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};
    private static final ResourceLocation[] STRUCTURE_LOCATION_OCEAN = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/upsidedown_full"), new ResourceLocation("shipwreck/upsidedown_fronthalf"), new ResourceLocation("shipwreck/upsidedown_backhalf"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/upsidedown_full_degraded"), new ResourceLocation("shipwreck/upsidedown_fronthalf_degraded"), new ResourceLocation("shipwreck/upsidedown_backhalf_degraded"), new ResourceLocation("shipwreck/sideways_full_degraded"), new ResourceLocation("shipwreck/sideways_fronthalf_degraded"), new ResourceLocation("shipwreck/sideways_backhalf_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};
    static final Map<String, ResourceLocation> MARKERS_TO_LOOT = Map.of((Object)"map_chest", (Object)BuiltInLootTables.SHIPWRECK_MAP, (Object)"treasure_chest", (Object)BuiltInLootTables.SHIPWRECK_TREASURE, (Object)"supply_chest", (Object)BuiltInLootTables.SHIPWRECK_SUPPLY);

    public static void addPieces(StructureTemplateManager $$0, BlockPos $$1, Rotation $$2, StructurePieceAccessor $$3, RandomSource $$4, boolean $$5) {
        ResourceLocation $$6 = Util.getRandom($$5 ? STRUCTURE_LOCATION_BEACHED : STRUCTURE_LOCATION_OCEAN, $$4);
        $$3.addPiece(new ShipwreckPiece($$0, $$6, $$1, $$2, $$5));
    }

    public static class ShipwreckPiece
    extends TemplateStructurePiece {
        private final boolean isBeached;

        public ShipwreckPiece(StructureTemplateManager $$0, ResourceLocation $$1, BlockPos $$2, Rotation $$3, boolean $$4) {
            super(StructurePieceType.SHIPWRECK_PIECE, 0, $$0, $$1, $$1.toString(), ShipwreckPiece.makeSettings($$3), $$2);
            this.isBeached = $$4;
        }

        public ShipwreckPiece(StructureTemplateManager $$0, CompoundTag $$12) {
            super(StructurePieceType.SHIPWRECK_PIECE, $$12, $$0, (Function<ResourceLocation, StructurePlaceSettings>)((Function)$$1 -> ShipwreckPiece.makeSettings(Rotation.valueOf($$12.getString("Rot")))));
            this.isBeached = $$12.getBoolean("isBeached");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            super.addAdditionalSaveData($$0, $$1);
            $$1.putBoolean("isBeached", this.isBeached);
            $$1.putString("Rot", this.placeSettings.getRotation().name());
        }

        private static StructurePlaceSettings makeSettings(Rotation $$0) {
            return new StructurePlaceSettings().setRotation($$0).setMirror(Mirror.NONE).setRotationPivot(PIVOT).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        }

        @Override
        protected void handleDataMarker(String $$0, BlockPos $$1, ServerLevelAccessor $$2, RandomSource $$3, BoundingBox $$4) {
            ResourceLocation $$5 = (ResourceLocation)MARKERS_TO_LOOT.get((Object)$$0);
            if ($$5 != null) {
                RandomizableContainerBlockEntity.setLootTable($$2, $$3, (BlockPos)$$1.below(), $$5);
            }
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            int $$7 = $$0.getMaxBuildHeight();
            int $$8 = 0;
            Vec3i $$9 = this.template.getSize();
            Heightmap.Types $$10 = this.isBeached ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.OCEAN_FLOOR_WG;
            int $$11 = $$9.getX() * $$9.getZ();
            if ($$11 == 0) {
                $$8 = $$0.getHeight($$10, this.templatePosition.getX(), this.templatePosition.getZ());
            } else {
                BlockPos $$12 = this.templatePosition.offset($$9.getX() - 1, 0, $$9.getZ() - 1);
                for (BlockPos $$13 : BlockPos.betweenClosed(this.templatePosition, $$12)) {
                    int $$14 = $$0.getHeight($$10, $$13.getX(), $$13.getZ());
                    $$8 += $$14;
                    $$7 = Math.min((int)$$7, (int)$$14);
                }
                $$8 /= $$11;
            }
            int $$15 = this.isBeached ? $$7 - $$9.getY() / 2 - $$3.nextInt(3) : $$8;
            this.templatePosition = new BlockPos(this.templatePosition.getX(), $$15, this.templatePosition.getZ());
            super.postProcess($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        }
    }
}
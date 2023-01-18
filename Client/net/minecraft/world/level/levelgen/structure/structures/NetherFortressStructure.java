/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;

public class NetherFortressStructure
extends Structure {
    public static final WeightedRandomList<MobSpawnSettings.SpawnerData> FORTRESS_ENEMIES = WeightedRandomList.create((WeightedEntry[])new MobSpawnSettings.SpawnerData[]{new MobSpawnSettings.SpawnerData(EntityType.BLAZE, 10, 2, 3), new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4), new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 8, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 2, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 3, 4, 4)});
    public static final Codec<NetherFortressStructure> CODEC = NetherFortressStructure.simpleCodec(NetherFortressStructure::new);

    public NetherFortressStructure(Structure.StructureSettings $$0) {
        super($$0);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        ChunkPos $$12 = $$0.chunkPos();
        BlockPos $$2 = new BlockPos($$12.getMinBlockX(), 64, $$12.getMinBlockZ());
        return Optional.of((Object)((Object)new Structure.GenerationStub($$2, (Consumer<StructurePiecesBuilder>)((Consumer)$$1 -> NetherFortressStructure.generatePieces($$1, $$0)))));
    }

    private static void generatePieces(StructurePiecesBuilder $$0, Structure.GenerationContext $$1) {
        NetherFortressPieces.StartPiece $$2 = new NetherFortressPieces.StartPiece($$1.random(), $$1.chunkPos().getBlockX(2), $$1.chunkPos().getBlockZ(2));
        $$0.addPiece($$2);
        $$2.addChildren($$2, $$0, $$1.random());
        List<StructurePiece> $$3 = $$2.pendingChildren;
        while (!$$3.isEmpty()) {
            int $$4 = $$1.random().nextInt($$3.size());
            StructurePiece $$5 = (StructurePiece)$$3.remove($$4);
            $$5.addChildren($$2, $$0, $$1.random());
        }
        $$0.moveInsideHeights($$1.random(), 48, 70);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.FORTRESS;
    }
}
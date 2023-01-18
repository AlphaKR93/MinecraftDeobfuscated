/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.shorts.ShortList
 *  java.lang.Object
 *  java.lang.Short
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.EnumSet
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.Set
 *  java.util.function.Function
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.ProtoChunkTicks;
import org.slf4j.Logger;

public class ChunkSerializer {
    private static final Codec<PalettedContainer<BlockState>> BLOCK_STATE_CODEC = PalettedContainer.codecRW(Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, PalettedContainer.Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState());
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TAG_UPGRADE_DATA = "UpgradeData";
    private static final String BLOCK_TICKS_TAG = "block_ticks";
    private static final String FLUID_TICKS_TAG = "fluid_ticks";
    public static final String X_POS_TAG = "xPos";
    public static final String Z_POS_TAG = "zPos";
    public static final String HEIGHTMAPS_TAG = "Heightmaps";
    public static final String IS_LIGHT_ON_TAG = "isLightOn";
    public static final String SECTIONS_TAG = "sections";
    public static final String BLOCK_LIGHT_TAG = "BlockLight";
    public static final String SKY_LIGHT_TAG = "SkyLight";

    public static ProtoChunk read(ServerLevel $$02, PoiManager $$1, ChunkPos $$22, CompoundTag $$3) {
        ProtoChunk $$37;
        BlendingData $$30;
        ChunkPos $$4 = new ChunkPos($$3.getInt(X_POS_TAG), $$3.getInt(Z_POS_TAG));
        if (!Objects.equals((Object)$$22, (Object)$$4)) {
            LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", new Object[]{$$22, $$22, $$4});
        }
        UpgradeData $$5 = $$3.contains(TAG_UPGRADE_DATA, 10) ? new UpgradeData($$3.getCompound(TAG_UPGRADE_DATA), $$02) : UpgradeData.EMPTY;
        boolean $$6 = $$3.getBoolean(IS_LIGHT_ON_TAG);
        ListTag $$7 = $$3.getList(SECTIONS_TAG, 10);
        int $$8 = $$02.getSectionsCount();
        LevelChunkSection[] $$9 = new LevelChunkSection[$$8];
        boolean $$10 = $$02.dimensionType().hasSkyLight();
        ServerChunkCache $$11 = $$02.getChunkSource();
        LevelLightEngine $$12 = ((ChunkSource)$$11).getLightEngine();
        Registry<Biome> $$13 = $$02.registryAccess().registryOrThrow(Registries.BIOME);
        Codec<PalettedContainerRO<Holder<Biome>>> $$14 = ChunkSerializer.makeBiomeCodec($$13);
        boolean $$15 = false;
        for (int $$16 = 0; $$16 < $$7.size(); ++$$16) {
            boolean $$26;
            CompoundTag $$17 = $$7.getCompound($$16);
            byte $$18 = $$17.getByte("Y");
            int $$19 = $$02.getSectionIndexFromSectionY($$18);
            if ($$19 >= 0 && $$19 < $$9.length) {
                LevelChunkSection $$24;
                PalettedContainer<Holder<Biome>> $$23;
                PalettedContainer<BlockState> $$21;
                if ($$17.contains("block_states", 10)) {
                    PalettedContainer $$20 = (PalettedContainer)BLOCK_STATE_CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)$$17.getCompound("block_states")).promotePartial($$2 -> ChunkSerializer.logErrors($$22, $$18, $$2)).getOrThrow(false, arg_0 -> ((Logger)LOGGER).error(arg_0));
                } else {
                    $$21 = new PalettedContainer<BlockState>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
                }
                if ($$17.contains("biomes", 10)) {
                    PalettedContainerRO $$222 = (PalettedContainerRO)$$14.parse((DynamicOps)NbtOps.INSTANCE, (Object)$$17.getCompound("biomes")).promotePartial($$2 -> ChunkSerializer.logErrors($$22, $$18, $$2)).getOrThrow(false, arg_0 -> ((Logger)LOGGER).error(arg_0));
                } else {
                    $$23 = new PalettedContainer<Holder<Biome>>($$13.asHolderIdMap(), $$13.getHolderOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES);
                }
                $$9[$$19] = $$24 = new LevelChunkSection($$18, $$21, $$23);
                $$1.checkConsistencyWithBlocks($$22, $$24);
            }
            boolean $$25 = $$17.contains(BLOCK_LIGHT_TAG, 7);
            boolean bl = $$26 = $$10 && $$17.contains(SKY_LIGHT_TAG, 7);
            if (!$$25 && !$$26) continue;
            if (!$$15) {
                $$12.retainData($$22, true);
                $$15 = true;
            }
            if ($$25) {
                $$12.queueSectionData(LightLayer.BLOCK, SectionPos.of($$22, $$18), new DataLayer($$17.getByteArray(BLOCK_LIGHT_TAG)), true);
            }
            if (!$$26) continue;
            $$12.queueSectionData(LightLayer.SKY, SectionPos.of($$22, $$18), new DataLayer($$17.getByteArray(SKY_LIGHT_TAG)), true);
        }
        long $$27 = $$3.getLong("InhabitedTime");
        ChunkStatus.ChunkType $$28 = ChunkSerializer.getChunkTypeFromTag($$3);
        if ($$3.contains("blending_data", 10)) {
            BlendingData $$29 = (BlendingData)BlendingData.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$3.getCompound("blending_data"))).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).orElse(null);
        } else {
            $$30 = null;
        }
        if ($$28 == ChunkStatus.ChunkType.LEVELCHUNK) {
            LevelChunkTicks<Block> $$31 = LevelChunkTicks.load($$3.getList(BLOCK_TICKS_TAG, 10), $$0 -> BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse($$0)), $$22);
            LevelChunkTicks<Fluid> $$32 = LevelChunkTicks.load($$3.getList(FLUID_TICKS_TAG, 10), $$0 -> BuiltInRegistries.FLUID.getOptional(ResourceLocation.tryParse($$0)), $$22);
            LevelChunk $$33 = new LevelChunk($$02.getLevel(), $$22, $$5, $$31, $$32, $$27, $$9, ChunkSerializer.postLoadChunk($$02, $$3), $$30);
        } else {
            boolean $$40;
            ProtoChunk $$36;
            ProtoChunkTicks<Block> $$34 = ProtoChunkTicks.load($$3.getList(BLOCK_TICKS_TAG, 10), $$0 -> BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse($$0)), $$22);
            ProtoChunkTicks<Fluid> $$35 = ProtoChunkTicks.load($$3.getList(FLUID_TICKS_TAG, 10), $$0 -> BuiltInRegistries.FLUID.getOptional(ResourceLocation.tryParse($$0)), $$22);
            $$37 = $$36 = new ProtoChunk($$22, $$5, $$9, $$34, $$35, $$02, $$13, $$30);
            $$37.setInhabitedTime($$27);
            if ($$3.contains("below_zero_retrogen", 10)) {
                BelowZeroRetrogen.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$3.getCompound("below_zero_retrogen"))).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$36::setBelowZeroRetrogen);
            }
            ChunkStatus $$38 = ChunkStatus.byName($$3.getString("Status"));
            $$36.setStatus($$38);
            if ($$38.isOrAfter(ChunkStatus.FEATURES)) {
                $$36.setLightEngine($$12);
            }
            BelowZeroRetrogen $$39 = $$36.getBelowZeroRetrogen();
            boolean bl = $$40 = $$38.isOrAfter(ChunkStatus.LIGHT) || $$39 != null && $$39.targetStatus().isOrAfter(ChunkStatus.LIGHT);
            if (!$$6 && $$40) {
                for (BlockPos $$41 : BlockPos.betweenClosed($$22.getMinBlockX(), $$02.getMinBuildHeight(), $$22.getMinBlockZ(), $$22.getMaxBlockX(), $$02.getMaxBuildHeight() - 1, $$22.getMaxBlockZ())) {
                    if ($$37.getBlockState($$41).getLightEmission() == 0) continue;
                    $$36.addLight($$41);
                }
            }
        }
        $$37.setLightCorrect($$6);
        CompoundTag $$42 = $$3.getCompound(HEIGHTMAPS_TAG);
        EnumSet $$43 = EnumSet.noneOf(Heightmap.Types.class);
        for (Heightmap.Types $$44 : ((ChunkAccess)$$37).getStatus().heightmapsAfter()) {
            String $$45 = $$44.getSerializationKey();
            if ($$42.contains($$45, 12)) {
                $$37.setHeightmap($$44, $$42.getLongArray($$45));
                continue;
            }
            $$43.add((Object)$$44);
        }
        Heightmap.primeHeightmaps($$37, (Set<Heightmap.Types>)$$43);
        CompoundTag $$46 = $$3.getCompound("structures");
        $$37.setAllStarts(ChunkSerializer.unpackStructureStart(StructurePieceSerializationContext.fromLevel($$02), $$46, $$02.getSeed()));
        $$37.setAllReferences(ChunkSerializer.unpackStructureReferences($$02.registryAccess(), $$22, $$46));
        if ($$3.getBoolean("shouldSave")) {
            $$37.setUnsaved(true);
        }
        ListTag $$47 = $$3.getList("PostProcessing", 9);
        for (int $$48 = 0; $$48 < $$47.size(); ++$$48) {
            ListTag $$49 = $$47.getList($$48);
            for (int $$50 = 0; $$50 < $$49.size(); ++$$50) {
                ((ChunkAccess)$$37).addPackedPostProcess($$49.getShort($$50), $$48);
            }
        }
        if ($$28 == ChunkStatus.ChunkType.LEVELCHUNK) {
            return new ImposterProtoChunk((LevelChunk)((Object)$$37), false);
        }
        ProtoChunk $$51 = $$37;
        ListTag $$52 = $$3.getList("entities", 10);
        for (int $$53 = 0; $$53 < $$52.size(); ++$$53) {
            $$51.addEntity($$52.getCompound($$53));
        }
        ListTag $$54 = $$3.getList("block_entities", 10);
        for (int $$55 = 0; $$55 < $$54.size(); ++$$55) {
            CompoundTag $$56 = $$54.getCompound($$55);
            $$37.setBlockEntityNbt($$56);
        }
        ListTag $$57 = $$3.getList("Lights", 9);
        for (int $$58 = 0; $$58 < $$57.size(); ++$$58) {
            LevelChunkSection $$59 = $$9[$$58];
            if ($$59 == null || $$59.hasOnlyAir()) continue;
            ListTag $$60 = $$57.getList($$58);
            for (int $$61 = 0; $$61 < $$60.size(); ++$$61) {
                $$51.addLight($$60.getShort($$61), $$58);
            }
        }
        CompoundTag $$62 = $$3.getCompound("CarvingMasks");
        for (String $$63 : $$62.getAllKeys()) {
            GenerationStep.Carving $$64 = GenerationStep.Carving.valueOf($$63);
            $$51.setCarvingMask($$64, new CarvingMask($$62.getLongArray($$63), $$37.getMinBuildHeight()));
        }
        return $$51;
    }

    private static void logErrors(ChunkPos $$0, int $$1, String $$2) {
        LOGGER.error("Recoverable errors when loading section [" + $$0.x + ", " + $$1 + ", " + $$0.z + "]: " + $$2);
    }

    private static Codec<PalettedContainerRO<Holder<Biome>>> makeBiomeCodec(Registry<Biome> $$0) {
        return PalettedContainer.codecRO($$0.asHolderIdMap(), $$0.holderByNameCodec(), PalettedContainer.Strategy.SECTION_BIOMES, $$0.getHolderOrThrow(Biomes.PLAINS));
    }

    public static CompoundTag write(ServerLevel $$0, ChunkAccess $$12) {
        UpgradeData $$6;
        BelowZeroRetrogen $$5;
        ChunkPos $$2 = $$12.getPos();
        CompoundTag $$3 = new CompoundTag();
        $$3.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        $$3.putInt(X_POS_TAG, $$2.x);
        $$3.putInt("yPos", $$12.getMinSection());
        $$3.putInt(Z_POS_TAG, $$2.z);
        $$3.putLong("LastUpdate", $$0.getGameTime());
        $$3.putLong("InhabitedTime", $$12.getInhabitedTime());
        $$3.putString("Status", $$12.getStatus().getName());
        BlendingData $$4 = $$12.getBlendingData();
        if ($$4 != null) {
            BlendingData.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)$$4).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$3.put("blending_data", (Tag)$$1));
        }
        if (($$5 = $$12.getBelowZeroRetrogen()) != null) {
            BelowZeroRetrogen.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)$$5).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$3.put("below_zero_retrogen", (Tag)$$1));
        }
        if (!($$6 = $$12.getUpgradeData()).isEmpty()) {
            $$3.put(TAG_UPGRADE_DATA, $$6.write());
        }
        LevelChunkSection[] $$7 = $$12.getSections();
        ListTag $$8 = new ListTag();
        ThreadedLevelLightEngine $$9 = $$0.getChunkSource().getLightEngine();
        Registry<Biome> $$10 = $$0.registryAccess().registryOrThrow(Registries.BIOME);
        Codec<PalettedContainerRO<Holder<Biome>>> $$11 = ChunkSerializer.makeBiomeCodec($$10);
        boolean $$122 = $$12.isLightCorrect();
        for (int $$13 = $$9.getMinLightSection(); $$13 < $$9.getMaxLightSection(); ++$$13) {
            int $$14 = $$12.getSectionIndexFromSectionY($$13);
            boolean $$15 = $$14 >= 0 && $$14 < $$7.length;
            DataLayer $$16 = $$9.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of($$2, $$13));
            DataLayer $$17 = $$9.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of($$2, $$13));
            if (!$$15 && $$16 == null && $$17 == null) continue;
            CompoundTag $$18 = new CompoundTag();
            if ($$15) {
                LevelChunkSection $$19 = $$7[$$14];
                $$18.put("block_states", (Tag)BLOCK_STATE_CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, $$19.getStates()).getOrThrow(false, arg_0 -> ((Logger)LOGGER).error(arg_0)));
                $$18.put("biomes", (Tag)$$11.encodeStart((DynamicOps)NbtOps.INSTANCE, $$19.getBiomes()).getOrThrow(false, arg_0 -> ((Logger)LOGGER).error(arg_0)));
            }
            if ($$16 != null && !$$16.isEmpty()) {
                $$18.putByteArray(BLOCK_LIGHT_TAG, $$16.getData());
            }
            if ($$17 != null && !$$17.isEmpty()) {
                $$18.putByteArray(SKY_LIGHT_TAG, $$17.getData());
            }
            if ($$18.isEmpty()) continue;
            $$18.putByte("Y", (byte)$$13);
            $$8.add($$18);
        }
        $$3.put(SECTIONS_TAG, $$8);
        if ($$122) {
            $$3.putBoolean(IS_LIGHT_ON_TAG, true);
        }
        ListTag $$20 = new ListTag();
        for (BlockPos $$21 : $$12.getBlockEntitiesPos()) {
            CompoundTag $$22 = $$12.getBlockEntityNbtForSaving($$21);
            if ($$22 == null) continue;
            $$20.add($$22);
        }
        $$3.put("block_entities", $$20);
        if ($$12.getStatus().getChunkType() == ChunkStatus.ChunkType.PROTOCHUNK) {
            ProtoChunk $$23 = (ProtoChunk)$$12;
            ListTag $$24 = new ListTag();
            $$24.addAll((Collection)$$23.getEntities());
            $$3.put("entities", $$24);
            $$3.put("Lights", ChunkSerializer.packOffsets($$23.getPackedLights()));
            CompoundTag $$25 = new CompoundTag();
            for (GenerationStep.Carving $$26 : GenerationStep.Carving.values()) {
                CarvingMask $$27 = $$23.getCarvingMask($$26);
                if ($$27 == null) continue;
                $$25.putLongArray($$26.toString(), $$27.toArray());
            }
            $$3.put("CarvingMasks", $$25);
        }
        ChunkSerializer.saveTicks($$0, $$3, $$12.getTicksForSerialization());
        $$3.put("PostProcessing", ChunkSerializer.packOffsets($$12.getPostProcessing()));
        CompoundTag $$28 = new CompoundTag();
        for (Map.Entry $$29 : $$12.getHeightmaps()) {
            if (!$$12.getStatus().heightmapsAfter().contains($$29.getKey())) continue;
            $$28.put(((Heightmap.Types)$$29.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)$$29.getValue()).getRawData()));
        }
        $$3.put(HEIGHTMAPS_TAG, $$28);
        $$3.put("structures", ChunkSerializer.packStructureData(StructurePieceSerializationContext.fromLevel($$0), $$2, $$12.getAllStarts(), $$12.getAllReferences()));
        return $$3;
    }

    private static void saveTicks(ServerLevel $$02, CompoundTag $$1, ChunkAccess.TicksToSave $$2) {
        long $$3 = $$02.getLevelData().getGameTime();
        $$1.put(BLOCK_TICKS_TAG, $$2.blocks().save($$3, (Function<Block, String>)((Function)$$0 -> BuiltInRegistries.BLOCK.getKey((Block)$$0).toString())));
        $$1.put(FLUID_TICKS_TAG, $$2.fluids().save($$3, (Function<Fluid, String>)((Function)$$0 -> BuiltInRegistries.FLUID.getKey((Fluid)$$0).toString())));
    }

    public static ChunkStatus.ChunkType getChunkTypeFromTag(@Nullable CompoundTag $$0) {
        if ($$0 != null) {
            return ChunkStatus.byName($$0.getString("Status")).getChunkType();
        }
        return ChunkStatus.ChunkType.PROTOCHUNK;
    }

    @Nullable
    private static LevelChunk.PostLoadProcessor postLoadChunk(ServerLevel $$0, CompoundTag $$1) {
        ListTag $$2 = ChunkSerializer.getListOfCompoundsOrNull($$1, "entities");
        ListTag $$32 = ChunkSerializer.getListOfCompoundsOrNull($$1, "block_entities");
        if ($$2 == null && $$32 == null) {
            return null;
        }
        return $$3 -> {
            if ($$2 != null) {
                $$0.addLegacyChunkEntities(EntityType.loadEntitiesRecursive((List<? extends Tag>)$$2, $$0));
            }
            if ($$32 != null) {
                for (int $$4 = 0; $$4 < $$32.size(); ++$$4) {
                    CompoundTag $$5 = $$32.getCompound($$4);
                    boolean $$6 = $$5.getBoolean("keepPacked");
                    if ($$6) {
                        $$3.setBlockEntityNbt($$5);
                        continue;
                    }
                    BlockPos $$7 = BlockEntity.getPosFromTag($$5);
                    BlockEntity $$8 = BlockEntity.loadStatic($$7, $$3.getBlockState($$7), $$5);
                    if ($$8 == null) continue;
                    $$3.setBlockEntity($$8);
                }
            }
        };
    }

    @Nullable
    private static ListTag getListOfCompoundsOrNull(CompoundTag $$0, String $$1) {
        ListTag $$2 = $$0.getList($$1, 10);
        return $$2.isEmpty() ? null : $$2;
    }

    private static CompoundTag packStructureData(StructurePieceSerializationContext $$0, ChunkPos $$1, Map<Structure, StructureStart> $$2, Map<Structure, LongSet> $$3) {
        CompoundTag $$4 = new CompoundTag();
        CompoundTag $$5 = new CompoundTag();
        Registry<Structure> $$6 = $$0.registryAccess().registryOrThrow(Registries.STRUCTURE);
        for (Map.Entry $$7 : $$2.entrySet()) {
            ResourceLocation $$8 = $$6.getKey((Structure)$$7.getKey());
            $$5.put($$8.toString(), ((StructureStart)$$7.getValue()).createTag($$0, $$1));
        }
        $$4.put("starts", $$5);
        CompoundTag $$9 = new CompoundTag();
        for (Map.Entry $$10 : $$3.entrySet()) {
            if (((LongSet)$$10.getValue()).isEmpty()) continue;
            ResourceLocation $$11 = $$6.getKey((Structure)$$10.getKey());
            $$9.put($$11.toString(), new LongArrayTag((LongSet)$$10.getValue()));
        }
        $$4.put("References", $$9);
        return $$4;
    }

    private static Map<Structure, StructureStart> unpackStructureStart(StructurePieceSerializationContext $$0, CompoundTag $$1, long $$2) {
        HashMap $$3 = Maps.newHashMap();
        Registry<Structure> $$4 = $$0.registryAccess().registryOrThrow(Registries.STRUCTURE);
        CompoundTag $$5 = $$1.getCompound("starts");
        for (String $$6 : $$5.getAllKeys()) {
            ResourceLocation $$7 = ResourceLocation.tryParse($$6);
            Structure $$8 = $$4.get($$7);
            if ($$8 == null) {
                LOGGER.error("Unknown structure start: {}", (Object)$$7);
                continue;
            }
            StructureStart $$9 = StructureStart.loadStaticStart($$0, $$5.getCompound($$6), $$2);
            if ($$9 == null) continue;
            $$3.put((Object)$$8, (Object)$$9);
        }
        return $$3;
    }

    private static Map<Structure, LongSet> unpackStructureReferences(RegistryAccess $$0, ChunkPos $$1, CompoundTag $$22) {
        HashMap $$3 = Maps.newHashMap();
        Registry<Structure> $$4 = $$0.registryOrThrow(Registries.STRUCTURE);
        CompoundTag $$5 = $$22.getCompound("References");
        for (String $$6 : $$5.getAllKeys()) {
            ResourceLocation $$7 = ResourceLocation.tryParse($$6);
            Structure $$8 = $$4.get($$7);
            if ($$8 == null) {
                LOGGER.warn("Found reference to unknown structure '{}' in chunk {}, discarding", (Object)$$7, (Object)$$1);
                continue;
            }
            long[] $$9 = $$5.getLongArray($$6);
            if ($$9.length == 0) continue;
            $$3.put((Object)$$8, (Object)new LongOpenHashSet(Arrays.stream((long[])$$9).filter($$2 -> {
                ChunkPos $$3 = new ChunkPos($$2);
                if ($$3.getChessboardDistance($$1) > 8) {
                    LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", new Object[]{$$7, $$3, $$1});
                    return false;
                }
                return true;
            }).toArray()));
        }
        return $$3;
    }

    public static ListTag packOffsets(ShortList[] $$0) {
        ListTag $$1 = new ListTag();
        for (ShortList $$2 : $$0) {
            ListTag $$3 = new ListTag();
            if ($$2 != null) {
                for (Short $$4 : $$2) {
                    $$3.add(ShortTag.valueOf($$4));
                }
            }
            $$1.add($$3);
        }
        return $$1;
    }
}
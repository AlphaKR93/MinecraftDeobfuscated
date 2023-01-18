/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.Reader
 *  java.lang.IllegalArgumentException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.file.FileVisitOption
 *  java.nio.file.Files
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Optional
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 */
package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.data.structures.StructureUpdater;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTicks;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureUtils {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String DEFAULT_TEST_STRUCTURES_DIR = "gameteststructures";
    public static String testStructuresDir = "gameteststructures";
    private static final int HOW_MANY_CHUNKS_TO_LOAD_IN_EACH_DIRECTION_OF_STRUCTURE = 4;

    public static Rotation getRotationForRotationSteps(int $$0) {
        switch ($$0) {
            case 0: {
                return Rotation.NONE;
            }
            case 1: {
                return Rotation.CLOCKWISE_90;
            }
            case 2: {
                return Rotation.CLOCKWISE_180;
            }
            case 3: {
                return Rotation.COUNTERCLOCKWISE_90;
            }
        }
        throw new IllegalArgumentException("rotationSteps must be a value from 0-3. Got value " + $$0);
    }

    public static int getRotationStepsForRotation(Rotation $$0) {
        switch ($$0) {
            case NONE: {
                return 0;
            }
            case CLOCKWISE_90: {
                return 1;
            }
            case CLOCKWISE_180: {
                return 2;
            }
            case COUNTERCLOCKWISE_90: {
                return 3;
            }
        }
        throw new IllegalArgumentException("Unknown rotation value, don't know how many steps it represents: " + $$0);
    }

    public static void main(String[] $$02) throws IOException {
        Bootstrap.bootStrap();
        Files.walk((Path)Paths.get((String)testStructuresDir, (String[])new String[0]), (FileVisitOption[])new FileVisitOption[0]).filter($$0 -> $$0.toString().endsWith(".snbt")).forEach($$0 -> {
            try {
                String $$1 = Files.readString((Path)$$0);
                CompoundTag $$2 = NbtUtils.snbtToStructure($$1);
                CompoundTag $$3 = StructureUpdater.update($$0.toString(), $$2);
                NbtToSnbt.writeSnbt(CachedOutput.NO_CACHE, $$0, NbtUtils.structureToSnbt($$3));
            }
            catch (CommandSyntaxException | IOException $$4) {
                LOGGER.error("Something went wrong upgrading: {}", $$0, (Object)$$4);
            }
        });
    }

    public static AABB getStructureBounds(StructureBlockEntity $$0) {
        BlockPos $$1 = $$0.getBlockPos();
        Vec3i $$2 = $$1.offset($$0.getStructureSize().offset(-1, -1, -1));
        BlockPos $$3 = StructureTemplate.transform((BlockPos)$$2, Mirror.NONE, $$0.getRotation(), $$1);
        return new AABB($$1, $$3);
    }

    public static BoundingBox getStructureBoundingBox(StructureBlockEntity $$0) {
        BlockPos $$1 = $$0.getBlockPos();
        Vec3i $$2 = $$1.offset($$0.getStructureSize().offset(-1, -1, -1));
        BlockPos $$3 = StructureTemplate.transform((BlockPos)$$2, Mirror.NONE, $$0.getRotation(), $$1);
        return BoundingBox.fromCorners($$1, $$3);
    }

    public static void addCommandBlockAndButtonToStartTest(BlockPos $$0, BlockPos $$1, Rotation $$2, ServerLevel $$3) {
        BlockPos $$4 = StructureTemplate.transform((BlockPos)$$0.offset($$1), Mirror.NONE, $$2, $$0);
        $$3.setBlockAndUpdate($$4, Blocks.COMMAND_BLOCK.defaultBlockState());
        CommandBlockEntity $$5 = (CommandBlockEntity)$$3.getBlockEntity($$4);
        $$5.getCommandBlock().setCommand("test runthis");
        BlockPos $$6 = StructureTemplate.transform($$4.offset(0, 0, -1), Mirror.NONE, $$2, $$4);
        $$3.setBlockAndUpdate($$6, Blocks.STONE_BUTTON.defaultBlockState().rotate($$2));
    }

    public static void createNewEmptyStructureBlock(String $$0, BlockPos $$1, Vec3i $$2, Rotation $$3, ServerLevel $$4) {
        BoundingBox $$5 = StructureUtils.getStructureBoundingBox($$1, $$2, $$3);
        StructureUtils.clearSpaceForStructure($$5, $$1.getY(), $$4);
        $$4.setBlockAndUpdate($$1, Blocks.STRUCTURE_BLOCK.defaultBlockState());
        StructureBlockEntity $$6 = (StructureBlockEntity)$$4.getBlockEntity($$1);
        $$6.setIgnoreEntities(false);
        $$6.setStructureName(new ResourceLocation($$0));
        $$6.setStructureSize($$2);
        $$6.setMode(StructureMode.SAVE);
        $$6.setShowBoundingBox(true);
    }

    /*
     * WARNING - void declaration
     */
    public static StructureBlockEntity spawnStructure(String $$0, BlockPos $$1, Rotation $$2, int $$3, ServerLevel $$4, boolean $$5) {
        void $$12;
        Vec3i $$6 = StructureUtils.getStructureTemplate($$0, $$4).getSize();
        BoundingBox $$7 = StructureUtils.getStructureBoundingBox($$1, $$6, $$2);
        if ($$2 == Rotation.NONE) {
            BlockPos $$8 = $$1;
        } else if ($$2 == Rotation.CLOCKWISE_90) {
            BlockPos $$9 = $$1.offset($$6.getZ() - 1, 0, 0);
        } else if ($$2 == Rotation.CLOCKWISE_180) {
            BlockPos $$10 = $$1.offset($$6.getX() - 1, 0, $$6.getZ() - 1);
        } else if ($$2 == Rotation.COUNTERCLOCKWISE_90) {
            BlockPos $$11 = $$1.offset(0, 0, $$6.getX() - 1);
        } else {
            throw new IllegalArgumentException("Invalid rotation: " + $$2);
        }
        StructureUtils.forceLoadChunks($$1, $$4);
        StructureUtils.clearSpaceForStructure($$7, $$1.getY(), $$4);
        StructureBlockEntity $$13 = StructureUtils.createStructureBlock($$0, (BlockPos)$$12, $$2, $$4, $$5);
        ((LevelTicks)$$4.getBlockTicks()).clearArea($$7);
        $$4.clearBlockEvents($$7);
        return $$13;
    }

    private static void forceLoadChunks(BlockPos $$0, ServerLevel $$1) {
        ChunkPos $$2 = new ChunkPos($$0);
        for (int $$3 = -1; $$3 < 4; ++$$3) {
            for (int $$4 = -1; $$4 < 4; ++$$4) {
                int $$5 = $$2.x + $$3;
                int $$6 = $$2.z + $$4;
                $$1.setChunkForced($$5, $$6, true);
            }
        }
    }

    public static void clearSpaceForStructure(BoundingBox $$02, int $$1, ServerLevel $$22) {
        BoundingBox $$3 = new BoundingBox($$02.minX() - 2, $$02.minY() - 3, $$02.minZ() - 3, $$02.maxX() + 3, $$02.maxY() + 20, $$02.maxZ() + 3);
        BlockPos.betweenClosedStream($$3).forEach($$2 -> StructureUtils.clearBlock($$1, $$2, $$22));
        ((LevelTicks)$$22.getBlockTicks()).clearArea($$3);
        $$22.clearBlockEvents($$3);
        AABB $$4 = new AABB($$3.minX(), $$3.minY(), $$3.minZ(), $$3.maxX(), $$3.maxY(), $$3.maxZ());
        List $$5 = $$22.getEntitiesOfClass(Entity.class, $$4, $$0 -> !($$0 instanceof Player));
        $$5.forEach(Entity::discard);
    }

    public static BoundingBox getStructureBoundingBox(BlockPos $$0, Vec3i $$1, Rotation $$2) {
        BlockPos $$3 = ((BlockPos)$$0.offset($$1)).offset(-1, -1, -1);
        BlockPos $$4 = StructureTemplate.transform($$3, Mirror.NONE, $$2, $$0);
        BoundingBox $$5 = BoundingBox.fromCorners($$0, $$4);
        int $$6 = Math.min((int)$$5.minX(), (int)$$5.maxX());
        int $$7 = Math.min((int)$$5.minZ(), (int)$$5.maxZ());
        return $$5.move($$0.getX() - $$6, 0, $$0.getZ() - $$7);
    }

    public static Optional<BlockPos> findStructureBlockContainingPos(BlockPos $$0, int $$1, ServerLevel $$22) {
        return StructureUtils.findStructureBlocks($$0, $$1, $$22).stream().filter($$2 -> StructureUtils.doesStructureContain($$2, $$0, $$22)).findFirst();
    }

    @Nullable
    public static BlockPos findNearestStructureBlock(BlockPos $$0, int $$12, ServerLevel $$2) {
        Comparator $$3 = Comparator.comparingInt($$1 -> $$1.distManhattan($$0));
        Collection<BlockPos> $$4 = StructureUtils.findStructureBlocks($$0, $$12, $$2);
        Optional $$5 = $$4.stream().min($$3);
        return (BlockPos)$$5.orElse(null);
    }

    public static Collection<BlockPos> findStructureBlocks(BlockPos $$0, int $$1, ServerLevel $$2) {
        ArrayList $$3 = Lists.newArrayList();
        AABB $$4 = new AABB($$0);
        $$4 = $$4.inflate($$1);
        for (int $$5 = (int)$$4.minX; $$5 <= (int)$$4.maxX; ++$$5) {
            for (int $$6 = (int)$$4.minY; $$6 <= (int)$$4.maxY; ++$$6) {
                for (int $$7 = (int)$$4.minZ; $$7 <= (int)$$4.maxZ; ++$$7) {
                    BlockPos $$8 = new BlockPos($$5, $$6, $$7);
                    BlockState $$9 = $$2.getBlockState($$8);
                    if (!$$9.is(Blocks.STRUCTURE_BLOCK)) continue;
                    $$3.add((Object)$$8);
                }
            }
        }
        return $$3;
    }

    private static StructureTemplate getStructureTemplate(String $$0, ServerLevel $$1) {
        StructureTemplateManager $$2 = $$1.getStructureManager();
        Optional<StructureTemplate> $$3 = $$2.get(new ResourceLocation($$0));
        if ($$3.isPresent()) {
            return (StructureTemplate)$$3.get();
        }
        String $$4 = $$0 + ".snbt";
        Path $$5 = Paths.get((String)testStructuresDir, (String[])new String[]{$$4});
        CompoundTag $$6 = StructureUtils.tryLoadStructure($$5);
        if ($$6 == null) {
            throw new RuntimeException("Could not find structure file " + $$5 + ", and the structure is not available in the world structures either.");
        }
        return $$2.readStructure($$6);
    }

    private static StructureBlockEntity createStructureBlock(String $$0, BlockPos $$1, Rotation $$2, ServerLevel $$3, boolean $$4) {
        $$3.setBlockAndUpdate($$1, Blocks.STRUCTURE_BLOCK.defaultBlockState());
        StructureBlockEntity $$5 = (StructureBlockEntity)$$3.getBlockEntity($$1);
        $$5.setMode(StructureMode.LOAD);
        $$5.setRotation($$2);
        $$5.setIgnoreEntities(false);
        $$5.setStructureName(new ResourceLocation($$0));
        $$5.loadStructure($$3, $$4);
        if ($$5.getStructureSize() != Vec3i.ZERO) {
            return $$5;
        }
        StructureTemplate $$6 = StructureUtils.getStructureTemplate($$0, $$3);
        $$5.loadStructure($$3, $$4, $$6);
        if ($$5.getStructureSize() == Vec3i.ZERO) {
            throw new RuntimeException("Failed to load structure " + $$0);
        }
        return $$5;
    }

    @Nullable
    private static CompoundTag tryLoadStructure(Path $$0) {
        try {
            BufferedReader $$1 = Files.newBufferedReader((Path)$$0);
            String $$2 = IOUtils.toString((Reader)$$1);
            return NbtUtils.snbtToStructure($$2);
        }
        catch (IOException $$3) {
            return null;
        }
        catch (CommandSyntaxException $$4) {
            throw new RuntimeException("Error while trying to load structure " + $$0, (Throwable)$$4);
        }
    }

    private static void clearBlock(int $$0, BlockPos $$1, ServerLevel $$2) {
        BlockState $$3 = null;
        RegistryAccess $$4 = $$2.registryAccess();
        FlatLevelGeneratorSettings $$5 = FlatLevelGeneratorSettings.getDefault($$4.lookupOrThrow(Registries.BIOME), $$4.lookupOrThrow(Registries.STRUCTURE_SET), $$4.lookupOrThrow(Registries.PLACED_FEATURE));
        List<BlockState> $$6 = $$5.getLayers();
        int $$7 = $$1.getY() - $$2.getMinBuildHeight();
        if ($$1.getY() < $$0 && $$7 > 0 && $$7 <= $$6.size()) {
            $$3 = (BlockState)$$6.get($$7 - 1);
        }
        if ($$3 == null) {
            $$3 = Blocks.AIR.defaultBlockState();
        }
        BlockInput $$8 = new BlockInput($$3, Collections.emptySet(), null);
        $$8.place($$2, $$1, 2);
        $$2.blockUpdated($$1, $$3.getBlock());
    }

    private static boolean doesStructureContain(BlockPos $$0, BlockPos $$1, ServerLevel $$2) {
        StructureBlockEntity $$3 = (StructureBlockEntity)$$2.getBlockEntity($$0);
        AABB $$4 = StructureUtils.getStructureBounds($$3).inflate(1.0);
        return $$4.contains(Vec3.atCenterOf($$1));
    }
}
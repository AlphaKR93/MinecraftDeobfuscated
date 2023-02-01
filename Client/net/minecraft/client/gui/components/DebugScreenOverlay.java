/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DataFixUtils
 *  it.unimi.dsi.fastutil.longs.LongSets
 *  it.unimi.dsi.fastutil.longs.LongSets$EmptySet
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Character
 *  java.lang.Comparable
 *  java.lang.Enum
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Runtime
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.management.GarbageCollectorMXBean
 *  java.lang.management.ManagementFactory
 *  java.util.ArrayList
 *  java.util.EnumMap
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.TimeUnit
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.Entity
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix4f;

public class DebugScreenOverlay
extends GuiComponent {
    private static final int COLOR_GREY = 0xE0E0E0;
    private static final int MARGIN_RIGHT = 2;
    private static final int MARGIN_LEFT = 2;
    private static final int MARGIN_TOP = 2;
    private static final Map<Heightmap.Types, String> HEIGHTMAP_NAMES = (Map)Util.make(new EnumMap(Heightmap.Types.class), $$0 -> {
        $$0.put((Enum)Heightmap.Types.WORLD_SURFACE_WG, (Object)"SW");
        $$0.put((Enum)Heightmap.Types.WORLD_SURFACE, (Object)"S");
        $$0.put((Enum)Heightmap.Types.OCEAN_FLOOR_WG, (Object)"OW");
        $$0.put((Enum)Heightmap.Types.OCEAN_FLOOR, (Object)"O");
        $$0.put((Enum)Heightmap.Types.MOTION_BLOCKING, (Object)"M");
        $$0.put((Enum)Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (Object)"ML");
    });
    private final Minecraft minecraft;
    private final AllocationRateCalculator allocationRateCalculator;
    private final Font font;
    private HitResult block;
    private HitResult liquid;
    @Nullable
    private ChunkPos lastPos;
    @Nullable
    private LevelChunk clientChunk;
    @Nullable
    private CompletableFuture<LevelChunk> serverChunk;
    private static final int RED = -65536;
    private static final int YELLOW = -256;
    private static final int GREEN = -16711936;

    public DebugScreenOverlay(Minecraft $$0) {
        this.minecraft = $$0;
        this.allocationRateCalculator = new AllocationRateCalculator();
        this.font = $$0.font;
    }

    public void clearChunkCache() {
        this.serverChunk = null;
        this.clientChunk = null;
    }

    public void render(PoseStack $$0) {
        this.minecraft.getProfiler().push("debug");
        Entity $$1 = this.minecraft.getCameraEntity();
        this.block = $$1.pick(20.0, 0.0f, false);
        this.liquid = $$1.pick(20.0, 0.0f, true);
        this.drawGameInformation($$0);
        this.drawSystemInformation($$0);
        if (this.minecraft.options.renderFpsChart) {
            int $$2 = this.minecraft.getWindow().getGuiScaledWidth();
            this.drawChart($$0, this.minecraft.getFrameTimer(), 0, $$2 / 2, true);
            IntegratedServer $$3 = this.minecraft.getSingleplayerServer();
            if ($$3 != null) {
                this.drawChart($$0, $$3.getFrameTimer(), $$2 - Math.min((int)($$2 / 2), (int)240), $$2 / 2, false);
            }
        }
        this.minecraft.getProfiler().pop();
    }

    protected void drawGameInformation(PoseStack $$0) {
        List<String> $$1 = this.getGameInformation();
        $$1.add((Object)"");
        boolean $$2 = this.minecraft.getSingleplayerServer() != null;
        $$1.add((Object)("Debug: Pie [shift]: " + (this.minecraft.options.renderDebugCharts ? "visible" : "hidden") + ($$2 ? " FPS + TPS" : " FPS") + " [alt]: " + (this.minecraft.options.renderFpsChart ? "visible" : "hidden")));
        $$1.add((Object)"For help: press F3 + Q");
        for (int $$3 = 0; $$3 < $$1.size(); ++$$3) {
            String $$4 = (String)$$1.get($$3);
            if (Strings.isNullOrEmpty((String)$$4)) continue;
            Objects.requireNonNull((Object)this.font);
            int $$5 = 9;
            int $$6 = this.font.width($$4);
            int $$7 = 2;
            int $$8 = 2 + $$5 * $$3;
            DebugScreenOverlay.fill($$0, 1, $$8 - 1, 2 + $$6 + 1, $$8 + $$5 - 1, -1873784752);
            this.font.draw($$0, $$4, 2.0f, (float)$$8, 0xE0E0E0);
        }
    }

    protected void drawSystemInformation(PoseStack $$0) {
        List<String> $$1 = this.getSystemInformation();
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            String $$3 = (String)$$1.get($$2);
            if (Strings.isNullOrEmpty((String)$$3)) continue;
            Objects.requireNonNull((Object)this.font);
            int $$4 = 9;
            int $$5 = this.font.width($$3);
            int $$6 = this.minecraft.getWindow().getGuiScaledWidth() - 2 - $$5;
            int $$7 = 2 + $$4 * $$2;
            DebugScreenOverlay.fill($$0, $$6 - 1, $$7 - 1, $$6 + $$5 + 1, $$7 + $$4 - 1, -1873784752);
            this.font.draw($$0, $$3, (float)$$6, (float)$$7, 0xE0E0E0);
        }
    }

    protected List<String> getGameInformation() {
        PostChain $$39;
        Level $$15;
        String $$13;
        String $$5;
        IntegratedServer $$0 = this.minecraft.getSingleplayerServer();
        Connection $$12 = this.minecraft.getConnection().getConnection();
        float $$2 = $$12.getAverageSentPackets();
        float $$3 = $$12.getAverageReceivedPackets();
        if ($$0 != null) {
            String $$4 = String.format((Locale)Locale.ROOT, (String)"Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", (Object[])new Object[]{Float.valueOf((float)$$0.getAverageTickTime()), Float.valueOf((float)$$2), Float.valueOf((float)$$3)});
        } else {
            $$5 = String.format((Locale)Locale.ROOT, (String)"\"%s\" server, %.0f tx, %.0f rx", (Object[])new Object[]{this.minecraft.player.getServerBrand(), Float.valueOf((float)$$2), Float.valueOf((float)$$3)});
        }
        BlockPos $$6 = this.minecraft.getCameraEntity().blockPosition();
        if (this.minecraft.showOnlyReducedInfo()) {
            return Lists.newArrayList((Object[])new String[]{"Minecraft " + SharedConstants.getCurrentVersion().getName() + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.minecraft.fpsString, $$5, this.minecraft.levelRenderer.getChunkStatistics(), this.minecraft.levelRenderer.getEntityStatistics(), "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(), this.minecraft.level.gatherChunkSourceStats(), "", String.format((Locale)Locale.ROOT, (String)"Chunk-relative: %d %d %d", (Object[])new Object[]{$$6.getX() & 0xF, $$6.getY() & 0xF, $$6.getZ() & 0xF})});
        }
        Entity $$7 = this.minecraft.getCameraEntity();
        Direction $$8 = $$7.getDirection();
        switch ($$8) {
            case NORTH: {
                String $$9 = "Towards negative Z";
                break;
            }
            case SOUTH: {
                String $$10 = "Towards positive Z";
                break;
            }
            case WEST: {
                String $$11 = "Towards negative X";
                break;
            }
            case EAST: {
                String $$122 = "Towards positive X";
                break;
            }
            default: {
                $$13 = "Invalid";
            }
        }
        ChunkPos $$14 = new ChunkPos($$6);
        if (!Objects.equals((Object)this.lastPos, (Object)$$14)) {
            this.lastPos = $$14;
            this.clearChunkCache();
        }
        LongSets.EmptySet $$16 = ($$15 = this.getLevel()) instanceof ServerLevel ? ((ServerLevel)$$15).getForcedChunks() : LongSets.EMPTY_SET;
        ArrayList $$17 = Lists.newArrayList((Object[])new String[]{"Minecraft " + SharedConstants.getCurrentVersion().getName() + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType()) + ")", this.minecraft.fpsString, $$5, this.minecraft.levelRenderer.getChunkStatistics(), this.minecraft.levelRenderer.getEntityStatistics(), "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(), this.minecraft.level.gatherChunkSourceStats()});
        String $$18 = this.getServerChunkStats();
        if ($$18 != null) {
            $$17.add((Object)$$18);
        }
        $$17.add((Object)(this.minecraft.level.dimension().location() + " FC: " + $$16.size()));
        $$17.add((Object)"");
        $$17.add((Object)String.format((Locale)Locale.ROOT, (String)"XYZ: %.3f / %.5f / %.3f", (Object[])new Object[]{this.minecraft.getCameraEntity().getX(), this.minecraft.getCameraEntity().getY(), this.minecraft.getCameraEntity().getZ()}));
        $$17.add((Object)String.format((Locale)Locale.ROOT, (String)"Block: %d %d %d [%d %d %d]", (Object[])new Object[]{$$6.getX(), $$6.getY(), $$6.getZ(), $$6.getX() & 0xF, $$6.getY() & 0xF, $$6.getZ() & 0xF}));
        $$17.add((Object)String.format((Locale)Locale.ROOT, (String)"Chunk: %d %d %d [%d %d in r.%d.%d.mca]", (Object[])new Object[]{$$14.x, SectionPos.blockToSectionCoord($$6.getY()), $$14.z, $$14.getRegionLocalX(), $$14.getRegionLocalZ(), $$14.getRegionX(), $$14.getRegionZ()}));
        $$17.add((Object)String.format((Locale)Locale.ROOT, (String)"Facing: %s (%s) (%.1f / %.1f)", (Object[])new Object[]{$$8, $$13, Float.valueOf((float)Mth.wrapDegrees($$7.getYRot())), Float.valueOf((float)Mth.wrapDegrees($$7.getXRot()))}));
        LevelChunk $$19 = this.getClientChunk();
        if ($$19.isEmpty()) {
            $$17.add((Object)"Waiting for chunk...");
        } else {
            int $$20 = this.minecraft.level.getChunkSource().getLightEngine().getRawBrightness($$6, 0);
            int $$21 = this.minecraft.level.getBrightness(LightLayer.SKY, $$6);
            int $$22 = this.minecraft.level.getBrightness(LightLayer.BLOCK, $$6);
            $$17.add((Object)("Client Light: " + $$20 + " (" + $$21 + " sky, " + $$22 + " block)"));
            LevelChunk $$23 = this.getServerChunk();
            StringBuilder $$24 = new StringBuilder("CH");
            for (Heightmap.Types $$25 : Heightmap.Types.values()) {
                if (!$$25.sendToClient()) continue;
                $$24.append(" ").append((String)HEIGHTMAP_NAMES.get((Object)$$25)).append(": ").append($$19.getHeight($$25, $$6.getX(), $$6.getZ()));
            }
            $$17.add((Object)$$24.toString());
            $$24.setLength(0);
            $$24.append("SH");
            for (Heightmap.Types $$26 : Heightmap.Types.values()) {
                if (!$$26.keepAfterWorldgen()) continue;
                $$24.append(" ").append((String)HEIGHTMAP_NAMES.get((Object)$$26)).append(": ");
                if ($$23 != null) {
                    $$24.append($$23.getHeight($$26, $$6.getX(), $$6.getZ()));
                    continue;
                }
                $$24.append("??");
            }
            $$17.add((Object)$$24.toString());
            if ($$6.getY() >= this.minecraft.level.getMinBuildHeight() && $$6.getY() < this.minecraft.level.getMaxBuildHeight()) {
                $$17.add((Object)("Biome: " + DebugScreenOverlay.printBiome(this.minecraft.level.getBiome($$6))));
                long $$27 = 0L;
                float $$28 = 0.0f;
                if ($$23 != null) {
                    $$28 = $$15.getMoonBrightness();
                    $$27 = $$23.getInhabitedTime();
                }
                DifficultyInstance $$29 = new DifficultyInstance($$15.getDifficulty(), $$15.getDayTime(), $$27, $$28);
                $$17.add((Object)String.format((Locale)Locale.ROOT, (String)"Local Difficulty: %.2f // %.2f (Day %d)", (Object[])new Object[]{Float.valueOf((float)$$29.getEffectiveDifficulty()), Float.valueOf((float)$$29.getSpecialMultiplier()), this.minecraft.level.getDayTime() / 24000L}));
            }
            if ($$23 != null && $$23.isOldNoiseGeneration()) {
                $$17.add((Object)"Blending: Old");
            }
        }
        ServerLevel $$30 = this.getServerLevel();
        if ($$30 != null) {
            ServerChunkCache $$31 = $$30.getChunkSource();
            ChunkGenerator $$32 = $$31.getGenerator();
            RandomState $$33 = $$31.randomState();
            $$32.addDebugScreenInfo((List<String>)$$17, $$33, $$6);
            Climate.Sampler $$34 = $$33.sampler();
            BiomeSource $$35 = $$32.getBiomeSource();
            $$35.addDebugInfo((List<String>)$$17, $$6, $$34);
            NaturalSpawner.SpawnState $$36 = $$31.getLastSpawnState();
            if ($$36 != null) {
                Object2IntMap<MobCategory> $$37 = $$36.getMobCategoryCounts();
                int $$38 = $$36.getSpawnableChunkCount();
                $$17.add((Object)("SC: " + $$38 + ", " + (String)Stream.of((Object[])MobCategory.values()).map($$1 -> Character.toUpperCase((char)$$1.getName().charAt(0)) + ": " + $$37.getInt($$1)).collect(Collectors.joining((CharSequence)", "))));
            } else {
                $$17.add((Object)"SC: N/A");
            }
        }
        if (($$39 = this.minecraft.gameRenderer.currentEffect()) != null) {
            $$17.add((Object)("Shader: " + $$39.getName()));
        }
        $$17.add((Object)(this.minecraft.getSoundManager().getDebugString() + String.format((Locale)Locale.ROOT, (String)" (Mood %d%%)", (Object[])new Object[]{Math.round((float)(this.minecraft.player.getCurrentMood() * 100.0f))})));
        return $$17;
    }

    private static String printBiome(Holder<Biome> $$02) {
        return (String)$$02.unwrap().map($$0 -> $$0.location().toString(), $$0 -> "[unregistered " + $$0 + "]");
    }

    @Nullable
    private ServerLevel getServerLevel() {
        IntegratedServer $$0 = this.minecraft.getSingleplayerServer();
        if ($$0 != null) {
            return $$0.getLevel(this.minecraft.level.dimension());
        }
        return null;
    }

    @Nullable
    private String getServerChunkStats() {
        ServerLevel $$0 = this.getServerLevel();
        if ($$0 != null) {
            return $$0.gatherChunkSourceStats();
        }
        return null;
    }

    private Level getLevel() {
        return (Level)DataFixUtils.orElse((Optional)Optional.ofNullable((Object)this.minecraft.getSingleplayerServer()).flatMap($$0 -> Optional.ofNullable((Object)$$0.getLevel(this.minecraft.level.dimension()))), (Object)this.minecraft.level);
    }

    @Nullable
    private LevelChunk getServerChunk() {
        if (this.serverChunk == null) {
            ServerLevel $$0 = this.getServerLevel();
            if ($$0 != null) {
                this.serverChunk = $$0.getChunkSource().getChunkFuture(this.lastPos.x, this.lastPos.z, ChunkStatus.FULL, false).thenApply($$02 -> (LevelChunk)$$02.map($$0 -> (LevelChunk)$$0, $$0 -> null));
            }
            if (this.serverChunk == null) {
                this.serverChunk = CompletableFuture.completedFuture((Object)this.getClientChunk());
            }
        }
        return (LevelChunk)this.serverChunk.getNow(null);
    }

    private LevelChunk getClientChunk() {
        if (this.clientChunk == null) {
            this.clientChunk = this.minecraft.level.getChunk(this.lastPos.x, this.lastPos.z);
        }
        return this.clientChunk;
    }

    protected List<String> getSystemInformation() {
        Entity $$11;
        long $$02 = Runtime.getRuntime().maxMemory();
        long $$1 = Runtime.getRuntime().totalMemory();
        long $$2 = Runtime.getRuntime().freeMemory();
        long $$3 = $$1 - $$2;
        ArrayList $$4 = Lists.newArrayList((Object[])new String[]{String.format((Locale)Locale.ROOT, (String)"Java: %s %dbit", (Object[])new Object[]{System.getProperty((String)"java.version"), this.minecraft.is64Bit() ? 64 : 32}), String.format((Locale)Locale.ROOT, (String)"Mem: % 2d%% %03d/%03dMB", (Object[])new Object[]{$$3 * 100L / $$02, DebugScreenOverlay.bytesToMegabytes($$3), DebugScreenOverlay.bytesToMegabytes($$02)}), String.format((Locale)Locale.ROOT, (String)"Allocation rate: %03dMB /s", (Object[])new Object[]{DebugScreenOverlay.bytesToMegabytes(this.allocationRateCalculator.bytesAllocatedPerSecond($$3))}), String.format((Locale)Locale.ROOT, (String)"Allocated: % 2d%% %03dMB", (Object[])new Object[]{$$1 * 100L / $$02, DebugScreenOverlay.bytesToMegabytes($$1)}), "", String.format((Locale)Locale.ROOT, (String)"CPU: %s", (Object[])new Object[]{GlUtil.getCpuInfo()}), "", String.format((Locale)Locale.ROOT, (String)"Display: %dx%d (%s)", (Object[])new Object[]{Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), GlUtil.getVendor()}), GlUtil.getRenderer(), GlUtil.getOpenGLVersion()});
        if (this.minecraft.showOnlyReducedInfo()) {
            return $$4;
        }
        if (this.block.getType() == HitResult.Type.BLOCK) {
            BlockPos $$5 = ((BlockHitResult)this.block).getBlockPos();
            BlockState $$6 = this.minecraft.level.getBlockState($$5);
            $$4.add((Object)"");
            $$4.add((Object)(ChatFormatting.UNDERLINE + "Targeted Block: " + $$5.getX() + ", " + $$5.getY() + ", " + $$5.getZ()));
            $$4.add((Object)String.valueOf((Object)BuiltInRegistries.BLOCK.getKey($$6.getBlock())));
            for (Map.Entry $$7 : $$6.getValues().entrySet()) {
                $$4.add((Object)this.getPropertyValueString($$7));
            }
            $$6.getTags().map($$0 -> "#" + $$0.location()).forEach(arg_0 -> ((List)$$4).add(arg_0));
        }
        if (this.liquid.getType() == HitResult.Type.BLOCK) {
            BlockPos $$8 = ((BlockHitResult)this.liquid).getBlockPos();
            FluidState $$9 = this.minecraft.level.getFluidState($$8);
            $$4.add((Object)"");
            $$4.add((Object)(ChatFormatting.UNDERLINE + "Targeted Fluid: " + $$8.getX() + ", " + $$8.getY() + ", " + $$8.getZ()));
            $$4.add((Object)String.valueOf((Object)BuiltInRegistries.FLUID.getKey($$9.getType())));
            for (Map.Entry $$10 : $$9.getValues().entrySet()) {
                $$4.add((Object)this.getPropertyValueString($$10));
            }
            $$9.getTags().map($$0 -> "#" + $$0.location()).forEach(arg_0 -> ((List)$$4).add(arg_0));
        }
        if (($$11 = this.minecraft.crosshairPickEntity) != null) {
            $$4.add((Object)"");
            $$4.add((Object)(ChatFormatting.UNDERLINE + "Targeted Entity"));
            $$4.add((Object)String.valueOf((Object)BuiltInRegistries.ENTITY_TYPE.getKey($$11.getType())));
        }
        return $$4;
    }

    private String getPropertyValueString(Map.Entry<Property<?>, Comparable<?>> $$0) {
        Property $$1 = (Property)$$0.getKey();
        Comparable $$2 = (Comparable)$$0.getValue();
        String $$3 = Util.getPropertyName($$1, $$2);
        if (Boolean.TRUE.equals((Object)$$2)) {
            $$3 = ChatFormatting.GREEN + $$3;
        } else if (Boolean.FALSE.equals((Object)$$2)) {
            $$3 = ChatFormatting.RED + $$3;
        }
        return $$1.getName() + ": " + $$3;
    }

    private void drawChart(PoseStack $$0, FrameTimer $$1, int $$2, int $$3, boolean $$4) {
        RenderSystem.disableDepthTest();
        int $$5 = $$1.getLogStart();
        int $$6 = $$1.getLogEnd();
        long[] $$7 = $$1.getLog();
        int $$8 = $$5;
        int $$9 = $$2;
        int $$10 = Math.max((int)0, (int)($$7.length - $$3));
        int $$11 = $$7.length - $$10;
        $$8 = $$1.wrapIndex($$8 + $$10);
        long $$12 = 0L;
        int $$13 = Integer.MAX_VALUE;
        int $$14 = Integer.MIN_VALUE;
        for (int $$15 = 0; $$15 < $$11; ++$$15) {
            int $$16 = (int)($$7[$$1.wrapIndex($$8 + $$15)] / 1000000L);
            $$13 = Math.min((int)$$13, (int)$$16);
            $$14 = Math.max((int)$$14, (int)$$16);
            $$12 += (long)$$16;
        }
        int $$17 = this.minecraft.getWindow().getGuiScaledHeight();
        DebugScreenOverlay.fill($$0, $$2, $$17 - 60, $$2 + $$11, $$17, -1873784752);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        BufferBuilder $$18 = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        $$18.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f $$19 = Transformation.identity().getMatrix();
        while ($$8 != $$6) {
            int $$20 = $$1.scaleSampleTo($$7[$$8], $$4 ? 30 : 60, $$4 ? 60 : 20);
            int $$21 = $$4 ? 100 : 60;
            int $$22 = this.getSampleColor(Mth.clamp($$20, 0, $$21), 0, $$21 / 2, $$21);
            int $$23 = $$22 >> 24 & 0xFF;
            int $$24 = $$22 >> 16 & 0xFF;
            int $$25 = $$22 >> 8 & 0xFF;
            int $$26 = $$22 & 0xFF;
            $$18.vertex($$19, $$9 + 1, $$17, 0.0f).color($$24, $$25, $$26, $$23).endVertex();
            $$18.vertex($$19, $$9 + 1, $$17 - $$20 + 1, 0.0f).color($$24, $$25, $$26, $$23).endVertex();
            $$18.vertex($$19, $$9, $$17 - $$20 + 1, 0.0f).color($$24, $$25, $$26, $$23).endVertex();
            $$18.vertex($$19, $$9, $$17, 0.0f).color($$24, $$25, $$26, $$23).endVertex();
            ++$$9;
            $$8 = $$1.wrapIndex($$8 + 1);
        }
        BufferUploader.drawWithShader($$18.end());
        RenderSystem.disableBlend();
        if ($$4) {
            DebugScreenOverlay.fill($$0, $$2 + 1, $$17 - 30 + 1, $$2 + 14, $$17 - 30 + 10, -1873784752);
            this.font.draw($$0, "60 FPS", (float)($$2 + 2), (float)($$17 - 30 + 2), 0xE0E0E0);
            this.hLine($$0, $$2, $$2 + $$11 - 1, $$17 - 30, -1);
            DebugScreenOverlay.fill($$0, $$2 + 1, $$17 - 60 + 1, $$2 + 14, $$17 - 60 + 10, -1873784752);
            this.font.draw($$0, "30 FPS", (float)($$2 + 2), (float)($$17 - 60 + 2), 0xE0E0E0);
            this.hLine($$0, $$2, $$2 + $$11 - 1, $$17 - 60, -1);
        } else {
            DebugScreenOverlay.fill($$0, $$2 + 1, $$17 - 60 + 1, $$2 + 14, $$17 - 60 + 10, -1873784752);
            this.font.draw($$0, "20 TPS", (float)($$2 + 2), (float)($$17 - 60 + 2), 0xE0E0E0);
            this.hLine($$0, $$2, $$2 + $$11 - 1, $$17 - 60, -1);
        }
        this.hLine($$0, $$2, $$2 + $$11 - 1, $$17 - 1, -1);
        this.vLine($$0, $$2, $$17 - 60, $$17, -1);
        this.vLine($$0, $$2 + $$11 - 1, $$17 - 60, $$17, -1);
        int $$27 = this.minecraft.options.framerateLimit().get();
        if ($$4 && $$27 > 0 && $$27 <= 250) {
            this.hLine($$0, $$2, $$2 + $$11 - 1, $$17 - 1 - (int)(1800.0 / (double)$$27), -16711681);
        }
        String $$28 = $$13 + " ms min";
        String $$29 = $$12 / (long)$$11 + " ms avg";
        String $$30 = $$14 + " ms max";
        float f = $$2 + 2;
        Objects.requireNonNull((Object)this.font);
        this.font.drawShadow($$0, $$28, f, (float)($$17 - 60 - 9), 0xE0E0E0);
        float f2 = $$2 + $$11 / 2 - this.font.width($$29) / 2;
        Objects.requireNonNull((Object)this.font);
        this.font.drawShadow($$0, $$29, f2, (float)($$17 - 60 - 9), 0xE0E0E0);
        float f3 = $$2 + $$11 - this.font.width($$30);
        Objects.requireNonNull((Object)this.font);
        this.font.drawShadow($$0, $$30, f3, (float)($$17 - 60 - 9), 0xE0E0E0);
        RenderSystem.enableDepthTest();
    }

    private int getSampleColor(int $$0, int $$1, int $$2, int $$3) {
        if ($$0 < $$2) {
            return this.colorLerp(-16711936, -256, (float)$$0 / (float)$$2);
        }
        return this.colorLerp(-256, -65536, (float)($$0 - $$2) / (float)($$3 - $$2));
    }

    private int colorLerp(int $$0, int $$1, float $$2) {
        int $$3 = $$0 >> 24 & 0xFF;
        int $$4 = $$0 >> 16 & 0xFF;
        int $$5 = $$0 >> 8 & 0xFF;
        int $$6 = $$0 & 0xFF;
        int $$7 = $$1 >> 24 & 0xFF;
        int $$8 = $$1 >> 16 & 0xFF;
        int $$9 = $$1 >> 8 & 0xFF;
        int $$10 = $$1 & 0xFF;
        int $$11 = Mth.clamp((int)Mth.lerp($$2, $$3, $$7), 0, 255);
        int $$12 = Mth.clamp((int)Mth.lerp($$2, $$4, $$8), 0, 255);
        int $$13 = Mth.clamp((int)Mth.lerp($$2, $$5, $$9), 0, 255);
        int $$14 = Mth.clamp((int)Mth.lerp($$2, $$6, $$10), 0, 255);
        return $$11 << 24 | $$12 << 16 | $$13 << 8 | $$14;
    }

    private static long bytesToMegabytes(long $$0) {
        return $$0 / 1024L / 1024L;
    }

    static class AllocationRateCalculator {
        private static final int UPDATE_INTERVAL_MS = 500;
        private static final List<GarbageCollectorMXBean> GC_MBEANS = ManagementFactory.getGarbageCollectorMXBeans();
        private long lastTime = 0L;
        private long lastHeapUsage = -1L;
        private long lastGcCounts = -1L;
        private long lastRate = 0L;

        AllocationRateCalculator() {
        }

        long bytesAllocatedPerSecond(long $$0) {
            long $$1 = System.currentTimeMillis();
            if ($$1 - this.lastTime < 500L) {
                return this.lastRate;
            }
            long $$2 = AllocationRateCalculator.gcCounts();
            if (this.lastTime != 0L && $$2 == this.lastGcCounts) {
                double $$3 = (double)TimeUnit.SECONDS.toMillis(1L) / (double)($$1 - this.lastTime);
                long $$4 = $$0 - this.lastHeapUsage;
                this.lastRate = Math.round((double)((double)$$4 * $$3));
            }
            this.lastTime = $$1;
            this.lastHeapUsage = $$0;
            this.lastGcCounts = $$2;
            return this.lastRate;
        }

        private static long gcCounts() {
            long $$0 = 0L;
            for (GarbageCollectorMXBean $$1 : GC_MBEANS) {
                $$0 += $$1.getCollectionCount();
            }
            return $$0;
        }
    }
}
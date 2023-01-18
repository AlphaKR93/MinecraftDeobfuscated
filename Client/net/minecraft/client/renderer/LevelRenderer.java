/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  java.io.IOException
 *  java.lang.AutoCloseable
 *  java.lang.Double
 *  java.lang.Exception
 *  java.lang.Float
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.util.ArrayDeque
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Comparator
 *  java.util.LinkedHashSet
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Queue
 *  java.util.Set
 *  java.util.SortedSet
 *  java.util.concurrent.BlockingQueue
 *  java.util.concurrent.Executor
 *  java.util.concurrent.Future
 *  java.util.concurrent.LinkedBlockingQueue
 *  java.util.concurrent.atomic.AtomicBoolean
 *  java.util.concurrent.atomic.AtomicLong
 *  java.util.concurrent.atomic.AtomicReference
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3d
 *  org.joml.Vector4f
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RunningTrimmedMean;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3d;
import org.joml.Vector4f;
import org.slf4j.Logger;

public class LevelRenderer
implements ResourceManagerReloadListener,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int CHUNK_SIZE = 16;
    private static final int HALF_CHUNK_SIZE = 8;
    private static final float SKY_DISC_RADIUS = 512.0f;
    private static final int MINIMUM_ADVANCED_CULLING_DISTANCE = 60;
    private static final double CEILED_SECTION_DIAGONAL = Math.ceil((double)(Math.sqrt((double)3.0) * 16.0));
    private static final int MIN_FOG_DISTANCE = 32;
    private static final int RAIN_RADIUS = 10;
    private static final int RAIN_DIAMETER = 21;
    private static final int TRANSPARENT_SORT_COUNT = 15;
    private static final int HALF_A_SECOND_IN_MILLIS = 500;
    private static final ResourceLocation MOON_LOCATION = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation CLOUDS_LOCATION = new ResourceLocation("textures/environment/clouds.png");
    private static final ResourceLocation END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation FORCEFIELD_LOCATION = new ResourceLocation("textures/misc/forcefield.png");
    private static final ResourceLocation RAIN_LOCATION = new ResourceLocation("textures/environment/rain.png");
    private static final ResourceLocation SNOW_LOCATION = new ResourceLocation("textures/environment/snow.png");
    public static final Direction[] DIRECTIONS = Direction.values();
    private final Minecraft minecraft;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final RenderBuffers renderBuffers;
    @Nullable
    private ClientLevel level;
    private final BlockingQueue<ChunkRenderDispatcher.RenderChunk> recentlyCompiledChunks = new LinkedBlockingQueue();
    private final AtomicReference<RenderChunkStorage> renderChunkStorage = new AtomicReference();
    private final ObjectArrayList<RenderChunkInfo> renderChunksInFrustum = new ObjectArrayList(10000);
    private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
    @Nullable
    private Future<?> lastFullRenderChunkUpdate;
    @Nullable
    private ViewArea viewArea;
    @Nullable
    private VertexBuffer starBuffer;
    @Nullable
    private VertexBuffer skyBuffer;
    @Nullable
    private VertexBuffer darkBuffer;
    private boolean generateClouds = true;
    @Nullable
    private VertexBuffer cloudBuffer;
    private final RunningTrimmedMean frameTimes = new RunningTrimmedMean(100);
    private int ticks;
    private final Int2ObjectMap<BlockDestructionProgress> destroyingBlocks = new Int2ObjectOpenHashMap();
    private final Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress = new Long2ObjectOpenHashMap();
    private final Map<BlockPos, SoundInstance> playingRecords = Maps.newHashMap();
    @Nullable
    private RenderTarget entityTarget;
    @Nullable
    private PostChain entityEffect;
    @Nullable
    private RenderTarget translucentTarget;
    @Nullable
    private RenderTarget itemEntityTarget;
    @Nullable
    private RenderTarget particlesTarget;
    @Nullable
    private RenderTarget weatherTarget;
    @Nullable
    private RenderTarget cloudsTarget;
    @Nullable
    private PostChain transparencyChain;
    private double lastCameraX = Double.MIN_VALUE;
    private double lastCameraY = Double.MIN_VALUE;
    private double lastCameraZ = Double.MIN_VALUE;
    private int lastCameraChunkX = Integer.MIN_VALUE;
    private int lastCameraChunkY = Integer.MIN_VALUE;
    private int lastCameraChunkZ = Integer.MIN_VALUE;
    private double prevCamX = Double.MIN_VALUE;
    private double prevCamY = Double.MIN_VALUE;
    private double prevCamZ = Double.MIN_VALUE;
    private double prevCamRotX = Double.MIN_VALUE;
    private double prevCamRotY = Double.MIN_VALUE;
    private int prevCloudX = Integer.MIN_VALUE;
    private int prevCloudY = Integer.MIN_VALUE;
    private int prevCloudZ = Integer.MIN_VALUE;
    private Vec3 prevCloudColor = Vec3.ZERO;
    @Nullable
    private CloudStatus prevCloudsType;
    @Nullable
    private ChunkRenderDispatcher chunkRenderDispatcher;
    private int lastViewDistance = -1;
    private int renderedEntities;
    private int culledEntities;
    private Frustum cullingFrustum;
    private boolean captureFrustum;
    @Nullable
    private Frustum capturedFrustum;
    private final Vector4f[] frustumPoints = new Vector4f[8];
    private final Vector3d frustumPos = new Vector3d(0.0, 0.0, 0.0);
    private double xTransparentOld;
    private double yTransparentOld;
    private double zTransparentOld;
    private boolean needsFullRenderChunkUpdate = true;
    private final AtomicLong nextFullUpdateMillis = new AtomicLong(0L);
    private final AtomicBoolean needsFrustumUpdate = new AtomicBoolean(false);
    private int rainSoundTime;
    private final float[] rainSizeX = new float[1024];
    private final float[] rainSizeZ = new float[1024];

    public LevelRenderer(Minecraft $$0, EntityRenderDispatcher $$1, BlockEntityRenderDispatcher $$2, RenderBuffers $$3) {
        this.minecraft = $$0;
        this.entityRenderDispatcher = $$1;
        this.blockEntityRenderDispatcher = $$2;
        this.renderBuffers = $$3;
        for (int $$4 = 0; $$4 < 32; ++$$4) {
            for (int $$5 = 0; $$5 < 32; ++$$5) {
                float $$6 = $$5 - 16;
                float $$7 = $$4 - 16;
                float $$8 = Mth.sqrt($$6 * $$6 + $$7 * $$7);
                this.rainSizeX[$$4 << 5 | $$5] = -$$7 / $$8;
                this.rainSizeZ[$$4 << 5 | $$5] = $$6 / $$8;
            }
        }
        this.createStars();
        this.createLightSky();
        this.createDarkSky();
    }

    private void renderSnowAndRain(LightTexture $$0, float $$1, double $$2, double $$3, double $$4) {
        float $$5 = this.minecraft.level.getRainLevel($$1);
        if ($$5 <= 0.0f) {
            return;
        }
        $$0.turnOnLightLayer();
        ClientLevel $$6 = this.minecraft.level;
        int $$7 = Mth.floor($$2);
        int $$8 = Mth.floor($$3);
        int $$9 = Mth.floor($$4);
        Tesselator $$10 = Tesselator.getInstance();
        BufferBuilder $$11 = $$10.getBuilder();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int $$12 = 5;
        if (Minecraft.useFancyGraphics()) {
            $$12 = 10;
        }
        RenderSystem.depthMask(Minecraft.useShaderTransparency());
        int $$13 = -1;
        float $$14 = (float)this.ticks + $$1;
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getParticleShader));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        BlockPos.MutableBlockPos $$15 = new BlockPos.MutableBlockPos();
        for (int $$16 = $$9 - $$12; $$16 <= $$9 + $$12; ++$$16) {
            for (int $$17 = $$7 - $$12; $$17 <= $$7 + $$12; ++$$17) {
                int $$25;
                int $$18 = ($$16 - $$9 + 16) * 32 + $$17 - $$7 + 16;
                double $$19 = (double)this.rainSizeX[$$18] * 0.5;
                double $$20 = (double)this.rainSizeZ[$$18] * 0.5;
                $$15.set((double)$$17, $$3, (double)$$16);
                Biome $$21 = (Biome)$$6.getBiome($$15).value();
                if ($$21.getPrecipitation() == Biome.Precipitation.NONE) continue;
                int $$22 = $$6.getHeight(Heightmap.Types.MOTION_BLOCKING, $$17, $$16);
                int $$23 = $$8 - $$12;
                int $$24 = $$8 + $$12;
                if ($$23 < $$22) {
                    $$23 = $$22;
                }
                if ($$24 < $$22) {
                    $$24 = $$22;
                }
                if (($$25 = $$22) < $$8) {
                    $$25 = $$8;
                }
                if ($$23 == $$24) continue;
                RandomSource $$26 = RandomSource.create($$17 * $$17 * 3121 + $$17 * 45238971 ^ $$16 * $$16 * 418711 + $$16 * 13761);
                $$15.set($$17, $$23, $$16);
                if ($$21.warmEnoughToRain($$15)) {
                    if ($$13 != 0) {
                        if ($$13 >= 0) {
                            $$10.end();
                        }
                        $$13 = 0;
                        RenderSystem.setShaderTexture(0, RAIN_LOCATION);
                        $$11.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                    }
                    int $$27 = this.ticks + $$17 * $$17 * 3121 + $$17 * 45238971 + $$16 * $$16 * 418711 + $$16 * 13761 & 0x1F;
                    float $$28 = -((float)$$27 + $$1) / 32.0f * (3.0f + $$26.nextFloat());
                    double $$29 = (double)$$17 + 0.5 - $$2;
                    double $$30 = (double)$$16 + 0.5 - $$4;
                    float $$31 = (float)Math.sqrt((double)($$29 * $$29 + $$30 * $$30)) / (float)$$12;
                    float $$32 = ((1.0f - $$31 * $$31) * 0.5f + 0.5f) * $$5;
                    $$15.set($$17, $$25, $$16);
                    int $$33 = LevelRenderer.getLightColor($$6, $$15);
                    $$11.vertex((double)$$17 - $$2 - $$19 + 0.5, (double)$$24 - $$3, (double)$$16 - $$4 - $$20 + 0.5).uv(0.0f, (float)$$23 * 0.25f + $$28).color(1.0f, 1.0f, 1.0f, $$32).uv2($$33).endVertex();
                    $$11.vertex((double)$$17 - $$2 + $$19 + 0.5, (double)$$24 - $$3, (double)$$16 - $$4 + $$20 + 0.5).uv(1.0f, (float)$$23 * 0.25f + $$28).color(1.0f, 1.0f, 1.0f, $$32).uv2($$33).endVertex();
                    $$11.vertex((double)$$17 - $$2 + $$19 + 0.5, (double)$$23 - $$3, (double)$$16 - $$4 + $$20 + 0.5).uv(1.0f, (float)$$24 * 0.25f + $$28).color(1.0f, 1.0f, 1.0f, $$32).uv2($$33).endVertex();
                    $$11.vertex((double)$$17 - $$2 - $$19 + 0.5, (double)$$23 - $$3, (double)$$16 - $$4 - $$20 + 0.5).uv(0.0f, (float)$$24 * 0.25f + $$28).color(1.0f, 1.0f, 1.0f, $$32).uv2($$33).endVertex();
                    continue;
                }
                if ($$13 != 1) {
                    if ($$13 >= 0) {
                        $$10.end();
                    }
                    $$13 = 1;
                    RenderSystem.setShaderTexture(0, SNOW_LOCATION);
                    $$11.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                }
                float $$34 = -((float)(this.ticks & 0x1FF) + $$1) / 512.0f;
                float $$35 = (float)($$26.nextDouble() + (double)$$14 * 0.01 * (double)((float)$$26.nextGaussian()));
                float $$36 = (float)($$26.nextDouble() + (double)($$14 * (float)$$26.nextGaussian()) * 0.001);
                double $$37 = (double)$$17 + 0.5 - $$2;
                double $$38 = (double)$$16 + 0.5 - $$4;
                float $$39 = (float)Math.sqrt((double)($$37 * $$37 + $$38 * $$38)) / (float)$$12;
                float $$40 = ((1.0f - $$39 * $$39) * 0.3f + 0.5f) * $$5;
                $$15.set($$17, $$25, $$16);
                int $$41 = LevelRenderer.getLightColor($$6, $$15);
                int $$42 = $$41 >> 16 & 0xFFFF;
                int $$43 = $$41 & 0xFFFF;
                int $$44 = ($$42 * 3 + 240) / 4;
                int $$45 = ($$43 * 3 + 240) / 4;
                $$11.vertex((double)$$17 - $$2 - $$19 + 0.5, (double)$$24 - $$3, (double)$$16 - $$4 - $$20 + 0.5).uv(0.0f + $$35, (float)$$23 * 0.25f + $$34 + $$36).color(1.0f, 1.0f, 1.0f, $$40).uv2($$45, $$44).endVertex();
                $$11.vertex((double)$$17 - $$2 + $$19 + 0.5, (double)$$24 - $$3, (double)$$16 - $$4 + $$20 + 0.5).uv(1.0f + $$35, (float)$$23 * 0.25f + $$34 + $$36).color(1.0f, 1.0f, 1.0f, $$40).uv2($$45, $$44).endVertex();
                $$11.vertex((double)$$17 - $$2 + $$19 + 0.5, (double)$$23 - $$3, (double)$$16 - $$4 + $$20 + 0.5).uv(1.0f + $$35, (float)$$24 * 0.25f + $$34 + $$36).color(1.0f, 1.0f, 1.0f, $$40).uv2($$45, $$44).endVertex();
                $$11.vertex((double)$$17 - $$2 - $$19 + 0.5, (double)$$23 - $$3, (double)$$16 - $$4 - $$20 + 0.5).uv(0.0f + $$35, (float)$$24 * 0.25f + $$34 + $$36).color(1.0f, 1.0f, 1.0f, $$40).uv2($$45, $$44).endVertex();
            }
        }
        if ($$13 >= 0) {
            $$10.end();
        }
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        $$0.turnOffLightLayer();
    }

    public void tickRain(Camera $$0) {
        float $$1 = this.minecraft.level.getRainLevel(1.0f) / (Minecraft.useFancyGraphics() ? 1.0f : 2.0f);
        if ($$1 <= 0.0f) {
            return;
        }
        RandomSource $$2 = RandomSource.create((long)this.ticks * 312987231L);
        ClientLevel $$3 = this.minecraft.level;
        BlockPos $$4 = new BlockPos($$0.getPosition());
        Vec3i $$5 = null;
        int $$6 = (int)(100.0f * $$1 * $$1) / (this.minecraft.options.particles().get() == ParticleStatus.DECREASED ? 2 : 1);
        for (int $$7 = 0; $$7 < $$6; ++$$7) {
            int $$8 = $$2.nextInt(21) - 10;
            int $$9 = $$2.nextInt(21) - 10;
            BlockPos $$10 = $$3.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, $$4.offset($$8, 0, $$9));
            Biome $$11 = $$3.getBiome($$10).value();
            if ($$10.getY() <= $$3.getMinBuildHeight() || $$10.getY() > $$4.getY() + 10 || $$10.getY() < $$4.getY() - 10 || $$11.getPrecipitation() != Biome.Precipitation.RAIN || !$$11.warmEnoughToRain($$10)) continue;
            $$5 = $$10.below();
            if (this.minecraft.options.particles().get() == ParticleStatus.MINIMAL) break;
            double $$12 = $$2.nextDouble();
            double $$13 = $$2.nextDouble();
            BlockState $$14 = $$3.getBlockState((BlockPos)$$5);
            FluidState $$15 = $$3.getFluidState((BlockPos)$$5);
            VoxelShape $$16 = $$14.getCollisionShape($$3, (BlockPos)$$5);
            double $$17 = $$16.max(Direction.Axis.Y, $$12, $$13);
            double $$18 = $$15.getHeight($$3, (BlockPos)$$5);
            double $$19 = Math.max((double)$$17, (double)$$18);
            SimpleParticleType $$20 = $$15.is(FluidTags.LAVA) || $$14.is(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire($$14) ? ParticleTypes.SMOKE : ParticleTypes.RAIN;
            this.minecraft.level.addParticle($$20, (double)$$5.getX() + $$12, (double)$$5.getY() + $$19, (double)$$5.getZ() + $$13, 0.0, 0.0, 0.0);
        }
        if ($$5 != null && $$2.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            if ($$5.getY() > $$4.getY() + 1 && $$3.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, $$4).getY() > Mth.floor($$4.getY())) {
                this.minecraft.level.playLocalSound((BlockPos)$$5, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1f, 0.5f, false);
            } else {
                this.minecraft.level.playLocalSound((BlockPos)$$5, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2f, 1.0f, false);
            }
        }
    }

    public void close() {
        if (this.entityEffect != null) {
            this.entityEffect.close();
        }
        if (this.transparencyChain != null) {
            this.transparencyChain.close();
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager $$0) {
        this.initOutline();
        if (Minecraft.useShaderTransparency()) {
            this.initTransparency();
        }
    }

    public void initOutline() {
        if (this.entityEffect != null) {
            this.entityEffect.close();
        }
        ResourceLocation $$0 = new ResourceLocation("shaders/post/entity_outline.json");
        try {
            this.entityEffect = new PostChain(this.minecraft.getTextureManager(), this.minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), $$0);
            this.entityEffect.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
            this.entityTarget = this.entityEffect.getTempTarget("final");
        }
        catch (IOException $$1) {
            LOGGER.warn("Failed to load shader: {}", (Object)$$0, (Object)$$1);
            this.entityEffect = null;
            this.entityTarget = null;
        }
        catch (JsonSyntaxException $$2) {
            LOGGER.warn("Failed to parse shader: {}", (Object)$$0, (Object)$$2);
            this.entityEffect = null;
            this.entityTarget = null;
        }
    }

    private void initTransparency() {
        this.deinitTransparency();
        ResourceLocation $$02 = new ResourceLocation("shaders/post/transparency.json");
        try {
            PostChain $$1 = new PostChain(this.minecraft.getTextureManager(), this.minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), $$02);
            $$1.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
            RenderTarget $$2 = $$1.getTempTarget("translucent");
            RenderTarget $$3 = $$1.getTempTarget("itemEntity");
            RenderTarget $$4 = $$1.getTempTarget("particles");
            RenderTarget $$5 = $$1.getTempTarget("weather");
            RenderTarget $$6 = $$1.getTempTarget("clouds");
            this.transparencyChain = $$1;
            this.translucentTarget = $$2;
            this.itemEntityTarget = $$3;
            this.particlesTarget = $$4;
            this.weatherTarget = $$5;
            this.cloudsTarget = $$6;
        }
        catch (Exception $$7) {
            String $$8 = $$7 instanceof JsonSyntaxException ? "parse" : "load";
            String $$9 = "Failed to " + $$8 + " shader: " + $$02;
            TransparencyShaderException $$10 = new TransparencyShaderException($$9, $$7);
            if (this.minecraft.getResourcePackRepository().getSelectedIds().size() > 1) {
                Component $$11 = (Component)this.minecraft.getResourceManager().listPacks().findFirst().map($$0 -> Component.literal($$0.packId())).orElse(null);
                this.minecraft.options.graphicsMode().set(GraphicsStatus.FANCY);
                this.minecraft.clearResourcePacksOnError((Throwable)$$10, $$11);
            }
            CrashReport $$12 = this.minecraft.fillReport(new CrashReport($$9, (Throwable)$$10));
            this.minecraft.options.graphicsMode().set(GraphicsStatus.FANCY);
            this.minecraft.options.save();
            LOGGER.error(LogUtils.FATAL_MARKER, $$9, (Throwable)$$10);
            this.minecraft.emergencySave();
            Minecraft.crash($$12);
        }
    }

    private void deinitTransparency() {
        if (this.transparencyChain != null) {
            this.transparencyChain.close();
            this.translucentTarget.destroyBuffers();
            this.itemEntityTarget.destroyBuffers();
            this.particlesTarget.destroyBuffers();
            this.weatherTarget.destroyBuffers();
            this.cloudsTarget.destroyBuffers();
            this.transparencyChain = null;
            this.translucentTarget = null;
            this.itemEntityTarget = null;
            this.particlesTarget = null;
            this.weatherTarget = null;
            this.cloudsTarget = null;
        }
    }

    public void doEntityOutline() {
        if (this.shouldShowEntityOutlines()) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            this.entityTarget.blitToScreen(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), false);
            RenderSystem.disableBlend();
        }
    }

    protected boolean shouldShowEntityOutlines() {
        return !this.minecraft.gameRenderer.isPanoramicMode() && this.entityTarget != null && this.entityEffect != null && this.minecraft.player != null;
    }

    private void createDarkSky() {
        Tesselator $$0 = Tesselator.getInstance();
        BufferBuilder $$1 = $$0.getBuilder();
        if (this.darkBuffer != null) {
            this.darkBuffer.close();
        }
        this.darkBuffer = new VertexBuffer();
        BufferBuilder.RenderedBuffer $$2 = LevelRenderer.buildSkyDisc($$1, -16.0f);
        this.darkBuffer.bind();
        this.darkBuffer.upload($$2);
        VertexBuffer.unbind();
    }

    private void createLightSky() {
        Tesselator $$0 = Tesselator.getInstance();
        BufferBuilder $$1 = $$0.getBuilder();
        if (this.skyBuffer != null) {
            this.skyBuffer.close();
        }
        this.skyBuffer = new VertexBuffer();
        BufferBuilder.RenderedBuffer $$2 = LevelRenderer.buildSkyDisc($$1, 16.0f);
        this.skyBuffer.bind();
        this.skyBuffer.upload($$2);
        VertexBuffer.unbind();
    }

    private static BufferBuilder.RenderedBuffer buildSkyDisc(BufferBuilder $$0, float $$1) {
        float $$2 = Math.signum((float)$$1) * 512.0f;
        float $$3 = 512.0f;
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionShader));
        $$0.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        $$0.vertex(0.0, $$1, 0.0).endVertex();
        for (int $$4 = -180; $$4 <= 180; $$4 += 45) {
            $$0.vertex($$2 * Mth.cos((float)$$4 * ((float)Math.PI / 180)), $$1, 512.0f * Mth.sin((float)$$4 * ((float)Math.PI / 180))).endVertex();
        }
        return $$0.end();
    }

    private void createStars() {
        Tesselator $$0 = Tesselator.getInstance();
        BufferBuilder $$1 = $$0.getBuilder();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionShader));
        if (this.starBuffer != null) {
            this.starBuffer.close();
        }
        this.starBuffer = new VertexBuffer();
        BufferBuilder.RenderedBuffer $$2 = this.drawStars($$1);
        this.starBuffer.bind();
        this.starBuffer.upload($$2);
        VertexBuffer.unbind();
    }

    private BufferBuilder.RenderedBuffer drawStars(BufferBuilder $$0) {
        RandomSource $$1 = RandomSource.create(10842L);
        $$0.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        for (int $$2 = 0; $$2 < 1500; ++$$2) {
            double $$3 = $$1.nextFloat() * 2.0f - 1.0f;
            double $$4 = $$1.nextFloat() * 2.0f - 1.0f;
            double $$5 = $$1.nextFloat() * 2.0f - 1.0f;
            double $$6 = 0.15f + $$1.nextFloat() * 0.1f;
            double $$7 = $$3 * $$3 + $$4 * $$4 + $$5 * $$5;
            if (!($$7 < 1.0) || !($$7 > 0.01)) continue;
            $$7 = 1.0 / Math.sqrt((double)$$7);
            double $$8 = ($$3 *= $$7) * 100.0;
            double $$9 = ($$4 *= $$7) * 100.0;
            double $$10 = ($$5 *= $$7) * 100.0;
            double $$11 = Math.atan2((double)$$3, (double)$$5);
            double $$12 = Math.sin((double)$$11);
            double $$13 = Math.cos((double)$$11);
            double $$14 = Math.atan2((double)Math.sqrt((double)($$3 * $$3 + $$5 * $$5)), (double)$$4);
            double $$15 = Math.sin((double)$$14);
            double $$16 = Math.cos((double)$$14);
            double $$17 = $$1.nextDouble() * Math.PI * 2.0;
            double $$18 = Math.sin((double)$$17);
            double $$19 = Math.cos((double)$$17);
            for (int $$20 = 0; $$20 < 4; ++$$20) {
                double $$26;
                double $$21 = 0.0;
                double $$22 = (double)(($$20 & 2) - 1) * $$6;
                double $$23 = (double)(($$20 + 1 & 2) - 1) * $$6;
                double $$24 = 0.0;
                double $$25 = $$22 * $$19 - $$23 * $$18;
                double $$27 = $$26 = $$23 * $$19 + $$22 * $$18;
                double $$28 = $$25 * $$15 + 0.0 * $$16;
                double $$29 = 0.0 * $$15 - $$25 * $$16;
                double $$30 = $$29 * $$12 - $$27 * $$13;
                double $$31 = $$28;
                double $$32 = $$27 * $$12 + $$29 * $$13;
                $$0.vertex($$8 + $$30, $$9 + $$31, $$10 + $$32).endVertex();
            }
        }
        return $$0.end();
    }

    public void setLevel(@Nullable ClientLevel $$0) {
        this.lastCameraX = Double.MIN_VALUE;
        this.lastCameraY = Double.MIN_VALUE;
        this.lastCameraZ = Double.MIN_VALUE;
        this.lastCameraChunkX = Integer.MIN_VALUE;
        this.lastCameraChunkY = Integer.MIN_VALUE;
        this.lastCameraChunkZ = Integer.MIN_VALUE;
        this.entityRenderDispatcher.setLevel($$0);
        this.level = $$0;
        if ($$0 != null) {
            this.allChanged();
        } else {
            if (this.viewArea != null) {
                this.viewArea.releaseAllBuffers();
                this.viewArea = null;
            }
            if (this.chunkRenderDispatcher != null) {
                this.chunkRenderDispatcher.dispose();
            }
            this.chunkRenderDispatcher = null;
            this.globalBlockEntities.clear();
            this.renderChunkStorage.set(null);
            this.renderChunksInFrustum.clear();
        }
    }

    public void graphicsChanged() {
        if (Minecraft.useShaderTransparency()) {
            this.initTransparency();
        } else {
            this.deinitTransparency();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void allChanged() {
        if (this.level == null) {
            return;
        }
        this.graphicsChanged();
        this.level.clearTintCaches();
        if (this.chunkRenderDispatcher == null) {
            this.chunkRenderDispatcher = new ChunkRenderDispatcher(this.level, this, (Executor)Util.backgroundExecutor(), this.minecraft.is64Bit(), this.renderBuffers.fixedBufferPack());
        } else {
            this.chunkRenderDispatcher.setLevel(this.level);
        }
        this.needsFullRenderChunkUpdate = true;
        this.generateClouds = true;
        this.recentlyCompiledChunks.clear();
        ItemBlockRenderTypes.setFancy(Minecraft.useFancyGraphics());
        this.lastViewDistance = this.minecraft.options.getEffectiveRenderDistance();
        if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
        }
        this.chunkRenderDispatcher.blockUntilClear();
        Set<BlockEntity> set = this.globalBlockEntities;
        synchronized (set) {
            this.globalBlockEntities.clear();
        }
        this.viewArea = new ViewArea(this.chunkRenderDispatcher, this.level, this.minecraft.options.getEffectiveRenderDistance(), this);
        if (this.lastFullRenderChunkUpdate != null) {
            try {
                this.lastFullRenderChunkUpdate.get();
                this.lastFullRenderChunkUpdate = null;
            }
            catch (Exception $$0) {
                LOGGER.warn("Full update failed", (Throwable)$$0);
            }
        }
        this.renderChunkStorage.set((Object)new RenderChunkStorage(this.viewArea.chunks.length));
        this.renderChunksInFrustum.clear();
        Entity $$1 = this.minecraft.getCameraEntity();
        if ($$1 != null) {
            this.viewArea.repositionCamera($$1.getX(), $$1.getZ());
        }
    }

    public void resize(int $$0, int $$1) {
        this.needsUpdate();
        if (this.entityEffect != null) {
            this.entityEffect.resize($$0, $$1);
        }
        if (this.transparencyChain != null) {
            this.transparencyChain.resize($$0, $$1);
        }
    }

    public String getChunkStatistics() {
        int $$0 = this.viewArea.chunks.length;
        int $$1 = this.countRenderedChunks();
        return String.format((Locale)Locale.ROOT, (String)"C: %d/%d %sD: %d, %s", (Object[])new Object[]{$$1, $$0, this.minecraft.smartCull ? "(s) " : "", this.lastViewDistance, this.chunkRenderDispatcher == null ? "null" : this.chunkRenderDispatcher.getStats()});
    }

    public ChunkRenderDispatcher getChunkRenderDispatcher() {
        return this.chunkRenderDispatcher;
    }

    public double getTotalChunks() {
        return this.viewArea.chunks.length;
    }

    public double getLastViewDistance() {
        return this.lastViewDistance;
    }

    public int countRenderedChunks() {
        int $$0 = 0;
        for (RenderChunkInfo $$1 : this.renderChunksInFrustum) {
            if ($$1.chunk.getCompiledChunk().hasNoRenderableLayers()) continue;
            ++$$0;
        }
        return $$0;
    }

    public String getEntityStatistics() {
        return "E: " + this.renderedEntities + "/" + this.level.getEntityCount() + ", B: " + this.culledEntities + ", SD: " + this.level.getServerSimulationDistance();
    }

    private void setupRender(Camera $$02, Frustum $$1, boolean $$2, boolean $$3) {
        Vec3 $$4 = $$02.getPosition();
        if (this.minecraft.options.getEffectiveRenderDistance() != this.lastViewDistance) {
            this.allChanged();
        }
        this.level.getProfiler().push("camera");
        double $$5 = this.minecraft.player.getX();
        double $$6 = this.minecraft.player.getY();
        double $$7 = this.minecraft.player.getZ();
        int $$8 = SectionPos.posToSectionCoord($$5);
        int $$9 = SectionPos.posToSectionCoord($$6);
        int $$10 = SectionPos.posToSectionCoord($$7);
        if (this.lastCameraChunkX != $$8 || this.lastCameraChunkY != $$9 || this.lastCameraChunkZ != $$10) {
            this.lastCameraX = $$5;
            this.lastCameraY = $$6;
            this.lastCameraZ = $$7;
            this.lastCameraChunkX = $$8;
            this.lastCameraChunkY = $$9;
            this.lastCameraChunkZ = $$10;
            this.viewArea.repositionCamera($$5, $$7);
        }
        this.chunkRenderDispatcher.setCamera($$4);
        this.level.getProfiler().popPush("cull");
        this.minecraft.getProfiler().popPush("culling");
        BlockPos $$11 = $$02.getBlockPosition();
        double $$12 = Math.floor((double)($$4.x / 8.0));
        double $$13 = Math.floor((double)($$4.y / 8.0));
        double $$14 = Math.floor((double)($$4.z / 8.0));
        this.needsFullRenderChunkUpdate = this.needsFullRenderChunkUpdate || $$12 != this.prevCamX || $$13 != this.prevCamY || $$14 != this.prevCamZ;
        this.nextFullUpdateMillis.updateAndGet($$0 -> {
            if ($$0 > 0L && System.currentTimeMillis() > $$0) {
                this.needsFullRenderChunkUpdate = true;
                return 0L;
            }
            return $$0;
        });
        this.prevCamX = $$12;
        this.prevCamY = $$13;
        this.prevCamZ = $$14;
        this.minecraft.getProfiler().popPush("update");
        boolean $$15 = this.minecraft.smartCull;
        if ($$3 && this.level.getBlockState($$11).isSolidRender(this.level, $$11)) {
            $$15 = false;
        }
        if (!$$2) {
            if (this.needsFullRenderChunkUpdate && (this.lastFullRenderChunkUpdate == null || this.lastFullRenderChunkUpdate.isDone())) {
                this.minecraft.getProfiler().push("full_update_schedule");
                this.needsFullRenderChunkUpdate = false;
                boolean $$16 = $$15;
                this.lastFullRenderChunkUpdate = Util.backgroundExecutor().submit(() -> {
                    ArrayDeque $$3 = Queues.newArrayDeque();
                    this.initializeQueueForFullUpdate($$02, (Queue<RenderChunkInfo>)$$3);
                    RenderChunkStorage $$4 = new RenderChunkStorage(this.viewArea.chunks.length);
                    this.updateRenderChunks($$4.renderChunks, $$4.renderInfoMap, $$4, (Queue<RenderChunkInfo>)$$3, $$16);
                    this.renderChunkStorage.set((Object)$$4);
                    this.needsFrustumUpdate.set(true);
                });
                this.minecraft.getProfiler().pop();
            }
            RenderChunkStorage $$17 = (RenderChunkStorage)this.renderChunkStorage.get();
            if (!this.recentlyCompiledChunks.isEmpty()) {
                this.minecraft.getProfiler().push("partial_update");
                ArrayDeque $$18 = Queues.newArrayDeque();
                while (!this.recentlyCompiledChunks.isEmpty()) {
                    ChunkRenderDispatcher.RenderChunk $$19 = (ChunkRenderDispatcher.RenderChunk)this.recentlyCompiledChunks.poll();
                    RenderChunkInfo $$20 = $$17.renderInfoMap.get($$19);
                    if ($$20 == null || $$20.chunk != $$19) continue;
                    $$18.add((Object)$$20);
                }
                this.updateRenderChunks($$17.renderChunks, $$17.renderInfoMap, $$4, (Queue<RenderChunkInfo>)$$18, $$15);
                this.needsFrustumUpdate.set(true);
                this.minecraft.getProfiler().pop();
            }
            double $$21 = Math.floor((double)($$02.getXRot() / 2.0f));
            double $$22 = Math.floor((double)($$02.getYRot() / 2.0f));
            if (this.needsFrustumUpdate.compareAndSet(true, false) || $$21 != this.prevCamRotX || $$22 != this.prevCamRotY) {
                this.applyFrustum(new Frustum($$1).offsetToFullyIncludeCameraCube(8));
                this.prevCamRotX = $$21;
                this.prevCamRotY = $$22;
            }
        }
        this.minecraft.getProfiler().pop();
    }

    private void applyFrustum(Frustum $$0) {
        if (!Minecraft.getInstance().isSameThread()) {
            throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
        }
        this.minecraft.getProfiler().push("apply_frustum");
        this.renderChunksInFrustum.clear();
        for (RenderChunkInfo $$1 : ((RenderChunkStorage)this.renderChunkStorage.get()).renderChunks) {
            if (!$$0.isVisible($$1.chunk.getBoundingBox())) continue;
            this.renderChunksInFrustum.add((Object)$$1);
        }
        this.minecraft.getProfiler().pop();
    }

    private void initializeQueueForFullUpdate(Camera $$0, Queue<RenderChunkInfo> $$12) {
        int $$2 = 16;
        Vec3 $$3 = $$0.getPosition();
        BlockPos $$4 = $$0.getBlockPosition();
        ChunkRenderDispatcher.RenderChunk $$5 = this.viewArea.getRenderChunkAt($$4);
        if ($$5 == null) {
            boolean $$6 = $$4.getY() > this.level.getMinBuildHeight();
            int $$7 = $$6 ? this.level.getMaxBuildHeight() - 8 : this.level.getMinBuildHeight() + 8;
            int $$8 = Mth.floor($$3.x / 16.0) * 16;
            int $$9 = Mth.floor($$3.z / 16.0) * 16;
            ArrayList $$10 = Lists.newArrayList();
            for (int $$11 = -this.lastViewDistance; $$11 <= this.lastViewDistance; ++$$11) {
                for (int $$122 = -this.lastViewDistance; $$122 <= this.lastViewDistance; ++$$122) {
                    ChunkRenderDispatcher.RenderChunk $$13 = this.viewArea.getRenderChunkAt(new BlockPos($$8 + SectionPos.sectionToBlockCoord($$11, 8), $$7, $$9 + SectionPos.sectionToBlockCoord($$122, 8)));
                    if ($$13 == null) continue;
                    $$10.add((Object)new RenderChunkInfo($$13, null, 0));
                }
            }
            $$10.sort(Comparator.comparingDouble($$1 -> $$4.distSqr($$1.chunk.getOrigin().offset(8, 8, 8))));
            $$12.addAll((Collection)$$10);
        } else {
            $$12.add((Object)new RenderChunkInfo($$5, null, 0));
        }
    }

    public void addRecentlyCompiledChunk(ChunkRenderDispatcher.RenderChunk $$0) {
        this.recentlyCompiledChunks.add((Object)$$0);
    }

    private void updateRenderChunks(LinkedHashSet<RenderChunkInfo> $$0, RenderInfoMap $$1, Vec3 $$2, Queue<RenderChunkInfo> $$3, boolean $$4) {
        int $$5 = 16;
        BlockPos $$6 = new BlockPos(Mth.floor($$2.x / 16.0) * 16, Mth.floor($$2.y / 16.0) * 16, Mth.floor($$2.z / 16.0) * 16);
        BlockPos $$7 = $$6.offset(8, 8, 8);
        Entity.setViewScale(Mth.clamp((double)this.minecraft.options.getEffectiveRenderDistance() / 8.0, 1.0, 2.5) * this.minecraft.options.entityDistanceScaling().get());
        while (!$$3.isEmpty()) {
            RenderChunkInfo $$8 = (RenderChunkInfo)$$3.poll();
            ChunkRenderDispatcher.RenderChunk $$9 = $$8.chunk;
            $$0.add((Object)$$8);
            boolean $$10 = Math.abs((int)($$9.getOrigin().getX() - $$6.getX())) > 60 || Math.abs((int)($$9.getOrigin().getY() - $$6.getY())) > 60 || Math.abs((int)($$9.getOrigin().getZ() - $$6.getZ())) > 60;
            for (Direction $$11 : DIRECTIONS) {
                RenderChunkInfo $$22;
                ChunkRenderDispatcher.RenderChunk $$12 = this.getRelativeFrom($$6, $$9, $$11);
                if ($$12 == null || $$4 && $$8.hasDirection($$11.getOpposite())) continue;
                if ($$4 && $$8.hasSourceDirections()) {
                    ChunkRenderDispatcher.CompiledChunk $$13 = $$9.getCompiledChunk();
                    boolean $$14 = false;
                    for (int $$15 = 0; $$15 < DIRECTIONS.length; ++$$15) {
                        if (!$$8.hasSourceDirection($$15) || !$$13.facesCanSeeEachother(DIRECTIONS[$$15].getOpposite(), $$11)) continue;
                        $$14 = true;
                        break;
                    }
                    if (!$$14) continue;
                }
                if ($$4 && $$10) {
                    BlockPos $$16 = $$12.getOrigin();
                    BlockPos $$17 = $$16.offset(($$11.getAxis() == Direction.Axis.X ? $$7.getX() > $$16.getX() : $$7.getX() < $$16.getX()) ? 16 : 0, ($$11.getAxis() == Direction.Axis.Y ? $$7.getY() > $$16.getY() : $$7.getY() < $$16.getY()) ? 16 : 0, ($$11.getAxis() == Direction.Axis.Z ? $$7.getZ() > $$16.getZ() : $$7.getZ() < $$16.getZ()) ? 16 : 0);
                    Vec3 $$18 = new Vec3($$17.getX(), $$17.getY(), $$17.getZ());
                    Vec3 $$19 = $$2.subtract($$18).normalize().scale(CEILED_SECTION_DIAGONAL);
                    boolean $$20 = true;
                    while ($$2.subtract($$18).lengthSqr() > 3600.0) {
                        $$18 = $$18.add($$19);
                        if ($$18.y > (double)this.level.getMaxBuildHeight() || $$18.y < (double)this.level.getMinBuildHeight()) break;
                        ChunkRenderDispatcher.RenderChunk $$21 = this.viewArea.getRenderChunkAt(new BlockPos($$18.x, $$18.y, $$18.z));
                        if ($$21 != null && $$1.get($$21) != null) continue;
                        $$20 = false;
                        break;
                    }
                    if (!$$20) continue;
                }
                if (($$22 = $$1.get($$12)) != null) {
                    $$22.addSourceDirection($$11);
                    continue;
                }
                if (!$$12.hasAllNeighbors()) {
                    if (this.closeToBorder($$6, $$9)) continue;
                    this.nextFullUpdateMillis.set(System.currentTimeMillis() + 500L);
                    continue;
                }
                RenderChunkInfo $$23 = new RenderChunkInfo($$12, $$11, $$8.step + 1);
                $$23.setDirections($$8.directions, $$11);
                $$3.add((Object)$$23);
                $$1.put($$12, $$23);
            }
        }
    }

    @Nullable
    private ChunkRenderDispatcher.RenderChunk getRelativeFrom(BlockPos $$0, ChunkRenderDispatcher.RenderChunk $$1, Direction $$2) {
        BlockPos $$3 = $$1.getRelativeOrigin($$2);
        if (Mth.abs($$0.getX() - $$3.getX()) > this.lastViewDistance * 16) {
            return null;
        }
        if (Mth.abs($$0.getY() - $$3.getY()) > this.lastViewDistance * 16 || $$3.getY() < this.level.getMinBuildHeight() || $$3.getY() >= this.level.getMaxBuildHeight()) {
            return null;
        }
        if (Mth.abs($$0.getZ() - $$3.getZ()) > this.lastViewDistance * 16) {
            return null;
        }
        return this.viewArea.getRenderChunkAt($$3);
    }

    private boolean closeToBorder(BlockPos $$0, ChunkRenderDispatcher.RenderChunk $$1) {
        int $$6;
        int $$2 = SectionPos.blockToSectionCoord($$0.getX());
        int $$3 = SectionPos.blockToSectionCoord($$0.getZ());
        BlockPos $$4 = $$1.getOrigin();
        int $$5 = SectionPos.blockToSectionCoord($$4.getX());
        return !ChunkMap.isChunkInRange($$5, $$6 = SectionPos.blockToSectionCoord($$4.getZ()), $$2, $$3, this.lastViewDistance - 2);
    }

    private void captureFrustum(Matrix4f $$0, Matrix4f $$1, double $$2, double $$3, double $$4, Frustum $$5) {
        this.capturedFrustum = $$5;
        Matrix4f $$6 = new Matrix4f((Matrix4fc)$$1);
        $$6.mul((Matrix4fc)$$0);
        $$6.invert();
        this.frustumPos.x = $$2;
        this.frustumPos.y = $$3;
        this.frustumPos.z = $$4;
        this.frustumPoints[0] = new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f);
        this.frustumPoints[1] = new Vector4f(1.0f, -1.0f, -1.0f, 1.0f);
        this.frustumPoints[2] = new Vector4f(1.0f, 1.0f, -1.0f, 1.0f);
        this.frustumPoints[3] = new Vector4f(-1.0f, 1.0f, -1.0f, 1.0f);
        this.frustumPoints[4] = new Vector4f(-1.0f, -1.0f, 1.0f, 1.0f);
        this.frustumPoints[5] = new Vector4f(1.0f, -1.0f, 1.0f, 1.0f);
        this.frustumPoints[6] = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.frustumPoints[7] = new Vector4f(-1.0f, 1.0f, 1.0f, 1.0f);
        for (int $$7 = 0; $$7 < 8; ++$$7) {
            $$6.transform(this.frustumPoints[$$7]);
            this.frustumPoints[$$7].div(this.frustumPoints[$$7].w());
        }
    }

    public void prepareCullFrustum(PoseStack $$0, Vec3 $$1, Matrix4f $$2) {
        Matrix4f $$3 = $$0.last().pose();
        double $$4 = $$1.x();
        double $$5 = $$1.y();
        double $$6 = $$1.z();
        this.cullingFrustum = new Frustum($$3, $$2);
        this.cullingFrustum.prepare($$4, $$5, $$6);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void renderLevel(PoseStack $$0, float $$1, long $$22, boolean $$3, Camera $$4, GameRenderer $$5, LightTexture $$6, Matrix4f $$7) {
        Frustum $$17;
        boolean $$15;
        RenderSystem.setShaderGameTime(this.level.getGameTime(), $$1);
        this.blockEntityRenderDispatcher.prepare(this.level, $$4, this.minecraft.hitResult);
        this.entityRenderDispatcher.prepare(this.level, $$4, this.minecraft.crosshairPickEntity);
        ProfilerFiller $$8 = this.level.getProfiler();
        $$8.popPush("light_update_queue");
        this.level.pollLightUpdates();
        $$8.popPush("light_updates");
        boolean $$9 = this.level.isLightUpdateQueueEmpty();
        this.level.getChunkSource().getLightEngine().runUpdates(Integer.MAX_VALUE, $$9, true);
        Vec3 $$10 = $$4.getPosition();
        double $$11 = $$10.x();
        double $$12 = $$10.y();
        double $$13 = $$10.z();
        Matrix4f $$14 = $$0.last().pose();
        $$8.popPush("culling");
        boolean bl = $$15 = this.capturedFrustum != null;
        if ($$15) {
            Frustum $$16 = this.capturedFrustum;
            $$16.prepare(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z);
        } else {
            $$17 = this.cullingFrustum;
        }
        this.minecraft.getProfiler().popPush("captureFrustum");
        if (this.captureFrustum) {
            this.captureFrustum($$14, $$7, $$10.x, $$10.y, $$10.z, $$15 ? new Frustum($$14, $$7) : $$17);
            this.captureFrustum = false;
        }
        $$8.popPush("clear");
        FogRenderer.setupColor($$4, $$1, this.minecraft.level, this.minecraft.options.getEffectiveRenderDistance(), $$5.getDarkenWorldAmount($$1));
        FogRenderer.levelFogColor();
        RenderSystem.clear(16640, Minecraft.ON_OSX);
        float $$18 = $$5.getRenderDistance();
        boolean $$19 = this.minecraft.level.effects().isFoggyAt(Mth.floor($$11), Mth.floor($$12)) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
        $$8.popPush("sky");
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionShader));
        this.renderSky($$0, $$7, $$1, $$4, $$19, () -> FogRenderer.setupFog($$4, FogRenderer.FogMode.FOG_SKY, $$18, $$19, $$1));
        $$8.popPush("fog");
        FogRenderer.setupFog($$4, FogRenderer.FogMode.FOG_TERRAIN, Math.max((float)$$18, (float)32.0f), $$19, $$1);
        $$8.popPush("terrain_setup");
        this.setupRender($$4, $$17, $$15, this.minecraft.player.isSpectator());
        $$8.popPush("compilechunks");
        this.compileChunks($$4);
        $$8.popPush("terrain");
        this.renderChunkLayer(RenderType.solid(), $$0, $$11, $$12, $$13, $$7);
        this.renderChunkLayer(RenderType.cutoutMipped(), $$0, $$11, $$12, $$13, $$7);
        this.renderChunkLayer(RenderType.cutout(), $$0, $$11, $$12, $$13, $$7);
        if (this.level.effects().constantAmbientLight()) {
            Lighting.setupNetherLevel($$0.last().pose());
        } else {
            Lighting.setupLevel($$0.last().pose());
        }
        $$8.popPush("entities");
        this.renderedEntities = 0;
        this.culledEntities = 0;
        if (this.itemEntityTarget != null) {
            this.itemEntityTarget.clear(Minecraft.ON_OSX);
            this.itemEntityTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }
        if (this.weatherTarget != null) {
            this.weatherTarget.clear(Minecraft.ON_OSX);
        }
        if (this.shouldShowEntityOutlines()) {
            this.entityTarget.clear(Minecraft.ON_OSX);
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }
        boolean $$20 = false;
        MultiBufferSource.BufferSource $$21 = this.renderBuffers.bufferSource();
        for (Entity $$222 : this.level.entitiesForRendering()) {
            MultiBufferSource.BufferSource $$31;
            BlockPos $$23;
            if (!this.entityRenderDispatcher.shouldRender($$222, $$17, $$11, $$12, $$13) && !$$222.hasIndirectPassenger(this.minecraft.player) || !this.level.isOutsideBuildHeight(($$23 = $$222.blockPosition()).getY()) && !this.isChunkCompiled($$23) || $$222 == $$4.getEntity() && !$$4.isDetached() && (!($$4.getEntity() instanceof LivingEntity) || !((LivingEntity)$$4.getEntity()).isSleeping()) || $$222 instanceof LocalPlayer && $$4.getEntity() != $$222) continue;
            ++this.renderedEntities;
            if ($$222.tickCount == 0) {
                $$222.xOld = $$222.getX();
                $$222.yOld = $$222.getY();
                $$222.zOld = $$222.getZ();
            }
            if (this.shouldShowEntityOutlines() && this.minecraft.shouldEntityAppearGlowing($$222)) {
                OutlineBufferSource $$24;
                $$20 = true;
                OutlineBufferSource $$25 = $$24 = this.renderBuffers.outlineBufferSource();
                int $$26 = $$222.getTeamColor();
                int $$27 = 255;
                int $$28 = $$26 >> 16 & 0xFF;
                int $$29 = $$26 >> 8 & 0xFF;
                int $$30 = $$26 & 0xFF;
                $$24.setColor($$28, $$29, $$30, 255);
            } else {
                $$31 = $$21;
            }
            this.renderEntity($$222, $$11, $$12, $$13, $$1, $$0, $$31);
        }
        $$21.endLastBatch();
        this.checkPoseStack($$0);
        $$21.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
        $$21.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
        $$21.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
        $$21.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));
        $$8.popPush("blockentities");
        for (RenderChunkInfo $$32 : this.renderChunksInFrustum) {
            List<BlockEntity> $$33 = $$32.chunk.getCompiledChunk().getRenderableBlockEntities();
            if ($$33.isEmpty()) continue;
            for (BlockEntity $$34 : $$33) {
                int $$38;
                BlockPos $$35 = $$34.getBlockPos();
                MultiBufferSource $$36 = $$21;
                $$0.pushPose();
                $$0.translate((double)$$35.getX() - $$11, (double)$$35.getY() - $$12, (double)$$35.getZ() - $$13);
                SortedSet $$37 = (SortedSet)this.destructionProgress.get($$35.asLong());
                if ($$37 != null && !$$37.isEmpty() && ($$38 = ((BlockDestructionProgress)$$37.last()).getProgress()) >= 0) {
                    PoseStack.Pose $$39 = $$0.last();
                    SheetedDecalTextureGenerator $$40 = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get($$38)), $$39.pose(), $$39.normal(), 1.0f);
                    $$36 = $$2 -> {
                        VertexConsumer $$3 = $$21.getBuffer($$2);
                        if ($$2.affectsCrumbling()) {
                            return VertexMultiConsumer.create($$40, $$3);
                        }
                        return $$3;
                    };
                }
                this.blockEntityRenderDispatcher.render($$34, $$1, $$0, $$36);
                $$0.popPose();
            }
        }
        ObjectIterator objectIterator = this.globalBlockEntities;
        synchronized (objectIterator) {
            for (BlockEntity $$41 : this.globalBlockEntities) {
                BlockPos $$42 = $$41.getBlockPos();
                $$0.pushPose();
                $$0.translate((double)$$42.getX() - $$11, (double)$$42.getY() - $$12, (double)$$42.getZ() - $$13);
                this.blockEntityRenderDispatcher.render($$41, $$1, $$0, $$21);
                $$0.popPose();
            }
        }
        this.checkPoseStack($$0);
        $$21.endBatch(RenderType.solid());
        $$21.endBatch(RenderType.endPortal());
        $$21.endBatch(RenderType.endGateway());
        $$21.endBatch(Sheets.solidBlockSheet());
        $$21.endBatch(Sheets.cutoutBlockSheet());
        $$21.endBatch(Sheets.bedSheet());
        $$21.endBatch(Sheets.shulkerBoxSheet());
        $$21.endBatch(Sheets.signSheet());
        $$21.endBatch(Sheets.hangingSignSheet());
        $$21.endBatch(Sheets.chestSheet());
        this.renderBuffers.outlineBufferSource().endOutlineBatch();
        if ($$20) {
            this.entityEffect.process($$1);
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }
        $$8.popPush("destroyProgress");
        for (Long2ObjectMap.Entry $$43 : this.destructionProgress.long2ObjectEntrySet()) {
            SortedSet $$48;
            double $$47;
            double $$46;
            BlockPos $$44 = BlockPos.of($$43.getLongKey());
            double $$45 = (double)$$44.getX() - $$11;
            if ($$45 * $$45 + ($$46 = (double)$$44.getY() - $$12) * $$46 + ($$47 = (double)$$44.getZ() - $$13) * $$47 > 1024.0 || ($$48 = (SortedSet)$$43.getValue()) == null || $$48.isEmpty()) continue;
            int $$49 = ((BlockDestructionProgress)$$48.last()).getProgress();
            $$0.pushPose();
            $$0.translate((double)$$44.getX() - $$11, (double)$$44.getY() - $$12, (double)$$44.getZ() - $$13);
            PoseStack.Pose $$50 = $$0.last();
            SheetedDecalTextureGenerator $$51 = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get($$49)), $$50.pose(), $$50.normal(), 1.0f);
            this.minecraft.getBlockRenderer().renderBreakingTexture(this.level.getBlockState($$44), $$44, this.level, $$0, $$51);
            $$0.popPose();
        }
        this.checkPoseStack($$0);
        HitResult $$52 = this.minecraft.hitResult;
        if ($$3 && $$52 != null && $$52.getType() == HitResult.Type.BLOCK) {
            $$8.popPush("outline");
            BlockPos $$53 = ((BlockHitResult)$$52).getBlockPos();
            BlockState $$54 = this.level.getBlockState($$53);
            if (!$$54.isAir() && this.level.getWorldBorder().isWithinBounds($$53)) {
                VertexConsumer $$55 = $$21.getBuffer(RenderType.lines());
                this.renderHitOutline($$0, $$55, $$4.getEntity(), $$11, $$12, $$13, $$53, $$54);
            }
        }
        PoseStack $$56 = RenderSystem.getModelViewStack();
        $$56.pushPose();
        $$56.mulPoseMatrix($$0.last().pose());
        RenderSystem.applyModelViewMatrix();
        this.minecraft.debugRenderer.render($$0, $$21, $$11, $$12, $$13);
        $$56.popPose();
        RenderSystem.applyModelViewMatrix();
        $$21.endBatch(Sheets.translucentCullBlockSheet());
        $$21.endBatch(Sheets.bannerSheet());
        $$21.endBatch(Sheets.shieldSheet());
        $$21.endBatch(RenderType.armorGlint());
        $$21.endBatch(RenderType.armorEntityGlint());
        $$21.endBatch(RenderType.glint());
        $$21.endBatch(RenderType.glintDirect());
        $$21.endBatch(RenderType.glintTranslucent());
        $$21.endBatch(RenderType.entityGlint());
        $$21.endBatch(RenderType.entityGlintDirect());
        $$21.endBatch(RenderType.waterMask());
        this.renderBuffers.crumblingBufferSource().endBatch();
        if (this.transparencyChain != null) {
            $$21.endBatch(RenderType.lines());
            $$21.endBatch();
            this.translucentTarget.clear(Minecraft.ON_OSX);
            this.translucentTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
            $$8.popPush("translucent");
            this.renderChunkLayer(RenderType.translucent(), $$0, $$11, $$12, $$13, $$7);
            $$8.popPush("string");
            this.renderChunkLayer(RenderType.tripwire(), $$0, $$11, $$12, $$13, $$7);
            this.particlesTarget.clear(Minecraft.ON_OSX);
            this.particlesTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
            RenderStateShard.PARTICLES_TARGET.setupRenderState();
            $$8.popPush("particles");
            this.minecraft.particleEngine.render($$0, $$21, $$6, $$4, $$1);
            RenderStateShard.PARTICLES_TARGET.clearRenderState();
        } else {
            $$8.popPush("translucent");
            if (this.translucentTarget != null) {
                this.translucentTarget.clear(Minecraft.ON_OSX);
            }
            this.renderChunkLayer(RenderType.translucent(), $$0, $$11, $$12, $$13, $$7);
            $$21.endBatch(RenderType.lines());
            $$21.endBatch();
            $$8.popPush("string");
            this.renderChunkLayer(RenderType.tripwire(), $$0, $$11, $$12, $$13, $$7);
            $$8.popPush("particles");
            this.minecraft.particleEngine.render($$0, $$21, $$6, $$4, $$1);
        }
        $$56.pushPose();
        $$56.mulPoseMatrix($$0.last().pose());
        RenderSystem.applyModelViewMatrix();
        if (this.minecraft.options.getCloudsType() != CloudStatus.OFF) {
            if (this.transparencyChain != null) {
                this.cloudsTarget.clear(Minecraft.ON_OSX);
                RenderStateShard.CLOUDS_TARGET.setupRenderState();
                $$8.popPush("clouds");
                this.renderClouds($$0, $$7, $$1, $$11, $$12, $$13);
                RenderStateShard.CLOUDS_TARGET.clearRenderState();
            } else {
                $$8.popPush("clouds");
                RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexColorNormalShader));
                this.renderClouds($$0, $$7, $$1, $$11, $$12, $$13);
            }
        }
        if (this.transparencyChain != null) {
            RenderStateShard.WEATHER_TARGET.setupRenderState();
            $$8.popPush("weather");
            this.renderSnowAndRain($$6, $$1, $$11, $$12, $$13);
            this.renderWorldBorder($$4);
            RenderStateShard.WEATHER_TARGET.clearRenderState();
            this.transparencyChain.process($$1);
            this.minecraft.getMainRenderTarget().bindWrite(false);
        } else {
            RenderSystem.depthMask(false);
            $$8.popPush("weather");
            this.renderSnowAndRain($$6, $$1, $$11, $$12, $$13);
            this.renderWorldBorder($$4);
            RenderSystem.depthMask(true);
        }
        this.renderDebug($$4);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        $$56.popPose();
        RenderSystem.applyModelViewMatrix();
        FogRenderer.setupNoFog();
    }

    private void checkPoseStack(PoseStack $$0) {
        if (!$$0.clear()) {
            throw new IllegalStateException("Pose stack not empty");
        }
    }

    private void renderEntity(Entity $$0, double $$1, double $$2, double $$3, float $$4, PoseStack $$5, MultiBufferSource $$6) {
        double $$7 = Mth.lerp((double)$$4, $$0.xOld, $$0.getX());
        double $$8 = Mth.lerp((double)$$4, $$0.yOld, $$0.getY());
        double $$9 = Mth.lerp((double)$$4, $$0.zOld, $$0.getZ());
        float $$10 = Mth.lerp($$4, $$0.yRotO, $$0.getYRot());
        this.entityRenderDispatcher.render($$0, $$7 - $$1, $$8 - $$2, $$9 - $$3, $$10, $$4, $$5, $$6, this.entityRenderDispatcher.getPackedLightCoords($$0, $$4));
    }

    private void renderChunkLayer(RenderType $$0, PoseStack $$1, double $$2, double $$3, double $$4, Matrix4f $$5) {
        RenderSystem.assertOnRenderThread();
        $$0.setupRenderState();
        if ($$0 == RenderType.translucent()) {
            this.minecraft.getProfiler().push("translucent_sort");
            double $$6 = $$2 - this.xTransparentOld;
            double $$7 = $$3 - this.yTransparentOld;
            double $$8 = $$4 - this.zTransparentOld;
            if ($$6 * $$6 + $$7 * $$7 + $$8 * $$8 > 1.0) {
                this.xTransparentOld = $$2;
                this.yTransparentOld = $$3;
                this.zTransparentOld = $$4;
                int $$9 = 0;
                for (RenderChunkInfo $$10 : this.renderChunksInFrustum) {
                    if ($$9 >= 15 || !$$10.chunk.resortTransparency($$0, this.chunkRenderDispatcher)) continue;
                    ++$$9;
                }
            }
            this.minecraft.getProfiler().pop();
        }
        this.minecraft.getProfiler().push("filterempty");
        this.minecraft.getProfiler().popPush((Supplier<String>)((Supplier)() -> "render_" + $$0));
        boolean $$11 = $$0 != RenderType.translucent();
        ObjectListIterator $$12 = this.renderChunksInFrustum.listIterator($$11 ? 0 : this.renderChunksInFrustum.size());
        ShaderInstance $$13 = RenderSystem.getShader();
        for (int $$14 = 0; $$14 < 12; ++$$14) {
            int $$15 = RenderSystem.getShaderTexture($$14);
            $$13.setSampler("Sampler" + $$14, $$15);
        }
        if ($$13.MODEL_VIEW_MATRIX != null) {
            $$13.MODEL_VIEW_MATRIX.set($$1.last().pose());
        }
        if ($$13.PROJECTION_MATRIX != null) {
            $$13.PROJECTION_MATRIX.set($$5);
        }
        if ($$13.COLOR_MODULATOR != null) {
            $$13.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        }
        if ($$13.FOG_START != null) {
            $$13.FOG_START.set(RenderSystem.getShaderFogStart());
        }
        if ($$13.FOG_END != null) {
            $$13.FOG_END.set(RenderSystem.getShaderFogEnd());
        }
        if ($$13.FOG_COLOR != null) {
            $$13.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }
        if ($$13.FOG_SHAPE != null) {
            $$13.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
        }
        if ($$13.TEXTURE_MATRIX != null) {
            $$13.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        }
        if ($$13.GAME_TIME != null) {
            $$13.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }
        RenderSystem.setupShaderLights($$13);
        $$13.apply();
        Uniform $$16 = $$13.CHUNK_OFFSET;
        while ($$11 ? $$12.hasNext() : $$12.hasPrevious()) {
            RenderChunkInfo $$17 = $$11 ? (RenderChunkInfo)$$12.next() : (RenderChunkInfo)$$12.previous();
            ChunkRenderDispatcher.RenderChunk $$18 = $$17.chunk;
            if ($$18.getCompiledChunk().isEmpty($$0)) continue;
            VertexBuffer $$19 = $$18.getBuffer($$0);
            BlockPos $$20 = $$18.getOrigin();
            if ($$16 != null) {
                $$16.set((float)((double)$$20.getX() - $$2), (float)((double)$$20.getY() - $$3), (float)((double)$$20.getZ() - $$4));
                $$16.upload();
            }
            $$19.bind();
            $$19.draw();
        }
        if ($$16 != null) {
            $$16.set(0.0f, 0.0f, 0.0f);
        }
        $$13.clear();
        VertexBuffer.unbind();
        this.minecraft.getProfiler().pop();
        $$0.clearRenderState();
    }

    private void renderDebug(Camera $$0) {
        Tesselator $$1 = Tesselator.getInstance();
        BufferBuilder $$2 = $$1.getBuilder();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        if (this.minecraft.chunkPath || this.minecraft.chunkVisibility) {
            double $$3 = $$0.getPosition().x();
            double $$4 = $$0.getPosition().y();
            double $$5 = $$0.getPosition().z();
            RenderSystem.depthMask(true);
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableTexture();
            for (RenderChunkInfo $$6 : this.renderChunksInFrustum) {
                ChunkRenderDispatcher.RenderChunk $$7 = $$6.chunk;
                BlockPos $$8 = $$7.getOrigin();
                PoseStack $$9 = RenderSystem.getModelViewStack();
                $$9.pushPose();
                $$9.translate((double)$$8.getX() - $$3, (double)$$8.getY() - $$4, (double)$$8.getZ() - $$5);
                RenderSystem.applyModelViewMatrix();
                RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeLinesShader));
                if (this.minecraft.chunkPath) {
                    $$2.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
                    RenderSystem.lineWidth(5.0f);
                    int $$10 = $$6.step == 0 ? 0 : Mth.hsvToRgb((float)$$6.step / 50.0f, 0.9f, 0.9f);
                    int $$11 = $$10 >> 16 & 0xFF;
                    int $$12 = $$10 >> 8 & 0xFF;
                    int $$13 = $$10 & 0xFF;
                    for (int $$14 = 0; $$14 < DIRECTIONS.length; ++$$14) {
                        if (!$$6.hasSourceDirection($$14)) continue;
                        Direction $$15 = DIRECTIONS[$$14];
                        $$2.vertex(8.0, 8.0, 8.0).color($$11, $$12, $$13, 255).normal($$15.getStepX(), $$15.getStepY(), $$15.getStepZ()).endVertex();
                        $$2.vertex(8 - 16 * $$15.getStepX(), 8 - 16 * $$15.getStepY(), 8 - 16 * $$15.getStepZ()).color($$11, $$12, $$13, 255).normal($$15.getStepX(), $$15.getStepY(), $$15.getStepZ()).endVertex();
                    }
                    $$1.end();
                    RenderSystem.lineWidth(1.0f);
                }
                if (this.minecraft.chunkVisibility && !$$7.getCompiledChunk().hasNoRenderableLayers()) {
                    $$2.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
                    RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeLinesShader));
                    RenderSystem.lineWidth(5.0f);
                    int $$16 = 0;
                    for (Direction $$17 : DIRECTIONS) {
                        for (Direction $$18 : DIRECTIONS) {
                            boolean $$19 = $$7.getCompiledChunk().facesCanSeeEachother($$17, $$18);
                            if ($$19) continue;
                            ++$$16;
                            $$2.vertex(8 + 8 * $$17.getStepX(), 8 + 8 * $$17.getStepY(), 8 + 8 * $$17.getStepZ()).color(255, 0, 0, 255).normal($$17.getStepX(), $$17.getStepY(), $$17.getStepZ()).endVertex();
                            $$2.vertex(8 + 8 * $$18.getStepX(), 8 + 8 * $$18.getStepY(), 8 + 8 * $$18.getStepZ()).color(255, 0, 0, 255).normal($$18.getStepX(), $$18.getStepY(), $$18.getStepZ()).endVertex();
                        }
                    }
                    $$1.end();
                    RenderSystem.lineWidth(1.0f);
                    RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
                    if ($$16 > 0) {
                        $$2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                        float $$20 = 0.5f;
                        float $$21 = 0.2f;
                        $$2.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$2.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        $$1.end();
                    }
                }
                $$9.popPose();
                RenderSystem.applyModelViewMatrix();
            }
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableTexture();
        }
        if (this.capturedFrustum != null) {
            RenderSystem.disableCull();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.lineWidth(5.0f);
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
            PoseStack $$22 = RenderSystem.getModelViewStack();
            $$22.pushPose();
            $$22.translate((float)(this.frustumPos.x - $$0.getPosition().x), (float)(this.frustumPos.y - $$0.getPosition().y), (float)(this.frustumPos.z - $$0.getPosition().z));
            RenderSystem.applyModelViewMatrix();
            RenderSystem.depthMask(true);
            $$2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            this.addFrustumQuad($$2, 0, 1, 2, 3, 0, 1, 1);
            this.addFrustumQuad($$2, 4, 5, 6, 7, 1, 0, 0);
            this.addFrustumQuad($$2, 0, 1, 5, 4, 1, 1, 0);
            this.addFrustumQuad($$2, 2, 3, 7, 6, 0, 0, 1);
            this.addFrustumQuad($$2, 0, 4, 7, 3, 0, 1, 0);
            this.addFrustumQuad($$2, 1, 5, 6, 2, 1, 0, 1);
            $$1.end();
            RenderSystem.depthMask(false);
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeLinesShader));
            $$2.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            this.addFrustumVertex($$2, 0);
            this.addFrustumVertex($$2, 1);
            this.addFrustumVertex($$2, 1);
            this.addFrustumVertex($$2, 2);
            this.addFrustumVertex($$2, 2);
            this.addFrustumVertex($$2, 3);
            this.addFrustumVertex($$2, 3);
            this.addFrustumVertex($$2, 0);
            this.addFrustumVertex($$2, 4);
            this.addFrustumVertex($$2, 5);
            this.addFrustumVertex($$2, 5);
            this.addFrustumVertex($$2, 6);
            this.addFrustumVertex($$2, 6);
            this.addFrustumVertex($$2, 7);
            this.addFrustumVertex($$2, 7);
            this.addFrustumVertex($$2, 4);
            this.addFrustumVertex($$2, 0);
            this.addFrustumVertex($$2, 4);
            this.addFrustumVertex($$2, 1);
            this.addFrustumVertex($$2, 5);
            this.addFrustumVertex($$2, 2);
            this.addFrustumVertex($$2, 6);
            this.addFrustumVertex($$2, 3);
            this.addFrustumVertex($$2, 7);
            $$1.end();
            $$22.popPose();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableTexture();
            RenderSystem.lineWidth(1.0f);
        }
    }

    private void addFrustumVertex(VertexConsumer $$0, int $$1) {
        $$0.vertex(this.frustumPoints[$$1].x(), this.frustumPoints[$$1].y(), this.frustumPoints[$$1].z()).color(0, 0, 0, 255).normal(0.0f, 0.0f, -1.0f).endVertex();
    }

    private void addFrustumQuad(VertexConsumer $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7) {
        float $$8 = 0.25f;
        $$0.vertex(this.frustumPoints[$$1].x(), this.frustumPoints[$$1].y(), this.frustumPoints[$$1].z()).color((float)$$5, (float)$$6, (float)$$7, 0.25f).endVertex();
        $$0.vertex(this.frustumPoints[$$2].x(), this.frustumPoints[$$2].y(), this.frustumPoints[$$2].z()).color((float)$$5, (float)$$6, (float)$$7, 0.25f).endVertex();
        $$0.vertex(this.frustumPoints[$$3].x(), this.frustumPoints[$$3].y(), this.frustumPoints[$$3].z()).color((float)$$5, (float)$$6, (float)$$7, 0.25f).endVertex();
        $$0.vertex(this.frustumPoints[$$4].x(), this.frustumPoints[$$4].y(), this.frustumPoints[$$4].z()).color((float)$$5, (float)$$6, (float)$$7, 0.25f).endVertex();
    }

    public void captureFrustum() {
        this.captureFrustum = true;
    }

    public void killFrustum() {
        this.capturedFrustum = null;
    }

    public void tick() {
        ++this.ticks;
        if (this.ticks % 20 != 0) {
            return;
        }
        ObjectIterator $$0 = this.destroyingBlocks.values().iterator();
        while ($$0.hasNext()) {
            BlockDestructionProgress $$1 = (BlockDestructionProgress)$$0.next();
            int $$2 = $$1.getUpdatedRenderTick();
            if (this.ticks - $$2 <= 400) continue;
            $$0.remove();
            this.removeProgress($$1);
        }
    }

    private void removeProgress(BlockDestructionProgress $$0) {
        long $$1 = $$0.getPos().asLong();
        Set $$2 = (Set)this.destructionProgress.get($$1);
        $$2.remove((Object)$$0);
        if ($$2.isEmpty()) {
            this.destructionProgress.remove($$1);
        }
    }

    private void renderEndSky(PoseStack $$0) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexColorShader));
        RenderSystem.setShaderTexture(0, END_SKY_LOCATION);
        Tesselator $$1 = Tesselator.getInstance();
        BufferBuilder $$2 = $$1.getBuilder();
        for (int $$3 = 0; $$3 < 6; ++$$3) {
            $$0.pushPose();
            if ($$3 == 1) {
                $$0.mulPose(Axis.XP.rotationDegrees(90.0f));
            }
            if ($$3 == 2) {
                $$0.mulPose(Axis.XP.rotationDegrees(-90.0f));
            }
            if ($$3 == 3) {
                $$0.mulPose(Axis.XP.rotationDegrees(180.0f));
            }
            if ($$3 == 4) {
                $$0.mulPose(Axis.ZP.rotationDegrees(90.0f));
            }
            if ($$3 == 5) {
                $$0.mulPose(Axis.ZP.rotationDegrees(-90.0f));
            }
            Matrix4f $$4 = $$0.last().pose();
            $$2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            $$2.vertex($$4, -100.0f, -100.0f, -100.0f).uv(0.0f, 0.0f).color(40, 40, 40, 255).endVertex();
            $$2.vertex($$4, -100.0f, -100.0f, 100.0f).uv(0.0f, 16.0f).color(40, 40, 40, 255).endVertex();
            $$2.vertex($$4, 100.0f, -100.0f, 100.0f).uv(16.0f, 16.0f).color(40, 40, 40, 255).endVertex();
            $$2.vertex($$4, 100.0f, -100.0f, -100.0f).uv(16.0f, 0.0f).color(40, 40, 40, 255).endVertex();
            $$1.end();
            $$0.popPose();
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public void renderSky(PoseStack $$0, Matrix4f $$1, float $$2, Camera $$3, boolean $$4, Runnable $$5) {
        $$5.run();
        if ($$4) {
            return;
        }
        FogType $$6 = $$3.getFluidInCamera();
        if ($$6 == FogType.POWDER_SNOW || $$6 == FogType.LAVA || this.doesMobEffectBlockSky($$3)) {
            return;
        }
        if (this.minecraft.level.effects().skyType() == DimensionSpecialEffects.SkyType.END) {
            this.renderEndSky($$0);
            return;
        }
        if (this.minecraft.level.effects().skyType() != DimensionSpecialEffects.SkyType.NORMAL) {
            return;
        }
        RenderSystem.disableTexture();
        Vec3 $$7 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), $$2);
        float $$8 = (float)$$7.x;
        float $$9 = (float)$$7.y;
        float $$10 = (float)$$7.z;
        FogRenderer.levelFogColor();
        BufferBuilder $$11 = Tesselator.getInstance().getBuilder();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor($$8, $$9, $$10, 1.0f);
        ShaderInstance $$12 = RenderSystem.getShader();
        this.skyBuffer.bind();
        this.skyBuffer.drawWithShader($$0.last().pose(), $$1, $$12);
        VertexBuffer.unbind();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float[] $$13 = this.level.effects().getSunriseColor(this.level.getTimeOfDay($$2), $$2);
        if ($$13 != null) {
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
            RenderSystem.disableTexture();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            $$0.pushPose();
            $$0.mulPose(Axis.XP.rotationDegrees(90.0f));
            float $$14 = Mth.sin(this.level.getSunAngle($$2)) < 0.0f ? 180.0f : 0.0f;
            $$0.mulPose(Axis.ZP.rotationDegrees($$14));
            $$0.mulPose(Axis.ZP.rotationDegrees(90.0f));
            float $$15 = $$13[0];
            float $$16 = $$13[1];
            float $$17 = $$13[2];
            Matrix4f $$18 = $$0.last().pose();
            $$11.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            $$11.vertex($$18, 0.0f, 100.0f, 0.0f).color($$15, $$16, $$17, $$13[3]).endVertex();
            int $$19 = 16;
            for (int $$20 = 0; $$20 <= 16; ++$$20) {
                float $$21 = (float)$$20 * ((float)Math.PI * 2) / 16.0f;
                float $$22 = Mth.sin($$21);
                float $$23 = Mth.cos($$21);
                $$11.vertex($$18, $$22 * 120.0f, $$23 * 120.0f, -$$23 * 40.0f * $$13[3]).color($$13[0], $$13[1], $$13[2], 0.0f).endVertex();
            }
            BufferUploader.drawWithShader($$11.end());
            $$0.popPose();
        }
        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        $$0.pushPose();
        float $$24 = 1.0f - this.level.getRainLevel($$2);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, $$24);
        $$0.mulPose(Axis.YP.rotationDegrees(-90.0f));
        $$0.mulPose(Axis.XP.rotationDegrees(this.level.getTimeOfDay($$2) * 360.0f));
        Matrix4f $$25 = $$0.last().pose();
        float $$26 = 30.0f;
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, SUN_LOCATION);
        $$11.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        $$11.vertex($$25, -$$26, 100.0f, -$$26).uv(0.0f, 0.0f).endVertex();
        $$11.vertex($$25, $$26, 100.0f, -$$26).uv(1.0f, 0.0f).endVertex();
        $$11.vertex($$25, $$26, 100.0f, $$26).uv(1.0f, 1.0f).endVertex();
        $$11.vertex($$25, -$$26, 100.0f, $$26).uv(0.0f, 1.0f).endVertex();
        BufferUploader.drawWithShader($$11.end());
        $$26 = 20.0f;
        RenderSystem.setShaderTexture(0, MOON_LOCATION);
        int $$27 = this.level.getMoonPhase();
        int $$28 = $$27 % 4;
        int $$29 = $$27 / 4 % 2;
        float $$30 = (float)($$28 + 0) / 4.0f;
        float $$31 = (float)($$29 + 0) / 2.0f;
        float $$32 = (float)($$28 + 1) / 4.0f;
        float $$33 = (float)($$29 + 1) / 2.0f;
        $$11.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        $$11.vertex($$25, -$$26, -100.0f, $$26).uv($$32, $$33).endVertex();
        $$11.vertex($$25, $$26, -100.0f, $$26).uv($$30, $$33).endVertex();
        $$11.vertex($$25, $$26, -100.0f, -$$26).uv($$30, $$31).endVertex();
        $$11.vertex($$25, -$$26, -100.0f, -$$26).uv($$32, $$31).endVertex();
        BufferUploader.drawWithShader($$11.end());
        RenderSystem.disableTexture();
        float $$34 = this.level.getStarBrightness($$2) * $$24;
        if ($$34 > 0.0f) {
            RenderSystem.setShaderColor($$34, $$34, $$34, $$34);
            FogRenderer.setupNoFog();
            this.starBuffer.bind();
            this.starBuffer.drawWithShader($$0.last().pose(), $$1, GameRenderer.getPositionShader());
            VertexBuffer.unbind();
            $$5.run();
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        $$0.popPose();
        RenderSystem.disableTexture();
        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
        double $$35 = this.minecraft.player.getEyePosition((float)$$2).y - this.level.getLevelData().getHorizonHeight(this.level);
        if ($$35 < 0.0) {
            $$0.pushPose();
            $$0.translate(0.0f, 12.0f, 0.0f);
            this.darkBuffer.bind();
            this.darkBuffer.drawWithShader($$0.last().pose(), $$1, $$12);
            VertexBuffer.unbind();
            $$0.popPose();
        }
        if (this.level.effects().hasGround()) {
            RenderSystem.setShaderColor($$8 * 0.2f + 0.04f, $$9 * 0.2f + 0.04f, $$10 * 0.6f + 0.1f, 1.0f);
        } else {
            RenderSystem.setShaderColor($$8, $$9, $$10, 1.0f);
        }
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
    }

    private boolean doesMobEffectBlockSky(Camera $$0) {
        Entity entity = $$0.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity $$1 = (LivingEntity)entity;
            return $$1.hasEffect(MobEffects.BLINDNESS) || $$1.hasEffect(MobEffects.DARKNESS);
        }
        return false;
    }

    public void renderClouds(PoseStack $$0, Matrix4f $$1, float $$2, double $$3, double $$4, double $$5) {
        float $$6 = this.level.effects().getCloudHeight();
        if (Float.isNaN((float)$$6)) {
            return;
        }
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(true);
        float $$7 = 12.0f;
        float $$8 = 4.0f;
        double $$9 = 2.0E-4;
        double $$10 = ((float)this.ticks + $$2) * 0.03f;
        double $$11 = ($$3 + $$10) / 12.0;
        double $$12 = $$6 - (float)$$4 + 0.33f;
        double $$13 = $$5 / 12.0 + (double)0.33f;
        $$11 -= (double)(Mth.floor($$11 / 2048.0) * 2048);
        $$13 -= (double)(Mth.floor($$13 / 2048.0) * 2048);
        float $$14 = (float)($$11 - (double)Mth.floor($$11));
        float $$15 = (float)($$12 / 4.0 - (double)Mth.floor($$12 / 4.0)) * 4.0f;
        float $$16 = (float)($$13 - (double)Mth.floor($$13));
        Vec3 $$17 = this.level.getCloudColor($$2);
        int $$18 = (int)Math.floor((double)$$11);
        int $$19 = (int)Math.floor((double)($$12 / 4.0));
        int $$20 = (int)Math.floor((double)$$13);
        if ($$18 != this.prevCloudX || $$19 != this.prevCloudY || $$20 != this.prevCloudZ || this.minecraft.options.getCloudsType() != this.prevCloudsType || this.prevCloudColor.distanceToSqr($$17) > 2.0E-4) {
            this.prevCloudX = $$18;
            this.prevCloudY = $$19;
            this.prevCloudZ = $$20;
            this.prevCloudColor = $$17;
            this.prevCloudsType = this.minecraft.options.getCloudsType();
            this.generateClouds = true;
        }
        if (this.generateClouds) {
            this.generateClouds = false;
            BufferBuilder $$21 = Tesselator.getInstance().getBuilder();
            if (this.cloudBuffer != null) {
                this.cloudBuffer.close();
            }
            this.cloudBuffer = new VertexBuffer();
            BufferBuilder.RenderedBuffer $$22 = this.buildClouds($$21, $$11, $$12, $$13, $$17);
            this.cloudBuffer.bind();
            this.cloudBuffer.upload($$22);
            VertexBuffer.unbind();
        }
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexColorNormalShader));
        RenderSystem.setShaderTexture(0, CLOUDS_LOCATION);
        FogRenderer.levelFogColor();
        $$0.pushPose();
        $$0.scale(12.0f, 1.0f, 12.0f);
        $$0.translate(-$$14, $$15, -$$16);
        if (this.cloudBuffer != null) {
            int $$23;
            this.cloudBuffer.bind();
            for (int $$24 = $$23 = this.prevCloudsType == CloudStatus.FANCY ? 0 : 1; $$24 < 2; ++$$24) {
                if ($$24 == 0) {
                    RenderSystem.colorMask(false, false, false, false);
                } else {
                    RenderSystem.colorMask(true, true, true, true);
                }
                ShaderInstance $$25 = RenderSystem.getShader();
                this.cloudBuffer.drawWithShader($$0.last().pose(), $$1, $$25);
            }
            VertexBuffer.unbind();
        }
        $$0.popPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private BufferBuilder.RenderedBuffer buildClouds(BufferBuilder $$0, double $$1, double $$2, double $$3, Vec3 $$4) {
        float $$5 = 4.0f;
        float $$6 = 0.00390625f;
        int $$7 = 8;
        int $$8 = 4;
        float $$9 = 9.765625E-4f;
        float $$10 = (float)Mth.floor($$1) * 0.00390625f;
        float $$11 = (float)Mth.floor($$3) * 0.00390625f;
        float $$12 = (float)$$4.x;
        float $$13 = (float)$$4.y;
        float $$14 = (float)$$4.z;
        float $$15 = $$12 * 0.9f;
        float $$16 = $$13 * 0.9f;
        float $$17 = $$14 * 0.9f;
        float $$18 = $$12 * 0.7f;
        float $$19 = $$13 * 0.7f;
        float $$20 = $$14 * 0.7f;
        float $$21 = $$12 * 0.8f;
        float $$22 = $$13 * 0.8f;
        float $$23 = $$14 * 0.8f;
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexColorNormalShader));
        $$0.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        float $$24 = (float)Math.floor((double)($$2 / 4.0)) * 4.0f;
        if (this.prevCloudsType == CloudStatus.FANCY) {
            for (int $$25 = -3; $$25 <= 4; ++$$25) {
                for (int $$26 = -3; $$26 <= 4; ++$$26) {
                    float $$27 = $$25 * 8;
                    float $$28 = $$26 * 8;
                    if ($$24 > -5.0f) {
                        $$0.vertex($$27 + 0.0f, $$24 + 0.0f, $$28 + 8.0f).uv(($$27 + 0.0f) * 0.00390625f + $$10, ($$28 + 8.0f) * 0.00390625f + $$11).color($$18, $$19, $$20, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        $$0.vertex($$27 + 8.0f, $$24 + 0.0f, $$28 + 8.0f).uv(($$27 + 8.0f) * 0.00390625f + $$10, ($$28 + 8.0f) * 0.00390625f + $$11).color($$18, $$19, $$20, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        $$0.vertex($$27 + 8.0f, $$24 + 0.0f, $$28 + 0.0f).uv(($$27 + 8.0f) * 0.00390625f + $$10, ($$28 + 0.0f) * 0.00390625f + $$11).color($$18, $$19, $$20, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        $$0.vertex($$27 + 0.0f, $$24 + 0.0f, $$28 + 0.0f).uv(($$27 + 0.0f) * 0.00390625f + $$10, ($$28 + 0.0f) * 0.00390625f + $$11).color($$18, $$19, $$20, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    }
                    if ($$24 <= 5.0f) {
                        $$0.vertex($$27 + 0.0f, $$24 + 4.0f - 9.765625E-4f, $$28 + 8.0f).uv(($$27 + 0.0f) * 0.00390625f + $$10, ($$28 + 8.0f) * 0.00390625f + $$11).color($$12, $$13, $$14, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        $$0.vertex($$27 + 8.0f, $$24 + 4.0f - 9.765625E-4f, $$28 + 8.0f).uv(($$27 + 8.0f) * 0.00390625f + $$10, ($$28 + 8.0f) * 0.00390625f + $$11).color($$12, $$13, $$14, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        $$0.vertex($$27 + 8.0f, $$24 + 4.0f - 9.765625E-4f, $$28 + 0.0f).uv(($$27 + 8.0f) * 0.00390625f + $$10, ($$28 + 0.0f) * 0.00390625f + $$11).color($$12, $$13, $$14, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        $$0.vertex($$27 + 0.0f, $$24 + 4.0f - 9.765625E-4f, $$28 + 0.0f).uv(($$27 + 0.0f) * 0.00390625f + $$10, ($$28 + 0.0f) * 0.00390625f + $$11).color($$12, $$13, $$14, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                    }
                    if ($$25 > -1) {
                        for (int $$29 = 0; $$29 < 8; ++$$29) {
                            $$0.vertex($$27 + (float)$$29 + 0.0f, $$24 + 0.0f, $$28 + 8.0f).uv(($$27 + (float)$$29 + 0.5f) * 0.00390625f + $$10, ($$28 + 8.0f) * 0.00390625f + $$11).color($$15, $$16, $$17, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            $$0.vertex($$27 + (float)$$29 + 0.0f, $$24 + 4.0f, $$28 + 8.0f).uv(($$27 + (float)$$29 + 0.5f) * 0.00390625f + $$10, ($$28 + 8.0f) * 0.00390625f + $$11).color($$15, $$16, $$17, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            $$0.vertex($$27 + (float)$$29 + 0.0f, $$24 + 4.0f, $$28 + 0.0f).uv(($$27 + (float)$$29 + 0.5f) * 0.00390625f + $$10, ($$28 + 0.0f) * 0.00390625f + $$11).color($$15, $$16, $$17, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            $$0.vertex($$27 + (float)$$29 + 0.0f, $$24 + 0.0f, $$28 + 0.0f).uv(($$27 + (float)$$29 + 0.5f) * 0.00390625f + $$10, ($$28 + 0.0f) * 0.00390625f + $$11).color($$15, $$16, $$17, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                        }
                    }
                    if ($$25 <= 1) {
                        for (int $$30 = 0; $$30 < 8; ++$$30) {
                            $$0.vertex($$27 + (float)$$30 + 1.0f - 9.765625E-4f, $$24 + 0.0f, $$28 + 8.0f).uv(($$27 + (float)$$30 + 0.5f) * 0.00390625f + $$10, ($$28 + 8.0f) * 0.00390625f + $$11).color($$15, $$16, $$17, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            $$0.vertex($$27 + (float)$$30 + 1.0f - 9.765625E-4f, $$24 + 4.0f, $$28 + 8.0f).uv(($$27 + (float)$$30 + 0.5f) * 0.00390625f + $$10, ($$28 + 8.0f) * 0.00390625f + $$11).color($$15, $$16, $$17, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            $$0.vertex($$27 + (float)$$30 + 1.0f - 9.765625E-4f, $$24 + 4.0f, $$28 + 0.0f).uv(($$27 + (float)$$30 + 0.5f) * 0.00390625f + $$10, ($$28 + 0.0f) * 0.00390625f + $$11).color($$15, $$16, $$17, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            $$0.vertex($$27 + (float)$$30 + 1.0f - 9.765625E-4f, $$24 + 0.0f, $$28 + 0.0f).uv(($$27 + (float)$$30 + 0.5f) * 0.00390625f + $$10, ($$28 + 0.0f) * 0.00390625f + $$11).color($$15, $$16, $$17, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                        }
                    }
                    if ($$26 > -1) {
                        for (int $$31 = 0; $$31 < 8; ++$$31) {
                            $$0.vertex($$27 + 0.0f, $$24 + 4.0f, $$28 + (float)$$31 + 0.0f).uv(($$27 + 0.0f) * 0.00390625f + $$10, ($$28 + (float)$$31 + 0.5f) * 0.00390625f + $$11).color($$21, $$22, $$23, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            $$0.vertex($$27 + 8.0f, $$24 + 4.0f, $$28 + (float)$$31 + 0.0f).uv(($$27 + 8.0f) * 0.00390625f + $$10, ($$28 + (float)$$31 + 0.5f) * 0.00390625f + $$11).color($$21, $$22, $$23, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            $$0.vertex($$27 + 8.0f, $$24 + 0.0f, $$28 + (float)$$31 + 0.0f).uv(($$27 + 8.0f) * 0.00390625f + $$10, ($$28 + (float)$$31 + 0.5f) * 0.00390625f + $$11).color($$21, $$22, $$23, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            $$0.vertex($$27 + 0.0f, $$24 + 0.0f, $$28 + (float)$$31 + 0.0f).uv(($$27 + 0.0f) * 0.00390625f + $$10, ($$28 + (float)$$31 + 0.5f) * 0.00390625f + $$11).color($$21, $$22, $$23, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                        }
                    }
                    if ($$26 > 1) continue;
                    for (int $$32 = 0; $$32 < 8; ++$$32) {
                        $$0.vertex($$27 + 0.0f, $$24 + 4.0f, $$28 + (float)$$32 + 1.0f - 9.765625E-4f).uv(($$27 + 0.0f) * 0.00390625f + $$10, ($$28 + (float)$$32 + 0.5f) * 0.00390625f + $$11).color($$21, $$22, $$23, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                        $$0.vertex($$27 + 8.0f, $$24 + 4.0f, $$28 + (float)$$32 + 1.0f - 9.765625E-4f).uv(($$27 + 8.0f) * 0.00390625f + $$10, ($$28 + (float)$$32 + 0.5f) * 0.00390625f + $$11).color($$21, $$22, $$23, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                        $$0.vertex($$27 + 8.0f, $$24 + 0.0f, $$28 + (float)$$32 + 1.0f - 9.765625E-4f).uv(($$27 + 8.0f) * 0.00390625f + $$10, ($$28 + (float)$$32 + 0.5f) * 0.00390625f + $$11).color($$21, $$22, $$23, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                        $$0.vertex($$27 + 0.0f, $$24 + 0.0f, $$28 + (float)$$32 + 1.0f - 9.765625E-4f).uv(($$27 + 0.0f) * 0.00390625f + $$10, ($$28 + (float)$$32 + 0.5f) * 0.00390625f + $$11).color($$21, $$22, $$23, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                    }
                }
            }
        } else {
            boolean $$33 = true;
            int $$34 = 32;
            for (int $$35 = -32; $$35 < 32; $$35 += 32) {
                for (int $$36 = -32; $$36 < 32; $$36 += 32) {
                    $$0.vertex($$35 + 0, $$24, $$36 + 32).uv((float)($$35 + 0) * 0.00390625f + $$10, (float)($$36 + 32) * 0.00390625f + $$11).color($$12, $$13, $$14, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    $$0.vertex($$35 + 32, $$24, $$36 + 32).uv((float)($$35 + 32) * 0.00390625f + $$10, (float)($$36 + 32) * 0.00390625f + $$11).color($$12, $$13, $$14, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    $$0.vertex($$35 + 32, $$24, $$36 + 0).uv((float)($$35 + 32) * 0.00390625f + $$10, (float)($$36 + 0) * 0.00390625f + $$11).color($$12, $$13, $$14, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    $$0.vertex($$35 + 0, $$24, $$36 + 0).uv((float)($$35 + 0) * 0.00390625f + $$10, (float)($$36 + 0) * 0.00390625f + $$11).color($$12, $$13, $$14, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                }
            }
        }
        return $$0.end();
    }

    private void compileChunks(Camera $$0) {
        this.minecraft.getProfiler().push("populate_chunks_to_compile");
        RenderRegionCache $$1 = new RenderRegionCache();
        BlockPos $$2 = $$0.getBlockPosition();
        ArrayList $$3 = Lists.newArrayList();
        for (RenderChunkInfo $$4 : this.renderChunksInFrustum) {
            ChunkRenderDispatcher.RenderChunk $$5 = $$4.chunk;
            ChunkPos $$6 = new ChunkPos($$5.getOrigin());
            if (!$$5.isDirty() || !this.level.getChunk($$6.x, $$6.z).isClientLightReady()) continue;
            boolean $$7 = false;
            if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.NEARBY) {
                BlockPos $$8 = $$5.getOrigin().offset(8, 8, 8);
                $$7 = $$8.distSqr($$2) < 768.0 || $$5.isDirtyFromPlayer();
            } else if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.PLAYER_AFFECTED) {
                $$7 = $$5.isDirtyFromPlayer();
            }
            if ($$7) {
                this.minecraft.getProfiler().push("build_near_sync");
                this.chunkRenderDispatcher.rebuildChunkSync($$5, $$1);
                $$5.setNotDirty();
                this.minecraft.getProfiler().pop();
                continue;
            }
            $$3.add((Object)$$5);
        }
        this.minecraft.getProfiler().popPush("upload");
        this.chunkRenderDispatcher.uploadAllPendingUploads();
        this.minecraft.getProfiler().popPush("schedule_async_compile");
        for (ChunkRenderDispatcher.RenderChunk $$9 : $$3) {
            $$9.rebuildChunkAsync(this.chunkRenderDispatcher, $$1);
            $$9.setNotDirty();
        }
        this.minecraft.getProfiler().pop();
    }

    private void renderWorldBorder(Camera $$0) {
        BufferBuilder $$1 = Tesselator.getInstance().getBuilder();
        WorldBorder $$2 = this.level.getWorldBorder();
        double $$3 = this.minecraft.options.getEffectiveRenderDistance() * 16;
        if ($$0.getPosition().x < $$2.getMaxX() - $$3 && $$0.getPosition().x > $$2.getMinX() + $$3 && $$0.getPosition().z < $$2.getMaxZ() - $$3 && $$0.getPosition().z > $$2.getMinZ() + $$3) {
            return;
        }
        double $$4 = 1.0 - $$2.getDistanceToBorder($$0.getPosition().x, $$0.getPosition().z) / $$3;
        $$4 = Math.pow((double)$$4, (double)4.0);
        $$4 = Mth.clamp($$4, 0.0, 1.0);
        double $$5 = $$0.getPosition().x;
        double $$6 = $$0.getPosition().z;
        double $$7 = this.minecraft.gameRenderer.getDepthFar();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderTexture(0, FORCEFIELD_LOCATION);
        RenderSystem.depthMask(Minecraft.useShaderTransparency());
        PoseStack $$8 = RenderSystem.getModelViewStack();
        $$8.pushPose();
        RenderSystem.applyModelViewMatrix();
        int $$9 = $$2.getStatus().getColor();
        float $$10 = (float)($$9 >> 16 & 0xFF) / 255.0f;
        float $$11 = (float)($$9 >> 8 & 0xFF) / 255.0f;
        float $$12 = (float)($$9 & 0xFF) / 255.0f;
        RenderSystem.setShaderColor($$10, $$11, $$12, (float)$$4);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.polygonOffset(-3.0f, -3.0f);
        RenderSystem.enablePolygonOffset();
        RenderSystem.disableCull();
        float $$13 = (float)(Util.getMillis() % 3000L) / 3000.0f;
        float $$14 = (float)(-Mth.frac($$0.getPosition().y * 0.5));
        float $$15 = $$14 + (float)$$7;
        $$1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        double $$16 = Math.max((double)Mth.floor($$6 - $$3), (double)$$2.getMinZ());
        double $$17 = Math.min((double)Mth.ceil($$6 + $$3), (double)$$2.getMaxZ());
        float $$18 = (float)(Mth.floor($$16) & 1) * 0.5f;
        if ($$5 > $$2.getMaxX() - $$3) {
            float $$19 = $$18;
            double $$20 = $$16;
            while ($$20 < $$17) {
                double $$21 = Math.min((double)1.0, (double)($$17 - $$20));
                float $$22 = (float)$$21 * 0.5f;
                $$1.vertex($$2.getMaxX() - $$5, -$$7, $$20 - $$6).uv($$13 - $$19, $$13 + $$15).endVertex();
                $$1.vertex($$2.getMaxX() - $$5, -$$7, $$20 + $$21 - $$6).uv($$13 - ($$22 + $$19), $$13 + $$15).endVertex();
                $$1.vertex($$2.getMaxX() - $$5, $$7, $$20 + $$21 - $$6).uv($$13 - ($$22 + $$19), $$13 + $$14).endVertex();
                $$1.vertex($$2.getMaxX() - $$5, $$7, $$20 - $$6).uv($$13 - $$19, $$13 + $$14).endVertex();
                $$20 += 1.0;
                $$19 += 0.5f;
            }
        }
        if ($$5 < $$2.getMinX() + $$3) {
            float $$23 = $$18;
            double $$24 = $$16;
            while ($$24 < $$17) {
                double $$25 = Math.min((double)1.0, (double)($$17 - $$24));
                float $$26 = (float)$$25 * 0.5f;
                $$1.vertex($$2.getMinX() - $$5, -$$7, $$24 - $$6).uv($$13 + $$23, $$13 + $$15).endVertex();
                $$1.vertex($$2.getMinX() - $$5, -$$7, $$24 + $$25 - $$6).uv($$13 + $$26 + $$23, $$13 + $$15).endVertex();
                $$1.vertex($$2.getMinX() - $$5, $$7, $$24 + $$25 - $$6).uv($$13 + $$26 + $$23, $$13 + $$14).endVertex();
                $$1.vertex($$2.getMinX() - $$5, $$7, $$24 - $$6).uv($$13 + $$23, $$13 + $$14).endVertex();
                $$24 += 1.0;
                $$23 += 0.5f;
            }
        }
        $$16 = Math.max((double)Mth.floor($$5 - $$3), (double)$$2.getMinX());
        $$17 = Math.min((double)Mth.ceil($$5 + $$3), (double)$$2.getMaxX());
        $$18 = (float)(Mth.floor($$16) & 1) * 0.5f;
        if ($$6 > $$2.getMaxZ() - $$3) {
            float $$27 = $$18;
            double $$28 = $$16;
            while ($$28 < $$17) {
                double $$29 = Math.min((double)1.0, (double)($$17 - $$28));
                float $$30 = (float)$$29 * 0.5f;
                $$1.vertex($$28 - $$5, -$$7, $$2.getMaxZ() - $$6).uv($$13 + $$27, $$13 + $$15).endVertex();
                $$1.vertex($$28 + $$29 - $$5, -$$7, $$2.getMaxZ() - $$6).uv($$13 + $$30 + $$27, $$13 + $$15).endVertex();
                $$1.vertex($$28 + $$29 - $$5, $$7, $$2.getMaxZ() - $$6).uv($$13 + $$30 + $$27, $$13 + $$14).endVertex();
                $$1.vertex($$28 - $$5, $$7, $$2.getMaxZ() - $$6).uv($$13 + $$27, $$13 + $$14).endVertex();
                $$28 += 1.0;
                $$27 += 0.5f;
            }
        }
        if ($$6 < $$2.getMinZ() + $$3) {
            float $$31 = $$18;
            double $$32 = $$16;
            while ($$32 < $$17) {
                double $$33 = Math.min((double)1.0, (double)($$17 - $$32));
                float $$34 = (float)$$33 * 0.5f;
                $$1.vertex($$32 - $$5, -$$7, $$2.getMinZ() - $$6).uv($$13 - $$31, $$13 + $$15).endVertex();
                $$1.vertex($$32 + $$33 - $$5, -$$7, $$2.getMinZ() - $$6).uv($$13 - ($$34 + $$31), $$13 + $$15).endVertex();
                $$1.vertex($$32 + $$33 - $$5, $$7, $$2.getMinZ() - $$6).uv($$13 - ($$34 + $$31), $$13 + $$14).endVertex();
                $$1.vertex($$32 - $$5, $$7, $$2.getMinZ() - $$6).uv($$13 - $$31, $$13 + $$14).endVertex();
                $$32 += 1.0;
                $$31 += 0.5f;
            }
        }
        BufferUploader.drawWithShader($$1.end());
        RenderSystem.enableCull();
        RenderSystem.polygonOffset(0.0f, 0.0f);
        RenderSystem.disablePolygonOffset();
        RenderSystem.disableBlend();
        $$8.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
    }

    private void renderHitOutline(PoseStack $$0, VertexConsumer $$1, Entity $$2, double $$3, double $$4, double $$5, BlockPos $$6, BlockState $$7) {
        LevelRenderer.renderShape($$0, $$1, $$7.getShape(this.level, $$6, CollisionContext.of($$2)), (double)$$6.getX() - $$3, (double)$$6.getY() - $$4, (double)$$6.getZ() - $$5, 0.0f, 0.0f, 0.0f, 0.4f);
    }

    public static void renderVoxelShape(PoseStack $$0, VertexConsumer $$1, VoxelShape $$2, double $$3, double $$4, double $$5, float $$6, float $$7, float $$8, float $$9) {
        List<AABB> $$10 = $$2.toAabbs();
        int $$11 = Mth.ceil((double)$$10.size() / 3.0);
        for (int $$12 = 0; $$12 < $$10.size(); ++$$12) {
            AABB $$13 = (AABB)$$10.get($$12);
            float $$14 = ((float)$$12 % (float)$$11 + 1.0f) / (float)$$11;
            float $$15 = $$12 / $$11;
            float $$16 = $$14 * (float)($$15 == 0.0f ? 1 : 0);
            float $$17 = $$14 * (float)($$15 == 1.0f ? 1 : 0);
            float $$18 = $$14 * (float)($$15 == 2.0f ? 1 : 0);
            LevelRenderer.renderShape($$0, $$1, Shapes.create($$13.move(0.0, 0.0, 0.0)), $$3, $$4, $$5, $$16, $$17, $$18, 1.0f);
        }
    }

    private static void renderShape(PoseStack $$0, VertexConsumer $$1, VoxelShape $$2, double $$3, double $$4, double $$5, float $$6, float $$7, float $$8, float $$92) {
        PoseStack.Pose $$102 = $$0.last();
        $$2.forAllEdges(($$9, $$10, $$11, $$12, $$13, $$14) -> {
            float $$15 = (float)($$12 - $$9);
            float $$16 = (float)($$13 - $$10);
            float $$17 = (float)($$14 - $$11);
            float $$18 = Mth.sqrt($$15 * $$15 + $$16 * $$16 + $$17 * $$17);
            $$1.vertex($$102.pose(), (float)($$9 + $$3), (float)($$10 + $$4), (float)($$11 + $$5)).color($$6, $$7, $$8, $$92).normal($$102.normal(), $$15 /= $$18, $$16 /= $$18, $$17 /= $$18).endVertex();
            $$1.vertex($$102.pose(), (float)($$12 + $$3), (float)($$13 + $$4), (float)($$14 + $$5)).color($$6, $$7, $$8, $$92).normal($$102.normal(), $$15, $$16, $$17).endVertex();
        });
    }

    public static void renderLineBox(VertexConsumer $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, float $$7, float $$8, float $$9, float $$10) {
        LevelRenderer.renderLineBox(new PoseStack(), $$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$7, $$8, $$9);
    }

    public static void renderLineBox(PoseStack $$0, VertexConsumer $$1, AABB $$2, float $$3, float $$4, float $$5, float $$6) {
        LevelRenderer.renderLineBox($$0, $$1, $$2.minX, $$2.minY, $$2.minZ, $$2.maxX, $$2.maxY, $$2.maxZ, $$3, $$4, $$5, $$6, $$3, $$4, $$5);
    }

    public static void renderLineBox(PoseStack $$0, VertexConsumer $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7, float $$8, float $$9, float $$10, float $$11) {
        LevelRenderer.renderLineBox($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11, $$8, $$9, $$10);
    }

    public static void renderLineBox(PoseStack $$0, VertexConsumer $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7, float $$8, float $$9, float $$10, float $$11, float $$12, float $$13, float $$14) {
        Matrix4f $$15 = $$0.last().pose();
        Matrix3f $$16 = $$0.last().normal();
        float $$17 = (float)$$2;
        float $$18 = (float)$$3;
        float $$19 = (float)$$4;
        float $$20 = (float)$$5;
        float $$21 = (float)$$6;
        float $$22 = (float)$$7;
        $$1.vertex($$15, $$17, $$18, $$19).color($$8, $$13, $$14, $$11).normal($$16, 1.0f, 0.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$20, $$18, $$19).color($$8, $$13, $$14, $$11).normal($$16, 1.0f, 0.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$17, $$18, $$19).color($$12, $$9, $$14, $$11).normal($$16, 0.0f, 1.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$17, $$21, $$19).color($$12, $$9, $$14, $$11).normal($$16, 0.0f, 1.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$17, $$18, $$19).color($$12, $$13, $$10, $$11).normal($$16, 0.0f, 0.0f, 1.0f).endVertex();
        $$1.vertex($$15, $$17, $$18, $$22).color($$12, $$13, $$10, $$11).normal($$16, 0.0f, 0.0f, 1.0f).endVertex();
        $$1.vertex($$15, $$20, $$18, $$19).color($$8, $$9, $$10, $$11).normal($$16, 0.0f, 1.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$20, $$21, $$19).color($$8, $$9, $$10, $$11).normal($$16, 0.0f, 1.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$20, $$21, $$19).color($$8, $$9, $$10, $$11).normal($$16, -1.0f, 0.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$17, $$21, $$19).color($$8, $$9, $$10, $$11).normal($$16, -1.0f, 0.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$17, $$21, $$19).color($$8, $$9, $$10, $$11).normal($$16, 0.0f, 0.0f, 1.0f).endVertex();
        $$1.vertex($$15, $$17, $$21, $$22).color($$8, $$9, $$10, $$11).normal($$16, 0.0f, 0.0f, 1.0f).endVertex();
        $$1.vertex($$15, $$17, $$21, $$22).color($$8, $$9, $$10, $$11).normal($$16, 0.0f, -1.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$17, $$18, $$22).color($$8, $$9, $$10, $$11).normal($$16, 0.0f, -1.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$17, $$18, $$22).color($$8, $$9, $$10, $$11).normal($$16, 1.0f, 0.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$20, $$18, $$22).color($$8, $$9, $$10, $$11).normal($$16, 1.0f, 0.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$20, $$18, $$22).color($$8, $$9, $$10, $$11).normal($$16, 0.0f, 0.0f, -1.0f).endVertex();
        $$1.vertex($$15, $$20, $$18, $$19).color($$8, $$9, $$10, $$11).normal($$16, 0.0f, 0.0f, -1.0f).endVertex();
        $$1.vertex($$15, $$17, $$21, $$22).color($$8, $$9, $$10, $$11).normal($$16, 1.0f, 0.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$20, $$21, $$22).color($$8, $$9, $$10, $$11).normal($$16, 1.0f, 0.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$20, $$18, $$22).color($$8, $$9, $$10, $$11).normal($$16, 0.0f, 1.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$20, $$21, $$22).color($$8, $$9, $$10, $$11).normal($$16, 0.0f, 1.0f, 0.0f).endVertex();
        $$1.vertex($$15, $$20, $$21, $$19).color($$8, $$9, $$10, $$11).normal($$16, 0.0f, 0.0f, 1.0f).endVertex();
        $$1.vertex($$15, $$20, $$21, $$22).color($$8, $$9, $$10, $$11).normal($$16, 0.0f, 0.0f, 1.0f).endVertex();
    }

    public static void addChainedFilledBoxVertices(BufferBuilder $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, float $$7, float $$8, float $$9, float $$10) {
        $$0.vertex($$1, $$2, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$2, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$2, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$2, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$5, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$5, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$5, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$2, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$5, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$2, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$2, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$2, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$5, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$5, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$5, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$2, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$5, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$2, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$2, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$2, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$2, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$2, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$2, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$5, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$5, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$1, $$5, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$5, $$3).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$5, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$5, $$6).color($$7, $$8, $$9, $$10).endVertex();
        $$0.vertex($$4, $$5, $$6).color($$7, $$8, $$9, $$10).endVertex();
    }

    public void blockChanged(BlockGetter $$0, BlockPos $$1, BlockState $$2, BlockState $$3, int $$4) {
        this.setBlockDirty($$1, ($$4 & 8) != 0);
    }

    private void setBlockDirty(BlockPos $$0, boolean $$1) {
        for (int $$2 = $$0.getZ() - 1; $$2 <= $$0.getZ() + 1; ++$$2) {
            for (int $$3 = $$0.getX() - 1; $$3 <= $$0.getX() + 1; ++$$3) {
                for (int $$4 = $$0.getY() - 1; $$4 <= $$0.getY() + 1; ++$$4) {
                    this.setSectionDirty(SectionPos.blockToSectionCoord($$3), SectionPos.blockToSectionCoord($$4), SectionPos.blockToSectionCoord($$2), $$1);
                }
            }
        }
    }

    public void setBlocksDirty(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        for (int $$6 = $$2 - 1; $$6 <= $$5 + 1; ++$$6) {
            for (int $$7 = $$0 - 1; $$7 <= $$3 + 1; ++$$7) {
                for (int $$8 = $$1 - 1; $$8 <= $$4 + 1; ++$$8) {
                    this.setSectionDirty(SectionPos.blockToSectionCoord($$7), SectionPos.blockToSectionCoord($$8), SectionPos.blockToSectionCoord($$6));
                }
            }
        }
    }

    public void setBlockDirty(BlockPos $$0, BlockState $$1, BlockState $$2) {
        if (this.minecraft.getModelManager().requiresRender($$1, $$2)) {
            this.setBlocksDirty($$0.getX(), $$0.getY(), $$0.getZ(), $$0.getX(), $$0.getY(), $$0.getZ());
        }
    }

    public void setSectionDirtyWithNeighbors(int $$0, int $$1, int $$2) {
        for (int $$3 = $$2 - 1; $$3 <= $$2 + 1; ++$$3) {
            for (int $$4 = $$0 - 1; $$4 <= $$0 + 1; ++$$4) {
                for (int $$5 = $$1 - 1; $$5 <= $$1 + 1; ++$$5) {
                    this.setSectionDirty($$4, $$5, $$3);
                }
            }
        }
    }

    public void setSectionDirty(int $$0, int $$1, int $$2) {
        this.setSectionDirty($$0, $$1, $$2, false);
    }

    private void setSectionDirty(int $$0, int $$1, int $$2, boolean $$3) {
        this.viewArea.setDirty($$0, $$1, $$2, $$3);
    }

    public void playStreamingMusic(@Nullable SoundEvent $$0, BlockPos $$1) {
        SoundInstance $$2 = (SoundInstance)this.playingRecords.get((Object)$$1);
        if ($$2 != null) {
            this.minecraft.getSoundManager().stop($$2);
            this.playingRecords.remove((Object)$$1);
        }
        if ($$0 != null) {
            RecordItem $$3 = RecordItem.getBySound($$0);
            if ($$3 != null) {
                this.minecraft.gui.setNowPlaying($$3.getDisplayName());
            }
            $$2 = SimpleSoundInstance.forRecord($$0, Vec3.atCenterOf($$1));
            this.playingRecords.put((Object)$$1, (Object)$$2);
            this.minecraft.getSoundManager().play($$2);
        }
        this.notifyNearbyEntities(this.level, $$1, $$0 != null);
    }

    private void notifyNearbyEntities(Level $$0, BlockPos $$1, boolean $$2) {
        List $$3 = $$0.getEntitiesOfClass(LivingEntity.class, new AABB($$1).inflate(3.0));
        for (LivingEntity $$4 : $$3) {
            $$4.setRecordPlayingNearby($$1, $$2);
        }
    }

    public void addParticle(ParticleOptions $$0, boolean $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        this.addParticle($$0, $$1, false, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    public void addParticle(ParticleOptions $$0, boolean $$1, boolean $$2, double $$3, double $$4, double $$5, double $$6, double $$7, double $$8) {
        try {
            this.addParticleInternal($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8);
        }
        catch (Throwable $$9) {
            CrashReport $$10 = CrashReport.forThrowable($$9, "Exception while adding particle");
            CrashReportCategory $$11 = $$10.addCategory("Particle being added");
            $$11.setDetail("ID", BuiltInRegistries.PARTICLE_TYPE.getKey($$0.getType()));
            $$11.setDetail("Parameters", $$0.writeToString());
            $$11.setDetail("Position", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this.level, $$3, $$4, $$5));
            throw new ReportedException($$10);
        }
    }

    private <T extends ParticleOptions> void addParticle(T $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        this.addParticle($$0, $$0.getType().getOverrideLimiter(), $$1, $$2, $$3, $$4, $$5, $$6);
    }

    @Nullable
    private Particle addParticleInternal(ParticleOptions $$0, boolean $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        return this.addParticleInternal($$0, $$1, false, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Nullable
    private Particle addParticleInternal(ParticleOptions $$0, boolean $$1, boolean $$2, double $$3, double $$4, double $$5, double $$6, double $$7, double $$8) {
        Camera $$9 = this.minecraft.gameRenderer.getMainCamera();
        if (this.minecraft == null || !$$9.isInitialized() || this.minecraft.particleEngine == null) {
            return null;
        }
        ParticleStatus $$10 = this.calculateParticleLevel($$2);
        if ($$1) {
            return this.minecraft.particleEngine.createParticle($$0, $$3, $$4, $$5, $$6, $$7, $$8);
        }
        if ($$9.getPosition().distanceToSqr($$3, $$4, $$5) > 1024.0) {
            return null;
        }
        if ($$10 == ParticleStatus.MINIMAL) {
            return null;
        }
        return this.minecraft.particleEngine.createParticle($$0, $$3, $$4, $$5, $$6, $$7, $$8);
    }

    private ParticleStatus calculateParticleLevel(boolean $$0) {
        ParticleStatus $$1 = this.minecraft.options.particles().get();
        if ($$0 && $$1 == ParticleStatus.MINIMAL && this.level.random.nextInt(10) == 0) {
            $$1 = ParticleStatus.DECREASED;
        }
        if ($$1 == ParticleStatus.DECREASED && this.level.random.nextInt(3) == 0) {
            $$1 = ParticleStatus.MINIMAL;
        }
        return $$1;
    }

    public void clear() {
    }

    public void globalLevelEvent(int $$0, BlockPos $$1, int $$2) {
        switch ($$0) {
            case 1023: 
            case 1028: 
            case 1038: {
                Camera $$3 = this.minecraft.gameRenderer.getMainCamera();
                if (!$$3.isInitialized()) break;
                double $$4 = (double)$$1.getX() - $$3.getPosition().x;
                double $$5 = (double)$$1.getY() - $$3.getPosition().y;
                double $$6 = (double)$$1.getZ() - $$3.getPosition().z;
                double $$7 = Math.sqrt((double)($$4 * $$4 + $$5 * $$5 + $$6 * $$6));
                double $$8 = $$3.getPosition().x;
                double $$9 = $$3.getPosition().y;
                double $$10 = $$3.getPosition().z;
                if ($$7 > 0.0) {
                    $$8 += $$4 / $$7 * 2.0;
                    $$9 += $$5 / $$7 * 2.0;
                    $$10 += $$6 / $$7 * 2.0;
                }
                if ($$0 == 1023) {
                    this.level.playLocalSound($$8, $$9, $$10, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                if ($$0 == 1038) {
                    this.level.playLocalSound($$8, $$9, $$10, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                this.level.playLocalSound($$8, $$9, $$10, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 5.0f, 1.0f, false);
            }
        }
    }

    public void levelEvent(int $$0, BlockPos $$1, int $$2) {
        RandomSource $$3 = this.level.random;
        switch ($$0) {
            case 1035: {
                this.level.playLocalSound($$1, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1033: {
                this.level.playLocalSound($$1, SoundEvents.CHORUS_FLOWER_GROW, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.level.playLocalSound($$1, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1032: {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRAVEL, $$3.nextFloat() * 0.4f + 0.8f, 0.25f));
                break;
            }
            case 1001: {
                this.level.playLocalSound($$1, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1000: {
                this.level.playLocalSound($$1, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1003: {
                this.level.playLocalSound($$1, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1004: {
                this.level.playLocalSound($$1, SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.level.playLocalSound($$1, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 2000: {
                Direction $$4 = Direction.from3DDataValue($$2);
                int $$5 = $$4.getStepX();
                int $$6 = $$4.getStepY();
                int $$7 = $$4.getStepZ();
                double $$8 = (double)$$1.getX() + (double)$$5 * 0.6 + 0.5;
                double $$9 = (double)$$1.getY() + (double)$$6 * 0.6 + 0.5;
                double $$10 = (double)$$1.getZ() + (double)$$7 * 0.6 + 0.5;
                for (int $$11 = 0; $$11 < 10; ++$$11) {
                    double $$12 = $$3.nextDouble() * 0.2 + 0.01;
                    double $$13 = $$8 + (double)$$5 * 0.01 + ($$3.nextDouble() - 0.5) * (double)$$7 * 0.5;
                    double $$14 = $$9 + (double)$$6 * 0.01 + ($$3.nextDouble() - 0.5) * (double)$$6 * 0.5;
                    double $$15 = $$10 + (double)$$7 * 0.01 + ($$3.nextDouble() - 0.5) * (double)$$5 * 0.5;
                    double $$16 = (double)$$5 * $$12 + $$3.nextGaussian() * 0.01;
                    double $$17 = (double)$$6 * $$12 + $$3.nextGaussian() * 0.01;
                    double $$18 = (double)$$7 * $$12 + $$3.nextGaussian() * 0.01;
                    this.addParticle(ParticleTypes.SMOKE, $$13, $$14, $$15, $$16, $$17, $$18);
                }
                break;
            }
            case 2003: {
                double $$19 = (double)$$1.getX() + 0.5;
                double $$20 = $$1.getY();
                double $$21 = (double)$$1.getZ() + 0.5;
                for (int $$22 = 0; $$22 < 8; ++$$22) {
                    this.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), $$19, $$20, $$21, $$3.nextGaussian() * 0.15, $$3.nextDouble() * 0.2, $$3.nextGaussian() * 0.15);
                }
                for (double $$23 = 0.0; $$23 < Math.PI * 2; $$23 += 0.15707963267948966) {
                    this.addParticle(ParticleTypes.PORTAL, $$19 + Math.cos((double)$$23) * 5.0, $$20 - 0.4, $$21 + Math.sin((double)$$23) * 5.0, Math.cos((double)$$23) * -5.0, 0.0, Math.sin((double)$$23) * -5.0);
                    this.addParticle(ParticleTypes.PORTAL, $$19 + Math.cos((double)$$23) * 5.0, $$20 - 0.4, $$21 + Math.sin((double)$$23) * 5.0, Math.cos((double)$$23) * -7.0, 0.0, Math.sin((double)$$23) * -7.0);
                }
                break;
            }
            case 2002: 
            case 2007: {
                Vec3 $$24 = Vec3.atBottomCenterOf($$1);
                for (int $$25 = 0; $$25 < 8; ++$$25) {
                    this.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), $$24.x, $$24.y, $$24.z, $$3.nextGaussian() * 0.15, $$3.nextDouble() * 0.2, $$3.nextGaussian() * 0.15);
                }
                float $$26 = (float)($$2 >> 16 & 0xFF) / 255.0f;
                float $$27 = (float)($$2 >> 8 & 0xFF) / 255.0f;
                float $$28 = (float)($$2 >> 0 & 0xFF) / 255.0f;
                SimpleParticleType $$29 = $$0 == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;
                for (int $$30 = 0; $$30 < 100; ++$$30) {
                    double $$31 = $$3.nextDouble() * 4.0;
                    double $$32 = $$3.nextDouble() * Math.PI * 2.0;
                    double $$33 = Math.cos((double)$$32) * $$31;
                    double $$34 = 0.01 + $$3.nextDouble() * 0.5;
                    double $$35 = Math.sin((double)$$32) * $$31;
                    Particle $$36 = this.addParticleInternal($$29, $$29.getType().getOverrideLimiter(), $$24.x + $$33 * 0.1, $$24.y + 0.3, $$24.z + $$35 * 0.1, $$33, $$34, $$35);
                    if ($$36 == null) continue;
                    float $$37 = 0.75f + $$3.nextFloat() * 0.25f;
                    $$36.setColor($$26 * $$37, $$27 * $$37, $$28 * $$37);
                    $$36.setPower((float)$$31);
                }
                this.level.playLocalSound($$1, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0f, $$3.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2001: {
                BlockState $$38 = Block.stateById($$2);
                if (!$$38.isAir()) {
                    SoundType $$39 = $$38.getSoundType();
                    this.level.playLocalSound($$1, $$39.getBreakSound(), SoundSource.BLOCKS, ($$39.getVolume() + 1.0f) / 2.0f, $$39.getPitch() * 0.8f, false);
                }
                this.level.addDestroyBlockEffect($$1, $$38);
                break;
            }
            case 2004: {
                for (int $$40 = 0; $$40 < 20; ++$$40) {
                    double $$41 = (double)$$1.getX() + 0.5 + ($$3.nextDouble() - 0.5) * 2.0;
                    double $$42 = (double)$$1.getY() + 0.5 + ($$3.nextDouble() - 0.5) * 2.0;
                    double $$43 = (double)$$1.getZ() + 0.5 + ($$3.nextDouble() - 0.5) * 2.0;
                    this.level.addParticle(ParticleTypes.SMOKE, $$41, $$42, $$43, 0.0, 0.0, 0.0);
                    this.level.addParticle(ParticleTypes.FLAME, $$41, $$42, $$43, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2005: {
                BoneMealItem.addGrowthParticles(this.level, $$1, $$2);
                break;
            }
            case 1505: {
                BoneMealItem.addGrowthParticles(this.level, $$1, $$2);
                this.level.playLocalSound($$1, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 3002: {
                if ($$2 >= 0 && $$2 < Direction.Axis.VALUES.length) {
                    ParticleUtils.spawnParticlesAlongAxis(Direction.Axis.VALUES[$$2], this.level, $$1, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(10, 19));
                    break;
                }
                ParticleUtils.spawnParticlesOnBlockFaces(this.level, $$1, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(3, 5));
                break;
            }
            case 3006: {
                int $$44 = $$2 >> 6;
                if ($$44 > 0) {
                    if ($$3.nextFloat() < 0.3f + (float)$$44 * 0.1f) {
                        float $$45 = 0.15f + 0.02f * (float)$$44 * (float)$$44 * $$3.nextFloat();
                        float $$46 = 0.4f + 0.3f * (float)$$44 * $$3.nextFloat();
                        this.level.playLocalSound($$1, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, $$45, $$46, false);
                    }
                    byte $$47 = (byte)($$2 & 0x3F);
                    UniformInt $$48 = UniformInt.of(0, $$44);
                    float $$49 = 0.005f;
                    Supplier $$50 = () -> new Vec3(Mth.nextDouble($$3, -0.005f, 0.005f), Mth.nextDouble($$3, -0.005f, 0.005f), Mth.nextDouble($$3, -0.005f, 0.005f));
                    if ($$47 == 0) {
                        for (Direction $$51 : Direction.values()) {
                            float $$52 = $$51 == Direction.DOWN ? (float)Math.PI : 0.0f;
                            double $$53 = $$51.getAxis() == Direction.Axis.Y ? 0.65 : 0.57;
                            ParticleUtils.spawnParticlesOnBlockFace(this.level, $$1, new SculkChargeParticleOptions($$52), $$48, $$51, (Supplier<Vec3>)$$50, $$53);
                        }
                    } else {
                        for (Direction $$54 : MultifaceBlock.unpack($$47)) {
                            float $$55 = $$54 == Direction.UP ? (float)Math.PI : 0.0f;
                            double $$56 = 0.35;
                            ParticleUtils.spawnParticlesOnBlockFace(this.level, $$1, new SculkChargeParticleOptions($$55), $$48, $$54, (Supplier<Vec3>)$$50, 0.35);
                        }
                    }
                } else {
                    this.level.playLocalSound($$1, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                    boolean $$57 = this.level.getBlockState($$1).isCollisionShapeFullBlock(this.level, $$1);
                    int $$58 = $$57 ? 40 : 20;
                    float $$59 = $$57 ? 0.45f : 0.25f;
                    float $$60 = 0.07f;
                    for (int $$61 = 0; $$61 < $$58; ++$$61) {
                        float $$62 = 2.0f * $$3.nextFloat() - 1.0f;
                        float $$63 = 2.0f * $$3.nextFloat() - 1.0f;
                        float $$64 = 2.0f * $$3.nextFloat() - 1.0f;
                        this.level.addParticle(ParticleTypes.SCULK_CHARGE_POP, (double)$$1.getX() + 0.5 + (double)($$62 * $$59), (double)$$1.getY() + 0.5 + (double)($$63 * $$59), (double)$$1.getZ() + 0.5 + (double)($$64 * $$59), $$62 * 0.07f, $$63 * 0.07f, $$64 * 0.07f);
                    }
                }
                break;
            }
            case 3007: {
                for (int $$65 = 0; $$65 < 10; ++$$65) {
                    this.level.addParticle(new ShriekParticleOption($$65 * 5), false, (double)$$1.getX() + 0.5, (double)$$1.getY() + SculkShriekerBlock.TOP_Y, (double)$$1.getZ() + 0.5, 0.0, 0.0, 0.0);
                }
                this.level.playLocalSound((double)$$1.getX() + 0.5, (double)$$1.getY() + SculkShriekerBlock.TOP_Y, (double)$$1.getZ() + 0.5, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 2.0f, 0.6f + this.level.random.nextFloat() * 0.4f, false);
                break;
            }
            case 3003: {
                ParticleUtils.spawnParticlesOnBlockFaces(this.level, $$1, ParticleTypes.WAX_ON, UniformInt.of(3, 5));
                this.level.playLocalSound($$1, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 3004: {
                ParticleUtils.spawnParticlesOnBlockFaces(this.level, $$1, ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
                break;
            }
            case 3005: {
                ParticleUtils.spawnParticlesOnBlockFaces(this.level, $$1, ParticleTypes.SCRAPE, UniformInt.of(3, 5));
                break;
            }
            case 2008: {
                this.level.addParticle(ParticleTypes.EXPLOSION, (double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5, 0.0, 0.0, 0.0);
                break;
            }
            case 1500: {
                ComposterBlock.handleFill(this.level, $$1, $$2 > 0);
                break;
            }
            case 1504: {
                PointedDripstoneBlock.spawnDripParticle(this.level, $$1, this.level.getBlockState($$1));
                break;
            }
            case 1501: {
                this.level.playLocalSound($$1, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + ($$3.nextFloat() - $$3.nextFloat()) * 0.8f, false);
                for (int $$66 = 0; $$66 < 8; ++$$66) {
                    this.level.addParticle(ParticleTypes.LARGE_SMOKE, (double)$$1.getX() + $$3.nextDouble(), (double)$$1.getY() + 1.2, (double)$$1.getZ() + $$3.nextDouble(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1502: {
                this.level.playLocalSound($$1, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5f, 2.6f + ($$3.nextFloat() - $$3.nextFloat()) * 0.8f, false);
                for (int $$67 = 0; $$67 < 5; ++$$67) {
                    double $$68 = (double)$$1.getX() + $$3.nextDouble() * 0.6 + 0.2;
                    double $$69 = (double)$$1.getY() + $$3.nextDouble() * 0.6 + 0.2;
                    double $$70 = (double)$$1.getZ() + $$3.nextDouble() * 0.6 + 0.2;
                    this.level.addParticle(ParticleTypes.SMOKE, $$68, $$69, $$70, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1503: {
                this.level.playLocalSound($$1, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                for (int $$71 = 0; $$71 < 16; ++$$71) {
                    double $$72 = (double)$$1.getX() + (5.0 + $$3.nextDouble() * 6.0) / 16.0;
                    double $$73 = (double)$$1.getY() + 0.8125;
                    double $$74 = (double)$$1.getZ() + (5.0 + $$3.nextDouble() * 6.0) / 16.0;
                    this.level.addParticle(ParticleTypes.SMOKE, $$72, $$73, $$74, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2006: {
                for (int $$75 = 0; $$75 < 200; ++$$75) {
                    float $$76 = $$3.nextFloat() * 4.0f;
                    float $$77 = $$3.nextFloat() * ((float)Math.PI * 2);
                    double $$78 = Mth.cos($$77) * $$76;
                    double $$79 = 0.01 + $$3.nextDouble() * 0.5;
                    double $$80 = Mth.sin($$77) * $$76;
                    Particle $$81 = this.addParticleInternal(ParticleTypes.DRAGON_BREATH, false, (double)$$1.getX() + $$78 * 0.1, (double)$$1.getY() + 0.3, (double)$$1.getZ() + $$80 * 0.1, $$78, $$79, $$80);
                    if ($$81 == null) continue;
                    $$81.setPower($$76);
                }
                if ($$2 != 1) break;
                this.level.playLocalSound($$1, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.0f, $$3.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2009: {
                for (int $$82 = 0; $$82 < 8; ++$$82) {
                    this.level.addParticle(ParticleTypes.CLOUD, (double)$$1.getX() + $$3.nextDouble(), (double)$$1.getY() + 1.2, (double)$$1.getZ() + $$3.nextDouble(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1009: {
                if ($$2 == 0) {
                    this.level.playLocalSound($$1, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + ($$3.nextFloat() - $$3.nextFloat()) * 0.8f, false);
                    break;
                }
                if ($$2 != 1) break;
                this.level.playLocalSound($$1, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.7f, 1.6f + ($$3.nextFloat() - $$3.nextFloat()) * 0.4f, false);
                break;
            }
            case 1029: {
                this.level.playLocalSound($$1, SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1.0f, $$3.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.level.playLocalSound($$1, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, $$3.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1044: {
                this.level.playLocalSound($$1, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.level.playLocalSound($$1, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1039: {
                this.level.playLocalSound($$1, SoundEvents.PHANTOM_BITE, SoundSource.HOSTILE, 0.3f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1010: {
                if (Item.byId($$2) instanceof RecordItem) {
                    this.playStreamingMusic(((RecordItem)Item.byId($$2)).getSound(), $$1);
                    break;
                }
                this.playStreamingMusic(null, $$1);
                break;
            }
            case 1015: {
                this.level.playLocalSound($$1, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.level.playLocalSound($$1, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 10.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.level.playLocalSound($$1, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.level.playLocalSound($$1, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.level.playLocalSound($$1, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.level.playLocalSound($$1, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.level.playLocalSound($$1, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.level.playLocalSound($$1, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.level.playLocalSound($$1, SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.level.playLocalSound($$1, SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.level.playLocalSound($$1, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1040: {
                this.level.playLocalSound($$1, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1041: {
                this.level.playLocalSound($$1, SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.level.playLocalSound($$1, SoundEvents.BAT_TAKEOFF, SoundSource.NEUTRAL, 0.05f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1042: {
                this.level.playLocalSound($$1, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1043: {
                this.level.playLocalSound($$1, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 3000: {
                this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5, 0.0, 0.0, 0.0);
                this.level.playLocalSound($$1, SoundEvents.END_GATEWAY_SPAWN, SoundSource.BLOCKS, 10.0f, (1.0f + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.level.playLocalSound($$1, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 64.0f, 0.8f + this.level.random.nextFloat() * 0.3f, false);
                break;
            }
            case 1045: {
                this.level.playLocalSound($$1, SoundEvents.POINTED_DRIPSTONE_LAND, SoundSource.BLOCKS, 2.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1046: {
                this.level.playLocalSound($$1, SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON, SoundSource.BLOCKS, 2.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1047: {
                this.level.playLocalSound($$1, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundSource.BLOCKS, 2.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1048: {
                this.level.playLocalSound($$1, SoundEvents.SKELETON_CONVERTED_TO_STRAY, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
            }
        }
    }

    public void destroyBlockProgress(int $$02, BlockPos $$1, int $$2) {
        if ($$2 < 0 || $$2 >= 10) {
            BlockDestructionProgress $$3 = (BlockDestructionProgress)this.destroyingBlocks.remove($$02);
            if ($$3 != null) {
                this.removeProgress($$3);
            }
        } else {
            BlockDestructionProgress $$4 = (BlockDestructionProgress)this.destroyingBlocks.get($$02);
            if ($$4 != null) {
                this.removeProgress($$4);
            }
            if ($$4 == null || $$4.getPos().getX() != $$1.getX() || $$4.getPos().getY() != $$1.getY() || $$4.getPos().getZ() != $$1.getZ()) {
                $$4 = new BlockDestructionProgress($$02, $$1);
                this.destroyingBlocks.put($$02, (Object)$$4);
            }
            $$4.setProgress($$2);
            $$4.updateTick(this.ticks);
            ((SortedSet)this.destructionProgress.computeIfAbsent($$4.getPos().asLong(), $$0 -> Sets.newTreeSet())).add((Object)$$4);
        }
    }

    public boolean hasRenderedAllChunks() {
        return this.chunkRenderDispatcher.isQueueEmpty();
    }

    public void needsUpdate() {
        this.needsFullRenderChunkUpdate = true;
        this.generateClouds = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateGlobalBlockEntities(Collection<BlockEntity> $$0, Collection<BlockEntity> $$1) {
        Set<BlockEntity> set = this.globalBlockEntities;
        synchronized (set) {
            this.globalBlockEntities.removeAll($$0);
            this.globalBlockEntities.addAll($$1);
        }
    }

    public static int getLightColor(BlockAndTintGetter $$0, BlockPos $$1) {
        return LevelRenderer.getLightColor($$0, $$0.getBlockState($$1), $$1);
    }

    public static int getLightColor(BlockAndTintGetter $$0, BlockState $$1, BlockPos $$2) {
        int $$5;
        if ($$1.emissiveRendering($$0, $$2)) {
            return 0xF000F0;
        }
        int $$3 = $$0.getBrightness(LightLayer.SKY, $$2);
        int $$4 = $$0.getBrightness(LightLayer.BLOCK, $$2);
        if ($$4 < ($$5 = $$1.getLightEmission())) {
            $$4 = $$5;
        }
        return $$3 << 20 | $$4 << 4;
    }

    public boolean isChunkCompiled(BlockPos $$0) {
        ChunkRenderDispatcher.RenderChunk $$1 = this.viewArea.getRenderChunkAt($$0);
        return $$1 != null && $$1.compiled.get() != ChunkRenderDispatcher.CompiledChunk.UNCOMPILED;
    }

    @Nullable
    public RenderTarget entityTarget() {
        return this.entityTarget;
    }

    @Nullable
    public RenderTarget getTranslucentTarget() {
        return this.translucentTarget;
    }

    @Nullable
    public RenderTarget getItemEntityTarget() {
        return this.itemEntityTarget;
    }

    @Nullable
    public RenderTarget getParticlesTarget() {
        return this.particlesTarget;
    }

    @Nullable
    public RenderTarget getWeatherTarget() {
        return this.weatherTarget;
    }

    @Nullable
    public RenderTarget getCloudsTarget() {
        return this.cloudsTarget;
    }

    public static class TransparencyShaderException
    extends RuntimeException {
        public TransparencyShaderException(String $$0, Throwable $$1) {
            super($$0, $$1);
        }
    }

    static class RenderChunkStorage {
        public final RenderInfoMap renderInfoMap;
        public final LinkedHashSet<RenderChunkInfo> renderChunks;

        public RenderChunkStorage(int $$0) {
            this.renderInfoMap = new RenderInfoMap($$0);
            this.renderChunks = new LinkedHashSet($$0);
        }
    }

    static class RenderChunkInfo {
        final ChunkRenderDispatcher.RenderChunk chunk;
        private byte sourceDirections;
        byte directions;
        final int step;

        RenderChunkInfo(ChunkRenderDispatcher.RenderChunk $$0, @Nullable Direction $$1, int $$2) {
            this.chunk = $$0;
            if ($$1 != null) {
                this.addSourceDirection($$1);
            }
            this.step = $$2;
        }

        public void setDirections(byte $$0, Direction $$1) {
            this.directions = (byte)(this.directions | ($$0 | 1 << $$1.ordinal()));
        }

        public boolean hasDirection(Direction $$0) {
            return (this.directions & 1 << $$0.ordinal()) > 0;
        }

        public void addSourceDirection(Direction $$0) {
            this.sourceDirections = (byte)(this.sourceDirections | (this.sourceDirections | 1 << $$0.ordinal()));
        }

        public boolean hasSourceDirection(int $$0) {
            return (this.sourceDirections & 1 << $$0) > 0;
        }

        public boolean hasSourceDirections() {
            return this.sourceDirections != 0;
        }

        public int hashCode() {
            return this.chunk.getOrigin().hashCode();
        }

        public boolean equals(Object $$0) {
            if (!($$0 instanceof RenderChunkInfo)) {
                return false;
            }
            RenderChunkInfo $$1 = (RenderChunkInfo)$$0;
            return this.chunk.getOrigin().equals($$1.chunk.getOrigin());
        }
    }

    static class RenderInfoMap {
        private final RenderChunkInfo[] infos;

        RenderInfoMap(int $$0) {
            this.infos = new RenderChunkInfo[$$0];
        }

        public void put(ChunkRenderDispatcher.RenderChunk $$0, RenderChunkInfo $$1) {
            this.infos[$$0.index] = $$1;
        }

        @Nullable
        public RenderChunkInfo get(ChunkRenderDispatcher.RenderChunk $$0) {
            int $$1 = $$0.index;
            if ($$1 < 0 || $$1 >= this.infos.length) {
                return null;
            }
            return this.infos[$$1];
        }
    }
}
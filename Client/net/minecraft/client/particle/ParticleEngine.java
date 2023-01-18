/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.EvictingQueue
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.Reader
 *  java.lang.CharSequence
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.lang.Void
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Queue
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.particle;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.AshParticle;
import net.minecraft.client.particle.AttackSweepParticle;
import net.minecraft.client.particle.BlockMarker;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.client.particle.BubbleParticle;
import net.minecraft.client.particle.BubblePopParticle;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.CritParticle;
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.DustColorTransitionParticle;
import net.minecraft.client.particle.DustParticle;
import net.minecraft.client.particle.EnchantmentTableParticle;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.ExplodeParticle;
import net.minecraft.client.particle.FallingDustParticle;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.client.particle.HugeExplosionSeedParticle;
import net.minecraft.client.particle.LargeSmokeParticle;
import net.minecraft.client.particle.LavaParticle;
import net.minecraft.client.particle.MobAppearanceParticle;
import net.minecraft.client.particle.NoteParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.PlayerCloudParticle;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.ReversePortalParticle;
import net.minecraft.client.particle.SculkChargeParticle;
import net.minecraft.client.particle.SculkChargePopParticle;
import net.minecraft.client.particle.ShriekParticle;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.particle.SnowflakeParticle;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.particle.SoulParticle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.SpitParticle;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SquidInkParticle;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.client.particle.VibrationSignalParticle;
import net.minecraft.client.particle.WakeParticle;
import net.minecraft.client.particle.WaterCurrentDownParticle;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.client.particle.WhiteAshParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class ParticleEngine
implements PreparableReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter PARTICLE_LISTER = FileToIdConverter.json("particles");
    private static final ResourceLocation PARTICLES_ATLAS_INFO = new ResourceLocation("particles");
    private static final int MAX_PARTICLES_PER_LAYER = 16384;
    private static final List<ParticleRenderType> RENDER_ORDER = ImmutableList.of((Object)ParticleRenderType.TERRAIN_SHEET, (Object)ParticleRenderType.PARTICLE_SHEET_OPAQUE, (Object)ParticleRenderType.PARTICLE_SHEET_LIT, (Object)ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT, (Object)ParticleRenderType.CUSTOM);
    protected ClientLevel level;
    private final Map<ParticleRenderType, Queue<Particle>> particles = Maps.newIdentityHashMap();
    private final Queue<TrackingEmitter> trackingEmitters = Queues.newArrayDeque();
    private final TextureManager textureManager;
    private final RandomSource random = RandomSource.create();
    private final Int2ObjectMap<ParticleProvider<?>> providers = new Int2ObjectOpenHashMap();
    private final Queue<Particle> particlesToAdd = Queues.newArrayDeque();
    private final Map<ResourceLocation, MutableSpriteSet> spriteSets = Maps.newHashMap();
    private final TextureAtlas textureAtlas;
    private final Object2IntOpenHashMap<ParticleGroup> trackedParticleCounts = new Object2IntOpenHashMap();

    public ParticleEngine(ClientLevel $$0, TextureManager $$1) {
        this.textureAtlas = new TextureAtlas(TextureAtlas.LOCATION_PARTICLES);
        $$1.register(this.textureAtlas.location(), this.textureAtlas);
        this.level = $$0;
        this.textureManager = $$1;
        this.registerProviders();
    }

    private void registerProviders() {
        this.register(ParticleTypes.AMBIENT_ENTITY_EFFECT, SpellParticle.AmbientMobProvider::new);
        this.register(ParticleTypes.ANGRY_VILLAGER, HeartParticle.AngryVillagerProvider::new);
        this.register(ParticleTypes.BLOCK_MARKER, new BlockMarker.Provider());
        this.register(ParticleTypes.BLOCK, new TerrainParticle.Provider());
        this.register(ParticleTypes.BUBBLE, BubbleParticle.Provider::new);
        this.register(ParticleTypes.BUBBLE_COLUMN_UP, BubbleColumnUpParticle.Provider::new);
        this.register(ParticleTypes.BUBBLE_POP, BubblePopParticle.Provider::new);
        this.register(ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireSmokeParticle.CosyProvider::new);
        this.register(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireSmokeParticle.SignalProvider::new);
        this.register(ParticleTypes.CLOUD, PlayerCloudParticle.Provider::new);
        this.register(ParticleTypes.COMPOSTER, SuspendedTownParticle.ComposterFillProvider::new);
        this.register(ParticleTypes.CRIT, CritParticle.Provider::new);
        this.register(ParticleTypes.CURRENT_DOWN, WaterCurrentDownParticle.Provider::new);
        this.register(ParticleTypes.DAMAGE_INDICATOR, CritParticle.DamageIndicatorProvider::new);
        this.register(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Provider::new);
        this.register(ParticleTypes.DOLPHIN, SuspendedTownParticle.DolphinSpeedProvider::new);
        this.register(ParticleTypes.DRIPPING_LAVA, DripParticle.LavaHangProvider::new);
        this.register(ParticleTypes.FALLING_LAVA, DripParticle.LavaFallProvider::new);
        this.register(ParticleTypes.LANDING_LAVA, DripParticle.LavaLandProvider::new);
        this.register(ParticleTypes.DRIPPING_WATER, DripParticle.WaterHangProvider::new);
        this.register(ParticleTypes.FALLING_WATER, DripParticle.WaterFallProvider::new);
        this.register(ParticleTypes.DUST, DustParticle.Provider::new);
        this.register(ParticleTypes.DUST_COLOR_TRANSITION, DustColorTransitionParticle.Provider::new);
        this.register(ParticleTypes.EFFECT, SpellParticle.Provider::new);
        this.register(ParticleTypes.ELDER_GUARDIAN, new MobAppearanceParticle.Provider());
        this.register(ParticleTypes.ENCHANTED_HIT, CritParticle.MagicProvider::new);
        this.register(ParticleTypes.ENCHANT, EnchantmentTableParticle.Provider::new);
        this.register(ParticleTypes.END_ROD, EndRodParticle.Provider::new);
        this.register(ParticleTypes.ENTITY_EFFECT, SpellParticle.MobProvider::new);
        this.register(ParticleTypes.EXPLOSION_EMITTER, new HugeExplosionSeedParticle.Provider());
        this.register(ParticleTypes.EXPLOSION, HugeExplosionParticle.Provider::new);
        this.register(ParticleTypes.SONIC_BOOM, SonicBoomParticle.Provider::new);
        this.register(ParticleTypes.FALLING_DUST, FallingDustParticle.Provider::new);
        this.register(ParticleTypes.FIREWORK, FireworkParticles.SparkProvider::new);
        this.register(ParticleTypes.FISHING, WakeParticle.Provider::new);
        this.register(ParticleTypes.FLAME, FlameParticle.Provider::new);
        this.register(ParticleTypes.SCULK_SOUL, SoulParticle.EmissiveProvider::new);
        this.register(ParticleTypes.SCULK_CHARGE, SculkChargeParticle.Provider::new);
        this.register(ParticleTypes.SCULK_CHARGE_POP, SculkChargePopParticle.Provider::new);
        this.register(ParticleTypes.SOUL, SoulParticle.Provider::new);
        this.register(ParticleTypes.SOUL_FIRE_FLAME, FlameParticle.Provider::new);
        this.register(ParticleTypes.FLASH, FireworkParticles.FlashProvider::new);
        this.register(ParticleTypes.HAPPY_VILLAGER, SuspendedTownParticle.HappyVillagerProvider::new);
        this.register(ParticleTypes.HEART, HeartParticle.Provider::new);
        this.register(ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantProvider::new);
        this.register(ParticleTypes.ITEM, new BreakingItemParticle.Provider());
        this.register(ParticleTypes.ITEM_SLIME, new BreakingItemParticle.SlimeProvider());
        this.register(ParticleTypes.ITEM_SNOWBALL, new BreakingItemParticle.SnowballProvider());
        this.register(ParticleTypes.LARGE_SMOKE, LargeSmokeParticle.Provider::new);
        this.register(ParticleTypes.LAVA, LavaParticle.Provider::new);
        this.register(ParticleTypes.MYCELIUM, SuspendedTownParticle.Provider::new);
        this.register(ParticleTypes.NAUTILUS, EnchantmentTableParticle.NautilusProvider::new);
        this.register(ParticleTypes.NOTE, NoteParticle.Provider::new);
        this.register(ParticleTypes.POOF, ExplodeParticle.Provider::new);
        this.register(ParticleTypes.PORTAL, PortalParticle.Provider::new);
        this.register(ParticleTypes.RAIN, WaterDropParticle.Provider::new);
        this.register(ParticleTypes.SMOKE, SmokeParticle.Provider::new);
        this.register(ParticleTypes.SNEEZE, PlayerCloudParticle.SneezeProvider::new);
        this.register(ParticleTypes.SNOWFLAKE, SnowflakeParticle.Provider::new);
        this.register(ParticleTypes.SPIT, SpitParticle.Provider::new);
        this.register(ParticleTypes.SWEEP_ATTACK, AttackSweepParticle.Provider::new);
        this.register(ParticleTypes.TOTEM_OF_UNDYING, TotemParticle.Provider::new);
        this.register(ParticleTypes.SQUID_INK, SquidInkParticle.Provider::new);
        this.register(ParticleTypes.UNDERWATER, SuspendedParticle.UnderwaterProvider::new);
        this.register(ParticleTypes.SPLASH, SplashParticle.Provider::new);
        this.register(ParticleTypes.WITCH, SpellParticle.WitchProvider::new);
        this.register(ParticleTypes.DRIPPING_HONEY, DripParticle.HoneyHangProvider::new);
        this.register(ParticleTypes.FALLING_HONEY, DripParticle.HoneyFallProvider::new);
        this.register(ParticleTypes.LANDING_HONEY, DripParticle.HoneyLandProvider::new);
        this.register(ParticleTypes.FALLING_NECTAR, DripParticle.NectarFallProvider::new);
        this.register(ParticleTypes.FALLING_SPORE_BLOSSOM, DripParticle.SporeBlossomFallProvider::new);
        this.register(ParticleTypes.SPORE_BLOSSOM_AIR, SuspendedParticle.SporeBlossomAirProvider::new);
        this.register(ParticleTypes.ASH, AshParticle.Provider::new);
        this.register(ParticleTypes.CRIMSON_SPORE, SuspendedParticle.CrimsonSporeProvider::new);
        this.register(ParticleTypes.WARPED_SPORE, SuspendedParticle.WarpedSporeProvider::new);
        this.register(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, DripParticle.ObsidianTearHangProvider::new);
        this.register(ParticleTypes.FALLING_OBSIDIAN_TEAR, DripParticle.ObsidianTearFallProvider::new);
        this.register(ParticleTypes.LANDING_OBSIDIAN_TEAR, DripParticle.ObsidianTearLandProvider::new);
        this.register(ParticleTypes.REVERSE_PORTAL, ReversePortalParticle.ReversePortalProvider::new);
        this.register(ParticleTypes.WHITE_ASH, WhiteAshParticle.Provider::new);
        this.register(ParticleTypes.SMALL_FLAME, FlameParticle.SmallFlameProvider::new);
        this.register(ParticleTypes.DRIPPING_DRIPSTONE_WATER, DripParticle.DripstoneWaterHangProvider::new);
        this.register(ParticleTypes.FALLING_DRIPSTONE_WATER, DripParticle.DripstoneWaterFallProvider::new);
        this.register(ParticleTypes.DRIPPING_DRIPSTONE_LAVA, DripParticle.DripstoneLavaHangProvider::new);
        this.register(ParticleTypes.FALLING_DRIPSTONE_LAVA, DripParticle.DripstoneLavaFallProvider::new);
        this.register(ParticleTypes.VIBRATION, VibrationSignalParticle.Provider::new);
        this.register(ParticleTypes.GLOW_SQUID_INK, SquidInkParticle.GlowInkProvider::new);
        this.register(ParticleTypes.GLOW, GlowParticle.GlowSquidProvider::new);
        this.register(ParticleTypes.WAX_ON, GlowParticle.WaxOnProvider::new);
        this.register(ParticleTypes.WAX_OFF, GlowParticle.WaxOffProvider::new);
        this.register(ParticleTypes.ELECTRIC_SPARK, GlowParticle.ElectricSparkProvider::new);
        this.register(ParticleTypes.SCRAPE, GlowParticle.ScrapeProvider::new);
        this.register(ParticleTypes.SHRIEK, ShriekParticle.Provider::new);
    }

    private <T extends ParticleOptions> void register(ParticleType<T> $$0, ParticleProvider<T> $$1) {
        this.providers.put(BuiltInRegistries.PARTICLE_TYPE.getId($$0), $$1);
    }

    private <T extends ParticleOptions> void register(ParticleType<T> $$0, SpriteParticleRegistration<T> $$1) {
        MutableSpriteSet $$2 = new MutableSpriteSet();
        this.spriteSets.put((Object)BuiltInRegistries.PARTICLE_TYPE.getKey($$0), (Object)$$2);
        this.providers.put(BuiltInRegistries.PARTICLE_TYPE.getId($$0), $$1.create($$2));
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier $$0, ResourceManager $$12, ProfilerFiller $$2, ProfilerFiller $$32, Executor $$4, Executor $$5) {
        CompletableFuture $$6 = CompletableFuture.supplyAsync(() -> PARTICLE_LISTER.listMatchingResources($$12), (Executor)$$4).thenCompose($$1 -> {
            ArrayList $$2 = new ArrayList($$1.size());
            $$1.forEach((arg_0, arg_1) -> this.lambda$reload$2((List)$$2, $$4, arg_0, arg_1));
            return Util.sequence($$2);
        });
        CompletableFuture $$7 = SpriteLoader.create(this.textureAtlas).loadAndStitch($$12, PARTICLES_ATLAS_INFO, 0, $$4).thenCompose(SpriteLoader.Preparations::waitForUpload);
        return CompletableFuture.allOf((CompletableFuture[])new CompletableFuture[]{$$7, $$6}).thenCompose($$0::wait).thenAcceptAsync($$3 -> {
            this.clearParticles();
            $$32.startTick();
            $$32.push("upload");
            SpriteLoader.Preparations $$4 = (SpriteLoader.Preparations)((Object)((Object)$$7.join()));
            this.textureAtlas.upload($$4);
            $$32.popPush("bindSpriteSets");
            HashSet $$5 = new HashSet();
            TextureAtlasSprite $$6 = $$4.missing();
            ((List)$$6.join()).forEach(arg_0 -> this.lambda$reload$4($$4, (Set)$$5, $$6, arg_0));
            if (!$$5.isEmpty()) {
                LOGGER.warn("Missing particle sprites: {}", $$5.stream().sorted().map(ResourceLocation::toString).collect(Collectors.joining((CharSequence)",")));
            }
            $$32.pop();
            $$32.endTick();
        }, $$5);
    }

    public void close() {
        this.textureAtlas.clearTextureData();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Optional<List<ResourceLocation>> loadParticleDescription(ResourceLocation $$0, Resource $$1) {
        try (BufferedReader $$2 = $$1.openAsReader();){
            ParticleDescription $$3 = ParticleDescription.fromJson(GsonHelper.parse((Reader)$$2));
            List<ResourceLocation> $$4 = $$3.getTextures();
            boolean $$5 = this.spriteSets.containsKey((Object)$$0);
            if ($$4 == null) {
                if ($$5) {
                    throw new IllegalStateException("Missing texture list for particle " + $$0);
                }
                Optional optional2 = Optional.empty();
                return optional2;
            }
            if (!$$5) {
                throw new IllegalStateException("Redundant texture list for particle " + $$0);
            }
            Optional optional = Optional.of($$4);
            return optional;
        }
        catch (IOException $$6) {
            throw new IllegalStateException("Failed to load description for particle " + $$0, (Throwable)$$6);
        }
    }

    public void createTrackingEmitter(Entity $$0, ParticleOptions $$1) {
        this.trackingEmitters.add((Object)new TrackingEmitter(this.level, $$0, $$1));
    }

    public void createTrackingEmitter(Entity $$0, ParticleOptions $$1, int $$2) {
        this.trackingEmitters.add((Object)new TrackingEmitter(this.level, $$0, $$1, $$2));
    }

    @Nullable
    public Particle createParticle(ParticleOptions $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        Particle $$7 = this.makeParticle($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        if ($$7 != null) {
            this.add($$7);
            return $$7;
        }
        return null;
    }

    @Nullable
    private <T extends ParticleOptions> Particle makeParticle(T $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        ParticleProvider $$7 = (ParticleProvider)this.providers.get(BuiltInRegistries.PARTICLE_TYPE.getId($$0.getType()));
        if ($$7 == null) {
            return null;
        }
        return $$7.createParticle($$0, this.level, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    public void add(Particle $$0) {
        Optional<ParticleGroup> $$1 = $$0.getParticleGroup();
        if ($$1.isPresent()) {
            if (this.hasSpaceInParticleLimit((ParticleGroup)$$1.get())) {
                this.particlesToAdd.add((Object)$$0);
                this.updateCount((ParticleGroup)$$1.get(), 1);
            }
        } else {
            this.particlesToAdd.add((Object)$$0);
        }
    }

    public void tick() {
        this.particles.forEach(($$0, $$1) -> {
            this.level.getProfiler().push($$0.toString());
            this.tickParticleList((Collection<Particle>)$$1);
            this.level.getProfiler().pop();
        });
        if (!this.trackingEmitters.isEmpty()) {
            ArrayList $$02 = Lists.newArrayList();
            for (TrackingEmitter $$12 : this.trackingEmitters) {
                $$12.tick();
                if ($$12.isAlive()) continue;
                $$02.add((Object)$$12);
            }
            this.trackingEmitters.removeAll((Collection)$$02);
        }
        if (!this.particlesToAdd.isEmpty()) {
            Particle $$2;
            while (($$2 = (Particle)this.particlesToAdd.poll()) != null) {
                ((Queue)this.particles.computeIfAbsent((Object)$$2.getRenderType(), $$0 -> EvictingQueue.create((int)16384))).add((Object)$$2);
            }
        }
    }

    private void tickParticleList(Collection<Particle> $$02) {
        if (!$$02.isEmpty()) {
            Iterator $$1 = $$02.iterator();
            while ($$1.hasNext()) {
                Particle $$2 = (Particle)$$1.next();
                this.tickParticle($$2);
                if ($$2.isAlive()) continue;
                $$2.getParticleGroup().ifPresent($$0 -> this.updateCount((ParticleGroup)$$0, -1));
                $$1.remove();
            }
        }
    }

    private void updateCount(ParticleGroup $$0, int $$1) {
        this.trackedParticleCounts.addTo((Object)$$0, $$1);
    }

    private void tickParticle(Particle $$0) {
        try {
            $$0.tick();
        }
        catch (Throwable $$1) {
            CrashReport $$2 = CrashReport.forThrowable($$1, "Ticking Particle");
            CrashReportCategory $$3 = $$2.addCategory("Particle being ticked");
            $$3.setDetail("Particle", $$0::toString);
            $$3.setDetail("Particle Type", () -> ((ParticleRenderType)$$0.getRenderType()).toString());
            throw new ReportedException($$2);
        }
    }

    public void render(PoseStack $$0, MultiBufferSource.BufferSource $$1, LightTexture $$2, Camera $$3, float $$4) {
        $$2.turnOnLightLayer();
        RenderSystem.enableDepthTest();
        PoseStack $$5 = RenderSystem.getModelViewStack();
        $$5.pushPose();
        $$5.mulPoseMatrix($$0.last().pose());
        RenderSystem.applyModelViewMatrix();
        for (ParticleRenderType $$6 : RENDER_ORDER) {
            Iterable $$7 = (Iterable)this.particles.get((Object)$$6);
            if ($$7 == null) continue;
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getParticleShader));
            Tesselator $$8 = Tesselator.getInstance();
            BufferBuilder $$9 = $$8.getBuilder();
            $$6.begin($$9, this.textureManager);
            for (Particle $$10 : $$7) {
                try {
                    $$10.render($$9, $$3, $$4);
                }
                catch (Throwable $$11) {
                    CrashReport $$12 = CrashReport.forThrowable($$11, "Rendering Particle");
                    CrashReportCategory $$13 = $$12.addCategory("Particle being rendered");
                    $$13.setDetail("Particle", $$10::toString);
                    $$13.setDetail("Particle Type", () -> ((ParticleRenderType)$$6).toString());
                    throw new ReportedException($$12);
                }
            }
            $$6.end($$8);
        }
        $$5.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        $$2.turnOffLightLayer();
    }

    public void setLevel(@Nullable ClientLevel $$0) {
        this.level = $$0;
        this.clearParticles();
        this.trackingEmitters.clear();
    }

    public void destroy(BlockPos $$0, BlockState $$1) {
        if ($$1.isAir() || !$$1.shouldSpawnParticlesOnBreak()) {
            return;
        }
        VoxelShape $$22 = $$1.getShape(this.level, $$0);
        double $$32 = 0.25;
        $$22.forAllBoxes(($$2, $$3, $$4, $$5, $$6, $$7) -> {
            double $$8 = Math.min((double)1.0, (double)($$5 - $$2));
            double $$9 = Math.min((double)1.0, (double)($$6 - $$3));
            double $$10 = Math.min((double)1.0, (double)($$7 - $$4));
            int $$11 = Math.max((int)2, (int)Mth.ceil($$8 / 0.25));
            int $$12 = Math.max((int)2, (int)Mth.ceil($$9 / 0.25));
            int $$13 = Math.max((int)2, (int)Mth.ceil($$10 / 0.25));
            for (int $$14 = 0; $$14 < $$11; ++$$14) {
                for (int $$15 = 0; $$15 < $$12; ++$$15) {
                    for (int $$16 = 0; $$16 < $$13; ++$$16) {
                        double $$17 = ((double)$$14 + 0.5) / (double)$$11;
                        double $$18 = ((double)$$15 + 0.5) / (double)$$12;
                        double $$19 = ((double)$$16 + 0.5) / (double)$$13;
                        double $$20 = $$17 * $$8 + $$2;
                        double $$21 = $$18 * $$9 + $$3;
                        double $$22 = $$19 * $$10 + $$4;
                        this.add(new TerrainParticle(this.level, (double)$$0.getX() + $$20, (double)$$0.getY() + $$21, (double)$$0.getZ() + $$22, $$17 - 0.5, $$18 - 0.5, $$19 - 0.5, $$1, $$0));
                    }
                }
            }
        });
    }

    public void crack(BlockPos $$0, Direction $$1) {
        BlockState $$2 = this.level.getBlockState($$0);
        if ($$2.getRenderShape() == RenderShape.INVISIBLE) {
            return;
        }
        int $$3 = $$0.getX();
        int $$4 = $$0.getY();
        int $$5 = $$0.getZ();
        float $$6 = 0.1f;
        AABB $$7 = $$2.getShape(this.level, $$0).bounds();
        double $$8 = (double)$$3 + this.random.nextDouble() * ($$7.maxX - $$7.minX - (double)0.2f) + (double)0.1f + $$7.minX;
        double $$9 = (double)$$4 + this.random.nextDouble() * ($$7.maxY - $$7.minY - (double)0.2f) + (double)0.1f + $$7.minY;
        double $$10 = (double)$$5 + this.random.nextDouble() * ($$7.maxZ - $$7.minZ - (double)0.2f) + (double)0.1f + $$7.minZ;
        if ($$1 == Direction.DOWN) {
            $$9 = (double)$$4 + $$7.minY - (double)0.1f;
        }
        if ($$1 == Direction.UP) {
            $$9 = (double)$$4 + $$7.maxY + (double)0.1f;
        }
        if ($$1 == Direction.NORTH) {
            $$10 = (double)$$5 + $$7.minZ - (double)0.1f;
        }
        if ($$1 == Direction.SOUTH) {
            $$10 = (double)$$5 + $$7.maxZ + (double)0.1f;
        }
        if ($$1 == Direction.WEST) {
            $$8 = (double)$$3 + $$7.minX - (double)0.1f;
        }
        if ($$1 == Direction.EAST) {
            $$8 = (double)$$3 + $$7.maxX + (double)0.1f;
        }
        this.add(new TerrainParticle(this.level, $$8, $$9, $$10, 0.0, 0.0, 0.0, $$2, $$0).setPower(0.2f).scale(0.6f));
    }

    public String countParticles() {
        return String.valueOf((int)this.particles.values().stream().mapToInt(Collection::size).sum());
    }

    private boolean hasSpaceInParticleLimit(ParticleGroup $$0) {
        return this.trackedParticleCounts.getInt((Object)$$0) < $$0.getLimit();
    }

    private void clearParticles() {
        this.particles.clear();
        this.trackedParticleCounts.clear();
    }

    private /* synthetic */ void lambda$reload$4(SpriteLoader.Preparations $$0, Set $$1, TextureAtlasSprite $$2, 1ParticleDefinition $$3) {
        record ParticleDefinition(ResourceLocation id, Optional<List<ResourceLocation>> sprites) {
        }
        Optional<List<ResourceLocation>> $$4 = $$3.sprites();
        if ($$4.isEmpty()) {
            return;
        }
        ArrayList $$5 = new ArrayList();
        for (ResourceLocation $$6 : (List)$$4.get()) {
            TextureAtlasSprite $$7 = (TextureAtlasSprite)$$0.regions().get((Object)$$6);
            if ($$7 == null) {
                $$1.add((Object)$$6);
                $$5.add((Object)$$2);
                continue;
            }
            $$5.add((Object)$$7);
        }
        if ($$5.isEmpty()) {
            $$5.add((Object)$$2);
        }
        ((MutableSpriteSet)this.spriteSets.get((Object)$$3.id())).rebind((List<TextureAtlasSprite>)$$5);
    }

    private /* synthetic */ void lambda$reload$2(List $$0, Executor $$1, ResourceLocation $$2, Resource $$3) {
        ResourceLocation $$4 = PARTICLE_LISTER.fileToId($$2);
        $$0.add((Object)CompletableFuture.supplyAsync(() -> {
            record ParticleDefinition(ResourceLocation id, Optional<List<ResourceLocation>> sprites) {
            }
            return new ParticleDefinition($$4, this.loadParticleDescription($$4, $$3));
        }, (Executor)$$1));
    }

    @FunctionalInterface
    static interface SpriteParticleRegistration<T extends ParticleOptions> {
        public ParticleProvider<T> create(SpriteSet var1);
    }

    static class MutableSpriteSet
    implements SpriteSet {
        private List<TextureAtlasSprite> sprites;

        MutableSpriteSet() {
        }

        @Override
        public TextureAtlasSprite get(int $$0, int $$1) {
            return (TextureAtlasSprite)this.sprites.get($$0 * (this.sprites.size() - 1) / $$1);
        }

        @Override
        public TextureAtlasSprite get(RandomSource $$0) {
            return (TextureAtlasSprite)this.sprites.get($$0.nextInt(this.sprites.size()));
        }

        public void rebind(List<TextureAtlasSprite> $$0) {
            this.sprites = ImmutableList.copyOf($$0);
        }
    }
}
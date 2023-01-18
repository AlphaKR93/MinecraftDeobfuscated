/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  java.io.ByteArrayInputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.AutoCloseable
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.Path
 *  java.util.ArrayList
 *  java.util.HashMap
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class GameRenderer
implements AutoCloseable {
    private static final ResourceLocation NAUSEA_LOCATION = new ResourceLocation("textures/misc/nausea.png");
    static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean DEPTH_BUFFER_DEBUG = false;
    public static final float PROJECTION_Z_NEAR = 0.05f;
    final Minecraft minecraft;
    private final ResourceManager resourceManager;
    private final RandomSource random = RandomSource.create();
    private float renderDistance;
    public final ItemInHandRenderer itemInHandRenderer;
    private final MapRenderer mapRenderer;
    private final RenderBuffers renderBuffers;
    private int tick;
    private float fov;
    private float oldFov;
    private float darkenWorldAmount;
    private float darkenWorldAmountO;
    private boolean renderHand = true;
    private boolean renderBlockOutline = true;
    private long lastScreenshotAttempt;
    private boolean hasWorldScreenshot;
    private long lastActiveTime = Util.getMillis();
    private final LightTexture lightTexture;
    private final OverlayTexture overlayTexture = new OverlayTexture();
    private boolean panoramicMode;
    private float zoom = 1.0f;
    private float zoomX;
    private float zoomY;
    public static final int ITEM_ACTIVATION_ANIMATION_LENGTH = 40;
    @Nullable
    private ItemStack itemActivationItem;
    private int itemActivationTicks;
    private float itemActivationOffX;
    private float itemActivationOffY;
    @Nullable
    PostChain postEffect;
    static final ResourceLocation[] EFFECTS = new ResourceLocation[]{new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json")};
    public static final int EFFECT_NONE = EFFECTS.length;
    int effectIndex = EFFECT_NONE;
    private boolean effectActive;
    private final Camera mainCamera = new Camera();
    public ShaderInstance blitShader;
    private final Map<String, ShaderInstance> shaders = Maps.newHashMap();
    @Nullable
    private static ShaderInstance positionShader;
    @Nullable
    private static ShaderInstance positionColorShader;
    @Nullable
    private static ShaderInstance positionColorTexShader;
    @Nullable
    private static ShaderInstance positionTexShader;
    @Nullable
    private static ShaderInstance positionTexColorShader;
    @Nullable
    private static ShaderInstance blockShader;
    @Nullable
    private static ShaderInstance newEntityShader;
    @Nullable
    private static ShaderInstance particleShader;
    @Nullable
    private static ShaderInstance positionColorLightmapShader;
    @Nullable
    private static ShaderInstance positionColorTexLightmapShader;
    @Nullable
    private static ShaderInstance positionTexColorNormalShader;
    @Nullable
    private static ShaderInstance positionTexLightmapColorShader;
    @Nullable
    private static ShaderInstance rendertypeSolidShader;
    @Nullable
    private static ShaderInstance rendertypeCutoutMippedShader;
    @Nullable
    private static ShaderInstance rendertypeCutoutShader;
    @Nullable
    private static ShaderInstance rendertypeTranslucentShader;
    @Nullable
    private static ShaderInstance rendertypeTranslucentMovingBlockShader;
    @Nullable
    private static ShaderInstance rendertypeTranslucentNoCrumblingShader;
    @Nullable
    private static ShaderInstance rendertypeArmorCutoutNoCullShader;
    @Nullable
    private static ShaderInstance rendertypeEntitySolidShader;
    @Nullable
    private static ShaderInstance rendertypeEntityCutoutShader;
    @Nullable
    private static ShaderInstance rendertypeEntityCutoutNoCullShader;
    @Nullable
    private static ShaderInstance rendertypeEntityCutoutNoCullZOffsetShader;
    @Nullable
    private static ShaderInstance rendertypeItemEntityTranslucentCullShader;
    @Nullable
    private static ShaderInstance rendertypeEntityTranslucentCullShader;
    @Nullable
    private static ShaderInstance rendertypeEntityTranslucentShader;
    @Nullable
    private static ShaderInstance rendertypeEntityTranslucentEmissiveShader;
    @Nullable
    private static ShaderInstance rendertypeEntitySmoothCutoutShader;
    @Nullable
    private static ShaderInstance rendertypeBeaconBeamShader;
    @Nullable
    private static ShaderInstance rendertypeEntityDecalShader;
    @Nullable
    private static ShaderInstance rendertypeEntityNoOutlineShader;
    @Nullable
    private static ShaderInstance rendertypeEntityShadowShader;
    @Nullable
    private static ShaderInstance rendertypeEntityAlphaShader;
    @Nullable
    private static ShaderInstance rendertypeEyesShader;
    @Nullable
    private static ShaderInstance rendertypeEnergySwirlShader;
    @Nullable
    private static ShaderInstance rendertypeLeashShader;
    @Nullable
    private static ShaderInstance rendertypeWaterMaskShader;
    @Nullable
    private static ShaderInstance rendertypeOutlineShader;
    @Nullable
    private static ShaderInstance rendertypeArmorGlintShader;
    @Nullable
    private static ShaderInstance rendertypeArmorEntityGlintShader;
    @Nullable
    private static ShaderInstance rendertypeGlintTranslucentShader;
    @Nullable
    private static ShaderInstance rendertypeGlintShader;
    @Nullable
    private static ShaderInstance rendertypeGlintDirectShader;
    @Nullable
    private static ShaderInstance rendertypeEntityGlintShader;
    @Nullable
    private static ShaderInstance rendertypeEntityGlintDirectShader;
    @Nullable
    private static ShaderInstance rendertypeTextShader;
    @Nullable
    private static ShaderInstance rendertypeTextIntensityShader;
    @Nullable
    private static ShaderInstance rendertypeTextSeeThroughShader;
    @Nullable
    private static ShaderInstance rendertypeTextIntensitySeeThroughShader;
    @Nullable
    private static ShaderInstance rendertypeLightningShader;
    @Nullable
    private static ShaderInstance rendertypeTripwireShader;
    @Nullable
    private static ShaderInstance rendertypeEndPortalShader;
    @Nullable
    private static ShaderInstance rendertypeEndGatewayShader;
    @Nullable
    private static ShaderInstance rendertypeLinesShader;
    @Nullable
    private static ShaderInstance rendertypeCrumblingShader;

    public GameRenderer(Minecraft $$0, ItemInHandRenderer $$1, ResourceManager $$2, RenderBuffers $$3) {
        this.minecraft = $$0;
        this.resourceManager = $$2;
        this.itemInHandRenderer = $$1;
        this.mapRenderer = new MapRenderer($$0.getTextureManager());
        this.lightTexture = new LightTexture(this, $$0);
        this.renderBuffers = $$3;
        this.postEffect = null;
    }

    public void close() {
        this.lightTexture.close();
        this.mapRenderer.close();
        this.overlayTexture.close();
        this.shutdownEffect();
        this.shutdownShaders();
        if (this.blitShader != null) {
            this.blitShader.close();
        }
    }

    public void setRenderHand(boolean $$0) {
        this.renderHand = $$0;
    }

    public void setRenderBlockOutline(boolean $$0) {
        this.renderBlockOutline = $$0;
    }

    public void setPanoramicMode(boolean $$0) {
        this.panoramicMode = $$0;
    }

    public boolean isPanoramicMode() {
        return this.panoramicMode;
    }

    public void shutdownEffect() {
        if (this.postEffect != null) {
            this.postEffect.close();
        }
        this.postEffect = null;
        this.effectIndex = EFFECT_NONE;
    }

    public void togglePostEffect() {
        this.effectActive = !this.effectActive;
    }

    public void checkEntityPostEffect(@Nullable Entity $$0) {
        if (this.postEffect != null) {
            this.postEffect.close();
        }
        this.postEffect = null;
        if ($$0 instanceof Creeper) {
            this.loadEffect(new ResourceLocation("shaders/post/creeper.json"));
        } else if ($$0 instanceof Spider) {
            this.loadEffect(new ResourceLocation("shaders/post/spider.json"));
        } else if ($$0 instanceof EnderMan) {
            this.loadEffect(new ResourceLocation("shaders/post/invert.json"));
        }
    }

    public void cycleEffect() {
        if (!(this.minecraft.getCameraEntity() instanceof Player)) {
            return;
        }
        if (this.postEffect != null) {
            this.postEffect.close();
        }
        this.effectIndex = (this.effectIndex + 1) % (EFFECTS.length + 1);
        if (this.effectIndex == EFFECT_NONE) {
            this.postEffect = null;
        } else {
            this.loadEffect(EFFECTS[this.effectIndex]);
        }
    }

    void loadEffect(ResourceLocation $$0) {
        if (this.postEffect != null) {
            this.postEffect.close();
        }
        try {
            this.postEffect = new PostChain(this.minecraft.getTextureManager(), this.resourceManager, this.minecraft.getMainRenderTarget(), $$0);
            this.postEffect.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
            this.effectActive = true;
        }
        catch (IOException $$1) {
            LOGGER.warn("Failed to load shader: {}", (Object)$$0, (Object)$$1);
            this.effectIndex = EFFECT_NONE;
            this.effectActive = false;
        }
        catch (JsonSyntaxException $$2) {
            LOGGER.warn("Failed to parse shader: {}", (Object)$$0, (Object)$$2);
            this.effectIndex = EFFECT_NONE;
            this.effectActive = false;
        }
    }

    public PreparableReloadListener createReloadListener() {
        return new SimplePreparableReloadListener<ResourceCache>(){

            @Override
            protected ResourceCache prepare(ResourceManager $$02, ProfilerFiller $$1) {
                Map<ResourceLocation, Resource> $$2 = $$02.listResources("shaders", (Predicate<ResourceLocation>)((Predicate)$$0 -> {
                    String $$1 = $$0.getPath();
                    return $$1.endsWith(".json") || $$1.endsWith(Program.Type.FRAGMENT.getExtension()) || $$1.endsWith(Program.Type.VERTEX.getExtension()) || $$1.endsWith(".glsl");
                }));
                HashMap $$3 = new HashMap();
                $$2.forEach((arg_0, arg_1) -> 1.lambda$prepare$2((Map)$$3, arg_0, arg_1));
                return new ResourceCache($$02, (Map<ResourceLocation, Resource>)$$3);
            }

            @Override
            protected void apply(ResourceCache $$0, ResourceManager $$1, ProfilerFiller $$2) {
                GameRenderer.this.reloadShaders($$0);
                if (GameRenderer.this.postEffect != null) {
                    GameRenderer.this.postEffect.close();
                }
                GameRenderer.this.postEffect = null;
                if (GameRenderer.this.effectIndex == EFFECT_NONE) {
                    GameRenderer.this.checkEntityPostEffect(GameRenderer.this.minecraft.getCameraEntity());
                } else {
                    GameRenderer.this.loadEffect(EFFECTS[GameRenderer.this.effectIndex]);
                }
            }

            @Override
            public String getName() {
                return "Shader Loader";
            }

            private static /* synthetic */ void lambda$prepare$2(Map $$0, ResourceLocation $$1, Resource $$2) {
                try (InputStream $$3 = $$2.open();){
                    byte[] $$4 = $$3.readAllBytes();
                    $$0.put((Object)$$1, (Object)new Resource($$2.source(), () -> new ByteArrayInputStream($$4)));
                }
                catch (Exception $$5) {
                    LOGGER.warn("Failed to read resource {}", (Object)$$1, (Object)$$5);
                }
            }
        };
    }

    public void preloadUiShader(ResourceProvider $$0) {
        if (this.blitShader != null) {
            throw new RuntimeException("Blit shader already preloaded");
        }
        try {
            this.blitShader = new ShaderInstance($$0, "blit_screen", DefaultVertexFormat.BLIT_SCREEN);
        }
        catch (IOException $$1) {
            throw new RuntimeException("could not preload blit shader", (Throwable)$$1);
        }
        positionShader = this.preloadShader($$0, "position", DefaultVertexFormat.POSITION);
        positionColorShader = this.preloadShader($$0, "position_color", DefaultVertexFormat.POSITION_COLOR);
        positionColorTexShader = this.preloadShader($$0, "position_color_tex", DefaultVertexFormat.POSITION_COLOR_TEX);
        positionTexShader = this.preloadShader($$0, "position_tex", DefaultVertexFormat.POSITION_TEX);
        positionTexColorShader = this.preloadShader($$0, "position_tex_color", DefaultVertexFormat.POSITION_TEX_COLOR);
        rendertypeTextShader = this.preloadShader($$0, "rendertype_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
    }

    private ShaderInstance preloadShader(ResourceProvider $$0, String $$1, VertexFormat $$2) {
        try {
            ShaderInstance $$3 = new ShaderInstance($$0, $$1, $$2);
            this.shaders.put((Object)$$1, (Object)$$3);
            return $$3;
        }
        catch (Exception $$4) {
            throw new IllegalStateException("could not preload shader " + $$1, (Throwable)$$4);
        }
    }

    void reloadShaders(ResourceProvider $$02) {
        RenderSystem.assertOnRenderThread();
        ArrayList $$1 = Lists.newArrayList();
        $$1.addAll(Program.Type.FRAGMENT.getPrograms().values());
        $$1.addAll(Program.Type.VERTEX.getPrograms().values());
        $$1.forEach(Program::close);
        ArrayList $$2 = Lists.newArrayListWithCapacity((int)this.shaders.size());
        try {
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "block", DefaultVertexFormat.BLOCK), $$0 -> {
                blockShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "new_entity", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                newEntityShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "particle", DefaultVertexFormat.PARTICLE), $$0 -> {
                particleShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "position", DefaultVertexFormat.POSITION), $$0 -> {
                positionShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "position_color", DefaultVertexFormat.POSITION_COLOR), $$0 -> {
                positionColorShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "position_color_lightmap", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP), $$0 -> {
                positionColorLightmapShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "position_color_tex", DefaultVertexFormat.POSITION_COLOR_TEX), $$0 -> {
                positionColorTexShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "position_color_tex_lightmap", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), $$0 -> {
                positionColorTexLightmapShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "position_tex", DefaultVertexFormat.POSITION_TEX), $$0 -> {
                positionTexShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "position_tex_color", DefaultVertexFormat.POSITION_TEX_COLOR), $$0 -> {
                positionTexColorShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "position_tex_color_normal", DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL), $$0 -> {
                positionTexColorNormalShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "position_tex_lightmap_color", DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR), $$0 -> {
                positionTexLightmapColorShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_solid", DefaultVertexFormat.BLOCK), $$0 -> {
                rendertypeSolidShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_cutout_mipped", DefaultVertexFormat.BLOCK), $$0 -> {
                rendertypeCutoutMippedShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_cutout", DefaultVertexFormat.BLOCK), $$0 -> {
                rendertypeCutoutShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_translucent", DefaultVertexFormat.BLOCK), $$0 -> {
                rendertypeTranslucentShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_translucent_moving_block", DefaultVertexFormat.BLOCK), $$0 -> {
                rendertypeTranslucentMovingBlockShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_translucent_no_crumbling", DefaultVertexFormat.BLOCK), $$0 -> {
                rendertypeTranslucentNoCrumblingShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_armor_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeArmorCutoutNoCullShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_solid", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEntitySolidShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_cutout", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEntityCutoutShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEntityCutoutNoCullShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_cutout_no_cull_z_offset", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEntityCutoutNoCullZOffsetShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_item_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeItemEntityTranslucentCullShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEntityTranslucentCullShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_translucent", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEntityTranslucentShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEntityTranslucentEmissiveShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_smooth_cutout", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEntitySmoothCutoutShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_beacon_beam", DefaultVertexFormat.BLOCK), $$0 -> {
                rendertypeBeaconBeamShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_decal", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEntityDecalShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_no_outline", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEntityNoOutlineShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_shadow", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEntityShadowShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_alpha", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEntityAlphaShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_eyes", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEyesShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_energy_swirl", DefaultVertexFormat.NEW_ENTITY), $$0 -> {
                rendertypeEnergySwirlShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_leash", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP), $$0 -> {
                rendertypeLeashShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_water_mask", DefaultVertexFormat.POSITION), $$0 -> {
                rendertypeWaterMaskShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_outline", DefaultVertexFormat.POSITION_COLOR_TEX), $$0 -> {
                rendertypeOutlineShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_armor_glint", DefaultVertexFormat.POSITION_TEX), $$0 -> {
                rendertypeArmorGlintShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_armor_entity_glint", DefaultVertexFormat.POSITION_TEX), $$0 -> {
                rendertypeArmorEntityGlintShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_glint_translucent", DefaultVertexFormat.POSITION_TEX), $$0 -> {
                rendertypeGlintTranslucentShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_glint", DefaultVertexFormat.POSITION_TEX), $$0 -> {
                rendertypeGlintShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_glint_direct", DefaultVertexFormat.POSITION_TEX), $$0 -> {
                rendertypeGlintDirectShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_glint", DefaultVertexFormat.POSITION_TEX), $$0 -> {
                rendertypeEntityGlintShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_entity_glint_direct", DefaultVertexFormat.POSITION_TEX), $$0 -> {
                rendertypeEntityGlintDirectShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), $$0 -> {
                rendertypeTextShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_text_intensity", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), $$0 -> {
                rendertypeTextIntensityShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_text_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), $$0 -> {
                rendertypeTextSeeThroughShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_text_intensity_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), $$0 -> {
                rendertypeTextIntensitySeeThroughShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_lightning", DefaultVertexFormat.POSITION_COLOR), $$0 -> {
                rendertypeLightningShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_tripwire", DefaultVertexFormat.BLOCK), $$0 -> {
                rendertypeTripwireShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_end_portal", DefaultVertexFormat.POSITION), $$0 -> {
                rendertypeEndPortalShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_end_gateway", DefaultVertexFormat.POSITION), $$0 -> {
                rendertypeEndGatewayShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_lines", DefaultVertexFormat.POSITION_COLOR_NORMAL), $$0 -> {
                rendertypeLinesShader = $$0;
            }));
            $$2.add((Object)Pair.of((Object)new ShaderInstance($$02, "rendertype_crumbling", DefaultVertexFormat.BLOCK), $$0 -> {
                rendertypeCrumblingShader = $$0;
            }));
        }
        catch (IOException $$3) {
            $$2.forEach($$0 -> ((ShaderInstance)$$0.getFirst()).close());
            throw new RuntimeException("could not reload shaders", (Throwable)$$3);
        }
        this.shutdownShaders();
        $$2.forEach($$0 -> {
            ShaderInstance $$1 = (ShaderInstance)$$0.getFirst();
            this.shaders.put((Object)$$1.getName(), (Object)$$1);
            ((Consumer)$$0.getSecond()).accept((Object)$$1);
        });
    }

    private void shutdownShaders() {
        RenderSystem.assertOnRenderThread();
        this.shaders.values().forEach(ShaderInstance::close);
        this.shaders.clear();
    }

    @Nullable
    public ShaderInstance getShader(@Nullable String $$0) {
        if ($$0 == null) {
            return null;
        }
        return (ShaderInstance)this.shaders.get((Object)$$0);
    }

    public void tick() {
        this.tickFov();
        this.lightTexture.tick();
        if (this.minecraft.getCameraEntity() == null) {
            this.minecraft.setCameraEntity(this.minecraft.player);
        }
        this.mainCamera.tick();
        ++this.tick;
        this.itemInHandRenderer.tick();
        this.minecraft.levelRenderer.tickRain(this.mainCamera);
        this.darkenWorldAmountO = this.darkenWorldAmount;
        if (this.minecraft.gui.getBossOverlay().shouldDarkenScreen()) {
            this.darkenWorldAmount += 0.05f;
            if (this.darkenWorldAmount > 1.0f) {
                this.darkenWorldAmount = 1.0f;
            }
        } else if (this.darkenWorldAmount > 0.0f) {
            this.darkenWorldAmount -= 0.0125f;
        }
        if (this.itemActivationTicks > 0) {
            --this.itemActivationTicks;
            if (this.itemActivationTicks == 0) {
                this.itemActivationItem = null;
            }
        }
    }

    @Nullable
    public PostChain currentEffect() {
        return this.postEffect;
    }

    public void resize(int $$0, int $$1) {
        if (this.postEffect != null) {
            this.postEffect.resize($$0, $$1);
        }
        this.minecraft.levelRenderer.resize($$0, $$1);
    }

    public void pick(float $$02) {
        Entity $$1 = this.minecraft.getCameraEntity();
        if ($$1 == null) {
            return;
        }
        if (this.minecraft.level == null) {
            return;
        }
        this.minecraft.getProfiler().push("pick");
        this.minecraft.crosshairPickEntity = null;
        double $$2 = this.minecraft.gameMode.getPickRange();
        this.minecraft.hitResult = $$1.pick($$2, $$02, false);
        Vec3 $$3 = $$1.getEyePosition($$02);
        boolean $$4 = false;
        int $$5 = 3;
        double $$6 = $$2;
        if (this.minecraft.gameMode.hasFarPickRange()) {
            $$2 = $$6 = 6.0;
        } else {
            if ($$6 > 3.0) {
                $$4 = true;
            }
            $$2 = $$6;
        }
        $$6 *= $$6;
        if (this.minecraft.hitResult != null) {
            $$6 = this.minecraft.hitResult.getLocation().distanceToSqr($$3);
        }
        Vec3 $$7 = $$1.getViewVector(1.0f);
        Vec3 $$8 = $$3.add($$7.x * $$2, $$7.y * $$2, $$7.z * $$2);
        float $$9 = 1.0f;
        AABB $$10 = $$1.getBoundingBox().expandTowards($$7.scale($$2)).inflate(1.0, 1.0, 1.0);
        EntityHitResult $$11 = ProjectileUtil.getEntityHitResult($$1, $$3, $$8, $$10, (Predicate<Entity>)((Predicate)$$0 -> !$$0.isSpectator() && $$0.isPickable()), $$6);
        if ($$11 != null) {
            Entity $$12 = $$11.getEntity();
            Vec3 $$13 = $$11.getLocation();
            double $$14 = $$3.distanceToSqr($$13);
            if ($$4 && $$14 > 9.0) {
                this.minecraft.hitResult = BlockHitResult.miss($$13, Direction.getNearest($$7.x, $$7.y, $$7.z), new BlockPos($$13));
            } else if ($$14 < $$6 || this.minecraft.hitResult == null) {
                this.minecraft.hitResult = $$11;
                if ($$12 instanceof LivingEntity || $$12 instanceof ItemFrame) {
                    this.minecraft.crosshairPickEntity = $$12;
                }
            }
        }
        this.minecraft.getProfiler().pop();
    }

    private void tickFov() {
        float $$0 = 1.0f;
        if (this.minecraft.getCameraEntity() instanceof AbstractClientPlayer) {
            AbstractClientPlayer $$1 = (AbstractClientPlayer)this.minecraft.getCameraEntity();
            $$0 = $$1.getFieldOfViewModifier();
        }
        this.oldFov = this.fov;
        this.fov += ($$0 - this.fov) * 0.5f;
        if (this.fov > 1.5f) {
            this.fov = 1.5f;
        }
        if (this.fov < 0.1f) {
            this.fov = 0.1f;
        }
    }

    private double getFov(Camera $$0, float $$1, boolean $$2) {
        FogType $$5;
        if (this.panoramicMode) {
            return 90.0;
        }
        double $$3 = 70.0;
        if ($$2) {
            $$3 = this.minecraft.options.fov().get().intValue();
            $$3 *= (double)Mth.lerp($$1, this.oldFov, this.fov);
        }
        if ($$0.getEntity() instanceof LivingEntity && ((LivingEntity)$$0.getEntity()).isDeadOrDying()) {
            float $$4 = Math.min((float)((float)((LivingEntity)$$0.getEntity()).deathTime + $$1), (float)20.0f);
            $$3 /= (double)((1.0f - 500.0f / ($$4 + 500.0f)) * 2.0f + 1.0f);
        }
        if (($$5 = $$0.getFluidInCamera()) == FogType.LAVA || $$5 == FogType.WATER) {
            $$3 *= Mth.lerp(this.minecraft.options.fovEffectScale().get(), 1.0, 0.8571428656578064);
        }
        return $$3;
    }

    private void bobHurt(PoseStack $$0, float $$1) {
        if (this.minecraft.getCameraEntity() instanceof LivingEntity) {
            LivingEntity $$2 = (LivingEntity)this.minecraft.getCameraEntity();
            float $$3 = (float)$$2.hurtTime - $$1;
            if ($$2.isDeadOrDying()) {
                float $$4 = Math.min((float)((float)$$2.deathTime + $$1), (float)20.0f);
                $$0.mulPose(Axis.ZP.rotationDegrees(40.0f - 8000.0f / ($$4 + 200.0f)));
            }
            if ($$3 < 0.0f) {
                return;
            }
            $$3 /= (float)$$2.hurtDuration;
            $$3 = Mth.sin($$3 * $$3 * $$3 * $$3 * (float)Math.PI);
            float $$5 = $$2.hurtDir;
            $$0.mulPose(Axis.YP.rotationDegrees(-$$5));
            $$0.mulPose(Axis.ZP.rotationDegrees(-$$3 * 14.0f));
            $$0.mulPose(Axis.YP.rotationDegrees($$5));
        }
    }

    private void bobView(PoseStack $$0, float $$1) {
        if (!(this.minecraft.getCameraEntity() instanceof Player)) {
            return;
        }
        Player $$2 = (Player)this.minecraft.getCameraEntity();
        float $$3 = $$2.walkDist - $$2.walkDistO;
        float $$4 = -($$2.walkDist + $$3 * $$1);
        float $$5 = Mth.lerp($$1, $$2.oBob, $$2.bob);
        $$0.translate(Mth.sin($$4 * (float)Math.PI) * $$5 * 0.5f, -Math.abs((float)(Mth.cos($$4 * (float)Math.PI) * $$5)), 0.0f);
        $$0.mulPose(Axis.ZP.rotationDegrees(Mth.sin($$4 * (float)Math.PI) * $$5 * 3.0f));
        $$0.mulPose(Axis.XP.rotationDegrees(Math.abs((float)(Mth.cos($$4 * (float)Math.PI - 0.2f) * $$5)) * 5.0f));
    }

    public void renderZoomed(float $$0, float $$1, float $$2) {
        this.zoom = $$0;
        this.zoomX = $$1;
        this.zoomY = $$2;
        this.setRenderBlockOutline(false);
        this.setRenderHand(false);
        this.renderLevel(1.0f, 0L, new PoseStack());
        this.zoom = 1.0f;
    }

    private void renderItemInHand(PoseStack $$0, Camera $$1, float $$2) {
        boolean $$3;
        if (this.panoramicMode) {
            return;
        }
        this.resetProjectionMatrix(this.getProjectionMatrix(this.getFov($$1, $$2, false)));
        $$0.setIdentity();
        $$0.pushPose();
        this.bobHurt($$0, $$2);
        if (this.minecraft.options.bobView().get().booleanValue()) {
            this.bobView($$0, $$2);
        }
        boolean bl = $$3 = this.minecraft.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.minecraft.getCameraEntity()).isSleeping();
        if (this.minecraft.options.getCameraType().isFirstPerson() && !$$3 && !this.minecraft.options.hideGui && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.lightTexture.turnOnLightLayer();
            this.itemInHandRenderer.renderHandsWithItems($$2, $$0, this.renderBuffers.bufferSource(), this.minecraft.player, this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, $$2));
            this.lightTexture.turnOffLightLayer();
        }
        $$0.popPose();
        if (this.minecraft.options.getCameraType().isFirstPerson() && !$$3) {
            ScreenEffectRenderer.renderScreenEffect(this.minecraft, $$0);
            this.bobHurt($$0, $$2);
        }
        if (this.minecraft.options.bobView().get().booleanValue()) {
            this.bobView($$0, $$2);
        }
    }

    public void resetProjectionMatrix(Matrix4f $$0) {
        RenderSystem.setProjectionMatrix($$0);
    }

    public Matrix4f getProjectionMatrix(double $$0) {
        PoseStack $$1 = new PoseStack();
        $$1.last().pose().identity();
        if (this.zoom != 1.0f) {
            $$1.translate(this.zoomX, -this.zoomY, 0.0f);
            $$1.scale(this.zoom, this.zoom, 1.0f);
        }
        $$1.last().pose().mul((Matrix4fc)new Matrix4f().setPerspective((float)($$0 * 0.01745329238474369), (float)this.minecraft.getWindow().getWidth() / (float)this.minecraft.getWindow().getHeight(), 0.05f, this.getDepthFar()));
        return $$1.last().pose();
    }

    public float getDepthFar() {
        return this.renderDistance * 4.0f;
    }

    public static float getNightVisionScale(LivingEntity $$0, float $$1) {
        int $$2 = $$0.getEffect(MobEffects.NIGHT_VISION).getDuration();
        if ($$2 > 200) {
            return 1.0f;
        }
        return 0.7f + Mth.sin(((float)$$2 - $$1) * (float)Math.PI * 0.2f) * 0.3f;
    }

    public void render(float $$0, long $$1, boolean $$2) {
        if (this.minecraft.isWindowActive() || !this.minecraft.options.pauseOnLostFocus || this.minecraft.options.touchscreen().get().booleanValue() && this.minecraft.mouseHandler.isRightPressed()) {
            this.lastActiveTime = Util.getMillis();
        } else if (Util.getMillis() - this.lastActiveTime > 500L) {
            this.minecraft.pauseGame(false);
        }
        if (this.minecraft.noRender) {
            return;
        }
        int $$3 = (int)(this.minecraft.mouseHandler.xpos() * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth());
        int $$4 = (int)(this.minecraft.mouseHandler.ypos() * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight());
        RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
        if ($$2 && this.minecraft.level != null) {
            this.minecraft.getProfiler().push("level");
            this.renderLevel($$0, $$1, new PoseStack());
            this.tryTakeScreenshotIfNeeded();
            this.minecraft.levelRenderer.doEntityOutline();
            if (this.postEffect != null && this.effectActive) {
                RenderSystem.disableBlend();
                RenderSystem.disableDepthTest();
                RenderSystem.enableTexture();
                RenderSystem.resetTextureMatrix();
                this.postEffect.process($$0);
            }
            this.minecraft.getMainRenderTarget().bindWrite(true);
        }
        Window $$5 = this.minecraft.getWindow();
        RenderSystem.clear(256, Minecraft.ON_OSX);
        Matrix4f $$6 = new Matrix4f().setOrtho(0.0f, (float)((double)$$5.getWidth() / $$5.getGuiScale()), (float)((double)$$5.getHeight() / $$5.getGuiScale()), 0.0f, 1000.0f, 3000.0f);
        RenderSystem.setProjectionMatrix($$6);
        PoseStack $$7 = RenderSystem.getModelViewStack();
        $$7.setIdentity();
        $$7.translate(0.0f, 0.0f, -2000.0f);
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
        PoseStack $$8 = new PoseStack();
        if ($$2 && this.minecraft.level != null) {
            this.minecraft.getProfiler().popPush("gui");
            if (this.minecraft.player != null) {
                float $$9 = Mth.lerp($$0, this.minecraft.player.oPortalTime, this.minecraft.player.portalTime);
                float $$10 = this.minecraft.options.screenEffectScale().get().floatValue();
                if ($$9 > 0.0f && this.minecraft.player.hasEffect(MobEffects.CONFUSION) && $$10 < 1.0f) {
                    this.renderConfusionOverlay($$9 * (1.0f - $$10));
                }
            }
            if (!this.minecraft.options.hideGui || this.minecraft.screen != null) {
                this.renderItemActivationAnimation(this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), $$0);
                this.minecraft.gui.render($$8, $$0);
                RenderSystem.clear(256, Minecraft.ON_OSX);
            }
            this.minecraft.getProfiler().pop();
        }
        if (this.minecraft.getOverlay() != null) {
            try {
                this.minecraft.getOverlay().render($$8, $$3, $$4, this.minecraft.getDeltaFrameTime());
            }
            catch (Throwable $$11) {
                CrashReport $$12 = CrashReport.forThrowable($$11, "Rendering overlay");
                CrashReportCategory $$13 = $$12.addCategory("Overlay render details");
                $$13.setDetail("Overlay name", () -> this.minecraft.getOverlay().getClass().getCanonicalName());
                throw new ReportedException($$12);
            }
        }
        if (this.minecraft.screen != null) {
            try {
                this.minecraft.screen.renderWithTooltip($$8, $$3, $$4, this.minecraft.getDeltaFrameTime());
            }
            catch (Throwable $$14) {
                CrashReport $$15 = CrashReport.forThrowable($$14, "Rendering screen");
                CrashReportCategory $$16 = $$15.addCategory("Screen render details");
                $$16.setDetail("Screen name", () -> this.minecraft.screen.getClass().getCanonicalName());
                $$16.setDetail("Mouse location", () -> String.format((Locale)Locale.ROOT, (String)"Scaled: (%d, %d). Absolute: (%f, %f)", (Object[])new Object[]{$$3, $$4, this.minecraft.mouseHandler.xpos(), this.minecraft.mouseHandler.ypos()}));
                $$16.setDetail("Screen size", () -> String.format((Locale)Locale.ROOT, (String)"Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", (Object[])new Object[]{this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), this.minecraft.getWindow().getGuiScale()}));
                throw new ReportedException($$15);
            }
            try {
                if (this.minecraft.screen != null) {
                    this.minecraft.screen.handleDelayedNarration();
                }
            }
            catch (Throwable $$17) {
                CrashReport $$18 = CrashReport.forThrowable($$17, "Narrating screen");
                CrashReportCategory $$19 = $$18.addCategory("Screen details");
                $$19.setDetail("Screen name", () -> this.minecraft.screen.getClass().getCanonicalName());
                throw new ReportedException($$18);
            }
        }
    }

    private void tryTakeScreenshotIfNeeded() {
        if (this.hasWorldScreenshot || !this.minecraft.isLocalServer()) {
            return;
        }
        long $$02 = Util.getMillis();
        if ($$02 - this.lastScreenshotAttempt < 1000L) {
            return;
        }
        this.lastScreenshotAttempt = $$02;
        IntegratedServer $$1 = this.minecraft.getSingleplayerServer();
        if ($$1 == null || $$1.isStopped()) {
            return;
        }
        $$1.getWorldScreenshotFile().ifPresent($$0 -> {
            if (Files.isRegularFile((Path)$$0, (LinkOption[])new LinkOption[0])) {
                this.hasWorldScreenshot = true;
            } else {
                this.takeAutoScreenshot((Path)$$0);
            }
        });
    }

    private void takeAutoScreenshot(Path $$0) {
        if (this.minecraft.levelRenderer.countRenderedChunks() > 10 && this.minecraft.levelRenderer.hasRenderedAllChunks()) {
            NativeImage $$1 = Screenshot.takeScreenshot(this.minecraft.getMainRenderTarget());
            Util.ioPool().execute(() -> {
                int $$2 = $$1.getWidth();
                int $$3 = $$1.getHeight();
                int $$4 = 0;
                int $$5 = 0;
                if ($$2 > $$3) {
                    $$4 = ($$2 - $$3) / 2;
                    $$2 = $$3;
                } else {
                    $$5 = ($$3 - $$2) / 2;
                    $$3 = $$2;
                }
                try (NativeImage $$6 = new NativeImage(64, 64, false);){
                    $$1.resizeSubRectTo($$4, $$5, $$2, $$3, $$6);
                    $$6.writeToFile($$0);
                }
                catch (IOException $$7) {
                    LOGGER.warn("Couldn't save auto screenshot", (Throwable)$$7);
                }
                finally {
                    $$1.close();
                }
            });
        }
    }

    private boolean shouldRenderBlockOutline() {
        boolean $$1;
        if (!this.renderBlockOutline) {
            return false;
        }
        Entity $$0 = this.minecraft.getCameraEntity();
        boolean bl = $$1 = $$0 instanceof Player && !this.minecraft.options.hideGui;
        if ($$1 && !((Player)$$0).getAbilities().mayBuild) {
            ItemStack $$2 = ((LivingEntity)$$0).getMainHandItem();
            HitResult $$3 = this.minecraft.hitResult;
            if ($$3 != null && $$3.getType() == HitResult.Type.BLOCK) {
                BlockPos $$4 = ((BlockHitResult)$$3).getBlockPos();
                BlockState $$5 = this.minecraft.level.getBlockState($$4);
                if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
                    $$1 = $$5.getMenuProvider(this.minecraft.level, $$4) != null;
                } else {
                    BlockInWorld $$6 = new BlockInWorld(this.minecraft.level, $$4, false);
                    Registry<Block> $$7 = this.minecraft.level.registryAccess().registryOrThrow(Registries.BLOCK);
                    $$1 = !$$2.isEmpty() && ($$2.hasAdventureModeBreakTagForBlock($$7, $$6) || $$2.hasAdventureModePlaceTagForBlock($$7, $$6));
                }
            }
        }
        return $$1;
    }

    public void renderLevel(float $$0, long $$1, PoseStack $$2) {
        this.lightTexture.updateLightTexture($$0);
        if (this.minecraft.getCameraEntity() == null) {
            this.minecraft.setCameraEntity(this.minecraft.player);
        }
        this.pick($$0);
        this.minecraft.getProfiler().push("center");
        boolean $$3 = this.shouldRenderBlockOutline();
        this.minecraft.getProfiler().popPush("camera");
        Camera $$4 = this.mainCamera;
        this.renderDistance = this.minecraft.options.getEffectiveRenderDistance() * 16;
        PoseStack $$5 = new PoseStack();
        double $$6 = this.getFov($$4, $$0, true);
        $$5.mulPoseMatrix(this.getProjectionMatrix($$6));
        this.bobHurt($$5, $$0);
        if (this.minecraft.options.bobView().get().booleanValue()) {
            this.bobView($$5, $$0);
        }
        float $$7 = this.minecraft.options.screenEffectScale().get().floatValue();
        float $$8 = Mth.lerp($$0, this.minecraft.player.oPortalTime, this.minecraft.player.portalTime) * ($$7 * $$7);
        if ($$8 > 0.0f) {
            int $$9 = this.minecraft.player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
            float $$10 = 5.0f / ($$8 * $$8 + 5.0f) - $$8 * 0.04f;
            $$10 *= $$10;
            Axis $$11 = Axis.of(new Vector3f(0.0f, Mth.SQRT_OF_TWO / 2.0f, Mth.SQRT_OF_TWO / 2.0f));
            $$5.mulPose($$11.rotationDegrees(((float)this.tick + $$0) * (float)$$9));
            $$5.scale(1.0f / $$10, 1.0f, 1.0f);
            float $$12 = -((float)this.tick + $$0) * (float)$$9;
            $$5.mulPose($$11.rotationDegrees($$12));
        }
        Matrix4f $$13 = $$5.last().pose();
        this.resetProjectionMatrix($$13);
        $$4.setup(this.minecraft.level, this.minecraft.getCameraEntity() == null ? this.minecraft.player : this.minecraft.getCameraEntity(), !this.minecraft.options.getCameraType().isFirstPerson(), this.minecraft.options.getCameraType().isMirrored(), $$0);
        $$2.mulPose(Axis.XP.rotationDegrees($$4.getXRot()));
        $$2.mulPose(Axis.YP.rotationDegrees($$4.getYRot() + 180.0f));
        Matrix3f $$14 = new Matrix3f((Matrix3fc)$$2.last().normal()).invert();
        RenderSystem.setInverseViewRotationMatrix($$14);
        this.minecraft.levelRenderer.prepareCullFrustum($$2, $$4.getPosition(), this.getProjectionMatrix(Math.max((double)$$6, (double)this.minecraft.options.fov().get().intValue())));
        this.minecraft.levelRenderer.renderLevel($$2, $$0, $$1, $$3, $$4, this, this.lightTexture, $$13);
        this.minecraft.getProfiler().popPush("hand");
        if (this.renderHand) {
            RenderSystem.clear(256, Minecraft.ON_OSX);
            this.renderItemInHand($$2, $$4, $$0);
        }
        this.minecraft.getProfiler().pop();
    }

    public void resetData() {
        this.itemActivationItem = null;
        this.mapRenderer.resetData();
        this.mainCamera.reset();
        this.hasWorldScreenshot = false;
    }

    public MapRenderer getMapRenderer() {
        return this.mapRenderer;
    }

    public void displayItemActivation(ItemStack $$0) {
        this.itemActivationItem = $$0;
        this.itemActivationTicks = 40;
        this.itemActivationOffX = this.random.nextFloat() * 2.0f - 1.0f;
        this.itemActivationOffY = this.random.nextFloat() * 2.0f - 1.0f;
    }

    private void renderItemActivationAnimation(int $$0, int $$1, float $$2) {
        if (this.itemActivationItem == null || this.itemActivationTicks <= 0) {
            return;
        }
        int $$3 = 40 - this.itemActivationTicks;
        float $$4 = ((float)$$3 + $$2) / 40.0f;
        float $$5 = $$4 * $$4;
        float $$6 = $$4 * $$5;
        float $$7 = 10.25f * $$6 * $$5 - 24.95f * $$5 * $$5 + 25.5f * $$6 - 13.8f * $$5 + 4.0f * $$4;
        float $$8 = $$7 * (float)Math.PI;
        float $$9 = this.itemActivationOffX * (float)($$0 / 4);
        float $$10 = this.itemActivationOffY * (float)($$1 / 4);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        PoseStack $$11 = new PoseStack();
        $$11.pushPose();
        $$11.translate((float)($$0 / 2) + $$9 * Mth.abs(Mth.sin($$8 * 2.0f)), (float)($$1 / 2) + $$10 * Mth.abs(Mth.sin($$8 * 2.0f)), -50.0f);
        float $$12 = 50.0f + 175.0f * Mth.sin($$8);
        $$11.scale($$12, -$$12, $$12);
        $$11.mulPose(Axis.YP.rotationDegrees(900.0f * Mth.abs(Mth.sin($$8))));
        $$11.mulPose(Axis.XP.rotationDegrees(6.0f * Mth.cos($$4 * 8.0f)));
        $$11.mulPose(Axis.ZP.rotationDegrees(6.0f * Mth.cos($$4 * 8.0f)));
        MultiBufferSource.BufferSource $$13 = this.renderBuffers.bufferSource();
        this.minecraft.getItemRenderer().renderStatic(this.itemActivationItem, ItemTransforms.TransformType.FIXED, 0xF000F0, OverlayTexture.NO_OVERLAY, $$11, $$13, 0);
        $$11.popPose();
        $$13.endBatch();
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
    }

    private void renderConfusionOverlay(float $$0) {
        int $$1 = this.minecraft.getWindow().getGuiScaledWidth();
        int $$2 = this.minecraft.getWindow().getGuiScaledHeight();
        double $$3 = Mth.lerp((double)$$0, 2.0, 1.0);
        float $$4 = 0.2f * $$0;
        float $$5 = 0.4f * $$0;
        float $$6 = 0.2f * $$0;
        double $$7 = (double)$$1 * $$3;
        double $$8 = (double)$$2 * $$3;
        double $$9 = ((double)$$1 - $$7) / 2.0;
        double $$10 = ((double)$$2 - $$8) / 2.0;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        RenderSystem.setShaderColor($$4, $$5, $$6, 1.0f);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, NAUSEA_LOCATION);
        Tesselator $$11 = Tesselator.getInstance();
        BufferBuilder $$12 = $$11.getBuilder();
        $$12.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        $$12.vertex($$9, $$10 + $$8, -90.0).uv(0.0f, 1.0f).endVertex();
        $$12.vertex($$9 + $$7, $$10 + $$8, -90.0).uv(1.0f, 1.0f).endVertex();
        $$12.vertex($$9 + $$7, $$10, -90.0).uv(1.0f, 0.0f).endVertex();
        $$12.vertex($$9, $$10, -90.0).uv(0.0f, 0.0f).endVertex();
        $$11.end();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public float getDarkenWorldAmount(float $$0) {
        return Mth.lerp($$0, this.darkenWorldAmountO, this.darkenWorldAmount);
    }

    public float getRenderDistance() {
        return this.renderDistance;
    }

    public Camera getMainCamera() {
        return this.mainCamera;
    }

    public LightTexture lightTexture() {
        return this.lightTexture;
    }

    public OverlayTexture overlayTexture() {
        return this.overlayTexture;
    }

    @Nullable
    public static ShaderInstance getPositionShader() {
        return positionShader;
    }

    @Nullable
    public static ShaderInstance getPositionColorShader() {
        return positionColorShader;
    }

    @Nullable
    public static ShaderInstance getPositionColorTexShader() {
        return positionColorTexShader;
    }

    @Nullable
    public static ShaderInstance getPositionTexShader() {
        return positionTexShader;
    }

    @Nullable
    public static ShaderInstance getPositionTexColorShader() {
        return positionTexColorShader;
    }

    @Nullable
    public static ShaderInstance getBlockShader() {
        return blockShader;
    }

    @Nullable
    public static ShaderInstance getNewEntityShader() {
        return newEntityShader;
    }

    @Nullable
    public static ShaderInstance getParticleShader() {
        return particleShader;
    }

    @Nullable
    public static ShaderInstance getPositionColorLightmapShader() {
        return positionColorLightmapShader;
    }

    @Nullable
    public static ShaderInstance getPositionColorTexLightmapShader() {
        return positionColorTexLightmapShader;
    }

    @Nullable
    public static ShaderInstance getPositionTexColorNormalShader() {
        return positionTexColorNormalShader;
    }

    @Nullable
    public static ShaderInstance getPositionTexLightmapColorShader() {
        return positionTexLightmapColorShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeSolidShader() {
        return rendertypeSolidShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeCutoutMippedShader() {
        return rendertypeCutoutMippedShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeCutoutShader() {
        return rendertypeCutoutShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeTranslucentShader() {
        return rendertypeTranslucentShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeTranslucentMovingBlockShader() {
        return rendertypeTranslucentMovingBlockShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeTranslucentNoCrumblingShader() {
        return rendertypeTranslucentNoCrumblingShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeArmorCutoutNoCullShader() {
        return rendertypeArmorCutoutNoCullShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntitySolidShader() {
        return rendertypeEntitySolidShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntityCutoutShader() {
        return rendertypeEntityCutoutShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntityCutoutNoCullShader() {
        return rendertypeEntityCutoutNoCullShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntityCutoutNoCullZOffsetShader() {
        return rendertypeEntityCutoutNoCullZOffsetShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeItemEntityTranslucentCullShader() {
        return rendertypeItemEntityTranslucentCullShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntityTranslucentCullShader() {
        return rendertypeEntityTranslucentCullShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntityTranslucentShader() {
        return rendertypeEntityTranslucentShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntityTranslucentEmissiveShader() {
        return rendertypeEntityTranslucentEmissiveShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntitySmoothCutoutShader() {
        return rendertypeEntitySmoothCutoutShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeBeaconBeamShader() {
        return rendertypeBeaconBeamShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntityDecalShader() {
        return rendertypeEntityDecalShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntityNoOutlineShader() {
        return rendertypeEntityNoOutlineShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntityShadowShader() {
        return rendertypeEntityShadowShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntityAlphaShader() {
        return rendertypeEntityAlphaShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEyesShader() {
        return rendertypeEyesShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEnergySwirlShader() {
        return rendertypeEnergySwirlShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeLeashShader() {
        return rendertypeLeashShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeWaterMaskShader() {
        return rendertypeWaterMaskShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeOutlineShader() {
        return rendertypeOutlineShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeArmorGlintShader() {
        return rendertypeArmorGlintShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeArmorEntityGlintShader() {
        return rendertypeArmorEntityGlintShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeGlintTranslucentShader() {
        return rendertypeGlintTranslucentShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeGlintShader() {
        return rendertypeGlintShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeGlintDirectShader() {
        return rendertypeGlintDirectShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntityGlintShader() {
        return rendertypeEntityGlintShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEntityGlintDirectShader() {
        return rendertypeEntityGlintDirectShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeTextShader() {
        return rendertypeTextShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeTextIntensityShader() {
        return rendertypeTextIntensityShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeTextSeeThroughShader() {
        return rendertypeTextSeeThroughShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeTextIntensitySeeThroughShader() {
        return rendertypeTextIntensitySeeThroughShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeLightningShader() {
        return rendertypeLightningShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeTripwireShader() {
        return rendertypeTripwireShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEndPortalShader() {
        return rendertypeEndPortalShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeEndGatewayShader() {
        return rendertypeEndGatewayShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeLinesShader() {
        return rendertypeLinesShader;
    }

    @Nullable
    public static ShaderInstance getRendertypeCrumblingShader() {
        return rendertypeCrumblingShader;
    }

    public record ResourceCache(ResourceProvider original, Map<ResourceLocation, Resource> cache) implements ResourceProvider
    {
        @Override
        public Optional<Resource> getResource(ResourceLocation $$0) {
            Resource $$1 = (Resource)this.cache.get((Object)$$0);
            if ($$1 != null) {
                return Optional.of((Object)$$1);
            }
            return this.original.getResource($$0);
        }
    }
}
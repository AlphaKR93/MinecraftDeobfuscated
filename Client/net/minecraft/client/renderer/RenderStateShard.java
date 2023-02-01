/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  java.io.Serializable
 *  java.lang.Boolean
 *  java.lang.Double
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.OptionalDouble
 *  java.util.function.Supplier
 *  org.apache.commons.lang3.tuple.Triple
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix4f;

public abstract class RenderStateShard {
    private static final float VIEW_SCALE_Z_EPSILON = 0.99975586f;
    public static final double MAX_ENCHANTMENT_GLINT_SPEED_MILLIS = 8.0;
    protected final String name;
    private final Runnable setupState;
    private final Runnable clearState;
    protected static final TransparencyStateShard NO_TRANSPARENCY = new TransparencyStateShard("no_transparency", () -> RenderSystem.disableBlend(), () -> {});
    protected static final TransparencyStateShard ADDITIVE_TRANSPARENCY = new TransparencyStateShard("additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final TransparencyStateShard LIGHTNING_TRANSPARENCY = new TransparencyStateShard("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final TransparencyStateShard GLINT_TRANSPARENCY = new TransparencyStateShard("glint_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final TransparencyStateShard CRUMBLING_TRANSPARENCY = new TransparencyStateShard("crumbling_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new TransparencyStateShard("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final ShaderStateShard NO_SHADER = new ShaderStateShard();
    protected static final ShaderStateShard BLOCK_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getBlockShader));
    protected static final ShaderStateShard NEW_ENTITY_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getNewEntityShader));
    protected static final ShaderStateShard POSITION_COLOR_LIGHTMAP_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorLightmapShader));
    protected static final ShaderStateShard POSITION_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionShader));
    protected static final ShaderStateShard POSITION_COLOR_TEX_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorTexShader));
    protected static final ShaderStateShard POSITION_TEX_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
    protected static final ShaderStateShard POSITION_COLOR_TEX_LIGHTMAP_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorTexLightmapShader));
    protected static final ShaderStateShard POSITION_COLOR_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
    protected static final ShaderStateShard RENDERTYPE_SOLID_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeSolidShader));
    protected static final ShaderStateShard RENDERTYPE_CUTOUT_MIPPED_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeCutoutMippedShader));
    protected static final ShaderStateShard RENDERTYPE_CUTOUT_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeCutoutShader));
    protected static final ShaderStateShard RENDERTYPE_TRANSLUCENT_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeTranslucentShader));
    protected static final ShaderStateShard RENDERTYPE_TRANSLUCENT_MOVING_BLOCK_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeTranslucentMovingBlockShader));
    protected static final ShaderStateShard RENDERTYPE_TRANSLUCENT_NO_CRUMBLING_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeTranslucentNoCrumblingShader));
    protected static final ShaderStateShard RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeArmorCutoutNoCullShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_SOLID_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntitySolidShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntityCutoutShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntityCutoutNoCullShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntityCutoutNoCullZOffsetShader));
    protected static final ShaderStateShard RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeItemEntityTranslucentCullShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntityTranslucentCullShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntityTranslucentShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntityTranslucentEmissiveShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_SMOOTH_CUTOUT_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntitySmoothCutoutShader));
    protected static final ShaderStateShard RENDERTYPE_BEACON_BEAM_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeBeaconBeamShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_DECAL_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntityDecalShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_NO_OUTLINE_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntityNoOutlineShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_SHADOW_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntityShadowShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_ALPHA_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntityAlphaShader));
    protected static final ShaderStateShard RENDERTYPE_EYES_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEyesShader));
    protected static final ShaderStateShard RENDERTYPE_ENERGY_SWIRL_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEnergySwirlShader));
    protected static final ShaderStateShard RENDERTYPE_LEASH_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeLeashShader));
    protected static final ShaderStateShard RENDERTYPE_WATER_MASK_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeWaterMaskShader));
    protected static final ShaderStateShard RENDERTYPE_OUTLINE_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeOutlineShader));
    protected static final ShaderStateShard RENDERTYPE_ARMOR_GLINT_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeArmorGlintShader));
    protected static final ShaderStateShard RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeArmorEntityGlintShader));
    protected static final ShaderStateShard RENDERTYPE_GLINT_TRANSLUCENT_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeGlintTranslucentShader));
    protected static final ShaderStateShard RENDERTYPE_GLINT_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeGlintShader));
    protected static final ShaderStateShard RENDERTYPE_GLINT_DIRECT_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeGlintDirectShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_GLINT_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntityGlintShader));
    protected static final ShaderStateShard RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEntityGlintDirectShader));
    protected static final ShaderStateShard RENDERTYPE_CRUMBLING_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeCrumblingShader));
    protected static final ShaderStateShard RENDERTYPE_TEXT_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeTextShader));
    protected static final ShaderStateShard RENDERTYPE_TEXT_INTENSITY_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeTextIntensityShader));
    protected static final ShaderStateShard RENDERTYPE_TEXT_SEE_THROUGH_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeTextSeeThroughShader));
    protected static final ShaderStateShard RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeTextIntensitySeeThroughShader));
    protected static final ShaderStateShard RENDERTYPE_LIGHTNING_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeLightningShader));
    protected static final ShaderStateShard RENDERTYPE_TRIPWIRE_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeTripwireShader));
    protected static final ShaderStateShard RENDERTYPE_END_PORTAL_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEndPortalShader));
    protected static final ShaderStateShard RENDERTYPE_END_GATEWAY_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeEndGatewayShader));
    protected static final ShaderStateShard RENDERTYPE_LINES_SHADER = new ShaderStateShard((Supplier<ShaderInstance>)((Supplier)GameRenderer::getRendertypeLinesShader));
    protected static final TextureStateShard BLOCK_SHEET_MIPPED = new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, true);
    protected static final TextureStateShard BLOCK_SHEET = new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false);
    protected static final EmptyTextureStateShard NO_TEXTURE = new EmptyTextureStateShard();
    protected static final TexturingStateShard DEFAULT_TEXTURING = new TexturingStateShard("default_texturing", () -> {}, () -> {});
    protected static final TexturingStateShard GLINT_TEXTURING = new TexturingStateShard("glint_texturing", () -> RenderStateShard.setupGlintTexturing(8.0f), () -> RenderSystem.resetTextureMatrix());
    protected static final TexturingStateShard ENTITY_GLINT_TEXTURING = new TexturingStateShard("entity_glint_texturing", () -> RenderStateShard.setupGlintTexturing(0.16f), () -> RenderSystem.resetTextureMatrix());
    protected static final LightmapStateShard LIGHTMAP = new LightmapStateShard(true);
    protected static final LightmapStateShard NO_LIGHTMAP = new LightmapStateShard(false);
    protected static final OverlayStateShard OVERLAY = new OverlayStateShard(true);
    protected static final OverlayStateShard NO_OVERLAY = new OverlayStateShard(false);
    protected static final CullStateShard CULL = new CullStateShard(true);
    protected static final CullStateShard NO_CULL = new CullStateShard(false);
    protected static final DepthTestStateShard NO_DEPTH_TEST = new DepthTestStateShard("always", 519);
    protected static final DepthTestStateShard EQUAL_DEPTH_TEST = new DepthTestStateShard("==", 514);
    protected static final DepthTestStateShard LEQUAL_DEPTH_TEST = new DepthTestStateShard("<=", 515);
    protected static final WriteMaskStateShard COLOR_DEPTH_WRITE = new WriteMaskStateShard(true, true);
    protected static final WriteMaskStateShard COLOR_WRITE = new WriteMaskStateShard(true, false);
    protected static final WriteMaskStateShard DEPTH_WRITE = new WriteMaskStateShard(false, true);
    protected static final LayeringStateShard NO_LAYERING = new LayeringStateShard("no_layering", () -> {}, () -> {});
    protected static final LayeringStateShard POLYGON_OFFSET_LAYERING = new LayeringStateShard("polygon_offset_layering", () -> {
        RenderSystem.polygonOffset(-1.0f, -10.0f);
        RenderSystem.enablePolygonOffset();
    }, () -> {
        RenderSystem.polygonOffset(0.0f, 0.0f);
        RenderSystem.disablePolygonOffset();
    });
    protected static final LayeringStateShard VIEW_OFFSET_Z_LAYERING = new LayeringStateShard("view_offset_z_layering", () -> {
        PoseStack $$0 = RenderSystem.getModelViewStack();
        $$0.pushPose();
        $$0.scale(0.99975586f, 0.99975586f, 0.99975586f);
        RenderSystem.applyModelViewMatrix();
    }, () -> {
        PoseStack $$0 = RenderSystem.getModelViewStack();
        $$0.popPose();
        RenderSystem.applyModelViewMatrix();
    });
    protected static final OutputStateShard MAIN_TARGET = new OutputStateShard("main_target", () -> {}, () -> {});
    protected static final OutputStateShard OUTLINE_TARGET = new OutputStateShard("outline_target", () -> Minecraft.getInstance().levelRenderer.entityTarget().bindWrite(false), () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false));
    protected static final OutputStateShard TRANSLUCENT_TARGET = new OutputStateShard("translucent_target", () -> {
        if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().levelRenderer.getTranslucentTarget().bindWrite(false);
        }
    }, () -> {
        if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
    });
    protected static final OutputStateShard PARTICLES_TARGET = new OutputStateShard("particles_target", () -> {
        if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().levelRenderer.getParticlesTarget().bindWrite(false);
        }
    }, () -> {
        if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
    });
    protected static final OutputStateShard WEATHER_TARGET = new OutputStateShard("weather_target", () -> {
        if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().levelRenderer.getWeatherTarget().bindWrite(false);
        }
    }, () -> {
        if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
    });
    protected static final OutputStateShard CLOUDS_TARGET = new OutputStateShard("clouds_target", () -> {
        if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().levelRenderer.getCloudsTarget().bindWrite(false);
        }
    }, () -> {
        if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
    });
    protected static final OutputStateShard ITEM_ENTITY_TARGET = new OutputStateShard("item_entity_target", () -> {
        if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().levelRenderer.getItemEntityTarget().bindWrite(false);
        }
    }, () -> {
        if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
    });
    protected static final LineStateShard DEFAULT_LINE = new LineStateShard(OptionalDouble.of((double)1.0));

    public RenderStateShard(String $$0, Runnable $$1, Runnable $$2) {
        this.name = $$0;
        this.setupState = $$1;
        this.clearState = $$2;
    }

    public void setupRenderState() {
        this.setupState.run();
    }

    public void clearRenderState() {
        this.clearState.run();
    }

    public String toString() {
        return this.name;
    }

    private static void setupGlintTexturing(float $$0) {
        long $$1 = (long)((double)Util.getMillis() * Minecraft.getInstance().options.glintSpeed().get() * 8.0);
        float $$2 = (float)($$1 % 110000L) / 110000.0f;
        float $$3 = (float)($$1 % 30000L) / 30000.0f;
        Matrix4f $$4 = new Matrix4f().translation(-$$2, $$3, 0.0f);
        $$4.rotateZ(0.17453292f).scale($$0);
        RenderSystem.setTextureMatrix($$4);
    }

    protected static class TransparencyStateShard
    extends RenderStateShard {
        public TransparencyStateShard(String $$0, Runnable $$1, Runnable $$2) {
            super($$0, $$1, $$2);
        }
    }

    protected static class ShaderStateShard
    extends RenderStateShard {
        private final Optional<Supplier<ShaderInstance>> shader;

        public ShaderStateShard(Supplier<ShaderInstance> $$0) {
            super("shader", () -> RenderSystem.setShader($$0), () -> {});
            this.shader = Optional.of($$0);
        }

        public ShaderStateShard() {
            super("shader", () -> RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)() -> null)), () -> {});
            this.shader = Optional.empty();
        }

        @Override
        public String toString() {
            return this.name + "[" + this.shader + "]";
        }
    }

    protected static class TextureStateShard
    extends EmptyTextureStateShard {
        private final Optional<ResourceLocation> texture;
        private final boolean blur;
        private final boolean mipmap;

        public TextureStateShard(ResourceLocation $$0, boolean $$1, boolean $$2) {
            super(() -> {
                TextureManager $$3 = Minecraft.getInstance().getTextureManager();
                $$3.getTexture($$0).setFilter($$1, $$2);
                RenderSystem.setShaderTexture(0, $$0);
            }, () -> {});
            this.texture = Optional.of((Object)$$0);
            this.blur = $$1;
            this.mipmap = $$2;
        }

        @Override
        public String toString() {
            return this.name + "[" + this.texture + "(blur=" + this.blur + ", mipmap=" + this.mipmap + ")]";
        }

        @Override
        protected Optional<ResourceLocation> cutoutTexture() {
            return this.texture;
        }
    }

    protected static class EmptyTextureStateShard
    extends RenderStateShard {
        public EmptyTextureStateShard(Runnable $$0, Runnable $$1) {
            super("texture", $$0, $$1);
        }

        EmptyTextureStateShard() {
            super("texture", () -> {}, () -> {});
        }

        protected Optional<ResourceLocation> cutoutTexture() {
            return Optional.empty();
        }
    }

    protected static class TexturingStateShard
    extends RenderStateShard {
        public TexturingStateShard(String $$0, Runnable $$1, Runnable $$2) {
            super($$0, $$1, $$2);
        }
    }

    protected static class LightmapStateShard
    extends BooleanStateShard {
        public LightmapStateShard(boolean $$0) {
            super("lightmap", () -> {
                if ($$0) {
                    Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
                }
            }, () -> {
                if ($$0) {
                    Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
                }
            }, $$0);
        }
    }

    protected static class OverlayStateShard
    extends BooleanStateShard {
        public OverlayStateShard(boolean $$0) {
            super("overlay", () -> {
                if ($$0) {
                    Minecraft.getInstance().gameRenderer.overlayTexture().setupOverlayColor();
                }
            }, () -> {
                if ($$0) {
                    Minecraft.getInstance().gameRenderer.overlayTexture().teardownOverlayColor();
                }
            }, $$0);
        }
    }

    protected static class CullStateShard
    extends BooleanStateShard {
        public CullStateShard(boolean $$0) {
            super("cull", () -> {
                if (!$$0) {
                    RenderSystem.disableCull();
                }
            }, () -> {
                if (!$$0) {
                    RenderSystem.enableCull();
                }
            }, $$0);
        }
    }

    protected static class DepthTestStateShard
    extends RenderStateShard {
        private final String functionName;

        public DepthTestStateShard(String $$0, int $$1) {
            super("depth_test", () -> {
                if ($$1 != 519) {
                    RenderSystem.enableDepthTest();
                    RenderSystem.depthFunc($$1);
                }
            }, () -> {
                if ($$1 != 519) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.depthFunc(515);
                }
            });
            this.functionName = $$0;
        }

        @Override
        public String toString() {
            return this.name + "[" + this.functionName + "]";
        }
    }

    protected static class WriteMaskStateShard
    extends RenderStateShard {
        private final boolean writeColor;
        private final boolean writeDepth;

        public WriteMaskStateShard(boolean $$0, boolean $$1) {
            super("write_mask_state", () -> {
                if (!$$1) {
                    RenderSystem.depthMask($$1);
                }
                if (!$$0) {
                    RenderSystem.colorMask($$0, $$0, $$0, $$0);
                }
            }, () -> {
                if (!$$1) {
                    RenderSystem.depthMask(true);
                }
                if (!$$0) {
                    RenderSystem.colorMask(true, true, true, true);
                }
            });
            this.writeColor = $$0;
            this.writeDepth = $$1;
        }

        @Override
        public String toString() {
            return this.name + "[writeColor=" + this.writeColor + ", writeDepth=" + this.writeDepth + "]";
        }
    }

    protected static class LayeringStateShard
    extends RenderStateShard {
        public LayeringStateShard(String $$0, Runnable $$1, Runnable $$2) {
            super($$0, $$1, $$2);
        }
    }

    protected static class OutputStateShard
    extends RenderStateShard {
        public OutputStateShard(String $$0, Runnable $$1, Runnable $$2) {
            super($$0, $$1, $$2);
        }
    }

    protected static class LineStateShard
    extends RenderStateShard {
        private final OptionalDouble width;

        public LineStateShard(OptionalDouble $$0) {
            super("line_width", () -> {
                if (!Objects.equals((Object)$$0, (Object)OptionalDouble.of((double)1.0))) {
                    if ($$0.isPresent()) {
                        RenderSystem.lineWidth((float)$$0.getAsDouble());
                    } else {
                        RenderSystem.lineWidth(Math.max((float)2.5f, (float)((float)Minecraft.getInstance().getWindow().getWidth() / 1920.0f * 2.5f)));
                    }
                }
            }, () -> {
                if (!Objects.equals((Object)$$0, (Object)OptionalDouble.of((double)1.0))) {
                    RenderSystem.lineWidth(1.0f);
                }
            });
            this.width = $$0;
        }

        @Override
        public String toString() {
            return this.name + "[" + (Serializable)(this.width.isPresent() ? Double.valueOf((double)this.width.getAsDouble()) : "window_scale") + "]";
        }
    }

    static class BooleanStateShard
    extends RenderStateShard {
        private final boolean enabled;

        public BooleanStateShard(String $$0, Runnable $$1, Runnable $$2, boolean $$3) {
            super($$0, $$1, $$2);
            this.enabled = $$3;
        }

        @Override
        public String toString() {
            return this.name + "[" + this.enabled + "]";
        }
    }

    protected static final class OffsetTexturingStateShard
    extends TexturingStateShard {
        public OffsetTexturingStateShard(float $$0, float $$1) {
            super("offset_texturing", () -> RenderSystem.setTextureMatrix(new Matrix4f().translation($$0, $$1, 0.0f)), () -> RenderSystem.resetTextureMatrix());
        }
    }

    protected static class MultiTextureStateShard
    extends EmptyTextureStateShard {
        private final Optional<ResourceLocation> cutoutTexture;

        MultiTextureStateShard(ImmutableList<Triple<ResourceLocation, Boolean, Boolean>> $$0) {
            super(() -> {
                int $$1 = 0;
                for (Triple $$2 : $$0) {
                    TextureManager $$3 = Minecraft.getInstance().getTextureManager();
                    $$3.getTexture((ResourceLocation)$$2.getLeft()).setFilter((Boolean)$$2.getMiddle(), (Boolean)$$2.getRight());
                    RenderSystem.setShaderTexture($$1++, (ResourceLocation)$$2.getLeft());
                }
            }, () -> {});
            this.cutoutTexture = $$0.stream().findFirst().map(Triple::getLeft);
        }

        @Override
        protected Optional<ResourceLocation> cutoutTexture() {
            return this.cutoutTexture;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private final ImmutableList.Builder<Triple<ResourceLocation, Boolean, Boolean>> builder = new ImmutableList.Builder();

            public Builder add(ResourceLocation $$0, boolean $$1, boolean $$2) {
                this.builder.add((Object)Triple.of((Object)$$0, (Object)$$1, (Object)$$2));
                return this;
            }

            public MultiTextureStateShard build() {
                return new MultiTextureStateShard((ImmutableList<Triple<ResourceLocation, Boolean, Boolean>>)this.builder.build());
            }
        }
    }
}
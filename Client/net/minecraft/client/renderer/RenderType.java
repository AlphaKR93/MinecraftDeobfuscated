/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.util.List
 *  java.util.Optional
 *  java.util.OptionalDouble
 *  java.util.function.BiFunction
 *  java.util.function.Function
 */
package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;

public abstract class RenderType
extends RenderStateShard {
    private static final int BYTES_IN_INT = 4;
    private static final int MEGABYTE = 0x100000;
    public static final int BIG_BUFFER_SIZE = 0x200000;
    public static final int MEDIUM_BUFFER_SIZE = 262144;
    public static final int SMALL_BUFFER_SIZE = 131072;
    public static final int TRANSIENT_BUFFER_SIZE = 256;
    private static final RenderType SOLID = RenderType.create("solid", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 0x200000, true, false, CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_SOLID_SHADER).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true));
    private static final RenderType CUTOUT_MIPPED = RenderType.create("cutout_mipped", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 131072, true, false, CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_CUTOUT_MIPPED_SHADER).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true));
    private static final RenderType CUTOUT = RenderType.create("cutout", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 131072, true, false, CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_CUTOUT_SHADER).setTextureState(BLOCK_SHEET).createCompositeState(true));
    private static final RenderType TRANSLUCENT = RenderType.create("translucent", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 0x200000, true, true, RenderType.translucentState(RENDERTYPE_TRANSLUCENT_SHADER));
    private static final RenderType TRANSLUCENT_MOVING_BLOCK = RenderType.create("translucent_moving_block", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 262144, false, true, RenderType.translucentMovingBlockState());
    private static final RenderType TRANSLUCENT_NO_CRUMBLING = RenderType.create("translucent_no_crumbling", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 262144, false, true, RenderType.translucentState(RENDERTYPE_TRANSLUCENT_NO_CRUMBLING_SHADER));
    private static final Function<ResourceLocation, RenderType> ARMOR_CUTOUT_NO_CULL = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setShaderState(RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(true);
        return RenderType.create("armor_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, $$1);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_SOLID = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return RenderType.create("entity_solid", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, $$1);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return RenderType.create("entity_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, $$1);
    });
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL = Util.memoize(($$0, $$1) -> {
        CompositeState $$2 = CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState((boolean)$$1);
        return RenderType.create("entity_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, $$2);
    });
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL_Z_OFFSET = Util.memoize(($$0, $$1) -> {
        CompositeState $$2 = CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState((boolean)$$1);
        return RenderType.create("entity_cutout_no_cull_z_offset", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, $$2);
    });
    private static final Function<ResourceLocation, RenderType> ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setShaderState(RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).createCompositeState(true);
        return RenderType.create("item_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, $$1);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_CULL = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return RenderType.create("entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, $$1);
    });
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT = Util.memoize(($$0, $$1) -> {
        CompositeState $$2 = CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState((boolean)$$1);
        return RenderType.create("entity_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, $$2);
    });
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize(($$0, $$1) -> {
        CompositeState $$2 = CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).setOverlayState(OVERLAY).createCompositeState((boolean)$$1);
        return RenderType.create("entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, $$2);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_SMOOTH_CUTOUT = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_SMOOTH_CUTOUT_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setCullState(NO_CULL).setLightmapState(LIGHTMAP).createCompositeState(true);
        return RenderType.create("entity_smooth_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, $$1);
    });
    private static final BiFunction<ResourceLocation, Boolean, RenderType> BEACON_BEAM = Util.memoize(($$0, $$1) -> {
        CompositeState $$2 = CompositeState.builder().setShaderState(RENDERTYPE_BEACON_BEAM_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState($$1 != false ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY).setWriteMaskState($$1 != false ? COLOR_WRITE : COLOR_DEPTH_WRITE).createCompositeState(false);
        return RenderType.create("beacon_beam", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, true, $$2);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_DECAL = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_DECAL_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setDepthTestState(EQUAL_DEPTH_TEST).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false);
        return RenderType.create("entity_decal", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, $$1);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_NO_OUTLINE = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_NO_OUTLINE_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).createCompositeState(false);
        return RenderType.create("entity_no_outline", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, $$1);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_SHADOW = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_SHADOW_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).setDepthTestState(LEQUAL_DEPTH_TEST).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false);
        return RenderType.create("entity_shadow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, $$1);
    });
    private static final Function<ResourceLocation, RenderType> DRAGON_EXPLOSION_ALPHA = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_ALPHA_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setCullState(NO_CULL).createCompositeState(true);
        return RenderType.create("entity_alpha", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, $$1);
    });
    private static final Function<ResourceLocation, RenderType> EYES = Util.memoize($$0 -> {
        RenderStateShard.TextureStateShard $$1 = new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false);
        return RenderType.create("eyes", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder().setShaderState(RENDERTYPE_EYES_SHADER).setTextureState($$1).setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
    });
    private static final RenderType LEASH = RenderType.create("leash", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.TRIANGLE_STRIP, 256, CompositeState.builder().setShaderState(RENDERTYPE_LEASH_SHADER).setTextureState(NO_TEXTURE).setCullState(NO_CULL).setLightmapState(LIGHTMAP).createCompositeState(false));
    private static final RenderType WATER_MASK = RenderType.create("water_mask", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, CompositeState.builder().setShaderState(RENDERTYPE_WATER_MASK_SHADER).setTextureState(NO_TEXTURE).setWriteMaskState(DEPTH_WRITE).createCompositeState(false));
    private static final RenderType ARMOR_GLINT = RenderType.create("armor_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, CompositeState.builder().setShaderState(RENDERTYPE_ARMOR_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
    private static final RenderType ARMOR_ENTITY_GLINT = RenderType.create("armor_entity_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, CompositeState.builder().setShaderState(RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
    private static final RenderType GLINT_TRANSLUCENT = RenderType.create("glint_translucent", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, CompositeState.builder().setShaderState(RENDERTYPE_GLINT_TRANSLUCENT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(false));
    private static final RenderType GLINT = RenderType.create("glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, CompositeState.builder().setShaderState(RENDERTYPE_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).createCompositeState(false));
    private static final RenderType GLINT_DIRECT = RenderType.create("glint_direct", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, CompositeState.builder().setShaderState(RENDERTYPE_GLINT_DIRECT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).createCompositeState(false));
    private static final RenderType ENTITY_GLINT = RenderType.create("entity_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));
    private static final RenderType ENTITY_GLINT_DIRECT = RenderType.create("entity_glint_direct", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));
    private static final Function<ResourceLocation, RenderType> CRUMBLING = Util.memoize($$0 -> {
        RenderStateShard.TextureStateShard $$1 = new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false);
        return RenderType.create("crumbling", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder().setShaderState(RENDERTYPE_CRUMBLING_SHADER).setTextureState($$1).setTransparencyState(CRUMBLING_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setLayeringState(POLYGON_OFFSET_LAYERING).createCompositeState(false));
    });
    private static final Function<ResourceLocation, RenderType> TEXT = Util.memoize($$0 -> RenderType.create("text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder().setShaderState(RENDERTYPE_TEXT_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY = Util.memoize($$0 -> RenderType.create("text_intensity", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder().setShaderState(RENDERTYPE_TEXT_INTENSITY_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> TEXT_POLYGON_OFFSET = Util.memoize($$0 -> RenderType.create("text_polygon_offset", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder().setShaderState(RENDERTYPE_TEXT_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setLayeringState(POLYGON_OFFSET_LAYERING).createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize($$0 -> RenderType.create("text_intensity_polygon_offset", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder().setShaderState(RENDERTYPE_TEXT_INTENSITY_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setLayeringState(POLYGON_OFFSET_LAYERING).createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> TEXT_SEE_THROUGH = Util.memoize($$0 -> RenderType.create("text_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder().setShaderState(RENDERTYPE_TEXT_SEE_THROUGH_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setDepthTestState(NO_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY_SEE_THROUGH = Util.memoize($$0 -> RenderType.create("text_intensity_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder().setShaderState(RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setDepthTestState(NO_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false)));
    private static final RenderType LIGHTNING = RenderType.create("lightning", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setWriteMaskState(COLOR_DEPTH_WRITE).setTransparencyState(LIGHTNING_TRANSPARENCY).setOutputState(WEATHER_TARGET).createCompositeState(false));
    private static final RenderType TRIPWIRE = RenderType.create("tripwire", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 262144, true, true, RenderType.tripwireState());
    private static final RenderType END_PORTAL = RenderType.create("end_portal", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder().setShaderState(RENDERTYPE_END_PORTAL_SHADER).setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(TheEndPortalRenderer.END_SKY_LOCATION, false, false).add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build()).createCompositeState(false));
    private static final RenderType END_GATEWAY = RenderType.create("end_gateway", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder().setShaderState(RENDERTYPE_END_GATEWAY_SHADER).setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(TheEndPortalRenderer.END_SKY_LOCATION, false, false).add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build()).createCompositeState(false));
    public static final CompositeRenderType LINES = RenderType.create("lines", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 256, CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(VIEW_OFFSET_Z_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).createCompositeState(false));
    public static final CompositeRenderType LINE_STRIP = RenderType.create("line_strip", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINE_STRIP, 256, CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(VIEW_OFFSET_Z_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).createCompositeState(false));
    private static final ImmutableList<RenderType> CHUNK_BUFFER_LAYERS = ImmutableList.of((Object)RenderType.solid(), (Object)RenderType.cutoutMipped(), (Object)RenderType.cutout(), (Object)RenderType.translucent(), (Object)RenderType.tripwire());
    private final VertexFormat format;
    private final VertexFormat.Mode mode;
    private final int bufferSize;
    private final boolean affectsCrumbling;
    private final boolean sortOnUpload;
    private final Optional<RenderType> asOptional;

    public static RenderType solid() {
        return SOLID;
    }

    public static RenderType cutoutMipped() {
        return CUTOUT_MIPPED;
    }

    public static RenderType cutout() {
        return CUTOUT;
    }

    private static CompositeState translucentState(RenderStateShard.ShaderStateShard $$0) {
        return CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState($$0).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(TRANSLUCENT_TARGET).createCompositeState(true);
    }

    public static RenderType translucent() {
        return TRANSLUCENT;
    }

    private static CompositeState translucentMovingBlockState() {
        return CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_TRANSLUCENT_MOVING_BLOCK_SHADER).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(true);
    }

    public static RenderType translucentMovingBlock() {
        return TRANSLUCENT_MOVING_BLOCK;
    }

    public static RenderType translucentNoCrumbling() {
        return TRANSLUCENT_NO_CRUMBLING;
    }

    public static RenderType armorCutoutNoCull(ResourceLocation $$0) {
        return (RenderType)ARMOR_CUTOUT_NO_CULL.apply((Object)$$0);
    }

    public static RenderType entitySolid(ResourceLocation $$0) {
        return (RenderType)ENTITY_SOLID.apply((Object)$$0);
    }

    public static RenderType entityCutout(ResourceLocation $$0) {
        return (RenderType)ENTITY_CUTOUT.apply((Object)$$0);
    }

    public static RenderType entityCutoutNoCull(ResourceLocation $$0, boolean $$1) {
        return (RenderType)ENTITY_CUTOUT_NO_CULL.apply((Object)$$0, (Object)$$1);
    }

    public static RenderType entityCutoutNoCull(ResourceLocation $$0) {
        return RenderType.entityCutoutNoCull($$0, true);
    }

    public static RenderType entityCutoutNoCullZOffset(ResourceLocation $$0, boolean $$1) {
        return (RenderType)ENTITY_CUTOUT_NO_CULL_Z_OFFSET.apply((Object)$$0, (Object)$$1);
    }

    public static RenderType entityCutoutNoCullZOffset(ResourceLocation $$0) {
        return RenderType.entityCutoutNoCullZOffset($$0, true);
    }

    public static RenderType itemEntityTranslucentCull(ResourceLocation $$0) {
        return (RenderType)ITEM_ENTITY_TRANSLUCENT_CULL.apply((Object)$$0);
    }

    public static RenderType entityTranslucentCull(ResourceLocation $$0) {
        return (RenderType)ENTITY_TRANSLUCENT_CULL.apply((Object)$$0);
    }

    public static RenderType entityTranslucent(ResourceLocation $$0, boolean $$1) {
        return (RenderType)ENTITY_TRANSLUCENT.apply((Object)$$0, (Object)$$1);
    }

    public static RenderType entityTranslucent(ResourceLocation $$0) {
        return RenderType.entityTranslucent($$0, true);
    }

    public static RenderType entityTranslucentEmissive(ResourceLocation $$0, boolean $$1) {
        return (RenderType)ENTITY_TRANSLUCENT_EMISSIVE.apply((Object)$$0, (Object)$$1);
    }

    public static RenderType entityTranslucentEmissive(ResourceLocation $$0) {
        return RenderType.entityTranslucentEmissive($$0, true);
    }

    public static RenderType entitySmoothCutout(ResourceLocation $$0) {
        return (RenderType)ENTITY_SMOOTH_CUTOUT.apply((Object)$$0);
    }

    public static RenderType beaconBeam(ResourceLocation $$0, boolean $$1) {
        return (RenderType)BEACON_BEAM.apply((Object)$$0, (Object)$$1);
    }

    public static RenderType entityDecal(ResourceLocation $$0) {
        return (RenderType)ENTITY_DECAL.apply((Object)$$0);
    }

    public static RenderType entityNoOutline(ResourceLocation $$0) {
        return (RenderType)ENTITY_NO_OUTLINE.apply((Object)$$0);
    }

    public static RenderType entityShadow(ResourceLocation $$0) {
        return (RenderType)ENTITY_SHADOW.apply((Object)$$0);
    }

    public static RenderType dragonExplosionAlpha(ResourceLocation $$0) {
        return (RenderType)DRAGON_EXPLOSION_ALPHA.apply((Object)$$0);
    }

    public static RenderType eyes(ResourceLocation $$0) {
        return (RenderType)EYES.apply((Object)$$0);
    }

    public static RenderType energySwirl(ResourceLocation $$0, float $$1, float $$2) {
        return RenderType.create("energy_swirl", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder().setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new RenderStateShard.TextureStateShard($$0, false, false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard($$1, $$2)).setTransparencyState(ADDITIVE_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }

    public static RenderType leash() {
        return LEASH;
    }

    public static RenderType waterMask() {
        return WATER_MASK;
    }

    public static RenderType outline(ResourceLocation $$0) {
        return (RenderType)CompositeRenderType.OUTLINE.apply((Object)$$0, (Object)NO_CULL);
    }

    public static RenderType armorGlint() {
        return ARMOR_GLINT;
    }

    public static RenderType armorEntityGlint() {
        return ARMOR_ENTITY_GLINT;
    }

    public static RenderType glintTranslucent() {
        return GLINT_TRANSLUCENT;
    }

    public static RenderType glint() {
        return GLINT;
    }

    public static RenderType glintDirect() {
        return GLINT_DIRECT;
    }

    public static RenderType entityGlint() {
        return ENTITY_GLINT;
    }

    public static RenderType entityGlintDirect() {
        return ENTITY_GLINT_DIRECT;
    }

    public static RenderType crumbling(ResourceLocation $$0) {
        return (RenderType)CRUMBLING.apply((Object)$$0);
    }

    public static RenderType text(ResourceLocation $$0) {
        return (RenderType)TEXT.apply((Object)$$0);
    }

    public static RenderType textIntensity(ResourceLocation $$0) {
        return (RenderType)TEXT_INTENSITY.apply((Object)$$0);
    }

    public static RenderType textPolygonOffset(ResourceLocation $$0) {
        return (RenderType)TEXT_POLYGON_OFFSET.apply((Object)$$0);
    }

    public static RenderType textIntensityPolygonOffset(ResourceLocation $$0) {
        return (RenderType)TEXT_INTENSITY_POLYGON_OFFSET.apply((Object)$$0);
    }

    public static RenderType textSeeThrough(ResourceLocation $$0) {
        return (RenderType)TEXT_SEE_THROUGH.apply((Object)$$0);
    }

    public static RenderType textIntensitySeeThrough(ResourceLocation $$0) {
        return (RenderType)TEXT_INTENSITY_SEE_THROUGH.apply((Object)$$0);
    }

    public static RenderType lightning() {
        return LIGHTNING;
    }

    private static CompositeState tripwireState() {
        return CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_TRIPWIRE_SHADER).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(WEATHER_TARGET).createCompositeState(true);
    }

    public static RenderType tripwire() {
        return TRIPWIRE;
    }

    public static RenderType endPortal() {
        return END_PORTAL;
    }

    public static RenderType endGateway() {
        return END_GATEWAY;
    }

    public static RenderType lines() {
        return LINES;
    }

    public static RenderType lineStrip() {
        return LINE_STRIP;
    }

    public RenderType(String $$0, VertexFormat $$1, VertexFormat.Mode $$2, int $$3, boolean $$4, boolean $$5, Runnable $$6, Runnable $$7) {
        super($$0, $$6, $$7);
        this.format = $$1;
        this.mode = $$2;
        this.bufferSize = $$3;
        this.affectsCrumbling = $$4;
        this.sortOnUpload = $$5;
        this.asOptional = Optional.of((Object)this);
    }

    static CompositeRenderType create(String $$0, VertexFormat $$1, VertexFormat.Mode $$2, int $$3, CompositeState $$4) {
        return RenderType.create($$0, $$1, $$2, $$3, false, false, $$4);
    }

    private static CompositeRenderType create(String $$0, VertexFormat $$1, VertexFormat.Mode $$2, int $$3, boolean $$4, boolean $$5, CompositeState $$6) {
        return new CompositeRenderType($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    public void end(BufferBuilder $$0, int $$1, int $$2, int $$3) {
        if (!$$0.building()) {
            return;
        }
        if (this.sortOnUpload) {
            $$0.setQuadSortOrigin($$1, $$2, $$3);
        }
        BufferBuilder.RenderedBuffer $$4 = $$0.end();
        this.setupRenderState();
        BufferUploader.drawWithShader($$4);
        this.clearRenderState();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static List<RenderType> chunkBufferLayers() {
        return CHUNK_BUFFER_LAYERS;
    }

    public int bufferSize() {
        return this.bufferSize;
    }

    public VertexFormat format() {
        return this.format;
    }

    public VertexFormat.Mode mode() {
        return this.mode;
    }

    public Optional<RenderType> outline() {
        return Optional.empty();
    }

    public boolean isOutline() {
        return false;
    }

    public boolean affectsCrumbling() {
        return this.affectsCrumbling;
    }

    public boolean canConsolidateConsecutiveGeometry() {
        return !this.mode.connectedPrimitives;
    }

    public Optional<RenderType> asOptional() {
        return this.asOptional;
    }

    protected static final class CompositeState {
        final RenderStateShard.EmptyTextureStateShard textureState;
        private final RenderStateShard.ShaderStateShard shaderState;
        private final RenderStateShard.TransparencyStateShard transparencyState;
        private final RenderStateShard.DepthTestStateShard depthTestState;
        final RenderStateShard.CullStateShard cullState;
        private final RenderStateShard.LightmapStateShard lightmapState;
        private final RenderStateShard.OverlayStateShard overlayState;
        private final RenderStateShard.LayeringStateShard layeringState;
        private final RenderStateShard.OutputStateShard outputState;
        private final RenderStateShard.TexturingStateShard texturingState;
        private final RenderStateShard.WriteMaskStateShard writeMaskState;
        private final RenderStateShard.LineStateShard lineState;
        final OutlineProperty outlineProperty;
        final ImmutableList<RenderStateShard> states;

        CompositeState(RenderStateShard.EmptyTextureStateShard $$0, RenderStateShard.ShaderStateShard $$1, RenderStateShard.TransparencyStateShard $$2, RenderStateShard.DepthTestStateShard $$3, RenderStateShard.CullStateShard $$4, RenderStateShard.LightmapStateShard $$5, RenderStateShard.OverlayStateShard $$6, RenderStateShard.LayeringStateShard $$7, RenderStateShard.OutputStateShard $$8, RenderStateShard.TexturingStateShard $$9, RenderStateShard.WriteMaskStateShard $$10, RenderStateShard.LineStateShard $$11, OutlineProperty $$12) {
            this.textureState = $$0;
            this.shaderState = $$1;
            this.transparencyState = $$2;
            this.depthTestState = $$3;
            this.cullState = $$4;
            this.lightmapState = $$5;
            this.overlayState = $$6;
            this.layeringState = $$7;
            this.outputState = $$8;
            this.texturingState = $$9;
            this.writeMaskState = $$10;
            this.lineState = $$11;
            this.outlineProperty = $$12;
            this.states = ImmutableList.of((Object)this.textureState, (Object)this.shaderState, (Object)this.transparencyState, (Object)this.depthTestState, (Object)this.cullState, (Object)this.lightmapState, (Object)this.overlayState, (Object)this.layeringState, (Object)this.outputState, (Object)this.texturingState, (Object)this.writeMaskState, (Object)this.lineState, (Object[])new RenderStateShard[0]);
        }

        public String toString() {
            return "CompositeState[" + this.states + ", outlineProperty=" + this.outlineProperty + "]";
        }

        public static CompositeStateBuilder builder() {
            return new CompositeStateBuilder();
        }

        public static class CompositeStateBuilder {
            private RenderStateShard.EmptyTextureStateShard textureState = RenderStateShard.NO_TEXTURE;
            private RenderStateShard.ShaderStateShard shaderState = RenderStateShard.NO_SHADER;
            private RenderStateShard.TransparencyStateShard transparencyState = RenderStateShard.NO_TRANSPARENCY;
            private RenderStateShard.DepthTestStateShard depthTestState = RenderStateShard.LEQUAL_DEPTH_TEST;
            private RenderStateShard.CullStateShard cullState = RenderStateShard.CULL;
            private RenderStateShard.LightmapStateShard lightmapState = RenderStateShard.NO_LIGHTMAP;
            private RenderStateShard.OverlayStateShard overlayState = RenderStateShard.NO_OVERLAY;
            private RenderStateShard.LayeringStateShard layeringState = RenderStateShard.NO_LAYERING;
            private RenderStateShard.OutputStateShard outputState = RenderStateShard.MAIN_TARGET;
            private RenderStateShard.TexturingStateShard texturingState = RenderStateShard.DEFAULT_TEXTURING;
            private RenderStateShard.WriteMaskStateShard writeMaskState = RenderStateShard.COLOR_DEPTH_WRITE;
            private RenderStateShard.LineStateShard lineState = RenderStateShard.DEFAULT_LINE;

            CompositeStateBuilder() {
            }

            public CompositeStateBuilder setTextureState(RenderStateShard.EmptyTextureStateShard $$0) {
                this.textureState = $$0;
                return this;
            }

            public CompositeStateBuilder setShaderState(RenderStateShard.ShaderStateShard $$0) {
                this.shaderState = $$0;
                return this;
            }

            public CompositeStateBuilder setTransparencyState(RenderStateShard.TransparencyStateShard $$0) {
                this.transparencyState = $$0;
                return this;
            }

            public CompositeStateBuilder setDepthTestState(RenderStateShard.DepthTestStateShard $$0) {
                this.depthTestState = $$0;
                return this;
            }

            public CompositeStateBuilder setCullState(RenderStateShard.CullStateShard $$0) {
                this.cullState = $$0;
                return this;
            }

            public CompositeStateBuilder setLightmapState(RenderStateShard.LightmapStateShard $$0) {
                this.lightmapState = $$0;
                return this;
            }

            public CompositeStateBuilder setOverlayState(RenderStateShard.OverlayStateShard $$0) {
                this.overlayState = $$0;
                return this;
            }

            public CompositeStateBuilder setLayeringState(RenderStateShard.LayeringStateShard $$0) {
                this.layeringState = $$0;
                return this;
            }

            public CompositeStateBuilder setOutputState(RenderStateShard.OutputStateShard $$0) {
                this.outputState = $$0;
                return this;
            }

            public CompositeStateBuilder setTexturingState(RenderStateShard.TexturingStateShard $$0) {
                this.texturingState = $$0;
                return this;
            }

            public CompositeStateBuilder setWriteMaskState(RenderStateShard.WriteMaskStateShard $$0) {
                this.writeMaskState = $$0;
                return this;
            }

            public CompositeStateBuilder setLineState(RenderStateShard.LineStateShard $$0) {
                this.lineState = $$0;
                return this;
            }

            public CompositeState createCompositeState(boolean $$0) {
                return this.createCompositeState($$0 ? OutlineProperty.AFFECTS_OUTLINE : OutlineProperty.NONE);
            }

            public CompositeState createCompositeState(OutlineProperty $$0) {
                return new CompositeState(this.textureState, this.shaderState, this.transparencyState, this.depthTestState, this.cullState, this.lightmapState, this.overlayState, this.layeringState, this.outputState, this.texturingState, this.writeMaskState, this.lineState, $$0);
            }
        }
    }

    static final class CompositeRenderType
    extends RenderType {
        static final BiFunction<ResourceLocation, RenderStateShard.CullStateShard, RenderType> OUTLINE = Util.memoize(($$0, $$1) -> RenderType.create("outline", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, CompositeState.builder().setShaderState(RENDERTYPE_OUTLINE_SHADER).setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false, false)).setCullState((RenderStateShard.CullStateShard)$$1).setDepthTestState(NO_DEPTH_TEST).setOutputState(OUTLINE_TARGET).createCompositeState(OutlineProperty.IS_OUTLINE)));
        private final CompositeState state;
        private final Optional<RenderType> outline;
        private final boolean isOutline;

        CompositeRenderType(String $$0, VertexFormat $$12, VertexFormat.Mode $$2, int $$3, boolean $$4, boolean $$5, CompositeState $$6) {
            super($$0, $$12, $$2, $$3, $$4, $$5, () -> $$0.states.forEach(RenderStateShard::setupRenderState), () -> $$0.states.forEach(RenderStateShard::clearRenderState));
            this.state = $$6;
            this.outline = $$6.outlineProperty == OutlineProperty.AFFECTS_OUTLINE ? $$6.textureState.cutoutTexture().map($$1 -> (RenderType)OUTLINE.apply($$1, (Object)$$0.cullState)) : Optional.empty();
            this.isOutline = $$6.outlineProperty == OutlineProperty.IS_OUTLINE;
        }

        @Override
        public Optional<RenderType> outline() {
            return this.outline;
        }

        @Override
        public boolean isOutline() {
            return this.isOutline;
        }

        protected final CompositeState state() {
            return this.state;
        }

        @Override
        public String toString() {
            return "RenderType[" + this.name + ":" + this.state + "]";
        }
    }

    static enum OutlineProperty {
        NONE("none"),
        IS_OUTLINE("is_outline"),
        AFFECTS_OUTLINE("affects_outline");

        private final String name;

        private OutlineProperty(String $$0) {
            this.name = $$0;
        }

        public String toString() {
            return this.name;
        }
    }
}
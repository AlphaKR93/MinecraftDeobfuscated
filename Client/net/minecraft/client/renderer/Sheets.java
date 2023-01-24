/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 */
package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class Sheets {
    public static final ResourceLocation SHULKER_SHEET = new ResourceLocation("textures/atlas/shulker_boxes.png");
    public static final ResourceLocation BED_SHEET = new ResourceLocation("textures/atlas/beds.png");
    public static final ResourceLocation BANNER_SHEET = new ResourceLocation("textures/atlas/banner_patterns.png");
    public static final ResourceLocation SHIELD_SHEET = new ResourceLocation("textures/atlas/shield_patterns.png");
    public static final ResourceLocation SIGN_SHEET = new ResourceLocation("textures/atlas/signs.png");
    public static final ResourceLocation CHEST_SHEET = new ResourceLocation("textures/atlas/chest.png");
    public static final ResourceLocation ARMOR_TRIMS_SHEET = new ResourceLocation("textures/atlas/armor_trims.png");
    private static final RenderType SHULKER_BOX_SHEET_TYPE = RenderType.entityCutoutNoCull(SHULKER_SHEET);
    private static final RenderType BED_SHEET_TYPE = RenderType.entitySolid(BED_SHEET);
    private static final RenderType BANNER_SHEET_TYPE = RenderType.entityNoOutline(BANNER_SHEET);
    private static final RenderType SHIELD_SHEET_TYPE = RenderType.entityNoOutline(SHIELD_SHEET);
    private static final RenderType SIGN_SHEET_TYPE = RenderType.entityCutoutNoCull(SIGN_SHEET);
    private static final RenderType CHEST_SHEET_TYPE = RenderType.entityCutout(CHEST_SHEET);
    private static final RenderType ARMOR_TRIMS_SHEET_TYPE = RenderType.armorCutoutNoCull(ARMOR_TRIMS_SHEET);
    private static final RenderType SOLID_BLOCK_SHEET = RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS);
    private static final RenderType CUTOUT_BLOCK_SHEET = RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS);
    private static final RenderType TRANSLUCENT_ITEM_CULL_BLOCK_SHEET = RenderType.itemEntityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);
    private static final RenderType TRANSLUCENT_CULL_BLOCK_SHEET = RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);
    public static final Material DEFAULT_SHULKER_TEXTURE_LOCATION = new Material(SHULKER_SHEET, new ResourceLocation("entity/shulker/shulker"));
    public static final List<Material> SHULKER_TEXTURE_LOCATION = (List)Stream.of((Object[])new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"}).map($$0 -> new Material(SHULKER_SHEET, new ResourceLocation("entity/shulker/shulker_" + $$0))).collect(ImmutableList.toImmutableList());
    public static final Map<WoodType, Material> SIGN_MATERIALS = (Map)WoodType.values().collect(Collectors.toMap((Function)Function.identity(), Sheets::createSignMaterial));
    public static final Map<WoodType, Material> HANGING_SIGN_MATERIALS = (Map)WoodType.values().collect(Collectors.toMap((Function)Function.identity(), Sheets::createHangingSignMaterial));
    public static final Map<ResourceKey<BannerPattern>, Material> BANNER_MATERIALS = (Map)BuiltInRegistries.BANNER_PATTERN.registryKeySet().stream().collect(Collectors.toMap((Function)Function.identity(), Sheets::createBannerMaterial));
    public static final Map<ResourceKey<BannerPattern>, Material> SHIELD_MATERIALS = (Map)BuiltInRegistries.BANNER_PATTERN.registryKeySet().stream().collect(Collectors.toMap((Function)Function.identity(), Sheets::createShieldMaterial));
    public static final Material[] BED_TEXTURES = (Material[])Arrays.stream((Object[])DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map($$0 -> new Material(BED_SHEET, new ResourceLocation("entity/bed/" + $$0.getName()))).toArray(Material[]::new);
    public static final Material CHEST_TRAP_LOCATION = Sheets.chestMaterial("trapped");
    public static final Material CHEST_TRAP_LOCATION_LEFT = Sheets.chestMaterial("trapped_left");
    public static final Material CHEST_TRAP_LOCATION_RIGHT = Sheets.chestMaterial("trapped_right");
    public static final Material CHEST_XMAS_LOCATION = Sheets.chestMaterial("christmas");
    public static final Material CHEST_XMAS_LOCATION_LEFT = Sheets.chestMaterial("christmas_left");
    public static final Material CHEST_XMAS_LOCATION_RIGHT = Sheets.chestMaterial("christmas_right");
    public static final Material CHEST_LOCATION = Sheets.chestMaterial("normal");
    public static final Material CHEST_LOCATION_LEFT = Sheets.chestMaterial("normal_left");
    public static final Material CHEST_LOCATION_RIGHT = Sheets.chestMaterial("normal_right");
    public static final Material ENDER_CHEST_LOCATION = Sheets.chestMaterial("ender");

    public static RenderType bannerSheet() {
        return BANNER_SHEET_TYPE;
    }

    public static RenderType shieldSheet() {
        return SHIELD_SHEET_TYPE;
    }

    public static RenderType bedSheet() {
        return BED_SHEET_TYPE;
    }

    public static RenderType shulkerBoxSheet() {
        return SHULKER_BOX_SHEET_TYPE;
    }

    public static RenderType signSheet() {
        return SIGN_SHEET_TYPE;
    }

    public static RenderType hangingSignSheet() {
        return SIGN_SHEET_TYPE;
    }

    public static RenderType chestSheet() {
        return CHEST_SHEET_TYPE;
    }

    public static RenderType armorTrimsSheet() {
        return ARMOR_TRIMS_SHEET_TYPE;
    }

    public static RenderType solidBlockSheet() {
        return SOLID_BLOCK_SHEET;
    }

    public static RenderType cutoutBlockSheet() {
        return CUTOUT_BLOCK_SHEET;
    }

    public static RenderType translucentItemSheet() {
        return TRANSLUCENT_ITEM_CULL_BLOCK_SHEET;
    }

    public static RenderType translucentCullBlockSheet() {
        return TRANSLUCENT_CULL_BLOCK_SHEET;
    }

    public static void getAllMaterials(Consumer<Material> $$0) {
        $$0.accept((Object)DEFAULT_SHULKER_TEXTURE_LOCATION);
        SHULKER_TEXTURE_LOCATION.forEach($$0);
        BANNER_MATERIALS.values().forEach($$0);
        SHIELD_MATERIALS.values().forEach($$0);
        SIGN_MATERIALS.values().forEach($$0);
        HANGING_SIGN_MATERIALS.values().forEach($$0);
        for (Material $$1 : BED_TEXTURES) {
            $$0.accept((Object)$$1);
        }
        $$0.accept((Object)CHEST_TRAP_LOCATION);
        $$0.accept((Object)CHEST_TRAP_LOCATION_LEFT);
        $$0.accept((Object)CHEST_TRAP_LOCATION_RIGHT);
        $$0.accept((Object)CHEST_XMAS_LOCATION);
        $$0.accept((Object)CHEST_XMAS_LOCATION_LEFT);
        $$0.accept((Object)CHEST_XMAS_LOCATION_RIGHT);
        $$0.accept((Object)CHEST_LOCATION);
        $$0.accept((Object)CHEST_LOCATION_LEFT);
        $$0.accept((Object)CHEST_LOCATION_RIGHT);
        $$0.accept((Object)ENDER_CHEST_LOCATION);
    }

    private static Material createSignMaterial(WoodType $$0) {
        return new Material(SIGN_SHEET, new ResourceLocation("entity/signs/" + $$0.name()));
    }

    private static Material createHangingSignMaterial(WoodType $$0) {
        return new Material(SIGN_SHEET, new ResourceLocation("entity/signs/hanging/" + $$0.name()));
    }

    public static Material getSignMaterial(WoodType $$0) {
        return (Material)SIGN_MATERIALS.get((Object)$$0);
    }

    public static Material getHangingSignMaterial(WoodType $$0) {
        return (Material)HANGING_SIGN_MATERIALS.get((Object)$$0);
    }

    private static Material createBannerMaterial(ResourceKey<BannerPattern> $$0) {
        return new Material(BANNER_SHEET, BannerPattern.location($$0, true));
    }

    public static Material getBannerMaterial(ResourceKey<BannerPattern> $$0) {
        return (Material)BANNER_MATERIALS.get($$0);
    }

    private static Material createShieldMaterial(ResourceKey<BannerPattern> $$0) {
        return new Material(SHIELD_SHEET, BannerPattern.location($$0, false));
    }

    public static Material getShieldMaterial(ResourceKey<BannerPattern> $$0) {
        return (Material)SHIELD_MATERIALS.get($$0);
    }

    private static Material chestMaterial(String $$0) {
        return new Material(CHEST_SHEET, new ResourceLocation("entity/chest/" + $$0));
    }

    public static Material chooseMaterial(BlockEntity $$0, ChestType $$1, boolean $$2) {
        if ($$0 instanceof EnderChestBlockEntity) {
            return ENDER_CHEST_LOCATION;
        }
        if ($$2) {
            return Sheets.chooseMaterial($$1, CHEST_XMAS_LOCATION, CHEST_XMAS_LOCATION_LEFT, CHEST_XMAS_LOCATION_RIGHT);
        }
        if ($$0 instanceof TrappedChestBlockEntity) {
            return Sheets.chooseMaterial($$1, CHEST_TRAP_LOCATION, CHEST_TRAP_LOCATION_LEFT, CHEST_TRAP_LOCATION_RIGHT);
        }
        return Sheets.chooseMaterial($$1, CHEST_LOCATION, CHEST_LOCATION_LEFT, CHEST_LOCATION_RIGHT);
    }

    private static Material chooseMaterial(ChestType $$0, Material $$1, Material $$2, Material $$3) {
        switch ($$0) {
            case LEFT: {
                return $$2;
            }
            case RIGHT: {
                return $$3;
            }
        }
        return $$1;
    }
}
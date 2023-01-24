/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Float
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.BiConsumer
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 */
package net.minecraft.data.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemModelGenerators {
    public static final ResourceLocation TRIM_TYPE_PREDICATE_ID = new ResourceLocation("trim_type");
    private static final List<TrimModelData> GENERATED_TRIM_MODELS = List.of((Object)((Object)new TrimModelData("quartz", 0.1f, (Optional<ArmorMaterials>)Optional.empty())), (Object)((Object)new TrimModelData("iron", 0.2f, (Optional<ArmorMaterials>)Optional.of((Object)ArmorMaterials.IRON))), (Object)((Object)new TrimModelData("netherite", 0.3f, (Optional<ArmorMaterials>)Optional.of((Object)ArmorMaterials.NETHERITE))), (Object)((Object)new TrimModelData("redstone", 0.4f, (Optional<ArmorMaterials>)Optional.empty())), (Object)((Object)new TrimModelData("copper", 0.5f, (Optional<ArmorMaterials>)Optional.empty())), (Object)((Object)new TrimModelData("gold", 0.6f, (Optional<ArmorMaterials>)Optional.of((Object)ArmorMaterials.GOLD))), (Object)((Object)new TrimModelData("emerald", 0.7f, (Optional<ArmorMaterials>)Optional.empty())), (Object)((Object)new TrimModelData("diamond", 0.8f, (Optional<ArmorMaterials>)Optional.of((Object)ArmorMaterials.DIAMOND))), (Object)((Object)new TrimModelData("lapis", 0.9f, (Optional<ArmorMaterials>)Optional.empty())), (Object)((Object)new TrimModelData("amethyst", 1.0f, (Optional<ArmorMaterials>)Optional.empty())));
    private final BiConsumer<ResourceLocation, Supplier<JsonElement>> output;

    public ItemModelGenerators(BiConsumer<ResourceLocation, Supplier<JsonElement>> $$0) {
        this.output = $$0;
    }

    private void generateFlatItem(Item $$0, ModelTemplate $$1) {
        $$1.create(ModelLocationUtils.getModelLocation($$0), TextureMapping.layer0($$0), this.output);
    }

    private void generateFlatItem(Item $$0, String $$1, ModelTemplate $$2) {
        $$2.create(ModelLocationUtils.getModelLocation($$0, $$1), TextureMapping.layer0(TextureMapping.getItemTexture($$0, $$1)), this.output);
    }

    private void generateFlatItem(Item $$0, Item $$1, ModelTemplate $$2) {
        $$2.create(ModelLocationUtils.getModelLocation($$0), TextureMapping.layer0($$1), this.output);
    }

    private void generateCompassItem(Item $$0) {
        for (int $$1 = 0; $$1 < 32; ++$$1) {
            if ($$1 == 16) continue;
            this.generateFlatItem($$0, String.format((Locale)Locale.ROOT, (String)"_%02d", (Object[])new Object[]{$$1}), ModelTemplates.FLAT_ITEM);
        }
    }

    private void generateClockItem(Item $$0) {
        for (int $$1 = 1; $$1 < 64; ++$$1) {
            this.generateFlatItem($$0, String.format((Locale)Locale.ROOT, (String)"_%02d", (Object[])new Object[]{$$1}), ModelTemplates.FLAT_ITEM);
        }
    }

    private void generateLayeredItem(ResourceLocation $$0, ResourceLocation $$1, ResourceLocation $$2) {
        ModelTemplates.LAYERED_ITEM.create($$0, TextureMapping.layered($$1, $$2), this.output);
    }

    private ResourceLocation getItemModelForTrimMaterial(ResourceLocation $$0, String $$1) {
        return $$0.withSuffix("_" + $$1 + "_trim");
    }

    private List<TrimModelData> getCompatibleTrimModels(ArmorItem $$0) {
        return (List)GENERATED_TRIM_MODELS.stream().filter($$1 -> $$1.incompatibleArmorMaterial().isEmpty() || $$1.incompatibleArmorMaterial().get() != $$0.getMaterial()).collect(Collectors.toList());
    }

    private JsonObject generateBaseArmorTrimTemplate(ResourceLocation $$0, Map<TextureSlot, ResourceLocation> $$1, List<TrimModelData> $$2) {
        JsonObject $$3 = ModelTemplates.LAYERED_ITEM.createBaseTemplate($$0, $$1);
        JsonArray $$4 = new JsonArray();
        for (TrimModelData $$5 : $$2) {
            JsonObject $$6 = new JsonObject();
            JsonObject $$7 = new JsonObject();
            $$7.addProperty(TRIM_TYPE_PREDICATE_ID.getPath(), (Number)Float.valueOf((float)$$5.itemModelIndex()));
            $$6.add("predicate", (JsonElement)$$7);
            $$6.addProperty("model", this.getItemModelForTrimMaterial($$0, $$5.name()).toString());
            $$4.add((JsonElement)$$6);
        }
        $$3.add("overrides", (JsonElement)$$4);
        return $$3;
    }

    private void generateArmorTrims(ArmorItem $$0) {
        List<TrimModelData> $$12 = this.getCompatibleTrimModels($$0);
        ResourceLocation $$22 = ModelLocationUtils.getModelLocation($$0);
        ModelTemplates.FLAT_ITEM.create($$22, TextureMapping.layer0(TextureMapping.getItemTexture($$0)), this.output, ($$1, $$2) -> this.generateBaseArmorTrimTemplate($$1, (Map<TextureSlot, ResourceLocation>)$$2, $$12));
        for (TrimModelData $$3 : $$12) {
            ResourceLocation $$4 = this.getItemModelForTrimMaterial($$22, $$3.name());
            String $$5 = $$0.getType().getName() + "_trim_" + $$3.name();
            this.generateLayeredItem($$4, TextureMapping.getItemTexture($$0), new ResourceLocation($$5).withPrefix("trims/items/"));
        }
    }

    public void run() {
        this.generateFlatItem(Items.ACACIA_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ACACIA_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.AMETHYST_SHARD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.APPLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ARMOR_STAND, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ARROW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BAKED_POTATO, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BAMBOO, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.BEEF, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BEETROOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BEETROOT_SOUP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BIRCH_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BIRCH_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLACK_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLAZE_POWDER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLAZE_ROD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.BLUE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BONE_MEAL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BOWL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BREAD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BRICK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BROWN_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CARROT_ON_A_STICK, ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
        this.generateFlatItem(Items.WARPED_FUNGUS_ON_A_STICK, ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
        this.generateFlatItem(Items.CHARCOAL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHEST_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHICKEN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHORUS_FRUIT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CLAY_BALL, ModelTemplates.FLAT_ITEM);
        this.generateClockItem(Items.CLOCK);
        this.generateFlatItem(Items.COAL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COD_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COMMAND_BLOCK_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateCompassItem(Items.COMPASS);
        this.generateCompassItem(Items.RECOVERY_COMPASS);
        this.generateFlatItem(Items.COOKED_BEEF, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_CHICKEN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_COD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_MUTTON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_PORKCHOP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_RABBIT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_SALMON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKIE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RAW_COPPER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COPPER_INGOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CREEPER_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CYAN_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DARK_OAK_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DARK_OAK_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DIAMOND, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DIAMOND_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DIAMOND_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DIAMOND_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DIAMOND_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DIAMOND_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DIAMOND_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DRAGON_BREATH, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DRIED_KELP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EMERALD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ENCHANTED_BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ENDER_EYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ENDER_PEARL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.END_CRYSTAL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EXPERIENCE_BOTTLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FERMENTED_SPIDER_EYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FIREWORK_ROCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FIRE_CHARGE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLINT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLINT_AND_STEEL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLOWER_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FURNACE_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GHAST_TEAR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLASS_BOTTLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLISTERING_MELON_SLICE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOBE_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOW_BERRIES, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOWSTONE_DUST, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOW_INK_SAC, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOW_ITEM_FRAME, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RAW_GOLD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLDEN_APPLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLDEN_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLDEN_CARROT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLDEN_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLDEN_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLDEN_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLDEN_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLDEN_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLD_INGOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLD_NUGGET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GRAY_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GREEN_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GUNPOWDER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HEART_OF_THE_SEA, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HONEYCOMB, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HONEY_BOTTLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HOPPER_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.INK_SAC, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RAW_IRON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.IRON_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.IRON_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.IRON_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.IRON_INGOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.IRON_NUGGET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.IRON_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.IRON_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.IRON_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.ITEM_FRAME, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.JUNGLE_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.JUNGLE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.KNOWLEDGE_BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LAPIS_LAZULI, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LAVA_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LEATHER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LEATHER_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LIGHT_BLUE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LIGHT_GRAY_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LIME_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MAGENTA_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MAGMA_CREAM, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MANGROVE_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MANGROVE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BAMBOO_RAFT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BAMBOO_CHEST_RAFT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MAP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MELON_SLICE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MILK_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MOJANG_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSHROOM_STEW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DISC_FRAGMENT_5, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_11, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_13, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_BLOCKS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_CAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_CHIRP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_FAR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_MALL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_MELLOHI, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_PIGSTEP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_STAL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_STRAD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_WAIT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_WARD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_OTHERSIDE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_5, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUTTON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NAME_TAG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NAUTILUS_SHELL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHERITE_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHERITE_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHERITE_INGOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHERITE_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHERITE_SCRAP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHERITE_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHERITE_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHER_BRICK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHER_STAR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.OAK_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.OAK_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ORANGE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PAINTING, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PAPER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PHANTOM_MEMBRANE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PIGLIN_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PINK_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.POISONOUS_POTATO, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.POPPED_CHORUS_FRUIT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PORKCHOP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.POWDER_SNOW_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PRISMARINE_CRYSTALS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PRISMARINE_SHARD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PUFFERFISH, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PUFFERFISH_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PUMPKIN_PIE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PURPLE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.QUARTZ, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RABBIT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RABBIT_FOOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RABBIT_HIDE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RABBIT_STEW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RED_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ROTTEN_FLESH, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SADDLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SALMON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SALMON_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SCUTE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHEARS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHULKER_SHELL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SKULL_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SLIME_BALL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SNOWBALL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ECHO_SHARD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPECTRAL_ARROW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPIDER_EYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPRUCE_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPRUCE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPYGLASS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.STICK, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.SUGAR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SUSPICIOUS_STEW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TNT_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TOTEM_OF_UNDYING, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TRIDENT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TROPICAL_FISH, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TROPICAL_FISH_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.AXOLOTL_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TADPOLE_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WATER_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WHEAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WHITE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WOODEN_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WOODEN_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WOODEN_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WOODEN_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WOODEN_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WRITABLE_BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WRITTEN_BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.YELLOW_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DEBUG_STICK, Items.STICK, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_APPLE, ModelTemplates.FLAT_ITEM);
        for (Item $$0 : BuiltInRegistries.ITEM) {
            ArmorItem $$1;
            if (!($$0 instanceof ArmorItem) || !($$1 = (ArmorItem)$$0).getMaterial().canHaveTrims()) continue;
            this.generateArmorTrims($$1);
        }
    }

    record TrimModelData(String name, float itemModelIndex, Optional<ArmorMaterials> incompatibleArmorMaterial) {
    }
}
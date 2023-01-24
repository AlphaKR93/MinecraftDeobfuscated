/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.item;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LightBlock;

public class ItemProperties {
    private static final Map<ResourceLocation, ItemPropertyFunction> GENERIC_PROPERTIES = Maps.newHashMap();
    private static final String TAG_CUSTOM_MODEL_DATA = "CustomModelData";
    private static final ResourceLocation DAMAGED = new ResourceLocation("damaged");
    private static final ResourceLocation DAMAGE = new ResourceLocation("damage");
    private static final ClampedItemPropertyFunction PROPERTY_DAMAGED = ($$0, $$1, $$2, $$3) -> $$0.isDamaged() ? 1.0f : 0.0f;
    private static final ClampedItemPropertyFunction PROPERTY_DAMAGE = ($$0, $$1, $$2, $$3) -> Mth.clamp((float)$$0.getDamageValue() / (float)$$0.getMaxDamage(), 0.0f, 1.0f);
    private static final Map<Item, Map<ResourceLocation, ItemPropertyFunction>> PROPERTIES = Maps.newHashMap();

    private static ClampedItemPropertyFunction registerGeneric(ResourceLocation $$0, ClampedItemPropertyFunction $$1) {
        GENERIC_PROPERTIES.put((Object)$$0, (Object)$$1);
        return $$1;
    }

    private static void registerCustomModelData(ItemPropertyFunction $$0) {
        GENERIC_PROPERTIES.put((Object)new ResourceLocation("custom_model_data"), (Object)$$0);
    }

    private static void register(Item $$02, ResourceLocation $$1, ClampedItemPropertyFunction $$2) {
        ((Map)PROPERTIES.computeIfAbsent((Object)$$02, $$0 -> Maps.newHashMap())).put((Object)$$1, (Object)$$2);
    }

    @Nullable
    public static ItemPropertyFunction getProperty(Item $$0, ResourceLocation $$1) {
        ItemPropertyFunction $$2;
        if ($$0.getMaxDamage() > 0) {
            if (DAMAGE.equals($$1)) {
                return PROPERTY_DAMAGE;
            }
            if (DAMAGED.equals($$1)) {
                return PROPERTY_DAMAGED;
            }
        }
        if (($$2 = (ItemPropertyFunction)GENERIC_PROPERTIES.get((Object)$$1)) != null) {
            return $$2;
        }
        Map $$3 = (Map)PROPERTIES.get((Object)$$0);
        if ($$3 == null) {
            return null;
        }
        return (ItemPropertyFunction)$$3.get((Object)$$1);
    }

    static {
        ItemProperties.registerGeneric(new ResourceLocation("lefthanded"), ($$0, $$1, $$2, $$3) -> $$2 == null || $$2.getMainArm() == HumanoidArm.RIGHT ? 0.0f : 1.0f);
        ItemProperties.registerGeneric(new ResourceLocation("cooldown"), ($$0, $$1, $$2, $$3) -> $$2 instanceof Player ? ((Player)$$2).getCooldowns().getCooldownPercent($$0.getItem(), 0.0f) : 0.0f);
        ClampedItemPropertyFunction $$02 = ($$0, $$1, $$2, $$3) -> {
            if (!$$0.is(ItemTags.TRIMMABLE_ARMOR)) {
                return Float.NEGATIVE_INFINITY;
            }
            if ($$1 == null) {
                return 0.0f;
            }
            if (!$$1.enabledFeatures().contains(FeatureFlags.UPDATE_1_20)) {
                return Float.NEGATIVE_INFINITY;
            }
            return ((Float)ArmorTrim.getTrim($$1.registryAccess(), $$0).map(ArmorTrim::material).map(Holder::value).map(TrimMaterial::itemModelIndex).orElse((Object)Float.valueOf((float)0.0f))).floatValue();
        };
        ItemProperties.registerGeneric(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID, $$02);
        ItemProperties.registerCustomModelData(($$0, $$1, $$2, $$3) -> $$0.hasTag() ? (float)$$0.getTag().getInt(TAG_CUSTOM_MODEL_DATA) : 0.0f);
        ItemProperties.register(Items.BOW, new ResourceLocation("pull"), ($$0, $$1, $$2, $$3) -> {
            if ($$2 == null) {
                return 0.0f;
            }
            if ($$2.getUseItem() != $$0) {
                return 0.0f;
            }
            return (float)($$0.getUseDuration() - $$2.getUseItemRemainingTicks()) / 20.0f;
        });
        ItemProperties.register(Items.BOW, new ResourceLocation("pulling"), ($$0, $$1, $$2, $$3) -> $$2 != null && $$2.isUsingItem() && $$2.getUseItem() == $$0 ? 1.0f : 0.0f);
        ItemProperties.register(Items.BUNDLE, new ResourceLocation("filled"), ($$0, $$1, $$2, $$3) -> BundleItem.getFullnessDisplay($$0));
        ItemProperties.register(Items.CLOCK, new ResourceLocation("time"), new ClampedItemPropertyFunction(){
            private double rotation;
            private double rota;
            private long lastUpdateTick;

            @Override
            public float unclampedCall(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3) {
                double $$6;
                Entity $$4;
                Entity entity = $$4 = $$2 != null ? $$2 : $$0.getEntityRepresentation();
                if ($$4 == null) {
                    return 0.0f;
                }
                if ($$1 == null && $$4.level instanceof ClientLevel) {
                    $$1 = (ClientLevel)$$4.level;
                }
                if ($$1 == null) {
                    return 0.0f;
                }
                if ($$1.dimensionType().natural()) {
                    double $$5 = $$1.getTimeOfDay(1.0f);
                } else {
                    $$6 = Math.random();
                }
                $$6 = this.wobble($$1, $$6);
                return (float)$$6;
            }

            private double wobble(Level $$0, double $$1) {
                if ($$0.getGameTime() != this.lastUpdateTick) {
                    this.lastUpdateTick = $$0.getGameTime();
                    double $$2 = $$1 - this.rotation;
                    $$2 = Mth.positiveModulo($$2 + 0.5, 1.0) - 0.5;
                    this.rota += $$2 * 0.1;
                    this.rota *= 0.9;
                    this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0);
                }
                return this.rotation;
            }
        });
        ItemProperties.register(Items.COMPASS, new ResourceLocation("angle"), new CompassItemPropertyFunction(($$0, $$1, $$2) -> {
            if (CompassItem.isLodestoneCompass($$1)) {
                return CompassItem.getLodestonePosition($$1.getOrCreateTag());
            }
            return CompassItem.getSpawnPosition($$0);
        }));
        ItemProperties.register(Items.RECOVERY_COMPASS, new ResourceLocation("angle"), new CompassItemPropertyFunction(($$0, $$1, $$2) -> {
            if ($$2 instanceof Player) {
                Player $$3 = (Player)$$2;
                return (GlobalPos)$$3.getLastDeathLocation().orElse(null);
            }
            return null;
        }));
        ItemProperties.register(Items.CROSSBOW, new ResourceLocation("pull"), ($$0, $$1, $$2, $$3) -> {
            if ($$2 == null) {
                return 0.0f;
            }
            if (CrossbowItem.isCharged($$0)) {
                return 0.0f;
            }
            return (float)($$0.getUseDuration() - $$2.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration($$0);
        });
        ItemProperties.register(Items.CROSSBOW, new ResourceLocation("pulling"), ($$0, $$1, $$2, $$3) -> $$2 != null && $$2.isUsingItem() && $$2.getUseItem() == $$0 && !CrossbowItem.isCharged($$0) ? 1.0f : 0.0f);
        ItemProperties.register(Items.CROSSBOW, new ResourceLocation("charged"), ($$0, $$1, $$2, $$3) -> $$2 != null && CrossbowItem.isCharged($$0) ? 1.0f : 0.0f);
        ItemProperties.register(Items.CROSSBOW, new ResourceLocation("firework"), ($$0, $$1, $$2, $$3) -> $$2 != null && CrossbowItem.isCharged($$0) && CrossbowItem.containsChargedProjectile($$0, Items.FIREWORK_ROCKET) ? 1.0f : 0.0f);
        ItemProperties.register(Items.ELYTRA, new ResourceLocation("broken"), ($$0, $$1, $$2, $$3) -> ElytraItem.isFlyEnabled($$0) ? 0.0f : 1.0f);
        ItemProperties.register(Items.FISHING_ROD, new ResourceLocation("cast"), ($$0, $$1, $$2, $$3) -> {
            boolean $$5;
            if ($$2 == null) {
                return 0.0f;
            }
            boolean $$4 = $$2.getMainHandItem() == $$0;
            boolean bl = $$5 = $$2.getOffhandItem() == $$0;
            if ($$2.getMainHandItem().getItem() instanceof FishingRodItem) {
                $$5 = false;
            }
            return ($$4 || $$5) && $$2 instanceof Player && ((Player)$$2).fishing != null ? 1.0f : 0.0f;
        });
        ItemProperties.register(Items.SHIELD, new ResourceLocation("blocking"), ($$0, $$1, $$2, $$3) -> $$2 != null && $$2.isUsingItem() && $$2.getUseItem() == $$0 ? 1.0f : 0.0f);
        ItemProperties.register(Items.TRIDENT, new ResourceLocation("throwing"), ($$0, $$1, $$2, $$3) -> $$2 != null && $$2.isUsingItem() && $$2.getUseItem() == $$0 ? 1.0f : 0.0f);
        ItemProperties.register(Items.LIGHT, new ResourceLocation("level"), ($$0, $$1, $$2, $$3) -> {
            CompoundTag $$4 = $$0.getTagElement("BlockStateTag");
            try {
                Tag $$5;
                if ($$4 != null && ($$5 = $$4.get(LightBlock.LEVEL.getName())) != null) {
                    return (float)Integer.parseInt((String)$$5.getAsString()) / 16.0f;
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            return 1.0f;
        });
        ItemProperties.register(Items.GOAT_HORN, new ResourceLocation("tooting"), ($$0, $$1, $$2, $$3) -> $$2 != null && $$2.isUsingItem() && $$2.getUseItem() == $$0 ? 1.0f : 0.0f);
    }
}
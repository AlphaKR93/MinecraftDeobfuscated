/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.function.IntFunction
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkStarItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FireworkRocketItem
extends Item {
    public static final byte[] CRAFTABLE_DURATIONS = new byte[]{1, 2, 3};
    public static final String TAG_FIREWORKS = "Fireworks";
    public static final String TAG_EXPLOSION = "Explosion";
    public static final String TAG_EXPLOSIONS = "Explosions";
    public static final String TAG_FLIGHT = "Flight";
    public static final String TAG_EXPLOSION_TYPE = "Type";
    public static final String TAG_EXPLOSION_TRAIL = "Trail";
    public static final String TAG_EXPLOSION_FLICKER = "Flicker";
    public static final String TAG_EXPLOSION_COLORS = "Colors";
    public static final String TAG_EXPLOSION_FADECOLORS = "FadeColors";
    public static final double ROCKET_PLACEMENT_OFFSET = 0.15;

    public FireworkRocketItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        Level $$1 = $$0.getLevel();
        if (!$$1.isClientSide) {
            ItemStack $$2 = $$0.getItemInHand();
            Vec3 $$3 = $$0.getClickLocation();
            Direction $$4 = $$0.getClickedFace();
            FireworkRocketEntity $$5 = new FireworkRocketEntity($$1, $$0.getPlayer(), $$3.x + (double)$$4.getStepX() * 0.15, $$3.y + (double)$$4.getStepY() * 0.15, $$3.z + (double)$$4.getStepZ() * 0.15, $$2);
            $$1.addFreshEntity($$5);
            $$2.shrink(1);
        }
        return InteractionResult.sidedSuccess($$1.isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        if ($$1.isFallFlying()) {
            ItemStack $$3 = $$1.getItemInHand($$2);
            if (!$$0.isClientSide) {
                FireworkRocketEntity $$4 = new FireworkRocketEntity($$0, $$3, $$1);
                $$0.addFreshEntity($$4);
                if (!$$1.getAbilities().instabuild) {
                    $$3.shrink(1);
                }
                $$1.awardStat(Stats.ITEM_USED.get(this));
            }
            return InteractionResultHolder.sidedSuccess($$1.getItemInHand($$2), $$0.isClientSide());
        }
        return InteractionResultHolder.pass($$1.getItemInHand($$2));
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        ListTag $$5;
        CompoundTag $$4 = $$0.getTagElement(TAG_FIREWORKS);
        if ($$4 == null) {
            return;
        }
        if ($$4.contains(TAG_FLIGHT, 99)) {
            $$2.add((Object)Component.translatable("item.minecraft.firework_rocket.flight").append(CommonComponents.SPACE).append(String.valueOf((int)$$4.getByte(TAG_FLIGHT))).withStyle(ChatFormatting.GRAY));
        }
        if (!($$5 = $$4.getList(TAG_EXPLOSIONS, 10)).isEmpty()) {
            for (int $$6 = 0; $$6 < $$5.size(); ++$$6) {
                CompoundTag $$7 = $$5.getCompound($$6);
                ArrayList $$8 = Lists.newArrayList();
                FireworkStarItem.appendHoverText($$7, (List<Component>)$$8);
                if ($$8.isEmpty()) continue;
                for (int $$9 = 1; $$9 < $$8.size(); ++$$9) {
                    $$8.set($$9, (Object)Component.literal("  ").append((Component)$$8.get($$9)).withStyle(ChatFormatting.GRAY));
                }
                $$2.addAll((Collection)$$8);
            }
        }
    }

    public static void setDuration(ItemStack $$0, byte $$1) {
        $$0.getOrCreateTagElement(TAG_FIREWORKS).putByte(TAG_FLIGHT, $$1);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack $$0 = new ItemStack(this);
        FireworkRocketItem.setDuration($$0, (byte)1);
        return $$0;
    }

    public static enum Shape {
        SMALL_BALL(0, "small_ball"),
        LARGE_BALL(1, "large_ball"),
        STAR(2, "star"),
        CREEPER(3, "creeper"),
        BURST(4, "burst");

        private static final IntFunction<Shape> BY_ID;
        private final int id;
        private final String name;

        private Shape(int $$0, String $$1) {
            this.id = $$0;
            this.name = $$1;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static Shape byId(int $$0) {
            return (Shape)((Object)BY_ID.apply($$0));
        }

        static {
            BY_ID = ByIdMap.continuous(Shape::getId, Shape.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        }
    }
}
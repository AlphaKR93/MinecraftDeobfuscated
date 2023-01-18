/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  javax.annotation.Nullable
 *  net.minecraft.world.item.ItemStack
 *  org.slf4j.Logger
 */
package net.minecraft.world.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;

public class CompassItem
extends Item
implements Vanishable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String TAG_LODESTONE_POS = "LodestonePos";
    public static final String TAG_LODESTONE_DIMENSION = "LodestoneDimension";
    public static final String TAG_LODESTONE_TRACKED = "LodestoneTracked";

    public CompassItem(Item.Properties $$0) {
        super($$0);
    }

    public static boolean isLodestoneCompass(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTag();
        return $$1 != null && ($$1.contains(TAG_LODESTONE_DIMENSION) || $$1.contains(TAG_LODESTONE_POS));
    }

    private static Optional<ResourceKey<Level>> getLodestoneDimension(CompoundTag $$0) {
        return Level.RESOURCE_KEY_CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)$$0.get(TAG_LODESTONE_DIMENSION)).result();
    }

    @Nullable
    public static GlobalPos getLodestonePosition(CompoundTag $$0) {
        Optional<ResourceKey<Level>> $$3;
        boolean $$1 = $$0.contains(TAG_LODESTONE_POS);
        boolean $$2 = $$0.contains(TAG_LODESTONE_DIMENSION);
        if ($$1 && $$2 && ($$3 = CompassItem.getLodestoneDimension($$0)).isPresent()) {
            BlockPos $$4 = NbtUtils.readBlockPos($$0.getCompound(TAG_LODESTONE_POS));
            return GlobalPos.of((ResourceKey)$$3.get(), $$4);
        }
        return null;
    }

    @Nullable
    public static GlobalPos getSpawnPosition(Level $$0) {
        return $$0.dimensionType().natural() ? GlobalPos.of($$0.dimension(), $$0.getSharedSpawnPos()) : null;
    }

    @Override
    public boolean isFoil(ItemStack $$0) {
        return CompassItem.isLodestoneCompass($$0) || super.isFoil($$0);
    }

    @Override
    public void inventoryTick(ItemStack $$0, Level $$1, Entity $$2, int $$3, boolean $$4) {
        if ($$1.isClientSide) {
            return;
        }
        if (CompassItem.isLodestoneCompass($$0)) {
            BlockPos $$7;
            CompoundTag $$5 = $$0.getOrCreateTag();
            if ($$5.contains(TAG_LODESTONE_TRACKED) && !$$5.getBoolean(TAG_LODESTONE_TRACKED)) {
                return;
            }
            Optional<ResourceKey<Level>> $$6 = CompassItem.getLodestoneDimension($$5);
            if ($$6.isPresent() && $$6.get() == $$1.dimension() && $$5.contains(TAG_LODESTONE_POS) && (!$$1.isInWorldBounds($$7 = NbtUtils.readBlockPos($$5.getCompound(TAG_LODESTONE_POS))) || !((ServerLevel)$$1).getPoiManager().existsAtPosition(PoiTypes.LODESTONE, $$7))) {
                $$5.remove(TAG_LODESTONE_POS);
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockPos $$1 = $$0.getClickedPos();
        Level $$2 = $$0.getLevel();
        if ($$2.getBlockState($$1).is(Blocks.LODESTONE)) {
            boolean $$5;
            $$2.playSound(null, $$1, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0f, 1.0f);
            Player $$3 = $$0.getPlayer();
            ItemStack $$4 = $$0.getItemInHand();
            boolean bl = $$5 = !$$3.getAbilities().instabuild && $$4.getCount() == 1;
            if ($$5) {
                this.addLodestoneTags($$2.dimension(), $$1, $$4.getOrCreateTag());
            } else {
                ItemStack $$6 = new ItemStack((ItemLike)Items.COMPASS, 1);
                CompoundTag $$7 = $$4.hasTag() ? $$4.getTag().copy() : new CompoundTag();
                $$6.setTag($$7);
                if (!$$3.getAbilities().instabuild) {
                    $$4.shrink(1);
                }
                this.addLodestoneTags($$2.dimension(), $$1, $$7);
                if (!$$3.getInventory().add($$6)) {
                    $$3.drop($$6, false);
                }
            }
            return InteractionResult.sidedSuccess($$2.isClientSide);
        }
        return super.useOn($$0);
    }

    private void addLodestoneTags(ResourceKey<Level> $$0, BlockPos $$12, CompoundTag $$2) {
        $$2.put(TAG_LODESTONE_POS, NbtUtils.writeBlockPos($$12));
        Level.RESOURCE_KEY_CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, $$0).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$2.put(TAG_LODESTONE_DIMENSION, (Tag)$$1));
        $$2.putBoolean(TAG_LODESTONE_TRACKED, true);
    }

    @Override
    public String getDescriptionId(ItemStack $$0) {
        return CompassItem.isLodestoneCompass($$0) ? "item.minecraft.lodestone_compass" : super.getDescriptionId($$0);
    }
}
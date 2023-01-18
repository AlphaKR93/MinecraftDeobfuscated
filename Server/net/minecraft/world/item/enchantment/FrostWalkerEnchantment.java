/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;

public class FrostWalkerEnchantment
extends Enchantment {
    public FrostWalkerEnchantment(Enchantment.Rarity $$0, EquipmentSlot ... $$1) {
        super($$0, EnchantmentCategory.ARMOR_FEET, $$1);
    }

    @Override
    public int getMinCost(int $$0) {
        return $$0 * 10;
    }

    @Override
    public int getMaxCost(int $$0) {
        return this.getMinCost($$0) + 15;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    public static void onEntityMoved(LivingEntity $$0, Level $$1, BlockPos $$2, int $$3) {
        if (!$$0.isOnGround()) {
            return;
        }
        BlockState $$4 = Blocks.FROSTED_ICE.defaultBlockState();
        float $$5 = Math.min((int)16, (int)(2 + $$3));
        BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos();
        for (BlockPos $$7 : BlockPos.betweenClosed($$2.offset(-$$5, -1.0, -$$5), $$2.offset($$5, -1.0, $$5))) {
            BlockState $$9;
            if (!$$7.closerToCenterThan($$0.position(), $$5)) continue;
            $$6.set($$7.getX(), $$7.getY() + 1, $$7.getZ());
            BlockState $$8 = $$1.getBlockState($$6);
            if (!$$8.isAir() || ($$9 = $$1.getBlockState($$7)).getMaterial() != Material.WATER || $$9.getValue(LiquidBlock.LEVEL) != 0 || !$$4.canSurvive($$1, $$7) || !$$1.isUnobstructed($$4, $$7, CollisionContext.empty())) continue;
            $$1.setBlockAndUpdate($$7, $$4);
            $$1.scheduleTick($$7, Blocks.FROSTED_ICE, Mth.nextInt($$0.getRandom(), 60, 120));
        }
    }

    @Override
    public boolean checkCompatibility(Enchantment $$0) {
        return super.checkCompatibility($$0) && $$0 != Enchantments.DEPTH_STRIDER;
    }
}
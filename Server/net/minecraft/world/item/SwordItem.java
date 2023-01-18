/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Multimap
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class SwordItem
extends TieredItem
implements Vanishable {
    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public SwordItem(Tier $$0, int $$1, float $$2, Item.Properties $$3) {
        super($$0, $$3);
        this.attackDamage = (float)$$1 + $$0.getAttackDamageBonus();
        ImmutableMultimap.Builder $$4 = ImmutableMultimap.builder();
        $$4.put((Object)Attributes.ATTACK_DAMAGE, (Object)new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
        $$4.put((Object)Attributes.ATTACK_SPEED, (Object)new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)$$2, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = $$4.build();
    }

    public float getDamage() {
        return this.attackDamage;
    }

    @Override
    public boolean canAttackBlock(BlockState $$0, Level $$1, BlockPos $$2, Player $$3) {
        return !$$3.isCreative();
    }

    @Override
    public float getDestroySpeed(ItemStack $$0, BlockState $$1) {
        if ($$1.is(Blocks.COBWEB)) {
            return 15.0f;
        }
        Material $$2 = $$1.getMaterial();
        if ($$2 == Material.PLANT || $$2 == Material.REPLACEABLE_PLANT || $$1.is(BlockTags.LEAVES) || $$2 == Material.VEGETABLE) {
            return 1.5f;
        }
        return 1.0f;
    }

    @Override
    public boolean hurtEnemy(ItemStack $$02, LivingEntity $$1, LivingEntity $$2) {
        $$02.hurtAndBreak(1, $$2, $$0 -> $$0.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack $$02, Level $$1, BlockState $$2, BlockPos $$3, LivingEntity $$4) {
        if ($$2.getDestroySpeed($$1, $$3) != 0.0f) {
            $$02.hurtAndBreak(2, $$4, $$0 -> $$0.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState $$0) {
        return $$0.is(Blocks.COBWEB);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot $$0) {
        if ($$0 == EquipmentSlot.MAINHAND) {
            return this.defaultModifiers;
        }
        return super.getDefaultAttributeModifiers($$0);
    }
}
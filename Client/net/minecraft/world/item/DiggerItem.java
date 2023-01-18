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
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DiggerItem
extends TieredItem
implements Vanishable {
    private final TagKey<Block> blocks;
    protected final float speed;
    private final float attackDamageBaseline;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    protected DiggerItem(float $$0, float $$1, Tier $$2, TagKey<Block> $$3, Item.Properties $$4) {
        super($$2, $$4);
        this.blocks = $$3;
        this.speed = $$2.getSpeed();
        this.attackDamageBaseline = $$0 + $$2.getAttackDamageBonus();
        ImmutableMultimap.Builder $$5 = ImmutableMultimap.builder();
        $$5.put((Object)Attributes.ATTACK_DAMAGE, (Object)new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", (double)this.attackDamageBaseline, AttributeModifier.Operation.ADDITION));
        $$5.put((Object)Attributes.ATTACK_SPEED, (Object)new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)$$1, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = $$5.build();
    }

    @Override
    public float getDestroySpeed(ItemStack $$0, BlockState $$1) {
        return $$1.is(this.blocks) ? this.speed : 1.0f;
    }

    @Override
    public boolean hurtEnemy(ItemStack $$02, LivingEntity $$1, LivingEntity $$2) {
        $$02.hurtAndBreak(2, $$2, $$0 -> $$0.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack $$02, Level $$1, BlockState $$2, BlockPos $$3, LivingEntity $$4) {
        if (!$$1.isClientSide && $$2.getDestroySpeed($$1, $$3) != 0.0f) {
            $$02.hurtAndBreak(1, $$4, $$0 -> $$0.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot $$0) {
        if ($$0 == EquipmentSlot.MAINHAND) {
            return this.defaultModifiers;
        }
        return super.getDefaultAttributeModifiers($$0);
    }

    public float getAttackDamage() {
        return this.attackDamageBaseline;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState $$0) {
        int $$1 = this.getTier().getLevel();
        if ($$1 < 3 && $$0.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return false;
        }
        if ($$1 < 2 && $$0.is(BlockTags.NEEDS_IRON_TOOL)) {
            return false;
        }
        if ($$1 < 1 && $$0.is(BlockTags.NEEDS_STONE_TOOL)) {
            return false;
        }
        return $$0.is(this.blocks);
    }
}
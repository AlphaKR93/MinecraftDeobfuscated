/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class SkeletonTrapGoal
extends Goal {
    private final SkeletonHorse horse;

    public SkeletonTrapGoal(SkeletonHorse $$0) {
        this.horse = $$0;
    }

    @Override
    public boolean canUse() {
        return this.horse.level.hasNearbyAlivePlayer(this.horse.getX(), this.horse.getY(), this.horse.getZ(), 10.0);
    }

    @Override
    public void tick() {
        ServerLevel $$0 = (ServerLevel)this.horse.level;
        DifficultyInstance $$1 = $$0.getCurrentDifficultyAt(this.horse.blockPosition());
        this.horse.setTrap(false);
        this.horse.setTamed(true);
        this.horse.setAge(0);
        LightningBolt $$2 = EntityType.LIGHTNING_BOLT.create($$0);
        if ($$2 == null) {
            return;
        }
        $$2.moveTo(this.horse.getX(), this.horse.getY(), this.horse.getZ());
        $$2.setVisualOnly(true);
        $$0.addFreshEntity($$2);
        Skeleton $$3 = this.createSkeleton($$1, this.horse);
        if ($$3 == null) {
            return;
        }
        $$3.startRiding(this.horse);
        $$0.addFreshEntityWithPassengers($$3);
        for (int $$4 = 0; $$4 < 3; ++$$4) {
            Skeleton $$6;
            AbstractHorse $$5 = this.createHorse($$1);
            if ($$5 == null || ($$6 = this.createSkeleton($$1, $$5)) == null) continue;
            $$6.startRiding($$5);
            $$5.push(this.horse.getRandom().triangle(0.0, 1.1485), 0.0, this.horse.getRandom().triangle(0.0, 1.1485));
            $$0.addFreshEntityWithPassengers($$5);
        }
    }

    @Nullable
    private AbstractHorse createHorse(DifficultyInstance $$0) {
        SkeletonHorse $$1 = EntityType.SKELETON_HORSE.create(this.horse.level);
        if ($$1 != null) {
            $$1.finalizeSpawn((ServerLevel)this.horse.level, $$0, MobSpawnType.TRIGGERED, null, null);
            $$1.setPos(this.horse.getX(), this.horse.getY(), this.horse.getZ());
            $$1.invulnerableTime = 60;
            $$1.setPersistenceRequired();
            $$1.setTamed(true);
            $$1.setAge(0);
        }
        return $$1;
    }

    @Nullable
    private Skeleton createSkeleton(DifficultyInstance $$0, AbstractHorse $$1) {
        Skeleton $$2 = EntityType.SKELETON.create($$1.level);
        if ($$2 != null) {
            $$2.finalizeSpawn((ServerLevel)$$1.level, $$0, MobSpawnType.TRIGGERED, null, null);
            $$2.setPos($$1.getX(), $$1.getY(), $$1.getZ());
            $$2.invulnerableTime = 60;
            $$2.setPersistenceRequired();
            if ($$2.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                $$2.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            }
            $$2.setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem($$2.getRandom(), this.disenchant($$2.getMainHandItem()), (int)(5.0f + $$0.getSpecialMultiplier() * (float)$$2.getRandom().nextInt(18)), false));
            $$2.setItemSlot(EquipmentSlot.HEAD, EnchantmentHelper.enchantItem($$2.getRandom(), this.disenchant($$2.getItemBySlot(EquipmentSlot.HEAD)), (int)(5.0f + $$0.getSpecialMultiplier() * (float)$$2.getRandom().nextInt(18)), false));
        }
        return $$2;
    }

    private ItemStack disenchant(ItemStack $$0) {
        $$0.removeTagKey("Enchantments");
        return $$0;
    }
}
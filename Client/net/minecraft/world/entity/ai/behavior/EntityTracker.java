/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 */
package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.phys.Vec3;

public class EntityTracker
implements PositionTracker {
    private final Entity entity;
    private final boolean trackEyeHeight;

    public EntityTracker(Entity $$0, boolean $$1) {
        this.entity = $$0;
        this.trackEyeHeight = $$1;
    }

    @Override
    public Vec3 currentPosition() {
        return this.trackEyeHeight ? this.entity.position().add(0.0, this.entity.getEyeHeight(), 0.0) : this.entity.position();
    }

    @Override
    public BlockPos currentBlockPosition() {
        return this.entity.blockPosition();
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public boolean isVisibleBy(LivingEntity $$0) {
        void $$2;
        Entity entity = this.entity;
        if (!(entity instanceof LivingEntity)) {
            return true;
        }
        LivingEntity $$1 = (LivingEntity)entity;
        if (!$$2.isAlive()) {
            return false;
        }
        Optional<NearestVisibleLivingEntities> $$3 = $$0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        return $$3.isPresent() && ((NearestVisibleLivingEntities)$$3.get()).contains((LivingEntity)$$2);
    }

    public Entity getEntity() {
        return this.entity;
    }

    public String toString() {
        return "EntityTracker for " + this.entity;
    }
}
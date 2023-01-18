/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.animal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;

public abstract class ShoulderRidingEntity
extends TamableAnimal {
    private static final int RIDE_COOLDOWN = 100;
    private int rideCooldownCounter;

    protected ShoulderRidingEntity(EntityType<? extends ShoulderRidingEntity> $$0, Level $$1) {
        super((EntityType<? extends TamableAnimal>)$$0, $$1);
    }

    public boolean setEntityOnShoulder(ServerPlayer $$0) {
        CompoundTag $$1 = new CompoundTag();
        $$1.putString("id", this.getEncodeId());
        this.saveWithoutId($$1);
        if ($$0.setEntityOnShoulder($$1)) {
            this.discard();
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        ++this.rideCooldownCounter;
        super.tick();
    }

    public boolean canSitOnShoulder() {
        return this.rideCooldownCounter > 100;
    }
}
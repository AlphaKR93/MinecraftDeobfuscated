/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.entity.player;

import net.minecraft.nbt.CompoundTag;

public class Abilities {
    public boolean invulnerable;
    public boolean flying;
    public boolean mayfly;
    public boolean instabuild;
    public boolean mayBuild = true;
    private float flyingSpeed = 0.05f;
    private float walkingSpeed = 0.1f;

    public void addSaveData(CompoundTag $$0) {
        CompoundTag $$1 = new CompoundTag();
        $$1.putBoolean("invulnerable", this.invulnerable);
        $$1.putBoolean("flying", this.flying);
        $$1.putBoolean("mayfly", this.mayfly);
        $$1.putBoolean("instabuild", this.instabuild);
        $$1.putBoolean("mayBuild", this.mayBuild);
        $$1.putFloat("flySpeed", this.flyingSpeed);
        $$1.putFloat("walkSpeed", this.walkingSpeed);
        $$0.put("abilities", $$1);
    }

    public void loadSaveData(CompoundTag $$0) {
        if ($$0.contains("abilities", 10)) {
            CompoundTag $$1 = $$0.getCompound("abilities");
            this.invulnerable = $$1.getBoolean("invulnerable");
            this.flying = $$1.getBoolean("flying");
            this.mayfly = $$1.getBoolean("mayfly");
            this.instabuild = $$1.getBoolean("instabuild");
            if ($$1.contains("flySpeed", 99)) {
                this.flyingSpeed = $$1.getFloat("flySpeed");
                this.walkingSpeed = $$1.getFloat("walkSpeed");
            }
            if ($$1.contains("mayBuild", 1)) {
                this.mayBuild = $$1.getBoolean("mayBuild");
            }
        }
    }

    public float getFlyingSpeed() {
        return this.flyingSpeed;
    }

    public void setFlyingSpeed(float $$0) {
        this.flyingSpeed = $$0;
    }

    public float getWalkingSpeed() {
        return this.walkingSpeed;
    }

    public void setWalkingSpeed(float $$0) {
        this.walkingSpeed = $$0;
    }
}
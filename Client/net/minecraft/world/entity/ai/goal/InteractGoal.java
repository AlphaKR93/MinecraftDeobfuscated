/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.Enum
 *  java.lang.Object
 *  java.util.EnumSet
 *  net.minecraft.world.entity.LivingEntity
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class InteractGoal
extends LookAtPlayerGoal {
    public InteractGoal(Mob $$0, Class<? extends LivingEntity> $$1, float $$2) {
        super($$0, $$1, $$2);
        this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.LOOK, (Enum)Goal.Flag.MOVE));
    }

    public InteractGoal(Mob $$0, Class<? extends LivingEntity> $$1, float $$2, float $$3) {
        super($$0, $$1, $$2, $$3);
        this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.LOOK, (Enum)Goal.Flag.MOVE));
    }
}
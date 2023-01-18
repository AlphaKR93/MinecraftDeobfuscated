/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  java.lang.Object
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;

public class ResetProfession {
    public static BehaviorControl<Villager> create() {
        return BehaviorBuilder.create($$0 -> $$0.group($$0.absent(MemoryModuleType.JOB_SITE)).apply((Applicative)$$0, $$02 -> ($$0, $$1, $$2) -> {
            VillagerData $$3 = $$1.getVillagerData();
            if ($$3.getProfession() != VillagerProfession.NONE && $$3.getProfession() != VillagerProfession.NITWIT && $$1.getVillagerXp() == 0 && $$3.getLevel() <= 1) {
                $$1.setVillagerData($$1.getVillagerData().setProfession(VillagerProfession.NONE));
                $$1.refreshBrain($$0);
                return true;
            }
            return false;
        }));
    }
}
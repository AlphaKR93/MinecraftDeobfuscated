/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  java.lang.Object
 *  java.util.Optional
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Position;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class AssignProfessionFromJobSite {
    public static BehaviorControl<Villager> create() {
        return BehaviorBuilder.create($$0 -> $$0.group($$0.present(MemoryModuleType.POTENTIAL_JOB_SITE), $$0.registered(MemoryModuleType.JOB_SITE)).apply((Applicative)$$0, ($$1, $$2) -> ($$3, $$4, $$5) -> {
            GlobalPos $$6 = (GlobalPos)$$0.get($$1);
            if (!$$6.pos().closerToCenterThan((Position)$$4.position(), 2.0) && !$$4.assignProfessionWhenSpawned()) {
                return false;
            }
            $$1.erase();
            $$2.set($$6);
            $$3.broadcastEntityEvent($$4, (byte)14);
            if ($$4.getVillagerData().getProfession() != VillagerProfession.NONE) {
                return true;
            }
            MinecraftServer $$7 = $$3.getServer();
            Optional.ofNullable((Object)$$7.getLevel($$6.dimension())).flatMap($$1 -> $$1.getPoiManager().getType($$6.pos())).flatMap($$0 -> BuiltInRegistries.VILLAGER_PROFESSION.stream().filter($$1 -> $$1.heldJobSite().test($$0)).findFirst()).ifPresent($$2 -> {
                $$4.setVillagerData($$4.getVillagerData().setProfession((VillagerProfession)((Object)((Object)((Object)((Object)$$2))))));
                $$4.refreshBrain($$3);
            });
            return true;
        }));
    }
}
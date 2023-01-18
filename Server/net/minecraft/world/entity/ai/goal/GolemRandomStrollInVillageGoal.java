/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

public class GolemRandomStrollInVillageGoal
extends RandomStrollGoal {
    private static final int POI_SECTION_SCAN_RADIUS = 2;
    private static final int VILLAGER_SCAN_RADIUS = 32;
    private static final int RANDOM_POS_XY_DISTANCE = 10;
    private static final int RANDOM_POS_Y_DISTANCE = 7;

    public GolemRandomStrollInVillageGoal(PathfinderMob $$0, double $$1) {
        super($$0, $$1, 240, false);
    }

    @Override
    @Nullable
    protected Vec3 getPosition() {
        Vec3 $$2;
        float $$0 = this.mob.level.random.nextFloat();
        if (this.mob.level.random.nextFloat() < 0.3f) {
            return this.getPositionTowardsAnywhere();
        }
        if ($$0 < 0.7f) {
            Vec3 $$1 = this.getPositionTowardsVillagerWhoWantsGolem();
            if ($$1 == null) {
                $$1 = this.getPositionTowardsPoi();
            }
        } else {
            $$2 = this.getPositionTowardsPoi();
            if ($$2 == null) {
                $$2 = this.getPositionTowardsVillagerWhoWantsGolem();
            }
        }
        return $$2 == null ? this.getPositionTowardsAnywhere() : $$2;
    }

    @Nullable
    private Vec3 getPositionTowardsAnywhere() {
        return LandRandomPos.getPos(this.mob, 10, 7);
    }

    @Nullable
    private Vec3 getPositionTowardsVillagerWhoWantsGolem() {
        ServerLevel $$0 = (ServerLevel)this.mob.level;
        List $$1 = $$0.getEntities(EntityType.VILLAGER, this.mob.getBoundingBox().inflate(32.0), this::doesVillagerWantGolem);
        if ($$1.isEmpty()) {
            return null;
        }
        Villager $$2 = (Villager)$$1.get(this.mob.level.random.nextInt($$1.size()));
        Vec3 $$3 = $$2.position();
        return LandRandomPos.getPosTowards(this.mob, 10, 7, $$3);
    }

    @Nullable
    private Vec3 getPositionTowardsPoi() {
        SectionPos $$0 = this.getRandomVillageSection();
        if ($$0 == null) {
            return null;
        }
        BlockPos $$1 = this.getRandomPoiWithinSection($$0);
        if ($$1 == null) {
            return null;
        }
        return LandRandomPos.getPosTowards(this.mob, 10, 7, Vec3.atBottomCenterOf($$1));
    }

    @Nullable
    private SectionPos getRandomVillageSection() {
        ServerLevel $$0 = (ServerLevel)this.mob.level;
        List $$12 = (List)SectionPos.cube(SectionPos.of(this.mob), 2).filter($$1 -> $$0.sectionsToVillage((SectionPos)$$1) == 0).collect(Collectors.toList());
        if ($$12.isEmpty()) {
            return null;
        }
        return (SectionPos)$$12.get($$0.random.nextInt($$12.size()));
    }

    @Nullable
    private BlockPos getRandomPoiWithinSection(SectionPos $$02) {
        ServerLevel $$1 = (ServerLevel)this.mob.level;
        PoiManager $$2 = $$1.getPoiManager();
        List $$3 = (List)$$2.getInRange((Predicate<Holder<PoiType>>)((Predicate)$$0 -> true), $$02.center(), 8, PoiManager.Occupancy.IS_OCCUPIED).map(PoiRecord::getPos).collect(Collectors.toList());
        if ($$3.isEmpty()) {
            return null;
        }
        return (BlockPos)$$3.get($$1.random.nextInt($$3.size()));
    }

    private boolean doesVillagerWantGolem(Villager $$0) {
        return $$0.wantsToSpawnGolem(this.mob.level.getGameTime());
    }
}
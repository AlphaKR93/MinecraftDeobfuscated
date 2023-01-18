/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.raid;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

public class Raids
extends SavedData {
    private static final String RAID_FILE_ID = "raids";
    private final Map<Integer, Raid> raidMap = Maps.newHashMap();
    private final ServerLevel level;
    private int nextAvailableID;
    private int tick;

    public Raids(ServerLevel $$0) {
        this.level = $$0;
        this.nextAvailableID = 1;
        this.setDirty();
    }

    public Raid get(int $$0) {
        return (Raid)this.raidMap.get((Object)$$0);
    }

    public void tick() {
        ++this.tick;
        Iterator $$0 = this.raidMap.values().iterator();
        while ($$0.hasNext()) {
            Raid $$1 = (Raid)$$0.next();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                $$1.stop();
            }
            if ($$1.isStopped()) {
                $$0.remove();
                this.setDirty();
                continue;
            }
            $$1.tick();
        }
        if (this.tick % 200 == 0) {
            this.setDirty();
        }
        DebugPackets.sendRaids(this.level, (Collection<Raid>)this.raidMap.values());
    }

    public static boolean canJoinRaid(Raider $$0, Raid $$1) {
        if ($$0 != null && $$1 != null && $$1.getLevel() != null) {
            return $$0.isAlive() && $$0.canJoinRaid() && $$0.getNoActionTime() <= 2400 && $$0.level.dimensionType() == $$1.getLevel().dimensionType();
        }
        return false;
    }

    @Nullable
    public Raid createOrExtendRaid(ServerPlayer $$02) {
        BlockPos $$9;
        if ($$02.isSpectator()) {
            return null;
        }
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
            return null;
        }
        DimensionType $$1 = $$02.level.dimensionType();
        if (!$$1.hasRaids()) {
            return null;
        }
        BlockPos $$2 = $$02.blockPosition();
        List $$3 = this.level.getPoiManager().getInRange((Predicate<Holder<PoiType>>)((Predicate)$$0 -> $$0.is(PoiTypeTags.VILLAGE)), $$2, 64, PoiManager.Occupancy.IS_OCCUPIED).toList();
        int $$4 = 0;
        Vec3 $$5 = Vec3.ZERO;
        for (PoiRecord $$6 : $$3) {
            BlockPos $$7 = $$6.getPos();
            $$5 = $$5.add($$7.getX(), $$7.getY(), $$7.getZ());
            ++$$4;
        }
        if ($$4 > 0) {
            $$5 = $$5.scale(1.0 / (double)$$4);
            BlockPos $$8 = new BlockPos($$5);
        } else {
            $$9 = $$2;
        }
        Raid $$10 = this.getOrCreateRaid($$02.getLevel(), $$9);
        boolean $$11 = false;
        if (!$$10.isStarted()) {
            if (!this.raidMap.containsKey((Object)$$10.getId())) {
                this.raidMap.put((Object)$$10.getId(), (Object)$$10);
            }
            $$11 = true;
        } else if ($$10.getBadOmenLevel() < $$10.getMaxBadOmenLevel()) {
            $$11 = true;
        } else {
            $$02.removeEffect(MobEffects.BAD_OMEN);
            $$02.connection.send(new ClientboundEntityEventPacket($$02, 43));
        }
        if ($$11) {
            $$10.absorbBadOmen($$02);
            $$02.connection.send(new ClientboundEntityEventPacket($$02, 43));
            if (!$$10.hasFirstWaveSpawned()) {
                $$02.awardStat(Stats.RAID_TRIGGER);
                CriteriaTriggers.BAD_OMEN.trigger($$02);
            }
        }
        this.setDirty();
        return $$10;
    }

    private Raid getOrCreateRaid(ServerLevel $$0, BlockPos $$1) {
        Raid $$2 = $$0.getRaidAt($$1);
        return $$2 != null ? $$2 : new Raid(this.getUniqueId(), $$0, $$1);
    }

    public static Raids load(ServerLevel $$0, CompoundTag $$1) {
        Raids $$2 = new Raids($$0);
        $$2.nextAvailableID = $$1.getInt("NextAvailableID");
        $$2.tick = $$1.getInt("Tick");
        ListTag $$3 = $$1.getList("Raids", 10);
        for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
            CompoundTag $$5 = $$3.getCompound($$4);
            Raid $$6 = new Raid($$0, $$5);
            $$2.raidMap.put((Object)$$6.getId(), (Object)$$6);
        }
        return $$2;
    }

    @Override
    public CompoundTag save(CompoundTag $$0) {
        $$0.putInt("NextAvailableID", this.nextAvailableID);
        $$0.putInt("Tick", this.tick);
        ListTag $$1 = new ListTag();
        for (Raid $$2 : this.raidMap.values()) {
            CompoundTag $$3 = new CompoundTag();
            $$2.save($$3);
            $$1.add($$3);
        }
        $$0.put("Raids", $$1);
        return $$0;
    }

    public static String getFileId(Holder<DimensionType> $$0) {
        if ($$0.is(BuiltinDimensionTypes.END)) {
            return "raids_end";
        }
        return RAID_FILE_ID;
    }

    private int getUniqueId() {
        return ++this.nextAvailableID;
    }

    @Nullable
    public Raid getNearbyRaid(BlockPos $$0, int $$1) {
        Raid $$2 = null;
        double $$3 = $$1;
        for (Raid $$4 : this.raidMap.values()) {
            double $$5 = $$4.getCenter().distSqr($$0);
            if (!$$4.isActive() || !($$5 < $$3)) continue;
            $$2 = $$4;
            $$3 = $$5;
        }
        return $$2;
    }
}
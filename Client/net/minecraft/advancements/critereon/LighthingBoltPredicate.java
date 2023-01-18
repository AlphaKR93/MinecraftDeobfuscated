/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public class LighthingBoltPredicate
implements EntitySubPredicate {
    private static final String BLOCKS_SET_ON_FIRE_KEY = "blocks_set_on_fire";
    private static final String ENTITY_STRUCK_KEY = "entity_struck";
    private final MinMaxBounds.Ints blocksSetOnFire;
    private final EntityPredicate entityStruck;

    private LighthingBoltPredicate(MinMaxBounds.Ints $$0, EntityPredicate $$1) {
        this.blocksSetOnFire = $$0;
        this.entityStruck = $$1;
    }

    public static LighthingBoltPredicate blockSetOnFire(MinMaxBounds.Ints $$0) {
        return new LighthingBoltPredicate($$0, EntityPredicate.ANY);
    }

    public static LighthingBoltPredicate fromJson(JsonObject $$0) {
        return new LighthingBoltPredicate(MinMaxBounds.Ints.fromJson($$0.get(BLOCKS_SET_ON_FIRE_KEY)), EntityPredicate.fromJson($$0.get(ENTITY_STRUCK_KEY)));
    }

    @Override
    public JsonObject serializeCustomData() {
        JsonObject $$0 = new JsonObject();
        $$0.add(BLOCKS_SET_ON_FIRE_KEY, this.blocksSetOnFire.serializeToJson());
        $$0.add(ENTITY_STRUCK_KEY, this.entityStruck.serializeToJson());
        return $$0;
    }

    @Override
    public EntitySubPredicate.Type type() {
        return EntitySubPredicate.Types.LIGHTNING;
    }

    @Override
    public boolean matches(Entity $$0, ServerLevel $$1, @Nullable Vec3 $$22) {
        if (!($$0 instanceof LightningBolt)) {
            return false;
        }
        LightningBolt $$3 = (LightningBolt)$$0;
        return this.blocksSetOnFire.matches($$3.getBlocksSetOnFire()) && (this.entityStruck == EntityPredicate.ANY || $$3.getHitEntities().anyMatch($$2 -> this.entityStruck.matches($$1, $$22, (Entity)$$2)));
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.phys.Vec3;

public class SlimePredicate
implements EntitySubPredicate {
    private final MinMaxBounds.Ints size;

    private SlimePredicate(MinMaxBounds.Ints $$0) {
        this.size = $$0;
    }

    public static SlimePredicate sized(MinMaxBounds.Ints $$0) {
        return new SlimePredicate($$0);
    }

    public static SlimePredicate fromJson(JsonObject $$0) {
        MinMaxBounds.Ints $$1 = MinMaxBounds.Ints.fromJson($$0.get("size"));
        return new SlimePredicate($$1);
    }

    @Override
    public JsonObject serializeCustomData() {
        JsonObject $$0 = new JsonObject();
        $$0.add("size", this.size.serializeToJson());
        return $$0;
    }

    @Override
    public boolean matches(Entity $$0, ServerLevel $$1, @Nullable Vec3 $$2) {
        if ($$0 instanceof Slime) {
            Slime $$3 = (Slime)$$0;
            return this.size.matches($$3.getSize());
        }
        return false;
    }

    @Override
    public EntitySubPredicate.Type type() {
        return EntitySubPredicate.Types.SLIME;
    }
}
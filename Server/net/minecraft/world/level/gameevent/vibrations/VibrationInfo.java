/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
 *  java.lang.Object
 *  java.util.Optional
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record VibrationInfo(GameEvent gameEvent, float distance, Vec3 pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {
    public static final Codec<VibrationInfo> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BuiltInRegistries.GAME_EVENT.byNameCodec().fieldOf("game_event").forGetter(VibrationInfo::gameEvent), (App)Codec.floatRange((float)0.0f, (float)Float.MAX_VALUE).fieldOf("distance").forGetter(VibrationInfo::distance), (App)Vec3.CODEC.fieldOf("pos").forGetter(VibrationInfo::pos), (App)UUIDUtil.CODEC.optionalFieldOf("source").forGetter($$0 -> Optional.ofNullable((Object)$$0.uuid())), (App)UUIDUtil.CODEC.optionalFieldOf("projectile_owner").forGetter($$0 -> Optional.ofNullable((Object)$$0.projectileOwnerUuid()))).apply((Applicative)$$02, ($$0, $$1, $$2, $$3, $$4) -> new VibrationInfo((GameEvent)$$0, $$1.floatValue(), (Vec3)$$2, (UUID)$$3.orElse(null), (UUID)$$4.orElse(null))));

    public VibrationInfo(GameEvent $$0, float $$1, Vec3 $$2, @Nullable UUID $$3, @Nullable UUID $$4) {
        this($$0, $$1, $$2, $$3, $$4, null);
    }

    public VibrationInfo(GameEvent $$0, float $$1, Vec3 $$2, @Nullable Entity $$3) {
        this($$0, $$1, $$2, $$3 == null ? null : $$3.getUUID(), VibrationInfo.getProjectileOwner($$3), $$3);
    }

    @Nullable
    private static UUID getProjectileOwner(@Nullable Entity $$0) {
        Projectile $$1;
        if ($$0 instanceof Projectile && ($$1 = (Projectile)$$0).getOwner() != null) {
            return $$1.getOwner().getUUID();
        }
        return null;
    }

    public Optional<Entity> getEntity(ServerLevel $$0) {
        return Optional.ofNullable((Object)this.entity).or(() -> Optional.ofNullable((Object)this.uuid).map($$0::getEntity));
    }

    public Optional<Entity> getProjectileOwner(ServerLevel $$02) {
        return this.getEntity($$02).filter($$0 -> $$0 instanceof Projectile).map($$0 -> (Projectile)$$0).map(Projectile::getOwner).or(() -> Optional.ofNullable((Object)this.projectileOwnerUuid).map($$02::getEntity));
    }
}
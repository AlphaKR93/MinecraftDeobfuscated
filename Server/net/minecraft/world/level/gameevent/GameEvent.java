/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Comparable
 *  java.lang.Deprecated
 *  java.lang.Double
 *  java.lang.Object
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.gameevent;

import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.Vec3;

public class GameEvent {
    public static final GameEvent BLOCK_ACTIVATE = GameEvent.register("block_activate");
    public static final GameEvent BLOCK_ATTACH = GameEvent.register("block_attach");
    public static final GameEvent BLOCK_CHANGE = GameEvent.register("block_change");
    public static final GameEvent BLOCK_CLOSE = GameEvent.register("block_close");
    public static final GameEvent BLOCK_DEACTIVATE = GameEvent.register("block_deactivate");
    public static final GameEvent BLOCK_DESTROY = GameEvent.register("block_destroy");
    public static final GameEvent BLOCK_DETACH = GameEvent.register("block_detach");
    public static final GameEvent BLOCK_OPEN = GameEvent.register("block_open");
    public static final GameEvent BLOCK_PLACE = GameEvent.register("block_place");
    public static final GameEvent CONTAINER_CLOSE = GameEvent.register("container_close");
    public static final GameEvent CONTAINER_OPEN = GameEvent.register("container_open");
    public static final GameEvent DISPENSE_FAIL = GameEvent.register("dispense_fail");
    public static final GameEvent DRINK = GameEvent.register("drink");
    public static final GameEvent EAT = GameEvent.register("eat");
    public static final GameEvent ELYTRA_GLIDE = GameEvent.register("elytra_glide");
    public static final GameEvent ENTITY_DAMAGE = GameEvent.register("entity_damage");
    public static final GameEvent ENTITY_DIE = GameEvent.register("entity_die");
    public static final GameEvent ENTITY_INTERACT = GameEvent.register("entity_interact");
    public static final GameEvent ENTITY_PLACE = GameEvent.register("entity_place");
    public static final GameEvent ENTITY_ROAR = GameEvent.register("entity_roar");
    public static final GameEvent ENTITY_SHAKE = GameEvent.register("entity_shake");
    public static final GameEvent EQUIP = GameEvent.register("equip");
    public static final GameEvent EXPLODE = GameEvent.register("explode");
    public static final GameEvent FLAP = GameEvent.register("flap");
    public static final GameEvent FLUID_PICKUP = GameEvent.register("fluid_pickup");
    public static final GameEvent FLUID_PLACE = GameEvent.register("fluid_place");
    public static final GameEvent HIT_GROUND = GameEvent.register("hit_ground");
    public static final GameEvent INSTRUMENT_PLAY = GameEvent.register("instrument_play");
    public static final GameEvent ITEM_INTERACT_FINISH = GameEvent.register("item_interact_finish");
    public static final GameEvent ITEM_INTERACT_START = GameEvent.register("item_interact_start");
    public static final GameEvent JUKEBOX_PLAY = GameEvent.register("jukebox_play", 10);
    public static final GameEvent JUKEBOX_STOP_PLAY = GameEvent.register("jukebox_stop_play", 10);
    public static final GameEvent LIGHTNING_STRIKE = GameEvent.register("lightning_strike");
    public static final GameEvent NOTE_BLOCK_PLAY = GameEvent.register("note_block_play");
    public static final GameEvent PISTON_CONTRACT = GameEvent.register("piston_contract");
    public static final GameEvent PISTON_EXTEND = GameEvent.register("piston_extend");
    public static final GameEvent PRIME_FUSE = GameEvent.register("prime_fuse");
    public static final GameEvent PROJECTILE_LAND = GameEvent.register("projectile_land");
    public static final GameEvent PROJECTILE_SHOOT = GameEvent.register("projectile_shoot");
    public static final GameEvent SCULK_SENSOR_TENDRILS_CLICKING = GameEvent.register("sculk_sensor_tendrils_clicking");
    public static final GameEvent SHEAR = GameEvent.register("shear");
    public static final GameEvent SHRIEK = GameEvent.register("shriek", 32);
    public static final GameEvent SPLASH = GameEvent.register("splash");
    public static final GameEvent STEP = GameEvent.register("step");
    public static final GameEvent SWIM = GameEvent.register("swim");
    public static final GameEvent TELEPORT = GameEvent.register("teleport");
    public static final int DEFAULT_NOTIFICATION_RADIUS = 16;
    private final String name;
    private final int notificationRadius;
    private final Holder.Reference<GameEvent> builtInRegistryHolder = BuiltInRegistries.GAME_EVENT.createIntrusiveHolder(this);

    public GameEvent(String $$0, int $$1) {
        this.name = $$0;
        this.notificationRadius = $$1;
    }

    public String getName() {
        return this.name;
    }

    public int getNotificationRadius() {
        return this.notificationRadius;
    }

    private static GameEvent register(String $$0) {
        return GameEvent.register($$0, 16);
    }

    private static GameEvent register(String $$0, int $$1) {
        return Registry.register(BuiltInRegistries.GAME_EVENT, $$0, new GameEvent($$0, $$1));
    }

    public String toString() {
        return "Game Event{ " + this.name + " , " + this.notificationRadius + "}";
    }

    @Deprecated
    public Holder.Reference<GameEvent> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    public boolean is(TagKey<GameEvent> $$0) {
        return this.builtInRegistryHolder.is($$0);
    }

    public static final class ListenerInfo
    implements Comparable<ListenerInfo> {
        private final GameEvent gameEvent;
        private final Vec3 source;
        private final Context context;
        private final GameEventListener recipient;
        private final double distanceToRecipient;

        public ListenerInfo(GameEvent $$0, Vec3 $$1, Context $$2, GameEventListener $$3, Vec3 $$4) {
            this.gameEvent = $$0;
            this.source = $$1;
            this.context = $$2;
            this.recipient = $$3;
            this.distanceToRecipient = $$1.distanceToSqr($$4);
        }

        public int compareTo(ListenerInfo $$0) {
            return Double.compare((double)this.distanceToRecipient, (double)$$0.distanceToRecipient);
        }

        public GameEvent gameEvent() {
            return this.gameEvent;
        }

        public Vec3 source() {
            return this.source;
        }

        public Context context() {
            return this.context;
        }

        public GameEventListener recipient() {
            return this.recipient;
        }
    }

    public record Context(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
        public static Context of(@Nullable Entity $$0) {
            return new Context($$0, null);
        }

        public static Context of(@Nullable BlockState $$0) {
            return new Context(null, $$0);
        }

        public static Context of(@Nullable Entity $$0, @Nullable BlockState $$1) {
            return new Context($$0, $$1);
        }
    }
}
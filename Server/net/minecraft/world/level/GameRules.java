/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicLike
 *  java.lang.Boolean
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Comparator
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.function.BiConsumer
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class GameRules {
    public static final int DEFAULT_RANDOM_TICK_SPEED = 3;
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Key<?>, Type<?>> GAME_RULE_TYPES = Maps.newTreeMap((Comparator)Comparator.comparing($$0 -> $$0.id));
    public static final Key<BooleanValue> RULE_DOFIRETICK = GameRules.register("doFireTick", Category.UPDATES, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_MOBGRIEFING = GameRules.register("mobGriefing", Category.MOBS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_KEEPINVENTORY = GameRules.register("keepInventory", Category.PLAYER, BooleanValue.create(false));
    public static final Key<BooleanValue> RULE_DOMOBSPAWNING = GameRules.register("doMobSpawning", Category.SPAWNING, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DOMOBLOOT = GameRules.register("doMobLoot", Category.DROPS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DOBLOCKDROPS = GameRules.register("doTileDrops", Category.DROPS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DOENTITYDROPS = GameRules.register("doEntityDrops", Category.DROPS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_COMMANDBLOCKOUTPUT = GameRules.register("commandBlockOutput", Category.CHAT, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_NATURAL_REGENERATION = GameRules.register("naturalRegeneration", Category.PLAYER, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DAYLIGHT = GameRules.register("doDaylightCycle", Category.UPDATES, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_LOGADMINCOMMANDS = GameRules.register("logAdminCommands", Category.CHAT, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_SHOWDEATHMESSAGES = GameRules.register("showDeathMessages", Category.CHAT, BooleanValue.create(true));
    public static final Key<IntegerValue> RULE_RANDOMTICKING = GameRules.register("randomTickSpeed", Category.UPDATES, IntegerValue.create(3));
    public static final Key<BooleanValue> RULE_SENDCOMMANDFEEDBACK = GameRules.register("sendCommandFeedback", Category.CHAT, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_REDUCEDDEBUGINFO = GameRules.register("reducedDebugInfo", Category.MISC, BooleanValue.create(false, (BiConsumer<MinecraftServer, BooleanValue>)((BiConsumer)($$0, $$1) -> {
        byte $$2 = $$1.get() ? (byte)22 : (byte)23;
        for (ServerPlayer $$3 : $$0.getPlayerList().getPlayers()) {
            $$3.connection.send(new ClientboundEntityEventPacket((Entity)((Object)$$3), $$2));
        }
    })));
    public static final Key<BooleanValue> RULE_SPECTATORSGENERATECHUNKS = GameRules.register("spectatorsGenerateChunks", Category.PLAYER, BooleanValue.create(true));
    public static final Key<IntegerValue> RULE_SPAWN_RADIUS = GameRules.register("spawnRadius", Category.PLAYER, IntegerValue.create(10));
    public static final Key<BooleanValue> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = GameRules.register("disableElytraMovementCheck", Category.PLAYER, BooleanValue.create(false));
    public static final Key<IntegerValue> RULE_MAX_ENTITY_CRAMMING = GameRules.register("maxEntityCramming", Category.MOBS, IntegerValue.create(24));
    public static final Key<BooleanValue> RULE_WEATHER_CYCLE = GameRules.register("doWeatherCycle", Category.UPDATES, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_LIMITED_CRAFTING = GameRules.register("doLimitedCrafting", Category.PLAYER, BooleanValue.create(false));
    public static final Key<IntegerValue> RULE_MAX_COMMAND_CHAIN_LENGTH = GameRules.register("maxCommandChainLength", Category.MISC, IntegerValue.create(65536));
    public static final Key<BooleanValue> RULE_ANNOUNCE_ADVANCEMENTS = GameRules.register("announceAdvancements", Category.CHAT, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DISABLE_RAIDS = GameRules.register("disableRaids", Category.MOBS, BooleanValue.create(false));
    public static final Key<BooleanValue> RULE_DOINSOMNIA = GameRules.register("doInsomnia", Category.SPAWNING, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DO_IMMEDIATE_RESPAWN = GameRules.register("doImmediateRespawn", Category.PLAYER, BooleanValue.create(false, (BiConsumer<MinecraftServer, BooleanValue>)((BiConsumer)($$0, $$1) -> {
        for (ServerPlayer $$2 : $$0.getPlayerList().getPlayers()) {
            $$2.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.IMMEDIATE_RESPAWN, $$1.get() ? 1.0f : 0.0f));
        }
    })));
    public static final Key<BooleanValue> RULE_DROWNING_DAMAGE = GameRules.register("drowningDamage", Category.PLAYER, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_FALL_DAMAGE = GameRules.register("fallDamage", Category.PLAYER, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_FIRE_DAMAGE = GameRules.register("fireDamage", Category.PLAYER, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_FREEZE_DAMAGE = GameRules.register("freezeDamage", Category.PLAYER, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DO_PATROL_SPAWNING = GameRules.register("doPatrolSpawning", Category.SPAWNING, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DO_TRADER_SPAWNING = GameRules.register("doTraderSpawning", Category.SPAWNING, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DO_WARDEN_SPAWNING = GameRules.register("doWardenSpawning", Category.SPAWNING, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_FORGIVE_DEAD_PLAYERS = GameRules.register("forgiveDeadPlayers", Category.MOBS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_UNIVERSAL_ANGER = GameRules.register("universalAnger", Category.MOBS, BooleanValue.create(false));
    public static final Key<IntegerValue> RULE_PLAYERS_SLEEPING_PERCENTAGE = GameRules.register("playersSleepingPercentage", Category.PLAYER, IntegerValue.create(100));
    public static final Key<BooleanValue> RULE_BLOCK_EXPLOSION_DROP_DECAY = GameRules.register("blockExplosionDropDecay", Category.DROPS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_MOB_EXPLOSION_DROP_DECAY = GameRules.register("mobExplosionDropDecay", Category.DROPS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_TNT_EXPLOSION_DROP_DECAY = GameRules.register("tntExplosionDropDecay", Category.DROPS, BooleanValue.create(false));
    public static final Key<IntegerValue> RULE_SNOW_ACCUMULATION_HEIGHT = GameRules.register("snowAccumulationHeight", Category.UPDATES, IntegerValue.create(1));
    public static final Key<BooleanValue> RULE_WATER_SOURCE_CONVERSION = GameRules.register("waterSourceConversion", Category.UPDATES, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_LAVA_SOURCE_CONVERSION = GameRules.register("lavaSourceConversion", Category.UPDATES, BooleanValue.create(false));
    public static final Key<BooleanValue> RULE_GLOBAL_SOUND_EVENTS = GameRules.register("globalSoundEvents", Category.MISC, BooleanValue.create(true));
    private final Map<Key<?>, Value<?>> rules;

    private static <T extends Value<T>> Key<T> register(String $$0, Category $$1, Type<T> $$2) {
        Key $$3 = new Key($$0, $$1);
        Type $$4 = (Type)GAME_RULE_TYPES.put($$3, $$2);
        if ($$4 != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + $$0);
        }
        return $$3;
    }

    public GameRules(DynamicLike<?> $$0) {
        this();
        this.loadFromTag($$0);
    }

    public GameRules() {
        this.rules = (Map)GAME_RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, $$0 -> ((Type)$$0.getValue()).createRule()));
    }

    private GameRules(Map<Key<?>, Value<?>> $$0) {
        this.rules = $$0;
    }

    public <T extends Value<T>> T getRule(Key<T> $$0) {
        return (T)((Value)this.rules.get($$0));
    }

    public CompoundTag createTag() {
        CompoundTag $$0 = new CompoundTag();
        this.rules.forEach(($$1, $$2) -> $$0.putString($$1.id, $$2.serialize()));
        return $$0;
    }

    private void loadFromTag(DynamicLike<?> $$0) {
        this.rules.forEach(($$1, $$2) -> $$0.get($$1.id).asString().result().ifPresent($$2::deserialize));
    }

    public GameRules copy() {
        return new GameRules((Map)this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, $$0 -> ((Value)$$0.getValue()).copy())));
    }

    public static void visitGameRuleTypes(GameRuleTypeVisitor $$0) {
        GAME_RULE_TYPES.forEach(($$1, $$2) -> GameRules.callVisitorCap($$0, $$1, $$2));
    }

    private static <T extends Value<T>> void callVisitorCap(GameRuleTypeVisitor $$0, Key<?> $$1, Type<?> $$2) {
        Key<?> $$3 = $$1;
        Type<?> $$4 = $$2;
        $$0.visit($$3, $$4);
        $$4.callVisitor($$0, $$3);
    }

    public void assignFrom(GameRules $$0, @Nullable MinecraftServer $$1) {
        $$0.rules.keySet().forEach($$2 -> this.assignCap((Key)$$2, $$0, $$1));
    }

    private <T extends Value<T>> void assignCap(Key<T> $$0, GameRules $$1, @Nullable MinecraftServer $$2) {
        T $$3 = $$1.getRule($$0);
        ((Value)this.getRule($$0)).setFrom($$3, $$2);
    }

    public boolean getBoolean(Key<BooleanValue> $$0) {
        return this.getRule($$0).get();
    }

    public int getInt(Key<IntegerValue> $$0) {
        return this.getRule($$0).get();
    }

    public static final class Key<T extends Value<T>> {
        final String id;
        private final Category category;

        public Key(String $$0, Category $$1) {
            this.id = $$0;
            this.category = $$1;
        }

        public String toString() {
            return this.id;
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            return $$0 instanceof Key && ((Key)$$0).id.equals((Object)this.id);
        }

        public int hashCode() {
            return this.id.hashCode();
        }

        public String getId() {
            return this.id;
        }

        public String getDescriptionId() {
            return "gamerule." + this.id;
        }

        public Category getCategory() {
            return this.category;
        }
    }

    public static enum Category {
        PLAYER("gamerule.category.player"),
        MOBS("gamerule.category.mobs"),
        SPAWNING("gamerule.category.spawning"),
        DROPS("gamerule.category.drops"),
        UPDATES("gamerule.category.updates"),
        CHAT("gamerule.category.chat"),
        MISC("gamerule.category.misc");

        private final String descriptionId;

        private Category(String $$0) {
            this.descriptionId = $$0;
        }

        public String getDescriptionId() {
            return this.descriptionId;
        }
    }

    public static class Type<T extends Value<T>> {
        private final Supplier<ArgumentType<?>> argument;
        private final Function<Type<T>, T> constructor;
        final BiConsumer<MinecraftServer, T> callback;
        private final VisitorCaller<T> visitorCaller;

        Type(Supplier<ArgumentType<?>> $$0, Function<Type<T>, T> $$1, BiConsumer<MinecraftServer, T> $$2, VisitorCaller<T> $$3) {
            this.argument = $$0;
            this.constructor = $$1;
            this.callback = $$2;
            this.visitorCaller = $$3;
        }

        public RequiredArgumentBuilder<CommandSourceStack, ?> createArgument(String $$0) {
            return Commands.argument($$0, (ArgumentType)this.argument.get());
        }

        public T createRule() {
            return (T)((Value)this.constructor.apply((Object)this));
        }

        public void callVisitor(GameRuleTypeVisitor $$0, Key<T> $$1) {
            this.visitorCaller.call($$0, $$1, this);
        }
    }

    public static abstract class Value<T extends Value<T>> {
        protected final Type<T> type;

        public Value(Type<T> $$0) {
            this.type = $$0;
        }

        protected abstract void updateFromArgument(CommandContext<CommandSourceStack> var1, String var2);

        public void setFromArgument(CommandContext<CommandSourceStack> $$0, String $$1) {
            this.updateFromArgument($$0, $$1);
            this.onChanged(((CommandSourceStack)$$0.getSource()).getServer());
        }

        protected void onChanged(@Nullable MinecraftServer $$0) {
            if ($$0 != null) {
                this.type.callback.accept((Object)$$0, this.getSelf());
            }
        }

        protected abstract void deserialize(String var1);

        public abstract String serialize();

        public String toString() {
            return this.serialize();
        }

        public abstract int getCommandResult();

        protected abstract T getSelf();

        protected abstract T copy();

        public abstract void setFrom(T var1, @Nullable MinecraftServer var2);
    }

    public static interface GameRuleTypeVisitor {
        default public <T extends Value<T>> void visit(Key<T> $$0, Type<T> $$1) {
        }

        default public void visitBoolean(Key<BooleanValue> $$0, Type<BooleanValue> $$1) {
        }

        default public void visitInteger(Key<IntegerValue> $$0, Type<IntegerValue> $$1) {
        }
    }

    public static class BooleanValue
    extends Value<BooleanValue> {
        private boolean value;

        static Type<BooleanValue> create(boolean $$0, BiConsumer<MinecraftServer, BooleanValue> $$12) {
            return new Type<BooleanValue>(BoolArgumentType::bool, $$1 -> new BooleanValue((Type<BooleanValue>)$$1, $$0), $$12, GameRuleTypeVisitor::visitBoolean);
        }

        static Type<BooleanValue> create(boolean $$02) {
            return BooleanValue.create($$02, (BiConsumer<MinecraftServer, BooleanValue>)((BiConsumer)($$0, $$1) -> {}));
        }

        public BooleanValue(Type<BooleanValue> $$0, boolean $$1) {
            super($$0);
            this.value = $$1;
        }

        @Override
        protected void updateFromArgument(CommandContext<CommandSourceStack> $$0, String $$1) {
            this.value = BoolArgumentType.getBool($$0, (String)$$1);
        }

        public boolean get() {
            return this.value;
        }

        public void set(boolean $$0, @Nullable MinecraftServer $$1) {
            this.value = $$0;
            this.onChanged($$1);
        }

        @Override
        public String serialize() {
            return Boolean.toString((boolean)this.value);
        }

        @Override
        protected void deserialize(String $$0) {
            this.value = Boolean.parseBoolean((String)$$0);
        }

        @Override
        public int getCommandResult() {
            return this.value ? 1 : 0;
        }

        @Override
        protected BooleanValue getSelf() {
            return this;
        }

        @Override
        protected BooleanValue copy() {
            return new BooleanValue(this.type, this.value);
        }

        @Override
        public void setFrom(BooleanValue $$0, @Nullable MinecraftServer $$1) {
            this.value = $$0.value;
            this.onChanged($$1);
        }
    }

    public static class IntegerValue
    extends Value<IntegerValue> {
        private int value;

        private static Type<IntegerValue> create(int $$0, BiConsumer<MinecraftServer, IntegerValue> $$12) {
            return new Type<IntegerValue>(IntegerArgumentType::integer, $$1 -> new IntegerValue((Type<IntegerValue>)$$1, $$0), $$12, GameRuleTypeVisitor::visitInteger);
        }

        static Type<IntegerValue> create(int $$02) {
            return IntegerValue.create($$02, (BiConsumer<MinecraftServer, IntegerValue>)((BiConsumer)($$0, $$1) -> {}));
        }

        public IntegerValue(Type<IntegerValue> $$0, int $$1) {
            super($$0);
            this.value = $$1;
        }

        @Override
        protected void updateFromArgument(CommandContext<CommandSourceStack> $$0, String $$1) {
            this.value = IntegerArgumentType.getInteger($$0, (String)$$1);
        }

        public int get() {
            return this.value;
        }

        public void set(int $$0, @Nullable MinecraftServer $$1) {
            this.value = $$0;
            this.onChanged($$1);
        }

        @Override
        public String serialize() {
            return Integer.toString((int)this.value);
        }

        @Override
        protected void deserialize(String $$0) {
            this.value = IntegerValue.safeParse($$0);
        }

        public boolean tryDeserialize(String $$0) {
            try {
                this.value = Integer.parseInt((String)$$0);
                return true;
            }
            catch (NumberFormatException numberFormatException) {
                return false;
            }
        }

        private static int safeParse(String $$0) {
            if (!$$0.isEmpty()) {
                try {
                    return Integer.parseInt((String)$$0);
                }
                catch (NumberFormatException $$1) {
                    LOGGER.warn("Failed to parse integer {}", (Object)$$0);
                }
            }
            return 0;
        }

        @Override
        public int getCommandResult() {
            return this.value;
        }

        @Override
        protected IntegerValue getSelf() {
            return this;
        }

        @Override
        protected IntegerValue copy() {
            return new IntegerValue(this.type, this.value);
        }

        @Override
        public void setFrom(IntegerValue $$0, @Nullable MinecraftServer $$1) {
            this.value = $$0.value;
            this.onChanged($$1);
        }
    }

    static interface VisitorCaller<T extends Value<T>> {
        public void call(GameRuleTypeVisitor var1, Key<T> var2, Type<T> var3);
    }
}
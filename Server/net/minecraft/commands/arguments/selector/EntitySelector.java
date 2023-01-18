/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.Class
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.List
 *  java.util.UUID
 *  java.util.function.BiConsumer
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments.selector;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntitySelector {
    public static final int INFINITE = Integer.MAX_VALUE;
    public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_ARBITRARY = ($$0, $$1) -> {};
    private static final EntityTypeTest<Entity, ?> ANY_TYPE = new EntityTypeTest<Entity, Entity>(){

        @Override
        public Entity tryCast(Entity $$0) {
            return $$0;
        }

        @Override
        public Class<? extends Entity> getBaseClass() {
            return Entity.class;
        }
    };
    private final int maxResults;
    private final boolean includesEntities;
    private final boolean worldLimited;
    private final Predicate<Entity> predicate;
    private final MinMaxBounds.Doubles range;
    private final Function<Vec3, Vec3> position;
    @Nullable
    private final AABB aabb;
    private final BiConsumer<Vec3, List<? extends Entity>> order;
    private final boolean currentEntity;
    @Nullable
    private final String playerName;
    @Nullable
    private final UUID entityUUID;
    private final EntityTypeTest<Entity, ?> type;
    private final boolean usesSelector;

    public EntitySelector(int $$0, boolean $$1, boolean $$2, Predicate<Entity> $$3, MinMaxBounds.Doubles $$4, Function<Vec3, Vec3> $$5, @Nullable AABB $$6, BiConsumer<Vec3, List<? extends Entity>> $$7, boolean $$8, @Nullable String $$9, @Nullable UUID $$10, @Nullable EntityType<?> $$11, boolean $$12) {
        this.maxResults = $$0;
        this.includesEntities = $$1;
        this.worldLimited = $$2;
        this.predicate = $$3;
        this.range = $$4;
        this.position = $$5;
        this.aabb = $$6;
        this.order = $$7;
        this.currentEntity = $$8;
        this.playerName = $$9;
        this.entityUUID = $$10;
        this.type = $$11 == null ? ANY_TYPE : $$11;
        this.usesSelector = $$12;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public boolean includesEntities() {
        return this.includesEntities;
    }

    public boolean isSelfSelector() {
        return this.currentEntity;
    }

    public boolean isWorldLimited() {
        return this.worldLimited;
    }

    public boolean usesSelector() {
        return this.usesSelector;
    }

    private void checkPermissions(CommandSourceStack $$0) throws CommandSyntaxException {
        if (this.usesSelector && !$$0.hasPermission(2)) {
            throw EntityArgument.ERROR_SELECTORS_NOT_ALLOWED.create();
        }
    }

    public Entity findSingleEntity(CommandSourceStack $$0) throws CommandSyntaxException {
        this.checkPermissions($$0);
        List<? extends Entity> $$1 = this.findEntities($$0);
        if ($$1.isEmpty()) {
            throw EntityArgument.NO_ENTITIES_FOUND.create();
        }
        if ($$1.size() > 1) {
            throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
        }
        return (Entity)$$1.get(0);
    }

    public List<? extends Entity> findEntities(CommandSourceStack $$0) throws CommandSyntaxException {
        return this.findEntitiesRaw($$0).stream().filter($$1 -> $$1.getType().isEnabled($$0.enabledFeatures())).toList();
    }

    private List<? extends Entity> findEntitiesRaw(CommandSourceStack $$0) throws CommandSyntaxException {
        this.checkPermissions($$0);
        if (!this.includesEntities) {
            return this.findPlayers($$0);
        }
        if (this.playerName != null) {
            ServerPlayer $$1 = $$0.getServer().getPlayerList().getPlayerByName(this.playerName);
            if ($$1 == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayer[]{$$1});
        }
        if (this.entityUUID != null) {
            for (ServerLevel $$2 : $$0.getServer().getAllLevels()) {
                Entity $$3 = $$2.getEntity(this.entityUUID);
                if ($$3 == null) continue;
                return Lists.newArrayList((Object[])new Entity[]{$$3});
            }
            return Collections.emptyList();
        }
        Vec3 $$4 = (Vec3)this.position.apply((Object)$$0.getPosition());
        Predicate<Entity> $$5 = this.getPredicate($$4);
        if (this.currentEntity) {
            if ($$0.getEntity() != null && $$5.test((Object)$$0.getEntity())) {
                return Lists.newArrayList((Object[])new Entity[]{$$0.getEntity()});
            }
            return Collections.emptyList();
        }
        ArrayList $$6 = Lists.newArrayList();
        if (this.isWorldLimited()) {
            this.addEntities((List<Entity>)$$6, $$0.getLevel(), $$4, $$5);
        } else {
            for (ServerLevel $$7 : $$0.getServer().getAllLevels()) {
                this.addEntities((List<Entity>)$$6, $$7, $$4, $$5);
            }
        }
        return this.sortAndLimit($$4, (List)$$6);
    }

    private void addEntities(List<Entity> $$0, ServerLevel $$1, Vec3 $$2, Predicate<Entity> $$3) {
        int $$4 = this.getResultLimit();
        if ($$0.size() >= $$4) {
            return;
        }
        if (this.aabb != null) {
            $$1.getEntities(this.type, this.aabb.move($$2), $$3, $$0, $$4);
        } else {
            $$1.getEntities(this.type, $$3, $$0, $$4);
        }
    }

    private int getResultLimit() {
        return this.order == ORDER_ARBITRARY ? this.maxResults : Integer.MAX_VALUE;
    }

    public ServerPlayer findSinglePlayer(CommandSourceStack $$0) throws CommandSyntaxException {
        this.checkPermissions($$0);
        List<ServerPlayer> $$1 = this.findPlayers($$0);
        if ($$1.size() != 1) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
        }
        return (ServerPlayer)$$1.get(0);
    }

    public List<ServerPlayer> findPlayers(CommandSourceStack $$0) throws CommandSyntaxException {
        ArrayList $$8;
        this.checkPermissions($$0);
        if (this.playerName != null) {
            ServerPlayer $$1 = $$0.getServer().getPlayerList().getPlayerByName(this.playerName);
            if ($$1 == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayer[]{$$1});
        }
        if (this.entityUUID != null) {
            ServerPlayer $$2 = $$0.getServer().getPlayerList().getPlayer(this.entityUUID);
            if ($$2 == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayer[]{$$2});
        }
        Vec3 $$3 = (Vec3)this.position.apply((Object)$$0.getPosition());
        Predicate<Entity> $$4 = this.getPredicate($$3);
        if (this.currentEntity) {
            ServerPlayer $$5;
            if ($$0.getEntity() instanceof ServerPlayer && $$4.test((Object)($$5 = (ServerPlayer)$$0.getEntity()))) {
                return Lists.newArrayList((Object[])new ServerPlayer[]{$$5});
            }
            return Collections.emptyList();
        }
        int $$6 = this.getResultLimit();
        if (this.isWorldLimited()) {
            List<ServerPlayer> $$7 = $$0.getLevel().getPlayers($$4, $$6);
        } else {
            $$8 = Lists.newArrayList();
            for (ServerPlayer $$9 : $$0.getServer().getPlayerList().getPlayers()) {
                if (!$$4.test((Object)$$9)) continue;
                $$8.add((Object)$$9);
                if ($$8.size() < $$6) continue;
                return $$8;
            }
        }
        return this.sortAndLimit($$3, (List)$$8);
    }

    private Predicate<Entity> getPredicate(Vec3 $$0) {
        Predicate $$12 = this.predicate;
        if (this.aabb != null) {
            AABB $$2 = this.aabb.move($$0);
            $$12 = $$12.and($$1 -> $$2.intersects($$1.getBoundingBox()));
        }
        if (!this.range.isAny()) {
            $$12 = $$12.and($$1 -> this.range.matchesSqr($$1.distanceToSqr($$0)));
        }
        return $$12;
    }

    private <T extends Entity> List<T> sortAndLimit(Vec3 $$0, List<T> $$1) {
        if ($$1.size() > 1) {
            this.order.accept((Object)$$0, $$1);
        }
        return $$1.subList(0, Math.min((int)this.maxResults, (int)$$1.size()));
    }

    public static Component joinNames(List<? extends Entity> $$0) {
        return ComponentUtils.formatList($$0, Entity::getDisplayName);
    }
}
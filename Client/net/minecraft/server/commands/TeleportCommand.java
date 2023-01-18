/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.EnumSet
 *  java.util.Locale
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class TeleportCommand {
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType((Message)Component.translatable("commands.teleport.invalidPosition"));

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        LiteralCommandNode $$1 = $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("teleport").requires($$0 -> $$0.hasPermission(2))).then(Commands.argument("location", Vec3Argument.vec3()).executes($$0 -> TeleportCommand.teleportToPos((CommandSourceStack)$$0.getSource(), (Collection<? extends Entity>)Collections.singleton((Object)((CommandSourceStack)$$0.getSource()).getEntityOrException()), ((CommandSourceStack)$$0.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)$$0, "location"), WorldCoordinates.current(), null)))).then(Commands.argument("destination", EntityArgument.entity()).executes($$0 -> TeleportCommand.teleportToEntity((CommandSourceStack)$$0.getSource(), (Collection<? extends Entity>)Collections.singleton((Object)((CommandSourceStack)$$0.getSource()).getEntityOrException()), EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "destination"))))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("location", Vec3Argument.vec3()).executes($$0 -> TeleportCommand.teleportToPos((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets"), ((CommandSourceStack)$$0.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)$$0, "location"), null, null))).then(Commands.argument("rotation", RotationArgument.rotation()).executes($$0 -> TeleportCommand.teleportToPos((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets"), ((CommandSourceStack)$$0.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)$$0, "location"), RotationArgument.getRotation((CommandContext<CommandSourceStack>)$$0, "rotation"), null)))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("facingEntity", EntityArgument.entity()).executes($$0 -> TeleportCommand.teleportToPos((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets"), ((CommandSourceStack)$$0.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)$$0, "location"), null, new LookAt(EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "facingEntity"), EntityAnchorArgument.Anchor.FEET)))).then(Commands.argument("facingAnchor", EntityAnchorArgument.anchor()).executes($$0 -> TeleportCommand.teleportToPos((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets"), ((CommandSourceStack)$$0.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)$$0, "location"), null, new LookAt(EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "facingEntity"), EntityAnchorArgument.getAnchor((CommandContext<CommandSourceStack>)$$0, "facingAnchor")))))))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes($$0 -> TeleportCommand.teleportToPos((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets"), ((CommandSourceStack)$$0.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)$$0, "location"), null, new LookAt(Vec3Argument.getVec3((CommandContext<CommandSourceStack>)$$0, "facingLocation")))))))).then(Commands.argument("destination", EntityArgument.entity()).executes($$0 -> TeleportCommand.teleportToEntity((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets"), EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "destination"))))));
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tp").requires($$0 -> $$0.hasPermission(2))).redirect((CommandNode)$$1));
    }

    private static int teleportToEntity(CommandSourceStack $$0, Collection<? extends Entity> $$1, Entity $$2) throws CommandSyntaxException {
        for (Entity $$3 : $$1) {
            TeleportCommand.performTeleport($$0, $$3, (ServerLevel)$$2.level, $$2.getX(), $$2.getY(), $$2.getZ(), (Set<ClientboundPlayerPositionPacket.RelativeArgument>)EnumSet.noneOf(ClientboundPlayerPositionPacket.RelativeArgument.class), $$2.getYRot(), $$2.getXRot(), null);
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.teleport.success.entity.single", ((Entity)$$1.iterator().next()).getDisplayName(), $$2.getDisplayName()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.teleport.success.entity.multiple", $$1.size(), $$2.getDisplayName()), true);
        }
        return $$1.size();
    }

    private static int teleportToPos(CommandSourceStack $$0, Collection<? extends Entity> $$1, ServerLevel $$2, Coordinates $$3, @Nullable Coordinates $$4, @Nullable LookAt $$5) throws CommandSyntaxException {
        Vec3 $$6 = $$3.getPosition($$0);
        Vec2 $$7 = $$4 == null ? null : $$4.getRotation($$0);
        EnumSet $$8 = EnumSet.noneOf(ClientboundPlayerPositionPacket.RelativeArgument.class);
        if ($$3.isXRelative()) {
            $$8.add((Object)ClientboundPlayerPositionPacket.RelativeArgument.X);
        }
        if ($$3.isYRelative()) {
            $$8.add((Object)ClientboundPlayerPositionPacket.RelativeArgument.Y);
        }
        if ($$3.isZRelative()) {
            $$8.add((Object)ClientboundPlayerPositionPacket.RelativeArgument.Z);
        }
        if ($$4 == null) {
            $$8.add((Object)ClientboundPlayerPositionPacket.RelativeArgument.X_ROT);
            $$8.add((Object)ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT);
        } else {
            if ($$4.isXRelative()) {
                $$8.add((Object)ClientboundPlayerPositionPacket.RelativeArgument.X_ROT);
            }
            if ($$4.isYRelative()) {
                $$8.add((Object)ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT);
            }
        }
        for (Entity $$9 : $$1) {
            if ($$4 == null) {
                TeleportCommand.performTeleport($$0, $$9, $$2, $$6.x, $$6.y, $$6.z, (Set<ClientboundPlayerPositionPacket.RelativeArgument>)$$8, $$9.getYRot(), $$9.getXRot(), $$5);
                continue;
            }
            TeleportCommand.performTeleport($$0, $$9, $$2, $$6.x, $$6.y, $$6.z, (Set<ClientboundPlayerPositionPacket.RelativeArgument>)$$8, $$7.y, $$7.x, $$5);
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.teleport.success.location.single", ((Entity)$$1.iterator().next()).getDisplayName(), TeleportCommand.formatDouble($$6.x), TeleportCommand.formatDouble($$6.y), TeleportCommand.formatDouble($$6.z)), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.teleport.success.location.multiple", $$1.size(), TeleportCommand.formatDouble($$6.x), TeleportCommand.formatDouble($$6.y), TeleportCommand.formatDouble($$6.z)), true);
        }
        return $$1.size();
    }

    private static String formatDouble(double $$0) {
        return String.format((Locale)Locale.ROOT, (String)"%f", (Object[])new Object[]{$$0});
    }

    private static void performTeleport(CommandSourceStack $$0, Entity $$1, ServerLevel $$2, double $$3, double $$4, double $$5, Set<ClientboundPlayerPositionPacket.RelativeArgument> $$6, float $$7, float $$8, @Nullable LookAt $$9) throws CommandSyntaxException {
        BlockPos $$10 = new BlockPos($$3, $$4, $$5);
        if (!Level.isInSpawnableBounds($$10)) {
            throw INVALID_POSITION.create();
        }
        float $$11 = Mth.wrapDegrees($$7);
        float $$12 = Mth.wrapDegrees($$8);
        if ($$1 instanceof ServerPlayer) {
            ChunkPos $$13 = new ChunkPos(new BlockPos($$3, $$4, $$5));
            $$2.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, $$13, 1, $$1.getId());
            $$1.stopRiding();
            if (((ServerPlayer)$$1).isSleeping()) {
                ((ServerPlayer)$$1).stopSleepInBed(true, true);
            }
            if ($$2 == $$1.level) {
                ((ServerPlayer)$$1).connection.teleport($$3, $$4, $$5, $$11, $$12, $$6);
            } else {
                ((ServerPlayer)$$1).teleportTo($$2, $$3, $$4, $$5, $$11, $$12);
            }
            $$1.setYHeadRot($$11);
        } else {
            float $$14 = Mth.clamp($$12, -90.0f, 90.0f);
            if ($$2 == $$1.level) {
                $$1.moveTo($$3, $$4, $$5, $$11, $$14);
                $$1.setYHeadRot($$11);
            } else {
                $$1.unRide();
                Entity $$15 = $$1;
                $$1 = $$15.getType().create($$2);
                if ($$1 != null) {
                    $$1.restoreFrom($$15);
                    $$1.moveTo($$3, $$4, $$5, $$11, $$14);
                    $$1.setYHeadRot($$11);
                    $$15.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                    $$2.addDuringTeleport($$1);
                } else {
                    return;
                }
            }
        }
        if ($$9 != null) {
            $$9.perform($$0, $$1);
        }
        if (!($$1 instanceof LivingEntity) || !((LivingEntity)$$1).isFallFlying()) {
            $$1.setDeltaMovement($$1.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            $$1.setOnGround(true);
        }
        if ($$1 instanceof PathfinderMob) {
            ((PathfinderMob)$$1).getNavigation().stop();
        }
    }

    static class LookAt {
        private final Vec3 position;
        private final Entity entity;
        private final EntityAnchorArgument.Anchor anchor;

        public LookAt(Entity $$0, EntityAnchorArgument.Anchor $$1) {
            this.entity = $$0;
            this.anchor = $$1;
            this.position = $$1.apply($$0);
        }

        public LookAt(Vec3 $$0) {
            this.entity = null;
            this.position = $$0;
            this.anchor = null;
        }

        public void perform(CommandSourceStack $$0, Entity $$1) {
            if (this.entity != null) {
                if ($$1 instanceof ServerPlayer) {
                    ((ServerPlayer)$$1).lookAt($$0.getAnchor(), this.entity, this.anchor);
                } else {
                    $$1.lookAt($$0.getAnchor(), this.position);
                }
            } else {
                $$1.lookAt($$0.getAnchor(), this.position);
            }
        }
    }
}
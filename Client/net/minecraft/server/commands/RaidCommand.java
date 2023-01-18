/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.phys.Vec3;

public class RaidCommand {
    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("raid").requires($$0 -> $$0.hasPermission(3))).then(Commands.literal("start").then(Commands.argument("omenlvl", IntegerArgumentType.integer((int)0)).executes($$0 -> RaidCommand.start((CommandSourceStack)$$0.getSource(), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"omenlvl")))))).then(Commands.literal("stop").executes($$0 -> RaidCommand.stop((CommandSourceStack)$$0.getSource())))).then(Commands.literal("check").executes($$0 -> RaidCommand.check((CommandSourceStack)$$0.getSource())))).then(Commands.literal("sound").then(Commands.argument("type", ComponentArgument.textComponent()).executes($$0 -> RaidCommand.playSound((CommandSourceStack)$$0.getSource(), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)$$0, "type")))))).then(Commands.literal("spawnleader").executes($$0 -> RaidCommand.spawnLeader((CommandSourceStack)$$0.getSource())))).then(Commands.literal("setomen").then(Commands.argument("level", IntegerArgumentType.integer((int)0)).executes($$0 -> RaidCommand.setBadOmenLevel((CommandSourceStack)$$0.getSource(), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"level")))))).then(Commands.literal("glow").executes($$0 -> RaidCommand.glow((CommandSourceStack)$$0.getSource()))));
    }

    private static int glow(CommandSourceStack $$0) throws CommandSyntaxException {
        Raid $$1 = RaidCommand.getRaid($$0.getPlayerOrException());
        if ($$1 != null) {
            Set<Raider> $$2 = $$1.getAllRaiders();
            for (Raider $$3 : $$2) {
                $$3.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1000, 1));
            }
        }
        return 1;
    }

    private static int setBadOmenLevel(CommandSourceStack $$0, int $$1) throws CommandSyntaxException {
        Raid $$2 = RaidCommand.getRaid($$0.getPlayerOrException());
        if ($$2 != null) {
            int $$3 = $$2.getMaxBadOmenLevel();
            if ($$1 > $$3) {
                $$0.sendFailure(Component.literal("Sorry, the max bad omen level you can set is " + $$3));
            } else {
                int $$4 = $$2.getBadOmenLevel();
                $$2.setBadOmenLevel($$1);
                $$0.sendSuccess(Component.literal("Changed village's bad omen level from " + $$4 + " to " + $$1), false);
            }
        } else {
            $$0.sendFailure(Component.literal("No raid found here"));
        }
        return 1;
    }

    private static int spawnLeader(CommandSourceStack $$0) {
        $$0.sendSuccess(Component.literal("Spawned a raid captain"), false);
        Raider $$1 = EntityType.PILLAGER.create($$0.getLevel());
        if ($$1 == null) {
            $$0.sendFailure(Component.literal("Pillager failed to spawn"));
            return 0;
        }
        $$1.setPatrolLeader(true);
        $$1.setItemSlot(EquipmentSlot.HEAD, Raid.getLeaderBannerInstance());
        $$1.setPos($$0.getPosition().x, $$0.getPosition().y, $$0.getPosition().z);
        $$1.finalizeSpawn($$0.getLevel(), $$0.getLevel().getCurrentDifficultyAt(new BlockPos($$0.getPosition())), MobSpawnType.COMMAND, null, null);
        $$0.getLevel().addFreshEntityWithPassengers($$1);
        return 1;
    }

    private static int playSound(CommandSourceStack $$0, @Nullable Component $$1) {
        if ($$1 != null && $$1.getString().equals((Object)"local")) {
            ServerLevel $$2 = $$0.getLevel();
            Vec3 $$3 = $$0.getPosition().add(5.0, 0.0, 0.0);
            $$2.playSeededSound(null, $$3.x, $$3.y, $$3.z, SoundEvents.RAID_HORN, SoundSource.NEUTRAL, 2.0f, 1.0f, $$2.random.nextLong());
        }
        return 1;
    }

    private static int start(CommandSourceStack $$0, int $$1) throws CommandSyntaxException {
        ServerPlayer $$2 = $$0.getPlayerOrException();
        BlockPos $$3 = $$2.blockPosition();
        if ($$2.getLevel().isRaided($$3)) {
            $$0.sendFailure(Component.literal("Raid already started close by"));
            return -1;
        }
        Raids $$4 = $$2.getLevel().getRaids();
        Raid $$5 = $$4.createOrExtendRaid($$2);
        if ($$5 != null) {
            $$5.setBadOmenLevel($$1);
            $$4.setDirty();
            $$0.sendSuccess(Component.literal("Created a raid in your local village"), false);
        } else {
            $$0.sendFailure(Component.literal("Failed to create a raid in your local village"));
        }
        return 1;
    }

    private static int stop(CommandSourceStack $$0) throws CommandSyntaxException {
        ServerPlayer $$1 = $$0.getPlayerOrException();
        BlockPos $$2 = $$1.blockPosition();
        Raid $$3 = $$1.getLevel().getRaidAt($$2);
        if ($$3 != null) {
            $$3.stop();
            $$0.sendSuccess(Component.literal("Stopped raid"), false);
            return 1;
        }
        $$0.sendFailure(Component.literal("No raid here"));
        return -1;
    }

    private static int check(CommandSourceStack $$0) throws CommandSyntaxException {
        Raid $$1 = RaidCommand.getRaid($$0.getPlayerOrException());
        if ($$1 != null) {
            StringBuilder $$2 = new StringBuilder();
            $$2.append("Found a started raid! ");
            $$0.sendSuccess(Component.literal($$2.toString()), false);
            $$2 = new StringBuilder();
            $$2.append("Num groups spawned: ");
            $$2.append($$1.getGroupsSpawned());
            $$2.append(" Bad omen level: ");
            $$2.append($$1.getBadOmenLevel());
            $$2.append(" Num mobs: ");
            $$2.append($$1.getTotalRaidersAlive());
            $$2.append(" Raid health: ");
            $$2.append($$1.getHealthOfLivingRaiders());
            $$2.append(" / ");
            $$2.append($$1.getTotalHealth());
            $$0.sendSuccess(Component.literal($$2.toString()), false);
            return 1;
        }
        $$0.sendFailure(Component.literal("Found no started raids"));
        return 0;
    }

    @Nullable
    private static Raid getRaid(ServerPlayer $$0) {
        return $$0.getLevel().getRaidAt($$0.blockPosition());
    }
}
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
 *  java.lang.Object
 *  java.util.function.Function
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SummonCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType((Message)Component.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType((Message)Component.translatable("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType((Message)Component.translatable("commands.summon.invalidPosition"));

    public static void register(CommandDispatcher<CommandSourceStack> $$02, CommandBuildContext $$1) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("summon").requires($$0 -> $$0.hasPermission(2))).then(((RequiredArgumentBuilder)Commands.argument("entity", ResourceArgument.resource($$1, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes($$0 -> SummonCommand.spawnEntity((CommandSourceStack)$$0.getSource(), ResourceArgument.getSummonableEntityType((CommandContext<CommandSourceStack>)$$0, "entity"), ((CommandSourceStack)$$0.getSource()).getPosition(), new CompoundTag(), true))).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes($$0 -> SummonCommand.spawnEntity((CommandSourceStack)$$0.getSource(), ResourceArgument.getSummonableEntityType((CommandContext<CommandSourceStack>)$$0, "entity"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)$$0, "pos"), new CompoundTag(), true))).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes($$0 -> SummonCommand.spawnEntity((CommandSourceStack)$$0.getSource(), ResourceArgument.getSummonableEntityType((CommandContext<CommandSourceStack>)$$0, "entity"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)$$0, "pos"), CompoundTagArgument.getCompoundTag($$0, "nbt"), false))))));
    }

    private static int spawnEntity(CommandSourceStack $$0, Holder.Reference<EntityType<?>> $$12, Vec3 $$2, CompoundTag $$3, boolean $$4) throws CommandSyntaxException {
        BlockPos $$5 = new BlockPos($$2);
        if (!Level.isInSpawnableBounds($$5)) {
            throw INVALID_POSITION.create();
        }
        CompoundTag $$6 = $$3.copy();
        $$6.putString("id", $$12.key().location().toString());
        ServerLevel $$7 = $$0.getLevel();
        Entity $$8 = EntityType.loadEntityRecursive($$6, $$7, (Function<Entity, Entity>)((Function)$$1 -> {
            $$1.moveTo($$0.x, $$0.y, $$0.z, $$1.getYRot(), $$1.getXRot());
            return $$1;
        }));
        if ($$8 == null) {
            throw ERROR_FAILED.create();
        }
        if ($$4 && $$8 instanceof Mob) {
            ((Mob)$$8).finalizeSpawn($$0.getLevel(), $$0.getLevel().getCurrentDifficultyAt($$8.blockPosition()), MobSpawnType.COMMAND, null, null);
        }
        if (!$$7.tryAddFreshEntityWithPassengers($$8)) {
            throw ERROR_DUPLICATE_UUID.create();
        }
        $$0.sendSuccess(Component.translatable("commands.summon.success", $$8.getDisplayName()), true);
        return 1;
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.FunctionalInterface
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.Objects
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.ItemCommands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LootCommand {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_LOOT_TABLE = ($$0, $$1) -> {
        LootTables $$2 = ((CommandSourceStack)$$0.getSource()).getServer().getLootTables();
        return SharedSuggestionProvider.suggestResource($$2.getIds(), $$1);
    };
    private static final DynamicCommandExceptionType ERROR_NO_HELD_ITEMS = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.drop.no_held_items", $$0));
    private static final DynamicCommandExceptionType ERROR_NO_LOOT_TABLE = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.drop.no_loot_table", $$0));

    public static void register(CommandDispatcher<CommandSourceStack> $$02, CommandBuildContext $$1) {
        $$02.register(LootCommand.addTargets((LiteralArgumentBuilder)Commands.literal("loot").requires($$0 -> $$0.hasPermission(2)), ($$12, $$2) -> $$12.then(Commands.literal("fish").then(Commands.argument("loot_table", ResourceLocationArgument.id()).suggests(SUGGEST_LOOT_TABLE).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes($$1 -> LootCommand.dropFishingLoot((CommandContext<CommandSourceStack>)$$1, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)$$1, "loot_table"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$1, "pos"), ItemStack.EMPTY, $$2))).then(Commands.argument("tool", ItemArgument.item($$1)).executes($$1 -> LootCommand.dropFishingLoot((CommandContext<CommandSourceStack>)$$1, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)$$1, "loot_table"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$1, "pos"), ItemArgument.getItem($$1, "tool").createItemStack(1, false), $$2)))).then(Commands.literal("mainhand").executes($$1 -> LootCommand.dropFishingLoot((CommandContext<CommandSourceStack>)$$1, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)$$1, "loot_table"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$1, "pos"), LootCommand.getSourceHandItem((CommandSourceStack)$$1.getSource(), EquipmentSlot.MAINHAND), $$2)))).then(Commands.literal("offhand").executes($$1 -> LootCommand.dropFishingLoot((CommandContext<CommandSourceStack>)$$1, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)$$1, "loot_table"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$1, "pos"), LootCommand.getSourceHandItem((CommandSourceStack)$$1.getSource(), EquipmentSlot.OFFHAND), $$2)))))).then(Commands.literal("loot").then(Commands.argument("loot_table", ResourceLocationArgument.id()).suggests(SUGGEST_LOOT_TABLE).executes($$1 -> LootCommand.dropChestLoot((CommandContext<CommandSourceStack>)$$1, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)$$1, "loot_table"), $$2)))).then(Commands.literal("kill").then(Commands.argument("target", EntityArgument.entity()).executes($$1 -> LootCommand.dropKillLoot((CommandContext<CommandSourceStack>)$$1, EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$1, "target"), $$2)))).then(Commands.literal("mine").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes($$1 -> LootCommand.dropBlockLoot((CommandContext<CommandSourceStack>)$$1, BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$1, "pos"), ItemStack.EMPTY, $$2))).then(Commands.argument("tool", ItemArgument.item($$1)).executes($$1 -> LootCommand.dropBlockLoot((CommandContext<CommandSourceStack>)$$1, BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$1, "pos"), ItemArgument.getItem($$1, "tool").createItemStack(1, false), $$2)))).then(Commands.literal("mainhand").executes($$1 -> LootCommand.dropBlockLoot((CommandContext<CommandSourceStack>)$$1, BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$1, "pos"), LootCommand.getSourceHandItem((CommandSourceStack)$$1.getSource(), EquipmentSlot.MAINHAND), $$2)))).then(Commands.literal("offhand").executes($$1 -> LootCommand.dropBlockLoot((CommandContext<CommandSourceStack>)$$1, BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$1, "pos"), LootCommand.getSourceHandItem((CommandSourceStack)$$1.getSource(), EquipmentSlot.OFFHAND), $$2)))))));
    }

    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T addTargets(T $$02, TailProvider $$12) {
        return (T)$$02.then(((LiteralArgumentBuilder)Commands.literal("replace").then(Commands.literal("entity").then(Commands.argument("entities", EntityArgument.entities()).then($$12.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("slot", SlotArgument.slot()), ($$0, $$1, $$2) -> LootCommand.entityReplace(EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "entities"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)$$0, "slot"), $$1.size(), (List<ItemStack>)$$1, $$2)).then($$12.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("count", IntegerArgumentType.integer((int)0)), ($$0, $$1, $$2) -> LootCommand.entityReplace(EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "entities"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)$$0, "slot"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"count"), (List<ItemStack>)$$1, $$2))))))).then(Commands.literal("block").then(Commands.argument("targetPos", BlockPosArgument.blockPos()).then($$12.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("slot", SlotArgument.slot()), ($$0, $$1, $$2) -> LootCommand.blockReplace((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "targetPos"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)$$0, "slot"), $$1.size(), (List<ItemStack>)$$1, $$2)).then($$12.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("count", IntegerArgumentType.integer((int)0)), ($$0, $$1, $$2) -> LootCommand.blockReplace((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "targetPos"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"slot"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"count"), (List<ItemStack>)$$1, $$2))))))).then(Commands.literal("insert").then($$12.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("targetPos", BlockPosArgument.blockPos()), ($$0, $$1, $$2) -> LootCommand.blockDistribute((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "targetPos"), (List<ItemStack>)$$1, $$2)))).then(Commands.literal("give").then($$12.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("players", EntityArgument.players()), ($$0, $$1, $$2) -> LootCommand.playerGive(EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "players"), (List<ItemStack>)$$1, $$2)))).then(Commands.literal("spawn").then($$12.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("targetPos", Vec3Argument.vec3()), ($$0, $$1, $$2) -> LootCommand.dropInWorld((CommandSourceStack)$$0.getSource(), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)$$0, "targetPos"), (List<ItemStack>)$$1, $$2))));
    }

    private static Container getContainer(CommandSourceStack $$0, BlockPos $$1) throws CommandSyntaxException {
        BlockEntity $$2 = $$0.getLevel().getBlockEntity($$1);
        if (!($$2 instanceof Container)) {
            throw ItemCommands.ERROR_TARGET_NOT_A_CONTAINER.create((Object)$$1.getX(), (Object)$$1.getY(), (Object)$$1.getZ());
        }
        return (Container)((Object)$$2);
    }

    private static int blockDistribute(CommandSourceStack $$0, BlockPos $$1, List<ItemStack> $$2, Callback $$3) throws CommandSyntaxException {
        Container $$4 = LootCommand.getContainer($$0, $$1);
        ArrayList $$5 = Lists.newArrayListWithCapacity((int)$$2.size());
        for (ItemStack $$6 : $$2) {
            if (!LootCommand.distributeToContainer($$4, $$6.copy())) continue;
            $$4.setChanged();
            $$5.add((Object)$$6);
        }
        $$3.accept((List<ItemStack>)$$5);
        return $$5.size();
    }

    private static boolean distributeToContainer(Container $$0, ItemStack $$1) {
        boolean $$2 = false;
        for (int $$3 = 0; $$3 < $$0.getContainerSize() && !$$1.isEmpty(); ++$$3) {
            ItemStack $$4 = $$0.getItem($$3);
            if (!$$0.canPlaceItem($$3, $$1)) continue;
            if ($$4.isEmpty()) {
                $$0.setItem($$3, $$1);
                $$2 = true;
                break;
            }
            if (!LootCommand.canMergeItems($$4, $$1)) continue;
            int $$5 = $$1.getMaxStackSize() - $$4.getCount();
            int $$6 = Math.min((int)$$1.getCount(), (int)$$5);
            $$1.shrink($$6);
            $$4.grow($$6);
            $$2 = true;
        }
        return $$2;
    }

    private static int blockReplace(CommandSourceStack $$0, BlockPos $$1, int $$2, int $$3, List<ItemStack> $$4, Callback $$5) throws CommandSyntaxException {
        Container $$6 = LootCommand.getContainer($$0, $$1);
        int $$7 = $$6.getContainerSize();
        if ($$2 < 0 || $$2 >= $$7) {
            throw ItemCommands.ERROR_TARGET_INAPPLICABLE_SLOT.create((Object)$$2);
        }
        ArrayList $$8 = Lists.newArrayListWithCapacity((int)$$4.size());
        for (int $$9 = 0; $$9 < $$3; ++$$9) {
            ItemStack $$11;
            int $$10 = $$2 + $$9;
            ItemStack itemStack = $$11 = $$9 < $$4.size() ? (ItemStack)$$4.get($$9) : ItemStack.EMPTY;
            if (!$$6.canPlaceItem($$10, $$11)) continue;
            $$6.setItem($$10, $$11);
            $$8.add((Object)$$11);
        }
        $$5.accept((List<ItemStack>)$$8);
        return $$8.size();
    }

    private static boolean canMergeItems(ItemStack $$0, ItemStack $$1) {
        return $$0.is($$1.getItem()) && $$0.getDamageValue() == $$1.getDamageValue() && $$0.getCount() <= $$0.getMaxStackSize() && Objects.equals((Object)$$0.getTag(), (Object)$$1.getTag());
    }

    private static int playerGive(Collection<ServerPlayer> $$0, List<ItemStack> $$1, Callback $$2) throws CommandSyntaxException {
        ArrayList $$3 = Lists.newArrayListWithCapacity((int)$$1.size());
        for (ItemStack $$4 : $$1) {
            for (ServerPlayer $$5 : $$0) {
                if (!$$5.getInventory().add($$4.copy())) continue;
                $$3.add((Object)$$4);
            }
        }
        $$2.accept((List<ItemStack>)$$3);
        return $$3.size();
    }

    private static void setSlots(Entity $$0, List<ItemStack> $$1, int $$2, int $$3, List<ItemStack> $$4) {
        for (int $$5 = 0; $$5 < $$3; ++$$5) {
            ItemStack $$6 = $$5 < $$1.size() ? (ItemStack)$$1.get($$5) : ItemStack.EMPTY;
            SlotAccess $$7 = $$0.getSlot($$2 + $$5);
            if ($$7 == SlotAccess.NULL || !$$7.set($$6.copy())) continue;
            $$4.add((Object)$$6);
        }
    }

    private static int entityReplace(Collection<? extends Entity> $$0, int $$1, int $$2, List<ItemStack> $$3, Callback $$4) throws CommandSyntaxException {
        ArrayList $$5 = Lists.newArrayListWithCapacity((int)$$3.size());
        for (Entity $$6 : $$0) {
            if ($$6 instanceof ServerPlayer) {
                ServerPlayer $$7 = (ServerPlayer)$$6;
                LootCommand.setSlots($$6, $$3, $$1, $$2, (List<ItemStack>)$$5);
                $$7.containerMenu.broadcastChanges();
                continue;
            }
            LootCommand.setSlots($$6, $$3, $$1, $$2, (List<ItemStack>)$$5);
        }
        $$4.accept((List<ItemStack>)$$5);
        return $$5.size();
    }

    private static int dropInWorld(CommandSourceStack $$0, Vec3 $$1, List<ItemStack> $$22, Callback $$3) throws CommandSyntaxException {
        ServerLevel $$4 = $$0.getLevel();
        $$22.forEach($$2 -> {
            ItemEntity $$3 = new ItemEntity($$4, $$1.x, $$1.y, $$1.z, $$2.copy());
            $$3.setDefaultPickUpDelay();
            $$4.addFreshEntity($$3);
        });
        $$3.accept($$22);
        return $$22.size();
    }

    private static void callback(CommandSourceStack $$0, List<ItemStack> $$1) {
        if ($$1.size() == 1) {
            ItemStack $$2 = (ItemStack)$$1.get(0);
            $$0.sendSuccess(Component.translatable("commands.drop.success.single", $$2.getCount(), $$2.getDisplayName()), false);
        } else {
            $$0.sendSuccess(Component.translatable("commands.drop.success.multiple", $$1.size()), false);
        }
    }

    private static void callback(CommandSourceStack $$0, List<ItemStack> $$1, ResourceLocation $$2) {
        if ($$1.size() == 1) {
            ItemStack $$3 = (ItemStack)$$1.get(0);
            $$0.sendSuccess(Component.translatable("commands.drop.success.single_with_table", $$3.getCount(), $$3.getDisplayName(), $$2), false);
        } else {
            $$0.sendSuccess(Component.translatable("commands.drop.success.multiple_with_table", $$1.size(), $$2), false);
        }
    }

    private static ItemStack getSourceHandItem(CommandSourceStack $$0, EquipmentSlot $$1) throws CommandSyntaxException {
        Entity $$2 = $$0.getEntityOrException();
        if ($$2 instanceof LivingEntity) {
            return ((LivingEntity)$$2).getItemBySlot($$1);
        }
        throw ERROR_NO_HELD_ITEMS.create((Object)$$2.getDisplayName());
    }

    private static int dropBlockLoot(CommandContext<CommandSourceStack> $$0, BlockPos $$1, ItemStack $$22, DropConsumer $$3) throws CommandSyntaxException {
        CommandSourceStack $$4 = (CommandSourceStack)$$0.getSource();
        ServerLevel $$5 = $$4.getLevel();
        BlockState $$6 = $$5.getBlockState($$1);
        BlockEntity $$7 = $$5.getBlockEntity($$1);
        LootContext.Builder $$8 = new LootContext.Builder($$5).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf($$1)).withParameter(LootContextParams.BLOCK_STATE, $$6).withOptionalParameter(LootContextParams.BLOCK_ENTITY, $$7).withOptionalParameter(LootContextParams.THIS_ENTITY, $$4.getEntity()).withParameter(LootContextParams.TOOL, $$22);
        List<ItemStack> $$9 = $$6.getDrops($$8);
        return $$3.accept($$0, $$9, $$2 -> LootCommand.callback($$4, (List<ItemStack>)$$2, $$6.getBlock().getLootTable()));
    }

    private static int dropKillLoot(CommandContext<CommandSourceStack> $$0, Entity $$1, DropConsumer $$22) throws CommandSyntaxException {
        if (!($$1 instanceof LivingEntity)) {
            throw ERROR_NO_LOOT_TABLE.create((Object)$$1.getDisplayName());
        }
        ResourceLocation $$3 = ((LivingEntity)$$1).getLootTable();
        CommandSourceStack $$4 = (CommandSourceStack)$$0.getSource();
        LootContext.Builder $$5 = new LootContext.Builder($$4.getLevel());
        Entity $$6 = $$4.getEntity();
        if ($$6 instanceof Player) {
            $$5.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, (Player)$$6);
        }
        $$5.withParameter(LootContextParams.DAMAGE_SOURCE, DamageSource.MAGIC);
        $$5.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, $$6);
        $$5.withOptionalParameter(LootContextParams.KILLER_ENTITY, $$6);
        $$5.withParameter(LootContextParams.THIS_ENTITY, $$1);
        $$5.withParameter(LootContextParams.ORIGIN, $$4.getPosition());
        LootTable $$7 = $$4.getServer().getLootTables().get($$3);
        ObjectArrayList<ItemStack> $$8 = $$7.getRandomItems($$5.create(LootContextParamSets.ENTITY));
        return $$22.accept($$0, (List<ItemStack>)$$8, $$2 -> LootCommand.callback($$4, (List<ItemStack>)$$2, $$3));
    }

    private static int dropChestLoot(CommandContext<CommandSourceStack> $$0, ResourceLocation $$1, DropConsumer $$2) throws CommandSyntaxException {
        CommandSourceStack $$3 = (CommandSourceStack)$$0.getSource();
        LootContext.Builder $$4 = new LootContext.Builder($$3.getLevel()).withOptionalParameter(LootContextParams.THIS_ENTITY, $$3.getEntity()).withParameter(LootContextParams.ORIGIN, $$3.getPosition());
        return LootCommand.drop($$0, $$1, $$4.create(LootContextParamSets.CHEST), $$2);
    }

    private static int dropFishingLoot(CommandContext<CommandSourceStack> $$0, ResourceLocation $$1, BlockPos $$2, ItemStack $$3, DropConsumer $$4) throws CommandSyntaxException {
        CommandSourceStack $$5 = (CommandSourceStack)$$0.getSource();
        LootContext $$6 = new LootContext.Builder($$5.getLevel()).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf($$2)).withParameter(LootContextParams.TOOL, $$3).withOptionalParameter(LootContextParams.THIS_ENTITY, $$5.getEntity()).create(LootContextParamSets.FISHING);
        return LootCommand.drop($$0, $$1, $$6, $$4);
    }

    private static int drop(CommandContext<CommandSourceStack> $$0, ResourceLocation $$12, LootContext $$2, DropConsumer $$3) throws CommandSyntaxException {
        CommandSourceStack $$4 = (CommandSourceStack)$$0.getSource();
        LootTable $$5 = $$4.getServer().getLootTables().get($$12);
        ObjectArrayList<ItemStack> $$6 = $$5.getRandomItems($$2);
        return $$3.accept($$0, (List<ItemStack>)$$6, $$1 -> LootCommand.callback($$4, (List<ItemStack>)$$1));
    }

    @FunctionalInterface
    static interface TailProvider {
        public ArgumentBuilder<CommandSourceStack, ?> construct(ArgumentBuilder<CommandSourceStack, ?> var1, DropConsumer var2);
    }

    @FunctionalInterface
    static interface DropConsumer {
        public int accept(CommandContext<CommandSourceStack> var1, List<ItemStack> var2, Callback var3) throws CommandSyntaxException;
    }

    @FunctionalInterface
    static interface Callback {
        public void accept(List<ItemStack> var1) throws CommandSyntaxException;
    }
}
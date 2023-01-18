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
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.function.Predicate
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.commons.lang3.mutable.MutableInt;

public class FillBiomeCommand {
    private static final int MAX_FILL_AREA = 32768;
    public static final SimpleCommandExceptionType ERROR_NOT_LOADED = new SimpleCommandExceptionType((Message)Component.translatable("argument.pos.unloaded"));
    private static final Dynamic2CommandExceptionType ERROR_VOLUME_TOO_LARGE = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("commands.fillbiome.toobig", $$0, $$1));

    public static void register(CommandDispatcher<CommandSourceStack> $$03, CommandBuildContext $$1) {
        $$03.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fillbiome").requires($$0 -> $$0.hasPermission(2))).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("biome", ResourceArgument.resource($$1, Registries.BIOME)).executes($$02 -> FillBiomeCommand.fill((CommandSourceStack)$$02.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "to"), ResourceArgument.getResource((CommandContext<CommandSourceStack>)$$02, "biome", Registries.BIOME), (Predicate<Holder<Biome>>)((Predicate)$$0 -> true)))).then(Commands.literal("replace").then(Commands.argument("filter", ResourceOrTagArgument.resourceOrTag($$1, Registries.BIOME)).executes($$0 -> FillBiomeCommand.fill((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "to"), ResourceArgument.getResource((CommandContext<CommandSourceStack>)$$0, "biome", Registries.BIOME), (Predicate<Holder<Biome>>)((Predicate)arg_0 -> ResourceOrTagArgument.getResourceOrTag((CommandContext<CommandSourceStack>)$$0, "filter", Registries.BIOME).test(arg_0))))))))));
    }

    private static int quantize(int $$0) {
        return QuartPos.toBlock(QuartPos.fromBlock($$0));
    }

    private static BlockPos quantize(BlockPos $$0) {
        return new BlockPos(FillBiomeCommand.quantize($$0.getX()), FillBiomeCommand.quantize($$0.getY()), FillBiomeCommand.quantize($$0.getZ()));
    }

    private static BiomeResolver makeResolver(MutableInt $$0, ChunkAccess $$1, BoundingBox $$2, Holder<Biome> $$3, Predicate<Holder<Biome>> $$4) {
        return ($$5, $$6, $$7, $$8) -> {
            int $$9 = QuartPos.toBlock($$5);
            int $$10 = QuartPos.toBlock($$6);
            int $$11 = QuartPos.toBlock($$7);
            Holder<Biome> $$12 = $$1.getNoiseBiome($$5, $$6, $$7);
            if ($$2.isInside($$9, $$10, $$11) && $$4.test($$12)) {
                $$0.increment();
                return $$3;
            }
            return $$12;
        };
    }

    private static int fill(CommandSourceStack $$0, BlockPos $$1, BlockPos $$2, Holder.Reference<Biome> $$3, Predicate<Holder<Biome>> $$4) throws CommandSyntaxException {
        BlockPos $$6;
        BlockPos $$5 = FillBiomeCommand.quantize($$1);
        BoundingBox $$7 = BoundingBox.fromCorners($$5, $$6 = FillBiomeCommand.quantize($$2));
        int $$8 = $$7.getXSpan() * $$7.getYSpan() * $$7.getZSpan();
        if ($$8 > 32768) {
            throw ERROR_VOLUME_TOO_LARGE.create((Object)32768, (Object)$$8);
        }
        ServerLevel $$9 = $$0.getLevel();
        ArrayList $$10 = new ArrayList();
        for (int $$11 = SectionPos.blockToSectionCoord($$7.minZ()); $$11 <= SectionPos.blockToSectionCoord($$7.maxZ()); ++$$11) {
            for (int $$12 = SectionPos.blockToSectionCoord($$7.minX()); $$12 <= SectionPos.blockToSectionCoord($$7.maxX()); ++$$12) {
                ChunkAccess $$13 = $$9.getChunk($$12, $$11, ChunkStatus.FULL, false);
                if ($$13 == null) {
                    throw ERROR_NOT_LOADED.create();
                }
                $$10.add((Object)$$13);
            }
        }
        MutableInt $$14 = new MutableInt(0);
        for (ChunkAccess $$15 : $$10) {
            $$15.fillBiomesFromNoise(FillBiomeCommand.makeResolver($$14, $$15, $$7, $$3, $$4), $$9.getChunkSource().randomState().sampler());
            $$15.setUnsaved(true);
            $$9.getChunkSource().chunkMap.resendChunk($$15);
        }
        $$0.sendSuccess(Component.translatable("commands.fillbiome.success.count", $$14.getValue(), $$7.minX(), $$7.minY(), $$7.minZ(), $$7.maxX(), $$7.maxY(), $$7.maxZ()), true);
        return $$14.getValue();
    }
}
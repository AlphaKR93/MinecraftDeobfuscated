/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType
 *  java.lang.Double
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.HashSet
 *  java.util.Locale
 *  java.util.Set
 */
package net.minecraft.server.commands;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.scores.Team;

public class SpreadPlayersCommand {
    private static final int MAX_ITERATION_COUNT = 10000;
    private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_TEAMS = new Dynamic4CommandExceptionType(($$0, $$1, $$2, $$3) -> Component.translatable("commands.spreadplayers.failed.teams", $$0, $$1, $$2, $$3));
    private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_ENTITIES = new Dynamic4CommandExceptionType(($$0, $$1, $$2, $$3) -> Component.translatable("commands.spreadplayers.failed.entities", $$0, $$1, $$2, $$3));
    private static final Dynamic2CommandExceptionType ERROR_INVALID_MAX_HEIGHT = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("commands.spreadplayers.failed.invalid.height", $$0, $$1));

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spreadplayers").requires($$0 -> $$0.hasPermission(2))).then(Commands.argument("center", Vec2Argument.vec2()).then(Commands.argument("spreadDistance", FloatArgumentType.floatArg((float)0.0f)).then(((RequiredArgumentBuilder)Commands.argument("maxRange", FloatArgumentType.floatArg((float)1.0f)).then(Commands.argument("respectTeams", BoolArgumentType.bool()).then(Commands.argument("targets", EntityArgument.entities()).executes($$0 -> SpreadPlayersCommand.spreadPlayers((CommandSourceStack)$$0.getSource(), Vec2Argument.getVec2((CommandContext<CommandSourceStack>)$$0, "center"), FloatArgumentType.getFloat((CommandContext)$$0, (String)"spreadDistance"), FloatArgumentType.getFloat((CommandContext)$$0, (String)"maxRange"), ((CommandSourceStack)$$0.getSource()).getLevel().getMaxBuildHeight(), BoolArgumentType.getBool((CommandContext)$$0, (String)"respectTeams"), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets")))))).then(Commands.literal("under").then(Commands.argument("maxHeight", IntegerArgumentType.integer()).then(Commands.argument("respectTeams", BoolArgumentType.bool()).then(Commands.argument("targets", EntityArgument.entities()).executes($$0 -> SpreadPlayersCommand.spreadPlayers((CommandSourceStack)$$0.getSource(), Vec2Argument.getVec2((CommandContext<CommandSourceStack>)$$0, "center"), FloatArgumentType.getFloat((CommandContext)$$0, (String)"spreadDistance"), FloatArgumentType.getFloat((CommandContext)$$0, (String)"maxRange"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"maxHeight"), BoolArgumentType.getBool((CommandContext)$$0, (String)"respectTeams"), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets")))))))))));
    }

    private static int spreadPlayers(CommandSourceStack $$0, Vec2 $$1, float $$2, float $$3, int $$4, boolean $$5, Collection<? extends Entity> $$6) throws CommandSyntaxException {
        ServerLevel $$7 = $$0.getLevel();
        int $$8 = $$7.getMinBuildHeight();
        if ($$4 < $$8) {
            throw ERROR_INVALID_MAX_HEIGHT.create((Object)$$4, (Object)$$8);
        }
        RandomSource $$9 = RandomSource.create();
        double $$10 = $$1.x - $$3;
        double $$11 = $$1.y - $$3;
        double $$12 = $$1.x + $$3;
        double $$13 = $$1.y + $$3;
        Position[] $$14 = SpreadPlayersCommand.createInitialPositions($$9, $$5 ? SpreadPlayersCommand.getNumberOfTeams($$6) : $$6.size(), $$10, $$11, $$12, $$13);
        SpreadPlayersCommand.spreadPositions($$1, $$2, $$7, $$9, $$10, $$11, $$12, $$13, $$4, $$14, $$5);
        double $$15 = SpreadPlayersCommand.setPlayerPositions($$6, $$7, $$14, $$4, $$5);
        $$0.sendSuccess(Component.translatable("commands.spreadplayers.success." + ($$5 ? "teams" : "entities"), $$14.length, Float.valueOf((float)$$1.x), Float.valueOf((float)$$1.y), String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{$$15})), true);
        return $$14.length;
    }

    private static int getNumberOfTeams(Collection<? extends Entity> $$0) {
        HashSet $$1 = Sets.newHashSet();
        for (Entity $$2 : $$0) {
            if ($$2 instanceof Player) {
                $$1.add((Object)$$2.getTeam());
                continue;
            }
            $$1.add(null);
        }
        return $$1.size();
    }

    private static void spreadPositions(Vec2 $$0, double $$1, ServerLevel $$2, RandomSource $$3, double $$4, double $$5, double $$6, double $$7, int $$8, Position[] $$9, boolean $$10) throws CommandSyntaxException {
        int $$13;
        boolean $$11 = true;
        double $$12 = 3.4028234663852886E38;
        for ($$13 = 0; $$13 < 10000 && $$11; ++$$13) {
            $$11 = false;
            $$12 = 3.4028234663852886E38;
            for (int $$14 = 0; $$14 < $$9.length; ++$$14) {
                Position $$15 = $$9[$$14];
                int $$16 = 0;
                Position $$17 = new Position();
                for (int $$18 = 0; $$18 < $$9.length; ++$$18) {
                    if ($$14 == $$18) continue;
                    Position $$19 = $$9[$$18];
                    double $$20 = $$15.dist($$19);
                    $$12 = Math.min((double)$$20, (double)$$12);
                    if (!($$20 < $$1)) continue;
                    ++$$16;
                    $$17.x += $$19.x - $$15.x;
                    $$17.z += $$19.z - $$15.z;
                }
                if ($$16 > 0) {
                    $$17.x /= (double)$$16;
                    $$17.z /= (double)$$16;
                    double $$21 = $$17.getLength();
                    if ($$21 > 0.0) {
                        $$17.normalize();
                        $$15.moveAway($$17);
                    } else {
                        $$15.randomize($$3, $$4, $$5, $$6, $$7);
                    }
                    $$11 = true;
                }
                if (!$$15.clamp($$4, $$5, $$6, $$7)) continue;
                $$11 = true;
            }
            if ($$11) continue;
            for (Position $$22 : $$9) {
                if ($$22.isSafe($$2, $$8)) continue;
                $$22.randomize($$3, $$4, $$5, $$6, $$7);
                $$11 = true;
            }
        }
        if ($$12 == 3.4028234663852886E38) {
            $$12 = 0.0;
        }
        if ($$13 >= 10000) {
            if ($$10) {
                throw ERROR_FAILED_TO_SPREAD_TEAMS.create((Object)$$9.length, (Object)Float.valueOf((float)$$0.x), (Object)Float.valueOf((float)$$0.y), (Object)String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{$$12}));
            }
            throw ERROR_FAILED_TO_SPREAD_ENTITIES.create((Object)$$9.length, (Object)Float.valueOf((float)$$0.x), (Object)Float.valueOf((float)$$0.y), (Object)String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{$$12}));
        }
    }

    private static double setPlayerPositions(Collection<? extends Entity> $$0, ServerLevel $$1, Position[] $$2, int $$3, boolean $$4) {
        double $$5 = 0.0;
        int $$6 = 0;
        HashMap $$7 = Maps.newHashMap();
        for (Entity $$8 : $$0) {
            Position $$11;
            if ($$4) {
                Team $$9;
                Team team = $$9 = $$8 instanceof Player ? $$8.getTeam() : null;
                if (!$$7.containsKey((Object)$$9)) {
                    $$7.put((Object)$$9, (Object)$$2[$$6++]);
                }
                Position $$10 = (Position)$$7.get((Object)$$9);
            } else {
                $$11 = $$2[$$6++];
            }
            $$8.teleportTo($$1, (double)Mth.floor($$11.x) + 0.5, $$11.getSpawnY($$1, $$3), (double)Mth.floor($$11.z) + 0.5, (Set<RelativeMovement>)Set.of(), $$8.getYRot(), $$8.getXRot());
            double $$12 = Double.MAX_VALUE;
            for (Position $$13 : $$2) {
                if ($$11 == $$13) continue;
                double $$14 = $$11.dist($$13);
                $$12 = Math.min((double)$$14, (double)$$12);
            }
            $$5 += $$12;
        }
        if ($$0.size() < 2) {
            return 0.0;
        }
        return $$5 /= (double)$$0.size();
    }

    private static Position[] createInitialPositions(RandomSource $$0, int $$1, double $$2, double $$3, double $$4, double $$5) {
        Position[] $$6 = new Position[$$1];
        for (int $$7 = 0; $$7 < $$6.length; ++$$7) {
            Position $$8 = new Position();
            $$8.randomize($$0, $$2, $$3, $$4, $$5);
            $$6[$$7] = $$8;
        }
        return $$6;
    }

    static class Position {
        double x;
        double z;

        Position() {
        }

        double dist(Position $$0) {
            double $$1 = this.x - $$0.x;
            double $$2 = this.z - $$0.z;
            return Math.sqrt((double)($$1 * $$1 + $$2 * $$2));
        }

        void normalize() {
            double $$0 = this.getLength();
            this.x /= $$0;
            this.z /= $$0;
        }

        double getLength() {
            return Math.sqrt((double)(this.x * this.x + this.z * this.z));
        }

        public void moveAway(Position $$0) {
            this.x -= $$0.x;
            this.z -= $$0.z;
        }

        public boolean clamp(double $$0, double $$1, double $$2, double $$3) {
            boolean $$4 = false;
            if (this.x < $$0) {
                this.x = $$0;
                $$4 = true;
            } else if (this.x > $$2) {
                this.x = $$2;
                $$4 = true;
            }
            if (this.z < $$1) {
                this.z = $$1;
                $$4 = true;
            } else if (this.z > $$3) {
                this.z = $$3;
                $$4 = true;
            }
            return $$4;
        }

        public int getSpawnY(BlockGetter $$0, int $$1) {
            BlockPos.MutableBlockPos $$2 = new BlockPos.MutableBlockPos(this.x, (double)($$1 + 1), this.z);
            boolean $$3 = $$0.getBlockState($$2).isAir();
            $$2.move(Direction.DOWN);
            boolean $$4 = $$0.getBlockState($$2).isAir();
            while ($$2.getY() > $$0.getMinBuildHeight()) {
                $$2.move(Direction.DOWN);
                boolean $$5 = $$0.getBlockState($$2).isAir();
                if (!$$5 && $$4 && $$3) {
                    return $$2.getY() + 1;
                }
                $$3 = $$4;
                $$4 = $$5;
            }
            return $$1 + 1;
        }

        public boolean isSafe(BlockGetter $$0, int $$1) {
            BlockPos $$2 = new BlockPos(this.x, (double)(this.getSpawnY($$0, $$1) - 1), this.z);
            BlockState $$3 = $$0.getBlockState($$2);
            Material $$4 = $$3.getMaterial();
            return $$2.getY() < $$1 && !$$4.isLiquid() && $$4 != Material.FIRE;
        }

        public void randomize(RandomSource $$0, double $$1, double $$2, double $$3, double $$4) {
            this.x = Mth.nextDouble($$0, $$1, $$3);
            this.z = Mth.nextDouble($$0, $$2, $$4);
        }
    }
}
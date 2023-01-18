/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;

public class ScoreContents
implements ComponentContents {
    private static final String SCORER_PLACEHOLDER = "*";
    private final String name;
    @Nullable
    private final EntitySelector selector;
    private final String objective;

    @Nullable
    private static EntitySelector parseSelector(String $$0) {
        try {
            return new EntitySelectorParser(new StringReader($$0)).parse();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            return null;
        }
    }

    public ScoreContents(String $$0, String $$1) {
        this.name = $$0;
        this.selector = ScoreContents.parseSelector($$0);
        this.objective = $$1;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public EntitySelector getSelector() {
        return this.selector;
    }

    public String getObjective() {
        return this.objective;
    }

    private String findTargetName(CommandSourceStack $$0) throws CommandSyntaxException {
        List<? extends Entity> $$1;
        if (this.selector != null && !($$1 = this.selector.findEntities($$0)).isEmpty()) {
            if ($$1.size() != 1) {
                throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
            }
            return ((Entity)$$1.get(0)).getScoreboardName();
        }
        return this.name;
    }

    private String getScore(String $$0, CommandSourceStack $$1) {
        Objective $$4;
        ServerScoreboard $$3;
        MinecraftServer $$2 = $$1.getServer();
        if ($$2 != null && ($$3 = $$2.getScoreboard()).hasPlayerScore($$0, $$4 = $$3.getObjective(this.objective))) {
            Score $$5 = $$3.getOrCreatePlayerScore($$0, $$4);
            return Integer.toString((int)$$5.getScore());
        }
        return "";
    }

    @Override
    public MutableComponent resolve(@Nullable CommandSourceStack $$0, @Nullable Entity $$1, int $$2) throws CommandSyntaxException {
        if ($$0 == null) {
            return Component.empty();
        }
        String $$3 = this.findTargetName($$0);
        String $$4 = $$1 != null && $$3.equals((Object)SCORER_PLACEHOLDER) ? $$1.getScoreboardName() : $$3;
        return Component.literal(this.getScore($$4, $$0));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof ScoreContents)) return false;
        ScoreContents $$1 = (ScoreContents)$$0;
        if (!this.name.equals((Object)$$1.name)) return false;
        if (!this.objective.equals((Object)$$1.objective)) return false;
        return true;
    }

    public int hashCode() {
        int $$0 = this.name.hashCode();
        $$0 = 31 * $$0 + this.objective.hashCode();
        return $$0;
    }

    public String toString() {
        return "score{name='" + this.name + "', objective='" + this.objective + "'}";
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Map
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.world.scores;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public abstract class Team {
    public boolean isAlliedTo(@Nullable Team $$0) {
        if ($$0 == null) {
            return false;
        }
        return this == $$0;
    }

    public abstract String getName();

    public abstract MutableComponent getFormattedName(Component var1);

    public abstract boolean canSeeFriendlyInvisibles();

    public abstract boolean isAllowFriendlyFire();

    public abstract Visibility getNameTagVisibility();

    public abstract ChatFormatting getColor();

    public abstract Collection<String> getPlayers();

    public abstract Visibility getDeathMessageVisibility();

    public abstract CollisionRule getCollisionRule();

    public static enum CollisionRule {
        ALWAYS("always", 0),
        NEVER("never", 1),
        PUSH_OTHER_TEAMS("pushOtherTeams", 2),
        PUSH_OWN_TEAM("pushOwnTeam", 3);

        private static final Map<String, CollisionRule> BY_NAME;
        public final String name;
        public final int id;

        @Nullable
        public static CollisionRule byName(String $$0) {
            return (CollisionRule)((Object)BY_NAME.get((Object)$$0));
        }

        private CollisionRule(String $$0, int $$1) {
            this.name = $$0;
            this.id = $$1;
        }

        public Component getDisplayName() {
            return Component.translatable("team.collision." + this.name);
        }

        static {
            BY_NAME = (Map)Arrays.stream((Object[])CollisionRule.values()).collect(Collectors.toMap($$0 -> $$0.name, $$0 -> $$0));
        }
    }

    public static enum Visibility {
        ALWAYS("always", 0),
        NEVER("never", 1),
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
        HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

        private static final Map<String, Visibility> BY_NAME;
        public final String name;
        public final int id;

        public static String[] getAllNames() {
            return (String[])BY_NAME.keySet().toArray((Object[])new String[0]);
        }

        @Nullable
        public static Visibility byName(String $$0) {
            return (Visibility)((Object)BY_NAME.get((Object)$$0));
        }

        private Visibility(String $$0, int $$1) {
            this.name = $$0;
            this.id = $$1;
        }

        public Component getDisplayName() {
            return Component.translatable("team.visibility." + this.name);
        }

        static {
            BY_NAME = (Map)Arrays.stream((Object[])Visibility.values()).collect(Collectors.toMap($$0 -> $$0.name, $$0 -> $$0));
        }
    }
}
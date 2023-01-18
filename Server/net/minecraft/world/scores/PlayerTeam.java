/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.world.scores;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class PlayerTeam
extends Team {
    private static final int BIT_FRIENDLY_FIRE = 0;
    private static final int BIT_SEE_INVISIBLES = 1;
    private final Scoreboard scoreboard;
    private final String name;
    private final Set<String> players = Sets.newHashSet();
    private Component displayName;
    private Component playerPrefix = CommonComponents.EMPTY;
    private Component playerSuffix = CommonComponents.EMPTY;
    private boolean allowFriendlyFire = true;
    private boolean seeFriendlyInvisibles = true;
    private Team.Visibility nameTagVisibility = Team.Visibility.ALWAYS;
    private Team.Visibility deathMessageVisibility = Team.Visibility.ALWAYS;
    private ChatFormatting color = ChatFormatting.RESET;
    private Team.CollisionRule collisionRule = Team.CollisionRule.ALWAYS;
    private final Style displayNameStyle;

    public PlayerTeam(Scoreboard $$0, String $$1) {
        this.scoreboard = $$0;
        this.name = $$1;
        this.displayName = Component.literal($$1);
        this.displayNameStyle = Style.EMPTY.withInsertion($$1).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal($$1)));
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public MutableComponent getFormattedDisplayName() {
        MutableComponent $$0 = ComponentUtils.wrapInSquareBrackets(this.displayName.copy().withStyle(this.displayNameStyle));
        ChatFormatting $$1 = this.getColor();
        if ($$1 != ChatFormatting.RESET) {
            $$0.withStyle($$1);
        }
        return $$0;
    }

    public void setDisplayName(Component $$0) {
        if ($$0 == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.displayName = $$0;
        this.scoreboard.onTeamChanged(this);
    }

    public void setPlayerPrefix(@Nullable Component $$0) {
        this.playerPrefix = $$0 == null ? CommonComponents.EMPTY : $$0;
        this.scoreboard.onTeamChanged(this);
    }

    public Component getPlayerPrefix() {
        return this.playerPrefix;
    }

    public void setPlayerSuffix(@Nullable Component $$0) {
        this.playerSuffix = $$0 == null ? CommonComponents.EMPTY : $$0;
        this.scoreboard.onTeamChanged(this);
    }

    public Component getPlayerSuffix() {
        return this.playerSuffix;
    }

    @Override
    public Collection<String> getPlayers() {
        return this.players;
    }

    @Override
    public MutableComponent getFormattedName(Component $$0) {
        MutableComponent $$1 = Component.empty().append(this.playerPrefix).append($$0).append(this.playerSuffix);
        ChatFormatting $$2 = this.getColor();
        if ($$2 != ChatFormatting.RESET) {
            $$1.withStyle($$2);
        }
        return $$1;
    }

    public static MutableComponent formatNameForTeam(@Nullable Team $$0, Component $$1) {
        if ($$0 == null) {
            return $$1.copy();
        }
        return $$0.getFormattedName($$1);
    }

    @Override
    public boolean isAllowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean $$0) {
        this.allowFriendlyFire = $$0;
        this.scoreboard.onTeamChanged(this);
    }

    @Override
    public boolean canSeeFriendlyInvisibles() {
        return this.seeFriendlyInvisibles;
    }

    public void setSeeFriendlyInvisibles(boolean $$0) {
        this.seeFriendlyInvisibles = $$0;
        this.scoreboard.onTeamChanged(this);
    }

    @Override
    public Team.Visibility getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    @Override
    public Team.Visibility getDeathMessageVisibility() {
        return this.deathMessageVisibility;
    }

    public void setNameTagVisibility(Team.Visibility $$0) {
        this.nameTagVisibility = $$0;
        this.scoreboard.onTeamChanged(this);
    }

    public void setDeathMessageVisibility(Team.Visibility $$0) {
        this.deathMessageVisibility = $$0;
        this.scoreboard.onTeamChanged(this);
    }

    @Override
    public Team.CollisionRule getCollisionRule() {
        return this.collisionRule;
    }

    public void setCollisionRule(Team.CollisionRule $$0) {
        this.collisionRule = $$0;
        this.scoreboard.onTeamChanged(this);
    }

    public int packOptions() {
        int $$0 = 0;
        if (this.isAllowFriendlyFire()) {
            $$0 |= 1;
        }
        if (this.canSeeFriendlyInvisibles()) {
            $$0 |= 2;
        }
        return $$0;
    }

    public void unpackOptions(int $$0) {
        this.setAllowFriendlyFire(($$0 & 1) > 0);
        this.setSeeFriendlyInvisibles(($$0 & 2) > 0);
    }

    public void setColor(ChatFormatting $$0) {
        this.color = $$0;
        this.scoreboard.onTeamChanged(this);
    }

    @Override
    public ChatFormatting getColor() {
        return this.color;
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.UUID
 */
package net.minecraft.world;

import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public abstract class BossEvent {
    private final UUID id;
    protected Component name;
    protected float progress;
    protected BossBarColor color;
    protected BossBarOverlay overlay;
    protected boolean darkenScreen;
    protected boolean playBossMusic;
    protected boolean createWorldFog;

    public BossEvent(UUID $$0, Component $$1, BossBarColor $$2, BossBarOverlay $$3) {
        this.id = $$0;
        this.name = $$1;
        this.color = $$2;
        this.overlay = $$3;
        this.progress = 1.0f;
    }

    public UUID getId() {
        return this.id;
    }

    public Component getName() {
        return this.name;
    }

    public void setName(Component $$0) {
        this.name = $$0;
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float $$0) {
        this.progress = $$0;
    }

    public BossBarColor getColor() {
        return this.color;
    }

    public void setColor(BossBarColor $$0) {
        this.color = $$0;
    }

    public BossBarOverlay getOverlay() {
        return this.overlay;
    }

    public void setOverlay(BossBarOverlay $$0) {
        this.overlay = $$0;
    }

    public boolean shouldDarkenScreen() {
        return this.darkenScreen;
    }

    public BossEvent setDarkenScreen(boolean $$0) {
        this.darkenScreen = $$0;
        return this;
    }

    public boolean shouldPlayBossMusic() {
        return this.playBossMusic;
    }

    public BossEvent setPlayBossMusic(boolean $$0) {
        this.playBossMusic = $$0;
        return this;
    }

    public BossEvent setCreateWorldFog(boolean $$0) {
        this.createWorldFog = $$0;
        return this;
    }

    public boolean shouldCreateWorldFog() {
        return this.createWorldFog;
    }

    public static enum BossBarColor {
        PINK("pink", ChatFormatting.RED),
        BLUE("blue", ChatFormatting.BLUE),
        RED("red", ChatFormatting.DARK_RED),
        GREEN("green", ChatFormatting.GREEN),
        YELLOW("yellow", ChatFormatting.YELLOW),
        PURPLE("purple", ChatFormatting.DARK_BLUE),
        WHITE("white", ChatFormatting.WHITE);

        private final String name;
        private final ChatFormatting formatting;

        private BossBarColor(String $$0, ChatFormatting $$1) {
            this.name = $$0;
            this.formatting = $$1;
        }

        public ChatFormatting getFormatting() {
            return this.formatting;
        }

        public String getName() {
            return this.name;
        }

        public static BossBarColor byName(String $$0) {
            for (BossBarColor $$1 : BossBarColor.values()) {
                if (!$$1.name.equals((Object)$$0)) continue;
                return $$1;
            }
            return WHITE;
        }
    }

    public static enum BossBarOverlay {
        PROGRESS("progress"),
        NOTCHED_6("notched_6"),
        NOTCHED_10("notched_10"),
        NOTCHED_12("notched_12"),
        NOTCHED_20("notched_20");

        private final String name;

        private BossBarOverlay(String $$0) {
            this.name = $$0;
        }

        public String getName() {
            return this.name;
        }

        public static BossBarOverlay byName(String $$0) {
            for (BossBarOverlay $$1 : BossBarOverlay.values()) {
                if (!$$1.name.equals((Object)$$0)) continue;
                return $$1;
            }
            return PROGRESS;
        }
    }
}
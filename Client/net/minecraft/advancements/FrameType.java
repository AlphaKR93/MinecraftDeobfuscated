/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.advancements;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum FrameType {
    TASK("task", 0, ChatFormatting.GREEN),
    CHALLENGE("challenge", 26, ChatFormatting.DARK_PURPLE),
    GOAL("goal", 52, ChatFormatting.GREEN);

    private final String name;
    private final int texture;
    private final ChatFormatting chatColor;
    private final Component displayName;

    private FrameType(String $$0, int $$1, ChatFormatting $$2) {
        this.name = $$0;
        this.texture = $$1;
        this.chatColor = $$2;
        this.displayName = Component.translatable("advancements.toast." + $$0);
    }

    public String getName() {
        return this.name;
    }

    public int getTexture() {
        return this.texture;
    }

    public static FrameType byName(String $$0) {
        for (FrameType $$1 : FrameType.values()) {
            if (!$$1.name.equals((Object)$$0)) continue;
            return $$1;
        }
        throw new IllegalArgumentException("Unknown frame type '" + $$0 + "'");
    }

    public ChatFormatting getChatColor() {
        return this.chatColor;
    }

    public Component getDisplayName() {
        return this.displayName;
    }
}
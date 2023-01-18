/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public record GuiMessageTag(int indicatorColor, @Nullable Icon icon, @Nullable Component text, @Nullable String logTag) {
    private static final Component SYSTEM_TEXT = Component.translatable("chat.tag.system");
    private static final Component SYSTEM_TEXT_SINGLE_PLAYER = Component.translatable("chat.tag.system_single_player");
    private static final Component CHAT_NOT_SECURE_TEXT = Component.translatable("chat.tag.not_secure");
    private static final Component CHAT_MODIFIED_TEXT = Component.translatable("chat.tag.modified");
    private static final int CHAT_NOT_SECURE_INDICATOR_COLOR = 0xD0D0D0;
    private static final int CHAT_MODIFIED_INDICATOR_COLOR = 0x606060;
    private static final GuiMessageTag SYSTEM = new GuiMessageTag(0xD0D0D0, null, SYSTEM_TEXT, "System");
    private static final GuiMessageTag SYSTEM_SINGLE_PLAYER = new GuiMessageTag(0xD0D0D0, null, SYSTEM_TEXT_SINGLE_PLAYER, "System");
    private static final GuiMessageTag CHAT_NOT_SECURE = new GuiMessageTag(0xD0D0D0, null, CHAT_NOT_SECURE_TEXT, "Not Secure");
    static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/chat_tags.png");

    public static GuiMessageTag system() {
        return SYSTEM;
    }

    public static GuiMessageTag systemSinglePlayer() {
        return SYSTEM_SINGLE_PLAYER;
    }

    public static GuiMessageTag chatNotSecure() {
        return CHAT_NOT_SECURE;
    }

    public static GuiMessageTag chatModified(String $$0) {
        MutableComponent $$1 = Component.literal($$0).withStyle(ChatFormatting.GRAY);
        MutableComponent $$2 = Component.empty().append(CHAT_MODIFIED_TEXT).append(CommonComponents.NEW_LINE).append($$1);
        return new GuiMessageTag(0x606060, Icon.CHAT_MODIFIED, $$2, "Modified");
    }

    public static enum Icon {
        CHAT_MODIFIED(0, 0, 9, 9);

        public final int u;
        public final int v;
        public final int width;
        public final int height;

        private Icon(int $$0, int $$1, int $$2, int $$3) {
            this.u = $$0;
            this.v = $$1;
            this.width = $$2;
            this.height = $$3;
        }

        public void draw(PoseStack $$0, int $$1, int $$2) {
            RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
            GuiComponent.blit($$0, $$1, $$2, this.u, this.v, this.width, this.height, 32, 32);
        }
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.ListIterator
 *  java.util.Objects
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.slf4j.Logger;

public class ChatComponent
extends GuiComponent {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_CHAT_HISTORY = 100;
    private static final int MESSAGE_NOT_FOUND = -1;
    private static final int MESSAGE_INDENT = 4;
    private static final int MESSAGE_TAG_MARGIN_LEFT = 4;
    private static final int BOTTOM_MARGIN = 40;
    private static final int TIME_BEFORE_MESSAGE_DELETION = 60;
    private static final Component DELETED_CHAT_MESSAGE = Component.translatable("chat.deleted_marker").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
    private final Minecraft minecraft;
    private final List<String> recentChat = Lists.newArrayList();
    private final List<GuiMessage> allMessages = Lists.newArrayList();
    private final List<GuiMessage.Line> trimmedMessages = Lists.newArrayList();
    private int chatScrollbarPos;
    private boolean newMessageSinceScroll;
    private final List<DelayedMessageDeletion> messageDeletionQueue = new ArrayList();

    public ChatComponent(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void tick() {
        if (!this.messageDeletionQueue.isEmpty()) {
            this.processMessageDeletionQueue();
        }
    }

    public void render(PoseStack $$0, int $$1, int $$2, int $$3) {
        if (this.isChatHidden()) {
            return;
        }
        int $$4 = this.getLinesPerPage();
        int $$5 = this.trimmedMessages.size();
        if ($$5 <= 0) {
            return;
        }
        boolean $$6 = this.isChatFocused();
        float $$7 = (float)this.getScale();
        int $$8 = Mth.ceil((float)this.getWidth() / $$7);
        int $$9 = this.minecraft.getWindow().getGuiScaledHeight();
        $$0.pushPose();
        $$0.scale($$7, $$7, 1.0f);
        $$0.translate(4.0f, 0.0f, 0.0f);
        int $$10 = Mth.floor((float)($$9 - 40) / $$7);
        int $$11 = this.getMessageEndIndexAt(this.screenToChatX($$2), this.screenToChatY($$3));
        double $$12 = this.minecraft.options.chatOpacity().get() * (double)0.9f + (double)0.1f;
        double $$13 = this.minecraft.options.textBackgroundOpacity().get();
        double $$14 = this.minecraft.options.chatLineSpacing().get();
        int $$15 = this.getLineHeight();
        int $$16 = (int)Math.round((double)(-8.0 * ($$14 + 1.0) + 4.0 * $$14));
        int $$17 = 0;
        for (int $$18 = 0; $$18 + this.chatScrollbarPos < this.trimmedMessages.size() && $$18 < $$4; ++$$18) {
            int $$21;
            int $$19 = $$18 + this.chatScrollbarPos;
            GuiMessage.Line $$20 = (GuiMessage.Line)((Object)this.trimmedMessages.get($$19));
            if ($$20 == null || ($$21 = $$1 - $$20.addedTime()) >= 200 && !$$6) continue;
            double $$22 = $$6 ? 1.0 : ChatComponent.getTimeFactor($$21);
            int $$23 = (int)(255.0 * $$22 * $$12);
            int $$24 = (int)(255.0 * $$22 * $$13);
            ++$$17;
            if ($$23 <= 3) continue;
            boolean $$25 = false;
            int $$26 = $$10 - $$18 * $$15;
            int $$27 = $$26 + $$16;
            $$0.pushPose();
            $$0.translate(0.0f, 0.0f, 50.0f);
            ChatComponent.fill($$0, -4, $$26 - $$15, 0 + $$8 + 4 + 4, $$26, $$24 << 24);
            GuiMessageTag $$28 = $$20.tag();
            if ($$28 != null) {
                int $$29 = $$28.indicatorColor() | $$23 << 24;
                ChatComponent.fill($$0, -4, $$26 - $$15, -2, $$26, $$29);
                if ($$19 == $$11 && $$28.icon() != null) {
                    int $$30 = this.getTagIconLeft($$20);
                    Objects.requireNonNull((Object)this.minecraft.font);
                    int $$31 = $$27 + 9;
                    this.drawTagIcon($$0, $$30, $$31, $$28.icon());
                }
            }
            RenderSystem.enableBlend();
            $$0.translate(0.0f, 0.0f, 50.0f);
            this.minecraft.font.drawShadow($$0, $$20.content(), 0.0f, (float)$$27, 0xFFFFFF + ($$23 << 24));
            RenderSystem.disableBlend();
            $$0.popPose();
        }
        long $$32 = this.minecraft.getChatListener().queueSize();
        if ($$32 > 0L) {
            int $$33 = (int)(128.0 * $$12);
            int $$34 = (int)(255.0 * $$13);
            $$0.pushPose();
            $$0.translate(0.0f, $$10, 50.0f);
            ChatComponent.fill($$0, -2, 0, $$8 + 4, 9, $$34 << 24);
            RenderSystem.enableBlend();
            $$0.translate(0.0f, 0.0f, 50.0f);
            this.minecraft.font.drawShadow($$0, Component.translatable("chat.queue", $$32), 0.0f, 1.0f, 0xFFFFFF + ($$33 << 24));
            $$0.popPose();
            RenderSystem.disableBlend();
        }
        if ($$6) {
            int $$35 = this.getLineHeight();
            int $$36 = $$5 * $$35;
            int $$37 = $$17 * $$35;
            int $$38 = this.chatScrollbarPos * $$37 / $$5 - $$10;
            int $$39 = $$37 * $$37 / $$36;
            if ($$36 != $$37) {
                int $$40 = $$38 > 0 ? 170 : 96;
                int $$41 = this.newMessageSinceScroll ? 0xCC3333 : 0x3333AA;
                int $$42 = $$8 + 4;
                ChatComponent.fill($$0, $$42, -$$38, $$42 + 2, -$$38 - $$39, $$41 + ($$40 << 24));
                ChatComponent.fill($$0, $$42 + 2, -$$38, $$42 + 1, -$$38 - $$39, 0xCCCCCC + ($$40 << 24));
            }
        }
        $$0.popPose();
    }

    private void drawTagIcon(PoseStack $$0, int $$1, int $$2, GuiMessageTag.Icon $$3) {
        int $$4 = $$2 - $$3.height - 1;
        $$3.draw($$0, $$1, $$4);
    }

    private int getTagIconLeft(GuiMessage.Line $$0) {
        return this.minecraft.font.width($$0.content()) + 4;
    }

    private boolean isChatHidden() {
        return this.minecraft.options.chatVisibility().get() == ChatVisiblity.HIDDEN;
    }

    private static double getTimeFactor(int $$0) {
        double $$1 = (double)$$0 / 200.0;
        $$1 = 1.0 - $$1;
        $$1 *= 10.0;
        $$1 = Mth.clamp($$1, 0.0, 1.0);
        $$1 *= $$1;
        return $$1;
    }

    public void clearMessages(boolean $$0) {
        this.minecraft.getChatListener().clearQueue();
        this.messageDeletionQueue.clear();
        this.trimmedMessages.clear();
        this.allMessages.clear();
        if ($$0) {
            this.recentChat.clear();
        }
    }

    public void addMessage(Component $$0) {
        this.addMessage($$0, null, this.minecraft.isSingleplayer() ? GuiMessageTag.systemSinglePlayer() : GuiMessageTag.system());
    }

    public void addMessage(Component $$0, @Nullable MessageSignature $$1, @Nullable GuiMessageTag $$2) {
        this.logChatMessage($$0, $$2);
        this.addMessage($$0, $$1, this.minecraft.gui.getGuiTicks(), $$2, false);
    }

    private void logChatMessage(Component $$0, @Nullable GuiMessageTag $$1) {
        String $$2 = $$0.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
        String $$3 = (String)Util.mapNullable($$1, GuiMessageTag::logTag);
        if ($$3 != null) {
            LOGGER.info("[{}] [CHAT] {}", (Object)$$3, (Object)$$2);
        } else {
            LOGGER.info("[CHAT] {}", (Object)$$2);
        }
    }

    private void addMessage(Component $$0, @Nullable MessageSignature $$1, int $$2, @Nullable GuiMessageTag $$3, boolean $$4) {
        int $$5 = Mth.floor((double)this.getWidth() / this.getScale());
        if ($$3 != null && $$3.icon() != null) {
            $$5 -= $$3.icon().width + 4 + 2;
        }
        List<FormattedCharSequence> $$6 = ComponentRenderUtils.wrapComponents($$0, $$5, this.minecraft.font);
        boolean $$7 = this.isChatFocused();
        for (int $$8 = 0; $$8 < $$6.size(); ++$$8) {
            FormattedCharSequence $$9 = (FormattedCharSequence)$$6.get($$8);
            if ($$7 && this.chatScrollbarPos > 0) {
                this.newMessageSinceScroll = true;
                this.scrollChat(1);
            }
            boolean $$10 = $$8 == $$6.size() - 1;
            this.trimmedMessages.add(0, (Object)new GuiMessage.Line($$2, $$9, $$3, $$10));
        }
        while (this.trimmedMessages.size() > 100) {
            this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
        }
        if (!$$4) {
            this.allMessages.add(0, (Object)new GuiMessage($$2, $$0, $$1, $$3));
            while (this.allMessages.size() > 100) {
                this.allMessages.remove(this.allMessages.size() - 1);
            }
        }
    }

    private void processMessageDeletionQueue() {
        int $$0 = this.minecraft.gui.getGuiTicks();
        this.messageDeletionQueue.removeIf($$1 -> {
            if ($$0 >= $$1.deletableAfter()) {
                return this.deleteMessageOrDelay($$1.signature()) == null;
            }
            return false;
        });
    }

    public void deleteMessage(MessageSignature $$0) {
        DelayedMessageDeletion $$1 = this.deleteMessageOrDelay($$0);
        if ($$1 != null) {
            this.messageDeletionQueue.add((Object)$$1);
        }
    }

    @Nullable
    private DelayedMessageDeletion deleteMessageOrDelay(MessageSignature $$0) {
        int $$1 = this.minecraft.gui.getGuiTicks();
        ListIterator $$2 = this.allMessages.listIterator();
        while ($$2.hasNext()) {
            GuiMessage $$3 = (GuiMessage)((Object)$$2.next());
            if (!$$0.equals((Object)$$3.signature())) continue;
            int $$4 = $$3.addedTime() + 60;
            if ($$1 >= $$4) {
                $$2.set((Object)this.createDeletedMarker($$3));
                this.refreshTrimmedMessage();
                return null;
            }
            return new DelayedMessageDeletion($$0, $$4);
        }
        return null;
    }

    private GuiMessage createDeletedMarker(GuiMessage $$0) {
        return new GuiMessage($$0.addedTime(), DELETED_CHAT_MESSAGE, null, GuiMessageTag.system());
    }

    public void rescaleChat() {
        this.resetChatScroll();
        this.refreshTrimmedMessage();
    }

    private void refreshTrimmedMessage() {
        this.trimmedMessages.clear();
        for (int $$0 = this.allMessages.size() - 1; $$0 >= 0; --$$0) {
            GuiMessage $$1 = (GuiMessage)((Object)this.allMessages.get($$0));
            this.addMessage($$1.content(), $$1.signature(), $$1.addedTime(), $$1.tag(), true);
        }
    }

    public List<String> getRecentChat() {
        return this.recentChat;
    }

    public void addRecentChat(String $$0) {
        if (this.recentChat.isEmpty() || !((String)this.recentChat.get(this.recentChat.size() - 1)).equals((Object)$$0)) {
            this.recentChat.add((Object)$$0);
        }
    }

    public void resetChatScroll() {
        this.chatScrollbarPos = 0;
        this.newMessageSinceScroll = false;
    }

    public void scrollChat(int $$0) {
        this.chatScrollbarPos += $$0;
        int $$1 = this.trimmedMessages.size();
        if (this.chatScrollbarPos > $$1 - this.getLinesPerPage()) {
            this.chatScrollbarPos = $$1 - this.getLinesPerPage();
        }
        if (this.chatScrollbarPos <= 0) {
            this.chatScrollbarPos = 0;
            this.newMessageSinceScroll = false;
        }
    }

    public boolean handleChatQueueClicked(double $$0, double $$1) {
        if (!this.isChatFocused() || this.minecraft.options.hideGui || this.isChatHidden()) {
            return false;
        }
        ChatListener $$2 = this.minecraft.getChatListener();
        if ($$2.queueSize() == 0L) {
            return false;
        }
        double $$3 = $$0 - 2.0;
        double $$4 = (double)this.minecraft.getWindow().getGuiScaledHeight() - $$1 - 40.0;
        if ($$3 <= (double)Mth.floor((double)this.getWidth() / this.getScale()) && $$4 < 0.0 && $$4 > (double)Mth.floor(-9.0 * this.getScale())) {
            $$2.acceptNextDelayedMessage();
            return true;
        }
        return false;
    }

    @Nullable
    public Style getClickedComponentStyleAt(double $$0, double $$1) {
        double $$3;
        double $$2 = this.screenToChatX($$0);
        int $$4 = this.getMessageLineIndexAt($$2, $$3 = this.screenToChatY($$1));
        if ($$4 >= 0 && $$4 < this.trimmedMessages.size()) {
            GuiMessage.Line $$5 = (GuiMessage.Line)((Object)this.trimmedMessages.get($$4));
            return this.minecraft.font.getSplitter().componentStyleAtWidth($$5.content(), Mth.floor($$2));
        }
        return null;
    }

    @Nullable
    public GuiMessageTag getMessageTagAt(double $$0, double $$1) {
        GuiMessage.Line $$5;
        GuiMessageTag $$6;
        double $$3;
        double $$2 = this.screenToChatX($$0);
        int $$4 = this.getMessageEndIndexAt($$2, $$3 = this.screenToChatY($$1));
        if ($$4 >= 0 && $$4 < this.trimmedMessages.size() && ($$6 = ($$5 = (GuiMessage.Line)((Object)this.trimmedMessages.get($$4))).tag()) != null && this.hasSelectedMessageTag($$2, $$5, $$6)) {
            return $$6;
        }
        return null;
    }

    private boolean hasSelectedMessageTag(double $$0, GuiMessage.Line $$1, GuiMessageTag $$2) {
        if ($$0 < 0.0) {
            return true;
        }
        GuiMessageTag.Icon $$3 = $$2.icon();
        if ($$3 != null) {
            int $$4 = this.getTagIconLeft($$1);
            int $$5 = $$4 + $$3.width;
            return $$0 >= (double)$$4 && $$0 <= (double)$$5;
        }
        return false;
    }

    private double screenToChatX(double $$0) {
        return $$0 / this.getScale() - 4.0;
    }

    private double screenToChatY(double $$0) {
        double $$1 = (double)this.minecraft.getWindow().getGuiScaledHeight() - $$0 - 40.0;
        return $$1 / (this.getScale() * (double)this.getLineHeight());
    }

    private int getMessageEndIndexAt(double $$0, double $$1) {
        int $$2 = this.getMessageLineIndexAt($$0, $$1);
        if ($$2 == -1) {
            return -1;
        }
        while ($$2 >= 0) {
            if (((GuiMessage.Line)((Object)this.trimmedMessages.get($$2))).endOfEntry()) {
                return $$2;
            }
            --$$2;
        }
        return $$2;
    }

    private int getMessageLineIndexAt(double $$0, double $$1) {
        int $$3;
        if (!this.isChatFocused() || this.minecraft.options.hideGui || this.isChatHidden()) {
            return -1;
        }
        if ($$0 < -4.0 || $$0 > (double)Mth.floor((double)this.getWidth() / this.getScale())) {
            return -1;
        }
        int $$2 = Math.min((int)this.getLinesPerPage(), (int)this.trimmedMessages.size());
        if ($$1 >= 0.0 && $$1 < (double)$$2 && ($$3 = Mth.floor($$1 + (double)this.chatScrollbarPos)) >= 0 && $$3 < this.trimmedMessages.size()) {
            return $$3;
        }
        return -1;
    }

    private boolean isChatFocused() {
        return this.minecraft.screen instanceof ChatScreen;
    }

    public int getWidth() {
        return ChatComponent.getWidth(this.minecraft.options.chatWidth().get());
    }

    public int getHeight() {
        return ChatComponent.getHeight(this.isChatFocused() ? this.minecraft.options.chatHeightFocused().get() : this.minecraft.options.chatHeightUnfocused().get());
    }

    public double getScale() {
        return this.minecraft.options.chatScale().get();
    }

    public static int getWidth(double $$0) {
        int $$1 = 320;
        int $$2 = 40;
        return Mth.floor($$0 * 280.0 + 40.0);
    }

    public static int getHeight(double $$0) {
        int $$1 = 180;
        int $$2 = 20;
        return Mth.floor($$0 * 160.0 + 20.0);
    }

    public static double defaultUnfocusedPct() {
        int $$0 = 180;
        int $$1 = 20;
        return 70.0 / (double)(ChatComponent.getHeight(1.0) - 20);
    }

    public int getLinesPerPage() {
        return this.getHeight() / this.getLineHeight();
    }

    private int getLineHeight() {
        Objects.requireNonNull((Object)this.minecraft.font);
        return (int)(9.0 * (this.minecraft.options.chatLineSpacing().get() + 1.0));
    }

    record DelayedMessageDeletion(MessageSignature signature, int deletableAfter) {
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.UUID
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class SignBlockEntity
extends BlockEntity {
    public static final int LINES = 4;
    private static final int MAX_TEXT_LINE_WIDTH = 90;
    private static final int TEXT_LINE_HEIGHT = 10;
    private static final String[] RAW_TEXT_FIELD_NAMES = new String[]{"Text1", "Text2", "Text3", "Text4"};
    private static final String[] FILTERED_TEXT_FIELD_NAMES = new String[]{"FilteredText1", "FilteredText2", "FilteredText3", "FilteredText4"};
    private final Component[] messages = new Component[]{CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY};
    private final Component[] filteredMessages = new Component[]{CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY};
    private boolean isEditable = true;
    @Nullable
    private UUID playerWhoMayEdit;
    @Nullable
    private FormattedCharSequence[] renderMessages;
    private boolean renderMessagedFiltered;
    private DyeColor color = DyeColor.BLACK;
    private boolean hasGlowingText;

    public SignBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.SIGN, $$0, $$1);
    }

    public SignBlockEntity(BlockEntityType $$0, BlockPos $$1, BlockState $$2) {
        super($$0, $$1, $$2);
    }

    public int getTextLineHeight() {
        return 10;
    }

    public int getMaxTextLineWidth() {
        return 90;
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        for (int $$1 = 0; $$1 < 4; ++$$1) {
            Component $$2 = this.messages[$$1];
            String $$3 = Component.Serializer.toJson($$2);
            $$0.putString(RAW_TEXT_FIELD_NAMES[$$1], $$3);
            Component $$4 = this.filteredMessages[$$1];
            if ($$4.equals($$2)) continue;
            $$0.putString(FILTERED_TEXT_FIELD_NAMES[$$1], Component.Serializer.toJson($$4));
        }
        $$0.putString("Color", this.color.getName());
        $$0.putBoolean("GlowingText", this.hasGlowingText);
    }

    @Override
    public void load(CompoundTag $$0) {
        this.isEditable = false;
        super.load($$0);
        this.color = DyeColor.byName($$0.getString("Color"), DyeColor.BLACK);
        for (int $$1 = 0; $$1 < 4; ++$$1) {
            Component $$3;
            String $$2 = $$0.getString(RAW_TEXT_FIELD_NAMES[$$1]);
            this.messages[$$1] = $$3 = this.loadLine($$2);
            String $$4 = FILTERED_TEXT_FIELD_NAMES[$$1];
            this.filteredMessages[$$1] = $$0.contains($$4, 8) ? this.loadLine($$0.getString($$4)) : $$3;
        }
        this.renderMessages = null;
        this.hasGlowingText = $$0.getBoolean("GlowingText");
    }

    private Component loadLine(String $$0) {
        Component $$1 = this.deserializeTextSafe($$0);
        if (this.level instanceof ServerLevel) {
            try {
                return ComponentUtils.updateForEntity(this.createCommandSourceStack(null), $$1, null, 0);
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }
        return $$1;
    }

    private Component deserializeTextSafe(String $$0) {
        try {
            MutableComponent $$1 = Component.Serializer.fromJson($$0);
            if ($$1 != null) {
                return $$1;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return CommonComponents.EMPTY;
    }

    public Component getMessage(int $$0, boolean $$1) {
        return this.getMessages($$1)[$$0];
    }

    public void setMessage(int $$0, Component $$1) {
        this.setMessage($$0, $$1, $$1);
    }

    public void setMessage(int $$0, Component $$1, Component $$2) {
        this.messages[$$0] = $$1;
        this.filteredMessages[$$0] = $$2;
        this.renderMessages = null;
    }

    public FormattedCharSequence[] getRenderMessages(boolean $$0, Function<Component, FormattedCharSequence> $$1) {
        if (this.renderMessages == null || this.renderMessagedFiltered != $$0) {
            this.renderMessagedFiltered = $$0;
            this.renderMessages = new FormattedCharSequence[4];
            for (int $$2 = 0; $$2 < 4; ++$$2) {
                this.renderMessages[$$2] = (FormattedCharSequence)$$1.apply((Object)this.getMessage($$2, $$0));
            }
        }
        return this.renderMessages;
    }

    private Component[] getMessages(boolean $$0) {
        return $$0 ? this.filteredMessages : this.messages;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean $$0) {
        this.isEditable = $$0;
        if (!$$0) {
            this.playerWhoMayEdit = null;
        }
    }

    public void setAllowedPlayerEditor(UUID $$0) {
        this.playerWhoMayEdit = $$0;
    }

    @Nullable
    public UUID getPlayerWhoMayEdit() {
        return this.playerWhoMayEdit;
    }

    public boolean hasAnyClickCommands(Player $$0) {
        for (Component $$1 : this.getMessages($$0.isTextFilteringEnabled())) {
            Style $$2 = $$1.getStyle();
            ClickEvent $$3 = $$2.getClickEvent();
            if ($$3 == null || $$3.getAction() != ClickEvent.Action.RUN_COMMAND) continue;
            return true;
        }
        return false;
    }

    public boolean executeClickCommands(ServerPlayer $$0) {
        for (Component $$1 : this.getMessages($$0.isTextFilteringEnabled())) {
            Style $$2 = $$1.getStyle();
            ClickEvent $$3 = $$2.getClickEvent();
            if ($$3 == null || $$3.getAction() != ClickEvent.Action.RUN_COMMAND) continue;
            $$0.getServer().getCommands().performPrefixedCommand(this.createCommandSourceStack($$0), $$3.getValue());
        }
        return true;
    }

    public CommandSourceStack createCommandSourceStack(@Nullable ServerPlayer $$0) {
        String $$1 = $$0 == null ? "Sign" : $$0.getName().getString();
        Component $$2 = $$0 == null ? Component.literal("Sign") : $$0.getDisplayName();
        return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(this.worldPosition), Vec2.ZERO, (ServerLevel)this.level, 2, $$1, $$2, this.level.getServer(), $$0);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public boolean setColor(DyeColor $$0) {
        if ($$0 != this.getColor()) {
            this.color = $$0;
            this.markUpdated();
            return true;
        }
        return false;
    }

    public boolean hasGlowingText() {
        return this.hasGlowingText;
    }

    public boolean setHasGlowingText(boolean $$0) {
        if (this.hasGlowingText != $$0) {
            this.hasGlowingText = $$0;
            this.markUpdated();
            return true;
        }
        return false;
    }

    private void markUpdated() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }
}
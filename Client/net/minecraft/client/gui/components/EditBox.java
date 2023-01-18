/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Character
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Objects
 *  java.util.function.BiFunction
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class EditBox
extends AbstractWidget
implements Renderable {
    public static final int BACKWARDS = -1;
    public static final int FORWARDS = 1;
    private static final int CURSOR_INSERT_WIDTH = 1;
    private static final int CURSOR_INSERT_COLOR = -3092272;
    private static final String CURSOR_APPEND_CHARACTER = "_";
    public static final int DEFAULT_TEXT_COLOR = 0xE0E0E0;
    private static final int BORDER_COLOR_FOCUSED = -1;
    private static final int BORDER_COLOR = -6250336;
    private static final int BACKGROUND_COLOR = -16777216;
    private final Font font;
    private String value = "";
    private int maxLength = 32;
    private int frame;
    private boolean bordered = true;
    private boolean canLoseFocus = true;
    private boolean isEditable = true;
    private boolean shiftPressed;
    private int displayPos;
    private int cursorPos;
    private int highlightPos;
    private int textColor = 0xE0E0E0;
    private int textColorUneditable = 0x707070;
    @Nullable
    private String suggestion;
    @Nullable
    private Consumer<String> responder;
    private Predicate<String> filter = Objects::nonNull;
    private BiFunction<String, Integer, FormattedCharSequence> formatter = ($$0, $$1) -> FormattedCharSequence.forward($$0, Style.EMPTY);
    @Nullable
    private Component hint;

    public EditBox(Font $$0, int $$1, int $$2, int $$3, int $$4, Component $$5) {
        this($$0, $$1, $$2, $$3, $$4, null, $$5);
    }

    public EditBox(Font $$02, int $$12, int $$2, int $$3, int $$4, @Nullable EditBox $$5, Component $$6) {
        super($$12, $$2, $$3, $$4, $$6);
        this.font = $$02;
        if ($$5 != null) {
            this.setValue($$5.getValue());
        }
    }

    public void setResponder(Consumer<String> $$0) {
        this.responder = $$0;
    }

    public void setFormatter(BiFunction<String, Integer, FormattedCharSequence> $$0) {
        this.formatter = $$0;
    }

    public void tick() {
        ++this.frame;
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        Component $$0 = this.getMessage();
        return Component.translatable("gui.narrate.editBox", $$0, this.value);
    }

    public void setValue(String $$0) {
        if (!this.filter.test((Object)$$0)) {
            return;
        }
        this.value = $$0.length() > this.maxLength ? $$0.substring(0, this.maxLength) : $$0;
        this.moveCursorToEnd();
        this.setHighlightPos(this.cursorPos);
        this.onValueChange($$0);
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int $$0 = Math.min((int)this.cursorPos, (int)this.highlightPos);
        int $$1 = Math.max((int)this.cursorPos, (int)this.highlightPos);
        return this.value.substring($$0, $$1);
    }

    public void setFilter(Predicate<String> $$0) {
        this.filter = $$0;
    }

    public void insertText(String $$0) {
        String $$6;
        String $$4;
        int $$5;
        int $$1 = Math.min((int)this.cursorPos, (int)this.highlightPos);
        int $$2 = Math.max((int)this.cursorPos, (int)this.highlightPos);
        int $$3 = this.maxLength - this.value.length() - ($$1 - $$2);
        if ($$3 < ($$5 = ($$4 = SharedConstants.filterText($$0)).length())) {
            $$4 = $$4.substring(0, $$3);
            $$5 = $$3;
        }
        if (!this.filter.test((Object)($$6 = new StringBuilder(this.value).replace($$1, $$2, $$4).toString()))) {
            return;
        }
        this.value = $$6;
        this.setCursorPosition($$1 + $$5);
        this.setHighlightPos(this.cursorPos);
        this.onValueChange(this.value);
    }

    private void onValueChange(String $$0) {
        if (this.responder != null) {
            this.responder.accept((Object)$$0);
        }
    }

    private void deleteText(int $$0) {
        if (Screen.hasControlDown()) {
            this.deleteWords($$0);
        } else {
            this.deleteChars($$0);
        }
    }

    public void deleteWords(int $$0) {
        if (this.value.isEmpty()) {
            return;
        }
        if (this.highlightPos != this.cursorPos) {
            this.insertText("");
            return;
        }
        this.deleteChars(this.getWordPosition($$0) - this.cursorPos);
    }

    public void deleteChars(int $$0) {
        int $$3;
        if (this.value.isEmpty()) {
            return;
        }
        if (this.highlightPos != this.cursorPos) {
            this.insertText("");
            return;
        }
        int $$1 = this.getCursorPos($$0);
        int $$2 = Math.min((int)$$1, (int)this.cursorPos);
        if ($$2 == ($$3 = Math.max((int)$$1, (int)this.cursorPos))) {
            return;
        }
        String $$4 = new StringBuilder(this.value).delete($$2, $$3).toString();
        if (!this.filter.test((Object)$$4)) {
            return;
        }
        this.value = $$4;
        this.moveCursorTo($$2);
    }

    public int getWordPosition(int $$0) {
        return this.getWordPosition($$0, this.getCursorPosition());
    }

    private int getWordPosition(int $$0, int $$1) {
        return this.getWordPosition($$0, $$1, true);
    }

    private int getWordPosition(int $$0, int $$1, boolean $$2) {
        int $$3 = $$1;
        boolean $$4 = $$0 < 0;
        int $$5 = Math.abs((int)$$0);
        for (int $$6 = 0; $$6 < $$5; ++$$6) {
            if ($$4) {
                while ($$2 && $$3 > 0 && this.value.charAt($$3 - 1) == ' ') {
                    --$$3;
                }
                while ($$3 > 0 && this.value.charAt($$3 - 1) != ' ') {
                    --$$3;
                }
                continue;
            }
            int $$7 = this.value.length();
            if (($$3 = this.value.indexOf(32, $$3)) == -1) {
                $$3 = $$7;
                continue;
            }
            while ($$2 && $$3 < $$7 && this.value.charAt($$3) == ' ') {
                ++$$3;
            }
        }
        return $$3;
    }

    public void moveCursor(int $$0) {
        this.moveCursorTo(this.getCursorPos($$0));
    }

    private int getCursorPos(int $$0) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, $$0);
    }

    public void moveCursorTo(int $$0) {
        this.setCursorPosition($$0);
        if (!this.shiftPressed) {
            this.setHighlightPos(this.cursorPos);
        }
        this.onValueChange(this.value);
    }

    public void setCursorPosition(int $$0) {
        this.cursorPos = Mth.clamp($$0, 0, this.value.length());
    }

    public void moveCursorToStart() {
        this.moveCursorTo(0);
    }

    public void moveCursorToEnd() {
        this.moveCursorTo(this.value.length());
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (!this.canConsumeInput()) {
            return false;
        }
        this.shiftPressed = Screen.hasShiftDown();
        if (Screen.isSelectAll($$0)) {
            this.moveCursorToEnd();
            this.setHighlightPos(0);
            return true;
        }
        if (Screen.isCopy($$0)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            return true;
        }
        if (Screen.isPaste($$0)) {
            if (this.isEditable) {
                this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            }
            return true;
        }
        if (Screen.isCut($$0)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            if (this.isEditable) {
                this.insertText("");
            }
            return true;
        }
        switch ($$0) {
            case 263: {
                if (Screen.hasControlDown()) {
                    this.moveCursorTo(this.getWordPosition(-1));
                } else {
                    this.moveCursor(-1);
                }
                return true;
            }
            case 262: {
                if (Screen.hasControlDown()) {
                    this.moveCursorTo(this.getWordPosition(1));
                } else {
                    this.moveCursor(1);
                }
                return true;
            }
            case 259: {
                if (this.isEditable) {
                    this.shiftPressed = false;
                    this.deleteText(-1);
                    this.shiftPressed = Screen.hasShiftDown();
                }
                return true;
            }
            case 261: {
                if (this.isEditable) {
                    this.shiftPressed = false;
                    this.deleteText(1);
                    this.shiftPressed = Screen.hasShiftDown();
                }
                return true;
            }
            case 268: {
                this.moveCursorToStart();
                return true;
            }
            case 269: {
                this.moveCursorToEnd();
                return true;
            }
        }
        return false;
    }

    public boolean canConsumeInput() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    @Override
    public boolean charTyped(char $$0, int $$1) {
        if (!this.canConsumeInput()) {
            return false;
        }
        if (SharedConstants.isAllowedChatCharacter($$0)) {
            if (this.isEditable) {
                this.insertText(Character.toString((char)$$0));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        boolean $$3;
        if (!this.isVisible()) {
            return false;
        }
        boolean bl = $$3 = $$0 >= (double)this.getX() && $$0 < (double)(this.getX() + this.width) && $$1 >= (double)this.getY() && $$1 < (double)(this.getY() + this.height);
        if (this.canLoseFocus) {
            this.setFocused($$3);
        }
        if (this.isFocused() && $$3 && $$2 == 0) {
            int $$4 = Mth.floor($$0) - this.getX();
            if (this.bordered) {
                $$4 -= 4;
            }
            String $$5 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            this.moveCursorTo(this.font.plainSubstrByWidth($$5, $$4).length() + this.displayPos);
            return true;
        }
        return false;
    }

    @Override
    public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (!this.isVisible()) {
            return;
        }
        if (this.isBordered()) {
            int $$4 = this.isFocused() ? -1 : -6250336;
            EditBox.fill($$0, this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, $$4);
            EditBox.fill($$0, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
        }
        int $$5 = this.isEditable ? this.textColor : this.textColorUneditable;
        int $$6 = this.cursorPos - this.displayPos;
        int $$7 = this.highlightPos - this.displayPos;
        String $$8 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        boolean $$9 = $$6 >= 0 && $$6 <= $$8.length();
        boolean $$10 = this.isFocused() && this.frame / 6 % 2 == 0 && $$9;
        int $$11 = this.bordered ? this.getX() + 4 : this.getX();
        int $$12 = this.bordered ? this.getY() + (this.height - 8) / 2 : this.getY();
        int $$13 = $$11;
        if ($$7 > $$8.length()) {
            $$7 = $$8.length();
        }
        if (!$$8.isEmpty()) {
            String $$14 = $$9 ? $$8.substring(0, $$6) : $$8;
            $$13 = this.font.drawShadow($$0, (FormattedCharSequence)this.formatter.apply((Object)$$14, (Object)this.displayPos), (float)$$13, (float)$$12, $$5);
        }
        boolean $$15 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
        int $$16 = $$13;
        if (!$$9) {
            $$16 = $$6 > 0 ? $$11 + this.width : $$11;
        } else if ($$15) {
            --$$16;
            --$$13;
        }
        if (!$$8.isEmpty() && $$9 && $$6 < $$8.length()) {
            this.font.drawShadow($$0, (FormattedCharSequence)this.formatter.apply((Object)$$8.substring($$6), (Object)this.cursorPos), (float)$$13, (float)$$12, $$5);
        }
        if (this.hint != null && $$8.isEmpty() && !this.isFocused()) {
            this.font.drawShadow($$0, this.hint, (float)$$13, (float)$$12, $$5);
        }
        if (!$$15 && this.suggestion != null) {
            this.font.drawShadow($$0, this.suggestion, (float)($$16 - 1), (float)$$12, -8355712);
        }
        if ($$10) {
            if ($$15) {
                Objects.requireNonNull((Object)this.font);
                GuiComponent.fill($$0, $$16, $$12 - 1, $$16 + 1, $$12 + 1 + 9, -3092272);
            } else {
                this.font.drawShadow($$0, CURSOR_APPEND_CHARACTER, (float)$$16, (float)$$12, $$5);
            }
        }
        if ($$7 != $$6) {
            int $$17 = $$11 + this.font.width($$8.substring(0, $$7));
            Objects.requireNonNull((Object)this.font);
            this.renderHighlight($$0, $$16, $$12 - 1, $$17 - 1, $$12 + 1 + 9);
        }
    }

    private void renderHighlight(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        if ($$1 < $$3) {
            int $$5 = $$1;
            $$1 = $$3;
            $$3 = $$5;
        }
        if ($$2 < $$4) {
            int $$6 = $$2;
            $$2 = $$4;
            $$4 = $$6;
        }
        if ($$3 > this.getX() + this.width) {
            $$3 = this.getX() + this.width;
        }
        if ($$1 > this.getX() + this.width) {
            $$1 = this.getX() + this.width;
        }
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        EditBox.fill($$0, $$1, $$2, $$3, $$4, -16776961);
        RenderSystem.disableColorLogicOp();
    }

    public void setMaxLength(int $$0) {
        this.maxLength = $$0;
        if (this.value.length() > $$0) {
            this.value = this.value.substring(0, $$0);
            this.onValueChange(this.value);
        }
    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    private boolean isBordered() {
        return this.bordered;
    }

    public void setBordered(boolean $$0) {
        this.bordered = $$0;
    }

    public void setTextColor(int $$0) {
        this.textColor = $$0;
    }

    public void setTextColorUneditable(int $$0) {
        this.textColorUneditable = $$0;
    }

    @Override
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent $$0) {
        if (!this.visible || !this.isEditable) {
            return null;
        }
        return super.nextFocusPath($$0);
    }

    @Override
    public boolean isMouseOver(double $$0, double $$1) {
        return this.visible && $$0 >= (double)this.getX() && $$0 < (double)(this.getX() + this.width) && $$1 >= (double)this.getY() && $$1 < (double)(this.getY() + this.height);
    }

    @Override
    public void setFocused(boolean $$0) {
        if (!this.canLoseFocus && !$$0) {
            return;
        }
        super.setFocused($$0);
        if ($$0) {
            this.frame = 0;
        }
    }

    private boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean $$0) {
        this.isEditable = $$0;
    }

    public int getInnerWidth() {
        return this.isBordered() ? this.width - 8 : this.width;
    }

    public void setHighlightPos(int $$0) {
        int $$1 = this.value.length();
        this.highlightPos = Mth.clamp($$0, 0, $$1);
        if (this.font != null) {
            if (this.displayPos > $$1) {
                this.displayPos = $$1;
            }
            int $$2 = this.getInnerWidth();
            String $$3 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), $$2);
            int $$4 = $$3.length() + this.displayPos;
            if (this.highlightPos == this.displayPos) {
                this.displayPos -= this.font.plainSubstrByWidth(this.value, $$2, true).length();
            }
            if (this.highlightPos > $$4) {
                this.displayPos += this.highlightPos - $$4;
            } else if (this.highlightPos <= this.displayPos) {
                this.displayPos -= this.displayPos - this.highlightPos;
            }
            this.displayPos = Mth.clamp(this.displayPos, 0, $$1);
        }
    }

    public void setCanLoseFocus(boolean $$0) {
        this.canLoseFocus = $$0;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean $$0) {
        this.visible = $$0;
    }

    public void setSuggestion(@Nullable String $$0) {
        this.suggestion = $$0;
    }

    public int getScreenX(int $$0) {
        if ($$0 > this.value.length()) {
            return this.getX();
        }
        return this.getX() + this.font.width(this.value.substring(0, $$0));
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
    }

    public void setHint(Component $$0) {
        this.hint = $$0;
    }
}
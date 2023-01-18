/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  java.lang.Character
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.List
 *  java.util.Objects
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.components;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;

public class MultilineTextField {
    public static final int NO_CHARACTER_LIMIT = Integer.MAX_VALUE;
    private static final int LINE_SEEK_PIXEL_BIAS = 2;
    private final Font font;
    private final List<StringView> displayLines = Lists.newArrayList();
    private String value;
    private int cursor;
    private int selectCursor;
    private boolean selecting;
    private int characterLimit = Integer.MAX_VALUE;
    private final int width;
    private Consumer<String> valueListener = $$0 -> {};
    private Runnable cursorListener = () -> {};

    public MultilineTextField(Font $$02, int $$1) {
        this.font = $$02;
        this.width = $$1;
        this.setValue("");
    }

    public int characterLimit() {
        return this.characterLimit;
    }

    public void setCharacterLimit(int $$0) {
        if ($$0 < 0) {
            throw new IllegalArgumentException("Character limit cannot be negative");
        }
        this.characterLimit = $$0;
    }

    public boolean hasCharacterLimit() {
        return this.characterLimit != Integer.MAX_VALUE;
    }

    public void setValueListener(Consumer<String> $$0) {
        this.valueListener = $$0;
    }

    public void setCursorListener(Runnable $$0) {
        this.cursorListener = $$0;
    }

    public void setValue(String $$0) {
        this.value = this.truncateFullText($$0);
        this.selectCursor = this.cursor = this.value.length();
        this.onValueChange();
    }

    public String value() {
        return this.value;
    }

    public void insertText(String $$0) {
        if ($$0.isEmpty() && !this.hasSelection()) {
            return;
        }
        String $$1 = this.truncateInsertionText(SharedConstants.filterText($$0, true));
        StringView $$2 = this.getSelected();
        this.value = new StringBuilder(this.value).replace($$2.beginIndex, $$2.endIndex, $$1).toString();
        this.selectCursor = this.cursor = $$2.beginIndex + $$1.length();
        this.onValueChange();
    }

    public void deleteText(int $$0) {
        if (!this.hasSelection()) {
            this.selectCursor = Mth.clamp(this.cursor + $$0, 0, this.value.length());
        }
        this.insertText("");
    }

    public int cursor() {
        return this.cursor;
    }

    public void setSelecting(boolean $$0) {
        this.selecting = $$0;
    }

    public StringView getSelected() {
        return new StringView(Math.min((int)this.selectCursor, (int)this.cursor), Math.max((int)this.selectCursor, (int)this.cursor));
    }

    public int getLineCount() {
        return this.displayLines.size();
    }

    public int getLineAtCursor() {
        for (int $$0 = 0; $$0 < this.displayLines.size(); ++$$0) {
            StringView $$1 = (StringView)((Object)this.displayLines.get($$0));
            if (this.cursor < $$1.beginIndex || this.cursor > $$1.endIndex) continue;
            return $$0;
        }
        return -1;
    }

    public StringView getLineView(int $$0) {
        return (StringView)((Object)this.displayLines.get(Mth.clamp($$0, 0, this.displayLines.size() - 1)));
    }

    public void seekCursor(Whence $$0, int $$1) {
        switch ($$0) {
            case ABSOLUTE: {
                this.cursor = $$1;
                break;
            }
            case RELATIVE: {
                this.cursor += $$1;
                break;
            }
            case END: {
                this.cursor = this.value.length() + $$1;
            }
        }
        this.cursor = Mth.clamp(this.cursor, 0, this.value.length());
        this.cursorListener.run();
        if (!this.selecting) {
            this.selectCursor = this.cursor;
        }
    }

    public void seekCursorLine(int $$0) {
        if ($$0 == 0) {
            return;
        }
        int $$1 = this.font.width(this.value.substring(this.getCursorLineView().beginIndex, this.cursor)) + 2;
        StringView $$2 = this.getCursorLineView($$0);
        int $$3 = this.font.plainSubstrByWidth(this.value.substring($$2.beginIndex, $$2.endIndex), $$1).length();
        this.seekCursor(Whence.ABSOLUTE, $$2.beginIndex + $$3);
    }

    public void seekCursorToPoint(double $$0, double $$1) {
        int $$2 = Mth.floor($$0);
        Objects.requireNonNull((Object)this.font);
        int $$3 = Mth.floor($$1 / 9.0);
        StringView $$4 = (StringView)((Object)this.displayLines.get(Mth.clamp($$3, 0, this.displayLines.size() - 1)));
        int $$5 = this.font.plainSubstrByWidth(this.value.substring($$4.beginIndex, $$4.endIndex), $$2).length();
        this.seekCursor(Whence.ABSOLUTE, $$4.beginIndex + $$5);
    }

    public boolean keyPressed(int $$0) {
        this.selecting = Screen.hasShiftDown();
        if (Screen.isSelectAll($$0)) {
            this.cursor = this.value.length();
            this.selectCursor = 0;
            return true;
        }
        if (Screen.isCopy($$0)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            return true;
        }
        if (Screen.isPaste($$0)) {
            this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            return true;
        }
        if (Screen.isCut($$0)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            this.insertText("");
            return true;
        }
        switch ($$0) {
            case 263: {
                if (Screen.hasControlDown()) {
                    StringView $$1 = this.getPreviousWord();
                    this.seekCursor(Whence.ABSOLUTE, $$1.beginIndex);
                } else {
                    this.seekCursor(Whence.RELATIVE, -1);
                }
                return true;
            }
            case 262: {
                if (Screen.hasControlDown()) {
                    StringView $$2 = this.getNextWord();
                    this.seekCursor(Whence.ABSOLUTE, $$2.beginIndex);
                } else {
                    this.seekCursor(Whence.RELATIVE, 1);
                }
                return true;
            }
            case 265: {
                if (!Screen.hasControlDown()) {
                    this.seekCursorLine(-1);
                }
                return true;
            }
            case 264: {
                if (!Screen.hasControlDown()) {
                    this.seekCursorLine(1);
                }
                return true;
            }
            case 266: {
                this.seekCursor(Whence.ABSOLUTE, 0);
                return true;
            }
            case 267: {
                this.seekCursor(Whence.END, 0);
                return true;
            }
            case 268: {
                if (Screen.hasControlDown()) {
                    this.seekCursor(Whence.ABSOLUTE, 0);
                } else {
                    this.seekCursor(Whence.ABSOLUTE, this.getCursorLineView().beginIndex);
                }
                return true;
            }
            case 269: {
                if (Screen.hasControlDown()) {
                    this.seekCursor(Whence.END, 0);
                } else {
                    this.seekCursor(Whence.ABSOLUTE, this.getCursorLineView().endIndex);
                }
                return true;
            }
            case 259: {
                if (Screen.hasControlDown()) {
                    StringView $$3 = this.getPreviousWord();
                    this.deleteText($$3.beginIndex - this.cursor);
                } else {
                    this.deleteText(-1);
                }
                return true;
            }
            case 261: {
                if (Screen.hasControlDown()) {
                    StringView $$4 = this.getNextWord();
                    this.deleteText($$4.beginIndex - this.cursor);
                } else {
                    this.deleteText(1);
                }
                return true;
            }
            case 257: 
            case 335: {
                this.insertText("\n");
                return true;
            }
        }
        return false;
    }

    public Iterable<StringView> iterateLines() {
        return this.displayLines;
    }

    public boolean hasSelection() {
        return this.selectCursor != this.cursor;
    }

    @VisibleForTesting
    public String getSelectedText() {
        StringView $$0 = this.getSelected();
        return this.value.substring($$0.beginIndex, $$0.endIndex);
    }

    private StringView getCursorLineView() {
        return this.getCursorLineView(0);
    }

    private StringView getCursorLineView(int $$0) {
        int $$1 = this.getLineAtCursor();
        if ($$1 < 0) {
            throw new IllegalStateException("Cursor is not within text (cursor = " + this.cursor + ", length = " + this.value.length() + ")");
        }
        return (StringView)((Object)this.displayLines.get(Mth.clamp($$1 + $$0, 0, this.displayLines.size() - 1)));
    }

    @VisibleForTesting
    public StringView getPreviousWord() {
        int $$0;
        if (this.value.isEmpty()) {
            return StringView.EMPTY;
        }
        for ($$0 = Mth.clamp(this.cursor, 0, this.value.length() - 1); $$0 > 0 && Character.isWhitespace((char)this.value.charAt($$0 - 1)); --$$0) {
        }
        while ($$0 > 0 && !Character.isWhitespace((char)this.value.charAt($$0 - 1))) {
            --$$0;
        }
        return new StringView($$0, this.getWordEndPosition($$0));
    }

    @VisibleForTesting
    public StringView getNextWord() {
        int $$0;
        if (this.value.isEmpty()) {
            return StringView.EMPTY;
        }
        for ($$0 = Mth.clamp(this.cursor, 0, this.value.length() - 1); $$0 < this.value.length() && !Character.isWhitespace((char)this.value.charAt($$0)); ++$$0) {
        }
        while ($$0 < this.value.length() && Character.isWhitespace((char)this.value.charAt($$0))) {
            ++$$0;
        }
        return new StringView($$0, this.getWordEndPosition($$0));
    }

    private int getWordEndPosition(int $$0) {
        int $$1;
        for ($$1 = $$0; $$1 < this.value.length() && !Character.isWhitespace((char)this.value.charAt($$1)); ++$$1) {
        }
        return $$1;
    }

    private void onValueChange() {
        this.reflowDisplayLines();
        this.valueListener.accept((Object)this.value);
        this.cursorListener.run();
    }

    private void reflowDisplayLines() {
        this.displayLines.clear();
        if (this.value.isEmpty()) {
            this.displayLines.add((Object)StringView.EMPTY);
            return;
        }
        this.font.getSplitter().splitLines(this.value, this.width, Style.EMPTY, false, ($$0, $$1, $$2) -> this.displayLines.add((Object)new StringView($$1, $$2)));
        if (this.value.charAt(this.value.length() - 1) == '\n') {
            this.displayLines.add((Object)new StringView(this.value.length(), this.value.length()));
        }
    }

    private String truncateFullText(String $$0) {
        if (this.hasCharacterLimit()) {
            return StringUtil.truncateStringIfNecessary($$0, this.characterLimit, false);
        }
        return $$0;
    }

    private String truncateInsertionText(String $$0) {
        if (this.hasCharacterLimit()) {
            int $$1 = this.characterLimit - this.value.length();
            return StringUtil.truncateStringIfNecessary($$0, $$1, false);
        }
        return $$0;
    }

    protected record StringView(int beginIndex, int endIndex) {
        static final StringView EMPTY = new StringView(0, 0);
    }
}
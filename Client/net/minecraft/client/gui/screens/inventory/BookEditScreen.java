/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  java.lang.Character
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.List
 *  java.util.ListIterator
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class BookEditScreen
extends Screen {
    private static final int TEXT_WIDTH = 114;
    private static final int TEXT_HEIGHT = 128;
    private static final int IMAGE_WIDTH = 192;
    private static final int IMAGE_HEIGHT = 192;
    private static final Component EDIT_TITLE_LABEL = Component.translatable("book.editTitle");
    private static final Component FINALIZE_WARNING_LABEL = Component.translatable("book.finalizeWarning");
    private static final FormattedCharSequence BLACK_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.BLACK));
    private static final FormattedCharSequence GRAY_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.GRAY));
    private final Player owner;
    private final ItemStack book;
    private boolean isModified;
    private boolean isSigning;
    private int frameTick;
    private int currentPage;
    private final List<String> pages = Lists.newArrayList();
    private String title = "";
    private final TextFieldHelper pageEdit = new TextFieldHelper((Supplier<String>)((Supplier)this::getCurrentPageText), (Consumer<String>)((Consumer)this::setCurrentPageText), (Supplier<String>)((Supplier)this::getClipboard), (Consumer<String>)((Consumer)this::setClipboard), (Predicate<String>)((Predicate)$$0 -> $$0.length() < 1024 && this.font.wordWrapHeight((String)$$0, 114) <= 128));
    private final TextFieldHelper titleEdit = new TextFieldHelper((Supplier<String>)((Supplier)() -> this.title), (Consumer<String>)((Consumer)$$0 -> {
        this.title = $$0;
    }), (Supplier<String>)((Supplier)this::getClipboard), (Consumer<String>)((Consumer)this::setClipboard), (Predicate<String>)((Predicate)$$0 -> $$0.length() < 16));
    private long lastClickTime;
    private int lastIndex = -1;
    private PageButton forwardButton;
    private PageButton backButton;
    private Button doneButton;
    private Button signButton;
    private Button finalizeButton;
    private Button cancelButton;
    private final InteractionHand hand;
    @Nullable
    private DisplayCache displayCache = DisplayCache.EMPTY;
    private Component pageMsg = CommonComponents.EMPTY;
    private final Component ownerText;

    public BookEditScreen(Player $$02, ItemStack $$1, InteractionHand $$2) {
        super(GameNarrator.NO_TITLE);
        this.owner = $$02;
        this.book = $$1;
        this.hand = $$2;
        CompoundTag $$3 = $$1.getTag();
        if ($$3 != null) {
            BookViewScreen.loadPages($$3, (Consumer<String>)((Consumer)arg_0 -> this.pages.add(arg_0)));
        }
        if (this.pages.isEmpty()) {
            this.pages.add((Object)"");
        }
        this.ownerText = Component.translatable("book.byAuthor", $$02.getName()).withStyle(ChatFormatting.DARK_GRAY);
    }

    private void setClipboard(String $$0) {
        if (this.minecraft != null) {
            TextFieldHelper.setClipboardContents(this.minecraft, $$0);
        }
    }

    private String getClipboard() {
        return this.minecraft != null ? TextFieldHelper.getClipboardContents(this.minecraft) : "";
    }

    private int getNumPages() {
        return this.pages.size();
    }

    @Override
    public void tick() {
        super.tick();
        ++this.frameTick;
    }

    @Override
    protected void init() {
        this.clearDisplayCache();
        this.signButton = this.addRenderableWidget(Button.builder(Component.translatable("book.signButton"), $$0 -> {
            this.isSigning = true;
            this.updateButtonVisibility();
        }).bounds(this.width / 2 - 100, 196, 98, 20).build());
        this.doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> {
            this.minecraft.setScreen(null);
            this.saveChanges(false);
        }).bounds(this.width / 2 + 2, 196, 98, 20).build());
        this.finalizeButton = this.addRenderableWidget(Button.builder(Component.translatable("book.finalizeButton"), $$0 -> {
            if (this.isSigning) {
                this.saveChanges(true);
                this.minecraft.setScreen(null);
            }
        }).bounds(this.width / 2 - 100, 196, 98, 20).build());
        this.cancelButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> {
            if (this.isSigning) {
                this.isSigning = false;
            }
            this.updateButtonVisibility();
        }).bounds(this.width / 2 + 2, 196, 98, 20).build());
        int $$02 = (this.width - 192) / 2;
        int $$1 = 2;
        this.forwardButton = this.addRenderableWidget(new PageButton($$02 + 116, 159, true, $$0 -> this.pageForward(), true));
        this.backButton = this.addRenderableWidget(new PageButton($$02 + 43, 159, false, $$0 -> this.pageBack(), true));
        this.updateButtonVisibility();
    }

    private void pageBack() {
        if (this.currentPage > 0) {
            --this.currentPage;
        }
        this.updateButtonVisibility();
        this.clearDisplayCacheAfterPageChange();
    }

    private void pageForward() {
        if (this.currentPage < this.getNumPages() - 1) {
            ++this.currentPage;
        } else {
            this.appendPageToBook();
            if (this.currentPage < this.getNumPages() - 1) {
                ++this.currentPage;
            }
        }
        this.updateButtonVisibility();
        this.clearDisplayCacheAfterPageChange();
    }

    private void updateButtonVisibility() {
        this.backButton.visible = !this.isSigning && this.currentPage > 0;
        this.forwardButton.visible = !this.isSigning;
        this.doneButton.visible = !this.isSigning;
        this.signButton.visible = !this.isSigning;
        this.cancelButton.visible = this.isSigning;
        this.finalizeButton.visible = this.isSigning;
        this.finalizeButton.active = !this.title.trim().isEmpty();
    }

    private void eraseEmptyTrailingPages() {
        ListIterator $$0 = this.pages.listIterator(this.pages.size());
        while ($$0.hasPrevious() && ((String)$$0.previous()).isEmpty()) {
            $$0.remove();
        }
    }

    private void saveChanges(boolean $$0) {
        if (!this.isModified) {
            return;
        }
        this.eraseEmptyTrailingPages();
        this.updateLocalCopy($$0);
        int $$1 = this.hand == InteractionHand.MAIN_HAND ? this.owner.getInventory().selected : 40;
        this.minecraft.getConnection().send(new ServerboundEditBookPacket($$1, this.pages, (Optional<String>)($$0 ? Optional.of((Object)this.title.trim()) : Optional.empty())));
    }

    private void updateLocalCopy(boolean $$0) {
        ListTag $$1 = new ListTag();
        this.pages.stream().map(StringTag::valueOf).forEach(arg_0 -> ((ListTag)$$1).add(arg_0));
        if (!this.pages.isEmpty()) {
            this.book.addTagElement("pages", $$1);
        }
        if ($$0) {
            this.book.addTagElement("author", StringTag.valueOf(this.owner.getGameProfile().getName()));
            this.book.addTagElement("title", StringTag.valueOf(this.title.trim()));
        }
    }

    private void appendPageToBook() {
        if (this.getNumPages() >= 100) {
            return;
        }
        this.pages.add((Object)"");
        this.isModified = true;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        if (this.isSigning) {
            return this.titleKeyPressed($$0, $$1, $$2);
        }
        boolean $$3 = this.bookKeyPressed($$0, $$1, $$2);
        if ($$3) {
            this.clearDisplayCache();
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char $$0, int $$1) {
        if (super.charTyped($$0, $$1)) {
            return true;
        }
        if (this.isSigning) {
            boolean $$2 = this.titleEdit.charTyped($$0);
            if ($$2) {
                this.updateButtonVisibility();
                this.isModified = true;
                return true;
            }
            return false;
        }
        if (SharedConstants.isAllowedChatCharacter($$0)) {
            this.pageEdit.insertText(Character.toString((char)$$0));
            this.clearDisplayCache();
            return true;
        }
        return false;
    }

    private boolean bookKeyPressed(int $$0, int $$1, int $$2) {
        if (Screen.isSelectAll($$0)) {
            this.pageEdit.selectAll();
            return true;
        }
        if (Screen.isCopy($$0)) {
            this.pageEdit.copy();
            return true;
        }
        if (Screen.isPaste($$0)) {
            this.pageEdit.paste();
            return true;
        }
        if (Screen.isCut($$0)) {
            this.pageEdit.cut();
            return true;
        }
        TextFieldHelper.CursorStep $$3 = Screen.hasControlDown() ? TextFieldHelper.CursorStep.WORD : TextFieldHelper.CursorStep.CHARACTER;
        switch ($$0) {
            case 259: {
                this.pageEdit.removeFromCursor(-1, $$3);
                return true;
            }
            case 261: {
                this.pageEdit.removeFromCursor(1, $$3);
                return true;
            }
            case 257: 
            case 335: {
                this.pageEdit.insertText("\n");
                return true;
            }
            case 263: {
                this.pageEdit.moveBy(-1, Screen.hasShiftDown(), $$3);
                return true;
            }
            case 262: {
                this.pageEdit.moveBy(1, Screen.hasShiftDown(), $$3);
                return true;
            }
            case 265: {
                this.keyUp();
                return true;
            }
            case 264: {
                this.keyDown();
                return true;
            }
            case 266: {
                this.backButton.onPress();
                return true;
            }
            case 267: {
                this.forwardButton.onPress();
                return true;
            }
            case 268: {
                this.keyHome();
                return true;
            }
            case 269: {
                this.keyEnd();
                return true;
            }
        }
        return false;
    }

    private void keyUp() {
        this.changeLine(-1);
    }

    private void keyDown() {
        this.changeLine(1);
    }

    private void changeLine(int $$0) {
        int $$1 = this.pageEdit.getCursorPos();
        int $$2 = this.getDisplayCache().changeLine($$1, $$0);
        this.pageEdit.setCursorPos($$2, Screen.hasShiftDown());
    }

    private void keyHome() {
        if (Screen.hasControlDown()) {
            this.pageEdit.setCursorToStart(Screen.hasShiftDown());
        } else {
            int $$0 = this.pageEdit.getCursorPos();
            int $$1 = this.getDisplayCache().findLineStart($$0);
            this.pageEdit.setCursorPos($$1, Screen.hasShiftDown());
        }
    }

    private void keyEnd() {
        if (Screen.hasControlDown()) {
            this.pageEdit.setCursorToEnd(Screen.hasShiftDown());
        } else {
            DisplayCache $$0 = this.getDisplayCache();
            int $$1 = this.pageEdit.getCursorPos();
            int $$2 = $$0.findLineEnd($$1);
            this.pageEdit.setCursorPos($$2, Screen.hasShiftDown());
        }
    }

    private boolean titleKeyPressed(int $$0, int $$1, int $$2) {
        switch ($$0) {
            case 259: {
                this.titleEdit.removeCharsFromCursor(-1);
                this.updateButtonVisibility();
                this.isModified = true;
                return true;
            }
            case 257: 
            case 335: {
                if (!this.title.isEmpty()) {
                    this.saveChanges(true);
                    this.minecraft.setScreen(null);
                }
                return true;
            }
        }
        return false;
    }

    private String getCurrentPageText() {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            return (String)this.pages.get(this.currentPage);
        }
        return "";
    }

    private void setCurrentPageText(String $$0) {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            this.pages.set(this.currentPage, (Object)$$0);
            this.isModified = true;
            this.clearDisplayCache();
        }
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.setFocused(null);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, BookViewScreen.BOOK_LOCATION);
        int $$4 = (this.width - 192) / 2;
        int $$5 = 2;
        this.blit($$0, $$4, 2, 0, 0, 192, 192);
        if (this.isSigning) {
            boolean $$6 = this.frameTick / 6 % 2 == 0;
            FormattedCharSequence $$7 = FormattedCharSequence.composite(FormattedCharSequence.forward(this.title, Style.EMPTY), $$6 ? BLACK_CURSOR : GRAY_CURSOR);
            int $$8 = this.font.width(EDIT_TITLE_LABEL);
            this.font.draw($$0, EDIT_TITLE_LABEL, (float)($$4 + 36 + (114 - $$8) / 2), 34.0f, 0);
            int $$9 = this.font.width($$7);
            this.font.draw($$0, $$7, (float)($$4 + 36 + (114 - $$9) / 2), 50.0f, 0);
            int $$10 = this.font.width(this.ownerText);
            this.font.draw($$0, this.ownerText, (float)($$4 + 36 + (114 - $$10) / 2), 60.0f, 0);
            this.font.drawWordWrap(FINALIZE_WARNING_LABEL, $$4 + 36, 82, 114, 0);
        } else {
            int $$11 = this.font.width(this.pageMsg);
            this.font.draw($$0, this.pageMsg, (float)($$4 - $$11 + 192 - 44), 18.0f, 0);
            DisplayCache $$12 = this.getDisplayCache();
            for (LineInfo $$13 : $$12.lines) {
                this.font.draw($$0, $$13.asComponent, (float)$$13.x, (float)$$13.y, -16777216);
            }
            this.renderHighlight($$0, $$12.selection);
            this.renderCursor($$0, $$12.cursor, $$12.cursorAtEnd);
        }
        super.render($$0, $$1, $$2, $$3);
    }

    private void renderCursor(PoseStack $$0, Pos2i $$1, boolean $$2) {
        if (this.frameTick / 6 % 2 == 0) {
            $$1 = this.convertLocalToScreen($$1);
            if (!$$2) {
                int n = $$1.x;
                int n2 = $$1.y - 1;
                int n3 = $$1.x + 1;
                int n4 = $$1.y;
                Objects.requireNonNull((Object)this.font);
                GuiComponent.fill($$0, n, n2, n3, n4 + 9, -16777216);
            } else {
                this.font.draw($$0, "_", (float)$$1.x, (float)$$1.y, 0);
            }
        }
    }

    private void renderHighlight(PoseStack $$0, Rect2i[] $$1) {
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        for (Rect2i $$2 : $$1) {
            int $$3 = $$2.getX();
            int $$4 = $$2.getY();
            int $$5 = $$3 + $$2.getWidth();
            int $$6 = $$4 + $$2.getHeight();
            BookEditScreen.fill($$0, $$3, $$4, $$5, $$6, -16776961);
        }
        RenderSystem.disableColorLogicOp();
    }

    private Pos2i convertScreenToLocal(Pos2i $$0) {
        return new Pos2i($$0.x - (this.width - 192) / 2 - 36, $$0.y - 32);
    }

    private Pos2i convertLocalToScreen(Pos2i $$0) {
        return new Pos2i($$0.x + (this.width - 192) / 2 + 36, $$0.y + 32);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (super.mouseClicked($$0, $$1, $$2)) {
            return true;
        }
        if ($$2 == 0) {
            long $$3 = Util.getMillis();
            DisplayCache $$4 = this.getDisplayCache();
            int $$5 = $$4.getIndexAtPosition(this.font, this.convertScreenToLocal(new Pos2i((int)$$0, (int)$$1)));
            if ($$5 >= 0) {
                if ($$5 == this.lastIndex && $$3 - this.lastClickTime < 250L) {
                    if (!this.pageEdit.isSelecting()) {
                        this.selectWord($$5);
                    } else {
                        this.pageEdit.selectAll();
                    }
                } else {
                    this.pageEdit.setCursorPos($$5, Screen.hasShiftDown());
                }
                this.clearDisplayCache();
            }
            this.lastIndex = $$5;
            this.lastClickTime = $$3;
        }
        return true;
    }

    private void selectWord(int $$0) {
        String $$1 = this.getCurrentPageText();
        this.pageEdit.setSelectionRange(StringSplitter.getWordPosition($$1, -1, $$0, false), StringSplitter.getWordPosition($$1, 1, $$0, false));
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (super.mouseDragged($$0, $$1, $$2, $$3, $$4)) {
            return true;
        }
        if ($$2 == 0) {
            DisplayCache $$5 = this.getDisplayCache();
            int $$6 = $$5.getIndexAtPosition(this.font, this.convertScreenToLocal(new Pos2i((int)$$0, (int)$$1)));
            this.pageEdit.setCursorPos($$6, true);
            this.clearDisplayCache();
        }
        return true;
    }

    private DisplayCache getDisplayCache() {
        if (this.displayCache == null) {
            this.displayCache = this.rebuildDisplayCache();
            this.pageMsg = Component.translatable("book.pageIndicator", this.currentPage + 1, this.getNumPages());
        }
        return this.displayCache;
    }

    private void clearDisplayCache() {
        this.displayCache = null;
    }

    private void clearDisplayCacheAfterPageChange() {
        this.pageEdit.setCursorToEnd();
        this.clearDisplayCache();
    }

    private DisplayCache rebuildDisplayCache() {
        Pos2i $$13;
        boolean $$9;
        String $$0 = this.getCurrentPageText();
        if ($$0.isEmpty()) {
            return DisplayCache.EMPTY;
        }
        int $$1 = this.pageEdit.getCursorPos();
        int $$2 = this.pageEdit.getSelectionPos();
        IntArrayList $$3 = new IntArrayList();
        ArrayList $$4 = Lists.newArrayList();
        MutableInt $$5 = new MutableInt();
        MutableBoolean $$6 = new MutableBoolean();
        StringSplitter $$7 = this.font.getSplitter();
        $$7.splitLines($$0, 114, Style.EMPTY, true, (arg_0, arg_1, arg_2) -> this.lambda$rebuildDisplayCache$10($$5, $$0, $$6, (IntList)$$3, (List)$$4, arg_0, arg_1, arg_2));
        int[] $$8 = $$3.toIntArray();
        boolean bl = $$9 = $$1 == $$0.length();
        if ($$9 && $$6.isTrue()) {
            int n = $$4.size();
            Objects.requireNonNull((Object)this.font);
            Pos2i $$10 = new Pos2i(0, n * 9);
        } else {
            int $$11 = BookEditScreen.findLineFromPos($$8, $$1);
            int $$12 = this.font.width($$0.substring($$8[$$11], $$1));
            Objects.requireNonNull((Object)this.font);
            $$13 = new Pos2i($$12, $$11 * 9);
        }
        ArrayList $$14 = Lists.newArrayList();
        if ($$1 != $$2) {
            int $$18;
            int $$15 = Math.min((int)$$1, (int)$$2);
            int $$16 = Math.max((int)$$1, (int)$$2);
            int $$17 = BookEditScreen.findLineFromPos($$8, $$15);
            if ($$17 == ($$18 = BookEditScreen.findLineFromPos($$8, $$16))) {
                Objects.requireNonNull((Object)this.font);
                int $$19 = $$17 * 9;
                int $$20 = $$8[$$17];
                $$14.add((Object)this.createPartialLineSelection($$0, $$7, $$15, $$16, $$19, $$20));
            } else {
                int $$21 = $$17 + 1 > $$8.length ? $$0.length() : $$8[$$17 + 1];
                Objects.requireNonNull((Object)this.font);
                $$14.add((Object)this.createPartialLineSelection($$0, $$7, $$15, $$21, $$17 * 9, $$8[$$17]));
                for (int $$22 = $$17 + 1; $$22 < $$18; ++$$22) {
                    Objects.requireNonNull((Object)this.font);
                    int $$23 = $$22 * 9;
                    String $$24 = $$0.substring($$8[$$22], $$8[$$22 + 1]);
                    int $$25 = (int)$$7.stringWidth($$24);
                    Pos2i pos2i = new Pos2i(0, $$23);
                    Objects.requireNonNull((Object)this.font);
                    $$14.add((Object)this.createSelection(pos2i, new Pos2i($$25, $$23 + 9)));
                }
                int n = $$8[$$18];
                Objects.requireNonNull((Object)this.font);
                $$14.add((Object)this.createPartialLineSelection($$0, $$7, n, $$16, $$18 * 9, $$8[$$18]));
            }
        }
        return new DisplayCache($$0, $$13, $$9, $$8, (LineInfo[])$$4.toArray((Object[])new LineInfo[0]), (Rect2i[])$$14.toArray((Object[])new Rect2i[0]));
    }

    static int findLineFromPos(int[] $$0, int $$1) {
        int $$2 = Arrays.binarySearch((int[])$$0, (int)$$1);
        if ($$2 < 0) {
            return -($$2 + 2);
        }
        return $$2;
    }

    private Rect2i createPartialLineSelection(String $$0, StringSplitter $$1, int $$2, int $$3, int $$4, int $$5) {
        String $$6 = $$0.substring($$5, $$2);
        String $$7 = $$0.substring($$5, $$3);
        Pos2i $$8 = new Pos2i((int)$$1.stringWidth($$6), $$4);
        int n = (int)$$1.stringWidth($$7);
        Objects.requireNonNull((Object)this.font);
        Pos2i $$9 = new Pos2i(n, $$4 + 9);
        return this.createSelection($$8, $$9);
    }

    private Rect2i createSelection(Pos2i $$0, Pos2i $$1) {
        Pos2i $$2 = this.convertLocalToScreen($$0);
        Pos2i $$3 = this.convertLocalToScreen($$1);
        int $$4 = Math.min((int)$$2.x, (int)$$3.x);
        int $$5 = Math.max((int)$$2.x, (int)$$3.x);
        int $$6 = Math.min((int)$$2.y, (int)$$3.y);
        int $$7 = Math.max((int)$$2.y, (int)$$3.y);
        return new Rect2i($$4, $$6, $$5 - $$4, $$7 - $$6);
    }

    private /* synthetic */ void lambda$rebuildDisplayCache$10(MutableInt $$0, String $$1, MutableBoolean $$2, IntList $$3, List $$4, Style $$5, int $$6, int $$7) {
        int $$8 = $$0.getAndIncrement();
        String $$9 = $$1.substring($$6, $$7);
        $$2.setValue($$9.endsWith("\n"));
        String $$10 = StringUtils.stripEnd((String)$$9, (String)" \n");
        Objects.requireNonNull((Object)this.font);
        int $$11 = $$8 * 9;
        Pos2i $$12 = this.convertLocalToScreen(new Pos2i(0, $$11));
        $$3.add($$6);
        $$4.add((Object)new LineInfo($$5, $$10, $$12.x, $$12.y));
    }

    static class DisplayCache {
        static final DisplayCache EMPTY = new DisplayCache("", new Pos2i(0, 0), true, new int[]{0}, new LineInfo[]{new LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
        private final String fullText;
        final Pos2i cursor;
        final boolean cursorAtEnd;
        private final int[] lineStarts;
        final LineInfo[] lines;
        final Rect2i[] selection;

        public DisplayCache(String $$0, Pos2i $$1, boolean $$2, int[] $$3, LineInfo[] $$4, Rect2i[] $$5) {
            this.fullText = $$0;
            this.cursor = $$1;
            this.cursorAtEnd = $$2;
            this.lineStarts = $$3;
            this.lines = $$4;
            this.selection = $$5;
        }

        public int getIndexAtPosition(Font $$0, Pos2i $$1) {
            int n = $$1.y;
            Objects.requireNonNull((Object)$$0);
            int $$2 = n / 9;
            if ($$2 < 0) {
                return 0;
            }
            if ($$2 >= this.lines.length) {
                return this.fullText.length();
            }
            LineInfo $$3 = this.lines[$$2];
            return this.lineStarts[$$2] + $$0.getSplitter().plainIndexAtWidth($$3.contents, $$1.x, $$3.style);
        }

        public int changeLine(int $$0, int $$1) {
            int $$7;
            int $$2 = BookEditScreen.findLineFromPos(this.lineStarts, $$0);
            int $$3 = $$2 + $$1;
            if (0 <= $$3 && $$3 < this.lineStarts.length) {
                int $$4 = $$0 - this.lineStarts[$$2];
                int $$5 = this.lines[$$3].contents.length();
                int $$6 = this.lineStarts[$$3] + Math.min((int)$$4, (int)$$5);
            } else {
                $$7 = $$0;
            }
            return $$7;
        }

        public int findLineStart(int $$0) {
            int $$1 = BookEditScreen.findLineFromPos(this.lineStarts, $$0);
            return this.lineStarts[$$1];
        }

        public int findLineEnd(int $$0) {
            int $$1 = BookEditScreen.findLineFromPos(this.lineStarts, $$0);
            return this.lineStarts[$$1] + this.lines[$$1].contents.length();
        }
    }

    static class LineInfo {
        final Style style;
        final String contents;
        final Component asComponent;
        final int x;
        final int y;

        public LineInfo(Style $$0, String $$1, int $$2, int $$3) {
            this.style = $$0;
            this.contents = $$1;
            this.x = $$2;
            this.y = $$3;
            this.asComponent = Component.literal($$1).setStyle($$0);
        }
    }

    static class Pos2i {
        public final int x;
        public final int y;

        Pos2i(int $$0, int $$1) {
            this.x = $$0;
            this.y = $$1;
        }
    }
}
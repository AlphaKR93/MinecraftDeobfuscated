/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collections
 *  java.util.List
 *  java.util.Objects
 *  java.util.function.Consumer
 *  java.util.function.IntFunction
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;

public class BookViewScreen
extends Screen {
    public static final int PAGE_INDICATOR_TEXT_Y_OFFSET = 16;
    public static final int PAGE_TEXT_X_OFFSET = 36;
    public static final int PAGE_TEXT_Y_OFFSET = 30;
    public static final BookAccess EMPTY_ACCESS = new BookAccess(){

        @Override
        public int getPageCount() {
            return 0;
        }

        @Override
        public FormattedText getPageRaw(int $$0) {
            return FormattedText.EMPTY;
        }
    };
    public static final ResourceLocation BOOK_LOCATION = new ResourceLocation("textures/gui/book.png");
    protected static final int TEXT_WIDTH = 114;
    protected static final int TEXT_HEIGHT = 128;
    protected static final int IMAGE_WIDTH = 192;
    protected static final int IMAGE_HEIGHT = 192;
    private BookAccess bookAccess;
    private int currentPage;
    private List<FormattedCharSequence> cachedPageComponents = Collections.emptyList();
    private int cachedPage = -1;
    private Component pageMsg = CommonComponents.EMPTY;
    private PageButton forwardButton;
    private PageButton backButton;
    private final boolean playTurnSound;

    public BookViewScreen(BookAccess $$0) {
        this($$0, true);
    }

    public BookViewScreen() {
        this(EMPTY_ACCESS, false);
    }

    private BookViewScreen(BookAccess $$0, boolean $$1) {
        super(GameNarrator.NO_TITLE);
        this.bookAccess = $$0;
        this.playTurnSound = $$1;
    }

    public void setBookAccess(BookAccess $$0) {
        this.bookAccess = $$0;
        this.currentPage = Mth.clamp(this.currentPage, 0, $$0.getPageCount());
        this.updateButtonVisibility();
        this.cachedPage = -1;
    }

    public boolean setPage(int $$0) {
        int $$1 = Mth.clamp($$0, 0, this.bookAccess.getPageCount() - 1);
        if ($$1 != this.currentPage) {
            this.currentPage = $$1;
            this.updateButtonVisibility();
            this.cachedPage = -1;
            return true;
        }
        return false;
    }

    protected boolean forcePage(int $$0) {
        return this.setPage($$0);
    }

    @Override
    protected void init() {
        this.createMenuControls();
        this.createPageControlButtons();
    }

    protected void createMenuControls() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.minecraft.setScreen(null)).bounds(this.width / 2 - 100, 196, 200, 20).build());
    }

    protected void createPageControlButtons() {
        int $$02 = (this.width - 192) / 2;
        int $$1 = 2;
        this.forwardButton = this.addRenderableWidget(new PageButton($$02 + 116, 159, true, $$0 -> this.pageForward(), this.playTurnSound));
        this.backButton = this.addRenderableWidget(new PageButton($$02 + 43, 159, false, $$0 -> this.pageBack(), this.playTurnSound));
        this.updateButtonVisibility();
    }

    private int getNumPages() {
        return this.bookAccess.getPageCount();
    }

    protected void pageBack() {
        if (this.currentPage > 0) {
            --this.currentPage;
        }
        this.updateButtonVisibility();
    }

    protected void pageForward() {
        if (this.currentPage < this.getNumPages() - 1) {
            ++this.currentPage;
        }
        this.updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        this.forwardButton.visible = this.currentPage < this.getNumPages() - 1;
        this.backButton.visible = this.currentPage > 0;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        switch ($$0) {
            case 266: {
                this.backButton.onPress();
                return true;
            }
            case 267: {
                this.forwardButton.onPress();
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, BOOK_LOCATION);
        int $$4 = (this.width - 192) / 2;
        int $$5 = 2;
        this.blit($$0, $$4, 2, 0, 0, 192, 192);
        if (this.cachedPage != this.currentPage) {
            FormattedText $$6 = this.bookAccess.getPage(this.currentPage);
            this.cachedPageComponents = this.font.split($$6, 114);
            this.pageMsg = Component.translatable("book.pageIndicator", this.currentPage + 1, Math.max((int)this.getNumPages(), (int)1));
        }
        this.cachedPage = this.currentPage;
        int $$7 = this.font.width(this.pageMsg);
        this.font.draw($$0, this.pageMsg, (float)($$4 - $$7 + 192 - 44), 18.0f, 0);
        Objects.requireNonNull((Object)this.font);
        int $$8 = Math.min((int)(128 / 9), (int)this.cachedPageComponents.size());
        for (int $$9 = 0; $$9 < $$8; ++$$9) {
            FormattedCharSequence $$10 = (FormattedCharSequence)this.cachedPageComponents.get($$9);
            float f = $$4 + 36;
            Objects.requireNonNull((Object)this.font);
            this.font.draw($$0, $$10, f, (float)(32 + $$9 * 9), 0);
        }
        Style $$11 = this.getClickedComponentStyleAt($$1, $$2);
        if ($$11 != null) {
            this.renderComponentHoverEffect($$0, $$11, $$1, $$2);
        }
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        Style $$3;
        if ($$2 == 0 && ($$3 = this.getClickedComponentStyleAt($$0, $$1)) != null && this.handleComponentClicked($$3)) {
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean handleComponentClicked(Style $$0) {
        ClickEvent $$1 = $$0.getClickEvent();
        if ($$1 == null) {
            return false;
        }
        if ($$1.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String $$2 = $$1.getValue();
            try {
                int $$3 = Integer.parseInt((String)$$2) - 1;
                return this.forcePage($$3);
            }
            catch (Exception exception) {
                return false;
            }
        }
        boolean $$4 = super.handleComponentClicked($$0);
        if ($$4 && $$1.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.closeScreen();
        }
        return $$4;
    }

    protected void closeScreen() {
        this.minecraft.setScreen(null);
    }

    @Nullable
    public Style getClickedComponentStyleAt(double $$0, double $$1) {
        if (this.cachedPageComponents.isEmpty()) {
            return null;
        }
        int $$2 = Mth.floor($$0 - (double)((this.width - 192) / 2) - 36.0);
        int $$3 = Mth.floor($$1 - 2.0 - 30.0);
        if ($$2 < 0 || $$3 < 0) {
            return null;
        }
        Objects.requireNonNull((Object)this.font);
        int $$4 = Math.min((int)(128 / 9), (int)this.cachedPageComponents.size());
        if ($$2 <= 114) {
            Objects.requireNonNull((Object)this.minecraft.font);
            if ($$3 < 9 * $$4 + $$4) {
                Objects.requireNonNull((Object)this.minecraft.font);
                int $$5 = $$3 / 9;
                if ($$5 >= 0 && $$5 < this.cachedPageComponents.size()) {
                    FormattedCharSequence $$6 = (FormattedCharSequence)this.cachedPageComponents.get($$5);
                    return this.minecraft.font.getSplitter().componentStyleAtWidth($$6, $$2);
                }
                return null;
            }
        }
        return null;
    }

    static List<String> loadPages(CompoundTag $$0) {
        ImmutableList.Builder $$1 = ImmutableList.builder();
        BookViewScreen.loadPages($$0, (Consumer<String>)((Consumer)arg_0 -> ((ImmutableList.Builder)$$1).add(arg_0)));
        return $$1.build();
    }

    public static void loadPages(CompoundTag $$0, Consumer<String> $$1) {
        IntFunction $$5;
        ListTag $$22 = $$0.getList("pages", 8).copy();
        if (Minecraft.getInstance().isTextFilteringEnabled() && $$0.contains("filtered_pages", 10)) {
            CompoundTag $$3 = $$0.getCompound("filtered_pages");
            IntFunction $$4 = $$2 -> {
                String $$3 = String.valueOf((int)$$2);
                return $$3.contains($$3) ? $$3.getString($$3) : $$22.getString($$2);
            };
        } else {
            $$5 = $$22::getString;
        }
        for (int $$6 = 0; $$6 < $$22.size(); ++$$6) {
            $$1.accept((Object)((String)$$5.apply($$6)));
        }
    }

    public static interface BookAccess {
        public int getPageCount();

        public FormattedText getPageRaw(int var1);

        default public FormattedText getPage(int $$0) {
            if ($$0 >= 0 && $$0 < this.getPageCount()) {
                return this.getPageRaw($$0);
            }
            return FormattedText.EMPTY;
        }

        public static BookAccess fromItem(ItemStack $$0) {
            if ($$0.is(Items.WRITTEN_BOOK)) {
                return new WrittenBookAccess($$0);
            }
            if ($$0.is(Items.WRITABLE_BOOK)) {
                return new WritableBookAccess($$0);
            }
            return EMPTY_ACCESS;
        }
    }

    public static class WritableBookAccess
    implements BookAccess {
        private final List<String> pages;

        public WritableBookAccess(ItemStack $$0) {
            this.pages = WritableBookAccess.readPages($$0);
        }

        private static List<String> readPages(ItemStack $$0) {
            CompoundTag $$1 = $$0.getTag();
            return $$1 != null ? BookViewScreen.loadPages($$1) : ImmutableList.of();
        }

        @Override
        public int getPageCount() {
            return this.pages.size();
        }

        @Override
        public FormattedText getPageRaw(int $$0) {
            return FormattedText.of((String)this.pages.get($$0));
        }
    }

    public static class WrittenBookAccess
    implements BookAccess {
        private final List<String> pages;

        public WrittenBookAccess(ItemStack $$0) {
            this.pages = WrittenBookAccess.readPages($$0);
        }

        private static List<String> readPages(ItemStack $$0) {
            CompoundTag $$1 = $$0.getTag();
            if ($$1 != null && WrittenBookItem.makeSureTagIsValid($$1)) {
                return BookViewScreen.loadPages($$1);
            }
            return ImmutableList.of((Object)Component.Serializer.toJson(Component.translatable("book.invalid.tag").withStyle(ChatFormatting.DARK_RED)));
        }

        @Override
        public int getPageCount() {
            return this.pages.size();
        }

        @Override
        public FormattedText getPageRaw(int $$0) {
            String $$1 = (String)this.pages.get($$0);
            try {
                MutableComponent $$2 = Component.Serializer.fromJson($$1);
                if ($$2 != null) {
                    return $$2;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            return FormattedText.of($$1);
        }
    }
}
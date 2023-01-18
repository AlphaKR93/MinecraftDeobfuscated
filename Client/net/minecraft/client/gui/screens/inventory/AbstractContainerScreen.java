/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Iterator
 *  java.util.Set
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractContainerScreen<T extends AbstractContainerMenu>
extends Screen
implements MenuAccess<T> {
    public static final ResourceLocation INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/inventory.png");
    private static final float SNAPBACK_SPEED = 100.0f;
    private static final int QUICKDROP_DELAY = 500;
    public static final int SLOT_ITEM_BLIT_OFFSET = 100;
    private static final int HOVER_ITEM_BLIT_OFFSET = 200;
    protected int imageWidth = 176;
    protected int imageHeight = 166;
    protected int titleLabelX;
    protected int titleLabelY;
    protected int inventoryLabelX;
    protected int inventoryLabelY;
    protected final T menu;
    protected final Component playerInventoryTitle;
    @Nullable
    protected Slot hoveredSlot;
    @Nullable
    private Slot clickedSlot;
    @Nullable
    private Slot snapbackEnd;
    @Nullable
    private Slot quickdropSlot;
    @Nullable
    private Slot lastClickSlot;
    protected int leftPos;
    protected int topPos;
    private boolean isSplittingStack;
    private ItemStack draggingItem = ItemStack.EMPTY;
    private int snapbackStartX;
    private int snapbackStartY;
    private long snapbackTime;
    private ItemStack snapbackItem = ItemStack.EMPTY;
    private long quickdropTime;
    protected final Set<Slot> quickCraftSlots = Sets.newHashSet();
    protected boolean isQuickCrafting;
    private int quickCraftingType;
    private int quickCraftingButton;
    private boolean skipNextRelease;
    private int quickCraftingRemainder;
    private long lastClickTime;
    private int lastClickButton;
    private boolean doubleclick;
    private ItemStack lastQuickMoved = ItemStack.EMPTY;

    public AbstractContainerScreen(T $$0, Inventory $$1, Component $$2) {
        super($$2);
        this.menu = $$0;
        this.playerInventoryTitle = $$1.getDisplayName();
        this.skipNextRelease = true;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        ItemStack $$11;
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        this.renderBg($$0, $$3, $$1, $$2);
        RenderSystem.disableDepthTest();
        super.render($$0, $$1, $$2, $$3);
        PoseStack $$6 = RenderSystem.getModelViewStack();
        $$6.pushPose();
        $$6.translate($$4, $$5, 0.0f);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.hoveredSlot = null;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        for (int $$7 = 0; $$7 < ((AbstractContainerMenu)this.menu).slots.size(); ++$$7) {
            Slot $$8 = ((AbstractContainerMenu)this.menu).slots.get($$7);
            if ($$8.isActive()) {
                RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
                this.renderSlot($$0, $$8);
            }
            if (!this.isHovering($$8, $$1, $$2) || !$$8.isActive()) continue;
            this.hoveredSlot = $$8;
            int $$9 = $$8.x;
            int $$10 = $$8.y;
            AbstractContainerScreen.renderSlotHighlight($$0, $$9, $$10, this.getBlitOffset());
        }
        this.renderLabels($$0, $$1, $$2);
        ItemStack itemStack = $$11 = this.draggingItem.isEmpty() ? ((AbstractContainerMenu)this.menu).getCarried() : this.draggingItem;
        if (!$$11.isEmpty()) {
            int $$12 = 8;
            int $$13 = this.draggingItem.isEmpty() ? 8 : 16;
            String $$14 = null;
            if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
                $$11 = $$11.copy();
                $$11.setCount(Mth.ceil((float)$$11.getCount() / 2.0f));
            } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                $$11 = $$11.copy();
                $$11.setCount(this.quickCraftingRemainder);
                if ($$11.isEmpty()) {
                    $$14 = ChatFormatting.YELLOW + "0";
                }
            }
            this.renderFloatingItem($$11, $$1 - $$4 - 8, $$2 - $$5 - $$13, $$14);
        }
        if (!this.snapbackItem.isEmpty()) {
            float $$15 = (float)(Util.getMillis() - this.snapbackTime) / 100.0f;
            if ($$15 >= 1.0f) {
                $$15 = 1.0f;
                this.snapbackItem = ItemStack.EMPTY;
            }
            int $$16 = this.snapbackEnd.x - this.snapbackStartX;
            int $$17 = this.snapbackEnd.y - this.snapbackStartY;
            int $$18 = this.snapbackStartX + (int)((float)$$16 * $$15);
            int $$19 = this.snapbackStartY + (int)((float)$$17 * $$15);
            this.renderFloatingItem(this.snapbackItem, $$18, $$19, null);
        }
        $$6.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
    }

    public static void renderSlotHighlight(PoseStack $$0, int $$1, int $$2, int $$3) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        AbstractContainerScreen.fillGradient($$0, $$1, $$2, $$1 + 16, $$2 + 16, -2130706433, -2130706433, $$3);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    protected void renderTooltip(PoseStack $$0, int $$1, int $$2) {
        if (((AbstractContainerMenu)this.menu).getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            this.renderTooltip($$0, this.hoveredSlot.getItem(), $$1, $$2);
        }
    }

    private void renderFloatingItem(ItemStack $$0, int $$1, int $$2, String $$3) {
        PoseStack $$4 = RenderSystem.getModelViewStack();
        $$4.translate(0.0f, 0.0f, 32.0f);
        RenderSystem.applyModelViewMatrix();
        this.setBlitOffset(200);
        this.itemRenderer.blitOffset = 200.0f;
        this.itemRenderer.renderAndDecorateItem($$0, $$1, $$2);
        this.itemRenderer.renderGuiItemDecorations(this.font, $$0, $$1, $$2 - (this.draggingItem.isEmpty() ? 0 : 8), $$3);
        this.setBlitOffset(0);
        this.itemRenderer.blitOffset = 0.0f;
    }

    protected void renderLabels(PoseStack $$0, int $$1, int $$2) {
        this.font.draw($$0, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 0x404040);
        this.font.draw($$0, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 0x404040);
    }

    protected abstract void renderBg(PoseStack var1, float var2, int var3, int var4);

    private void renderSlot(PoseStack $$0, Slot $$1) {
        Pair<ResourceLocation, ResourceLocation> $$10;
        int $$2 = $$1.x;
        int $$3 = $$1.y;
        ItemStack $$4 = $$1.getItem();
        boolean $$5 = false;
        boolean $$6 = $$1 == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
        ItemStack $$7 = ((AbstractContainerMenu)this.menu).getCarried();
        String $$8 = null;
        if ($$1 == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !$$4.isEmpty()) {
            $$4 = $$4.copy();
            $$4.setCount($$4.getCount() / 2);
        } else if (this.isQuickCrafting && this.quickCraftSlots.contains((Object)$$1) && !$$7.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }
            if (AbstractContainerMenu.canItemQuickReplace($$1, $$7, true) && ((AbstractContainerMenu)this.menu).canDragTo($$1)) {
                $$4 = $$7.copy();
                $$5 = true;
                AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, $$4, $$1.getItem().isEmpty() ? 0 : $$1.getItem().getCount());
                int $$9 = Math.min((int)$$4.getMaxStackSize(), (int)$$1.getMaxStackSize($$4));
                if ($$4.getCount() > $$9) {
                    $$8 = ChatFormatting.YELLOW.toString() + $$9;
                    $$4.setCount($$9);
                }
            } else {
                this.quickCraftSlots.remove((Object)$$1);
                this.recalculateQuickCraftRemaining();
            }
        }
        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0f;
        if ($$4.isEmpty() && $$1.isActive() && ($$10 = $$1.getNoItemIcon()) != null) {
            TextureAtlasSprite $$11 = (TextureAtlasSprite)this.minecraft.getTextureAtlas((ResourceLocation)$$10.getFirst()).apply((Object)((ResourceLocation)$$10.getSecond()));
            RenderSystem.setShaderTexture(0, $$11.atlasLocation());
            AbstractContainerScreen.blit($$0, $$2, $$3, this.getBlitOffset(), 16, 16, $$11);
            $$6 = true;
        }
        if (!$$6) {
            if ($$5) {
                AbstractContainerScreen.fill($$0, $$2, $$3, $$2 + 16, $$3 + 16, -2130706433);
            }
            RenderSystem.enableDepthTest();
            this.itemRenderer.renderAndDecorateItem(this.minecraft.player, $$4, $$2, $$3, $$1.x + $$1.y * this.imageWidth);
            this.itemRenderer.renderGuiItemDecorations(this.font, $$4, $$2, $$3, $$8);
        }
        this.itemRenderer.blitOffset = 0.0f;
        this.setBlitOffset(0);
    }

    private void recalculateQuickCraftRemaining() {
        ItemStack $$0 = ((AbstractContainerMenu)this.menu).getCarried();
        if ($$0.isEmpty() || !this.isQuickCrafting) {
            return;
        }
        if (this.quickCraftingType == 2) {
            this.quickCraftingRemainder = $$0.getMaxStackSize();
            return;
        }
        this.quickCraftingRemainder = $$0.getCount();
        for (Slot $$1 : this.quickCraftSlots) {
            ItemStack $$2 = $$0.copy();
            ItemStack $$3 = $$1.getItem();
            int $$4 = $$3.isEmpty() ? 0 : $$3.getCount();
            AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, $$2, $$4);
            int $$5 = Math.min((int)$$2.getMaxStackSize(), (int)$$1.getMaxStackSize($$2));
            if ($$2.getCount() > $$5) {
                $$2.setCount($$5);
            }
            this.quickCraftingRemainder -= $$2.getCount() - $$4;
        }
    }

    @Nullable
    private Slot findSlot(double $$0, double $$1) {
        for (int $$2 = 0; $$2 < ((AbstractContainerMenu)this.menu).slots.size(); ++$$2) {
            Slot $$3 = ((AbstractContainerMenu)this.menu).slots.get($$2);
            if (!this.isHovering($$3, $$0, $$1) || !$$3.isActive()) continue;
            return $$3;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (super.mouseClicked($$0, $$1, $$2)) {
            return true;
        }
        boolean $$3 = this.minecraft.options.keyPickItem.matchesMouse($$2) && this.minecraft.gameMode.hasInfiniteItems();
        Slot $$4 = this.findSlot($$0, $$1);
        long $$5 = Util.getMillis();
        this.doubleclick = this.lastClickSlot == $$4 && $$5 - this.lastClickTime < 250L && this.lastClickButton == $$2;
        this.skipNextRelease = false;
        if ($$2 == 0 || $$2 == 1 || $$3) {
            int $$6 = this.leftPos;
            int $$7 = this.topPos;
            boolean $$8 = this.hasClickedOutside($$0, $$1, $$6, $$7, $$2);
            int $$9 = -1;
            if ($$4 != null) {
                $$9 = $$4.index;
            }
            if ($$8) {
                $$9 = -999;
            }
            if (this.minecraft.options.touchscreen().get().booleanValue() && $$8 && ((AbstractContainerMenu)this.menu).getCarried().isEmpty()) {
                this.onClose();
                return true;
            }
            if ($$9 != -1) {
                if (this.minecraft.options.touchscreen().get().booleanValue()) {
                    if ($$4 != null && $$4.hasItem()) {
                        this.clickedSlot = $$4;
                        this.draggingItem = ItemStack.EMPTY;
                        this.isSplittingStack = $$2 == 1;
                    } else {
                        this.clickedSlot = null;
                    }
                } else if (!this.isQuickCrafting) {
                    if (((AbstractContainerMenu)this.menu).getCarried().isEmpty()) {
                        if ($$3) {
                            this.slotClicked($$4, $$9, $$2, ClickType.CLONE);
                        } else {
                            boolean $$10 = $$9 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                            ClickType $$11 = ClickType.PICKUP;
                            if ($$10) {
                                this.lastQuickMoved = $$4 != null && $$4.hasItem() ? $$4.getItem().copy() : ItemStack.EMPTY;
                                $$11 = ClickType.QUICK_MOVE;
                            } else if ($$9 == -999) {
                                $$11 = ClickType.THROW;
                            }
                            this.slotClicked($$4, $$9, $$2, $$11);
                        }
                        this.skipNextRelease = true;
                    } else {
                        this.isQuickCrafting = true;
                        this.quickCraftingButton = $$2;
                        this.quickCraftSlots.clear();
                        if ($$2 == 0) {
                            this.quickCraftingType = 0;
                        } else if ($$2 == 1) {
                            this.quickCraftingType = 1;
                        } else if ($$3) {
                            this.quickCraftingType = 2;
                        }
                    }
                }
            }
        } else {
            this.checkHotbarMouseClicked($$2);
        }
        this.lastClickSlot = $$4;
        this.lastClickTime = $$5;
        this.lastClickButton = $$2;
        return true;
    }

    private void checkHotbarMouseClicked(int $$0) {
        if (this.hoveredSlot != null && ((AbstractContainerMenu)this.menu).getCarried().isEmpty()) {
            if (this.minecraft.options.keySwapOffhand.matchesMouse($$0)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
                return;
            }
            for (int $$1 = 0; $$1 < 9; ++$$1) {
                if (!this.minecraft.options.keyHotbarSlots[$$1].matchesMouse($$0)) continue;
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, $$1, ClickType.SWAP);
            }
        }
    }

    protected boolean hasClickedOutside(double $$0, double $$1, int $$2, int $$3, int $$4) {
        return $$0 < (double)$$2 || $$1 < (double)$$3 || $$0 >= (double)($$2 + this.imageWidth) || $$1 >= (double)($$3 + this.imageHeight);
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        Slot $$5 = this.findSlot($$0, $$1);
        ItemStack $$6 = ((AbstractContainerMenu)this.menu).getCarried();
        if (this.clickedSlot != null && this.minecraft.options.touchscreen().get().booleanValue()) {
            if ($$2 == 0 || $$2 == 1) {
                if (this.draggingItem.isEmpty()) {
                    if ($$5 != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
                        this.draggingItem = this.clickedSlot.getItem().copy();
                    }
                } else if (this.draggingItem.getCount() > 1 && $$5 != null && AbstractContainerMenu.canItemQuickReplace($$5, this.draggingItem, false)) {
                    long $$7 = Util.getMillis();
                    if (this.quickdropSlot == $$5) {
                        if ($$7 - this.quickdropTime > 500L) {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.slotClicked($$5, $$5.index, 1, ClickType.PICKUP);
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.quickdropTime = $$7 + 750L;
                            this.draggingItem.shrink(1);
                        }
                    } else {
                        this.quickdropSlot = $$5;
                        this.quickdropTime = $$7;
                    }
                }
            }
        } else if (this.isQuickCrafting && $$5 != null && !$$6.isEmpty() && ($$6.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && AbstractContainerMenu.canItemQuickReplace($$5, $$6, true) && $$5.mayPlace($$6) && ((AbstractContainerMenu)this.menu).canDragTo($$5)) {
            this.quickCraftSlots.add((Object)$$5);
            this.recalculateQuickCraftRemaining();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        Slot $$3 = this.findSlot($$0, $$1);
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        boolean $$6 = this.hasClickedOutside($$0, $$1, $$4, $$5, $$2);
        int $$7 = -1;
        if ($$3 != null) {
            $$7 = $$3.index;
        }
        if ($$6) {
            $$7 = -999;
        }
        if (this.doubleclick && $$3 != null && $$2 == 0 && ((AbstractContainerMenu)this.menu).canTakeItemForPickAll(ItemStack.EMPTY, $$3)) {
            if (AbstractContainerScreen.hasShiftDown()) {
                if (!this.lastQuickMoved.isEmpty()) {
                    Iterator iterator = ((AbstractContainerMenu)this.menu).slots.iterator();
                    while (iterator.hasNext()) {
                        Slot $$8 = (Slot)iterator.next();
                        if ($$8 == null || !$$8.mayPickup(this.minecraft.player) || !$$8.hasItem() || $$8.container != $$3.container || !AbstractContainerMenu.canItemQuickReplace($$8, this.lastQuickMoved, true)) continue;
                        this.slotClicked($$8, $$8.index, $$2, ClickType.QUICK_MOVE);
                    }
                }
            } else {
                this.slotClicked($$3, $$7, $$2, ClickType.PICKUP_ALL);
            }
            this.doubleclick = false;
            this.lastClickTime = 0L;
        } else {
            if (this.isQuickCrafting && this.quickCraftingButton != $$2) {
                this.isQuickCrafting = false;
                this.quickCraftSlots.clear();
                this.skipNextRelease = true;
                return true;
            }
            if (this.skipNextRelease) {
                this.skipNextRelease = false;
                return true;
            }
            if (this.clickedSlot != null && this.minecraft.options.touchscreen().get().booleanValue()) {
                if ($$2 == 0 || $$2 == 1) {
                    if (this.draggingItem.isEmpty() && $$3 != this.clickedSlot) {
                        this.draggingItem = this.clickedSlot.getItem();
                    }
                    boolean $$9 = AbstractContainerMenu.canItemQuickReplace($$3, this.draggingItem, false);
                    if ($$7 != -1 && !this.draggingItem.isEmpty() && $$9) {
                        this.slotClicked(this.clickedSlot, this.clickedSlot.index, $$2, ClickType.PICKUP);
                        this.slotClicked($$3, $$7, 0, ClickType.PICKUP);
                        if (((AbstractContainerMenu)this.menu).getCarried().isEmpty()) {
                            this.snapbackItem = ItemStack.EMPTY;
                        } else {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, $$2, ClickType.PICKUP);
                            this.snapbackStartX = Mth.floor($$0 - (double)$$4);
                            this.snapbackStartY = Mth.floor($$1 - (double)$$5);
                            this.snapbackEnd = this.clickedSlot;
                            this.snapbackItem = this.draggingItem;
                            this.snapbackTime = Util.getMillis();
                        }
                    } else if (!this.draggingItem.isEmpty()) {
                        this.snapbackStartX = Mth.floor($$0 - (double)$$4);
                        this.snapbackStartY = Mth.floor($$1 - (double)$$5);
                        this.snapbackEnd = this.clickedSlot;
                        this.snapbackItem = this.draggingItem;
                        this.snapbackTime = Util.getMillis();
                    }
                    this.clearDraggingState();
                }
            } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);
                for (Slot $$10 : this.quickCraftSlots) {
                    this.slotClicked($$10, $$10.index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ClickType.QUICK_CRAFT);
                }
                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
            } else if (!((AbstractContainerMenu)this.menu).getCarried().isEmpty()) {
                if (this.minecraft.options.keyPickItem.matchesMouse($$2)) {
                    this.slotClicked($$3, $$7, $$2, ClickType.CLONE);
                } else {
                    boolean $$11;
                    boolean bl = $$11 = $$7 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                    if ($$11) {
                        this.lastQuickMoved = $$3 != null && $$3.hasItem() ? $$3.getItem().copy() : ItemStack.EMPTY;
                    }
                    this.slotClicked($$3, $$7, $$2, $$11 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                }
            }
        }
        if (((AbstractContainerMenu)this.menu).getCarried().isEmpty()) {
            this.lastClickTime = 0L;
        }
        this.isQuickCrafting = false;
        return true;
    }

    public void clearDraggingState() {
        this.draggingItem = ItemStack.EMPTY;
        this.clickedSlot = null;
    }

    private boolean isHovering(Slot $$0, double $$1, double $$2) {
        return this.isHovering($$0.x, $$0.y, 16, 16, $$1, $$2);
    }

    protected boolean isHovering(int $$0, int $$1, int $$2, int $$3, double $$4, double $$5) {
        int $$6 = this.leftPos;
        int $$7 = this.topPos;
        return ($$4 -= (double)$$6) >= (double)($$0 - 1) && $$4 < (double)($$0 + $$2 + 1) && ($$5 -= (double)$$7) >= (double)($$1 - 1) && $$5 < (double)($$1 + $$3 + 1);
    }

    protected void slotClicked(Slot $$0, int $$1, int $$2, ClickType $$3) {
        if ($$0 != null) {
            $$1 = $$0.index;
        }
        this.minecraft.gameMode.handleInventoryMouseClick(((AbstractContainerMenu)this.menu).containerId, $$1, $$2, $$3, this.minecraft.player);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        if (this.minecraft.options.keyInventory.matches($$0, $$1)) {
            this.onClose();
            return true;
        }
        this.checkHotbarKeyPressed($$0, $$1);
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.minecraft.options.keyPickItem.matches($$0, $$1)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 0, ClickType.CLONE);
            } else if (this.minecraft.options.keyDrop.matches($$0, $$1)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, AbstractContainerScreen.hasControlDown() ? 1 : 0, ClickType.THROW);
            }
        }
        return true;
    }

    protected boolean checkHotbarKeyPressed(int $$0, int $$1) {
        if (((AbstractContainerMenu)this.menu).getCarried().isEmpty() && this.hoveredSlot != null) {
            if (this.minecraft.options.keySwapOffhand.matches($$0, $$1)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
                return true;
            }
            for (int $$2 = 0; $$2 < 9; ++$$2) {
                if (!this.minecraft.options.keyHotbarSlots[$$2].matches($$0, $$1)) continue;
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, $$2, ClickType.SWAP);
                return true;
            }
        }
        return false;
    }

    @Override
    public void removed() {
        if (this.minecraft.player == null) {
            return;
        }
        ((AbstractContainerMenu)this.menu).removed(this.minecraft.player);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public final void tick() {
        super.tick();
        if (!this.minecraft.player.isAlive() || this.minecraft.player.isRemoved()) {
            this.minecraft.player.closeContainer();
        } else {
            this.containerTick();
        }
    }

    protected void containerTick() {
    }

    @Override
    public T getMenu() {
        return this.menu;
    }

    @Override
    public void onClose() {
        this.minecraft.player.closeContainer();
        super.onClose();
    }
}
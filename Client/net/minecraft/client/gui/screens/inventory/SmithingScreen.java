/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 *  javax.annotation.Nullable
 *  org.joml.Quaternionf
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import org.joml.Quaternionf;

public class SmithingScreen
extends ItemCombinerScreen<SmithingMenu> {
    private static final ResourceLocation SMITHING_LOCATION = new ResourceLocation("textures/gui/container/smithing.png");
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM = new ResourceLocation("item/empty_slot_smithing_template_armor_trim");
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE = new ResourceLocation("item/empty_slot_smithing_template_netherite_upgrade");
    private static final Component MISSING_TEMPLATE_TOOLTIP = Component.translatable("container.upgrade.missing_template_tooltip");
    private static final Component ERROR_TOOLTIP = Component.translatable("container.upgrade.error_tooltip");
    private static final List<ResourceLocation> EMPTY_SLOT_SMITHING_TEMPLATES = List.of((Object)EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM, (Object)EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE);
    private static final int TITLE_LABEL_X = 44;
    private static final int TITLE_LABEL_Y = 22;
    private static final int ERROR_ICON_WIDTH = 28;
    private static final int ERROR_ICON_HEIGHT = 21;
    private static final int ERROR_ICON_X = 95;
    private static final int ERROR_ICON_Y = 45;
    private static final int TOOLTIP_WIDTH = 115;
    public static final int ARMOR_STAND_Y_ROT = 210;
    public static final int ARMOR_STAND_X_ROT = 25;
    public static final Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ(0.43633232f, 0.0f, (float)Math.PI);
    public static final int ARMOR_STAND_SCALE = 25;
    public static final int ARMOR_STAND_OFFSET_Y = 65;
    public static final int ARMOR_STAND_OFFSET_X = 141;
    private final CyclingSlotBackground templateIcon = new CyclingSlotBackground(0);
    private final CyclingSlotBackground baseIcon = new CyclingSlotBackground(1);
    private final CyclingSlotBackground additionalIcon = new CyclingSlotBackground(2);
    @Nullable
    private ArmorStand armorStandPreview;

    public SmithingScreen(SmithingMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2, SMITHING_LOCATION);
        this.titleLabelX = 44;
        this.titleLabelY = 22;
    }

    @Override
    protected void subInit() {
        this.armorStandPreview = new ArmorStand(this.minecraft.level, 0.0, 0.0, 0.0);
        this.armorStandPreview.setNoBasePlate(true);
        this.armorStandPreview.setShowArms(true);
        this.armorStandPreview.yBodyRot = 210.0f;
        this.armorStandPreview.setXRot(25.0f);
        this.armorStandPreview.yHeadRot = this.armorStandPreview.getYRot();
        this.armorStandPreview.yHeadRotO = this.armorStandPreview.getYRot();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        Optional<SmithingTemplateItem> $$0 = this.getTemplateItem();
        this.templateIcon.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
        this.baseIcon.tick((List<ResourceLocation>)((List)$$0.map(SmithingTemplateItem::getBaseSlotEmptyIcons).orElse((Object)List.of())));
        this.additionalIcon.tick((List<ResourceLocation>)((List)$$0.map(SmithingTemplateItem::getAdditionalSlotEmptyIcons).orElse((Object)List.of())));
    }

    private Optional<SmithingTemplateItem> getTemplateItem() {
        Item item;
        ItemStack $$0 = ((SmithingMenu)this.menu).getSlot(0).getItem();
        if (!$$0.isEmpty() && (item = $$0.getItem()) instanceof SmithingTemplateItem) {
            SmithingTemplateItem $$1 = (SmithingTemplateItem)item;
            return Optional.of((Object)$$1);
        }
        return Optional.empty();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.renderOnboardingTooltips($$0, $$1, $$2);
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        super.renderBg($$0, $$1, $$2, $$3);
        this.templateIcon.render(this.menu, $$0, $$1, this.leftPos, this.topPos);
        this.baseIcon.render(this.menu, $$0, $$1, this.leftPos, this.topPos);
        this.additionalIcon.render(this.menu, $$0, $$1, this.leftPos, this.topPos);
        InventoryScreen.renderEntityInInventory(this.leftPos + 141, this.topPos + 65, 25, ARMOR_STAND_ANGLE, null, this.armorStandPreview);
    }

    @Override
    public void slotChanged(AbstractContainerMenu $$0, int $$1, ItemStack $$2) {
        if ($$1 == 3 && this.armorStandPreview != null) {
            for (EquipmentSlot $$3 : EquipmentSlot.values()) {
                this.armorStandPreview.setItemSlot($$3, ItemStack.EMPTY);
            }
            if (!$$2.isEmpty()) {
                ItemStack $$4 = $$2.copy();
                Item item = $$2.getItem();
                if (item instanceof ArmorItem) {
                    ArmorItem $$5 = (ArmorItem)item;
                    this.armorStandPreview.setItemSlot($$5.getSlot(), $$4);
                } else {
                    this.armorStandPreview.setItemSlot(EquipmentSlot.OFFHAND, $$4);
                }
            }
        }
    }

    @Override
    protected void renderErrorIcon(PoseStack $$0, int $$1, int $$2) {
        if (this.hasRecipeError()) {
            this.blit($$0, $$1 + 95, $$2 + 45, this.imageWidth, 0, 28, 21);
        }
    }

    private void renderOnboardingTooltips(PoseStack $$0, int $$1, int $$2) {
        Optional $$32 = Optional.empty();
        if (this.hasRecipeError() && this.isHovering(95, 45, 28, 21, $$1, $$2)) {
            $$32 = Optional.of((Object)ERROR_TOOLTIP);
        }
        if (this.hoveredSlot != null) {
            ItemStack $$4 = ((SmithingMenu)this.menu).getSlot(0).getItem();
            ItemStack $$5 = this.hoveredSlot.getItem();
            if ($$4.isEmpty()) {
                if (this.hoveredSlot.index == 0) {
                    $$32 = Optional.of((Object)MISSING_TEMPLATE_TOOLTIP);
                }
            } else {
                Item item = $$4.getItem();
                if (item instanceof SmithingTemplateItem) {
                    SmithingTemplateItem $$6 = (SmithingTemplateItem)item;
                    if ($$5.isEmpty()) {
                        if (this.hoveredSlot.index == 1) {
                            $$32 = Optional.of((Object)$$6.getBaseSlotDescription());
                        } else if (this.hoveredSlot.index == 2) {
                            $$32 = Optional.of((Object)$$6.getAdditionSlotDescription());
                        }
                    }
                }
            }
        }
        $$32.ifPresent($$3 -> this.renderTooltip($$0, this.font.split((FormattedText)$$3, 115), $$1, $$2));
    }

    private boolean hasRecipeError() {
        return ((SmithingMenu)this.menu).getSlot(0).hasItem() && ((SmithingMenu)this.menu).getSlot(1).hasItem() && ((SmithingMenu)this.menu).getSlot(2).hasItem() && !((SmithingMenu)this.menu).getSlot(((SmithingMenu)this.menu).getResultSlot()).hasItem();
    }
}
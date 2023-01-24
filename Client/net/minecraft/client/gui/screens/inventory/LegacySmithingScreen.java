/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.LegacySmithingMenu;

@Deprecated(forRemoval=true)
public class LegacySmithingScreen
extends ItemCombinerScreen<LegacySmithingMenu> {
    private static final ResourceLocation SMITHING_LOCATION = new ResourceLocation("textures/gui/container/legacy_smithing.png");

    public LegacySmithingScreen(LegacySmithingMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2, SMITHING_LOCATION);
        this.titleLabelX = 60;
        this.titleLabelY = 18;
    }

    @Override
    protected void renderLabels(PoseStack $$0, int $$1, int $$2) {
        RenderSystem.disableBlend();
        super.renderLabels($$0, $$1, $$2);
    }

    @Override
    protected void renderErrorIcon(PoseStack $$0, int $$1, int $$2) {
        if ((((LegacySmithingMenu)this.menu).getSlot(0).hasItem() || ((LegacySmithingMenu)this.menu).getSlot(1).hasItem()) && !((LegacySmithingMenu)this.menu).getSlot(((LegacySmithingMenu)this.menu).getResultSlot()).hasItem()) {
            this.blit($$0, $$1 + 99, $$2 + 45, this.imageWidth, 0, 28, 21);
        }
    }
}
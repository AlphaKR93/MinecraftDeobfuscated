/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.List
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class CyclingSlotBackground {
    private static final int ICON_CHANGE_TICK_RATE = 30;
    private static final int ICON_SIZE = 16;
    private static final int ICON_TRANSITION_TICK_DURATION = 4;
    private final int slotIndex;
    private List<ResourceLocation> icons = List.of();
    private int tick;
    private int iconIndex;

    public CyclingSlotBackground(int $$0) {
        this.slotIndex = $$0;
    }

    public void tick(List<ResourceLocation> $$0) {
        if (!this.icons.equals($$0)) {
            this.icons = $$0;
            this.iconIndex = 0;
        }
        if (!this.icons.isEmpty() && ++this.tick % 30 == 0) {
            this.iconIndex = (this.iconIndex + 1) % this.icons.size();
        }
    }

    public void render(AbstractContainerMenu $$0, PoseStack $$1, float $$2, int $$3, int $$4) {
        float $$7;
        Slot $$5 = $$0.getSlot(this.slotIndex);
        if (this.icons.isEmpty() || $$5.hasItem()) {
            return;
        }
        boolean $$6 = this.icons.size() > 1 && this.tick >= 30;
        float f = $$7 = $$6 ? this.getIconTransitionTransparency($$2) : 1.0f;
        if ($$7 < 1.0f) {
            int $$8 = Math.floorMod((int)(this.iconIndex - 1), (int)this.icons.size());
            this.renderIcon($$5, (ResourceLocation)this.icons.get($$8), 1.0f - $$7, $$1, $$3, $$4);
        }
        this.renderIcon($$5, (ResourceLocation)this.icons.get(this.iconIndex), $$7, $$1, $$3, $$4);
    }

    private void renderIcon(Slot $$0, ResourceLocation $$1, float $$2, PoseStack $$3, int $$4, int $$5) {
        TextureAtlasSprite $$6 = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply((Object)$$1);
        RenderSystem.setShaderTexture(0, $$6.atlasLocation());
        GuiComponent.blit($$3, $$4 + $$0.x, $$5 + $$0.y, 0, 16, 16, $$6, 1.0f, 1.0f, 1.0f, $$2);
    }

    private float getIconTransitionTransparency(float $$0) {
        float $$1 = (float)(this.tick % 30) + $$0;
        return Math.min((float)$$1, (float)4.0f) / 4.0f;
    }
}
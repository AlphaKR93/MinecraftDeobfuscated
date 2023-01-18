/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;

public class HorseInventoryScreen
extends AbstractContainerScreen<HorseInventoryMenu> {
    private static final ResourceLocation HORSE_INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/horse.png");
    private final AbstractHorse horse;
    private float xMouse;
    private float yMouse;

    public HorseInventoryScreen(HorseInventoryMenu $$0, Inventory $$1, AbstractHorse $$2) {
        super($$0, $$1, $$2.getDisplayName());
        this.horse = $$2;
        this.passEvents = false;
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        AbstractChestedHorse $$6;
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, HORSE_INVENTORY_LOCATION);
        int $$4 = (this.width - this.imageWidth) / 2;
        int $$5 = (this.height - this.imageHeight) / 2;
        this.blit($$0, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        if (this.horse instanceof AbstractChestedHorse && ($$6 = (AbstractChestedHorse)this.horse).hasChest()) {
            this.blit($$0, $$4 + 79, $$5 + 17, 0, this.imageHeight, $$6.getInventoryColumns() * 18, 54);
        }
        if (this.horse.isSaddleable()) {
            this.blit($$0, $$4 + 7, $$5 + 35 - 18, 18, this.imageHeight + 54, 18, 18);
        }
        if (this.horse.canWearArmor()) {
            if (this.horse instanceof Llama) {
                this.blit($$0, $$4 + 7, $$5 + 35, 36, this.imageHeight + 54, 18, 18);
            } else {
                this.blit($$0, $$4 + 7, $$5 + 35, 0, this.imageHeight + 54, 18, 18);
            }
        }
        InventoryScreen.renderEntityInInventory($$4 + 51, $$5 + 60, 17, (float)($$4 + 51) - this.xMouse, (float)($$5 + 75 - 50) - this.yMouse, this.horse);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.xMouse = $$1;
        this.yMouse = $$2;
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }
}
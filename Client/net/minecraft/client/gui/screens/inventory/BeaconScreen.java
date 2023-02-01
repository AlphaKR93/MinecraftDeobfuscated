/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public class BeaconScreen
extends AbstractContainerScreen<BeaconMenu> {
    static final ResourceLocation BEACON_LOCATION = new ResourceLocation("textures/gui/container/beacon.png");
    private static final Component PRIMARY_EFFECT_LABEL = Component.translatable("block.minecraft.beacon.primary");
    private static final Component SECONDARY_EFFECT_LABEL = Component.translatable("block.minecraft.beacon.secondary");
    private final List<BeaconButton> beaconButtons = Lists.newArrayList();
    @Nullable
    MobEffect primary;
    @Nullable
    MobEffect secondary;

    public BeaconScreen(final BeaconMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        this.imageWidth = 230;
        this.imageHeight = 219;
        $$0.addSlotListener(new ContainerListener(){

            @Override
            public void slotChanged(AbstractContainerMenu $$02, int $$1, ItemStack $$2) {
            }

            @Override
            public void dataChanged(AbstractContainerMenu $$02, int $$1, int $$2) {
                BeaconScreen.this.primary = $$0.getPrimaryEffect();
                BeaconScreen.this.secondary = $$0.getSecondaryEffect();
            }
        });
    }

    private <T extends AbstractWidget> void addBeaconButton(T $$0) {
        this.addRenderableWidget($$0);
        this.beaconButtons.add((Object)((BeaconButton)((Object)$$0)));
    }

    @Override
    protected void init() {
        super.init();
        this.beaconButtons.clear();
        this.addBeaconButton(new BeaconConfirmButton(this.leftPos + 164, this.topPos + 107));
        this.addBeaconButton(new BeaconCancelButton(this.leftPos + 190, this.topPos + 107));
        for (int $$0 = 0; $$0 <= 2; ++$$0) {
            int $$1 = BeaconBlockEntity.BEACON_EFFECTS[$$0].length;
            int $$2 = $$1 * 22 + ($$1 - 1) * 2;
            for (int $$3 = 0; $$3 < $$1; ++$$3) {
                MobEffect $$4 = BeaconBlockEntity.BEACON_EFFECTS[$$0][$$3];
                BeaconPowerButton $$5 = new BeaconPowerButton(this.leftPos + 76 + $$3 * 24 - $$2 / 2, this.topPos + 22 + $$0 * 25, $$4, true, $$0);
                $$5.active = false;
                this.addBeaconButton($$5);
            }
        }
        int $$6 = 3;
        int $$7 = BeaconBlockEntity.BEACON_EFFECTS[3].length + 1;
        int $$8 = $$7 * 22 + ($$7 - 1) * 2;
        for (int $$9 = 0; $$9 < $$7 - 1; ++$$9) {
            MobEffect $$10 = BeaconBlockEntity.BEACON_EFFECTS[3][$$9];
            BeaconPowerButton $$11 = new BeaconPowerButton(this.leftPos + 167 + $$9 * 24 - $$8 / 2, this.topPos + 47, $$10, false, 3);
            $$11.active = false;
            this.addBeaconButton($$11);
        }
        BeaconUpgradePowerButton $$12 = new BeaconUpgradePowerButton(this.leftPos + 167 + ($$7 - 1) * 24 - $$8 / 2, this.topPos + 47, BeaconBlockEntity.BEACON_EFFECTS[0][0]);
        $$12.visible = false;
        this.addBeaconButton($$12);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.updateButtons();
    }

    void updateButtons() {
        int $$0 = ((BeaconMenu)this.menu).getLevels();
        this.beaconButtons.forEach($$1 -> $$1.updateStatus($$0));
    }

    @Override
    protected void renderLabels(PoseStack $$0, int $$1, int $$2) {
        BeaconScreen.drawCenteredString($$0, this.font, PRIMARY_EFFECT_LABEL, 62, 10, 0xE0E0E0);
        BeaconScreen.drawCenteredString($$0, this.font, SECONDARY_EFFECT_LABEL, 169, 10, 0xE0E0E0);
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, BEACON_LOCATION);
        int $$4 = (this.width - this.imageWidth) / 2;
        int $$5 = (this.height - this.imageHeight) / 2;
        this.blit($$0, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        this.itemRenderer.blitOffset = 100.0f;
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.NETHERITE_INGOT), $$4 + 20, $$5 + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.EMERALD), $$4 + 41, $$5 + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.DIAMOND), $$4 + 41 + 22, $$5 + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.GOLD_INGOT), $$4 + 42 + 44, $$5 + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.IRON_INGOT), $$4 + 42 + 66, $$5 + 109);
        this.itemRenderer.blitOffset = 0.0f;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    static interface BeaconButton {
        public void updateStatus(int var1);
    }

    class BeaconConfirmButton
    extends BeaconSpriteScreenButton {
        public BeaconConfirmButton(int $$0, int $$1) {
            super($$0, $$1, 90, 220, CommonComponents.GUI_DONE);
        }

        @Override
        public void onPress() {
            BeaconScreen.this.minecraft.getConnection().send(new ServerboundSetBeaconPacket((Optional<MobEffect>)Optional.ofNullable((Object)BeaconScreen.this.primary), (Optional<MobEffect>)Optional.ofNullable((Object)BeaconScreen.this.secondary)));
            ((BeaconScreen)BeaconScreen.this).minecraft.player.closeContainer();
        }

        @Override
        public void updateStatus(int $$0) {
            this.active = ((BeaconMenu)BeaconScreen.this.menu).hasPayment() && BeaconScreen.this.primary != null;
        }
    }

    class BeaconCancelButton
    extends BeaconSpriteScreenButton {
        public BeaconCancelButton(int $$0, int $$1) {
            super($$0, $$1, 112, 220, CommonComponents.GUI_CANCEL);
        }

        @Override
        public void onPress() {
            ((BeaconScreen)BeaconScreen.this).minecraft.player.closeContainer();
        }

        @Override
        public void updateStatus(int $$0) {
        }
    }

    class BeaconPowerButton
    extends BeaconScreenButton {
        private final boolean isPrimary;
        protected final int tier;
        private MobEffect effect;
        private TextureAtlasSprite sprite;

        public BeaconPowerButton(int $$0, int $$1, MobEffect $$2, boolean $$3, int $$4) {
            super($$0, $$1);
            this.isPrimary = $$3;
            this.tier = $$4;
            this.setEffect($$2);
        }

        protected void setEffect(MobEffect $$0) {
            this.effect = $$0;
            this.sprite = Minecraft.getInstance().getMobEffectTextures().get($$0);
            this.setTooltip(Tooltip.create(this.createEffectDescription($$0), null));
        }

        protected MutableComponent createEffectDescription(MobEffect $$0) {
            return Component.translatable($$0.getDescriptionId());
        }

        @Override
        public void onPress() {
            if (this.isSelected()) {
                return;
            }
            if (this.isPrimary) {
                BeaconScreen.this.primary = this.effect;
            } else {
                BeaconScreen.this.secondary = this.effect;
            }
            BeaconScreen.this.updateButtons();
        }

        @Override
        protected void renderIcon(PoseStack $$0) {
            RenderSystem.setShaderTexture(0, this.sprite.atlasLocation());
            BeaconPowerButton.blit($$0, this.getX() + 2, this.getY() + 2, this.getBlitOffset(), 18, 18, this.sprite);
        }

        @Override
        public void updateStatus(int $$0) {
            this.active = this.tier < $$0;
            this.setSelected(this.effect == (this.isPrimary ? BeaconScreen.this.primary : BeaconScreen.this.secondary));
        }

        @Override
        protected MutableComponent createNarrationMessage() {
            return this.createEffectDescription(this.effect);
        }
    }

    class BeaconUpgradePowerButton
    extends BeaconPowerButton {
        public BeaconUpgradePowerButton(int $$0, int $$1, MobEffect $$2) {
            super($$0, $$1, $$2, false, 3);
        }

        @Override
        protected MutableComponent createEffectDescription(MobEffect $$0) {
            return Component.translatable($$0.getDescriptionId()).append(" II");
        }

        @Override
        public void updateStatus(int $$0) {
            if (BeaconScreen.this.primary != null) {
                this.visible = true;
                this.setEffect(BeaconScreen.this.primary);
                super.updateStatus($$0);
            } else {
                this.visible = false;
            }
        }
    }

    static abstract class BeaconSpriteScreenButton
    extends BeaconScreenButton {
        private final int iconX;
        private final int iconY;

        protected BeaconSpriteScreenButton(int $$0, int $$1, int $$2, int $$3, Component $$4) {
            super($$0, $$1, $$4);
            this.iconX = $$2;
            this.iconY = $$3;
        }

        @Override
        protected void renderIcon(PoseStack $$0) {
            this.blit($$0, this.getX() + 2, this.getY() + 2, this.iconX, this.iconY, 18, 18);
        }
    }

    static abstract class BeaconScreenButton
    extends AbstractButton
    implements BeaconButton {
        private boolean selected;

        protected BeaconScreenButton(int $$0, int $$1) {
            super($$0, $$1, 22, 22, CommonComponents.EMPTY);
        }

        protected BeaconScreenButton(int $$0, int $$1, Component $$2) {
            super($$0, $$1, 22, 22, $$2);
        }

        @Override
        public void renderWidget(PoseStack $$0, int $$1, int $$2, float $$3) {
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
            RenderSystem.setShaderTexture(0, BEACON_LOCATION);
            int $$4 = 219;
            int $$5 = 0;
            if (!this.active) {
                $$5 += this.width * 2;
            } else if (this.selected) {
                $$5 += this.width * 1;
            } else if (this.isHoveredOrFocused()) {
                $$5 += this.width * 3;
            }
            this.blit($$0, this.getX(), this.getY(), $$5, 219, this.width, this.height);
            this.renderIcon($$0);
        }

        protected abstract void renderIcon(PoseStack var1);

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean $$0) {
            this.selected = $$0;
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput $$0) {
            this.defaultButtonNarrationText($$0);
        }
    }
}
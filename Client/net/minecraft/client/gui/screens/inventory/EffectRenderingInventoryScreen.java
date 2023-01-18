/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Ordering
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public abstract class EffectRenderingInventoryScreen<T extends AbstractContainerMenu>
extends AbstractContainerScreen<T> {
    public EffectRenderingInventoryScreen(T $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.renderEffects($$0, $$1, $$2);
    }

    public boolean canSeeEffects() {
        int $$0 = this.leftPos + this.imageWidth + 2;
        int $$1 = this.width - $$0;
        return $$1 >= 32;
    }

    private void renderEffects(PoseStack $$0, int $$1, int $$2) {
        int $$3 = this.leftPos + this.imageWidth + 2;
        int $$4 = this.width - $$3;
        Collection<MobEffectInstance> $$5 = this.minecraft.player.getActiveEffects();
        if ($$5.isEmpty() || $$4 < 32) {
            return;
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        boolean $$6 = $$4 >= 120;
        int $$7 = 33;
        if ($$5.size() > 5) {
            $$7 = 132 / ($$5.size() - 1);
        }
        List $$8 = Ordering.natural().sortedCopy($$5);
        this.renderBackgrounds($$0, $$3, $$7, (Iterable<MobEffectInstance>)$$8, $$6);
        this.renderIcons($$0, $$3, $$7, (Iterable<MobEffectInstance>)$$8, $$6);
        if ($$6) {
            this.renderLabels($$0, $$3, $$7, (Iterable<MobEffectInstance>)$$8);
        } else if ($$1 >= $$3 && $$1 <= $$3 + 33) {
            int $$9 = this.topPos;
            MobEffectInstance $$10 = null;
            for (MobEffectInstance $$11 : $$8) {
                if ($$2 >= $$9 && $$2 <= $$9 + $$7) {
                    $$10 = $$11;
                }
                $$9 += $$7;
            }
            if ($$10 != null) {
                List $$12 = List.of((Object)this.getEffectName($$10), (Object)Component.literal(MobEffectUtil.formatDuration($$10, 1.0f)));
                this.renderTooltip($$0, (List<Component>)$$12, (Optional<TooltipComponent>)Optional.empty(), $$1, $$2);
            }
        }
    }

    private void renderBackgrounds(PoseStack $$0, int $$1, int $$2, Iterable<MobEffectInstance> $$3, boolean $$4) {
        RenderSystem.setShaderTexture(0, INVENTORY_LOCATION);
        int $$5 = this.topPos;
        for (MobEffectInstance $$6 : $$3) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            if ($$4) {
                this.blit($$0, $$1, $$5, 0, 166, 120, 32);
            } else {
                this.blit($$0, $$1, $$5, 0, 198, 32, 32);
            }
            $$5 += $$2;
        }
    }

    private void renderIcons(PoseStack $$0, int $$1, int $$2, Iterable<MobEffectInstance> $$3, boolean $$4) {
        MobEffectTextureManager $$5 = this.minecraft.getMobEffectTextures();
        int $$6 = this.topPos;
        for (MobEffectInstance $$7 : $$3) {
            MobEffect $$8 = $$7.getEffect();
            TextureAtlasSprite $$9 = $$5.get($$8);
            RenderSystem.setShaderTexture(0, $$9.atlasLocation());
            EffectRenderingInventoryScreen.blit($$0, $$1 + ($$4 ? 6 : 7), $$6 + 7, this.getBlitOffset(), 18, 18, $$9);
            $$6 += $$2;
        }
    }

    private void renderLabels(PoseStack $$0, int $$1, int $$2, Iterable<MobEffectInstance> $$3) {
        int $$4 = this.topPos;
        for (MobEffectInstance $$5 : $$3) {
            Component $$6 = this.getEffectName($$5);
            this.font.drawShadow($$0, $$6, (float)($$1 + 10 + 18), (float)($$4 + 6), 0xFFFFFF);
            String $$7 = MobEffectUtil.formatDuration($$5, 1.0f);
            this.font.drawShadow($$0, $$7, (float)($$1 + 10 + 18), (float)($$4 + 6 + 10), 0x7F7F7F);
            $$4 += $$2;
        }
    }

    private Component getEffectName(MobEffectInstance $$0) {
        MutableComponent $$1 = $$0.getEffect().getDisplayName().copy();
        if ($$0.getAmplifier() >= 1 && $$0.getAmplifier() <= 9) {
            $$1.append(" ").append(Component.translatable("enchantment.level." + ($$0.getAmplifier() + 1)));
        }
        return $$1;
    }
}
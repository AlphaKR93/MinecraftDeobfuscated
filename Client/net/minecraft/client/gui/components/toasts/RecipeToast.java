/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeToast
implements Toast {
    private static final long DISPLAY_TIME = 5000L;
    private static final Component TITLE_TEXT = Component.translatable("recipe.toast.title");
    private static final Component DESCRIPTION_TEXT = Component.translatable("recipe.toast.description");
    private final List<Recipe<?>> recipes = Lists.newArrayList();
    private long lastChanged;
    private boolean changed;

    public RecipeToast(Recipe<?> $$0) {
        this.recipes.add($$0);
    }

    @Override
    public Toast.Visibility render(PoseStack $$0, ToastComponent $$1, long $$2) {
        if (this.changed) {
            this.lastChanged = $$2;
            this.changed = false;
        }
        if (this.recipes.isEmpty()) {
            return Toast.Visibility.HIDE;
        }
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, TEXTURE);
        $$1.blit($$0, 0, 0, 0, 32, this.width(), this.height());
        $$1.getMinecraft().font.draw($$0, TITLE_TEXT, 30.0f, 7.0f, -11534256);
        $$1.getMinecraft().font.draw($$0, DESCRIPTION_TEXT, 30.0f, 18.0f, -16777216);
        Recipe $$3 = (Recipe)this.recipes.get((int)((double)$$2 / Math.max((double)1.0, (double)(5000.0 * $$1.getNotificationDisplayTimeMultiplier() / (double)this.recipes.size())) % (double)this.recipes.size()));
        ItemStack $$4 = $$3.getToastSymbol();
        PoseStack $$5 = RenderSystem.getModelViewStack();
        $$5.pushPose();
        $$5.scale(0.6f, 0.6f, 1.0f);
        RenderSystem.applyModelViewMatrix();
        $$1.getMinecraft().getItemRenderer().renderAndDecorateFakeItem($$4, 3, 3);
        $$5.popPose();
        RenderSystem.applyModelViewMatrix();
        $$1.getMinecraft().getItemRenderer().renderAndDecorateFakeItem($$3.getResultItem(), 8, 8);
        return (double)($$2 - this.lastChanged) >= 5000.0 * $$1.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    private void addItem(Recipe<?> $$0) {
        this.recipes.add($$0);
        this.changed = true;
    }

    public static void addOrUpdate(ToastComponent $$0, Recipe<?> $$1) {
        RecipeToast $$2 = $$0.getToast(RecipeToast.class, NO_TOKEN);
        if ($$2 == null) {
            $$0.addToast(new RecipeToast($$1));
        } else {
            $$2.addItem($$1);
        }
    }
}
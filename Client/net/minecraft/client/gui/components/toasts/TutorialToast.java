/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TutorialToast
implements Toast {
    public static final int PROGRESS_BAR_WIDTH = 154;
    public static final int PROGRESS_BAR_HEIGHT = 1;
    public static final int PROGRESS_BAR_X = 3;
    public static final int PROGRESS_BAR_Y = 28;
    private final Icons icon;
    private final Component title;
    @Nullable
    private final Component message;
    private Toast.Visibility visibility = Toast.Visibility.SHOW;
    private long lastProgressTime;
    private float lastProgress;
    private float progress;
    private final boolean progressable;

    public TutorialToast(Icons $$0, Component $$1, @Nullable Component $$2, boolean $$3) {
        this.icon = $$0;
        this.title = $$1;
        this.message = $$2;
        this.progressable = $$3;
    }

    @Override
    public Toast.Visibility render(PoseStack $$0, ToastComponent $$1, long $$2) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        $$1.blit($$0, 0, 0, 0, 96, this.width(), this.height());
        this.icon.render($$0, $$1, 6, 6);
        if (this.message == null) {
            $$1.getMinecraft().font.draw($$0, this.title, 30.0f, 12.0f, -11534256);
        } else {
            $$1.getMinecraft().font.draw($$0, this.title, 30.0f, 7.0f, -11534256);
            $$1.getMinecraft().font.draw($$0, this.message, 30.0f, 18.0f, -16777216);
        }
        if (this.progressable) {
            int $$5;
            GuiComponent.fill($$0, 3, 28, 157, 29, -1);
            float $$3 = Mth.clampedLerp(this.lastProgress, this.progress, (float)($$2 - this.lastProgressTime) / 100.0f);
            if (this.progress >= this.lastProgress) {
                int $$4 = -16755456;
            } else {
                $$5 = -11206656;
            }
            GuiComponent.fill($$0, 3, 28, (int)(3.0f + 154.0f * $$3), 29, $$5);
            this.lastProgress = $$3;
            this.lastProgressTime = $$2;
        }
        return this.visibility;
    }

    public void hide() {
        this.visibility = Toast.Visibility.HIDE;
    }

    public void updateProgress(float $$0) {
        this.progress = $$0;
    }

    public static enum Icons {
        MOVEMENT_KEYS(0, 0),
        MOUSE(1, 0),
        TREE(2, 0),
        RECIPE_BOOK(0, 1),
        WOODEN_PLANKS(1, 1),
        SOCIAL_INTERACTIONS(2, 1),
        RIGHT_CLICK(3, 1);

        private final int x;
        private final int y;

        private Icons(int $$0, int $$1) {
            this.x = $$0;
            this.y = $$1;
        }

        public void render(PoseStack $$0, GuiComponent $$1, int $$2, int $$3) {
            RenderSystem.enableBlend();
            $$1.blit($$0, $$2, $$3, 176 + this.x * 20, this.y * 20, 20, 20);
            RenderSystem.enableBlend();
        }
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  java.lang.Class
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.BitSet
 *  java.util.Deque
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.util.Mth;

public class ToastComponent
extends GuiComponent {
    private static final int SLOT_COUNT = 5;
    private static final int NO_SPACE = -1;
    final Minecraft minecraft;
    private final List<ToastInstance<?>> visible = new ArrayList();
    private final BitSet occupiedSlots = new BitSet(5);
    private final Deque<Toast> queued = Queues.newArrayDeque();

    public ToastComponent(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void render(PoseStack $$02) {
        if (this.minecraft.options.hideGui) {
            return;
        }
        int $$1 = this.minecraft.getWindow().getGuiScaledWidth();
        this.visible.removeIf($$2 -> {
            if ($$2 != null && $$2.render($$1, $$02)) {
                this.occupiedSlots.clear($$2.index, $$2.index + $$2.slotCount);
                return true;
            }
            return false;
        });
        if (!this.queued.isEmpty() && this.freeSlots() > 0) {
            this.queued.removeIf($$0 -> {
                int $$1 = $$0.slotCount();
                int $$2 = this.findFreeIndex($$1);
                if ($$2 != -1) {
                    this.visible.add((Object)new ToastInstance(this, $$0, $$2, $$1));
                    this.occupiedSlots.set($$2, $$2 + $$1);
                    return true;
                }
                return false;
            });
        }
    }

    private int findFreeIndex(int $$0) {
        if (this.freeSlots() >= $$0) {
            int $$1 = 0;
            for (int $$2 = 0; $$2 < 5; ++$$2) {
                if (this.occupiedSlots.get($$2)) {
                    $$1 = 0;
                    continue;
                }
                if (++$$1 != $$0) continue;
                return $$2 + 1 - $$1;
            }
        }
        return -1;
    }

    private int freeSlots() {
        return 5 - this.occupiedSlots.cardinality();
    }

    @Nullable
    public <T extends Toast> T getToast(Class<? extends T> $$0, Object $$1) {
        for (ToastInstance $$2 : this.visible) {
            if ($$2 == null || !$$0.isAssignableFrom($$2.getToast().getClass()) || !$$2.getToast().getToken().equals($$1)) continue;
            return $$2.getToast();
        }
        for (Toast $$3 : this.queued) {
            if (!$$0.isAssignableFrom($$3.getClass()) || !$$3.getToken().equals($$1)) continue;
            return (T)$$3;
        }
        return null;
    }

    public void clear() {
        this.occupiedSlots.clear();
        this.visible.clear();
        this.queued.clear();
    }

    public void addToast(Toast $$0) {
        this.queued.add((Object)$$0);
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public double getNotificationDisplayTimeMultiplier() {
        return this.minecraft.options.notificationDisplayTime().get();
    }

    class ToastInstance<T extends Toast> {
        private static final long ANIMATION_TIME = 600L;
        private final T toast;
        final int index;
        final int slotCount;
        private long animationTime = -1L;
        private long visibleTime = -1L;
        private Toast.Visibility visibility = Toast.Visibility.SHOW;
        final /* synthetic */ ToastComponent this$0;

        /*
         * WARNING - Possible parameter corruption
         */
        ToastInstance(T $$0, int $$1, int $$2) {
            this.this$0 = (ToastComponent)n;
            this.toast = $$0;
            this.index = $$1;
            this.slotCount = $$2;
        }

        public T getToast() {
            return this.toast;
        }

        private float getVisibility(long $$0) {
            float $$1 = Mth.clamp((float)($$0 - this.animationTime) / 600.0f, 0.0f, 1.0f);
            $$1 *= $$1;
            if (this.visibility == Toast.Visibility.HIDE) {
                return 1.0f - $$1;
            }
            return $$1;
        }

        public boolean render(int $$0, PoseStack $$1) {
            long $$2 = Util.getMillis();
            if (this.animationTime == -1L) {
                this.animationTime = $$2;
                this.visibility.playSound(this.this$0.minecraft.getSoundManager());
            }
            if (this.visibility == Toast.Visibility.SHOW && $$2 - this.animationTime <= 600L) {
                this.visibleTime = $$2;
            }
            PoseStack $$3 = RenderSystem.getModelViewStack();
            $$3.pushPose();
            $$3.translate((float)$$0 - (float)this.toast.width() * this.getVisibility($$2), this.index * 32, 800.0f);
            RenderSystem.applyModelViewMatrix();
            Toast.Visibility $$4 = this.toast.render($$1, this.this$0, $$2 - this.visibleTime);
            $$3.popPose();
            RenderSystem.applyModelViewMatrix();
            if ($$4 != this.visibility) {
                this.animationTime = $$2 - (long)((int)((1.0f - this.getVisibility($$2)) * 600.0f));
                this.visibility = $$4;
                this.visibility.playSound(this.this$0.minecraft.getSoundManager());
            }
            return this.visibility == Toast.Visibility.HIDE && $$2 - this.animationTime > 600L;
        }
    }
}
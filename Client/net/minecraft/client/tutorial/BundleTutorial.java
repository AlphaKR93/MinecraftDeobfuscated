/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BundleTutorial {
    private final Tutorial tutorial;
    private final Options options;
    @Nullable
    private TutorialToast toast;

    public BundleTutorial(Tutorial $$0, Options $$1) {
        this.tutorial = $$0;
        this.options = $$1;
    }

    private void showToast() {
        if (this.toast != null) {
            this.tutorial.removeTimedToast(this.toast);
        }
        MutableComponent $$0 = Component.translatable("tutorial.bundleInsert.title");
        MutableComponent $$1 = Component.translatable("tutorial.bundleInsert.description");
        this.toast = new TutorialToast(TutorialToast.Icons.RIGHT_CLICK, $$0, $$1, true);
        this.tutorial.addTimedToast(this.toast, 160);
    }

    private void clearToast() {
        if (this.toast != null) {
            this.tutorial.removeTimedToast(this.toast);
            this.toast = null;
        }
        if (!this.options.hideBundleTutorial) {
            this.options.hideBundleTutorial = true;
            this.options.save();
        }
    }

    public void onInventoryAction(ItemStack $$0, ItemStack $$1, ClickAction $$2) {
        if (this.options.hideBundleTutorial) {
            return;
        }
        if (!$$0.isEmpty() && $$1.is(Items.BUNDLE)) {
            if ($$2 == ClickAction.PRIMARY) {
                this.showToast();
            } else if ($$2 == ClickAction.SECONDARY) {
                this.clearToast();
            }
        } else if ($$0.is(Items.BUNDLE) && !$$1.isEmpty() && $$2 == ClickAction.SECONDARY) {
            this.clearToast();
        }
    }
}
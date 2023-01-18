/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.tutorial;

import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.network.chat.Component;

public class OpenInventoryTutorialStep
implements TutorialStepInstance {
    private static final int HINT_DELAY = 600;
    private static final Component TITLE = Component.translatable("tutorial.open_inventory.title");
    private static final Component DESCRIPTION = Component.translatable("tutorial.open_inventory.description", Tutorial.key("inventory"));
    private final Tutorial tutorial;
    private TutorialToast toast;
    private int timeWaiting;

    public OpenInventoryTutorialStep(Tutorial $$0) {
        this.tutorial = $$0;
    }

    @Override
    public void tick() {
        ++this.timeWaiting;
        if (!this.tutorial.isSurvival()) {
            this.tutorial.setStep(TutorialSteps.NONE);
            return;
        }
        if (this.timeWaiting >= 600 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.RECIPE_BOOK, TITLE, DESCRIPTION, false);
            this.tutorial.getMinecraft().getToasts().addToast(this.toast);
        }
    }

    @Override
    public void clear() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }

    @Override
    public void onOpenInventory() {
        this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
    }
}
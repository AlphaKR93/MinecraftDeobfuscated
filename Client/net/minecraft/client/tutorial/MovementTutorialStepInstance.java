/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.tutorial;

import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.player.Input;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.network.chat.Component;

public class MovementTutorialStepInstance
implements TutorialStepInstance {
    private static final int MINIMUM_TIME_MOVED = 40;
    private static final int MINIMUM_TIME_LOOKED = 40;
    private static final int MOVE_HINT_DELAY = 100;
    private static final int LOOK_HINT_DELAY = 20;
    private static final int INCOMPLETE = -1;
    private static final Component MOVE_TITLE = Component.translatable("tutorial.move.title", Tutorial.key("forward"), Tutorial.key("left"), Tutorial.key("back"), Tutorial.key("right"));
    private static final Component MOVE_DESCRIPTION = Component.translatable("tutorial.move.description", Tutorial.key("jump"));
    private static final Component LOOK_TITLE = Component.translatable("tutorial.look.title");
    private static final Component LOOK_DESCRIPTION = Component.translatable("tutorial.look.description");
    private final Tutorial tutorial;
    private TutorialToast moveToast;
    private TutorialToast lookToast;
    private int timeWaiting;
    private int timeMoved;
    private int timeLooked;
    private boolean moved;
    private boolean turned;
    private int moveCompleted = -1;
    private int lookCompleted = -1;

    public MovementTutorialStepInstance(Tutorial $$0) {
        this.tutorial = $$0;
    }

    @Override
    public void tick() {
        ++this.timeWaiting;
        if (this.moved) {
            ++this.timeMoved;
            this.moved = false;
        }
        if (this.turned) {
            ++this.timeLooked;
            this.turned = false;
        }
        if (this.moveCompleted == -1 && this.timeMoved > 40) {
            if (this.moveToast != null) {
                this.moveToast.hide();
                this.moveToast = null;
            }
            this.moveCompleted = this.timeWaiting;
        }
        if (this.lookCompleted == -1 && this.timeLooked > 40) {
            if (this.lookToast != null) {
                this.lookToast.hide();
                this.lookToast = null;
            }
            this.lookCompleted = this.timeWaiting;
        }
        if (this.moveCompleted != -1 && this.lookCompleted != -1) {
            if (this.tutorial.isSurvival()) {
                this.tutorial.setStep(TutorialSteps.FIND_TREE);
            } else {
                this.tutorial.setStep(TutorialSteps.NONE);
            }
        }
        if (this.moveToast != null) {
            this.moveToast.updateProgress((float)this.timeMoved / 40.0f);
        }
        if (this.lookToast != null) {
            this.lookToast.updateProgress((float)this.timeLooked / 40.0f);
        }
        if (this.timeWaiting >= 100) {
            if (this.moveCompleted == -1 && this.moveToast == null) {
                this.moveToast = new TutorialToast(TutorialToast.Icons.MOVEMENT_KEYS, MOVE_TITLE, MOVE_DESCRIPTION, true);
                this.tutorial.getMinecraft().getToasts().addToast(this.moveToast);
            } else if (this.moveCompleted != -1 && this.timeWaiting - this.moveCompleted >= 20 && this.lookCompleted == -1 && this.lookToast == null) {
                this.lookToast = new TutorialToast(TutorialToast.Icons.MOUSE, LOOK_TITLE, LOOK_DESCRIPTION, true);
                this.tutorial.getMinecraft().getToasts().addToast(this.lookToast);
            }
        }
    }

    @Override
    public void clear() {
        if (this.moveToast != null) {
            this.moveToast.hide();
            this.moveToast = null;
        }
        if (this.lookToast != null) {
            this.lookToast.hide();
            this.lookToast = null;
        }
    }

    @Override
    public void onInput(Input $$0) {
        if ($$0.up || $$0.down || $$0.left || $$0.right || $$0.jumping) {
            this.moved = true;
        }
    }

    @Override
    public void onMouse(double $$0, double $$1) {
        if (Math.abs((double)$$0) > 0.01 || Math.abs((double)$$1) > 0.01) {
            this.turned = true;
        }
    }
}
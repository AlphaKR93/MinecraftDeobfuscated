/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.player;

import net.minecraft.client.Options;
import net.minecraft.client.player.Input;

public class KeyboardInput
extends Input {
    private final Options options;

    public KeyboardInput(Options $$0) {
        this.options = $$0;
    }

    private static float calculateImpulse(boolean $$0, boolean $$1) {
        if ($$0 == $$1) {
            return 0.0f;
        }
        return $$0 ? 1.0f : -1.0f;
    }

    @Override
    public void tick(boolean $$0, float $$1) {
        this.up = this.options.keyUp.isDown();
        this.down = this.options.keyDown.isDown();
        this.left = this.options.keyLeft.isDown();
        this.right = this.options.keyRight.isDown();
        this.forwardImpulse = KeyboardInput.calculateImpulse(this.up, this.down);
        this.leftImpulse = KeyboardInput.calculateImpulse(this.left, this.right);
        this.jumping = this.options.keyJump.isDown();
        this.shiftKeyDown = this.options.keyShift.isDown();
        if ($$0) {
            this.leftImpulse *= $$1;
            this.forwardImpulse *= $$1;
        }
    }
}
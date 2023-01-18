/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.BooleanSupplier
 */
package net.minecraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.BooleanSupplier;
import net.minecraft.client.KeyMapping;

public class ToggleKeyMapping
extends KeyMapping {
    private final BooleanSupplier needsToggle;

    public ToggleKeyMapping(String $$0, int $$1, String $$2, BooleanSupplier $$3) {
        super($$0, InputConstants.Type.KEYSYM, $$1, $$2);
        this.needsToggle = $$3;
    }

    @Override
    public void setDown(boolean $$0) {
        if (this.needsToggle.getAsBoolean()) {
            if ($$0) {
                super.setDown(!this.isDown());
            }
        } else {
            super.setDown($$0);
        }
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Button
extends AbstractButton {
    public static final int SMALL_WIDTH = 120;
    public static final int DEFAULT_WIDTH = 150;
    public static final int DEFAULT_HEIGHT = 20;
    protected static final CreateNarration DEFAULT_NARRATION = $$0 -> (MutableComponent)$$0.get();
    protected final OnPress onPress;
    protected final CreateNarration createNarration;

    public static Builder builder(Component $$0, OnPress $$1) {
        return new Builder($$0, $$1);
    }

    protected Button(int $$0, int $$1, int $$2, int $$3, Component $$4, OnPress $$5, CreateNarration $$6) {
        super($$0, $$1, $$2, $$3, $$4);
        this.onPress = $$5;
        this.createNarration = $$6;
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return this.createNarration.createNarrationMessage((Supplier<MutableComponent>)((Supplier)() -> super.createNarrationMessage()));
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        this.defaultButtonNarrationText($$0);
    }

    public static class Builder {
        private final Component message;
        private final OnPress onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private CreateNarration createNarration = DEFAULT_NARRATION;

        public Builder(Component $$0, OnPress $$1) {
            this.message = $$0;
            this.onPress = $$1;
        }

        public Builder pos(int $$0, int $$1) {
            this.x = $$0;
            this.y = $$1;
            return this;
        }

        public Builder width(int $$0) {
            this.width = $$0;
            return this;
        }

        public Builder size(int $$0, int $$1) {
            this.width = $$0;
            this.height = $$1;
            return this;
        }

        public Builder bounds(int $$0, int $$1, int $$2, int $$3) {
            return this.pos($$0, $$1).size($$2, $$3);
        }

        public Builder tooltip(@Nullable Tooltip $$0) {
            this.tooltip = $$0;
            return this;
        }

        public Builder createNarration(CreateNarration $$0) {
            this.createNarration = $$0;
            return this;
        }

        public Button build() {
            Button $$0 = new Button(this.x, this.y, this.width, this.height, this.message, this.onPress, this.createNarration);
            $$0.setTooltip(this.tooltip);
            return $$0;
        }
    }

    public static interface OnPress {
        public void onPress(Button var1);
    }

    public static interface CreateNarration {
        public MutableComponent createNarrationMessage(Supplier<MutableComponent> var1);
    }
}
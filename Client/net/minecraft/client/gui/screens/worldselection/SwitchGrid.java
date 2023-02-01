/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.BooleanSupplier
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.worldselection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

class SwitchGrid {
    private static final int DEFAULT_SWITCH_BUTTON_WIDTH = 44;
    private final List<LabeledSwitch> switches;

    SwitchGrid(List<LabeledSwitch> $$0) {
        this.switches = $$0;
    }

    public void refreshStates() {
        this.switches.forEach(LabeledSwitch::refreshState);
    }

    public static Builder builder(int $$0) {
        return new Builder($$0);
    }

    public static class Builder {
        private final int width;
        private final List<SwitchBuilder> switchBuilders = new ArrayList();
        int paddingLeft;

        public Builder(int $$0) {
            this.width = $$0;
        }

        public SwitchBuilder addSwitch(Component $$0, BooleanSupplier $$1, Consumer<Boolean> $$2) {
            SwitchBuilder $$3 = new SwitchBuilder($$0, $$1, $$2, 44);
            this.switchBuilders.add((Object)$$3);
            return $$3;
        }

        public Builder withPaddingLeft(int $$0) {
            this.paddingLeft = $$0;
            return this;
        }

        public SwitchGrid build(Consumer<LayoutElement> $$0) {
            GridLayout $$1 = new GridLayout().rowSpacing(4);
            $$1.addChild(SpacerElement.width(this.width - 44), 0, 0);
            $$1.addChild(SpacerElement.width(44), 0, 1);
            ArrayList $$2 = new ArrayList();
            int $$3 = 0;
            for (SwitchBuilder $$4 : this.switchBuilders) {
                $$2.add((Object)$$4.build(this, $$1, $$3++, 0));
            }
            $$1.arrangeElements();
            $$0.accept((Object)$$1);
            return new SwitchGrid((List<LabeledSwitch>)$$2);
        }
    }

    static class LabeledSwitch {
        private final CycleButton<Boolean> button;
        private final BooleanSupplier stateSupplier;
        @Nullable
        private final BooleanSupplier isActiveCondition;

        public LabeledSwitch(CycleButton<Boolean> $$0, BooleanSupplier $$1, @Nullable BooleanSupplier $$2) {
            this.button = $$0;
            this.stateSupplier = $$1;
            this.isActiveCondition = $$2;
        }

        public void refreshState() {
            this.button.setValue(this.stateSupplier.getAsBoolean());
            if (this.isActiveCondition != null) {
                this.button.active = this.isActiveCondition.getAsBoolean();
            }
        }
    }

    public static class SwitchBuilder {
        private final Component label;
        private final BooleanSupplier stateSupplier;
        private final Consumer<Boolean> onClicked;
        @Nullable
        private Component info;
        @Nullable
        private BooleanSupplier isActiveCondition;
        private final int buttonWidth;

        SwitchBuilder(Component $$0, BooleanSupplier $$1, Consumer<Boolean> $$2, int $$3) {
            this.label = $$0;
            this.stateSupplier = $$1;
            this.onClicked = $$2;
            this.buttonWidth = $$3;
        }

        public SwitchBuilder withIsActiveCondition(BooleanSupplier $$0) {
            this.isActiveCondition = $$0;
            return this;
        }

        public SwitchBuilder withInfo(Component $$0) {
            this.info = $$0;
            return this;
        }

        LabeledSwitch build(Builder $$02, GridLayout $$12, int $$2, int $$3) {
            StringWidget $$4 = new StringWidget(this.label, Minecraft.getInstance().font).alignLeft();
            $$12.addChild($$4, $$2, $$3, $$12.newCellSettings().align(0.0f, 0.5f).paddingLeft($$02.paddingLeft));
            CycleButton.Builder<Boolean> $$5 = CycleButton.onOffBuilder(this.stateSupplier.getAsBoolean());
            $$5.displayOnlyValue();
            $$5.withCustomNarration((Function<CycleButton<Boolean>, MutableComponent>)((Function)$$0 -> CommonComponents.joinForNarration(this.label, $$0.createDefaultNarrationMessage())));
            if (this.info != null) {
                $$5.withTooltip($$0 -> Tooltip.create(this.info));
            }
            CycleButton<Boolean> $$6 = $$5.create(0, 0, this.buttonWidth, 20, Component.empty(), ($$0, $$1) -> this.onClicked.accept($$1));
            if (this.isActiveCondition != null) {
                $$6.active = this.isActiveCondition.getAsBoolean();
            }
            $$12.addChild($$6, $$2, $$3 + 1, $$12.newCellSettings().alignHorizontallyRight());
            return new LabeledSwitch($$6, this.stateSupplier, this.isActiveCondition);
        }
    }
}
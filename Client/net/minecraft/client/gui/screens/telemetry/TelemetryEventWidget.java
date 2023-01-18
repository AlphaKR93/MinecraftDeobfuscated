/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Comparator
 *  java.util.Objects
 *  java.util.function.Consumer
 *  java.util.function.DoubleConsumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.telemetry;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TelemetryEventWidget
extends AbstractScrollWidget {
    private static final int HEADER_HORIZONTAL_PADDING = 32;
    private static final String TELEMETRY_REQUIRED_TRANSLATION_KEY = "telemetry.event.required";
    private static final String TELEMETRY_OPTIONAL_TRANSLATION_KEY = "telemetry.event.optional";
    private static final Component PROPERTY_TITLE = Component.translatable("telemetry_info.property_title").withStyle(ChatFormatting.UNDERLINE);
    private final Font font;
    private Content content;
    @Nullable
    private DoubleConsumer onScrolledListener;

    public TelemetryEventWidget(int $$0, int $$1, int $$2, int $$3, Font $$4) {
        super($$0, $$1, $$2, $$3, Component.empty());
        this.font = $$4;
        this.content = this.buildContent(Minecraft.getInstance().telemetryOptInExtra());
    }

    public void onOptInChanged(boolean $$0) {
        this.content = this.buildContent($$0);
        this.setScrollAmount(this.scrollAmount());
    }

    private Content buildContent(boolean $$0) {
        ContentBuilder $$1 = new ContentBuilder(this.containerWidth());
        ArrayList $$2 = new ArrayList(TelemetryEventType.values());
        $$2.sort(Comparator.comparing(TelemetryEventType::isOptIn));
        if (!$$0) {
            $$2.removeIf(TelemetryEventType::isOptIn);
        }
        for (int $$3 = 0; $$3 < $$2.size(); ++$$3) {
            TelemetryEventType $$4 = (TelemetryEventType)$$2.get($$3);
            this.addEventType($$1, $$4);
            if ($$3 >= $$2.size() - 1) continue;
            Objects.requireNonNull((Object)this.font);
            $$1.addSpacer(9);
        }
        return $$1.build();
    }

    public void setOnScrolledListener(@Nullable DoubleConsumer $$0) {
        this.onScrolledListener = $$0;
    }

    @Override
    protected void setScrollAmount(double $$0) {
        super.setScrollAmount($$0);
        if (this.onScrolledListener != null) {
            this.onScrolledListener.accept(this.scrollAmount());
        }
    }

    @Override
    protected int getInnerHeight() {
        return this.content.container().getHeight();
    }

    @Override
    protected boolean scrollbarVisible() {
        return this.getInnerHeight() > this.height;
    }

    @Override
    protected double scrollRate() {
        Objects.requireNonNull((Object)this.font);
        return 9.0;
    }

    @Override
    protected void renderContents(PoseStack $$0, int $$1, int $$2, float $$3) {
        int $$42 = this.getY() + this.innerPadding();
        int $$5 = this.getX() + this.innerPadding();
        $$0.pushPose();
        $$0.translate((double)$$5, (double)$$42, 0.0);
        this.content.container().visitWidgets((Consumer<AbstractWidget>)((Consumer)$$4 -> $$4.render($$0, $$1, $$2, $$3)));
        $$0.popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, this.content.narration());
    }

    private void addEventType(ContentBuilder $$0, TelemetryEventType $$1) {
        String $$2 = $$1.isOptIn() ? TELEMETRY_OPTIONAL_TRANSLATION_KEY : TELEMETRY_REQUIRED_TRANSLATION_KEY;
        $$0.addHeader(this.font, Component.translatable($$2, $$1.title()));
        $$0.addHeader(this.font, $$1.description().withStyle(ChatFormatting.GRAY));
        Objects.requireNonNull((Object)this.font);
        $$0.addSpacer(9 / 2);
        $$0.addLine(this.font, PROPERTY_TITLE, 2);
        this.addEventTypeProperties($$1, $$0);
    }

    private void addEventTypeProperties(TelemetryEventType $$0, ContentBuilder $$1) {
        for (TelemetryProperty $$2 : $$0.properties()) {
            $$1.addLine(this.font, $$2.title());
        }
    }

    private int containerWidth() {
        return this.width - this.totalInnerPadding();
    }

    record Content(GridLayout container, Component narration) {
    }

    static class ContentBuilder {
        private final int width;
        private final GridLayout grid;
        private final GridLayout.RowHelper helper;
        private final LayoutSettings alignHeader;
        private final MutableComponent narration = Component.empty();

        public ContentBuilder(int $$0) {
            this.width = $$0;
            this.grid = new GridLayout();
            this.grid.defaultCellSetting().alignHorizontallyLeft();
            this.helper = this.grid.createRowHelper(1);
            this.helper.addChild(SpacerElement.width($$0));
            this.alignHeader = this.helper.newCellSettings().alignHorizontallyCenter().paddingHorizontal(32);
        }

        public void addLine(Font $$0, Component $$1) {
            this.addLine($$0, $$1, 0);
        }

        public void addLine(Font $$0, Component $$1, int $$2) {
            this.helper.addChild(MultiLineTextWidget.create(this.width, $$0, $$1), this.helper.newCellSettings().paddingBottom($$2));
            this.narration.append($$1).append("\n");
        }

        public void addHeader(Font $$0, Component $$1) {
            this.helper.addChild(MultiLineTextWidget.createCentered(this.width - 64, $$0, $$1), this.alignHeader);
            this.narration.append($$1).append("\n");
        }

        public void addSpacer(int $$0) {
            this.helper.addChild(SpacerElement.height($$0));
        }

        public Content build() {
            this.grid.arrangeElements();
            return new Content(this.grid, this.narration);
        }
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.multiplayer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public abstract class WarningScreen
extends Screen {
    private final Component content;
    @Nullable
    private final Component check;
    private final Component narration;
    @Nullable
    protected Checkbox stopShowing;
    private MultiLineLabel message = MultiLineLabel.EMPTY;

    protected WarningScreen(Component $$0, Component $$1, Component $$2) {
        this($$0, $$1, null, $$2);
    }

    protected WarningScreen(Component $$0, Component $$1, @Nullable Component $$2, Component $$3) {
        super($$0);
        this.content = $$1;
        this.check = $$2;
        this.narration = $$3;
    }

    protected abstract void initButtons(int var1);

    @Override
    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, (FormattedText)this.content, this.width - 100);
        int $$0 = (this.message.getLineCount() + 1) * this.getLineHeight();
        if (this.check != null) {
            int $$1 = this.font.width(this.check);
            this.stopShowing = new Checkbox(this.width / 2 - $$1 / 2 - 8, 76 + $$0, $$1 + 24, 20, this.check, false);
            this.addRenderableWidget(this.stopShowing);
        }
        this.initButtons($$0);
    }

    @Override
    public Component getNarrationMessage() {
        return this.narration;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.renderTitle($$0);
        int $$4 = this.width / 2 - this.message.getWidth() / 2;
        this.message.renderLeftAligned($$0, $$4, 70, this.getLineHeight(), 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }

    protected void renderTitle(PoseStack $$0) {
        WarningScreen.drawString($$0, this.font, this.title, 25, 30, 0xFFFFFF);
    }

    protected int getLineHeight() {
        Objects.requireNonNull((Object)this.font);
        return 9 * 2;
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  java.util.stream.IntStream
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class AbstractSignEditScreen
extends Screen {
    protected final SignBlockEntity sign;
    protected final String[] messages;
    protected final WoodType woodType;
    private int frame;
    private int line;
    private TextFieldHelper signField;

    public AbstractSignEditScreen(SignBlockEntity $$0, boolean $$1) {
        this($$0, $$1, Component.translatable("sign.edit"));
    }

    public AbstractSignEditScreen(SignBlockEntity $$0, boolean $$1, Component $$22) {
        super($$22);
        this.woodType = SignBlock.getWoodType($$0.getBlockState().getBlock());
        this.messages = (String[])IntStream.range((int)0, (int)4).mapToObj($$2 -> $$0.getMessage($$2, $$1)).map(Component::getString).toArray(String[]::new);
        this.sign = $$0;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onDone()).bounds(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build());
        this.sign.setEditable(false);
        this.signField = new TextFieldHelper((Supplier<String>)((Supplier)() -> this.messages[this.line]), (Consumer<String>)((Consumer)$$0 -> {
            this.messages[this.line] = $$0;
            this.sign.setMessage(this.line, Component.literal($$0));
        }), TextFieldHelper.createClipboardGetter(this.minecraft), TextFieldHelper.createClipboardSetter(this.minecraft), (Predicate<String>)((Predicate)$$0 -> this.minecraft.font.width((String)$$0) <= this.sign.getMaxTextLineWidth()));
    }

    @Override
    public void removed() {
        ClientPacketListener $$0 = this.minecraft.getConnection();
        if ($$0 != null) {
            $$0.send(new ServerboundSignUpdatePacket(this.sign.getBlockPos(), this.messages[0], this.messages[1], this.messages[2], this.messages[3]));
        }
        this.sign.setEditable(true);
    }

    @Override
    public void tick() {
        ++this.frame;
        if (!this.sign.getType().isValid(this.sign.getBlockState())) {
            this.onDone();
        }
    }

    private void onDone() {
        this.sign.setChanged();
        this.minecraft.setScreen(null);
    }

    @Override
    public boolean charTyped(char $$0, int $$1) {
        this.signField.charTyped($$0);
        return true;
    }

    @Override
    public void onClose() {
        this.onDone();
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 265) {
            this.line = this.line - 1 & 3;
            this.signField.setCursorToEnd();
            return true;
        }
        if ($$0 == 264 || $$0 == 257 || $$0 == 335) {
            this.line = this.line + 1 & 3;
            this.signField.setCursorToEnd();
            return true;
        }
        if (this.signField.keyPressed($$0)) {
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        Lighting.setupForFlatItems();
        this.renderBackground($$0);
        AbstractSignEditScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 40, 0xFFFFFF);
        this.renderSign($$0);
        Lighting.setupFor3DItems();
        super.render($$0, $$1, $$2, $$3);
    }

    protected abstract void renderSignBackground(PoseStack var1, MultiBufferSource.BufferSource var2, BlockState var3);

    protected abstract Vector3f getSignTextScale();

    protected void offsetSign(PoseStack $$0, BlockState $$1) {
        $$0.translate((float)this.width / 2.0f, 90.0f, 50.0f);
    }

    private void renderSign(PoseStack $$0) {
        MultiBufferSource.BufferSource $$1 = this.minecraft.renderBuffers().bufferSource();
        BlockState $$2 = this.sign.getBlockState();
        $$0.pushPose();
        this.offsetSign($$0, $$2);
        $$0.pushPose();
        this.renderSignBackground($$0, $$1, $$2);
        $$0.popPose();
        this.renderSignText($$0, $$1);
        $$0.popPose();
    }

    private void renderSignText(PoseStack $$0, MultiBufferSource.BufferSource $$1) {
        $$0.translate(0.0f, 0.0f, 4.0f);
        Vector3f $$2 = this.getSignTextScale();
        $$0.scale($$2.x(), $$2.y(), $$2.z());
        int $$3 = this.sign.getColor().getTextColor();
        boolean $$4 = this.frame / 6 % 2 == 0;
        int $$5 = this.signField.getCursorPos();
        int $$6 = this.signField.getSelectionPos();
        int $$7 = 4 * this.sign.getTextLineHeight() / 2;
        int $$8 = this.line * this.sign.getTextLineHeight() - $$7;
        Matrix4f $$9 = $$0.last().pose();
        for (int $$10 = 0; $$10 < this.messages.length; ++$$10) {
            String $$11 = this.messages[$$10];
            if ($$11 == null) continue;
            if (this.font.isBidirectional()) {
                $$11 = this.font.bidirectionalShaping($$11);
            }
            float $$12 = -this.minecraft.font.width($$11) / 2;
            this.minecraft.font.drawInBatch($$11, $$12, $$10 * this.sign.getTextLineHeight() - $$7, $$3, false, $$9, $$1, false, 0, 0xF000F0, false);
            if ($$10 != this.line || $$5 < 0 || !$$4) continue;
            int $$13 = this.minecraft.font.width($$11.substring(0, Math.max((int)Math.min((int)$$5, (int)$$11.length()), (int)0)));
            int $$14 = $$13 - this.minecraft.font.width($$11) / 2;
            if ($$5 < $$11.length()) continue;
            this.minecraft.font.drawInBatch("_", $$14, $$8, $$3, false, $$9, $$1, false, 0, 0xF000F0, false);
        }
        $$1.endBatch();
        for (int $$15 = 0; $$15 < this.messages.length; ++$$15) {
            String $$16 = this.messages[$$15];
            if ($$16 == null || $$15 != this.line || $$5 < 0) continue;
            int $$17 = this.minecraft.font.width($$16.substring(0, Math.max((int)Math.min((int)$$5, (int)$$16.length()), (int)0)));
            int $$18 = $$17 - this.minecraft.font.width($$16) / 2;
            if ($$4 && $$5 < $$16.length()) {
                AbstractSignEditScreen.fill($$0, $$18, $$8 - 1, $$18 + 1, $$8 + this.sign.getTextLineHeight(), 0xFF000000 | $$3);
            }
            if ($$6 == $$5) continue;
            int $$19 = Math.min((int)$$5, (int)$$6);
            int $$20 = Math.max((int)$$5, (int)$$6);
            int $$21 = this.minecraft.font.width($$16.substring(0, $$19)) - this.minecraft.font.width($$16) / 2;
            int $$22 = this.minecraft.font.width($$16.substring(0, $$20)) - this.minecraft.font.width($$16) / 2;
            int $$23 = Math.min((int)$$21, (int)$$22);
            int $$24 = Math.max((int)$$21, (int)$$22);
            RenderSystem.enableColorLogicOp();
            RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
            AbstractSignEditScreen.fill($$0, $$23, $$8, $$24, $$8 + this.sign.getTextLineHeight(), -16776961);
            RenderSystem.disableColorLogicOp();
        }
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;

public class JigsawBlockEditScreen
extends Screen {
    private static final int MAX_LEVELS = 7;
    private static final Component JOINT_LABEL = Component.translatable("jigsaw_block.joint_label");
    private static final Component POOL_LABEL = Component.translatable("jigsaw_block.pool");
    private static final Component NAME_LABEL = Component.translatable("jigsaw_block.name");
    private static final Component TARGET_LABEL = Component.translatable("jigsaw_block.target");
    private static final Component FINAL_STATE_LABEL = Component.translatable("jigsaw_block.final_state");
    private final JigsawBlockEntity jigsawEntity;
    private EditBox nameEdit;
    private EditBox targetEdit;
    private EditBox poolEdit;
    private EditBox finalStateEdit;
    int levels;
    private boolean keepJigsaws = true;
    private CycleButton<JigsawBlockEntity.JointType> jointButton;
    private Button doneButton;
    private Button generateButton;
    private JigsawBlockEntity.JointType joint;

    public JigsawBlockEditScreen(JigsawBlockEntity $$0) {
        super(GameNarrator.NO_TITLE);
        this.jigsawEntity = $$0;
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
        this.targetEdit.tick();
        this.poolEdit.tick();
        this.finalStateEdit.tick();
    }

    private void onDone() {
        this.sendToServer();
        this.minecraft.setScreen(null);
    }

    private void onCancel() {
        this.minecraft.setScreen(null);
    }

    private void sendToServer() {
        this.minecraft.getConnection().send(new ServerboundSetJigsawBlockPacket(this.jigsawEntity.getBlockPos(), new ResourceLocation(this.nameEdit.getValue()), new ResourceLocation(this.targetEdit.getValue()), new ResourceLocation(this.poolEdit.getValue()), this.finalStateEdit.getValue(), this.joint));
    }

    private void sendGenerate() {
        this.minecraft.getConnection().send(new ServerboundJigsawGeneratePacket(this.jigsawEntity.getBlockPos(), this.levels, this.keepJigsaws));
    }

    @Override
    public void onClose() {
        this.onCancel();
    }

    @Override
    protected void init() {
        boolean $$12;
        this.poolEdit = new EditBox(this.font, this.width / 2 - 152, 20, 300, 20, Component.translatable("jigsaw_block.pool"));
        this.poolEdit.setMaxLength(128);
        this.poolEdit.setValue(this.jigsawEntity.getPool().location().toString());
        this.poolEdit.setResponder((Consumer<String>)((Consumer)$$0 -> this.updateValidity()));
        this.addWidget(this.poolEdit);
        this.nameEdit = new EditBox(this.font, this.width / 2 - 152, 55, 300, 20, Component.translatable("jigsaw_block.name"));
        this.nameEdit.setMaxLength(128);
        this.nameEdit.setValue(this.jigsawEntity.getName().toString());
        this.nameEdit.setResponder((Consumer<String>)((Consumer)$$0 -> this.updateValidity()));
        this.addWidget(this.nameEdit);
        this.targetEdit = new EditBox(this.font, this.width / 2 - 152, 90, 300, 20, Component.translatable("jigsaw_block.target"));
        this.targetEdit.setMaxLength(128);
        this.targetEdit.setValue(this.jigsawEntity.getTarget().toString());
        this.targetEdit.setResponder((Consumer<String>)((Consumer)$$0 -> this.updateValidity()));
        this.addWidget(this.targetEdit);
        this.finalStateEdit = new EditBox(this.font, this.width / 2 - 152, 125, 300, 20, Component.translatable("jigsaw_block.final_state"));
        this.finalStateEdit.setMaxLength(256);
        this.finalStateEdit.setValue(this.jigsawEntity.getFinalState());
        this.addWidget(this.finalStateEdit);
        this.joint = this.jigsawEntity.getJoint();
        int $$02 = this.font.width(JOINT_LABEL) + 10;
        this.jointButton = this.addRenderableWidget(CycleButton.builder(JigsawBlockEntity.JointType::getTranslatedName).withValues(JigsawBlockEntity.JointType.values()).withInitialValue(this.joint).displayOnlyValue().create(this.width / 2 - 152 + $$02, 150, 300 - $$02, 20, JOINT_LABEL, ($$0, $$1) -> {
            this.joint = $$1;
        }));
        this.jointButton.active = $$12 = JigsawBlock.getFrontFacing(this.jigsawEntity.getBlockState()).getAxis().isVertical();
        this.jointButton.visible = $$12;
        this.addRenderableWidget(new AbstractSliderButton(this.width / 2 - 154, 180, 100, 20, CommonComponents.EMPTY, 0.0){
            {
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("jigsaw_block.levels", JigsawBlockEditScreen.this.levels));
            }

            @Override
            protected void applyValue() {
                JigsawBlockEditScreen.this.levels = Mth.floor(Mth.clampedLerp(0.0, 7.0, this.value));
            }
        });
        this.addRenderableWidget(CycleButton.onOffBuilder(this.keepJigsaws).create(this.width / 2 - 50, 180, 100, 20, Component.translatable("jigsaw_block.keep_jigsaws"), ($$0, $$1) -> {
            this.keepJigsaws = $$1;
        }));
        this.generateButton = this.addRenderableWidget(Button.builder(Component.translatable("jigsaw_block.generate"), $$0 -> {
            this.onDone();
            this.sendGenerate();
        }).bounds(this.width / 2 + 54, 180, 100, 20).build());
        this.doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onDone()).bounds(this.width / 2 - 4 - 150, 210, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onCancel()).bounds(this.width / 2 + 4, 210, 150, 20).build());
        this.setInitialFocus(this.poolEdit);
        this.updateValidity();
    }

    private void updateValidity() {
        boolean $$0;
        this.doneButton.active = $$0 = ResourceLocation.isValidResourceLocation(this.nameEdit.getValue()) && ResourceLocation.isValidResourceLocation(this.targetEdit.getValue()) && ResourceLocation.isValidResourceLocation(this.poolEdit.getValue());
        this.generateButton.active = $$0;
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        String $$3 = this.nameEdit.getValue();
        String $$4 = this.targetEdit.getValue();
        String $$5 = this.poolEdit.getValue();
        String $$6 = this.finalStateEdit.getValue();
        int $$7 = this.levels;
        JigsawBlockEntity.JointType $$8 = this.joint;
        this.init($$0, $$1, $$2);
        this.nameEdit.setValue($$3);
        this.targetEdit.setValue($$4);
        this.poolEdit.setValue($$5);
        this.finalStateEdit.setValue($$6);
        this.levels = $$7;
        this.joint = $$8;
        this.jointButton.setValue($$8);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        if (this.doneButton.active && ($$0 == 257 || $$0 == 335)) {
            this.onDone();
            return true;
        }
        return false;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        JigsawBlockEditScreen.drawString($$0, this.font, POOL_LABEL, this.width / 2 - 153, 10, 0xA0A0A0);
        this.poolEdit.render($$0, $$1, $$2, $$3);
        JigsawBlockEditScreen.drawString($$0, this.font, NAME_LABEL, this.width / 2 - 153, 45, 0xA0A0A0);
        this.nameEdit.render($$0, $$1, $$2, $$3);
        JigsawBlockEditScreen.drawString($$0, this.font, TARGET_LABEL, this.width / 2 - 153, 80, 0xA0A0A0);
        this.targetEdit.render($$0, $$1, $$2, $$3);
        JigsawBlockEditScreen.drawString($$0, this.font, FINAL_STATE_LABEL, this.width / 2 - 153, 115, 0xA0A0A0);
        this.finalStateEdit.render($$0, $$1, $$2, $$3);
        if (JigsawBlock.getFrontFacing(this.jigsawEntity.getBlockState()).getAxis().isVertical()) {
            JigsawBlockEditScreen.drawString($$0, this.font, JOINT_LABEL, this.width / 2 - 153, 156, 0xFFFFFF);
        }
        super.render($$0, $$1, $$2, $$3);
    }
}
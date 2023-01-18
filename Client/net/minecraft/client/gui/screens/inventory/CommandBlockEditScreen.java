/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

public class CommandBlockEditScreen
extends AbstractCommandBlockEditScreen {
    private final CommandBlockEntity autoCommandBlock;
    private CycleButton<CommandBlockEntity.Mode> modeButton;
    private CycleButton<Boolean> conditionalButton;
    private CycleButton<Boolean> autoexecButton;
    private CommandBlockEntity.Mode mode = CommandBlockEntity.Mode.REDSTONE;
    private boolean conditional;
    private boolean autoexec;

    public CommandBlockEditScreen(CommandBlockEntity $$0) {
        this.autoCommandBlock = $$0;
    }

    @Override
    BaseCommandBlock getCommandBlock() {
        return this.autoCommandBlock.getCommandBlock();
    }

    @Override
    int getPreviousY() {
        return 135;
    }

    @Override
    protected void init() {
        super.init();
        this.modeButton = this.addRenderableWidget(CycleButton.builder($$0 -> {
            switch ($$0) {
                case SEQUENCE: {
                    return Component.translatable("advMode.mode.sequence");
                }
                case AUTO: {
                    return Component.translatable("advMode.mode.auto");
                }
            }
            return Component.translatable("advMode.mode.redstone");
        }).withValues(CommandBlockEntity.Mode.values()).displayOnlyValue().withInitialValue(this.mode).create(this.width / 2 - 50 - 100 - 4, 165, 100, 20, Component.translatable("advMode.mode"), ($$0, $$1) -> {
            this.mode = $$1;
        }));
        this.conditionalButton = this.addRenderableWidget(CycleButton.booleanBuilder(Component.translatable("advMode.mode.conditional"), Component.translatable("advMode.mode.unconditional")).displayOnlyValue().withInitialValue(this.conditional).create(this.width / 2 - 50, 165, 100, 20, Component.translatable("advMode.type"), ($$0, $$1) -> {
            this.conditional = $$1;
        }));
        this.autoexecButton = this.addRenderableWidget(CycleButton.booleanBuilder(Component.translatable("advMode.mode.autoexec.bat"), Component.translatable("advMode.mode.redstoneTriggered")).displayOnlyValue().withInitialValue(this.autoexec).create(this.width / 2 + 50 + 4, 165, 100, 20, Component.translatable("advMode.triggering"), ($$0, $$1) -> {
            this.autoexec = $$1;
        }));
        this.enableControls(false);
    }

    private void enableControls(boolean $$0) {
        this.doneButton.active = $$0;
        this.outputButton.active = $$0;
        this.modeButton.active = $$0;
        this.conditionalButton.active = $$0;
        this.autoexecButton.active = $$0;
    }

    public void updateGui() {
        BaseCommandBlock $$0 = this.autoCommandBlock.getCommandBlock();
        this.commandEdit.setValue($$0.getCommand());
        boolean $$1 = $$0.isTrackOutput();
        this.mode = this.autoCommandBlock.getMode();
        this.conditional = this.autoCommandBlock.isConditional();
        this.autoexec = this.autoCommandBlock.isAutomatic();
        this.outputButton.setValue($$1);
        this.modeButton.setValue(this.mode);
        this.conditionalButton.setValue(this.conditional);
        this.autoexecButton.setValue(this.autoexec);
        this.updatePreviousOutput($$1);
        this.enableControls(true);
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        super.resize($$0, $$1, $$2);
        this.enableControls(true);
    }

    @Override
    protected void populateAndSendPacket(BaseCommandBlock $$0) {
        this.minecraft.getConnection().send(new ServerboundSetCommandBlockPacket(new BlockPos($$0.getPosition()), this.commandEdit.getValue(), this.mode, $$0.isTrackOutput(), this.conditional, this.autoexec));
    }
}
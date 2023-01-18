/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  java.lang.Character
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.text.MessageFormat
 *  java.util.Locale
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.blaze3d.platform.InputConstants;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.InputType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class KeyboardHandler {
    public static final int DEBUG_CRASH_TIME = 10000;
    private final Minecraft minecraft;
    private final ClipboardManager clipboardManager = new ClipboardManager();
    private long debugCrashKeyTime = -1L;
    private long debugCrashKeyReportedTime = -1L;
    private long debugCrashKeyReportedCount = -1L;
    private boolean handledDebugKey;

    public KeyboardHandler(Minecraft $$0) {
        this.minecraft = $$0;
    }

    private boolean handleChunkDebugKeys(int $$0) {
        switch ($$0) {
            case 69: {
                this.minecraft.chunkPath = !this.minecraft.chunkPath;
                this.debugFeedback("ChunkPath: {0}", this.minecraft.chunkPath ? "shown" : "hidden");
                return true;
            }
            case 76: {
                this.minecraft.smartCull = !this.minecraft.smartCull;
                this.debugFeedback("SmartCull: {0}", this.minecraft.smartCull ? "enabled" : "disabled");
                return true;
            }
            case 85: {
                if (Screen.hasShiftDown()) {
                    this.minecraft.levelRenderer.killFrustum();
                    this.debugFeedback("Killed frustum", new Object[0]);
                } else {
                    this.minecraft.levelRenderer.captureFrustum();
                    this.debugFeedback("Captured frustum", new Object[0]);
                }
                return true;
            }
            case 86: {
                this.minecraft.chunkVisibility = !this.minecraft.chunkVisibility;
                this.debugFeedback("ChunkVisibility: {0}", this.minecraft.chunkVisibility ? "enabled" : "disabled");
                return true;
            }
            case 87: {
                this.minecraft.wireframe = !this.minecraft.wireframe;
                this.debugFeedback("WireFrame: {0}", this.minecraft.wireframe ? "enabled" : "disabled");
                return true;
            }
        }
        return false;
    }

    private void debugComponent(ChatFormatting $$0, Component $$1) {
        this.minecraft.gui.getChat().addMessage(Component.empty().append(Component.translatable("debug.prefix").withStyle($$0, ChatFormatting.BOLD)).append(CommonComponents.SPACE).append($$1));
    }

    private void debugFeedbackComponent(Component $$0) {
        this.debugComponent(ChatFormatting.YELLOW, $$0);
    }

    private void debugFeedbackTranslated(String $$0, Object ... $$1) {
        this.debugFeedbackComponent(Component.translatable($$0, $$1));
    }

    private void debugWarningTranslated(String $$0, Object ... $$1) {
        this.debugComponent(ChatFormatting.RED, Component.translatable($$0, $$1));
    }

    private void debugFeedback(String $$0, Object ... $$1) {
        this.debugFeedbackComponent(Component.literal(MessageFormat.format((String)$$0, (Object[])$$1)));
    }

    private boolean handleDebugKeys(int $$0) {
        if (this.debugCrashKeyTime > 0L && this.debugCrashKeyTime < Util.getMillis() - 100L) {
            return true;
        }
        switch ($$0) {
            case 65: {
                this.minecraft.levelRenderer.allChanged();
                this.debugFeedbackTranslated("debug.reload_chunks.message", new Object[0]);
                return true;
            }
            case 66: {
                boolean $$1 = !this.minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes();
                this.minecraft.getEntityRenderDispatcher().setRenderHitBoxes($$1);
                this.debugFeedbackTranslated($$1 ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off", new Object[0]);
                return true;
            }
            case 68: {
                if (this.minecraft.gui != null) {
                    this.minecraft.gui.getChat().clearMessages(false);
                }
                return true;
            }
            case 71: {
                boolean $$2 = this.minecraft.debugRenderer.switchRenderChunkborder();
                this.debugFeedbackTranslated($$2 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off", new Object[0]);
                return true;
            }
            case 72: {
                this.minecraft.options.advancedItemTooltips = !this.minecraft.options.advancedItemTooltips;
                this.debugFeedbackTranslated(this.minecraft.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off", new Object[0]);
                this.minecraft.options.save();
                return true;
            }
            case 73: {
                if (!this.minecraft.player.isReducedDebugInfo()) {
                    this.copyRecreateCommand(this.minecraft.player.hasPermissions(2), !Screen.hasShiftDown());
                }
                return true;
            }
            case 78: {
                if (!this.minecraft.player.hasPermissions(2)) {
                    this.debugFeedbackTranslated("debug.creative_spectator.error", new Object[0]);
                } else if (!this.minecraft.player.isSpectator()) {
                    this.minecraft.player.connection.sendUnsignedCommand("gamemode spectator");
                } else {
                    this.minecraft.player.connection.sendUnsignedCommand("gamemode " + ((GameType)MoreObjects.firstNonNull((Object)this.minecraft.gameMode.getPreviousPlayerMode(), (Object)GameType.CREATIVE)).getName());
                }
                return true;
            }
            case 293: {
                if (!this.minecraft.player.hasPermissions(2)) {
                    this.debugFeedbackTranslated("debug.gamemodes.error", new Object[0]);
                } else {
                    this.minecraft.setScreen(new GameModeSwitcherScreen());
                }
                return true;
            }
            case 80: {
                this.minecraft.options.pauseOnLostFocus = !this.minecraft.options.pauseOnLostFocus;
                this.minecraft.options.save();
                this.debugFeedbackTranslated(this.minecraft.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off", new Object[0]);
                return true;
            }
            case 81: {
                this.debugFeedbackTranslated("debug.help.message", new Object[0]);
                ChatComponent $$3 = this.minecraft.gui.getChat();
                $$3.addMessage(Component.translatable("debug.reload_chunks.help"));
                $$3.addMessage(Component.translatable("debug.show_hitboxes.help"));
                $$3.addMessage(Component.translatable("debug.copy_location.help"));
                $$3.addMessage(Component.translatable("debug.clear_chat.help"));
                $$3.addMessage(Component.translatable("debug.chunk_boundaries.help"));
                $$3.addMessage(Component.translatable("debug.advanced_tooltips.help"));
                $$3.addMessage(Component.translatable("debug.inspect.help"));
                $$3.addMessage(Component.translatable("debug.profiling.help"));
                $$3.addMessage(Component.translatable("debug.creative_spectator.help"));
                $$3.addMessage(Component.translatable("debug.pause_focus.help"));
                $$3.addMessage(Component.translatable("debug.help.help"));
                $$3.addMessage(Component.translatable("debug.reload_resourcepacks.help"));
                $$3.addMessage(Component.translatable("debug.pause.help"));
                $$3.addMessage(Component.translatable("debug.gamemodes.help"));
                return true;
            }
            case 84: {
                this.debugFeedbackTranslated("debug.reload_resourcepacks.message", new Object[0]);
                this.minecraft.reloadResourcePacks();
                return true;
            }
            case 76: {
                if (this.minecraft.debugClientMetricsStart((Consumer<Component>)((Consumer)this::debugFeedbackComponent))) {
                    this.debugFeedbackTranslated("debug.profiling.start", 10);
                }
                return true;
            }
            case 67: {
                if (this.minecraft.player.isReducedDebugInfo()) {
                    return false;
                }
                ClientPacketListener $$4 = this.minecraft.player.connection;
                if ($$4 == null) {
                    return false;
                }
                this.debugFeedbackTranslated("debug.copy_location.message", new Object[0]);
                this.setClipboard(String.format((Locale)Locale.ROOT, (String)"/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", (Object[])new Object[]{this.minecraft.player.level.dimension().location(), this.minecraft.player.getX(), this.minecraft.player.getY(), this.minecraft.player.getZ(), Float.valueOf((float)this.minecraft.player.getYRot()), Float.valueOf((float)this.minecraft.player.getXRot())}));
                return true;
            }
        }
        return false;
    }

    private void copyRecreateCommand(boolean $$0, boolean $$1) {
        HitResult $$22 = this.minecraft.hitResult;
        if ($$22 == null) {
            return;
        }
        switch ($$22.getType()) {
            case BLOCK: {
                BlockPos $$3 = ((BlockHitResult)$$22).getBlockPos();
                BlockState $$4 = this.minecraft.player.level.getBlockState($$3);
                if ($$0) {
                    if ($$1) {
                        this.minecraft.player.connection.getDebugQueryHandler().queryBlockEntityTag($$3, (Consumer<CompoundTag>)((Consumer)$$2 -> {
                            this.copyCreateBlockCommand($$4, $$3, (CompoundTag)$$2);
                            this.debugFeedbackTranslated("debug.inspect.server.block", new Object[0]);
                        }));
                        break;
                    }
                    BlockEntity $$5 = this.minecraft.player.level.getBlockEntity($$3);
                    CompoundTag $$6 = $$5 != null ? $$5.saveWithoutMetadata() : null;
                    this.copyCreateBlockCommand($$4, $$3, $$6);
                    this.debugFeedbackTranslated("debug.inspect.client.block", new Object[0]);
                    break;
                }
                this.copyCreateBlockCommand($$4, $$3, null);
                this.debugFeedbackTranslated("debug.inspect.client.block", new Object[0]);
                break;
            }
            case ENTITY: {
                Entity $$7 = ((EntityHitResult)$$22).getEntity();
                ResourceLocation $$8 = BuiltInRegistries.ENTITY_TYPE.getKey($$7.getType());
                if ($$0) {
                    if ($$1) {
                        this.minecraft.player.connection.getDebugQueryHandler().queryEntityTag($$7.getId(), (Consumer<CompoundTag>)((Consumer)$$2 -> {
                            this.copyCreateEntityCommand($$8, $$7.position(), (CompoundTag)$$2);
                            this.debugFeedbackTranslated("debug.inspect.server.entity", new Object[0]);
                        }));
                        break;
                    }
                    CompoundTag $$9 = $$7.saveWithoutId(new CompoundTag());
                    this.copyCreateEntityCommand($$8, $$7.position(), $$9);
                    this.debugFeedbackTranslated("debug.inspect.client.entity", new Object[0]);
                    break;
                }
                this.copyCreateEntityCommand($$8, $$7.position(), null);
                this.debugFeedbackTranslated("debug.inspect.client.entity", new Object[0]);
                break;
            }
        }
    }

    private void copyCreateBlockCommand(BlockState $$0, BlockPos $$1, @Nullable CompoundTag $$2) {
        StringBuilder $$3 = new StringBuilder(BlockStateParser.serialize($$0));
        if ($$2 != null) {
            $$3.append((Object)$$2);
        }
        String $$4 = String.format((Locale)Locale.ROOT, (String)"/setblock %d %d %d %s", (Object[])new Object[]{$$1.getX(), $$1.getY(), $$1.getZ(), $$3});
        this.setClipboard($$4);
    }

    private void copyCreateEntityCommand(ResourceLocation $$0, Vec3 $$1, @Nullable CompoundTag $$2) {
        String $$5;
        if ($$2 != null) {
            $$2.remove("UUID");
            $$2.remove("Pos");
            $$2.remove("Dimension");
            String $$3 = NbtUtils.toPrettyComponent($$2).getString();
            String $$4 = String.format((Locale)Locale.ROOT, (String)"/summon %s %.2f %.2f %.2f %s", (Object[])new Object[]{$$0.toString(), $$1.x, $$1.y, $$1.z, $$3});
        } else {
            $$5 = String.format((Locale)Locale.ROOT, (String)"/summon %s %.2f %.2f %.2f", (Object[])new Object[]{$$0.toString(), $$1.x, $$1.y, $$1.z});
        }
        this.setClipboard($$5);
    }

    public void keyPress(long $$02, int $$1, int $$2, int $$3, int $$4) {
        if ($$02 != this.minecraft.getWindow().getWindow()) {
            return;
        }
        if (this.debugCrashKeyTime > 0L) {
            if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67) || !InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292)) {
                this.debugCrashKeyTime = -1L;
            }
        } else if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67) && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292)) {
            this.handledDebugKey = true;
            this.debugCrashKeyTime = Util.getMillis();
            this.debugCrashKeyReportedTime = Util.getMillis();
            this.debugCrashKeyReportedCount = 0L;
        }
        this.minecraft.setLastInputType($$1 == 258 ? InputType.KEYBOARD_TAB : InputType.KEYBOARD_OTHER);
        Screen $$5 = this.minecraft.screen;
        if (!($$3 != 1 || this.minecraft.screen instanceof KeyBindsScreen && ((KeyBindsScreen)$$5).lastKeySelection > Util.getMillis() - 20L)) {
            if (this.minecraft.options.keyFullscreen.matches($$1, $$2)) {
                this.minecraft.getWindow().toggleFullScreen();
                this.minecraft.options.fullscreen().set(this.minecraft.getWindow().isFullscreen());
                return;
            }
            if (this.minecraft.options.keyScreenshot.matches($$1, $$2)) {
                if (Screen.hasControlDown()) {
                    // empty if block
                }
                Screenshot.grab(this.minecraft.gameDirectory, this.minecraft.getMainRenderTarget(), (Consumer<Component>)((Consumer)$$0 -> this.minecraft.execute(() -> this.minecraft.gui.getChat().addMessage((Component)$$0))));
                return;
            }
        }
        if (this.minecraft.getNarrator().isActive()) {
            boolean $$6;
            boolean bl = $$6 = $$5 == null || !($$5.getFocused() instanceof EditBox) || !((EditBox)$$5.getFocused()).canConsumeInput();
            if ($$3 != 0 && $$1 == 66 && Screen.hasControlDown() && $$6) {
                boolean $$7 = this.minecraft.options.narrator().get() == NarratorStatus.OFF;
                this.minecraft.options.narrator().set(NarratorStatus.byId(this.minecraft.options.narrator().get().getId() + 1));
                if ($$5 instanceof SimpleOptionsSubScreen) {
                    ((SimpleOptionsSubScreen)$$5).updateNarratorButton();
                }
                if ($$7 && $$5 != null) {
                    $$5.narrationEnabled();
                }
            }
        }
        if ($$5 != null) {
            boolean[] $$8 = new boolean[]{false};
            Screen.wrapScreenError(() -> {
                if ($$3 == 1 || $$3 == 2) {
                    $$5.afterKeyboardAction();
                    $$2[0] = $$5.keyPressed($$1, $$2, $$4);
                } else if ($$3 == 0) {
                    $$2[0] = $$5.keyReleased($$1, $$2, $$4);
                }
            }, "keyPressed event handler", $$5.getClass().getCanonicalName());
            if ($$8[0]) {
                return;
            }
        }
        if (this.minecraft.screen == null || this.minecraft.screen.passEvents) {
            InputConstants.Key $$9 = InputConstants.getKey($$1, $$2);
            if ($$3 == 0) {
                KeyMapping.set($$9, false);
                if ($$1 == 292) {
                    if (this.handledDebugKey) {
                        this.handledDebugKey = false;
                    } else {
                        this.minecraft.options.renderDebug = !this.minecraft.options.renderDebug;
                        this.minecraft.options.renderDebugCharts = this.minecraft.options.renderDebug && Screen.hasShiftDown();
                        this.minecraft.options.renderFpsChart = this.minecraft.options.renderDebug && Screen.hasAltDown();
                    }
                }
            } else {
                if ($$1 == 293 && this.minecraft.gameRenderer != null) {
                    this.minecraft.gameRenderer.togglePostEffect();
                }
                boolean $$10 = false;
                if (this.minecraft.screen == null) {
                    if ($$1 == 256) {
                        boolean $$11 = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292);
                        this.minecraft.pauseGame($$11);
                    }
                    $$10 = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292) && this.handleDebugKeys($$1);
                    this.handledDebugKey |= $$10;
                    if ($$1 == 290) {
                        boolean bl = this.minecraft.options.hideGui = !this.minecraft.options.hideGui;
                    }
                }
                if ($$10) {
                    KeyMapping.set($$9, false);
                } else {
                    KeyMapping.set($$9, true);
                    KeyMapping.click($$9);
                }
                if (this.minecraft.options.renderDebugCharts && $$1 >= 48 && $$1 <= 57) {
                    this.minecraft.debugFpsMeterKeyPress($$1 - 48);
                }
            }
        }
    }

    private void charTyped(long $$0, int $$1, int $$2) {
        if ($$0 != this.minecraft.getWindow().getWindow()) {
            return;
        }
        Screen $$3 = this.minecraft.screen;
        if ($$3 == null || this.minecraft.getOverlay() != null) {
            return;
        }
        if (Character.charCount((int)$$1) == 1) {
            Screen.wrapScreenError(() -> $$3.charTyped((char)$$1, $$2), "charTyped event handler", $$3.getClass().getCanonicalName());
        } else {
            for (char $$4 : Character.toChars((int)$$1)) {
                Screen.wrapScreenError(() -> $$3.charTyped($$4, $$2), "charTyped event handler", $$3.getClass().getCanonicalName());
            }
        }
    }

    public void setup(long $$02) {
        InputConstants.setupKeyboardCallbacks($$02, ($$0, $$1, $$2, $$3, $$4) -> this.minecraft.execute(() -> this.keyPress($$0, $$1, $$2, $$3, $$4)), ($$0, $$1, $$2) -> this.minecraft.execute(() -> this.charTyped($$0, $$1, $$2)));
    }

    public String getClipboard() {
        return this.clipboardManager.getClipboard(this.minecraft.getWindow().getWindow(), ($$0, $$1) -> {
            if ($$0 != 65545) {
                this.minecraft.getWindow().defaultErrorCallback($$0, $$1);
            }
        });
    }

    public void setClipboard(String $$0) {
        if (!$$0.isEmpty()) {
            this.clipboardManager.setClipboard(this.minecraft.getWindow().getWindow(), $$0);
        }
    }

    public void tick() {
        if (this.debugCrashKeyTime > 0L) {
            long $$0 = Util.getMillis();
            long $$1 = 10000L - ($$0 - this.debugCrashKeyTime);
            long $$2 = $$0 - this.debugCrashKeyReportedTime;
            if ($$1 < 0L) {
                if (Screen.hasControlDown()) {
                    Blaze3D.youJustLostTheGame();
                }
                String $$3 = "Manually triggered debug crash";
                CrashReport $$4 = new CrashReport("Manually triggered debug crash", new Throwable("Manually triggered debug crash"));
                CrashReportCategory $$5 = $$4.addCategory("Manual crash details");
                NativeModuleLister.addCrashSection($$5);
                throw new ReportedException($$4);
            }
            if ($$2 >= 1000L) {
                if (this.debugCrashKeyReportedCount == 0L) {
                    this.debugFeedbackTranslated("debug.crash.message", new Object[0]);
                } else {
                    this.debugWarningTranslated("debug.crash.warning", Mth.ceil((float)$$1 / 1000.0f));
                }
                this.debugCrashKeyReportedTime = $$0;
                ++this.debugCrashKeyReportedCount;
            }
        }
    }
}
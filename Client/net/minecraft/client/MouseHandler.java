/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Double
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.util.Arrays
 *  java.util.List
 *  org.lwjgl.glfw.GLFWDropCallback
 */
package net.minecraft.client;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.util.SmoothDouble;
import org.lwjgl.glfw.GLFWDropCallback;

public class MouseHandler {
    private final Minecraft minecraft;
    private boolean isLeftPressed;
    private boolean isMiddlePressed;
    private boolean isRightPressed;
    private double xpos;
    private double ypos;
    private int fakeRightMouse;
    private int activeButton = -1;
    private boolean ignoreFirstMove = true;
    private int clickDepth;
    private double mousePressedTime;
    private final SmoothDouble smoothTurnX = new SmoothDouble();
    private final SmoothDouble smoothTurnY = new SmoothDouble();
    private double accumulatedDX;
    private double accumulatedDY;
    private double accumulatedScroll;
    private double lastMouseEventTime = Double.MIN_VALUE;
    private boolean mouseGrabbed;

    public MouseHandler(Minecraft $$0) {
        this.minecraft = $$0;
    }

    private void onPress(long $$0, int $$1, int $$2, int $$3) {
        boolean $$4;
        if ($$0 != this.minecraft.getWindow().getWindow()) {
            return;
        }
        boolean bl = $$4 = $$2 == 1;
        if (Minecraft.ON_OSX && $$1 == 0) {
            if ($$4) {
                if (($$3 & 2) == 2) {
                    $$1 = 1;
                    ++this.fakeRightMouse;
                }
            } else if (this.fakeRightMouse > 0) {
                $$1 = 1;
                --this.fakeRightMouse;
            }
        }
        int $$5 = $$1;
        if ($$4) {
            if (this.minecraft.options.touchscreen().get().booleanValue() && this.clickDepth++ > 0) {
                return;
            }
            this.activeButton = $$5;
            this.mousePressedTime = Blaze3D.getTime();
        } else if (this.activeButton != -1) {
            if (this.minecraft.options.touchscreen().get().booleanValue() && --this.clickDepth > 0) {
                return;
            }
            this.activeButton = -1;
        }
        boolean[] $$6 = new boolean[]{false};
        if (this.minecraft.getOverlay() == null) {
            if (this.minecraft.screen == null) {
                if (!this.mouseGrabbed && $$4) {
                    this.grabMouse();
                }
            } else {
                double $$7 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
                double $$8 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
                Screen $$9 = this.minecraft.screen;
                if ($$4) {
                    $$9.afterMouseAction();
                    Screen.wrapScreenError(() -> {
                        $$0[0] = $$9.mouseClicked($$7, $$8, $$5);
                    }, "mouseClicked event handler", $$9.getClass().getCanonicalName());
                } else {
                    Screen.wrapScreenError(() -> {
                        $$0[0] = $$9.mouseReleased($$7, $$8, $$5);
                    }, "mouseReleased event handler", $$9.getClass().getCanonicalName());
                }
            }
        }
        if (!$$6[0] && (this.minecraft.screen == null || this.minecraft.screen.passEvents) && this.minecraft.getOverlay() == null) {
            if ($$5 == 0) {
                this.isLeftPressed = $$4;
            } else if ($$5 == 2) {
                this.isMiddlePressed = $$4;
            } else if ($$5 == 1) {
                this.isRightPressed = $$4;
            }
            KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate($$5), $$4);
            if ($$4) {
                if (this.minecraft.player.isSpectator() && $$5 == 2) {
                    this.minecraft.gui.getSpectatorGui().onMouseMiddleClick();
                } else {
                    KeyMapping.click(InputConstants.Type.MOUSE.getOrCreate($$5));
                }
            }
        }
    }

    private void onScroll(long $$0, double $$1, double $$2) {
        if ($$0 == Minecraft.getInstance().getWindow().getWindow()) {
            double $$3 = (this.minecraft.options.discreteMouseScroll().get() != false ? Math.signum((double)$$2) : $$2) * this.minecraft.options.mouseWheelSensitivity().get();
            if (this.minecraft.getOverlay() == null) {
                if (this.minecraft.screen != null) {
                    double $$4 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
                    double $$5 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
                    this.minecraft.screen.mouseScrolled($$4, $$5, $$3);
                    this.minecraft.screen.afterMouseAction();
                } else if (this.minecraft.player != null) {
                    if (this.accumulatedScroll != 0.0 && Math.signum((double)$$3) != Math.signum((double)this.accumulatedScroll)) {
                        this.accumulatedScroll = 0.0;
                    }
                    this.accumulatedScroll += $$3;
                    int $$6 = (int)this.accumulatedScroll;
                    if ($$6 == 0) {
                        return;
                    }
                    this.accumulatedScroll -= (double)$$6;
                    if (this.minecraft.player.isSpectator()) {
                        if (this.minecraft.gui.getSpectatorGui().isMenuActive()) {
                            this.minecraft.gui.getSpectatorGui().onMouseScrolled(-$$6);
                        } else {
                            float $$7 = Mth.clamp(this.minecraft.player.getAbilities().getFlyingSpeed() + (float)$$6 * 0.005f, 0.0f, 0.2f);
                            this.minecraft.player.getAbilities().setFlyingSpeed($$7);
                        }
                    } else {
                        this.minecraft.player.getInventory().swapPaint($$6);
                    }
                }
            }
        }
    }

    private void onDrop(long $$0, List<Path> $$1) {
        if (this.minecraft.screen != null) {
            this.minecraft.screen.onFilesDrop($$1);
        }
    }

    public void setup(long $$02) {
        InputConstants.setupMouseCallbacks($$02, ($$0, $$1, $$2) -> this.minecraft.execute(() -> this.onMove($$0, $$1, $$2)), ($$0, $$1, $$2, $$3) -> this.minecraft.execute(() -> this.onPress($$0, $$1, $$2, $$3)), ($$0, $$1, $$2) -> this.minecraft.execute(() -> this.onScroll($$0, $$1, $$2)), ($$0, $$1, $$2) -> {
            Path[] $$3 = new Path[$$1];
            for (int $$4 = 0; $$4 < $$1; ++$$4) {
                $$3[$$4] = Paths.get((String)GLFWDropCallback.getName((long)$$2, (int)$$4), (String[])new String[0]);
            }
            this.minecraft.execute(() -> this.onDrop($$0, (List<Path>)Arrays.asList((Object[])$$3)));
        });
    }

    private void onMove(long $$0, double $$1, double $$2) {
        Screen $$3;
        if ($$0 != Minecraft.getInstance().getWindow().getWindow()) {
            return;
        }
        if (this.ignoreFirstMove) {
            this.xpos = $$1;
            this.ypos = $$2;
            this.ignoreFirstMove = false;
        }
        if (($$3 = this.minecraft.screen) != null && this.minecraft.getOverlay() == null) {
            double $$4 = $$1 * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
            double $$5 = $$2 * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
            Screen.wrapScreenError(() -> $$3.mouseMoved($$4, $$5), "mouseMoved event handler", $$3.getClass().getCanonicalName());
            if (this.activeButton != -1 && this.mousePressedTime > 0.0) {
                double $$6 = ($$1 - this.xpos) * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
                double $$7 = ($$2 - this.ypos) * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
                Screen.wrapScreenError(() -> $$3.mouseDragged($$4, $$5, this.activeButton, $$6, $$7), "mouseDragged event handler", $$3.getClass().getCanonicalName());
            }
            $$3.afterMouseMove();
        }
        this.minecraft.getProfiler().push("mouse");
        if (this.isMouseGrabbed() && this.minecraft.isWindowActive()) {
            this.accumulatedDX += $$1 - this.xpos;
            this.accumulatedDY += $$2 - this.ypos;
        }
        this.turnPlayer();
        this.xpos = $$1;
        this.ypos = $$2;
        this.minecraft.getProfiler().pop();
    }

    public void turnPlayer() {
        double $$12;
        double $$11;
        double $$0 = Blaze3D.getTime();
        double $$1 = $$0 - this.lastMouseEventTime;
        this.lastMouseEventTime = $$0;
        if (!this.isMouseGrabbed() || !this.minecraft.isWindowActive()) {
            this.accumulatedDX = 0.0;
            this.accumulatedDY = 0.0;
            return;
        }
        double $$2 = this.minecraft.options.sensitivity().get() * (double)0.6f + (double)0.2f;
        double $$3 = $$2 * $$2 * $$2;
        double $$4 = $$3 * 8.0;
        if (this.minecraft.options.smoothCamera) {
            double $$5 = this.smoothTurnX.getNewDeltaValue(this.accumulatedDX * $$4, $$1 * $$4);
            double $$6 = this.smoothTurnY.getNewDeltaValue(this.accumulatedDY * $$4, $$1 * $$4);
            double $$7 = $$5;
            double $$8 = $$6;
        } else if (this.minecraft.options.getCameraType().isFirstPerson() && this.minecraft.player.isScoping()) {
            this.smoothTurnX.reset();
            this.smoothTurnY.reset();
            double $$9 = this.accumulatedDX * $$3;
            double $$10 = this.accumulatedDY * $$3;
        } else {
            this.smoothTurnX.reset();
            this.smoothTurnY.reset();
            $$11 = this.accumulatedDX * $$4;
            $$12 = this.accumulatedDY * $$4;
        }
        this.accumulatedDX = 0.0;
        this.accumulatedDY = 0.0;
        int $$13 = 1;
        if (this.minecraft.options.invertYMouse().get().booleanValue()) {
            $$13 = -1;
        }
        this.minecraft.getTutorial().onMouse($$11, $$12);
        if (this.minecraft.player != null) {
            this.minecraft.player.turn($$11, $$12 * (double)$$13);
        }
    }

    public boolean isLeftPressed() {
        return this.isLeftPressed;
    }

    public boolean isMiddlePressed() {
        return this.isMiddlePressed;
    }

    public boolean isRightPressed() {
        return this.isRightPressed;
    }

    public double xpos() {
        return this.xpos;
    }

    public double ypos() {
        return this.ypos;
    }

    public void setIgnoreFirstMove() {
        this.ignoreFirstMove = true;
    }

    public boolean isMouseGrabbed() {
        return this.mouseGrabbed;
    }

    public void grabMouse() {
        if (!this.minecraft.isWindowActive()) {
            return;
        }
        if (this.mouseGrabbed) {
            return;
        }
        if (!Minecraft.ON_OSX) {
            KeyMapping.setAll();
        }
        this.mouseGrabbed = true;
        this.xpos = this.minecraft.getWindow().getScreenWidth() / 2;
        this.ypos = this.minecraft.getWindow().getScreenHeight() / 2;
        InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212995, this.xpos, this.ypos);
        this.minecraft.setScreen(null);
        this.minecraft.missTime = 10000;
        this.ignoreFirstMove = true;
    }

    public void releaseMouse() {
        if (!this.mouseGrabbed) {
            return;
        }
        this.mouseGrabbed = false;
        this.xpos = this.minecraft.getWindow().getScreenWidth() / 2;
        this.ypos = this.minecraft.getWindow().getScreenHeight() / 2;
        InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, this.xpos, this.ypos);
    }

    public void cursorEntered() {
        this.ignoreFirstMove = true;
    }
}
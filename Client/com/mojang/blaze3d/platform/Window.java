/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.AutoCloseable
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.Buffer
 *  java.nio.ByteBuffer
 *  java.nio.IntBuffer
 *  java.util.Locale
 *  java.util.Locale$Category
 *  java.util.Optional
 *  java.util.function.BiConsumer
 *  javax.annotation.Nullable
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.glfw.Callbacks
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.glfw.GLFWImage
 *  org.lwjgl.glfw.GLFWImage$Buffer
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 *  org.slf4j.Logger
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.MacosUtil;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.SilentInitException;
import net.minecraft.server.packs.resources.IoSupplier;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

public final class Window
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final GLFWErrorCallback defaultErrorCallback = GLFWErrorCallback.create(this::defaultErrorCallback);
    private final WindowEventHandler eventHandler;
    private final ScreenManager screenManager;
    private final long window;
    private int windowedX;
    private int windowedY;
    private int windowedWidth;
    private int windowedHeight;
    private Optional<VideoMode> preferredFullscreenVideoMode;
    private boolean fullscreen;
    private boolean actuallyFullscreen;
    private int x;
    private int y;
    private int width;
    private int height;
    private int framebufferWidth;
    private int framebufferHeight;
    private int guiScaledWidth;
    private int guiScaledHeight;
    private double guiScale;
    private String errorSection = "";
    private boolean dirty;
    private int framerateLimit;
    private boolean vsync;

    public Window(WindowEventHandler $$0, ScreenManager $$1, DisplayData $$2, @Nullable String $$3, String $$4) {
        RenderSystem.assertInInitPhase();
        this.screenManager = $$1;
        this.setBootErrorCallback();
        this.setErrorSection("Pre startup");
        this.eventHandler = $$0;
        Optional<VideoMode> $$5 = VideoMode.read($$3);
        this.preferredFullscreenVideoMode = $$5.isPresent() ? $$5 : ($$2.fullscreenWidth.isPresent() && $$2.fullscreenHeight.isPresent() ? Optional.of((Object)new VideoMode($$2.fullscreenWidth.getAsInt(), $$2.fullscreenHeight.getAsInt(), 8, 8, 8, 60)) : Optional.empty());
        this.actuallyFullscreen = this.fullscreen = $$2.isFullscreen;
        Monitor $$6 = $$1.getMonitor(GLFW.glfwGetPrimaryMonitor());
        this.width = $$2.width > 0 ? $$2.width : 1;
        this.windowedWidth = this.width;
        this.height = $$2.height > 0 ? $$2.height : 1;
        this.windowedHeight = this.height;
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint((int)139265, (int)196609);
        GLFW.glfwWindowHint((int)139275, (int)221185);
        GLFW.glfwWindowHint((int)139266, (int)3);
        GLFW.glfwWindowHint((int)139267, (int)2);
        GLFW.glfwWindowHint((int)139272, (int)204801);
        GLFW.glfwWindowHint((int)139270, (int)1);
        this.window = GLFW.glfwCreateWindow((int)this.width, (int)this.height, (CharSequence)$$4, (long)(this.fullscreen && $$6 != null ? $$6.getMonitor() : 0L), (long)0L);
        if ($$6 != null) {
            VideoMode $$7 = $$6.getPreferredVidMode(this.fullscreen ? this.preferredFullscreenVideoMode : Optional.empty());
            this.windowedX = this.x = $$6.getX() + $$7.getWidth() / 2 - this.width / 2;
            this.windowedY = this.y = $$6.getY() + $$7.getHeight() / 2 - this.height / 2;
        } else {
            int[] $$8 = new int[1];
            int[] $$9 = new int[1];
            GLFW.glfwGetWindowPos((long)this.window, (int[])$$8, (int[])$$9);
            this.windowedX = this.x = $$8[0];
            this.windowedY = this.y = $$9[0];
        }
        GLFW.glfwMakeContextCurrent((long)this.window);
        Locale $$10 = Locale.getDefault((Locale.Category)Locale.Category.FORMAT);
        Locale.setDefault((Locale.Category)Locale.Category.FORMAT, (Locale)Locale.ROOT);
        GL.createCapabilities();
        Locale.setDefault((Locale.Category)Locale.Category.FORMAT, (Locale)$$10);
        this.setMode();
        this.refreshFramebufferSize();
        GLFW.glfwSetFramebufferSizeCallback((long)this.window, this::onFramebufferResize);
        GLFW.glfwSetWindowPosCallback((long)this.window, this::onMove);
        GLFW.glfwSetWindowSizeCallback((long)this.window, this::onResize);
        GLFW.glfwSetWindowFocusCallback((long)this.window, this::onFocus);
        GLFW.glfwSetCursorEnterCallback((long)this.window, this::onEnter);
    }

    public int getRefreshRate() {
        RenderSystem.assertOnRenderThread();
        return GLX._getRefreshRate(this);
    }

    public boolean shouldClose() {
        return GLX._shouldClose(this);
    }

    public static void checkGlfwError(BiConsumer<Integer, String> $$0) {
        RenderSystem.assertInInitPhase();
        try (MemoryStack $$1 = MemoryStack.stackPush();){
            PointerBuffer $$2 = $$1.mallocPointer(1);
            int $$3 = GLFW.glfwGetError((PointerBuffer)$$2);
            if ($$3 != 0) {
                long $$4 = $$2.get();
                String $$5 = $$4 == 0L ? "" : MemoryUtil.memUTF8((long)$$4);
                $$0.accept((Object)$$3, (Object)$$5);
            }
        }
    }

    public void setIcon(IoSupplier<InputStream> $$0, IoSupplier<InputStream> $$1) {
        RenderSystem.assertInInitPhase();
        try (MemoryStack $$2 = MemoryStack.stackPush();){
            IntBuffer $$3 = $$2.mallocInt(1);
            IntBuffer $$4 = $$2.mallocInt(1);
            IntBuffer $$5 = $$2.mallocInt(1);
            GLFWImage.Buffer $$6 = GLFWImage.malloc((int)2, (MemoryStack)$$2);
            ByteBuffer $$7 = this.readIconPixels($$0, $$3, $$4, $$5);
            if ($$7 == null) {
                throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }
            $$6.position(0);
            $$6.width($$3.get(0));
            $$6.height($$4.get(0));
            $$6.pixels($$7);
            ByteBuffer $$8 = this.readIconPixels($$1, $$3, $$4, $$5);
            if ($$8 == null) {
                STBImage.stbi_image_free((ByteBuffer)$$7);
                throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }
            $$6.position(1);
            $$6.width($$3.get(0));
            $$6.height($$4.get(0));
            $$6.pixels($$8);
            $$6.position(0);
            GLFW.glfwSetWindowIcon((long)this.window, (GLFWImage.Buffer)$$6);
            STBImage.stbi_image_free((ByteBuffer)$$7);
            STBImage.stbi_image_free((ByteBuffer)$$8);
        }
        catch (IOException $$9) {
            LOGGER.error("Couldn't set icon", (Throwable)$$9);
        }
    }

    /*
     * Loose catch block
     */
    @Nullable
    private ByteBuffer readIconPixels(IoSupplier<InputStream> $$0, IntBuffer $$1, IntBuffer $$2, IntBuffer $$3) throws IOException {
        ByteBuffer byteBuffer;
        InputStream $$5;
        ByteBuffer $$4;
        block10: {
            block9: {
                RenderSystem.assertInInitPhase();
                $$4 = null;
                $$5 = $$0.get();
                $$4 = TextureUtil.readResource($$5);
                $$4.rewind();
                byteBuffer = STBImage.stbi_load_from_memory((ByteBuffer)$$4, (IntBuffer)$$1, (IntBuffer)$$2, (IntBuffer)$$3, (int)0);
                if ($$5 == null) break block9;
                $$5.close();
            }
            if ($$4 == null) break block10;
            MemoryUtil.memFree((Buffer)$$4);
        }
        return byteBuffer;
        {
            catch (Throwable throwable) {
                try {
                    if ($$5 != null) {
                        try {
                            $$5.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Throwable throwable3) {
                    if ($$4 != null) {
                        MemoryUtil.memFree($$4);
                    }
                    throw throwable3;
                }
            }
        }
    }

    public void setErrorSection(String $$0) {
        this.errorSection = $$0;
    }

    private void setBootErrorCallback() {
        RenderSystem.assertInInitPhase();
        GLFW.glfwSetErrorCallback(Window::bootCrash);
    }

    private static void bootCrash(int $$0, long $$1) {
        RenderSystem.assertInInitPhase();
        String $$2 = "GLFW error " + $$0 + ": " + MemoryUtil.memUTF8((long)$$1);
        TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)($$2 + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions)."), (CharSequence)"ok", (CharSequence)"error", (boolean)false);
        throw new WindowInitFailed($$2);
    }

    public void defaultErrorCallback(int $$0, long $$1) {
        RenderSystem.assertOnRenderThread();
        String $$2 = MemoryUtil.memUTF8((long)$$1);
        LOGGER.error("########## GL ERROR ##########");
        LOGGER.error("@ {}", (Object)this.errorSection);
        LOGGER.error("{}: {}", (Object)$$0, (Object)$$2);
    }

    public void setDefaultErrorCallback() {
        GLFWErrorCallback $$0 = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)this.defaultErrorCallback);
        if ($$0 != null) {
            $$0.free();
        }
    }

    public void updateVsync(boolean $$0) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.vsync = $$0;
        GLFW.glfwSwapInterval((int)($$0 ? 1 : 0));
    }

    public void close() {
        RenderSystem.assertOnRenderThread();
        Callbacks.glfwFreeCallbacks((long)this.window);
        this.defaultErrorCallback.close();
        GLFW.glfwDestroyWindow((long)this.window);
        GLFW.glfwTerminate();
    }

    private void onMove(long $$0, int $$1, int $$2) {
        this.x = $$1;
        this.y = $$2;
    }

    private void onFramebufferResize(long $$0, int $$1, int $$2) {
        if ($$0 != this.window) {
            return;
        }
        int $$3 = this.getWidth();
        int $$4 = this.getHeight();
        if ($$1 == 0 || $$2 == 0) {
            return;
        }
        this.framebufferWidth = $$1;
        this.framebufferHeight = $$2;
        if (this.getWidth() != $$3 || this.getHeight() != $$4) {
            this.eventHandler.resizeDisplay();
        }
    }

    private void refreshFramebufferSize() {
        RenderSystem.assertInInitPhase();
        int[] $$0 = new int[1];
        int[] $$1 = new int[1];
        GLFW.glfwGetFramebufferSize((long)this.window, (int[])$$0, (int[])$$1);
        this.framebufferWidth = $$0[0] > 0 ? $$0[0] : 1;
        this.framebufferHeight = $$1[0] > 0 ? $$1[0] : 1;
    }

    private void onResize(long $$0, int $$1, int $$2) {
        this.width = $$1;
        this.height = $$2;
    }

    private void onFocus(long $$0, boolean $$1) {
        if ($$0 == this.window) {
            this.eventHandler.setWindowActive($$1);
        }
    }

    private void onEnter(long $$0, boolean $$1) {
        if ($$1) {
            this.eventHandler.cursorEntered();
        }
    }

    public void setFramerateLimit(int $$0) {
        this.framerateLimit = $$0;
    }

    public int getFramerateLimit() {
        return this.framerateLimit;
    }

    public void updateDisplay() {
        RenderSystem.flipFrame(this.window);
        if (this.fullscreen != this.actuallyFullscreen) {
            this.actuallyFullscreen = this.fullscreen;
            this.updateFullscreen(this.vsync);
        }
    }

    public Optional<VideoMode> getPreferredFullscreenVideoMode() {
        return this.preferredFullscreenVideoMode;
    }

    public void setPreferredFullscreenVideoMode(Optional<VideoMode> $$0) {
        boolean $$1 = !$$0.equals(this.preferredFullscreenVideoMode);
        this.preferredFullscreenVideoMode = $$0;
        if ($$1) {
            this.dirty = true;
        }
    }

    public void changeFullscreenVideoMode() {
        if (this.fullscreen && this.dirty) {
            this.dirty = false;
            this.setMode();
            this.eventHandler.resizeDisplay();
        }
    }

    private void setMode() {
        boolean $$0;
        RenderSystem.assertInInitPhase();
        boolean bl = $$0 = GLFW.glfwGetWindowMonitor((long)this.window) != 0L;
        if (this.fullscreen) {
            Monitor $$1 = this.screenManager.findBestMonitor(this);
            if ($$1 == null) {
                LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
                this.fullscreen = false;
            } else {
                if (Minecraft.ON_OSX) {
                    MacosUtil.toggleFullscreen(this.window);
                }
                VideoMode $$2 = $$1.getPreferredVidMode(this.preferredFullscreenVideoMode);
                if (!$$0) {
                    this.windowedX = this.x;
                    this.windowedY = this.y;
                    this.windowedWidth = this.width;
                    this.windowedHeight = this.height;
                }
                this.x = 0;
                this.y = 0;
                this.width = $$2.getWidth();
                this.height = $$2.getHeight();
                GLFW.glfwSetWindowMonitor((long)this.window, (long)$$1.getMonitor(), (int)this.x, (int)this.y, (int)this.width, (int)this.height, (int)$$2.getRefreshRate());
            }
        } else {
            this.x = this.windowedX;
            this.y = this.windowedY;
            this.width = this.windowedWidth;
            this.height = this.windowedHeight;
            GLFW.glfwSetWindowMonitor((long)this.window, (long)0L, (int)this.x, (int)this.y, (int)this.width, (int)this.height, (int)-1);
        }
    }

    public void toggleFullScreen() {
        this.fullscreen = !this.fullscreen;
    }

    public void setWindowed(int $$0, int $$1) {
        this.windowedWidth = $$0;
        this.windowedHeight = $$1;
        this.fullscreen = false;
        this.setMode();
    }

    private void updateFullscreen(boolean $$0) {
        RenderSystem.assertOnRenderThread();
        try {
            this.setMode();
            this.eventHandler.resizeDisplay();
            this.updateVsync($$0);
            this.updateDisplay();
        }
        catch (Exception $$1) {
            LOGGER.error("Couldn't toggle fullscreen", (Throwable)$$1);
        }
    }

    public int calculateScale(int $$0, boolean $$1) {
        int $$2;
        for ($$2 = 1; $$2 != $$0 && $$2 < this.framebufferWidth && $$2 < this.framebufferHeight && this.framebufferWidth / ($$2 + 1) >= 320 && this.framebufferHeight / ($$2 + 1) >= 240; ++$$2) {
        }
        if ($$1 && $$2 % 2 != 0) {
            ++$$2;
        }
        return $$2;
    }

    public void setGuiScale(double $$0) {
        this.guiScale = $$0;
        int $$1 = (int)((double)this.framebufferWidth / $$0);
        this.guiScaledWidth = (double)this.framebufferWidth / $$0 > (double)$$1 ? $$1 + 1 : $$1;
        int $$2 = (int)((double)this.framebufferHeight / $$0);
        this.guiScaledHeight = (double)this.framebufferHeight / $$0 > (double)$$2 ? $$2 + 1 : $$2;
    }

    public void setTitle(String $$0) {
        GLFW.glfwSetWindowTitle((long)this.window, (CharSequence)$$0);
    }

    public long getWindow() {
        return this.window;
    }

    public boolean isFullscreen() {
        return this.fullscreen;
    }

    public int getWidth() {
        return this.framebufferWidth;
    }

    public int getHeight() {
        return this.framebufferHeight;
    }

    public void setWidth(int $$0) {
        this.framebufferWidth = $$0;
    }

    public void setHeight(int $$0) {
        this.framebufferHeight = $$0;
    }

    public int getScreenWidth() {
        return this.width;
    }

    public int getScreenHeight() {
        return this.height;
    }

    public int getGuiScaledWidth() {
        return this.guiScaledWidth;
    }

    public int getGuiScaledHeight() {
        return this.guiScaledHeight;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public double getGuiScale() {
        return this.guiScale;
    }

    @Nullable
    public Monitor findBestMonitor() {
        return this.screenManager.findBestMonitor(this);
    }

    public void updateRawMouseInput(boolean $$0) {
        InputConstants.updateRawMouseInput(this.window, $$0);
    }

    public static class WindowInitFailed
    extends SilentInitException {
        WindowInitFailed(String $$0) {
            super($$0);
        }
    }
}
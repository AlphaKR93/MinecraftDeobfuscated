/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.authlib.properties.PropertyMap$Serializer
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.InterruptedException
 *  java.lang.Object
 *  java.lang.Runtime
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Thread$UncaughtExceptionHandler
 *  java.lang.Throwable
 *  java.net.Authenticator
 *  java.net.InetSocketAddress
 *  java.net.PasswordAuthentication
 *  java.net.Proxy
 *  java.net.Proxy$Type
 *  java.net.SocketAddress
 *  java.util.List
 *  java.util.Optional
 *  java.util.OptionalInt
 *  javax.annotation.Nullable
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.NonOptionArgumentSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 *  org.slf4j.Logger
 */
package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.main.SilentInitException;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class Main {
    static final Logger LOGGER = LogUtils.getLogger();

    @DontObfuscate
    public static void main(String[] $$0) {
        Main.run($$0, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    public static void run(String[] $$0, boolean $$1) {
        Object $$69;
        void $$67;
        SharedConstants.tryDetectVersion();
        if ($$1) {
            SharedConstants.enableDataFixerOptimizations();
        }
        OptionParser $$2 = new OptionParser();
        $$2.allowsUnrecognizedOptions();
        $$2.accepts("demo");
        $$2.accepts("disableMultiplayer");
        $$2.accepts("disableChat");
        $$2.accepts("fullscreen");
        $$2.accepts("checkGlErrors");
        OptionSpecBuilder $$3 = $$2.accepts("jfrProfile");
        ArgumentAcceptingOptionSpec $$4 = $$2.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec $$5 = $$2.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo((Object)25565, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec $$6 = $$2.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo((Object)new File("."), (Object[])new File[0]);
        ArgumentAcceptingOptionSpec $$7 = $$2.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec $$8 = $$2.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec $$9 = $$2.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec $$10 = $$2.accepts("proxyPort").withRequiredArg().defaultsTo((Object)"8080", (Object[])new String[0]).ofType(Integer.class);
        ArgumentAcceptingOptionSpec $$11 = $$2.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec $$12 = $$2.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec $$13 = $$2.accepts("username").withRequiredArg().defaultsTo((Object)("Player" + Util.getMillis() % 1000L), (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$14 = $$2.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec $$15 = $$2.accepts("xuid").withOptionalArg().defaultsTo((Object)"", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$16 = $$2.accepts("clientId").withOptionalArg().defaultsTo((Object)"", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$17 = $$2.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec $$18 = $$2.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec $$19 = $$2.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo((Object)854, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec $$20 = $$2.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo((Object)480, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec $$21 = $$2.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec $$22 = $$2.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec $$23 = $$2.accepts("userProperties").withRequiredArg().defaultsTo((Object)"{}", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$24 = $$2.accepts("profileProperties").withRequiredArg().defaultsTo((Object)"{}", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$25 = $$2.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec $$26 = $$2.accepts("userType").withRequiredArg().defaultsTo((Object)User.Type.LEGACY.getName(), (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$27 = $$2.accepts("versionType").withRequiredArg().defaultsTo((Object)"release", (Object[])new String[0]);
        NonOptionArgumentSpec $$28 = $$2.nonOptions();
        OptionSet $$29 = $$2.parse($$0);
        List $$30 = $$29.valuesOf((OptionSpec)$$28);
        if (!$$30.isEmpty()) {
            System.out.println("Completely ignored arguments: " + $$30);
        }
        String $$31 = (String)Main.parseArgument($$29, $$9);
        Proxy $$32 = Proxy.NO_PROXY;
        if ($$31 != null) {
            try {
                $$32 = new Proxy(Proxy.Type.SOCKS, (SocketAddress)new InetSocketAddress($$31, ((Integer)Main.parseArgument($$29, $$10)).intValue()));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        final String $$33 = (String)Main.parseArgument($$29, $$11);
        final String $$34 = (String)Main.parseArgument($$29, $$12);
        if (!$$32.equals((Object)Proxy.NO_PROXY) && Main.stringHasValue($$33) && Main.stringHasValue($$34)) {
            Authenticator.setDefault((Authenticator)new Authenticator(){

                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication($$33, $$34.toCharArray());
                }
            });
        }
        int $$35 = (Integer)Main.parseArgument($$29, $$19);
        int $$36 = (Integer)Main.parseArgument($$29, $$20);
        OptionalInt $$37 = Main.ofNullable((Integer)Main.parseArgument($$29, $$21));
        OptionalInt $$38 = Main.ofNullable((Integer)Main.parseArgument($$29, $$22));
        boolean $$39 = $$29.has("fullscreen");
        boolean $$40 = $$29.has("demo");
        boolean $$41 = $$29.has("disableMultiplayer");
        boolean $$42 = $$29.has("disableChat");
        String $$43 = (String)Main.parseArgument($$29, $$18);
        Gson $$44 = new GsonBuilder().registerTypeAdapter(PropertyMap.class, (Object)new PropertyMap.Serializer()).create();
        PropertyMap $$45 = GsonHelper.fromJson($$44, (String)Main.parseArgument($$29, $$23), PropertyMap.class);
        PropertyMap $$46 = GsonHelper.fromJson($$44, (String)Main.parseArgument($$29, $$24), PropertyMap.class);
        String $$47 = (String)Main.parseArgument($$29, $$27);
        File $$48 = (File)Main.parseArgument($$29, $$6);
        File $$49 = $$29.has((OptionSpec)$$7) ? (File)Main.parseArgument($$29, $$7) : new File($$48, "assets/");
        File $$50 = $$29.has((OptionSpec)$$8) ? (File)Main.parseArgument($$29, $$8) : new File($$48, "resourcepacks/");
        String $$51 = $$29.has((OptionSpec)$$14) ? (String)$$14.value($$29) : UUIDUtil.createOfflinePlayerUUID((String)$$13.value($$29)).toString();
        String $$52 = $$29.has((OptionSpec)$$25) ? (String)$$25.value($$29) : null;
        String $$53 = (String)$$29.valueOf((OptionSpec)$$15);
        String $$54 = (String)$$29.valueOf((OptionSpec)$$16);
        String $$55 = (String)Main.parseArgument($$29, $$4);
        Integer $$56 = (Integer)Main.parseArgument($$29, $$5);
        if ($$29.has((OptionSpec)$$3)) {
            JvmProfiler.INSTANCE.start(Environment.CLIENT);
        }
        CrashReport.preload();
        Bootstrap.bootStrap();
        Bootstrap.validate();
        Util.startTimerHackThread();
        String $$57 = (String)$$26.value($$29);
        User.Type $$58 = User.Type.byName($$57);
        if ($$58 == null) {
            LOGGER.warn("Unrecognized user type: {}", (Object)$$57);
        }
        User $$59 = new User((String)$$13.value($$29), $$51, (String)$$17.value($$29), Main.emptyStringToEmptyOptional($$53), Main.emptyStringToEmptyOptional($$54), $$58);
        GameConfig $$60 = new GameConfig(new GameConfig.UserData($$59, $$45, $$46, $$32), new DisplayData($$35, $$36, $$37, $$38, $$39), new GameConfig.FolderData($$48, $$50, $$49, $$52), new GameConfig.GameData($$40, $$43, $$47, $$41, $$42), new GameConfig.ServerData($$55, $$56));
        Thread $$61 = new Thread("Client Shutdown Thread"){

            public void run() {
                Minecraft $$0 = Minecraft.getInstance();
                if ($$0 == null) {
                    return;
                }
                IntegratedServer $$1 = $$0.getSingleplayerServer();
                if ($$1 != null) {
                    $$1.halt(true);
                }
            }
        };
        $$61.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(LOGGER));
        Runtime.getRuntime().addShutdownHook($$61);
        try {
            Thread.currentThread().setName("Render thread");
            RenderSystem.initRenderThread();
            RenderSystem.beginInitialization();
            Minecraft $$62 = new Minecraft($$60);
            RenderSystem.finishInitialization();
        }
        catch (SilentInitException $$63) {
            LOGGER.warn("Failed to create window: ", (Throwable)((Object)$$63));
            return;
        }
        catch (Throwable $$64) {
            CrashReport $$65 = CrashReport.forThrowable($$64, "Initializing game");
            CrashReportCategory $$66 = $$65.addCategory("Initialization");
            NativeModuleLister.addCrashSection($$66);
            Minecraft.fillReport(null, null, $$60.game.launchVersion, null, $$65);
            Minecraft.crash($$65);
            return;
        }
        if ($$67.renderOnThread()) {
            Thread $$68 = new Thread("Game thread", (Minecraft)$$67){
                final /* synthetic */ Minecraft val$minecraft;
                {
                    this.val$minecraft = minecraft;
                    super($$0);
                }

                public void run() {
                    try {
                        RenderSystem.initGameThread(true);
                        this.val$minecraft.run();
                    }
                    catch (Throwable $$0) {
                        LOGGER.error("Exception in client thread", $$0);
                    }
                }
            };
            $$68.start();
            while ($$67.isRunning()) {
            }
        } else {
            $$69 = null;
            try {
                RenderSystem.initGameThread(false);
                $$67.run();
            }
            catch (Throwable $$70) {
                LOGGER.error("Unhandled game exception", $$70);
            }
        }
        BufferUploader.reset();
        try {
            $$67.stop();
            if ($$69 != null) {
                $$69.join();
            }
        }
        catch (InterruptedException $$71) {
            LOGGER.error("Exception during client thread shutdown", (Throwable)$$71);
        }
        finally {
            $$67.destroy();
        }
    }

    private static Optional<String> emptyStringToEmptyOptional(String $$0) {
        return $$0.isEmpty() ? Optional.empty() : Optional.of((Object)$$0);
    }

    private static OptionalInt ofNullable(@Nullable Integer $$0) {
        return $$0 != null ? OptionalInt.of((int)$$0) : OptionalInt.empty();
    }

    @Nullable
    private static <T> T parseArgument(OptionSet $$0, OptionSpec<T> $$1) {
        try {
            return (T)$$0.valueOf($$1);
        }
        catch (Throwable $$2) {
            ArgumentAcceptingOptionSpec $$3;
            List $$4;
            if ($$1 instanceof ArgumentAcceptingOptionSpec && !($$4 = ($$3 = (ArgumentAcceptingOptionSpec)$$1).defaultValues()).isEmpty()) {
                return (T)$$4.get(0);
            }
            throw $$2;
        }
    }

    private static boolean stringHasValue(@Nullable String $$0) {
        return $$0 != null && !$$0.isEmpty();
    }

    static {
        System.setProperty((String)"java.awt.headless", (String)"true");
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.bridge.Bridge
 *  com.mojang.bridge.game.GameSession
 *  com.mojang.bridge.game.GameVersion
 *  com.mojang.bridge.game.Language
 *  com.mojang.bridge.game.PerformanceMetrics
 *  com.mojang.bridge.game.RunningGame
 *  com.mojang.bridge.launcher.Launcher
 *  com.mojang.bridge.launcher.SessionEventListener
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.client;

import com.mojang.bridge.Bridge;
import com.mojang.bridge.game.GameSession;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.Language;
import com.mojang.bridge.game.PerformanceMetrics;
import com.mojang.bridge.game.RunningGame;
import com.mojang.bridge.launcher.Launcher;
import com.mojang.bridge.launcher.SessionEventListener;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.FrameTimer;

public class Game
implements RunningGame {
    private final Minecraft minecraft;
    @Nullable
    private final Launcher launcher;
    private SessionEventListener listener = SessionEventListener.NONE;

    public Game(Minecraft $$0) {
        this.minecraft = $$0;
        this.launcher = Bridge.getLauncher();
        if (this.launcher != null) {
            this.launcher.registerGame((RunningGame)this);
        }
    }

    public GameVersion getVersion() {
        return SharedConstants.getCurrentVersion();
    }

    public Language getSelectedLanguage() {
        return this.minecraft.getLanguageManager().getSelected();
    }

    @Nullable
    public GameSession getCurrentSession() {
        ClientLevel $$0 = this.minecraft.level;
        return $$0 == null ? null : new Session($$0, this.minecraft.player, this.minecraft.player.connection);
    }

    public PerformanceMetrics getPerformanceMetrics() {
        FrameTimer $$0 = this.minecraft.getFrameTimer();
        long $$1 = Integer.MAX_VALUE;
        long $$2 = Integer.MIN_VALUE;
        long $$3 = 0L;
        for (long $$4 : $$0.getLog()) {
            $$1 = Math.min((long)$$1, (long)$$4);
            $$2 = Math.max((long)$$2, (long)$$4);
            $$3 += $$4;
        }
        return new Metrics((int)$$1, (int)$$2, (int)($$3 / (long)$$0.getLog().length), $$0.getLog().length);
    }

    public void setSessionEventListener(SessionEventListener $$0) {
        this.listener = $$0;
    }

    public void onStartGameSession() {
        this.listener.onStartGameSession(this.getCurrentSession());
    }

    public void onLeaveGameSession() {
        this.listener.onLeaveGameSession(this.getCurrentSession());
    }

    static class Metrics
    implements PerformanceMetrics {
        private final int min;
        private final int max;
        private final int average;
        private final int samples;

        public Metrics(int $$0, int $$1, int $$2, int $$3) {
            this.min = $$0;
            this.max = $$1;
            this.average = $$2;
            this.samples = $$3;
        }

        public int getMinTime() {
            return this.min;
        }

        public int getMaxTime() {
            return this.max;
        }

        public int getAverageTime() {
            return this.average;
        }

        public int getSampleCount() {
            return this.samples;
        }
    }
}
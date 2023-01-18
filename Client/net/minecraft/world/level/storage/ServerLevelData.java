/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Locale
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage;

import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.timers.TimerQueue;

public interface ServerLevelData
extends WritableLevelData {
    public String getLevelName();

    public void setThundering(boolean var1);

    public int getRainTime();

    public void setRainTime(int var1);

    public void setThunderTime(int var1);

    public int getThunderTime();

    @Override
    default public void fillCrashReportCategory(CrashReportCategory $$0, LevelHeightAccessor $$1) {
        WritableLevelData.super.fillCrashReportCategory($$0, $$1);
        $$0.setDetail("Level name", this::getLevelName);
        $$0.setDetail("Level game mode", () -> String.format((Locale)Locale.ROOT, (String)"Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", (Object[])new Object[]{this.getGameType().getName(), this.getGameType().getId(), this.isHardcore(), this.getAllowCommands()}));
        $$0.setDetail("Level weather", () -> String.format((Locale)Locale.ROOT, (String)"Rain time: %d (now: %b), thunder time: %d (now: %b)", (Object[])new Object[]{this.getRainTime(), this.isRaining(), this.getThunderTime(), this.isThundering()}));
    }

    public int getClearWeatherTime();

    public void setClearWeatherTime(int var1);

    public int getWanderingTraderSpawnDelay();

    public void setWanderingTraderSpawnDelay(int var1);

    public int getWanderingTraderSpawnChance();

    public void setWanderingTraderSpawnChance(int var1);

    @Nullable
    public UUID getWanderingTraderId();

    public void setWanderingTraderId(UUID var1);

    public GameType getGameType();

    public void setWorldBorder(WorldBorder.Settings var1);

    public WorldBorder.Settings getWorldBorder();

    public boolean isInitialized();

    public void setInitialized(boolean var1);

    public boolean getAllowCommands();

    public void setGameType(GameType var1);

    public TimerQueue<MinecraftServer> getScheduledEvents();

    public void setGameTime(long var1);

    public void setDayTime(long var1);
}
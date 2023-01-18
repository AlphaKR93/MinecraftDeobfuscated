/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Locale
 */
package net.minecraft.world.level.storage;

import java.util.Locale;
import net.minecraft.CrashReportCategory;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelHeightAccessor;

public interface LevelData {
    public int getXSpawn();

    public int getYSpawn();

    public int getZSpawn();

    public float getSpawnAngle();

    public long getGameTime();

    public long getDayTime();

    public boolean isThundering();

    public boolean isRaining();

    public void setRaining(boolean var1);

    public boolean isHardcore();

    public GameRules getGameRules();

    public Difficulty getDifficulty();

    public boolean isDifficultyLocked();

    default public void fillCrashReportCategory(CrashReportCategory $$0, LevelHeightAccessor $$1) {
        $$0.setDetail("Level spawn location", () -> CrashReportCategory.formatLocation($$1, this.getXSpawn(), this.getYSpawn(), this.getZSpawn()));
        $$0.setDetail("Level time", () -> String.format((Locale)Locale.ROOT, (String)"%d game time, %d day time", (Object[])new Object[]{this.getGameTime(), this.getDayTime()}));
    }
}
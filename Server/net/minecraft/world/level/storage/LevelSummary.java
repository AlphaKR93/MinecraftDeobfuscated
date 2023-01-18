/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.CharSequence
 *  java.lang.Comparable
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.file.Path
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.world.level.storage;

import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelVersion;
import org.apache.commons.lang3.StringUtils;

public class LevelSummary
implements Comparable<LevelSummary> {
    private final LevelSettings settings;
    private final LevelVersion levelVersion;
    private final String levelId;
    private final boolean requiresManualConversion;
    private final boolean locked;
    private final boolean experimental;
    private final Path icon;
    @Nullable
    private Component info;

    public LevelSummary(LevelSettings $$0, LevelVersion $$1, String $$2, boolean $$3, boolean $$4, boolean $$5, Path $$6) {
        this.settings = $$0;
        this.levelVersion = $$1;
        this.levelId = $$2;
        this.locked = $$4;
        this.experimental = $$5;
        this.icon = $$6;
        this.requiresManualConversion = $$3;
    }

    public String getLevelId() {
        return this.levelId;
    }

    public String getLevelName() {
        return StringUtils.isEmpty((CharSequence)this.settings.levelName()) ? this.levelId : this.settings.levelName();
    }

    public Path getIcon() {
        return this.icon;
    }

    public boolean requiresManualConversion() {
        return this.requiresManualConversion;
    }

    public boolean isExperimental() {
        return this.experimental;
    }

    public long getLastPlayed() {
        return this.levelVersion.lastPlayed();
    }

    public int compareTo(LevelSummary $$0) {
        if (this.levelVersion.lastPlayed() < $$0.levelVersion.lastPlayed()) {
            return 1;
        }
        if (this.levelVersion.lastPlayed() > $$0.levelVersion.lastPlayed()) {
            return -1;
        }
        return this.levelId.compareTo($$0.levelId);
    }

    public LevelSettings getSettings() {
        return this.settings;
    }

    public GameType getGameMode() {
        return this.settings.gameType();
    }

    public boolean isHardcore() {
        return this.settings.hardcore();
    }

    public boolean hasCheats() {
        return this.settings.allowCommands();
    }

    public MutableComponent getWorldVersionName() {
        if (StringUtil.isNullOrEmpty(this.levelVersion.minecraftVersionName())) {
            return Component.translatable("selectWorld.versionUnknown");
        }
        return Component.literal(this.levelVersion.minecraftVersionName());
    }

    public LevelVersion levelVersion() {
        return this.levelVersion;
    }

    public boolean markVersionInList() {
        return this.askToOpenWorld() || !SharedConstants.getCurrentVersion().isStable() && !this.levelVersion.snapshot() || this.backupStatus().shouldBackup();
    }

    public boolean askToOpenWorld() {
        return this.levelVersion.minecraftVersion().getVersion() > SharedConstants.getCurrentVersion().getDataVersion().getVersion();
    }

    public BackupStatus backupStatus() {
        WorldVersion $$0 = SharedConstants.getCurrentVersion();
        int $$1 = $$0.getDataVersion().getVersion();
        int $$2 = this.levelVersion.minecraftVersion().getVersion();
        if (!$$0.isStable() && $$2 < $$1) {
            return BackupStatus.UPGRADE_TO_SNAPSHOT;
        }
        if ($$2 > $$1) {
            return BackupStatus.DOWNGRADE;
        }
        return BackupStatus.NONE;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public boolean isDisabled() {
        if (this.isLocked() || this.requiresManualConversion()) {
            return true;
        }
        return !this.isCompatible();
    }

    public boolean isCompatible() {
        return SharedConstants.getCurrentVersion().getDataVersion().isCompatible(this.levelVersion.minecraftVersion());
    }

    public Component getInfo() {
        if (this.info == null) {
            this.info = this.createInfo();
        }
        return this.info;
    }

    private Component createInfo() {
        MutableComponent $$0;
        if (this.isLocked()) {
            return Component.translatable("selectWorld.locked").withStyle(ChatFormatting.RED);
        }
        if (this.requiresManualConversion()) {
            return Component.translatable("selectWorld.conversion").withStyle(ChatFormatting.RED);
        }
        if (!this.isCompatible()) {
            return Component.translatable("selectWorld.incompatible_series").withStyle(ChatFormatting.RED);
        }
        MutableComponent mutableComponent = $$0 = this.isHardcore() ? Component.empty().append(Component.translatable("gameMode.hardcore").withStyle(ChatFormatting.DARK_RED)) : Component.translatable("gameMode." + this.getGameMode().getName());
        if (this.hasCheats()) {
            $$0.append(", ").append(Component.translatable("selectWorld.cheats"));
        }
        if (this.isExperimental()) {
            $$0.append(", ").append(Component.translatable("selectWorld.experimental").withStyle(ChatFormatting.YELLOW));
        }
        MutableComponent $$1 = this.getWorldVersionName();
        MutableComponent $$2 = Component.literal(", ").append(Component.translatable("selectWorld.version")).append(" ");
        if (this.markVersionInList()) {
            $$2.append($$1.withStyle(this.askToOpenWorld() ? ChatFormatting.RED : ChatFormatting.ITALIC));
        } else {
            $$2.append($$1);
        }
        $$0.append($$2);
        return $$0;
    }

    public static enum BackupStatus {
        NONE(false, false, ""),
        DOWNGRADE(true, true, "downgrade"),
        UPGRADE_TO_SNAPSHOT(true, false, "snapshot");

        private final boolean shouldBackup;
        private final boolean severe;
        private final String translationKey;

        private BackupStatus(boolean $$0, boolean $$1, String $$2) {
            this.shouldBackup = $$0;
            this.severe = $$1;
            this.translationKey = $$2;
        }

        public boolean shouldBackup() {
            return this.shouldBackup;
        }

        public boolean isSevere() {
            return this.severe;
        }

        public String getTranslationKey() {
            return this.translationKey;
        }
    }
}
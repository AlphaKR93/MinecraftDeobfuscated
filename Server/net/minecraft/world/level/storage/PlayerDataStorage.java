/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class PlayerDataStorage {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final File playerDir;
    protected final DataFixer fixerUpper;

    public PlayerDataStorage(LevelStorageSource.LevelStorageAccess $$0, DataFixer $$1) {
        this.fixerUpper = $$1;
        this.playerDir = $$0.getLevelPath(LevelResource.PLAYER_DATA_DIR).toFile();
        this.playerDir.mkdirs();
    }

    public void save(Player $$0) {
        try {
            CompoundTag $$1 = $$0.saveWithoutId(new CompoundTag());
            File $$2 = File.createTempFile((String)($$0.getStringUUID() + "-"), (String)".dat", (File)this.playerDir);
            NbtIo.writeCompressed($$1, $$2);
            File $$3 = new File(this.playerDir, $$0.getStringUUID() + ".dat");
            File $$4 = new File(this.playerDir, $$0.getStringUUID() + ".dat_old");
            Util.safeReplaceFile($$3, $$2, $$4);
        }
        catch (Exception $$5) {
            LOGGER.warn("Failed to save player data for {}", (Object)$$0.getName().getString());
        }
    }

    @Nullable
    public CompoundTag load(Player $$0) {
        CompoundTag $$1 = null;
        try {
            File $$2 = new File(this.playerDir, $$0.getStringUUID() + ".dat");
            if ($$2.exists() && $$2.isFile()) {
                $$1 = NbtIo.readCompressed($$2);
            }
        }
        catch (Exception $$3) {
            LOGGER.warn("Failed to load player data for {}", (Object)$$0.getName().getString());
        }
        if ($$1 != null) {
            int $$4 = NbtUtils.getDataVersion($$1, -1);
            $$0.load(DataFixTypes.PLAYER.updateToCurrentVersion(this.fixerUpper, $$1, $$4));
        }
        return $$1;
    }

    public String[] getSeenPlayers() {
        String[] $$0 = this.playerDir.list();
        if ($$0 == null) {
            $$0 = new String[]{};
        }
        for (int $$1 = 0; $$1 < $$0.length; ++$$1) {
            if (!$$0[$$1].endsWith(".dat")) continue;
            $$0[$$1] = $$0[$$1].substring(0, $$0[$$1].length() - 4);
        }
        return $$0;
    }
}
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
 *  java.lang.Throwable
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.File;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import org.slf4j.Logger;

public class HotbarManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int NUM_HOTBAR_GROUPS = 9;
    private final File optionsFile;
    private final DataFixer fixerUpper;
    private final Hotbar[] hotbars = new Hotbar[9];
    private boolean loaded;

    public HotbarManager(File $$0, DataFixer $$1) {
        this.optionsFile = new File($$0, "hotbar.nbt");
        this.fixerUpper = $$1;
        for (int $$2 = 0; $$2 < 9; ++$$2) {
            this.hotbars[$$2] = new Hotbar();
        }
    }

    private void load() {
        try {
            CompoundTag $$0 = NbtIo.read(this.optionsFile);
            if ($$0 == null) {
                return;
            }
            int $$1 = NbtUtils.getDataVersion($$0, 1343);
            $$0 = DataFixTypes.HOTBAR.updateToCurrentVersion(this.fixerUpper, $$0, $$1);
            for (int $$2 = 0; $$2 < 9; ++$$2) {
                this.hotbars[$$2].fromTag($$0.getList(String.valueOf((int)$$2), 10));
            }
        }
        catch (Exception $$3) {
            LOGGER.error("Failed to load creative mode options", (Throwable)$$3);
        }
    }

    public void save() {
        try {
            CompoundTag $$0 = NbtUtils.addCurrentDataVersion(new CompoundTag());
            for (int $$1 = 0; $$1 < 9; ++$$1) {
                $$0.put(String.valueOf((int)$$1), this.get($$1).createTag());
            }
            NbtIo.write($$0, this.optionsFile);
        }
        catch (Exception $$2) {
            LOGGER.error("Failed to save creative mode options", (Throwable)$$2);
        }
    }

    public Hotbar get(int $$0) {
        if (!this.loaded) {
            this.load();
            this.loaded = true;
        }
        return this.hotbars[$$0];
    }
}
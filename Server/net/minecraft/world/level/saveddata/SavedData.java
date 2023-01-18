/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.io.IOException
 *  java.lang.Object
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.saveddata;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import org.slf4j.Logger;

public abstract class SavedData {
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean dirty;

    public abstract CompoundTag save(CompoundTag var1);

    public void setDirty() {
        this.setDirty(true);
    }

    public void setDirty(boolean $$0) {
        this.dirty = $$0;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void save(File $$0) {
        if (!this.isDirty()) {
            return;
        }
        CompoundTag $$1 = new CompoundTag();
        $$1.put("data", this.save(new CompoundTag()));
        NbtUtils.addCurrentDataVersion($$1);
        try {
            NbtIo.writeCompressed($$1, $$0);
        }
        catch (IOException $$2) {
            LOGGER.error("Could not save data {}", (Object)this, (Object)$$2);
        }
        this.setDirty(false);
    }
}
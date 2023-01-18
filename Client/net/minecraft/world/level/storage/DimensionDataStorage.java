/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  java.io.DataInput
 *  java.io.DataInputStream
 *  java.io.File
 *  java.io.FileInputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.PushbackInputStream
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

public class DimensionDataStorage {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, SavedData> cache = Maps.newHashMap();
    private final DataFixer fixerUpper;
    private final File dataFolder;

    public DimensionDataStorage(File $$0, DataFixer $$1) {
        this.fixerUpper = $$1;
        this.dataFolder = $$0;
    }

    private File getDataFile(String $$0) {
        return new File(this.dataFolder, $$0 + ".dat");
    }

    public <T extends SavedData> T computeIfAbsent(Function<CompoundTag, T> $$0, Supplier<T> $$1, String $$2) {
        T $$3 = this.get($$0, $$2);
        if ($$3 != null) {
            return $$3;
        }
        SavedData $$4 = (SavedData)$$1.get();
        this.set($$2, $$4);
        return (T)$$4;
    }

    @Nullable
    public <T extends SavedData> T get(Function<CompoundTag, T> $$0, String $$1) {
        SavedData $$2 = (SavedData)this.cache.get((Object)$$1);
        if ($$2 == null && !this.cache.containsKey((Object)$$1)) {
            $$2 = this.readSavedData($$0, $$1);
            this.cache.put((Object)$$1, (Object)$$2);
        }
        return (T)$$2;
    }

    @Nullable
    private <T extends SavedData> T readSavedData(Function<CompoundTag, T> $$0, String $$1) {
        try {
            File $$2 = this.getDataFile($$1);
            if ($$2.exists()) {
                CompoundTag $$3 = this.readTagFromDisk($$1, SharedConstants.getCurrentVersion().getWorldVersion());
                return (T)((SavedData)$$0.apply((Object)$$3.getCompound("data")));
            }
        }
        catch (Exception $$4) {
            LOGGER.error("Error loading saved data: {}", (Object)$$1, (Object)$$4);
        }
        return null;
    }

    public void set(String $$0, SavedData $$1) {
        this.cache.put((Object)$$0, (Object)$$1);
    }

    /*
     * WARNING - void declaration
     */
    public CompoundTag readTagFromDisk(String $$0, int $$1) throws IOException {
        File $$2 = this.getDataFile($$0);
        try (FileInputStream $$3 = new FileInputStream($$2);){
            CompoundTag compoundTag;
            try (PushbackInputStream $$4 = new PushbackInputStream((InputStream)$$3, 2);){
                void $$8;
                if (this.isGzip($$4)) {
                    CompoundTag $$5 = NbtIo.readCompressed((InputStream)$$4);
                } else {
                    try (DataInputStream $$6 = new DataInputStream((InputStream)$$4);){
                        CompoundTag $$7 = NbtIo.read((DataInput)$$6);
                    }
                }
                int $$9 = $$8.contains("DataVersion", 99) ? $$8.getInt("DataVersion") : 1343;
                compoundTag = NbtUtils.update(this.fixerUpper, DataFixTypes.SAVED_DATA, (CompoundTag)$$8, $$9, $$1);
            }
            return compoundTag;
        }
    }

    private boolean isGzip(PushbackInputStream $$0) throws IOException {
        int $$4;
        byte[] $$1 = new byte[2];
        boolean $$2 = false;
        int $$3 = $$0.read($$1, 0, 2);
        if ($$3 == 2 && ($$4 = ($$1[1] & 0xFF) << 8 | $$1[0] & 0xFF) == 35615) {
            $$2 = true;
        }
        if ($$3 != 0) {
            $$0.unread($$1, 0, $$3);
        }
        return $$2;
    }

    public void save() {
        this.cache.forEach(($$0, $$1) -> {
            if ($$1 != null) {
                $$1.save(this.getDataFile((String)$$0));
            }
        });
    }
}
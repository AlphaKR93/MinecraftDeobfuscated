/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Map
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class CommandStorage {
    private static final String ID_PREFIX = "command_storage_";
    private final Map<String, Container> namespaces = Maps.newHashMap();
    private final DimensionDataStorage storage;

    public CommandStorage(DimensionDataStorage $$0) {
        this.storage = $$0;
    }

    private Container newStorage(String $$0) {
        Container $$1 = new Container();
        this.namespaces.put((Object)$$0, (Object)$$1);
        return $$1;
    }

    public CompoundTag get(ResourceLocation $$0) {
        String $$12 = $$0.getNamespace();
        Container $$2 = (Container)this.storage.get($$1 -> this.newStorage($$12).load((CompoundTag)$$1), CommandStorage.createId($$12));
        return $$2 != null ? $$2.get($$0.getPath()) : new CompoundTag();
    }

    public void set(ResourceLocation $$0, CompoundTag $$12) {
        String $$2 = $$0.getNamespace();
        ((Container)this.storage.computeIfAbsent($$1 -> this.newStorage($$2).load((CompoundTag)$$1), () -> this.newStorage($$2), CommandStorage.createId($$2))).put($$0.getPath(), $$12);
    }

    public Stream<ResourceLocation> keys() {
        return this.namespaces.entrySet().stream().flatMap($$0 -> ((Container)$$0.getValue()).getKeys((String)$$0.getKey()));
    }

    private static String createId(String $$0) {
        return ID_PREFIX + $$0;
    }

    static class Container
    extends SavedData {
        private static final String TAG_CONTENTS = "contents";
        private final Map<String, CompoundTag> storage = Maps.newHashMap();

        Container() {
        }

        Container load(CompoundTag $$0) {
            CompoundTag $$1 = $$0.getCompound(TAG_CONTENTS);
            for (String $$2 : $$1.getAllKeys()) {
                this.storage.put((Object)$$2, (Object)$$1.getCompound($$2));
            }
            return this;
        }

        @Override
        public CompoundTag save(CompoundTag $$0) {
            CompoundTag $$12 = new CompoundTag();
            this.storage.forEach(($$1, $$2) -> $$12.put((String)$$1, $$2.copy()));
            $$0.put(TAG_CONTENTS, $$12);
            return $$0;
        }

        public CompoundTag get(String $$0) {
            CompoundTag $$1 = (CompoundTag)this.storage.get((Object)$$0);
            return $$1 != null ? $$1 : new CompoundTag();
        }

        public void put(String $$0, CompoundTag $$1) {
            if ($$1.isEmpty()) {
                this.storage.remove((Object)$$0);
            } else {
                this.storage.put((Object)$$0, (Object)$$1);
            }
            this.setDirty();
        }

        public Stream<ResourceLocation> getKeys(String $$0) {
            return this.storage.keySet().stream().map($$1 -> new ResourceLocation($$0, (String)$$1));
        }
    }
}
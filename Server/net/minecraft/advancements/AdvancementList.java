/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.Iterator
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 *  java.util.function.Function
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class AdvancementList {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<ResourceLocation, Advancement> advancements = Maps.newHashMap();
    private final Set<Advancement> roots = Sets.newLinkedHashSet();
    private final Set<Advancement> tasks = Sets.newLinkedHashSet();
    @Nullable
    private Listener listener;

    private void remove(Advancement $$0) {
        for (Advancement $$1 : $$0.getChildren()) {
            this.remove($$1);
        }
        LOGGER.info("Forgot about advancement {}", (Object)$$0.getId());
        this.advancements.remove((Object)$$0.getId());
        if ($$0.getParent() == null) {
            this.roots.remove((Object)$$0);
            if (this.listener != null) {
                this.listener.onRemoveAdvancementRoot($$0);
            }
        } else {
            this.tasks.remove((Object)$$0);
            if (this.listener != null) {
                this.listener.onRemoveAdvancementTask($$0);
            }
        }
    }

    public void remove(Set<ResourceLocation> $$0) {
        for (ResourceLocation $$1 : $$0) {
            Advancement $$2 = (Advancement)this.advancements.get((Object)$$1);
            if ($$2 == null) {
                LOGGER.warn("Told to remove advancement {} but I don't know what that is", (Object)$$1);
                continue;
            }
            this.remove($$2);
        }
    }

    public void add(Map<ResourceLocation, Advancement.Builder> $$0) {
        HashMap $$1 = Maps.newHashMap($$0);
        while (!$$1.isEmpty()) {
            boolean $$2 = false;
            Iterator $$3 = $$1.entrySet().iterator();
            while ($$3.hasNext()) {
                Map.Entry $$4 = (Map.Entry)$$3.next();
                ResourceLocation $$5 = (ResourceLocation)$$4.getKey();
                Advancement.Builder $$6 = (Advancement.Builder)$$4.getValue();
                if (!$$6.canBuild((Function<ResourceLocation, Advancement>)((Function)arg_0 -> this.advancements.get(arg_0)))) continue;
                Advancement $$7 = $$6.build($$5);
                this.advancements.put((Object)$$5, (Object)$$7);
                $$2 = true;
                $$3.remove();
                if ($$7.getParent() == null) {
                    this.roots.add((Object)$$7);
                    if (this.listener == null) continue;
                    this.listener.onAddAdvancementRoot($$7);
                    continue;
                }
                this.tasks.add((Object)$$7);
                if (this.listener == null) continue;
                this.listener.onAddAdvancementTask($$7);
            }
            if ($$2) continue;
            for (Map.Entry $$8 : $$1.entrySet()) {
                LOGGER.error("Couldn't load advancement {}: {}", $$8.getKey(), $$8.getValue());
            }
        }
        LOGGER.info("Loaded {} advancements", (Object)this.advancements.size());
    }

    public void clear() {
        this.advancements.clear();
        this.roots.clear();
        this.tasks.clear();
        if (this.listener != null) {
            this.listener.onAdvancementsCleared();
        }
    }

    public Iterable<Advancement> getRoots() {
        return this.roots;
    }

    public Collection<Advancement> getAllAdvancements() {
        return this.advancements.values();
    }

    @Nullable
    public Advancement get(ResourceLocation $$0) {
        return (Advancement)this.advancements.get((Object)$$0);
    }

    public void setListener(@Nullable Listener $$0) {
        this.listener = $$0;
        if ($$0 != null) {
            for (Advancement $$1 : this.roots) {
                $$0.onAddAdvancementRoot($$1);
            }
            for (Advancement $$2 : this.tasks) {
                $$0.onAddAdvancementTask($$2);
            }
        }
    }

    public static interface Listener {
        public void onAddAdvancementRoot(Advancement var1);

        public void onRemoveAdvancementRoot(Advancement var1);

        public void onAddAdvancementTask(Advancement var1);

        public void onRemoveAdvancementTask(Advancement var1);

        public void onAdvancementsCleared();
    }
}
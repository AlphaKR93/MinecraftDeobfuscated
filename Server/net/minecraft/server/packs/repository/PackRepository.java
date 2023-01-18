/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Functions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.List
 *  java.util.Map
 *  java.util.Objects
 *  java.util.Set
 *  java.util.TreeMap
 *  java.util.function.Consumer
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.server.packs.repository;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;

public class PackRepository {
    private final Set<RepositorySource> sources;
    private Map<String, Pack> available = ImmutableMap.of();
    private List<Pack> selected = ImmutableList.of();

    public PackRepository(RepositorySource ... $$0) {
        this.sources = ImmutableSet.copyOf((Object[])$$0);
    }

    public void reload() {
        List $$0 = (List)this.selected.stream().map(Pack::getId).collect(ImmutableList.toImmutableList());
        this.available = this.discoverAvailable();
        this.selected = this.rebuildSelected((Collection<String>)$$0);
    }

    private Map<String, Pack> discoverAvailable() {
        TreeMap $$0 = Maps.newTreeMap();
        for (RepositorySource $$1 : this.sources) {
            $$1.loadPacks((Consumer<Pack>)((Consumer)arg_0 -> PackRepository.lambda$discoverAvailable$0((Map)$$0, arg_0)));
        }
        return ImmutableMap.copyOf((Map)$$0);
    }

    public void setSelected(Collection<String> $$0) {
        this.selected = this.rebuildSelected($$0);
    }

    private List<Pack> rebuildSelected(Collection<String> $$0) {
        List $$1 = (List)this.getAvailablePacks($$0).collect(Collectors.toList());
        for (Pack $$2 : this.available.values()) {
            if (!$$2.isRequired() || $$1.contains((Object)$$2)) continue;
            $$2.getDefaultPosition().insert($$1, $$2, Functions.identity(), false);
        }
        return ImmutableList.copyOf((Collection)$$1);
    }

    private Stream<Pack> getAvailablePacks(Collection<String> $$0) {
        return $$0.stream().map(arg_0 -> this.available.get(arg_0)).filter(Objects::nonNull);
    }

    public Collection<String> getAvailableIds() {
        return this.available.keySet();
    }

    public Collection<Pack> getAvailablePacks() {
        return this.available.values();
    }

    public Collection<String> getSelectedIds() {
        return (Collection)this.selected.stream().map(Pack::getId).collect(ImmutableSet.toImmutableSet());
    }

    public FeatureFlagSet getRequestedFeatureFlags() {
        return (FeatureFlagSet)this.getSelectedPacks().stream().map(Pack::getRequestedFeatures).reduce(FeatureFlagSet::join).orElse((Object)FeatureFlagSet.of());
    }

    public Collection<Pack> getSelectedPacks() {
        return this.selected;
    }

    @Nullable
    public Pack getPack(String $$0) {
        return (Pack)this.available.get((Object)$$0);
    }

    public boolean isAvailable(String $$0) {
        return this.available.containsKey((Object)$$0);
    }

    public List<PackResources> openAllSelected() {
        return (List)this.selected.stream().map(Pack::open).collect(ImmutableList.toImmutableList());
    }

    private static /* synthetic */ void lambda$discoverAvailable$0(Map $$0, Pack $$1) {
        $$0.put((Object)$$1.getId(), (Object)$$1);
    }
}
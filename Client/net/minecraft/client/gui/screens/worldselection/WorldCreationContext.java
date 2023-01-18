/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.util.function.BiFunction
 *  java.util.function.UnaryOperator
 */
package net.minecraft.client.gui.screens.worldselection;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;

public record WorldCreationContext(WorldOptions options, Registry<LevelStem> datapackDimensions, WorldDimensions selectedDimensions, LayeredRegistryAccess<RegistryLayer> worldgenRegistries, ReloadableServerResources dataPackResources, WorldDataConfiguration dataConfiguration) {
    public WorldCreationContext(WorldGenSettings $$0, LayeredRegistryAccess<RegistryLayer> $$1, ReloadableServerResources $$2, WorldDataConfiguration $$3) {
        this($$0.options(), $$0.dimensions(), $$1, $$2, $$3);
    }

    public WorldCreationContext(WorldOptions $$0, WorldDimensions $$1, LayeredRegistryAccess<RegistryLayer> $$2, ReloadableServerResources $$3, WorldDataConfiguration $$4) {
        this($$0, $$2.getLayer(RegistryLayer.DIMENSIONS).registryOrThrow(Registries.LEVEL_STEM), $$1, $$2.replaceFrom(RegistryLayer.DIMENSIONS, new RegistryAccess.Frozen[0]), $$3, $$4);
    }

    public WorldCreationContext withSettings(WorldOptions $$0, WorldDimensions $$1) {
        return new WorldCreationContext($$0, this.datapackDimensions, $$1, this.worldgenRegistries, this.dataPackResources, this.dataConfiguration);
    }

    public WorldCreationContext withOptions(OptionsModifier $$0) {
        return new WorldCreationContext((WorldOptions)$$0.apply(this.options), this.datapackDimensions, this.selectedDimensions, this.worldgenRegistries, this.dataPackResources, this.dataConfiguration);
    }

    public WorldCreationContext withDimensions(DimensionsUpdater $$0) {
        return new WorldCreationContext(this.options, this.datapackDimensions, (WorldDimensions)((Object)$$0.apply(this.worldgenLoadContext(), (Object)this.selectedDimensions)), this.worldgenRegistries, this.dataPackResources, this.dataConfiguration);
    }

    public RegistryAccess.Frozen worldgenLoadContext() {
        return this.worldgenRegistries.compositeAccess();
    }

    public static interface OptionsModifier
    extends UnaryOperator<WorldOptions> {
    }

    @FunctionalInterface
    public static interface DimensionsUpdater
    extends BiFunction<RegistryAccess.Frozen, WorldDimensions, WorldDimensions> {
    }
}
/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.Path
 *  javax.annotation.Nullable
 */
package net.minecraft.server.packs.repository;

import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;

public class ServerPacksSource
extends BuiltInPackSource {
    private static final PackMetadataSection VERSION_METADATA_SECTION = new PackMetadataSection(Component.translatable("dataPack.vanilla.description"), PackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()));
    private static final FeatureFlagsMetadataSection FEATURE_FLAGS_METADATA_SECTION = new FeatureFlagsMetadataSection(FeatureFlags.DEFAULT_FLAGS);
    private static final BuiltInMetadata BUILT_IN_METADATA = BuiltInMetadata.of(PackMetadataSection.TYPE, VERSION_METADATA_SECTION, FeatureFlagsMetadataSection.TYPE, FEATURE_FLAGS_METADATA_SECTION);
    private static final Component VANILLA_NAME = Component.translatable("dataPack.vanilla.name");
    private static final ResourceLocation PACKS_DIR = new ResourceLocation("minecraft", "datapacks");

    public ServerPacksSource() {
        super(PackType.SERVER_DATA, ServerPacksSource.createVanillaPackSource(), PACKS_DIR);
    }

    private static VanillaPackResources createVanillaPackSource() {
        return new VanillaPackResourcesBuilder().setMetadata(BUILT_IN_METADATA).exposeNamespace("minecraft").applyDevelopmentConfig().pushJarResources().build();
    }

    @Override
    protected Component getPackTitle(String $$0) {
        return Component.literal($$0);
    }

    @Override
    @Nullable
    protected Pack createVanillaPack(PackResources $$0) {
        return Pack.readMetaAndCreate("vanilla", VANILLA_NAME, false, $$1 -> $$0, PackType.SERVER_DATA, Pack.Position.BOTTOM, PackSource.BUILT_IN);
    }

    @Override
    @Nullable
    protected Pack createBuiltinPack(String $$0, Pack.ResourcesSupplier $$1, Component $$2) {
        return Pack.readMetaAndCreate($$0, $$2, false, $$1, PackType.SERVER_DATA, Pack.Position.TOP, PackSource.FEATURE);
    }

    public static PackRepository createPackRepository(Path $$0) {
        return new PackRepository(new ServerPacksSource(), new FolderRepositorySource($$0, PackType.SERVER_DATA, PackSource.WORLD));
    }

    public static PackRepository createPackRepository(LevelStorageSource.LevelStorageAccess $$0) {
        return ServerPacksSource.createPackRepository($$0.getLevelPath(LevelResource.DATAPACK_DIR));
    }
}
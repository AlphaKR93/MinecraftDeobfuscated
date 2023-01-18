/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.FileSystems
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.Path
 *  java.util.Map
 *  java.util.function.BiConsumer
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

public class ClientPackSource
extends BuiltInPackSource {
    private static final PackMetadataSection VERSION_METADATA_SECTION = new PackMetadataSection(Component.translatable("resourcePack.vanilla.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES));
    private static final BuiltInMetadata BUILT_IN_METADATA = BuiltInMetadata.of(PackMetadataSection.TYPE, VERSION_METADATA_SECTION);
    private static final Component VANILLA_NAME = Component.translatable("resourcePack.vanilla.name");
    private static final Map<String, Component> SPECIAL_PACK_NAMES = Map.of((Object)"programmer_art", (Object)Component.translatable("resourcePack.programmer_art.name"));
    private static final ResourceLocation PACKS_DIR = new ResourceLocation("minecraft", "resourcepacks");
    @Nullable
    private final Path externalAssetDir;

    public ClientPackSource(Path $$0) {
        super(PackType.CLIENT_RESOURCES, ClientPackSource.createVanillaPackSource($$0), PACKS_DIR);
        this.externalAssetDir = this.findExplodedAssetPacks($$0);
    }

    @Nullable
    private Path findExplodedAssetPacks(Path $$0) {
        Path $$1;
        if (SharedConstants.IS_RUNNING_IN_IDE && $$0.getFileSystem() == FileSystems.getDefault() && Files.isDirectory((Path)($$1 = $$0.getParent().resolve("resourcepacks")), (LinkOption[])new LinkOption[0])) {
            return $$1;
        }
        return null;
    }

    private static VanillaPackResources createVanillaPackSource(Path $$0) {
        return new VanillaPackResourcesBuilder().setMetadata(BUILT_IN_METADATA).exposeNamespace("minecraft", "realms").applyDevelopmentConfig().pushJarResources().pushAssetPath(PackType.CLIENT_RESOURCES, $$0).build();
    }

    @Override
    protected Component getPackTitle(String $$0) {
        Component $$1 = (Component)SPECIAL_PACK_NAMES.get((Object)$$0);
        return $$1 != null ? $$1 : Component.literal($$0);
    }

    @Override
    @Nullable
    protected Pack createVanillaPack(PackResources $$0) {
        return Pack.readMetaAndCreate("vanilla", VANILLA_NAME, true, $$1 -> $$0, PackType.CLIENT_RESOURCES, Pack.Position.BOTTOM, PackSource.BUILT_IN);
    }

    @Override
    @Nullable
    protected Pack createBuiltinPack(String $$0, Pack.ResourcesSupplier $$1, Component $$2) {
        return Pack.readMetaAndCreate($$0, $$2, false, $$1, PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
    }

    @Override
    protected void populatePackList(BiConsumer<String, Function<String, Pack>> $$0) {
        super.populatePackList($$0);
        if (this.externalAssetDir != null) {
            this.discoverPacksInPath(this.externalAssetDir, $$0);
        }
    }
}
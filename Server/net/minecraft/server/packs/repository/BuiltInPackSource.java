/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.Path
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.function.BiConsumer
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 */
package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public abstract class BuiltInPackSource
implements RepositorySource {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String VANILLA_ID = "vanilla";
    private final PackType packType;
    private final VanillaPackResources vanillaPack;
    private final ResourceLocation packDir;

    public BuiltInPackSource(PackType $$0, VanillaPackResources $$1, ResourceLocation $$2) {
        this.packType = $$0;
        this.vanillaPack = $$1;
        this.packDir = $$2;
    }

    @Override
    public void loadPacks(Consumer<Pack> $$0) {
        Pack $$1 = this.createVanillaPack(this.vanillaPack);
        if ($$1 != null) {
            $$0.accept((Object)$$1);
        }
        this.listBundledPacks($$0);
    }

    @Nullable
    protected abstract Pack createVanillaPack(PackResources var1);

    protected abstract Component getPackTitle(String var1);

    public VanillaPackResources getVanillaPack() {
        return this.vanillaPack;
    }

    private void listBundledPacks(Consumer<Pack> $$0) {
        HashMap $$12 = new HashMap();
        this.populatePackList((BiConsumer<String, Function<String, Pack>>)((BiConsumer)(arg_0, arg_1) -> ((Map)$$12).put(arg_0, arg_1)));
        $$12.forEach(($$1, $$2) -> {
            Pack $$3 = (Pack)$$2.apply($$1);
            if ($$3 != null) {
                $$0.accept((Object)$$3);
            }
        });
    }

    protected void populatePackList(BiConsumer<String, Function<String, Pack>> $$0) {
        this.vanillaPack.listRawPaths(this.packType, this.packDir, (Consumer<Path>)((Consumer)$$1 -> this.discoverPacksInPath((Path)$$1, $$0)));
    }

    protected void discoverPacksInPath(@Nullable Path $$0, BiConsumer<String, Function<String, Pack>> $$1) {
        if ($$0 != null && Files.isDirectory((Path)$$0, (LinkOption[])new LinkOption[0])) {
            try {
                FolderRepositorySource.discoverPacks($$0, true, (BiConsumer<Path, Pack.ResourcesSupplier>)((BiConsumer)($$12, $$2) -> $$1.accept((Object)BuiltInPackSource.pathToId($$12), $$1 -> this.createBuiltinPack((String)$$1, (Pack.ResourcesSupplier)$$2, this.getPackTitle((String)$$1)))));
            }
            catch (IOException $$22) {
                LOGGER.warn("Failed to discover packs in {}", (Object)$$0, (Object)$$22);
            }
        }
    }

    private static String pathToId(Path $$0) {
        return StringUtils.removeEnd((String)$$0.getFileName().toString(), (String)".zip");
    }

    @Nullable
    protected abstract Pack createBuiltinPack(String var1, Pack.ResourcesSupplier var2, Component var3);
}
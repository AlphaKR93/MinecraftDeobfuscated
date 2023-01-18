/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedReader
 *  java.io.File
 *  java.io.FileInputStream
 *  java.io.FileNotFoundException
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.io.Reader
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.file.FileVisitOption
 *  java.nio.file.Files
 *  java.nio.file.InvalidPathException
 *  java.nio.file.LinkOption
 *  java.nio.file.NoSuchFileException
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.nio.file.attribute.FileAttribute
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  java.util.stream.Stream
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.FileUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureTemplateManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String STRUCTURE_DIRECTORY_NAME = "structures";
    private static final String TEST_STRUCTURES_DIR = "gameteststructures";
    private static final String STRUCTURE_FILE_EXTENSION = ".nbt";
    private static final String STRUCTURE_TEXT_FILE_EXTENSION = ".snbt";
    private final Map<ResourceLocation, Optional<StructureTemplate>> structureRepository = Maps.newConcurrentMap();
    private final DataFixer fixerUpper;
    private ResourceManager resourceManager;
    private final Path generatedDir;
    private final List<Source> sources;
    private final HolderGetter<Block> blockLookup;
    private static final FileToIdConverter LISTER = new FileToIdConverter("structures", ".nbt");

    public StructureTemplateManager(ResourceManager $$0, LevelStorageSource.LevelStorageAccess $$1, DataFixer $$2, HolderGetter<Block> $$3) {
        this.resourceManager = $$0;
        this.fixerUpper = $$2;
        this.generatedDir = $$1.getLevelPath(LevelResource.GENERATED_DIR).normalize();
        this.blockLookup = $$3;
        ImmutableList.Builder $$4 = ImmutableList.builder();
        $$4.add((Object)new Source((Function<ResourceLocation, Optional<StructureTemplate>>)((Function)this::loadFromGenerated), (Supplier<Stream<ResourceLocation>>)((Supplier)this::listGenerated)));
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            $$4.add((Object)new Source((Function<ResourceLocation, Optional<StructureTemplate>>)((Function)this::loadFromTestStructures), (Supplier<Stream<ResourceLocation>>)((Supplier)this::listTestStructures)));
        }
        $$4.add((Object)new Source((Function<ResourceLocation, Optional<StructureTemplate>>)((Function)this::loadFromResource), (Supplier<Stream<ResourceLocation>>)((Supplier)this::listResources)));
        this.sources = $$4.build();
    }

    public StructureTemplate getOrCreate(ResourceLocation $$0) {
        Optional<StructureTemplate> $$1 = this.get($$0);
        if ($$1.isPresent()) {
            return (StructureTemplate)$$1.get();
        }
        StructureTemplate $$2 = new StructureTemplate();
        this.structureRepository.put((Object)$$0, (Object)Optional.of((Object)$$2));
        return $$2;
    }

    public Optional<StructureTemplate> get(ResourceLocation $$0) {
        return (Optional)this.structureRepository.computeIfAbsent((Object)$$0, this::tryLoad);
    }

    public Stream<ResourceLocation> listTemplates() {
        return this.sources.stream().flatMap($$0 -> (Stream)$$0.lister().get()).distinct();
    }

    private Optional<StructureTemplate> tryLoad(ResourceLocation $$0) {
        for (Source $$1 : this.sources) {
            try {
                Optional $$2 = (Optional)$$1.loader().apply((Object)$$0);
                if (!$$2.isPresent()) continue;
                return $$2;
            }
            catch (Exception exception) {
            }
        }
        return Optional.empty();
    }

    public void onResourceManagerReload(ResourceManager $$0) {
        this.resourceManager = $$0;
        this.structureRepository.clear();
    }

    private Optional<StructureTemplate> loadFromResource(ResourceLocation $$0) {
        ResourceLocation $$12 = LISTER.idToFile($$0);
        return this.load(() -> this.resourceManager.open($$12), (Consumer<Throwable>)((Consumer)$$1 -> LOGGER.error("Couldn't load structure {}", (Object)$$0, $$1)));
    }

    private Stream<ResourceLocation> listResources() {
        return LISTER.listMatchingResources(this.resourceManager).keySet().stream().map(LISTER::fileToId);
    }

    private Optional<StructureTemplate> loadFromTestStructures(ResourceLocation $$0) {
        return this.loadFromSnbt($$0, Paths.get((String)TEST_STRUCTURES_DIR, (String[])new String[0]));
    }

    private Stream<ResourceLocation> listTestStructures() {
        return this.listFolderContents(Paths.get((String)TEST_STRUCTURES_DIR, (String[])new String[0]), "minecraft", STRUCTURE_TEXT_FILE_EXTENSION);
    }

    private Optional<StructureTemplate> loadFromGenerated(ResourceLocation $$0) {
        if (!Files.isDirectory((Path)this.generatedDir, (LinkOption[])new LinkOption[0])) {
            return Optional.empty();
        }
        Path $$12 = StructureTemplateManager.createAndValidatePathToStructure(this.generatedDir, $$0, STRUCTURE_FILE_EXTENSION);
        return this.load(() -> new FileInputStream($$12.toFile()), (Consumer<Throwable>)((Consumer)$$1 -> LOGGER.error("Couldn't load structure from {}", (Object)$$12, $$1)));
    }

    private Stream<ResourceLocation> listGenerated() {
        if (!Files.isDirectory((Path)this.generatedDir, (LinkOption[])new LinkOption[0])) {
            return Stream.empty();
        }
        try {
            return Files.list((Path)this.generatedDir).filter($$0 -> Files.isDirectory((Path)$$0, (LinkOption[])new LinkOption[0])).flatMap($$0 -> this.listGeneratedInNamespace((Path)$$0));
        }
        catch (IOException $$02) {
            return Stream.empty();
        }
    }

    private Stream<ResourceLocation> listGeneratedInNamespace(Path $$0) {
        Path $$1 = $$0.resolve(STRUCTURE_DIRECTORY_NAME);
        return this.listFolderContents($$1, $$0.getFileName().toString(), STRUCTURE_FILE_EXTENSION);
    }

    private Stream<ResourceLocation> listFolderContents(Path $$0, String $$12, String $$2) {
        if (!Files.isDirectory((Path)$$0, (LinkOption[])new LinkOption[0])) {
            return Stream.empty();
        }
        int $$32 = $$2.length();
        Function $$42 = $$1 -> $$1.substring(0, $$1.length() - $$32);
        try {
            return Files.walk((Path)$$0, (FileVisitOption[])new FileVisitOption[0]).filter($$1 -> $$1.toString().endsWith($$2)).mapMulti(($$3, $$4) -> {
                try {
                    $$4.accept((Object)new ResourceLocation($$12, (String)$$42.apply((Object)this.relativize($$0, (Path)$$3))));
                }
                catch (ResourceLocationException $$5) {
                    LOGGER.error("Invalid location while listing pack contents", (Throwable)$$5);
                }
            });
        }
        catch (IOException $$5) {
            LOGGER.error("Failed to list folder contents", (Throwable)$$5);
            return Stream.empty();
        }
    }

    private String relativize(Path $$0, Path $$1) {
        return $$0.relativize($$1).toString().replace((CharSequence)File.separator, (CharSequence)"/");
    }

    private Optional<StructureTemplate> loadFromSnbt(ResourceLocation $$0, Path $$1) {
        Optional optional;
        block10: {
            if (!Files.isDirectory((Path)$$1, (LinkOption[])new LinkOption[0])) {
                return Optional.empty();
            }
            Path $$2 = FileUtil.createPathToResource($$1, $$0.getPath(), STRUCTURE_TEXT_FILE_EXTENSION);
            BufferedReader $$3 = Files.newBufferedReader((Path)$$2);
            try {
                String $$4 = IOUtils.toString((Reader)$$3);
                optional = Optional.of((Object)this.readStructure(NbtUtils.snbtToStructure($$4)));
                if ($$3 == null) break block10;
            }
            catch (Throwable throwable) {
                try {
                    if ($$3 != null) {
                        try {
                            $$3.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (NoSuchFileException $$5) {
                    return Optional.empty();
                }
                catch (CommandSyntaxException | IOException $$6) {
                    LOGGER.error("Couldn't load structure from {}", (Object)$$2, (Object)$$6);
                    return Optional.empty();
                }
            }
            $$3.close();
        }
        return optional;
    }

    private Optional<StructureTemplate> load(InputStreamOpener $$0, Consumer<Throwable> $$1) {
        Optional optional;
        block9: {
            InputStream $$2 = $$0.open();
            try {
                optional = Optional.of((Object)this.readStructure($$2));
                if ($$2 == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if ($$2 != null) {
                        try {
                            $$2.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (FileNotFoundException $$3) {
                    return Optional.empty();
                }
                catch (Throwable $$4) {
                    $$1.accept((Object)$$4);
                    return Optional.empty();
                }
            }
            $$2.close();
        }
        return optional;
    }

    private StructureTemplate readStructure(InputStream $$0) throws IOException {
        CompoundTag $$1 = NbtIo.readCompressed($$0);
        return this.readStructure($$1);
    }

    public StructureTemplate readStructure(CompoundTag $$0) {
        if (!$$0.contains("DataVersion", 99)) {
            $$0.putInt("DataVersion", 500);
        }
        StructureTemplate $$1 = new StructureTemplate();
        $$1.load(this.blockLookup, NbtUtils.update(this.fixerUpper, DataFixTypes.STRUCTURE, $$0, $$0.getInt("DataVersion")));
        return $$1;
    }

    public boolean save(ResourceLocation $$0) {
        Optional $$1 = (Optional)this.structureRepository.get((Object)$$0);
        if (!$$1.isPresent()) {
            return false;
        }
        StructureTemplate $$2 = (StructureTemplate)$$1.get();
        Path $$3 = StructureTemplateManager.createAndValidatePathToStructure(this.generatedDir, $$0, STRUCTURE_FILE_EXTENSION);
        Path $$4 = $$3.getParent();
        if ($$4 == null) {
            return false;
        }
        try {
            Files.createDirectories((Path)(Files.exists((Path)$$4, (LinkOption[])new LinkOption[0]) ? $$4.toRealPath(new LinkOption[0]) : $$4), (FileAttribute[])new FileAttribute[0]);
        }
        catch (IOException $$5) {
            LOGGER.error("Failed to create parent directory: {}", (Object)$$4);
            return false;
        }
        CompoundTag $$6 = $$2.save(new CompoundTag());
        try (FileOutputStream $$7 = new FileOutputStream($$3.toFile());){
            NbtIo.writeCompressed($$6, (OutputStream)$$7);
        }
        catch (Throwable $$8) {
            return false;
        }
        return true;
    }

    public Path getPathToGeneratedStructure(ResourceLocation $$0, String $$1) {
        return StructureTemplateManager.createPathToStructure(this.generatedDir, $$0, $$1);
    }

    public static Path createPathToStructure(Path $$0, ResourceLocation $$1, String $$2) {
        try {
            Path $$3 = $$0.resolve($$1.getNamespace());
            Path $$4 = $$3.resolve(STRUCTURE_DIRECTORY_NAME);
            return FileUtil.createPathToResource($$4, $$1.getPath(), $$2);
        }
        catch (InvalidPathException $$5) {
            throw new ResourceLocationException("Invalid resource path: " + $$1, $$5);
        }
    }

    private static Path createAndValidatePathToStructure(Path $$0, ResourceLocation $$1, String $$2) {
        if ($$1.getPath().contains((CharSequence)"//")) {
            throw new ResourceLocationException("Invalid resource path: " + $$1);
        }
        Path $$3 = StructureTemplateManager.createPathToStructure($$0, $$1, $$2);
        if (!($$3.startsWith($$0) && FileUtil.isPathNormalized($$3) && FileUtil.isPathPortable($$3))) {
            throw new ResourceLocationException("Invalid resource path: " + $$3);
        }
        return $$3;
    }

    public void remove(ResourceLocation $$0) {
        this.structureRepository.remove((Object)$$0);
    }

    record Source(Function<ResourceLocation, Optional<StructureTemplate>> loader, Supplier<Stream<ResourceLocation>> lister) {
    }

    @FunctionalInterface
    static interface InputStreamOpener {
        public InputStream open() throws IOException;
    }
}
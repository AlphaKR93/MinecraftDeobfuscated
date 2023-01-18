/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  java.io.BufferedOutputStream
 *  java.io.File
 *  java.io.IOException
 *  java.io.OutputStream
 *  java.io.UncheckedIOException
 *  java.lang.AutoCloseable
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.InterruptedException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.OutOfMemoryError
 *  java.lang.RuntimeException
 *  java.lang.StackOverflowError
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.nio.file.FileVisitResult
 *  java.nio.file.FileVisitor
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.nio.file.SimpleFileVisitor
 *  java.nio.file.attribute.BasicFileAttributes
 *  java.nio.file.attribute.FileAttribute
 *  java.time.LocalDateTime
 *  java.time.format.DateTimeFormatter
 *  java.time.format.DateTimeFormatterBuilder
 *  java.time.format.SignStyle
 *  java.time.temporal.ChronoField
 *  java.time.temporal.TemporalField
 *  java.util.ArrayList
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.BiFunction
 *  java.util.function.Consumer
 *  java.util.stream.Collectors
 *  java.util.zip.ZipEntry
 *  java.util.zip.ZipOutputStream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.nbt.visitors.SkipFields;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.DirectoryLock;
import net.minecraft.util.MemoryReserve;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.slf4j.Logger;

public class LevelStorageSource {
    static final Logger LOGGER = LogUtils.getLogger();
    static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendValue((TemporalField)ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue((TemporalField)ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue((TemporalField)ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue((TemporalField)ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue((TemporalField)ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue((TemporalField)ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
    private static final ImmutableList<String> OLD_SETTINGS_KEYS = ImmutableList.of((Object)"RandomSeed", (Object)"generatorName", (Object)"generatorOptions", (Object)"generatorVersion", (Object)"legacy_custom_options", (Object)"MapFeatures", (Object)"BonusChest");
    private static final String TAG_DATA = "Data";
    final Path baseDir;
    private final Path backupDir;
    final DataFixer fixerUpper;

    public LevelStorageSource(Path $$0, Path $$1, DataFixer $$2) {
        this.fixerUpper = $$2;
        try {
            FileUtil.createDirectoriesSafe($$0);
        }
        catch (IOException $$3) {
            throw new RuntimeException((Throwable)$$3);
        }
        this.baseDir = $$0;
        this.backupDir = $$1;
    }

    public static LevelStorageSource createDefault(Path $$0) {
        return new LevelStorageSource($$0, $$0.resolve("../backups"), DataFixers.getDataFixer());
    }

    private static <T> DataResult<WorldGenSettings> readWorldGenSettings(Dynamic<T> $$0, DataFixer $$1, int $$2) {
        Dynamic $$3 = $$0.get("WorldGenSettings").orElseEmptyMap();
        for (String $$4 : OLD_SETTINGS_KEYS) {
            Optional $$5 = $$0.get($$4).result();
            if (!$$5.isPresent()) continue;
            $$3 = $$3.set($$4, (Dynamic)$$5.get());
        }
        Dynamic $$6 = DataFixTypes.WORLD_GEN_SETTINGS.updateToCurrentVersion($$1, $$3, $$2);
        return WorldGenSettings.CODEC.parse($$6);
    }

    private static WorldDataConfiguration readDataConfig(Dynamic<?> $$0) {
        return (WorldDataConfiguration)((Object)WorldDataConfiguration.CODEC.parse($$0).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).orElse((Object)WorldDataConfiguration.DEFAULT));
    }

    public String getName() {
        return "Anvil";
    }

    public LevelCandidates findLevelCandidates() throws LevelStorageException {
        if (!Files.isDirectory((Path)this.baseDir, (LinkOption[])new LinkOption[0])) {
            throw new LevelStorageException(Component.translatable("selectWorld.load_folder_access"));
        }
        try {
            List $$02 = Files.list((Path)this.baseDir).filter($$0 -> Files.isDirectory((Path)$$0, (LinkOption[])new LinkOption[0])).map(LevelDirectory::new).filter($$0 -> Files.isRegularFile((Path)$$0.dataFile(), (LinkOption[])new LinkOption[0]) || Files.isRegularFile((Path)$$0.oldDataFile(), (LinkOption[])new LinkOption[0])).toList();
            return new LevelCandidates((List<LevelDirectory>)$$02);
        }
        catch (IOException $$1) {
            throw new LevelStorageException(Component.translatable("selectWorld.load_folder_access"));
        }
    }

    public CompletableFuture<List<LevelSummary>> loadLevelSummaries(LevelCandidates $$02) {
        ArrayList $$1 = new ArrayList($$02.levels.size());
        for (LevelDirectory $$2 : $$02.levels) {
            $$1.add((Object)CompletableFuture.supplyAsync(() -> {
                try {
                    boolean $$1 = DirectoryLock.isLocked($$2.path());
                }
                catch (Exception $$2) {
                    LOGGER.warn("Failed to read {} lock", (Object)$$2.path(), (Object)$$2);
                    return null;
                }
                try {
                    void $$3;
                    LevelSummary $$4 = this.readLevelData($$2, this.levelSummaryReader($$2, (boolean)$$3));
                    if ($$4 != null) {
                        return $$4;
                    }
                }
                catch (OutOfMemoryError $$5) {
                    MemoryReserve.release();
                    System.gc();
                    LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of memory trying to read summary of {}", (Object)$$2.directoryName());
                    throw $$5;
                }
                catch (StackOverflowError $$6) {
                    LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of stack trying to read summary of {}. Assuming corruption; attempting to restore from from level.dat_old.", (Object)$$2.directoryName());
                    Util.safeReplaceOrMoveFile($$2.dataFile(), $$2.oldDataFile(), $$2.corruptedDataFile(LocalDateTime.now()), true);
                    throw $$6;
                }
                return null;
            }, (Executor)Util.backgroundExecutor()));
        }
        return Util.sequenceFailFastAndCancel($$1).thenApply($$0 -> $$0.stream().filter(Objects::nonNull).sorted().toList());
    }

    private int getStorageVersion() {
        return 19133;
    }

    @Nullable
    <T> T readLevelData(LevelDirectory $$0, BiFunction<Path, DataFixer, T> $$1) {
        Object $$3;
        if (!Files.exists((Path)$$0.path(), (LinkOption[])new LinkOption[0])) {
            return null;
        }
        Path $$2 = $$0.dataFile();
        if (Files.exists((Path)$$2, (LinkOption[])new LinkOption[0]) && ($$3 = $$1.apply((Object)$$2, (Object)this.fixerUpper)) != null) {
            return (T)$$3;
        }
        $$2 = $$0.oldDataFile();
        if (Files.exists((Path)$$2, (LinkOption[])new LinkOption[0])) {
            return (T)$$1.apply((Object)$$2, (Object)this.fixerUpper);
        }
        return null;
    }

    @Nullable
    private static WorldDataConfiguration getDataConfiguration(Path $$0, DataFixer $$1) {
        try {
            Tag $$2 = LevelStorageSource.readLightweightData($$0);
            if ($$2 instanceof CompoundTag) {
                CompoundTag $$3 = (CompoundTag)$$2;
                CompoundTag $$4 = $$3.getCompound(TAG_DATA);
                int $$5 = NbtUtils.getDataVersion($$4, -1);
                Dynamic $$6 = DataFixTypes.LEVEL.updateToCurrentVersion($$1, new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$4), $$5);
                return LevelStorageSource.readDataConfig($$6);
            }
        }
        catch (Exception $$7) {
            LOGGER.error("Exception reading {}", (Object)$$0, (Object)$$7);
        }
        return null;
    }

    static BiFunction<Path, DataFixer, Pair<WorldData, WorldDimensions.Complete>> getLevelData(DynamicOps<Tag> $$0, WorldDataConfiguration $$1, Registry<LevelStem> $$2, Lifecycle $$3) {
        return ($$4, $$5) -> {
            void $$8;
            try {
                CompoundTag $$6 = NbtIo.readCompressed($$4.toFile());
            }
            catch (IOException $$7) {
                throw new UncheckedIOException($$7);
            }
            CompoundTag $$9 = $$8.getCompound(TAG_DATA);
            CompoundTag $$10 = $$9.contains("Player", 10) ? $$9.getCompound("Player") : null;
            $$9.remove("Player");
            int $$11 = NbtUtils.getDataVersion($$9, -1);
            Dynamic $$12 = DataFixTypes.LEVEL.updateToCurrentVersion((DataFixer)$$5, new Dynamic($$0, (Object)$$9), $$11);
            WorldGenSettings $$13 = (WorldGenSettings)((Object)((Object)LevelStorageSource.readWorldGenSettings($$12, $$5, $$11).getOrThrow(false, Util.prefix("WorldGenSettings: ", (Consumer<String>)((Consumer)arg_0 -> ((Logger)LOGGER).error(arg_0))))));
            LevelVersion $$14 = LevelVersion.parse($$12);
            LevelSettings $$15 = LevelSettings.parse($$12, $$1);
            WorldDimensions.Complete $$16 = $$13.dimensions().bake($$2);
            Lifecycle $$17 = $$16.lifecycle().add($$3);
            PrimaryLevelData $$18 = PrimaryLevelData.parse($$12, $$5, $$11, $$10, $$15, $$14, $$16.specialWorldProperty(), $$13.options(), $$17);
            return Pair.of((Object)$$18, (Object)((Object)$$16));
        };
    }

    BiFunction<Path, DataFixer, LevelSummary> levelSummaryReader(LevelDirectory $$0, boolean $$1) {
        return ($$2, $$3) -> {
            try {
                Tag $$4 = LevelStorageSource.readLightweightData($$2);
                if ($$4 instanceof CompoundTag) {
                    int $$7;
                    CompoundTag $$5 = (CompoundTag)$$4;
                    CompoundTag $$6 = $$5.getCompound(TAG_DATA);
                    Dynamic $$8 = DataFixTypes.LEVEL.updateToCurrentVersion((DataFixer)$$3, new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$6), $$7 = NbtUtils.getDataVersion($$6, -1));
                    LevelVersion $$9 = LevelVersion.parse($$8);
                    int $$10 = $$9.levelDataVersion();
                    if ($$10 == 19132 || $$10 == 19133) {
                        boolean $$11 = $$10 != this.getStorageVersion();
                        Path $$12 = $$0.iconFile();
                        WorldDataConfiguration $$13 = LevelStorageSource.readDataConfig($$8);
                        LevelSettings $$14 = LevelSettings.parse($$8, $$13);
                        FeatureFlagSet $$15 = LevelStorageSource.parseFeatureFlagsFromSummary($$8);
                        boolean $$16 = FeatureFlags.isExperimental($$15);
                        return new LevelSummary($$14, $$9, $$0.directoryName(), $$11, $$1, $$16, $$12);
                    }
                } else {
                    LOGGER.warn("Invalid root tag in {}", $$2);
                }
                return null;
            }
            catch (Exception $$17) {
                LOGGER.error("Exception reading {}", $$2, (Object)$$17);
                return null;
            }
        };
    }

    private static FeatureFlagSet parseFeatureFlagsFromSummary(Dynamic<?> $$02) {
        Set $$1 = (Set)$$02.get("enabled_features").asStream().flatMap($$0 -> $$0.asString().result().map(ResourceLocation::tryParse).stream()).collect(Collectors.toSet());
        return FeatureFlags.REGISTRY.fromNames((Iterable<ResourceLocation>)$$1, (Consumer<ResourceLocation>)((Consumer)$$0 -> {}));
    }

    @Nullable
    private static Tag readLightweightData(Path $$0) throws IOException {
        SkipFields $$1 = new SkipFields(new FieldSelector(TAG_DATA, CompoundTag.TYPE, "Player"), new FieldSelector(TAG_DATA, CompoundTag.TYPE, "WorldGenSettings"));
        NbtIo.parseCompressed($$0.toFile(), (StreamTagVisitor)$$1);
        return $$1.getResult();
    }

    public boolean isNewLevelIdAcceptable(String $$0) {
        try {
            Path $$1 = this.baseDir.resolve($$0);
            Files.createDirectory((Path)$$1, (FileAttribute[])new FileAttribute[0]);
            Files.deleteIfExists((Path)$$1);
            return true;
        }
        catch (IOException $$2) {
            return false;
        }
    }

    public boolean levelExists(String $$0) {
        return Files.isDirectory((Path)this.baseDir.resolve($$0), (LinkOption[])new LinkOption[0]);
    }

    public Path getBaseDir() {
        return this.baseDir;
    }

    public Path getBackupPath() {
        return this.backupDir;
    }

    public LevelStorageAccess createAccess(String $$0) throws IOException {
        return new LevelStorageAccess($$0);
    }

    public record LevelCandidates(List<LevelDirectory> levels) implements Iterable<LevelDirectory>
    {
        public boolean isEmpty() {
            return this.levels.isEmpty();
        }

        public Iterator<LevelDirectory> iterator() {
            return this.levels.iterator();
        }
    }

    public record LevelDirectory(Path path) {
        public String directoryName() {
            return this.path.getFileName().toString();
        }

        public Path dataFile() {
            return this.resourcePath(LevelResource.LEVEL_DATA_FILE);
        }

        public Path oldDataFile() {
            return this.resourcePath(LevelResource.OLD_LEVEL_DATA_FILE);
        }

        public Path corruptedDataFile(LocalDateTime $$0) {
            return this.path.resolve(LevelResource.LEVEL_DATA_FILE.getId() + "_corrupted_" + $$0.format(FORMATTER));
        }

        public Path iconFile() {
            return this.resourcePath(LevelResource.ICON_FILE);
        }

        public Path lockFile() {
            return this.resourcePath(LevelResource.LOCK_FILE);
        }

        public Path resourcePath(LevelResource $$0) {
            return this.path.resolve($$0.getId());
        }
    }

    public class LevelStorageAccess
    implements AutoCloseable {
        final DirectoryLock lock;
        final LevelDirectory levelDirectory;
        private final String levelId;
        private final Map<LevelResource, Path> resources = Maps.newHashMap();

        public LevelStorageAccess(String $$1) throws IOException {
            this.levelId = $$1;
            this.levelDirectory = new LevelDirectory(LevelStorageSource.this.baseDir.resolve($$1));
            this.lock = DirectoryLock.create(this.levelDirectory.path());
        }

        public String getLevelId() {
            return this.levelId;
        }

        public Path getLevelPath(LevelResource $$0) {
            return (Path)this.resources.computeIfAbsent((Object)$$0, this.levelDirectory::resourcePath);
        }

        public Path getDimensionPath(ResourceKey<Level> $$0) {
            return DimensionType.getStorageFolder($$0, this.levelDirectory.path());
        }

        private void checkLock() {
            if (!this.lock.isValid()) {
                throw new IllegalStateException("Lock is no longer valid");
            }
        }

        public PlayerDataStorage createPlayerStorage() {
            this.checkLock();
            return new PlayerDataStorage(this, LevelStorageSource.this.fixerUpper);
        }

        @Nullable
        public LevelSummary getSummary() {
            this.checkLock();
            return LevelStorageSource.this.readLevelData(this.levelDirectory, LevelStorageSource.this.levelSummaryReader(this.levelDirectory, false));
        }

        @Nullable
        public Pair<WorldData, WorldDimensions.Complete> getDataTag(DynamicOps<Tag> $$0, WorldDataConfiguration $$1, Registry<LevelStem> $$2, Lifecycle $$3) {
            this.checkLock();
            return LevelStorageSource.this.readLevelData(this.levelDirectory, LevelStorageSource.getLevelData($$0, $$1, $$2, $$3));
        }

        @Nullable
        public WorldDataConfiguration getDataConfiguration() {
            this.checkLock();
            return (WorldDataConfiguration)((Object)LevelStorageSource.this.readLevelData(this.levelDirectory, LevelStorageSource::getDataConfiguration));
        }

        public void saveDataTag(RegistryAccess $$0, WorldData $$1) {
            this.saveDataTag($$0, $$1, null);
        }

        public void saveDataTag(RegistryAccess $$0, WorldData $$1, @Nullable CompoundTag $$2) {
            File $$3 = this.levelDirectory.path().toFile();
            CompoundTag $$4 = $$1.createTag($$0, $$2);
            CompoundTag $$5 = new CompoundTag();
            $$5.put(LevelStorageSource.TAG_DATA, $$4);
            try {
                File $$6 = File.createTempFile((String)"level", (String)".dat", (File)$$3);
                NbtIo.writeCompressed($$5, $$6);
                File $$7 = this.levelDirectory.oldDataFile().toFile();
                File $$8 = this.levelDirectory.dataFile().toFile();
                Util.safeReplaceFile($$8, $$6, $$7);
            }
            catch (Exception $$9) {
                LOGGER.error("Failed to save level {}", (Object)$$3, (Object)$$9);
            }
        }

        public Optional<Path> getIconFile() {
            if (!this.lock.isValid()) {
                return Optional.empty();
            }
            return Optional.of((Object)this.levelDirectory.iconFile());
        }

        public void deleteLevel() throws IOException {
            this.checkLock();
            final Path $$0 = this.levelDirectory.lockFile();
            LOGGER.info("Deleting level {}", (Object)this.levelId);
            for (int $$1 = 1; $$1 <= 5; ++$$1) {
                LOGGER.info("Attempt {}...", (Object)$$1);
                try {
                    Files.walkFileTree((Path)this.levelDirectory.path(), (FileVisitor)new SimpleFileVisitor<Path>(){

                        public FileVisitResult visitFile(Path $$02, BasicFileAttributes $$1) throws IOException {
                            if (!$$02.equals((Object)$$0)) {
                                LOGGER.debug("Deleting {}", (Object)$$02);
                                Files.delete((Path)$$02);
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        public FileVisitResult postVisitDirectory(Path $$02, IOException $$1) throws IOException {
                            if ($$1 != null) {
                                throw $$1;
                            }
                            if ($$02.equals((Object)LevelStorageAccess.this.levelDirectory.path())) {
                                LevelStorageAccess.this.lock.close();
                                Files.deleteIfExists((Path)$$0);
                            }
                            Files.delete((Path)$$02);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                    break;
                }
                catch (IOException $$2) {
                    if ($$1 < 5) {
                        LOGGER.warn("Failed to delete {}", (Object)this.levelDirectory.path(), (Object)$$2);
                        try {
                            Thread.sleep((long)500L);
                        }
                        catch (InterruptedException interruptedException) {}
                        continue;
                    }
                    throw $$2;
                }
            }
        }

        public void renameLevel(String $$0) throws IOException {
            this.checkLock();
            Path $$1 = this.levelDirectory.dataFile();
            if (Files.exists((Path)$$1, (LinkOption[])new LinkOption[0])) {
                CompoundTag $$2 = NbtIo.readCompressed($$1.toFile());
                CompoundTag $$3 = $$2.getCompound(LevelStorageSource.TAG_DATA);
                $$3.putString("LevelName", $$0);
                NbtIo.writeCompressed($$2, $$1.toFile());
            }
        }

        public long makeWorldBackup() throws IOException {
            this.checkLock();
            String $$0 = LocalDateTime.now().format(FORMATTER) + "_" + this.levelId;
            Path $$1 = LevelStorageSource.this.getBackupPath();
            try {
                FileUtil.createDirectoriesSafe($$1);
            }
            catch (IOException $$2) {
                throw new RuntimeException((Throwable)$$2);
            }
            Path $$3 = $$1.resolve(FileUtil.findAvailableName($$1, $$0, ".zip"));
            try (final ZipOutputStream $$4 = new ZipOutputStream((OutputStream)new BufferedOutputStream(Files.newOutputStream((Path)$$3, (OpenOption[])new OpenOption[0])));){
                final Path $$5 = Paths.get((String)this.levelId, (String[])new String[0]);
                Files.walkFileTree((Path)this.levelDirectory.path(), (FileVisitor)new SimpleFileVisitor<Path>(){

                    public FileVisitResult visitFile(Path $$0, BasicFileAttributes $$1) throws IOException {
                        if ($$0.endsWith("session.lock")) {
                            return FileVisitResult.CONTINUE;
                        }
                        String $$2 = $$5.resolve(LevelStorageAccess.this.levelDirectory.path().relativize($$0)).toString().replace('\\', '/');
                        ZipEntry $$3 = new ZipEntry($$2);
                        $$4.putNextEntry($$3);
                        com.google.common.io.Files.asByteSource((File)$$0.toFile()).copyTo((OutputStream)$$4);
                        $$4.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            return Files.size((Path)$$3);
        }

        public void close() throws IOException {
            this.lock.close();
        }
    }
}
package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.FileUtils;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.nbt.visitors.SkipFields;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.MemoryReserve;
import net.minecraft.util.SessionLock;
import net.minecraft.util.datafix.DataConverterRegistry;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.fixes.DataConverterTypes;
import net.minecraft.world.level.DataPackConfiguration;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldSettings;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.levelgen.GeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.slf4j.Logger;

public class Convertable {

    static final Logger LOGGER = LogUtils.getLogger();
    static final DateTimeFormatter FORMATTER = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
    private static final ImmutableList<String> OLD_SETTINGS_KEYS = ImmutableList.of("RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest");
    private static final String TAG_DATA = "Data";
    public final Path baseDir;
    private final Path backupDir;
    final DataFixer fixerUpper;

    public Convertable(Path path, Path path1, DataFixer datafixer) {
        this.fixerUpper = datafixer;

        try {
            Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
        } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
        }

        this.baseDir = path;
        this.backupDir = path1;
    }

    public static Convertable createDefault(Path path) {
        return new Convertable(path, path.resolve("../backups"), DataConverterRegistry.getDataFixer());
    }

    private static <T> Pair<GeneratorSettings, Lifecycle> readWorldGenSettings(Dynamic<T> dynamic, DataFixer datafixer, int i) {
        Dynamic<T> dynamic1 = dynamic.get("WorldGenSettings").orElseEmptyMap();
        UnmodifiableIterator unmodifiableiterator = Convertable.OLD_SETTINGS_KEYS.iterator();

        while (unmodifiableiterator.hasNext()) {
            String s = (String) unmodifiableiterator.next();
            Optional<? extends Dynamic<?>> optional = dynamic.get(s).result();

            if (optional.isPresent()) {
                dynamic1 = dynamic1.set(s, (Dynamic) optional.get());
            }
        }

        Dynamic<T> dynamic2 = datafixer.update(DataConverterTypes.WORLD_GEN_SETTINGS, dynamic1, i, SharedConstants.getCurrentVersion().getWorldVersion());
        DataResult<GeneratorSettings> dataresult = GeneratorSettings.CODEC.parse(dynamic2);
        Logger logger = Convertable.LOGGER;

        Objects.requireNonNull(logger);
        return Pair.of((GeneratorSettings) dataresult.resultOrPartial(SystemUtils.prefix("WorldGenSettings: ", logger::error)).orElseGet(() -> {
            IRegistryCustom iregistrycustom = IRegistryCustom.readFromDisk(dynamic2);

            return WorldPresets.createNormalWorldFromPreset(iregistrycustom);
        }), dataresult.lifecycle());
    }

    private static DataPackConfiguration readDataPackConfig(Dynamic<?> dynamic) {
        DataResult dataresult = DataPackConfiguration.CODEC.parse(dynamic);
        Logger logger = Convertable.LOGGER;

        Objects.requireNonNull(logger);
        return (DataPackConfiguration) dataresult.resultOrPartial(logger::error).orElse(DataPackConfiguration.DEFAULT);
    }

    public String getName() {
        return "Anvil";
    }

    public Convertable.a findLevelCandidates() throws LevelStorageException {
        if (!Files.isDirectory(this.baseDir, new LinkOption[0])) {
            throw new LevelStorageException(IChatBaseComponent.translatable("selectWorld.load_folder_access"));
        } else {
            try {
                List<Convertable.b> list = Files.list(this.baseDir).filter((path) -> {
                    return Files.isDirectory(path, new LinkOption[0]);
                }).map(Convertable.b::new).filter((convertable_b) -> {
                    return Files.isRegularFile(convertable_b.dataFile(), new LinkOption[0]) || Files.isRegularFile(convertable_b.oldDataFile(), new LinkOption[0]);
                }).toList();

                return new Convertable.a(list);
            } catch (IOException ioexception) {
                throw new LevelStorageException(IChatBaseComponent.translatable("selectWorld.load_folder_access"));
            }
        }
    }

    public CompletableFuture<List<WorldInfo>> loadLevelSummaries(Convertable.a convertable_a) {
        List<CompletableFuture<WorldInfo>> list = new ArrayList(convertable_a.levels.size());
        Iterator iterator = convertable_a.levels.iterator();

        while (iterator.hasNext()) {
            Convertable.b convertable_b = (Convertable.b) iterator.next();

            list.add(CompletableFuture.supplyAsync(() -> {
                boolean flag;

                try {
                    flag = SessionLock.isLocked(convertable_b.path());
                } catch (Exception exception) {
                    Convertable.LOGGER.warn("Failed to read {} lock", convertable_b.path(), exception);
                    return null;
                }

                try {
                    WorldInfo worldinfo = (WorldInfo) this.readLevelData(convertable_b, this.levelSummaryReader(convertable_b, flag));

                    return worldinfo != null ? worldinfo : null;
                } catch (OutOfMemoryError outofmemoryerror) {
                    MemoryReserve.release();
                    System.gc();
                    Convertable.LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of memory trying to read summary of {}", convertable_b.directoryName());
                    throw outofmemoryerror;
                } catch (StackOverflowError stackoverflowerror) {
                    Convertable.LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of stack trying to read summary of {}. Assuming corruption; attempting to restore from from level.dat_old.", convertable_b.directoryName());
                    SystemUtils.safeReplaceOrMoveFile(convertable_b.dataFile(), convertable_b.oldDataFile(), convertable_b.corruptedDataFile(LocalDateTime.now()), true);
                    throw stackoverflowerror;
                }
            }, SystemUtils.backgroundExecutor()));
        }

        return SystemUtils.sequenceFailFastAndCancel(list).thenApply((list1) -> {
            return list1.stream().filter(Objects::nonNull).sorted().toList();
        });
    }

    private int getStorageVersion() {
        return 19133;
    }

    @Nullable
    <T> T readLevelData(Convertable.b convertable_b, BiFunction<Path, DataFixer, T> bifunction) {
        if (!Files.exists(convertable_b.path(), new LinkOption[0])) {
            return null;
        } else {
            Path path = convertable_b.dataFile();

            if (Files.exists(path, new LinkOption[0])) {
                T t0 = bifunction.apply(path, this.fixerUpper);

                if (t0 != null) {
                    return t0;
                }
            }

            path = convertable_b.oldDataFile();
            return Files.exists(path, new LinkOption[0]) ? bifunction.apply(path, this.fixerUpper) : null;
        }
    }

    @Nullable
    private static DataPackConfiguration getDataPacks(Path path, DataFixer datafixer) {
        try {
            NBTBase nbtbase = readLightweightData(path);

            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");
                int i = nbttagcompound1.contains("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : -1;
                Dynamic<NBTBase> dynamic = datafixer.update(DataFixTypes.LEVEL.getType(), new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound1), i, SharedConstants.getCurrentVersion().getWorldVersion());

                return (DataPackConfiguration) dynamic.get("DataPacks").result().map(Convertable::readDataPackConfig).orElse(DataPackConfiguration.DEFAULT);
            }
        } catch (Exception exception) {
            Convertable.LOGGER.error("Exception reading {}", path, exception);
        }

        return null;
    }

    static BiFunction<Path, DataFixer, WorldDataServer> getLevelData(DynamicOps<NBTBase> dynamicops, DataPackConfiguration datapackconfiguration, Lifecycle lifecycle) {
        return (path, datafixer) -> {
            try {
                NBTTagCompound nbttagcompound = NBTCompressedStreamTools.readCompressed(path.toFile());
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");
                NBTTagCompound nbttagcompound2 = nbttagcompound1.contains("Player", 10) ? nbttagcompound1.getCompound("Player") : null;

                nbttagcompound1.remove("Player");
                int i = nbttagcompound1.contains("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : -1;
                Dynamic<NBTBase> dynamic = datafixer.update(DataFixTypes.LEVEL.getType(), new Dynamic(dynamicops, nbttagcompound1), i, SharedConstants.getCurrentVersion().getWorldVersion());
                Pair<GeneratorSettings, Lifecycle> pair = readWorldGenSettings(dynamic, datafixer, i);
                LevelVersion levelversion = LevelVersion.parse(dynamic);
                WorldSettings worldsettings = WorldSettings.parse(dynamic, datapackconfiguration);
                Lifecycle lifecycle1 = ((Lifecycle) pair.getSecond()).add(lifecycle);

                return WorldDataServer.parse(dynamic, datafixer, i, nbttagcompound2, worldsettings, levelversion, (GeneratorSettings) pair.getFirst(), lifecycle1);
            } catch (Exception exception) {
                Convertable.LOGGER.error("Exception reading {}", path, exception);
                return null;
            }
        };
    }

    BiFunction<Path, DataFixer, WorldInfo> levelSummaryReader(Convertable.b convertable_b, boolean flag) {
        return (path, datafixer) -> {
            try {
                NBTBase nbtbase = readLightweightData(path);

                if (nbtbase instanceof NBTTagCompound) {
                    NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");
                    int i = nbttagcompound1.contains("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : -1;
                    Dynamic<NBTBase> dynamic = datafixer.update(DataFixTypes.LEVEL.getType(), new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound1), i, SharedConstants.getCurrentVersion().getWorldVersion());
                    LevelVersion levelversion = LevelVersion.parse(dynamic);
                    int j = levelversion.levelDataVersion();

                    if (j == 19132 || j == 19133) {
                        boolean flag1 = j != this.getStorageVersion();
                        Path path1 = convertable_b.iconFile();
                        DataPackConfiguration datapackconfiguration = (DataPackConfiguration) dynamic.get("DataPacks").result().map(Convertable::readDataPackConfig).orElse(DataPackConfiguration.DEFAULT);
                        WorldSettings worldsettings = WorldSettings.parse(dynamic, datapackconfiguration);

                        return new WorldInfo(worldsettings, levelversion, convertable_b.directoryName(), flag1, flag, path1);
                    }
                } else {
                    Convertable.LOGGER.warn("Invalid root tag in {}", path);
                }

                return null;
            } catch (Exception exception) {
                Convertable.LOGGER.error("Exception reading {}", path, exception);
                return null;
            }
        };
    }

    @Nullable
    private static NBTBase readLightweightData(Path path) throws IOException {
        SkipFields skipfields = new SkipFields(new FieldSelector[]{new FieldSelector("Data", NBTTagCompound.TYPE, "Player"), new FieldSelector("Data", NBTTagCompound.TYPE, "WorldGenSettings")});

        NBTCompressedStreamTools.parseCompressed(path.toFile(), skipfields);
        return skipfields.getResult();
    }

    public boolean isNewLevelIdAcceptable(String s) {
        try {
            Path path = this.baseDir.resolve(s);

            Files.createDirectory(path);
            Files.deleteIfExists(path);
            return true;
        } catch (IOException ioexception) {
            return false;
        }
    }

    public boolean levelExists(String s) {
        return Files.isDirectory(this.baseDir.resolve(s), new LinkOption[0]);
    }

    public Path getBaseDir() {
        return this.baseDir;
    }

    public Path getBackupPath() {
        return this.backupDir;
    }

    public Convertable.ConversionSession createAccess(String s) throws IOException {
        return new Convertable.ConversionSession(s);
    }

    public static record a(List<Convertable.b> levels) implements Iterable<Convertable.b> {

        public boolean isEmpty() {
            return this.levels.isEmpty();
        }

        public Iterator<Convertable.b> iterator() {
            return this.levels.iterator();
        }
    }

    public static record b(Path path) {

        public String directoryName() {
            return this.path.getFileName().toString();
        }

        public Path dataFile() {
            return this.resourcePath(SavedFile.LEVEL_DATA_FILE);
        }

        public Path oldDataFile() {
            return this.resourcePath(SavedFile.OLD_LEVEL_DATA_FILE);
        }

        public Path corruptedDataFile(LocalDateTime localdatetime) {
            Path path = this.path;
            String s = SavedFile.LEVEL_DATA_FILE.getId();

            return path.resolve(s + "_corrupted_" + localdatetime.format(Convertable.FORMATTER));
        }

        public Path iconFile() {
            return this.resourcePath(SavedFile.ICON_FILE);
        }

        public Path lockFile() {
            return this.resourcePath(SavedFile.LOCK_FILE);
        }

        public Path resourcePath(SavedFile savedfile) {
            return this.path.resolve(savedfile.getId());
        }
    }

    public class ConversionSession implements AutoCloseable {

        final SessionLock lock;
        public final Convertable.b levelDirectory;
        private final String levelId;
        private final Map<SavedFile, Path> resources = Maps.newHashMap();

        public ConversionSession(String s) throws IOException {
            this.levelId = s;
            this.levelDirectory = new Convertable.b(Convertable.this.baseDir.resolve(s));
            this.lock = SessionLock.create(this.levelDirectory.path());
        }

        public String getLevelId() {
            return this.levelId;
        }

        public Path getLevelPath(SavedFile savedfile) {
            Map map = this.resources;
            Convertable.b convertable_b = this.levelDirectory;

            Objects.requireNonNull(this.levelDirectory);
            return (Path) map.computeIfAbsent(savedfile, convertable_b::resourcePath);
        }

        public Path getDimensionPath(ResourceKey<World> resourcekey) {
            return DimensionManager.getStorageFolder(resourcekey, this.levelDirectory.path());
        }

        private void checkLock() {
            if (!this.lock.isValid()) {
                throw new IllegalStateException("Lock is no longer valid");
            }
        }

        public WorldNBTStorage createPlayerStorage() {
            this.checkLock();
            return new WorldNBTStorage(this, Convertable.this.fixerUpper);
        }

        @Nullable
        public WorldInfo getSummary() {
            this.checkLock();
            return (WorldInfo) Convertable.this.readLevelData(this.levelDirectory, Convertable.this.levelSummaryReader(this.levelDirectory, false));
        }

        @Nullable
        public SaveData getDataTag(DynamicOps<NBTBase> dynamicops, DataPackConfiguration datapackconfiguration, Lifecycle lifecycle) {
            this.checkLock();
            return (SaveData) Convertable.this.readLevelData(this.levelDirectory, Convertable.getLevelData(dynamicops, datapackconfiguration, lifecycle));
        }

        @Nullable
        public DataPackConfiguration getDataPacks() {
            this.checkLock();
            return (DataPackConfiguration) Convertable.this.readLevelData(this.levelDirectory, Convertable::getDataPacks);
        }

        public void saveDataTag(IRegistryCustom iregistrycustom, SaveData savedata) {
            this.saveDataTag(iregistrycustom, savedata, (NBTTagCompound) null);
        }

        public void saveDataTag(IRegistryCustom iregistrycustom, SaveData savedata, @Nullable NBTTagCompound nbttagcompound) {
            File file = this.levelDirectory.path().toFile();
            NBTTagCompound nbttagcompound1 = savedata.createTag(iregistrycustom, nbttagcompound);
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();

            nbttagcompound2.put("Data", nbttagcompound1);

            try {
                File file1 = File.createTempFile("level", ".dat", file);

                NBTCompressedStreamTools.writeCompressed(nbttagcompound2, file1);
                File file2 = this.levelDirectory.oldDataFile().toFile();
                File file3 = this.levelDirectory.dataFile().toFile();

                SystemUtils.safeReplaceFile(file3, file1, file2);
            } catch (Exception exception) {
                Convertable.LOGGER.error("Failed to save level {}", file, exception);
            }

        }

        public Optional<Path> getIconFile() {
            return !this.lock.isValid() ? Optional.empty() : Optional.of(this.levelDirectory.iconFile());
        }

        public void deleteLevel() throws IOException {
            this.checkLock();
            final Path path = this.levelDirectory.lockFile();

            Convertable.LOGGER.info("Deleting level {}", this.levelId);
            int i = 1;

            while (i <= 5) {
                Convertable.LOGGER.info("Attempt {}...", i);

                try {
                    Files.walkFileTree(this.levelDirectory.path(), new SimpleFileVisitor<Path>() {
                        public FileVisitResult visitFile(Path path1, BasicFileAttributes basicfileattributes) throws IOException {
                            if (!path1.equals(path)) {
                                Convertable.LOGGER.debug("Deleting {}", path1);
                                Files.delete(path1);
                            }

                            return FileVisitResult.CONTINUE;
                        }

                        public FileVisitResult postVisitDirectory(Path path1, IOException ioexception) throws IOException {
                            if (ioexception != null) {
                                throw ioexception;
                            } else {
                                if (path1.equals(ConversionSession.this.levelDirectory.path())) {
                                    ConversionSession.this.lock.close();
                                    Files.deleteIfExists(path);
                                }

                                Files.delete(path1);
                                return FileVisitResult.CONTINUE;
                            }
                        }
                    });
                    break;
                } catch (IOException ioexception) {
                    if (i >= 5) {
                        throw ioexception;
                    }

                    Convertable.LOGGER.warn("Failed to delete {}", this.levelDirectory.path(), ioexception);

                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException interruptedexception) {
                        ;
                    }

                    ++i;
                }
            }

        }

        public void renameLevel(String s) throws IOException {
            this.checkLock();
            Path path = this.levelDirectory.dataFile();

            if (Files.exists(path, new LinkOption[0])) {
                NBTTagCompound nbttagcompound = NBTCompressedStreamTools.readCompressed(path.toFile());
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");

                nbttagcompound1.putString("LevelName", s);
                NBTCompressedStreamTools.writeCompressed(nbttagcompound, path.toFile());
            }

        }

        public long makeWorldBackup() throws IOException {
            this.checkLock();
            String s = LocalDateTime.now().format(Convertable.FORMATTER);
            String s1 = s + "_" + this.levelId;
            Path path = Convertable.this.getBackupPath();

            try {
                Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
            } catch (IOException ioexception) {
                throw new RuntimeException(ioexception);
            }

            Path path1 = path.resolve(FileUtils.findAvailableName(path, s1, ".zip"));
            final ZipOutputStream zipoutputstream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path1)));

            try {
                final Path path2 = Paths.get(this.levelId);

                Files.walkFileTree(this.levelDirectory.path(), new SimpleFileVisitor<Path>() {
                    public FileVisitResult visitFile(Path path3, BasicFileAttributes basicfileattributes) throws IOException {
                        if (path3.endsWith("session.lock")) {
                            return FileVisitResult.CONTINUE;
                        } else {
                            String s2 = path2.resolve(ConversionSession.this.levelDirectory.path().relativize(path3)).toString().replace('\\', '/');
                            ZipEntry zipentry = new ZipEntry(s2);

                            zipoutputstream.putNextEntry(zipentry);
                            com.google.common.io.Files.asByteSource(path3.toFile()).copyTo(zipoutputstream);
                            zipoutputstream.closeEntry();
                            return FileVisitResult.CONTINUE;
                        }
                    }
                });
            } catch (Throwable throwable) {
                try {
                    zipoutputstream.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }

                throw throwable;
            }

            zipoutputstream.close();
            return Files.size(path1);
        }

        public void close() throws IOException {
            this.lock.close();
        }
    }
}

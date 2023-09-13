package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
import net.minecraft.network.chat.ChatMessage;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Convertable {

    static final Logger LOGGER = LogManager.getLogger();
    static final DateTimeFormatter FORMATTER = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
    private static final String ICON_FILENAME = "icon.png";
    private static final ImmutableList<String> OLD_SETTINGS_KEYS = ImmutableList.of("RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest");
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
            IRegistryCustom iregistrycustom = IRegistryCustom.Dimension.readFromDisk(dynamic2);

            return GeneratorSettings.makeDefault(iregistrycustom);
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

    public List<WorldInfo> getLevelList() throws LevelStorageException {
        if (!Files.isDirectory(this.baseDir, new LinkOption[0])) {
            throw new LevelStorageException((new ChatMessage("selectWorld.load_folder_access")).getString());
        } else {
            List<WorldInfo> list = Lists.newArrayList();
            File[] afile = this.baseDir.toFile().listFiles();
            File[] afile1 = afile;
            int i = afile.length;

            for (int j = 0; j < i; ++j) {
                File file = afile1[j];

                if (file.isDirectory()) {
                    boolean flag;

                    try {
                        flag = SessionLock.isLocked(file.toPath());
                    } catch (Exception exception) {
                        Convertable.LOGGER.warn("Failed to read {} lock", file, exception);
                        continue;
                    }

                    try {
                        WorldInfo worldinfo = (WorldInfo) this.readLevelData(file, this.levelSummaryReader(file, flag));

                        if (worldinfo != null) {
                            list.add(worldinfo);
                        }
                    } catch (OutOfMemoryError outofmemoryerror) {
                        MemoryReserve.release();
                        System.gc();
                        Convertable.LOGGER.fatal("Ran out of memory trying to read summary of {}", file);
                        throw outofmemoryerror;
                    }
                }
            }

            return list;
        }
    }

    private int getStorageVersion() {
        return 19133;
    }

    @Nullable
    <T> T readLevelData(File file, BiFunction<File, DataFixer, T> bifunction) {
        if (!file.exists()) {
            return null;
        } else {
            File file1 = new File(file, "level.dat");

            if (file1.exists()) {
                T t0 = bifunction.apply(file1, this.fixerUpper);

                if (t0 != null) {
                    return t0;
                }
            }

            file1 = new File(file, "level.dat_old");
            return file1.exists() ? bifunction.apply(file1, this.fixerUpper) : null;
        }
    }

    @Nullable
    private static DataPackConfiguration getDataPacks(File file, DataFixer datafixer) {
        try {
            NBTTagCompound nbttagcompound = NBTCompressedStreamTools.readCompressed(file);
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");

            nbttagcompound1.remove("Player");
            int i = nbttagcompound1.contains("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : -1;
            Dynamic<NBTBase> dynamic = datafixer.update(DataFixTypes.LEVEL.getType(), new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound1), i, SharedConstants.getCurrentVersion().getWorldVersion());

            return (DataPackConfiguration) dynamic.get("DataPacks").result().map(Convertable::readDataPackConfig).orElse(DataPackConfiguration.DEFAULT);
        } catch (Exception exception) {
            Convertable.LOGGER.error("Exception reading {}", file, exception);
            return null;
        }
    }

    static BiFunction<File, DataFixer, WorldDataServer> getLevelData(DynamicOps<NBTBase> dynamicops, DataPackConfiguration datapackconfiguration) {
        return (file, datafixer) -> {
            try {
                NBTTagCompound nbttagcompound = NBTCompressedStreamTools.readCompressed(file);
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");
                NBTTagCompound nbttagcompound2 = nbttagcompound1.contains("Player", 10) ? nbttagcompound1.getCompound("Player") : null;

                nbttagcompound1.remove("Player");
                int i = nbttagcompound1.contains("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : -1;
                Dynamic<NBTBase> dynamic = datafixer.update(DataFixTypes.LEVEL.getType(), new Dynamic(dynamicops, nbttagcompound1), i, SharedConstants.getCurrentVersion().getWorldVersion());
                Pair<GeneratorSettings, Lifecycle> pair = readWorldGenSettings(dynamic, datafixer, i);
                LevelVersion levelversion = LevelVersion.parse(dynamic);
                WorldSettings worldsettings = WorldSettings.parse(dynamic, datapackconfiguration);

                return WorldDataServer.parse(dynamic, datafixer, i, nbttagcompound2, worldsettings, levelversion, (GeneratorSettings) pair.getFirst(), (Lifecycle) pair.getSecond());
            } catch (Exception exception) {
                Convertable.LOGGER.error("Exception reading {}", file, exception);
                return null;
            }
        };
    }

    BiFunction<File, DataFixer, WorldInfo> levelSummaryReader(File file, boolean flag) {
        return (file1, datafixer) -> {
            try {
                NBTTagCompound nbttagcompound = NBTCompressedStreamTools.readCompressed(file1);
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");

                nbttagcompound1.remove("Player");
                int i = nbttagcompound1.contains("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : -1;
                Dynamic<NBTBase> dynamic = datafixer.update(DataFixTypes.LEVEL.getType(), new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound1), i, SharedConstants.getCurrentVersion().getWorldVersion());
                LevelVersion levelversion = LevelVersion.parse(dynamic);
                int j = levelversion.levelDataVersion();

                if (j != 19132 && j != 19133) {
                    return null;
                } else {
                    boolean flag1 = j != this.getStorageVersion();
                    File file2 = new File(file, "icon.png");
                    DataPackConfiguration datapackconfiguration = (DataPackConfiguration) dynamic.get("DataPacks").result().map(Convertable::readDataPackConfig).orElse(DataPackConfiguration.DEFAULT);
                    WorldSettings worldsettings = WorldSettings.parse(dynamic, datapackconfiguration);

                    return new WorldInfo(worldsettings, levelversion, file.getName(), flag1, flag, file2);
                }
            } catch (Exception exception) {
                Convertable.LOGGER.error("Exception reading {}", file1, exception);
                return null;
            }
        };
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

    public class ConversionSession implements AutoCloseable {

        final SessionLock lock;
        public final Path levelPath;
        private final String levelId;
        private final Map<SavedFile, Path> resources = Maps.newHashMap();

        public ConversionSession(String s) throws IOException {
            this.levelId = s;
            this.levelPath = Convertable.this.baseDir.resolve(s);
            this.lock = SessionLock.create(this.levelPath);
        }

        public String getLevelId() {
            return this.levelId;
        }

        public Path getLevelPath(SavedFile savedfile) {
            return (Path) this.resources.computeIfAbsent(savedfile, (savedfile1) -> {
                return this.levelPath.resolve(savedfile1.getId());
            });
        }

        public Path getDimensionPath(ResourceKey<World> resourcekey) {
            return DimensionManager.getStorageFolder(resourcekey, this.levelPath);
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
            return (WorldInfo) Convertable.this.readLevelData(this.levelPath.toFile(), Convertable.this.levelSummaryReader(this.levelPath.toFile(), false));
        }

        @Nullable
        public SaveData getDataTag(DynamicOps<NBTBase> dynamicops, DataPackConfiguration datapackconfiguration) {
            this.checkLock();
            return (SaveData) Convertable.this.readLevelData(this.levelPath.toFile(), Convertable.getLevelData(dynamicops, datapackconfiguration));
        }

        @Nullable
        public DataPackConfiguration getDataPacks() {
            this.checkLock();
            return (DataPackConfiguration) Convertable.this.readLevelData(this.levelPath.toFile(), Convertable::getDataPacks);
        }

        public void saveDataTag(IRegistryCustom iregistrycustom, SaveData savedata) {
            this.saveDataTag(iregistrycustom, savedata, (NBTTagCompound) null);
        }

        public void saveDataTag(IRegistryCustom iregistrycustom, SaveData savedata, @Nullable NBTTagCompound nbttagcompound) {
            File file = this.levelPath.toFile();
            NBTTagCompound nbttagcompound1 = savedata.createTag(iregistrycustom, nbttagcompound);
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();

            nbttagcompound2.put("Data", nbttagcompound1);

            try {
                File file1 = File.createTempFile("level", ".dat", file);

                NBTCompressedStreamTools.writeCompressed(nbttagcompound2, file1);
                File file2 = new File(file, "level.dat_old");
                File file3 = new File(file, "level.dat");

                SystemUtils.safeReplaceFile(file3, file1, file2);
            } catch (Exception exception) {
                Convertable.LOGGER.error("Failed to save level {}", file, exception);
            }

        }

        public Optional<Path> getIconFile() {
            return !this.lock.isValid() ? Optional.empty() : Optional.of(this.levelPath.resolve("icon.png"));
        }

        public void deleteLevel() throws IOException {
            this.checkLock();
            final Path path = this.levelPath.resolve("session.lock");
            int i = 1;

            while (i <= 5) {
                Convertable.LOGGER.info("Attempt {}...", i);

                try {
                    Files.walkFileTree(this.levelPath, new SimpleFileVisitor<Path>() {
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
                                if (path1.equals(ConversionSession.this.levelPath)) {
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

                    Convertable.LOGGER.warn("Failed to delete {}", this.levelPath, ioexception);

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
            File file = new File(Convertable.this.baseDir.toFile(), this.levelId);

            if (file.exists()) {
                File file1 = new File(file, "level.dat");

                if (file1.exists()) {
                    NBTTagCompound nbttagcompound = NBTCompressedStreamTools.readCompressed(file1);
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");

                    nbttagcompound1.putString("LevelName", s);
                    NBTCompressedStreamTools.writeCompressed(nbttagcompound, file1);
                }

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

                Files.walkFileTree(this.levelPath, new SimpleFileVisitor<Path>() {
                    public FileVisitResult visitFile(Path path3, BasicFileAttributes basicfileattributes) throws IOException {
                        if (path3.endsWith("session.lock")) {
                            return FileVisitResult.CONTINUE;
                        } else {
                            String s2 = path2.resolve(ConversionSession.this.levelPath.relativize(path3)).toString().replace('\\', '/');
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

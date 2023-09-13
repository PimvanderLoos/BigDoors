package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.SessionLock;
import net.minecraft.util.datafix.DataConverterRegistry;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.fixes.DataConverterTypes;
import net.minecraft.world.level.DataPackConfiguration;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldSettings;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;
import net.minecraft.world.level.levelgen.GeneratorSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Convertable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateTimeFormatter b = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
    private static final ImmutableList<String> c = ImmutableList.of("RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest");
    public final Path universe;
    private final Path backupUniverse;
    private final DataFixer f;

    public Convertable(Path path, Path path1, DataFixer datafixer) {
        this.f = datafixer;

        try {
            Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
        } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
        }

        this.universe = path;
        this.backupUniverse = path1;
    }

    public static Convertable a(Path path) {
        return new Convertable(path, path.resolve("../backups"), DataConverterRegistry.a());
    }

    private static <T> Pair<GeneratorSettings, Lifecycle> a(Dynamic<T> dynamic, DataFixer datafixer, int i) {
        Dynamic<T> dynamic1 = dynamic.get("WorldGenSettings").orElseEmptyMap();
        UnmodifiableIterator unmodifiableiterator = Convertable.c.iterator();

        while (unmodifiableiterator.hasNext()) {
            String s = (String) unmodifiableiterator.next();
            Optional<? extends Dynamic<?>> optional = dynamic.get(s).result();

            if (optional.isPresent()) {
                dynamic1 = dynamic1.set(s, (Dynamic) optional.get());
            }
        }

        Dynamic<T> dynamic2 = datafixer.update(DataConverterTypes.WORLD_GEN_SETTINGS, dynamic1, i, SharedConstants.getGameVersion().getWorldVersion());
        DataResult<GeneratorSettings> dataresult = GeneratorSettings.a.parse(dynamic2);
        Logger logger = Convertable.LOGGER;

        logger.getClass();
        return Pair.of(dataresult.resultOrPartial(SystemUtils.a("WorldGenSettings: ", logger::error)).orElseGet(() -> {
            DataResult dataresult1 = RegistryLookupCodec.a(IRegistry.K).codec().parse(dynamic2);
            Logger logger1 = Convertable.LOGGER;

            logger1.getClass();
            IRegistry<DimensionManager> iregistry = (IRegistry) dataresult1.resultOrPartial(SystemUtils.a("Dimension type registry: ", logger1::error)).orElseThrow(() -> {
                return new IllegalStateException("Failed to get dimension registry");
            });

            dataresult1 = RegistryLookupCodec.a(IRegistry.ay).codec().parse(dynamic2);
            logger1 = Convertable.LOGGER;
            logger1.getClass();
            IRegistry<BiomeBase> iregistry1 = (IRegistry) dataresult1.resultOrPartial(SystemUtils.a("Biome registry: ", logger1::error)).orElseThrow(() -> {
                return new IllegalStateException("Failed to get biome registry");
            });

            dataresult1 = RegistryLookupCodec.a(IRegistry.ar).codec().parse(dynamic2);
            logger1 = Convertable.LOGGER;
            logger1.getClass();
            IRegistry<GeneratorSettingBase> iregistry2 = (IRegistry) dataresult1.resultOrPartial(SystemUtils.a("Noise settings registry: ", logger1::error)).orElseThrow(() -> {
                return new IllegalStateException("Failed to get noise settings registry");
            });

            return GeneratorSettings.a(iregistry, iregistry1, iregistry2);
        }), dataresult.lifecycle());
    }

    private static DataPackConfiguration a(Dynamic<?> dynamic) {
        DataResult dataresult = DataPackConfiguration.b.parse(dynamic);
        Logger logger = Convertable.LOGGER;

        logger.getClass();
        return (DataPackConfiguration) dataresult.resultOrPartial(logger::error).orElse(DataPackConfiguration.a);
    }

    private int g() {
        return 19133;
    }

    @Nullable
    private <T> T a(File file, BiFunction<File, DataFixer, T> bifunction) {
        if (!file.exists()) {
            return null;
        } else {
            File file1 = new File(file, "level.dat");

            if (file1.exists()) {
                T t0 = bifunction.apply(file1, this.f);

                if (t0 != null) {
                    return t0;
                }
            }

            file1 = new File(file, "level.dat_old");
            return file1.exists() ? bifunction.apply(file1, this.f) : null;
        }
    }

    @Nullable
    private static DataPackConfiguration b(File file, DataFixer datafixer) {
        try {
            NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(file);
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");

            nbttagcompound1.remove("Player");
            int i = nbttagcompound1.hasKeyOfType("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : -1;
            Dynamic<NBTBase> dynamic = datafixer.update(DataFixTypes.LEVEL.a(), new Dynamic(DynamicOpsNBT.a, nbttagcompound1), i, SharedConstants.getGameVersion().getWorldVersion());

            return (DataPackConfiguration) dynamic.get("DataPacks").result().map(Convertable::a).orElse(DataPackConfiguration.a);
        } catch (Exception exception) {
            Convertable.LOGGER.error("Exception reading {}", file, exception);
            return null;
        }
    }

    private static BiFunction<File, DataFixer, WorldDataServer> b(DynamicOps<NBTBase> dynamicops, DataPackConfiguration datapackconfiguration) {
        return (file, datafixer) -> {
            try {
                NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(file);
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");
                NBTTagCompound nbttagcompound2 = nbttagcompound1.hasKeyOfType("Player", 10) ? nbttagcompound1.getCompound("Player") : null;

                nbttagcompound1.remove("Player");
                int i = nbttagcompound1.hasKeyOfType("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : -1;
                Dynamic<NBTBase> dynamic = datafixer.update(DataFixTypes.LEVEL.a(), new Dynamic(dynamicops, nbttagcompound1), i, SharedConstants.getGameVersion().getWorldVersion());
                Pair<GeneratorSettings, Lifecycle> pair = a(dynamic, datafixer, i);
                LevelVersion levelversion = LevelVersion.a(dynamic);
                WorldSettings worldsettings = WorldSettings.a(dynamic, datapackconfiguration);

                return WorldDataServer.a(dynamic, datafixer, i, nbttagcompound2, worldsettings, levelversion, (GeneratorSettings) pair.getFirst(), (Lifecycle) pair.getSecond());
            } catch (Exception exception) {
                Convertable.LOGGER.error("Exception reading {}", file, exception);
                return null;
            }
        };
    }

    private BiFunction<File, DataFixer, WorldInfo> a(File file, boolean flag) {
        return (file1, datafixer) -> {
            try {
                NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(file1);
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");

                nbttagcompound1.remove("Player");
                int i = nbttagcompound1.hasKeyOfType("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : -1;
                Dynamic<NBTBase> dynamic = datafixer.update(DataFixTypes.LEVEL.a(), new Dynamic(DynamicOpsNBT.a, nbttagcompound1), i, SharedConstants.getGameVersion().getWorldVersion());
                LevelVersion levelversion = LevelVersion.a(dynamic);
                int j = levelversion.a();

                if (j != 19132 && j != 19133) {
                    return null;
                } else {
                    boolean flag1 = j != this.g();
                    File file2 = new File(file, "icon.png");
                    DataPackConfiguration datapackconfiguration = (DataPackConfiguration) dynamic.get("DataPacks").result().map(Convertable::a).orElse(DataPackConfiguration.a);
                    WorldSettings worldsettings = WorldSettings.a(dynamic, datapackconfiguration);

                    return new WorldInfo(worldsettings, levelversion, file.getName(), flag1, flag, file2);
                }
            } catch (Exception exception) {
                Convertable.LOGGER.error("Exception reading {}", file1, exception);
                return null;
            }
        };
    }

    public Convertable.ConversionSession c(String s) throws IOException {
        return new Convertable.ConversionSession(s);
    }

    public class ConversionSession implements AutoCloseable {

        private final SessionLock lock;
        public final Path folder;
        private final String levelName;
        private final Map<SavedFile, Path> e = Maps.newHashMap();

        public ConversionSession(String s) throws IOException {
            this.levelName = s;
            this.folder = Convertable.this.universe.resolve(s);
            this.lock = SessionLock.a(this.folder);
        }

        public String getLevelName() {
            return this.levelName;
        }

        public Path getWorldFolder(SavedFile savedfile) {
            return (Path) this.e.computeIfAbsent(savedfile, (savedfile1) -> {
                return this.folder.resolve(savedfile1.a());
            });
        }

        public File a(ResourceKey<World> resourcekey) {
            return DimensionManager.a(resourcekey, this.folder.toFile());
        }

        private void checkSession() {
            if (!this.lock.a()) {
                throw new IllegalStateException("Lock is no longer valid");
            }
        }

        public WorldNBTStorage b() {
            this.checkSession();
            return new WorldNBTStorage(this, Convertable.this.f);
        }

        public boolean isConvertable() {
            WorldInfo worldinfo = this.d();

            return worldinfo != null && worldinfo.k().a() != Convertable.this.g();
        }

        public boolean convert(IProgressUpdate iprogressupdate) {
            this.checkSession();
            return WorldUpgraderIterator.a(this, iprogressupdate);
        }

        @Nullable
        public WorldInfo d() {
            this.checkSession();
            return (WorldInfo) Convertable.this.a(this.folder.toFile(), Convertable.this.a(this.folder.toFile(), false));
        }

        @Nullable
        public SaveData a(DynamicOps<NBTBase> dynamicops, DataPackConfiguration datapackconfiguration) {
            this.checkSession();
            return (SaveData) Convertable.this.a(this.folder.toFile(), Convertable.b(dynamicops, datapackconfiguration));
        }

        @Nullable
        public DataPackConfiguration e() {
            this.checkSession();
            return (DataPackConfiguration) Convertable.this.a(this.folder.toFile(), (file, datafixer) -> {
                return Convertable.b(file, datafixer);
            });
        }

        public void a(IRegistryCustom iregistrycustom, SaveData savedata) {
            this.a(iregistrycustom, savedata, (NBTTagCompound) null);
        }

        public void a(IRegistryCustom iregistrycustom, SaveData savedata, @Nullable NBTTagCompound nbttagcompound) {
            File file = this.folder.toFile();
            NBTTagCompound nbttagcompound1 = savedata.a(iregistrycustom, nbttagcompound);
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();

            nbttagcompound2.set("Data", nbttagcompound1);

            try {
                File file1 = File.createTempFile("level", ".dat", file);

                NBTCompressedStreamTools.a(nbttagcompound2, file1);
                File file2 = new File(file, "level.dat_old");
                File file3 = new File(file, "level.dat");

                SystemUtils.a(file3, file1, file2);
            } catch (Exception exception) {
                Convertable.LOGGER.error("Failed to save level {}", file, exception);
            }

        }

        public File f() {
            this.checkSession();
            return this.folder.resolve("icon.png").toFile();
        }

        public void close() throws IOException {
            this.lock.close();
        }
    }
}

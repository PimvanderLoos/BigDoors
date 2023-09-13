package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.CrashReport;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.level.progress.WorldLoadListenerLogger;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.ResourcePackRepository;
import net.minecraft.server.packs.repository.ResourcePackSource;
import net.minecraft.server.packs.repository.ResourcePackSourceFolder;
import net.minecraft.server.packs.repository.ResourcePackSourceVanilla;
import net.minecraft.util.MathHelper;
import net.minecraft.util.datafix.DataConverterRegistry;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.DataPackConfiguration;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.WorldSettings;
import net.minecraft.world.level.levelgen.GeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.SaveData;
import net.minecraft.world.level.storage.SavedFile;
import net.minecraft.world.level.storage.WorldDataServer;
import net.minecraft.world.level.storage.WorldInfo;
import org.slf4j.Logger;

public class Main {

    private static final Logger LOGGER = LogUtils.getLogger();

    public Main() {}

    @DontObfuscate
    public static void main(String[] astring) {
        SharedConstants.tryDetectVersion();
        OptionParser optionparser = new OptionParser();
        OptionSpec<Void> optionspec = optionparser.accepts("nogui");
        OptionSpec<Void> optionspec1 = optionparser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
        OptionSpec<Void> optionspec2 = optionparser.accepts("demo");
        OptionSpec<Void> optionspec3 = optionparser.accepts("bonusChest");
        OptionSpec<Void> optionspec4 = optionparser.accepts("forceUpgrade");
        OptionSpec<Void> optionspec5 = optionparser.accepts("eraseCache");
        OptionSpec<Void> optionspec6 = optionparser.accepts("safeMode", "Loads level with vanilla datapack only");
        OptionSpec<Void> optionspec7 = optionparser.accepts("help").forHelp();
        OptionSpec<String> optionspec8 = optionparser.accepts("singleplayer").withRequiredArg();
        OptionSpec<String> optionspec9 = optionparser.accepts("universe").withRequiredArg().defaultsTo(".", new String[0]);
        OptionSpec<String> optionspec10 = optionparser.accepts("world").withRequiredArg();
        OptionSpec<Integer> optionspec11 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(-1, new Integer[0]);
        OptionSpec<String> optionspec12 = optionparser.accepts("serverId").withRequiredArg();
        OptionSpec<Void> optionspec13 = optionparser.accepts("jfrProfile");
        NonOptionArgumentSpec nonoptionargumentspec = optionparser.nonOptions();

        try {
            OptionSet optionset = optionparser.parse(astring);

            if (optionset.has(optionspec7)) {
                optionparser.printHelpOn(System.err);
                return;
            }

            CrashReport.preload();
            if (optionset.has(optionspec13)) {
                JvmProfiler.INSTANCE.start(Environment.SERVER);
            }

            DispenserRegistry.bootStrap();
            DispenserRegistry.validate();
            SystemUtils.startTimerHackThread();
            Path path = Paths.get("server.properties");
            DedicatedServerSettings dedicatedserversettings = new DedicatedServerSettings(path);

            dedicatedserversettings.forceSave();
            Path path1 = Paths.get("eula.txt");
            EULA eula = new EULA(path1);

            if (optionset.has(optionspec1)) {
                Main.LOGGER.info("Initialized '{}' and '{}'", path.toAbsolutePath(), path1.toAbsolutePath());
                return;
            }

            if (!eula.hasAgreedToEULA()) {
                Main.LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
                return;
            }

            File file = new File((String) optionset.valueOf(optionspec9));
            Services services = Services.create(new YggdrasilAuthenticationService(Proxy.NO_PROXY), file);
            String s = (String) Optional.ofNullable((String) optionset.valueOf(optionspec10)).orElse(dedicatedserversettings.getProperties().levelName);
            Convertable convertable = Convertable.createDefault(file.toPath());
            Convertable.ConversionSession convertable_conversionsession = convertable.createAccess(s);
            WorldInfo worldinfo = convertable_conversionsession.getSummary();

            if (worldinfo != null) {
                if (worldinfo.requiresManualConversion()) {
                    Main.LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
                    return;
                }

                if (!worldinfo.isCompatible()) {
                    Main.LOGGER.info("This world was created by an incompatible version.");
                    return;
                }
            }

            boolean flag = optionset.has(optionspec6);

            if (flag) {
                Main.LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
            }

            ResourcePackRepository resourcepackrepository = new ResourcePackRepository(EnumResourcePackType.SERVER_DATA, new ResourcePackSource[]{new ResourcePackSourceVanilla(), new ResourcePackSourceFolder(convertable_conversionsession.getLevelPath(SavedFile.DATAPACK_DIR).toFile(), PackSource.WORLD)});

            WorldStem worldstem;

            try {
                DataPackConfiguration datapackconfiguration = (DataPackConfiguration) Objects.requireNonNullElse(convertable_conversionsession.getDataPacks(), DataPackConfiguration.DEFAULT);
                WorldLoader.b worldloader_b = new WorldLoader.b(resourcepackrepository, datapackconfiguration, flag);
                WorldLoader.a worldloader_a = new WorldLoader.a(worldloader_b, CommandDispatcher.ServerType.DEDICATED, dedicatedserversettings.getProperties().functionPermissionLevel);

                worldstem = (WorldStem) SystemUtils.blockUntilDone((executor) -> {
                    return WorldStem.load(worldloader_a, (iresourcemanager, datapackconfiguration1) -> {
                        IRegistryCustom.e iregistrycustom_e = IRegistryCustom.builtinCopy();
                        DynamicOps<NBTBase> dynamicops = RegistryOps.createAndLoad(DynamicOpsNBT.INSTANCE, iregistrycustom_e, iresourcemanager);
                        SaveData savedata = convertable_conversionsession.getDataTag(dynamicops, datapackconfiguration1, iregistrycustom_e.allElementsLifecycle());

                        if (savedata != null) {
                            return Pair.of(savedata, iregistrycustom_e.freeze());
                        } else {
                            WorldSettings worldsettings;
                            GeneratorSettings generatorsettings;

                            if (optionset.has(optionspec2)) {
                                worldsettings = MinecraftServer.DEMO_SETTINGS;
                                generatorsettings = WorldPresets.demoSettings(iregistrycustom_e);
                            } else {
                                DedicatedServerProperties dedicatedserverproperties = dedicatedserversettings.getProperties();

                                worldsettings = new WorldSettings(dedicatedserverproperties.levelName, dedicatedserverproperties.gamemode, dedicatedserverproperties.hardcore, dedicatedserverproperties.difficulty, false, new GameRules(), datapackconfiguration1);
                                generatorsettings = optionset.has(optionspec3) ? dedicatedserverproperties.getWorldGenSettings(iregistrycustom_e).withBonusChest() : dedicatedserverproperties.getWorldGenSettings(iregistrycustom_e);
                            }

                            WorldDataServer worlddataserver = new WorldDataServer(worldsettings, generatorsettings, Lifecycle.stable());

                            return Pair.of(worlddataserver, iregistrycustom_e.freeze());
                        }
                    }, SystemUtils.backgroundExecutor(), executor);
                }).get();
            } catch (Exception exception) {
                Main.LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", exception);
                return;
            }

            IRegistryCustom.Dimension iregistrycustom_dimension = worldstem.registryAccess();

            dedicatedserversettings.getProperties().getWorldGenSettings(iregistrycustom_dimension);
            SaveData savedata = worldstem.worldData();

            if (optionset.has(optionspec4)) {
                forceUpgrade(convertable_conversionsession, DataConverterRegistry.getDataFixer(), optionset.has(optionspec5), () -> {
                    return true;
                }, savedata.worldGenSettings());
            }

            convertable_conversionsession.saveDataTag(iregistrycustom_dimension, savedata);
            final DedicatedServer dedicatedserver = (DedicatedServer) MinecraftServer.spin((thread) -> {
                DedicatedServer dedicatedserver1 = new DedicatedServer(thread, convertable_conversionsession, resourcepackrepository, worldstem, dedicatedserversettings, DataConverterRegistry.getDataFixer(), services, WorldLoadListenerLogger::new);

                dedicatedserver1.setSingleplayerProfile(optionset.has(optionspec8) ? new GameProfile((UUID) null, (String) optionset.valueOf(optionspec8)) : null);
                dedicatedserver1.setPort((Integer) optionset.valueOf(optionspec11));
                dedicatedserver1.setDemo(optionset.has(optionspec2));
                dedicatedserver1.setId((String) optionset.valueOf(optionspec12));
                boolean flag1 = !optionset.has(optionspec) && !optionset.valuesOf(nonoptionargumentspec).contains("nogui");

                if (flag1 && !GraphicsEnvironment.isHeadless()) {
                    dedicatedserver1.showGui();
                }

                return dedicatedserver1;
            });
            Thread thread = new Thread("Server Shutdown Thread") {
                public void run() {
                    dedicatedserver.halt(true);
                }
            };

            thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(Main.LOGGER));
            Runtime.getRuntime().addShutdownHook(thread);
        } catch (Exception exception1) {
            Main.LOGGER.error(LogUtils.FATAL_MARKER, "Failed to start the minecraft server", exception1);
        }

    }

    public static void forceUpgrade(Convertable.ConversionSession convertable_conversionsession, DataFixer datafixer, boolean flag, BooleanSupplier booleansupplier, GeneratorSettings generatorsettings) {
        Main.LOGGER.info("Forcing world upgrade!");
        WorldUpgrader worldupgrader = new WorldUpgrader(convertable_conversionsession, datafixer, generatorsettings, flag);
        IChatBaseComponent ichatbasecomponent = null;

        while (!worldupgrader.isFinished()) {
            IChatBaseComponent ichatbasecomponent1 = worldupgrader.getStatus();

            if (ichatbasecomponent != ichatbasecomponent1) {
                ichatbasecomponent = ichatbasecomponent1;
                Main.LOGGER.info(worldupgrader.getStatus().getString());
            }

            int i = worldupgrader.getTotalChunks();

            if (i > 0) {
                int j = worldupgrader.getConverted() + worldupgrader.getSkipped();

                Main.LOGGER.info("{}% completed ({} / {} chunks)...", new Object[]{MathHelper.floor((float) j / (float) i * 100.0F), j, i});
            }

            if (!booleansupplier.getAsBoolean()) {
                worldupgrader.cancel();
            } else {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException interruptedexception) {
                    ;
                }
            }
        }

    }
}

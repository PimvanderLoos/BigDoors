package net.minecraft.server;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer implements IAsyncTaskHandler, IMojangStatistics, ICommandListener, Runnable {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final File a = new File("usercache.json");
    public Convertable convertable;
    private final MojangStatisticsGenerator j = new MojangStatisticsGenerator("server", this, SystemUtils.b());
    public File universe;
    private final List<ITickable> l = Lists.newArrayList();
    public final MethodProfiler methodProfiler = new MethodProfiler();
    private final ServerConnection m;
    private final ServerPing n = new ServerPing();
    private final Random o = new Random();
    public final DataFixer dataConverterManager;
    private String serverIp;
    private int r = -1;
    public WorldServer[] worldServer;
    private PlayerList s;
    private boolean isRunning = true;
    private boolean isStopped;
    private int ticks;
    protected final Proxy d;
    private IChatBaseComponent w;
    private int x;
    private boolean onlineMode;
    private boolean z;
    private boolean spawnAnimals;
    private boolean spawnNPCs;
    private boolean pvpMode;
    private boolean allowFlight;
    private String motd;
    private int F;
    private int G;
    public final long[] e = new long[100];
    public long[][] f;
    private KeyPair H;
    private String I;
    private String J;
    private boolean demoMode;
    private boolean M;
    private String N = "";
    private String O = "";
    private boolean P;
    private long Q;
    private IChatBaseComponent R;
    private boolean S;
    private boolean T;
    private final YggdrasilAuthenticationService U;
    private final MinecraftSessionService V;
    private final GameProfileRepository W;
    private final UserCache X;
    private long Y;
    protected final Queue<FutureTask<?>> g = Queues.newConcurrentLinkedQueue();
    private Thread serverThread;
    private long aa = SystemUtils.b();
    private final IReloadableResourceManager ac;
    private final ResourcePackRepository<ResourcePackLoader> resourcePackRepository;
    private ResourcePackSourceFolder resourcePackFolder;
    public CommandDispatcher commandDispatcher;
    private final CraftingManager ag;
    private final TagRegistry ah;
    private final ScoreboardServer ai;
    private final BossBattleCustomData aj;
    private final LootTableRegistry ak;
    private final AdvancementDataWorld al;
    private final CustomFunctionData am;
    private boolean an;
    private boolean forceUpgrade;
    private float ap;

    public MinecraftServer(@Nullable File file, Proxy proxy, DataFixer datafixer, CommandDispatcher commanddispatcher, YggdrasilAuthenticationService yggdrasilauthenticationservice, MinecraftSessionService minecraftsessionservice, GameProfileRepository gameprofilerepository, UserCache usercache) {
        this.ac = new ResourceManager(EnumResourcePackType.SERVER_DATA);
        this.resourcePackRepository = new ResourcePackRepository(ResourcePackLoader::new);
        this.ag = new CraftingManager();
        this.ah = new TagRegistry();
        this.ai = new ScoreboardServer(this);
        this.aj = new BossBattleCustomData(this);
        this.ak = new LootTableRegistry();
        this.al = new AdvancementDataWorld();
        this.am = new CustomFunctionData(this);
        this.d = proxy;
        this.commandDispatcher = commanddispatcher;
        this.U = yggdrasilauthenticationservice;
        this.V = minecraftsessionservice;
        this.W = gameprofilerepository;
        this.X = usercache;
        this.universe = file;
        this.m = file == null ? null : new ServerConnection(this);
        this.convertable = file == null ? null : new WorldLoaderServer(file.toPath(), file.toPath().resolve("../backups"), datafixer);
        this.dataConverterManager = datafixer;
        this.ac.a((IResourcePackListener) this.ah);
        this.ac.a((IResourcePackListener) this.ag);
        this.ac.a((IResourcePackListener) this.ak);
        this.ac.a((IResourcePackListener) this.am);
        this.ac.a((IResourcePackListener) this.al);
    }

    public abstract boolean init() throws IOException;

    public void convertWorld(String s) {
        if (this.getConvertable().isConvertable(s)) {
            MinecraftServer.LOGGER.info("Converting map!");
            this.b((IChatBaseComponent) (new ChatMessage("menu.convertingLevel", new Object[0])));
            this.getConvertable().convert(s, new IProgressUpdate() {
                private long b = SystemUtils.b();

                public void a(IChatBaseComponent ichatbasecomponent) {}

                public void a(int i) {
                    if (SystemUtils.b() - this.b >= 1000L) {
                        this.b = SystemUtils.b();
                        MinecraftServer.LOGGER.info("Converting... {}%", Integer.valueOf(i));
                    }

                }

                public void c(IChatBaseComponent ichatbasecomponent) {}
            });
        }

        if (this.forceUpgrade) {
            MinecraftServer.LOGGER.info("Forcing world upgrade!");
            WorldData worlddata = this.getConvertable().c(this.getWorld());

            if (worlddata != null) {
                WorldUpgrader worldupgrader = new WorldUpgrader(this.getWorld(), this.getConvertable(), worlddata);
                IChatBaseComponent ichatbasecomponent = null;

                while (!worldupgrader.b()) {
                    IChatBaseComponent ichatbasecomponent1 = worldupgrader.m();

                    if (ichatbasecomponent != ichatbasecomponent1) {
                        ichatbasecomponent = ichatbasecomponent1;
                        MinecraftServer.LOGGER.info(worldupgrader.m().getString());
                    }

                    int i = worldupgrader.j();

                    if (i > 0) {
                        int j = worldupgrader.k() + worldupgrader.l();

                        MinecraftServer.LOGGER.info("{}% completed ({} / {} chunks)...", Integer.valueOf(MathHelper.d((float) j / (float) i * 100.0F)), Integer.valueOf(j), Integer.valueOf(i));
                    }

                    if (this.isStopped()) {
                        worldupgrader.a();
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

    }

    protected synchronized void b(IChatBaseComponent ichatbasecomponent) {
        this.R = ichatbasecomponent;
    }

    public void a(String s, String s1, long i, WorldType worldtype, JsonElement jsonelement) {
        this.convertWorld(s);
        this.b((IChatBaseComponent) (new ChatMessage("menu.loadingLevel", new Object[0])));
        this.worldServer = new WorldServer[3];
        this.f = new long[this.worldServer.length][100];
        IDataManager idatamanager = this.convertable.a(s, this);

        this.a(this.getWorld(), idatamanager);
        WorldData worlddata = idatamanager.getWorldData();
        WorldSettings worldsettings;

        if (worlddata == null) {
            if (this.N()) {
                worldsettings = DemoWorldServer.a;
            } else {
                worldsettings = new WorldSettings(i, this.getGamemode(), this.getGenerateStructures(), this.isHardcore(), worldtype);
                worldsettings.setGeneratorSettings(jsonelement);
                if (this.M) {
                    worldsettings.a();
                }
            }

            worlddata = new WorldData(worldsettings, s1);
        } else {
            worlddata.a(s1);
            worldsettings = new WorldSettings(worlddata);
        }

        this.a(idatamanager.getDirectory(), worlddata);

        for (int j = 0; j < this.worldServer.length; ++j) {
            byte b0 = 0;

            if (j == 1) {
                b0 = -1;
            }

            if (j == 2) {
                b0 = 1;
            }

            if (j == 0) {
                if (this.N()) {
                    this.worldServer[j] = (WorldServer) (new DemoWorldServer(this, idatamanager, worlddata, b0, this.methodProfiler)).b();
                } else {
                    this.worldServer[j] = (WorldServer) (new WorldServer(this, idatamanager, worlddata, b0, this.methodProfiler)).b();
                }

                this.worldServer[j].a(worldsettings);
            } else {
                this.worldServer[j] = (WorldServer) (new SecondaryWorldServer(this, idatamanager, b0, this.worldServer[0], this.methodProfiler)).b();
            }

            this.worldServer[j].addIWorldAccess(new WorldManager(this, this.worldServer[j]));
            if (!this.J()) {
                this.worldServer[j].getWorldData().setGameType(this.getGamemode());
            }
        }

        this.s.setPlayerFileData(this.worldServer);
        if (worlddata.P() != null) {
            this.aR().a(worlddata.P());
        }

        this.a(this.getDifficulty());
        this.g_();
    }

    protected void a(File file, WorldData worlddata) {
        this.resourcePackRepository.a((ResourcePackSource) (new ResourcePackSourceVanilla()));
        this.resourcePackFolder = new ResourcePackSourceFolder(new File(file, "datapacks"));
        this.resourcePackRepository.a((ResourcePackSource) this.resourcePackFolder);
        this.resourcePackRepository.a();
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = worlddata.O().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            ResourcePackLoader resourcepackloader = this.resourcePackRepository.a(s);

            if (resourcepackloader != null) {
                arraylist.add(resourcepackloader);
            } else {
                MinecraftServer.LOGGER.warn("Missing data pack {}", s);
            }
        }

        this.resourcePackRepository.a((Collection) arraylist);
        this.a(worlddata);
    }

    protected void g_() {
        boolean flag = true;
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;
        boolean flag4 = true;

        this.b((IChatBaseComponent) (new ChatMessage("menu.generatingTerrain", new Object[0])));
        boolean flag5 = false;

        MinecraftServer.LOGGER.info("Preparing start region for level 0");
        WorldServer worldserver = this.worldServer[0];
        BlockPosition blockposition = worldserver.getSpawn();
        ArrayList arraylist = Lists.newArrayList();
        Set set = Sets.newConcurrentHashSet();
        Stopwatch stopwatch = Stopwatch.createStarted();

        for (int i = -192; i <= 192 && this.isRunning(); i += 16) {
            for (int j = -192; j <= 192 && this.isRunning(); j += 16) {
                arraylist.add(new ChunkCoordIntPair(blockposition.getX() + i >> 4, blockposition.getZ() + j >> 4));
            }

            CompletableFuture completablefuture = worldserver.getChunkProviderServer().a((Iterable) arraylist, (chunk) -> {
                set.add(chunk.getPos());
            });

            while (!completablefuture.isDone()) {
                try {
                    completablefuture.get(1L, TimeUnit.SECONDS);
                } catch (InterruptedException interruptedexception) {
                    throw new RuntimeException(interruptedexception);
                } catch (ExecutionException executionexception) {
                    if (executionexception.getCause() instanceof RuntimeException) {
                        throw (RuntimeException) executionexception.getCause();
                    }

                    throw new RuntimeException(executionexception.getCause());
                } catch (TimeoutException timeoutexception) {
                    this.a(new ChatMessage("menu.preparingSpawn", new Object[0]), set.size() * 100 / 625);
                }
            }

            this.a(new ChatMessage("menu.preparingSpawn", new Object[0]), set.size() * 100 / 625);
        }

        this.m();
        MinecraftServer.LOGGER.info("Time elapsed: {} ms", Long.valueOf(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
    }

    protected void a(String s, IDataManager idatamanager) {
        File file = new File(idatamanager.getDirectory(), "resources.zip");

        if (file.isFile()) {
            try {
                this.setResourcePack("level://" + URLEncoder.encode(s, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
            } catch (UnsupportedEncodingException unsupportedencodingexception) {
                MinecraftServer.LOGGER.warn("Something went wrong url encoding {}", s);
            }
        }

    }

    public abstract boolean getGenerateStructures();

    public abstract EnumGamemode getGamemode();

    public abstract EnumDifficulty getDifficulty();

    public abstract boolean isHardcore();

    public abstract int k();

    public abstract boolean l();

    protected void a(IChatBaseComponent ichatbasecomponent, int i) {
        this.w = ichatbasecomponent;
        this.x = i;
        MinecraftServer.LOGGER.info("{}: {}%", ichatbasecomponent.getString(), Integer.valueOf(i));
    }

    protected void m() {
        this.w = null;
        this.x = 0;
    }

    protected void saveChunks(boolean flag) {
        WorldServer[] aworldserver = this.worldServer;
        int i = aworldserver.length;

        for (int j = 0; j < i; ++j) {
            WorldServer worldserver = aworldserver[j];

            if (worldserver != null) {
                if (!flag) {
                    MinecraftServer.LOGGER.info("Saving chunks for level \'{}\'/{}", worldserver.getWorldData().getName(), worldserver.worldProvider.getDimensionManager().b());
                }

                try {
                    worldserver.save(true, (IProgressUpdate) null);
                } catch (ExceptionWorldConflict exceptionworldconflict) {
                    MinecraftServer.LOGGER.warn(exceptionworldconflict.getMessage());
                }
            }
        }

    }

    protected void stop() {
        MinecraftServer.LOGGER.info("Stopping server");
        if (this.getServerConnection() != null) {
            this.getServerConnection().b();
        }

        if (this.s != null) {
            MinecraftServer.LOGGER.info("Saving players");
            this.s.savePlayers();
            this.s.u();
        }

        if (this.worldServer != null) {
            MinecraftServer.LOGGER.info("Saving worlds");
            WorldServer[] aworldserver = this.worldServer;
            int i = aworldserver.length;

            int j;
            WorldServer worldserver;

            for (j = 0; j < i; ++j) {
                worldserver = aworldserver[j];
                if (worldserver != null) {
                    worldserver.savingDisabled = false;
                }
            }

            this.saveChunks(false);
            aworldserver = this.worldServer;
            i = aworldserver.length;

            for (j = 0; j < i; ++j) {
                worldserver = aworldserver[j];
                if (worldserver != null) {
                    worldserver.close();
                }
            }
        }

        if (this.j.d()) {
            this.j.e();
        }

    }

    public String getServerIp() {
        return this.serverIp;
    }

    public void b(String s) {
        this.serverIp = s;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void safeShutdown() {
        this.isRunning = false;
    }

    public void run() {
        try {
            if (this.init()) {
                this.aa = SystemUtils.b();
                this.n.setMOTD(new ChatComponentText(this.motd));
                this.n.setServerInfo(new ServerPing.ServerData("1.13", 393));
                this.a(this.n);

                while (this.isRunning) {
                    long i = SystemUtils.b() - this.aa;

                    if (i > 2000L && this.aa - this.Q >= 15000L) {
                        long j = i / 50L;

                        MinecraftServer.LOGGER.warn("Can\'t keep up! Is the server overloaded? Running {}ms or {} ticks behind", Long.valueOf(i), Long.valueOf(j));
                        this.aa += j * 50L;
                        this.Q = this.aa;
                    }

                    this.v();
                    this.aa += 50L;

                    while (SystemUtils.b() < this.aa) {
                        Thread.sleep(1L);
                    }

                    this.P = true;
                }
            } else {
                this.a((CrashReport) null);
            }
        } catch (Throwable throwable) {
            MinecraftServer.LOGGER.error("Encountered an unexpected exception", throwable);
            CrashReport crashreport;

            if (throwable instanceof ReportedException) {
                crashreport = this.b(((ReportedException) throwable).a());
            } else {
                crashreport = this.b(new CrashReport("Exception in server tick loop", throwable));
            }

            File file = new File(new File(this.t(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

            if (crashreport.a(file)) {
                MinecraftServer.LOGGER.error("This crash report has been saved to: {}", file.getAbsolutePath());
            } else {
                MinecraftServer.LOGGER.error("We were unable to save this crash report to disk.");
            }

            this.a(crashreport);
        } finally {
            try {
                this.isStopped = true;
                this.stop();
            } catch (Throwable throwable1) {
                MinecraftServer.LOGGER.error("Exception stopping the server", throwable1);
            } finally {
                this.u();
            }

        }

    }

    public void a(ServerPing serverping) {
        File file = this.c("server-icon.png");

        if (!file.exists()) {
            file = this.getConvertable().b(this.getWorld(), "icon.png");
        }

        if (file.isFile()) {
            ByteBuf bytebuf = Unpooled.buffer();

            try {
                BufferedImage bufferedimage = ImageIO.read(file);

                Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
                ByteBuffer bytebuffer = Base64.getEncoder().encode(bytebuf.nioBuffer());

                serverping.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(bytebuffer));
            } catch (Exception exception) {
                MinecraftServer.LOGGER.error("Couldn\'t load server icon", exception);
            } finally {
                bytebuf.release();
            }
        }

    }

    public File t() {
        return new File(".");
    }

    protected void a(CrashReport crashreport) {}

    public void u() {}

    protected void v() {
        long i = SystemUtils.c();

        ++this.ticks;
        if (this.S) {
            this.S = false;
            this.methodProfiler.a(this.ticks);
        }

        this.methodProfiler.a("root");
        this.w();
        if (i - this.Y >= 5000000000L) {
            this.Y = i;
            this.n.setPlayerSample(new ServerPing.ServerPingPlayerSample(this.B(), this.A()));
            GameProfile[] agameprofile = new GameProfile[Math.min(this.A(), 12)];
            int j = MathHelper.nextInt(this.o, 0, this.A() - agameprofile.length);

            for (int k = 0; k < agameprofile.length; ++k) {
                agameprofile[k] = ((EntityPlayer) this.s.v().get(j + k)).getProfile();
            }

            Collections.shuffle(Arrays.asList(agameprofile));
            this.n.b().a(agameprofile);
        }

        if (this.ticks % 900 == 0) {
            this.methodProfiler.a("save");
            this.s.savePlayers();
            this.saveChunks(true);
            this.methodProfiler.e();
        }

        this.methodProfiler.a("snooper");
        if (!this.j.d() && this.ticks > 100) {
            this.j.a();
        }

        if (this.ticks % 6000 == 0) {
            this.j.b();
        }

        this.methodProfiler.e();
        this.methodProfiler.a("tallying");
        long l = this.e[this.ticks % 100] = SystemUtils.c() - i;

        this.ap = this.ap * 0.8F + (float) l / 1000000.0F * 0.19999999F;
        this.methodProfiler.e();
        this.methodProfiler.e();
    }

    public void w() {
        this.methodProfiler.a("jobs");

        FutureTask futuretask;

        while ((futuretask = (FutureTask) this.g.poll()) != null) {
            SystemUtils.a(futuretask, MinecraftServer.LOGGER);
        }

        this.methodProfiler.c("commandFunctions");
        this.getFunctionData().Y_();
        this.methodProfiler.c("levels");

        int i;

        for (i = 0; i < this.worldServer.length; ++i) {
            long j = SystemUtils.c();

            if (i == 0 || this.getAllowNether()) {
                WorldServer worldserver = this.worldServer[i];

                this.methodProfiler.a(() -> {
                    return worldserver.getWorldData().getName();
                });
                if (this.ticks % 20 == 0) {
                    this.methodProfiler.a("timeSync");
                    this.s.a((Packet) (new PacketPlayOutUpdateTime(worldserver.getTime(), worldserver.getDayTime(), worldserver.getGameRules().getBoolean("doDaylightCycle"))), worldserver.worldProvider.getDimensionManager().getDimensionID());
                    this.methodProfiler.e();
                }

                this.methodProfiler.a("tick");

                CrashReport crashreport;

                try {
                    worldserver.doTick();
                } catch (Throwable throwable) {
                    crashreport = CrashReport.a(throwable, "Exception ticking world");
                    worldserver.a(crashreport);
                    throw new ReportedException(crashreport);
                }

                try {
                    worldserver.tickEntities();
                } catch (Throwable throwable1) {
                    crashreport = CrashReport.a(throwable1, "Exception ticking world entities");
                    worldserver.a(crashreport);
                    throw new ReportedException(crashreport);
                }

                this.methodProfiler.e();
                this.methodProfiler.a("tracker");
                worldserver.getTracker().updatePlayers();
                this.methodProfiler.e();
                this.methodProfiler.e();
            }

            this.f[i][this.ticks % 100] = SystemUtils.c() - j;
        }

        this.methodProfiler.c("connection");
        this.getServerConnection().c();
        this.methodProfiler.c("players");
        this.s.tick();
        this.methodProfiler.c("tickables");

        for (i = 0; i < this.l.size(); ++i) {
            ((ITickable) this.l.get(i)).Y_();
        }

        this.methodProfiler.e();
    }

    public boolean getAllowNether() {
        return true;
    }

    public void a(ITickable itickable) {
        this.l.add(itickable);
    }

    public static void main(String[] astring) {
        DispenserRegistry.c();

        try {
            boolean flag = true;
            String s = null;
            String s1 = ".";
            String s2 = null;
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;
            int i = -1;

            for (int j = 0; j < astring.length; ++j) {
                String s3 = astring[j];
                String s4 = j == astring.length - 1 ? null : astring[j + 1];
                boolean flag4 = false;

                if (!"nogui".equals(s3) && !"--nogui".equals(s3)) {
                    if ("--port".equals(s3) && s4 != null) {
                        flag4 = true;

                        try {
                            i = Integer.parseInt(s4);
                        } catch (NumberFormatException numberformatexception) {
                            ;
                        }
                    } else if ("--singleplayer".equals(s3) && s4 != null) {
                        flag4 = true;
                        s = s4;
                    } else if ("--universe".equals(s3) && s4 != null) {
                        flag4 = true;
                        s1 = s4;
                    } else if ("--world".equals(s3) && s4 != null) {
                        flag4 = true;
                        s2 = s4;
                    } else if ("--demo".equals(s3)) {
                        flag1 = true;
                    } else if ("--bonusChest".equals(s3)) {
                        flag2 = true;
                    } else if ("--forceUpgrade".equals(s3)) {
                        flag3 = true;
                    }
                } else {
                    flag = false;
                }

                if (flag4) {
                    ++j;
                }
            }

            YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
            MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
            GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
            UserCache usercache = new UserCache(gameprofilerepository, new File(s1, MinecraftServer.a.getName()));
            final DedicatedServer dedicatedserver = new DedicatedServer(new File(s1), DataConverterRegistry.a(), yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, usercache);

            if (s != null) {
                dedicatedserver.h(s);
            }

            if (s2 != null) {
                dedicatedserver.setWorld(s2);
            }

            if (i >= 0) {
                dedicatedserver.setPort(i);
            }

            if (flag1) {
                dedicatedserver.c(true);
            }

            if (flag2) {
                dedicatedserver.d(true);
            }

            if (flag && !GraphicsEnvironment.isHeadless()) {
                dedicatedserver.aY();
            }

            if (flag3) {
                dedicatedserver.setForceUpgrade(true);
            }

            dedicatedserver.y();
            Thread thread = new Thread("Server Shutdown Thread") {
                public void run() {
                    dedicatedserver.stop();
                }
            };

            thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(MinecraftServer.LOGGER));
            Runtime.getRuntime().addShutdownHook(thread);
        } catch (Exception exception) {
            MinecraftServer.LOGGER.fatal("Failed to start the minecraft server", exception);
        }

    }

    protected void setForceUpgrade(boolean flag) {
        this.forceUpgrade = flag;
    }

    public void y() {
        this.serverThread = new Thread(this, "Server thread");
        this.serverThread.setUncaughtExceptionHandler((thread, throwable) -> {
            MinecraftServer.LOGGER.error(throwable);
        });
        this.serverThread.start();
    }

    public File c(String s) {
        return new File(this.t(), s);
    }

    public void info(String s) {
        MinecraftServer.LOGGER.info(s);
    }

    public void warning(String s) {
        MinecraftServer.LOGGER.warn(s);
    }

    public WorldServer getWorldServer(int i) {
        return i == -1 ? this.worldServer[1] : (i == 1 ? this.worldServer[2] : this.worldServer[0]);
    }

    public WorldServer a(DimensionManager dimensionmanager) {
        return dimensionmanager == DimensionManager.NETHER ? this.worldServer[1] : (dimensionmanager == DimensionManager.THE_END ? this.worldServer[2] : this.worldServer[0]);
    }

    public String getVersion() {
        return "1.13";
    }

    public int A() {
        return this.s.getPlayerCount();
    }

    public int B() {
        return this.s.getMaxPlayers();
    }

    public String[] getPlayers() {
        return this.s.f();
    }

    public boolean isDebugging() {
        return false;
    }

    public void f(String s) {
        MinecraftServer.LOGGER.error(s);
    }

    public void g(String s) {
        if (this.isDebugging()) {
            MinecraftServer.LOGGER.info(s);
        }

    }

    public String getServerModName() {
        return "vanilla";
    }

    public CrashReport b(CrashReport crashreport) {
        crashreport.g().a("Profiler Position", () -> {
            return this.methodProfiler.a() ? this.methodProfiler.f() : "N/A (disabled)";
        });
        if (this.s != null) {
            crashreport.g().a("Player Count", () -> {
                return this.s.getPlayerCount() + " / " + this.s.getMaxPlayers() + "; " + this.s.v();
            });
        }

        crashreport.g().a("Data Packs", () -> {
            StringBuilder stringbuilder = new StringBuilder();
            Iterator iterator = this.resourcePackRepository.d().iterator();

            while (iterator.hasNext()) {
                ResourcePackLoader resourcepackloader = (ResourcePackLoader) iterator.next();

                if (stringbuilder.length() > 0) {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(resourcepackloader.e());
                if (!resourcepackloader.c().a()) {
                    stringbuilder.append(" (incompatible)");
                }
            }

            return stringbuilder.toString();
        });
        return crashreport;
    }

    public boolean F() {
        return this.universe != null;
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        MinecraftServer.LOGGER.info(ichatbasecomponent.getString());
    }

    public KeyPair G() {
        return this.H;
    }

    public int H() {
        return this.r;
    }

    public void setPort(int i) {
        this.r = i;
    }

    public String I() {
        return this.I;
    }

    public void h(String s) {
        this.I = s;
    }

    public boolean J() {
        return this.I != null;
    }

    public String getWorld() {
        return this.J;
    }

    public void setWorld(String s) {
        this.J = s;
    }

    public void a(KeyPair keypair) {
        this.H = keypair;
    }

    public void a(EnumDifficulty enumdifficulty) {
        WorldServer[] aworldserver = this.worldServer;
        int i = aworldserver.length;

        for (int j = 0; j < i; ++j) {
            WorldServer worldserver = aworldserver[j];

            if (worldserver != null) {
                if (worldserver.getWorldData().isHardcore()) {
                    worldserver.getWorldData().setDifficulty(EnumDifficulty.HARD);
                    worldserver.setSpawnFlags(true, true);
                } else if (this.J()) {
                    worldserver.getWorldData().setDifficulty(enumdifficulty);
                    worldserver.setSpawnFlags(worldserver.getDifficulty() != EnumDifficulty.PEACEFUL, true);
                } else {
                    worldserver.getWorldData().setDifficulty(enumdifficulty);
                    worldserver.setSpawnFlags(this.getSpawnMonsters(), this.spawnAnimals);
                }
            }
        }

    }

    public boolean getSpawnMonsters() {
        return true;
    }

    public boolean N() {
        return this.demoMode;
    }

    public void c(boolean flag) {
        this.demoMode = flag;
    }

    public void d(boolean flag) {
        this.M = flag;
    }

    public Convertable getConvertable() {
        return this.convertable;
    }

    public String getResourcePack() {
        return this.N;
    }

    public String getResourcePackHash() {
        return this.O;
    }

    public void setResourcePack(String s, String s1) {
        this.N = s;
        this.O = s1;
    }

    public void a(MojangStatisticsGenerator mojangstatisticsgenerator) {
        mojangstatisticsgenerator.a("whitelist_enabled", Boolean.valueOf(false));
        mojangstatisticsgenerator.a("whitelist_count", Integer.valueOf(0));
        if (this.s != null) {
            mojangstatisticsgenerator.a("players_current", Integer.valueOf(this.A()));
            mojangstatisticsgenerator.a("players_max", Integer.valueOf(this.B()));
            mojangstatisticsgenerator.a("players_seen", Integer.valueOf(this.s.getSeenPlayers().length));
        }

        mojangstatisticsgenerator.a("uses_auth", Boolean.valueOf(this.onlineMode));
        mojangstatisticsgenerator.a("gui_state", this.ai() ? "enabled" : "disabled");
        mojangstatisticsgenerator.a("run_time", Long.valueOf((SystemUtils.b() - mojangstatisticsgenerator.g()) / 60L * 1000L));
        mojangstatisticsgenerator.a("avg_tick_ms", Integer.valueOf((int) (MathHelper.a(this.e) * 1.0E-6D)));
        int i = 0;

        if (this.worldServer != null) {
            WorldServer[] aworldserver = this.worldServer;
            int j = aworldserver.length;

            for (int k = 0; k < j; ++k) {
                WorldServer worldserver = aworldserver[k];

                if (worldserver != null) {
                    WorldData worlddata = worldserver.getWorldData();

                    mojangstatisticsgenerator.a("world[" + i + "][dimension]", Integer.valueOf(worldserver.worldProvider.getDimensionManager().getDimensionID()));
                    mojangstatisticsgenerator.a("world[" + i + "][mode]", worlddata.getGameType());
                    mojangstatisticsgenerator.a("world[" + i + "][difficulty]", worldserver.getDifficulty());
                    mojangstatisticsgenerator.a("world[" + i + "][hardcore]", Boolean.valueOf(worlddata.isHardcore()));
                    mojangstatisticsgenerator.a("world[" + i + "][generator_name]", worlddata.getType().name());
                    mojangstatisticsgenerator.a("world[" + i + "][generator_version]", Integer.valueOf(worlddata.getType().getVersion()));
                    mojangstatisticsgenerator.a("world[" + i + "][height]", Integer.valueOf(this.F));
                    mojangstatisticsgenerator.a("world[" + i + "][chunks_loaded]", Integer.valueOf(worldserver.getChunkProviderServer().h()));
                    ++i;
                }
            }
        }

        mojangstatisticsgenerator.a("worlds", Integer.valueOf(i));
    }

    public boolean getSnooperEnabled() {
        return true;
    }

    public abstract boolean S();

    public boolean getOnlineMode() {
        return this.onlineMode;
    }

    public void setOnlineMode(boolean flag) {
        this.onlineMode = flag;
    }

    public boolean U() {
        return this.z;
    }

    public void f(boolean flag) {
        this.z = flag;
    }

    public boolean getSpawnAnimals() {
        return this.spawnAnimals;
    }

    public void setSpawnAnimals(boolean flag) {
        this.spawnAnimals = flag;
    }

    public boolean getSpawnNPCs() {
        return this.spawnNPCs;
    }

    public abstract boolean X();

    public void setSpawnNPCs(boolean flag) {
        this.spawnNPCs = flag;
    }

    public boolean getPVP() {
        return this.pvpMode;
    }

    public void setPVP(boolean flag) {
        this.pvpMode = flag;
    }

    public boolean getAllowFlight() {
        return this.allowFlight;
    }

    public void setAllowFlight(boolean flag) {
        this.allowFlight = flag;
    }

    public abstract boolean getEnableCommandBlock();

    public String getMotd() {
        return this.motd;
    }

    public void setMotd(String s) {
        this.motd = s;
    }

    public int getMaxBuildHeight() {
        return this.F;
    }

    public void c(int i) {
        this.F = i;
    }

    public boolean isStopped() {
        return this.isStopped;
    }

    public PlayerList getPlayerList() {
        return this.s;
    }

    public void a(PlayerList playerlist) {
        this.s = playerlist;
    }

    public abstract boolean af();

    public void setGamemode(EnumGamemode enumgamemode) {
        WorldServer[] aworldserver = this.worldServer;
        int i = aworldserver.length;

        for (int j = 0; j < i; ++j) {
            WorldServer worldserver = aworldserver[j];

            worldserver.getWorldData().setGameType(enumgamemode);
        }

    }

    public ServerConnection getServerConnection() {
        return this.m;
    }

    public boolean ai() {
        return false;
    }

    public abstract boolean a(EnumGamemode enumgamemode, boolean flag, int i);

    public int aj() {
        return this.ticks;
    }

    public void ak() {
        this.S = true;
    }

    public int getSpawnProtection() {
        return 16;
    }

    public boolean a(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        return false;
    }

    public void setForceGamemode(boolean flag) {
        this.T = flag;
    }

    public boolean getForceGamemode() {
        return this.T;
    }

    public int getIdleTimeout() {
        return this.G;
    }

    public void setIdleTimeout(int i) {
        this.G = i;
    }

    public MinecraftSessionService ar() {
        return this.V;
    }

    public GameProfileRepository getGameProfileRepository() {
        return this.W;
    }

    public UserCache getUserCache() {
        return this.X;
    }

    public ServerPing getServerPing() {
        return this.n;
    }

    public void av() {
        this.Y = 0L;
    }

    public int aw() {
        return 29999984;
    }

    public <V> ListenableFuture<V> a(Callable<V> callable) {
        Validate.notNull(callable);
        if (!this.isMainThread() && !this.isStopped()) {
            ListenableFutureTask listenablefuturetask = ListenableFutureTask.create(callable);

            this.g.add(listenablefuturetask);
            return listenablefuturetask;
        } else {
            try {
                return Futures.immediateFuture(callable.call());
            } catch (Exception exception) {
                return Futures.immediateFailedCheckedFuture(exception);
            }
        }
    }

    public ListenableFuture<Object> postToMainThread(Runnable runnable) {
        Validate.notNull(runnable);
        return this.a(Executors.callable(runnable));
    }

    public boolean isMainThread() {
        return Thread.currentThread() == this.serverThread;
    }

    public int ay() {
        return 256;
    }

    public long az() {
        return this.aa;
    }

    public Thread aA() {
        return this.serverThread;
    }

    public DataFixer aB() {
        return this.dataConverterManager;
    }

    public int a(@Nullable WorldServer worldserver) {
        return worldserver != null ? worldserver.getGameRules().c("spawnRadius") : 10;
    }

    public AdvancementDataWorld getAdvancementData() {
        return this.al;
    }

    public CustomFunctionData getFunctionData() {
        return this.am;
    }

    public void reload() {
        if (!this.isMainThread()) {
            this.postToMainThread(this::reload);
        } else {
            this.getPlayerList().savePlayers();
            this.resourcePackRepository.a();
            this.a(this.worldServer[0].getWorldData());
            this.getPlayerList().reload();
        }
    }

    private void a(WorldData worlddata) {
        ArrayList arraylist = Lists.newArrayList(this.resourcePackRepository.d());
        Iterator iterator = this.resourcePackRepository.b().iterator();

        while (iterator.hasNext()) {
            ResourcePackLoader resourcepackloader = (ResourcePackLoader) iterator.next();

            if (!worlddata.N().contains(resourcepackloader.e()) && !arraylist.contains(resourcepackloader)) {
                MinecraftServer.LOGGER.info("Found new data pack {}, loading it automatically", resourcepackloader.e());
                resourcepackloader.h().a(arraylist, resourcepackloader, (resourcepackloader) -> {
                    return resourcepackloader;
                }, false);
            }
        }

        this.resourcePackRepository.a((Collection) arraylist);
        ArrayList arraylist1 = Lists.newArrayList();

        this.resourcePackRepository.d().forEach((resourcepackloader) -> {
            list.add(resourcepackloader.d());
        });
        this.ac.a((List) arraylist1);
        worlddata.O().clear();
        worlddata.N().clear();
        this.resourcePackRepository.d().forEach((resourcepackloader) -> {
            worlddata.O().add(resourcepackloader.e());
        });
        this.resourcePackRepository.b().forEach((resourcepackloader) -> {
            if (!this.resourcePackRepository.d().contains(resourcepackloader)) {
                worlddata.N().add(resourcepackloader.e());
            }

        });
    }

    public void a(CommandListenerWrapper commandlistenerwrapper) {
        if (this.aS()) {
            PlayerList playerlist = commandlistenerwrapper.getServer().getPlayerList();
            WhiteList whitelist = playerlist.getWhitelist();

            if (whitelist.isEnabled()) {
                ArrayList arraylist = Lists.newArrayList(playerlist.v());
                Iterator iterator = arraylist.iterator();

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    if (!whitelist.isWhitelisted(entityplayer.getProfile())) {
                        entityplayer.playerConnection.disconnect(new ChatMessage("multiplayer.disconnect.not_whitelisted", new Object[0]));
                    }
                }

            }
        }
    }

    public IReloadableResourceManager getResourceManager() {
        return this.ac;
    }

    public ResourcePackRepository<ResourcePackLoader> getResourcePackRepository() {
        return this.resourcePackRepository;
    }

    public CommandDispatcher getCommandDispatcher() {
        return this.commandDispatcher;
    }

    public CommandListenerWrapper getServerCommandListener() {
        return new CommandListenerWrapper(this, this.worldServer[0] == null ? Vec3D.a : new Vec3D(this.worldServer[0].getSpawn()), Vec2F.a, this.worldServer[0], 4, "Server", new ChatComponentText("Server"), this, (Entity) null);
    }

    public boolean a() {
        return true;
    }

    public boolean b() {
        return true;
    }

    public CraftingManager getCraftingManager() {
        return this.ag;
    }

    public TagRegistry getTagRegistry() {
        return this.ah;
    }

    public ScoreboardServer getScoreboard() {
        return this.ai;
    }

    public LootTableRegistry aP() {
        return this.ak;
    }

    public GameRules aQ() {
        return this.worldServer[0].getGameRules();
    }

    public BossBattleCustomData aR() {
        return this.aj;
    }

    public boolean aS() {
        return this.an;
    }

    public void l(boolean flag) {
        this.an = flag;
    }

    public int a(GameProfile gameprofile) {
        if (this.getPlayerList().isOp(gameprofile)) {
            OpListEntry oplistentry = (OpListEntry) this.getPlayerList().getOPs().get(gameprofile);

            return oplistentry != null ? oplistentry.a() : (this.J() ? (this.I().equals(gameprofile.getName()) ? 4 : (this.getPlayerList().x() ? 4 : 0)) : this.k());
        } else {
            return 0;
        }
    }
}

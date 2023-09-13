package net.minecraft.util.worldupdate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.storage.IChunkLoader;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.WorldPersistentData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldUpgrader {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setDaemon(true).build();
    private final ImmutableSet<ResourceKey<World>> levels;
    private final boolean eraseCache;
    private final Convertable.ConversionSession levelStorage;
    private final Thread thread;
    private final DataFixer dataFixer;
    private volatile boolean running = true;
    private volatile boolean finished;
    private volatile float progress;
    private volatile int totalChunks;
    private volatile int converted;
    private volatile int skipped;
    private final Object2FloatMap<ResourceKey<World>> progressMap = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap(SystemUtils.k()));
    private volatile IChatBaseComponent status = new ChatMessage("optimizeWorld.stage.counting");
    private static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    private final WorldPersistentData overworldDataStorage;

    public WorldUpgrader(Convertable.ConversionSession convertable_conversionsession, DataFixer datafixer, ImmutableSet<ResourceKey<World>> immutableset, boolean flag) {
        this.levels = immutableset;
        this.eraseCache = flag;
        this.dataFixer = datafixer;
        this.levelStorage = convertable_conversionsession;
        this.overworldDataStorage = new WorldPersistentData(new File(this.levelStorage.a(World.OVERWORLD), "data"), datafixer);
        this.thread = WorldUpgrader.THREAD_FACTORY.newThread(this::i);
        this.thread.setUncaughtExceptionHandler((thread, throwable) -> {
            WorldUpgrader.LOGGER.error("Error upgrading world", throwable);
            this.status = new ChatMessage("optimizeWorld.stage.failed");
            this.finished = true;
        });
        this.thread.start();
    }

    public void a() {
        this.running = false;

        try {
            this.thread.join();
        } catch (InterruptedException interruptedexception) {
            ;
        }

    }

    private void i() {
        this.totalChunks = 0;
        Builder<ResourceKey<World>, ListIterator<ChunkCoordIntPair>> builder = ImmutableMap.builder();

        List list;

        for (UnmodifiableIterator unmodifiableiterator = this.levels.iterator(); unmodifiableiterator.hasNext(); this.totalChunks += list.size()) {
            ResourceKey<World> resourcekey = (ResourceKey) unmodifiableiterator.next();

            list = this.b(resourcekey);
            builder.put(resourcekey, list.listIterator());
        }

        if (this.totalChunks == 0) {
            this.finished = true;
        } else {
            float f = (float) this.totalChunks;
            ImmutableMap<ResourceKey<World>, ListIterator<ChunkCoordIntPair>> immutablemap = builder.build();
            Builder<ResourceKey<World>, IChunkLoader> builder1 = ImmutableMap.builder();
            UnmodifiableIterator unmodifiableiterator1 = this.levels.iterator();

            while (unmodifiableiterator1.hasNext()) {
                ResourceKey<World> resourcekey1 = (ResourceKey) unmodifiableiterator1.next();
                File file = this.levelStorage.a(resourcekey1);

                builder1.put(resourcekey1, new IChunkLoader(new File(file, "region"), this.dataFixer, true));
            }

            ImmutableMap<ResourceKey<World>, IChunkLoader> immutablemap1 = builder1.build();
            long i = SystemUtils.getMonotonicMillis();

            this.status = new ChatMessage("optimizeWorld.stage.upgrading");

            while (this.running) {
                boolean flag = false;
                float f1 = 0.0F;

                float f2;

                for (UnmodifiableIterator unmodifiableiterator2 = this.levels.iterator(); unmodifiableiterator2.hasNext(); f1 += f2) {
                    ResourceKey<World> resourcekey2 = (ResourceKey) unmodifiableiterator2.next();
                    ListIterator<ChunkCoordIntPair> listiterator = (ListIterator) immutablemap.get(resourcekey2);
                    IChunkLoader ichunkloader = (IChunkLoader) immutablemap1.get(resourcekey2);

                    if (listiterator.hasNext()) {
                        ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) listiterator.next();
                        boolean flag1 = false;

                        try {
                            NBTTagCompound nbttagcompound = ichunkloader.read(chunkcoordintpair);

                            if (nbttagcompound != null) {
                                int j = IChunkLoader.a(nbttagcompound);
                                NBTTagCompound nbttagcompound1 = ichunkloader.getChunkData(resourcekey2, () -> {
                                    return this.overworldDataStorage;
                                }, nbttagcompound);
                                NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("Level");
                                ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(nbttagcompound2.getInt("xPos"), nbttagcompound2.getInt("zPos"));

                                if (!chunkcoordintpair1.equals(chunkcoordintpair)) {
                                    WorldUpgrader.LOGGER.warn("Chunk {} has invalid position {}", chunkcoordintpair, chunkcoordintpair1);
                                }

                                boolean flag2 = j < SharedConstants.getGameVersion().getWorldVersion();

                                if (this.eraseCache) {
                                    flag2 = flag2 || nbttagcompound2.hasKey("Heightmaps");
                                    nbttagcompound2.remove("Heightmaps");
                                    flag2 = flag2 || nbttagcompound2.hasKey("isLightOn");
                                    nbttagcompound2.remove("isLightOn");
                                }

                                if (flag2) {
                                    ichunkloader.a(chunkcoordintpair, nbttagcompound1);
                                    flag1 = true;
                                }
                            }
                        } catch (ReportedException reportedexception) {
                            Throwable throwable = reportedexception.getCause();

                            if (!(throwable instanceof IOException)) {
                                throw reportedexception;
                            }

                            WorldUpgrader.LOGGER.error("Error upgrading chunk {}", chunkcoordintpair, throwable);
                        } catch (IOException ioexception) {
                            WorldUpgrader.LOGGER.error("Error upgrading chunk {}", chunkcoordintpair, ioexception);
                        }

                        if (flag1) {
                            ++this.converted;
                        } else {
                            ++this.skipped;
                        }

                        flag = true;
                    }

                    f2 = (float) listiterator.nextIndex() / f;
                    this.progressMap.put(resourcekey2, f2);
                }

                this.progress = f1;
                if (!flag) {
                    this.running = false;
                }
            }

            this.status = new ChatMessage("optimizeWorld.stage.finished");
            UnmodifiableIterator unmodifiableiterator3 = immutablemap1.values().iterator();

            while (unmodifiableiterator3.hasNext()) {
                IChunkLoader ichunkloader1 = (IChunkLoader) unmodifiableiterator3.next();

                try {
                    ichunkloader1.close();
                } catch (IOException ioexception1) {
                    WorldUpgrader.LOGGER.error("Error upgrading chunk", ioexception1);
                }
            }

            this.overworldDataStorage.a();
            i = SystemUtils.getMonotonicMillis() - i;
            WorldUpgrader.LOGGER.info("World optimizaton finished after {} ms", i);
            this.finished = true;
        }
    }

    private List<ChunkCoordIntPair> b(ResourceKey<World> resourcekey) {
        File file = this.levelStorage.a(resourcekey);
        File file1 = new File(file, "region");
        File[] afile = file1.listFiles((file2, s) -> {
            return s.endsWith(".mca");
        });

        if (afile == null) {
            return ImmutableList.of();
        } else {
            List<ChunkCoordIntPair> list = Lists.newArrayList();
            File[] afile1 = afile;
            int i = afile.length;

            for (int j = 0; j < i; ++j) {
                File file2 = afile1[j];
                Matcher matcher = WorldUpgrader.REGEX.matcher(file2.getName());

                if (matcher.matches()) {
                    int k = Integer.parseInt(matcher.group(1)) << 5;
                    int l = Integer.parseInt(matcher.group(2)) << 5;

                    try {
                        RegionFile regionfile = new RegionFile(file2, file1, true);

                        try {
                            for (int i1 = 0; i1 < 32; ++i1) {
                                for (int j1 = 0; j1 < 32; ++j1) {
                                    ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i1 + k, j1 + l);

                                    if (regionfile.b(chunkcoordintpair)) {
                                        list.add(chunkcoordintpair);
                                    }
                                }
                            }
                        } catch (Throwable throwable) {
                            try {
                                regionfile.close();
                            } catch (Throwable throwable1) {
                                throwable.addSuppressed(throwable1);
                            }

                            throw throwable;
                        }

                        regionfile.close();
                    } catch (Throwable throwable2) {
                        ;
                    }
                }
            }

            return list;
        }
    }

    public boolean b() {
        return this.finished;
    }

    public ImmutableSet<ResourceKey<World>> c() {
        return this.levels;
    }

    public float a(ResourceKey<World> resourcekey) {
        return this.progressMap.getFloat(resourcekey);
    }

    public float d() {
        return this.progress;
    }

    public int e() {
        return this.totalChunks;
    }

    public int f() {
        return this.converted;
    }

    public int g() {
        return this.skipped;
    }

    public IChatBaseComponent h() {
        return this.status;
    }
}

package net.minecraft.util.worldupdate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.IChunkLoader;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.dimension.WorldDimension;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.WorldPersistentData;
import org.slf4j.Logger;

public class WorldUpgrader {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setDaemon(true).build();
    private final IRegistry<WorldDimension> dimensions;
    private final Set<ResourceKey<World>> levels;
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
    private final Object2FloatMap<ResourceKey<World>> progressMap = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap(SystemUtils.identityStrategy()));
    private volatile IChatBaseComponent status = IChatBaseComponent.translatable("optimizeWorld.stage.counting");
    private static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    private final WorldPersistentData overworldDataStorage;

    public WorldUpgrader(Convertable.ConversionSession convertable_conversionsession, DataFixer datafixer, IRegistry<WorldDimension> iregistry, boolean flag) {
        this.dimensions = iregistry;
        this.levels = (Set) iregistry.registryKeySet().stream().map(Registries::levelStemToLevel).collect(Collectors.toUnmodifiableSet());
        this.eraseCache = flag;
        this.dataFixer = datafixer;
        this.levelStorage = convertable_conversionsession;
        this.overworldDataStorage = new WorldPersistentData(this.levelStorage.getDimensionPath(World.OVERWORLD).resolve("data").toFile(), datafixer);
        this.thread = WorldUpgrader.THREAD_FACTORY.newThread(this::work);
        this.thread.setUncaughtExceptionHandler((thread, throwable) -> {
            WorldUpgrader.LOGGER.error("Error upgrading world", throwable);
            this.status = IChatBaseComponent.translatable("optimizeWorld.stage.failed");
            this.finished = true;
        });
        this.thread.start();
    }

    public void cancel() {
        this.running = false;

        try {
            this.thread.join();
        } catch (InterruptedException interruptedexception) {
            ;
        }

    }

    private void work() {
        this.totalChunks = 0;
        Builder<ResourceKey<World>, ListIterator<ChunkCoordIntPair>> builder = ImmutableMap.builder();

        List list;

        for (Iterator iterator = this.levels.iterator(); iterator.hasNext(); this.totalChunks += list.size()) {
            ResourceKey<World> resourcekey = (ResourceKey) iterator.next();

            list = this.getAllChunkPos(resourcekey);
            builder.put(resourcekey, list.listIterator());
        }

        if (this.totalChunks == 0) {
            this.finished = true;
        } else {
            float f = (float) this.totalChunks;
            ImmutableMap<ResourceKey<World>, ListIterator<ChunkCoordIntPair>> immutablemap = builder.build();
            Builder<ResourceKey<World>, IChunkLoader> builder1 = ImmutableMap.builder();
            Iterator iterator1 = this.levels.iterator();

            while (iterator1.hasNext()) {
                ResourceKey<World> resourcekey1 = (ResourceKey) iterator1.next();
                Path path = this.levelStorage.getDimensionPath(resourcekey1);

                builder1.put(resourcekey1, new IChunkLoader(path.resolve("region"), this.dataFixer, true));
            }

            ImmutableMap<ResourceKey<World>, IChunkLoader> immutablemap1 = builder1.build();
            long i = SystemUtils.getMillis();

            this.status = IChatBaseComponent.translatable("optimizeWorld.stage.upgrading");

            while (this.running) {
                boolean flag = false;
                float f1 = 0.0F;

                float f2;

                for (Iterator iterator2 = this.levels.iterator(); iterator2.hasNext(); f1 += f2) {
                    ResourceKey<World> resourcekey2 = (ResourceKey) iterator2.next();
                    ListIterator<ChunkCoordIntPair> listiterator = (ListIterator) immutablemap.get(resourcekey2);
                    IChunkLoader ichunkloader = (IChunkLoader) immutablemap1.get(resourcekey2);

                    if (listiterator.hasNext()) {
                        ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) listiterator.next();
                        boolean flag1 = false;

                        try {
                            NBTTagCompound nbttagcompound = (NBTTagCompound) ((Optional) ichunkloader.read(chunkcoordintpair).join()).orElse((Object) null);

                            if (nbttagcompound != null) {
                                int j = IChunkLoader.getVersion(nbttagcompound);
                                ChunkGenerator chunkgenerator = ((WorldDimension) this.dimensions.getOrThrow(Registries.levelToLevelStem(resourcekey2))).generator();
                                NBTTagCompound nbttagcompound1 = ichunkloader.upgradeChunkTag(resourcekey2, () -> {
                                    return this.overworldDataStorage;
                                }, nbttagcompound, chunkgenerator.getTypeNameForDataFixer());
                                ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(nbttagcompound1.getInt("xPos"), nbttagcompound1.getInt("zPos"));

                                if (!chunkcoordintpair1.equals(chunkcoordintpair)) {
                                    WorldUpgrader.LOGGER.warn("Chunk {} has invalid position {}", chunkcoordintpair, chunkcoordintpair1);
                                }

                                boolean flag2 = j < SharedConstants.getCurrentVersion().getDataVersion().getVersion();

                                if (this.eraseCache) {
                                    flag2 = flag2 || nbttagcompound1.contains("Heightmaps");
                                    nbttagcompound1.remove("Heightmaps");
                                    flag2 = flag2 || nbttagcompound1.contains("isLightOn");
                                    nbttagcompound1.remove("isLightOn");
                                    NBTTagList nbttaglist = nbttagcompound1.getList("sections", 10);

                                    for (int k = 0; k < nbttaglist.size(); ++k) {
                                        NBTTagCompound nbttagcompound2 = nbttaglist.getCompound(k);

                                        flag2 = flag2 || nbttagcompound2.contains("BlockLight");
                                        nbttagcompound2.remove("BlockLight");
                                        flag2 = flag2 || nbttagcompound2.contains("SkyLight");
                                        nbttagcompound2.remove("SkyLight");
                                    }
                                }

                                if (flag2) {
                                    ichunkloader.write(chunkcoordintpair, nbttagcompound1);
                                    flag1 = true;
                                }
                            }
                        } catch (CompletionException | ReportedException reportedexception) {
                            Throwable throwable = reportedexception.getCause();

                            if (!(throwable instanceof IOException)) {
                                throw reportedexception;
                            }

                            WorldUpgrader.LOGGER.error("Error upgrading chunk {}", chunkcoordintpair, throwable);
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

            this.status = IChatBaseComponent.translatable("optimizeWorld.stage.finished");
            UnmodifiableIterator unmodifiableiterator = immutablemap1.values().iterator();

            while (unmodifiableiterator.hasNext()) {
                IChunkLoader ichunkloader1 = (IChunkLoader) unmodifiableiterator.next();

                try {
                    ichunkloader1.close();
                } catch (IOException ioexception) {
                    WorldUpgrader.LOGGER.error("Error upgrading chunk", ioexception);
                }
            }

            this.overworldDataStorage.save();
            i = SystemUtils.getMillis() - i;
            WorldUpgrader.LOGGER.info("World optimizaton finished after {} ms", i);
            this.finished = true;
        }
    }

    private List<ChunkCoordIntPair> getAllChunkPos(ResourceKey<World> resourcekey) {
        File file = this.levelStorage.getDimensionPath(resourcekey).toFile();
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
                        RegionFile regionfile = new RegionFile(file2.toPath(), file1.toPath(), true);

                        try {
                            for (int i1 = 0; i1 < 32; ++i1) {
                                for (int j1 = 0; j1 < 32; ++j1) {
                                    ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i1 + k, j1 + l);

                                    if (regionfile.doesChunkExist(chunkcoordintpair)) {
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

    public boolean isFinished() {
        return this.finished;
    }

    public Set<ResourceKey<World>> levels() {
        return this.levels;
    }

    public float dimensionProgress(ResourceKey<World> resourcekey) {
        return this.progressMap.getFloat(resourcekey);
    }

    public float getProgress() {
        return this.progress;
    }

    public int getTotalChunks() {
        return this.totalChunks;
    }

    public int getConverted() {
        return this.converted;
    }

    public int getSkipped() {
        return this.skipped;
    }

    public IChatBaseComponent getStatus() {
        return this.status;
    }
}

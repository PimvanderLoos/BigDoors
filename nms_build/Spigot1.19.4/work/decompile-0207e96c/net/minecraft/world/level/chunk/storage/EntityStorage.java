package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.thread.ThreadedMailbox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import org.slf4j.Logger;

public class EntityStorage implements EntityPersistentStorage<Entity> {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ENTITIES_TAG = "Entities";
    private static final String POSITION_TAG = "Position";
    public final WorldServer level;
    private final IOWorker worker;
    private final LongSet emptyChunks = new LongOpenHashSet();
    public final ThreadedMailbox<Runnable> entityDeserializerQueue;
    protected final DataFixer fixerUpper;

    public EntityStorage(WorldServer worldserver, Path path, DataFixer datafixer, boolean flag, Executor executor) {
        this.level = worldserver;
        this.fixerUpper = datafixer;
        this.entityDeserializerQueue = ThreadedMailbox.create(executor, "entity-deserializer");
        this.worker = new IOWorker(path, flag, "entities");
    }

    @Override
    public CompletableFuture<ChunkEntities<Entity>> loadEntities(ChunkCoordIntPair chunkcoordintpair) {
        if (this.emptyChunks.contains(chunkcoordintpair.toLong())) {
            return CompletableFuture.completedFuture(emptyChunk(chunkcoordintpair));
        } else {
            CompletableFuture completablefuture = this.worker.loadAsync(chunkcoordintpair);
            Function function = (optional) -> {
                if (optional.isEmpty()) {
                    this.emptyChunks.add(chunkcoordintpair.toLong());
                    return emptyChunk(chunkcoordintpair);
                } else {
                    try {
                        ChunkCoordIntPair chunkcoordintpair1 = readChunkPos((NBTTagCompound) optional.get());

                        if (!Objects.equals(chunkcoordintpair, chunkcoordintpair1)) {
                            EntityStorage.LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", new Object[]{chunkcoordintpair, chunkcoordintpair, chunkcoordintpair1});
                        }
                    } catch (Exception exception) {
                        EntityStorage.LOGGER.warn("Failed to parse chunk {} position info", chunkcoordintpair, exception);
                    }

                    NBTTagCompound nbttagcompound = this.upgradeChunkTag((NBTTagCompound) optional.get());
                    NBTTagList nbttaglist = nbttagcompound.getList("Entities", 10);
                    List<Entity> list = (List) EntityTypes.loadEntitiesRecursive(nbttaglist, this.level).collect(ImmutableList.toImmutableList());

                    return new ChunkEntities<>(chunkcoordintpair, list);
                }
            };
            ThreadedMailbox threadedmailbox = this.entityDeserializerQueue;

            Objects.requireNonNull(this.entityDeserializerQueue);
            return completablefuture.thenApplyAsync(function, threadedmailbox::tell);
        }
    }

    private static ChunkCoordIntPair readChunkPos(NBTTagCompound nbttagcompound) {
        int[] aint = nbttagcompound.getIntArray("Position");

        return new ChunkCoordIntPair(aint[0], aint[1]);
    }

    private static void writeChunkPos(NBTTagCompound nbttagcompound, ChunkCoordIntPair chunkcoordintpair) {
        nbttagcompound.put("Position", new NBTTagIntArray(new int[]{chunkcoordintpair.x, chunkcoordintpair.z}));
    }

    private static ChunkEntities<Entity> emptyChunk(ChunkCoordIntPair chunkcoordintpair) {
        return new ChunkEntities<>(chunkcoordintpair, ImmutableList.of());
    }

    @Override
    public void storeEntities(ChunkEntities<Entity> chunkentities) {
        ChunkCoordIntPair chunkcoordintpair = chunkentities.getPos();

        if (chunkentities.isEmpty()) {
            if (this.emptyChunks.add(chunkcoordintpair.toLong())) {
                this.worker.store(chunkcoordintpair, (NBTTagCompound) null);
            }

        } else {
            NBTTagList nbttaglist = new NBTTagList();

            chunkentities.getEntities().forEach((entity) -> {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                if (entity.save(nbttagcompound)) {
                    nbttaglist.add(nbttagcompound);
                }

            });
            NBTTagCompound nbttagcompound = GameProfileSerializer.addCurrentDataVersion(new NBTTagCompound());

            nbttagcompound.put("Entities", nbttaglist);
            writeChunkPos(nbttagcompound, chunkcoordintpair);
            this.worker.store(chunkcoordintpair, nbttagcompound).exceptionally((throwable) -> {
                EntityStorage.LOGGER.error("Failed to store chunk {}", chunkcoordintpair, throwable);
                return null;
            });
            this.emptyChunks.remove(chunkcoordintpair.toLong());
        }
    }

    @Override
    public void flush(boolean flag) {
        this.worker.synchronize(flag).join();
        this.entityDeserializerQueue.runAll();
    }

    private NBTTagCompound upgradeChunkTag(NBTTagCompound nbttagcompound) {
        int i = GameProfileSerializer.getDataVersion(nbttagcompound, -1);

        return DataFixTypes.ENTITY_CHUNK.updateToCurrentVersion(this.fixerUpper, nbttagcompound, i);
    }

    @Override
    public void close() throws IOException {
        this.worker.close();
    }
}

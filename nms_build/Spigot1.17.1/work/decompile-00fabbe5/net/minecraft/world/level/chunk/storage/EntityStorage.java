package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.minecraft.SharedConstants;
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
import net.minecraft.world.level.World;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityStorage implements EntityPersistentStorage<Entity> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String ENTITIES_TAG = "Entities";
    private static final String POSITION_TAG = "Position";
    private final WorldServer level;
    private final IOWorker worker;
    private final LongSet emptyChunks = new LongOpenHashSet();
    private final ThreadedMailbox<Runnable> entityDeserializerQueue;
    protected final DataFixer fixerUpper;

    public EntityStorage(WorldServer worldserver, File file, DataFixer datafixer, boolean flag, Executor executor) {
        this.level = worldserver;
        this.fixerUpper = datafixer;
        this.entityDeserializerQueue = ThreadedMailbox.a(executor, "entity-deserializer");
        this.worker = new IOWorker(file, flag, "entities");
    }

    @Override
    public CompletableFuture<ChunkEntities<Entity>> a(ChunkCoordIntPair chunkcoordintpair) {
        if (this.emptyChunks.contains(chunkcoordintpair.pair())) {
            return CompletableFuture.completedFuture(b(chunkcoordintpair));
        } else {
            CompletableFuture completablefuture = this.worker.b(chunkcoordintpair);
            Function function = (nbttagcompound) -> {
                if (nbttagcompound == null) {
                    this.emptyChunks.add(chunkcoordintpair.pair());
                    return b(chunkcoordintpair);
                } else {
                    try {
                        ChunkCoordIntPair chunkcoordintpair1 = b(nbttagcompound);

                        if (!Objects.equals(chunkcoordintpair, chunkcoordintpair1)) {
                            EntityStorage.LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", chunkcoordintpair, chunkcoordintpair, chunkcoordintpair1);
                        }
                    } catch (Exception exception) {
                        EntityStorage.LOGGER.warn("Failed to parse chunk {} position info", chunkcoordintpair, exception);
                    }

                    NBTTagCompound nbttagcompound1 = this.c(nbttagcompound);
                    NBTTagList nbttaglist = nbttagcompound1.getList("Entities", 10);
                    List<Entity> list = (List) EntityTypes.a((List) nbttaglist, (World) this.level).collect(ImmutableList.toImmutableList());

                    return new ChunkEntities<>(chunkcoordintpair, list);
                }
            };
            ThreadedMailbox threadedmailbox = this.entityDeserializerQueue;

            Objects.requireNonNull(this.entityDeserializerQueue);
            return completablefuture.thenApplyAsync(function, threadedmailbox::a);
        }
    }

    private static ChunkCoordIntPair b(NBTTagCompound nbttagcompound) {
        int[] aint = nbttagcompound.getIntArray("Position");

        return new ChunkCoordIntPair(aint[0], aint[1]);
    }

    private static void a(NBTTagCompound nbttagcompound, ChunkCoordIntPair chunkcoordintpair) {
        nbttagcompound.set("Position", new NBTTagIntArray(new int[]{chunkcoordintpair.x, chunkcoordintpair.z}));
    }

    private static ChunkEntities<Entity> b(ChunkCoordIntPair chunkcoordintpair) {
        return new ChunkEntities<>(chunkcoordintpair, ImmutableList.of());
    }

    @Override
    public void a(ChunkEntities<Entity> chunkentities) {
        ChunkCoordIntPair chunkcoordintpair = chunkentities.a();

        if (chunkentities.c()) {
            if (this.emptyChunks.add(chunkcoordintpair.pair())) {
                this.worker.a(chunkcoordintpair, (NBTTagCompound) null);
            }

        } else {
            NBTTagList nbttaglist = new NBTTagList();

            chunkentities.b().forEach((entity) -> {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                if (entity.e(nbttagcompound)) {
                    nbttaglist.add(nbttagcompound);
                }

            });
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.setInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
            nbttagcompound.set("Entities", nbttaglist);
            a(nbttagcompound, chunkcoordintpair);
            this.worker.a(chunkcoordintpair, nbttagcompound).exceptionally((throwable) -> {
                EntityStorage.LOGGER.error("Failed to store chunk {}", chunkcoordintpair, throwable);
                return null;
            });
            this.emptyChunks.remove(chunkcoordintpair.pair());
        }
    }

    @Override
    public void a(boolean flag) {
        this.worker.a(flag).join();
        this.entityDeserializerQueue.a();
    }

    private NBTTagCompound c(NBTTagCompound nbttagcompound) {
        int i = a(nbttagcompound);

        return GameProfileSerializer.a(this.fixerUpper, DataFixTypes.ENTITY_CHUNK, nbttagcompound, i);
    }

    public static int a(NBTTagCompound nbttagcompound) {
        return nbttagcompound.hasKeyOfType("DataVersion", 99) ? nbttagcompound.getInt("DataVersion") : -1;
    }

    @Override
    public void close() throws IOException {
        this.worker.close();
    }
}

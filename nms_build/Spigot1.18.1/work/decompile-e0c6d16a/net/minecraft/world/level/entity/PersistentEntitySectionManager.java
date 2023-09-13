package net.minecraft.world.level.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.PlayerChunk;
import net.minecraft.util.CSVWriter;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkCoordIntPair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PersistentEntitySectionManager<T extends EntityAccess> implements AutoCloseable {

    static final Logger LOGGER = LogManager.getLogger();
    final Set<UUID> knownUuids = Sets.newHashSet();
    final LevelCallback<T> callbacks;
    public final EntityPersistentStorage<T> permanentStorage;
    private final EntityLookup<T> visibleEntityStorage = new EntityLookup<>();
    final EntitySectionStorage<T> sectionStorage;
    private final LevelEntityGetter<T> entityGetter;
    private final Long2ObjectMap<Visibility> chunkVisibility = new Long2ObjectOpenHashMap();
    private final Long2ObjectMap<PersistentEntitySectionManager.b> chunkLoadStatuses = new Long2ObjectOpenHashMap();
    private final LongSet chunksToUnload = new LongOpenHashSet();
    private final Queue<ChunkEntities<T>> loadingInbox = Queues.newConcurrentLinkedQueue();

    public PersistentEntitySectionManager(Class<T> oclass, LevelCallback<T> levelcallback, EntityPersistentStorage<T> entitypersistentstorage) {
        this.sectionStorage = new EntitySectionStorage<>(oclass, this.chunkVisibility);
        this.chunkVisibility.defaultReturnValue(Visibility.HIDDEN);
        this.chunkLoadStatuses.defaultReturnValue(PersistentEntitySectionManager.b.FRESH);
        this.callbacks = levelcallback;
        this.permanentStorage = entitypersistentstorage;
        this.entityGetter = new LevelEntityGetterAdapter<>(this.visibleEntityStorage, this.sectionStorage);
    }

    void removeSectionIfEmpty(long i, EntitySection<T> entitysection) {
        if (entitysection.isEmpty()) {
            this.sectionStorage.remove(i);
        }

    }

    private boolean addEntityUuid(T t0) {
        if (!this.knownUuids.add(t0.getUUID())) {
            PersistentEntitySectionManager.LOGGER.warn("UUID of added entity already exists: {}", t0);
            return false;
        } else {
            return true;
        }
    }

    public boolean addNewEntity(T t0) {
        return this.addEntity(t0, false);
    }

    private boolean addEntity(T t0, boolean flag) {
        if (!this.addEntityUuid(t0)) {
            return false;
        } else {
            long i = SectionPosition.asLong(t0.blockPosition());
            EntitySection<T> entitysection = this.sectionStorage.getOrCreateSection(i);

            entitysection.add(t0);
            t0.setLevelCallback(new PersistentEntitySectionManager.a(t0, i, entitysection));
            if (!flag) {
                this.callbacks.onCreated(t0);
            }

            Visibility visibility = getEffectiveStatus(t0, entitysection.getStatus());

            if (visibility.isAccessible()) {
                this.startTracking(t0);
            }

            if (visibility.isTicking()) {
                this.startTicking(t0);
            }

            return true;
        }
    }

    static <T extends EntityAccess> Visibility getEffectiveStatus(T t0, Visibility visibility) {
        return t0.isAlwaysTicking() ? Visibility.TICKING : visibility;
    }

    public void addLegacyChunkEntities(Stream<T> stream) {
        stream.forEach((entityaccess) -> {
            this.addEntity(entityaccess, true);
        });
    }

    public void addWorldGenChunkEntities(Stream<T> stream) {
        stream.forEach((entityaccess) -> {
            this.addEntity(entityaccess, false);
        });
    }

    void startTicking(T t0) {
        this.callbacks.onTickingStart(t0);
    }

    void stopTicking(T t0) {
        this.callbacks.onTickingEnd(t0);
    }

    void startTracking(T t0) {
        this.visibleEntityStorage.add(t0);
        this.callbacks.onTrackingStart(t0);
    }

    void stopTracking(T t0) {
        this.callbacks.onTrackingEnd(t0);
        this.visibleEntityStorage.remove(t0);
    }

    public void updateChunkStatus(ChunkCoordIntPair chunkcoordintpair, PlayerChunk.State playerchunk_state) {
        Visibility visibility = Visibility.fromFullChunkStatus(playerchunk_state);

        this.updateChunkStatus(chunkcoordintpair, visibility);
    }

    public void updateChunkStatus(ChunkCoordIntPair chunkcoordintpair, Visibility visibility) {
        long i = chunkcoordintpair.toLong();

        if (visibility == Visibility.HIDDEN) {
            this.chunkVisibility.remove(i);
            this.chunksToUnload.add(i);
        } else {
            this.chunkVisibility.put(i, visibility);
            this.chunksToUnload.remove(i);
            this.ensureChunkQueuedForLoad(i);
        }

        this.sectionStorage.getExistingSectionsInChunk(i).forEach((entitysection) -> {
            Visibility visibility1 = entitysection.updateChunkStatus(visibility);
            boolean flag = visibility1.isAccessible();
            boolean flag1 = visibility.isAccessible();
            boolean flag2 = visibility1.isTicking();
            boolean flag3 = visibility.isTicking();

            if (flag2 && !flag3) {
                entitysection.getEntities().filter((entityaccess) -> {
                    return !entityaccess.isAlwaysTicking();
                }).forEach(this::stopTicking);
            }

            if (flag && !flag1) {
                entitysection.getEntities().filter((entityaccess) -> {
                    return !entityaccess.isAlwaysTicking();
                }).forEach(this::stopTracking);
            } else if (!flag && flag1) {
                entitysection.getEntities().filter((entityaccess) -> {
                    return !entityaccess.isAlwaysTicking();
                }).forEach(this::startTracking);
            }

            if (!flag2 && flag3) {
                entitysection.getEntities().filter((entityaccess) -> {
                    return !entityaccess.isAlwaysTicking();
                }).forEach(this::startTicking);
            }

        });
    }

    public void ensureChunkQueuedForLoad(long i) {
        PersistentEntitySectionManager.b persistententitysectionmanager_b = (PersistentEntitySectionManager.b) this.chunkLoadStatuses.get(i);

        if (persistententitysectionmanager_b == PersistentEntitySectionManager.b.FRESH) {
            this.requestChunkLoad(i);
        }

    }

    private boolean storeChunkSections(long i, Consumer<T> consumer) {
        PersistentEntitySectionManager.b persistententitysectionmanager_b = (PersistentEntitySectionManager.b) this.chunkLoadStatuses.get(i);

        if (persistententitysectionmanager_b == PersistentEntitySectionManager.b.PENDING) {
            return false;
        } else {
            List<T> list = (List) this.sectionStorage.getExistingSectionsInChunk(i).flatMap((entitysection) -> {
                return entitysection.getEntities().filter(EntityAccess::shouldBeSaved);
            }).collect(Collectors.toList());

            if (list.isEmpty()) {
                if (persistententitysectionmanager_b == PersistentEntitySectionManager.b.LOADED) {
                    this.permanentStorage.storeEntities(new ChunkEntities<>(new ChunkCoordIntPair(i), ImmutableList.of()));
                }

                return true;
            } else if (persistententitysectionmanager_b == PersistentEntitySectionManager.b.FRESH) {
                this.requestChunkLoad(i);
                return false;
            } else {
                this.permanentStorage.storeEntities(new ChunkEntities<>(new ChunkCoordIntPair(i), list));
                list.forEach(consumer);
                return true;
            }
        }
    }

    private void requestChunkLoad(long i) {
        this.chunkLoadStatuses.put(i, PersistentEntitySectionManager.b.PENDING);
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i);
        CompletableFuture completablefuture = this.permanentStorage.loadEntities(chunkcoordintpair);
        Queue queue = this.loadingInbox;

        Objects.requireNonNull(this.loadingInbox);
        completablefuture.thenAccept(queue::add).exceptionally((throwable) -> {
            PersistentEntitySectionManager.LOGGER.error("Failed to read chunk {}", chunkcoordintpair, throwable);
            return null;
        });
    }

    private boolean processChunkUnload(long i) {
        boolean flag = this.storeChunkSections(i, (entityaccess) -> {
            entityaccess.getPassengersAndSelf().forEach(this::unloadEntity);
        });

        if (!flag) {
            return false;
        } else {
            this.chunkLoadStatuses.remove(i);
            return true;
        }
    }

    private void unloadEntity(EntityAccess entityaccess) {
        entityaccess.setRemoved(Entity.RemovalReason.UNLOADED_TO_CHUNK);
        entityaccess.setLevelCallback(EntityInLevelCallback.NULL);
    }

    private void processUnloads() {
        this.chunksToUnload.removeIf((i) -> {
            return this.chunkVisibility.get(i) != Visibility.HIDDEN ? true : this.processChunkUnload(i);
        });
    }

    private void processPendingLoads() {
        ChunkEntities chunkentities;

        while ((chunkentities = (ChunkEntities) this.loadingInbox.poll()) != null) {
            chunkentities.getEntities().forEach((entityaccess) -> {
                this.addEntity(entityaccess, true);
            });
            this.chunkLoadStatuses.put(chunkentities.getPos().toLong(), PersistentEntitySectionManager.b.LOADED);
        }

    }

    public void tick() {
        this.processPendingLoads();
        this.processUnloads();
    }

    private LongSet getAllChunksToSave() {
        LongSet longset = this.sectionStorage.getAllChunksWithExistingSections();
        ObjectIterator objectiterator = Long2ObjectMaps.fastIterable(this.chunkLoadStatuses).iterator();

        while (objectiterator.hasNext()) {
            Entry<PersistentEntitySectionManager.b> entry = (Entry) objectiterator.next();

            if (entry.getValue() == PersistentEntitySectionManager.b.LOADED) {
                longset.add(entry.getLongKey());
            }
        }

        return longset;
    }

    public void autoSave() {
        this.getAllChunksToSave().forEach((i) -> {
            boolean flag = this.chunkVisibility.get(i) == Visibility.HIDDEN;

            if (flag) {
                this.processChunkUnload(i);
            } else {
                this.storeChunkSections(i, (entityaccess) -> {
                });
            }

        });
    }

    public void saveAll() {
        LongSet longset = this.getAllChunksToSave();

        while (!longset.isEmpty()) {
            this.permanentStorage.flush(false);
            this.processPendingLoads();
            longset.removeIf((i) -> {
                boolean flag = this.chunkVisibility.get(i) == Visibility.HIDDEN;

                return flag ? this.processChunkUnload(i) : this.storeChunkSections(i, (entityaccess) -> {
                });
            });
        }

        this.permanentStorage.flush(true);
    }

    public void close() throws IOException {
        this.saveAll();
        this.permanentStorage.close();
    }

    public boolean isLoaded(UUID uuid) {
        return this.knownUuids.contains(uuid);
    }

    public LevelEntityGetter<T> getEntityGetter() {
        return this.entityGetter;
    }

    public boolean isPositionTicking(BlockPosition blockposition) {
        return ((Visibility) this.chunkVisibility.get(ChunkCoordIntPair.asLong(blockposition))).isTicking();
    }

    public boolean isPositionTicking(ChunkCoordIntPair chunkcoordintpair) {
        return ((Visibility) this.chunkVisibility.get(chunkcoordintpair.toLong())).isTicking();
    }

    public boolean areEntitiesLoaded(long i) {
        return this.chunkLoadStatuses.get(i) == PersistentEntitySectionManager.b.LOADED;
    }

    public void dumpSections(Writer writer) throws IOException {
        CSVWriter csvwriter = CSVWriter.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("visibility").addColumn("load_status").addColumn("entity_count").build(writer);

        this.sectionStorage.getAllChunksWithExistingSections().forEach((i) -> {
            PersistentEntitySectionManager.b persistententitysectionmanager_b = (PersistentEntitySectionManager.b) this.chunkLoadStatuses.get(i);

            this.sectionStorage.getExistingSectionPositionsInChunk(i).forEach((j) -> {
                EntitySection<T> entitysection = this.sectionStorage.getSection(j);

                if (entitysection != null) {
                    try {
                        csvwriter.writeRow(SectionPosition.x(j), SectionPosition.y(j), SectionPosition.z(j), entitysection.getStatus(), persistententitysectionmanager_b, entitysection.size());
                    } catch (IOException ioexception) {
                        throw new UncheckedIOException(ioexception);
                    }
                }

            });
        });
    }

    @VisibleForDebug
    public String gatherStats() {
        int i = this.knownUuids.size();

        return i + "," + this.visibleEntityStorage.count() + "," + this.sectionStorage.count() + "," + this.chunkLoadStatuses.size() + "," + this.chunkVisibility.size() + "," + this.loadingInbox.size() + "," + this.chunksToUnload.size();
    }

    private static enum b {

        FRESH, PENDING, LOADED;

        private b() {}
    }

    private class a implements EntityInLevelCallback {

        private final T entity;
        private long currentSectionKey;
        private EntitySection<T> currentSection;

        a(EntityAccess entityaccess, long i, EntitySection entitysection) {
            this.entity = entityaccess;
            this.currentSectionKey = i;
            this.currentSection = entitysection;
        }

        @Override
        public void onMove() {
            BlockPosition blockposition = this.entity.blockPosition();
            long i = SectionPosition.asLong(blockposition);

            if (i != this.currentSectionKey) {
                Visibility visibility = this.currentSection.getStatus();

                if (!this.currentSection.remove(this.entity)) {
                    PersistentEntitySectionManager.LOGGER.warn("Entity {} wasn't found in section {} (moving to {})", this.entity, SectionPosition.of(this.currentSectionKey), i);
                }

                PersistentEntitySectionManager.this.removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
                EntitySection<T> entitysection = PersistentEntitySectionManager.this.sectionStorage.getOrCreateSection(i);

                entitysection.add(this.entity);
                this.currentSection = entitysection;
                this.currentSectionKey = i;
                this.updateStatus(visibility, entitysection.getStatus());
            }

        }

        private void updateStatus(Visibility visibility, Visibility visibility1) {
            Visibility visibility2 = PersistentEntitySectionManager.getEffectiveStatus(this.entity, visibility);
            Visibility visibility3 = PersistentEntitySectionManager.getEffectiveStatus(this.entity, visibility1);

            if (visibility2 != visibility3) {
                boolean flag = visibility2.isAccessible();
                boolean flag1 = visibility3.isAccessible();

                if (flag && !flag1) {
                    PersistentEntitySectionManager.this.stopTracking(this.entity);
                } else if (!flag && flag1) {
                    PersistentEntitySectionManager.this.startTracking(this.entity);
                }

                boolean flag2 = visibility2.isTicking();
                boolean flag3 = visibility3.isTicking();

                if (flag2 && !flag3) {
                    PersistentEntitySectionManager.this.stopTicking(this.entity);
                } else if (!flag2 && flag3) {
                    PersistentEntitySectionManager.this.startTicking(this.entity);
                }

            }
        }

        @Override
        public void onRemove(Entity.RemovalReason entity_removalreason) {
            if (!this.currentSection.remove(this.entity)) {
                PersistentEntitySectionManager.LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", this.entity, SectionPosition.of(this.currentSectionKey), entity_removalreason);
            }

            Visibility visibility = PersistentEntitySectionManager.getEffectiveStatus(this.entity, this.currentSection.getStatus());

            if (visibility.isTicking()) {
                PersistentEntitySectionManager.this.stopTicking(this.entity);
            }

            if (visibility.isAccessible()) {
                PersistentEntitySectionManager.this.stopTracking(this.entity);
            }

            if (entity_removalreason.shouldDestroy()) {
                PersistentEntitySectionManager.this.callbacks.onDestroyed(this.entity);
            }

            PersistentEntitySectionManager.this.knownUuids.remove(this.entity.getUUID());
            this.entity.setLevelCallback(PersistentEntitySectionManager.a.NULL);
            PersistentEntitySectionManager.this.removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
        }
    }
}

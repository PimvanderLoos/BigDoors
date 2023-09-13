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
    private final EntityPersistentStorage<T> permanentStorage;
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

    void a(long i, EntitySection<T> entitysection) {
        if (entitysection.a()) {
            this.sectionStorage.e(i);
        }

    }

    private boolean b(T t0) {
        if (!this.knownUuids.add(t0.getUniqueID())) {
            PersistentEntitySectionManager.LOGGER.warn("UUID of added entity already exists: {}", t0);
            return false;
        } else {
            return true;
        }
    }

    public boolean a(T t0) {
        return this.a(t0, false);
    }

    private boolean a(T t0, boolean flag) {
        if (!this.b(t0)) {
            return false;
        } else {
            long i = SectionPosition.c(t0.getChunkCoordinates());
            EntitySection<T> entitysection = this.sectionStorage.c(i);

            entitysection.a((Object) t0);
            t0.a(new PersistentEntitySectionManager.a(t0, i, entitysection));
            if (!flag) {
                this.callbacks.f(t0);
            }

            Visibility visibility = a(t0, entitysection.c());

            if (visibility.b()) {
                this.e(t0);
            }

            if (visibility.a()) {
                this.c(t0);
            }

            return true;
        }
    }

    static <T extends EntityAccess> Visibility a(T t0, Visibility visibility) {
        return t0.dn() ? Visibility.TICKING : visibility;
    }

    public void a(Stream<T> stream) {
        stream.forEach((entityaccess) -> {
            this.a(entityaccess, true);
        });
    }

    public void b(Stream<T> stream) {
        stream.forEach((entityaccess) -> {
            this.a(entityaccess, false);
        });
    }

    void c(T t0) {
        this.callbacks.d(t0);
    }

    void d(T t0) {
        this.callbacks.c(t0);
    }

    void e(T t0) {
        this.visibleEntityStorage.a(t0);
        this.callbacks.b(t0);
    }

    void f(T t0) {
        this.callbacks.a(t0);
        this.visibleEntityStorage.b(t0);
    }

    public void a(ChunkCoordIntPair chunkcoordintpair, PlayerChunk.State playerchunk_state) {
        Visibility visibility = Visibility.a(playerchunk_state);

        this.a(chunkcoordintpair, visibility);
    }

    public void a(ChunkCoordIntPair chunkcoordintpair, Visibility visibility) {
        long i = chunkcoordintpair.pair();

        if (visibility == Visibility.HIDDEN) {
            this.chunkVisibility.remove(i);
            this.chunksToUnload.add(i);
        } else {
            this.chunkVisibility.put(i, visibility);
            this.chunksToUnload.remove(i);
            this.b(i);
        }

        this.sectionStorage.b(i).forEach((entitysection) -> {
            Visibility visibility1 = entitysection.a(visibility);
            boolean flag = visibility1.b();
            boolean flag1 = visibility.b();
            boolean flag2 = visibility1.a();
            boolean flag3 = visibility.a();

            if (flag2 && !flag3) {
                entitysection.b().filter((entityaccess) -> {
                    return !entityaccess.dn();
                }).forEach(this::d);
            }

            if (flag && !flag1) {
                entitysection.b().filter((entityaccess) -> {
                    return !entityaccess.dn();
                }).forEach(this::f);
            } else if (!flag && flag1) {
                entitysection.b().filter((entityaccess) -> {
                    return !entityaccess.dn();
                }).forEach(this::e);
            }

            if (!flag2 && flag3) {
                entitysection.b().filter((entityaccess) -> {
                    return !entityaccess.dn();
                }).forEach(this::c);
            }

        });
    }

    private void b(long i) {
        PersistentEntitySectionManager.b persistententitysectionmanager_b = (PersistentEntitySectionManager.b) this.chunkLoadStatuses.get(i);

        if (persistententitysectionmanager_b == PersistentEntitySectionManager.b.FRESH) {
            this.c(i);
        }

    }

    private boolean a(long i, Consumer<T> consumer) {
        PersistentEntitySectionManager.b persistententitysectionmanager_b = (PersistentEntitySectionManager.b) this.chunkLoadStatuses.get(i);

        if (persistententitysectionmanager_b == PersistentEntitySectionManager.b.PENDING) {
            return false;
        } else {
            List<T> list = (List) this.sectionStorage.b(i).flatMap((entitysection) -> {
                return entitysection.b().filter(EntityAccess::dm);
            }).collect(Collectors.toList());

            if (list.isEmpty()) {
                if (persistententitysectionmanager_b == PersistentEntitySectionManager.b.LOADED) {
                    this.permanentStorage.a(new ChunkEntities<>(new ChunkCoordIntPair(i), ImmutableList.of()));
                }

                return true;
            } else if (persistententitysectionmanager_b == PersistentEntitySectionManager.b.FRESH) {
                this.c(i);
                return false;
            } else {
                this.permanentStorage.a(new ChunkEntities<>(new ChunkCoordIntPair(i), list));
                list.forEach(consumer);
                return true;
            }
        }
    }

    private void c(long i) {
        this.chunkLoadStatuses.put(i, PersistentEntitySectionManager.b.PENDING);
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i);
        CompletableFuture completablefuture = this.permanentStorage.a(chunkcoordintpair);
        Queue queue = this.loadingInbox;

        Objects.requireNonNull(this.loadingInbox);
        completablefuture.thenAccept(queue::add).exceptionally((throwable) -> {
            PersistentEntitySectionManager.LOGGER.error("Failed to read chunk {}", chunkcoordintpair, throwable);
            return null;
        });
    }

    private boolean d(long i) {
        boolean flag = this.a(i, (entityaccess) -> {
            entityaccess.cD().forEach(this::g);
        });

        if (!flag) {
            return false;
        } else {
            this.chunkLoadStatuses.remove(i);
            return true;
        }
    }

    private void g(EntityAccess entityaccess) {
        entityaccess.setRemoved(Entity.RemovalReason.UNLOADED_TO_CHUNK);
        entityaccess.a(EntityInLevelCallback.NULL);
    }

    private void f() {
        this.chunksToUnload.removeIf((i) -> {
            return this.chunkVisibility.get(i) != Visibility.HIDDEN ? true : this.d(i);
        });
    }

    private void g() {
        ChunkEntities chunkentities;

        while ((chunkentities = (ChunkEntities) this.loadingInbox.poll()) != null) {
            chunkentities.b().forEach((entityaccess) -> {
                this.a(entityaccess, true);
            });
            this.chunkLoadStatuses.put(chunkentities.a().pair(), PersistentEntitySectionManager.b.LOADED);
        }

    }

    public void tick() {
        this.g();
        this.f();
    }

    private LongSet h() {
        LongSet longset = this.sectionStorage.a();
        ObjectIterator objectiterator = Long2ObjectMaps.fastIterable(this.chunkLoadStatuses).iterator();

        while (objectiterator.hasNext()) {
            Entry<PersistentEntitySectionManager.b> entry = (Entry) objectiterator.next();

            if (entry.getValue() == PersistentEntitySectionManager.b.LOADED) {
                longset.add(entry.getLongKey());
            }
        }

        return longset;
    }

    public void b() {
        this.h().forEach((i) -> {
            boolean flag = this.chunkVisibility.get(i) == Visibility.HIDDEN;

            if (flag) {
                this.d(i);
            } else {
                this.a(i, (entityaccess) -> {
                });
            }

        });
    }

    public void c() {
        LongSet longset = this.h();

        while (!longset.isEmpty()) {
            this.permanentStorage.a(false);
            this.g();
            longset.removeIf((i) -> {
                boolean flag = this.chunkVisibility.get(i) == Visibility.HIDDEN;

                return flag ? this.d(i) : this.a(i, (entityaccess) -> {
                });
            });
        }

        this.permanentStorage.a(true);
    }

    public void close() throws IOException {
        this.c();
        this.permanentStorage.close();
    }

    public boolean a(UUID uuid) {
        return this.knownUuids.contains(uuid);
    }

    public LevelEntityGetter<T> d() {
        return this.entityGetter;
    }

    public boolean a(BlockPosition blockposition) {
        return ((Visibility) this.chunkVisibility.get(ChunkCoordIntPair.a(blockposition))).a();
    }

    public boolean a(ChunkCoordIntPair chunkcoordintpair) {
        return ((Visibility) this.chunkVisibility.get(chunkcoordintpair.pair())).a();
    }

    public boolean a(long i) {
        return this.chunkLoadStatuses.get(i) == PersistentEntitySectionManager.b.LOADED;
    }

    public void a(Writer writer) throws IOException {
        CSVWriter csvwriter = CSVWriter.a().a("x").a("y").a("z").a("visibility").a("load_status").a("entity_count").a(writer);

        this.sectionStorage.a().forEach((i) -> {
            PersistentEntitySectionManager.b persistententitysectionmanager_b = (PersistentEntitySectionManager.b) this.chunkLoadStatuses.get(i);

            this.sectionStorage.a(i).forEach((j) -> {
                EntitySection<T> entitysection = this.sectionStorage.d(j);

                if (entitysection != null) {
                    try {
                        csvwriter.a(SectionPosition.b(j), SectionPosition.c(j), SectionPosition.d(j), entitysection.c(), persistententitysectionmanager_b, entitysection.d());
                    } catch (IOException ioexception) {
                        throw new UncheckedIOException(ioexception);
                    }
                }

            });
        });
    }

    @VisibleForDebug
    public String e() {
        int i = this.knownUuids.size();

        return i + "," + this.visibleEntityStorage.b() + "," + this.sectionStorage.b() + "," + this.chunkLoadStatuses.size() + "," + this.chunkVisibility.size() + "," + this.loadingInbox.size() + "," + this.chunksToUnload.size();
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
        public void a() {
            BlockPosition blockposition = this.entity.getChunkCoordinates();
            long i = SectionPosition.c(blockposition);

            if (i != this.currentSectionKey) {
                Visibility visibility = this.currentSection.c();

                if (!this.currentSection.b(this.entity)) {
                    PersistentEntitySectionManager.LOGGER.warn("Entity {} wasn't found in section {} (moving to {})", this.entity, SectionPosition.a(this.currentSectionKey), i);
                }

                PersistentEntitySectionManager.this.a(this.currentSectionKey, this.currentSection);
                EntitySection<T> entitysection = PersistentEntitySectionManager.this.sectionStorage.c(i);

                entitysection.a((Object) this.entity);
                this.currentSection = entitysection;
                this.currentSectionKey = i;
                this.a(visibility, entitysection.c());
            }

        }

        private void a(Visibility visibility, Visibility visibility1) {
            Visibility visibility2 = PersistentEntitySectionManager.a(this.entity, visibility);
            Visibility visibility3 = PersistentEntitySectionManager.a(this.entity, visibility1);

            if (visibility2 != visibility3) {
                boolean flag = visibility2.b();
                boolean flag1 = visibility3.b();

                if (flag && !flag1) {
                    PersistentEntitySectionManager.this.f(this.entity);
                } else if (!flag && flag1) {
                    PersistentEntitySectionManager.this.e(this.entity);
                }

                boolean flag2 = visibility2.a();
                boolean flag3 = visibility3.a();

                if (flag2 && !flag3) {
                    PersistentEntitySectionManager.this.d(this.entity);
                } else if (!flag2 && flag3) {
                    PersistentEntitySectionManager.this.c(this.entity);
                }

            }
        }

        @Override
        public void a(Entity.RemovalReason entity_removalreason) {
            if (!this.currentSection.b(this.entity)) {
                PersistentEntitySectionManager.LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", this.entity, SectionPosition.a(this.currentSectionKey), entity_removalreason);
            }

            Visibility visibility = PersistentEntitySectionManager.a(this.entity, this.currentSection.c());

            if (visibility.a()) {
                PersistentEntitySectionManager.this.d(this.entity);
            }

            if (visibility.b()) {
                PersistentEntitySectionManager.this.f(this.entity);
            }

            if (entity_removalreason.a()) {
                PersistentEntitySectionManager.this.callbacks.e(this.entity);
            }

            PersistentEntitySectionManager.this.knownUuids.remove(this.entity.getUniqueID());
            this.entity.a(PersistentEntitySectionManager.a.NULL);
            PersistentEntitySectionManager.this.a(this.currentSectionKey, this.currentSection);
        }
    }
}

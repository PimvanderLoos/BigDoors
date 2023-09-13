package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.Mailbox;
import net.minecraft.util.thread.PairedQueue;
import net.minecraft.util.thread.ThreadedMailbox;
import net.minecraft.world.level.ChunkCoordIntPair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkTaskQueueSorter implements PlayerChunk.d, AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<Mailbox<?>, ChunkTaskQueue<? extends Function<Mailbox<Unit>, ?>>> queues;
    private final Set<Mailbox<?>> sleeping;
    private final ThreadedMailbox<PairedQueue.b> mailbox;

    public ChunkTaskQueueSorter(List<Mailbox<?>> list, Executor executor, int i) {
        this.queues = (Map) list.stream().collect(Collectors.toMap(Function.identity(), (mailbox) -> {
            return new ChunkTaskQueue<>(mailbox.bn() + "_queue", i);
        }));
        this.sleeping = Sets.newHashSet(list);
        this.mailbox = new ThreadedMailbox<>(new PairedQueue.a(4), executor, "sorter");
    }

    public static <T> ChunkTaskQueueSorter.a<T> a(Function<Mailbox<Unit>, T> function, long i, IntSupplier intsupplier) {
        return new ChunkTaskQueueSorter.a<>(function, i, intsupplier);
    }

    public static ChunkTaskQueueSorter.a<Runnable> a(Runnable runnable, long i, IntSupplier intsupplier) {
        return new ChunkTaskQueueSorter.a<>((mailbox) -> {
            return () -> {
                runnable.run();
                mailbox.a(Unit.INSTANCE);
            };
        }, i, intsupplier);
    }

    public static ChunkTaskQueueSorter.a<Runnable> a(PlayerChunk playerchunk, Runnable runnable) {
        long i = playerchunk.i().pair();

        Objects.requireNonNull(playerchunk);
        return a(runnable, i, playerchunk::k);
    }

    public static <T> ChunkTaskQueueSorter.a<T> a(PlayerChunk playerchunk, Function<Mailbox<Unit>, T> function) {
        long i = playerchunk.i().pair();

        Objects.requireNonNull(playerchunk);
        return a(function, i, playerchunk::k);
    }

    public static ChunkTaskQueueSorter.b a(Runnable runnable, long i, boolean flag) {
        return new ChunkTaskQueueSorter.b(runnable, i, flag);
    }

    public <T> Mailbox<ChunkTaskQueueSorter.a<T>> a(Mailbox<T> mailbox, boolean flag) {
        return (Mailbox) this.mailbox.b((mailbox1) -> {
            return new PairedQueue.b(0, () -> {
                this.b(mailbox);
                mailbox1.a(Mailbox.a("chunk priority sorter around " + mailbox.bn(), (chunktaskqueuesorter_a) -> {
                    this.a(mailbox, chunktaskqueuesorter_a.task, chunktaskqueuesorter_a.pos, chunktaskqueuesorter_a.level, flag);
                }));
            });
        }).join();
    }

    public Mailbox<ChunkTaskQueueSorter.b> a(Mailbox<Runnable> mailbox) {
        return (Mailbox) this.mailbox.b((mailbox1) -> {
            return new PairedQueue.b(0, () -> {
                mailbox1.a(Mailbox.a("chunk priority sorter around " + mailbox.bn(), (chunktaskqueuesorter_b) -> {
                    this.a(mailbox, chunktaskqueuesorter_b.pos, chunktaskqueuesorter_b.task, chunktaskqueuesorter_b.clearQueue);
                }));
            });
        }).join();
    }

    @Override
    public void a(ChunkCoordIntPair chunkcoordintpair, IntSupplier intsupplier, int i, IntConsumer intconsumer) {
        this.mailbox.a((Object) (new PairedQueue.b(0, () -> {
            int j = intsupplier.getAsInt();

            this.queues.values().forEach((chunktaskqueue) -> {
                chunktaskqueue.a(j, chunkcoordintpair, i);
            });
            intconsumer.accept(i);
        })));
    }

    private <T> void a(Mailbox<T> mailbox, long i, Runnable runnable, boolean flag) {
        this.mailbox.a((Object) (new PairedQueue.b(1, () -> {
            ChunkTaskQueue<Function<Mailbox<Unit>, T>> chunktaskqueue = this.b(mailbox);

            chunktaskqueue.a(i, flag);
            if (this.sleeping.remove(mailbox)) {
                this.a(chunktaskqueue, mailbox);
            }

            runnable.run();
        })));
    }

    private <T> void a(Mailbox<T> mailbox, Function<Mailbox<Unit>, T> function, long i, IntSupplier intsupplier, boolean flag) {
        this.mailbox.a((Object) (new PairedQueue.b(2, () -> {
            ChunkTaskQueue<Function<Mailbox<Unit>, T>> chunktaskqueue = this.b(mailbox);
            int j = intsupplier.getAsInt();

            chunktaskqueue.a(Optional.of(function), i, j);
            if (flag) {
                chunktaskqueue.a(Optional.empty(), i, j);
            }

            if (this.sleeping.remove(mailbox)) {
                this.a(chunktaskqueue, mailbox);
            }

        })));
    }

    private <T> void a(ChunkTaskQueue<Function<Mailbox<Unit>, T>> chunktaskqueue, Mailbox<T> mailbox) {
        this.mailbox.a((Object) (new PairedQueue.b(3, () -> {
            Stream<Either<Function<Mailbox<Unit>, T>, Runnable>> stream = chunktaskqueue.a();

            if (stream == null) {
                this.sleeping.add(mailbox);
            } else {
                SystemUtils.b((List) stream.map((either) -> {
                    Objects.requireNonNull(mailbox);
                    return (CompletableFuture) either.map(mailbox::b, (runnable) -> {
                        runnable.run();
                        return CompletableFuture.completedFuture(Unit.INSTANCE);
                    });
                }).collect(Collectors.toList())).thenAccept((list) -> {
                    this.a(chunktaskqueue, mailbox);
                });
            }

        })));
    }

    private <T> ChunkTaskQueue<Function<Mailbox<Unit>, T>> b(Mailbox<T> mailbox) {
        ChunkTaskQueue<? extends Function<Mailbox<Unit>, ?>> chunktaskqueue = (ChunkTaskQueue) this.queues.get(mailbox);

        if (chunktaskqueue == null) {
            throw (IllegalArgumentException) SystemUtils.c((Throwable) (new IllegalArgumentException("No queue for: " + mailbox)));
        } else {
            return chunktaskqueue;
        }
    }

    @VisibleForTesting
    public String a() {
        String s = (String) this.queues.entrySet().stream().map((entry) -> {
            String s1 = ((Mailbox) entry.getKey()).bn();

            return s1 + "=[" + (String) ((ChunkTaskQueue) entry.getValue()).b().stream().map((olong) -> {
                return olong + ":" + new ChunkCoordIntPair(olong);
            }).collect(Collectors.joining(",")) + "]";
        }).collect(Collectors.joining(","));

        return s + ", s=" + this.sleeping.size();
    }

    public void close() {
        this.queues.keySet().forEach(Mailbox::close);
    }

    public static final class a<T> {

        final Function<Mailbox<Unit>, T> task;
        final long pos;
        final IntSupplier level;

        a(Function<Mailbox<Unit>, T> function, long i, IntSupplier intsupplier) {
            this.task = function;
            this.pos = i;
            this.level = intsupplier;
        }
    }

    public static final class b {

        final Runnable task;
        final long pos;
        final boolean clearQueue;

        b(Runnable runnable, long i, boolean flag) {
            this.task = runnable;
            this.pos = i;
            this.clearQueue = flag;
        }
    }
}

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
            return new ChunkTaskQueue<>(mailbox.name() + "_queue", i);
        }));
        this.sleeping = Sets.newHashSet(list);
        this.mailbox = new ThreadedMailbox<>(new PairedQueue.a(4), executor, "sorter");
    }

    public static <T> ChunkTaskQueueSorter.a<T> message(Function<Mailbox<Unit>, T> function, long i, IntSupplier intsupplier) {
        return new ChunkTaskQueueSorter.a<>(function, i, intsupplier);
    }

    public static ChunkTaskQueueSorter.a<Runnable> message(Runnable runnable, long i, IntSupplier intsupplier) {
        return new ChunkTaskQueueSorter.a<>((mailbox) -> {
            return () -> {
                runnable.run();
                mailbox.tell(Unit.INSTANCE);
            };
        }, i, intsupplier);
    }

    public static ChunkTaskQueueSorter.a<Runnable> message(PlayerChunk playerchunk, Runnable runnable) {
        long i = playerchunk.getPos().toLong();

        Objects.requireNonNull(playerchunk);
        return message(runnable, i, playerchunk::getQueueLevel);
    }

    public static <T> ChunkTaskQueueSorter.a<T> message(PlayerChunk playerchunk, Function<Mailbox<Unit>, T> function) {
        long i = playerchunk.getPos().toLong();

        Objects.requireNonNull(playerchunk);
        return message(function, i, playerchunk::getQueueLevel);
    }

    public static ChunkTaskQueueSorter.b release(Runnable runnable, long i, boolean flag) {
        return new ChunkTaskQueueSorter.b(runnable, i, flag);
    }

    public <T> Mailbox<ChunkTaskQueueSorter.a<T>> getProcessor(Mailbox<T> mailbox, boolean flag) {
        return (Mailbox) this.mailbox.ask((mailbox1) -> {
            return new PairedQueue.b(0, () -> {
                this.getQueue(mailbox);
                mailbox1.tell(Mailbox.of("chunk priority sorter around " + mailbox.name(), (chunktaskqueuesorter_a) -> {
                    this.submit(mailbox, chunktaskqueuesorter_a.task, chunktaskqueuesorter_a.pos, chunktaskqueuesorter_a.level, flag);
                }));
            });
        }).join();
    }

    public Mailbox<ChunkTaskQueueSorter.b> getReleaseProcessor(Mailbox<Runnable> mailbox) {
        return (Mailbox) this.mailbox.ask((mailbox1) -> {
            return new PairedQueue.b(0, () -> {
                mailbox1.tell(Mailbox.of("chunk priority sorter around " + mailbox.name(), (chunktaskqueuesorter_b) -> {
                    this.release(mailbox, chunktaskqueuesorter_b.pos, chunktaskqueuesorter_b.task, chunktaskqueuesorter_b.clearQueue);
                }));
            });
        }).join();
    }

    @Override
    public void onLevelChange(ChunkCoordIntPair chunkcoordintpair, IntSupplier intsupplier, int i, IntConsumer intconsumer) {
        this.mailbox.tell(new PairedQueue.b(0, () -> {
            int j = intsupplier.getAsInt();

            this.queues.values().forEach((chunktaskqueue) -> {
                chunktaskqueue.resortChunkTasks(j, chunkcoordintpair, i);
            });
            intconsumer.accept(i);
        }));
    }

    private <T> void release(Mailbox<T> mailbox, long i, Runnable runnable, boolean flag) {
        this.mailbox.tell(new PairedQueue.b(1, () -> {
            ChunkTaskQueue<Function<Mailbox<Unit>, T>> chunktaskqueue = this.getQueue(mailbox);

            chunktaskqueue.release(i, flag);
            if (this.sleeping.remove(mailbox)) {
                this.pollTask(chunktaskqueue, mailbox);
            }

            runnable.run();
        }));
    }

    private <T> void submit(Mailbox<T> mailbox, Function<Mailbox<Unit>, T> function, long i, IntSupplier intsupplier, boolean flag) {
        this.mailbox.tell(new PairedQueue.b(2, () -> {
            ChunkTaskQueue<Function<Mailbox<Unit>, T>> chunktaskqueue = this.getQueue(mailbox);
            int j = intsupplier.getAsInt();

            chunktaskqueue.submit(Optional.of(function), i, j);
            if (flag) {
                chunktaskqueue.submit(Optional.empty(), i, j);
            }

            if (this.sleeping.remove(mailbox)) {
                this.pollTask(chunktaskqueue, mailbox);
            }

        }));
    }

    private <T> void pollTask(ChunkTaskQueue<Function<Mailbox<Unit>, T>> chunktaskqueue, Mailbox<T> mailbox) {
        this.mailbox.tell(new PairedQueue.b(3, () -> {
            Stream<Either<Function<Mailbox<Unit>, T>, Runnable>> stream = chunktaskqueue.pop();

            if (stream == null) {
                this.sleeping.add(mailbox);
            } else {
                SystemUtils.sequence((List) stream.map((either) -> {
                    Objects.requireNonNull(mailbox);
                    return (CompletableFuture) either.map(mailbox::ask, (runnable) -> {
                        runnable.run();
                        return CompletableFuture.completedFuture(Unit.INSTANCE);
                    });
                }).collect(Collectors.toList())).thenAccept((list) -> {
                    this.pollTask(chunktaskqueue, mailbox);
                });
            }

        }));
    }

    private <T> ChunkTaskQueue<Function<Mailbox<Unit>, T>> getQueue(Mailbox<T> mailbox) {
        ChunkTaskQueue<? extends Function<Mailbox<Unit>, ?>> chunktaskqueue = (ChunkTaskQueue) this.queues.get(mailbox);

        if (chunktaskqueue == null) {
            throw (IllegalArgumentException) SystemUtils.pauseInIde(new IllegalArgumentException("No queue for: " + mailbox));
        } else {
            return chunktaskqueue;
        }
    }

    @VisibleForTesting
    public String getDebugStatus() {
        String s = (String) this.queues.entrySet().stream().map((entry) -> {
            String s1 = ((Mailbox) entry.getKey()).name();

            return s1 + "=[" + (String) ((ChunkTaskQueue) entry.getValue()).getAcquired().stream().map((olong) -> {
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

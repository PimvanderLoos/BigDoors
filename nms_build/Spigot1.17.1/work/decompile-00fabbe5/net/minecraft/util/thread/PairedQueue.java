package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

public interface PairedQueue<T, F> {

    @Nullable
    F a();

    boolean a(T t0);

    boolean b();

    int c();

    public static final class a implements PairedQueue<PairedQueue.b, Runnable> {

        private final List<Queue<Runnable>> queueList;

        public a(int i) {
            this.queueList = (List) IntStream.range(0, i).mapToObj((j) -> {
                return Queues.newConcurrentLinkedQueue();
            }).collect(Collectors.toList());
        }

        @Nullable
        @Override
        public Runnable a() {
            Iterator iterator = this.queueList.iterator();

            Runnable runnable;

            do {
                if (!iterator.hasNext()) {
                    return null;
                }

                Queue<Runnable> queue = (Queue) iterator.next();

                runnable = (Runnable) queue.poll();
            } while (runnable == null);

            return runnable;
        }

        public boolean a(PairedQueue.b pairedqueue_b) {
            int i = pairedqueue_b.a();

            ((Queue) this.queueList.get(i)).add(pairedqueue_b);
            return true;
        }

        @Override
        public boolean b() {
            return this.queueList.stream().allMatch(Collection::isEmpty);
        }

        @Override
        public int c() {
            int i = 0;

            Queue queue;

            for (Iterator iterator = this.queueList.iterator(); iterator.hasNext(); i += queue.size()) {
                queue = (Queue) iterator.next();
            }

            return i;
        }
    }

    public static final class b implements Runnable {

        private final int priority;
        private final Runnable task;

        public b(int i, Runnable runnable) {
            this.priority = i;
            this.task = runnable;
        }

        public void run() {
            this.task.run();
        }

        public int a() {
            return this.priority;
        }
    }

    public static final class c<T> implements PairedQueue<T, T> {

        private final Queue<T> queue;

        public c(Queue<T> queue) {
            this.queue = queue;
        }

        @Nullable
        @Override
        public T a() {
            return this.queue.poll();
        }

        @Override
        public boolean a(T t0) {
            return this.queue.add(t0);
        }

        @Override
        public boolean b() {
            return this.queue.isEmpty();
        }

        @Override
        public int c() {
            return this.queue.size();
        }
    }
}

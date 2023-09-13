package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamAccumulator<T> {

    final List<T> cache = Lists.newArrayList();
    final Spliterator<T> source;

    public StreamAccumulator(Stream<T> stream) {
        this.source = stream.spliterator();
    }

    public Stream<T> a() {
        return StreamSupport.stream(new AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
            private int index;

            public boolean tryAdvance(Consumer<? super T> consumer) {
                while (true) {
                    if (this.index >= StreamAccumulator.this.cache.size()) {
                        Spliterator spliterator = StreamAccumulator.this.source;
                        List list = StreamAccumulator.this.cache;

                        Objects.requireNonNull(StreamAccumulator.this.cache);
                        if (spliterator.tryAdvance(list::add)) {
                            continue;
                        }

                        return false;
                    }

                    consumer.accept(StreamAccumulator.this.cache.get(this.index++));
                    return true;
                }
            }
        }, false);
    }
}

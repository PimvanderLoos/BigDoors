package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamAccumulator<T> {

    private final List<T> a = Lists.newArrayList();
    private final Iterator<T> b;

    public StreamAccumulator(Stream<T> stream) {
        this.b = stream.iterator();
    }

    public Stream<T> a() {
        return StreamSupport.stream(new AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
            private int b = 0;

            public boolean tryAdvance(Consumer<? super T> consumer) {
                Object object;

                if (this.b >= StreamAccumulator.this.a.size()) {
                    if (!StreamAccumulator.this.b.hasNext()) {
                        return false;
                    }

                    object = StreamAccumulator.this.b.next();
                    StreamAccumulator.this.a.add(object);
                } else {
                    object = StreamAccumulator.this.a.get(this.b);
                }

                ++this.b;
                consumer.accept(object);
                return true;
            }
        }, false);
    }
}

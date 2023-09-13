package net.minecraft.server;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface IRegistry<K, V> extends Iterable<V> {

    void a(K k0, V v0);

    default Stream<V> e() {
        return StreamSupport.stream(this.spliterator(), false);
    }
}

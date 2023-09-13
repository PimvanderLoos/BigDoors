package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.ListBuilder.Builder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.MapBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class DynamicOpsWrapper<T> implements DynamicOps<T> {

    protected final DynamicOps<T> delegate;

    protected DynamicOpsWrapper(DynamicOps<T> dynamicops) {
        this.delegate = dynamicops;
    }

    public T empty() {
        return this.delegate.empty();
    }

    public <U> U convertTo(DynamicOps<U> dynamicops, T t0) {
        return this.delegate.convertTo(dynamicops, t0);
    }

    public DataResult<Number> getNumberValue(T t0) {
        return this.delegate.getNumberValue(t0);
    }

    public T createNumeric(Number number) {
        return this.delegate.createNumeric(number);
    }

    public T createByte(byte b0) {
        return this.delegate.createByte(b0);
    }

    public T createShort(short short0) {
        return this.delegate.createShort(short0);
    }

    public T createInt(int i) {
        return this.delegate.createInt(i);
    }

    public T createLong(long i) {
        return this.delegate.createLong(i);
    }

    public T createFloat(float f) {
        return this.delegate.createFloat(f);
    }

    public T createDouble(double d0) {
        return this.delegate.createDouble(d0);
    }

    public DataResult<Boolean> getBooleanValue(T t0) {
        return this.delegate.getBooleanValue(t0);
    }

    public T createBoolean(boolean flag) {
        return this.delegate.createBoolean(flag);
    }

    public DataResult<String> getStringValue(T t0) {
        return this.delegate.getStringValue(t0);
    }

    public T createString(String s) {
        return this.delegate.createString(s);
    }

    public DataResult<T> mergeToList(T t0, T t1) {
        return this.delegate.mergeToList(t0, t1);
    }

    public DataResult<T> mergeToList(T t0, List<T> list) {
        return this.delegate.mergeToList(t0, list);
    }

    public DataResult<T> mergeToMap(T t0, T t1, T t2) {
        return this.delegate.mergeToMap(t0, t1, t2);
    }

    public DataResult<T> mergeToMap(T t0, MapLike<T> maplike) {
        return this.delegate.mergeToMap(t0, maplike);
    }

    public DataResult<Stream<Pair<T, T>>> getMapValues(T t0) {
        return this.delegate.getMapValues(t0);
    }

    public DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(T t0) {
        return this.delegate.getMapEntries(t0);
    }

    public T createMap(Stream<Pair<T, T>> stream) {
        return this.delegate.createMap(stream);
    }

    public DataResult<MapLike<T>> getMap(T t0) {
        return this.delegate.getMap(t0);
    }

    public DataResult<Stream<T>> getStream(T t0) {
        return this.delegate.getStream(t0);
    }

    public DataResult<Consumer<Consumer<T>>> getList(T t0) {
        return this.delegate.getList(t0);
    }

    public T createList(Stream<T> stream) {
        return this.delegate.createList(stream);
    }

    public DataResult<ByteBuffer> getByteBuffer(T t0) {
        return this.delegate.getByteBuffer(t0);
    }

    public T createByteList(ByteBuffer bytebuffer) {
        return this.delegate.createByteList(bytebuffer);
    }

    public DataResult<IntStream> getIntStream(T t0) {
        return this.delegate.getIntStream(t0);
    }

    public T createIntList(IntStream intstream) {
        return this.delegate.createIntList(intstream);
    }

    public DataResult<LongStream> getLongStream(T t0) {
        return this.delegate.getLongStream(t0);
    }

    public T createLongList(LongStream longstream) {
        return this.delegate.createLongList(longstream);
    }

    public T remove(T t0, String s) {
        return this.delegate.remove(t0, s);
    }

    public boolean compressMaps() {
        return this.delegate.compressMaps();
    }

    public ListBuilder<T> listBuilder() {
        return new Builder(this);
    }

    public RecordBuilder<T> mapBuilder() {
        return new MapBuilder(this);
    }
}

package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class DynamicOpsWrapper<T> implements DynamicOps<T> {

    protected final DynamicOps<T> a;

    protected DynamicOpsWrapper(DynamicOps<T> dynamicops) {
        this.a = dynamicops;
    }

    public T empty() {
        return this.a.empty();
    }

    public <U> U convertTo(DynamicOps<U> dynamicops, T t0) {
        return this.a.convertTo(dynamicops, t0);
    }

    public DataResult<Number> getNumberValue(T t0) {
        return this.a.getNumberValue(t0);
    }

    public T createNumeric(Number number) {
        return this.a.createNumeric(number);
    }

    public T createByte(byte b0) {
        return this.a.createByte(b0);
    }

    public T createShort(short short0) {
        return this.a.createShort(short0);
    }

    public T createInt(int i) {
        return this.a.createInt(i);
    }

    public T createLong(long i) {
        return this.a.createLong(i);
    }

    public T createFloat(float f) {
        return this.a.createFloat(f);
    }

    public T createDouble(double d0) {
        return this.a.createDouble(d0);
    }

    public DataResult<Boolean> getBooleanValue(T t0) {
        return this.a.getBooleanValue(t0);
    }

    public T createBoolean(boolean flag) {
        return this.a.createBoolean(flag);
    }

    public DataResult<String> getStringValue(T t0) {
        return this.a.getStringValue(t0);
    }

    public T createString(String s) {
        return this.a.createString(s);
    }

    public DataResult<T> mergeToList(T t0, T t1) {
        return this.a.mergeToList(t0, t1);
    }

    public DataResult<T> mergeToList(T t0, List<T> list) {
        return this.a.mergeToList(t0, list);
    }

    public DataResult<T> mergeToMap(T t0, T t1, T t2) {
        return this.a.mergeToMap(t0, t1, t2);
    }

    public DataResult<T> mergeToMap(T t0, MapLike<T> maplike) {
        return this.a.mergeToMap(t0, maplike);
    }

    public DataResult<Stream<Pair<T, T>>> getMapValues(T t0) {
        return this.a.getMapValues(t0);
    }

    public DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(T t0) {
        return this.a.getMapEntries(t0);
    }

    public T createMap(Stream<Pair<T, T>> stream) {
        return this.a.createMap(stream);
    }

    public DataResult<MapLike<T>> getMap(T t0) {
        return this.a.getMap(t0);
    }

    public DataResult<Stream<T>> getStream(T t0) {
        return this.a.getStream(t0);
    }

    public DataResult<Consumer<Consumer<T>>> getList(T t0) {
        return this.a.getList(t0);
    }

    public T createList(Stream<T> stream) {
        return this.a.createList(stream);
    }

    public DataResult<ByteBuffer> getByteBuffer(T t0) {
        return this.a.getByteBuffer(t0);
    }

    public T createByteList(ByteBuffer bytebuffer) {
        return this.a.createByteList(bytebuffer);
    }

    public DataResult<IntStream> getIntStream(T t0) {
        return this.a.getIntStream(t0);
    }

    public T createIntList(IntStream intstream) {
        return this.a.createIntList(intstream);
    }

    public DataResult<LongStream> getLongStream(T t0) {
        return this.a.getLongStream(t0);
    }

    public T createLongList(LongStream longstream) {
        return this.a.createLongList(longstream);
    }

    public T remove(T t0, String s) {
        return this.a.remove(t0, s);
    }

    public boolean compressMaps() {
        return this.a.compressMaps();
    }

    public ListBuilder<T> listBuilder() {
        return this.a.listBuilder();
    }

    public RecordBuilder<T> mapBuilder() {
        return this.a.mapBuilder();
    }
}

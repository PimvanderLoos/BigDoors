package net.minecraft.resources;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.stream.Stream;
import net.minecraft.core.IRegistry;

public final class RegistryLookupCodec<E> extends MapCodec<IRegistry<E>> {

    private final ResourceKey<? extends IRegistry<E>> a;

    public static <E> RegistryLookupCodec<E> a(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return new RegistryLookupCodec<>(resourcekey);
    }

    private RegistryLookupCodec(ResourceKey<? extends IRegistry<E>> resourcekey) {
        this.a = resourcekey;
    }

    public <T> RecordBuilder<T> encode(IRegistry<E> iregistry, DynamicOps<T> dynamicops, RecordBuilder<T> recordbuilder) {
        return recordbuilder;
    }

    public <T> DataResult<IRegistry<E>> decode(DynamicOps<T> dynamicops, MapLike<T> maplike) {
        return dynamicops instanceof RegistryReadOps ? ((RegistryReadOps) dynamicops).a(this.a) : DataResult.error("Not a registry ops");
    }

    public String toString() {
        return "RegistryLookupCodec[" + this.a + "]";
    }

    public <T> Stream<T> keys(DynamicOps<T> dynamicops) {
        return Stream.empty();
    }
}

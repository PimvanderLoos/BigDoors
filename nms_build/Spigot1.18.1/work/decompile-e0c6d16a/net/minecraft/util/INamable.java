package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface INamable {

    String getSerializedName();

    static <E extends Enum<E> & INamable> Codec<E> fromEnum(Supplier<E[]> supplier, Function<String, E> function) {
        E[] ae = (Enum[]) supplier.get();

        return ExtraCodecs.orCompressed(ExtraCodecs.stringResolverCodec((object) -> {
            return ((INamable) object).getSerializedName();
        }, function), ExtraCodecs.idResolverCodec((object) -> {
            return ((Enum) object).ordinal();
        }, (i) -> {
            return i >= 0 && i < ae.length ? ae[i] : null;
        }, -1));
    }

    static Keyable keys(final INamable[] ainamable) {
        return new Keyable() {
            public <T> Stream<T> keys(DynamicOps<T> dynamicops) {
                Stream stream = Arrays.stream(ainamable).map(INamable::getSerializedName);

                Objects.requireNonNull(dynamicops);
                return stream.map(dynamicops::createString);
            }
        };
    }
}

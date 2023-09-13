package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface INamable {

    int PRE_BUILT_MAP_THRESHOLD = 16;

    String getSerializedName();

    static <E extends Enum<E> & INamable> INamable.a<E> fromEnum(Supplier<E[]> supplier) {
        return fromEnumWithMapping(supplier, (s) -> {
            return s;
        });
    }

    static <E extends Enum<E> & INamable> INamable.a<E> fromEnumWithMapping(Supplier<E[]> supplier, Function<String, String> function) {
        E[] ae = (Enum[]) supplier.get();

        if (ae.length > 16) {
            Map<String, E> map = (Map) Arrays.stream(ae).collect(Collectors.toMap((oenum) -> {
                return (String) function.apply(((INamable) oenum).getSerializedName());
            }, (oenum) -> {
                return oenum;
            }));

            return new INamable.a<>(ae, (s) -> {
                return s == null ? null : (Enum) map.get(s);
            });
        } else {
            return new INamable.a<>(ae, (s) -> {
                Enum[] aenum = ae;
                int i = ae.length;

                for (int j = 0; j < i; ++j) {
                    E e0 = aenum[j];

                    if (((String) function.apply(((INamable) e0).getSerializedName())).equals(s)) {
                        return e0;
                    }
                }

                return null;
            });
        }
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

    /** @deprecated */
    @Deprecated
    public static class a<E extends Enum<E> & INamable> implements Codec<E> {

        private final Codec<E> codec;
        private final Function<String, E> resolver;

        public a(E[] ae, Function<String, E> function) {
            this.codec = ExtraCodecs.orCompressed(ExtraCodecs.stringResolverCodec((object) -> {
                return ((INamable) object).getSerializedName();
            }, function), ExtraCodecs.idResolverCodec((object) -> {
                return ((Enum) object).ordinal();
            }, (i) -> {
                return i >= 0 && i < ae.length ? ae[i] : null;
            }, -1));
            this.resolver = function;
        }

        public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicops, T t0) {
            return this.codec.decode(dynamicops, t0);
        }

        public <T> DataResult<T> encode(E e0, DynamicOps<T> dynamicops, T t0) {
            return this.codec.encode(e0, dynamicops, t0);
        }

        @Nullable
        public E byName(@Nullable String s) {
            return (Enum) this.resolver.apply(s);
        }

        public E byName(@Nullable String s, E e0) {
            return (Enum) Objects.requireNonNullElse(this.byName(s), e0);
        }
    }
}

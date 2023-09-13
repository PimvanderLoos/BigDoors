package net.minecraft.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Codec.ResultFunction;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;
import net.minecraft.core.HolderSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.MinecraftKey;
import org.apache.commons.lang3.mutable.MutableObject;

public class ExtraCodecs {

    public static final Codec<UUID> UUID = UUIDUtil.CODEC;
    public static final Codec<Integer> NON_NEGATIVE_INT = intRangeWithMessage(0, Integer.MAX_VALUE, (integer) -> {
        return "Value must be non-negative: " + integer;
    });
    public static final Codec<Integer> POSITIVE_INT = intRangeWithMessage(1, Integer.MAX_VALUE, (integer) -> {
        return "Value must be positive: " + integer;
    });
    public static final Codec<Float> POSITIVE_FLOAT = floatRangeMinExclusiveWithMessage(0.0F, Float.MAX_VALUE, (ofloat) -> {
        return "Value must be positive: " + ofloat;
    });
    public static final Codec<Pattern> PATTERN = Codec.STRING.comapFlatMap((s) -> {
        try {
            return DataResult.success(Pattern.compile(s));
        } catch (PatternSyntaxException patternsyntaxexception) {
            return DataResult.error("Invalid regex pattern '" + s + "': " + patternsyntaxexception.getMessage());
        }
    }, Pattern::pattern);
    public static final Codec<Instant> INSTANT_ISO8601 = instantCodec(DateTimeFormatter.ISO_INSTANT);
    public static final Codec<byte[]> BASE64_STRING = Codec.STRING.comapFlatMap((s) -> {
        try {
            return DataResult.success(Base64.getDecoder().decode(s));
        } catch (IllegalArgumentException illegalargumentexception) {
            return DataResult.error("Malformed base64 string");
        }
    }, (abyte) -> {
        return Base64.getEncoder().encodeToString(abyte);
    });
    public static final Codec<ExtraCodecs.d> TAG_OR_ELEMENT_ID = Codec.STRING.comapFlatMap((s) -> {
        return s.startsWith("#") ? MinecraftKey.read(s.substring(1)).map((minecraftkey) -> {
            return new ExtraCodecs.d(minecraftkey, true);
        }) : MinecraftKey.read(s).map((minecraftkey) -> {
            return new ExtraCodecs.d(minecraftkey, false);
        });
    }, ExtraCodecs.d::decoratedId);
    public static final Function<Optional<Long>, OptionalLong> toOptionalLong = (optional) -> {
        return (OptionalLong) optional.map(OptionalLong::of).orElseGet(OptionalLong::empty);
    };
    public static final Function<OptionalLong, Optional<Long>> fromOptionalLong = (optionallong) -> {
        return optionallong.isPresent() ? Optional.of(optionallong.getAsLong()) : Optional.empty();
    };

    public ExtraCodecs() {}

    public static <F, S> Codec<Either<F, S>> xor(Codec<F> codec, Codec<S> codec1) {
        return new ExtraCodecs.e<>(codec, codec1);
    }

    public static <P, I> Codec<I> intervalCodec(Codec<P> codec, String s, String s1, BiFunction<P, P, DataResult<I>> bifunction, Function<I, P> function, Function<I, P> function1) {
        Codec<I> codec1 = Codec.list(codec).comapFlatMap((list) -> {
            return SystemUtils.fixedSize(list, 2).flatMap((list1) -> {
                P p0 = list1.get(0);
                P p1 = list1.get(1);

                return (DataResult) bifunction.apply(p0, p1);
            });
        }, (object) -> {
            return ImmutableList.of(function.apply(object), function1.apply(object));
        });
        Codec<I> codec2 = RecordCodecBuilder.create((instance) -> {
            return instance.group(codec.fieldOf(s).forGetter(Pair::getFirst), codec.fieldOf(s1).forGetter(Pair::getSecond)).apply(instance, Pair::of);
        }).comapFlatMap((pair) -> {
            return (DataResult) bifunction.apply(pair.getFirst(), pair.getSecond());
        }, (object) -> {
            return Pair.of(function.apply(object), function1.apply(object));
        });
        Codec<I> codec3 = (new ExtraCodecs.b<>(codec1, codec2)).xmap((either) -> {
            return either.map((object) -> {
                return object;
            }, (object) -> {
                return object;
            });
        }, Either::left);

        return Codec.either(codec, codec3).comapFlatMap((either) -> {
            return (DataResult) either.map((object) -> {
                return (DataResult) bifunction.apply(object, object);
            }, DataResult::success);
        }, (object) -> {
            P p0 = function.apply(object);
            P p1 = function1.apply(object);

            return Objects.equals(p0, p1) ? Either.left(p0) : Either.right(object);
        });
    }

    public static <A> ResultFunction<A> orElsePartial(final A a0) {
        return new ResultFunction<A>() {
            public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> dynamicops, T t0, DataResult<Pair<A, T>> dataresult) {
                MutableObject<String> mutableobject = new MutableObject();

                Objects.requireNonNull(mutableobject);
                Optional<Pair<A, T>> optional = dataresult.resultOrPartial(mutableobject::setValue);

                return optional.isPresent() ? dataresult : DataResult.error("(" + (String) mutableobject.getValue() + " -> using default)", Pair.of(a0, t0));
            }

            public <T> DataResult<T> coApply(DynamicOps<T> dynamicops, A a1, DataResult<T> dataresult) {
                return dataresult;
            }

            public String toString() {
                return "OrElsePartial[" + a0 + "]";
            }
        };
    }

    public static <E> Codec<E> idResolverCodec(ToIntFunction<E> tointfunction, IntFunction<E> intfunction, int i) {
        return Codec.INT.flatXmap((integer) -> {
            return (DataResult) Optional.ofNullable(intfunction.apply(integer)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown element id: " + integer);
            });
        }, (object) -> {
            int j = tointfunction.applyAsInt(object);

            return j == i ? DataResult.error("Element with unknown id: " + object) : DataResult.success(j);
        });
    }

    public static <E> Codec<E> stringResolverCodec(Function<E, String> function, Function<String, E> function1) {
        return Codec.STRING.flatXmap((s) -> {
            return (DataResult) Optional.ofNullable(function1.apply(s)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown element name:" + s);
            });
        }, (object) -> {
            return (DataResult) Optional.ofNullable((String) function.apply(object)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Element with unknown name: " + object);
            });
        });
    }

    public static <E> Codec<E> orCompressed(final Codec<E> codec, final Codec<E> codec1) {
        return new Codec<E>() {
            public <T> DataResult<T> encode(E e0, DynamicOps<T> dynamicops, T t0) {
                return dynamicops.compressMaps() ? codec1.encode(e0, dynamicops, t0) : codec.encode(e0, dynamicops, t0);
            }

            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicops, T t0) {
                return dynamicops.compressMaps() ? codec1.decode(dynamicops, t0) : codec.decode(dynamicops, t0);
            }

            public String toString() {
                return codec + " orCompressed " + codec1;
            }
        };
    }

    public static <E> Codec<E> overrideLifecycle(Codec<E> codec, final Function<E, Lifecycle> function, final Function<E, Lifecycle> function1) {
        return codec.mapResult(new ResultFunction<E>() {
            public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> dynamicops, T t0, DataResult<Pair<E, T>> dataresult) {
                return (DataResult) dataresult.result().map((pair) -> {
                    return dataresult.setLifecycle((Lifecycle) function.apply(pair.getFirst()));
                }).orElse(dataresult);
            }

            public <T> DataResult<T> coApply(DynamicOps<T> dynamicops, E e0, DataResult<T> dataresult) {
                return dataresult.setLifecycle((Lifecycle) function1.apply(e0));
            }

            public String toString() {
                return "WithLifecycle[" + function + " " + function1 + "]";
            }
        });
    }

    private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRangeWithMessage(N n0, N n1, Function<N, String> function) {
        return (number) -> {
            return ((Comparable) number).compareTo(n0) >= 0 && ((Comparable) number).compareTo(n1) <= 0 ? DataResult.success(number) : DataResult.error((String) function.apply(number));
        };
    }

    private static Codec<Integer> intRangeWithMessage(int i, int j, Function<Integer, String> function) {
        Function<Integer, DataResult<Integer>> function1 = checkRangeWithMessage(i, j, function);

        return Codec.INT.flatXmap(function1, function1);
    }

    private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRangeMinExclusiveWithMessage(N n0, N n1, Function<N, String> function) {
        return (number) -> {
            return ((Comparable) number).compareTo(n0) > 0 && ((Comparable) number).compareTo(n1) <= 0 ? DataResult.success(number) : DataResult.error((String) function.apply(number));
        };
    }

    private static Codec<Float> floatRangeMinExclusiveWithMessage(float f, float f1, Function<Float, String> function) {
        Function<Float, DataResult<Float>> function1 = checkRangeMinExclusiveWithMessage(f, f1, function);

        return Codec.FLOAT.flatXmap(function1, function1);
    }

    public static <T> Function<List<T>, DataResult<List<T>>> nonEmptyListCheck() {
        return (list) -> {
            return list.isEmpty() ? DataResult.error("List must have contents") : DataResult.success(list);
        };
    }

    public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> codec) {
        return codec.flatXmap(nonEmptyListCheck(), nonEmptyListCheck());
    }

    public static <T> Function<HolderSet<T>, DataResult<HolderSet<T>>> nonEmptyHolderSetCheck() {
        return (holderset) -> {
            return holderset.unwrap().right().filter(List::isEmpty).isPresent() ? DataResult.error("List must have contents") : DataResult.success(holderset);
        };
    }

    public static <T> Codec<HolderSet<T>> nonEmptyHolderSet(Codec<HolderSet<T>> codec) {
        return codec.flatXmap(nonEmptyHolderSetCheck(), nonEmptyHolderSetCheck());
    }

    public static <A> Codec<A> lazyInitializedCodec(Supplier<Codec<A>> supplier) {
        return new ExtraCodecs.c<>(supplier);
    }

    public static <E> MapCodec<E> retrieveContext(final Function<DynamicOps<?>, DataResult<E>> function) {
        class a extends MapCodec<E> {

            a() {}

            public <T> RecordBuilder<T> encode(E e0, DynamicOps<T> dynamicops, RecordBuilder<T> recordbuilder) {
                return recordbuilder;
            }

            public <T> DataResult<E> decode(DynamicOps<T> dynamicops, MapLike<T> maplike) {
                return (DataResult) function.apply(dynamicops);
            }

            public String toString() {
                return "ContextRetrievalCodec[" + function + "]";
            }

            public <T> Stream<T> keys(DynamicOps<T> dynamicops) {
                return Stream.empty();
            }
        }

        return new a();
    }

    public static <E, L extends Collection<E>, T> Function<L, DataResult<L>> ensureHomogenous(Function<E, T> function) {
        return (collection) -> {
            Iterator<E> iterator = collection.iterator();

            if (iterator.hasNext()) {
                Object object = function.apply(iterator.next());

                while (iterator.hasNext()) {
                    E e0 = iterator.next();
                    T t0 = function.apply(e0);

                    if (t0 != object) {
                        return DataResult.error("Mixed type list: element " + e0 + " had type " + t0 + ", but list is of type " + object);
                    }
                }
            }

            return DataResult.success(collection, Lifecycle.stable());
        };
    }

    public static <A> Codec<A> catchDecoderException(final Codec<A> codec) {
        return Codec.of(codec, new Decoder<A>() {
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> dynamicops, T t0) {
                try {
                    return codec.decode(dynamicops, t0);
                } catch (Exception exception) {
                    return DataResult.error("Cauch exception decoding " + t0 + ": " + exception.getMessage());
                }
            }
        });
    }

    public static Codec<Instant> instantCodec(DateTimeFormatter datetimeformatter) {
        PrimitiveCodec primitivecodec = Codec.STRING;
        Function function = (s) -> {
            try {
                return DataResult.success(Instant.from(datetimeformatter.parse(s)));
            } catch (Exception exception) {
                return DataResult.error(exception.getMessage());
            }
        };

        Objects.requireNonNull(datetimeformatter);
        return primitivecodec.comapFlatMap(function, datetimeformatter::format);
    }

    public static MapCodec<OptionalLong> asOptionalLong(MapCodec<Optional<Long>> mapcodec) {
        return mapcodec.xmap(ExtraCodecs.toOptionalLong, ExtraCodecs.fromOptionalLong);
    }

    private static final class e<F, S> implements Codec<Either<F, S>> {

        private final Codec<F> first;
        private final Codec<S> second;

        public e(Codec<F> codec, Codec<S> codec1) {
            this.first = codec;
            this.second = codec1;
        }

        public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> dynamicops, T t0) {
            DataResult<Pair<Either<F, S>, T>> dataresult = this.first.decode(dynamicops, t0).map((pair) -> {
                return pair.mapFirst(Either::left);
            });
            DataResult<Pair<Either<F, S>, T>> dataresult1 = this.second.decode(dynamicops, t0).map((pair) -> {
                return pair.mapFirst(Either::right);
            });
            Optional<Pair<Either<F, S>, T>> optional = dataresult.result();
            Optional<Pair<Either<F, S>, T>> optional1 = dataresult1.result();

            return optional.isPresent() && optional1.isPresent() ? DataResult.error("Both alternatives read successfully, can not pick the correct one; first: " + optional.get() + " second: " + optional1.get(), (Pair) optional.get()) : (optional.isPresent() ? dataresult : dataresult1);
        }

        public <T> DataResult<T> encode(Either<F, S> either, DynamicOps<T> dynamicops, T t0) {
            return (DataResult) either.map((object) -> {
                return this.first.encode(object, dynamicops, t0);
            }, (object) -> {
                return this.second.encode(object, dynamicops, t0);
            });
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                ExtraCodecs.e<?, ?> extracodecs_e = (ExtraCodecs.e) object;

                return Objects.equals(this.first, extracodecs_e.first) && Objects.equals(this.second, extracodecs_e.second);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.first, this.second});
        }

        public String toString() {
            return "XorCodec[" + this.first + ", " + this.second + "]";
        }
    }

    private static final class b<F, S> implements Codec<Either<F, S>> {

        private final Codec<F> first;
        private final Codec<S> second;

        public b(Codec<F> codec, Codec<S> codec1) {
            this.first = codec;
            this.second = codec1;
        }

        public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> dynamicops, T t0) {
            DataResult<Pair<Either<F, S>, T>> dataresult = this.first.decode(dynamicops, t0).map((pair) -> {
                return pair.mapFirst(Either::left);
            });

            if (!dataresult.error().isPresent()) {
                return dataresult;
            } else {
                DataResult<Pair<Either<F, S>, T>> dataresult1 = this.second.decode(dynamicops, t0).map((pair) -> {
                    return pair.mapFirst(Either::right);
                });

                return !dataresult1.error().isPresent() ? dataresult1 : dataresult.apply2((pair, pair1) -> {
                    return pair1;
                }, dataresult1);
            }
        }

        public <T> DataResult<T> encode(Either<F, S> either, DynamicOps<T> dynamicops, T t0) {
            return (DataResult) either.map((object) -> {
                return this.first.encode(object, dynamicops, t0);
            }, (object) -> {
                return this.second.encode(object, dynamicops, t0);
            });
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                ExtraCodecs.b<?, ?> extracodecs_b = (ExtraCodecs.b) object;

                return Objects.equals(this.first, extracodecs_b.first) && Objects.equals(this.second, extracodecs_b.second);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.first, this.second});
        }

        public String toString() {
            return "EitherCodec[" + this.first + ", " + this.second + "]";
        }
    }

    private static record c<A> (Supplier<Codec<A>> delegate) implements Codec<A> {

        c(Supplier<Codec<A>> supplier) {
            Objects.requireNonNull(supplier);
            Supplier<Codec<A>> supplier1 = Suppliers.memoize(supplier::get);

            this.delegate = supplier1;
        }

        public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> dynamicops, T t0) {
            return ((Codec) this.delegate.get()).decode(dynamicops, t0);
        }

        public <T> DataResult<T> encode(A a0, DynamicOps<T> dynamicops, T t0) {
            return ((Codec) this.delegate.get()).encode(a0, dynamicops, t0);
        }
    }

    public static record d(MinecraftKey id, boolean tag) {

        public String toString() {
            return this.decoratedId();
        }

        private String decoratedId() {
            return this.tag ? "#" + this.id : this.id.toString();
        }
    }
}

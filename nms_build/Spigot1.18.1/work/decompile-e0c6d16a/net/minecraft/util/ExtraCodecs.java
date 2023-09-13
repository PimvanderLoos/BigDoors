package net.minecraft.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Codec.ResultFunction;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.SystemUtils;
import org.apache.commons.lang3.mutable.MutableObject;

public class ExtraCodecs {

    public static final Codec<Integer> NON_NEGATIVE_INT = intRangeWithMessage(0, Integer.MAX_VALUE, (integer) -> {
        return "Value must be non-negative: " + integer;
    });
    public static final Codec<Integer> POSITIVE_INT = intRangeWithMessage(1, Integer.MAX_VALUE, (integer) -> {
        return "Value must be positive: " + integer;
    });
    public static final Codec<Float> POSITIVE_FLOAT = floatRangeMinExclusiveWithMessage(0.0F, Float.MAX_VALUE, (ofloat) -> {
        return "Value must be positive: " + ofloat;
    });

    public ExtraCodecs() {}

    public static <F, S> Codec<Either<F, S>> xor(Codec<F> codec, Codec<S> codec1) {
        return new ExtraCodecs.c<>(codec, codec1);
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
        Codec<I> codec3 = (new ExtraCodecs.a<>(codec1, codec2)).xmap((either) -> {
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

    public static <T> Function<List<Supplier<T>>, DataResult<List<Supplier<T>>>> nonNullSupplierListCheck() {
        return (list) -> {
            List<String> list1 = Lists.newArrayList();

            for (int i = 0; i < list.size(); ++i) {
                Supplier supplier = (Supplier) list.get(i);

                try {
                    if (supplier.get() == null) {
                        list1.add("Missing value [" + i + "] : " + supplier);
                    }
                } catch (Exception exception) {
                    list1.add("Invalid value [" + i + "]: " + supplier + ", message: " + exception.getMessage());
                }
            }

            return !list1.isEmpty() ? DataResult.error(String.join("; ", list1)) : DataResult.success(list, Lifecycle.stable());
        };
    }

    public static <T> Function<Supplier<T>, DataResult<Supplier<T>>> nonNullSupplierCheck() {
        return (supplier) -> {
            try {
                if (supplier.get() == null) {
                    return DataResult.error("Missing value: " + supplier);
                }
            } catch (Exception exception) {
                return DataResult.error("Invalid value: " + supplier + ", message: " + exception.getMessage());
            }

            return DataResult.success(supplier, Lifecycle.stable());
        };
    }

    public static <A> Codec<A> lazyInitializedCodec(Supplier<Codec<A>> supplier) {
        return new ExtraCodecs.b<>(supplier);
    }

    private static final class c<F, S> implements Codec<Either<F, S>> {

        private final Codec<F> first;
        private final Codec<S> second;

        public c(Codec<F> codec, Codec<S> codec1) {
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
                ExtraCodecs.c<?, ?> extracodecs_c = (ExtraCodecs.c) object;

                return Objects.equals(this.first, extracodecs_c.first) && Objects.equals(this.second, extracodecs_c.second);
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

    private static final class a<F, S> implements Codec<Either<F, S>> {

        private final Codec<F> first;
        private final Codec<S> second;

        public a(Codec<F> codec, Codec<S> codec1) {
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
                ExtraCodecs.a<?, ?> extracodecs_a = (ExtraCodecs.a) object;

                return Objects.equals(this.first, extracodecs_a.first) && Objects.equals(this.second, extracodecs_a.second);
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

    private static record b<A> (Supplier<Codec<A>> a) implements Codec<A> {

        private final Supplier<Codec<A>> delegate;

        b(Supplier<Codec<A>> supplier) {
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

        public Supplier<Codec<A>> delegate() {
            return this.delegate;
        }
    }
}

package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.mutable.MutableObject;

public interface CubicSpline<C> extends ToFloatFunction<C> {

    @VisibleForDebug
    String parityString();

    static <C> Codec<CubicSpline<C>> codec(Codec<ToFloatFunction<C>> codec) {
        MutableObject<Codec<CubicSpline<C>>> mutableobject = new MutableObject();
        Codec<a<C>> codec1 = RecordCodecBuilder.create((instance) -> {
            RecordCodecBuilder recordcodecbuilder = Codec.FLOAT.fieldOf("location").forGetter(a::location);

            Objects.requireNonNull(mutableobject);
            return instance.group(recordcodecbuilder, ExtraCodecs.lazyInitializedCodec(mutableobject::getValue).fieldOf("value").forGetter(a::value), Codec.FLOAT.fieldOf("derivative").forGetter(a::derivative)).apply(instance, (f, cubicspline, f1) -> {
                record a<C> (float a, CubicSpline<C> b, float c) {

                    private final float location;
                    private final CubicSpline<C> value;
                    private final float derivative;

                    a(float f2, CubicSpline<C> cubicspline1, float f3) {
                        this.location = f2;
                        this.value = cubicspline1;
                        this.derivative = f3;
                    }

                    public float location() {
                        return this.location;
                    }

                    public CubicSpline<C> value() {
                        return this.value;
                    }

                    public float derivative() {
                        return this.derivative;
                    }
                }

                return new a<>(f, cubicspline, f1);
            });
        });
        Codec<CubicSpline.d<C>> codec2 = RecordCodecBuilder.create((instance) -> {
            return instance.group(codec.fieldOf("coordinate").forGetter(CubicSpline.d::coordinate), ExtraCodecs.nonEmptyList(codec1.listOf()).fieldOf("points").forGetter((cubicspline_d) -> {
                return IntStream.range(0, cubicspline_d.locations.length).mapToObj((i) -> {
                    return new a<>(cubicspline_d.locations()[i], (CubicSpline) cubicspline_d.values().get(i), cubicspline_d.derivatives()[i]);
                }).toList();
            })).apply(instance, (tofloatfunction, list) -> {
                float[] afloat = new float[list.size()];
                Builder<CubicSpline<C>> builder = ImmutableList.builder();
                float[] afloat1 = new float[list.size()];

                for (int i = 0; i < list.size(); ++i) {
                    a<C> a0 = (a) list.get(i);

                    afloat[i] = a0.location();
                    builder.add(a0.value());
                    afloat1[i] = a0.derivative();
                }

                return new CubicSpline.d<>(tofloatfunction, afloat, builder.build(), afloat1);
            });
        });

        mutableobject.setValue(Codec.either(Codec.FLOAT, codec2).xmap((either) -> {
            return (CubicSpline) either.map(CubicSpline.c::new, (cubicspline_d) -> {
                return cubicspline_d;
            });
        }, (cubicspline) -> {
            Either either;

            if (cubicspline instanceof CubicSpline.c) {
                CubicSpline.c<C> cubicspline_c = (CubicSpline.c) cubicspline;

                either = Either.left(cubicspline_c.value());
            } else {
                either = Either.right((CubicSpline.d) cubicspline);
            }

            return either;
        }));
        return (Codec) mutableobject.getValue();
    }

    static <C> CubicSpline<C> constant(float f) {
        return new CubicSpline.c<>(f);
    }

    static <C> CubicSpline.b<C> builder(ToFloatFunction<C> tofloatfunction) {
        return new CubicSpline.b<>(tofloatfunction);
    }

    static <C> CubicSpline.b<C> builder(ToFloatFunction<C> tofloatfunction, ToFloatFunction<Float> tofloatfunction1) {
        return new CubicSpline.b<>(tofloatfunction, tofloatfunction1);
    }

    @VisibleForDebug
    public static record c<C> (float a) implements CubicSpline<C> {

        private final float value;

        public c(float f) {
            this.value = f;
        }

        @Override
        public float apply(C c0) {
            return this.value;
        }

        @Override
        public String parityString() {
            return String.format("k=%.3f", this.value);
        }

        public float value() {
            return this.value;
        }
    }

    public static final class b<C> {

        private final ToFloatFunction<C> coordinate;
        private final ToFloatFunction<Float> valueTransformer;
        private final FloatList locations;
        private final List<CubicSpline<C>> values;
        private final FloatList derivatives;

        protected b(ToFloatFunction<C> tofloatfunction) {
            this(tofloatfunction, (ofloat) -> {
                return ofloat;
            });
        }

        protected b(ToFloatFunction<C> tofloatfunction, ToFloatFunction<Float> tofloatfunction1) {
            this.locations = new FloatArrayList();
            this.values = Lists.newArrayList();
            this.derivatives = new FloatArrayList();
            this.coordinate = tofloatfunction;
            this.valueTransformer = tofloatfunction1;
        }

        public CubicSpline.b<C> addPoint(float f, float f1, float f2) {
            return this.addPoint(f, new CubicSpline.c<>(this.valueTransformer.apply(f1)), f2);
        }

        public CubicSpline.b<C> addPoint(float f, CubicSpline<C> cubicspline, float f1) {
            if (!this.locations.isEmpty() && f <= this.locations.getFloat(this.locations.size() - 1)) {
                throw new IllegalArgumentException("Please register points in ascending order");
            } else {
                this.locations.add(f);
                this.values.add(cubicspline);
                this.derivatives.add(f1);
                return this;
            }
        }

        public CubicSpline<C> build() {
            if (this.locations.isEmpty()) {
                throw new IllegalStateException("No elements added");
            } else {
                return new CubicSpline.d<>(this.coordinate, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
            }
        }
    }

    @VisibleForDebug
    public static record d<C> (ToFloatFunction<C> a, float[] b, List<CubicSpline<C>> c, float[] d) implements CubicSpline<C> {

        private final ToFloatFunction<C> coordinate;
        final float[] locations;
        private final List<CubicSpline<C>> values;
        private final float[] derivatives;

        public d(ToFloatFunction<C> tofloatfunction, float[] afloat, List<CubicSpline<C>> list, float[] afloat1) {
            if (afloat.length == list.size() && afloat.length == afloat1.length) {
                this.coordinate = tofloatfunction;
                this.locations = afloat;
                this.values = list;
                this.derivatives = afloat1;
            } else {
                throw new IllegalArgumentException("All lengths must be equal, got: " + afloat.length + " " + list.size() + " " + afloat1.length);
            }
        }

        @Override
        public float apply(C c0) {
            float f = this.coordinate.apply(c0);
            int i = MathHelper.binarySearch(0, this.locations.length, (j) -> {
                return f < this.locations[j];
            }) - 1;
            int j = this.locations.length - 1;

            if (i < 0) {
                return ((CubicSpline) this.values.get(0)).apply(c0) + this.derivatives[0] * (f - this.locations[0]);
            } else if (i == j) {
                return ((CubicSpline) this.values.get(j)).apply(c0) + this.derivatives[j] * (f - this.locations[j]);
            } else {
                float f1 = this.locations[i];
                float f2 = this.locations[i + 1];
                float f3 = (f - f1) / (f2 - f1);
                ToFloatFunction<C> tofloatfunction = (ToFloatFunction) this.values.get(i);
                ToFloatFunction<C> tofloatfunction1 = (ToFloatFunction) this.values.get(i + 1);
                float f4 = this.derivatives[i];
                float f5 = this.derivatives[i + 1];
                float f6 = tofloatfunction.apply(c0);
                float f7 = tofloatfunction1.apply(c0);
                float f8 = f4 * (f2 - f1) - (f7 - f6);
                float f9 = -f5 * (f2 - f1) + (f7 - f6);
                float f10 = MathHelper.lerp(f3, f6, f7) + f3 * (1.0F - f3) * MathHelper.lerp(f3, f8, f9);

                return f10;
            }
        }

        @VisibleForTesting
        @Override
        public String parityString() {
            return "Spline{coordinate=" + this.coordinate + ", locations=" + this.toString(this.locations) + ", derivatives=" + this.toString(this.derivatives) + ", values=" + (String) this.values.stream().map(CubicSpline::parityString).collect(Collectors.joining(", ", "[", "]")) + "}";
        }

        private String toString(float[] afloat) {
            Stream stream = IntStream.range(0, afloat.length).mapToDouble((i) -> {
                return (double) afloat[i];
            }).mapToObj((d0) -> {
                return String.format(Locale.ROOT, "%.3f", d0);
            });

            return "[" + (String) stream.collect(Collectors.joining(", ")) + "]";
        }

        public ToFloatFunction<C> coordinate() {
            return this.coordinate;
        }

        public float[] locations() {
            return this.locations;
        }

        public List<CubicSpline<C>> values() {
            return this.values;
        }

        public float[] derivatives() {
            return this.derivatives;
        }
    }
}

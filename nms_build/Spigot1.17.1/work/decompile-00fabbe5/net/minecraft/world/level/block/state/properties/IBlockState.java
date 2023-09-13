package net.minecraft.world.level.block.state.properties;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.IBlockDataHolder;

public abstract class IBlockState<T extends Comparable<T>> {

    private final Class<T> clazz;
    private final String name;
    private Integer hashCode;
    private final Codec<T> codec;
    private final Codec<IBlockState.a<T>> valueCodec;

    protected IBlockState(String s, Class<T> oclass) {
        this.codec = Codec.STRING.comapFlatMap((s1) -> {
            return (DataResult) this.b(s1).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unable to read property: " + this + " with value: " + s1);
            });
        }, this::a);
        this.valueCodec = this.codec.xmap(this::b, IBlockState.a::b);
        this.clazz = oclass;
        this.name = s;
    }

    public IBlockState.a<T> b(T t0) {
        return new IBlockState.a<>(this, t0);
    }

    public IBlockState.a<T> a(IBlockDataHolder<?, ?> iblockdataholder) {
        return new IBlockState.a<>(this, iblockdataholder.get(this));
    }

    public Stream<IBlockState.a<T>> c() {
        return this.getValues().stream().map(this::b);
    }

    public Codec<T> d() {
        return this.codec;
    }

    public Codec<IBlockState.a<T>> e() {
        return this.valueCodec;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getType() {
        return this.clazz;
    }

    public abstract Collection<T> getValues();

    public abstract String a(T t0);

    public abstract Optional<T> b(String s);

    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.clazz).add("values", this.getValues()).toString();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof IBlockState)) {
            return false;
        } else {
            IBlockState<?> iblockstate = (IBlockState) object;

            return this.clazz.equals(iblockstate.clazz) && this.name.equals(iblockstate.name);
        }
    }

    public final int hashCode() {
        if (this.hashCode == null) {
            this.hashCode = this.b();
        }

        return this.hashCode;
    }

    public int b() {
        return 31 * this.clazz.hashCode() + this.name.hashCode();
    }

    public <U, S extends IBlockDataHolder<?, S>> DataResult<S> a(DynamicOps<U> dynamicops, S s0, U u0) {
        DataResult<T> dataresult = this.codec.parse(dynamicops, u0);

        return dataresult.map((comparable) -> {
            return (IBlockDataHolder) s0.set(this, comparable);
        }).setPartial(s0);
    }

    public static final class a<T extends Comparable<T>> {

        private final IBlockState<T> property;
        private final T value;

        a(IBlockState<T> iblockstate, T t0) {
            if (!iblockstate.getValues().contains(t0)) {
                throw new IllegalArgumentException("Value " + t0 + " does not belong to property " + iblockstate);
            } else {
                this.property = iblockstate;
                this.value = t0;
            }
        }

        public IBlockState<T> a() {
            return this.property;
        }

        public T b() {
            return this.value;
        }

        public String toString() {
            String s = this.property.getName();

            return s + "=" + this.property.a(this.value);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (!(object instanceof IBlockState.a)) {
                return false;
            } else {
                IBlockState.a<?> iblockstate_a = (IBlockState.a) object;

                return this.property == iblockstate_a.property && this.value.equals(iblockstate_a.value);
            }
        }

        public int hashCode() {
            int i = this.property.hashCode();

            i = 31 * i + this.value.hashCode();
            return i;
        }
    }
}

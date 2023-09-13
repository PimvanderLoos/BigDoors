package net.minecraft.world.level.block.state.properties;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.IBlockDataHolder;

public abstract class IBlockState<T extends Comparable<T>> {

    private final Class<T> clazz;
    private final String name;
    @Nullable
    private Integer hashCode;
    private final Codec<T> codec;
    private final Codec<IBlockState.a<T>> valueCodec;

    protected IBlockState(String s, Class<T> oclass) {
        this.codec = Codec.STRING.comapFlatMap((s1) -> {
            return (DataResult) this.getValue(s1).map(DataResult::success).orElseGet(() -> {
                return DataResult.error(() -> {
                    return "Unable to read property: " + this + " with value: " + s1;
                });
            });
        }, this::getName);
        this.valueCodec = this.codec.xmap(this::value, IBlockState.a::value);
        this.clazz = oclass;
        this.name = s;
    }

    public IBlockState.a<T> value(T t0) {
        return new IBlockState.a<>(this, t0);
    }

    public IBlockState.a<T> value(IBlockDataHolder<?, ?> iblockdataholder) {
        return new IBlockState.a<>(this, iblockdataholder.getValue(this));
    }

    public Stream<IBlockState.a<T>> getAllValues() {
        return this.getPossibleValues().stream().map(this::value);
    }

    public Codec<T> codec() {
        return this.codec;
    }

    public Codec<IBlockState.a<T>> valueCodec() {
        return this.valueCodec;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getValueClass() {
        return this.clazz;
    }

    public abstract Collection<T> getPossibleValues();

    public abstract String getName(T t0);

    public abstract Optional<T> getValue(String s);

    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.clazz).add("values", this.getPossibleValues()).toString();
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
            this.hashCode = this.generateHashCode();
        }

        return this.hashCode;
    }

    public int generateHashCode() {
        return 31 * this.clazz.hashCode() + this.name.hashCode();
    }

    public <U, S extends IBlockDataHolder<?, S>> DataResult<S> parseValue(DynamicOps<U> dynamicops, S s0, U u0) {
        DataResult<T> dataresult = this.codec.parse(dynamicops, u0);

        return dataresult.map((comparable) -> {
            return (IBlockDataHolder) s0.setValue(this, comparable);
        }).setPartial(s0);
    }

    public static record a<T extends Comparable<T>> (IBlockState<T> property, T value) {

        public a(IBlockState<T> iblockstate, T t0) {
            if (!iblockstate.getPossibleValues().contains(t0)) {
                throw new IllegalArgumentException("Value " + t0 + " does not belong to property " + iblockstate);
            } else {
                this.property = iblockstate;
                this.value = t0;
            }
        }

        public String toString() {
            String s = this.property.getName();

            return s + "=" + this.property.getName(this.value);
        }
    }
}

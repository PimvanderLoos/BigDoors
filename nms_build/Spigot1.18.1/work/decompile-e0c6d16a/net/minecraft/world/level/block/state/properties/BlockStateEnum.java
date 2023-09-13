package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.INamable;

public class BlockStateEnum<T extends Enum<T> & INamable> extends IBlockState<T> {

    private final ImmutableSet<T> values;
    private final Map<String, T> names = Maps.newHashMap();

    protected BlockStateEnum(String s, Class<T> oclass, Collection<T> collection) {
        super(s, oclass);
        this.values = ImmutableSet.copyOf(collection);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            T t0 = (Enum) iterator.next();
            String s1 = ((INamable) t0).getSerializedName();

            if (this.names.containsKey(s1)) {
                throw new IllegalArgumentException("Multiple values have the same name '" + s1 + "'");
            }

            this.names.put(s1, t0);
        }

    }

    @Override
    public Collection<T> getPossibleValues() {
        return this.values;
    }

    @Override
    public Optional<T> getValue(String s) {
        return Optional.ofNullable((Enum) this.names.get(s));
    }

    public String getName(T t0) {
        return ((INamable) t0).getSerializedName();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof BlockStateEnum && super.equals(object)) {
            BlockStateEnum<?> blockstateenum = (BlockStateEnum) object;

            return this.values.equals(blockstateenum.values) && this.names.equals(blockstateenum.names);
        } else {
            return false;
        }
    }

    @Override
    public int generateHashCode() {
        int i = super.generateHashCode();

        i = 31 * i + this.values.hashCode();
        i = 31 * i + this.names.hashCode();
        return i;
    }

    public static <T extends Enum<T> & INamable> BlockStateEnum<T> create(String s, Class<T> oclass) {
        return create(s, oclass, (oenum) -> {
            return true;
        });
    }

    public static <T extends Enum<T> & INamable> BlockStateEnum<T> create(String s, Class<T> oclass, Predicate<T> predicate) {
        return create(s, oclass, (Collection) Arrays.stream((Enum[]) oclass.getEnumConstants()).filter(predicate).collect(Collectors.toList()));
    }

    public static <T extends Enum<T> & INamable> BlockStateEnum<T> create(String s, Class<T> oclass, T... at) {
        return create(s, oclass, (Collection) Lists.newArrayList(at));
    }

    public static <T extends Enum<T> & INamable> BlockStateEnum<T> create(String s, Class<T> oclass, Collection<T> collection) {
        return new BlockStateEnum<>(s, oclass, collection);
    }
}

package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.EnumDirection;

public class BlockStateDirection extends BlockStateEnum<EnumDirection> {

    protected BlockStateDirection(String s, Collection<EnumDirection> collection) {
        super(s, EnumDirection.class, collection);
    }

    public static BlockStateDirection create(String s) {
        return create(s, (enumdirection) -> {
            return true;
        });
    }

    public static BlockStateDirection create(String s, Predicate<EnumDirection> predicate) {
        return create(s, (Collection) Arrays.stream(EnumDirection.values()).filter(predicate).collect(Collectors.toList()));
    }

    public static BlockStateDirection create(String s, EnumDirection... aenumdirection) {
        return create(s, (Collection) Lists.newArrayList(aenumdirection));
    }

    public static BlockStateDirection create(String s, Collection<EnumDirection> collection) {
        return new BlockStateDirection(s, collection);
    }
}

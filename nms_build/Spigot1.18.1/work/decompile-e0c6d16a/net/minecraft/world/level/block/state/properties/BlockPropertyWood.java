package net.minecraft.world.level.block.state.properties;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;

public class BlockPropertyWood {

    private static final Set<BlockPropertyWood> VALUES = new ObjectArraySet();
    public static final BlockPropertyWood OAK = register(new BlockPropertyWood("oak"));
    public static final BlockPropertyWood SPRUCE = register(new BlockPropertyWood("spruce"));
    public static final BlockPropertyWood BIRCH = register(new BlockPropertyWood("birch"));
    public static final BlockPropertyWood ACACIA = register(new BlockPropertyWood("acacia"));
    public static final BlockPropertyWood JUNGLE = register(new BlockPropertyWood("jungle"));
    public static final BlockPropertyWood DARK_OAK = register(new BlockPropertyWood("dark_oak"));
    public static final BlockPropertyWood CRIMSON = register(new BlockPropertyWood("crimson"));
    public static final BlockPropertyWood WARPED = register(new BlockPropertyWood("warped"));
    private final String name;

    protected BlockPropertyWood(String s) {
        this.name = s;
    }

    private static BlockPropertyWood register(BlockPropertyWood blockpropertywood) {
        BlockPropertyWood.VALUES.add(blockpropertywood);
        return blockpropertywood;
    }

    public static Stream<BlockPropertyWood> values() {
        return BlockPropertyWood.VALUES.stream();
    }

    public String name() {
        return this.name;
    }
}

package net.minecraft.world.level.block.state.properties;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;

public class BlockPropertyWood {

    private static final Set<BlockPropertyWood> VALUES = new ObjectArraySet();
    public static final BlockPropertyWood OAK = a(new BlockPropertyWood("oak"));
    public static final BlockPropertyWood SPRUCE = a(new BlockPropertyWood("spruce"));
    public static final BlockPropertyWood BIRCH = a(new BlockPropertyWood("birch"));
    public static final BlockPropertyWood ACACIA = a(new BlockPropertyWood("acacia"));
    public static final BlockPropertyWood JUNGLE = a(new BlockPropertyWood("jungle"));
    public static final BlockPropertyWood DARK_OAK = a(new BlockPropertyWood("dark_oak"));
    public static final BlockPropertyWood CRIMSON = a(new BlockPropertyWood("crimson"));
    public static final BlockPropertyWood WARPED = a(new BlockPropertyWood("warped"));
    private final String name;

    protected BlockPropertyWood(String s) {
        this.name = s;
    }

    private static BlockPropertyWood a(BlockPropertyWood blockpropertywood) {
        BlockPropertyWood.VALUES.add(blockpropertywood);
        return blockpropertywood;
    }

    public static Stream<BlockPropertyWood> a() {
        return BlockPropertyWood.VALUES.stream();
    }

    public String b() {
        return this.name;
    }
}

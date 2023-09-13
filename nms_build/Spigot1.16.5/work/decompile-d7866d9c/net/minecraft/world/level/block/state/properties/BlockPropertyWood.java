package net.minecraft.world.level.block.state.properties;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;

public class BlockPropertyWood {

    private static final Set<BlockPropertyWood> i = new ObjectArraySet();
    public static final BlockPropertyWood a = a(new BlockPropertyWood("oak"));
    public static final BlockPropertyWood b = a(new BlockPropertyWood("spruce"));
    public static final BlockPropertyWood c = a(new BlockPropertyWood("birch"));
    public static final BlockPropertyWood d = a(new BlockPropertyWood("acacia"));
    public static final BlockPropertyWood e = a(new BlockPropertyWood("jungle"));
    public static final BlockPropertyWood f = a(new BlockPropertyWood("dark_oak"));
    public static final BlockPropertyWood g = a(new BlockPropertyWood("crimson"));
    public static final BlockPropertyWood h = a(new BlockPropertyWood("warped"));
    private final String j;

    protected BlockPropertyWood(String s) {
        this.j = s;
    }

    private static BlockPropertyWood a(BlockPropertyWood blockpropertywood) {
        BlockPropertyWood.i.add(blockpropertywood);
        return blockpropertywood;
    }
}

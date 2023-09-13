package net.minecraft.server;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;

public class BlockPropertyWood {

    private static final Set<BlockPropertyWood> g = new ObjectArraySet();
    public static final BlockPropertyWood a = a(new BlockPropertyWood("oak"));
    public static final BlockPropertyWood b = a(new BlockPropertyWood("spruce"));
    public static final BlockPropertyWood c = a(new BlockPropertyWood("birch"));
    public static final BlockPropertyWood d = a(new BlockPropertyWood("acacia"));
    public static final BlockPropertyWood e = a(new BlockPropertyWood("jungle"));
    public static final BlockPropertyWood f = a(new BlockPropertyWood("dark_oak"));
    private final String h;

    protected BlockPropertyWood(String s) {
        this.h = s;
    }

    private static BlockPropertyWood a(BlockPropertyWood blockpropertywood) {
        BlockPropertyWood.g.add(blockpropertywood);
        return blockpropertywood;
    }
}

package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class WorldGenBlockPlacerColumn extends WorldGenBlockPlacer {

    private final int b;
    private final int c;

    public WorldGenBlockPlacerColumn(int i, int j) {
        super(WorldGenBlockPlacers.c);
        this.b = i;
        this.c = j;
    }

    public <T> WorldGenBlockPlacerColumn(Dynamic<T> dynamic) {
        this(dynamic.get("min_size").asInt(1), dynamic.get("extra_size").asInt(2));
    }

    @Override
    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(blockposition);
        int i = this.b + random.nextInt(random.nextInt(this.c + 1) + 1);

        for (int j = 0; j < i; ++j) {
            generatoraccess.setTypeAndData(blockposition_mutableblockposition, iblockdata, 2);
            blockposition_mutableblockposition.c(EnumDirection.UP);
        }

    }

    @Override
    public <T> T a(DynamicOps<T> dynamicops) {
        return (new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("type"), dynamicops.createString(IRegistry.u.getKey(this.a).toString()), dynamicops.createString("min_size"), dynamicops.createInt(this.b), dynamicops.createString("extra_size"), dynamicops.createInt(this.c))))).getValue();
    }
}

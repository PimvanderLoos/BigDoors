package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class WorldGenBlockPlacerSimple extends WorldGenBlockPlacer {

    public WorldGenBlockPlacerSimple() {
        super(WorldGenBlockPlacers.a);
    }

    public <T> WorldGenBlockPlacerSimple(Dynamic<T> dynamic) {
        this();
    }

    @Override
    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        generatoraccess.setTypeAndData(blockposition, iblockdata, 2);
    }

    @Override
    public <T> T a(DynamicOps<T> dynamicops) {
        return (new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("type"), dynamicops.createString(IRegistry.u.getKey(this.a).toString()))))).getValue();
    }
}

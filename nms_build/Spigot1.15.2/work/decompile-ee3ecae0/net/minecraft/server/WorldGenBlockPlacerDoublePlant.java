package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class WorldGenBlockPlacerDoublePlant extends WorldGenBlockPlacer {

    public WorldGenBlockPlacerDoublePlant() {
        super(WorldGenBlockPlacers.b);
    }

    public <T> WorldGenBlockPlacerDoublePlant(Dynamic<T> dynamic) {
        this();
    }

    @Override
    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        ((BlockTallPlant) iblockdata.getBlock()).a(generatoraccess, blockposition, 2);
    }

    @Override
    public <T> T a(DynamicOps<T> dynamicops) {
        return (new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("type"), dynamicops.createString(IRegistry.u.getKey(this.a).toString()))))).getValue();
    }
}

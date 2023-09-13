package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import java.util.stream.Stream;

public class WorldGenFeatureHellFlowingLavaConfiguration implements WorldGenFeatureConfiguration {

    public final Fluid a;
    public final boolean b;
    public final int c;
    public final int d;
    public final Set<Block> f;

    public WorldGenFeatureHellFlowingLavaConfiguration(Fluid fluid, boolean flag, int i, int j, Set<Block> set) {
        this.a = fluid;
        this.b = flag;
        this.c = i;
        this.d = j;
        this.f = set;
    }

    @Override
    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        Object object = dynamicops.createString("state");
        Object object1 = Fluid.a(dynamicops, this.a).getValue();
        Object object2 = dynamicops.createString("requires_block_below");
        Object object3 = dynamicops.createBoolean(this.b);
        Object object4 = dynamicops.createString("rock_count");
        Object object5 = dynamicops.createInt(this.c);
        Object object6 = dynamicops.createString("hole_count");
        Object object7 = dynamicops.createInt(this.d);
        Object object8 = dynamicops.createString("valid_blocks");
        Stream stream = this.f.stream();
        RegistryBlocks registryblocks = IRegistry.BLOCK;

        registryblocks.getClass();
        stream = stream.map(registryblocks::getKey).map(MinecraftKey::toString);
        dynamicops.getClass();
        return new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(object, object1, object2, object3, object4, object5, object6, object7, object8, dynamicops.createList(stream.map(dynamicops::createString)))));
    }

    public static <T> WorldGenFeatureHellFlowingLavaConfiguration a(Dynamic<T> dynamic) {
        return new WorldGenFeatureHellFlowingLavaConfiguration((Fluid) dynamic.get("state").map(Fluid::a).orElse(FluidTypes.EMPTY.h()), dynamic.get("requires_block_below").asBoolean(true), dynamic.get("rock_count").asInt(4), dynamic.get("hole_count").asInt(1), ImmutableSet.copyOf(dynamic.get("valid_blocks").asList((dynamic1) -> {
            return (Block) IRegistry.BLOCK.get(new MinecraftKey(dynamic1.asString("minecraft:air")));
        })));
    }
}

package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class WorldGenFeatureHellFlowingLavaConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureHellFlowingLavaConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Fluid.a.fieldOf("state").forGetter((worldgenfeaturehellflowinglavaconfiguration) -> {
            return worldgenfeaturehellflowinglavaconfiguration.b;
        }), Codec.BOOL.fieldOf("requires_block_below").orElse(true).forGetter((worldgenfeaturehellflowinglavaconfiguration) -> {
            return worldgenfeaturehellflowinglavaconfiguration.c;
        }), Codec.INT.fieldOf("rock_count").orElse(4).forGetter((worldgenfeaturehellflowinglavaconfiguration) -> {
            return worldgenfeaturehellflowinglavaconfiguration.d;
        }), Codec.INT.fieldOf("hole_count").orElse(1).forGetter((worldgenfeaturehellflowinglavaconfiguration) -> {
            return worldgenfeaturehellflowinglavaconfiguration.e;
        }), IRegistry.BLOCK.listOf().fieldOf("valid_blocks").xmap(ImmutableSet::copyOf, ImmutableList::copyOf).forGetter((worldgenfeaturehellflowinglavaconfiguration) -> {
            return worldgenfeaturehellflowinglavaconfiguration.f;
        })).apply(instance, WorldGenFeatureHellFlowingLavaConfiguration::new);
    });
    public final Fluid b;
    public final boolean c;
    public final int d;
    public final int e;
    public final Set<Block> f;

    public WorldGenFeatureHellFlowingLavaConfiguration(Fluid fluid, boolean flag, int i, int j, Set<Block> set) {
        this.b = fluid;
        this.c = flag;
        this.d = i;
        this.e = j;
        this.f = set;
    }
}

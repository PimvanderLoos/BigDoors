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

    public static final Codec<WorldGenFeatureHellFlowingLavaConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Fluid.CODEC.fieldOf("state").forGetter((worldgenfeaturehellflowinglavaconfiguration) -> {
            return worldgenfeaturehellflowinglavaconfiguration.state;
        }), Codec.BOOL.fieldOf("requires_block_below").orElse(true).forGetter((worldgenfeaturehellflowinglavaconfiguration) -> {
            return worldgenfeaturehellflowinglavaconfiguration.requiresBlockBelow;
        }), Codec.INT.fieldOf("rock_count").orElse(4).forGetter((worldgenfeaturehellflowinglavaconfiguration) -> {
            return worldgenfeaturehellflowinglavaconfiguration.rockCount;
        }), Codec.INT.fieldOf("hole_count").orElse(1).forGetter((worldgenfeaturehellflowinglavaconfiguration) -> {
            return worldgenfeaturehellflowinglavaconfiguration.holeCount;
        }), IRegistry.BLOCK.listOf().fieldOf("valid_blocks").xmap(ImmutableSet::copyOf, ImmutableList::copyOf).forGetter((worldgenfeaturehellflowinglavaconfiguration) -> {
            return worldgenfeaturehellflowinglavaconfiguration.validBlocks;
        })).apply(instance, WorldGenFeatureHellFlowingLavaConfiguration::new);
    });
    public final Fluid state;
    public final boolean requiresBlockBelow;
    public final int rockCount;
    public final int holeCount;
    public final Set<Block> validBlocks;

    public WorldGenFeatureHellFlowingLavaConfiguration(Fluid fluid, boolean flag, int i, int j, Set<Block> set) {
        this.state = fluid;
        this.requiresBlockBelow = flag;
        this.rockCount = i;
        this.holeCount = j;
        this.validBlocks = set;
    }
}

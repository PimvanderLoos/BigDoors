package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
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
        }), RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("valid_blocks").forGetter((worldgenfeaturehellflowinglavaconfiguration) -> {
            return worldgenfeaturehellflowinglavaconfiguration.validBlocks;
        })).apply(instance, WorldGenFeatureHellFlowingLavaConfiguration::new);
    });
    public final Fluid state;
    public final boolean requiresBlockBelow;
    public final int rockCount;
    public final int holeCount;
    public final HolderSet<Block> validBlocks;

    public WorldGenFeatureHellFlowingLavaConfiguration(Fluid fluid, boolean flag, int i, int j, HolderSet<Block> holderset) {
        this.state = fluid;
        this.requiresBlockBelow = flag;
        this.rockCount = i;
        this.holeCount = j;
        this.validBlocks = holderset;
    }
}

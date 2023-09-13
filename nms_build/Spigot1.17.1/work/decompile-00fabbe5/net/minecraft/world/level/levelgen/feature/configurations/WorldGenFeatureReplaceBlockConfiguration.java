package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureTestBlockState;

public class WorldGenFeatureReplaceBlockConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureReplaceBlockConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.list(WorldGenFeatureOreConfiguration.b.CODEC).fieldOf("targets").forGetter((worldgenfeaturereplaceblockconfiguration) -> {
            return worldgenfeaturereplaceblockconfiguration.targetStates;
        })).apply(instance, WorldGenFeatureReplaceBlockConfiguration::new);
    });
    public final List<WorldGenFeatureOreConfiguration.b> targetStates;

    public WorldGenFeatureReplaceBlockConfiguration(IBlockData iblockdata, IBlockData iblockdata1) {
        this(ImmutableList.of(WorldGenFeatureOreConfiguration.a(new DefinedStructureTestBlockState(iblockdata), iblockdata1)));
    }

    public WorldGenFeatureReplaceBlockConfiguration(List<WorldGenFeatureOreConfiguration.b> list) {
        this.targetStates = list;
    }
}

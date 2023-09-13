package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public class WorldGenFeatureBlockConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureBlockConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureStateProvider.CODEC.fieldOf("to_place").forGetter((worldgenfeatureblockconfiguration) -> {
            return worldgenfeatureblockconfiguration.toPlace;
        }), IBlockData.CODEC.listOf().fieldOf("place_on").orElse(ImmutableList.of()).forGetter((worldgenfeatureblockconfiguration) -> {
            return worldgenfeatureblockconfiguration.placeOn;
        }), IBlockData.CODEC.listOf().fieldOf("place_in").orElse(ImmutableList.of()).forGetter((worldgenfeatureblockconfiguration) -> {
            return worldgenfeatureblockconfiguration.placeIn;
        }), IBlockData.CODEC.listOf().fieldOf("place_under").orElse(ImmutableList.of()).forGetter((worldgenfeatureblockconfiguration) -> {
            return worldgenfeatureblockconfiguration.placeUnder;
        })).apply(instance, WorldGenFeatureBlockConfiguration::new);
    });
    public final WorldGenFeatureStateProvider toPlace;
    public final List<IBlockData> placeOn;
    public final List<IBlockData> placeIn;
    public final List<IBlockData> placeUnder;

    public WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider, List<IBlockData> list, List<IBlockData> list1, List<IBlockData> list2) {
        this.toPlace = worldgenfeaturestateprovider;
        this.placeOn = list;
        this.placeIn = list1;
        this.placeUnder = list2;
    }

    public WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider) {
        this(worldgenfeaturestateprovider, ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
    }
}

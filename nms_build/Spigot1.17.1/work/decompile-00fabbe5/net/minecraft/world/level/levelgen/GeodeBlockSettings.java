package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public class GeodeBlockSettings {

    public final WorldGenFeatureStateProvider fillingProvider;
    public final WorldGenFeatureStateProvider innerLayerProvider;
    public final WorldGenFeatureStateProvider alternateInnerLayerProvider;
    public final WorldGenFeatureStateProvider middleLayerProvider;
    public final WorldGenFeatureStateProvider outerLayerProvider;
    public final List<IBlockData> innerPlacements;
    public final MinecraftKey cannotReplace;
    public final MinecraftKey invalidBlocks;
    public static final Codec<GeodeBlockSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureStateProvider.CODEC.fieldOf("filling_provider").forGetter((geodeblocksettings) -> {
            return geodeblocksettings.fillingProvider;
        }), WorldGenFeatureStateProvider.CODEC.fieldOf("inner_layer_provider").forGetter((geodeblocksettings) -> {
            return geodeblocksettings.innerLayerProvider;
        }), WorldGenFeatureStateProvider.CODEC.fieldOf("alternate_inner_layer_provider").forGetter((geodeblocksettings) -> {
            return geodeblocksettings.alternateInnerLayerProvider;
        }), WorldGenFeatureStateProvider.CODEC.fieldOf("middle_layer_provider").forGetter((geodeblocksettings) -> {
            return geodeblocksettings.middleLayerProvider;
        }), WorldGenFeatureStateProvider.CODEC.fieldOf("outer_layer_provider").forGetter((geodeblocksettings) -> {
            return geodeblocksettings.outerLayerProvider;
        }), ExtraCodecs.a(IBlockData.CODEC.listOf()).fieldOf("inner_placements").forGetter((geodeblocksettings) -> {
            return geodeblocksettings.innerPlacements;
        }), MinecraftKey.CODEC.fieldOf("cannot_replace").forGetter((geodeblocksettings) -> {
            return geodeblocksettings.cannotReplace;
        }), MinecraftKey.CODEC.fieldOf("invalid_blocks").forGetter((geodeblocksettings) -> {
            return geodeblocksettings.invalidBlocks;
        })).apply(instance, GeodeBlockSettings::new);
    });

    public GeodeBlockSettings(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider1, WorldGenFeatureStateProvider worldgenfeaturestateprovider2, WorldGenFeatureStateProvider worldgenfeaturestateprovider3, WorldGenFeatureStateProvider worldgenfeaturestateprovider4, List<IBlockData> list, MinecraftKey minecraftkey, MinecraftKey minecraftkey1) {
        this.fillingProvider = worldgenfeaturestateprovider;
        this.innerLayerProvider = worldgenfeaturestateprovider1;
        this.alternateInnerLayerProvider = worldgenfeaturestateprovider2;
        this.middleLayerProvider = worldgenfeaturestateprovider3;
        this.outerLayerProvider = worldgenfeaturestateprovider4;
        this.innerPlacements = list;
        this.cannotReplace = minecraftkey;
        this.invalidBlocks = minecraftkey1;
    }
}

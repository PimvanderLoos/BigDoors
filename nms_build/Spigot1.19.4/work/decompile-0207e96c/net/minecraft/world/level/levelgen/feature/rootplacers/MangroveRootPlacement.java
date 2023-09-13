package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public record MangroveRootPlacement(HolderSet<Block> canGrowThrough, HolderSet<Block> muddyRootsIn, WorldGenFeatureStateProvider muddyRootsProvider, int maxRootWidth, int maxRootLength, float randomSkewChance) {

    public static final Codec<MangroveRootPlacement> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_grow_through").forGetter((mangroverootplacement) -> {
            return mangroverootplacement.canGrowThrough;
        }), RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("muddy_roots_in").forGetter((mangroverootplacement) -> {
            return mangroverootplacement.muddyRootsIn;
        }), WorldGenFeatureStateProvider.CODEC.fieldOf("muddy_roots_provider").forGetter((mangroverootplacement) -> {
            return mangroverootplacement.muddyRootsProvider;
        }), Codec.intRange(1, 12).fieldOf("max_root_width").forGetter((mangroverootplacement) -> {
            return mangroverootplacement.maxRootWidth;
        }), Codec.intRange(1, 64).fieldOf("max_root_length").forGetter((mangroverootplacement) -> {
            return mangroverootplacement.maxRootLength;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("random_skew_chance").forGetter((mangroverootplacement) -> {
            return mangroverootplacement.randomSkewChance;
        })).apply(instance, MangroveRootPlacement::new);
    });
}

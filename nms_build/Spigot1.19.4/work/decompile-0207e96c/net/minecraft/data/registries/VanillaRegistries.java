package net.minecraft.data.registries;

import java.util.List;
import net.minecraft.SystemUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.data.worldgen.NoiseData;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.StructureSets;
import net.minecraft.data.worldgen.Structures;
import net.minecraft.data.worldgen.WorldGenCarvers;
import net.minecraft.data.worldgen.WorldGenFeaturePieces;
import net.minecraft.data.worldgen.biome.BiomeData;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPresets;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public class VanillaRegistries {

    private static final RegistrySetBuilder BUILDER = (new RegistrySetBuilder()).add(Registries.DIMENSION_TYPE, DimensionTypes::bootstrap).add(Registries.CONFIGURED_CARVER, WorldGenCarvers::bootstrap).add(Registries.CONFIGURED_FEATURE, FeatureUtils::bootstrap).add(Registries.PLACED_FEATURE, PlacementUtils::bootstrap).add(Registries.STRUCTURE, Structures::bootstrap).add(Registries.STRUCTURE_SET, StructureSets::bootstrap).add(Registries.PROCESSOR_LIST, ProcessorLists::bootstrap).add(Registries.TEMPLATE_POOL, WorldGenFeaturePieces::bootstrap).add(Registries.BIOME, BiomeData::bootstrap).add(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterLists::bootstrap).add(Registries.NOISE, NoiseData::bootstrap).add(Registries.DENSITY_FUNCTION, NoiseRouterData::bootstrap).add(Registries.NOISE_SETTINGS, GeneratorSettingBase::bootstrap).add(Registries.WORLD_PRESET, WorldPresets::bootstrap).add(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPresets::bootstrap).add(Registries.CHAT_TYPE, ChatMessageType::bootstrap).add(Registries.TRIM_PATTERN, TrimPatterns::bootstrap).add(Registries.TRIM_MATERIAL, TrimMaterials::bootstrap).add(Registries.DAMAGE_TYPE, DamageTypes::bootstrap);

    public VanillaRegistries() {}

    private static void validateThatAllBiomeFeaturesHaveBiomeFilter(HolderLookup.b holderlookup_b) {
        validateThatAllBiomeFeaturesHaveBiomeFilter(holderlookup_b.lookupOrThrow(Registries.PLACED_FEATURE), holderlookup_b.lookupOrThrow(Registries.BIOME));
    }

    public static void validateThatAllBiomeFeaturesHaveBiomeFilter(HolderGetter<PlacedFeature> holdergetter, HolderLookup<BiomeBase> holderlookup) {
        holderlookup.listElements().forEach((holder_c) -> {
            MinecraftKey minecraftkey = holder_c.key().location();
            List<HolderSet<PlacedFeature>> list = ((BiomeBase) holder_c.value()).getGenerationSettings().features();

            list.stream().flatMap(HolderSet::stream).forEach((holder) -> {
                holder.unwrap().ifLeft((resourcekey) -> {
                    Holder.c<PlacedFeature> holder_c1 = holdergetter.getOrThrow(resourcekey);

                    if (!validatePlacedFeature((PlacedFeature) holder_c1.value())) {
                        MinecraftKey minecraftkey1 = resourcekey.location();

                        SystemUtils.logAndPauseIfInIde("Placed feature " + minecraftkey1 + " in biome " + minecraftkey + " is missing BiomeFilter.biome()");
                    }

                }).ifRight((placedfeature) -> {
                    if (!validatePlacedFeature(placedfeature)) {
                        SystemUtils.logAndPauseIfInIde("Placed inline feature in biome " + holder_c + " is missing BiomeFilter.biome()");
                    }

                });
            });
        });
    }

    private static boolean validatePlacedFeature(PlacedFeature placedfeature) {
        return placedfeature.placement().contains(BiomeFilter.biome());
    }

    public static HolderLookup.b createLookup() {
        IRegistryCustom.Dimension iregistrycustom_dimension = IRegistryCustom.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        HolderLookup.b holderlookup_b = VanillaRegistries.BUILDER.build(iregistrycustom_dimension);

        validateThatAllBiomeFeaturesHaveBiomeFilter(holderlookup_b);
        return holderlookup_b;
    }
}

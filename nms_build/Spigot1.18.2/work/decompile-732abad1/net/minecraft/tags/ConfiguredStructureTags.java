package net.minecraft.tags;

import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public interface ConfiguredStructureTags {

    TagKey<StructureFeature<?, ?>> EYE_OF_ENDER_LOCATED = create("eye_of_ender_located");
    TagKey<StructureFeature<?, ?>> DOLPHIN_LOCATED = create("dolphin_located");
    TagKey<StructureFeature<?, ?>> ON_WOODLAND_EXPLORER_MAPS = create("on_woodland_explorer_maps");
    TagKey<StructureFeature<?, ?>> ON_OCEAN_EXPLORER_MAPS = create("on_ocean_explorer_maps");
    TagKey<StructureFeature<?, ?>> ON_TREASURE_MAPS = create("on_treasure_maps");
    TagKey<StructureFeature<?, ?>> VILLAGE = create("village");
    TagKey<StructureFeature<?, ?>> MINESHAFT = create("mineshaft");
    TagKey<StructureFeature<?, ?>> SHIPWRECK = create("shipwreck");
    TagKey<StructureFeature<?, ?>> RUINED_PORTAL = create("ruined_portal");
    TagKey<StructureFeature<?, ?>> OCEAN_RUIN = create("ocean_ruin");

    private static TagKey<StructureFeature<?, ?>> create(String s) {
        return TagKey.create(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new MinecraftKey(s));
    }
}

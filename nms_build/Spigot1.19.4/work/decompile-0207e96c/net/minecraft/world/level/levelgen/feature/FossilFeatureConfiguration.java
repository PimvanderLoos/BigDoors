package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureStructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;

public class FossilFeatureConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<FossilFeatureConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(MinecraftKey.CODEC.listOf().fieldOf("fossil_structures").forGetter((fossilfeatureconfiguration) -> {
            return fossilfeatureconfiguration.fossilStructures;
        }), MinecraftKey.CODEC.listOf().fieldOf("overlay_structures").forGetter((fossilfeatureconfiguration) -> {
            return fossilfeatureconfiguration.overlayStructures;
        }), DefinedStructureStructureProcessorType.LIST_CODEC.fieldOf("fossil_processors").forGetter((fossilfeatureconfiguration) -> {
            return fossilfeatureconfiguration.fossilProcessors;
        }), DefinedStructureStructureProcessorType.LIST_CODEC.fieldOf("overlay_processors").forGetter((fossilfeatureconfiguration) -> {
            return fossilfeatureconfiguration.overlayProcessors;
        }), Codec.intRange(0, 7).fieldOf("max_empty_corners_allowed").forGetter((fossilfeatureconfiguration) -> {
            return fossilfeatureconfiguration.maxEmptyCornersAllowed;
        })).apply(instance, FossilFeatureConfiguration::new);
    });
    public final List<MinecraftKey> fossilStructures;
    public final List<MinecraftKey> overlayStructures;
    public final Holder<ProcessorList> fossilProcessors;
    public final Holder<ProcessorList> overlayProcessors;
    public final int maxEmptyCornersAllowed;

    public FossilFeatureConfiguration(List<MinecraftKey> list, List<MinecraftKey> list1, Holder<ProcessorList> holder, Holder<ProcessorList> holder1, int i) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Fossil structure lists need at least one entry");
        } else if (list.size() != list1.size()) {
            throw new IllegalArgumentException("Fossil structure lists must be equal lengths");
        } else {
            this.fossilStructures = list;
            this.overlayStructures = list1;
            this.fossilProcessors = holder;
            this.overlayProcessors = holder1;
            this.maxEmptyCornersAllowed = i;
        }
    }
}

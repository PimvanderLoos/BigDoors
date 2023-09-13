package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public record StructureSet(List<StructureSet.a> c, StructurePlacement d) {

    private final List<StructureSet.a> structures;
    private final StructurePlacement placement;
    public static final Codec<StructureSet> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(StructureSet.a.CODEC.listOf().fieldOf("structures").forGetter(StructureSet::structures), StructurePlacement.CODEC.fieldOf("placement").forGetter(StructureSet::placement)).apply(instance, StructureSet::new);
    });
    public static final Codec<Holder<StructureSet>> CODEC = RegistryFileCodec.create(IRegistry.STRUCTURE_SET_REGISTRY, StructureSet.DIRECT_CODEC);

    public StructureSet(Holder<StructureFeature<?, ?>> holder, StructurePlacement structureplacement) {
        this(List.of(new StructureSet.a(holder, 1)), structureplacement);
    }

    public StructureSet(List<StructureSet.a> list, StructurePlacement structureplacement) {
        this.structures = list;
        this.placement = structureplacement;
    }

    public static StructureSet.a entry(Holder<StructureFeature<?, ?>> holder, int i) {
        return new StructureSet.a(holder, i);
    }

    public static StructureSet.a entry(Holder<StructureFeature<?, ?>> holder) {
        return new StructureSet.a(holder, 1);
    }

    public List<StructureSet.a> structures() {
        return this.structures;
    }

    public StructurePlacement placement() {
        return this.placement;
    }

    public static record a(Holder<StructureFeature<?, ?>> b, int c) {

        private final Holder<StructureFeature<?, ?>> structure;
        private final int weight;
        public static final Codec<StructureSet.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(StructureFeature.CODEC.fieldOf("structure").forGetter(StructureSet.a::structure), ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(StructureSet.a::weight)).apply(instance, StructureSet.a::new);
        });

        public a(Holder<StructureFeature<?, ?>> holder, int i) {
            this.structure = holder;
            this.weight = i;
        }

        public boolean generatesInMatchingBiome(Predicate<Holder<BiomeBase>> predicate) {
            HolderSet<BiomeBase> holderset = ((StructureFeature) this.structure().value()).biomes();

            return holderset.stream().anyMatch(predicate);
        }

        public Holder<StructureFeature<?, ?>> structure() {
            return this.structure;
        }

        public int weight() {
            return this.weight;
        }
    }
}

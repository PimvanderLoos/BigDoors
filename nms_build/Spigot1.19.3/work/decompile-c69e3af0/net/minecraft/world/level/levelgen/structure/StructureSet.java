package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public record StructureSet(List<StructureSet.a> structures, StructurePlacement placement) {

    public static final Codec<StructureSet> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(StructureSet.a.CODEC.listOf().fieldOf("structures").forGetter(StructureSet::structures), StructurePlacement.CODEC.fieldOf("placement").forGetter(StructureSet::placement)).apply(instance, StructureSet::new);
    });
    public static final Codec<Holder<StructureSet>> CODEC = RegistryFileCodec.create(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC);

    public StructureSet(Holder<Structure> holder, StructurePlacement structureplacement) {
        this(List.of(new StructureSet.a(holder, 1)), structureplacement);
    }

    public static StructureSet.a entry(Holder<Structure> holder, int i) {
        return new StructureSet.a(holder, i);
    }

    public static StructureSet.a entry(Holder<Structure> holder) {
        return new StructureSet.a(holder, 1);
    }

    public static record a(Holder<Structure> structure, int weight) {

        public static final Codec<StructureSet.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Structure.CODEC.fieldOf("structure").forGetter(StructureSet.a::structure), ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(StructureSet.a::weight)).apply(instance, StructureSet.a::new);
        });
    }
}

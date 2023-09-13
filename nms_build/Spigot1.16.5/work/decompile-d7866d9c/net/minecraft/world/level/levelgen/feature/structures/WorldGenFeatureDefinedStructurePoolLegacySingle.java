package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;

public class WorldGenFeatureDefinedStructurePoolLegacySingle extends WorldGenFeatureDefinedStructurePoolSingle {

    public static final Codec<WorldGenFeatureDefinedStructurePoolLegacySingle> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(c(), b(), d()).apply(instance, WorldGenFeatureDefinedStructurePoolLegacySingle::new);
    });

    protected WorldGenFeatureDefinedStructurePoolLegacySingle(Either<MinecraftKey, DefinedStructure> either, Supplier<ProcessorList> supplier, WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        super(either, supplier, worldgenfeaturedefinedstructurepooltemplate_matching);
    }

    @Override
    protected DefinedStructureInfo a(EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox, boolean flag) {
        DefinedStructureInfo definedstructureinfo = super.a(enumblockrotation, structureboundingbox, flag);

        definedstructureinfo.b((DefinedStructureProcessor) DefinedStructureProcessorBlockIgnore.b);
        definedstructureinfo.a((DefinedStructureProcessor) DefinedStructureProcessorBlockIgnore.d);
        return definedstructureinfo;
    }

    @Override
    public WorldGenFeatureDefinedStructurePools<?> a() {
        return WorldGenFeatureDefinedStructurePools.e;
    }

    @Override
    public String toString() {
        return "LegacySingle[" + this.c + "]";
    }
}

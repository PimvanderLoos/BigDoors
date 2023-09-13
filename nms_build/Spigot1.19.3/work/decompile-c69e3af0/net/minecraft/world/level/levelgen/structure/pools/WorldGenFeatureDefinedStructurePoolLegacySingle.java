package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;

public class WorldGenFeatureDefinedStructurePoolLegacySingle extends WorldGenFeatureDefinedStructurePoolSingle {

    public static final Codec<WorldGenFeatureDefinedStructurePoolLegacySingle> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(templateCodec(), processorsCodec(), projectionCodec()).apply(instance, WorldGenFeatureDefinedStructurePoolLegacySingle::new);
    });

    protected WorldGenFeatureDefinedStructurePoolLegacySingle(Either<MinecraftKey, DefinedStructure> either, Holder<ProcessorList> holder, WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        super(either, holder, worldgenfeaturedefinedstructurepooltemplate_matching);
    }

    @Override
    protected DefinedStructureInfo getSettings(EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox, boolean flag) {
        DefinedStructureInfo definedstructureinfo = super.getSettings(enumblockrotation, structureboundingbox, flag);

        definedstructureinfo.popProcessor(DefinedStructureProcessorBlockIgnore.STRUCTURE_BLOCK);
        definedstructureinfo.addProcessor(DefinedStructureProcessorBlockIgnore.STRUCTURE_AND_AIR);
        return definedstructureinfo;
    }

    @Override
    public WorldGenFeatureDefinedStructurePools<?> getType() {
        return WorldGenFeatureDefinedStructurePools.LEGACY;
    }

    @Override
    public String toString() {
        return "LegacySingle[" + this.template + "]";
    }
}

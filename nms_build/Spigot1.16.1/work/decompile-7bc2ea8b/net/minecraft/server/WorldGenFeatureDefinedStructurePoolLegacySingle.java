package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class WorldGenFeatureDefinedStructurePoolLegacySingle extends WorldGenFeatureDefinedStructurePoolSingle {

    public static final Codec<WorldGenFeatureDefinedStructurePoolLegacySingle> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(c(), b(), d()).apply(instance, WorldGenFeatureDefinedStructurePoolLegacySingle::new);
    });

    @Deprecated
    public WorldGenFeatureDefinedStructurePoolLegacySingle(String s, List<DefinedStructureProcessor> list) {
        super(s, list);
    }

    private WorldGenFeatureDefinedStructurePoolLegacySingle(Either<MinecraftKey, DefinedStructure> either, List<DefinedStructureProcessor> list, WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        super(either, list, worldgenfeaturedefinedstructurepooltemplate_matching);
    }

    @Deprecated
    public WorldGenFeatureDefinedStructurePoolLegacySingle(String s) {
        super(s, ImmutableList.of());
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

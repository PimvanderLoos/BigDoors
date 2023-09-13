package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public abstract class WorldGenFeatureDefinedStructurePoolStructure {

    public static final Codec<WorldGenFeatureDefinedStructurePoolStructure> e = IRegistry.STRUCTURE_POOL_ELEMENT.dispatch("element_type", WorldGenFeatureDefinedStructurePoolStructure::a, WorldGenFeatureDefinedStructurePools::codec);
    @Nullable
    private volatile WorldGenFeatureDefinedStructurePoolTemplate.Matching a;

    protected static <E extends WorldGenFeatureDefinedStructurePoolStructure> RecordCodecBuilder<E, WorldGenFeatureDefinedStructurePoolTemplate.Matching> d() {
        return WorldGenFeatureDefinedStructurePoolTemplate.Matching.c.fieldOf("projection").forGetter(WorldGenFeatureDefinedStructurePoolStructure::e);
    }

    protected WorldGenFeatureDefinedStructurePoolStructure(WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        this.a = worldgenfeaturedefinedstructurepooltemplate_matching;
    }

    public abstract List<DefinedStructure.BlockInfo> a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, Random random);

    public abstract StructureBoundingBox a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation);

    public abstract boolean a(DefinedStructureManager definedstructuremanager, GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox, Random random, boolean flag);

    public abstract WorldGenFeatureDefinedStructurePools<?> a();

    public void a(GeneratorAccess generatoraccess, DefinedStructure.BlockInfo definedstructure_blockinfo, BlockPosition blockposition, EnumBlockRotation enumblockrotation, Random random, StructureBoundingBox structureboundingbox) {}

    public WorldGenFeatureDefinedStructurePoolStructure a(WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        this.a = worldgenfeaturedefinedstructurepooltemplate_matching;
        return this;
    }

    public WorldGenFeatureDefinedStructurePoolTemplate.Matching e() {
        WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching = this.a;

        if (worldgenfeaturedefinedstructurepooltemplate_matching == null) {
            throw new IllegalStateException();
        } else {
            return worldgenfeaturedefinedstructurepooltemplate_matching;
        }
    }

    public int f() {
        return 1;
    }
}

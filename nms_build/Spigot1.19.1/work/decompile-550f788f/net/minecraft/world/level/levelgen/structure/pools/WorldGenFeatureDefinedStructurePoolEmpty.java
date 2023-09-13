package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class WorldGenFeatureDefinedStructurePoolEmpty extends WorldGenFeatureDefinedStructurePoolStructure {

    public static final Codec<WorldGenFeatureDefinedStructurePoolEmpty> CODEC = Codec.unit(() -> {
        return WorldGenFeatureDefinedStructurePoolEmpty.INSTANCE;
    });
    public static final WorldGenFeatureDefinedStructurePoolEmpty INSTANCE = new WorldGenFeatureDefinedStructurePoolEmpty();

    private WorldGenFeatureDefinedStructurePoolEmpty() {
        super(WorldGenFeatureDefinedStructurePoolTemplate.Matching.TERRAIN_MATCHING);
    }

    @Override
    public BaseBlockPosition getSize(StructureTemplateManager structuretemplatemanager, EnumBlockRotation enumblockrotation) {
        return BaseBlockPosition.ZERO;
    }

    @Override
    public List<DefinedStructure.BlockInfo> getShuffledJigsawBlocks(StructureTemplateManager structuretemplatemanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, RandomSource randomsource) {
        return Collections.emptyList();
    }

    @Override
    public StructureBoundingBox getBoundingBox(StructureTemplateManager structuretemplatemanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation) {
        throw new IllegalStateException("Invalid call to EmtyPoolElement.getBoundingBox, filter me!");
    }

    @Override
    public boolean place(StructureTemplateManager structuretemplatemanager, GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox, RandomSource randomsource, boolean flag) {
        return true;
    }

    @Override
    public WorldGenFeatureDefinedStructurePools<?> getType() {
        return WorldGenFeatureDefinedStructurePools.EMPTY;
    }

    public String toString() {
        return "Empty";
    }
}

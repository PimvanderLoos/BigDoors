package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.commons.lang3.mutable.MutableInt;

public class WorldGenFossils extends WorldGenerator<FossilFeatureConfiguration> {

    public WorldGenFossils(Codec<FossilFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<FossilFeatureConfiguration> featureplacecontext) {
        RandomSource randomsource = featureplacecontext.random();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(randomsource);
        FossilFeatureConfiguration fossilfeatureconfiguration = (FossilFeatureConfiguration) featureplacecontext.config();
        int i = randomsource.nextInt(fossilfeatureconfiguration.fossilStructures.size());
        StructureTemplateManager structuretemplatemanager = generatoraccessseed.getLevel().getServer().getStructureManager();
        DefinedStructure definedstructure = structuretemplatemanager.getOrCreate((MinecraftKey) fossilfeatureconfiguration.fossilStructures.get(i));
        DefinedStructure definedstructure1 = structuretemplatemanager.getOrCreate((MinecraftKey) fossilfeatureconfiguration.overlayStructures.get(i));
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);
        StructureBoundingBox structureboundingbox = new StructureBoundingBox(chunkcoordintpair.getMinBlockX() - 16, generatoraccessseed.getMinBuildHeight(), chunkcoordintpair.getMinBlockZ() - 16, chunkcoordintpair.getMaxBlockX() + 16, generatoraccessseed.getMaxBuildHeight(), chunkcoordintpair.getMaxBlockZ() + 16);
        DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).setRotation(enumblockrotation).setBoundingBox(structureboundingbox).setRandom(randomsource);
        BaseBlockPosition baseblockposition = definedstructure.getSize(enumblockrotation);
        BlockPosition blockposition1 = blockposition.offset(-baseblockposition.getX() / 2, 0, -baseblockposition.getZ() / 2);
        int j = blockposition.getY();

        int k;

        for (k = 0; k < baseblockposition.getX(); ++k) {
            for (int l = 0; l < baseblockposition.getZ(); ++l) {
                j = Math.min(j, generatoraccessseed.getHeight(HeightMap.Type.OCEAN_FLOOR_WG, blockposition1.getX() + k, blockposition1.getZ() + l));
            }
        }

        k = Math.max(j - 15 - randomsource.nextInt(10), generatoraccessseed.getMinBuildHeight() + 10);
        BlockPosition blockposition2 = definedstructure.getZeroPositionWithTransform(blockposition1.atY(k), EnumBlockMirror.NONE, enumblockrotation);

        if (countEmptyCorners(generatoraccessseed, definedstructure.getBoundingBox(definedstructureinfo, blockposition2)) > fossilfeatureconfiguration.maxEmptyCornersAllowed) {
            return false;
        } else {
            definedstructureinfo.clearProcessors();
            List list = ((ProcessorList) fossilfeatureconfiguration.fossilProcessors.value()).list();

            Objects.requireNonNull(definedstructureinfo);
            list.forEach(definedstructureinfo::addProcessor);
            definedstructure.placeInWorld(generatoraccessseed, blockposition2, blockposition2, definedstructureinfo, randomsource, 4);
            definedstructureinfo.clearProcessors();
            list = ((ProcessorList) fossilfeatureconfiguration.overlayProcessors.value()).list();
            Objects.requireNonNull(definedstructureinfo);
            list.forEach(definedstructureinfo::addProcessor);
            definedstructure1.placeInWorld(generatoraccessseed, blockposition2, blockposition2, definedstructureinfo, randomsource, 4);
            return true;
        }
    }

    private static int countEmptyCorners(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox) {
        MutableInt mutableint = new MutableInt(0);

        structureboundingbox.forAllCorners((blockposition) -> {
            IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition);

            if (iblockdata.isAir() || iblockdata.is(Blocks.LAVA) || iblockdata.is(Blocks.WATER)) {
                mutableint.add(1);
            }

        });
        return mutableint.getValue();
    }
}

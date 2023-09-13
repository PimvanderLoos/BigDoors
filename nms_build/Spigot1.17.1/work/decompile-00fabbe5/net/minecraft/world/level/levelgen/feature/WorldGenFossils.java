package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.resources.MinecraftKey;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;
import org.apache.commons.lang3.mutable.MutableInt;

public class WorldGenFossils extends WorldGenerator<FossilFeatureConfiguration> {

    public WorldGenFossils(Codec<FossilFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<FossilFeatureConfiguration> featureplacecontext) {
        Random random = featureplacecontext.c();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();
        EnumBlockRotation enumblockrotation = EnumBlockRotation.a(random);
        FossilFeatureConfiguration fossilfeatureconfiguration = (FossilFeatureConfiguration) featureplacecontext.e();
        int i = random.nextInt(fossilfeatureconfiguration.fossilStructures.size());
        DefinedStructureManager definedstructuremanager = generatoraccessseed.getLevel().getMinecraftServer().getDefinedStructureManager();
        DefinedStructure definedstructure = definedstructuremanager.a((MinecraftKey) fossilfeatureconfiguration.fossilStructures.get(i));
        DefinedStructure definedstructure1 = definedstructuremanager.a((MinecraftKey) fossilfeatureconfiguration.overlayStructures.get(i));
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);
        StructureBoundingBox structureboundingbox = new StructureBoundingBox(chunkcoordintpair.d(), generatoraccessseed.getMinBuildHeight(), chunkcoordintpair.e(), chunkcoordintpair.f(), generatoraccessseed.getMaxBuildHeight(), chunkcoordintpair.g());
        DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).a(enumblockrotation).a(structureboundingbox).a(random);
        BaseBlockPosition baseblockposition = definedstructure.a(enumblockrotation);
        int j = random.nextInt(16 - baseblockposition.getX());
        int k = random.nextInt(16 - baseblockposition.getZ());
        int l = generatoraccessseed.getMaxBuildHeight();

        int i1;

        for (i1 = 0; i1 < baseblockposition.getX(); ++i1) {
            for (int j1 = 0; j1 < baseblockposition.getZ(); ++j1) {
                l = Math.min(l, generatoraccessseed.a(HeightMap.Type.OCEAN_FLOOR_WG, blockposition.getX() + i1 + j, blockposition.getZ() + j1 + k));
            }
        }

        i1 = Math.max(l - 15 - random.nextInt(10), generatoraccessseed.getMinBuildHeight() + 10);
        BlockPosition blockposition1 = definedstructure.a(blockposition.c(j, 0, k).h(i1), EnumBlockMirror.NONE, enumblockrotation);

        if (a(generatoraccessseed, definedstructure.b(definedstructureinfo, blockposition1)) > fossilfeatureconfiguration.maxEmptyCornersAllowed) {
            return false;
        } else {
            definedstructureinfo.b();
            ((ProcessorList) fossilfeatureconfiguration.fossilProcessors.get()).a().forEach((definedstructureprocessor) -> {
                definedstructureinfo.a(definedstructureprocessor);
            });
            definedstructure.a(generatoraccessseed, blockposition1, blockposition1, definedstructureinfo, random, 4);
            definedstructureinfo.b();
            ((ProcessorList) fossilfeatureconfiguration.overlayProcessors.get()).a().forEach((definedstructureprocessor) -> {
                definedstructureinfo.a(definedstructureprocessor);
            });
            definedstructure1.a(generatoraccessseed, blockposition1, blockposition1, definedstructureinfo, random, 4);
            return true;
        }
    }

    private static int a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox) {
        MutableInt mutableint = new MutableInt(0);

        structureboundingbox.a((blockposition) -> {
            IBlockData iblockdata = generatoraccessseed.getType(blockposition);

            if (iblockdata.isAir() || iblockdata.a(Blocks.LAVA) || iblockdata.a(Blocks.WATER)) {
                mutableint.add(1);
            }

        });
        return mutableint.getValue();
    }
}

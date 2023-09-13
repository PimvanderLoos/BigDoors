package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomPatchConfiguration;

public class WorldGenFeatureRandomPatch extends WorldGenerator<WorldGenFeatureRandomPatchConfiguration> {

    public WorldGenFeatureRandomPatch(Codec<WorldGenFeatureRandomPatchConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration) {
        IBlockData iblockdata = worldgenfeaturerandompatchconfiguration.b.a(random, blockposition);
        BlockPosition blockposition1;

        if (worldgenfeaturerandompatchconfiguration.l) {
            blockposition1 = generatoraccessseed.getHighestBlockYAt(HeightMap.Type.WORLD_SURFACE_WG, blockposition);
        } else {
            blockposition1 = blockposition;
        }

        int i = 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j = 0; j < worldgenfeaturerandompatchconfiguration.f; ++j) {
            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition1, random.nextInt(worldgenfeaturerandompatchconfiguration.g + 1) - random.nextInt(worldgenfeaturerandompatchconfiguration.g + 1), random.nextInt(worldgenfeaturerandompatchconfiguration.h + 1) - random.nextInt(worldgenfeaturerandompatchconfiguration.h + 1), random.nextInt(worldgenfeaturerandompatchconfiguration.i + 1) - random.nextInt(worldgenfeaturerandompatchconfiguration.i + 1));
            BlockPosition blockposition2 = blockposition_mutableblockposition.down();
            IBlockData iblockdata1 = generatoraccessseed.getType(blockposition2);

            if ((generatoraccessseed.isEmpty(blockposition_mutableblockposition) || worldgenfeaturerandompatchconfiguration.j && generatoraccessseed.getType(blockposition_mutableblockposition).getMaterial().isReplaceable()) && iblockdata.canPlace(generatoraccessseed, blockposition_mutableblockposition) && (worldgenfeaturerandompatchconfiguration.d.isEmpty() || worldgenfeaturerandompatchconfiguration.d.contains(iblockdata1.getBlock())) && !worldgenfeaturerandompatchconfiguration.e.contains(iblockdata1) && (!worldgenfeaturerandompatchconfiguration.m || generatoraccessseed.getFluid(blockposition2.west()).a((Tag) TagsFluid.WATER) || generatoraccessseed.getFluid(blockposition2.east()).a((Tag) TagsFluid.WATER) || generatoraccessseed.getFluid(blockposition2.north()).a((Tag) TagsFluid.WATER) || generatoraccessseed.getFluid(blockposition2.south()).a((Tag) TagsFluid.WATER))) {
                worldgenfeaturerandompatchconfiguration.c.a(generatoraccessseed, blockposition_mutableblockposition, iblockdata, random);
                ++i;
            }
        }

        return i > 0;
    }
}

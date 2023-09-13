package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class WorldGenFeatureRandomPatch extends WorldGenerator<WorldGenFeatureRandomPatchConfiguration> {

    public WorldGenFeatureRandomPatch(Function<Dynamic<?>, ? extends WorldGenFeatureRandomPatchConfiguration> function) {
        super(function);
    }

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration) {
        IBlockData iblockdata = worldgenfeaturerandompatchconfiguration.a.a(random, blockposition);
        BlockPosition blockposition1;

        if (worldgenfeaturerandompatchconfiguration.k) {
            blockposition1 = generatoraccess.getHighestBlockYAt(HeightMap.Type.WORLD_SURFACE_WG, blockposition);
        } else {
            blockposition1 = blockposition;
        }

        int i = 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j = 0; j < worldgenfeaturerandompatchconfiguration.f; ++j) {
            blockposition_mutableblockposition.g(blockposition1).e(random.nextInt(worldgenfeaturerandompatchconfiguration.g + 1) - random.nextInt(worldgenfeaturerandompatchconfiguration.g + 1), random.nextInt(worldgenfeaturerandompatchconfiguration.h + 1) - random.nextInt(worldgenfeaturerandompatchconfiguration.h + 1), random.nextInt(worldgenfeaturerandompatchconfiguration.i + 1) - random.nextInt(worldgenfeaturerandompatchconfiguration.i + 1));
            BlockPosition blockposition2 = blockposition_mutableblockposition.down();
            IBlockData iblockdata1 = generatoraccess.getType(blockposition2);

            if ((generatoraccess.isEmpty(blockposition_mutableblockposition) || worldgenfeaturerandompatchconfiguration.j && generatoraccess.getType(blockposition_mutableblockposition).getMaterial().isReplaceable()) && iblockdata.canPlace(generatoraccess, blockposition_mutableblockposition) && (worldgenfeaturerandompatchconfiguration.c.isEmpty() || worldgenfeaturerandompatchconfiguration.c.contains(iblockdata1.getBlock())) && !worldgenfeaturerandompatchconfiguration.d.contains(iblockdata1) && (!worldgenfeaturerandompatchconfiguration.l || generatoraccess.getFluid(blockposition2.west()).a(TagsFluid.WATER) || generatoraccess.getFluid(blockposition2.east()).a(TagsFluid.WATER) || generatoraccess.getFluid(blockposition2.north()).a(TagsFluid.WATER) || generatoraccess.getFluid(blockposition2.south()).a(TagsFluid.WATER))) {
                worldgenfeaturerandompatchconfiguration.b.a(generatoraccess, blockposition_mutableblockposition, iblockdata, random);
                ++i;
            }
        }

        return i > 0;
    }
}

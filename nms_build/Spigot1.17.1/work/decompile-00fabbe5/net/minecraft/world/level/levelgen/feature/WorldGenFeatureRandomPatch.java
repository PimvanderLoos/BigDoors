package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomPatchConfiguration;

public class WorldGenFeatureRandomPatch extends WorldGenerator<WorldGenFeatureRandomPatchConfiguration> {

    public WorldGenFeatureRandomPatch(Codec<WorldGenFeatureRandomPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureRandomPatchConfiguration> featureplacecontext) {
        WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration = (WorldGenFeatureRandomPatchConfiguration) featureplacecontext.e();
        Random random = featureplacecontext.c();
        BlockPosition blockposition = featureplacecontext.d();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        IBlockData iblockdata = worldgenfeaturerandompatchconfiguration.stateProvider.a(random, blockposition);
        BlockPosition blockposition1;

        if (worldgenfeaturerandompatchconfiguration.project) {
            blockposition1 = generatoraccessseed.getHighestBlockYAt(HeightMap.Type.WORLD_SURFACE_WG, blockposition);
        } else {
            blockposition1 = blockposition;
        }

        int i = 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j = 0; j < worldgenfeaturerandompatchconfiguration.tries; ++j) {
            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition1, random.nextInt(worldgenfeaturerandompatchconfiguration.xspread + 1) - random.nextInt(worldgenfeaturerandompatchconfiguration.xspread + 1), random.nextInt(worldgenfeaturerandompatchconfiguration.yspread + 1) - random.nextInt(worldgenfeaturerandompatchconfiguration.yspread + 1), random.nextInt(worldgenfeaturerandompatchconfiguration.zspread + 1) - random.nextInt(worldgenfeaturerandompatchconfiguration.zspread + 1));
            BlockPosition blockposition2 = blockposition_mutableblockposition.down();
            IBlockData iblockdata1 = generatoraccessseed.getType(blockposition2);

            if ((generatoraccessseed.isEmpty(blockposition_mutableblockposition) || worldgenfeaturerandompatchconfiguration.canReplace && generatoraccessseed.getType(blockposition_mutableblockposition).getMaterial().isReplaceable()) && iblockdata.canPlace(generatoraccessseed, blockposition_mutableblockposition) && (worldgenfeaturerandompatchconfiguration.whitelist.isEmpty() || worldgenfeaturerandompatchconfiguration.whitelist.contains(iblockdata1.getBlock())) && !worldgenfeaturerandompatchconfiguration.blacklist.contains(iblockdata1) && (!worldgenfeaturerandompatchconfiguration.needWater || generatoraccessseed.getFluid(blockposition2.west()).a((Tag) TagsFluid.WATER) || generatoraccessseed.getFluid(blockposition2.east()).a((Tag) TagsFluid.WATER) || generatoraccessseed.getFluid(blockposition2.north()).a((Tag) TagsFluid.WATER) || generatoraccessseed.getFluid(blockposition2.south()).a((Tag) TagsFluid.WATER))) {
                worldgenfeaturerandompatchconfiguration.blockPlacer.a(generatoraccessseed, blockposition_mutableblockposition, iblockdata, random);
                ++i;
            }
        }

        return i > 0;
    }
}

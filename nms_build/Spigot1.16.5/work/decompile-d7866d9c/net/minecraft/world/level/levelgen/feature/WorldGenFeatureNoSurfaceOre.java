package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOreConfiguration;

public class WorldGenFeatureNoSurfaceOre extends WorldGenerator<WorldGenFeatureOreConfiguration> {

    WorldGenFeatureNoSurfaceOre(Codec<WorldGenFeatureOreConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureOreConfiguration worldgenfeatureoreconfiguration) {
        int i = random.nextInt(worldgenfeatureoreconfiguration.c + 1);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j = 0; j < i; ++j) {
            this.a(blockposition_mutableblockposition, random, blockposition, Math.min(j, 7));
            if (worldgenfeatureoreconfiguration.b.a(generatoraccessseed.getType(blockposition_mutableblockposition), random) && !this.a((GeneratorAccess) generatoraccessseed, (BlockPosition) blockposition_mutableblockposition)) {
                generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, worldgenfeatureoreconfiguration.d, 2);
            }
        }

        return true;
    }

    private void a(BlockPosition.MutableBlockPosition blockposition_mutableblockposition, Random random, BlockPosition blockposition, int i) {
        int j = this.a(random, i);
        int k = this.a(random, i);
        int l = this.a(random, i);

        blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, j, k, l);
    }

    private int a(Random random, int i) {
        return Math.round((random.nextFloat() - random.nextFloat()) * (float) i);
    }

    private boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection);
            if (generatoraccess.getType(blockposition_mutableblockposition).isAir()) {
                return true;
            }
        }

        return false;
    }
}

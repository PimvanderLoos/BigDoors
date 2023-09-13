package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomPatchConfiguration;

public class WorldGenFeatureFlower extends WorldGenFlowers<WorldGenFeatureRandomPatchConfiguration> {

    public WorldGenFeatureFlower(Codec<WorldGenFeatureRandomPatchConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration) {
        return !worldgenfeaturerandompatchconfiguration.blacklist.contains(generatoraccess.getType(blockposition));
    }

    public int a(WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration) {
        return worldgenfeaturerandompatchconfiguration.tries;
    }

    public BlockPosition a(Random random, BlockPosition blockposition, WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration) {
        return blockposition.c(random.nextInt(worldgenfeaturerandompatchconfiguration.xspread) - random.nextInt(worldgenfeaturerandompatchconfiguration.xspread), random.nextInt(worldgenfeaturerandompatchconfiguration.yspread) - random.nextInt(worldgenfeaturerandompatchconfiguration.yspread), random.nextInt(worldgenfeaturerandompatchconfiguration.zspread) - random.nextInt(worldgenfeaturerandompatchconfiguration.zspread));
    }

    public IBlockData b(Random random, BlockPosition blockposition, WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration) {
        return worldgenfeaturerandompatchconfiguration.stateProvider.a(random, blockposition);
    }
}

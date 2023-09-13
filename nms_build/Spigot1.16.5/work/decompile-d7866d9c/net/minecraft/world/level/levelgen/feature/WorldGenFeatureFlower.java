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
        return !worldgenfeaturerandompatchconfiguration.e.contains(generatoraccess.getType(blockposition));
    }

    public int a(WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration) {
        return worldgenfeaturerandompatchconfiguration.f;
    }

    public BlockPosition a(Random random, BlockPosition blockposition, WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration) {
        return blockposition.b(random.nextInt(worldgenfeaturerandompatchconfiguration.g) - random.nextInt(worldgenfeaturerandompatchconfiguration.g), random.nextInt(worldgenfeaturerandompatchconfiguration.h) - random.nextInt(worldgenfeaturerandompatchconfiguration.h), random.nextInt(worldgenfeaturerandompatchconfiguration.i) - random.nextInt(worldgenfeaturerandompatchconfiguration.i));
    }

    public IBlockData b(Random random, BlockPosition blockposition, WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration) {
        return worldgenfeaturerandompatchconfiguration.b.a(random, blockposition);
    }
}

package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class WorldGenFeatureFlower extends WorldGenFlowers<WorldGenFeatureRandomPatchConfiguration> {

    public WorldGenFeatureFlower(Function<Dynamic<?>, ? extends WorldGenFeatureRandomPatchConfiguration> function) {
        super(function);
    }

    public boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration) {
        return !worldgenfeaturerandompatchconfiguration.d.contains(generatoraccess.getType(blockposition));
    }

    public int a(WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration) {
        return worldgenfeaturerandompatchconfiguration.f;
    }

    public BlockPosition a(Random random, BlockPosition blockposition, WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration) {
        return blockposition.b(random.nextInt(worldgenfeaturerandompatchconfiguration.g) - random.nextInt(worldgenfeaturerandompatchconfiguration.g), random.nextInt(worldgenfeaturerandompatchconfiguration.h) - random.nextInt(worldgenfeaturerandompatchconfiguration.h), random.nextInt(worldgenfeaturerandompatchconfiguration.i) - random.nextInt(worldgenfeaturerandompatchconfiguration.i));
    }

    public IBlockData b(Random random, BlockPosition blockposition, WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration) {
        return worldgenfeaturerandompatchconfiguration.a.a(random, blockposition);
    }
}

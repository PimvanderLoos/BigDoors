package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenDecoratorConfigured<DC extends WorldGenFeatureDecoratorConfiguration> {

    public static final Codec<WorldGenDecoratorConfigured<?>> a = IRegistry.DECORATOR.dispatch("name", (worldgendecoratorconfigured) -> {
        return worldgendecoratorconfigured.b;
    }, WorldGenDecorator::a);
    public final WorldGenDecorator<DC> b;
    public final DC c;

    public WorldGenDecoratorConfigured(WorldGenDecorator<DC> worldgendecorator, DC dc) {
        this.b = worldgendecorator;
        this.c = dc;
    }

    public <FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureConfigured<FC, F> worldgenfeatureconfigured) {
        return this.b.a(generatoraccessseed, structuremanager, chunkgenerator, random, blockposition, this.c, worldgenfeatureconfigured);
    }
}

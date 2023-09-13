package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;

public class WorldGenDecoratorDecorated extends WorldGenDecorator<WorldGenDecoratorDecpratedConfiguration> {

    public WorldGenDecoratorDecorated(Codec<WorldGenDecoratorDecpratedConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WorldGenDecoratorDecpratedConfiguration worldgendecoratordecpratedconfiguration, BlockPosition blockposition) {
        return worldgendecoratordecpratedconfiguration.a().a(worldgendecoratorcontext, random, blockposition).flatMap((blockposition1) -> {
            return worldgendecoratordecpratedconfiguration.b().a(worldgendecoratorcontext, random, blockposition1);
        });
    }
}

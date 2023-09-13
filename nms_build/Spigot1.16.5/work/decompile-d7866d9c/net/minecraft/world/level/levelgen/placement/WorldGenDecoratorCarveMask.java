package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.ChunkCoordIntPair;

public class WorldGenDecoratorCarveMask extends WorldGenDecorator<WorldGenDecoratorCarveMaskConfiguration> {

    public WorldGenDecoratorCarveMask(Codec<WorldGenDecoratorCarveMaskConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WorldGenDecoratorCarveMaskConfiguration worldgendecoratorcarvemaskconfiguration, BlockPosition blockposition) {
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);
        BitSet bitset = worldgendecoratorcontext.a(chunkcoordintpair, worldgendecoratorcarvemaskconfiguration.c);

        return IntStream.range(0, bitset.length()).filter((i) -> {
            return bitset.get(i) && random.nextFloat() < worldgendecoratorcarvemaskconfiguration.d;
        }).mapToObj((i) -> {
            int j = i & 15;
            int k = i >> 4 & 15;
            int l = i >> 8;

            return new BlockPosition(chunkcoordintpair.d() + j, l, chunkcoordintpair.e() + k);
        });
    }
}

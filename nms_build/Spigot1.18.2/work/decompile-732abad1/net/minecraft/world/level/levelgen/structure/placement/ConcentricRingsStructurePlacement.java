package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkGenerator;

public record ConcentricRingsStructurePlacement(int c, int d, int e) implements StructurePlacement {

    private final int distance;
    private final int spread;
    private final int count;
    public static final Codec<ConcentricRingsStructurePlacement> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 1023).fieldOf("distance").forGetter(ConcentricRingsStructurePlacement::distance), Codec.intRange(0, 1023).fieldOf("spread").forGetter(ConcentricRingsStructurePlacement::spread), Codec.intRange(1, 4095).fieldOf("count").forGetter(ConcentricRingsStructurePlacement::count)).apply(instance, ConcentricRingsStructurePlacement::new);
    });

    public ConcentricRingsStructurePlacement(int i, int j, int k) {
        this.distance = i;
        this.spread = j;
        this.count = k;
    }

    @Override
    public boolean isFeatureChunk(ChunkGenerator chunkgenerator, long i, int j, int k) {
        List<ChunkCoordIntPair> list = chunkgenerator.getRingPositionsFor(this);

        return list == null ? false : list.contains(new ChunkCoordIntPair(j, k));
    }

    @Override
    public StructurePlacementType<?> type() {
        return StructurePlacementType.CONCENTRIC_RINGS;
    }

    public int distance() {
        return this.distance;
    }

    public int spread() {
        return this.spread;
    }

    public int count() {
        return this.count;
    }
}

package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;

public class WorldChunkManagerCheckerBoard extends WorldChunkManager {

    public static final Codec<WorldChunkManagerCheckerBoard> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BiomeBase.LIST_CODEC.fieldOf("biomes").forGetter((worldchunkmanagercheckerboard) -> {
            return worldchunkmanagercheckerboard.allowedBiomes;
        }), Codec.intRange(0, 62).fieldOf("scale").orElse(2).forGetter((worldchunkmanagercheckerboard) -> {
            return worldchunkmanagercheckerboard.size;
        })).apply(instance, WorldChunkManagerCheckerBoard::new);
    });
    private final List<Supplier<BiomeBase>> allowedBiomes;
    private final int bitShift;
    private final int size;

    public WorldChunkManagerCheckerBoard(List<Supplier<BiomeBase>> list, int i) {
        super(list.stream());
        this.allowedBiomes = list;
        this.bitShift = i + 2;
        this.size = i;
    }

    @Override
    protected Codec<? extends WorldChunkManager> a() {
        return WorldChunkManagerCheckerBoard.CODEC;
    }

    @Override
    public WorldChunkManager a(long i) {
        return this;
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        return (BiomeBase) ((Supplier) this.allowedBiomes.get(Math.floorMod((i >> this.bitShift) + (k >> this.bitShift), this.allowedBiomes.size()))).get();
    }
}

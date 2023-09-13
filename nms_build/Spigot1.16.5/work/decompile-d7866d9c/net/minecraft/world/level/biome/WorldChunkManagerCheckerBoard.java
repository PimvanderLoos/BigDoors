package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;

public class WorldChunkManagerCheckerBoard extends WorldChunkManager {

    public static final Codec<WorldChunkManagerCheckerBoard> e = RecordCodecBuilder.create((instance) -> {
        return instance.group(BiomeBase.e.fieldOf("biomes").forGetter((worldchunkmanagercheckerboard) -> {
            return worldchunkmanagercheckerboard.f;
        }), Codec.intRange(0, 62).fieldOf("scale").orElse(2).forGetter((worldchunkmanagercheckerboard) -> {
            return worldchunkmanagercheckerboard.h;
        })).apply(instance, WorldChunkManagerCheckerBoard::new);
    });
    private final List<Supplier<BiomeBase>> f;
    private final int g;
    private final int h;

    public WorldChunkManagerCheckerBoard(List<Supplier<BiomeBase>> list, int i) {
        super(list.stream());
        this.f = list;
        this.g = i + 2;
        this.h = i;
    }

    @Override
    protected Codec<? extends WorldChunkManager> a() {
        return WorldChunkManagerCheckerBoard.e;
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        return (BiomeBase) ((Supplier) this.f.get(Math.floorMod((i >> this.g) + (k >> this.g), this.f.size()))).get();
    }
}

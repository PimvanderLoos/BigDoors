package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class WorldChunkManagerCheckerBoard extends WorldChunkManager {

    public static final Codec<WorldChunkManagerCheckerBoard> e = RecordCodecBuilder.create((instance) -> {
        return instance.group(IRegistry.BIOME.listOf().fieldOf("biomes").forGetter((worldchunkmanagercheckerboard) -> {
            return worldchunkmanagercheckerboard.f;
        }), Codecs.a(0, 62).fieldOf("scale").withDefault(2).forGetter((worldchunkmanagercheckerboard) -> {
            return worldchunkmanagercheckerboard.h;
        })).apply(instance, WorldChunkManagerCheckerBoard::new);
    });
    private final List<BiomeBase> f;
    private final int g;
    private final int h;

    public WorldChunkManagerCheckerBoard(List<BiomeBase> list, int i) {
        super(ImmutableList.copyOf(list));
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
        return (BiomeBase) this.f.get(Math.floorMod((i >> this.g) + (k >> this.g), this.f.size()));
    }
}

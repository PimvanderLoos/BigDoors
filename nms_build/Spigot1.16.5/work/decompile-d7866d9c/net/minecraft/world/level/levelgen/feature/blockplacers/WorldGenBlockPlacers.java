package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public class WorldGenBlockPlacers<P extends WorldGenBlockPlacer> {

    public static final WorldGenBlockPlacers<WorldGenBlockPlacerSimple> a = a("simple_block_placer", WorldGenBlockPlacerSimple.b);
    public static final WorldGenBlockPlacers<WorldGenBlockPlacerDoublePlant> b = a("double_plant_placer", WorldGenBlockPlacerDoublePlant.b);
    public static final WorldGenBlockPlacers<WorldGenBlockPlacerColumn> c = a("column_placer", WorldGenBlockPlacerColumn.b);
    private final Codec<P> d;

    private static <P extends WorldGenBlockPlacer> WorldGenBlockPlacers<P> a(String s, Codec<P> codec) {
        return (WorldGenBlockPlacers) IRegistry.a(IRegistry.BLOCK_PLACER_TYPE, s, (Object) (new WorldGenBlockPlacers<>(codec)));
    }

    private WorldGenBlockPlacers(Codec<P> codec) {
        this.d = codec;
    }

    public Codec<P> a() {
        return this.d;
    }
}

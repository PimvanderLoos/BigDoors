package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public class WorldGenBlockPlacers<P extends WorldGenBlockPlacer> {

    public static final WorldGenBlockPlacers<WorldGenBlockPlacerSimple> SIMPLE_BLOCK_PLACER = a("simple_block_placer", WorldGenBlockPlacerSimple.CODEC);
    public static final WorldGenBlockPlacers<WorldGenBlockPlacerDoublePlant> DOUBLE_PLANT_PLACER = a("double_plant_placer", WorldGenBlockPlacerDoublePlant.CODEC);
    public static final WorldGenBlockPlacers<WorldGenBlockPlacerColumn> COLUMN_PLACER = a("column_placer", WorldGenBlockPlacerColumn.CODEC);
    private final Codec<P> codec;

    private static <P extends WorldGenBlockPlacer> WorldGenBlockPlacers<P> a(String s, Codec<P> codec) {
        return (WorldGenBlockPlacers) IRegistry.a(IRegistry.BLOCK_PLACER_TYPES, s, (Object) (new WorldGenBlockPlacers<>(codec)));
    }

    private WorldGenBlockPlacers(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> a() {
        return this.codec;
    }
}

package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenBlockPlacerSimple extends WorldGenBlockPlacer {

    public static final Codec<WorldGenBlockPlacerSimple> b = Codec.unit(() -> {
        return WorldGenBlockPlacerSimple.c;
    });
    public static final WorldGenBlockPlacerSimple c = new WorldGenBlockPlacerSimple();

    public WorldGenBlockPlacerSimple() {}

    @Override
    protected WorldGenBlockPlacers<?> a() {
        return WorldGenBlockPlacers.a;
    }

    @Override
    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        generatoraccess.setTypeAndData(blockposition, iblockdata, 2);
    }
}

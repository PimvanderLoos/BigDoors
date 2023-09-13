package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.BlockTallPlant;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenBlockPlacerDoublePlant extends WorldGenBlockPlacer {

    public static final Codec<WorldGenBlockPlacerDoublePlant> b = Codec.unit(() -> {
        return WorldGenBlockPlacerDoublePlant.c;
    });
    public static final WorldGenBlockPlacerDoublePlant c = new WorldGenBlockPlacerDoublePlant();

    public WorldGenBlockPlacerDoublePlant() {}

    @Override
    protected WorldGenBlockPlacers<?> a() {
        return WorldGenBlockPlacers.b;
    }

    @Override
    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        ((BlockTallPlant) iblockdata.getBlock()).a(generatoraccess, blockposition, 2);
    }
}

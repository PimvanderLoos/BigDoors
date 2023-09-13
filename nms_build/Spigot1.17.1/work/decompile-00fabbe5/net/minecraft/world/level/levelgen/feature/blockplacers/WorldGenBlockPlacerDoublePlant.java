package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.BlockTallPlant;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenBlockPlacerDoublePlant extends WorldGenBlockPlacer {

    public static final Codec<WorldGenBlockPlacerDoublePlant> CODEC = Codec.unit(() -> {
        return WorldGenBlockPlacerDoublePlant.INSTANCE;
    });
    public static final WorldGenBlockPlacerDoublePlant INSTANCE = new WorldGenBlockPlacerDoublePlant();

    public WorldGenBlockPlacerDoublePlant() {}

    @Override
    protected WorldGenBlockPlacers<?> a() {
        return WorldGenBlockPlacers.DOUBLE_PLANT_PLACER;
    }

    @Override
    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        BlockTallPlant.a(generatoraccess, iblockdata, blockposition, 2);
    }
}

package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class WorldGenBlockPlacer {

    public static final Codec<WorldGenBlockPlacer> a = IRegistry.BLOCK_PLACER_TYPE.dispatch(WorldGenBlockPlacer::a, WorldGenBlockPlacers::a);

    public WorldGenBlockPlacer() {}

    public abstract void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random);

    protected abstract WorldGenBlockPlacers<?> a();
}

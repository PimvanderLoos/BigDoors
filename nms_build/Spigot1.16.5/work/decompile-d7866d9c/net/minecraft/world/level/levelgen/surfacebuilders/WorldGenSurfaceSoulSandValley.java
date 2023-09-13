package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenSurfaceSoulSandValley extends WorldGenSurfaceNetherAbstract {

    private static final IBlockData a = Blocks.SOUL_SAND.getBlockData();
    private static final IBlockData b = Blocks.SOUL_SOIL.getBlockData();
    private static final IBlockData c = Blocks.GRAVEL.getBlockData();
    private static final ImmutableList<IBlockData> d = ImmutableList.of(WorldGenSurfaceSoulSandValley.a, WorldGenSurfaceSoulSandValley.b);

    public WorldGenSurfaceSoulSandValley(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    @Override
    protected ImmutableList<IBlockData> a() {
        return WorldGenSurfaceSoulSandValley.d;
    }

    @Override
    protected ImmutableList<IBlockData> b() {
        return WorldGenSurfaceSoulSandValley.d;
    }

    @Override
    protected IBlockData c() {
        return WorldGenSurfaceSoulSandValley.c;
    }
}

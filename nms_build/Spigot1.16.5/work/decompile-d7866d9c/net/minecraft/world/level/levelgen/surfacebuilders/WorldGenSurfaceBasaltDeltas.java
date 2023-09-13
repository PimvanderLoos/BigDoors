package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenSurfaceBasaltDeltas extends WorldGenSurfaceNetherAbstract {

    private static final IBlockData a = Blocks.BASALT.getBlockData();
    private static final IBlockData b = Blocks.BLACKSTONE.getBlockData();
    private static final IBlockData c = Blocks.GRAVEL.getBlockData();
    private static final ImmutableList<IBlockData> d = ImmutableList.of(WorldGenSurfaceBasaltDeltas.a, WorldGenSurfaceBasaltDeltas.b);
    private static final ImmutableList<IBlockData> e = ImmutableList.of(WorldGenSurfaceBasaltDeltas.a);

    public WorldGenSurfaceBasaltDeltas(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    @Override
    protected ImmutableList<IBlockData> a() {
        return WorldGenSurfaceBasaltDeltas.d;
    }

    @Override
    protected ImmutableList<IBlockData> b() {
        return WorldGenSurfaceBasaltDeltas.e;
    }

    @Override
    protected IBlockData c() {
        return WorldGenSurfaceBasaltDeltas.c;
    }
}

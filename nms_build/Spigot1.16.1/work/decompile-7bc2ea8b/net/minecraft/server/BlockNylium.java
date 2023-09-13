package net.minecraft.server;

import java.util.Random;

public class BlockNylium extends Block implements IBlockFragilePlantElement {

    protected BlockNylium(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    private static boolean b(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.up();
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);
        int i = LightEngineLayer.a(iworldreader, iblockdata, blockposition, iblockdata1, blockposition1, EnumDirection.UP, iblockdata1.b((IBlockAccess) iworldreader, blockposition1));

        return i < iworldreader.H();
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (!b(iblockdata, (IWorldReader) worldserver, blockposition)) {
            worldserver.setTypeUpdate(blockposition, Blocks.NETHERRACK.getBlockData());
        }

    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return iblockaccess.getType(blockposition.up()).isAir();
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        IBlockData iblockdata1 = worldserver.getType(blockposition);
        BlockPosition blockposition1 = blockposition.up();

        if (iblockdata1.a(Blocks.CRIMSON_NYLIUM)) {
            WorldGenFeatureNetherForestVegetation.a(worldserver, random, blockposition1, BiomeDecoratorGroups.aY, 3, 1);
        } else if (iblockdata1.a(Blocks.WARPED_NYLIUM)) {
            WorldGenFeatureNetherForestVegetation.a(worldserver, random, blockposition1, BiomeDecoratorGroups.aZ, 3, 1);
            WorldGenFeatureNetherForestVegetation.a(worldserver, random, blockposition1, BiomeDecoratorGroups.ba, 3, 1);
            if (random.nextInt(8) == 0) {
                WorldGenFeatureTwistingVines.a(worldserver, random, blockposition1, 3, 1, 2);
            }
        }

    }
}

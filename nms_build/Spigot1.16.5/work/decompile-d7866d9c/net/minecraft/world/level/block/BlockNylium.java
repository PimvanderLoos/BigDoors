package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.data.worldgen.BiomeDecoratorGroups;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureNetherForestVegetation;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureTwistingVines;
import net.minecraft.world.level.lighting.LightEngineLayer;

public class BlockNylium extends Block implements IBlockFragilePlantElement {

    protected BlockNylium(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    private static boolean b(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.up();
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);
        int i = LightEngineLayer.a(iworldreader, iblockdata, blockposition, iblockdata1, blockposition1, EnumDirection.UP, iblockdata1.b((IBlockAccess) iworldreader, blockposition1));

        return i < iworldreader.K();
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
            WorldGenFeatureNetherForestVegetation.a(worldserver, random, blockposition1, BiomeDecoratorGroups.a.k, 3, 1);
        } else if (iblockdata1.a(Blocks.WARPED_NYLIUM)) {
            WorldGenFeatureNetherForestVegetation.a(worldserver, random, blockposition1, BiomeDecoratorGroups.a.l, 3, 1);
            WorldGenFeatureNetherForestVegetation.a(worldserver, random, blockposition1, BiomeDecoratorGroups.a.m, 3, 1);
            if (random.nextInt(8) == 0) {
                WorldGenFeatureTwistingVines.a(worldserver, random, blockposition1, 3, 1, 2);
            }
        }

    }
}

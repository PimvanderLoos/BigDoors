package net.minecraft.world.level.block;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureHugeFungiConfiguration;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockFungi extends BlockPlant implements IBlockFragilePlantElement {

    protected static final VoxelShape a = Block.a(4.0D, 0.0D, 4.0D, 12.0D, 9.0D, 12.0D);
    private final Supplier<WorldGenFeatureConfigured<WorldGenFeatureHugeFungiConfiguration, ?>> b;

    protected BlockFungi(BlockBase.Info blockbase_info, Supplier<WorldGenFeatureConfigured<WorldGenFeatureHugeFungiConfiguration, ?>> supplier) {
        super(blockbase_info);
        this.b = supplier;
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockFungi.a;
    }

    @Override
    protected boolean c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.a((Tag) TagsBlock.NYLIUM) || iblockdata.a(Blocks.MYCELIUM) || iblockdata.a(Blocks.SOUL_SOIL) || super.c(iblockdata, iblockaccess, blockposition);
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        Block block = ((WorldGenFeatureHugeFungiConfiguration) ((WorldGenFeatureConfigured) this.b.get()).f).f.getBlock();
        Block block1 = iblockaccess.getType(blockposition.down()).getBlock();

        return block1 == block;
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return (double) random.nextFloat() < 0.4D;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        ((WorldGenFeatureConfigured) this.b.get()).a(worldserver, worldserver.getChunkProvider().getChunkGenerator(), random, blockposition);
    }
}

package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.grower.WorldGenTreeProvider;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockSapling extends BlockPlant implements IBlockFragilePlantElement {

    public static final BlockStateInteger STAGE = BlockProperties.aA;
    protected static final VoxelShape b = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
    private final WorldGenTreeProvider c;

    protected BlockSapling(WorldGenTreeProvider worldgentreeprovider, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.c = worldgentreeprovider;
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockSapling.STAGE, 0));
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockSapling.b;
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (worldserver.getLightLevel(blockposition.up()) >= 9 && random.nextInt(7) == 0) {
            this.grow(worldserver, blockposition, iblockdata, random);
        }

    }

    public void grow(WorldServer worldserver, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if ((Integer) iblockdata.get(BlockSapling.STAGE) == 0) {
            worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockSapling.STAGE), 4);
        } else {
            this.c.a(worldserver, worldserver.getChunkProvider().getChunkGenerator(), blockposition, iblockdata, random);
        }

    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return (double) world.random.nextFloat() < 0.45D;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        this.grow(worldserver, blockposition, iblockdata, random);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockSapling.STAGE);
    }
}

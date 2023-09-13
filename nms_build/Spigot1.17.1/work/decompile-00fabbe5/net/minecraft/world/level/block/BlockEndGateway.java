package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityEndGateway;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.FluidType;

public class BlockEndGateway extends BlockTileEntity {

    protected BlockEndGateway(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityEndGateway(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> a(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return a(tileentitytypes, TileEntityTypes.END_GATEWAY, world.isClientSide ? TileEntityEndGateway::a : TileEntityEndGateway::b);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityEndGateway) {
            int i = ((TileEntityEndGateway) tileentity).g();

            for (int j = 0; j < i; ++j) {
                double d0 = (double) blockposition.getX() + random.nextDouble();
                double d1 = (double) blockposition.getY() + random.nextDouble();
                double d2 = (double) blockposition.getZ() + random.nextDouble();
                double d3 = (random.nextDouble() - 0.5D) * 0.5D;
                double d4 = (random.nextDouble() - 0.5D) * 0.5D;
                double d5 = (random.nextDouble() - 0.5D) * 0.5D;
                int k = random.nextInt(2) * 2 - 1;

                if (random.nextBoolean()) {
                    d2 = (double) blockposition.getZ() + 0.5D + 0.25D * (double) k;
                    d5 = (double) (random.nextFloat() * 2.0F * (float) k);
                } else {
                    d0 = (double) blockposition.getX() + 0.5D + 0.25D * (double) k;
                    d3 = (double) (random.nextFloat() * 2.0F * (float) k);
                }

                world.addParticle(Particles.PORTAL, d0, d1, d2, d3, d4, d5);
            }

        }
    }

    @Override
    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean a(IBlockData iblockdata, FluidType fluidtype) {
        return false;
    }
}

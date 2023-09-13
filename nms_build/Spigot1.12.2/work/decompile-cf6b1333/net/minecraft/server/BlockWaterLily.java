package net.minecraft.server;

import java.util.List;
import javax.annotation.Nullable;

public class BlockWaterLily extends BlockPlant {

    protected static final AxisAlignedBB a = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.09375D, 0.9375D);

    protected BlockWaterLily() {
        this.a(CreativeModeTab.c);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag) {
        if (!(entity instanceof EntityBoat)) {
            a(blockposition, axisalignedbb, list, BlockWaterLily.a);
        }

    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
        super.a(world, blockposition, iblockdata, entity);
        if (entity instanceof EntityBoat) {
            world.setAir(new BlockPosition(blockposition), true);
        }

    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockWaterLily.a;
    }

    protected boolean x(IBlockData iblockdata) {
        return iblockdata.getBlock() == Blocks.WATER || iblockdata.getMaterial() == Material.ICE;
    }

    public boolean f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (blockposition.getY() >= 0 && blockposition.getY() < 256) {
            IBlockData iblockdata1 = world.getType(blockposition.down());
            Material material = iblockdata1.getMaterial();

            return material == Material.WATER && ((Integer) iblockdata1.get(BlockFluids.LEVEL)).intValue() == 0 || material == Material.ICE;
        } else {
            return false;
        }
    }

    public int toLegacyData(IBlockData iblockdata) {
        return 0;
    }
}

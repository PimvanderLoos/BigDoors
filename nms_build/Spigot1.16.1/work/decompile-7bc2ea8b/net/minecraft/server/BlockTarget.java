package net.minecraft.server;

import java.util.Random;

public class BlockTarget extends Block {

    private static final BlockStateInteger a = BlockProperties.az;

    public BlockTarget(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockTarget.a, 0));
    }

    @Override
    public void a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        int i = a((GeneratorAccess) world, iblockdata, movingobjectpositionblock, (Entity) iprojectile);
        Entity entity = iprojectile.getShooter();

        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;

            entityplayer.a(StatisticList.TARGET_HIT);
            CriterionTriggers.L.a(entityplayer, iprojectile, movingobjectpositionblock.getPos(), i);
        }

    }

    private static int a(GeneratorAccess generatoraccess, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, Entity entity) {
        int i = a(movingobjectpositionblock, movingobjectpositionblock.getPos());
        int j = entity instanceof EntityArrow ? 20 : 8;

        if (!generatoraccess.getBlockTickList().a(movingobjectpositionblock.getBlockPosition(), iblockdata.getBlock())) {
            a(generatoraccess, iblockdata, i, movingobjectpositionblock.getBlockPosition(), j);
        }

        return i;
    }

    private static int a(MovingObjectPositionBlock movingobjectpositionblock, Vec3D vec3d) {
        EnumDirection enumdirection = movingobjectpositionblock.getDirection();
        double d0 = Math.abs(MathHelper.h(vec3d.x) - 0.5D);
        double d1 = Math.abs(MathHelper.h(vec3d.y) - 0.5D);
        double d2 = Math.abs(MathHelper.h(vec3d.z) - 0.5D);
        EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.n();
        double d3;

        if (enumdirection_enumaxis == EnumDirection.EnumAxis.Y) {
            d3 = Math.max(d0, d2);
        } else if (enumdirection_enumaxis == EnumDirection.EnumAxis.Z) {
            d3 = Math.max(d0, d1);
        } else {
            d3 = Math.max(d1, d2);
        }

        return Math.max(1, MathHelper.f(15.0D * MathHelper.a((0.5D - d3) / 0.5D, 0.0D, 1.0D)));
    }

    private static void a(GeneratorAccess generatoraccess, IBlockData iblockdata, int i, BlockPosition blockposition, int j) {
        generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockTarget.a, i), 3);
        generatoraccess.getBlockTickList().a(blockposition, iblockdata.getBlock(), j);
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if ((Integer) iblockdata.get(BlockTarget.a) != 0) {
            worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockTarget.a, 0), 3);
        }

    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Integer) iblockdata.get(BlockTarget.a);
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockTarget.a);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!world.s_() && !iblockdata.a(iblockdata1.getBlock())) {
            if ((Integer) iblockdata.get(BlockTarget.a) > 0 && !world.getBlockTickList().a(blockposition, this)) {
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockTarget.a, 0), 18);
            }

        }
    }
}

package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class BlockMushroom extends BlockPlant implements IBlockFragilePlantElement {

    protected static final AxisAlignedBB a = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 0.4000000059604645D, 0.699999988079071D);

    protected BlockMushroom() {
        this.a(true);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockMushroom.a;
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (random.nextInt(25) == 0) {
            int i = 5;
            boolean flag = true;
            Iterator iterator = BlockPosition.b(blockposition.a(-4, -1, -4), blockposition.a(4, 1, 4)).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                if (world.getType(blockposition1).getBlock() == this) {
                    --i;
                    if (i <= 0) {
                        return;
                    }
                }
            }

            BlockPosition blockposition2 = blockposition.a(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

            for (int j = 0; j < 4; ++j) {
                if (world.isEmpty(blockposition2) && this.f(world, blockposition2, this.getBlockData())) {
                    blockposition = blockposition2;
                }

                blockposition2 = blockposition.a(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }

            if (world.isEmpty(blockposition2) && this.f(world, blockposition2, this.getBlockData())) {
                world.setTypeAndData(blockposition2, this.getBlockData(), 2);
            }
        }

    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return super.canPlace(world, blockposition) && this.f(world, blockposition, this.getBlockData());
    }

    protected boolean x(IBlockData iblockdata) {
        return iblockdata.b();
    }

    public boolean f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (blockposition.getY() >= 0 && blockposition.getY() < 256) {
            IBlockData iblockdata1 = world.getType(blockposition.down());

            return iblockdata1.getBlock() == Blocks.MYCELIUM ? true : (iblockdata1.getBlock() == Blocks.DIRT && iblockdata1.get(BlockDirt.VARIANT) == BlockDirt.EnumDirtVariant.PODZOL ? true : world.j(blockposition) < 13 && this.x(iblockdata1));
        } else {
            return false;
        }
    }

    public boolean c(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        world.setAir(blockposition);
        WorldGenHugeMushroom worldgenhugemushroom = null;

        if (this == Blocks.BROWN_MUSHROOM) {
            worldgenhugemushroom = new WorldGenHugeMushroom(Blocks.BROWN_MUSHROOM_BLOCK);
        } else if (this == Blocks.RED_MUSHROOM) {
            worldgenhugemushroom = new WorldGenHugeMushroom(Blocks.RED_MUSHROOM_BLOCK);
        }

        if (worldgenhugemushroom != null && worldgenhugemushroom.generate(world, random, blockposition)) {
            return true;
        } else {
            world.setTypeAndData(blockposition, iblockdata, 3);
            return false;
        }
    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return (double) random.nextFloat() < 0.4D;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        this.c(world, blockposition, iblockdata, random);
    }
}

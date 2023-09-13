package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;

public abstract class BlockFluids extends Block {

    public static final BlockStateInteger LEVEL = BlockStateInteger.of("level", 0, 15);

    protected BlockFluids(Material material) {
        super(material);
        this.w(this.blockStateList.getBlockData().set(BlockFluids.LEVEL, Integer.valueOf(0)));
        this.a(true);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockFluids.j;
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockFluids.k;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.material != Material.LAVA;
    }

    public static float b(int i) {
        if (i >= 8) {
            i = 0;
        }

        return (float) (i + 1) / 9.0F;
    }

    protected int x(IBlockData iblockdata) {
        return iblockdata.getMaterial() == this.material ? ((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue() : -1;
    }

    protected int y(IBlockData iblockdata) {
        int i = this.x(iblockdata);

        return i >= 8 ? 0 : i;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean a(IBlockData iblockdata, boolean flag) {
        return flag && ((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue() == 0;
    }

    private boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);
        Block block = iblockdata.getBlock();
        Material material = iblockdata.getMaterial();

        if (material == this.material) {
            return false;
        } else if (enumdirection == EnumDirection.UP) {
            return true;
        } else if (material == Material.ICE) {
            return false;
        } else {
            boolean flag = c(block) || block instanceof BlockStairs;

            return !flag && iblockdata.d(iblockaccess, blockposition, enumdirection) == EnumBlockFaceShape.SOLID;
        }
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.LIQUID;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.a;
    }

    public int a(Random random) {
        return 0;
    }

    protected Vec3D a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        double d0 = 0.0D;
        double d1 = 0.0D;
        double d2 = 0.0D;
        int i = this.y(iblockdata);
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.s();
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            blockposition_pooledblockposition.j(blockposition).d(enumdirection);
            int j = this.y(iblockaccess.getType(blockposition_pooledblockposition));
            int k;

            if (j < 0) {
                if (!iblockaccess.getType(blockposition_pooledblockposition).getMaterial().isSolid()) {
                    j = this.y(iblockaccess.getType(blockposition_pooledblockposition.down()));
                    if (j >= 0) {
                        k = j - (i - 8);
                        d0 += (double) (enumdirection.getAdjacentX() * k);
                        d1 += (double) (enumdirection.getAdjacentY() * k);
                        d2 += (double) (enumdirection.getAdjacentZ() * k);
                    }
                }
            } else if (j >= 0) {
                k = j - i;
                d0 += (double) (enumdirection.getAdjacentX() * k);
                d1 += (double) (enumdirection.getAdjacentY() * k);
                d2 += (double) (enumdirection.getAdjacentZ() * k);
            }
        }

        Vec3D vec3d = new Vec3D(d0, d1, d2);

        if (((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue() >= 8) {
            Iterator iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator1.hasNext()) {
                EnumDirection enumdirection1 = (EnumDirection) iterator1.next();

                blockposition_pooledblockposition.j(blockposition).d(enumdirection1);
                if (this.a(iblockaccess, (BlockPosition) blockposition_pooledblockposition, enumdirection1) || this.a(iblockaccess, blockposition_pooledblockposition.up(), enumdirection1)) {
                    vec3d = vec3d.a().add(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }

        blockposition_pooledblockposition.t();
        return vec3d.a();
    }

    public Vec3D a(World world, BlockPosition blockposition, Entity entity, Vec3D vec3d) {
        return vec3d.e(this.a((IBlockAccess) world, blockposition, world.getType(blockposition)));
    }

    public int a(World world) {
        return this.material == Material.WATER ? 5 : (this.material == Material.LAVA ? (world.worldProvider.n() ? 10 : 30) : 0);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.e(world, blockposition, iblockdata);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        this.e(world, blockposition, iblockdata);
    }

    public boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.material == Material.LAVA) {
            boolean flag = false;
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                if (enumdirection != EnumDirection.DOWN && world.getType(blockposition.shift(enumdirection)).getMaterial() == Material.WATER) {
                    flag = true;
                    break;
                }
            }

            if (flag) {
                Integer integer = (Integer) iblockdata.get(BlockFluids.LEVEL);

                if (integer.intValue() == 0) {
                    world.setTypeUpdate(blockposition, Blocks.OBSIDIAN.getBlockData());
                    this.fizz(world, blockposition);
                    return true;
                }

                if (integer.intValue() <= 4) {
                    world.setTypeUpdate(blockposition, Blocks.COBBLESTONE.getBlockData());
                    this.fizz(world, blockposition);
                    return true;
                }
            }
        }

        return false;
    }

    protected void fizz(World world, BlockPosition blockposition) {
        double d0 = (double) blockposition.getX();
        double d1 = (double) blockposition.getY();
        double d2 = (double) blockposition.getZ();

        world.a((EntityHuman) null, blockposition, SoundEffects.dE, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

        for (int i = 0; i < 8; ++i) {
            world.addParticle(EnumParticle.SMOKE_LARGE, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
        }

    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockFluids.LEVEL, Integer.valueOf(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockFluids.LEVEL});
    }

    public static BlockFlowing a(Material material) {
        if (material == Material.WATER) {
            return Blocks.FLOWING_WATER;
        } else if (material == Material.LAVA) {
            return Blocks.FLOWING_LAVA;
        } else {
            throw new IllegalArgumentException("Invalid material");
        }
    }

    public static BlockStationary b(Material material) {
        if (material == Material.WATER) {
            return Blocks.WATER;
        } else if (material == Material.LAVA) {
            return Blocks.LAVA;
        } else {
            throw new IllegalArgumentException("Invalid material");
        }
    }

    public static float g(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        int i = ((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue();

        return (i & 7) == 0 && iblockaccess.getType(blockposition.up()).getMaterial() == Material.WATER ? 1.0F : 1.0F - b(i);
    }

    public static float h(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return (float) blockposition.getY() + g(iblockdata, iblockaccess, blockposition);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}

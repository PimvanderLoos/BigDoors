package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class BlockFluids extends Block implements IFluidSource {

    public static final BlockStateInteger LEVEL = BlockProperties.ag;
    protected final FluidTypeFlowing b;
    private final Map<IBlockData, VoxelShape> c = Maps.newIdentityHashMap();

    protected BlockFluids(FluidTypeFlowing fluidtypeflowing, Block.Info block_info) {
        super(block_info);
        this.b = fluidtypeflowing;
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockFluids.LEVEL, Integer.valueOf(0)));
    }

    public void b(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        world.b(blockposition).b(world, blockposition, random);
    }

    public boolean a_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return !this.b.a(TagsFluid.b);
    }

    public Fluid h(IBlockData iblockdata) {
        int i = ((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue();

        return i >= 8 ? this.b.a(8, true) : (i == 0 ? this.b.a(false) : this.b.a(8 - i, false));
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public boolean d(IBlockData iblockdata) {
        return false;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Fluid fluid = iblockaccess.b(blockposition.up());

        return fluid.c().a((FluidType) this.b) ? VoxelShapes.b() : (VoxelShape) this.c.computeIfAbsent(iblockdata, (iblockdata) -> {
            Fluid fluid = iblockdata.s();

            return VoxelShapes.a(0.0D, 0.0D, 0.0D, 1.0D, (double) fluid.f(), 1.0D);
        });
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.AIR;
    }

    public int a(IWorldReader iworldreader) {
        return this.b.a(iworldreader);
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (this.a(world, blockposition, iblockdata)) {
            world.H().a(blockposition, iblockdata.s().c(), this.a((IWorldReader) world));
        }

    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (iblockdata.s().d() || iblockdata1.s().d()) {
            generatoraccess.H().a(blockposition, iblockdata.s().c(), this.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (this.a(world, blockposition, iblockdata)) {
            world.H().a(blockposition, iblockdata.s().c(), this.a((IWorldReader) world));
        }

    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.b.a(TagsFluid.b)) {
            boolean flag = false;
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                if (enumdirection != EnumDirection.DOWN && world.b(blockposition.shift(enumdirection)).a(TagsFluid.a)) {
                    flag = true;
                    break;
                }
            }

            if (flag) {
                Fluid fluid = world.b(blockposition);

                if (fluid.d()) {
                    world.setTypeUpdate(blockposition, Blocks.OBSIDIAN.getBlockData());
                    this.fizz(world, blockposition);
                    return false;
                }

                if (fluid.f() >= 0.44444445F) {
                    world.setTypeUpdate(blockposition, Blocks.COBBLESTONE.getBlockData());
                    this.fizz(world, blockposition);
                    return false;
                }
            }
        }

        return true;
    }

    protected void fizz(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        double d0 = (double) blockposition.getX();
        double d1 = (double) blockposition.getY();
        double d2 = (double) blockposition.getZ();

        generatoraccess.a((EntityHuman) null, blockposition, SoundEffects.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (generatoraccess.m().nextFloat() - generatoraccess.m().nextFloat()) * 0.8F);

        for (int i = 0; i < 8; ++i) {
            generatoraccess.addParticle(Particles.F, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D);
        }

    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(new IBlockState[] { BlockFluids.LEVEL});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public FluidType a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if (((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue() == 0) {
            generatoraccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 11);
            return this.b;
        } else {
            return FluidTypes.a;
        }
    }
}

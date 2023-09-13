package net.minecraft.server;

public abstract class FluidTypeWater extends FluidTypeFlowing {

    public FluidTypeWater() {}

    public FluidType e() {
        return FluidTypes.FLOWING_WATER;
    }

    public FluidType f() {
        return FluidTypes.WATER;
    }

    public Item b() {
        return Items.WATER_BUCKET;
    }

    protected boolean g() {
        return true;
    }

    protected void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        iblockdata.a(generatoraccess.getMinecraftWorld(), blockposition, 0);
    }

    public int b(IWorldReader iworldreader) {
        return 4;
    }

    public IBlockData b(Fluid fluid) {
        return (IBlockData) Blocks.WATER.getBlockData().set(BlockFluids.LEVEL, e(fluid));
    }

    public boolean a(FluidType fluidtype) {
        return fluidtype == FluidTypes.WATER || fluidtype == FluidTypes.FLOWING_WATER;
    }

    public int c(IWorldReader iworldreader) {
        return 1;
    }

    public int a(IWorldReader iworldreader) {
        return 5;
    }

    public boolean a(Fluid fluid, FluidType fluidtype, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN && !fluidtype.a(TagsFluid.WATER);
    }

    protected float d() {
        return 100.0F;
    }

    public static class a extends FluidTypeWater {

        public a() {}

        protected void a(BlockStateList.a<FluidType, Fluid> blockstatelist_a) {
            super.a(blockstatelist_a);
            blockstatelist_a.a(FluidTypeWater.a.LEVEL);
        }

        public int d(Fluid fluid) {
            return (Integer) fluid.get(FluidTypeWater.a.LEVEL);
        }

        public boolean c(Fluid fluid) {
            return false;
        }
    }

    public static class b extends FluidTypeWater {

        public b() {}

        public int d(Fluid fluid) {
            return 8;
        }

        public boolean c(Fluid fluid) {
            return true;
        }
    }
}

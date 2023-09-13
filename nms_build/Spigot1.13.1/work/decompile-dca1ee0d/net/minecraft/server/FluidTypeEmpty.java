package net.minecraft.server;

public class FluidTypeEmpty extends FluidType {

    public FluidTypeEmpty() {}

    public Item b() {
        return Items.AIR;
    }

    public boolean a(Fluid fluid, FluidType fluidtype, EnumDirection enumdirection) {
        return true;
    }

    public Vec3D a(IWorldReader iworldreader, BlockPosition blockposition, Fluid fluid) {
        return Vec3D.a;
    }

    public int a(IWorldReader iworldreader) {
        return 0;
    }

    protected boolean c() {
        return true;
    }

    protected float d() {
        return 0.0F;
    }

    public float a(Fluid fluid) {
        return 0.0F;
    }

    protected IBlockData b(Fluid fluid) {
        return Blocks.AIR.getBlockData();
    }

    public boolean c(Fluid fluid) {
        return false;
    }

    public int d(Fluid fluid) {
        return 0;
    }
}

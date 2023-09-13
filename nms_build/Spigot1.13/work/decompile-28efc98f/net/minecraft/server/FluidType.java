package net.minecraft.server;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Random;

public abstract class FluidType {

    private static final MinecraftKey a = new MinecraftKey("empty");
    public static final RegistryBlocks<MinecraftKey, FluidType> c = new RegistryBlocks(FluidType.a);
    public static final RegistryBlockID<Fluid> d = new RegistryBlockID();
    protected final BlockStateList<FluidType, Fluid> e;
    private Fluid b;

    protected FluidType() {
        BlockStateList.a blockstatelist_a = new BlockStateList.a(this);

        this.a(blockstatelist_a);
        this.e = blockstatelist_a.a(FluidImpl::new);
        this.f((Fluid) this.e.getBlockData());
    }

    protected void a(BlockStateList.a<FluidType, Fluid> blockstatelist_a) {}

    public BlockStateList<FluidType, Fluid> h() {
        return this.e;
    }

    protected final void f(Fluid fluid) {
        this.b = fluid;
    }

    public final Fluid i() {
        return this.b;
    }

    public abstract Item b();

    protected void a(World world, BlockPosition blockposition, Fluid fluid) {}

    protected void b(World world, BlockPosition blockposition, Fluid fluid, Random random) {}

    protected abstract boolean a(Fluid fluid, FluidType fluidtype, EnumDirection enumdirection);

    protected abstract Vec3D a(IWorldReader iworldreader, BlockPosition blockposition, Fluid fluid);

    public abstract int a(IWorldReader iworldreader);

    protected boolean k() {
        return false;
    }

    protected boolean c() {
        return false;
    }

    protected abstract float d();

    public abstract float a(Fluid fluid);

    protected abstract IBlockData b(Fluid fluid);

    public abstract boolean c(Fluid fluid);

    public abstract int d(Fluid fluid);

    public boolean a(FluidType fluidtype) {
        return fluidtype == this;
    }

    public boolean a(Tag<FluidType> tag) {
        return tag.isTagged(this);
    }

    public static void l() {
        a(FluidType.a, new FluidTypeEmpty());
        a("flowing_water", new FluidTypeWater.a());
        a("water", new FluidTypeWater.b());
        a("flowing_lava", new FluidTypeLava.a());
        a("lava", new FluidTypeLava.b());
        FluidType.c.a();
        Iterator iterator = FluidType.c.iterator();

        while (iterator.hasNext()) {
            FluidType fluidtype = (FluidType) iterator.next();
            UnmodifiableIterator unmodifiableiterator = fluidtype.h().a().iterator();

            while (unmodifiableiterator.hasNext()) {
                Fluid fluid = (Fluid) unmodifiableiterator.next();

                FluidType.d.b(fluid);
            }
        }

    }

    private static void a(String s, FluidType fluidtype) {
        a(new MinecraftKey(s), fluidtype);
    }

    private static void a(MinecraftKey minecraftkey, FluidType fluidtype) {
        FluidType.c.a(minecraftkey, fluidtype);
    }
}

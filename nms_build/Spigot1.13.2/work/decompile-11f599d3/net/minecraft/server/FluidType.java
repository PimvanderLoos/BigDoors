package net.minecraft.server;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Random;

public abstract class FluidType {

    public static final RegistryBlockID<Fluid> c = new RegistryBlockID<>();
    protected final BlockStateList<FluidType, Fluid> d;
    private Fluid a;

    protected FluidType() {
        BlockStateList.a<FluidType, Fluid> blockstatelist_a = new BlockStateList.a<>(this);

        this.a(blockstatelist_a);
        this.d = blockstatelist_a.a(FluidImpl::new);
        this.f((Fluid) this.d.getBlockData());
    }

    protected void a(BlockStateList.a<FluidType, Fluid> blockstatelist_a) {}

    public BlockStateList<FluidType, Fluid> h() {
        return this.d;
    }

    protected final void f(Fluid fluid) {
        this.a = fluid;
    }

    public final Fluid i() {
        return this.a;
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
        a(IRegistry.FLUID.b(), new FluidTypeEmpty());
        a("flowing_water", new FluidTypeWater.a());
        a("water", new FluidTypeWater.b());
        a("flowing_lava", new FluidTypeLava.a());
        a("lava", new FluidTypeLava.b());
        Iterator iterator = IRegistry.FLUID.iterator();

        while (iterator.hasNext()) {
            FluidType fluidtype = (FluidType) iterator.next();
            UnmodifiableIterator unmodifiableiterator = fluidtype.h().a().iterator();

            while (unmodifiableiterator.hasNext()) {
                Fluid fluid = (Fluid) unmodifiableiterator.next();

                FluidType.c.b(fluid);
            }
        }

    }

    private static void a(String s, FluidType fluidtype) {
        a(new MinecraftKey(s), fluidtype);
    }

    private static void a(MinecraftKey minecraftkey, FluidType fluidtype) {
        IRegistry.FLUID.a(minecraftkey, (Object) fluidtype);
    }
}

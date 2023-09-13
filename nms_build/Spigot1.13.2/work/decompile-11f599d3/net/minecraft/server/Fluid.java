package net.minecraft.server;

import java.util.Random;

public interface Fluid extends IBlockDataHolder<Fluid> {

    FluidType c();

    default boolean d() {
        return this.c().c(this);
    }

    default boolean e() {
        return this.c().c();
    }

    default float getHeight() {
        return this.c().a(this);
    }

    default int g() {
        return this.c().d(this);
    }

    default void a(World world, BlockPosition blockposition) {
        this.c().a(world, blockposition, this);
    }

    default boolean h() {
        return this.c().k();
    }

    default void b(World world, BlockPosition blockposition, Random random) {
        this.c().b(world, blockposition, this, random);
    }

    default Vec3D a(IWorldReader iworldreader, BlockPosition blockposition) {
        return this.c().a(iworldreader, blockposition, this);
    }

    default IBlockData i() {
        return this.c().b(this);
    }

    default boolean a(Tag<FluidType> tag) {
        return this.c().a(tag);
    }

    default float l() {
        return this.c().d();
    }

    default boolean a(FluidType fluidtype, EnumDirection enumdirection) {
        return this.c().a(this, fluidtype, enumdirection);
    }
}

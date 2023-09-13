package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;

public final class Fluid extends IBlockDataHolder<FluidType, Fluid> {

    public static final Codec<Fluid> a = a((Codec) IRegistry.FLUID, FluidType::h).stable();

    public Fluid(FluidType fluidtype, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap, MapCodec<Fluid> mapcodec) {
        super(fluidtype, immutablemap, mapcodec);
    }

    public FluidType getType() {
        return (FluidType) this.c;
    }

    public boolean isSource() {
        return this.getType().c(this);
    }

    public boolean isEmpty() {
        return this.getType().b();
    }

    public float getHeight(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getType().a(this, iblockaccess, blockposition);
    }

    public float d() {
        return this.getType().a(this);
    }

    public int e() {
        return this.getType().d(this);
    }

    public void a(World world, BlockPosition blockposition) {
        this.getType().a(world, blockposition, this);
    }

    public boolean f() {
        return this.getType().j();
    }

    public void b(World world, BlockPosition blockposition, Random random) {
        this.getType().b(world, blockposition, this, random);
    }

    public Vec3D c(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getType().a(iblockaccess, blockposition, this);
    }

    public IBlockData getBlockData() {
        return this.getType().b(this);
    }

    public boolean a(Tag<FluidType> tag) {
        return this.getType().a(tag);
    }

    public float i() {
        return this.getType().c();
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, FluidType fluidtype, EnumDirection enumdirection) {
        return this.getType().a(this, iblockaccess, blockposition, fluidtype, enumdirection);
    }

    public VoxelShape d(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getType().b(this, iblockaccess, blockposition);
    }
}

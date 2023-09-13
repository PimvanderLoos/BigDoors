package net.minecraft.world.level.material;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.IBlockDataHolder;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class Fluid extends IBlockDataHolder<FluidType, Fluid> {

    public static final Codec<Fluid> CODEC = a((Codec) IRegistry.FLUID, FluidType::h).stable();
    public static final int AMOUNT_MAX = 9;
    public static final int AMOUNT_FULL = 8;

    public Fluid(FluidType fluidtype, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap, MapCodec<Fluid> mapcodec) {
        super(fluidtype, immutablemap, mapcodec);
    }

    public FluidType getType() {
        return (FluidType) this.owner;
    }

    public boolean isSource() {
        return this.getType().c(this);
    }

    public boolean a(FluidType fluidtype) {
        return this.owner == fluidtype && ((FluidType) this.owner).c(this);
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

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                BlockPosition blockposition1 = blockposition.c(i, 0, j);
                Fluid fluid = iblockaccess.getFluid(blockposition1);

                if (!fluid.getType().a(this.getType()) && !iblockaccess.getType(blockposition1).i(iblockaccess, blockposition1)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void a(World world, BlockPosition blockposition) {
        this.getType().a(world, blockposition, this);
    }

    public void a(World world, BlockPosition blockposition, Random random) {
        this.getType().a(world, blockposition, this, random);
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

    @Nullable
    public ParticleParam h() {
        return this.getType().i();
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

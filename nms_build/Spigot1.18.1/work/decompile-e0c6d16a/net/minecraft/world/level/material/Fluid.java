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

    public static final Codec<Fluid> CODEC = codec(IRegistry.FLUID.byNameCodec(), FluidType::defaultFluidState).stable();
    public static final int AMOUNT_MAX = 9;
    public static final int AMOUNT_FULL = 8;

    public Fluid(FluidType fluidtype, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap, MapCodec<Fluid> mapcodec) {
        super(fluidtype, immutablemap, mapcodec);
    }

    public FluidType getType() {
        return (FluidType) this.owner;
    }

    public boolean isSource() {
        return this.getType().isSource(this);
    }

    public boolean isSourceOfType(FluidType fluidtype) {
        return this.owner == fluidtype && ((FluidType) this.owner).isSource(this);
    }

    public boolean isEmpty() {
        return this.getType().isEmpty();
    }

    public float getHeight(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getType().getHeight(this, iblockaccess, blockposition);
    }

    public float getOwnHeight() {
        return this.getType().getOwnHeight(this);
    }

    public int getAmount() {
        return this.getType().getAmount(this);
    }

    public boolean shouldRenderBackwardUpFace(IBlockAccess iblockaccess, BlockPosition blockposition) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                BlockPosition blockposition1 = blockposition.offset(i, 0, j);
                Fluid fluid = iblockaccess.getFluidState(blockposition1);

                if (!fluid.getType().isSame(this.getType()) && !iblockaccess.getBlockState(blockposition1).isSolidRender(iblockaccess, blockposition1)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void tick(World world, BlockPosition blockposition) {
        this.getType().tick(world, blockposition, this);
    }

    public void animateTick(World world, BlockPosition blockposition, Random random) {
        this.getType().animateTick(world, blockposition, this, random);
    }

    public boolean isRandomlyTicking() {
        return this.getType().isRandomlyTicking();
    }

    public void randomTick(World world, BlockPosition blockposition, Random random) {
        this.getType().randomTick(world, blockposition, this, random);
    }

    public Vec3D getFlow(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getType().getFlow(iblockaccess, blockposition, this);
    }

    public IBlockData createLegacyBlock() {
        return this.getType().createLegacyBlock(this);
    }

    @Nullable
    public ParticleParam getDripParticle() {
        return this.getType().getDripParticle();
    }

    public boolean is(Tag<FluidType> tag) {
        return this.getType().is(tag);
    }

    public boolean is(FluidType fluidtype) {
        return this.getType() == fluidtype;
    }

    public float getExplosionResistance() {
        return this.getType().getExplosionResistance();
    }

    public boolean canBeReplacedWith(IBlockAccess iblockaccess, BlockPosition blockposition, FluidType fluidtype, EnumDirection enumdirection) {
        return this.getType().canBeReplacedWith(this, iblockaccess, blockposition, fluidtype, enumdirection);
    }

    public VoxelShape getShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getType().getShape(this, iblockaccess, blockposition);
    }
}

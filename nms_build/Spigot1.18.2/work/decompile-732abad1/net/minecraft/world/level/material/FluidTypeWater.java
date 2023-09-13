package net.minecraft.world.level.material;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockFluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class FluidTypeWater extends FluidTypeFlowing {

    public FluidTypeWater() {}

    @Override
    public FluidType getFlowing() {
        return FluidTypes.FLOWING_WATER;
    }

    @Override
    public FluidType getSource() {
        return FluidTypes.WATER;
    }

    @Override
    public Item getBucket() {
        return Items.WATER_BUCKET;
    }

    @Override
    public void animateTick(World world, BlockPosition blockposition, Fluid fluid, Random random) {
        if (!fluid.isSource() && !(Boolean) fluid.getValue(FluidTypeWater.FALLING)) {
            if (random.nextInt(64) == 0) {
                world.playLocalSound((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, SoundEffects.WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
            }
        } else if (random.nextInt(10) == 0) {
            world.addParticle(Particles.UNDERWATER, (double) blockposition.getX() + random.nextDouble(), (double) blockposition.getY() + random.nextDouble(), (double) blockposition.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
        }

    }

    @Nullable
    @Override
    public ParticleParam getDripParticle() {
        return Particles.DRIPPING_WATER;
    }

    @Override
    protected boolean canConvertToSource() {
        return true;
    }

    @Override
    protected void beforeDestroyingBlock(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = iblockdata.hasBlockEntity() ? generatoraccess.getBlockEntity(blockposition) : null;

        Block.dropResources(iblockdata, generatoraccess, blockposition, tileentity);
    }

    @Override
    public int getSlopeFindDistance(IWorldReader iworldreader) {
        return 4;
    }

    @Override
    public IBlockData createLegacyBlock(Fluid fluid) {
        return (IBlockData) Blocks.WATER.defaultBlockState().setValue(BlockFluids.LEVEL, getLegacyLevel(fluid));
    }

    @Override
    public boolean isSame(FluidType fluidtype) {
        return fluidtype == FluidTypes.WATER || fluidtype == FluidTypes.FLOWING_WATER;
    }

    @Override
    public int getDropOff(IWorldReader iworldreader) {
        return 1;
    }

    @Override
    public int getTickDelay(IWorldReader iworldreader) {
        return 5;
    }

    @Override
    public boolean canBeReplacedWith(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition, FluidType fluidtype, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN && !fluidtype.is(TagsFluid.WATER);
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    public Optional<SoundEffect> getPickupSound() {
        return Optional.of(SoundEffects.BUCKET_FILL);
    }

    public static class a extends FluidTypeWater {

        public a() {}

        @Override
        protected void createFluidStateDefinition(BlockStateList.a<FluidType, Fluid> blockstatelist_a) {
            super.createFluidStateDefinition(blockstatelist_a);
            blockstatelist_a.add(FluidTypeWater.a.LEVEL);
        }

        @Override
        public int getAmount(Fluid fluid) {
            return (Integer) fluid.getValue(FluidTypeWater.a.LEVEL);
        }

        @Override
        public boolean isSource(Fluid fluid) {
            return false;
        }
    }

    public static class b extends FluidTypeWater {

        public b() {}

        @Override
        public int getAmount(Fluid fluid) {
            return 8;
        }

        @Override
        public boolean isSource(Fluid fluid) {
            return true;
        }
    }
}

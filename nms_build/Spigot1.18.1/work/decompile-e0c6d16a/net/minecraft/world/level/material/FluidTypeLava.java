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
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockFireAbstract;
import net.minecraft.world.level.block.BlockFluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class FluidTypeLava extends FluidTypeFlowing {

    public static final float MIN_LEVEL_CUTOFF = 0.44444445F;

    public FluidTypeLava() {}

    @Override
    public FluidType getFlowing() {
        return FluidTypes.FLOWING_LAVA;
    }

    @Override
    public FluidType getSource() {
        return FluidTypes.LAVA;
    }

    @Override
    public Item getBucket() {
        return Items.LAVA_BUCKET;
    }

    @Override
    public void animateTick(World world, BlockPosition blockposition, Fluid fluid, Random random) {
        BlockPosition blockposition1 = blockposition.above();

        if (world.getBlockState(blockposition1).isAir() && !world.getBlockState(blockposition1).isSolidRender(world, blockposition1)) {
            if (random.nextInt(100) == 0) {
                double d0 = (double) blockposition.getX() + random.nextDouble();
                double d1 = (double) blockposition.getY() + 1.0D;
                double d2 = (double) blockposition.getZ() + random.nextDouble();

                world.addParticle(Particles.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                world.playLocalSound(d0, d1, d2, SoundEffects.LAVA_POP, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }

            if (random.nextInt(200) == 0) {
                world.playLocalSound((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), SoundEffects.LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }

    }

    @Override
    public void randomTick(World world, BlockPosition blockposition, Fluid fluid, Random random) {
        if (world.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            int i = random.nextInt(3);

            if (i > 0) {
                BlockPosition blockposition1 = blockposition;

                for (int j = 0; j < i; ++j) {
                    blockposition1 = blockposition1.offset(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                    if (!world.isLoaded(blockposition1)) {
                        return;
                    }

                    IBlockData iblockdata = world.getBlockState(blockposition1);

                    if (iblockdata.isAir()) {
                        if (this.hasFlammableNeighbours(world, blockposition1)) {
                            world.setBlockAndUpdate(blockposition1, BlockFireAbstract.getState(world, blockposition1));
                            return;
                        }
                    } else if (iblockdata.getMaterial().blocksMotion()) {
                        return;
                    }
                }
            } else {
                for (int k = 0; k < 3; ++k) {
                    BlockPosition blockposition2 = blockposition.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);

                    if (!world.isLoaded(blockposition2)) {
                        return;
                    }

                    if (world.isEmptyBlock(blockposition2.above()) && this.isFlammable(world, blockposition2)) {
                        world.setBlockAndUpdate(blockposition2.above(), BlockFireAbstract.getState(world, blockposition2));
                    }
                }
            }

        }
    }

    private boolean hasFlammableNeighbours(IWorldReader iworldreader, BlockPosition blockposition) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (this.isFlammable(iworldreader, blockposition.relative(enumdirection))) {
                return true;
            }
        }

        return false;
    }

    private boolean isFlammable(IWorldReader iworldreader, BlockPosition blockposition) {
        return blockposition.getY() >= iworldreader.getMinBuildHeight() && blockposition.getY() < iworldreader.getMaxBuildHeight() && !iworldreader.hasChunkAt(blockposition) ? false : iworldreader.getBlockState(blockposition).getMaterial().isFlammable();
    }

    @Nullable
    @Override
    public ParticleParam getDripParticle() {
        return Particles.DRIPPING_LAVA;
    }

    @Override
    protected void beforeDestroyingBlock(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        this.fizz(generatoraccess, blockposition);
    }

    @Override
    public int getSlopeFindDistance(IWorldReader iworldreader) {
        return iworldreader.dimensionType().ultraWarm() ? 4 : 2;
    }

    @Override
    public IBlockData createLegacyBlock(Fluid fluid) {
        return (IBlockData) Blocks.LAVA.defaultBlockState().setValue(BlockFluids.LEVEL, getLegacyLevel(fluid));
    }

    @Override
    public boolean isSame(FluidType fluidtype) {
        return fluidtype == FluidTypes.LAVA || fluidtype == FluidTypes.FLOWING_LAVA;
    }

    @Override
    public int getDropOff(IWorldReader iworldreader) {
        return iworldreader.dimensionType().ultraWarm() ? 1 : 2;
    }

    @Override
    public boolean canBeReplacedWith(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition, FluidType fluidtype, EnumDirection enumdirection) {
        return fluid.getHeight(iblockaccess, blockposition) >= 0.44444445F && fluidtype.is(TagsFluid.WATER);
    }

    @Override
    public int getTickDelay(IWorldReader iworldreader) {
        return iworldreader.dimensionType().ultraWarm() ? 10 : 30;
    }

    @Override
    public int getSpreadDelay(World world, BlockPosition blockposition, Fluid fluid, Fluid fluid1) {
        int i = this.getTickDelay(world);

        if (!fluid.isEmpty() && !fluid1.isEmpty() && !(Boolean) fluid.getValue(FluidTypeLava.FALLING) && !(Boolean) fluid1.getValue(FluidTypeLava.FALLING) && fluid1.getHeight(world, blockposition) > fluid.getHeight(world, blockposition) && world.getRandom().nextInt(4) != 0) {
            i *= 4;
        }

        return i;
    }

    private void fizz(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        generatoraccess.levelEvent(1501, blockposition, 0);
    }

    @Override
    protected boolean canConvertToSource() {
        return false;
    }

    @Override
    protected void spreadTo(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection, Fluid fluid) {
        if (enumdirection == EnumDirection.DOWN) {
            Fluid fluid1 = generatoraccess.getFluidState(blockposition);

            if (this.is(TagsFluid.LAVA) && fluid1.is((Tag) TagsFluid.WATER)) {
                if (iblockdata.getBlock() instanceof BlockFluids) {
                    generatoraccess.setBlock(blockposition, Blocks.STONE.defaultBlockState(), 3);
                }

                this.fizz(generatoraccess, blockposition);
                return;
            }
        }

        super.spreadTo(generatoraccess, blockposition, iblockdata, enumdirection, fluid);
    }

    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    public Optional<SoundEffect> getPickupSound() {
        return Optional.of(SoundEffects.BUCKET_FILL_LAVA);
    }

    public static class a extends FluidTypeLava {

        public a() {}

        @Override
        protected void createFluidStateDefinition(BlockStateList.a<FluidType, Fluid> blockstatelist_a) {
            super.createFluidStateDefinition(blockstatelist_a);
            blockstatelist_a.add(FluidTypeLava.a.LEVEL);
        }

        @Override
        public int getAmount(Fluid fluid) {
            return (Integer) fluid.getValue(FluidTypeLava.a.LEVEL);
        }

        @Override
        public boolean isSource(Fluid fluid) {
            return false;
        }
    }

    public static class b extends FluidTypeLava {

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

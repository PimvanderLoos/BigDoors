package net.minecraft.world.level.material;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockFluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class FluidTypeWater extends FluidTypeFlowing {

    public FluidTypeWater() {}

    @Override
    public FluidType d() {
        return FluidTypes.FLOWING_WATER;
    }

    @Override
    public FluidType e() {
        return FluidTypes.WATER;
    }

    @Override
    public Item a() {
        return Items.WATER_BUCKET;
    }

    @Override
    protected boolean f() {
        return true;
    }

    @Override
    protected void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = iblockdata.getBlock().isTileEntity() ? generatoraccess.getTileEntity(blockposition) : null;

        Block.a(iblockdata, generatoraccess, blockposition, tileentity);
    }

    @Override
    public int b(IWorldReader iworldreader) {
        return 4;
    }

    @Override
    public IBlockData b(Fluid fluid) {
        return (IBlockData) Blocks.WATER.getBlockData().set(BlockFluids.LEVEL, e(fluid));
    }

    @Override
    public boolean a(FluidType fluidtype) {
        return fluidtype == FluidTypes.WATER || fluidtype == FluidTypes.FLOWING_WATER;
    }

    @Override
    public int c(IWorldReader iworldreader) {
        return 1;
    }

    @Override
    public int a(IWorldReader iworldreader) {
        return 5;
    }

    @Override
    public boolean a(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition, FluidType fluidtype, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN && !fluidtype.a((Tag) TagsFluid.WATER);
    }

    @Override
    protected float c() {
        return 100.0F;
    }

    public static class a extends FluidTypeWater {

        public a() {}

        @Override
        protected void a(BlockStateList.a<FluidType, Fluid> blockstatelist_a) {
            super.a(blockstatelist_a);
            blockstatelist_a.a(FluidTypeWater.a.LEVEL);
        }

        @Override
        public int d(Fluid fluid) {
            return (Integer) fluid.get(FluidTypeWater.a.LEVEL);
        }

        @Override
        public boolean c(Fluid fluid) {
            return false;
        }
    }

    public static class b extends FluidTypeWater {

        public b() {}

        @Override
        public int d(Fluid fluid) {
            return 8;
        }

        @Override
        public boolean c(Fluid fluid) {
            return true;
        }
    }
}

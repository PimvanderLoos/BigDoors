package net.minecraft.world.level.block;

import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;

public class LayeredCauldronBlock extends AbstractCauldronBlock {

    public static final int MIN_FILL_LEVEL = 1;
    public static final int MAX_FILL_LEVEL = 3;
    public static final BlockStateInteger LEVEL = BlockProperties.LEVEL_CAULDRON;
    private static final int BASE_CONTENT_HEIGHT = 6;
    private static final double HEIGHT_PER_LEVEL = 3.0D;
    public static final Predicate<BiomeBase.Precipitation> RAIN = (biomebase_precipitation) -> {
        return biomebase_precipitation == BiomeBase.Precipitation.RAIN;
    };
    public static final Predicate<BiomeBase.Precipitation> SNOW = (biomebase_precipitation) -> {
        return biomebase_precipitation == BiomeBase.Precipitation.SNOW;
    };
    private final Predicate<BiomeBase.Precipitation> fillPredicate;

    public LayeredCauldronBlock(BlockBase.Info blockbase_info, Predicate<BiomeBase.Precipitation> predicate, Map<Item, CauldronInteraction> map) {
        super(blockbase_info, map);
        this.fillPredicate = predicate;
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(LayeredCauldronBlock.LEVEL, 1));
    }

    @Override
    public boolean c(IBlockData iblockdata) {
        return (Integer) iblockdata.get(LayeredCauldronBlock.LEVEL) == 3;
    }

    @Override
    protected boolean a(FluidType fluidtype) {
        return fluidtype == FluidTypes.WATER && this.fillPredicate == LayeredCauldronBlock.RAIN;
    }

    @Override
    protected double a(IBlockData iblockdata) {
        return (6.0D + (double) (Integer) iblockdata.get(LayeredCauldronBlock.LEVEL) * 3.0D) / 16.0D;
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide && entity.isBurning() && this.a(iblockdata, blockposition, entity)) {
            entity.extinguish();
            if (entity.a(world, blockposition)) {
                this.d(iblockdata, world, blockposition);
            }
        }

    }

    protected void d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        e(iblockdata, world, blockposition);
    }

    public static void e(IBlockData iblockdata, World world, BlockPosition blockposition) {
        int i = (Integer) iblockdata.get(LayeredCauldronBlock.LEVEL) - 1;

        world.setTypeUpdate(blockposition, i == 0 ? Blocks.CAULDRON.getBlockData() : (IBlockData) iblockdata.set(LayeredCauldronBlock.LEVEL, i));
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, BiomeBase.Precipitation biomebase_precipitation) {
        if (BlockCauldron.a(world, biomebase_precipitation) && (Integer) iblockdata.get(LayeredCauldronBlock.LEVEL) != 3 && this.fillPredicate.test(biomebase_precipitation)) {
            world.setTypeUpdate(blockposition, (IBlockData) iblockdata.a((IBlockState) LayeredCauldronBlock.LEVEL));
        }
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (Integer) iblockdata.get(LayeredCauldronBlock.LEVEL);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(LayeredCauldronBlock.LEVEL);
    }

    @Override
    protected void a(IBlockData iblockdata, World world, BlockPosition blockposition, FluidType fluidtype) {
        if (!this.c(iblockdata)) {
            world.setTypeUpdate(blockposition, (IBlockData) iblockdata.set(LayeredCauldronBlock.LEVEL, (Integer) iblockdata.get(LayeredCauldronBlock.LEVEL) + 1));
            world.triggerEffect(1047, blockposition, 0);
        }
    }
}

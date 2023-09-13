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
import net.minecraft.world.level.gameevent.GameEvent;
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
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(LayeredCauldronBlock.LEVEL, 1));
    }

    @Override
    public boolean isFull(IBlockData iblockdata) {
        return (Integer) iblockdata.getValue(LayeredCauldronBlock.LEVEL) == 3;
    }

    @Override
    protected boolean canReceiveStalactiteDrip(FluidType fluidtype) {
        return fluidtype == FluidTypes.WATER && this.fillPredicate == LayeredCauldronBlock.RAIN;
    }

    @Override
    protected double getContentHeight(IBlockData iblockdata) {
        return (6.0D + (double) (Integer) iblockdata.getValue(LayeredCauldronBlock.LEVEL) * 3.0D) / 16.0D;
    }

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide && entity.isOnFire() && this.isEntityInsideContent(iblockdata, blockposition, entity)) {
            entity.clearFire();
            if (entity.mayInteract(world, blockposition)) {
                this.handleEntityOnFireInside(iblockdata, world, blockposition);
            }
        }

    }

    protected void handleEntityOnFireInside(IBlockData iblockdata, World world, BlockPosition blockposition) {
        lowerFillLevel(iblockdata, world, blockposition);
    }

    public static void lowerFillLevel(IBlockData iblockdata, World world, BlockPosition blockposition) {
        int i = (Integer) iblockdata.getValue(LayeredCauldronBlock.LEVEL) - 1;
        IBlockData iblockdata1 = i == 0 ? Blocks.CAULDRON.defaultBlockState() : (IBlockData) iblockdata.setValue(LayeredCauldronBlock.LEVEL, i);

        world.setBlockAndUpdate(blockposition, iblockdata1);
        world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(iblockdata1));
    }

    @Override
    public void handlePrecipitation(IBlockData iblockdata, World world, BlockPosition blockposition, BiomeBase.Precipitation biomebase_precipitation) {
        if (BlockCauldron.shouldHandlePrecipitation(world, biomebase_precipitation) && (Integer) iblockdata.getValue(LayeredCauldronBlock.LEVEL) != 3 && this.fillPredicate.test(biomebase_precipitation)) {
            IBlockData iblockdata1 = (IBlockData) iblockdata.cycle(LayeredCauldronBlock.LEVEL);

            world.setBlockAndUpdate(blockposition, iblockdata1);
            world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(iblockdata1));
        }
    }

    @Override
    public int getAnalogOutputSignal(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (Integer) iblockdata.getValue(LayeredCauldronBlock.LEVEL);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(LayeredCauldronBlock.LEVEL);
    }

    @Override
    protected void receiveStalactiteDrip(IBlockData iblockdata, World world, BlockPosition blockposition, FluidType fluidtype) {
        if (!this.isFull(iblockdata)) {
            IBlockData iblockdata1 = (IBlockData) iblockdata.setValue(LayeredCauldronBlock.LEVEL, (Integer) iblockdata.getValue(LayeredCauldronBlock.LEVEL) + 1);

            world.setBlockAndUpdate(blockposition, iblockdata1);
            world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(iblockdata1));
            world.levelEvent(1047, blockposition, 0);
        }
    }
}

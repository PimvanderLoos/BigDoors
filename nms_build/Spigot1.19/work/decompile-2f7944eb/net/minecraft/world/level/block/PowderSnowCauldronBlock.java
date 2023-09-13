package net.minecraft.world.level.block;

import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class PowderSnowCauldronBlock extends LayeredCauldronBlock {

    public PowderSnowCauldronBlock(BlockBase.Info blockbase_info, Predicate<BiomeBase.Precipitation> predicate, Map<Item, CauldronInteraction> map) {
        super(blockbase_info, predicate, map);
    }

    @Override
    protected void handleEntityOnFireInside(IBlockData iblockdata, World world, BlockPosition blockposition) {
        lowerFillLevel((IBlockData) Blocks.WATER_CAULDRON.defaultBlockState().setValue(PowderSnowCauldronBlock.LEVEL, (Integer) iblockdata.getValue(PowderSnowCauldronBlock.LEVEL)), world, blockposition);
    }
}

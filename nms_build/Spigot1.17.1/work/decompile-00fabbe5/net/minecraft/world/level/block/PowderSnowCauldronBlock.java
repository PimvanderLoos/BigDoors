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
    protected void d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        e((IBlockData) Blocks.WATER_CAULDRON.getBlockData().set(PowderSnowCauldronBlock.LEVEL, (Integer) iblockdata.get(PowderSnowCauldronBlock.LEVEL)), world, blockposition);
    }
}

package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockRotatable;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureStateProviderRotatedBlock extends WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProviderRotatedBlock> CODEC = IBlockData.CODEC.fieldOf("state").xmap(BlockBase.BlockData::getBlock, Block::defaultBlockState).xmap(WorldGenFeatureStateProviderRotatedBlock::new, (worldgenfeaturestateproviderrotatedblock) -> {
        return worldgenfeaturestateproviderrotatedblock.block;
    }).codec();
    private final Block block;

    public WorldGenFeatureStateProviderRotatedBlock(Block block) {
        this.block = block;
    }

    @Override
    protected WorldGenFeatureStateProviders<?> type() {
        return WorldGenFeatureStateProviders.ROTATED_BLOCK_PROVIDER;
    }

    @Override
    public IBlockData getState(RandomSource randomsource, BlockPosition blockposition) {
        EnumDirection.EnumAxis enumdirection_enumaxis = EnumDirection.EnumAxis.getRandom(randomsource);

        return (IBlockData) this.block.defaultBlockState().setValue(BlockRotatable.AXIS, enumdirection_enumaxis);
    }
}

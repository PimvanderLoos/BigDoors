package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockRotatable;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureStateProviderRotatedBlock extends WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProviderRotatedBlock> CODEC = IBlockData.CODEC.fieldOf("state").xmap(BlockBase.BlockData::getBlock, Block::getBlockData).xmap(WorldGenFeatureStateProviderRotatedBlock::new, (worldgenfeaturestateproviderrotatedblock) -> {
        return worldgenfeaturestateproviderrotatedblock.block;
    }).codec();
    private final Block block;

    public WorldGenFeatureStateProviderRotatedBlock(Block block) {
        this.block = block;
    }

    @Override
    protected WorldGenFeatureStateProviders<?> a() {
        return WorldGenFeatureStateProviders.ROTATED_BLOCK_PROVIDER;
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        EnumDirection.EnumAxis enumdirection_enumaxis = EnumDirection.EnumAxis.a(random);

        return (IBlockData) this.block.getBlockData().set(BlockRotatable.AXIS, enumdirection_enumaxis);
    }
}

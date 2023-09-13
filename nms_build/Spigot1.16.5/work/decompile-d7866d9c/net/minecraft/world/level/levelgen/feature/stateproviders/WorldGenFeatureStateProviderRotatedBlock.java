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

    public static final Codec<WorldGenFeatureStateProviderRotatedBlock> b = IBlockData.b.fieldOf("state").xmap(BlockBase.BlockData::getBlock, Block::getBlockData).xmap(WorldGenFeatureStateProviderRotatedBlock::new, (worldgenfeaturestateproviderrotatedblock) -> {
        return worldgenfeaturestateproviderrotatedblock.c;
    }).codec();
    private final Block c;

    public WorldGenFeatureStateProviderRotatedBlock(Block block) {
        this.c = block;
    }

    @Override
    protected WorldGenFeatureStateProviders<?> a() {
        return WorldGenFeatureStateProviders.e;
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        EnumDirection.EnumAxis enumdirection_enumaxis = EnumDirection.EnumAxis.a(random);

        return (IBlockData) this.c.getBlockData().set(BlockRotatable.AXIS, enumdirection_enumaxis);
    }
}

package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public class IBlockData extends BlockBase.BlockData {

    public static final Codec<IBlockData> b = a((Codec) IRegistry.BLOCK, Block::getBlockData).stable();

    public IBlockData(Block block, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap, MapCodec<IBlockData> mapcodec) {
        super(block, immutablemap, mapcodec);
    }

    @Override
    protected IBlockData p() {
        return this;
    }
}

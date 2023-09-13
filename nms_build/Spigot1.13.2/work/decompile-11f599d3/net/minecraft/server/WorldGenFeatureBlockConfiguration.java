package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;

public class WorldGenFeatureBlockConfiguration implements WorldGenFeatureConfiguration {

    final IBlockData a;
    final List<IBlockData> b;
    final List<IBlockData> c;
    final List<IBlockData> d;

    public WorldGenFeatureBlockConfiguration(IBlockData iblockdata, IBlockData[] aiblockdata, IBlockData[] aiblockdata1, IBlockData[] aiblockdata2) {
        this.a = iblockdata;
        this.b = Lists.newArrayList(aiblockdata);
        this.c = Lists.newArrayList(aiblockdata1);
        this.d = Lists.newArrayList(aiblockdata2);
    }
}

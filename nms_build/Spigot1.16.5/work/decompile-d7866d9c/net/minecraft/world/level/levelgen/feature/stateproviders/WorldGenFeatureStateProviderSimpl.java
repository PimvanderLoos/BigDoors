package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureStateProviderSimpl extends WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProviderSimpl> b = IBlockData.b.fieldOf("state").xmap(WorldGenFeatureStateProviderSimpl::new, (worldgenfeaturestateprovidersimpl) -> {
        return worldgenfeaturestateprovidersimpl.c;
    }).codec();
    private final IBlockData c;

    public WorldGenFeatureStateProviderSimpl(IBlockData iblockdata) {
        this.c = iblockdata;
    }

    @Override
    protected WorldGenFeatureStateProviders<?> a() {
        return WorldGenFeatureStateProviders.a;
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        return this.c;
    }
}

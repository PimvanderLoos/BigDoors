package net.minecraft.world.level.levelgen;

import net.minecraft.util.MathHelper;
import net.minecraft.world.level.block.state.IBlockData;

public class DepthBasedReplacingBaseStoneSource implements BaseStoneSource {

    private static final int ALWAYS_REPLACE_BELOW_Y = -8;
    private static final int NEVER_REPLACE_ABOVE_Y = 0;
    private final SeededRandom random;
    private final long seed;
    private final IBlockData normalBlock;
    private final IBlockData replacementBlock;
    private final GeneratorSettingBase settings;

    public DepthBasedReplacingBaseStoneSource(long i, IBlockData iblockdata, IBlockData iblockdata1, GeneratorSettingBase generatorsettingbase) {
        this.random = new SeededRandom(i);
        this.seed = i;
        this.normalBlock = iblockdata;
        this.replacementBlock = iblockdata1;
        this.settings = generatorsettingbase;
    }

    @Override
    public IBlockData getBaseBlock(int i, int j, int k) {
        if (!this.settings.l()) {
            return this.normalBlock;
        } else if (j < -8) {
            return this.replacementBlock;
        } else if (j > 0) {
            return this.normalBlock;
        } else {
            double d0 = MathHelper.b((double) j, -8.0D, 0.0D, 1.0D, 0.0D);

            this.random.a(this.seed, i, j, k);
            return (double) this.random.nextFloat() < d0 ? this.replacementBlock : this.normalBlock;
        }
    }
}

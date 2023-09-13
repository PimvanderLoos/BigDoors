package net.minecraft.world.level.levelgen.material;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;

public record MaterialRuleList(List<NoiseChunk.c> materialRuleList) implements NoiseChunk.c {

    @Nullable
    @Override
    public IBlockData calculate(DensityFunction.b densityfunction_b) {
        Iterator iterator = this.materialRuleList.iterator();

        IBlockData iblockdata;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            NoiseChunk.c noisechunk_c = (NoiseChunk.c) iterator.next();

            iblockdata = noisechunk_c.calculate(densityfunction_b);
        } while (iblockdata == null);

        return iblockdata;
    }
}

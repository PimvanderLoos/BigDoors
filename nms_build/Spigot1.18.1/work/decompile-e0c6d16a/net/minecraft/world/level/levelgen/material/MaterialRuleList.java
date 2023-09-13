package net.minecraft.world.level.levelgen.material;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.NoiseChunk;

public class MaterialRuleList implements WorldGenMaterialRule {

    private final List<WorldGenMaterialRule> materialRuleList;

    public MaterialRuleList(List<WorldGenMaterialRule> list) {
        this.materialRuleList = list;
    }

    @Nullable
    @Override
    public IBlockData apply(NoiseChunk noisechunk, int i, int j, int k) {
        Iterator iterator = this.materialRuleList.iterator();

        IBlockData iblockdata;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            WorldGenMaterialRule worldgenmaterialrule = (WorldGenMaterialRule) iterator.next();

            iblockdata = worldgenmaterialrule.apply(noisechunk, i, j, k);
        } while (iblockdata == null);

        return iblockdata;
    }
}

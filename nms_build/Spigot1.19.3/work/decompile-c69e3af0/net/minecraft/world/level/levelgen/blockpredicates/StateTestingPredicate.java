package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.datafixers.Products.P1;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class StateTestingPredicate implements BlockPredicate {

    protected final BaseBlockPosition offset;

    protected static <P extends StateTestingPredicate> P1<Mu<P>, BaseBlockPosition> stateTestingCodec(Instance<P> instance) {
        return instance.group(BaseBlockPosition.offsetCodec(16).optionalFieldOf("offset", BaseBlockPosition.ZERO).forGetter((statetestingpredicate) -> {
            return statetestingpredicate.offset;
        }));
    }

    protected StateTestingPredicate(BaseBlockPosition baseblockposition) {
        this.offset = baseblockposition;
    }

    public final boolean test(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition) {
        return this.test(generatoraccessseed.getBlockState(blockposition.offset(this.offset)));
    }

    protected abstract boolean test(IBlockData iblockdata);
}

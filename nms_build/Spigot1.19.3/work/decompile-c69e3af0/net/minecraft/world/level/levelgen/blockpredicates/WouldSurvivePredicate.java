package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;

public class WouldSurvivePredicate implements BlockPredicate {

    public static final Codec<WouldSurvivePredicate> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BaseBlockPosition.offsetCodec(16).optionalFieldOf("offset", BaseBlockPosition.ZERO).forGetter((wouldsurvivepredicate) -> {
            return wouldsurvivepredicate.offset;
        }), IBlockData.CODEC.fieldOf("state").forGetter((wouldsurvivepredicate) -> {
            return wouldsurvivepredicate.state;
        })).apply(instance, WouldSurvivePredicate::new);
    });
    private final BaseBlockPosition offset;
    private final IBlockData state;

    protected WouldSurvivePredicate(BaseBlockPosition baseblockposition, IBlockData iblockdata) {
        this.offset = baseblockposition;
        this.state = iblockdata;
    }

    public boolean test(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition) {
        return this.state.canSurvive(generatoraccessseed, blockposition.offset(this.offset));
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.WOULD_SURVIVE;
    }
}

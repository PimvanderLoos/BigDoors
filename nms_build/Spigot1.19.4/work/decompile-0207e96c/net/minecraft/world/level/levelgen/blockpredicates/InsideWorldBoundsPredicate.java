package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;

public class InsideWorldBoundsPredicate implements BlockPredicate {

    public static final Codec<InsideWorldBoundsPredicate> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BaseBlockPosition.offsetCodec(16).optionalFieldOf("offset", BlockPosition.ZERO).forGetter((insideworldboundspredicate) -> {
            return insideworldboundspredicate.offset;
        })).apply(instance, InsideWorldBoundsPredicate::new);
    });
    private final BaseBlockPosition offset;

    public InsideWorldBoundsPredicate(BaseBlockPosition baseblockposition) {
        this.offset = baseblockposition;
    }

    public boolean test(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition) {
        return !generatoraccessseed.isOutsideBuildHeight(blockposition.offset(this.offset));
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.INSIDE_WORLD_BOUNDS;
    }
}

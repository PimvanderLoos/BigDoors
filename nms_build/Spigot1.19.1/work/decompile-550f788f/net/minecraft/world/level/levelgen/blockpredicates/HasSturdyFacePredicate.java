package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccessSeed;

public class HasSturdyFacePredicate implements BlockPredicate {

    private final BaseBlockPosition offset;
    private final EnumDirection direction;
    public static final Codec<HasSturdyFacePredicate> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BaseBlockPosition.offsetCodec(16).optionalFieldOf("offset", BaseBlockPosition.ZERO).forGetter((hassturdyfacepredicate) -> {
            return hassturdyfacepredicate.offset;
        }), EnumDirection.CODEC.fieldOf("direction").forGetter((hassturdyfacepredicate) -> {
            return hassturdyfacepredicate.direction;
        })).apply(instance, HasSturdyFacePredicate::new);
    });

    public HasSturdyFacePredicate(BaseBlockPosition baseblockposition, EnumDirection enumdirection) {
        this.offset = baseblockposition;
        this.direction = enumdirection;
    }

    public boolean test(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.offset(this.offset);

        return generatoraccessseed.getBlockState(blockposition1).isFaceSturdy(generatoraccessseed, blockposition1, this.direction);
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.HAS_STURDY_FACE;
    }
}

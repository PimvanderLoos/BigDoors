package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;

class AnyOfPredicate extends CombiningPredicate {

    public static final Codec<AnyOfPredicate> CODEC = codec(AnyOfPredicate::new);

    public AnyOfPredicate(List<BlockPredicate> list) {
        super(list);
    }

    public boolean test(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition) {
        Iterator iterator = this.predicates.iterator();

        BlockPredicate blockpredicate;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            blockpredicate = (BlockPredicate) iterator.next();
        } while (!blockpredicate.test(generatoraccessseed, blockposition));

        return true;
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.ANY_OF;
    }
}

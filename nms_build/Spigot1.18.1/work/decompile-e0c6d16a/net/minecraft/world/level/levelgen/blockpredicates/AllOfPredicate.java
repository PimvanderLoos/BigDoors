package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;

class AllOfPredicate extends CombiningPredicate {

    public static final Codec<AllOfPredicate> CODEC = codec(AllOfPredicate::new);

    public AllOfPredicate(List<BlockPredicate> list) {
        super(list);
    }

    public boolean test(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition) {
        Iterator iterator = this.predicates.iterator();

        BlockPredicate blockpredicate;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            blockpredicate = (BlockPredicate) iterator.next();
        } while (blockpredicate.test(generatoraccessseed, blockposition));

        return false;
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.ALL_OF;
    }
}

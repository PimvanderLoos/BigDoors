package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.world.level.block.state.IBlockData;

public class SolidPredicate extends StateTestingPredicate {

    public static final Codec<SolidPredicate> CODEC = RecordCodecBuilder.create((instance) -> {
        return stateTestingCodec(instance).apply(instance, SolidPredicate::new);
    });

    public SolidPredicate(BaseBlockPosition baseblockposition) {
        super(baseblockposition);
    }

    @Override
    protected boolean test(IBlockData iblockdata) {
        return iblockdata.getMaterial().isSolid();
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.SOLID;
    }
}

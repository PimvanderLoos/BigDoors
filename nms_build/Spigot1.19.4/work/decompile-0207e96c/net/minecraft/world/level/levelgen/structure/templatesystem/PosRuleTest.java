package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;

public abstract class PosRuleTest {

    public static final Codec<PosRuleTest> CODEC = BuiltInRegistries.POS_RULE_TEST.byNameCodec().dispatch("predicate_type", PosRuleTest::getType, PosRuleTestType::codec);

    public PosRuleTest() {}

    public abstract boolean test(BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, RandomSource randomsource);

    protected abstract PosRuleTestType<?> getType();
}

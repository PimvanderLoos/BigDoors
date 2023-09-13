package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public abstract class PosRuleTest {

    public static final Codec<PosRuleTest> c = IRegistry.POS_RULE_TEST.dispatch("predicate_type", PosRuleTest::a, PosRuleTestType::codec);

    public PosRuleTest() {}

    public abstract boolean a(BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, Random random);

    protected abstract PosRuleTestType<?> a();
}

package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;

public abstract class PosRuleTest {

    public static final Codec<PosRuleTest> c = IRegistry.POS_RULE_TEST.dispatch("predicate_type", PosRuleTest::a, PosRuleTestType::codec);

    public PosRuleTest() {}

    public abstract boolean a(BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, Random random);

    protected abstract PosRuleTestType<?> a();
}

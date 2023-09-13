package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public interface PosRuleTestType<P extends PosRuleTest> {

    PosRuleTestType<PosRuleTestTrue> ALWAYS_TRUE_TEST = a("always_true", PosRuleTestTrue.CODEC);
    PosRuleTestType<PosRuleTestLinear> LINEAR_POS_TEST = a("linear_pos", PosRuleTestLinear.CODEC);
    PosRuleTestType<PosRuleTestAxisAlignedLinear> AXIS_ALIGNED_LINEAR_POS_TEST = a("axis_aligned_linear_pos", PosRuleTestAxisAlignedLinear.CODEC);

    Codec<P> codec();

    static <P extends PosRuleTest> PosRuleTestType<P> a(String s, Codec<P> codec) {
        return (PosRuleTestType) IRegistry.a(IRegistry.POS_RULE_TEST, s, (Object) (() -> {
            return codec;
        }));
    }
}

package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public interface PosRuleTestType<P extends PosRuleTest> {

    PosRuleTestType<PosRuleTestTrue> a = a("always_true", PosRuleTestTrue.a);
    PosRuleTestType<PosRuleTestLinear> b = a("linear_pos", PosRuleTestLinear.a);
    PosRuleTestType<PosRuleTestAxisAlignedLinear> c = a("axis_aligned_linear_pos", PosRuleTestAxisAlignedLinear.a);

    Codec<P> codec();

    static <P extends PosRuleTest> PosRuleTestType<P> a(String s, Codec<P> codec) {
        return (PosRuleTestType) IRegistry.a(IRegistry.POS_RULE_TEST, s, (Object) (() -> {
            return codec;
        }));
    }
}

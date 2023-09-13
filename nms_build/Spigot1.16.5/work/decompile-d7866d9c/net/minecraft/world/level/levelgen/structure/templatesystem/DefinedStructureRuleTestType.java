package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public interface DefinedStructureRuleTestType<P extends DefinedStructureRuleTest> {

    DefinedStructureRuleTestType<DefinedStructureTestTrue> a = a("always_true", DefinedStructureTestTrue.a);
    DefinedStructureRuleTestType<DefinedStructureTestBlock> b = a("block_match", DefinedStructureTestBlock.a);
    DefinedStructureRuleTestType<DefinedStructureTestBlockState> c = a("blockstate_match", DefinedStructureTestBlockState.a);
    DefinedStructureRuleTestType<DefinedStructureTestTag> d = a("tag_match", DefinedStructureTestTag.a);
    DefinedStructureRuleTestType<DefinedStructureTestRandomBlock> e = a("random_block_match", DefinedStructureTestRandomBlock.a);
    DefinedStructureRuleTestType<DefinedStructureTestRandomBlockState> f = a("random_blockstate_match", DefinedStructureTestRandomBlockState.a);

    Codec<P> codec();

    static <P extends DefinedStructureRuleTest> DefinedStructureRuleTestType<P> a(String s, Codec<P> codec) {
        return (DefinedStructureRuleTestType) IRegistry.a(IRegistry.RULE_TEST, s, (Object) (() -> {
            return codec;
        }));
    }
}

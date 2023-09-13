package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public interface DefinedStructureRuleTestType<P extends DefinedStructureRuleTest> {

    DefinedStructureRuleTestType<DefinedStructureTestTrue> ALWAYS_TRUE_TEST = a("always_true", DefinedStructureTestTrue.CODEC);
    DefinedStructureRuleTestType<DefinedStructureTestBlock> BLOCK_TEST = a("block_match", DefinedStructureTestBlock.CODEC);
    DefinedStructureRuleTestType<DefinedStructureTestBlockState> BLOCKSTATE_TEST = a("blockstate_match", DefinedStructureTestBlockState.CODEC);
    DefinedStructureRuleTestType<DefinedStructureTestTag> TAG_TEST = a("tag_match", DefinedStructureTestTag.CODEC);
    DefinedStructureRuleTestType<DefinedStructureTestRandomBlock> RANDOM_BLOCK_TEST = a("random_block_match", DefinedStructureTestRandomBlock.CODEC);
    DefinedStructureRuleTestType<DefinedStructureTestRandomBlockState> RANDOM_BLOCKSTATE_TEST = a("random_blockstate_match", DefinedStructureTestRandomBlockState.CODEC);

    Codec<P> codec();

    static <P extends DefinedStructureRuleTest> DefinedStructureRuleTestType<P> a(String s, Codec<P> codec) {
        return (DefinedStructureRuleTestType) IRegistry.a(IRegistry.RULE_TEST, s, (Object) (() -> {
            return codec;
        }));
    }
}

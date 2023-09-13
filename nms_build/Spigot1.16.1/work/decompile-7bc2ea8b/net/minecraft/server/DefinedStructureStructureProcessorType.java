package net.minecraft.server;

import com.mojang.serialization.Codec;

public interface DefinedStructureStructureProcessorType<P extends DefinedStructureProcessor> {

    DefinedStructureStructureProcessorType<DefinedStructureProcessorBlockIgnore> a = a("block_ignore", DefinedStructureProcessorBlockIgnore.a);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorRotation> b = a("block_rot", DefinedStructureProcessorRotation.a);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorGravity> c = a("gravity", DefinedStructureProcessorGravity.a);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorJigsawReplacement> d = a("jigsaw_replacement", DefinedStructureProcessorJigsawReplacement.a);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorRule> e = a("rule", DefinedStructureProcessorRule.a);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorNop> f = a("nop", DefinedStructureProcessorNop.a);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorBlockAge> g = a("block_age", DefinedStructureProcessorBlockAge.a);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorBlackstoneReplace> h = a("blackstone_replace", DefinedStructureProcessorBlackstoneReplace.a);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorLavaSubmergedBlock> i = a("lava_submerged_block", DefinedStructureProcessorLavaSubmergedBlock.a);
    Codec<DefinedStructureProcessor> j = IRegistry.STRUCTURE_PROCESSOR.dispatch("processor_type", DefinedStructureProcessor::a, DefinedStructureStructureProcessorType::codec);

    Codec<P> codec();

    static <P extends DefinedStructureProcessor> DefinedStructureStructureProcessorType<P> a(String s, Codec<P> codec) {
        return (DefinedStructureStructureProcessorType) IRegistry.a(IRegistry.STRUCTURE_PROCESSOR, s, (Object) (() -> {
            return codec;
        }));
    }
}

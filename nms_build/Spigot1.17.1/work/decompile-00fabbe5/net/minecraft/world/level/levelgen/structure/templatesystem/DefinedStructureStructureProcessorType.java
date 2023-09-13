package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryFileCodec;

public interface DefinedStructureStructureProcessorType<P extends DefinedStructureProcessor> {

    DefinedStructureStructureProcessorType<DefinedStructureProcessorBlockIgnore> BLOCK_IGNORE = a("block_ignore", DefinedStructureProcessorBlockIgnore.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorRotation> BLOCK_ROT = a("block_rot", DefinedStructureProcessorRotation.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorGravity> GRAVITY = a("gravity", DefinedStructureProcessorGravity.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorJigsawReplacement> JIGSAW_REPLACEMENT = a("jigsaw_replacement", DefinedStructureProcessorJigsawReplacement.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorRule> RULE = a("rule", DefinedStructureProcessorRule.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorNop> NOP = a("nop", DefinedStructureProcessorNop.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorBlockAge> BLOCK_AGE = a("block_age", DefinedStructureProcessorBlockAge.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorBlackstoneReplace> BLACKSTONE_REPLACE = a("blackstone_replace", DefinedStructureProcessorBlackstoneReplace.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorLavaSubmergedBlock> LAVA_SUBMERGED_BLOCK = a("lava_submerged_block", DefinedStructureProcessorLavaSubmergedBlock.CODEC);
    DefinedStructureStructureProcessorType<ProtectedBlockProcessor> PROTECTED_BLOCKS = a("protected_blocks", ProtectedBlockProcessor.CODEC);
    Codec<DefinedStructureProcessor> SINGLE_CODEC = IRegistry.STRUCTURE_PROCESSOR.dispatch("processor_type", DefinedStructureProcessor::a, DefinedStructureStructureProcessorType::codec);
    Codec<ProcessorList> LIST_OBJECT_CODEC = DefinedStructureStructureProcessorType.SINGLE_CODEC.listOf().xmap(ProcessorList::new, ProcessorList::a);
    Codec<ProcessorList> DIRECT_CODEC = Codec.either(DefinedStructureStructureProcessorType.LIST_OBJECT_CODEC.fieldOf("processors").codec(), DefinedStructureStructureProcessorType.LIST_OBJECT_CODEC).xmap((either) -> {
        return (ProcessorList) either.map((processorlist) -> {
            return processorlist;
        }, (processorlist) -> {
            return processorlist;
        });
    }, Either::left);
    Codec<Supplier<ProcessorList>> LIST_CODEC = RegistryFileCodec.a(IRegistry.PROCESSOR_LIST_REGISTRY, DefinedStructureStructureProcessorType.DIRECT_CODEC);

    Codec<P> codec();

    static <P extends DefinedStructureProcessor> DefinedStructureStructureProcessorType<P> a(String s, Codec<P> codec) {
        return (DefinedStructureStructureProcessorType) IRegistry.a(IRegistry.STRUCTURE_PROCESSOR, s, (Object) (() -> {
            return codec;
        }));
    }
}

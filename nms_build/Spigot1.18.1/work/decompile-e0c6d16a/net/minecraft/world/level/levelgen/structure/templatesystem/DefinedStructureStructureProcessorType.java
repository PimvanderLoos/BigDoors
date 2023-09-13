package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryFileCodec;

public interface DefinedStructureStructureProcessorType<P extends DefinedStructureProcessor> {

    DefinedStructureStructureProcessorType<DefinedStructureProcessorBlockIgnore> BLOCK_IGNORE = register("block_ignore", DefinedStructureProcessorBlockIgnore.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorRotation> BLOCK_ROT = register("block_rot", DefinedStructureProcessorRotation.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorGravity> GRAVITY = register("gravity", DefinedStructureProcessorGravity.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorJigsawReplacement> JIGSAW_REPLACEMENT = register("jigsaw_replacement", DefinedStructureProcessorJigsawReplacement.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorRule> RULE = register("rule", DefinedStructureProcessorRule.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorNop> NOP = register("nop", DefinedStructureProcessorNop.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorBlockAge> BLOCK_AGE = register("block_age", DefinedStructureProcessorBlockAge.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorBlackstoneReplace> BLACKSTONE_REPLACE = register("blackstone_replace", DefinedStructureProcessorBlackstoneReplace.CODEC);
    DefinedStructureStructureProcessorType<DefinedStructureProcessorLavaSubmergedBlock> LAVA_SUBMERGED_BLOCK = register("lava_submerged_block", DefinedStructureProcessorLavaSubmergedBlock.CODEC);
    DefinedStructureStructureProcessorType<ProtectedBlockProcessor> PROTECTED_BLOCKS = register("protected_blocks", ProtectedBlockProcessor.CODEC);
    Codec<DefinedStructureProcessor> SINGLE_CODEC = IRegistry.STRUCTURE_PROCESSOR.byNameCodec().dispatch("processor_type", DefinedStructureProcessor::getType, DefinedStructureStructureProcessorType::codec);
    Codec<ProcessorList> LIST_OBJECT_CODEC = DefinedStructureStructureProcessorType.SINGLE_CODEC.listOf().xmap(ProcessorList::new, ProcessorList::list);
    Codec<ProcessorList> DIRECT_CODEC = Codec.either(DefinedStructureStructureProcessorType.LIST_OBJECT_CODEC.fieldOf("processors").codec(), DefinedStructureStructureProcessorType.LIST_OBJECT_CODEC).xmap((either) -> {
        return (ProcessorList) either.map((processorlist) -> {
            return processorlist;
        }, (processorlist) -> {
            return processorlist;
        });
    }, Either::left);
    Codec<Supplier<ProcessorList>> LIST_CODEC = RegistryFileCodec.create(IRegistry.PROCESSOR_LIST_REGISTRY, DefinedStructureStructureProcessorType.DIRECT_CODEC);

    Codec<P> codec();

    static <P extends DefinedStructureProcessor> DefinedStructureStructureProcessorType<P> register(String s, Codec<P> codec) {
        return (DefinedStructureStructureProcessorType) IRegistry.register(IRegistry.STRUCTURE_PROCESSOR, s, () -> {
            return codec;
        });
    }
}

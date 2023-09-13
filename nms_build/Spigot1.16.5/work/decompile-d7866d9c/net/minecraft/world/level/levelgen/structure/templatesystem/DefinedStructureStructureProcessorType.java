package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryFileCodec;

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
    Codec<ProcessorList> k = DefinedStructureStructureProcessorType.j.listOf().xmap(ProcessorList::new, ProcessorList::a);
    Codec<ProcessorList> l = Codec.either(DefinedStructureStructureProcessorType.k.fieldOf("processors").codec(), DefinedStructureStructureProcessorType.k).xmap((either) -> {
        return (ProcessorList) either.map((processorlist) -> {
            return processorlist;
        }, (processorlist) -> {
            return processorlist;
        });
    }, Either::left);
    Codec<Supplier<ProcessorList>> m = RegistryFileCodec.a(IRegistry.aw, DefinedStructureStructureProcessorType.l);

    Codec<P> codec();

    static <P extends DefinedStructureProcessor> DefinedStructureStructureProcessorType<P> a(String s, Codec<P> codec) {
        return (DefinedStructureStructureProcessorType) IRegistry.a(IRegistry.STRUCTURE_PROCESSOR, s, (Object) (() -> {
            return codec;
        }));
    }
}

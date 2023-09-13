package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.Block;

public class DefinedStructureProcessorRotation extends DefinedStructureProcessor {

    public static final Codec<DefinedStructureProcessorRotation> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RegistryCodecs.homogeneousList(IRegistry.BLOCK_REGISTRY).optionalFieldOf("rottable_blocks").forGetter((definedstructureprocessorrotation) -> {
            return definedstructureprocessorrotation.rottableBlocks;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("integrity").forGetter((definedstructureprocessorrotation) -> {
            return definedstructureprocessorrotation.integrity;
        })).apply(instance, DefinedStructureProcessorRotation::new);
    });
    private Optional<HolderSet<Block>> rottableBlocks;
    private final float integrity;

    public DefinedStructureProcessorRotation(TagKey<Block> tagkey, float f) {
        this(Optional.of(IRegistry.BLOCK.getOrCreateTag(tagkey)), f);
    }

    public DefinedStructureProcessorRotation(float f) {
        this(Optional.empty(), f);
    }

    private DefinedStructureProcessorRotation(Optional<HolderSet<Block>> optional, float f) {
        this.integrity = f;
        this.rottableBlocks = optional;
    }

    @Nullable
    @Override
    public DefinedStructure.BlockInfo processBlock(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        RandomSource randomsource = definedstructureinfo.getRandom(definedstructure_blockinfo1.pos);

        return (!this.rottableBlocks.isPresent() || definedstructure_blockinfo.state.is((HolderSet) this.rottableBlocks.get())) && randomsource.nextFloat() > this.integrity ? null : definedstructure_blockinfo1;
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> getType() {
        return DefinedStructureStructureProcessorType.BLOCK_ROT;
    }
}

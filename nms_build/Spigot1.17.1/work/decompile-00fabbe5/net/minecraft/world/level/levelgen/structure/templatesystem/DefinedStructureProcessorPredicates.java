package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureProcessorPredicates {

    public static final Codec<DefinedStructureProcessorPredicates> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(DefinedStructureRuleTest.CODEC.fieldOf("input_predicate").forGetter((definedstructureprocessorpredicates) -> {
            return definedstructureprocessorpredicates.inputPredicate;
        }), DefinedStructureRuleTest.CODEC.fieldOf("location_predicate").forGetter((definedstructureprocessorpredicates) -> {
            return definedstructureprocessorpredicates.locPredicate;
        }), PosRuleTest.CODEC.optionalFieldOf("position_predicate", PosRuleTestTrue.INSTANCE).forGetter((definedstructureprocessorpredicates) -> {
            return definedstructureprocessorpredicates.posPredicate;
        }), IBlockData.CODEC.fieldOf("output_state").forGetter((definedstructureprocessorpredicates) -> {
            return definedstructureprocessorpredicates.outputState;
        }), NBTTagCompound.CODEC.optionalFieldOf("output_nbt").forGetter((definedstructureprocessorpredicates) -> {
            return Optional.ofNullable(definedstructureprocessorpredicates.outputTag);
        })).apply(instance, DefinedStructureProcessorPredicates::new);
    });
    private final DefinedStructureRuleTest inputPredicate;
    private final DefinedStructureRuleTest locPredicate;
    private final PosRuleTest posPredicate;
    private final IBlockData outputState;
    @Nullable
    private final NBTTagCompound outputTag;

    public DefinedStructureProcessorPredicates(DefinedStructureRuleTest definedstructureruletest, DefinedStructureRuleTest definedstructureruletest1, IBlockData iblockdata) {
        this(definedstructureruletest, definedstructureruletest1, PosRuleTestTrue.INSTANCE, iblockdata, Optional.empty());
    }

    public DefinedStructureProcessorPredicates(DefinedStructureRuleTest definedstructureruletest, DefinedStructureRuleTest definedstructureruletest1, PosRuleTest posruletest, IBlockData iblockdata) {
        this(definedstructureruletest, definedstructureruletest1, posruletest, iblockdata, Optional.empty());
    }

    public DefinedStructureProcessorPredicates(DefinedStructureRuleTest definedstructureruletest, DefinedStructureRuleTest definedstructureruletest1, PosRuleTest posruletest, IBlockData iblockdata, Optional<NBTTagCompound> optional) {
        this.inputPredicate = definedstructureruletest;
        this.locPredicate = definedstructureruletest1;
        this.posPredicate = posruletest;
        this.outputState = iblockdata;
        this.outputTag = (NBTTagCompound) optional.orElse((Object) null);
    }

    public boolean a(IBlockData iblockdata, IBlockData iblockdata1, BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, Random random) {
        return this.inputPredicate.a(iblockdata, random) && this.locPredicate.a(iblockdata1, random) && this.posPredicate.a(blockposition, blockposition1, blockposition2, random);
    }

    public IBlockData a() {
        return this.outputState;
    }

    @Nullable
    public NBTTagCompound b() {
        return this.outputTag;
    }
}

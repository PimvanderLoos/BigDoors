package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureTestRandomBlockState extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestRandomBlockState> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.CODEC.fieldOf("block_state").forGetter((definedstructuretestrandomblockstate) -> {
            return definedstructuretestrandomblockstate.blockState;
        }), Codec.FLOAT.fieldOf("probability").forGetter((definedstructuretestrandomblockstate) -> {
            return definedstructuretestrandomblockstate.probability;
        })).apply(instance, DefinedStructureTestRandomBlockState::new);
    });
    private final IBlockData blockState;
    private final float probability;

    public DefinedStructureTestRandomBlockState(IBlockData iblockdata, float f) {
        this.blockState = iblockdata;
        this.probability = f;
    }

    @Override
    public boolean test(IBlockData iblockdata, RandomSource randomsource) {
        return iblockdata == this.blockState && randomsource.nextFloat() < this.probability;
    }

    @Override
    protected DefinedStructureRuleTestType<?> getType() {
        return DefinedStructureRuleTestType.RANDOM_BLOCKSTATE_TEST;
    }
}

package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
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
    public boolean a(IBlockData iblockdata, Random random) {
        return iblockdata == this.blockState && random.nextFloat() < this.probability;
    }

    @Override
    protected DefinedStructureRuleTestType<?> a() {
        return DefinedStructureRuleTestType.RANDOM_BLOCKSTATE_TEST;
    }
}

package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureTestRandomBlockState extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestRandomBlockState> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.b.fieldOf("block_state").forGetter((definedstructuretestrandomblockstate) -> {
            return definedstructuretestrandomblockstate.b;
        }), Codec.FLOAT.fieldOf("probability").forGetter((definedstructuretestrandomblockstate) -> {
            return definedstructuretestrandomblockstate.d;
        })).apply(instance, DefinedStructureTestRandomBlockState::new);
    });
    private final IBlockData b;
    private final float d;

    public DefinedStructureTestRandomBlockState(IBlockData iblockdata, float f) {
        this.b = iblockdata;
        this.d = f;
    }

    @Override
    public boolean a(IBlockData iblockdata, Random random) {
        return iblockdata == this.b && random.nextFloat() < this.d;
    }

    @Override
    protected DefinedStructureRuleTestType<?> a() {
        return DefinedStructureRuleTestType.f;
    }
}

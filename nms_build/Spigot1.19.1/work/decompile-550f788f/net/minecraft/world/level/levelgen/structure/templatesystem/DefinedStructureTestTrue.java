package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureTestTrue extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestTrue> CODEC = Codec.unit(() -> {
        return DefinedStructureTestTrue.INSTANCE;
    });
    public static final DefinedStructureTestTrue INSTANCE = new DefinedStructureTestTrue();

    private DefinedStructureTestTrue() {}

    @Override
    public boolean test(IBlockData iblockdata, RandomSource randomsource) {
        return true;
    }

    @Override
    protected DefinedStructureRuleTestType<?> getType() {
        return DefinedStructureRuleTestType.ALWAYS_TRUE_TEST;
    }
}

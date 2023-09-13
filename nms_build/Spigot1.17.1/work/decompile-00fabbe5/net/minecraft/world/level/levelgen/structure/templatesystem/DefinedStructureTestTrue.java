package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureTestTrue extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestTrue> CODEC = Codec.unit(() -> {
        return DefinedStructureTestTrue.INSTANCE;
    });
    public static final DefinedStructureTestTrue INSTANCE = new DefinedStructureTestTrue();

    private DefinedStructureTestTrue() {}

    @Override
    public boolean a(IBlockData iblockdata, Random random) {
        return true;
    }

    @Override
    protected DefinedStructureRuleTestType<?> a() {
        return DefinedStructureRuleTestType.ALWAYS_TRUE_TEST;
    }
}

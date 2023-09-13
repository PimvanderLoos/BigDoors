package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class DefinedStructureTestTrue extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestTrue> a = Codec.unit(() -> {
        return DefinedStructureTestTrue.b;
    });
    public static final DefinedStructureTestTrue b = new DefinedStructureTestTrue();

    private DefinedStructureTestTrue() {}

    @Override
    public boolean a(IBlockData iblockdata, Random random) {
        return true;
    }

    @Override
    protected DefinedStructureRuleTestType<?> a() {
        return DefinedStructureRuleTestType.a;
    }
}

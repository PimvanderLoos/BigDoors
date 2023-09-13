package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class PosRuleTestTrue extends PosRuleTest {

    public static final Codec<PosRuleTestTrue> a = Codec.unit(() -> {
        return PosRuleTestTrue.b;
    });
    public static final PosRuleTestTrue b = new PosRuleTestTrue();

    private PosRuleTestTrue() {}

    @Override
    public boolean a(BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, Random random) {
        return true;
    }

    @Override
    protected PosRuleTestType<?> a() {
        return PosRuleTestType.a;
    }
}

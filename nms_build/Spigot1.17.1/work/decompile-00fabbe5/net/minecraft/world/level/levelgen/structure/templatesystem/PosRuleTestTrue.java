package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;

public class PosRuleTestTrue extends PosRuleTest {

    public static final Codec<PosRuleTestTrue> CODEC = Codec.unit(() -> {
        return PosRuleTestTrue.INSTANCE;
    });
    public static final PosRuleTestTrue INSTANCE = new PosRuleTestTrue();

    private PosRuleTestTrue() {}

    @Override
    public boolean a(BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, Random random) {
        return true;
    }

    @Override
    protected PosRuleTestType<?> a() {
        return PosRuleTestType.ALWAYS_TRUE_TEST;
    }
}

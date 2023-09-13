package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;

public abstract class PosRuleTest {

    public static final Codec<PosRuleTest> CODEC = IRegistry.POS_RULE_TEST.byNameCodec().dispatch("predicate_type", PosRuleTest::getType, PosRuleTestType::codec);

    public PosRuleTest() {}

    public abstract boolean test(BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, Random random);

    protected abstract PosRuleTestType<?> getType();
}

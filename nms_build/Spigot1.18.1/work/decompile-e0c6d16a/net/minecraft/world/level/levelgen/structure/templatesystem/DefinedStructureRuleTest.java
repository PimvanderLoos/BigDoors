package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class DefinedStructureRuleTest {

    public static final Codec<DefinedStructureRuleTest> CODEC = IRegistry.RULE_TEST.byNameCodec().dispatch("predicate_type", DefinedStructureRuleTest::getType, DefinedStructureRuleTestType::codec);

    public DefinedStructureRuleTest() {}

    public abstract boolean test(IBlockData iblockdata, Random random);

    protected abstract DefinedStructureRuleTestType<?> getType();
}

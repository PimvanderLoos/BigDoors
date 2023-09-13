package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class DefinedStructureRuleTest {

    public static final Codec<DefinedStructureRuleTest> c = IRegistry.RULE_TEST.dispatch("predicate_type", DefinedStructureRuleTest::a, DefinedStructureRuleTestType::codec);

    public DefinedStructureRuleTest() {}

    public abstract boolean a(IBlockData iblockdata, Random random);

    protected abstract DefinedStructureRuleTestType<?> a();
}

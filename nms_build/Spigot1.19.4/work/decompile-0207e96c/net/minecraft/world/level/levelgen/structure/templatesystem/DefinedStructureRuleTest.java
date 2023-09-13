package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class DefinedStructureRuleTest {

    public static final Codec<DefinedStructureRuleTest> CODEC = BuiltInRegistries.RULE_TEST.byNameCodec().dispatch("predicate_type", DefinedStructureRuleTest::getType, DefinedStructureRuleTestType::codec);

    public DefinedStructureRuleTest() {}

    public abstract boolean test(IBlockData iblockdata, RandomSource randomsource);

    protected abstract DefinedStructureRuleTestType<?> getType();
}

package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureTestBlock extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestBlock> CODEC = BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").xmap(DefinedStructureTestBlock::new, (definedstructuretestblock) -> {
        return definedstructuretestblock.block;
    }).codec();
    private final Block block;

    public DefinedStructureTestBlock(Block block) {
        this.block = block;
    }

    @Override
    public boolean test(IBlockData iblockdata, RandomSource randomsource) {
        return iblockdata.is(this.block);
    }

    @Override
    protected DefinedStructureRuleTestType<?> getType() {
        return DefinedStructureRuleTestType.BLOCK_TEST;
    }
}

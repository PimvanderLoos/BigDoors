package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class DefinedStructureTestBlock extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestBlock> a = IRegistry.BLOCK.fieldOf("block").xmap(DefinedStructureTestBlock::new, (definedstructuretestblock) -> {
        return definedstructuretestblock.b;
    }).codec();
    private final Block b;

    public DefinedStructureTestBlock(Block block) {
        this.b = block;
    }

    @Override
    public boolean a(IBlockData iblockdata, Random random) {
        return iblockdata.a(this.b);
    }

    @Override
    protected DefinedStructureRuleTestType<?> a() {
        return DefinedStructureRuleTestType.b;
    }
}

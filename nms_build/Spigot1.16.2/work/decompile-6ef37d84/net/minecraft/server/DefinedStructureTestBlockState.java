package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class DefinedStructureTestBlockState extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestBlockState> a = IBlockData.b.fieldOf("block_state").xmap(DefinedStructureTestBlockState::new, (definedstructuretestblockstate) -> {
        return definedstructuretestblockstate.b;
    }).codec();
    private final IBlockData b;

    public DefinedStructureTestBlockState(IBlockData iblockdata) {
        this.b = iblockdata;
    }

    @Override
    public boolean a(IBlockData iblockdata, Random random) {
        return iblockdata == this.b;
    }

    @Override
    protected DefinedStructureRuleTestType<?> a() {
        return DefinedStructureRuleTestType.c;
    }
}

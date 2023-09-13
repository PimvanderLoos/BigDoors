package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureTestBlockState extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestBlockState> CODEC = IBlockData.CODEC.fieldOf("block_state").xmap(DefinedStructureTestBlockState::new, (definedstructuretestblockstate) -> {
        return definedstructuretestblockstate.blockState;
    }).codec();
    private final IBlockData blockState;

    public DefinedStructureTestBlockState(IBlockData iblockdata) {
        this.blockState = iblockdata;
    }

    @Override
    public boolean a(IBlockData iblockdata, Random random) {
        return iblockdata == this.blockState;
    }

    @Override
    protected DefinedStructureRuleTestType<?> a() {
        return DefinedStructureRuleTestType.BLOCKSTATE_TEST;
    }
}

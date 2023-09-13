package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureTestTag extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestTag> CODEC = TagKey.codec(IRegistry.BLOCK_REGISTRY).fieldOf("tag").xmap(DefinedStructureTestTag::new, (definedstructuretesttag) -> {
        return definedstructuretesttag.tag;
    }).codec();
    private final TagKey<Block> tag;

    public DefinedStructureTestTag(TagKey<Block> tagkey) {
        this.tag = tagkey;
    }

    @Override
    public boolean test(IBlockData iblockdata, RandomSource randomsource) {
        return iblockdata.is(this.tag);
    }

    @Override
    protected DefinedStructureRuleTestType<?> getType() {
        return DefinedStructureRuleTestType.TAG_TEST;
    }
}

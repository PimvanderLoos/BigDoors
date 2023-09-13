package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.IRegistry;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsInstance;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureTestTag extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestTag> CODEC = Tag.a(() -> {
        return TagsInstance.a().a(IRegistry.BLOCK_REGISTRY);
    }).fieldOf("tag").xmap(DefinedStructureTestTag::new, (definedstructuretesttag) -> {
        return definedstructuretesttag.tag;
    }).codec();
    private final Tag<Block> tag;

    public DefinedStructureTestTag(Tag<Block> tag) {
        this.tag = tag;
    }

    @Override
    public boolean a(IBlockData iblockdata, Random random) {
        return iblockdata.a(this.tag);
    }

    @Override
    protected DefinedStructureRuleTestType<?> a() {
        return DefinedStructureRuleTestType.TAG_TEST;
    }
}

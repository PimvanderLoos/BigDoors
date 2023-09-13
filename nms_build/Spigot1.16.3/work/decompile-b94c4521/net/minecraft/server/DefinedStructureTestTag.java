package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class DefinedStructureTestTag extends DefinedStructureRuleTest {

    public static final Codec<DefinedStructureTestTag> a = Tag.a(() -> {
        return TagsInstance.a().getBlockTags();
    }).fieldOf("tag").xmap(DefinedStructureTestTag::new, (definedstructuretesttag) -> {
        return definedstructuretesttag.b;
    }).codec();
    private final Tag<Block> b;

    public DefinedStructureTestTag(Tag<Block> tag) {
        this.b = tag;
    }

    @Override
    public boolean a(IBlockData iblockdata, Random random) {
        return iblockdata.a(this.b);
    }

    @Override
    protected DefinedStructureRuleTestType<?> a() {
        return DefinedStructureRuleTestType.d;
    }
}

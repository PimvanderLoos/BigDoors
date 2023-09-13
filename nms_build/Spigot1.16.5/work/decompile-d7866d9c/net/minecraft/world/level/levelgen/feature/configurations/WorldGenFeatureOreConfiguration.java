package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureTestBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureTestTag;

public class WorldGenFeatureOreConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureOreConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(DefinedStructureRuleTest.c.fieldOf("target").forGetter((worldgenfeatureoreconfiguration) -> {
            return worldgenfeatureoreconfiguration.b;
        }), IBlockData.b.fieldOf("state").forGetter((worldgenfeatureoreconfiguration) -> {
            return worldgenfeatureoreconfiguration.d;
        }), Codec.intRange(0, 64).fieldOf("size").forGetter((worldgenfeatureoreconfiguration) -> {
            return worldgenfeatureoreconfiguration.c;
        })).apply(instance, WorldGenFeatureOreConfiguration::new);
    });
    public final DefinedStructureRuleTest b;
    public final int c;
    public final IBlockData d;

    public WorldGenFeatureOreConfiguration(DefinedStructureRuleTest definedstructureruletest, IBlockData iblockdata, int i) {
        this.c = i;
        this.d = iblockdata;
        this.b = definedstructureruletest;
    }

    public static final class Target {

        public static final DefinedStructureRuleTest NATURAL_STONE = new DefinedStructureTestTag(TagsBlock.aH);
        public static final DefinedStructureRuleTest NETHERRACK = new DefinedStructureTestBlock(Blocks.NETHERRACK);
        public static final DefinedStructureRuleTest NETHER_ORE_REPLACEABLES = new DefinedStructureTestTag(TagsBlock.aI);
    }
}

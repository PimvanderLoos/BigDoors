package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureTestBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureTestTag;

public class WorldGenFeatureOreConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureOreConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.list(WorldGenFeatureOreConfiguration.b.CODEC).fieldOf("targets").forGetter((worldgenfeatureoreconfiguration) -> {
            return worldgenfeatureoreconfiguration.targetStates;
        }), Codec.intRange(0, 64).fieldOf("size").forGetter((worldgenfeatureoreconfiguration) -> {
            return worldgenfeatureoreconfiguration.size;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("discard_chance_on_air_exposure").forGetter((worldgenfeatureoreconfiguration) -> {
            return worldgenfeatureoreconfiguration.discardChanceOnAirExposure;
        })).apply(instance, WorldGenFeatureOreConfiguration::new);
    });
    public final List<WorldGenFeatureOreConfiguration.b> targetStates;
    public final int size;
    public final float discardChanceOnAirExposure;

    public WorldGenFeatureOreConfiguration(List<WorldGenFeatureOreConfiguration.b> list, int i, float f) {
        this.size = i;
        this.targetStates = list;
        this.discardChanceOnAirExposure = f;
    }

    public WorldGenFeatureOreConfiguration(List<WorldGenFeatureOreConfiguration.b> list, int i) {
        this(list, i, 0.0F);
    }

    public WorldGenFeatureOreConfiguration(DefinedStructureRuleTest definedstructureruletest, IBlockData iblockdata, int i, float f) {
        this(ImmutableList.of(new WorldGenFeatureOreConfiguration.b(definedstructureruletest, iblockdata)), i, f);
    }

    public WorldGenFeatureOreConfiguration(DefinedStructureRuleTest definedstructureruletest, IBlockData iblockdata, int i) {
        this(ImmutableList.of(new WorldGenFeatureOreConfiguration.b(definedstructureruletest, iblockdata)), i, 0.0F);
    }

    public static WorldGenFeatureOreConfiguration.b a(DefinedStructureRuleTest definedstructureruletest, IBlockData iblockdata) {
        return new WorldGenFeatureOreConfiguration.b(definedstructureruletest, iblockdata);
    }

    public static class b {

        public static final Codec<WorldGenFeatureOreConfiguration.b> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(DefinedStructureRuleTest.CODEC.fieldOf("target").forGetter((worldgenfeatureoreconfiguration_b) -> {
                return worldgenfeatureoreconfiguration_b.target;
            }), IBlockData.CODEC.fieldOf("state").forGetter((worldgenfeatureoreconfiguration_b) -> {
                return worldgenfeatureoreconfiguration_b.state;
            })).apply(instance, WorldGenFeatureOreConfiguration.b::new);
        });
        public final DefinedStructureRuleTest target;
        public final IBlockData state;

        b(DefinedStructureRuleTest definedstructureruletest, IBlockData iblockdata) {
            this.target = definedstructureruletest;
            this.state = iblockdata;
        }
    }

    public static final class Target {

        public static final DefinedStructureRuleTest NATURAL_STONE = new DefinedStructureTestTag(TagsBlock.BASE_STONE_OVERWORLD);
        public static final DefinedStructureRuleTest STONE_ORE_REPLACEABLES = new DefinedStructureTestTag(TagsBlock.STONE_ORE_REPLACEABLES);
        public static final DefinedStructureRuleTest DEEPSLATE_ORE_REPLACEABLES = new DefinedStructureTestTag(TagsBlock.DEEPSLATE_ORE_REPLACEABLES);
        public static final DefinedStructureRuleTest NETHERRACK = new DefinedStructureTestBlock(Blocks.NETHERRACK);
        public static final DefinedStructureRuleTest NETHER_ORE_REPLACEABLES = new DefinedStructureTestTag(TagsBlock.BASE_STONE_NETHER);

        public Target() {}
    }
}

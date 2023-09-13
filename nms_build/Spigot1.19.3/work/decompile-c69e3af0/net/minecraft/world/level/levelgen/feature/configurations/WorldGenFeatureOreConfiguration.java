package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureRuleTest;

public class WorldGenFeatureOreConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureOreConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.list(WorldGenFeatureOreConfiguration.a.CODEC).fieldOf("targets").forGetter((worldgenfeatureoreconfiguration) -> {
            return worldgenfeatureoreconfiguration.targetStates;
        }), Codec.intRange(0, 64).fieldOf("size").forGetter((worldgenfeatureoreconfiguration) -> {
            return worldgenfeatureoreconfiguration.size;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("discard_chance_on_air_exposure").forGetter((worldgenfeatureoreconfiguration) -> {
            return worldgenfeatureoreconfiguration.discardChanceOnAirExposure;
        })).apply(instance, WorldGenFeatureOreConfiguration::new);
    });
    public final List<WorldGenFeatureOreConfiguration.a> targetStates;
    public final int size;
    public final float discardChanceOnAirExposure;

    public WorldGenFeatureOreConfiguration(List<WorldGenFeatureOreConfiguration.a> list, int i, float f) {
        this.size = i;
        this.targetStates = list;
        this.discardChanceOnAirExposure = f;
    }

    public WorldGenFeatureOreConfiguration(List<WorldGenFeatureOreConfiguration.a> list, int i) {
        this(list, i, 0.0F);
    }

    public WorldGenFeatureOreConfiguration(DefinedStructureRuleTest definedstructureruletest, IBlockData iblockdata, int i, float f) {
        this(ImmutableList.of(new WorldGenFeatureOreConfiguration.a(definedstructureruletest, iblockdata)), i, f);
    }

    public WorldGenFeatureOreConfiguration(DefinedStructureRuleTest definedstructureruletest, IBlockData iblockdata, int i) {
        this(ImmutableList.of(new WorldGenFeatureOreConfiguration.a(definedstructureruletest, iblockdata)), i, 0.0F);
    }

    public static WorldGenFeatureOreConfiguration.a target(DefinedStructureRuleTest definedstructureruletest, IBlockData iblockdata) {
        return new WorldGenFeatureOreConfiguration.a(definedstructureruletest, iblockdata);
    }

    public static class a {

        public static final Codec<WorldGenFeatureOreConfiguration.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(DefinedStructureRuleTest.CODEC.fieldOf("target").forGetter((worldgenfeatureoreconfiguration_a) -> {
                return worldgenfeatureoreconfiguration_a.target;
            }), IBlockData.CODEC.fieldOf("state").forGetter((worldgenfeatureoreconfiguration_a) -> {
                return worldgenfeatureoreconfiguration_a.state;
            })).apply(instance, WorldGenFeatureOreConfiguration.a::new);
        });
        public final DefinedStructureRuleTest target;
        public final IBlockData state;

        a(DefinedStructureRuleTest definedstructureruletest, IBlockData iblockdata) {
            this.target = definedstructureruletest;
            this.state = iblockdata;
        }
    }
}

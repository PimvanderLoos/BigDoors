package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class SurfaceRuleData {

    private static final SurfaceRules.o AIR = makeStateRule(Blocks.AIR);
    private static final SurfaceRules.o BEDROCK = makeStateRule(Blocks.BEDROCK);
    private static final SurfaceRules.o WHITE_TERRACOTTA = makeStateRule(Blocks.WHITE_TERRACOTTA);
    private static final SurfaceRules.o ORANGE_TERRACOTTA = makeStateRule(Blocks.ORANGE_TERRACOTTA);
    private static final SurfaceRules.o TERRACOTTA = makeStateRule(Blocks.TERRACOTTA);
    private static final SurfaceRules.o RED_SAND = makeStateRule(Blocks.RED_SAND);
    private static final SurfaceRules.o RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
    private static final SurfaceRules.o STONE = makeStateRule(Blocks.STONE);
    private static final SurfaceRules.o DEEPSLATE = makeStateRule(Blocks.DEEPSLATE);
    private static final SurfaceRules.o DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.o PODZOL = makeStateRule(Blocks.PODZOL);
    private static final SurfaceRules.o COARSE_DIRT = makeStateRule(Blocks.COARSE_DIRT);
    private static final SurfaceRules.o MYCELIUM = makeStateRule(Blocks.MYCELIUM);
    private static final SurfaceRules.o GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.o CALCITE = makeStateRule(Blocks.CALCITE);
    private static final SurfaceRules.o GRAVEL = makeStateRule(Blocks.GRAVEL);
    private static final SurfaceRules.o SAND = makeStateRule(Blocks.SAND);
    private static final SurfaceRules.o SANDSTONE = makeStateRule(Blocks.SANDSTONE);
    private static final SurfaceRules.o PACKED_ICE = makeStateRule(Blocks.PACKED_ICE);
    private static final SurfaceRules.o SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
    private static final SurfaceRules.o MUD = makeStateRule(Blocks.MUD);
    private static final SurfaceRules.o POWDER_SNOW = makeStateRule(Blocks.POWDER_SNOW);
    private static final SurfaceRules.o ICE = makeStateRule(Blocks.ICE);
    private static final SurfaceRules.o WATER = makeStateRule(Blocks.WATER);
    private static final SurfaceRules.o LAVA = makeStateRule(Blocks.LAVA);
    private static final SurfaceRules.o NETHERRACK = makeStateRule(Blocks.NETHERRACK);
    private static final SurfaceRules.o SOUL_SAND = makeStateRule(Blocks.SOUL_SAND);
    private static final SurfaceRules.o SOUL_SOIL = makeStateRule(Blocks.SOUL_SOIL);
    private static final SurfaceRules.o BASALT = makeStateRule(Blocks.BASALT);
    private static final SurfaceRules.o BLACKSTONE = makeStateRule(Blocks.BLACKSTONE);
    private static final SurfaceRules.o WARPED_WART_BLOCK = makeStateRule(Blocks.WARPED_WART_BLOCK);
    private static final SurfaceRules.o WARPED_NYLIUM = makeStateRule(Blocks.WARPED_NYLIUM);
    private static final SurfaceRules.o NETHER_WART_BLOCK = makeStateRule(Blocks.NETHER_WART_BLOCK);
    private static final SurfaceRules.o CRIMSON_NYLIUM = makeStateRule(Blocks.CRIMSON_NYLIUM);
    private static final SurfaceRules.o ENDSTONE = makeStateRule(Blocks.END_STONE);

    public SurfaceRuleData() {}

    private static SurfaceRules.o makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    public static SurfaceRules.o overworld() {
        return overworldLike(true, false, true);
    }

    public static SurfaceRules.o overworldLike(boolean flag, boolean flag1, boolean flag2) {
        SurfaceRules.f surfacerules_f = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(97), 2);
        SurfaceRules.f surfacerules_f1 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(256), 0);
        SurfaceRules.f surfacerules_f2 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(63), -1);
        SurfaceRules.f surfacerules_f3 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(74), 1);
        SurfaceRules.f surfacerules_f4 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(60), 0);
        SurfaceRules.f surfacerules_f5 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0);
        SurfaceRules.f surfacerules_f6 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
        SurfaceRules.f surfacerules_f7 = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.f surfacerules_f8 = SurfaceRules.waterBlockCheck(0, 0);
        SurfaceRules.f surfacerules_f9 = SurfaceRules.waterStartCheck(-6, -1);
        SurfaceRules.f surfacerules_f10 = SurfaceRules.hole();
        SurfaceRules.f surfacerules_f11 = SurfaceRules.isBiome(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
        SurfaceRules.f surfacerules_f12 = SurfaceRules.steep();
        SurfaceRules.o surfacerules_o = SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f8, SurfaceRuleData.GRASS_BLOCK), SurfaceRuleData.DIRT);
        SurfaceRules.o surfacerules_o1 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SurfaceRuleData.SANDSTONE), SurfaceRuleData.SAND);
        SurfaceRules.o surfacerules_o2 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SurfaceRuleData.STONE), SurfaceRuleData.GRAVEL);
        SurfaceRules.f surfacerules_f13 = SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH);
        SurfaceRules.f surfacerules_f14 = SurfaceRules.isBiome(Biomes.DESERT);
        SurfaceRules.o surfacerules_o3 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.STONY_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.CALCITE, -0.0125D, 0.0125D), SurfaceRuleData.CALCITE), SurfaceRuleData.STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.STONY_SHORE), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, -0.05D, 0.05D), surfacerules_o2), SurfaceRuleData.STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_HILLS), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0D), SurfaceRuleData.STONE)), SurfaceRules.ifTrue(surfacerules_f13, surfacerules_o1), SurfaceRules.ifTrue(surfacerules_f14, surfacerules_o1), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.DRIPSTONE_CAVES), SurfaceRuleData.STONE));
        SurfaceRules.o surfacerules_o4 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.45D, 0.58D), SurfaceRules.ifTrue(surfacerules_f8, SurfaceRuleData.POWDER_SNOW));
        SurfaceRules.o surfacerules_o5 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.35D, 0.6D), SurfaceRules.ifTrue(surfacerules_f8, SurfaceRuleData.POWDER_SNOW));
        SurfaceRules.o surfacerules_o6 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f12, SurfaceRuleData.PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, -0.5D, 0.2D), SurfaceRuleData.PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, -0.0625D, 0.025D), SurfaceRuleData.ICE), SurfaceRules.ifTrue(surfacerules_f8, SurfaceRuleData.SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f12, SurfaceRuleData.STONE), surfacerules_o4, SurfaceRules.ifTrue(surfacerules_f8, SurfaceRuleData.SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), SurfaceRuleData.STONE), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(surfacerules_o4, SurfaceRuleData.DIRT)), surfacerules_o3, SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.ifTrue(surfaceNoiseAbove(1.75D), SurfaceRuleData.STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(2.0D), surfacerules_o2), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0D), SurfaceRuleData.STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0D), SurfaceRuleData.DIRT), surfacerules_o2)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), SurfaceRuleData.MUD), SurfaceRuleData.DIRT);
        SurfaceRules.o surfacerules_o7 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f12, SurfaceRuleData.PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, 0.0D, 0.2D), SurfaceRuleData.PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, 0.0D, 0.025D), SurfaceRuleData.ICE), SurfaceRules.ifTrue(surfacerules_f8, SurfaceRuleData.SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f12, SurfaceRuleData.STONE), surfacerules_o5, SurfaceRules.ifTrue(surfacerules_f8, SurfaceRuleData.SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f12, SurfaceRuleData.STONE), SurfaceRules.ifTrue(surfacerules_f8, SurfaceRuleData.SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(surfacerules_o5, SurfaceRules.ifTrue(surfacerules_f8, SurfaceRuleData.SNOW_BLOCK))), surfacerules_o3, SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75D), SurfaceRuleData.STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.5D), SurfaceRuleData.COARSE_DIRT))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(2.0D), surfacerules_o2), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0D), SurfaceRuleData.STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0D), surfacerules_o), surfacerules_o2)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75D), SurfaceRuleData.COARSE_DIRT), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.95D), SurfaceRuleData.PODZOL))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.ICE_SPIKES), SurfaceRules.ifTrue(surfacerules_f8, SurfaceRuleData.SNOW_BLOCK)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), SurfaceRuleData.MUD), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MUSHROOM_FIELDS), SurfaceRuleData.MYCELIUM), surfacerules_o);
        SurfaceRules.f surfacerules_f15 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.909D, -0.5454D);
        SurfaceRules.f surfacerules_f16 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.1818D, 0.1818D);
        SurfaceRules.f surfacerules_f17 = SurfaceRules.noiseCondition(Noises.SURFACE, 0.5454D, 0.909D);
        SurfaceRules.o surfacerules_o8 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WOODED_BADLANDS), SurfaceRules.ifTrue(surfacerules_f, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f15, SurfaceRuleData.COARSE_DIRT), SurfaceRules.ifTrue(surfacerules_f16, SurfaceRuleData.COARSE_DIRT), SurfaceRules.ifTrue(surfacerules_f17, SurfaceRuleData.COARSE_DIRT), surfacerules_o))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SWAMP), SurfaceRules.ifTrue(surfacerules_f5, SurfaceRules.ifTrue(SurfaceRules.not(surfacerules_f6), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0D), SurfaceRuleData.WATER)))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), SurfaceRules.ifTrue(surfacerules_f4, SurfaceRules.ifTrue(SurfaceRules.not(surfacerules_f6), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0D), SurfaceRuleData.WATER)))))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f1, SurfaceRuleData.ORANGE_TERRACOTTA), SurfaceRules.ifTrue(surfacerules_f3, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f15, SurfaceRuleData.TERRACOTTA), SurfaceRules.ifTrue(surfacerules_f16, SurfaceRuleData.TERRACOTTA), SurfaceRules.ifTrue(surfacerules_f17, SurfaceRuleData.TERRACOTTA), SurfaceRules.bandlands())), SurfaceRules.ifTrue(surfacerules_f7, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SurfaceRuleData.RED_SANDSTONE), SurfaceRuleData.RED_SAND)), SurfaceRules.ifTrue(SurfaceRules.not(surfacerules_f10), SurfaceRuleData.ORANGE_TERRACOTTA), SurfaceRules.ifTrue(surfacerules_f9, SurfaceRuleData.WHITE_TERRACOTTA), surfacerules_o2)), SurfaceRules.ifTrue(surfacerules_f2, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f6, SurfaceRules.ifTrue(SurfaceRules.not(surfacerules_f3), SurfaceRuleData.ORANGE_TERRACOTTA)), SurfaceRules.bandlands())), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(surfacerules_f9, SurfaceRuleData.WHITE_TERRACOTTA)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(surfacerules_f7, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f11, SurfaceRules.ifTrue(surfacerules_f10, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f8, SurfaceRuleData.AIR), SurfaceRules.ifTrue(SurfaceRules.temperature(), SurfaceRuleData.ICE), SurfaceRuleData.WATER))), surfacerules_o7))), SurfaceRules.ifTrue(surfacerules_f9, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(surfacerules_f11, SurfaceRules.ifTrue(surfacerules_f10, SurfaceRuleData.WATER))), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, surfacerules_o6), SurfaceRules.ifTrue(surfacerules_f13, SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, SurfaceRuleData.SANDSTONE)), SurfaceRules.ifTrue(surfacerules_f14, SurfaceRules.ifTrue(SurfaceRules.VERY_DEEP_UNDER_FLOOR, SurfaceRuleData.SANDSTONE)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS), SurfaceRuleData.STONE), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), surfacerules_o1), surfacerules_o2)));
        Builder<SurfaceRules.o> builder = ImmutableList.builder();

        if (flag1) {
            builder.add(SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), SurfaceRuleData.BEDROCK));
        }

        if (flag2) {
            builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), SurfaceRuleData.BEDROCK));
        }

        SurfaceRules.o surfacerules_o9 = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), surfacerules_o8);

        builder.add(flag ? surfacerules_o9 : surfacerules_o8);
        builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), SurfaceRuleData.DEEPSLATE));
        return SurfaceRules.sequence((SurfaceRules.o[]) builder.build().toArray((i) -> {
            return new SurfaceRules.o[i];
        }));
    }

    public static SurfaceRules.o nether() {
        SurfaceRules.f surfacerules_f = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(31), 0);
        SurfaceRules.f surfacerules_f1 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(32), 0);
        SurfaceRules.f surfacerules_f2 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(30), 0);
        SurfaceRules.f surfacerules_f3 = SurfaceRules.not(SurfaceRules.yStartCheck(VerticalAnchor.absolute(35), 0));
        SurfaceRules.f surfacerules_f4 = SurfaceRules.yBlockCheck(VerticalAnchor.belowTop(5), 0);
        SurfaceRules.f surfacerules_f5 = SurfaceRules.hole();
        SurfaceRules.f surfacerules_f6 = SurfaceRules.noiseCondition(Noises.SOUL_SAND_LAYER, -0.012D);
        SurfaceRules.f surfacerules_f7 = SurfaceRules.noiseCondition(Noises.GRAVEL_LAYER, -0.012D);
        SurfaceRules.f surfacerules_f8 = SurfaceRules.noiseCondition(Noises.PATCH, -0.012D);
        SurfaceRules.f surfacerules_f9 = SurfaceRules.noiseCondition(Noises.NETHERRACK, 0.54D);
        SurfaceRules.f surfacerules_f10 = SurfaceRules.noiseCondition(Noises.NETHER_WART, 1.17D);
        SurfaceRules.f surfacerules_f11 = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, 0.0D);
        SurfaceRules.o surfacerules_o = SurfaceRules.ifTrue(surfacerules_f8, SurfaceRules.ifTrue(surfacerules_f2, SurfaceRules.ifTrue(surfacerules_f3, SurfaceRuleData.GRAVEL)));

        return SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), SurfaceRuleData.BEDROCK), SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), SurfaceRuleData.BEDROCK), SurfaceRules.ifTrue(surfacerules_f4, SurfaceRuleData.NETHERRACK), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.BASALT_DELTAS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, SurfaceRuleData.BASALT), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(surfacerules_o, SurfaceRules.ifTrue(surfacerules_f11, SurfaceRuleData.BASALT), SurfaceRuleData.BLACKSTONE)))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SOUL_SAND_VALLEY), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f11, SurfaceRuleData.SOUL_SAND), SurfaceRuleData.SOUL_SOIL)), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(surfacerules_o, SurfaceRules.ifTrue(surfacerules_f11, SurfaceRuleData.SOUL_SAND), SurfaceRuleData.SOUL_SOIL)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.not(surfacerules_f1), SurfaceRules.ifTrue(surfacerules_f5, SurfaceRuleData.LAVA)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WARPED_FOREST), SurfaceRules.ifTrue(SurfaceRules.not(surfacerules_f9), SurfaceRules.ifTrue(surfacerules_f, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f10, SurfaceRuleData.WARPED_WART_BLOCK), SurfaceRuleData.WARPED_NYLIUM)))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.CRIMSON_FOREST), SurfaceRules.ifTrue(SurfaceRules.not(surfacerules_f9), SurfaceRules.ifTrue(surfacerules_f, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f10, SurfaceRuleData.NETHER_WART_BLOCK), SurfaceRuleData.CRIMSON_NYLIUM)))))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.NETHER_WASTES), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(surfacerules_f6, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.not(surfacerules_f5), SurfaceRules.ifTrue(surfacerules_f2, SurfaceRules.ifTrue(surfacerules_f3, SurfaceRuleData.SOUL_SAND))), SurfaceRuleData.NETHERRACK))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(surfacerules_f, SurfaceRules.ifTrue(surfacerules_f3, SurfaceRules.ifTrue(surfacerules_f7, SurfaceRules.sequence(SurfaceRules.ifTrue(surfacerules_f1, SurfaceRuleData.GRAVEL), SurfaceRules.ifTrue(SurfaceRules.not(surfacerules_f5), SurfaceRuleData.GRAVEL)))))))), SurfaceRuleData.NETHERRACK);
    }

    public static SurfaceRules.o end() {
        return SurfaceRuleData.ENDSTONE;
    }

    public static SurfaceRules.o air() {
        return SurfaceRuleData.AIR;
    }

    private static SurfaceRules.f surfaceNoiseAbove(double d0) {
        return SurfaceRules.noiseCondition(Noises.SURFACE, d0 / 8.25D, Double.MAX_VALUE);
    }
}

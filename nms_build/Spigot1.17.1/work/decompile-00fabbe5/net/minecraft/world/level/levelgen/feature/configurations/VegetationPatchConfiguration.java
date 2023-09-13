package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public class VegetationPatchConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<VegetationPatchConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(MinecraftKey.CODEC.fieldOf("replaceable").forGetter((vegetationpatchconfiguration) -> {
            return vegetationpatchconfiguration.replaceable;
        }), WorldGenFeatureStateProvider.CODEC.fieldOf("ground_state").forGetter((vegetationpatchconfiguration) -> {
            return vegetationpatchconfiguration.groundState;
        }), WorldGenFeatureConfigured.CODEC.fieldOf("vegetation_feature").forGetter((vegetationpatchconfiguration) -> {
            return vegetationpatchconfiguration.vegetationFeature;
        }), CaveSurface.CODEC.fieldOf("surface").forGetter((vegetationpatchconfiguration) -> {
            return vegetationpatchconfiguration.surface;
        }), IntProvider.b(1, 128).fieldOf("depth").forGetter((vegetationpatchconfiguration) -> {
            return vegetationpatchconfiguration.depth;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("extra_bottom_block_chance").forGetter((vegetationpatchconfiguration) -> {
            return vegetationpatchconfiguration.extraBottomBlockChance;
        }), Codec.intRange(1, 256).fieldOf("vertical_range").forGetter((vegetationpatchconfiguration) -> {
            return vegetationpatchconfiguration.verticalRange;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("vegetation_chance").forGetter((vegetationpatchconfiguration) -> {
            return vegetationpatchconfiguration.vegetationChance;
        }), IntProvider.CODEC.fieldOf("xz_radius").forGetter((vegetationpatchconfiguration) -> {
            return vegetationpatchconfiguration.xzRadius;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("extra_edge_column_chance").forGetter((vegetationpatchconfiguration) -> {
            return vegetationpatchconfiguration.extraEdgeColumnChance;
        })).apply(instance, VegetationPatchConfiguration::new);
    });
    public final MinecraftKey replaceable;
    public final WorldGenFeatureStateProvider groundState;
    public final Supplier<WorldGenFeatureConfigured<?, ?>> vegetationFeature;
    public final CaveSurface surface;
    public final IntProvider depth;
    public final float extraBottomBlockChance;
    public final int verticalRange;
    public final float vegetationChance;
    public final IntProvider xzRadius;
    public final float extraEdgeColumnChance;

    public VegetationPatchConfiguration(MinecraftKey minecraftkey, WorldGenFeatureStateProvider worldgenfeaturestateprovider, Supplier<WorldGenFeatureConfigured<?, ?>> supplier, CaveSurface cavesurface, IntProvider intprovider, float f, int i, float f1, IntProvider intprovider1, float f2) {
        this.replaceable = minecraftkey;
        this.groundState = worldgenfeaturestateprovider;
        this.vegetationFeature = supplier;
        this.surface = cavesurface;
        this.depth = intprovider;
        this.extraBottomBlockChance = f;
        this.verticalRange = i;
        this.vegetationChance = f1;
        this.xzRadius = intprovider1;
        this.extraEdgeColumnChance = f2;
    }
}

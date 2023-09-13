package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public record GeneratorSettingBase(NoiseSettings j, IBlockData k, IBlockData l, NoiseRouterWithOnlyNoises m, SurfaceRules.o n, int o, boolean p, boolean q, boolean r, boolean s) {

    private final NoiseSettings noiseSettings;
    private final IBlockData defaultBlock;
    private final IBlockData defaultFluid;
    private final NoiseRouterWithOnlyNoises noiseRouter;
    private final SurfaceRules.o surfaceRule;
    private final int seaLevel;
    private final boolean disableMobGeneration;
    private final boolean aquifersEnabled;
    private final boolean oreVeinsEnabled;
    private final boolean useLegacyRandomSource;
    public static final Codec<GeneratorSettingBase> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(NoiseSettings.CODEC.fieldOf("noise").forGetter(GeneratorSettingBase::noiseSettings), IBlockData.CODEC.fieldOf("default_block").forGetter(GeneratorSettingBase::defaultBlock), IBlockData.CODEC.fieldOf("default_fluid").forGetter(GeneratorSettingBase::defaultFluid), NoiseRouterWithOnlyNoises.CODEC.fieldOf("noise_router").forGetter(GeneratorSettingBase::noiseRouter), SurfaceRules.o.CODEC.fieldOf("surface_rule").forGetter(GeneratorSettingBase::surfaceRule), Codec.INT.fieldOf("sea_level").forGetter(GeneratorSettingBase::seaLevel), Codec.BOOL.fieldOf("disable_mob_generation").forGetter(GeneratorSettingBase::disableMobGeneration), Codec.BOOL.fieldOf("aquifers_enabled").forGetter(GeneratorSettingBase::isAquifersEnabled), Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(GeneratorSettingBase::oreVeinsEnabled), Codec.BOOL.fieldOf("legacy_random_source").forGetter(GeneratorSettingBase::useLegacyRandomSource)).apply(instance, GeneratorSettingBase::new);
    });
    public static final Codec<Holder<GeneratorSettingBase>> CODEC = RegistryFileCodec.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, GeneratorSettingBase.DIRECT_CODEC);
    public static final ResourceKey<GeneratorSettingBase> OVERWORLD = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("overworld"));
    public static final ResourceKey<GeneratorSettingBase> LARGE_BIOMES = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("large_biomes"));
    public static final ResourceKey<GeneratorSettingBase> AMPLIFIED = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("amplified"));
    public static final ResourceKey<GeneratorSettingBase> NETHER = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("nether"));
    public static final ResourceKey<GeneratorSettingBase> END = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("end"));
    public static final ResourceKey<GeneratorSettingBase> CAVES = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("caves"));
    public static final ResourceKey<GeneratorSettingBase> FLOATING_ISLANDS = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("floating_islands"));

    public GeneratorSettingBase(NoiseSettings noisesettings, IBlockData iblockdata, IBlockData iblockdata1, NoiseRouterWithOnlyNoises noiserouterwithonlynoises, SurfaceRules.o surfacerules_o, int i, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        this.noiseSettings = noisesettings;
        this.defaultBlock = iblockdata;
        this.defaultFluid = iblockdata1;
        this.noiseRouter = noiserouterwithonlynoises;
        this.surfaceRule = surfacerules_o;
        this.seaLevel = i;
        this.disableMobGeneration = flag;
        this.aquifersEnabled = flag1;
        this.oreVeinsEnabled = flag2;
        this.useLegacyRandomSource = flag3;
    }

    /** @deprecated */
    @Deprecated
    public boolean disableMobGeneration() {
        return this.disableMobGeneration;
    }

    public boolean isAquifersEnabled() {
        return this.aquifersEnabled;
    }

    public boolean oreVeinsEnabled() {
        return this.oreVeinsEnabled;
    }

    public SeededRandom.a getRandomSource() {
        return this.useLegacyRandomSource ? SeededRandom.a.LEGACY : SeededRandom.a.XOROSHIRO;
    }

    public NoiseRouter createNoiseRouter(IRegistry<NoiseGeneratorNormal.a> iregistry, long i) {
        return NoiseRouterData.createNoiseRouter(this.noiseSettings, i, iregistry, this.getRandomSource(), this.noiseRouter);
    }

    private static void register(ResourceKey<GeneratorSettingBase> resourcekey, GeneratorSettingBase generatorsettingbase) {
        RegistryGeneration.register(RegistryGeneration.NOISE_GENERATOR_SETTINGS, resourcekey.location(), generatorsettingbase);
    }

    public static Holder<GeneratorSettingBase> bootstrap() {
        return (Holder) RegistryGeneration.NOISE_GENERATOR_SETTINGS.holders().iterator().next();
    }

    private static GeneratorSettingBase end() {
        return new GeneratorSettingBase(NoiseSettings.END_NOISE_SETTINGS, Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), NoiseRouterData.end(NoiseSettings.END_NOISE_SETTINGS), SurfaceRuleData.end(), 0, true, false, false, true);
    }

    private static GeneratorSettingBase nether() {
        return new GeneratorSettingBase(NoiseSettings.NETHER_NOISE_SETTINGS, Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), NoiseRouterData.nether(NoiseSettings.NETHER_NOISE_SETTINGS), SurfaceRuleData.nether(), 32, false, false, false, true);
    }

    private static GeneratorSettingBase overworld(boolean flag, boolean flag1) {
        NoiseSettings noisesettings = NoiseSettings.overworldNoiseSettings(flag);

        return new GeneratorSettingBase(noisesettings, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), NoiseRouterData.overworld(noisesettings, flag1), SurfaceRuleData.overworld(), 63, false, true, true, false);
    }

    private static GeneratorSettingBase caves() {
        return new GeneratorSettingBase(NoiseSettings.CAVES_NOISE_SETTINGS, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), NoiseRouterData.overworldWithoutCaves(NoiseSettings.CAVES_NOISE_SETTINGS), SurfaceRuleData.overworldLike(false, true, true), 32, false, false, false, true);
    }

    private static GeneratorSettingBase floatingIslands() {
        return new GeneratorSettingBase(NoiseSettings.FLOATING_ISLANDS_NOISE_SETTINGS, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), NoiseRouterData.overworldWithoutCaves(NoiseSettings.FLOATING_ISLANDS_NOISE_SETTINGS), SurfaceRuleData.overworldLike(false, false, false), -64, false, false, false, true);
    }

    public NoiseSettings noiseSettings() {
        return this.noiseSettings;
    }

    public IBlockData defaultBlock() {
        return this.defaultBlock;
    }

    public IBlockData defaultFluid() {
        return this.defaultFluid;
    }

    public NoiseRouterWithOnlyNoises noiseRouter() {
        return this.noiseRouter;
    }

    public SurfaceRules.o surfaceRule() {
        return this.surfaceRule;
    }

    public int seaLevel() {
        return this.seaLevel;
    }

    public boolean aquifersEnabled() {
        return this.aquifersEnabled;
    }

    public boolean useLegacyRandomSource() {
        return this.useLegacyRandomSource;
    }

    static {
        register(GeneratorSettingBase.OVERWORLD, overworld(false, false));
        register(GeneratorSettingBase.LARGE_BIOMES, overworld(false, true));
        register(GeneratorSettingBase.AMPLIFIED, overworld(true, false));
        register(GeneratorSettingBase.NETHER, nether());
        register(GeneratorSettingBase.END, end());
        register(GeneratorSettingBase.CAVES, caves());
        register(GeneratorSettingBase.FLOATING_ISLANDS, floatingIslands());
    }
}

package net.minecraft.world.level.levelgen;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;

public final class GeneratorSettingBase {

    public static final Codec<GeneratorSettingBase> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(StructureSettings.CODEC.fieldOf("structures").forGetter(GeneratorSettingBase::a), NoiseSettings.CODEC.fieldOf("noise").forGetter(GeneratorSettingBase::b), IBlockData.CODEC.fieldOf("default_block").forGetter(GeneratorSettingBase::c), IBlockData.CODEC.fieldOf("default_fluid").forGetter(GeneratorSettingBase::d), Codec.INT.fieldOf("bedrock_roof_position").forGetter(GeneratorSettingBase::e), Codec.INT.fieldOf("bedrock_floor_position").forGetter(GeneratorSettingBase::f), Codec.INT.fieldOf("sea_level").forGetter(GeneratorSettingBase::g), Codec.INT.fieldOf("min_surface_level").forGetter(GeneratorSettingBase::h), Codec.BOOL.fieldOf("disable_mob_generation").forGetter(GeneratorSettingBase::i), Codec.BOOL.fieldOf("aquifers_enabled").forGetter(GeneratorSettingBase::j), Codec.BOOL.fieldOf("noise_caves_enabled").forGetter(GeneratorSettingBase::k), Codec.BOOL.fieldOf("deepslate_enabled").forGetter(GeneratorSettingBase::l), Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(GeneratorSettingBase::m), Codec.BOOL.fieldOf("noodle_caves_enabled").forGetter(GeneratorSettingBase::m)).apply(instance, GeneratorSettingBase::new);
    });
    public static final Codec<Supplier<GeneratorSettingBase>> CODEC = RegistryFileCodec.a(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, GeneratorSettingBase.DIRECT_CODEC);
    private final StructureSettings structureSettings;
    private final NoiseSettings noiseSettings;
    private final IBlockData defaultBlock;
    private final IBlockData defaultFluid;
    private final int bedrockRoofPosition;
    private final int bedrockFloorPosition;
    private final int seaLevel;
    private final int minSurfaceLevel;
    private final boolean disableMobGeneration;
    private final boolean aquifersEnabled;
    private final boolean noiseCavesEnabled;
    private final boolean deepslateEnabled;
    private final boolean oreVeinsEnabled;
    private final boolean noodleCavesEnabled;
    public static final ResourceKey<GeneratorSettingBase> OVERWORLD = ResourceKey.a(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("overworld"));
    public static final ResourceKey<GeneratorSettingBase> AMPLIFIED = ResourceKey.a(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("amplified"));
    public static final ResourceKey<GeneratorSettingBase> NETHER = ResourceKey.a(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("nether"));
    public static final ResourceKey<GeneratorSettingBase> END = ResourceKey.a(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("end"));
    public static final ResourceKey<GeneratorSettingBase> CAVES = ResourceKey.a(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("caves"));
    public static final ResourceKey<GeneratorSettingBase> FLOATING_ISLANDS = ResourceKey.a(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("floating_islands"));
    private static final GeneratorSettingBase BUILTIN_OVERWORLD = a(GeneratorSettingBase.OVERWORLD, a(new StructureSettings(true), false));

    private GeneratorSettingBase(StructureSettings structuresettings, NoiseSettings noisesettings, IBlockData iblockdata, IBlockData iblockdata1, int i, int j, int k, int l, boolean flag, boolean flag1, boolean flag2, boolean flag3, boolean flag4, boolean flag5) {
        this.structureSettings = structuresettings;
        this.noiseSettings = noisesettings;
        this.defaultBlock = iblockdata;
        this.defaultFluid = iblockdata1;
        this.bedrockRoofPosition = i;
        this.bedrockFloorPosition = j;
        this.seaLevel = k;
        this.minSurfaceLevel = l;
        this.disableMobGeneration = flag;
        this.aquifersEnabled = flag1;
        this.noiseCavesEnabled = flag2;
        this.deepslateEnabled = flag3;
        this.oreVeinsEnabled = flag4;
        this.noodleCavesEnabled = flag5;
    }

    public StructureSettings a() {
        return this.structureSettings;
    }

    public NoiseSettings b() {
        return this.noiseSettings;
    }

    public IBlockData c() {
        return this.defaultBlock;
    }

    public IBlockData d() {
        return this.defaultFluid;
    }

    public int e() {
        return this.bedrockRoofPosition;
    }

    public int f() {
        return this.bedrockFloorPosition;
    }

    public int g() {
        return this.seaLevel;
    }

    public int h() {
        return this.minSurfaceLevel;
    }

    @Deprecated
    protected boolean i() {
        return this.disableMobGeneration;
    }

    protected boolean j() {
        return this.aquifersEnabled;
    }

    protected boolean k() {
        return this.noiseCavesEnabled;
    }

    protected boolean l() {
        return this.deepslateEnabled;
    }

    protected boolean m() {
        return this.oreVeinsEnabled;
    }

    protected boolean n() {
        return this.noodleCavesEnabled;
    }

    public boolean a(ResourceKey<GeneratorSettingBase> resourcekey) {
        return Objects.equals(this, RegistryGeneration.NOISE_GENERATOR_SETTINGS.a(resourcekey));
    }

    private static GeneratorSettingBase a(ResourceKey<GeneratorSettingBase> resourcekey, GeneratorSettingBase generatorsettingbase) {
        RegistryGeneration.a(RegistryGeneration.NOISE_GENERATOR_SETTINGS, resourcekey.a(), (Object) generatorsettingbase);
        return generatorsettingbase;
    }

    public static GeneratorSettingBase o() {
        return GeneratorSettingBase.BUILTIN_OVERWORLD;
    }

    private static GeneratorSettingBase a(StructureSettings structuresettings, IBlockData iblockdata, IBlockData iblockdata1, boolean flag, boolean flag1) {
        return new GeneratorSettingBase(structuresettings, NoiseSettings.a(0, 128, new NoiseSamplingSettings(2.0D, 1.0D, 80.0D, 160.0D), new NoiseSlideSettings(-3000, 64, -46), new NoiseSlideSettings(-30, 7, 1), 2, 1, 0.0D, 0.0D, true, false, flag1, false), iblockdata, iblockdata1, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0, flag, false, false, false, false, false);
    }

    private static GeneratorSettingBase a(StructureSettings structuresettings, IBlockData iblockdata, IBlockData iblockdata1) {
        Map<StructureGenerator<?>, StructureSettingsFeature> map = Maps.newHashMap(StructureSettings.DEFAULTS);

        map.put(StructureGenerator.RUINED_PORTAL, new StructureSettingsFeature(25, 10, 34222645));
        return new GeneratorSettingBase(new StructureSettings(Optional.ofNullable(structuresettings.b()), map), NoiseSettings.a(0, 128, new NoiseSamplingSettings(1.0D, 3.0D, 80.0D, 60.0D), new NoiseSlideSettings(120, 3, 0), new NoiseSlideSettings(320, 4, -1), 1, 2, 0.0D, 0.019921875D, false, false, false, false), iblockdata, iblockdata1, 0, 0, 32, 0, false, false, false, false, false, false);
    }

    private static GeneratorSettingBase a(StructureSettings structuresettings, boolean flag) {
        double d0 = 0.9999999814507745D;

        return new GeneratorSettingBase(structuresettings, NoiseSettings.a(0, 256, new NoiseSamplingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D), new NoiseSlideSettings(-10, 3, 0), new NoiseSlideSettings(15, 3, 0), 1, 2, 1.0D, -0.46875D, true, true, false, flag), Blocks.STONE.getBlockData(), Blocks.WATER.getBlockData(), Integer.MIN_VALUE, 0, 63, 0, false, false, false, false, false, false);
    }

    static {
        a(GeneratorSettingBase.AMPLIFIED, a(new StructureSettings(true), true));
        a(GeneratorSettingBase.NETHER, a(new StructureSettings(false), Blocks.NETHERRACK.getBlockData(), Blocks.LAVA.getBlockData()));
        a(GeneratorSettingBase.END, a(new StructureSettings(false), Blocks.END_STONE.getBlockData(), Blocks.AIR.getBlockData(), true, true));
        a(GeneratorSettingBase.CAVES, a(new StructureSettings(true), Blocks.STONE.getBlockData(), Blocks.WATER.getBlockData()));
        a(GeneratorSettingBase.FLOATING_ISLANDS, a(new StructureSettings(true), Blocks.STONE.getBlockData(), Blocks.WATER.getBlockData(), false, false));
    }
}

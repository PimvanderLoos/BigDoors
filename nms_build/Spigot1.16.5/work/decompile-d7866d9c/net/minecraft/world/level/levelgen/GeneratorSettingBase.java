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

    public static final Codec<GeneratorSettingBase> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(StructureSettings.a.fieldOf("structures").forGetter(GeneratorSettingBase::a), NoiseSettings.a.fieldOf("noise").forGetter(GeneratorSettingBase::b), IBlockData.b.fieldOf("default_block").forGetter(GeneratorSettingBase::c), IBlockData.b.fieldOf("default_fluid").forGetter(GeneratorSettingBase::d), Codec.intRange(-20, 276).fieldOf("bedrock_roof_position").forGetter(GeneratorSettingBase::e), Codec.intRange(-20, 276).fieldOf("bedrock_floor_position").forGetter(GeneratorSettingBase::f), Codec.intRange(0, 255).fieldOf("sea_level").forGetter(GeneratorSettingBase::g), Codec.BOOL.fieldOf("disable_mob_generation").forGetter(GeneratorSettingBase::h)).apply(instance, GeneratorSettingBase::new);
    });
    public static final Codec<Supplier<GeneratorSettingBase>> b = RegistryFileCodec.a(IRegistry.ar, GeneratorSettingBase.a);
    private final StructureSettings i;
    private final NoiseSettings j;
    private final IBlockData k;
    private final IBlockData l;
    private final int m;
    private final int n;
    private final int o;
    private final boolean p;
    public static final ResourceKey<GeneratorSettingBase> c = ResourceKey.a(IRegistry.ar, new MinecraftKey("overworld"));
    public static final ResourceKey<GeneratorSettingBase> d = ResourceKey.a(IRegistry.ar, new MinecraftKey("amplified"));
    public static final ResourceKey<GeneratorSettingBase> e = ResourceKey.a(IRegistry.ar, new MinecraftKey("nether"));
    public static final ResourceKey<GeneratorSettingBase> f = ResourceKey.a(IRegistry.ar, new MinecraftKey("end"));
    public static final ResourceKey<GeneratorSettingBase> g = ResourceKey.a(IRegistry.ar, new MinecraftKey("caves"));
    public static final ResourceKey<GeneratorSettingBase> h = ResourceKey.a(IRegistry.ar, new MinecraftKey("floating_islands"));
    private static final GeneratorSettingBase q = a(GeneratorSettingBase.c, a(new StructureSettings(true), false, GeneratorSettingBase.c.a()));

    private GeneratorSettingBase(StructureSettings structuresettings, NoiseSettings noisesettings, IBlockData iblockdata, IBlockData iblockdata1, int i, int j, int k, boolean flag) {
        this.i = structuresettings;
        this.j = noisesettings;
        this.k = iblockdata;
        this.l = iblockdata1;
        this.m = i;
        this.n = j;
        this.o = k;
        this.p = flag;
    }

    public StructureSettings a() {
        return this.i;
    }

    public NoiseSettings b() {
        return this.j;
    }

    public IBlockData c() {
        return this.k;
    }

    public IBlockData d() {
        return this.l;
    }

    public int e() {
        return this.m;
    }

    public int f() {
        return this.n;
    }

    public int g() {
        return this.o;
    }

    @Deprecated
    protected boolean h() {
        return this.p;
    }

    public boolean a(ResourceKey<GeneratorSettingBase> resourcekey) {
        return Objects.equals(this, RegistryGeneration.j.a(resourcekey));
    }

    private static GeneratorSettingBase a(ResourceKey<GeneratorSettingBase> resourcekey, GeneratorSettingBase generatorsettingbase) {
        RegistryGeneration.a(RegistryGeneration.j, resourcekey.a(), (Object) generatorsettingbase);
        return generatorsettingbase;
    }

    public static GeneratorSettingBase i() {
        return GeneratorSettingBase.q;
    }

    private static GeneratorSettingBase a(StructureSettings structuresettings, IBlockData iblockdata, IBlockData iblockdata1, MinecraftKey minecraftkey, boolean flag, boolean flag1) {
        return new GeneratorSettingBase(structuresettings, new NoiseSettings(128, new NoiseSamplingSettings(2.0D, 1.0D, 80.0D, 160.0D), new NoiseSlideSettings(-3000, 64, -46), new NoiseSlideSettings(-30, 7, 1), 2, 1, 0.0D, 0.0D, true, false, flag1, false), iblockdata, iblockdata1, -10, -10, 0, flag);
    }

    private static GeneratorSettingBase a(StructureSettings structuresettings, IBlockData iblockdata, IBlockData iblockdata1, MinecraftKey minecraftkey) {
        Map<StructureGenerator<?>, StructureSettingsFeature> map = Maps.newHashMap(StructureSettings.b);

        map.put(StructureGenerator.RUINED_PORTAL, new StructureSettingsFeature(25, 10, 34222645));
        return new GeneratorSettingBase(new StructureSettings(Optional.ofNullable(structuresettings.b()), map), new NoiseSettings(128, new NoiseSamplingSettings(1.0D, 3.0D, 80.0D, 60.0D), new NoiseSlideSettings(120, 3, 0), new NoiseSlideSettings(320, 4, -1), 1, 2, 0.0D, 0.019921875D, false, false, false, false), iblockdata, iblockdata1, 0, 0, 32, false);
    }

    private static GeneratorSettingBase a(StructureSettings structuresettings, boolean flag, MinecraftKey minecraftkey) {
        double d0 = 0.9999999814507745D;

        return new GeneratorSettingBase(structuresettings, new NoiseSettings(256, new NoiseSamplingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D), new NoiseSlideSettings(-10, 3, 0), new NoiseSlideSettings(-30, 0, 0), 1, 2, 1.0D, -0.46875D, true, true, false, flag), Blocks.STONE.getBlockData(), Blocks.WATER.getBlockData(), -10, 0, 63, false);
    }

    static {
        a(GeneratorSettingBase.d, a(new StructureSettings(true), true, GeneratorSettingBase.d.a()));
        a(GeneratorSettingBase.e, a(new StructureSettings(false), Blocks.NETHERRACK.getBlockData(), Blocks.LAVA.getBlockData(), GeneratorSettingBase.e.a()));
        a(GeneratorSettingBase.f, a(new StructureSettings(false), Blocks.END_STONE.getBlockData(), Blocks.AIR.getBlockData(), GeneratorSettingBase.f.a(), true, true));
        a(GeneratorSettingBase.g, a(new StructureSettings(true), Blocks.STONE.getBlockData(), Blocks.WATER.getBlockData(), GeneratorSettingBase.g.a()));
        a(GeneratorSettingBase.h, a(new StructureSettings(true), Blocks.STONE.getBlockData(), Blocks.WATER.getBlockData(), GeneratorSettingBase.h.a(), false, false));
    }
}

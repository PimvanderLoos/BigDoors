package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class GeneratorSettingBase {

    public static final Codec<GeneratorSettingBase> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(StructureSettings.a.fieldOf("structures").forGetter(GeneratorSettingBase::a), NoiseSettings.a.fieldOf("noise").forGetter(GeneratorSettingBase::b), IBlockData.b.fieldOf("default_block").forGetter(GeneratorSettingBase::c), IBlockData.b.fieldOf("default_fluid").forGetter(GeneratorSettingBase::d), Codecs.a(-20, 276).fieldOf("bedrock_roof_position").forGetter(GeneratorSettingBase::e), Codecs.a(-20, 276).fieldOf("bedrock_floor_position").forGetter(GeneratorSettingBase::f), Codecs.a(0, 255).fieldOf("sea_level").forGetter(GeneratorSettingBase::g), Codec.BOOL.fieldOf("disable_mob_generation").forGetter(GeneratorSettingBase::h)).apply(instance, GeneratorSettingBase::new);
    });
    public static final Codec<GeneratorSettingBase> b = Codec.either(GeneratorSettingBase.a.a, GeneratorSettingBase.a).xmap((either) -> {
        return (GeneratorSettingBase) either.map(GeneratorSettingBase.a::b, Function.identity());
    }, (generatorsettingbase) -> {
        return (Either) generatorsettingbase.k.map(Either::left).orElseGet(() -> {
            return Either.right(generatorsettingbase);
        });
    });
    private final StructureSettings c;
    private final NoiseSettings d;
    private final IBlockData e;
    private final IBlockData f;
    private final int g;
    private final int h;
    private final int i;
    private final boolean j;
    private final Optional<GeneratorSettingBase.a> k;

    private GeneratorSettingBase(StructureSettings structuresettings, NoiseSettings noisesettings, IBlockData iblockdata, IBlockData iblockdata1, int i, int j, int k, boolean flag) {
        this(structuresettings, noisesettings, iblockdata, iblockdata1, i, j, k, flag, Optional.empty());
    }

    private GeneratorSettingBase(StructureSettings structuresettings, NoiseSettings noisesettings, IBlockData iblockdata, IBlockData iblockdata1, int i, int j, int k, boolean flag, Optional<GeneratorSettingBase.a> optional) {
        this.c = structuresettings;
        this.d = noisesettings;
        this.e = iblockdata;
        this.f = iblockdata1;
        this.g = i;
        this.h = j;
        this.i = k;
        this.j = flag;
        this.k = optional;
    }

    public StructureSettings a() {
        return this.c;
    }

    public NoiseSettings b() {
        return this.d;
    }

    public IBlockData c() {
        return this.e;
    }

    public IBlockData d() {
        return this.f;
    }

    public int e() {
        return this.g;
    }

    public int f() {
        return this.h;
    }

    public int g() {
        return this.i;
    }

    @Deprecated
    protected boolean h() {
        return this.j;
    }

    public boolean a(GeneratorSettingBase.a generatorsettingbase_a) {
        return Objects.equals(this.k, Optional.of(generatorsettingbase_a));
    }

    public static class a {

        private static final Map<MinecraftKey, GeneratorSettingBase.a> h = Maps.newHashMap();
        public static final Codec<GeneratorSettingBase.a> a = MinecraftKey.a.flatXmap((minecraftkey) -> {
            return (DataResult) Optional.ofNullable(GeneratorSettingBase.a.h.get(minecraftkey)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown preset: " + minecraftkey);
            });
        }, (generatorsettingbase_a) -> {
            return DataResult.success(generatorsettingbase_a.j);
        }).stable();
        public static final GeneratorSettingBase.a b = new GeneratorSettingBase.a("overworld", (generatorsettingbase_a) -> {
            return a(new StructureSettings(true), false, generatorsettingbase_a);
        });
        public static final GeneratorSettingBase.a c = new GeneratorSettingBase.a("amplified", (generatorsettingbase_a) -> {
            return a(new StructureSettings(true), true, generatorsettingbase_a);
        });
        public static final GeneratorSettingBase.a d = new GeneratorSettingBase.a("nether", (generatorsettingbase_a) -> {
            return a(new StructureSettings(false), Blocks.NETHERRACK.getBlockData(), Blocks.LAVA.getBlockData(), generatorsettingbase_a);
        });
        public static final GeneratorSettingBase.a e = new GeneratorSettingBase.a("end", (generatorsettingbase_a) -> {
            return a(new StructureSettings(false), Blocks.END_STONE.getBlockData(), Blocks.AIR.getBlockData(), generatorsettingbase_a, true, true);
        });
        public static final GeneratorSettingBase.a f = new GeneratorSettingBase.a("caves", (generatorsettingbase_a) -> {
            return a(new StructureSettings(false), Blocks.STONE.getBlockData(), Blocks.WATER.getBlockData(), generatorsettingbase_a);
        });
        public static final GeneratorSettingBase.a g = new GeneratorSettingBase.a("floating_islands", (generatorsettingbase_a) -> {
            return a(new StructureSettings(false), Blocks.STONE.getBlockData(), Blocks.WATER.getBlockData(), generatorsettingbase_a, false, false);
        });
        private final IChatBaseComponent i;
        private final MinecraftKey j;
        private final GeneratorSettingBase k;

        public a(String s, Function<GeneratorSettingBase.a, GeneratorSettingBase> function) {
            this.j = new MinecraftKey(s);
            this.i = new ChatMessage("generator.noise." + s);
            this.k = (GeneratorSettingBase) function.apply(this);
            GeneratorSettingBase.a.h.put(this.j, this);
        }

        public GeneratorSettingBase b() {
            return this.k;
        }

        private static GeneratorSettingBase a(StructureSettings structuresettings, IBlockData iblockdata, IBlockData iblockdata1, GeneratorSettingBase.a generatorsettingbase_a, boolean flag, boolean flag1) {
            return new GeneratorSettingBase(structuresettings, new NoiseSettings(128, new NoiseSamplingSettings(2.0D, 1.0D, 80.0D, 160.0D), new NoiseSlideSettings(-3000, 64, -46), new NoiseSlideSettings(-30, 7, 1), 2, 1, 0.0D, 0.0D, true, false, flag1, false), iblockdata, iblockdata1, -10, -10, 0, flag, Optional.of(generatorsettingbase_a));
        }

        private static GeneratorSettingBase a(StructureSettings structuresettings, IBlockData iblockdata, IBlockData iblockdata1, GeneratorSettingBase.a generatorsettingbase_a) {
            Map<StructureGenerator<?>, StructureSettingsFeature> map = Maps.newHashMap(StructureSettings.b);

            map.put(StructureGenerator.RUINED_PORTAL, new StructureSettingsFeature(25, 10, 34222645));
            return new GeneratorSettingBase(new StructureSettings(Optional.ofNullable(structuresettings.b()), map), new NoiseSettings(128, new NoiseSamplingSettings(1.0D, 3.0D, 80.0D, 60.0D), new NoiseSlideSettings(120, 3, 0), new NoiseSlideSettings(320, 4, -1), 1, 2, 0.0D, 0.019921875D, false, false, false, false), iblockdata, iblockdata1, 0, 0, 32, false, Optional.of(generatorsettingbase_a));
        }

        private static GeneratorSettingBase a(StructureSettings structuresettings, boolean flag, GeneratorSettingBase.a generatorsettingbase_a) {
            double d0 = 0.9999999814507745D;

            return new GeneratorSettingBase(structuresettings, new NoiseSettings(256, new NoiseSamplingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D), new NoiseSlideSettings(-10, 3, 0), new NoiseSlideSettings(-30, 0, 0), 1, 2, 1.0D, -0.46875D, true, true, false, flag), Blocks.STONE.getBlockData(), Blocks.WATER.getBlockData(), -10, 0, 63, false, Optional.of(generatorsettingbase_a));
        }
    }
}

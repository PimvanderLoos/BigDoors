package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.INamable;

public class BiomeFog {

    public static final Codec<BiomeFog> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("fog_color").forGetter((biomefog) -> {
            return biomefog.b;
        }), Codec.INT.fieldOf("water_color").forGetter((biomefog) -> {
            return biomefog.c;
        }), Codec.INT.fieldOf("water_fog_color").forGetter((biomefog) -> {
            return biomefog.d;
        }), Codec.INT.fieldOf("sky_color").forGetter((biomefog) -> {
            return biomefog.e;
        }), Codec.INT.optionalFieldOf("foliage_color").forGetter((biomefog) -> {
            return biomefog.f;
        }), Codec.INT.optionalFieldOf("grass_color").forGetter((biomefog) -> {
            return biomefog.g;
        }), BiomeFog.GrassColor.d.optionalFieldOf("grass_color_modifier", BiomeFog.GrassColor.NONE).forGetter((biomefog) -> {
            return biomefog.h;
        }), BiomeParticles.a.optionalFieldOf("particle").forGetter((biomefog) -> {
            return biomefog.i;
        }), SoundEffect.a.optionalFieldOf("ambient_sound").forGetter((biomefog) -> {
            return biomefog.j;
        }), CaveSoundSettings.a.optionalFieldOf("mood_sound").forGetter((biomefog) -> {
            return biomefog.k;
        }), CaveSound.a.optionalFieldOf("additions_sound").forGetter((biomefog) -> {
            return biomefog.l;
        }), Music.a.optionalFieldOf("music").forGetter((biomefog) -> {
            return biomefog.m;
        })).apply(instance, BiomeFog::new);
    });
    private final int b;
    private final int c;
    private final int d;
    private final int e;
    private final Optional<Integer> f;
    private final Optional<Integer> g;
    private final BiomeFog.GrassColor h;
    private final Optional<BiomeParticles> i;
    private final Optional<SoundEffect> j;
    private final Optional<CaveSoundSettings> k;
    private final Optional<CaveSound> l;
    private final Optional<Music> m;

    private BiomeFog(int i, int j, int k, int l, Optional<Integer> optional, Optional<Integer> optional1, BiomeFog.GrassColor biomefog_grasscolor, Optional<BiomeParticles> optional2, Optional<SoundEffect> optional3, Optional<CaveSoundSettings> optional4, Optional<CaveSound> optional5, Optional<Music> optional6) {
        this.b = i;
        this.c = j;
        this.d = k;
        this.e = l;
        this.f = optional;
        this.g = optional1;
        this.h = biomefog_grasscolor;
        this.i = optional2;
        this.j = optional3;
        this.k = optional4;
        this.l = optional5;
        this.m = optional6;
    }

    public static enum GrassColor implements INamable {

        NONE("none") {
        },
        DARK_FOREST("dark_forest") {
        },
        SWAMP("swamp") {
        };

        private final String e;
        public static final Codec<BiomeFog.GrassColor> d = INamable.a(BiomeFog.GrassColor::values, BiomeFog.GrassColor::a);
        private static final Map<String, BiomeFog.GrassColor> f = (Map) Arrays.stream(values()).collect(Collectors.toMap(BiomeFog.GrassColor::b, (biomefog_grasscolor) -> {
            return biomefog_grasscolor;
        }));

        private GrassColor(String s) {
            this.e = s;
        }

        public String b() {
            return this.e;
        }

        @Override
        public String getName() {
            return this.e;
        }

        public static BiomeFog.GrassColor a(String s) {
            return (BiomeFog.GrassColor) BiomeFog.GrassColor.f.get(s);
        }
    }

    public static class a {

        private OptionalInt a = OptionalInt.empty();
        private OptionalInt b = OptionalInt.empty();
        private OptionalInt c = OptionalInt.empty();
        private OptionalInt d = OptionalInt.empty();
        private Optional<Integer> e = Optional.empty();
        private Optional<Integer> f = Optional.empty();
        private BiomeFog.GrassColor g;
        private Optional<BiomeParticles> h;
        private Optional<SoundEffect> i;
        private Optional<CaveSoundSettings> j;
        private Optional<CaveSound> k;
        private Optional<Music> l;

        public a() {
            this.g = BiomeFog.GrassColor.NONE;
            this.h = Optional.empty();
            this.i = Optional.empty();
            this.j = Optional.empty();
            this.k = Optional.empty();
            this.l = Optional.empty();
        }

        public BiomeFog.a a(int i) {
            this.a = OptionalInt.of(i);
            return this;
        }

        public BiomeFog.a b(int i) {
            this.b = OptionalInt.of(i);
            return this;
        }

        public BiomeFog.a c(int i) {
            this.c = OptionalInt.of(i);
            return this;
        }

        public BiomeFog.a d(int i) {
            this.d = OptionalInt.of(i);
            return this;
        }

        public BiomeFog.a e(int i) {
            this.e = Optional.of(i);
            return this;
        }

        public BiomeFog.a f(int i) {
            this.f = Optional.of(i);
            return this;
        }

        public BiomeFog.a a(BiomeFog.GrassColor biomefog_grasscolor) {
            this.g = biomefog_grasscolor;
            return this;
        }

        public BiomeFog.a a(BiomeParticles biomeparticles) {
            this.h = Optional.of(biomeparticles);
            return this;
        }

        public BiomeFog.a a(SoundEffect soundeffect) {
            this.i = Optional.of(soundeffect);
            return this;
        }

        public BiomeFog.a a(CaveSoundSettings cavesoundsettings) {
            this.j = Optional.of(cavesoundsettings);
            return this;
        }

        public BiomeFog.a a(CaveSound cavesound) {
            this.k = Optional.of(cavesound);
            return this;
        }

        public BiomeFog.a a(Music music) {
            this.l = Optional.of(music);
            return this;
        }

        public BiomeFog a() {
            return new BiomeFog(this.a.orElseThrow(() -> {
                return new IllegalStateException("Missing 'fog' color.");
            }), this.b.orElseThrow(() -> {
                return new IllegalStateException("Missing 'water' color.");
            }), this.c.orElseThrow(() -> {
                return new IllegalStateException("Missing 'water fog' color.");
            }), this.d.orElseThrow(() -> {
                return new IllegalStateException("Missing 'sky' color.");
            }), this.e, this.f, this.g, this.h, this.i, this.j, this.k, this.l);
        }
    }
}

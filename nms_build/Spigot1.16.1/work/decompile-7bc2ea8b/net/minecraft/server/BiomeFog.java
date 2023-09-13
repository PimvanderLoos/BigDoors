package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;

public class BiomeFog {

    public static final Codec<BiomeFog> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("fog_color").forGetter((biomefog) -> {
            return biomefog.b;
        }), Codec.INT.fieldOf("water_color").forGetter((biomefog) -> {
            return biomefog.c;
        }), Codec.INT.fieldOf("water_fog_color").forGetter((biomefog) -> {
            return biomefog.d;
        }), BiomeParticles.a.optionalFieldOf("particle").forGetter((biomefog) -> {
            return biomefog.e;
        }), SoundEffect.a.optionalFieldOf("ambient_sound").forGetter((biomefog) -> {
            return biomefog.f;
        }), CaveSoundSettings.a.optionalFieldOf("mood_sound").forGetter((biomefog) -> {
            return biomefog.g;
        }), CaveSound.a.optionalFieldOf("additions_sound").forGetter((biomefog) -> {
            return biomefog.h;
        }), Music.a.optionalFieldOf("music").forGetter((biomefog) -> {
            return biomefog.i;
        })).apply(instance, BiomeFog::new);
    });
    private final int b;
    private final int c;
    private final int d;
    private final Optional<BiomeParticles> e;
    private final Optional<SoundEffect> f;
    private final Optional<CaveSoundSettings> g;
    private final Optional<CaveSound> h;
    private final Optional<Music> i;

    private BiomeFog(int i, int j, int k, Optional<BiomeParticles> optional, Optional<SoundEffect> optional1, Optional<CaveSoundSettings> optional2, Optional<CaveSound> optional3, Optional<Music> optional4) {
        this.b = i;
        this.c = j;
        this.d = k;
        this.e = optional;
        this.f = optional1;
        this.g = optional2;
        this.h = optional3;
        this.i = optional4;
    }

    public static class a {

        private OptionalInt a = OptionalInt.empty();
        private OptionalInt b = OptionalInt.empty();
        private OptionalInt c = OptionalInt.empty();
        private Optional<BiomeParticles> d = Optional.empty();
        private Optional<SoundEffect> e = Optional.empty();
        private Optional<CaveSoundSettings> f = Optional.empty();
        private Optional<CaveSound> g = Optional.empty();
        private Optional<Music> h = Optional.empty();

        public a() {}

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

        public BiomeFog.a a(BiomeParticles biomeparticles) {
            this.d = Optional.of(biomeparticles);
            return this;
        }

        public BiomeFog.a a(SoundEffect soundeffect) {
            this.e = Optional.of(soundeffect);
            return this;
        }

        public BiomeFog.a a(CaveSoundSettings cavesoundsettings) {
            this.f = Optional.of(cavesoundsettings);
            return this;
        }

        public BiomeFog.a a(CaveSound cavesound) {
            this.g = Optional.of(cavesound);
            return this;
        }

        public BiomeFog.a a(Music music) {
            this.h = Optional.of(music);
            return this;
        }

        public BiomeFog a() {
            return new BiomeFog(this.a.orElseThrow(() -> {
                return new IllegalStateException("Missing 'fog' color.");
            }), this.b.orElseThrow(() -> {
                return new IllegalStateException("Missing 'water' color.");
            }), this.c.orElseThrow(() -> {
                return new IllegalStateException("Missing 'water fog' color.");
            }), this.d, this.e, this.f, this.g, this.h);
        }
    }
}

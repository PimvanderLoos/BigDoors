package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.INamable;

public class BiomeFog {

    public static final Codec<BiomeFog> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("fog_color").forGetter((biomefog) -> {
            return biomefog.fogColor;
        }), Codec.INT.fieldOf("water_color").forGetter((biomefog) -> {
            return biomefog.waterColor;
        }), Codec.INT.fieldOf("water_fog_color").forGetter((biomefog) -> {
            return biomefog.waterFogColor;
        }), Codec.INT.fieldOf("sky_color").forGetter((biomefog) -> {
            return biomefog.skyColor;
        }), Codec.INT.optionalFieldOf("foliage_color").forGetter((biomefog) -> {
            return biomefog.foliageColorOverride;
        }), Codec.INT.optionalFieldOf("grass_color").forGetter((biomefog) -> {
            return biomefog.grassColorOverride;
        }), BiomeFog.GrassColor.CODEC.optionalFieldOf("grass_color_modifier", BiomeFog.GrassColor.NONE).forGetter((biomefog) -> {
            return biomefog.grassColorModifier;
        }), BiomeParticles.CODEC.optionalFieldOf("particle").forGetter((biomefog) -> {
            return biomefog.ambientParticleSettings;
        }), SoundEffect.CODEC.optionalFieldOf("ambient_sound").forGetter((biomefog) -> {
            return biomefog.ambientLoopSoundEvent;
        }), CaveSoundSettings.CODEC.optionalFieldOf("mood_sound").forGetter((biomefog) -> {
            return biomefog.ambientMoodSettings;
        }), CaveSound.CODEC.optionalFieldOf("additions_sound").forGetter((biomefog) -> {
            return biomefog.ambientAdditionsSettings;
        }), Music.CODEC.optionalFieldOf("music").forGetter((biomefog) -> {
            return biomefog.backgroundMusic;
        })).apply(instance, BiomeFog::new);
    });
    private final int fogColor;
    private final int waterColor;
    private final int waterFogColor;
    private final int skyColor;
    private final Optional<Integer> foliageColorOverride;
    private final Optional<Integer> grassColorOverride;
    private final BiomeFog.GrassColor grassColorModifier;
    private final Optional<BiomeParticles> ambientParticleSettings;
    private final Optional<Holder<SoundEffect>> ambientLoopSoundEvent;
    private final Optional<CaveSoundSettings> ambientMoodSettings;
    private final Optional<CaveSound> ambientAdditionsSettings;
    private final Optional<Music> backgroundMusic;

    BiomeFog(int i, int j, int k, int l, Optional<Integer> optional, Optional<Integer> optional1, BiomeFog.GrassColor biomefog_grasscolor, Optional<BiomeParticles> optional2, Optional<Holder<SoundEffect>> optional3, Optional<CaveSoundSettings> optional4, Optional<CaveSound> optional5, Optional<Music> optional6) {
        this.fogColor = i;
        this.waterColor = j;
        this.waterFogColor = k;
        this.skyColor = l;
        this.foliageColorOverride = optional;
        this.grassColorOverride = optional1;
        this.grassColorModifier = biomefog_grasscolor;
        this.ambientParticleSettings = optional2;
        this.ambientLoopSoundEvent = optional3;
        this.ambientMoodSettings = optional4;
        this.ambientAdditionsSettings = optional5;
        this.backgroundMusic = optional6;
    }

    public int getFogColor() {
        return this.fogColor;
    }

    public int getWaterColor() {
        return this.waterColor;
    }

    public int getWaterFogColor() {
        return this.waterFogColor;
    }

    public int getSkyColor() {
        return this.skyColor;
    }

    public Optional<Integer> getFoliageColorOverride() {
        return this.foliageColorOverride;
    }

    public Optional<Integer> getGrassColorOverride() {
        return this.grassColorOverride;
    }

    public BiomeFog.GrassColor getGrassColorModifier() {
        return this.grassColorModifier;
    }

    public Optional<BiomeParticles> getAmbientParticleSettings() {
        return this.ambientParticleSettings;
    }

    public Optional<Holder<SoundEffect>> getAmbientLoopSoundEvent() {
        return this.ambientLoopSoundEvent;
    }

    public Optional<CaveSoundSettings> getAmbientMoodSettings() {
        return this.ambientMoodSettings;
    }

    public Optional<CaveSound> getAmbientAdditionsSettings() {
        return this.ambientAdditionsSettings;
    }

    public Optional<Music> getBackgroundMusic() {
        return this.backgroundMusic;
    }

    public static enum GrassColor implements INamable {

        NONE("none") {
            @Override
            public int modifyColor(double d0, double d1, int i) {
                return i;
            }
        },
        DARK_FOREST("dark_forest") {
            @Override
            public int modifyColor(double d0, double d1, int i) {
                return (i & 16711422) + 2634762 >> 1;
            }
        },
        SWAMP("swamp") {
            @Override
            public int modifyColor(double d0, double d1, int i) {
                double d2 = BiomeBase.BIOME_INFO_NOISE.getValue(d0 * 0.0225D, d1 * 0.0225D, false);

                return d2 < -0.1D ? 5011004 : 6975545;
            }
        };

        private final String name;
        public static final Codec<BiomeFog.GrassColor> CODEC = INamable.fromEnum(BiomeFog.GrassColor::values);

        public abstract int modifyColor(double d0, double d1, int i);

        GrassColor(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static class a {

        private OptionalInt fogColor = OptionalInt.empty();
        private OptionalInt waterColor = OptionalInt.empty();
        private OptionalInt waterFogColor = OptionalInt.empty();
        private OptionalInt skyColor = OptionalInt.empty();
        private Optional<Integer> foliageColorOverride = Optional.empty();
        private Optional<Integer> grassColorOverride = Optional.empty();
        private BiomeFog.GrassColor grassColorModifier;
        private Optional<BiomeParticles> ambientParticle;
        private Optional<Holder<SoundEffect>> ambientLoopSoundEvent;
        private Optional<CaveSoundSettings> ambientMoodSettings;
        private Optional<CaveSound> ambientAdditionsSettings;
        private Optional<Music> backgroundMusic;

        public a() {
            this.grassColorModifier = BiomeFog.GrassColor.NONE;
            this.ambientParticle = Optional.empty();
            this.ambientLoopSoundEvent = Optional.empty();
            this.ambientMoodSettings = Optional.empty();
            this.ambientAdditionsSettings = Optional.empty();
            this.backgroundMusic = Optional.empty();
        }

        public BiomeFog.a fogColor(int i) {
            this.fogColor = OptionalInt.of(i);
            return this;
        }

        public BiomeFog.a waterColor(int i) {
            this.waterColor = OptionalInt.of(i);
            return this;
        }

        public BiomeFog.a waterFogColor(int i) {
            this.waterFogColor = OptionalInt.of(i);
            return this;
        }

        public BiomeFog.a skyColor(int i) {
            this.skyColor = OptionalInt.of(i);
            return this;
        }

        public BiomeFog.a foliageColorOverride(int i) {
            this.foliageColorOverride = Optional.of(i);
            return this;
        }

        public BiomeFog.a grassColorOverride(int i) {
            this.grassColorOverride = Optional.of(i);
            return this;
        }

        public BiomeFog.a grassColorModifier(BiomeFog.GrassColor biomefog_grasscolor) {
            this.grassColorModifier = biomefog_grasscolor;
            return this;
        }

        public BiomeFog.a ambientParticle(BiomeParticles biomeparticles) {
            this.ambientParticle = Optional.of(biomeparticles);
            return this;
        }

        public BiomeFog.a ambientLoopSound(Holder<SoundEffect> holder) {
            this.ambientLoopSoundEvent = Optional.of(holder);
            return this;
        }

        public BiomeFog.a ambientMoodSound(CaveSoundSettings cavesoundsettings) {
            this.ambientMoodSettings = Optional.of(cavesoundsettings);
            return this;
        }

        public BiomeFog.a ambientAdditionsSound(CaveSound cavesound) {
            this.ambientAdditionsSettings = Optional.of(cavesound);
            return this;
        }

        public BiomeFog.a backgroundMusic(@Nullable Music music) {
            this.backgroundMusic = Optional.ofNullable(music);
            return this;
        }

        public BiomeFog build() {
            return new BiomeFog(this.fogColor.orElseThrow(() -> {
                return new IllegalStateException("Missing 'fog' color.");
            }), this.waterColor.orElseThrow(() -> {
                return new IllegalStateException("Missing 'water' color.");
            }), this.waterFogColor.orElseThrow(() -> {
                return new IllegalStateException("Missing 'water fog' color.");
            }), this.skyColor.orElseThrow(() -> {
                return new IllegalStateException("Missing 'sky' color.");
            }), this.foliageColorOverride, this.grassColorOverride, this.grassColorModifier, this.ambientParticle, this.ambientLoopSoundEvent, this.ambientMoodSettings, this.ambientAdditionsSettings, this.backgroundMusic);
        }
    }
}

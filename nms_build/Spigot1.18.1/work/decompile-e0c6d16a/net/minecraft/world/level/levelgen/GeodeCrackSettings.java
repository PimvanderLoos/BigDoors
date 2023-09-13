package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;

public class GeodeCrackSettings {

    public static final Codec<GeodeCrackSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(GeodeConfiguration.CHANCE_RANGE.fieldOf("generate_crack_chance").orElse(1.0D).forGetter((geodecracksettings) -> {
            return geodecracksettings.generateCrackChance;
        }), Codec.doubleRange(0.0D, 5.0D).fieldOf("base_crack_size").orElse(2.0D).forGetter((geodecracksettings) -> {
            return geodecracksettings.baseCrackSize;
        }), Codec.intRange(0, 10).fieldOf("crack_point_offset").orElse(2).forGetter((geodecracksettings) -> {
            return geodecracksettings.crackPointOffset;
        })).apply(instance, GeodeCrackSettings::new);
    });
    public final double generateCrackChance;
    public final double baseCrackSize;
    public final int crackPointOffset;

    public GeodeCrackSettings(double d0, double d1, int i) {
        this.generateCrackChance = d0;
        this.baseCrackSize = d1;
        this.crackPointOffset = i;
    }
}

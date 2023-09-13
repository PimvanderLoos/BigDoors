package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;

public class StructureSettingsFeature {

    public static final Codec<StructureSettingsFeature> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 4096).fieldOf("spacing").forGetter((structuresettingsfeature) -> {
            return structuresettingsfeature.spacing;
        }), Codec.intRange(0, 4096).fieldOf("separation").forGetter((structuresettingsfeature) -> {
            return structuresettingsfeature.separation;
        }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter((structuresettingsfeature) -> {
            return structuresettingsfeature.salt;
        })).apply(instance, StructureSettingsFeature::new);
    }).comapFlatMap((structuresettingsfeature) -> {
        return structuresettingsfeature.spacing <= structuresettingsfeature.separation ? DataResult.error("Spacing has to be smaller than separation") : DataResult.success(structuresettingsfeature);
    }, Function.identity());
    private final int spacing;
    private final int separation;
    private final int salt;

    public StructureSettingsFeature(int i, int j, int k) {
        this.spacing = i;
        this.separation = j;
        this.salt = k;
    }

    public int a() {
        return this.spacing;
    }

    public int b() {
        return this.separation;
    }

    public int c() {
        return this.salt;
    }
}

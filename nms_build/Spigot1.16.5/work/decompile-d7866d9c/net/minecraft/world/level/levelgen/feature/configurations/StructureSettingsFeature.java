package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;

public class StructureSettingsFeature {

    public static final Codec<StructureSettingsFeature> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 4096).fieldOf("spacing").forGetter((structuresettingsfeature) -> {
            return structuresettingsfeature.b;
        }), Codec.intRange(0, 4096).fieldOf("separation").forGetter((structuresettingsfeature) -> {
            return structuresettingsfeature.c;
        }), Codec.intRange(0, Integer.MAX_VALUE).fieldOf("salt").forGetter((structuresettingsfeature) -> {
            return structuresettingsfeature.d;
        })).apply(instance, StructureSettingsFeature::new);
    }).comapFlatMap((structuresettingsfeature) -> {
        return structuresettingsfeature.b <= structuresettingsfeature.c ? DataResult.error("Spacing has to be smaller than separation") : DataResult.success(structuresettingsfeature);
    }, Function.identity());
    private final int b;
    private final int c;
    private final int d;

    public StructureSettingsFeature(int i, int j, int k) {
        this.b = i;
        this.c = j;
        this.d = k;
    }

    public int a() {
        return this.b;
    }

    public int b() {
        return this.c;
    }

    public int c() {
        return this.d;
    }
}

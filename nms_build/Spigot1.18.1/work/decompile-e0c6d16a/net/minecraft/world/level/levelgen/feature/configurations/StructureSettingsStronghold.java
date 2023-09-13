package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StructureSettingsStronghold {

    public static final Codec<StructureSettingsStronghold> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 1023).fieldOf("distance").forGetter(StructureSettingsStronghold::distance), Codec.intRange(0, 1023).fieldOf("spread").forGetter(StructureSettingsStronghold::spread), Codec.intRange(1, 4095).fieldOf("count").forGetter(StructureSettingsStronghold::count)).apply(instance, StructureSettingsStronghold::new);
    });
    private final int distance;
    private final int spread;
    private final int count;

    public StructureSettingsStronghold(int i, int j, int k) {
        this.distance = i;
        this.spread = j;
        this.count = k;
    }

    public int distance() {
        return this.distance;
    }

    public int spread() {
        return this.spread;
    }

    public int count() {
        return this.count;
    }
}

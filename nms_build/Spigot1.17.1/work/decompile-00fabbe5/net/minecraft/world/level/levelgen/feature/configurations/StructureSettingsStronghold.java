package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StructureSettingsStronghold {

    public static final Codec<StructureSettingsStronghold> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 1023).fieldOf("distance").forGetter(StructureSettingsStronghold::a), Codec.intRange(0, 1023).fieldOf("spread").forGetter(StructureSettingsStronghold::b), Codec.intRange(1, 4095).fieldOf("count").forGetter(StructureSettingsStronghold::c)).apply(instance, StructureSettingsStronghold::new);
    });
    private final int distance;
    private final int spread;
    private final int count;

    public StructureSettingsStronghold(int i, int j, int k) {
        this.distance = i;
        this.spread = j;
        this.count = k;
    }

    public int a() {
        return this.distance;
    }

    public int b() {
        return this.spread;
    }

    public int c() {
        return this.count;
    }
}

package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class DataPackConfiguration {

    public static final DataPackConfiguration a = new DataPackConfiguration(ImmutableList.of("vanilla"), ImmutableList.of());
    public static final Codec<DataPackConfiguration> b = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.STRING.listOf().fieldOf("Enabled").forGetter((datapackconfiguration) -> {
            return datapackconfiguration.c;
        }), Codec.STRING.listOf().fieldOf("Disabled").forGetter((datapackconfiguration) -> {
            return datapackconfiguration.d;
        })).apply(instance, DataPackConfiguration::new);
    });
    private final List<String> c;
    private final List<String> d;

    public DataPackConfiguration(List<String> list, List<String> list1) {
        this.c = ImmutableList.copyOf(list);
        this.d = ImmutableList.copyOf(list1);
    }

    public List<String> a() {
        return this.c;
    }

    public List<String> b() {
        return this.d;
    }
}

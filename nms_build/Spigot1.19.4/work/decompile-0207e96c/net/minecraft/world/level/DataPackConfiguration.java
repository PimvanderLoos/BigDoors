package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class DataPackConfiguration {

    public static final DataPackConfiguration DEFAULT = new DataPackConfiguration(ImmutableList.of("vanilla"), ImmutableList.of());
    public static final Codec<DataPackConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.STRING.listOf().fieldOf("Enabled").forGetter((datapackconfiguration) -> {
            return datapackconfiguration.enabled;
        }), Codec.STRING.listOf().fieldOf("Disabled").forGetter((datapackconfiguration) -> {
            return datapackconfiguration.disabled;
        })).apply(instance, DataPackConfiguration::new);
    });
    private final List<String> enabled;
    private final List<String> disabled;

    public DataPackConfiguration(List<String> list, List<String> list1) {
        this.enabled = ImmutableList.copyOf(list);
        this.disabled = ImmutableList.copyOf(list1);
    }

    public List<String> getEnabled() {
        return this.enabled;
    }

    public List<String> getDisabled() {
        return this.disabled;
    }
}

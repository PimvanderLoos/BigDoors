package net.minecraft.world.level.levelgen.flat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.Item;

public record FlatLevelGeneratorPreset(Holder<Item> displayItem, GeneratorSettingsFlat settings) {

    public static final Codec<FlatLevelGeneratorPreset> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RegistryFixedCodec.create(IRegistry.ITEM_REGISTRY).fieldOf("display").forGetter((flatlevelgeneratorpreset) -> {
            return flatlevelgeneratorpreset.displayItem;
        }), GeneratorSettingsFlat.CODEC.fieldOf("settings").forGetter((flatlevelgeneratorpreset) -> {
            return flatlevelgeneratorpreset.settings;
        })).apply(instance, FlatLevelGeneratorPreset::new);
    });
    public static final Codec<Holder<FlatLevelGeneratorPreset>> CODEC = RegistryFileCodec.create(IRegistry.FLAT_LEVEL_GENERATOR_PRESET_REGISTRY, FlatLevelGeneratorPreset.DIRECT_CODEC);
}

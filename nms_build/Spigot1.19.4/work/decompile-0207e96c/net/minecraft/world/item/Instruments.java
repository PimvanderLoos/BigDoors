package net.minecraft.world.item;

import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEffects;

public interface Instruments {

    int GOAT_HORN_RANGE_BLOCKS = 256;
    int GOAT_HORN_DURATION = 140;
    ResourceKey<Instrument> PONDER_GOAT_HORN = create("ponder_goat_horn");
    ResourceKey<Instrument> SING_GOAT_HORN = create("sing_goat_horn");
    ResourceKey<Instrument> SEEK_GOAT_HORN = create("seek_goat_horn");
    ResourceKey<Instrument> FEEL_GOAT_HORN = create("feel_goat_horn");
    ResourceKey<Instrument> ADMIRE_GOAT_HORN = create("admire_goat_horn");
    ResourceKey<Instrument> CALL_GOAT_HORN = create("call_goat_horn");
    ResourceKey<Instrument> YEARN_GOAT_HORN = create("yearn_goat_horn");
    ResourceKey<Instrument> DREAM_GOAT_HORN = create("dream_goat_horn");

    private static ResourceKey<Instrument> create(String s) {
        return ResourceKey.create(Registries.INSTRUMENT, new MinecraftKey(s));
    }

    static Instrument bootstrap(IRegistry<Instrument> iregistry) {
        IRegistry.register(iregistry, Instruments.PONDER_GOAT_HORN, new Instrument((Holder) SoundEffects.GOAT_HORN_SOUND_VARIANTS.get(0), 140, 256.0F));
        IRegistry.register(iregistry, Instruments.SING_GOAT_HORN, new Instrument((Holder) SoundEffects.GOAT_HORN_SOUND_VARIANTS.get(1), 140, 256.0F));
        IRegistry.register(iregistry, Instruments.SEEK_GOAT_HORN, new Instrument((Holder) SoundEffects.GOAT_HORN_SOUND_VARIANTS.get(2), 140, 256.0F));
        IRegistry.register(iregistry, Instruments.FEEL_GOAT_HORN, new Instrument((Holder) SoundEffects.GOAT_HORN_SOUND_VARIANTS.get(3), 140, 256.0F));
        IRegistry.register(iregistry, Instruments.ADMIRE_GOAT_HORN, new Instrument((Holder) SoundEffects.GOAT_HORN_SOUND_VARIANTS.get(4), 140, 256.0F));
        IRegistry.register(iregistry, Instruments.CALL_GOAT_HORN, new Instrument((Holder) SoundEffects.GOAT_HORN_SOUND_VARIANTS.get(5), 140, 256.0F));
        IRegistry.register(iregistry, Instruments.YEARN_GOAT_HORN, new Instrument((Holder) SoundEffects.GOAT_HORN_SOUND_VARIANTS.get(6), 140, 256.0F));
        return (Instrument) IRegistry.register(iregistry, Instruments.DREAM_GOAT_HORN, new Instrument((Holder) SoundEffects.GOAT_HORN_SOUND_VARIANTS.get(7), 140, 256.0F));
    }
}

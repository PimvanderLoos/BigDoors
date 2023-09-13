package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import net.minecraft.resources.MinecraftKey;

public class SoundEffect {

    public static final Codec<SoundEffect> CODEC = MinecraftKey.CODEC.xmap(SoundEffect::new, (soundeffect) -> {
        return soundeffect.location;
    });
    private final MinecraftKey location;

    public SoundEffect(MinecraftKey minecraftkey) {
        this.location = minecraftkey;
    }

    public MinecraftKey a() {
        return this.location;
    }
}

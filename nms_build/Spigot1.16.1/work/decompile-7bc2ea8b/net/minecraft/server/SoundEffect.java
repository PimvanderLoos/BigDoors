package net.minecraft.server;

import com.mojang.serialization.Codec;

public class SoundEffect {

    public static final Codec<SoundEffect> a = MinecraftKey.a.xmap(SoundEffect::new, (soundeffect) -> {
        return soundeffect.b;
    });
    private final MinecraftKey b;

    public SoundEffect(MinecraftKey minecraftkey) {
        this.b = minecraftkey;
    }
}

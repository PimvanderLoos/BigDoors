package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import net.minecraft.resources.MinecraftKey;

public class SoundEffect {

    public static final Codec<SoundEffect> CODEC = MinecraftKey.CODEC.xmap(SoundEffect::new, (soundeffect) -> {
        return soundeffect.location;
    });
    private final MinecraftKey location;
    private final float range;
    private final boolean newSystem;

    public SoundEffect(MinecraftKey minecraftkey) {
        this(minecraftkey, 16.0F, false);
    }

    public SoundEffect(MinecraftKey minecraftkey, float f) {
        this(minecraftkey, f, true);
    }

    private SoundEffect(MinecraftKey minecraftkey, float f, boolean flag) {
        this.location = minecraftkey;
        this.range = f;
        this.newSystem = flag;
    }

    public MinecraftKey getLocation() {
        return this.location;
    }

    public float getRange(float f) {
        return this.newSystem ? this.range : (f > 1.0F ? 16.0F * f : 16.0F);
    }
}

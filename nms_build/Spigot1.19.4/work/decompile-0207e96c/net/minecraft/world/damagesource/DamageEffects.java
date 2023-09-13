package net.minecraft.world.damagesource;

import com.mojang.serialization.Codec;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.INamable;

public enum DamageEffects implements INamable {

    HURT("hurt", SoundEffects.PLAYER_HURT), THORNS("thorns", SoundEffects.THORNS_HIT), DROWNING("drowning", SoundEffects.PLAYER_HURT_DROWN), BURNING("burning", SoundEffects.PLAYER_HURT_ON_FIRE), POKING("poking", SoundEffects.PLAYER_HURT_SWEET_BERRY_BUSH), FREEZING("freezing", SoundEffects.PLAYER_HURT_FREEZE);

    public static final Codec<DamageEffects> CODEC = INamable.fromEnum(DamageEffects::values);
    private final String id;
    private final SoundEffect sound;

    private DamageEffects(String s, SoundEffect soundeffect) {
        this.id = s;
        this.sound = soundeffect;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    public SoundEffect sound() {
        return this.sound;
    }
}

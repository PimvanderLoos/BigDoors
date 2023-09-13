package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;

public interface ISaddleable {

    boolean isSaddleable();

    void equipSaddle(@Nullable SoundCategory soundcategory);

    default SoundEffect getSaddleSoundEvent() {
        return SoundEffects.HORSE_SADDLE;
    }

    boolean isSaddled();
}

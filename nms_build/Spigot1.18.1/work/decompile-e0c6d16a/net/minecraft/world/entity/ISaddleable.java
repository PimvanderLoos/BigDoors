package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundCategory;

public interface ISaddleable {

    boolean isSaddleable();

    void equipSaddle(@Nullable SoundCategory soundcategory);

    boolean isSaddled();
}

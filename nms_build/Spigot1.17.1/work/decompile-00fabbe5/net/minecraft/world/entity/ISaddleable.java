package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundCategory;

public interface ISaddleable {

    boolean canSaddle();

    void saddle(@Nullable SoundCategory soundcategory);

    boolean hasSaddle();
}

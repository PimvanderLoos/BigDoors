package net.minecraft.world.entity;

import net.minecraft.sounds.SoundCategory;

public interface IShearable {

    void shear(SoundCategory soundcategory);

    boolean readyForShearing();
}

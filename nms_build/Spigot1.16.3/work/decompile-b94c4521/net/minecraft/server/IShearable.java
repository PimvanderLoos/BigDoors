package net.minecraft.server;

public interface IShearable {

    void shear(SoundCategory soundcategory);

    boolean canShear();
}

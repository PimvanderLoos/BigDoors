package net.minecraft.server;

import javax.annotation.Nullable;

public interface ISaddleable {

    boolean canSaddle();

    void saddle(@Nullable SoundCategory soundcategory);

    boolean hasSaddle();
}

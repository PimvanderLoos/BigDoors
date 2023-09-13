package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockStoneButton extends BlockButtonAbstract {

    protected BlockStoneButton() {
        super(false);
    }

    protected void a(@Nullable EntityHuman entityhuman, World world, BlockPosition blockposition) {
        world.a(entityhuman, blockposition, SoundEffects.hK, SoundCategory.BLOCKS, 0.3F, 0.6F);
    }

    protected void b(World world, BlockPosition blockposition) {
        world.a((EntityHuman) null, blockposition, SoundEffects.hJ, SoundCategory.BLOCKS, 0.3F, 0.5F);
    }
}

package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockWoodButton extends BlockButtonAbstract {

    protected BlockWoodButton() {
        super(true);
    }

    protected void a(@Nullable EntityHuman entityhuman, World world, BlockPosition blockposition) {
        world.a(entityhuman, blockposition, SoundEffects.jb, SoundCategory.BLOCKS, 0.3F, 0.6F);
    }

    protected void b(World world, BlockPosition blockposition) {
        world.a((EntityHuman) null, blockposition, SoundEffects.ja, SoundCategory.BLOCKS, 0.3F, 0.5F);
    }
}

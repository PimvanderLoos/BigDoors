package net.minecraft.world.level;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.lighting.LightEngine;

public interface IBlockLightAccess extends IBlockAccess {

    float a(EnumDirection enumdirection, boolean flag);

    LightEngine k_();

    int a(BlockPosition blockposition, ColorResolver colorresolver);

    default int getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
        return this.k_().a(enumskyblock).b(blockposition);
    }

    default int getLightLevel(BlockPosition blockposition, int i) {
        return this.k_().b(blockposition, i);
    }

    default boolean g(BlockPosition blockposition) {
        return this.getBrightness(EnumSkyBlock.SKY, blockposition) >= this.O();
    }
}

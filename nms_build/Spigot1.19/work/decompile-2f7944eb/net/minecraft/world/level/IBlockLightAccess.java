package net.minecraft.world.level;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.lighting.LightEngine;

public interface IBlockLightAccess extends IBlockAccess {

    float getShade(EnumDirection enumdirection, boolean flag);

    LightEngine getLightEngine();

    int getBlockTint(BlockPosition blockposition, ColorResolver colorresolver);

    default int getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
        return this.getLightEngine().getLayerListener(enumskyblock).getLightValue(blockposition);
    }

    default int getRawBrightness(BlockPosition blockposition, int i) {
        return this.getLightEngine().getRawBrightness(blockposition, i);
    }

    default boolean canSeeSky(BlockPosition blockposition) {
        return this.getBrightness(EnumSkyBlock.SKY, blockposition) >= this.getMaxLightLevel();
    }
}

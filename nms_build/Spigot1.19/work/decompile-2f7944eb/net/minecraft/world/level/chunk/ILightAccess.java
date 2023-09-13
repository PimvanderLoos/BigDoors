package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.IBlockAccess;

public interface ILightAccess {

    @Nullable
    IBlockAccess getChunkForLighting(int i, int j);

    default void onLightUpdate(EnumSkyBlock enumskyblock, SectionPosition sectionposition) {}

    IBlockAccess getLevel();
}

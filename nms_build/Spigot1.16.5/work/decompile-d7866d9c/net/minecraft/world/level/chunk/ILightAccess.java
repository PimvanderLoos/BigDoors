package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.IBlockAccess;

public interface ILightAccess {

    @Nullable
    IBlockAccess c(int i, int j);

    default void a(EnumSkyBlock enumskyblock, SectionPosition sectionposition) {}

    IBlockAccess getWorld();
}

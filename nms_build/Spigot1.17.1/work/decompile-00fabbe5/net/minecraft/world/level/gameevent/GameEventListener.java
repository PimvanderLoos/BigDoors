package net.minecraft.world.level.gameevent;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;

public interface GameEventListener {

    PositionSource a();

    int b();

    boolean a(World world, GameEvent gameevent, @Nullable Entity entity, BlockPosition blockposition);
}

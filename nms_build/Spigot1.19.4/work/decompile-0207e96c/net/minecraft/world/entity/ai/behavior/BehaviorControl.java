package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;

public interface BehaviorControl<E extends EntityLiving> {

    Behavior.Status getStatus();

    boolean tryStart(WorldServer worldserver, E e0, long i);

    void tickOrStop(WorldServer worldserver, E e0, long i);

    void doStop(WorldServer worldserver, E e0, long i);

    String debugString();
}

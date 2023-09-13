package net.minecraft.world.level.gameevent;

import net.minecraft.server.level.WorldServer;

public interface GameEventListener {

    default boolean handleEventsImmediately() {
        return false;
    }

    PositionSource getListenerSource();

    int getListenerRadius();

    boolean handleGameEvent(WorldServer worldserver, GameEvent.b gameevent_b);
}

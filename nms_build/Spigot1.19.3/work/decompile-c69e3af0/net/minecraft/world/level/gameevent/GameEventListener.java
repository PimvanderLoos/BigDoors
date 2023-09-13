package net.minecraft.world.level.gameevent;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.phys.Vec3D;

public interface GameEventListener {

    PositionSource getListenerSource();

    int getListenerRadius();

    boolean handleGameEvent(WorldServer worldserver, GameEvent gameevent, GameEvent.a gameevent_a, Vec3D vec3d);

    default GameEventListener.a getDeliveryMode() {
        return GameEventListener.a.UNSPECIFIED;
    }

    public static enum a {

        UNSPECIFIED, BY_DISTANCE;

        private a() {}
    }
}

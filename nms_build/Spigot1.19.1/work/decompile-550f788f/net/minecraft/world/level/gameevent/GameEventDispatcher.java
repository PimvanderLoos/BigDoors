package net.minecraft.world.level.gameevent;

import java.util.function.BiConsumer;
import net.minecraft.world.phys.Vec3D;

public interface GameEventDispatcher {

    GameEventDispatcher NOOP = new GameEventDispatcher() {
        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void register(GameEventListener gameeventlistener) {}

        @Override
        public void unregister(GameEventListener gameeventlistener) {}

        @Override
        public boolean walkListeners(GameEvent gameevent, Vec3D vec3d, GameEvent.a gameevent_a, BiConsumer<GameEventListener, Vec3D> biconsumer) {
            return false;
        }
    };

    boolean isEmpty();

    void register(GameEventListener gameeventlistener);

    void unregister(GameEventListener gameeventlistener);

    boolean walkListeners(GameEvent gameevent, Vec3D vec3d, GameEvent.a gameevent_a, BiConsumer<GameEventListener, Vec3D> biconsumer);
}

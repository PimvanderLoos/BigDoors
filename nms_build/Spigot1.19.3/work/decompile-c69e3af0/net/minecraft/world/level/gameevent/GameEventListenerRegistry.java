package net.minecraft.world.level.gameevent;

import net.minecraft.world.phys.Vec3D;

public interface GameEventListenerRegistry {

    GameEventListenerRegistry NOOP = new GameEventListenerRegistry() {
        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void register(GameEventListener gameeventlistener) {}

        @Override
        public void unregister(GameEventListener gameeventlistener) {}

        @Override
        public boolean visitInRangeListeners(GameEvent gameevent, Vec3D vec3d, GameEvent.a gameevent_a, GameEventListenerRegistry.a gameeventlistenerregistry_a) {
            return false;
        }
    };

    boolean isEmpty();

    void register(GameEventListener gameeventlistener);

    void unregister(GameEventListener gameeventlistener);

    boolean visitInRangeListeners(GameEvent gameevent, Vec3D vec3d, GameEvent.a gameevent_a, GameEventListenerRegistry.a gameeventlistenerregistry_a);

    @FunctionalInterface
    public interface a {

        void visit(GameEventListener gameeventlistener, Vec3D vec3d);
    }
}

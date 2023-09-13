package net.minecraft.world.level.gameevent;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;

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
        public void post(GameEvent gameevent, @Nullable Entity entity, BlockPosition blockposition) {}
    };

    boolean isEmpty();

    void register(GameEventListener gameeventlistener);

    void unregister(GameEventListener gameeventlistener);

    void post(GameEvent gameevent, @Nullable Entity entity, BlockPosition blockposition);
}

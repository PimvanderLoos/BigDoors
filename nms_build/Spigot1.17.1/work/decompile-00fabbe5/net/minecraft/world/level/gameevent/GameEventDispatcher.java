package net.minecraft.world.level.gameevent;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;

public interface GameEventDispatcher {

    GameEventDispatcher NOOP = new GameEventDispatcher() {
        @Override
        public boolean a() {
            return true;
        }

        @Override
        public void a(GameEventListener gameeventlistener) {}

        @Override
        public void b(GameEventListener gameeventlistener) {}

        @Override
        public void a(GameEvent gameevent, @Nullable Entity entity, BlockPosition blockposition) {}
    };

    boolean a();

    void a(GameEventListener gameeventlistener);

    void b(GameEventListener gameeventlistener);

    void a(GameEvent gameevent, @Nullable Entity entity, BlockPosition blockposition);
}

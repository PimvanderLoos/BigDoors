package net.minecraft.world.ticks;

import net.minecraft.core.BlockPosition;

public class TickListEmpty {

    private static final TickContainerAccess<Object> CONTAINER_BLACKHOLE = new TickContainerAccess<Object>() {
        @Override
        public void schedule(NextTickListEntry<Object> nextticklistentry) {}

        @Override
        public boolean hasScheduledTick(BlockPosition blockposition, Object object) {
            return false;
        }

        @Override
        public int count() {
            return 0;
        }
    };
    private static final LevelTickAccess<Object> LEVEL_BLACKHOLE = new LevelTickAccess<Object>() {
        @Override
        public void schedule(NextTickListEntry<Object> nextticklistentry) {}

        @Override
        public boolean hasScheduledTick(BlockPosition blockposition, Object object) {
            return false;
        }

        @Override
        public boolean willTickThisTick(BlockPosition blockposition, Object object) {
            return false;
        }

        @Override
        public int count() {
            return 0;
        }
    };

    public TickListEmpty() {}

    public static <T> TickContainerAccess<T> emptyContainer() {
        return TickListEmpty.CONTAINER_BLACKHOLE;
    }

    public static <T> LevelTickAccess<T> emptyLevelList() {
        return TickListEmpty.LEVEL_BLACKHOLE;
    }
}

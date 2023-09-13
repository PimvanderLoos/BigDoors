package net.minecraft.world.level.gameevent;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;

public class GameEventListenerRegistrar {

    private final GameEventListener listener;
    @Nullable
    private SectionPosition sectionPos;

    public GameEventListenerRegistrar(GameEventListener gameeventlistener) {
        this.listener = gameeventlistener;
    }

    public void onListenerRemoved(World world) {
        this.ifEventDispatcherExists(world, this.sectionPos, (gameeventdispatcher) -> {
            gameeventdispatcher.unregister(this.listener);
        });
    }

    public void onListenerMove(World world) {
        Optional<BlockPosition> optional = this.listener.getListenerSource().getPosition(world);

        if (optional.isPresent()) {
            long i = SectionPosition.blockToSection(((BlockPosition) optional.get()).asLong());

            if (this.sectionPos == null || this.sectionPos.asLong() != i) {
                SectionPosition sectionposition = this.sectionPos;

                this.sectionPos = SectionPosition.of(i);
                this.ifEventDispatcherExists(world, sectionposition, (gameeventdispatcher) -> {
                    gameeventdispatcher.unregister(this.listener);
                });
                this.ifEventDispatcherExists(world, this.sectionPos, (gameeventdispatcher) -> {
                    gameeventdispatcher.register(this.listener);
                });
            }
        }

    }

    private void ifEventDispatcherExists(World world, @Nullable SectionPosition sectionposition, Consumer<GameEventDispatcher> consumer) {
        if (sectionposition != null) {
            IChunkAccess ichunkaccess = world.getChunk(sectionposition.x(), sectionposition.z(), ChunkStatus.FULL, false);

            if (ichunkaccess != null) {
                consumer.accept(ichunkaccess.getEventDispatcher(sectionposition.y()));
            }

        }
    }
}

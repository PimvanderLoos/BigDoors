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

    public void a(World world) {
        this.a(world, this.sectionPos, (gameeventdispatcher) -> {
            gameeventdispatcher.b(this.listener);
        });
    }

    public void b(World world) {
        Optional<BlockPosition> optional = this.listener.a().a(world);

        if (optional.isPresent()) {
            long i = SectionPosition.e(((BlockPosition) optional.get()).asLong());

            if (this.sectionPos == null || this.sectionPos.s() != i) {
                SectionPosition sectionposition = this.sectionPos;

                this.sectionPos = SectionPosition.a(i);
                this.a(world, sectionposition, (gameeventdispatcher) -> {
                    gameeventdispatcher.b(this.listener);
                });
                this.a(world, this.sectionPos, (gameeventdispatcher) -> {
                    gameeventdispatcher.a(this.listener);
                });
            }
        }

    }

    private void a(World world, @Nullable SectionPosition sectionposition, Consumer<GameEventDispatcher> consumer) {
        if (sectionposition != null) {
            IChunkAccess ichunkaccess = world.getChunkAt(sectionposition.a(), sectionposition.c(), ChunkStatus.FULL, false);

            if (ichunkaccess != null) {
                consumer.accept(ichunkaccess.a(sectionposition.b()));
            }

        }
    }
}

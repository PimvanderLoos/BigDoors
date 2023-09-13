package net.minecraft.world.level.gameevent;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;

public class DynamicGameEventListener<T extends GameEventListener> {

    private T listener;
    @Nullable
    private SectionPosition lastSection;

    public DynamicGameEventListener(T t0) {
        this.listener = t0;
    }

    public void add(WorldServer worldserver) {
        this.move(worldserver);
    }

    public void updateListener(T t0, @Nullable World world) {
        T t1 = this.listener;

        if (t1 != t0) {
            if (world instanceof WorldServer) {
                WorldServer worldserver = (WorldServer) world;

                ifChunkExists(worldserver, this.lastSection, (gameeventdispatcher) -> {
                    gameeventdispatcher.unregister(t1);
                });
                ifChunkExists(worldserver, this.lastSection, (gameeventdispatcher) -> {
                    gameeventdispatcher.register(t0);
                });
            }

            this.listener = t0;
        }
    }

    public T getListener() {
        return this.listener;
    }

    public void remove(WorldServer worldserver) {
        ifChunkExists(worldserver, this.lastSection, (gameeventdispatcher) -> {
            gameeventdispatcher.unregister(this.listener);
        });
    }

    public void move(WorldServer worldserver) {
        this.listener.getListenerSource().getPosition(worldserver).map(SectionPosition::of).ifPresent((sectionposition) -> {
            if (this.lastSection == null || !this.lastSection.equals(sectionposition)) {
                ifChunkExists(worldserver, this.lastSection, (gameeventdispatcher) -> {
                    gameeventdispatcher.unregister(this.listener);
                });
                this.lastSection = sectionposition;
                ifChunkExists(worldserver, this.lastSection, (gameeventdispatcher) -> {
                    gameeventdispatcher.register(this.listener);
                });
            }

        });
    }

    private static void ifChunkExists(IWorldReader iworldreader, @Nullable SectionPosition sectionposition, Consumer<GameEventDispatcher> consumer) {
        if (sectionposition != null) {
            IChunkAccess ichunkaccess = iworldreader.getChunk(sectionposition.x(), sectionposition.z(), ChunkStatus.FULL, false);

            if (ichunkaccess != null) {
                consumer.accept(ichunkaccess.getEventDispatcher(sectionposition.y()));
            }

        }
    }
}

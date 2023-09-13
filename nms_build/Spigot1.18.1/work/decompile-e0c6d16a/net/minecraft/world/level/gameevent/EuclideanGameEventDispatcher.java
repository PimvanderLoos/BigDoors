package net.minecraft.world.level.gameevent;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;

public class EuclideanGameEventDispatcher implements GameEventDispatcher {

    private final List<GameEventListener> listeners = Lists.newArrayList();
    private final World level;

    public EuclideanGameEventDispatcher(World world) {
        this.level = world;
    }

    @Override
    public boolean isEmpty() {
        return this.listeners.isEmpty();
    }

    @Override
    public void register(GameEventListener gameeventlistener) {
        this.listeners.add(gameeventlistener);
        PacketDebug.sendGameEventListenerInfo(this.level, gameeventlistener);
    }

    @Override
    public void unregister(GameEventListener gameeventlistener) {
        this.listeners.remove(gameeventlistener);
    }

    @Override
    public void post(GameEvent gameevent, @Nullable Entity entity, BlockPosition blockposition) {
        boolean flag = false;
        Iterator iterator = this.listeners.iterator();

        while (iterator.hasNext()) {
            GameEventListener gameeventlistener = (GameEventListener) iterator.next();

            if (this.postToListener(this.level, gameevent, entity, blockposition, gameeventlistener)) {
                flag = true;
            }
        }

        if (flag) {
            PacketDebug.sendGameEventInfo(this.level, gameevent, blockposition);
        }

    }

    private boolean postToListener(World world, GameEvent gameevent, @Nullable Entity entity, BlockPosition blockposition, GameEventListener gameeventlistener) {
        Optional<BlockPosition> optional = gameeventlistener.getListenerSource().getPosition(world);

        if (!optional.isPresent()) {
            return false;
        } else {
            double d0 = ((BlockPosition) optional.get()).distSqr((BaseBlockPosition) blockposition, false);
            int i = gameeventlistener.getListenerRadius() * gameeventlistener.getListenerRadius();

            return d0 <= (double) i && gameeventlistener.handleGameEvent(world, gameevent, entity, blockposition);
        }
    }
}

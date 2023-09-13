package net.minecraft.world.level.gameevent;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.phys.Vec3D;

public class EuclideanGameEventDispatcher implements GameEventDispatcher {

    private final List<GameEventListener> listeners = Lists.newArrayList();
    private final Set<GameEventListener> listenersToRemove = Sets.newHashSet();
    private final List<GameEventListener> listenersToAdd = Lists.newArrayList();
    private boolean processing;
    private final WorldServer level;

    public EuclideanGameEventDispatcher(WorldServer worldserver) {
        this.level = worldserver;
    }

    @Override
    public boolean isEmpty() {
        return this.listeners.isEmpty();
    }

    @Override
    public void register(GameEventListener gameeventlistener) {
        if (this.processing) {
            this.listenersToAdd.add(gameeventlistener);
        } else {
            this.listeners.add(gameeventlistener);
        }

        PacketDebug.sendGameEventListenerInfo(this.level, gameeventlistener);
    }

    @Override
    public void unregister(GameEventListener gameeventlistener) {
        if (this.processing) {
            this.listenersToRemove.add(gameeventlistener);
        } else {
            this.listeners.remove(gameeventlistener);
        }

    }

    @Override
    public boolean walkListeners(GameEvent gameevent, Vec3D vec3d, GameEvent.a gameevent_a, BiConsumer<GameEventListener, Vec3D> biconsumer) {
        this.processing = true;
        boolean flag = false;

        try {
            Iterator iterator = this.listeners.iterator();

            while (iterator.hasNext()) {
                GameEventListener gameeventlistener = (GameEventListener) iterator.next();

                if (this.listenersToRemove.remove(gameeventlistener)) {
                    iterator.remove();
                } else {
                    Optional<Vec3D> optional = getPostableListenerPosition(this.level, vec3d, gameeventlistener);

                    if (optional.isPresent()) {
                        biconsumer.accept(gameeventlistener, (Vec3D) optional.get());
                        flag = true;
                    }
                }
            }
        } finally {
            this.processing = false;
        }

        if (!this.listenersToAdd.isEmpty()) {
            this.listeners.addAll(this.listenersToAdd);
            this.listenersToAdd.clear();
        }

        if (!this.listenersToRemove.isEmpty()) {
            this.listeners.removeAll(this.listenersToRemove);
            this.listenersToRemove.clear();
        }

        return flag;
    }

    private static Optional<Vec3D> getPostableListenerPosition(WorldServer worldserver, Vec3D vec3d, GameEventListener gameeventlistener) {
        Optional<Vec3D> optional = gameeventlistener.getListenerSource().getPosition(worldserver);

        if (optional.isEmpty()) {
            return Optional.empty();
        } else {
            double d0 = ((Vec3D) optional.get()).distanceToSqr(vec3d);
            int i = gameeventlistener.getListenerRadius() * gameeventlistener.getListenerRadius();

            return d0 > (double) i ? Optional.empty() : optional;
        }
    }
}

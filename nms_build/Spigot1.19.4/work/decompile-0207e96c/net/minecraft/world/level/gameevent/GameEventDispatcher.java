package net.minecraft.world.level.gameevent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.phys.Vec3D;

public class GameEventDispatcher {

    private final WorldServer level;

    public GameEventDispatcher(WorldServer worldserver) {
        this.level = worldserver;
    }

    public void post(GameEvent gameevent, Vec3D vec3d, GameEvent.a gameevent_a) {
        int i = gameevent.getNotificationRadius();
        BlockPosition blockposition = BlockPosition.containing(vec3d);
        int j = SectionPosition.blockToSectionCoord(blockposition.getX() - i);
        int k = SectionPosition.blockToSectionCoord(blockposition.getY() - i);
        int l = SectionPosition.blockToSectionCoord(blockposition.getZ() - i);
        int i1 = SectionPosition.blockToSectionCoord(blockposition.getX() + i);
        int j1 = SectionPosition.blockToSectionCoord(blockposition.getY() + i);
        int k1 = SectionPosition.blockToSectionCoord(blockposition.getZ() + i);
        List<GameEvent.b> list = new ArrayList();
        GameEventListenerRegistry.a gameeventlistenerregistry_a = (gameeventlistener, vec3d1) -> {
            if (gameeventlistener.getDeliveryMode() == GameEventListener.a.BY_DISTANCE) {
                list.add(new GameEvent.b(gameevent, vec3d, gameevent_a, gameeventlistener, vec3d1));
            } else {
                gameeventlistener.handleGameEvent(this.level, gameevent, gameevent_a, vec3d);
            }

        };
        boolean flag = false;

        for (int l1 = j; l1 <= i1; ++l1) {
            for (int i2 = l; i2 <= k1; ++i2) {
                Chunk chunk = this.level.getChunkSource().getChunkNow(l1, i2);

                if (chunk != null) {
                    for (int j2 = k; j2 <= j1; ++j2) {
                        flag |= chunk.getListenerRegistry(j2).visitInRangeListeners(gameevent, vec3d, gameevent_a, gameeventlistenerregistry_a);
                    }
                }
            }
        }

        if (!list.isEmpty()) {
            this.handleGameEventMessagesInQueue(list);
        }

        if (flag) {
            PacketDebug.sendGameEventInfo(this.level, gameevent, vec3d);
        }

    }

    private void handleGameEventMessagesInQueue(List<GameEvent.b> list) {
        Collections.sort(list);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            GameEvent.b gameevent_b = (GameEvent.b) iterator.next();
            GameEventListener gameeventlistener = gameevent_b.recipient();

            gameeventlistener.handleGameEvent(this.level, gameevent_b.gameEvent(), gameevent_b.context(), gameevent_b.source());
        }

    }
}

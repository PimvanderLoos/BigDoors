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
    public boolean a() {
        return this.listeners.isEmpty();
    }

    @Override
    public void a(GameEventListener gameeventlistener) {
        this.listeners.add(gameeventlistener);
        PacketDebug.a(this.level, gameeventlistener);
    }

    @Override
    public void b(GameEventListener gameeventlistener) {
        this.listeners.remove(gameeventlistener);
    }

    @Override
    public void a(GameEvent gameevent, @Nullable Entity entity, BlockPosition blockposition) {
        boolean flag = false;
        Iterator iterator = this.listeners.iterator();

        while (iterator.hasNext()) {
            GameEventListener gameeventlistener = (GameEventListener) iterator.next();

            if (this.a(this.level, gameevent, entity, blockposition, gameeventlistener)) {
                flag = true;
            }
        }

        if (flag) {
            PacketDebug.a(this.level, gameevent, blockposition);
        }

    }

    private boolean a(World world, GameEvent gameevent, @Nullable Entity entity, BlockPosition blockposition, GameEventListener gameeventlistener) {
        Optional<BlockPosition> optional = gameeventlistener.a().a(world);

        if (!optional.isPresent()) {
            return false;
        } else {
            double d0 = ((BlockPosition) optional.get()).a((BaseBlockPosition) blockposition, false);
            int i = gameeventlistener.b() * gameeventlistener.b();

            return d0 <= (double) i && gameeventlistener.a(world, gameevent, entity, blockposition);
        }
    }
}

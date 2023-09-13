package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.npc.EntityVillager;

public class BehaviorStrollPlaceList extends Behavior<EntityVillager> {

    private final MemoryModuleType<List<GlobalPos>> b;
    private final MemoryModuleType<GlobalPos> c;
    private final float d;
    private final int e;
    private final int f;
    private long g;
    @Nullable
    private GlobalPos h;

    public BehaviorStrollPlaceList(MemoryModuleType<List<GlobalPos>> memorymoduletype, float f, int i, int j, MemoryModuleType<GlobalPos> memorymoduletype1) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, memorymoduletype, MemoryStatus.VALUE_PRESENT, memorymoduletype1, MemoryStatus.VALUE_PRESENT));
        this.b = memorymoduletype;
        this.d = f;
        this.e = i;
        this.f = j;
        this.c = memorymoduletype1;
    }

    protected boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        Optional<List<GlobalPos>> optional = entityvillager.getBehaviorController().getMemory(this.b);
        Optional<GlobalPos> optional1 = entityvillager.getBehaviorController().getMemory(this.c);

        if (optional.isPresent() && optional1.isPresent()) {
            List<GlobalPos> list = (List) optional.get();

            if (!list.isEmpty()) {
                this.h = (GlobalPos) list.get(worldserver.getRandom().nextInt(list.size()));
                return this.h != null && worldserver.getDimensionKey() == this.h.getDimensionManager() && ((GlobalPos) optional1.get()).getBlockPosition().a((IPosition) entityvillager.getPositionVector(), (double) this.f);
            }
        }

        return false;
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        if (i > this.g && this.h != null) {
            entityvillager.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(this.h.getBlockPosition(), this.d, this.e)));
            this.g = i + 100L;
        }

    }
}

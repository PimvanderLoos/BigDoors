package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

public class BehaviorWork extends Behavior<EntityVillager> {

    private long b;

    public BehaviorWork() {
        super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
    }

    protected boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        if (worldserver.getTime() - this.b < 300L) {
            return false;
        } else if (worldserver.random.nextInt(2) != 0) {
            return false;
        } else {
            this.b = worldserver.getTime();
            GlobalPos globalpos = (GlobalPos) entityvillager.getBehaviorController().getMemory(MemoryModuleType.JOB_SITE).get();

            return globalpos.getDimensionManager() == worldserver.getDimensionKey() && globalpos.getBlockPosition().a((IPosition) entityvillager.getPositionVector(), 1.73D);
        }
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        BehaviorController<EntityVillager> behaviorcontroller = entityvillager.getBehaviorController();

        behaviorcontroller.setMemory(MemoryModuleType.LAST_WORKED_AT_POI, (Object) i);
        behaviorcontroller.getMemory(MemoryModuleType.JOB_SITE).ifPresent((globalpos) -> {
            behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorTarget(globalpos.getBlockPosition())));
        });
        entityvillager.fd();
        this.a(worldserver, entityvillager);
        if (entityvillager.fc()) {
            entityvillager.fb();
        }

    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager) {}

    protected boolean b(WorldServer worldserver, EntityVillager entityvillager, long i) {
        Optional<GlobalPos> optional = entityvillager.getBehaviorController().getMemory(MemoryModuleType.JOB_SITE);

        if (!optional.isPresent()) {
            return false;
        } else {
            GlobalPos globalpos = (GlobalPos) optional.get();

            return globalpos.getDimensionManager() == worldserver.getDimensionKey() && globalpos.getBlockPosition().a((IPosition) entityvillager.getPositionVector(), 1.73D);
        }
    }
}

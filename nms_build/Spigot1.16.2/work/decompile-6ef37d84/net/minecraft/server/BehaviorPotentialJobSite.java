package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

public class BehaviorPotentialJobSite extends Behavior<EntityVillager> {

    final float b;

    public BehaviorPotentialJobSite(float f) {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryStatus.VALUE_PRESENT), 1200);
        this.b = f;
    }

    protected boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        return (Boolean) entityvillager.getBehaviorController().f().map((activity) -> {
            return activity == Activity.IDLE || activity == Activity.WORK || activity == Activity.PLAY;
        }).orElse(true);
    }

    protected boolean b(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return entityvillager.getBehaviorController().hasMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
    }

    protected void d(WorldServer worldserver, EntityVillager entityvillager, long i) {
        BehaviorUtil.a(entityvillager, ((GlobalPos) entityvillager.getBehaviorController().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get()).getBlockPosition(), this.b, 1);
    }

    protected void c(WorldServer worldserver, EntityVillager entityvillager, long i) {
        Optional<GlobalPos> optional = entityvillager.getBehaviorController().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);

        optional.ifPresent((globalpos) -> {
            BlockPosition blockposition = globalpos.getBlockPosition();
            WorldServer worldserver1 = worldserver.getMinecraftServer().getWorldServer(globalpos.getDimensionManager());

            if (worldserver1 != null) {
                VillagePlace villageplace = worldserver1.y();

                if (villageplace.a(blockposition, (villageplacetype) -> {
                    return true;
                })) {
                    villageplace.b(blockposition);
                }

                PacketDebug.c(worldserver, blockposition);
            }
        });
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}

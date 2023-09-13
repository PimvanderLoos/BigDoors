package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class BehaviorBetterJob extends Behavior<EntityVillager> {

    final VillagerProfession b;

    public BehaviorBetterJob(VillagerProfession villagerprofession) {
        super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.MOBS, MemoryStatus.VALUE_PRESENT));
        this.b = villagerprofession;
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        GlobalPos globalpos = (GlobalPos) entityvillager.getBehaviorController().getMemory(MemoryModuleType.JOB_SITE).get();

        worldserver.y().c(globalpos.getBlockPosition()).ifPresent((villageplacetype) -> {
            BehaviorUtil.a(entityvillager, (entityvillager1) -> {
                return this.a(globalpos, villageplacetype, entityvillager1);
            }).reduce(entityvillager, BehaviorBetterJob::a);
        });
    }

    private static EntityVillager a(EntityVillager entityvillager, EntityVillager entityvillager1) {
        EntityVillager entityvillager2;
        EntityVillager entityvillager3;

        if (entityvillager.getExperience() > entityvillager1.getExperience()) {
            entityvillager2 = entityvillager;
            entityvillager3 = entityvillager1;
        } else {
            entityvillager2 = entityvillager1;
            entityvillager3 = entityvillager;
        }

        entityvillager3.getBehaviorController().removeMemory(MemoryModuleType.JOB_SITE);
        return entityvillager2;
    }

    private boolean a(GlobalPos globalpos, VillagePlaceType villageplacetype, EntityVillager entityvillager) {
        return this.a(entityvillager) && globalpos.equals(entityvillager.getBehaviorController().getMemory(MemoryModuleType.JOB_SITE).get()) && this.a(villageplacetype, entityvillager.getVillagerData().getProfession());
    }

    private boolean a(VillagePlaceType villageplacetype, VillagerProfession villagerprofession) {
        return villagerprofession.b().c().test(villageplacetype);
    }

    private boolean a(EntityVillager entityvillager) {
        return entityvillager.getBehaviorController().getMemory(MemoryModuleType.JOB_SITE).isPresent();
    }
}

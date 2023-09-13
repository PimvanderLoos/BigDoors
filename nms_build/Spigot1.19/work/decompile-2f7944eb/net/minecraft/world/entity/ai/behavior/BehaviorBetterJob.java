package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class BehaviorBetterJob extends Behavior<EntityVillager> {

    final VillagerProfession profession;

    public BehaviorBetterJob(VillagerProfession villagerprofession) {
        super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        this.profession = villagerprofession;
    }

    protected void start(WorldServer worldserver, EntityVillager entityvillager, long i) {
        GlobalPos globalpos = (GlobalPos) entityvillager.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();

        worldserver.getPoiManager().getType(globalpos.pos()).ifPresent((holder) -> {
            BehaviorUtil.getNearbyVillagersWithCondition(entityvillager, (entityvillager1) -> {
                return this.competesForSameJobsite(globalpos, holder, entityvillager1);
            }).reduce(entityvillager, BehaviorBetterJob::selectWinner);
        });
    }

    private static EntityVillager selectWinner(EntityVillager entityvillager, EntityVillager entityvillager1) {
        EntityVillager entityvillager2;
        EntityVillager entityvillager3;

        if (entityvillager.getVillagerXp() > entityvillager1.getVillagerXp()) {
            entityvillager2 = entityvillager;
            entityvillager3 = entityvillager1;
        } else {
            entityvillager2 = entityvillager1;
            entityvillager3 = entityvillager;
        }

        entityvillager3.getBrain().eraseMemory(MemoryModuleType.JOB_SITE);
        return entityvillager2;
    }

    private boolean competesForSameJobsite(GlobalPos globalpos, Holder<VillagePlaceType> holder, EntityVillager entityvillager) {
        return this.hasJobSite(entityvillager) && globalpos.equals(entityvillager.getBrain().getMemory(MemoryModuleType.JOB_SITE).get()) && this.hasMatchingProfession(holder, entityvillager.getVillagerData().getProfession());
    }

    private boolean hasMatchingProfession(Holder<VillagePlaceType> holder, VillagerProfession villagerprofession) {
        return villagerprofession.heldJobSite().test(holder);
    }

    private boolean hasJobSite(EntityVillager entityvillager) {
        return entityvillager.getBrain().getMemory(MemoryModuleType.JOB_SITE).isPresent();
    }
}

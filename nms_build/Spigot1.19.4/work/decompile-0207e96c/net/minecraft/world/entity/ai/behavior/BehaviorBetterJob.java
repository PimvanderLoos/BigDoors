package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class BehaviorBetterJob {

    public BehaviorBetterJob() {}

    public static BehaviorControl<EntityVillager> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.JOB_SITE), behaviorbuilder_b.present(MemoryModuleType.NEAREST_LIVING_ENTITIES)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entityvillager, i) -> {
                    GlobalPos globalpos = (GlobalPos) behaviorbuilder_b.get(memoryaccessor);

                    worldserver.getPoiManager().getType(globalpos.pos()).ifPresent((holder) -> {
                        ((List) behaviorbuilder_b.get(memoryaccessor1)).stream().filter((entityliving) -> {
                            return entityliving instanceof EntityVillager && entityliving != entityvillager;
                        }).map((entityliving) -> {
                            return (EntityVillager) entityliving;
                        }).filter(EntityLiving::isAlive).filter((entityvillager1) -> {
                            return competesForSameJobsite(globalpos, holder, entityvillager1);
                        }).reduce(entityvillager, BehaviorBetterJob::selectWinner);
                    });
                    return true;
                };
            });
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

    private static boolean competesForSameJobsite(GlobalPos globalpos, Holder<VillagePlaceType> holder, EntityVillager entityvillager) {
        Optional<GlobalPos> optional = entityvillager.getBrain().getMemory(MemoryModuleType.JOB_SITE);

        return optional.isPresent() && globalpos.equals(optional.get()) && hasMatchingProfession(holder, entityvillager.getVillagerData().getProfession());
    }

    private static boolean hasMatchingProfession(Holder<VillagePlaceType> holder, VillagerProfession villagerprofession) {
        return villagerprofession.heldJobSite().test(holder);
    }
}

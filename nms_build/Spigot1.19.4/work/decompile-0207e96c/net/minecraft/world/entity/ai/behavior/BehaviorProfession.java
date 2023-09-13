package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;

public class BehaviorProfession {

    public BehaviorProfession() {}

    public static BehaviorControl<EntityVillager> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.JOB_SITE)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entityvillager, i) -> {
                    VillagerData villagerdata = entityvillager.getVillagerData();

                    if (villagerdata.getProfession() != VillagerProfession.NONE && villagerdata.getProfession() != VillagerProfession.NITWIT && entityvillager.getVillagerXp() == 0 && villagerdata.getLevel() <= 1) {
                        entityvillager.setVillagerData(entityvillager.getVillagerData().setProfession(VillagerProfession.NONE));
                        entityvillager.refreshBrain(worldserver);
                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }
}

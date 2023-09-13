package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.pathfinder.PathEntity;

public class BehaviorLeaveJob {

    public BehaviorLeaveJob() {}

    public static BehaviorControl<EntityVillager> create(float f) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.POTENTIAL_JOB_SITE), behaviorbuilder_b.absent(MemoryModuleType.JOB_SITE), behaviorbuilder_b.present(MemoryModuleType.NEAREST_LIVING_ENTITIES), behaviorbuilder_b.registered(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3, memoryaccessor4) -> {
                return (worldserver, entityvillager, i) -> {
                    if (entityvillager.isBaby()) {
                        return false;
                    } else if (entityvillager.getVillagerData().getProfession() != VillagerProfession.NONE) {
                        return false;
                    } else {
                        BlockPosition blockposition = ((GlobalPos) behaviorbuilder_b.get(memoryaccessor)).pos();
                        Optional<Holder<VillagePlaceType>> optional = worldserver.getPoiManager().getType(blockposition);

                        if (optional.isEmpty()) {
                            return true;
                        } else {
                            ((List) behaviorbuilder_b.get(memoryaccessor2)).stream().filter((entityliving) -> {
                                return entityliving instanceof EntityVillager && entityliving != entityvillager;
                            }).map((entityliving) -> {
                                return (EntityVillager) entityliving;
                            }).filter(EntityLiving::isAlive).filter((entityvillager1) -> {
                                return nearbyWantsJobsite((Holder) optional.get(), entityvillager1, blockposition);
                            }).findFirst().ifPresent((entityvillager1) -> {
                                memoryaccessor3.erase();
                                memoryaccessor4.erase();
                                memoryaccessor.erase();
                                if (entityvillager1.getBrain().getMemory(MemoryModuleType.JOB_SITE).isEmpty()) {
                                    BehaviorUtil.setWalkAndLookTargetMemories(entityvillager1, blockposition, f, 1);
                                    entityvillager1.getBrain().setMemory(MemoryModuleType.POTENTIAL_JOB_SITE, (Object) GlobalPos.of(worldserver.dimension(), blockposition));
                                    PacketDebug.sendPoiTicketCountPacket(worldserver, blockposition);
                                }

                            });
                            return true;
                        }
                    }
                };
            });
        });
    }

    private static boolean nearbyWantsJobsite(Holder<VillagePlaceType> holder, EntityVillager entityvillager, BlockPosition blockposition) {
        boolean flag = entityvillager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();

        if (flag) {
            return false;
        } else {
            Optional<GlobalPos> optional = entityvillager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
            VillagerProfession villagerprofession = entityvillager.getVillagerData().getProfession();

            return villagerprofession.heldJobSite().test(holder) ? (optional.isEmpty() ? canReachPos(entityvillager, blockposition, (VillagePlaceType) holder.value()) : ((GlobalPos) optional.get()).pos().equals(blockposition)) : false;
        }
    }

    private static boolean canReachPos(EntityCreature entitycreature, BlockPosition blockposition, VillagePlaceType villageplacetype) {
        PathEntity pathentity = entitycreature.getNavigation().createPath(blockposition, villageplacetype.validRange());

        return pathentity != null && pathentity.canReach();
    }
}

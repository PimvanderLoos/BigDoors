package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.pathfinder.PathEntity;

public class BehaviorLeaveJob extends Behavior<EntityVillager> {

    private final float speedModifier;

    public BehaviorLeaveJob(float f) {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        this.speedModifier = f;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityVillager entityvillager) {
        return entityvillager.isBaby() ? false : entityvillager.getVillagerData().getProfession() == VillagerProfession.NONE;
    }

    protected void start(WorldServer worldserver, EntityVillager entityvillager, long i) {
        BlockPosition blockposition = ((GlobalPos) entityvillager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get()).pos();
        Optional<VillagePlaceType> optional = worldserver.getPoiManager().getType(blockposition);

        if (optional.isPresent()) {
            BehaviorUtil.getNearbyVillagersWithCondition(entityvillager, (entityvillager1) -> {
                return this.nearbyWantsJobsite((VillagePlaceType) optional.get(), entityvillager1, blockposition);
            }).findFirst().ifPresent((entityvillager1) -> {
                this.yieldJobSite(worldserver, entityvillager, entityvillager1, blockposition, entityvillager1.getBrain().getMemory(MemoryModuleType.JOB_SITE).isPresent());
            });
        }
    }

    private boolean nearbyWantsJobsite(VillagePlaceType villageplacetype, EntityVillager entityvillager, BlockPosition blockposition) {
        boolean flag = entityvillager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();

        if (flag) {
            return false;
        } else {
            Optional<GlobalPos> optional = entityvillager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
            VillagerProfession villagerprofession = entityvillager.getVillagerData().getProfession();

            return entityvillager.getVillagerData().getProfession() != VillagerProfession.NONE && villagerprofession.getJobPoiType().getPredicate().test(villageplacetype) ? (!optional.isPresent() ? this.canReachPos(entityvillager, blockposition, villageplacetype) : ((GlobalPos) optional.get()).pos().equals(blockposition)) : false;
        }
    }

    private void yieldJobSite(WorldServer worldserver, EntityVillager entityvillager, EntityVillager entityvillager1, BlockPosition blockposition, boolean flag) {
        this.eraseMemories(entityvillager);
        if (!flag) {
            BehaviorUtil.setWalkAndLookTargetMemories(entityvillager1, blockposition, this.speedModifier, 1);
            entityvillager1.getBrain().setMemory(MemoryModuleType.POTENTIAL_JOB_SITE, (Object) GlobalPos.of(worldserver.dimension(), blockposition));
            PacketDebug.sendPoiTicketCountPacket(worldserver, blockposition);
        }

    }

    private boolean canReachPos(EntityVillager entityvillager, BlockPosition blockposition, VillagePlaceType villageplacetype) {
        PathEntity pathentity = entityvillager.getNavigation().createPath(blockposition, villageplacetype.getValidRange());

        return pathentity != null && pathentity.canReach();
    }

    private void eraseMemories(EntityVillager entityvillager) {
        entityvillager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        entityvillager.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        entityvillager.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}

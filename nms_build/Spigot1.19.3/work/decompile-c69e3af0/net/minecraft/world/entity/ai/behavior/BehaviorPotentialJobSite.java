package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.schedule.Activity;

public class BehaviorPotentialJobSite extends Behavior<EntityVillager> {

    private static final int TICKS_UNTIL_TIMEOUT = 1200;
    final float speedModifier;

    public BehaviorPotentialJobSite(float f) {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryStatus.VALUE_PRESENT), 1200);
        this.speedModifier = f;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityVillager entityvillager) {
        return (Boolean) entityvillager.getBrain().getActiveNonCoreActivity().map((activity) -> {
            return activity == Activity.IDLE || activity == Activity.WORK || activity == Activity.PLAY;
        }).orElse(true);
    }

    protected boolean canStillUse(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return entityvillager.getBrain().hasMemoryValue(MemoryModuleType.POTENTIAL_JOB_SITE);
    }

    protected void tick(WorldServer worldserver, EntityVillager entityvillager, long i) {
        BehaviorUtil.setWalkAndLookTargetMemories(entityvillager, ((GlobalPos) entityvillager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get()).pos(), this.speedModifier, 1);
    }

    protected void stop(WorldServer worldserver, EntityVillager entityvillager, long i) {
        Optional<GlobalPos> optional = entityvillager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);

        optional.ifPresent((globalpos) -> {
            BlockPosition blockposition = globalpos.pos();
            WorldServer worldserver1 = worldserver.getServer().getLevel(globalpos.dimension());

            if (worldserver1 != null) {
                VillagePlace villageplace = worldserver1.getPoiManager();

                if (villageplace.exists(blockposition, (holder) -> {
                    return true;
                })) {
                    villageplace.release(blockposition);
                }

                PacketDebug.sendPoiTicketCountPacket(worldserver, blockposition);
            }
        });
        entityvillager.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}

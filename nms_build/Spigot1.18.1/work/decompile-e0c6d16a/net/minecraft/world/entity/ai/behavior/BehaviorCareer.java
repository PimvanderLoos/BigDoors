package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class BehaviorCareer extends Behavior<EntityVillager> {

    public BehaviorCareer() {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryStatus.VALUE_PRESENT));
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityVillager entityvillager) {
        BlockPosition blockposition = ((GlobalPos) entityvillager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get()).pos();

        return blockposition.closerThan((IPosition) entityvillager.position(), 2.0D) || entityvillager.assignProfessionWhenSpawned();
    }

    protected void start(WorldServer worldserver, EntityVillager entityvillager, long i) {
        GlobalPos globalpos = (GlobalPos) entityvillager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get();

        entityvillager.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        entityvillager.getBrain().setMemory(MemoryModuleType.JOB_SITE, (Object) globalpos);
        worldserver.broadcastEntityEvent(entityvillager, (byte) 14);
        if (entityvillager.getVillagerData().getProfession() == VillagerProfession.NONE) {
            MinecraftServer minecraftserver = worldserver.getServer();

            Optional.ofNullable(minecraftserver.getLevel(globalpos.dimension())).flatMap((worldserver1) -> {
                return worldserver1.getPoiManager().getType(globalpos.pos());
            }).flatMap((villageplacetype) -> {
                return IRegistry.VILLAGER_PROFESSION.stream().filter((villagerprofession) -> {
                    return villagerprofession.getJobPoiType() == villageplacetype;
                }).findFirst();
            }).ifPresent((villagerprofession) -> {
                entityvillager.setVillagerData(entityvillager.getVillagerData().setProfession(villagerprofession));
                entityvillager.refreshBrain(worldserver);
            });
        }
    }
}

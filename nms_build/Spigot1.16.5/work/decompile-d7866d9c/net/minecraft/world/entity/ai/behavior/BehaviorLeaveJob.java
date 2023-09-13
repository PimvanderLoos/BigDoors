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

    private final float b;

    public BehaviorLeaveJob(float f) {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_ABSENT, MemoryModuleType.MOBS, MemoryStatus.VALUE_PRESENT));
        this.b = f;
    }

    protected boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        return entityvillager.isBaby() ? false : entityvillager.getVillagerData().getProfession() == VillagerProfession.NONE;
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        BlockPosition blockposition = ((GlobalPos) entityvillager.getBehaviorController().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get()).getBlockPosition();
        Optional<VillagePlaceType> optional = worldserver.y().c(blockposition);

        if (optional.isPresent()) {
            BehaviorUtil.a(entityvillager, (entityvillager1) -> {
                return this.a((VillagePlaceType) optional.get(), entityvillager1, blockposition);
            }).findFirst().ifPresent((entityvillager1) -> {
                this.a(worldserver, entityvillager, entityvillager1, blockposition, entityvillager1.getBehaviorController().getMemory(MemoryModuleType.JOB_SITE).isPresent());
            });
        }
    }

    private boolean a(VillagePlaceType villageplacetype, EntityVillager entityvillager, BlockPosition blockposition) {
        boolean flag = entityvillager.getBehaviorController().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();

        if (flag) {
            return false;
        } else {
            Optional<GlobalPos> optional = entityvillager.getBehaviorController().getMemory(MemoryModuleType.JOB_SITE);
            VillagerProfession villagerprofession = entityvillager.getVillagerData().getProfession();

            return entityvillager.getVillagerData().getProfession() != VillagerProfession.NONE && villagerprofession.b().c().test(villageplacetype) ? (!optional.isPresent() ? this.a(entityvillager, blockposition, villageplacetype) : ((GlobalPos) optional.get()).getBlockPosition().equals(blockposition)) : false;
        }
    }

    private void a(WorldServer worldserver, EntityVillager entityvillager, EntityVillager entityvillager1, BlockPosition blockposition, boolean flag) {
        this.a(entityvillager);
        if (!flag) {
            BehaviorUtil.a(entityvillager1, blockposition, this.b, 1);
            entityvillager1.getBehaviorController().setMemory(MemoryModuleType.POTENTIAL_JOB_SITE, (Object) GlobalPos.create(worldserver.getDimensionKey(), blockposition));
            PacketDebug.c(worldserver, blockposition);
        }

    }

    private boolean a(EntityVillager entityvillager, BlockPosition blockposition, VillagePlaceType villageplacetype) {
        PathEntity pathentity = entityvillager.getNavigation().a(blockposition, villageplacetype.d());

        return pathentity != null && pathentity.j();
    }

    private void a(EntityVillager entityvillager) {
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.LOOK_TARGET);
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}

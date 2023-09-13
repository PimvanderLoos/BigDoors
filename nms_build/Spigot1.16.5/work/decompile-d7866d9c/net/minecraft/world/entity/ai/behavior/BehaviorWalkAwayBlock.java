package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.RandomPositionGenerator;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.phys.Vec3D;

public class BehaviorWalkAwayBlock extends Behavior<EntityVillager> {

    private final MemoryModuleType<GlobalPos> b;
    private final float c;
    private final int d;
    private final int e;
    private final int f;

    public BehaviorWalkAwayBlock(MemoryModuleType<GlobalPos> memorymoduletype, float f, int i, int j, int k) {
        super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, memorymoduletype, MemoryStatus.VALUE_PRESENT));
        this.b = memorymoduletype;
        this.c = f;
        this.d = i;
        this.e = j;
        this.f = k;
    }

    private void a(EntityVillager entityvillager, long i) {
        BehaviorController<?> behaviorcontroller = entityvillager.getBehaviorController();

        entityvillager.a(this.b);
        behaviorcontroller.removeMemory(this.b);
        behaviorcontroller.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object) i);
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        BehaviorController<?> behaviorcontroller = entityvillager.getBehaviorController();

        behaviorcontroller.getMemory(this.b).ifPresent((globalpos) -> {
            if (!this.a(worldserver, globalpos) && !this.a(worldserver, entityvillager)) {
                if (this.a(entityvillager, globalpos)) {
                    Vec3D vec3d = null;
                    int j = 0;

                    for (boolean flag = true; j < 1000 && (vec3d == null || this.a(entityvillager, GlobalPos.create(worldserver.getDimensionKey(), new BlockPosition(vec3d)))); ++j) {
                        vec3d = RandomPositionGenerator.b(entityvillager, 15, 7, Vec3D.c((BaseBlockPosition) globalpos.getBlockPosition()));
                    }

                    if (j == 1000) {
                        this.a(entityvillager, i);
                        return;
                    }

                    behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(vec3d, this.c, this.d)));
                } else if (!this.a(worldserver, entityvillager, globalpos)) {
                    behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(globalpos.getBlockPosition(), this.c, this.d)));
                }
            } else {
                this.a(entityvillager, i);
            }

        });
    }

    private boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        Optional<Long> optional = entityvillager.getBehaviorController().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);

        return optional.isPresent() ? worldserver.getTime() - (Long) optional.get() > (long) this.f : false;
    }

    private boolean a(EntityVillager entityvillager, GlobalPos globalpos) {
        return globalpos.getBlockPosition().k(entityvillager.getChunkCoordinates()) > this.e;
    }

    private boolean a(WorldServer worldserver, GlobalPos globalpos) {
        return globalpos.getDimensionManager() != worldserver.getDimensionKey();
    }

    private boolean a(WorldServer worldserver, EntityVillager entityvillager, GlobalPos globalpos) {
        return globalpos.getDimensionManager() == worldserver.getDimensionKey() && globalpos.getBlockPosition().k(entityvillager.getChunkCoordinates()) <= this.d;
    }
}

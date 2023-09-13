package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;

public class BehaviorHome extends Behavior<EntityLiving> {

    private final float b;
    private final int c;
    private final int d;
    private Optional<BlockPosition> e = Optional.empty();

    public BehaviorHome(int i, float f, int j) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryStatus.REGISTERED, MemoryModuleType.HIDING_PLACE, MemoryStatus.REGISTERED));
        this.c = i;
        this.b = f;
        this.d = j;
    }

    @Override
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        Optional<BlockPosition> optional = worldserver.y().c((villageplacetype) -> {
            return villageplacetype == VillagePlaceType.r;
        }, (blockposition) -> {
            return true;
        }, entityliving.getChunkCoordinates(), this.d + 1, VillagePlace.Occupancy.ANY);

        if (optional.isPresent() && ((BlockPosition) optional.get()).a((IPosition) entityliving.getPositionVector(), (double) this.d)) {
            this.e = optional;
        } else {
            this.e = Optional.empty();
        }

        return true;
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();
        Optional<BlockPosition> optional = this.e;

        if (!optional.isPresent()) {
            optional = worldserver.y().a((villageplacetype) -> {
                return villageplacetype == VillagePlaceType.r;
            }, (blockposition) -> {
                return true;
            }, VillagePlace.Occupancy.ANY, entityliving.getChunkCoordinates(), this.c, entityliving.getRandom());
            if (!optional.isPresent()) {
                Optional<GlobalPos> optional1 = behaviorcontroller.getMemory(MemoryModuleType.HOME);

                if (optional1.isPresent()) {
                    optional = Optional.of(((GlobalPos) optional1.get()).getBlockPosition());
                }
            }
        }

        if (optional.isPresent()) {
            behaviorcontroller.removeMemory(MemoryModuleType.PATH);
            behaviorcontroller.removeMemory(MemoryModuleType.LOOK_TARGET);
            behaviorcontroller.removeMemory(MemoryModuleType.BREED_TARGET);
            behaviorcontroller.removeMemory(MemoryModuleType.INTERACTION_TARGET);
            behaviorcontroller.setMemory(MemoryModuleType.HIDING_PLACE, (Object) GlobalPos.create(worldserver.getDimensionKey(), (BlockPosition) optional.get()));
            if (!((BlockPosition) optional.get()).a((IPosition) entityliving.getPositionVector(), (double) this.d)) {
                behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget((BlockPosition) optional.get(), this.b, this.d)));
            }
        }

    }
}

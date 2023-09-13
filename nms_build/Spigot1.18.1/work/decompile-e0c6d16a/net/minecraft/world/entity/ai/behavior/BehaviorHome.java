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

    private final float speedModifier;
    private final int radius;
    private final int closeEnoughDist;
    private Optional<BlockPosition> currentPos = Optional.empty();

    public BehaviorHome(int i, float f, int j) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryStatus.REGISTERED, MemoryModuleType.HIDING_PLACE, MemoryStatus.REGISTERED));
        this.radius = i;
        this.speedModifier = f;
        this.closeEnoughDist = j;
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityLiving entityliving) {
        Optional<BlockPosition> optional = worldserver.getPoiManager().find((villageplacetype) -> {
            return villageplacetype == VillagePlaceType.HOME;
        }, (blockposition) -> {
            return true;
        }, entityliving.blockPosition(), this.closeEnoughDist + 1, VillagePlace.Occupancy.ANY);

        if (optional.isPresent() && ((BlockPosition) optional.get()).closerThan((IPosition) entityliving.position(), (double) this.closeEnoughDist)) {
            this.currentPos = optional;
        } else {
            this.currentPos = Optional.empty();
        }

        return true;
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();
        Optional<BlockPosition> optional = this.currentPos;

        if (!optional.isPresent()) {
            optional = worldserver.getPoiManager().getRandom((villageplacetype) -> {
                return villageplacetype == VillagePlaceType.HOME;
            }, (blockposition) -> {
                return true;
            }, VillagePlace.Occupancy.ANY, entityliving.blockPosition(), this.radius, entityliving.getRandom());
            if (!optional.isPresent()) {
                Optional<GlobalPos> optional1 = behaviorcontroller.getMemory(MemoryModuleType.HOME);

                if (optional1.isPresent()) {
                    optional = Optional.of(((GlobalPos) optional1.get()).pos());
                }
            }
        }

        if (optional.isPresent()) {
            behaviorcontroller.eraseMemory(MemoryModuleType.PATH);
            behaviorcontroller.eraseMemory(MemoryModuleType.LOOK_TARGET);
            behaviorcontroller.eraseMemory(MemoryModuleType.BREED_TARGET);
            behaviorcontroller.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
            behaviorcontroller.setMemory(MemoryModuleType.HIDING_PLACE, (Object) GlobalPos.of(worldserver.dimension(), (BlockPosition) optional.get()));
            if (!((BlockPosition) optional.get()).closerThan((IPosition) entityliving.position(), (double) this.closeEnoughDist)) {
                behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget((BlockPosition) optional.get(), this.speedModifier, this.closeEnoughDist)));
            }
        }

    }
}

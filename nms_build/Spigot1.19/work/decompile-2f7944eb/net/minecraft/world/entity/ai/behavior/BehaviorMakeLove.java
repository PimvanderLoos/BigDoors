package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.level.pathfinder.PathEntity;

public class BehaviorMakeLove extends Behavior<EntityVillager> {

    private static final int INTERACT_DIST_SQR = 5;
    private static final float SPEED_MODIFIER = 0.5F;
    private long birthTimestamp;

    public BehaviorMakeLove() {
        super(ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT), 350, 350);
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityVillager entityvillager) {
        return this.isBreedingPossible(entityvillager);
    }

    protected boolean canStillUse(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return i <= this.birthTimestamp && this.isBreedingPossible(entityvillager);
    }

    protected void start(WorldServer worldserver, EntityVillager entityvillager, long i) {
        EntityAgeable entityageable = (EntityAgeable) entityvillager.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();

        BehaviorUtil.lockGazeAndWalkToEachOther(entityvillager, entityageable, 0.5F);
        worldserver.broadcastEntityEvent(entityageable, (byte) 18);
        worldserver.broadcastEntityEvent(entityvillager, (byte) 18);
        int j = 275 + entityvillager.getRandom().nextInt(50);

        this.birthTimestamp = i + (long) j;
    }

    protected void tick(WorldServer worldserver, EntityVillager entityvillager, long i) {
        EntityVillager entityvillager1 = (EntityVillager) entityvillager.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();

        if (entityvillager.distanceToSqr((Entity) entityvillager1) <= 5.0D) {
            BehaviorUtil.lockGazeAndWalkToEachOther(entityvillager, entityvillager1, 0.5F);
            if (i >= this.birthTimestamp) {
                entityvillager.eatAndDigestFood();
                entityvillager1.eatAndDigestFood();
                this.tryToGiveBirth(worldserver, entityvillager, entityvillager1);
            } else if (entityvillager.getRandom().nextInt(35) == 0) {
                worldserver.broadcastEntityEvent(entityvillager1, (byte) 12);
                worldserver.broadcastEntityEvent(entityvillager, (byte) 12);
            }

        }
    }

    private void tryToGiveBirth(WorldServer worldserver, EntityVillager entityvillager, EntityVillager entityvillager1) {
        Optional<BlockPosition> optional = this.takeVacantBed(worldserver, entityvillager);

        if (!optional.isPresent()) {
            worldserver.broadcastEntityEvent(entityvillager1, (byte) 13);
            worldserver.broadcastEntityEvent(entityvillager, (byte) 13);
        } else {
            Optional<EntityVillager> optional1 = this.breed(worldserver, entityvillager, entityvillager1);

            if (optional1.isPresent()) {
                this.giveBedToChild(worldserver, (EntityVillager) optional1.get(), (BlockPosition) optional.get());
            } else {
                worldserver.getPoiManager().release((BlockPosition) optional.get());
                PacketDebug.sendPoiTicketCountPacket(worldserver, (BlockPosition) optional.get());
            }
        }

    }

    protected void stop(WorldServer worldserver, EntityVillager entityvillager, long i) {
        entityvillager.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
    }

    private boolean isBreedingPossible(EntityVillager entityvillager) {
        BehaviorController<EntityVillager> behaviorcontroller = entityvillager.getBrain();
        Optional<EntityAgeable> optional = behaviorcontroller.getMemory(MemoryModuleType.BREED_TARGET).filter((entityageable) -> {
            return entityageable.getType() == EntityTypes.VILLAGER;
        });

        return !optional.isPresent() ? false : BehaviorUtil.targetIsValid(behaviorcontroller, MemoryModuleType.BREED_TARGET, EntityTypes.VILLAGER) && entityvillager.canBreed() && ((EntityAgeable) optional.get()).canBreed();
    }

    private Optional<BlockPosition> takeVacantBed(WorldServer worldserver, EntityVillager entityvillager) {
        return worldserver.getPoiManager().take((holder) -> {
            return holder.is(PoiTypes.HOME);
        }, (holder, blockposition) -> {
            return this.canReach(entityvillager, blockposition, holder);
        }, entityvillager.blockPosition(), 48);
    }

    private boolean canReach(EntityVillager entityvillager, BlockPosition blockposition, Holder<VillagePlaceType> holder) {
        PathEntity pathentity = entityvillager.getNavigation().createPath(blockposition, ((VillagePlaceType) holder.value()).validRange());

        return pathentity != null && pathentity.canReach();
    }

    private Optional<EntityVillager> breed(WorldServer worldserver, EntityVillager entityvillager, EntityVillager entityvillager1) {
        EntityVillager entityvillager2 = entityvillager.getBreedOffspring(worldserver, entityvillager1);

        if (entityvillager2 == null) {
            return Optional.empty();
        } else {
            entityvillager.setAge(6000);
            entityvillager1.setAge(6000);
            entityvillager2.setAge(-24000);
            entityvillager2.moveTo(entityvillager.getX(), entityvillager.getY(), entityvillager.getZ(), 0.0F, 0.0F);
            worldserver.addFreshEntityWithPassengers(entityvillager2);
            worldserver.broadcastEntityEvent(entityvillager2, (byte) 12);
            return Optional.of(entityvillager2);
        }
    }

    private void giveBedToChild(WorldServer worldserver, EntityVillager entityvillager, BlockPosition blockposition) {
        GlobalPos globalpos = GlobalPos.of(worldserver.dimension(), blockposition);

        entityvillager.getBrain().setMemory(MemoryModuleType.HOME, (Object) globalpos);
    }
}

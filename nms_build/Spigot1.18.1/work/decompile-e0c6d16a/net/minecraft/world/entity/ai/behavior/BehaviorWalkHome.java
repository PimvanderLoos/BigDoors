package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.level.pathfinder.PathEntity;

public class BehaviorWalkHome extends Behavior<EntityLiving> {

    private static final int CACHE_TIMEOUT = 40;
    private static final int BATCH_SIZE = 5;
    private static final int RATE = 20;
    private static final int OK_DISTANCE_SQR = 4;
    private final float speedModifier;
    private final Long2LongMap batchCache = new Long2LongOpenHashMap();
    private int triedCount;
    private long lastUpdate;

    public BehaviorWalkHome(float f) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT));
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityLiving entityliving) {
        if (worldserver.getGameTime() - this.lastUpdate < 20L) {
            return false;
        } else {
            EntityCreature entitycreature = (EntityCreature) entityliving;
            VillagePlace villageplace = worldserver.getPoiManager();
            Optional<BlockPosition> optional = villageplace.findClosest(VillagePlaceType.HOME.getPredicate(), entityliving.blockPosition(), 48, VillagePlace.Occupancy.ANY);

            return optional.isPresent() && ((BlockPosition) optional.get()).distSqr(entitycreature.blockPosition()) > 4.0D;
        }
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        this.triedCount = 0;
        this.lastUpdate = worldserver.getGameTime() + (long) worldserver.getRandom().nextInt(20);
        EntityCreature entitycreature = (EntityCreature) entityliving;
        VillagePlace villageplace = worldserver.getPoiManager();
        Predicate<BlockPosition> predicate = (blockposition) -> {
            long j = blockposition.asLong();

            if (this.batchCache.containsKey(j)) {
                return false;
            } else if (++this.triedCount >= 5) {
                return false;
            } else {
                this.batchCache.put(j, this.lastUpdate + 40L);
                return true;
            }
        };
        Stream<BlockPosition> stream = villageplace.findAll(VillagePlaceType.HOME.getPredicate(), predicate, entityliving.blockPosition(), 48, VillagePlace.Occupancy.ANY);
        PathEntity pathentity = entitycreature.getNavigation().createPath(stream, VillagePlaceType.HOME.getValidRange());

        if (pathentity != null && pathentity.canReach()) {
            BlockPosition blockposition = pathentity.getTarget();
            Optional<VillagePlaceType> optional = villageplace.getType(blockposition);

            if (optional.isPresent()) {
                entityliving.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(blockposition, this.speedModifier, 1)));
                PacketDebug.sendPoiTicketCountPacket(worldserver, blockposition);
            }
        } else if (this.triedCount < 5) {
            this.batchCache.long2LongEntrySet().removeIf((entry) -> {
                return entry.getLongValue() < this.lastUpdate;
            });
        }

    }
}

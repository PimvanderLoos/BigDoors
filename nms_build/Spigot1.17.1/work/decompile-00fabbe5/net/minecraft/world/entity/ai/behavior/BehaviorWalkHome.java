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
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        if (worldserver.getTime() - this.lastUpdate < 20L) {
            return false;
        } else {
            EntityCreature entitycreature = (EntityCreature) entityliving;
            VillagePlace villageplace = worldserver.A();
            Optional<BlockPosition> optional = villageplace.d(VillagePlaceType.HOME.c(), entityliving.getChunkCoordinates(), 48, VillagePlace.Occupancy.ANY);

            return optional.isPresent() && ((BlockPosition) optional.get()).j(entitycreature.getChunkCoordinates()) > 4.0D;
        }
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        this.triedCount = 0;
        this.lastUpdate = worldserver.getTime() + (long) worldserver.getRandom().nextInt(20);
        EntityCreature entitycreature = (EntityCreature) entityliving;
        VillagePlace villageplace = worldserver.A();
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
        Stream<BlockPosition> stream = villageplace.a(VillagePlaceType.HOME.c(), predicate, entityliving.getChunkCoordinates(), 48, VillagePlace.Occupancy.ANY);
        PathEntity pathentity = entitycreature.getNavigation().a(stream, VillagePlaceType.HOME.d());

        if (pathentity != null && pathentity.j()) {
            BlockPosition blockposition = pathentity.m();
            Optional<VillagePlaceType> optional = villageplace.c(blockposition);

            if (optional.isPresent()) {
                entityliving.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(blockposition, this.speedModifier, 1)));
                PacketDebug.c(worldserver, blockposition);
            }
        } else if (this.triedCount < 5) {
            this.batchCache.long2LongEntrySet().removeIf((entry) -> {
                return entry.getLongValue() < this.lastUpdate;
            });
        }

    }
}

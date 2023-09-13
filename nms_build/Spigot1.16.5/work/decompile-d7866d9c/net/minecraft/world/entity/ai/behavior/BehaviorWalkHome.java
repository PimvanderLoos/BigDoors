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

    private final float b;
    private final Long2LongMap c = new Long2LongOpenHashMap();
    private int d;
    private long e;

    public BehaviorWalkHome(float f) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT));
        this.b = f;
    }

    @Override
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        if (worldserver.getTime() - this.e < 20L) {
            return false;
        } else {
            EntityCreature entitycreature = (EntityCreature) entityliving;
            VillagePlace villageplace = worldserver.y();
            Optional<BlockPosition> optional = villageplace.d(VillagePlaceType.r.c(), entityliving.getChunkCoordinates(), 48, VillagePlace.Occupancy.ANY);

            return optional.isPresent() && ((BlockPosition) optional.get()).j(entitycreature.getChunkCoordinates()) > 4.0D;
        }
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        this.d = 0;
        this.e = worldserver.getTime() + (long) worldserver.getRandom().nextInt(20);
        EntityCreature entitycreature = (EntityCreature) entityliving;
        VillagePlace villageplace = worldserver.y();
        Predicate<BlockPosition> predicate = (blockposition) -> {
            long j = blockposition.asLong();

            if (this.c.containsKey(j)) {
                return false;
            } else if (++this.d >= 5) {
                return false;
            } else {
                this.c.put(j, this.e + 40L);
                return true;
            }
        };
        Stream<BlockPosition> stream = villageplace.a(VillagePlaceType.r.c(), predicate, entityliving.getChunkCoordinates(), 48, VillagePlace.Occupancy.ANY);
        PathEntity pathentity = entitycreature.getNavigation().a(stream, VillagePlaceType.r.d());

        if (pathentity != null && pathentity.j()) {
            BlockPosition blockposition = pathentity.m();
            Optional<VillagePlaceType> optional = villageplace.c(blockposition);

            if (optional.isPresent()) {
                entityliving.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(blockposition, this.b, 1)));
                PacketDebug.c(worldserver, blockposition);
            }
        } else if (this.d < 5) {
            this.c.long2LongEntrySet().removeIf((entry) -> {
                return entry.getLongValue() < this.e;
            });
        }

    }
}

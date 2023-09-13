package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class SensorGolemLastSeen extends Sensor<EntityLiving> {

    private static final int GOLEM_SCAN_RATE = 200;
    private static final int MEMORY_TIME_TO_LIVE = 600;

    public SensorGolemLastSeen() {
        this(200);
    }

    public SensorGolemLastSeen(int i) {
        super(i);
    }

    @Override
    protected void doTick(WorldServer worldserver, EntityLiving entityliving) {
        checkForNearbyGolem(entityliving);
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_LIVING_ENTITIES);
    }

    public static void checkForNearbyGolem(EntityLiving entityliving) {
        Optional<List<EntityLiving>> optional = entityliving.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);

        if (optional.isPresent()) {
            boolean flag = ((List) optional.get()).stream().anyMatch((entityliving1) -> {
                return entityliving1.getType().equals(EntityTypes.IRON_GOLEM);
            });

            if (flag) {
                golemDetected(entityliving);
            }

        }
    }

    public static void golemDetected(EntityLiving entityliving) {
        entityliving.getBrain().setMemoryWithExpiry(MemoryModuleType.GOLEM_DETECTED_RECENTLY, true, 600L);
    }
}

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

    public SensorGolemLastSeen() {
        this(200);
    }

    public SensorGolemLastSeen(int i) {
        super(i);
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving) {
        a(entityliving);
    }

    @Override
    public Set<MemoryModuleType<?>> a() {
        return ImmutableSet.of(MemoryModuleType.MOBS);
    }

    public static void a(EntityLiving entityliving) {
        Optional<List<EntityLiving>> optional = entityliving.getBehaviorController().getMemory(MemoryModuleType.MOBS);

        if (optional.isPresent()) {
            boolean flag = ((List) optional.get()).stream().anyMatch((entityliving1) -> {
                return entityliving1.getEntityType().equals(EntityTypes.IRON_GOLEM);
            });

            if (flag) {
                b(entityliving);
            }

        }
    }

    public static void b(EntityLiving entityliving) {
        entityliving.getBehaviorController().a(MemoryModuleType.GOLEM_DETECTED_RECENTLY, true, 600L);
    }
}

package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public abstract class NearestVisibleLivingEntitySensor extends Sensor<EntityLiving> {

    public NearestVisibleLivingEntitySensor() {}

    protected abstract boolean a(EntityLiving entityliving, EntityLiving entityliving1);

    protected abstract MemoryModuleType<EntityLiving> b();

    @Override
    public Set<MemoryModuleType<?>> a() {
        return ImmutableSet.of(this.b());
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving) {
        entityliving.getBehaviorController().setMemory(this.b(), this.b(entityliving));
    }

    private Optional<EntityLiving> b(EntityLiving entityliving) {
        return this.a(entityliving).flatMap((list) -> {
            Stream stream = list.stream().filter((entityliving1) -> {
                return this.a(entityliving, entityliving1);
            });

            Objects.requireNonNull(entityliving);
            return stream.min(Comparator.comparingDouble(entityliving::f));
        });
    }

    protected Optional<List<EntityLiving>> a(EntityLiving entityliving) {
        return entityliving.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }
}

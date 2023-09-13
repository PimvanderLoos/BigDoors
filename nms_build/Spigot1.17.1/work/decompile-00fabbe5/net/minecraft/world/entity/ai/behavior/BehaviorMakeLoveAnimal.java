package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.EntityAnimal;

public class BehaviorMakeLoveAnimal extends Behavior<EntityAnimal> {

    private static final int BREED_RANGE = 3;
    private static final int MIN_DURATION = 60;
    private static final int MAX_DURATION = 110;
    private final EntityTypes<? extends EntityAnimal> partnerType;
    private final float speedModifier;
    private long spawnChildAtTime;

    public BehaviorMakeLoveAnimal(EntityTypes<? extends EntityAnimal> entitytypes, float f) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), 110);
        this.partnerType = entitytypes;
        this.speedModifier = f;
    }

    protected boolean a(WorldServer worldserver, EntityAnimal entityanimal) {
        return entityanimal.isInLove() && this.c(entityanimal).isPresent();
    }

    protected void a(WorldServer worldserver, EntityAnimal entityanimal, long i) {
        EntityAnimal entityanimal1 = (EntityAnimal) this.c(entityanimal).get();

        entityanimal.getBehaviorController().setMemory(MemoryModuleType.BREED_TARGET, (Object) entityanimal1);
        entityanimal1.getBehaviorController().setMemory(MemoryModuleType.BREED_TARGET, (Object) entityanimal);
        BehaviorUtil.a(entityanimal, entityanimal1, this.speedModifier);
        int j = 60 + entityanimal.getRandom().nextInt(50);

        this.spawnChildAtTime = i + (long) j;
    }

    protected boolean b(WorldServer worldserver, EntityAnimal entityanimal, long i) {
        if (!this.b(entityanimal)) {
            return false;
        } else {
            EntityAnimal entityanimal1 = this.a(entityanimal);

            return entityanimal1.isAlive() && entityanimal.mate(entityanimal1) && BehaviorUtil.a(entityanimal.getBehaviorController(), (EntityLiving) entityanimal1) && i <= this.spawnChildAtTime;
        }
    }

    protected void d(WorldServer worldserver, EntityAnimal entityanimal, long i) {
        EntityAnimal entityanimal1 = this.a(entityanimal);

        BehaviorUtil.a(entityanimal, entityanimal1, this.speedModifier);
        if (entityanimal.a((Entity) entityanimal1, 3.0D)) {
            if (i >= this.spawnChildAtTime) {
                entityanimal.a(worldserver, entityanimal1);
                entityanimal.getBehaviorController().removeMemory(MemoryModuleType.BREED_TARGET);
                entityanimal1.getBehaviorController().removeMemory(MemoryModuleType.BREED_TARGET);
            }

        }
    }

    protected void c(WorldServer worldserver, EntityAnimal entityanimal, long i) {
        entityanimal.getBehaviorController().removeMemory(MemoryModuleType.BREED_TARGET);
        entityanimal.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
        entityanimal.getBehaviorController().removeMemory(MemoryModuleType.LOOK_TARGET);
        this.spawnChildAtTime = 0L;
    }

    private EntityAnimal a(EntityAnimal entityanimal) {
        return (EntityAnimal) entityanimal.getBehaviorController().getMemory(MemoryModuleType.BREED_TARGET).get();
    }

    private boolean b(EntityAnimal entityanimal) {
        BehaviorController<?> behaviorcontroller = entityanimal.getBehaviorController();

        return behaviorcontroller.hasMemory(MemoryModuleType.BREED_TARGET) && ((EntityAgeable) behaviorcontroller.getMemory(MemoryModuleType.BREED_TARGET).get()).getEntityType() == this.partnerType;
    }

    private Optional<? extends EntityAnimal> c(EntityAnimal entityanimal) {
        Stream stream = ((List) entityanimal.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).stream().filter((entityliving) -> {
            return entityliving.getEntityType() == this.partnerType;
        }).map((entityliving) -> {
            return (EntityAnimal) entityliving;
        });

        Objects.requireNonNull(entityanimal);
        return stream.filter(entityanimal::mate).findFirst();
    }
}

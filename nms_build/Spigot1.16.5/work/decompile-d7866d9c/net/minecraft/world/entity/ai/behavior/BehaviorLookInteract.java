package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorLookInteract extends Behavior<EntityLiving> {

    private final EntityTypes<?> b;
    private final int c;
    private final Predicate<EntityLiving> d;
    private final Predicate<EntityLiving> e;

    public BehaviorLookInteract(EntityTypes<?> entitytypes, int i, Predicate<EntityLiving> predicate, Predicate<EntityLiving> predicate1) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryStatus.VALUE_PRESENT));
        this.b = entitytypes;
        this.c = i * i;
        this.d = predicate1;
        this.e = predicate;
    }

    public BehaviorLookInteract(EntityTypes<?> entitytypes, int i) {
        this(entitytypes, i, (entityliving) -> {
            return true;
        }, (entityliving) -> {
            return true;
        });
    }

    @Override
    public boolean a(WorldServer worldserver, EntityLiving entityliving) {
        return this.e.test(entityliving) && this.b(entityliving).stream().anyMatch(this::a);
    }

    @Override
    public void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        super.a(worldserver, entityliving, i);
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();

        behaviorcontroller.getMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent((list) -> {
            list.stream().filter((entityliving1) -> {
                return entityliving1.h((Entity) entityliving) <= (double) this.c;
            }).filter(this::a).findFirst().ifPresent((entityliving1) -> {
                behaviorcontroller.setMemory(MemoryModuleType.INTERACTION_TARGET, (Object) entityliving1);
                behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving1, true)));
            });
        });
    }

    private boolean a(EntityLiving entityliving) {
        return this.b.equals(entityliving.getEntityType()) && this.d.test(entityliving);
    }

    private List<EntityLiving> b(EntityLiving entityliving) {
        return (List) entityliving.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_MOBS).get();
    }
}

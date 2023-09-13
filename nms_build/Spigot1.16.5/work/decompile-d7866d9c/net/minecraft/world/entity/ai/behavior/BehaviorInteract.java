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
import net.minecraft.world.entity.ai.memory.MemoryTarget;

public class BehaviorInteract<E extends EntityLiving, T extends EntityLiving> extends Behavior<E> {

    private final int b;
    private final float c;
    private final EntityTypes<? extends T> d;
    private final int e;
    private final Predicate<T> f;
    private final Predicate<E> g;
    private final MemoryModuleType<T> h;

    public BehaviorInteract(EntityTypes<? extends T> entitytypes, int i, Predicate<E> predicate, Predicate<T> predicate1, MemoryModuleType<T> memorymoduletype, float f, int j) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryStatus.VALUE_PRESENT));
        this.d = entitytypes;
        this.c = f;
        this.e = i * i;
        this.b = j;
        this.f = predicate1;
        this.g = predicate;
        this.h = memorymoduletype;
    }

    public static <T extends EntityLiving> BehaviorInteract<EntityLiving, T> a(EntityTypes<? extends T> entitytypes, int i, MemoryModuleType<T> memorymoduletype, float f, int j) {
        return new BehaviorInteract<>(entitytypes, i, (entityliving) -> {
            return true;
        }, (entityliving) -> {
            return true;
        }, memorymoduletype, f, j);
    }

    @Override
    protected boolean a(WorldServer worldserver, E e0) {
        return this.g.test(e0) && this.a(e0);
    }

    private boolean a(E e0) {
        List<EntityLiving> list = (List) e0.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_MOBS).get();

        return list.stream().anyMatch(this::b);
    }

    private boolean b(EntityLiving entityliving) {
        return this.d.equals(entityliving.getEntityType()) && this.f.test(entityliving);
    }

    @Override
    protected void a(WorldServer worldserver, E e0, long i) {
        BehaviorController<?> behaviorcontroller = e0.getBehaviorController();

        behaviorcontroller.getMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent((list) -> {
            list.stream().filter((entityliving) -> {
                return this.d.equals(entityliving.getEntityType());
            }).map((entityliving) -> {
                return entityliving;
            }).filter((entityliving) -> {
                return entityliving.h((Entity) e0) <= (double) this.e;
            }).filter(this.f).findFirst().ifPresent((entityliving) -> {
                behaviorcontroller.setMemory(this.h, (Object) entityliving);
                behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving, true)));
                behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(new BehaviorPositionEntity(entityliving, false), this.c, this.b)));
            });
        });
    }
}

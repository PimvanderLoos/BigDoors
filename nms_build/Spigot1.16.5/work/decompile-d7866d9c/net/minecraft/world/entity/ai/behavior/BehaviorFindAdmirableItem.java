package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.EntityItem;

public class BehaviorFindAdmirableItem<E extends EntityLiving> extends Behavior<E> {

    private final Predicate<E> b;
    private final int c;
    private final float d;

    public BehaviorFindAdmirableItem(float f, boolean flag, int i) {
        this((entityliving) -> {
            return true;
        }, f, flag, i);
    }

    public BehaviorFindAdmirableItem(Predicate<E> predicate, float f, boolean flag, int i) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, flag ? MemoryStatus.REGISTERED : MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT));
        this.b = predicate;
        this.c = i;
        this.d = f;
    }

    @Override
    protected boolean a(WorldServer worldserver, E e0) {
        return this.b.test(e0) && this.a(e0).a((Entity) e0, (double) this.c);
    }

    @Override
    protected void a(WorldServer worldserver, E e0, long i) {
        BehaviorUtil.a(e0, (Entity) this.a(e0), this.d, 0);
    }

    private EntityItem a(E e0) {
        return (EntityItem) e0.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
    }
}

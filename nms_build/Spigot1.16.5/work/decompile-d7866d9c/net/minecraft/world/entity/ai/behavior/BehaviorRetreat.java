package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorRetreat<E extends EntityInsentient> extends Behavior<E> {

    private final int b;
    private final float c;

    public BehaviorRetreat(int i, float f) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryStatus.VALUE_PRESENT));
        this.b = i;
        this.c = f;
    }

    protected boolean a(WorldServer worldserver, E e0) {
        return this.a(e0) && this.b(e0);
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        e0.getBehaviorController().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(this.c(e0), true)));
        e0.getControllerMove().a(-this.c, 0.0F);
        e0.yaw = MathHelper.b(e0.yaw, e0.aC, 0.0F);
    }

    private boolean a(E e0) {
        return ((List) e0.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_MOBS).get()).contains(this.c(e0));
    }

    private boolean b(E e0) {
        return this.c(e0).a((Entity) e0, (double) this.b);
    }

    private EntityLiving c(E e0) {
        return (EntityLiving) e0.getBehaviorController().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}

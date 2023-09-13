package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorRemoveMemory<E extends EntityLiving> extends Behavior<E> {

    private final Predicate<E> b;
    private final MemoryModuleType<?> c;

    public BehaviorRemoveMemory(Predicate<E> predicate, MemoryModuleType<?> memorymoduletype) {
        super(ImmutableMap.of(memorymoduletype, MemoryStatus.VALUE_PRESENT));
        this.b = predicate;
        this.c = memorymoduletype;
    }

    @Override
    protected boolean a(WorldServer worldserver, E e0) {
        return this.b.test(e0);
    }

    @Override
    protected void a(WorldServer worldserver, E e0, long i) {
        e0.getBehaviorController().removeMemory(this.c);
    }
}

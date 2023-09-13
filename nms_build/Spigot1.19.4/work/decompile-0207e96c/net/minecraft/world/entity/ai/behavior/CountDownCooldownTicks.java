package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class CountDownCooldownTicks extends Behavior<EntityLiving> {

    private final MemoryModuleType<Integer> cooldownTicks;

    public CountDownCooldownTicks(MemoryModuleType<Integer> memorymoduletype) {
        super(ImmutableMap.of(memorymoduletype, MemoryStatus.VALUE_PRESENT));
        this.cooldownTicks = memorymoduletype;
    }

    private Optional<Integer> getCooldownTickMemory(EntityLiving entityliving) {
        return entityliving.getBrain().getMemory(this.cooldownTicks);
    }

    @Override
    protected boolean timedOut(long i) {
        return false;
    }

    @Override
    protected boolean canStillUse(WorldServer worldserver, EntityLiving entityliving, long i) {
        Optional<Integer> optional = this.getCooldownTickMemory(entityliving);

        return optional.isPresent() && (Integer) optional.get() > 0;
    }

    @Override
    protected void tick(WorldServer worldserver, EntityLiving entityliving, long i) {
        Optional<Integer> optional = this.getCooldownTickMemory(entityliving);

        entityliving.getBrain().setMemory(this.cooldownTicks, (Object) ((Integer) optional.get() - 1));
    }

    @Override
    protected void stop(WorldServer worldserver, EntityLiving entityliving, long i) {
        entityliving.getBrain().eraseMemory(this.cooldownTicks);
    }
}

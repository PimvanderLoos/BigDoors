package net.minecraft.world.entity.ai.behavior.declarative;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.K1;
import java.util.Optional;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public final class MemoryAccessor<F extends K1, Value> {

    private final BehaviorController<?> brain;
    private final MemoryModuleType<Value> memoryType;
    private final App<F, Value> value;

    public MemoryAccessor(BehaviorController<?> behaviorcontroller, MemoryModuleType<Value> memorymoduletype, App<F, Value> app) {
        this.brain = behaviorcontroller;
        this.memoryType = memorymoduletype;
        this.value = app;
    }

    public App<F, Value> value() {
        return this.value;
    }

    public void set(Value value) {
        this.brain.setMemory(this.memoryType, Optional.of(value));
    }

    public void setOrErase(Optional<Value> optional) {
        this.brain.setMemory(this.memoryType, optional);
    }

    public void setWithExpiry(Value value, long i) {
        this.brain.setMemoryWithExpiry(this.memoryType, value, i);
    }

    public void erase() {
        this.brain.eraseMemory(this.memoryType);
    }
}

package net.minecraft.world.entity.ai.behavior.declarative;

import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.Const.Mu;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Unit;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public interface MemoryCondition<F extends K1, Value> {

    MemoryModuleType<Value> memory();

    MemoryStatus condition();

    @Nullable
    MemoryAccessor<F, Value> createAccessor(BehaviorController<?> behaviorcontroller, Optional<Value> optional);

    public static record a<Value> (MemoryModuleType<Value> memory) implements MemoryCondition<Mu<Unit>, Value> {

        @Override
        public MemoryStatus condition() {
            return MemoryStatus.VALUE_ABSENT;
        }

        @Override
        public MemoryAccessor<Mu<Unit>, Value> createAccessor(BehaviorController<?> behaviorcontroller, Optional<Value> optional) {
            return optional.isPresent() ? null : new MemoryAccessor<>(behaviorcontroller, this.memory, Const.create(Unit.INSTANCE));
        }
    }

    public static record b<Value> (MemoryModuleType<Value> memory) implements MemoryCondition<com.mojang.datafixers.kinds.IdF.Mu, Value> {

        @Override
        public MemoryStatus condition() {
            return MemoryStatus.VALUE_PRESENT;
        }

        @Override
        public MemoryAccessor<com.mojang.datafixers.kinds.IdF.Mu, Value> createAccessor(BehaviorController<?> behaviorcontroller, Optional<Value> optional) {
            return optional.isEmpty() ? null : new MemoryAccessor<>(behaviorcontroller, this.memory, IdF.create(optional.get()));
        }
    }

    public static record c<Value> (MemoryModuleType<Value> memory) implements MemoryCondition<com.mojang.datafixers.kinds.OptionalBox.Mu, Value> {

        @Override
        public MemoryStatus condition() {
            return MemoryStatus.REGISTERED;
        }

        @Override
        public MemoryAccessor<com.mojang.datafixers.kinds.OptionalBox.Mu, Value> createAccessor(BehaviorController<?> behaviorcontroller, Optional<Value> optional) {
            return new MemoryAccessor<>(behaviorcontroller, this.memory, OptionalBox.create(optional));
        }
    }
}

package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.Unit;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;

public class TryToSniff extends Behavior<Warden> {

    private static final IntProvider SNIFF_COOLDOWN = UniformInt.of(100, 200);

    public TryToSniff() {
        super(ImmutableMap.of(MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_ATTACKABLE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.DISTURBANCE_LOCATION, MemoryStatus.VALUE_ABSENT));
    }

    protected void start(WorldServer worldserver, Warden warden, long i) {
        BehaviorController<Warden> behaviorcontroller = warden.getBrain();

        behaviorcontroller.setMemory(MemoryModuleType.IS_SNIFFING, (Object) Unit.INSTANCE);
        behaviorcontroller.setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, (long) TryToSniff.SNIFF_COOLDOWN.sample(worldserver.getRandom()));
        behaviorcontroller.eraseMemory(MemoryModuleType.WALK_TARGET);
        warden.setPose(EntityPose.SNIFFING);
    }
}

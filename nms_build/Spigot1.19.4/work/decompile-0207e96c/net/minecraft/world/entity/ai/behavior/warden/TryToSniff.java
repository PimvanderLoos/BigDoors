package net.minecraft.world.entity.ai.behavior.warden;

import net.minecraft.util.Unit;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class TryToSniff {

    private static final IntProvider SNIFF_COOLDOWN = UniformInt.of(100, 200);

    public TryToSniff() {}

    public static BehaviorControl<EntityLiving> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.IS_SNIFFING), behaviorbuilder_b.registered(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.absent(MemoryModuleType.SNIFF_COOLDOWN), behaviorbuilder_b.present(MemoryModuleType.NEAREST_ATTACKABLE), behaviorbuilder_b.absent(MemoryModuleType.DISTURBANCE_LOCATION)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3, memoryaccessor4) -> {
                return (worldserver, entityliving, i) -> {
                    memoryaccessor.set(Unit.INSTANCE);
                    memoryaccessor2.setWithExpiry(Unit.INSTANCE, (long) TryToSniff.SNIFF_COOLDOWN.sample(worldserver.getRandom()));
                    memoryaccessor1.erase();
                    entityliving.setPose(EntityPose.SNIFFING);
                    return true;
                };
            });
        });
    }
}

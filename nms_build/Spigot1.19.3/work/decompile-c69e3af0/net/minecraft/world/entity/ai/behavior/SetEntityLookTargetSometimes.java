package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

/** @deprecated */
@Deprecated
public class SetEntityLookTargetSometimes {

    public SetEntityLookTargetSometimes() {}

    public static BehaviorControl<EntityLiving> create(float f, UniformInt uniformint) {
        return create(f, uniformint, (entityliving) -> {
            return true;
        });
    }

    public static BehaviorControl<EntityLiving> create(EntityTypes<?> entitytypes, float f, UniformInt uniformint) {
        return create(f, uniformint, (entityliving) -> {
            return entitytypes.equals(entityliving.getType());
        });
    }

    private static BehaviorControl<EntityLiving> create(float f, UniformInt uniformint, Predicate<EntityLiving> predicate) {
        float f1 = f * f;
        SetEntityLookTargetSometimes.a setentitylooktargetsometimes_a = new SetEntityLookTargetSometimes.a(uniformint);

        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entityliving, i) -> {
                    Optional<EntityLiving> optional = ((NearestVisibleLivingEntities) behaviorbuilder_b.get(memoryaccessor1)).findClosest(predicate.and((entityliving1) -> {
                        return entityliving1.distanceToSqr((Entity) entityliving) <= (double) f1;
                    }));

                    if (optional.isEmpty()) {
                        return false;
                    } else if (!setentitylooktargetsometimes_a.tickDownAndCheck(worldserver.random)) {
                        return false;
                    } else {
                        memoryaccessor.set(new BehaviorPositionEntity((Entity) optional.get(), true));
                        return true;
                    }
                };
            });
        });
    }

    public static final class a {

        private final UniformInt interval;
        private int ticksUntilNextStart;

        public a(UniformInt uniformint) {
            if (uniformint.getMinValue() <= 1) {
                throw new IllegalArgumentException();
            } else {
                this.interval = uniformint;
            }
        }

        public boolean tickDownAndCheck(RandomSource randomsource) {
            if (this.ticksUntilNextStart == 0) {
                this.ticksUntilNextStart = this.interval.sample(randomsource) - 1;
                return false;
            } else {
                return --this.ticksUntilNextStart == 0;
            }
        }
    }
}

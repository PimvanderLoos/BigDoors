package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3D;

public class RandomLookAround extends Behavior<EntityInsentient> {

    private final IntProvider interval;
    private final float maxYaw;
    private final float minPitch;
    private final float pitchRange;

    public RandomLookAround(IntProvider intprovider, float f, float f1, float f2) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.GAZE_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT));
        if (f1 > f2) {
            throw new IllegalArgumentException("Minimum pitch is larger than maximum pitch! " + f1 + " > " + f2);
        } else {
            this.interval = intprovider;
            this.maxYaw = f;
            this.minPitch = f1;
            this.pitchRange = f2 - f1;
        }
    }

    protected void start(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        RandomSource randomsource = entityinsentient.getRandom();
        float f = MathHelper.clamp(randomsource.nextFloat() * this.pitchRange + this.minPitch, -90.0F, 90.0F);
        float f1 = MathHelper.wrapDegrees(entityinsentient.getYRot() + 2.0F * randomsource.nextFloat() * this.maxYaw - this.maxYaw);
        Vec3D vec3d = Vec3D.directionFromRotation(f, f1);

        entityinsentient.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorTarget(entityinsentient.getEyePosition().add(vec3d))));
        entityinsentient.getBrain().setMemory(MemoryModuleType.GAZE_COOLDOWN_TICKS, (Object) this.interval.sample(randomsource));
    }
}

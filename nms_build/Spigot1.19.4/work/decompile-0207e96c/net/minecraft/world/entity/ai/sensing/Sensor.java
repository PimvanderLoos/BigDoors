package net.minecraft.world.entity.ai.sensing;

import java.util.Set;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;

public abstract class Sensor<E extends EntityLiving> {

    private static final RandomSource RANDOM = RandomSource.createThreadSafe();
    private static final int DEFAULT_SCAN_RATE = 20;
    protected static final int TARGETING_RANGE = 16;
    private static final PathfinderTargetCondition TARGET_CONDITIONS = PathfinderTargetCondition.forNonCombat().range(16.0D);
    private static final PathfinderTargetCondition TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = PathfinderTargetCondition.forNonCombat().range(16.0D).ignoreInvisibilityTesting();
    private static final PathfinderTargetCondition ATTACK_TARGET_CONDITIONS = PathfinderTargetCondition.forCombat().range(16.0D);
    private static final PathfinderTargetCondition ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = PathfinderTargetCondition.forCombat().range(16.0D).ignoreInvisibilityTesting();
    private static final PathfinderTargetCondition ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT = PathfinderTargetCondition.forCombat().range(16.0D).ignoreLineOfSight();
    private static final PathfinderTargetCondition ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT = PathfinderTargetCondition.forCombat().range(16.0D).ignoreLineOfSight().ignoreInvisibilityTesting();
    private final int scanRate;
    private long timeToTick;

    public Sensor(int i) {
        this.scanRate = i;
        this.timeToTick = (long) Sensor.RANDOM.nextInt(i);
    }

    public Sensor() {
        this(20);
    }

    public final void tick(WorldServer worldserver, E e0) {
        if (--this.timeToTick <= 0L) {
            this.timeToTick = (long) this.scanRate;
            this.doTick(worldserver, e0);
        }

    }

    protected abstract void doTick(WorldServer worldserver, E e0);

    public abstract Set<MemoryModuleType<?>> requires();

    public static boolean isEntityTargetable(EntityLiving entityliving, EntityLiving entityliving1) {
        return entityliving.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, entityliving1) ? Sensor.TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.test(entityliving, entityliving1) : Sensor.TARGET_CONDITIONS.test(entityliving, entityliving1);
    }

    public static boolean isEntityAttackable(EntityLiving entityliving, EntityLiving entityliving1) {
        return entityliving.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, entityliving1) ? Sensor.ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.test(entityliving, entityliving1) : Sensor.ATTACK_TARGET_CONDITIONS.test(entityliving, entityliving1);
    }

    public static boolean isEntityAttackableIgnoringLineOfSight(EntityLiving entityliving, EntityLiving entityliving1) {
        return entityliving.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, entityliving1) ? Sensor.ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT.test(entityliving, entityliving1) : Sensor.ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT.test(entityliving, entityliving1);
    }
}

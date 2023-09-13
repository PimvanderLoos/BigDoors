package net.minecraft.world.entity.ai.sensing;

import java.util.Random;
import java.util.Set;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;

public abstract class Sensor<E extends EntityLiving> {

    private static final Random RANDOM = new Random();
    private static final int DEFAULT_SCAN_RATE = 20;
    protected static final int TARGETING_RANGE = 16;
    private static final PathfinderTargetCondition TARGET_CONDITIONS = PathfinderTargetCondition.b().a(16.0D);
    private static final PathfinderTargetCondition TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = PathfinderTargetCondition.b().a(16.0D).e();
    private static final PathfinderTargetCondition ATTACK_TARGET_CONDITIONS = PathfinderTargetCondition.a().a(16.0D);
    private static final PathfinderTargetCondition ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = PathfinderTargetCondition.a().a(16.0D).e();
    private static final PathfinderTargetCondition ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT = PathfinderTargetCondition.a().a(16.0D).d();
    private static final PathfinderTargetCondition ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT = PathfinderTargetCondition.a().a(16.0D).d().e();
    private final int scanRate;
    private long timeToTick;

    public Sensor(int i) {
        this.scanRate = i;
        this.timeToTick = (long) Sensor.RANDOM.nextInt(i);
    }

    public Sensor() {
        this(20);
    }

    public final void b(WorldServer worldserver, E e0) {
        if (--this.timeToTick <= 0L) {
            this.timeToTick = (long) this.scanRate;
            this.a(worldserver, e0);
        }

    }

    protected abstract void a(WorldServer worldserver, E e0);

    public abstract Set<MemoryModuleType<?>> a();

    protected static boolean b(EntityLiving entityliving, EntityLiving entityliving1) {
        return entityliving.getBehaviorController().b(MemoryModuleType.ATTACK_TARGET, (Object) entityliving1) ? Sensor.TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.a(entityliving, entityliving1) : Sensor.TARGET_CONDITIONS.a(entityliving, entityliving1);
    }

    public static boolean c(EntityLiving entityliving, EntityLiving entityliving1) {
        return entityliving.getBehaviorController().b(MemoryModuleType.ATTACK_TARGET, (Object) entityliving1) ? Sensor.ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.a(entityliving, entityliving1) : Sensor.ATTACK_TARGET_CONDITIONS.a(entityliving, entityliving1);
    }

    public static boolean d(EntityLiving entityliving, EntityLiving entityliving1) {
        return entityliving.getBehaviorController().b(MemoryModuleType.ATTACK_TARGET, (Object) entityliving1) ? Sensor.ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT.a(entityliving, entityliving1) : Sensor.ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT.a(entityliving, entityliving1);
    }
}

package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.Vec3D;

public class RamTarget<E extends EntityCreature> extends Behavior<E> {

    public static final int TIME_OUT_DURATION = 200;
    public static final float RAM_SPEED_FORCE_FACTOR = 1.65F;
    private final Function<E, UniformInt> getTimeBetweenRams;
    private final PathfinderTargetCondition ramTargeting;
    private final float speed;
    private final ToDoubleFunction<E> getKnockbackForce;
    private Vec3D ramDirection;
    private final Function<E, SoundEffect> getImpactSound;

    public RamTarget(Function<E, UniformInt> function, PathfinderTargetCondition pathfindertargetcondition, float f, ToDoubleFunction<E> todoublefunction, Function<E, SoundEffect> function1) {
        super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_PRESENT), 200);
        this.getTimeBetweenRams = function;
        this.ramTargeting = pathfindertargetcondition;
        this.speed = f;
        this.getKnockbackForce = todoublefunction;
        this.getImpactSound = function1;
        this.ramDirection = Vec3D.ZERO;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityCreature entitycreature) {
        return entitycreature.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    protected boolean canStillUse(WorldServer worldserver, EntityCreature entitycreature, long i) {
        return entitycreature.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    protected void start(WorldServer worldserver, EntityCreature entitycreature, long i) {
        BlockPosition blockposition = entitycreature.blockPosition();
        BehaviorController<?> behaviorcontroller = entitycreature.getBrain();
        Vec3D vec3d = (Vec3D) behaviorcontroller.getMemory(MemoryModuleType.RAM_TARGET).get();

        this.ramDirection = (new Vec3D((double) blockposition.getX() - vec3d.x(), 0.0D, (double) blockposition.getZ() - vec3d.z())).normalize();
        behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(vec3d, this.speed, 0)));
    }

    protected void tick(WorldServer worldserver, E e0, long i) {
        List<EntityLiving> list = worldserver.getNearbyEntities(EntityLiving.class, this.ramTargeting, e0, e0.getBoundingBox());
        BehaviorController<?> behaviorcontroller = e0.getBrain();

        if (!list.isEmpty()) {
            EntityLiving entityliving = (EntityLiving) list.get(0);

            entityliving.hurt(DamageSource.mobAttack(e0).setNoAggro(), (float) e0.getAttributeValue(GenericAttributes.ATTACK_DAMAGE));
            int j = e0.hasEffect(MobEffects.MOVEMENT_SPEED) ? e0.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1 : 0;
            int k = e0.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ? e0.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 0;
            float f = 0.25F * (float) (j - k);
            float f1 = MathHelper.clamp(e0.getSpeed() * 1.65F, 0.2F, 3.0F) + f;
            float f2 = entityliving.isDamageSourceBlocked(DamageSource.mobAttack(e0)) ? 0.5F : 1.0F;

            entityliving.knockback((double) (f2 * f1) * this.getKnockbackForce.applyAsDouble(e0), this.ramDirection.x(), this.ramDirection.z());
            this.finishRam(worldserver, e0);
            worldserver.playSound((EntityHuman) null, (Entity) e0, (SoundEffect) this.getImpactSound.apply(e0), SoundCategory.HOSTILE, 1.0F, 1.0F);
        } else {
            Optional<MemoryTarget> optional = behaviorcontroller.getMemory(MemoryModuleType.WALK_TARGET);
            Optional<Vec3D> optional1 = behaviorcontroller.getMemory(MemoryModuleType.RAM_TARGET);
            boolean flag = !optional.isPresent() || !optional1.isPresent() || ((MemoryTarget) optional.get()).getTarget().currentPosition().distanceTo((Vec3D) optional1.get()) < 0.25D;

            if (flag) {
                this.finishRam(worldserver, e0);
            }
        }

    }

    protected void finishRam(WorldServer worldserver, E e0) {
        worldserver.broadcastEntityEvent(e0, (byte) 59);
        e0.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, (Object) ((UniformInt) this.getTimeBetweenRams.apply(e0)).sample(worldserver.random));
        e0.getBrain().eraseMemory(MemoryModuleType.RAM_TARGET);
    }
}

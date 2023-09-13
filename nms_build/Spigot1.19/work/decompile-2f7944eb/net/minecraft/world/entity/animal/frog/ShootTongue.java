package net.minecraft.world.entity.animal.frog;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtil;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.phys.Vec3D;

public class ShootTongue extends Behavior<Frog> {

    public static final int TIME_OUT_DURATION = 100;
    public static final int CATCH_ANIMATION_DURATION = 6;
    public static final int TONGUE_ANIMATION_DURATION = 10;
    private static final float EATING_DISTANCE = 1.75F;
    private static final float EATING_MOVEMENT_FACTOR = 0.75F;
    public static final int UNREACHABLE_TONGUE_TARGETS_COOLDOWN_DURATION = 100;
    public static final int MAX_UNREACHBLE_TONGUE_TARGETS_IN_MEMORY = 5;
    private int eatAnimationTimer;
    private int calculatePathCounter;
    private final SoundEffect tongueSound;
    private final SoundEffect eatSound;
    private Vec3D itemSpawnPos;
    private ShootTongue.a state;

    public ShootTongue(SoundEffect soundeffect, SoundEffect soundeffect1) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT), 100);
        this.state = ShootTongue.a.DONE;
        this.tongueSound = soundeffect;
        this.eatSound = soundeffect1;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, Frog frog) {
        EntityLiving entityliving = (EntityLiving) frog.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        boolean flag = this.canPathfindToTarget(frog, entityliving);

        if (!flag) {
            frog.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            this.addUnreachableTargetToMemory(frog, entityliving);
        }

        return flag && frog.getPose() != EntityPose.CROAKING && Frog.canEat(entityliving);
    }

    protected boolean canStillUse(WorldServer worldserver, Frog frog, long i) {
        return frog.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.state != ShootTongue.a.DONE && !frog.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING);
    }

    protected void start(WorldServer worldserver, Frog frog, long i) {
        EntityLiving entityliving = (EntityLiving) frog.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();

        BehaviorUtil.lookAtEntity(frog, entityliving);
        frog.setTongueTarget(entityliving);
        frog.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(entityliving.position(), 2.0F, 0)));
        this.calculatePathCounter = 10;
        this.state = ShootTongue.a.MOVE_TO_TARGET;
    }

    protected void stop(WorldServer worldserver, Frog frog, long i) {
        frog.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        frog.eraseTongueTarget();
        frog.setPose(EntityPose.STANDING);
    }

    private void eatEntity(WorldServer worldserver, Frog frog) {
        worldserver.playSound((EntityHuman) null, (Entity) frog, this.eatSound, SoundCategory.NEUTRAL, 2.0F, 1.0F);
        Optional<Entity> optional = frog.getTongueTarget();

        if (optional.isPresent()) {
            Entity entity = (Entity) optional.get();

            if (entity.isAlive()) {
                frog.doHurtTarget(entity);
                if (!entity.isAlive()) {
                    entity.remove(Entity.RemovalReason.KILLED);
                }
            }
        }

    }

    protected void tick(WorldServer worldserver, Frog frog, long i) {
        EntityLiving entityliving = (EntityLiving) frog.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();

        frog.setTongueTarget(entityliving);
        switch (this.state) {
            case MOVE_TO_TARGET:
                if (entityliving.distanceTo(frog) < 1.75F) {
                    worldserver.playSound((EntityHuman) null, (Entity) frog, this.tongueSound, SoundCategory.NEUTRAL, 2.0F, 1.0F);
                    frog.setPose(EntityPose.USING_TONGUE);
                    entityliving.setDeltaMovement(entityliving.position().vectorTo(frog.position()).normalize().scale(0.75D));
                    this.itemSpawnPos = entityliving.position();
                    this.eatAnimationTimer = 0;
                    this.state = ShootTongue.a.CATCH_ANIMATION;
                } else if (this.calculatePathCounter <= 0) {
                    frog.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(entityliving.position(), 2.0F, 0)));
                    this.calculatePathCounter = 10;
                } else {
                    --this.calculatePathCounter;
                }
                break;
            case CATCH_ANIMATION:
                if (this.eatAnimationTimer++ >= 6) {
                    this.state = ShootTongue.a.EAT_ANIMATION;
                    this.eatEntity(worldserver, frog);
                }
                break;
            case EAT_ANIMATION:
                if (this.eatAnimationTimer >= 10) {
                    this.state = ShootTongue.a.DONE;
                } else {
                    ++this.eatAnimationTimer;
                }
            case DONE:
        }

    }

    private boolean canPathfindToTarget(Frog frog, EntityLiving entityliving) {
        PathEntity pathentity = frog.getNavigation().createPath((Entity) entityliving, 0);

        return pathentity != null && pathentity.getDistToTarget() < 1.75F;
    }

    private void addUnreachableTargetToMemory(Frog frog, EntityLiving entityliving) {
        List<UUID> list = (List) frog.getBrain().getMemory(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS).orElseGet(ArrayList::new);
        boolean flag = !list.contains(entityliving.getUUID());

        if (list.size() == 5 && flag) {
            list.remove(0);
        }

        if (flag) {
            list.add(entityliving.getUUID());
        }

        frog.getBrain().setMemoryWithExpiry(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS, list, 100L);
    }

    private static enum a {

        MOVE_TO_TARGET, CATCH_ANIMATION, EAT_ANIMATION, DONE;

        private a() {}
    }
}

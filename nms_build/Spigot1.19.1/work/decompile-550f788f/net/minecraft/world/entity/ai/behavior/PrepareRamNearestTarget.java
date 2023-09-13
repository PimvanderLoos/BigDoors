package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathfinderNormal;
import net.minecraft.world.phys.Vec3D;

public class PrepareRamNearestTarget<E extends EntityCreature> extends Behavior<E> {

    public static final int TIME_OUT_DURATION = 160;
    private final ToIntFunction<E> getCooldownOnFail;
    private final int minRamDistance;
    private final int maxRamDistance;
    private final float walkSpeed;
    private final PathfinderTargetCondition ramTargeting;
    private final int ramPrepareTime;
    private final Function<E, SoundEffect> getPrepareRamSound;
    private Optional<Long> reachedRamPositionTimestamp = Optional.empty();
    private Optional<PrepareRamNearestTarget.a> ramCandidate = Optional.empty();

    public PrepareRamNearestTarget(ToIntFunction<E> tointfunction, int i, int j, float f, PathfinderTargetCondition pathfindertargetcondition, int k, Function<E, SoundEffect> function) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_ABSENT), 160);
        this.getCooldownOnFail = tointfunction;
        this.minRamDistance = i;
        this.maxRamDistance = j;
        this.walkSpeed = f;
        this.ramTargeting = pathfindertargetcondition;
        this.ramPrepareTime = k;
        this.getPrepareRamSound = function;
    }

    protected void start(WorldServer worldserver, EntityCreature entitycreature, long i) {
        BehaviorController<?> behaviorcontroller = entitycreature.getBrain();

        behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap((nearestvisiblelivingentities) -> {
            return nearestvisiblelivingentities.findClosest((entityliving) -> {
                return this.ramTargeting.test(entitycreature, entityliving);
            });
        }).ifPresent((entityliving) -> {
            this.chooseRamPosition(entitycreature, entityliving);
        });
    }

    protected void stop(WorldServer worldserver, E e0, long i) {
        BehaviorController<?> behaviorcontroller = e0.getBrain();

        if (!behaviorcontroller.hasMemoryValue(MemoryModuleType.RAM_TARGET)) {
            worldserver.broadcastEntityEvent(e0, (byte) 59);
            behaviorcontroller.setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, (Object) this.getCooldownOnFail.applyAsInt(e0));
        }

    }

    protected boolean canStillUse(WorldServer worldserver, EntityCreature entitycreature, long i) {
        return this.ramCandidate.isPresent() && ((PrepareRamNearestTarget.a) this.ramCandidate.get()).getTarget().isAlive();
    }

    protected void tick(WorldServer worldserver, E e0, long i) {
        if (this.ramCandidate.isPresent()) {
            e0.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(((PrepareRamNearestTarget.a) this.ramCandidate.get()).getStartPosition(), this.walkSpeed, 0)));
            e0.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(((PrepareRamNearestTarget.a) this.ramCandidate.get()).getTarget(), true)));
            boolean flag = !((PrepareRamNearestTarget.a) this.ramCandidate.get()).getTarget().blockPosition().equals(((PrepareRamNearestTarget.a) this.ramCandidate.get()).getTargetPosition());

            if (flag) {
                worldserver.broadcastEntityEvent(e0, (byte) 59);
                e0.getNavigation().stop();
                this.chooseRamPosition(e0, ((PrepareRamNearestTarget.a) this.ramCandidate.get()).target);
            } else {
                BlockPosition blockposition = e0.blockPosition();

                if (blockposition.equals(((PrepareRamNearestTarget.a) this.ramCandidate.get()).getStartPosition())) {
                    worldserver.broadcastEntityEvent(e0, (byte) 58);
                    if (!this.reachedRamPositionTimestamp.isPresent()) {
                        this.reachedRamPositionTimestamp = Optional.of(i);
                    }

                    if (i - (Long) this.reachedRamPositionTimestamp.get() >= (long) this.ramPrepareTime) {
                        e0.getBrain().setMemory(MemoryModuleType.RAM_TARGET, (Object) this.getEdgeOfBlock(blockposition, ((PrepareRamNearestTarget.a) this.ramCandidate.get()).getTargetPosition()));
                        worldserver.playSound((EntityHuman) null, (Entity) e0, (SoundEffect) this.getPrepareRamSound.apply(e0), SoundCategory.HOSTILE, 1.0F, e0.getVoicePitch());
                        this.ramCandidate = Optional.empty();
                    }
                }
            }

        }
    }

    private Vec3D getEdgeOfBlock(BlockPosition blockposition, BlockPosition blockposition1) {
        double d0 = 0.5D;
        double d1 = 0.5D * (double) MathHelper.sign((double) (blockposition1.getX() - blockposition.getX()));
        double d2 = 0.5D * (double) MathHelper.sign((double) (blockposition1.getZ() - blockposition.getZ()));

        return Vec3D.atBottomCenterOf(blockposition1).add(d1, 0.0D, d2);
    }

    private Optional<BlockPosition> calculateRammingStartPosition(EntityCreature entitycreature, EntityLiving entityliving) {
        BlockPosition blockposition = entityliving.blockPosition();

        if (!this.isWalkableBlock(entitycreature, blockposition)) {
            return Optional.empty();
        } else {
            List<BlockPosition> list = Lists.newArrayList();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                blockposition_mutableblockposition.set(blockposition);
                int i = 0;

                while (true) {
                    if (i < this.maxRamDistance) {
                        if (this.isWalkableBlock(entitycreature, blockposition_mutableblockposition.move(enumdirection))) {
                            ++i;
                            continue;
                        }

                        blockposition_mutableblockposition.move(enumdirection.getOpposite());
                    }

                    if (blockposition_mutableblockposition.distManhattan(blockposition) >= this.minRamDistance) {
                        list.add(blockposition_mutableblockposition.immutable());
                    }
                    break;
                }
            }

            NavigationAbstract navigationabstract = entitycreature.getNavigation();
            Stream stream = list.stream();
            BlockPosition blockposition1 = entitycreature.blockPosition();

            Objects.requireNonNull(blockposition1);
            return stream.sorted(Comparator.comparingDouble(blockposition1::distSqr)).filter((blockposition2) -> {
                PathEntity pathentity = navigationabstract.createPath(blockposition2, 0);

                return pathentity != null && pathentity.canReach();
            }).findFirst();
        }
    }

    private boolean isWalkableBlock(EntityCreature entitycreature, BlockPosition blockposition) {
        return entitycreature.getNavigation().isStableDestination(blockposition) && entitycreature.getPathfindingMalus(PathfinderNormal.getBlockPathTypeStatic(entitycreature.level, blockposition.mutable())) == 0.0F;
    }

    private void chooseRamPosition(EntityCreature entitycreature, EntityLiving entityliving) {
        this.reachedRamPositionTimestamp = Optional.empty();
        this.ramCandidate = this.calculateRammingStartPosition(entitycreature, entityliving).map((blockposition) -> {
            return new PrepareRamNearestTarget.a(blockposition, entityliving.blockPosition(), entityliving);
        });
    }

    public static class a {

        private final BlockPosition startPosition;
        private final BlockPosition targetPosition;
        final EntityLiving target;

        public a(BlockPosition blockposition, BlockPosition blockposition1, EntityLiving entityliving) {
            this.startPosition = blockposition;
            this.targetPosition = blockposition1;
            this.target = entityliving;
        }

        public BlockPosition getStartPosition() {
            return this.startPosition;
        }

        public BlockPosition getTargetPosition() {
            return this.targetPosition;
        }

        public EntityLiving getTarget() {
            return this.target;
        }
    }
}

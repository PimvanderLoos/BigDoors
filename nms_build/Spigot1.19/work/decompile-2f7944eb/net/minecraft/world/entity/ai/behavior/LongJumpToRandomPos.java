package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom2;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathfinderNormal;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class LongJumpToRandomPos<E extends EntityInsentient> extends Behavior<E> {

    protected static final int FIND_JUMP_TRIES = 20;
    private static final int PREPARE_JUMP_DURATION = 40;
    protected static final int MIN_PATHFIND_DISTANCE_TO_VALID_JUMP = 8;
    private static final int TIME_OUT_DURATION = 200;
    private static final List<Integer> ALLOWED_ANGLES = Lists.newArrayList(new Integer[]{65, 70, 75, 80});
    private final UniformInt timeBetweenLongJumps;
    protected final int maxLongJumpHeight;
    protected final int maxLongJumpWidth;
    protected final float maxJumpVelocity;
    protected List<LongJumpToRandomPos.a> jumpCandidates;
    protected Optional<Vec3D> initialPosition;
    @Nullable
    protected Vec3D chosenJump;
    protected int findJumpTries;
    protected long prepareJumpStart;
    private Function<E, SoundEffect> getJumpSound;
    private final Predicate<IBlockData> acceptableLandingSpot;

    public LongJumpToRandomPos(UniformInt uniformint, int i, int j, float f, Function<E, SoundEffect> function) {
        this(uniformint, i, j, f, function, (iblockdata) -> {
            return false;
        });
    }

    public LongJumpToRandomPos(UniformInt uniformint, int i, int j, float f, Function<E, SoundEffect> function, Predicate<IBlockData> predicate) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT), 200);
        this.jumpCandidates = Lists.newArrayList();
        this.initialPosition = Optional.empty();
        this.timeBetweenLongJumps = uniformint;
        this.maxLongJumpHeight = i;
        this.maxLongJumpWidth = j;
        this.maxJumpVelocity = f;
        this.getJumpSound = function;
        this.acceptableLandingSpot = predicate;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityInsentient entityinsentient) {
        boolean flag = entityinsentient.isOnGround() && !entityinsentient.isInWater() && !entityinsentient.isInLava() && !worldserver.getBlockState(entityinsentient.blockPosition()).is(Blocks.HONEY_BLOCK);

        if (!flag) {
            entityinsentient.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, (Object) (this.timeBetweenLongJumps.sample(worldserver.random) / 2));
        }

        return flag;
    }

    protected boolean canStillUse(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        boolean flag = this.initialPosition.isPresent() && ((Vec3D) this.initialPosition.get()).equals(entityinsentient.position()) && this.findJumpTries > 0 && !entityinsentient.isInWaterOrBubble() && (this.chosenJump != null || !this.jumpCandidates.isEmpty());

        if (!flag && entityinsentient.getBrain().getMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isEmpty()) {
            entityinsentient.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, (Object) (this.timeBetweenLongJumps.sample(worldserver.random) / 2));
            entityinsentient.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        }

        return flag;
    }

    protected void start(WorldServer worldserver, E e0, long i) {
        this.chosenJump = null;
        this.findJumpTries = 20;
        this.initialPosition = Optional.of(e0.position());
        BlockPosition blockposition = e0.blockPosition();
        int j = blockposition.getX();
        int k = blockposition.getY();
        int l = blockposition.getZ();

        this.jumpCandidates = (List) BlockPosition.betweenClosedStream(j - this.maxLongJumpWidth, k - this.maxLongJumpHeight, l - this.maxLongJumpWidth, j + this.maxLongJumpWidth, k + this.maxLongJumpHeight, l + this.maxLongJumpWidth).filter((blockposition1) -> {
            return !blockposition1.equals(blockposition);
        }).map((blockposition1) -> {
            return new LongJumpToRandomPos.a(blockposition1.immutable(), MathHelper.ceil(blockposition.distSqr(blockposition1)));
        }).collect(Collectors.toCollection(Lists::newArrayList));
    }

    protected void tick(WorldServer worldserver, E e0, long i) {
        if (this.chosenJump != null) {
            if (i - this.prepareJumpStart >= 40L) {
                e0.setYRot(e0.yBodyRot);
                e0.setDiscardFriction(true);
                double d0 = this.chosenJump.length();
                double d1 = d0 + e0.getJumpBoostPower();

                e0.setDeltaMovement(this.chosenJump.scale(d1 / d0));
                e0.getBrain().setMemory(MemoryModuleType.LONG_JUMP_MID_JUMP, (Object) true);
                worldserver.playSound((EntityHuman) null, (Entity) e0, (SoundEffect) this.getJumpSound.apply(e0), SoundCategory.NEUTRAL, 1.0F, 1.0F);
            }
        } else {
            --this.findJumpTries;
            this.pickCandidate(worldserver, e0, i);
        }

    }

    protected void pickCandidate(WorldServer worldserver, E e0, long i) {
        while (true) {
            if (!this.jumpCandidates.isEmpty()) {
                Optional<LongJumpToRandomPos.a> optional = this.getJumpCandidate(worldserver);

                if (optional.isEmpty()) {
                    continue;
                }

                LongJumpToRandomPos.a longjumptorandompos_a = (LongJumpToRandomPos.a) optional.get();
                BlockPosition blockposition = longjumptorandompos_a.getJumpTarget();

                if (!this.isAcceptableLandingPosition(worldserver, e0, blockposition)) {
                    continue;
                }

                Vec3D vec3d = Vec3D.atCenterOf(blockposition);
                Vec3D vec3d1 = this.calculateOptimalJumpVector(e0, vec3d);

                if (vec3d1 == null) {
                    continue;
                }

                e0.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorTarget(blockposition)));
                NavigationAbstract navigationabstract = e0.getNavigation();
                PathEntity pathentity = navigationabstract.createPath(blockposition, 0, 8);

                if (pathentity != null && pathentity.canReach()) {
                    continue;
                }

                this.chosenJump = vec3d1;
                this.prepareJumpStart = i;
                return;
            }

            return;
        }
    }

    protected Optional<LongJumpToRandomPos.a> getJumpCandidate(WorldServer worldserver) {
        Optional<LongJumpToRandomPos.a> optional = WeightedRandom2.getRandomItem(worldserver.random, this.jumpCandidates);
        List list = this.jumpCandidates;

        Objects.requireNonNull(this.jumpCandidates);
        optional.ifPresent(list::remove);
        return optional;
    }

    protected boolean isAcceptableLandingPosition(WorldServer worldserver, E e0, BlockPosition blockposition) {
        BlockPosition blockposition1 = e0.blockPosition();
        int i = blockposition1.getX();
        int j = blockposition1.getZ();

        return i == blockposition.getX() && j == blockposition.getZ() ? false : (!e0.getNavigation().isStableDestination(blockposition) && !this.acceptableLandingSpot.test(worldserver.getBlockState(blockposition.below())) ? false : e0.getPathfindingMalus(PathfinderNormal.getBlockPathTypeStatic(e0.level, blockposition.mutable())) == 0.0F);
    }

    @Nullable
    protected Vec3D calculateOptimalJumpVector(EntityInsentient entityinsentient, Vec3D vec3d) {
        List<Integer> list = Lists.newArrayList(LongJumpToRandomPos.ALLOWED_ANGLES);

        Collections.shuffle(list);
        Iterator iterator = list.iterator();

        Vec3D vec3d1;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            int i = (Integer) iterator.next();

            vec3d1 = this.calculateJumpVectorForAngle(entityinsentient, vec3d, i);
        } while (vec3d1 == null);

        return vec3d1;
    }

    @Nullable
    private Vec3D calculateJumpVectorForAngle(EntityInsentient entityinsentient, Vec3D vec3d, int i) {
        Vec3D vec3d1 = entityinsentient.position();
        Vec3D vec3d2 = (new Vec3D(vec3d.x - vec3d1.x, 0.0D, vec3d.z - vec3d1.z)).normalize().scale(0.5D);

        vec3d = vec3d.subtract(vec3d2);
        Vec3D vec3d3 = vec3d.subtract(vec3d1);
        float f = (float) i * 3.1415927F / 180.0F;
        double d0 = Math.atan2(vec3d3.z, vec3d3.x);
        double d1 = vec3d3.subtract(0.0D, vec3d3.y, 0.0D).lengthSqr();
        double d2 = Math.sqrt(d1);
        double d3 = vec3d3.y;
        double d4 = Math.sin((double) (2.0F * f));
        double d5 = 0.08D;
        double d6 = Math.pow(Math.cos((double) f), 2.0D);
        double d7 = Math.sin((double) f);
        double d8 = Math.cos((double) f);
        double d9 = Math.sin(d0);
        double d10 = Math.cos(d0);
        double d11 = d1 * 0.08D / (d2 * d4 - 2.0D * d3 * d6);

        if (d11 < 0.0D) {
            return null;
        } else {
            double d12 = Math.sqrt(d11);

            if (d12 > (double) this.maxJumpVelocity) {
                return null;
            } else {
                double d13 = d12 * d8;
                double d14 = d12 * d7;
                int j = MathHelper.ceil(d2 / d13) * 2;
                double d15 = 0.0D;
                Vec3D vec3d4 = null;

                for (int k = 0; k < j - 1; ++k) {
                    d15 += d2 / (double) j;
                    double d16 = d7 / d8 * d15 - Math.pow(d15, 2.0D) * 0.08D / (2.0D * d11 * Math.pow(d8, 2.0D));
                    double d17 = d15 * d10;
                    double d18 = d15 * d9;
                    Vec3D vec3d5 = new Vec3D(vec3d1.x + d17, vec3d1.y + d16, vec3d1.z + d18);

                    if (vec3d4 != null && !this.isClearTransition(entityinsentient, vec3d4, vec3d5)) {
                        return null;
                    }

                    vec3d4 = vec3d5;
                }

                return (new Vec3D(d13 * d10, d14, d13 * d9)).scale(0.949999988079071D);
            }
        }
    }

    private boolean isClearTransition(EntityInsentient entityinsentient, Vec3D vec3d, Vec3D vec3d1) {
        EntitySize entitysize = entityinsentient.getDimensions(EntityPose.LONG_JUMPING);
        Vec3D vec3d2 = vec3d1.subtract(vec3d);
        double d0 = (double) Math.min(entitysize.width, entitysize.height);
        int i = MathHelper.ceil(vec3d2.length() / d0);
        Vec3D vec3d3 = vec3d2.normalize();
        Vec3D vec3d4 = vec3d;

        for (int j = 0; j < i; ++j) {
            vec3d4 = j == i - 1 ? vec3d1 : vec3d4.add(vec3d3.scale(d0 * 0.8999999761581421D));
            AxisAlignedBB axisalignedbb = entitysize.makeBoundingBox(vec3d4);

            if (!entityinsentient.level.noCollision(entityinsentient, axisalignedbb)) {
                return false;
            }
        }

        return true;
    }

    public static class a extends WeightedEntry.a {

        private final BlockPosition jumpTarget;

        public a(BlockPosition blockposition, int i) {
            super(i);
            this.jumpTarget = blockposition;
        }

        public BlockPosition getJumpTarget() {
            return this.jumpTarget;
        }
    }
}

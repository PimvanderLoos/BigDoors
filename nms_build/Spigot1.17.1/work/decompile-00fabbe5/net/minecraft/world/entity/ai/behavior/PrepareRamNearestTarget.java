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
import net.minecraft.core.BaseBlockPosition;
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
import net.minecraft.world.level.IBlockAccess;
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

    protected void a(WorldServer worldserver, EntityCreature entitycreature, long i) {
        BehaviorController<?> behaviorcontroller = entitycreature.getBehaviorController();

        behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap((list) -> {
            return list.stream().filter((entityliving) -> {
                return this.ramTargeting.a(entitycreature, entityliving);
            }).findFirst();
        }).ifPresent((entityliving) -> {
            this.b(entitycreature, entityliving);
        });
    }

    protected void c(WorldServer worldserver, E e0, long i) {
        BehaviorController<?> behaviorcontroller = e0.getBehaviorController();

        if (!behaviorcontroller.hasMemory(MemoryModuleType.RAM_TARGET)) {
            worldserver.broadcastEntityEffect(e0, (byte) 59);
            behaviorcontroller.setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, (Object) this.getCooldownOnFail.applyAsInt(e0));
        }

    }

    protected boolean b(WorldServer worldserver, EntityCreature entitycreature, long i) {
        return this.ramCandidate.isPresent() && ((PrepareRamNearestTarget.a) this.ramCandidate.get()).c().isAlive();
    }

    protected void d(WorldServer worldserver, E e0, long i) {
        if (this.ramCandidate.isPresent()) {
            e0.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(((PrepareRamNearestTarget.a) this.ramCandidate.get()).a(), this.walkSpeed, 0)));
            e0.getBehaviorController().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(((PrepareRamNearestTarget.a) this.ramCandidate.get()).c(), true)));
            boolean flag = !((PrepareRamNearestTarget.a) this.ramCandidate.get()).c().getChunkCoordinates().equals(((PrepareRamNearestTarget.a) this.ramCandidate.get()).b());

            if (flag) {
                worldserver.broadcastEntityEffect(e0, (byte) 59);
                e0.getNavigation().o();
                this.b(e0, ((PrepareRamNearestTarget.a) this.ramCandidate.get()).target);
            } else {
                BlockPosition blockposition = e0.getChunkCoordinates();

                if (blockposition.equals(((PrepareRamNearestTarget.a) this.ramCandidate.get()).a())) {
                    worldserver.broadcastEntityEffect(e0, (byte) 58);
                    if (!this.reachedRamPositionTimestamp.isPresent()) {
                        this.reachedRamPositionTimestamp = Optional.of(i);
                    }

                    if (i - (Long) this.reachedRamPositionTimestamp.get() >= (long) this.ramPrepareTime) {
                        e0.getBehaviorController().setMemory(MemoryModuleType.RAM_TARGET, (Object) this.a(blockposition, ((PrepareRamNearestTarget.a) this.ramCandidate.get()).b()));
                        worldserver.playSound((EntityHuman) null, (Entity) e0, (SoundEffect) this.getPrepareRamSound.apply(e0), SoundCategory.HOSTILE, 1.0F, e0.ep());
                        this.ramCandidate = Optional.empty();
                    }
                }
            }

        }
    }

    private Vec3D a(BlockPosition blockposition, BlockPosition blockposition1) {
        double d0 = 0.5D;
        double d1 = 0.5D * (double) MathHelper.k((double) (blockposition1.getX() - blockposition.getX()));
        double d2 = 0.5D * (double) MathHelper.k((double) (blockposition1.getZ() - blockposition.getZ()));

        return Vec3D.c((BaseBlockPosition) blockposition1).add(d1, 0.0D, d2);
    }

    private Optional<BlockPosition> a(EntityCreature entitycreature, EntityLiving entityliving) {
        BlockPosition blockposition = entityliving.getChunkCoordinates();

        if (!this.a(entitycreature, blockposition)) {
            return Optional.empty();
        } else {
            List<BlockPosition> list = Lists.newArrayList();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                blockposition_mutableblockposition.g(blockposition);
                int i = 0;

                while (true) {
                    if (i < this.maxRamDistance) {
                        if (this.a(entitycreature, (BlockPosition) blockposition_mutableblockposition.c(enumdirection))) {
                            ++i;
                            continue;
                        }

                        blockposition_mutableblockposition.c(enumdirection.opposite());
                    }

                    if (blockposition_mutableblockposition.k(blockposition) >= this.minRamDistance) {
                        list.add(blockposition_mutableblockposition.immutableCopy());
                    }
                    break;
                }
            }

            NavigationAbstract navigationabstract = entitycreature.getNavigation();
            Stream stream = list.stream();
            BlockPosition blockposition1 = entitycreature.getChunkCoordinates();

            Objects.requireNonNull(blockposition1);
            return stream.sorted(Comparator.comparingDouble(blockposition1::j)).filter((blockposition2) -> {
                PathEntity pathentity = navigationabstract.a(blockposition2, 0);

                return pathentity != null && pathentity.j();
            }).findFirst();
        }
    }

    private boolean a(EntityCreature entitycreature, BlockPosition blockposition) {
        return entitycreature.getNavigation().a(blockposition) && entitycreature.a(PathfinderNormal.a((IBlockAccess) entitycreature.level, blockposition.i())) == 0.0F;
    }

    private void b(EntityCreature entitycreature, EntityLiving entityliving) {
        this.reachedRamPositionTimestamp = Optional.empty();
        this.ramCandidate = this.a(entitycreature, entityliving).map((blockposition) -> {
            return new PrepareRamNearestTarget.a(blockposition, entityliving.getChunkCoordinates(), entityliving);
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

        public BlockPosition a() {
            return this.startPosition;
        }

        public BlockPosition b() {
            return this.targetPosition;
        }

        public EntityLiving c() {
            return this.target;
        }
    }
}

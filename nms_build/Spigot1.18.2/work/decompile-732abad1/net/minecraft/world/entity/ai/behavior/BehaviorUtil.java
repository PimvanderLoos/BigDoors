package net.minecraft.world.entity.ai.behavior;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemProjectileWeapon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.Vec3D;

public class BehaviorUtil {

    private BehaviorUtil() {}

    public static void lockGazeAndWalkToEachOther(EntityLiving entityliving, EntityLiving entityliving1, float f) {
        lookAtEachOther(entityliving, entityliving1);
        setWalkAndLookTargetMemoriesToEachOther(entityliving, entityliving1, f);
    }

    public static boolean entityIsVisible(BehaviorController<?> behaviorcontroller, EntityLiving entityliving) {
        Optional<NearestVisibleLivingEntities> optional = behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);

        return optional.isPresent() && ((NearestVisibleLivingEntities) optional.get()).contains(entityliving);
    }

    public static boolean targetIsValid(BehaviorController<?> behaviorcontroller, MemoryModuleType<? extends EntityLiving> memorymoduletype, EntityTypes<?> entitytypes) {
        return targetIsValid(behaviorcontroller, memorymoduletype, (entityliving) -> {
            return entityliving.getType() == entitytypes;
        });
    }

    private static boolean targetIsValid(BehaviorController<?> behaviorcontroller, MemoryModuleType<? extends EntityLiving> memorymoduletype, Predicate<EntityLiving> predicate) {
        return behaviorcontroller.getMemory(memorymoduletype).filter(predicate).filter(EntityLiving::isAlive).filter((entityliving) -> {
            return entityIsVisible(behaviorcontroller, entityliving);
        }).isPresent();
    }

    private static void lookAtEachOther(EntityLiving entityliving, EntityLiving entityliving1) {
        lookAtEntity(entityliving, entityliving1);
        lookAtEntity(entityliving1, entityliving);
    }

    public static void lookAtEntity(EntityLiving entityliving, EntityLiving entityliving1) {
        entityliving.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving1, true)));
    }

    private static void setWalkAndLookTargetMemoriesToEachOther(EntityLiving entityliving, EntityLiving entityliving1, float f) {
        boolean flag = true;

        setWalkAndLookTargetMemories(entityliving, (Entity) entityliving1, f, 2);
        setWalkAndLookTargetMemories(entityliving1, (Entity) entityliving, f, 2);
    }

    public static void setWalkAndLookTargetMemories(EntityLiving entityliving, Entity entity, float f, int i) {
        MemoryTarget memorytarget = new MemoryTarget(new BehaviorPositionEntity(entity, false), f, i);

        entityliving.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entity, true)));
        entityliving.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object) memorytarget);
    }

    public static void setWalkAndLookTargetMemories(EntityLiving entityliving, BlockPosition blockposition, float f, int i) {
        MemoryTarget memorytarget = new MemoryTarget(new BehaviorTarget(blockposition), f, i);

        entityliving.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorTarget(blockposition)));
        entityliving.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object) memorytarget);
    }

    public static void throwItem(EntityLiving entityliving, ItemStack itemstack, Vec3D vec3d) {
        double d0 = entityliving.getEyeY() - 0.30000001192092896D;
        EntityItem entityitem = new EntityItem(entityliving.level, entityliving.getX(), d0, entityliving.getZ(), itemstack);
        float f = 0.3F;
        Vec3D vec3d1 = vec3d.subtract(entityliving.position());

        vec3d1 = vec3d1.normalize().scale(0.30000001192092896D);
        entityitem.setDeltaMovement(vec3d1);
        entityitem.setDefaultPickUpDelay();
        entityliving.level.addFreshEntity(entityitem);
    }

    public static SectionPosition findSectionClosestToVillage(WorldServer worldserver, SectionPosition sectionposition, int i) {
        int j = worldserver.sectionsToVillage(sectionposition);
        Stream stream = SectionPosition.cube(sectionposition, i).filter((sectionposition1) -> {
            return worldserver.sectionsToVillage(sectionposition1) < j;
        });

        Objects.requireNonNull(worldserver);
        return (SectionPosition) stream.min(Comparator.comparingInt(worldserver::sectionsToVillage)).orElse(sectionposition);
    }

    public static boolean isWithinAttackRange(EntityInsentient entityinsentient, EntityLiving entityliving, int i) {
        Item item = entityinsentient.getMainHandItem().getItem();

        if (item instanceof ItemProjectileWeapon) {
            ItemProjectileWeapon itemprojectileweapon = (ItemProjectileWeapon) item;

            if (entityinsentient.canFireProjectileWeapon((ItemProjectileWeapon) item)) {
                int j = itemprojectileweapon.getDefaultProjectileRange() - i;

                return entityinsentient.closerThan(entityliving, (double) j);
            }
        }

        return isWithinMeleeAttackRange(entityinsentient, entityliving);
    }

    public static boolean isWithinMeleeAttackRange(EntityInsentient entityinsentient, EntityLiving entityliving) {
        double d0 = entityinsentient.distanceToSqr(entityliving.getX(), entityliving.getY(), entityliving.getZ());

        return d0 <= entityinsentient.getMeleeAttackRangeSqr(entityliving);
    }

    public static boolean isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(EntityLiving entityliving, EntityLiving entityliving1, double d0) {
        Optional<EntityLiving> optional = entityliving.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);

        if (optional.isEmpty()) {
            return false;
        } else {
            double d1 = entityliving.distanceToSqr(((EntityLiving) optional.get()).position());
            double d2 = entityliving.distanceToSqr(entityliving1.position());

            return d2 > d1 + d0 * d0;
        }
    }

    public static boolean canSee(EntityLiving entityliving, EntityLiving entityliving1) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();

        return !behaviorcontroller.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES) ? false : ((NearestVisibleLivingEntities) behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).contains(entityliving1);
    }

    public static EntityLiving getNearestTarget(EntityLiving entityliving, Optional<EntityLiving> optional, EntityLiving entityliving1) {
        return optional.isEmpty() ? entityliving1 : getTargetNearestMe(entityliving, (EntityLiving) optional.get(), entityliving1);
    }

    public static EntityLiving getTargetNearestMe(EntityLiving entityliving, EntityLiving entityliving1, EntityLiving entityliving2) {
        Vec3D vec3d = entityliving1.position();
        Vec3D vec3d1 = entityliving2.position();

        return entityliving.distanceToSqr(vec3d) < entityliving.distanceToSqr(vec3d1) ? entityliving1 : entityliving2;
    }

    public static Optional<EntityLiving> getLivingEntityFromUUIDMemory(EntityLiving entityliving, MemoryModuleType<UUID> memorymoduletype) {
        Optional<UUID> optional = entityliving.getBrain().getMemory(memorymoduletype);

        return optional.map((uuid) -> {
            return ((WorldServer) entityliving.level).getEntity(uuid);
        }).map((entity) -> {
            EntityLiving entityliving1;

            if (entity instanceof EntityLiving) {
                EntityLiving entityliving2 = (EntityLiving) entity;

                entityliving1 = entityliving2;
            } else {
                entityliving1 = null;
            }

            return entityliving1;
        });
    }

    public static Stream<EntityVillager> getNearbyVillagersWithCondition(EntityVillager entityvillager, Predicate<EntityVillager> predicate) {
        return (Stream) entityvillager.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).map((list) -> {
            return list.stream().filter((entityliving) -> {
                return entityliving instanceof EntityVillager && entityliving != entityvillager;
            }).map((entityliving) -> {
                return (EntityVillager) entityliving;
            }).filter(EntityLiving::isAlive).filter(predicate);
        }).orElseGet(Stream::empty);
    }

    @Nullable
    public static Vec3D getRandomSwimmablePos(EntityCreature entitycreature, int i, int j) {
        Vec3D vec3d = DefaultRandomPos.getPos(entitycreature, i, j);

        for (int k = 0; vec3d != null && !entitycreature.level.getBlockState(new BlockPosition(vec3d)).isPathfindable(entitycreature.level, new BlockPosition(vec3d), PathMode.WATER) && k++ < 10; vec3d = DefaultRandomPos.getPos(entitycreature, i, j)) {
            ;
        }

        return vec3d;
    }
}

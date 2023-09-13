package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.TimeRange;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.BehaviorAttack;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetForget;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetSet;
import net.minecraft.world.entity.ai.behavior.BehaviorCelebrateDeath;
import net.minecraft.world.entity.ai.behavior.BehaviorCelebrateLocation;
import net.minecraft.world.entity.ai.behavior.BehaviorCrossbowAttack;
import net.minecraft.world.entity.ai.behavior.BehaviorExpirableMemory;
import net.minecraft.world.entity.ai.behavior.BehaviorFindAdmirableItem;
import net.minecraft.world.entity.ai.behavior.BehaviorForgetAnger;
import net.minecraft.world.entity.ai.behavior.BehaviorGateSingle;
import net.minecraft.world.entity.ai.behavior.BehaviorInteract;
import net.minecraft.world.entity.ai.behavior.BehaviorInteractDoor;
import net.minecraft.world.entity.ai.behavior.BehaviorLook;
import net.minecraft.world.entity.ai.behavior.BehaviorLookInteract;
import net.minecraft.world.entity.ai.behavior.BehaviorLookTarget;
import net.minecraft.world.entity.ai.behavior.BehaviorLookWalk;
import net.minecraft.world.entity.ai.behavior.BehaviorNop;
import net.minecraft.world.entity.ai.behavior.BehaviorRemoveMemory;
import net.minecraft.world.entity.ai.behavior.BehaviorRetreat;
import net.minecraft.world.entity.ai.behavior.BehaviorRunIf;
import net.minecraft.world.entity.ai.behavior.BehaviorRunSometimes;
import net.minecraft.world.entity.ai.behavior.BehaviorStartRiding;
import net.minecraft.world.entity.ai.behavior.BehaviorStopRiding;
import net.minecraft.world.entity.ai.behavior.BehaviorStrollRandomUnconstrained;
import net.minecraft.world.entity.ai.behavior.BehaviorUtil;
import net.minecraft.world.entity.ai.behavior.BehaviorWalkAway;
import net.minecraft.world.entity.ai.behavior.BehaviorWalkAwayOutOfRange;
import net.minecraft.world.entity.ai.behavior.BehavorMove;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.EnumArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.Vec3D;

public class PiglinAI {

    public static final int REPELLENT_DETECTION_RANGE_HORIZONTAL = 8;
    public static final int REPELLENT_DETECTION_RANGE_VERTICAL = 4;
    public static final Item BARTERING_ITEM = Items.GOLD_INGOT;
    private static final int PLAYER_ANGER_RANGE = 16;
    private static final int ANGER_DURATION = 600;
    private static final int ADMIRE_DURATION = 120;
    private static final int MAX_DISTANCE_TO_WALK_TO_ITEM = 9;
    private static final int MAX_TIME_TO_WALK_TO_ITEM = 200;
    private static final int HOW_LONG_TIME_TO_DISABLE_ADMIRE_WALKING_IF_CANT_REACH_ITEM = 200;
    private static final int CELEBRATION_TIME = 300;
    private static final UniformInt TIME_BETWEEN_HUNTS = TimeRange.a(30, 120);
    private static final int BABY_FLEE_DURATION_AFTER_GETTING_HIT = 100;
    private static final int HIT_BY_PLAYER_MEMORY_TIMEOUT = 400;
    private static final int MAX_WALK_DISTANCE_TO_START_RIDING = 8;
    private static final UniformInt RIDE_START_INTERVAL = TimeRange.a(10, 40);
    private static final UniformInt RIDE_DURATION = TimeRange.a(10, 30);
    private static final UniformInt RETREAT_DURATION = TimeRange.a(5, 20);
    private static final int MELEE_ATTACK_COOLDOWN = 20;
    private static final int EAT_COOLDOWN = 200;
    private static final int DESIRED_DISTANCE_FROM_ENTITY_WHEN_AVOIDING = 12;
    private static final int MAX_LOOK_DIST = 8;
    private static final int MAX_LOOK_DIST_FOR_PLAYER_HOLDING_LOVED_ITEM = 14;
    private static final int INTERACTION_RANGE = 8;
    private static final int MIN_DESIRED_DIST_FROM_TARGET_WHEN_HOLDING_CROSSBOW = 5;
    private static final float SPEED_WHEN_STRAFING_BACK_FROM_TARGET = 0.75F;
    private static final int DESIRED_DISTANCE_FROM_ZOMBIFIED = 6;
    private static final UniformInt AVOID_ZOMBIFIED_DURATION = TimeRange.a(5, 7);
    private static final UniformInt BABY_AVOID_NEMESIS_DURATION = TimeRange.a(5, 7);
    private static final float PROBABILITY_OF_CELEBRATION_DANCE = 0.1F;
    private static final float SPEED_MULTIPLIER_WHEN_AVOIDING = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_RETREATING = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_MOUNTING = 0.8F;
    private static final float SPEED_MULTIPLIER_WHEN_GOING_TO_WANTED_ITEM = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_GOING_TO_CELEBRATE_LOCATION = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_DANCING = 0.6F;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.6F;

    public PiglinAI() {}

    protected static BehaviorController<?> a(EntityPiglin entitypiglin, BehaviorController<EntityPiglin> behaviorcontroller) {
        a(behaviorcontroller);
        b(behaviorcontroller);
        d(behaviorcontroller);
        b(entitypiglin, behaviorcontroller);
        c(behaviorcontroller);
        e(behaviorcontroller);
        f(behaviorcontroller);
        behaviorcontroller.a((Set) ImmutableSet.of(Activity.CORE));
        behaviorcontroller.b(Activity.IDLE);
        behaviorcontroller.e();
        return behaviorcontroller;
    }

    protected static void a(EntityPiglin entitypiglin) {
        int i = PiglinAI.TIME_BETWEEN_HUNTS.a(entitypiglin.level.random);

        entitypiglin.getBehaviorController().a(MemoryModuleType.HUNTED_RECENTLY, true, (long) i);
    }

    private static void a(BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.CORE, 0, ImmutableList.of(new BehaviorLook(45, 90), new BehavorMove(), new BehaviorInteractDoor(), d(), e(), new BehaviorStopAdmiring<>(), new BehaviorStartAdmiringItem<>(120), new BehaviorCelebrateDeath(300, PiglinAI::a), new BehaviorForgetAnger<>()));
    }

    private static void b(BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.IDLE, 10, ImmutableList.of(new BehaviorLookTarget(PiglinAI::b, 14.0F), new BehaviorAttackTargetSet<>(EntityPiglinAbstract::fw, PiglinAI::k), new BehaviorRunIf<>(EntityPiglin::n, new BehaviorHuntHoglin<>()), c(), f(), a(), b(), new BehaviorLookInteract(EntityTypes.PLAYER, 4)));
    }

    private static void b(EntityPiglin entitypiglin, BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.FIGHT, 10, ImmutableList.of(new BehaviorAttackTargetForget<>((entityliving) -> {
            return !b(entitypiglin, entityliving);
        }), new BehaviorRunIf<>(PiglinAI::c, new BehaviorRetreat<>(5, 0.75F)), new BehaviorWalkAwayOutOfRange(1.0F), new BehaviorAttack(20), new BehaviorCrossbowAttack<>(), new BehaviorRememberHuntedHoglin<>(), new BehaviorRemoveMemory<>(PiglinAI::j, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
    }

    private static void c(BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.CELEBRATE, 10, ImmutableList.of(c(), new BehaviorLookTarget(PiglinAI::b, 14.0F), new BehaviorAttackTargetSet<>(EntityPiglinAbstract::fw, PiglinAI::k), new BehaviorRunIf<>((entitypiglin) -> {
            return !entitypiglin.fD();
        }, new BehaviorCelebrateLocation<>(2, 1.0F)), new BehaviorRunIf<>(EntityPiglin::fD, new BehaviorCelebrateLocation<>(4, 0.6F)), new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorLookTarget(EntityTypes.PIGLIN, 8.0F), 1), Pair.of(new BehaviorStrollRandomUnconstrained(0.6F, 2, 1), 1), Pair.of(new BehaviorNop(10, 20), 1)))), MemoryModuleType.CELEBRATE_LOCATION);
    }

    private static void d(BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.ADMIRE_ITEM, 10, ImmutableList.of(new BehaviorFindAdmirableItem<>(PiglinAI::z, 1.0F, true, 9), new BehaviorStopAdmiringItem<>(9), new BehaviorAdmireTimeout<>(200, 200)), MemoryModuleType.ADMIRING_ITEM);
    }

    private static void e(BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.AVOID, 10, ImmutableList.of(BehaviorWalkAway.b(MemoryModuleType.AVOID_TARGET, 1.0F, 12, true), a(), b(), new BehaviorRemoveMemory<>(PiglinAI::o, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
    }

    private static void f(BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.RIDE, 10, ImmutableList.of(new BehaviorStartRiding<>(0.8F), new BehaviorLookTarget(PiglinAI::b, 8.0F), new BehaviorRunIf<>(Entity::isPassenger, a()), new BehaviorStopRiding<>(8, PiglinAI::a)), MemoryModuleType.RIDE_TARGET);
    }

    private static BehaviorGateSingle<EntityPiglin> a() {
        return new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorLookTarget(EntityTypes.PLAYER, 8.0F), 1), Pair.of(new BehaviorLookTarget(EntityTypes.PIGLIN, 8.0F), 1), Pair.of(new BehaviorLookTarget(8.0F), 1), Pair.of(new BehaviorNop(30, 60), 1)));
    }

    private static BehaviorGateSingle<EntityPiglin> b() {
        return new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorStrollRandomUnconstrained(0.6F), 2), Pair.of(BehaviorInteract.a(EntityTypes.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(new BehaviorRunIf<>(PiglinAI::f, new BehaviorLookWalk(0.6F, 3)), 2), Pair.of(new BehaviorNop(30, 60), 1)));
    }

    private static BehaviorWalkAway<BlockPosition> c() {
        return BehaviorWalkAway.a(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, false);
    }

    private static BehaviorExpirableMemory<EntityPiglin, EntityLiving> d() {
        return new BehaviorExpirableMemory<>(EntityPiglin::isBaby, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.AVOID_TARGET, PiglinAI.BABY_AVOID_NEMESIS_DURATION);
    }

    private static BehaviorExpirableMemory<EntityPiglin, EntityLiving> e() {
        return new BehaviorExpirableMemory<>(PiglinAI::j, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.AVOID_TARGET, PiglinAI.AVOID_ZOMBIFIED_DURATION);
    }

    protected static void b(EntityPiglin entitypiglin) {
        BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();
        Activity activity = (Activity) behaviorcontroller.f().orElse((Object) null);

        behaviorcontroller.a((List) ImmutableList.of(Activity.ADMIRE_ITEM, Activity.FIGHT, Activity.AVOID, Activity.CELEBRATE, Activity.RIDE, Activity.IDLE));
        Activity activity1 = (Activity) behaviorcontroller.f().orElse((Object) null);

        if (activity != activity1) {
            Optional optional = d(entitypiglin);

            Objects.requireNonNull(entitypiglin);
            optional.ifPresent(entitypiglin::a);
        }

        entitypiglin.setAggressive(behaviorcontroller.hasMemory(MemoryModuleType.ATTACK_TARGET));
        if (!behaviorcontroller.hasMemory(MemoryModuleType.RIDE_TARGET) && h(entitypiglin)) {
            entitypiglin.stopRiding();
        }

        if (!behaviorcontroller.hasMemory(MemoryModuleType.CELEBRATE_LOCATION)) {
            behaviorcontroller.removeMemory(MemoryModuleType.DANCING);
        }

        entitypiglin.w(behaviorcontroller.hasMemory(MemoryModuleType.DANCING));
    }

    private static boolean h(EntityPiglin entitypiglin) {
        if (!entitypiglin.isBaby()) {
            return false;
        } else {
            Entity entity = entitypiglin.getVehicle();

            return entity instanceof EntityPiglin && ((EntityPiglin) entity).isBaby() || entity instanceof EntityHoglin && ((EntityHoglin) entity).isBaby();
        }
    }

    protected static void a(EntityPiglin entitypiglin, EntityItem entityitem) {
        n(entitypiglin);
        ItemStack itemstack;

        if (entityitem.getItemStack().a(Items.GOLD_NUGGET)) {
            entitypiglin.receive(entityitem, entityitem.getItemStack().getCount());
            itemstack = entityitem.getItemStack();
            entityitem.die();
        } else {
            entitypiglin.receive(entityitem, 1);
            itemstack = a(entityitem);
        }

        if (a(itemstack)) {
            entitypiglin.getBehaviorController().removeMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
            c(entitypiglin, itemstack);
            d((EntityLiving) entitypiglin);
        } else if (c(itemstack) && !u(entitypiglin)) {
            s(entitypiglin);
        } else {
            boolean flag = entitypiglin.j(itemstack);

            if (!flag) {
                d(entitypiglin, itemstack);
            }
        }
    }

    private static void c(EntityPiglin entitypiglin, ItemStack itemstack) {
        if (y(entitypiglin)) {
            entitypiglin.b(entitypiglin.b(EnumHand.OFF_HAND));
        }

        entitypiglin.p(itemstack);
    }

    private static ItemStack a(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();
        ItemStack itemstack1 = itemstack.cloneAndSubtract(1);

        if (itemstack.isEmpty()) {
            entityitem.die();
        } else {
            entityitem.setItemStack(itemstack);
        }

        return itemstack1;
    }

    protected static void a(EntityPiglin entitypiglin, boolean flag) {
        ItemStack itemstack = entitypiglin.b(EnumHand.OFF_HAND);

        entitypiglin.a(EnumHand.OFF_HAND, ItemStack.EMPTY);
        boolean flag1;

        if (entitypiglin.fw()) {
            flag1 = b(itemstack);
            if (flag && flag1) {
                a(entitypiglin, i(entitypiglin));
            } else if (!flag1) {
                boolean flag2 = entitypiglin.j(itemstack);

                if (!flag2) {
                    d(entitypiglin, itemstack);
                }
            }
        } else {
            flag1 = entitypiglin.j(itemstack);
            if (!flag1) {
                ItemStack itemstack1 = entitypiglin.getItemInMainHand();

                if (a(itemstack1)) {
                    d(entitypiglin, itemstack1);
                } else {
                    a(entitypiglin, Collections.singletonList(itemstack1));
                }

                entitypiglin.o(itemstack);
            }
        }

    }

    protected static void c(EntityPiglin entitypiglin) {
        if (v(entitypiglin) && !entitypiglin.getItemInOffHand().isEmpty()) {
            entitypiglin.b(entitypiglin.getItemInOffHand());
            entitypiglin.a(EnumHand.OFF_HAND, ItemStack.EMPTY);
        }

    }

    private static void d(EntityPiglin entitypiglin, ItemStack itemstack) {
        ItemStack itemstack1 = entitypiglin.m(itemstack);

        b(entitypiglin, Collections.singletonList(itemstack1));
    }

    private static void a(EntityPiglin entitypiglin, List<ItemStack> list) {
        Optional<EntityHuman> optional = entitypiglin.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);

        if (optional.isPresent()) {
            a(entitypiglin, (EntityHuman) optional.get(), list);
        } else {
            b(entitypiglin, list);
        }

    }

    private static void b(EntityPiglin entitypiglin, List<ItemStack> list) {
        a(entitypiglin, list, t(entitypiglin));
    }

    private static void a(EntityPiglin entitypiglin, EntityHuman entityhuman, List<ItemStack> list) {
        a(entitypiglin, list, entityhuman.getPositionVector());
    }

    private static void a(EntityPiglin entitypiglin, List<ItemStack> list, Vec3D vec3d) {
        if (!list.isEmpty()) {
            entitypiglin.swingHand(EnumHand.OFF_HAND);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                ItemStack itemstack = (ItemStack) iterator.next();

                BehaviorUtil.a((EntityLiving) entitypiglin, itemstack, vec3d.add(0.0D, 1.0D, 0.0D));
            }
        }

    }

    private static List<ItemStack> i(EntityPiglin entitypiglin) {
        LootTable loottable = entitypiglin.level.getMinecraftServer().getLootTableRegistry().getLootTable(LootTables.PIGLIN_BARTERING);
        List<ItemStack> list = loottable.populateLoot((new LootTableInfo.Builder((WorldServer) entitypiglin.level)).set(LootContextParameters.THIS_ENTITY, entitypiglin).a(entitypiglin.level.random).build(LootContextParameterSets.PIGLIN_BARTER));

        return list;
    }

    private static boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        return entityliving1.getEntityType() != EntityTypes.HOGLIN ? false : (new Random(entityliving.level.getTime())).nextFloat() < 0.1F;
    }

    protected static boolean a(EntityPiglin entitypiglin, ItemStack itemstack) {
        if (entitypiglin.isBaby() && itemstack.a((Tag) TagsItem.IGNORED_BY_PIGLIN_BABIES)) {
            return false;
        } else if (itemstack.a((Tag) TagsItem.PIGLIN_REPELLENTS)) {
            return false;
        } else if (x(entitypiglin) && entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.ATTACK_TARGET)) {
            return false;
        } else if (b(itemstack)) {
            return z(entitypiglin);
        } else {
            boolean flag = entitypiglin.n(itemstack);

            return itemstack.a(Items.GOLD_NUGGET) ? flag : (c(itemstack) ? !u(entitypiglin) && flag : (!a(itemstack) ? entitypiglin.q(itemstack) : z(entitypiglin) && flag));
        }
    }

    protected static boolean a(ItemStack itemstack) {
        return itemstack.a((Tag) TagsItem.PIGLIN_LOVED);
    }

    private static boolean a(EntityPiglin entitypiglin, Entity entity) {
        if (!(entity instanceof EntityInsentient)) {
            return false;
        } else {
            EntityInsentient entityinsentient = (EntityInsentient) entity;

            return !entityinsentient.isBaby() || !entityinsentient.isAlive() || g((EntityLiving) entitypiglin) || g((EntityLiving) entityinsentient) || entityinsentient instanceof EntityPiglin && entityinsentient.getVehicle() == null;
        }
    }

    private static boolean b(EntityPiglin entitypiglin, EntityLiving entityliving) {
        return k(entitypiglin).filter((entityliving1) -> {
            return entityliving1 == entityliving;
        }).isPresent();
    }

    private static boolean j(EntityPiglin entitypiglin) {
        BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();

        if (behaviorcontroller.hasMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED)) {
            EntityLiving entityliving = (EntityLiving) behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED).get();

            return entitypiglin.a((Entity) entityliving, 6.0D);
        } else {
            return false;
        }
    }

    private static Optional<? extends EntityLiving> k(EntityPiglin entitypiglin) {
        BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();

        if (j(entitypiglin)) {
            return Optional.empty();
        } else {
            Optional<EntityLiving> optional = BehaviorUtil.a((EntityLiving) entitypiglin, MemoryModuleType.ANGRY_AT);

            if (optional.isPresent() && Sensor.d(entitypiglin, (EntityLiving) optional.get())) {
                return optional;
            } else {
                Optional optional1;

                if (behaviorcontroller.hasMemory(MemoryModuleType.UNIVERSAL_ANGER)) {
                    optional1 = behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
                    if (optional1.isPresent()) {
                        return optional1;
                    }
                }

                optional1 = behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
                if (optional1.isPresent()) {
                    return optional1;
                } else {
                    Optional<EntityHuman> optional2 = behaviorcontroller.getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);

                    return optional2.isPresent() && Sensor.c(entitypiglin, (EntityLiving) optional2.get()) ? optional2 : Optional.empty();
                }
            }
        }
    }

    public static void a(EntityHuman entityhuman, boolean flag) {
        List<EntityPiglin> list = entityhuman.level.a(EntityPiglin.class, entityhuman.getBoundingBox().g(16.0D));

        list.stream().filter(PiglinAI::d).filter((entitypiglin) -> {
            return !flag || BehaviorUtil.b((EntityLiving) entitypiglin, entityhuman);
        }).forEach((entitypiglin) -> {
            if (entitypiglin.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                d((EntityPiglinAbstract) entitypiglin, (EntityLiving) entityhuman);
            } else {
                c((EntityPiglinAbstract) entitypiglin, (EntityLiving) entityhuman);
            }

        });
    }

    public static EnumInteractionResult a(EntityPiglin entitypiglin, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (b(entitypiglin, itemstack)) {
            ItemStack itemstack1 = itemstack.cloneAndSubtract(1);

            c(entitypiglin, itemstack1);
            d((EntityLiving) entitypiglin);
            n(entitypiglin);
            return EnumInteractionResult.CONSUME;
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    protected static boolean b(EntityPiglin entitypiglin, ItemStack itemstack) {
        return !x(entitypiglin) && !v(entitypiglin) && entitypiglin.fw() && b(itemstack);
    }

    protected static void a(EntityPiglin entitypiglin, EntityLiving entityliving) {
        if (!(entityliving instanceof EntityPiglin)) {
            if (y(entitypiglin)) {
                a(entitypiglin, false);
            }

            BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();

            behaviorcontroller.removeMemory(MemoryModuleType.CELEBRATE_LOCATION);
            behaviorcontroller.removeMemory(MemoryModuleType.DANCING);
            behaviorcontroller.removeMemory(MemoryModuleType.ADMIRING_ITEM);
            if (entityliving instanceof EntityHuman) {
                behaviorcontroller.a(MemoryModuleType.ADMIRING_DISABLED, true, 400L);
            }

            g(entitypiglin).ifPresent((entityliving1) -> {
                if (entityliving1.getEntityType() != entityliving.getEntityType()) {
                    behaviorcontroller.removeMemory(MemoryModuleType.AVOID_TARGET);
                }

            });
            if (entitypiglin.isBaby()) {
                behaviorcontroller.a(MemoryModuleType.AVOID_TARGET, entityliving, 100L);
                if (Sensor.d(entitypiglin, entityliving)) {
                    b((EntityPiglinAbstract) entitypiglin, entityliving);
                }

            } else if (entityliving.getEntityType() == EntityTypes.HOGLIN && q(entitypiglin)) {
                e(entitypiglin, entityliving);
                c(entitypiglin, entityliving);
            } else {
                a((EntityPiglinAbstract) entitypiglin, entityliving);
            }
        }
    }

    protected static void a(EntityPiglinAbstract entitypiglinabstract, EntityLiving entityliving) {
        if (!entitypiglinabstract.getBehaviorController().c(Activity.AVOID)) {
            if (Sensor.d(entitypiglinabstract, entityliving)) {
                if (!BehaviorUtil.a(entitypiglinabstract, entityliving, 4.0D)) {
                    if (entityliving.getEntityType() == EntityTypes.PLAYER && entitypiglinabstract.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                        d(entitypiglinabstract, entityliving);
                        a(entitypiglinabstract);
                    } else {
                        c(entitypiglinabstract, entityliving);
                        b(entitypiglinabstract, entityliving);
                    }

                }
            }
        }
    }

    public static Optional<SoundEffect> d(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().f().map((activity) -> {
            return a(entitypiglin, activity);
        });
    }

    private static SoundEffect a(EntityPiglin entitypiglin, Activity activity) {
        return activity == Activity.FIGHT ? SoundEffects.PIGLIN_ANGRY : (entitypiglin.isConverting() ? SoundEffects.PIGLIN_RETREAT : (activity == Activity.AVOID && l(entitypiglin) ? SoundEffects.PIGLIN_RETREAT : (activity == Activity.ADMIRE_ITEM ? SoundEffects.PIGLIN_ADMIRING_ITEM : (activity == Activity.CELEBRATE ? SoundEffects.PIGLIN_CELEBRATE : (e((EntityLiving) entitypiglin) ? SoundEffects.PIGLIN_JEALOUS : (w(entitypiglin) ? SoundEffects.PIGLIN_RETREAT : SoundEffects.PIGLIN_AMBIENT))))));
    }

    private static boolean l(EntityPiglin entitypiglin) {
        BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();

        return !behaviorcontroller.hasMemory(MemoryModuleType.AVOID_TARGET) ? false : ((EntityLiving) behaviorcontroller.getMemory(MemoryModuleType.AVOID_TARGET).get()).a((Entity) entitypiglin, 12.0D);
    }

    protected static boolean e(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.HUNTED_RECENTLY) || m(entitypiglin).stream().anyMatch((entitypiglinabstract) -> {
            return entitypiglinabstract.getBehaviorController().hasMemory(MemoryModuleType.HUNTED_RECENTLY);
        });
    }

    private static List<EntityPiglinAbstract> m(EntityPiglin entitypiglin) {
        return (List) entitypiglin.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS).orElse(ImmutableList.of());
    }

    private static List<EntityPiglinAbstract> e(EntityPiglinAbstract entitypiglinabstract) {
        return (List) entitypiglinabstract.getBehaviorController().getMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS).orElse(ImmutableList.of());
    }

    public static boolean a(EntityLiving entityliving) {
        Iterable<ItemStack> iterable = entityliving.getArmorItems();
        Iterator iterator = iterable.iterator();

        Item item;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            ItemStack itemstack = (ItemStack) iterator.next();

            item = itemstack.getItem();
        } while (!(item instanceof ItemArmor) || ((ItemArmor) item).d() != EnumArmorMaterial.GOLD);

        return true;
    }

    private static void n(EntityPiglin entitypiglin) {
        entitypiglin.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
        entitypiglin.getNavigation().o();
    }

    private static BehaviorRunSometimes<EntityPiglin> f() {
        return new BehaviorRunSometimes<>(new BehaviorExpirableMemory<>(EntityPiglin::isBaby, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.RIDE_TARGET, PiglinAI.RIDE_DURATION), PiglinAI.RIDE_START_INTERVAL);
    }

    protected static void b(EntityPiglinAbstract entitypiglinabstract, EntityLiving entityliving) {
        e(entitypiglinabstract).forEach((entitypiglinabstract1) -> {
            if (entityliving.getEntityType() != EntityTypes.HOGLIN || entitypiglinabstract1.n() && ((EntityHoglin) entityliving).fy()) {
                e(entitypiglinabstract1, entityliving);
            }
        });
    }

    protected static void a(EntityPiglinAbstract entitypiglinabstract) {
        e(entitypiglinabstract).forEach((entitypiglinabstract1) -> {
            b(entitypiglinabstract1).ifPresent((entityhuman) -> {
                c(entitypiglinabstract1, (EntityLiving) entityhuman);
            });
        });
    }

    protected static void f(EntityPiglin entitypiglin) {
        m(entitypiglin).forEach(PiglinAI::c);
    }

    protected static void c(EntityPiglinAbstract entitypiglinabstract, EntityLiving entityliving) {
        if (Sensor.d(entitypiglinabstract, entityliving)) {
            entitypiglinabstract.getBehaviorController().removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            entitypiglinabstract.getBehaviorController().a(MemoryModuleType.ANGRY_AT, entityliving.getUniqueID(), 600L);
            if (entityliving.getEntityType() == EntityTypes.HOGLIN && entitypiglinabstract.n()) {
                c(entitypiglinabstract);
            }

            if (entityliving.getEntityType() == EntityTypes.PLAYER && entitypiglinabstract.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                entitypiglinabstract.getBehaviorController().a(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
            }

        }
    }

    private static void d(EntityPiglinAbstract entitypiglinabstract, EntityLiving entityliving) {
        Optional<EntityHuman> optional = b(entitypiglinabstract);

        if (optional.isPresent()) {
            c(entitypiglinabstract, (EntityLiving) optional.get());
        } else {
            c(entitypiglinabstract, entityliving);
        }

    }

    private static void e(EntityPiglinAbstract entitypiglinabstract, EntityLiving entityliving) {
        Optional<EntityLiving> optional = f(entitypiglinabstract);
        EntityLiving entityliving1 = BehaviorUtil.a((EntityLiving) entitypiglinabstract, optional, entityliving);

        if (!optional.isPresent() || optional.get() != entityliving1) {
            c(entitypiglinabstract, entityliving1);
        }
    }

    private static Optional<EntityLiving> f(EntityPiglinAbstract entitypiglinabstract) {
        return BehaviorUtil.a((EntityLiving) entitypiglinabstract, MemoryModuleType.ANGRY_AT);
    }

    public static Optional<EntityLiving> g(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.AVOID_TARGET) ? entitypiglin.getBehaviorController().getMemory(MemoryModuleType.AVOID_TARGET) : Optional.empty();
    }

    public static Optional<EntityHuman> b(EntityPiglinAbstract entitypiglinabstract) {
        return entitypiglinabstract.getBehaviorController().hasMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) ? entitypiglinabstract.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) : Optional.empty();
    }

    private static void c(EntityPiglin entitypiglin, EntityLiving entityliving) {
        m(entitypiglin).stream().filter((entitypiglinabstract) -> {
            return entitypiglinabstract instanceof EntityPiglin;
        }).forEach((entitypiglinabstract) -> {
            d((EntityPiglin) entitypiglinabstract, entityliving);
        });
    }

    private static void d(EntityPiglin entitypiglin, EntityLiving entityliving) {
        BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();
        EntityLiving entityliving1 = BehaviorUtil.a((EntityLiving) entitypiglin, behaviorcontroller.getMemory(MemoryModuleType.AVOID_TARGET), entityliving);

        entityliving1 = BehaviorUtil.a((EntityLiving) entitypiglin, behaviorcontroller.getMemory(MemoryModuleType.ATTACK_TARGET), entityliving1);
        e(entitypiglin, entityliving1);
    }

    private static boolean o(EntityPiglin entitypiglin) {
        BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();

        if (!behaviorcontroller.hasMemory(MemoryModuleType.AVOID_TARGET)) {
            return true;
        } else {
            EntityLiving entityliving = (EntityLiving) behaviorcontroller.getMemory(MemoryModuleType.AVOID_TARGET).get();
            EntityTypes<?> entitytypes = entityliving.getEntityType();

            return entitytypes == EntityTypes.HOGLIN ? p(entitypiglin) : (a(entitytypes) ? !behaviorcontroller.b(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, (Object) entityliving) : false);
        }
    }

    private static boolean p(EntityPiglin entitypiglin) {
        return !q(entitypiglin);
    }

    private static boolean q(EntityPiglin entitypiglin) {
        int i = (Integer) entitypiglin.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0) + 1;
        int j = (Integer) entitypiglin.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0);

        return j > i;
    }

    private static void e(EntityPiglin entitypiglin, EntityLiving entityliving) {
        entitypiglin.getBehaviorController().removeMemory(MemoryModuleType.ANGRY_AT);
        entitypiglin.getBehaviorController().removeMemory(MemoryModuleType.ATTACK_TARGET);
        entitypiglin.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
        entitypiglin.getBehaviorController().a(MemoryModuleType.AVOID_TARGET, entityliving, (long) PiglinAI.RETREAT_DURATION.a(entitypiglin.level.random));
        c((EntityPiglinAbstract) entitypiglin);
    }

    protected static void c(EntityPiglinAbstract entitypiglinabstract) {
        entitypiglinabstract.getBehaviorController().a(MemoryModuleType.HUNTED_RECENTLY, true, (long) PiglinAI.TIME_BETWEEN_HUNTS.a(entitypiglinabstract.level.random));
    }

    private static boolean r(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
    }

    private static void s(EntityPiglin entitypiglin) {
        entitypiglin.getBehaviorController().a(MemoryModuleType.ATE_RECENTLY, true, 200L);
    }

    private static Vec3D t(EntityPiglin entitypiglin) {
        Vec3D vec3d = LandRandomPos.a(entitypiglin, 4, 2);

        return vec3d == null ? entitypiglin.getPositionVector() : vec3d;
    }

    private static boolean u(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.ATE_RECENTLY);
    }

    protected static boolean d(EntityPiglinAbstract entitypiglinabstract) {
        return entitypiglinabstract.getBehaviorController().c(Activity.IDLE);
    }

    private static boolean c(EntityLiving entityliving) {
        return entityliving.a(Items.CROSSBOW);
    }

    private static void d(EntityLiving entityliving) {
        entityliving.getBehaviorController().a(MemoryModuleType.ADMIRING_ITEM, true, 120L);
    }

    private static boolean v(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.ADMIRING_ITEM);
    }

    private static boolean b(ItemStack itemstack) {
        return itemstack.a(PiglinAI.BARTERING_ITEM);
    }

    private static boolean c(ItemStack itemstack) {
        return itemstack.a((Tag) TagsItem.PIGLIN_FOOD);
    }

    private static boolean w(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.NEAREST_REPELLENT);
    }

    private static boolean e(EntityLiving entityliving) {
        return entityliving.getBehaviorController().hasMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
    }

    private static boolean f(EntityLiving entityliving) {
        return !e(entityliving);
    }

    public static boolean b(EntityLiving entityliving) {
        return entityliving.getEntityType() == EntityTypes.PLAYER && entityliving.b(PiglinAI::a);
    }

    private static boolean x(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.ADMIRING_DISABLED);
    }

    private static boolean g(EntityLiving entityliving) {
        return entityliving.getBehaviorController().hasMemory(MemoryModuleType.HURT_BY);
    }

    private static boolean y(EntityPiglin entitypiglin) {
        return !entitypiglin.getItemInOffHand().isEmpty();
    }

    private static boolean z(EntityPiglin entitypiglin) {
        return entitypiglin.getItemInOffHand().isEmpty() || !a(entitypiglin.getItemInOffHand());
    }

    public static boolean a(EntityTypes<?> entitytypes) {
        return entitytypes == EntityTypes.ZOMBIFIED_PIGLIN || entitytypes == EntityTypes.ZOGLIN;
    }
}

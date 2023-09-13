package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class PiglinAI {

    public static final Item a = Items.GOLD_INGOT;
    private static final IntRange b = TimeRange.a(30, 120);
    private static final IntRange c = TimeRange.a(10, 40);
    private static final IntRange d = TimeRange.a(10, 30);
    private static final IntRange e = TimeRange.a(5, 20);
    private static final IntRange f = TimeRange.a(5, 7);
    private static final IntRange g = TimeRange.a(5, 7);
    private static final Set<Item> h = ImmutableSet.of(Items.PORKCHOP, Items.COOKED_PORKCHOP);

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
        int i = PiglinAI.b.a(entitypiglin.world.random);

        entitypiglin.getBehaviorController().a(MemoryModuleType.HUNTED_RECENTLY, true, (long) i);
    }

    private static void a(BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.CORE, 0, ImmutableList.of(new BehaviorLook(45, 90), new BehavorMove(200), new BehaviorInteractDoor(), d(), e(), new BehaviorStopAdmiring<>(), new BehaviorStartAdmiringItem<>(120), new BehaviorCelebrateDeath(300, PiglinAI::a), new BehaviorForgetAnger<>()));
    }

    private static void b(BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.IDLE, 10, ImmutableList.of(new BehaviorLookTarget(PiglinAI::b, 14.0F), new BehaviorAttackTargetSet<>(EntityPiglin::eM, PiglinAI::o), new BehaviorRunIf<>(EntityPiglin::eN, new BehaviorHuntHoglin<>()), c(), f(), a(), b(), new BehaviorLookInteract(EntityTypes.PLAYER, 4)));
    }

    private static void b(EntityPiglin entitypiglin, BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.FLIGHT, 10, ImmutableList.of(new BehaviorAttackTargetForget<>((entityliving) -> {
            return !d(entitypiglin, entityliving);
        }), new BehaviorRunIf<>(PiglinAI::c, new BehaviorRetreat<>(5, 0.75F)), new BehaviorWalkAwayOutOfRange(1.0F), new BehaviorAttack(20), new BehaviorCrossbowAttack<>(), new BehaviorRememberHuntedHoglin<>(), new BehaviorRemoveMemory<>(PiglinAI::n, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
    }

    private static void c(BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.CELEBRATE, 10, ImmutableList.of(c(), new BehaviorLookTarget(PiglinAI::b, 14.0F), new BehaviorAttackTargetSet<>(EntityPiglin::eM, PiglinAI::o), new BehaviorRunIf<>((entitypiglin) -> {
            return !entitypiglin.eQ();
        }, new BehaviorCelebrateLocation<>(2, 1.0F)), new BehaviorRunIf<>(EntityPiglin::eQ, new BehaviorCelebrateLocation<>(4, 0.6F)), new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorLookTarget(EntityTypes.PIGLIN, 8.0F), 1), Pair.of(new BehaviorStrollRandomUnconstrained(0.6F, 2, 1), 1), Pair.of(new BehaviorNop(10, 20), 1)))), MemoryModuleType.CELEBRATE_LOCATION);
    }

    private static void d(BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.ADMIRE_ITEM, 10, ImmutableList.of(new BehaviorFindAdmirableItem<>(PiglinAI::F, 1.0F, true, 9), new BehaviorStopAdmiringItem<>(9)), MemoryModuleType.ADMIRING_ITEM);
    }

    private static void e(BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.AVOID, 10, ImmutableList.of(BehaviorWalkAway.b(MemoryModuleType.AVOID_TARGET, 1.0F, 12, true), a(), b(), new BehaviorRemoveMemory<>(PiglinAI::u, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
    }

    private static void f(BehaviorController<EntityPiglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.RIDE, 10, ImmutableList.of(new BehaviorStartRiding<>(0.8F), new BehaviorLookTarget(PiglinAI::b, 8.0F), new BehaviorRunIf<>(Entity::isPassenger, a()), new BehaviorStopRiding<>(8, PiglinAI::a)), MemoryModuleType.RIDE_TARGET);
    }

    private static BehaviorGateSingle<EntityPiglin> a() {
        return new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorLookTarget(EntityTypes.PLAYER, 8.0F), 1), Pair.of(new BehaviorLookTarget(EntityTypes.PIGLIN, 8.0F), 1), Pair.of(new BehaviorLookTarget(8.0F), 1), Pair.of(new BehaviorNop(30, 60), 1)));
    }

    private static BehaviorGateSingle<EntityPiglin> b() {
        return new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorStrollRandomUnconstrained(0.6F), 2), Pair.of(BehaviorInteract.a(EntityTypes.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(new BehaviorRunIf<>(PiglinAI::g, new BehaviorLookWalk(0.6F, 3)), 2), Pair.of(new BehaviorNop(30, 60), 1)));
    }

    private static BehaviorWalkAway<BlockPosition> c() {
        return BehaviorWalkAway.a(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, false);
    }

    private static BehaviorExpirableMemory<EntityPiglin, EntityLiving> d() {
        return new BehaviorExpirableMemory<>(EntityPiglin::isBaby, MemoryModuleType.NEAREST_VISIBLE_NEMSIS, MemoryModuleType.AVOID_TARGET, PiglinAI.g);
    }

    private static BehaviorExpirableMemory<EntityPiglin, EntityLiving> e() {
        return new BehaviorExpirableMemory<>(PiglinAI::n, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.AVOID_TARGET, PiglinAI.f);
    }

    protected static void b(EntityPiglin entitypiglin) {
        BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();
        Activity activity = (Activity) behaviorcontroller.f().orElse((Object) null);

        behaviorcontroller.a((List) ImmutableList.of(Activity.ADMIRE_ITEM, Activity.FLIGHT, Activity.AVOID, Activity.CELEBRATE, Activity.RIDE, Activity.IDLE));
        Activity activity1 = (Activity) behaviorcontroller.f().orElse((Object) null);

        if (activity != activity1) {
            d(entitypiglin).ifPresent(entitypiglin::a);
        }

        entitypiglin.setAggressive(behaviorcontroller.hasMemory(MemoryModuleType.ATTACK_TARGET));
        if (!behaviorcontroller.hasMemory(MemoryModuleType.RIDE_TARGET) && l(entitypiglin)) {
            entitypiglin.stopRiding();
        }

        if (!behaviorcontroller.hasMemory(MemoryModuleType.CELEBRATE_LOCATION)) {
            behaviorcontroller.removeMemory(MemoryModuleType.DANCING);
        }

        entitypiglin.u(behaviorcontroller.hasMemory(MemoryModuleType.DANCING));
    }

    private static boolean l(EntityPiglin entitypiglin) {
        if (!entitypiglin.isBaby()) {
            return false;
        } else {
            Entity entity = entitypiglin.getVehicle();

            return entity instanceof EntityPiglin && ((EntityPiglin) entity).isBaby() || entity instanceof EntityHoglin && ((EntityHoglin) entity).isBaby();
        }
    }

    protected static void a(EntityPiglin entitypiglin, EntityItem entityitem) {
        s(entitypiglin);
        ItemStack itemstack;

        if (entityitem.getItemStack().getItem() == Items.GOLD_NUGGET) {
            entitypiglin.receive(entityitem, entityitem.getItemStack().getCount());
            itemstack = entityitem.getItemStack();
            entityitem.die();
        } else {
            entitypiglin.receive(entityitem, 1);
            itemstack = a(entityitem);
        }

        Item item = itemstack.getItem();

        if (a(item)) {
            c(entitypiglin, itemstack);
            d((EntityLiving) entitypiglin);
        } else if (c(item) && !A(entitypiglin)) {
            y(entitypiglin);
        } else {
            boolean flag = entitypiglin.g(itemstack);

            if (!flag) {
                d(entitypiglin, itemstack);
            }
        }
    }

    private static void c(EntityPiglin entitypiglin, ItemStack itemstack) {
        if (E(entitypiglin)) {
            entitypiglin.a(entitypiglin.b(EnumHand.OFF_HAND));
        }

        entitypiglin.n(itemstack);
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

        entitypiglin.a(EnumHand.OFF_HAND, ItemStack.b);
        boolean flag1;

        if (entitypiglin.eM()) {
            flag1 = b(itemstack.getItem());
            if (flag && flag1) {
                a(entitypiglin, m(entitypiglin));
            } else if (!flag1) {
                boolean flag2 = entitypiglin.g(itemstack);

                if (!flag2) {
                    d(entitypiglin, itemstack);
                }
            }
        } else {
            flag1 = entitypiglin.g(itemstack);
            if (!flag1) {
                ItemStack itemstack1 = entitypiglin.getItemInMainHand();

                if (a(itemstack1.getItem())) {
                    d(entitypiglin, itemstack1);
                } else {
                    a(entitypiglin, Collections.singletonList(itemstack1));
                }

                entitypiglin.m(itemstack);
            }
        }

    }

    protected static void c(EntityPiglin entitypiglin) {
        if (B(entitypiglin) && !entitypiglin.getItemInOffHand().isEmpty()) {
            entitypiglin.a(entitypiglin.getItemInOffHand());
            entitypiglin.a(EnumHand.OFF_HAND, ItemStack.b);
        }

    }

    private static void d(EntityPiglin entitypiglin, ItemStack itemstack) {
        ItemStack itemstack1 = entitypiglin.k(itemstack);

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
        a(entitypiglin, list, z(entitypiglin));
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

    private static List<ItemStack> m(EntityPiglin entitypiglin) {
        LootTable loottable = entitypiglin.world.getMinecraftServer().getLootTableRegistry().getLootTable(LootTables.ay);
        List<ItemStack> list = loottable.populateLoot((new LootTableInfo.Builder((WorldServer) entitypiglin.world)).set(LootContextParameters.THIS_ENTITY, entitypiglin).a(entitypiglin.world.random).build(LootContextParameterSets.BARTER));

        return list;
    }

    private static boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        return entityliving1.getEntityType() != EntityTypes.HOGLIN ? false : (new Random(entityliving.world.getTime())).nextFloat() < 0.1F;
    }

    protected static boolean a(EntityPiglin entitypiglin, ItemStack itemstack) {
        Item item = itemstack.getItem();

        if (item.a((Tag) TagsItem.PIGLIN_REPELLENTS)) {
            return false;
        } else if (D(entitypiglin) && entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.ATTACK_TARGET)) {
            return false;
        } else if (b(item)) {
            return F(entitypiglin);
        } else {
            boolean flag = entitypiglin.l(itemstack);

            return item == Items.GOLD_NUGGET ? flag : (c(item) ? !A(entitypiglin) && flag : (!a(item) ? entitypiglin.o(itemstack) : F(entitypiglin) && flag));
        }
    }

    protected static boolean a(Item item) {
        return item.a((Tag) TagsItem.PIGLIN_LOVED);
    }

    private static boolean a(EntityPiglin entitypiglin, Entity entity) {
        if (!(entity instanceof EntityInsentient)) {
            return false;
        } else {
            EntityInsentient entityinsentient = (EntityInsentient) entity;

            return !entityinsentient.isBaby() || !entityinsentient.isAlive() || h((EntityLiving) entitypiglin) || h((EntityLiving) entityinsentient) || entityinsentient instanceof EntityPiglin && entityinsentient.getVehicle() == null;
        }
    }

    private static boolean d(EntityPiglin entitypiglin, EntityLiving entityliving) {
        return o(entitypiglin).filter((entityliving1) -> {
            return entityliving1 == entityliving;
        }).isPresent();
    }

    private static boolean n(EntityPiglin entitypiglin) {
        BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();

        if (behaviorcontroller.hasMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED)) {
            EntityLiving entityliving = (EntityLiving) behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED).get();

            return entitypiglin.a((Entity) entityliving, 6.0D);
        } else {
            return false;
        }
    }

    private static Optional<? extends EntityLiving> o(EntityPiglin entitypiglin) {
        BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();

        if (n(entitypiglin)) {
            return Optional.empty();
        } else {
            Optional<EntityLiving> optional = BehaviorUtil.a((EntityLiving) entitypiglin, MemoryModuleType.ANGRY_AT);

            if (optional.isPresent() && e((EntityLiving) optional.get())) {
                return optional;
            } else {
                Optional optional1;

                if (behaviorcontroller.hasMemory(MemoryModuleType.UNIVERSAL_ANGER)) {
                    optional1 = behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
                    if (optional1.isPresent()) {
                        return optional1;
                    }
                }

                optional1 = behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMSIS);
                if (optional1.isPresent()) {
                    return optional1;
                } else {
                    Optional<EntityHuman> optional2 = behaviorcontroller.getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);

                    return optional2.isPresent() && e((EntityLiving) optional2.get()) ? optional2 : Optional.empty();
                }
            }
        }
    }

    public static void a(EntityHuman entityhuman, boolean flag) {
        List<EntityPiglin> list = entityhuman.world.a(EntityPiglin.class, entityhuman.getBoundingBox().g(16.0D));

        list.stream().filter(PiglinAI::k).filter((entitypiglin) -> {
            return !flag || BehaviorUtil.c(entitypiglin, entityhuman);
        }).forEach((entitypiglin) -> {
            if (entitypiglin.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
                f(entitypiglin, entityhuman);
            } else {
                c(entitypiglin, (EntityLiving) entityhuman);
            }

        });
    }

    public static EnumInteractionResult a(EntityPiglin entitypiglin, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (b(entitypiglin, itemstack)) {
            ItemStack itemstack1 = itemstack.cloneAndSubtract(1);

            c(entitypiglin, itemstack1);
            d((EntityLiving) entitypiglin);
            s(entitypiglin);
            return EnumInteractionResult.CONSUME;
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    protected static boolean b(EntityPiglin entitypiglin, ItemStack itemstack) {
        return !D(entitypiglin) && !B(entitypiglin) && entitypiglin.eM() && b(itemstack.getItem());
    }

    protected static void a(EntityPiglin entitypiglin, EntityLiving entityliving) {
        if (!(entityliving instanceof EntityPiglin)) {
            if (E(entitypiglin)) {
                a(entitypiglin, false);
            }

            BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();

            behaviorcontroller.removeMemory(MemoryModuleType.CELEBRATE_LOCATION);
            behaviorcontroller.removeMemory(MemoryModuleType.DANCING);
            behaviorcontroller.removeMemory(MemoryModuleType.ADMIRING_ITEM);
            if (entityliving instanceof EntityHuman) {
                behaviorcontroller.a(MemoryModuleType.ADMIRING_DISABLED, true, 400L);
            }

            h(entitypiglin).ifPresent((entityliving1) -> {
                if (entityliving1.getEntityType() != entityliving.getEntityType()) {
                    behaviorcontroller.removeMemory(MemoryModuleType.AVOID_TARGET);
                }

            });
            if (entitypiglin.isBaby()) {
                behaviorcontroller.a(MemoryModuleType.AVOID_TARGET, entityliving, 100L);
                if (e(entityliving)) {
                    b(entitypiglin, entityliving);
                }

            } else if (entityliving.getEntityType() == EntityTypes.HOGLIN && w(entitypiglin)) {
                j(entitypiglin, entityliving);
                h(entitypiglin, entityliving);
            } else {
                e(entitypiglin, entityliving);
            }
        }
    }

    private static void e(EntityPiglin entitypiglin, EntityLiving entityliving) {
        if (!entitypiglin.getBehaviorController().c(Activity.AVOID)) {
            if (e(entityliving)) {
                if (!BehaviorUtil.a(entitypiglin, entityliving, 4.0D)) {
                    if (entityliving.getEntityType() == EntityTypes.PLAYER && entitypiglin.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
                        f(entitypiglin, entityliving);
                        f(entitypiglin);
                    } else {
                        c(entitypiglin, entityliving);
                        b(entitypiglin, entityliving);
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
        return activity == Activity.FLIGHT ? SoundEffects.ENTITY_PIGLIN_ANGRY : (entitypiglin.eO() ? SoundEffects.ENTITY_PIGLIN_RETREAT : (activity == Activity.AVOID && p(entitypiglin) ? SoundEffects.ENTITY_PIGLIN_RETREAT : (activity == Activity.ADMIRE_ITEM ? SoundEffects.ENTITY_PIGLIN_ADMIRING_ITEM : (activity == Activity.CELEBRATE ? SoundEffects.ENTITY_PIGLIN_CELEBRATE : (f((EntityLiving) entitypiglin) ? SoundEffects.ENTITY_PIGLIN_JEALOUS : (C(entitypiglin) ? SoundEffects.ENTITY_PIGLIN_RETREAT : SoundEffects.ENTITY_PIGLIN_AMBIENT))))));
    }

    private static boolean p(EntityPiglin entitypiglin) {
        BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();

        return !behaviorcontroller.hasMemory(MemoryModuleType.AVOID_TARGET) ? false : ((EntityLiving) behaviorcontroller.getMemory(MemoryModuleType.AVOID_TARGET).get()).a((Entity) entitypiglin, 12.0D);
    }

    protected static boolean e(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.HUNTED_RECENTLY) || q(entitypiglin).stream().anyMatch((entitypiglin1) -> {
            return entitypiglin1.getBehaviorController().hasMemory(MemoryModuleType.HUNTED_RECENTLY);
        });
    }

    private static List<EntityPiglin> q(EntityPiglin entitypiglin) {
        return (List) entitypiglin.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS).orElse(ImmutableList.of());
    }

    private static List<EntityPiglin> r(EntityPiglin entitypiglin) {
        return (List) entitypiglin.getBehaviorController().getMemory(MemoryModuleType.NEAREST_ADULT_PIGLINS).orElse(ImmutableList.of());
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
        } while (!(item instanceof ItemArmor) || ((ItemArmor) item).ad_() != EnumArmorMaterial.GOLD);

        return true;
    }

    private static void s(EntityPiglin entitypiglin) {
        entitypiglin.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
        entitypiglin.getNavigation().o();
    }

    private static BehaviorRunSometimes<EntityPiglin> f() {
        return new BehaviorRunSometimes<>(new BehaviorExpirableMemory<>(EntityPiglin::isBaby, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.RIDE_TARGET, PiglinAI.d), PiglinAI.c);
    }

    protected static void b(EntityPiglin entitypiglin, EntityLiving entityliving) {
        r(entitypiglin).forEach((entitypiglin1) -> {
            if (entityliving.getEntityType() != EntityTypes.HOGLIN || entitypiglin1.eN() && ((EntityHoglin) entityliving).eP()) {
                g(entitypiglin1, entityliving);
            }
        });
    }

    protected static void f(EntityPiglin entitypiglin) {
        r(entitypiglin).forEach((entitypiglin1) -> {
            i(entitypiglin1).ifPresent((entityhuman) -> {
                c(entitypiglin1, (EntityLiving) entityhuman);
            });
        });
    }

    protected static void g(EntityPiglin entitypiglin) {
        q(entitypiglin).forEach(PiglinAI::j);
    }

    protected static void c(EntityPiglin entitypiglin, EntityLiving entityliving) {
        if (e(entityliving)) {
            entitypiglin.getBehaviorController().removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            entitypiglin.getBehaviorController().a(MemoryModuleType.ANGRY_AT, entityliving.getUniqueID(), 600L);
            if (entityliving.getEntityType() == EntityTypes.HOGLIN) {
                j(entitypiglin);
            }

            if (entityliving.getEntityType() == EntityTypes.PLAYER && entitypiglin.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
                entitypiglin.getBehaviorController().a(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
            }

        }
    }

    private static void f(EntityPiglin entitypiglin, EntityLiving entityliving) {
        Optional<EntityHuman> optional = i(entitypiglin);

        if (optional.isPresent()) {
            c(entitypiglin, (EntityLiving) optional.get());
        } else {
            c(entitypiglin, entityliving);
        }

    }

    private static void g(EntityPiglin entitypiglin, EntityLiving entityliving) {
        Optional<EntityLiving> optional = t(entitypiglin);
        EntityLiving entityliving1 = BehaviorUtil.a((EntityLiving) entitypiglin, optional, entityliving);

        if (!optional.isPresent() || optional.get() != entityliving1) {
            c(entitypiglin, entityliving1);
        }
    }

    private static Optional<EntityLiving> t(EntityPiglin entitypiglin) {
        return BehaviorUtil.a((EntityLiving) entitypiglin, MemoryModuleType.ANGRY_AT);
    }

    public static Optional<EntityLiving> h(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.AVOID_TARGET) ? entitypiglin.getBehaviorController().getMemory(MemoryModuleType.AVOID_TARGET) : Optional.empty();
    }

    public static Optional<EntityHuman> i(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER) ? entitypiglin.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER) : Optional.empty();
    }

    private static void h(EntityPiglin entitypiglin, EntityLiving entityliving) {
        q(entitypiglin).forEach((entitypiglin1) -> {
            i(entitypiglin1, entityliving);
        });
    }

    private static void i(EntityPiglin entitypiglin, EntityLiving entityliving) {
        BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();
        EntityLiving entityliving1 = BehaviorUtil.a((EntityLiving) entitypiglin, behaviorcontroller.getMemory(MemoryModuleType.AVOID_TARGET), entityliving);

        entityliving1 = BehaviorUtil.a((EntityLiving) entitypiglin, behaviorcontroller.getMemory(MemoryModuleType.ATTACK_TARGET), entityliving1);
        j(entitypiglin, entityliving1);
    }

    private static boolean u(EntityPiglin entitypiglin) {
        BehaviorController<EntityPiglin> behaviorcontroller = entitypiglin.getBehaviorController();

        if (!behaviorcontroller.hasMemory(MemoryModuleType.AVOID_TARGET)) {
            return true;
        } else {
            EntityLiving entityliving = (EntityLiving) behaviorcontroller.getMemory(MemoryModuleType.AVOID_TARGET).get();
            EntityTypes<?> entitytypes = entityliving.getEntityType();

            return entitytypes == EntityTypes.HOGLIN ? v(entitypiglin) : (a(entitytypes) ? !behaviorcontroller.b(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, (Object) entityliving) : false);
        }
    }

    private static boolean v(EntityPiglin entitypiglin) {
        return !w(entitypiglin);
    }

    private static boolean w(EntityPiglin entitypiglin) {
        int i = (Integer) entitypiglin.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0) + 1;
        int j = (Integer) entitypiglin.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0);

        return j > i;
    }

    private static void j(EntityPiglin entitypiglin, EntityLiving entityliving) {
        entitypiglin.getBehaviorController().removeMemory(MemoryModuleType.ANGRY_AT);
        entitypiglin.getBehaviorController().removeMemory(MemoryModuleType.ATTACK_TARGET);
        entitypiglin.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
        entitypiglin.getBehaviorController().a(MemoryModuleType.AVOID_TARGET, entityliving, (long) PiglinAI.e.a(entitypiglin.world.random));
        j(entitypiglin);
    }

    protected static void j(EntityPiglin entitypiglin) {
        entitypiglin.getBehaviorController().a(MemoryModuleType.HUNTED_RECENTLY, true, (long) PiglinAI.b.a(entitypiglin.world.random));
    }

    private static void y(EntityPiglin entitypiglin) {
        entitypiglin.getBehaviorController().a(MemoryModuleType.ATE_RECENTLY, true, 200L);
    }

    private static Vec3D z(EntityPiglin entitypiglin) {
        Vec3D vec3d = RandomPositionGenerator.b(entitypiglin, 4, 2);

        return vec3d == null ? entitypiglin.getPositionVector() : vec3d;
    }

    private static boolean A(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.ATE_RECENTLY);
    }

    protected static boolean k(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().c(Activity.IDLE);
    }

    private static boolean c(EntityLiving entityliving) {
        return entityliving.a(Items.CROSSBOW);
    }

    private static void d(EntityLiving entityliving) {
        entityliving.getBehaviorController().a(MemoryModuleType.ADMIRING_ITEM, true, 120L);
    }

    private static boolean B(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.ADMIRING_ITEM);
    }

    private static boolean b(Item item) {
        return item == PiglinAI.a;
    }

    private static boolean c(Item item) {
        return PiglinAI.h.contains(item);
    }

    private static boolean e(EntityLiving entityliving) {
        return IEntitySelector.f.test(entityliving);
    }

    private static boolean C(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.NEAREST_REPELLENT);
    }

    private static boolean f(EntityLiving entityliving) {
        return entityliving.getBehaviorController().hasMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
    }

    private static boolean g(EntityLiving entityliving) {
        return !f(entityliving);
    }

    public static boolean b(EntityLiving entityliving) {
        return entityliving.getEntityType() == EntityTypes.PLAYER && entityliving.a(PiglinAI::a);
    }

    private static boolean D(EntityPiglin entitypiglin) {
        return entitypiglin.getBehaviorController().hasMemory(MemoryModuleType.ADMIRING_DISABLED);
    }

    private static boolean h(EntityLiving entityliving) {
        return entityliving.getBehaviorController().hasMemory(MemoryModuleType.HURT_BY);
    }

    private static boolean E(EntityPiglin entitypiglin) {
        return !entitypiglin.getItemInOffHand().isEmpty();
    }

    private static boolean F(EntityPiglin entitypiglin) {
        return entitypiglin.getItemInOffHand().isEmpty() || !a(entitypiglin.getItemInOffHand().getItem());
    }

    public static boolean a(EntityTypes entitytypes) {
        return entitytypes == EntityTypes.ZOMBIFIED_PIGLIN || entitytypes == EntityTypes.ZOGLIN;
    }
}

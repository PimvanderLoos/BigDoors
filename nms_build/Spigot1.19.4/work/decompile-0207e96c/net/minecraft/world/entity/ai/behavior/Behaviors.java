package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.raid.Raid;

public class Behaviors {

    private static final float STROLL_SPEED_MODIFIER = 0.4F;

    public Behaviors() {}

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super EntityVillager>>> getCorePackage(VillagerProfession villagerprofession, float f) {
        return ImmutableList.of(Pair.of(0, new BehaviorSwim(0.8F)), Pair.of(0, BehaviorInteractDoor.create()), Pair.of(0, new BehaviorLook(45, 90)), Pair.of(0, new BehaviorPanic()), Pair.of(0, BehaviorWake.create()), Pair.of(0, BehaviorBellAlert.create()), Pair.of(0, BehaviorRaid.create()), Pair.of(0, BehaviorPositionValidate.create(villagerprofession.heldJobSite(), MemoryModuleType.JOB_SITE)), Pair.of(0, BehaviorPositionValidate.create(villagerprofession.acquirableJobSite(), MemoryModuleType.POTENTIAL_JOB_SITE)), Pair.of(1, new BehavorMove()), Pair.of(2, BehaviorBetterJob.create()), Pair.of(3, new BehaviorInteractPlayer(f)), new Pair[]{Pair.of(5, BehaviorFindAdmirableItem.create(f, false, 4)), Pair.of(6, BehaviorFindPosition.create(villagerprofession.acquirableJobSite(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())), Pair.of(7, new BehaviorPotentialJobSite(f)), Pair.of(8, BehaviorLeaveJob.create(f)), Pair.of(10, BehaviorFindPosition.create((holder) -> {
                    return holder.is(PoiTypes.HOME);
                }, MemoryModuleType.HOME, false, Optional.of((byte) 14))), Pair.of(10, BehaviorFindPosition.create((holder) -> {
                    return holder.is(PoiTypes.MEETING);
                }, MemoryModuleType.MEETING_POINT, true, Optional.of((byte) 14))), Pair.of(10, BehaviorCareer.create()), Pair.of(10, BehaviorProfession.create())});
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super EntityVillager>>> getWorkPackage(VillagerProfession villagerprofession, float f) {
        Object object;

        if (villagerprofession == VillagerProfession.FARMER) {
            object = new BehaviorWorkComposter();
        } else {
            object = new BehaviorWork();
        }

        return ImmutableList.of(getMinimalLookBehavior(), Pair.of(5, new BehaviorGateSingle<>(ImmutableList.of(Pair.of(object, 7), Pair.of(BehaviorStrollPosition.create(MemoryModuleType.JOB_SITE, 0.4F, 4), 2), Pair.of(BehaviorStrollPlace.create(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5), Pair.of(BehaviorStrollPlaceList.create(MemoryModuleType.SECONDARY_JOB_SITE, f, 1, 6, MemoryModuleType.JOB_SITE), 5), Pair.of(new BehaviorFarm(), villagerprofession == VillagerProfession.FARMER ? 2 : 5), Pair.of(new BehaviorBonemeal(), villagerprofession == VillagerProfession.FARMER ? 4 : 7)))), Pair.of(10, new BehaviorTradePlayer(400, 1600)), Pair.of(10, BehaviorLookInteract.create(EntityTypes.PLAYER, 4)), Pair.of(2, BehaviorWalkAwayBlock.create(MemoryModuleType.JOB_SITE, f, 9, 100, 1200)), Pair.of(3, new BehaviorVillageHeroGift(100)), Pair.of(99, BehaviorSchedule.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super EntityVillager>>> getPlayPackage(float f) {
        return ImmutableList.of(Pair.of(0, new BehavorMove(80, 120)), getFullLookBehavior(), Pair.of(5, BehaviorPlay.create()), Pair.of(5, new BehaviorGateSingle<>(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(BehaviorInteract.of(EntityTypes.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 2), Pair.of(BehaviorInteract.of(EntityTypes.CAT, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 1), Pair.of(BehaviorStrollRandom.create(f), 1), Pair.of(BehaviorLookWalk.create(f, 2), 1), Pair.of(new BehaviorBedJump(f), 2), Pair.of(new BehaviorNop(20, 40), 2)))), Pair.of(99, BehaviorSchedule.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super EntityVillager>>> getRestPackage(VillagerProfession villagerprofession, float f) {
        return ImmutableList.of(Pair.of(2, BehaviorWalkAwayBlock.create(MemoryModuleType.HOME, f, 1, 150, 1200)), Pair.of(3, BehaviorPositionValidate.create((holder) -> {
            return holder.is(PoiTypes.HOME);
        }, MemoryModuleType.HOME)), Pair.of(3, new BehaviorSleep()), Pair.of(5, new BehaviorGateSingle<>(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(BehaviorWalkHome.create(f), 1), Pair.of(BehaviorStrollInside.create(f), 4), Pair.of(BehaviorNearestVillage.create(f, 4), 2), Pair.of(new BehaviorNop(20, 40), 2)))), getMinimalLookBehavior(), Pair.of(99, BehaviorSchedule.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super EntityVillager>>> getMeetPackage(VillagerProfession villagerprofession, float f) {
        return ImmutableList.of(Pair.of(2, TriggerGate.triggerOneShuffled(ImmutableList.of(Pair.of(BehaviorStrollPosition.create(MemoryModuleType.MEETING_POINT, 0.4F, 40), 2), Pair.of(BehaviorBell.create(), 2)))), Pair.of(10, new BehaviorTradePlayer(400, 1600)), Pair.of(10, BehaviorLookInteract.create(EntityTypes.PLAYER, 4)), Pair.of(2, BehaviorWalkAwayBlock.create(MemoryModuleType.MEETING_POINT, f, 6, 100, 200)), Pair.of(3, new BehaviorVillageHeroGift(100)), Pair.of(3, BehaviorPositionValidate.create((holder) -> {
            return holder.is(PoiTypes.MEETING);
        }, MemoryModuleType.MEETING_POINT)), Pair.of(3, new BehaviorGate<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), BehaviorGate.Order.ORDERED, BehaviorGate.Execution.RUN_ONE, ImmutableList.of(Pair.of(new BehaviorTradeVillager(), 1)))), getFullLookBehavior(), Pair.of(99, BehaviorSchedule.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super EntityVillager>>> getIdlePackage(VillagerProfession villagerprofession, float f) {
        return ImmutableList.of(Pair.of(2, new BehaviorGateSingle<>(ImmutableList.of(Pair.of(BehaviorInteract.of(EntityTypes.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 2), Pair.of(BehaviorInteract.of(EntityTypes.VILLAGER, 8, EntityAgeable::canBreed, EntityAgeable::canBreed, MemoryModuleType.BREED_TARGET, f, 2), 1), Pair.of(BehaviorInteract.of(EntityTypes.CAT, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 1), Pair.of(BehaviorStrollRandom.create(f), 1), Pair.of(BehaviorLookWalk.create(f, 2), 1), Pair.of(new BehaviorBedJump(f), 1), Pair.of(new BehaviorNop(30, 60), 1)))), Pair.of(3, new BehaviorVillageHeroGift(100)), Pair.of(3, BehaviorLookInteract.create(EntityTypes.PLAYER, 4)), Pair.of(3, new BehaviorTradePlayer(400, 1600)), Pair.of(3, new BehaviorGate<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), BehaviorGate.Order.ORDERED, BehaviorGate.Execution.RUN_ONE, ImmutableList.of(Pair.of(new BehaviorTradeVillager(), 1)))), Pair.of(3, new BehaviorGate<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.BREED_TARGET), BehaviorGate.Order.ORDERED, BehaviorGate.Execution.RUN_ONE, ImmutableList.of(Pair.of(new BehaviorMakeLove(), 1)))), getFullLookBehavior(), Pair.of(99, BehaviorSchedule.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super EntityVillager>>> getPanicPackage(VillagerProfession villagerprofession, float f) {
        float f1 = f * 1.5F;

        return ImmutableList.of(Pair.of(0, BehaviorCooldown.create()), Pair.of(1, BehaviorWalkAway.entity(MemoryModuleType.NEAREST_HOSTILE, f1, 6, false)), Pair.of(1, BehaviorWalkAway.entity(MemoryModuleType.HURT_BY_ENTITY, f1, 6, false)), Pair.of(3, BehaviorStrollRandom.create(f1, 2, 2)), getMinimalLookBehavior());
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super EntityVillager>>> getPreRaidPackage(VillagerProfession villagerprofession, float f) {
        return ImmutableList.of(Pair.of(0, BehaviorBellRing.create()), Pair.of(0, TriggerGate.triggerOneShuffled(ImmutableList.of(Pair.of(BehaviorWalkAwayBlock.create(MemoryModuleType.MEETING_POINT, f * 1.5F, 2, 150, 200), 6), Pair.of(BehaviorStrollRandom.create(f * 1.5F), 2)))), getMinimalLookBehavior(), Pair.of(99, BehaviorRaidReset.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super EntityVillager>>> getRaidPackage(VillagerProfession villagerprofession, float f) {
        return ImmutableList.of(Pair.of(0, BehaviorBuilder.sequence(BehaviorBuilder.triggerIf(Behaviors::raidExistsAndNotVictory), TriggerGate.triggerOneShuffled(ImmutableList.of(Pair.of(BehaviorOutside.create(f), 5), Pair.of(BehaviorStrollRandom.create(f * 1.1F), 2))))), Pair.of(0, new BehaviorCelebrate(600, 600)), Pair.of(2, BehaviorBuilder.sequence(BehaviorBuilder.triggerIf(Behaviors::raidExistsAndActive), BehaviorHome.create(24, f * 1.4F, 1))), getMinimalLookBehavior(), Pair.of(99, BehaviorRaidReset.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super EntityVillager>>> getHidePackage(VillagerProfession villagerprofession, float f) {
        boolean flag = true;

        return ImmutableList.of(Pair.of(0, BehaviorHide.create(15, 3)), Pair.of(1, BehaviorHome.create(32, f * 1.25F, 2)), getMinimalLookBehavior());
    }

    private static Pair<Integer, BehaviorControl<EntityLiving>> getFullLookBehavior() {
        return Pair.of(5, new BehaviorGateSingle<>(ImmutableList.of(Pair.of(BehaviorLookTarget.create(EntityTypes.CAT, 8.0F), 8), Pair.of(BehaviorLookTarget.create(EntityTypes.VILLAGER, 8.0F), 2), Pair.of(BehaviorLookTarget.create(EntityTypes.PLAYER, 8.0F), 2), Pair.of(BehaviorLookTarget.create(EnumCreatureType.CREATURE, 8.0F), 1), Pair.of(BehaviorLookTarget.create(EnumCreatureType.WATER_CREATURE, 8.0F), 1), Pair.of(BehaviorLookTarget.create(EnumCreatureType.AXOLOTLS, 8.0F), 1), Pair.of(BehaviorLookTarget.create(EnumCreatureType.UNDERGROUND_WATER_CREATURE, 8.0F), 1), Pair.of(BehaviorLookTarget.create(EnumCreatureType.WATER_AMBIENT, 8.0F), 1), Pair.of(BehaviorLookTarget.create(EnumCreatureType.MONSTER, 8.0F), 1), Pair.of(new BehaviorNop(30, 60), 2))));
    }

    private static Pair<Integer, BehaviorControl<EntityLiving>> getMinimalLookBehavior() {
        return Pair.of(5, new BehaviorGateSingle<>(ImmutableList.of(Pair.of(BehaviorLookTarget.create(EntityTypes.VILLAGER, 8.0F), 2), Pair.of(BehaviorLookTarget.create(EntityTypes.PLAYER, 8.0F), 2), Pair.of(new BehaviorNop(30, 60), 8))));
    }

    private static boolean raidExistsAndActive(WorldServer worldserver, EntityLiving entityliving) {
        Raid raid = worldserver.getRaidAt(entityliving.blockPosition());

        return raid != null && raid.isActive() && !raid.isVictory() && !raid.isLoss();
    }

    private static boolean raidExistsAndNotVictory(WorldServer worldserver, EntityLiving entityliving) {
        Raid raid = worldserver.getRaidAt(entityliving.blockPosition());

        return raid != null && raid.isVictory();
    }
}

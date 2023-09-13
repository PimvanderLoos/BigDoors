package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.monster.EntitySkeletonWither;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglinAbstract;
import net.minecraft.world.entity.monster.piglin.EntityPiglinBrute;
import net.minecraft.world.entity.monster.piglin.PiglinAI;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.block.BlockCampfire;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class SensorPiglinSpecific extends Sensor<EntityLiving> {

    public SensorPiglinSpecific() {}

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, new MemoryModuleType[]{MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_REPELLENT});
    }

    @Override
    protected void doTick(WorldServer worldserver, EntityLiving entityliving) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_REPELLENT, findNearestRepellent(worldserver, entityliving));
        Optional<EntityInsentient> optional = Optional.empty();
        Optional<EntityHoglin> optional1 = Optional.empty();
        Optional<EntityHoglin> optional2 = Optional.empty();
        Optional<EntityPiglin> optional3 = Optional.empty();
        Optional<EntityLiving> optional4 = Optional.empty();
        Optional<EntityHuman> optional5 = Optional.empty();
        Optional<EntityHuman> optional6 = Optional.empty();
        int i = 0;
        List<EntityPiglinAbstract> list = Lists.newArrayList();
        List<EntityPiglinAbstract> list1 = Lists.newArrayList();
        NearestVisibleLivingEntities nearestvisiblelivingentities = (NearestVisibleLivingEntities) behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
        Iterator iterator = nearestvisiblelivingentities.findAll((entityliving1) -> {
            return true;
        }).iterator();

        while (iterator.hasNext()) {
            EntityLiving entityliving1 = (EntityLiving) iterator.next();

            if (entityliving1 instanceof EntityHoglin) {
                EntityHoglin entityhoglin = (EntityHoglin) entityliving1;

                if (entityhoglin.isBaby() && optional2.isEmpty()) {
                    optional2 = Optional.of(entityhoglin);
                } else if (entityhoglin.isAdult()) {
                    ++i;
                    if (optional1.isEmpty() && entityhoglin.canBeHunted()) {
                        optional1 = Optional.of(entityhoglin);
                    }
                }
            } else if (entityliving1 instanceof EntityPiglinBrute) {
                EntityPiglinBrute entitypiglinbrute = (EntityPiglinBrute) entityliving1;

                list.add(entitypiglinbrute);
            } else if (entityliving1 instanceof EntityPiglin) {
                EntityPiglin entitypiglin = (EntityPiglin) entityliving1;

                if (entitypiglin.isBaby() && optional3.isEmpty()) {
                    optional3 = Optional.of(entitypiglin);
                } else if (entitypiglin.isAdult()) {
                    list.add(entitypiglin);
                }
            } else if (entityliving1 instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entityliving1;

                if (optional5.isEmpty() && !PiglinAI.isWearingGold(entityhuman) && entityliving.canAttack(entityliving1)) {
                    optional5 = Optional.of(entityhuman);
                }

                if (optional6.isEmpty() && !entityhuman.isSpectator() && PiglinAI.isPlayerHoldingLovedItem(entityhuman)) {
                    optional6 = Optional.of(entityhuman);
                }
            } else if (optional.isEmpty() && (entityliving1 instanceof EntitySkeletonWither || entityliving1 instanceof EntityWither)) {
                optional = Optional.of((EntityInsentient) entityliving1);
            } else if (optional4.isEmpty() && PiglinAI.isZombified(entityliving1.getType())) {
                optional4 = Optional.of(entityliving1);
            }
        }

        List<EntityLiving> list2 = (List) behaviorcontroller.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse(ImmutableList.of());
        Iterator iterator1 = list2.iterator();

        while (iterator1.hasNext()) {
            EntityLiving entityliving2 = (EntityLiving) iterator1.next();

            if (entityliving2 instanceof EntityPiglinAbstract) {
                EntityPiglinAbstract entitypiglinabstract = (EntityPiglinAbstract) entityliving2;

                if (entitypiglinabstract.isAdult()) {
                    list1.add(entitypiglinabstract);
                }
            }
        }

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, optional1);
        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, optional2);
        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, optional4);
        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, optional5);
        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, optional6);
        behaviorcontroller.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, (Object) list1);
        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, (Object) list);
        behaviorcontroller.setMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, (Object) list.size());
        behaviorcontroller.setMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, (Object) i);
    }

    private static Optional<BlockPosition> findNearestRepellent(WorldServer worldserver, EntityLiving entityliving) {
        return BlockPosition.findClosestMatch(entityliving.blockPosition(), 8, 4, (blockposition) -> {
            return isValidRepellent(worldserver, blockposition);
        });
    }

    private static boolean isValidRepellent(WorldServer worldserver, BlockPosition blockposition) {
        IBlockData iblockdata = worldserver.getBlockState(blockposition);
        boolean flag = iblockdata.is(TagsBlock.PIGLIN_REPELLENTS);

        return flag && iblockdata.is(Blocks.SOUL_CAMPFIRE) ? BlockCampfire.isLitCampfire(iblockdata) : flag;
    }
}

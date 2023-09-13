package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BehaviorTradeVillager extends Behavior<EntityVillager> {

    private static final int INTERACT_DIST_SQR = 5;
    private static final float SPEED_MODIFIER = 0.5F;
    private Set<Item> trades = ImmutableSet.of();

    public BehaviorTradeVillager() {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityVillager entityvillager) {
        return BehaviorUtil.targetIsValid(entityvillager.getBrain(), MemoryModuleType.INTERACTION_TARGET, EntityTypes.VILLAGER);
    }

    protected boolean canStillUse(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.checkExtraStartConditions(worldserver, entityvillager);
    }

    protected void start(WorldServer worldserver, EntityVillager entityvillager, long i) {
        EntityVillager entityvillager1 = (EntityVillager) entityvillager.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();

        BehaviorUtil.lockGazeAndWalkToEachOther(entityvillager, entityvillager1, 0.5F);
        this.trades = figureOutWhatIAmWillingToTrade(entityvillager, entityvillager1);
    }

    protected void tick(WorldServer worldserver, EntityVillager entityvillager, long i) {
        EntityVillager entityvillager1 = (EntityVillager) entityvillager.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();

        if (entityvillager.distanceToSqr((Entity) entityvillager1) <= 5.0D) {
            BehaviorUtil.lockGazeAndWalkToEachOther(entityvillager, entityvillager1, 0.5F);
            entityvillager.gossip(worldserver, entityvillager1, i);
            if (entityvillager.hasExcessFood() && (entityvillager.getVillagerData().getProfession() == VillagerProfession.FARMER || entityvillager1.wantsMoreFood())) {
                throwHalfStack(entityvillager, EntityVillager.FOOD_POINTS.keySet(), entityvillager1);
            }

            if (entityvillager1.getVillagerData().getProfession() == VillagerProfession.FARMER && entityvillager.getInventory().countItem(Items.WHEAT) > Items.WHEAT.getMaxStackSize() / 2) {
                throwHalfStack(entityvillager, ImmutableSet.of(Items.WHEAT), entityvillager1);
            }

            if (!this.trades.isEmpty() && entityvillager.getInventory().hasAnyOf(this.trades)) {
                throwHalfStack(entityvillager, this.trades, entityvillager1);
            }

        }
    }

    protected void stop(WorldServer worldserver, EntityVillager entityvillager, long i) {
        entityvillager.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
    }

    private static Set<Item> figureOutWhatIAmWillingToTrade(EntityVillager entityvillager, EntityVillager entityvillager1) {
        ImmutableSet<Item> immutableset = entityvillager1.getVillagerData().getProfession().requestedItems();
        ImmutableSet<Item> immutableset1 = entityvillager.getVillagerData().getProfession().requestedItems();

        return (Set) immutableset.stream().filter((item) -> {
            return !immutableset1.contains(item);
        }).collect(Collectors.toSet());
    }

    private static void throwHalfStack(EntityVillager entityvillager, Set<Item> set, EntityLiving entityliving) {
        InventorySubcontainer inventorysubcontainer = entityvillager.getInventory();
        ItemStack itemstack = ItemStack.EMPTY;

        for (int i = 0; i < inventorysubcontainer.getContainerSize(); ++i) {
            ItemStack itemstack1 = inventorysubcontainer.getItem(i);

            if (!itemstack1.isEmpty()) {
                Item item = itemstack1.getItem();

                if (set.contains(item)) {
                    int j;

                    if (itemstack1.getCount() > itemstack1.getMaxStackSize() / 2) {
                        j = itemstack1.getCount() / 2;
                    } else {
                        if (itemstack1.getCount() <= 24) {
                            continue;
                        }

                        j = itemstack1.getCount() - 24;
                    }

                    itemstack1.shrink(j);
                    itemstack = new ItemStack(item, j);
                    break;
                }
            }
        }

        if (!itemstack.isEmpty()) {
            BehaviorUtil.throwItem(entityvillager, itemstack, entityliving.position());
        }

    }
}

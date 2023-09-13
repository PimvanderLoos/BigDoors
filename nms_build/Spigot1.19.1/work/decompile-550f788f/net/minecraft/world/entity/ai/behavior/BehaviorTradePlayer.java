package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantRecipe;

public class BehaviorTradePlayer extends Behavior<EntityVillager> {

    private static final int MAX_LOOK_TIME = 900;
    private static final int STARTING_LOOK_TIME = 40;
    @Nullable
    private ItemStack playerItemStack;
    private final List<ItemStack> displayItems = Lists.newArrayList();
    private int cycleCounter;
    private int displayIndex;
    private int lookTime;

    public BehaviorTradePlayer(int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT), i, j);
    }

    public boolean checkExtraStartConditions(WorldServer worldserver, EntityVillager entityvillager) {
        BehaviorController<?> behaviorcontroller = entityvillager.getBrain();

        if (!behaviorcontroller.getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
            return false;
        } else {
            EntityLiving entityliving = (EntityLiving) behaviorcontroller.getMemory(MemoryModuleType.INTERACTION_TARGET).get();

            return entityliving.getType() == EntityTypes.PLAYER && entityvillager.isAlive() && entityliving.isAlive() && !entityvillager.isBaby() && entityvillager.distanceToSqr((Entity) entityliving) <= 17.0D;
        }
    }

    public boolean canStillUse(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.checkExtraStartConditions(worldserver, entityvillager) && this.lookTime > 0 && entityvillager.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    public void start(WorldServer worldserver, EntityVillager entityvillager, long i) {
        super.start(worldserver, entityvillager, i);
        this.lookAtTarget(entityvillager);
        this.cycleCounter = 0;
        this.displayIndex = 0;
        this.lookTime = 40;
    }

    public void tick(WorldServer worldserver, EntityVillager entityvillager, long i) {
        EntityLiving entityliving = this.lookAtTarget(entityvillager);

        this.findItemsToDisplay(entityliving, entityvillager);
        if (!this.displayItems.isEmpty()) {
            this.displayCyclingItems(entityvillager);
        } else {
            clearHeldItem(entityvillager);
            this.lookTime = Math.min(this.lookTime, 40);
        }

        --this.lookTime;
    }

    public void stop(WorldServer worldserver, EntityVillager entityvillager, long i) {
        super.stop(worldserver, entityvillager, i);
        entityvillager.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        clearHeldItem(entityvillager);
        this.playerItemStack = null;
    }

    private void findItemsToDisplay(EntityLiving entityliving, EntityVillager entityvillager) {
        boolean flag = false;
        ItemStack itemstack = entityliving.getMainHandItem();

        if (this.playerItemStack == null || !ItemStack.isSame(this.playerItemStack, itemstack)) {
            this.playerItemStack = itemstack;
            flag = true;
            this.displayItems.clear();
        }

        if (flag && !this.playerItemStack.isEmpty()) {
            this.updateDisplayItems(entityvillager);
            if (!this.displayItems.isEmpty()) {
                this.lookTime = 900;
                this.displayFirstItem(entityvillager);
            }
        }

    }

    private void displayFirstItem(EntityVillager entityvillager) {
        displayAsHeldItem(entityvillager, (ItemStack) this.displayItems.get(0));
    }

    private void updateDisplayItems(EntityVillager entityvillager) {
        Iterator iterator = entityvillager.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            if (!merchantrecipe.isOutOfStock() && this.playerItemStackMatchesCostOfOffer(merchantrecipe)) {
                this.displayItems.add(merchantrecipe.getResult());
            }
        }

    }

    private boolean playerItemStackMatchesCostOfOffer(MerchantRecipe merchantrecipe) {
        return ItemStack.isSame(this.playerItemStack, merchantrecipe.getCostA()) || ItemStack.isSame(this.playerItemStack, merchantrecipe.getCostB());
    }

    private static void clearHeldItem(EntityVillager entityvillager) {
        entityvillager.setItemSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
        entityvillager.setDropChance(EnumItemSlot.MAINHAND, 0.085F);
    }

    private static void displayAsHeldItem(EntityVillager entityvillager, ItemStack itemstack) {
        entityvillager.setItemSlot(EnumItemSlot.MAINHAND, itemstack);
        entityvillager.setDropChance(EnumItemSlot.MAINHAND, 0.0F);
    }

    private EntityLiving lookAtTarget(EntityVillager entityvillager) {
        BehaviorController<?> behaviorcontroller = entityvillager.getBrain();
        EntityLiving entityliving = (EntityLiving) behaviorcontroller.getMemory(MemoryModuleType.INTERACTION_TARGET).get();

        behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving, true)));
        return entityliving;
    }

    private void displayCyclingItems(EntityVillager entityvillager) {
        if (this.displayItems.size() >= 2 && ++this.cycleCounter >= 40) {
            ++this.displayIndex;
            this.cycleCounter = 0;
            if (this.displayIndex > this.displayItems.size() - 1) {
                this.displayIndex = 0;
            }

            displayAsHeldItem(entityvillager, (ItemStack) this.displayItems.get(this.displayIndex));
        }

    }
}

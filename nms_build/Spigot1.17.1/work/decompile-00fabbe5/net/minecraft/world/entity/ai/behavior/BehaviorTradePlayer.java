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

    public boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        BehaviorController<?> behaviorcontroller = entityvillager.getBehaviorController();

        if (!behaviorcontroller.getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
            return false;
        } else {
            EntityLiving entityliving = (EntityLiving) behaviorcontroller.getMemory(MemoryModuleType.INTERACTION_TARGET).get();

            return entityliving.getEntityType() == EntityTypes.PLAYER && entityvillager.isAlive() && entityliving.isAlive() && !entityvillager.isBaby() && entityvillager.f((Entity) entityliving) <= 17.0D;
        }
    }

    public boolean b(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.a(worldserver, entityvillager) && this.lookTime > 0 && entityvillager.getBehaviorController().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    public void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        super.a(worldserver, entityvillager, i);
        this.d(entityvillager);
        this.cycleCounter = 0;
        this.displayIndex = 0;
        this.lookTime = 40;
    }

    public void d(WorldServer worldserver, EntityVillager entityvillager, long i) {
        EntityLiving entityliving = this.d(entityvillager);

        this.a(entityliving, entityvillager);
        if (!this.displayItems.isEmpty()) {
            this.e(entityvillager);
        } else {
            c(entityvillager);
            this.lookTime = Math.min(this.lookTime, 40);
        }

        --this.lookTime;
    }

    public void c(WorldServer worldserver, EntityVillager entityvillager, long i) {
        super.c(worldserver, entityvillager, i);
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.INTERACTION_TARGET);
        c(entityvillager);
        this.playerItemStack = null;
    }

    private void a(EntityLiving entityliving, EntityVillager entityvillager) {
        boolean flag = false;
        ItemStack itemstack = entityliving.getItemInMainHand();

        if (this.playerItemStack == null || !ItemStack.c(this.playerItemStack, itemstack)) {
            this.playerItemStack = itemstack;
            flag = true;
            this.displayItems.clear();
        }

        if (flag && !this.playerItemStack.isEmpty()) {
            this.b(entityvillager);
            if (!this.displayItems.isEmpty()) {
                this.lookTime = 900;
                this.a(entityvillager);
            }
        }

    }

    private void a(EntityVillager entityvillager) {
        a(entityvillager, (ItemStack) this.displayItems.get(0));
    }

    private void b(EntityVillager entityvillager) {
        Iterator iterator = entityvillager.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            if (!merchantrecipe.isFullyUsed() && this.a(merchantrecipe)) {
                this.displayItems.add(merchantrecipe.getSellingItem());
            }
        }

    }

    private boolean a(MerchantRecipe merchantrecipe) {
        return ItemStack.c(this.playerItemStack, merchantrecipe.getBuyItem1()) || ItemStack.c(this.playerItemStack, merchantrecipe.getBuyItem2());
    }

    private static void c(EntityVillager entityvillager) {
        entityvillager.setSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
        entityvillager.a(EnumItemSlot.MAINHAND, 0.085F);
    }

    private static void a(EntityVillager entityvillager, ItemStack itemstack) {
        entityvillager.setSlot(EnumItemSlot.MAINHAND, itemstack);
        entityvillager.a(EnumItemSlot.MAINHAND, 0.0F);
    }

    private EntityLiving d(EntityVillager entityvillager) {
        BehaviorController<?> behaviorcontroller = entityvillager.getBehaviorController();
        EntityLiving entityliving = (EntityLiving) behaviorcontroller.getMemory(MemoryModuleType.INTERACTION_TARGET).get();

        behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving, true)));
        return entityliving;
    }

    private void e(EntityVillager entityvillager) {
        if (this.displayItems.size() >= 2 && ++this.cycleCounter >= 40) {
            ++this.displayIndex;
            this.cycleCounter = 0;
            if (this.displayIndex > this.displayItems.size() - 1) {
                this.displayIndex = 0;
            }

            a(entityvillager, (ItemStack) this.displayItems.get(this.displayIndex));
        }

    }
}

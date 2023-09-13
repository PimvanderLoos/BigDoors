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

    @Nullable
    private ItemStack b;
    private final List<ItemStack> c = Lists.newArrayList();
    private int d;
    private int e;
    private int f;

    public BehaviorTradePlayer(int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT), i, j);
    }

    public boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        BehaviorController<?> behaviorcontroller = entityvillager.getBehaviorController();

        if (!behaviorcontroller.getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
            return false;
        } else {
            EntityLiving entityliving = (EntityLiving) behaviorcontroller.getMemory(MemoryModuleType.INTERACTION_TARGET).get();

            return entityliving.getEntityType() == EntityTypes.PLAYER && entityvillager.isAlive() && entityliving.isAlive() && !entityvillager.isBaby() && entityvillager.h((Entity) entityliving) <= 17.0D;
        }
    }

    public boolean b(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.a(worldserver, entityvillager) && this.f > 0 && entityvillager.getBehaviorController().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    public void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        super.a(worldserver, entityvillager, i);
        this.c(entityvillager);
        this.d = 0;
        this.e = 0;
        this.f = 40;
    }

    public void d(WorldServer worldserver, EntityVillager entityvillager, long i) {
        EntityLiving entityliving = this.c(entityvillager);

        this.a(entityliving, entityvillager);
        if (!this.c.isEmpty()) {
            this.d(entityvillager);
        } else {
            entityvillager.setSlot(EnumItemSlot.MAINHAND, ItemStack.b);
            this.f = Math.min(this.f, 40);
        }

        --this.f;
    }

    public void c(WorldServer worldserver, EntityVillager entityvillager, long i) {
        super.c(worldserver, entityvillager, i);
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.INTERACTION_TARGET);
        entityvillager.setSlot(EnumItemSlot.MAINHAND, ItemStack.b);
        this.b = null;
    }

    private void a(EntityLiving entityliving, EntityVillager entityvillager) {
        boolean flag = false;
        ItemStack itemstack = entityliving.getItemInMainHand();

        if (this.b == null || !ItemStack.c(this.b, itemstack)) {
            this.b = itemstack;
            flag = true;
            this.c.clear();
        }

        if (flag && !this.b.isEmpty()) {
            this.b(entityvillager);
            if (!this.c.isEmpty()) {
                this.f = 900;
                this.a(entityvillager);
            }
        }

    }

    private void a(EntityVillager entityvillager) {
        entityvillager.setSlot(EnumItemSlot.MAINHAND, (ItemStack) this.c.get(0));
    }

    private void b(EntityVillager entityvillager) {
        Iterator iterator = entityvillager.getOffers().iterator();

        while (iterator.hasNext()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

            if (!merchantrecipe.isFullyUsed() && this.a(merchantrecipe)) {
                this.c.add(merchantrecipe.getSellingItem());
            }
        }

    }

    private boolean a(MerchantRecipe merchantrecipe) {
        return ItemStack.c(this.b, merchantrecipe.getBuyItem1()) || ItemStack.c(this.b, merchantrecipe.getBuyItem2());
    }

    private EntityLiving c(EntityVillager entityvillager) {
        BehaviorController<?> behaviorcontroller = entityvillager.getBehaviorController();
        EntityLiving entityliving = (EntityLiving) behaviorcontroller.getMemory(MemoryModuleType.INTERACTION_TARGET).get();

        behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving, true)));
        return entityliving;
    }

    private void d(EntityVillager entityvillager) {
        if (this.c.size() >= 2 && ++this.d >= 40) {
            ++this.e;
            this.d = 0;
            if (this.e > this.c.size() - 1) {
                this.e = 0;
            }

            entityvillager.setSlot(EnumItemSlot.MAINHAND, (ItemStack) this.c.get(this.e));
        }

    }
}

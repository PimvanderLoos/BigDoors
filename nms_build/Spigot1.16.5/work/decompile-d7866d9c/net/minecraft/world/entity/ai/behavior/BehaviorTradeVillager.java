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

    private Set<Item> b = ImmutableSet.of();

    public BehaviorTradeVillager() {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryStatus.VALUE_PRESENT));
    }

    protected boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        return BehaviorUtil.a(entityvillager.getBehaviorController(), MemoryModuleType.INTERACTION_TARGET, EntityTypes.VILLAGER);
    }

    protected boolean b(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.a(worldserver, entityvillager);
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        EntityVillager entityvillager1 = (EntityVillager) entityvillager.getBehaviorController().getMemory(MemoryModuleType.INTERACTION_TARGET).get();

        BehaviorUtil.a(entityvillager, entityvillager1, 0.5F);
        this.b = a(entityvillager, entityvillager1);
    }

    protected void d(WorldServer worldserver, EntityVillager entityvillager, long i) {
        EntityVillager entityvillager1 = (EntityVillager) entityvillager.getBehaviorController().getMemory(MemoryModuleType.INTERACTION_TARGET).get();

        if (entityvillager.h((Entity) entityvillager1) <= 5.0D) {
            BehaviorUtil.a(entityvillager, entityvillager1, 0.5F);
            entityvillager.a(worldserver, entityvillager1, i);
            if (entityvillager.fg() && (entityvillager.getVillagerData().getProfession() == VillagerProfession.FARMER || entityvillager1.fh())) {
                a(entityvillager, EntityVillager.bp.keySet(), entityvillager1);
            }

            if (entityvillager1.getVillagerData().getProfession() == VillagerProfession.FARMER && entityvillager.getInventory().a(Items.WHEAT) > Items.WHEAT.getMaxStackSize() / 2) {
                a(entityvillager, ImmutableSet.of(Items.WHEAT), entityvillager1);
            }

            if (!this.b.isEmpty() && entityvillager.getInventory().a(this.b)) {
                a(entityvillager, this.b, entityvillager1);
            }

        }
    }

    protected void c(WorldServer worldserver, EntityVillager entityvillager, long i) {
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.INTERACTION_TARGET);
    }

    private static Set<Item> a(EntityVillager entityvillager, EntityVillager entityvillager1) {
        ImmutableSet<Item> immutableset = entityvillager1.getVillagerData().getProfession().c();
        ImmutableSet<Item> immutableset1 = entityvillager.getVillagerData().getProfession().c();

        return (Set) immutableset.stream().filter((item) -> {
            return !immutableset1.contains(item);
        }).collect(Collectors.toSet());
    }

    private static void a(EntityVillager entityvillager, Set<Item> set, EntityLiving entityliving) {
        InventorySubcontainer inventorysubcontainer = entityvillager.getInventory();
        ItemStack itemstack = ItemStack.b;

        for (int i = 0; i < inventorysubcontainer.getSize(); ++i) {
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

                    itemstack1.subtract(j);
                    itemstack = new ItemStack(item, j);
                    break;
                }
            }
        }

        if (!itemstack.isEmpty()) {
            BehaviorUtil.a((EntityLiving) entityvillager, itemstack, entityliving.getPositionVector());
        }

    }
}

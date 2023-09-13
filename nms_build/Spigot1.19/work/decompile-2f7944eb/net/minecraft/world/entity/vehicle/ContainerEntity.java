package net.minecraft.world.entity.vehicle;

import java.util.Iterator;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.IInventory;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.InventoryUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAI;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.Vec3D;

public interface ContainerEntity extends IInventory, ITileInventory {

    Vec3D position();

    @Nullable
    MinecraftKey getLootTable();

    void setLootTable(@Nullable MinecraftKey minecraftkey);

    long getLootTableSeed();

    void setLootTableSeed(long i);

    NonNullList<ItemStack> getItemStacks();

    void clearItemStacks();

    World getLevel();

    boolean isRemoved();

    @Override
    default boolean isEmpty() {
        return this.isChestVehicleEmpty();
    }

    default void addChestVehicleSaveData(NBTTagCompound nbttagcompound) {
        if (this.getLootTable() != null) {
            nbttagcompound.putString("LootTable", this.getLootTable().toString());
            if (this.getLootTableSeed() != 0L) {
                nbttagcompound.putLong("LootTableSeed", this.getLootTableSeed());
            }
        } else {
            ContainerUtil.saveAllItems(nbttagcompound, this.getItemStacks());
        }

    }

    default void readChestVehicleSaveData(NBTTagCompound nbttagcompound) {
        this.clearItemStacks();
        if (nbttagcompound.contains("LootTable", 8)) {
            this.setLootTable(new MinecraftKey(nbttagcompound.getString("LootTable")));
            this.setLootTableSeed(nbttagcompound.getLong("LootTableSeed"));
        } else {
            ContainerUtil.loadAllItems(nbttagcompound, this.getItemStacks());
        }

    }

    default void chestVehicleDestroyed(DamageSource damagesource, World world, Entity entity) {
        if (world.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            InventoryUtils.dropContents(world, entity, (IInventory) this);
            if (!world.isClientSide) {
                Entity entity1 = damagesource.getDirectEntity();

                if (entity1 != null && entity1.getType() == EntityTypes.PLAYER) {
                    PiglinAI.angerNearbyPiglins((EntityHuman) entity1, true);
                }
            }

        }
    }

    default EnumInteractionResult interactWithChestVehicle(BiConsumer<GameEvent, Entity> biconsumer, EntityHuman entityhuman) {
        entityhuman.openMenu(this);
        if (!entityhuman.level.isClientSide) {
            biconsumer.accept(GameEvent.CONTAINER_OPEN, entityhuman);
            PiglinAI.angerNearbyPiglins(entityhuman, true);
            return EnumInteractionResult.CONSUME;
        } else {
            return EnumInteractionResult.SUCCESS;
        }
    }

    default void unpackChestVehicleLootTable(@Nullable EntityHuman entityhuman) {
        MinecraftServer minecraftserver = this.getLevel().getServer();

        if (this.getLootTable() != null && minecraftserver != null) {
            LootTable loottable = minecraftserver.getLootTables().get(this.getLootTable());

            if (entityhuman != null) {
                CriterionTriggers.GENERATE_LOOT.trigger((EntityPlayer) entityhuman, this.getLootTable());
            }

            this.setLootTable((MinecraftKey) null);
            LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.getLevel())).withParameter(LootContextParameters.ORIGIN, this.position()).withOptionalRandomSeed(this.getLootTableSeed());

            if (entityhuman != null) {
                loottableinfo_builder.withLuck(entityhuman.getLuck()).withParameter(LootContextParameters.THIS_ENTITY, entityhuman);
            }

            loottable.fill(this, loottableinfo_builder.create(LootContextParameterSets.CHEST));
        }

    }

    default void clearChestVehicleContent() {
        this.unpackChestVehicleLootTable((EntityHuman) null);
        this.getItemStacks().clear();
    }

    default boolean isChestVehicleEmpty() {
        Iterator iterator = this.getItemStacks().iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    default ItemStack removeChestVehicleItemNoUpdate(int i) {
        this.unpackChestVehicleLootTable((EntityHuman) null);
        ItemStack itemstack = (ItemStack) this.getItemStacks().get(i);

        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.getItemStacks().set(i, ItemStack.EMPTY);
            return itemstack;
        }
    }

    default ItemStack getChestVehicleItem(int i) {
        this.unpackChestVehicleLootTable((EntityHuman) null);
        return (ItemStack) this.getItemStacks().get(i);
    }

    default ItemStack removeChestVehicleItem(int i, int j) {
        this.unpackChestVehicleLootTable((EntityHuman) null);
        return ContainerUtil.removeItem(this.getItemStacks(), i, j);
    }

    default void setChestVehicleItem(int i, ItemStack itemstack) {
        this.unpackChestVehicleLootTable((EntityHuman) null);
        this.getItemStacks().set(i, itemstack);
        if (!itemstack.isEmpty() && itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

    }

    default SlotAccess getChestVehicleSlot(final int i) {
        return i >= 0 && i < this.getContainerSize() ? new SlotAccess() {
            @Override
            public ItemStack get() {
                return ContainerEntity.this.getChestVehicleItem(i);
            }

            @Override
            public boolean set(ItemStack itemstack) {
                ContainerEntity.this.setChestVehicleItem(i, itemstack);
                return true;
            }
        } : SlotAccess.NULL;
    }

    default boolean isChestVehicleStillValid(EntityHuman entityhuman) {
        return !this.isRemoved() && this.position().closerThan(entityhuman.position(), 8.0D);
    }
}

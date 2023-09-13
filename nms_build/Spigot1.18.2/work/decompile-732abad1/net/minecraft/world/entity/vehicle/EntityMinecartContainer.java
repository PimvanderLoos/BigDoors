package net.minecraft.world.entity.vehicle;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.EnumHand;
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
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;

public abstract class EntityMinecartContainer extends EntityMinecartAbstract implements IInventory, ITileInventory {

    private NonNullList<ItemStack> itemStacks;
    @Nullable
    public MinecraftKey lootTable;
    public long lootTableSeed;

    protected EntityMinecartContainer(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
    }

    protected EntityMinecartContainer(EntityTypes<?> entitytypes, double d0, double d1, double d2, World world) {
        super(entitytypes, world, d0, d1, d2);
        this.itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
    }

    @Override
    public void destroy(DamageSource damagesource) {
        super.destroy(damagesource);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            InventoryUtils.dropContents(this.level, (Entity) this, (IInventory) this);
            if (!this.level.isClientSide) {
                Entity entity = damagesource.getDirectEntity();

                if (entity != null && entity.getType() == EntityTypes.PLAYER) {
                    PiglinAI.angerNearbyPiglins((EntityHuman) entity, true);
                }
            }
        }

    }

    @Override
    public boolean isEmpty() {
        Iterator iterator = this.itemStacks.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    @Override
    public ItemStack getItem(int i) {
        this.unpackLootTable((EntityHuman) null);
        return (ItemStack) this.itemStacks.get(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        this.unpackLootTable((EntityHuman) null);
        return ContainerUtil.removeItem(this.itemStacks, i, j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        this.unpackLootTable((EntityHuman) null);
        ItemStack itemstack = (ItemStack) this.itemStacks.get(i);

        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.itemStacks.set(i, ItemStack.EMPTY);
            return itemstack;
        }
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        this.unpackLootTable((EntityHuman) null);
        this.itemStacks.set(i, itemstack);
        if (!itemstack.isEmpty() && itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

    }

    @Override
    public SlotAccess getSlot(final int i) {
        return i >= 0 && i < this.getContainerSize() ? new SlotAccess() {
            @Override
            public ItemStack get() {
                return EntityMinecartContainer.this.getItem(i);
            }

            @Override
            public boolean set(ItemStack itemstack) {
                EntityMinecartContainer.this.setItem(i, itemstack);
                return true;
            }
        } : super.getSlot(i);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return this.isRemoved() ? false : entityhuman.distanceToSqr((Entity) this) <= 64.0D;
    }

    @Override
    public void remove(Entity.RemovalReason entity_removalreason) {
        if (!this.level.isClientSide && entity_removalreason.shouldDestroy()) {
            InventoryUtils.dropContents(this.level, (Entity) this, (IInventory) this);
        }

        super.remove(entity_removalreason);
    }

    @Override
    protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        if (this.lootTable != null) {
            nbttagcompound.putString("LootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                nbttagcompound.putLong("LootTableSeed", this.lootTableSeed);
            }
        } else {
            ContainerUtil.saveAllItems(nbttagcompound, this.itemStacks);
        }

    }

    @Override
    protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (nbttagcompound.contains("LootTable", 8)) {
            this.lootTable = new MinecraftKey(nbttagcompound.getString("LootTable"));
            this.lootTableSeed = nbttagcompound.getLong("LootTableSeed");
        } else {
            ContainerUtil.loadAllItems(nbttagcompound, this.itemStacks);
        }

    }

    @Override
    public EnumInteractionResult interact(EntityHuman entityhuman, EnumHand enumhand) {
        entityhuman.openMenu(this);
        if (!entityhuman.level.isClientSide) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, (Entity) entityhuman);
            PiglinAI.angerNearbyPiglins(entityhuman, true);
            return EnumInteractionResult.CONSUME;
        } else {
            return EnumInteractionResult.SUCCESS;
        }
    }

    @Override
    protected void applyNaturalSlowdown() {
        float f = 0.98F;

        if (this.lootTable == null) {
            int i = 15 - Container.getRedstoneSignalFromContainer(this);

            f += (float) i * 0.001F;
        }

        if (this.isInWater()) {
            f *= 0.95F;
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply((double) f, 0.0D, (double) f));
    }

    public void unpackLootTable(@Nullable EntityHuman entityhuman) {
        if (this.lootTable != null && this.level.getServer() != null) {
            LootTable loottable = this.level.getServer().getLootTables().get(this.lootTable);

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.GENERATE_LOOT.trigger((EntityPlayer) entityhuman, this.lootTable);
            }

            this.lootTable = null;
            LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.level)).withParameter(LootContextParameters.ORIGIN, this.position()).withOptionalRandomSeed(this.lootTableSeed);

            if (entityhuman != null) {
                loottableinfo_builder.withLuck(entityhuman.getLuck()).withParameter(LootContextParameters.THIS_ENTITY, entityhuman);
            }

            loottable.fill(this, loottableinfo_builder.create(LootContextParameterSets.CHEST));
        }

    }

    @Override
    public void clearContent() {
        this.unpackLootTable((EntityHuman) null);
        this.itemStacks.clear();
    }

    public void setLootTable(MinecraftKey minecraftkey, long i) {
        this.lootTable = minecraftkey;
        this.lootTableSeed = i;
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
        if (this.lootTable != null && entityhuman.isSpectator()) {
            return null;
        } else {
            this.unpackLootTable(playerinventory.player);
            return this.createMenu(i, playerinventory);
        }
    }

    protected abstract Container createMenu(int i, PlayerInventory playerinventory);
}

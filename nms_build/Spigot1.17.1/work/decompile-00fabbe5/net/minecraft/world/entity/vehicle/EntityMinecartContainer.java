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
        this.itemStacks = NonNullList.a(36, ItemStack.EMPTY);
    }

    protected EntityMinecartContainer(EntityTypes<?> entitytypes, double d0, double d1, double d2, World world) {
        super(entitytypes, world, d0, d1, d2);
        this.itemStacks = NonNullList.a(36, ItemStack.EMPTY);
    }

    @Override
    public void a(DamageSource damagesource) {
        super.a(damagesource);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            InventoryUtils.dropEntity(this.level, this, this);
            if (!this.level.isClientSide) {
                Entity entity = damagesource.k();

                if (entity != null && entity.getEntityType() == EntityTypes.PLAYER) {
                    PiglinAI.a((EntityHuman) entity, true);
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
        this.d((EntityHuman) null);
        return (ItemStack) this.itemStacks.get(i);
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        this.d((EntityHuman) null);
        return ContainerUtil.a(this.itemStacks, i, j);
    }

    @Override
    public ItemStack splitWithoutUpdate(int i) {
        this.d((EntityHuman) null);
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
        this.d((EntityHuman) null);
        this.itemStacks.set(i, itemstack);
        if (!itemstack.isEmpty() && itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

    }

    @Override
    public SlotAccess k(final int i) {
        return i >= 0 && i < this.getSize() ? new SlotAccess() {
            @Override
            public ItemStack a() {
                return EntityMinecartContainer.this.getItem(i);
            }

            @Override
            public boolean a(ItemStack itemstack) {
                EntityMinecartContainer.this.setItem(i, itemstack);
                return true;
            }
        } : super.k(i);
    }

    @Override
    public void update() {}

    @Override
    public boolean a(EntityHuman entityhuman) {
        return this.isRemoved() ? false : entityhuman.f((Entity) this) <= 64.0D;
    }

    @Override
    public void a(Entity.RemovalReason entity_removalreason) {
        if (!this.level.isClientSide && entity_removalreason.a()) {
            InventoryUtils.dropEntity(this.level, this, this);
        }

        super.a(entity_removalreason);
    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.lootTable != null) {
            nbttagcompound.setString("LootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                nbttagcompound.setLong("LootTableSeed", this.lootTableSeed);
            }
        } else {
            ContainerUtil.a(nbttagcompound, this.itemStacks);
        }

    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.itemStacks = NonNullList.a(this.getSize(), ItemStack.EMPTY);
        if (nbttagcompound.hasKeyOfType("LootTable", 8)) {
            this.lootTable = new MinecraftKey(nbttagcompound.getString("LootTable"));
            this.lootTableSeed = nbttagcompound.getLong("LootTableSeed");
        } else {
            ContainerUtil.b(nbttagcompound, this.itemStacks);
        }

    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
        entityhuman.openContainer(this);
        if (!entityhuman.level.isClientSide) {
            this.a(GameEvent.CONTAINER_OPEN, (Entity) entityhuman);
            PiglinAI.a(entityhuman, true);
            return EnumInteractionResult.CONSUME;
        } else {
            return EnumInteractionResult.SUCCESS;
        }
    }

    @Override
    protected void decelerate() {
        float f = 0.98F;

        if (this.lootTable == null) {
            int i = 15 - Container.b((IInventory) this);

            f += (float) i * 0.001F;
        }

        if (this.isInWater()) {
            f *= 0.95F;
        }

        this.setMot(this.getMot().d((double) f, 0.0D, (double) f));
    }

    public void d(@Nullable EntityHuman entityhuman) {
        if (this.lootTable != null && this.level.getMinecraftServer() != null) {
            LootTable loottable = this.level.getMinecraftServer().getLootTableRegistry().getLootTable(this.lootTable);

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.GENERATE_LOOT.a((EntityPlayer) entityhuman, this.lootTable);
            }

            this.lootTable = null;
            LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.level)).set(LootContextParameters.ORIGIN, this.getPositionVector()).a(this.lootTableSeed);

            if (entityhuman != null) {
                loottableinfo_builder.a(entityhuman.fF()).set(LootContextParameters.THIS_ENTITY, entityhuman);
            }

            loottable.fillInventory(this, loottableinfo_builder.build(LootContextParameterSets.CHEST));
        }

    }

    @Override
    public void clear() {
        this.d((EntityHuman) null);
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
            this.d(playerinventory.player);
            return this.a(i, playerinventory);
        }
    }

    protected abstract Container a(int i, PlayerInventory playerinventory);
}

package net.minecraft.world.inventory;

import java.util.Optional;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.AutoRecipeStackManager;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeCrafting;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;

public class ContainerWorkbench extends ContainerRecipeBook<InventoryCrafting> {

    public static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1;
    private static final int CRAFT_SLOT_END = 10;
    private static final int INV_SLOT_START = 10;
    private static final int INV_SLOT_END = 37;
    private static final int USE_ROW_SLOT_START = 37;
    private static final int USE_ROW_SLOT_END = 46;
    public final InventoryCrafting craftSlots;
    public final InventoryCraftResult resultSlots;
    public final ContainerAccess access;
    private final EntityHuman player;

    public ContainerWorkbench(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.NULL);
    }

    public ContainerWorkbench(int i, PlayerInventory playerinventory, ContainerAccess containeraccess) {
        super(Containers.CRAFTING, i);
        this.craftSlots = new InventoryCrafting(this, 3, 3);
        this.resultSlots = new InventoryCraftResult();
        this.access = containeraccess;
        this.player = playerinventory.player;
        this.addSlot(new SlotResult(playerinventory.player, this.craftSlots, this.resultSlots, 0, 124, 35));

        int j;
        int k;

        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 3; ++k) {
                this.addSlot(new Slot(this.craftSlots, k + j * 3, 30 + k * 18, 17 + j * 18));
            }
        }

        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerinventory, j, 8 + j * 18, 142));
        }

    }

    protected static void slotChangedCraftingGrid(Container container, World world, EntityHuman entityhuman, InventoryCrafting inventorycrafting, InventoryCraftResult inventorycraftresult) {
        if (!world.isClientSide) {
            EntityPlayer entityplayer = (EntityPlayer) entityhuman;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<RecipeCrafting> optional = world.getServer().getRecipeManager().getRecipeFor(Recipes.CRAFTING, inventorycrafting, world);

            if (optional.isPresent()) {
                RecipeCrafting recipecrafting = (RecipeCrafting) optional.get();

                if (inventorycraftresult.setRecipeUsed(world, entityplayer, recipecrafting)) {
                    itemstack = recipecrafting.assemble(inventorycrafting);
                }
            }

            inventorycraftresult.setItem(0, itemstack);
            container.setRemoteSlot(0, itemstack);
            entityplayer.connection.send(new PacketPlayOutSetSlot(container.containerId, container.incrementStateId(), 0, itemstack));
        }
    }

    @Override
    public void slotsChanged(IInventory iinventory) {
        this.access.execute((world, blockposition) -> {
            slotChangedCraftingGrid(this, world, this.player, this.craftSlots, this.resultSlots);
        });
    }

    @Override
    public void fillCraftSlotsStackedContents(AutoRecipeStackManager autorecipestackmanager) {
        this.craftSlots.fillStackedContents(autorecipestackmanager);
    }

    @Override
    public void clearCraftingContent() {
        this.craftSlots.clearContent();
        this.resultSlots.clearContent();
    }

    @Override
    public boolean recipeMatches(IRecipe<? super InventoryCrafting> irecipe) {
        return irecipe.matches(this.craftSlots, this.player.level);
    }

    @Override
    public void removed(EntityHuman entityhuman) {
        super.removed(entityhuman);
        this.access.execute((world, blockposition) -> {
            this.clearContainer(entityhuman, this.craftSlots);
        });
    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return stillValid(this.access, entityhuman, Blocks.CRAFTING_TABLE);
    }

    @Override
    public ItemStack quickMoveStack(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.copy();
            if (i == 0) {
                this.access.execute((world, blockposition) -> {
                    itemstack1.getItem().onCraftedBy(itemstack1, world, entityhuman);
                });
                if (!this.moveItemStackTo(itemstack1, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (i >= 10 && i < 46) {
                if (!this.moveItemStackTo(itemstack1, 1, 10, false)) {
                    if (i < 37) {
                        if (!this.moveItemStackTo(itemstack1, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemstack1, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(entityhuman, itemstack1);
            if (i == 0) {
                entityhuman.drop(itemstack1, false);
            }
        }

        return itemstack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemstack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(itemstack, slot);
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return this.craftSlots.getWidth();
    }

    @Override
    public int getGridHeight() {
        return this.craftSlots.getHeight();
    }

    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(int i) {
        return i != this.getResultSlotIndex();
    }
}

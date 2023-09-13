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
    private final InventoryCrafting craftSlots;
    private final InventoryCraftResult resultSlots;
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
        this.a((Slot) (new SlotResult(playerinventory.player, this.craftSlots, this.resultSlots, 0, 124, 35)));

        int j;
        int k;

        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 3; ++k) {
                this.a(new Slot(this.craftSlots, k + j * 3, 30 + k * 18, 17 + j * 18));
            }
        }

        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.a(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.a(new Slot(playerinventory, j, 8 + j * 18, 142));
        }

    }

    protected static void a(Container container, World world, EntityHuman entityhuman, InventoryCrafting inventorycrafting, InventoryCraftResult inventorycraftresult) {
        if (!world.isClientSide) {
            EntityPlayer entityplayer = (EntityPlayer) entityhuman;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<RecipeCrafting> optional = world.getMinecraftServer().getCraftingManager().craft(Recipes.CRAFTING, inventorycrafting, world);

            if (optional.isPresent()) {
                RecipeCrafting recipecrafting = (RecipeCrafting) optional.get();

                if (inventorycraftresult.setRecipeUsed(world, entityplayer, recipecrafting)) {
                    itemstack = recipecrafting.a((IInventory) inventorycrafting);
                }
            }

            inventorycraftresult.setItem(0, itemstack);
            container.a(0, itemstack);
            entityplayer.connection.sendPacket(new PacketPlayOutSetSlot(container.containerId, container.incrementStateId(), 0, itemstack));
        }
    }

    @Override
    public void a(IInventory iinventory) {
        this.access.a((world, blockposition) -> {
            a(this, world, this.player, this.craftSlots, this.resultSlots);
        });
    }

    @Override
    public void a(AutoRecipeStackManager autorecipestackmanager) {
        this.craftSlots.a(autorecipestackmanager);
    }

    @Override
    public void l() {
        this.craftSlots.clear();
        this.resultSlots.clear();
    }

    @Override
    public boolean a(IRecipe<? super InventoryCrafting> irecipe) {
        return irecipe.a(this.craftSlots, this.player.level);
    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.access.a((world, blockposition) -> {
            this.a(entityhuman, (IInventory) this.craftSlots);
        });
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return a(this.access, entityhuman, Blocks.CRAFTING_TABLE);
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 0) {
                this.access.a((world, blockposition) -> {
                    itemstack1.getItem().b(itemstack1, world, entityhuman);
                });
                if (!this.a(itemstack1, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot.a(itemstack1, itemstack);
            } else if (i >= 10 && i < 46) {
                if (!this.a(itemstack1, 1, 10, false)) {
                    if (i < 37) {
                        if (!this.a(itemstack1, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.a(itemstack1, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.a(itemstack1, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.d();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.a(entityhuman, itemstack1);
            if (i == 0) {
                entityhuman.drop(itemstack1, false);
            }
        }

        return itemstack;
    }

    @Override
    public boolean a(ItemStack itemstack, Slot slot) {
        return slot.container != this.resultSlots && super.a(itemstack, slot);
    }

    @Override
    public int m() {
        return 0;
    }

    @Override
    public int n() {
        return this.craftSlots.g();
    }

    @Override
    public int o() {
        return this.craftSlots.f();
    }

    @Override
    public int p() {
        return 10;
    }

    @Override
    public RecipeBookType t() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean d(int i) {
        return i != this.m();
    }
}

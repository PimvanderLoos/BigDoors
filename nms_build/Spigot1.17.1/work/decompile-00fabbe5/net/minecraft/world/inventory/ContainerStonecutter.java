package net.minecraft.world.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeStonecutting;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;

public class ContainerStonecutter extends Container {

    public static final int INPUT_SLOT = 0;
    public static final int RESULT_SLOT = 1;
    private static final int INV_SLOT_START = 2;
    private static final int INV_SLOT_END = 29;
    private static final int USE_ROW_SLOT_START = 29;
    private static final int USE_ROW_SLOT_END = 38;
    private final ContainerAccess access;
    private final ContainerProperty selectedRecipeIndex;
    private final World level;
    private List<RecipeStonecutting> recipes;
    private ItemStack input;
    long lastSoundTime;
    final Slot inputSlot;
    final Slot resultSlot;
    Runnable slotUpdateListener;
    public final IInventory container;
    final InventoryCraftResult resultContainer;

    public ContainerStonecutter(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.NULL);
    }

    public ContainerStonecutter(int i, PlayerInventory playerinventory, final ContainerAccess containeraccess) {
        super(Containers.STONECUTTER, i);
        this.selectedRecipeIndex = ContainerProperty.a();
        this.recipes = Lists.newArrayList();
        this.input = ItemStack.EMPTY;
        this.slotUpdateListener = () -> {
        };
        this.container = new InventorySubcontainer(1) {
            @Override
            public void update() {
                super.update();
                ContainerStonecutter.this.a((IInventory) this);
                ContainerStonecutter.this.slotUpdateListener.run();
            }
        };
        this.resultContainer = new InventoryCraftResult();
        this.access = containeraccess;
        this.level = playerinventory.player.level;
        this.inputSlot = this.a(new Slot(this.container, 0, 20, 33));
        this.resultSlot = this.a(new Slot(this.resultContainer, 1, 143, 33) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return false;
            }

            @Override
            public void a(EntityHuman entityhuman, ItemStack itemstack) {
                itemstack.a(entityhuman.level, entityhuman, itemstack.getCount());
                ContainerStonecutter.this.resultContainer.awardUsedRecipes(entityhuman);
                ItemStack itemstack1 = ContainerStonecutter.this.inputSlot.a(1);

                if (!itemstack1.isEmpty()) {
                    ContainerStonecutter.this.p();
                }

                containeraccess.a((world, blockposition) -> {
                    long j = world.getTime();

                    if (ContainerStonecutter.this.lastSoundTime != j) {
                        world.playSound((EntityHuman) null, blockposition, SoundEffects.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        ContainerStonecutter.this.lastSoundTime = j;
                    }

                });
                super.a(entityhuman, itemstack);
            }
        });

        int j;

        for (j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.a(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.a(new Slot(playerinventory, j, 8 + j * 18, 142));
        }

        this.a(this.selectedRecipeIndex);
    }

    public int l() {
        return this.selectedRecipeIndex.get();
    }

    public List<RecipeStonecutting> m() {
        return this.recipes;
    }

    public int n() {
        return this.recipes.size();
    }

    public boolean o() {
        return this.inputSlot.hasItem() && !this.recipes.isEmpty();
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return a(this.access, entityhuman, Blocks.STONECUTTER);
    }

    @Override
    public boolean a(EntityHuman entityhuman, int i) {
        if (this.d(i)) {
            this.selectedRecipeIndex.set(i);
            this.p();
        }

        return true;
    }

    private boolean d(int i) {
        return i >= 0 && i < this.recipes.size();
    }

    @Override
    public void a(IInventory iinventory) {
        ItemStack itemstack = this.inputSlot.getItem();

        if (!itemstack.a(this.input.getItem())) {
            this.input = itemstack.cloneItemStack();
            this.a(iinventory, itemstack);
        }

    }

    private void a(IInventory iinventory, ItemStack itemstack) {
        this.recipes.clear();
        this.selectedRecipeIndex.set(-1);
        this.resultSlot.set(ItemStack.EMPTY);
        if (!itemstack.isEmpty()) {
            this.recipes = this.level.getCraftingManager().b(Recipes.STONECUTTING, iinventory, this.level);
        }

    }

    void p() {
        if (!this.recipes.isEmpty() && this.d(this.selectedRecipeIndex.get())) {
            RecipeStonecutting recipestonecutting = (RecipeStonecutting) this.recipes.get(this.selectedRecipeIndex.get());

            this.resultContainer.setRecipeUsed(recipestonecutting);
            this.resultSlot.set(recipestonecutting.a(this.container));
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }

        this.d();
    }

    @Override
    public Containers<?> getType() {
        return Containers.STONECUTTER;
    }

    public void a(Runnable runnable) {
        this.slotUpdateListener = runnable;
    }

    @Override
    public boolean a(ItemStack itemstack, Slot slot) {
        return slot.container != this.resultContainer && super.a(itemstack, slot);
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 1) {
                item.b(itemstack1, entityhuman.level, entityhuman);
                if (!this.a(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.a(itemstack1, itemstack);
            } else if (i == 0) {
                if (!this.a(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.level.getCraftingManager().craft(Recipes.STONECUTTING, new InventorySubcontainer(new ItemStack[]{itemstack1}), this.level).isPresent()) {
                if (!this.a(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i >= 2 && i < 29) {
                if (!this.a(itemstack1, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i >= 29 && i < 38 && !this.a(itemstack1, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            slot.d();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.a(entityhuman, itemstack1);
            this.d();
        }

        return itemstack;
    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.resultContainer.splitWithoutUpdate(1);
        this.access.a((world, blockposition) -> {
            this.a(entityhuman, this.container);
        });
    }
}

package net.minecraft.world.inventory;

import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemWorldMap;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.maps.WorldMap;

public class ContainerCartography extends Container {

    public static final int MAP_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    private final ContainerAccess access;
    long lastSoundTime;
    public final IInventory container;
    private final InventoryCraftResult resultContainer;

    public ContainerCartography(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.NULL);
    }

    public ContainerCartography(int i, PlayerInventory playerinventory, final ContainerAccess containeraccess) {
        super(Containers.CARTOGRAPHY_TABLE, i);
        this.container = new InventorySubcontainer(2) {
            @Override
            public void setChanged() {
                ContainerCartography.this.slotsChanged(this);
                super.setChanged();
            }
        };
        this.resultContainer = new InventoryCraftResult() {
            @Override
            public void setChanged() {
                ContainerCartography.this.slotsChanged(this);
                super.setChanged();
            }
        };
        this.access = containeraccess;
        this.addSlot(new Slot(this.container, 0, 15, 15) {
            @Override
            public boolean mayPlace(ItemStack itemstack) {
                return itemstack.is(Items.FILLED_MAP);
            }
        });
        this.addSlot(new Slot(this.container, 1, 15, 52) {
            @Override
            public boolean mayPlace(ItemStack itemstack) {
                return itemstack.is(Items.PAPER) || itemstack.is(Items.MAP) || itemstack.is(Items.GLASS_PANE);
            }
        });
        this.addSlot(new Slot(this.resultContainer, 2, 145, 39) {
            @Override
            public boolean mayPlace(ItemStack itemstack) {
                return false;
            }

            @Override
            public void onTake(EntityHuman entityhuman, ItemStack itemstack) {
                ((Slot) ContainerCartography.this.slots.get(0)).remove(1);
                ((Slot) ContainerCartography.this.slots.get(1)).remove(1);
                itemstack.getItem().onCraftedBy(itemstack, entityhuman.level, entityhuman);
                containeraccess.execute((world, blockposition) -> {
                    long j = world.getGameTime();

                    if (ContainerCartography.this.lastSoundTime != j) {
                        world.playSound((EntityHuman) null, blockposition, SoundEffects.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        ContainerCartography.this.lastSoundTime = j;
                    }

                });
                super.onTake(entityhuman, itemstack);
            }
        });

        int j;

        for (j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerinventory, j, 8 + j * 18, 142));
        }

    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return stillValid(this.access, entityhuman, Blocks.CARTOGRAPHY_TABLE);
    }

    @Override
    public void slotsChanged(IInventory iinventory) {
        ItemStack itemstack = this.container.getItem(0);
        ItemStack itemstack1 = this.container.getItem(1);
        ItemStack itemstack2 = this.resultContainer.getItem(2);

        if (!itemstack2.isEmpty() && (itemstack.isEmpty() || itemstack1.isEmpty())) {
            this.resultContainer.removeItemNoUpdate(2);
        } else if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
            this.setupResultSlot(itemstack, itemstack1, itemstack2);
        }

    }

    private void setupResultSlot(ItemStack itemstack, ItemStack itemstack1, ItemStack itemstack2) {
        this.access.execute((world, blockposition) -> {
            WorldMap worldmap = ItemWorldMap.getSavedData(itemstack, world);

            if (worldmap != null) {
                ItemStack itemstack3;

                if (itemstack1.is(Items.PAPER) && !worldmap.locked && worldmap.scale < 4) {
                    itemstack3 = itemstack.copy();
                    itemstack3.setCount(1);
                    itemstack3.getOrCreateTag().putInt("map_scale_direction", 1);
                    this.broadcastChanges();
                } else if (itemstack1.is(Items.GLASS_PANE) && !worldmap.locked) {
                    itemstack3 = itemstack.copy();
                    itemstack3.setCount(1);
                    itemstack3.getOrCreateTag().putBoolean("map_to_lock", true);
                    this.broadcastChanges();
                } else {
                    if (!itemstack1.is(Items.MAP)) {
                        this.resultContainer.removeItemNoUpdate(2);
                        this.broadcastChanges();
                        return;
                    }

                    itemstack3 = itemstack.copy();
                    itemstack3.setCount(2);
                    this.broadcastChanges();
                }

                if (!ItemStack.matches(itemstack3, itemstack2)) {
                    this.resultContainer.setItem(2, itemstack3);
                    this.broadcastChanges();
                }

            }
        });
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemstack, Slot slot) {
        return slot.container != this.resultContainer && super.canTakeItemForPickAll(itemstack, slot);
    }

    @Override
    public ItemStack quickMoveStack(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.copy();
            if (i == 2) {
                itemstack1.getItem().onCraftedBy(itemstack1, entityhuman.level, entityhuman);
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (i != 1 && i != 0) {
                if (itemstack1.is(Items.FILLED_MAP)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!itemstack1.is(Items.PAPER) && !itemstack1.is(Items.MAP) && !itemstack1.is(Items.GLASS_PANE)) {
                    if (i >= 3 && i < 30) {
                        if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (i >= 30 && i < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(entityhuman, itemstack1);
            this.broadcastChanges();
        }

        return itemstack;
    }

    @Override
    public void removed(EntityHuman entityhuman) {
        super.removed(entityhuman);
        this.resultContainer.removeItemNoUpdate(2);
        this.access.execute((world, blockposition) -> {
            this.clearContainer(entityhuman, this.container);
        });
    }
}

package net.minecraft.world.inventory;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemBanner;
import net.minecraft.world.item.ItemBannerPattern;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemDye;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import net.minecraft.world.level.block.entity.TileEntityTypes;

public class ContainerLoom extends Container {

    private static final int INV_SLOT_START = 4;
    private static final int INV_SLOT_END = 31;
    private static final int USE_ROW_SLOT_START = 31;
    private static final int USE_ROW_SLOT_END = 40;
    private final ContainerAccess access;
    final ContainerProperty selectedBannerPatternIndex;
    Runnable slotUpdateListener;
    final Slot bannerSlot;
    final Slot dyeSlot;
    private final Slot patternSlot;
    private final Slot resultSlot;
    long lastSoundTime;
    private final IInventory inputContainer;
    private final IInventory outputContainer;

    public ContainerLoom(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.NULL);
    }

    public ContainerLoom(int i, PlayerInventory playerinventory, final ContainerAccess containeraccess) {
        super(Containers.LOOM, i);
        this.selectedBannerPatternIndex = ContainerProperty.standalone();
        this.slotUpdateListener = () -> {
        };
        this.inputContainer = new InventorySubcontainer(3) {
            @Override
            public void setChanged() {
                super.setChanged();
                ContainerLoom.this.slotsChanged(this);
                ContainerLoom.this.slotUpdateListener.run();
            }
        };
        this.outputContainer = new InventorySubcontainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                ContainerLoom.this.slotUpdateListener.run();
            }
        };
        this.access = containeraccess;
        this.bannerSlot = this.addSlot(new Slot(this.inputContainer, 0, 13, 26) {
            @Override
            public boolean mayPlace(ItemStack itemstack) {
                return itemstack.getItem() instanceof ItemBanner;
            }
        });
        this.dyeSlot = this.addSlot(new Slot(this.inputContainer, 1, 33, 26) {
            @Override
            public boolean mayPlace(ItemStack itemstack) {
                return itemstack.getItem() instanceof ItemDye;
            }
        });
        this.patternSlot = this.addSlot(new Slot(this.inputContainer, 2, 23, 45) {
            @Override
            public boolean mayPlace(ItemStack itemstack) {
                return itemstack.getItem() instanceof ItemBannerPattern;
            }
        });
        this.resultSlot = this.addSlot(new Slot(this.outputContainer, 0, 143, 58) {
            @Override
            public boolean mayPlace(ItemStack itemstack) {
                return false;
            }

            @Override
            public void onTake(EntityHuman entityhuman, ItemStack itemstack) {
                ContainerLoom.this.bannerSlot.remove(1);
                ContainerLoom.this.dyeSlot.remove(1);
                if (!ContainerLoom.this.bannerSlot.hasItem() || !ContainerLoom.this.dyeSlot.hasItem()) {
                    ContainerLoom.this.selectedBannerPatternIndex.set(0);
                }

                containeraccess.execute((world, blockposition) -> {
                    long j = world.getGameTime();

                    if (ContainerLoom.this.lastSoundTime != j) {
                        world.playSound((EntityHuman) null, blockposition, SoundEffects.UI_LOOM_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        ContainerLoom.this.lastSoundTime = j;
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

        this.addDataSlot(this.selectedBannerPatternIndex);
    }

    public int getSelectedBannerPatternIndex() {
        return this.selectedBannerPatternIndex.get();
    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return stillValid(this.access, entityhuman, Blocks.LOOM);
    }

    @Override
    public boolean clickMenuButton(EntityHuman entityhuman, int i) {
        if (i > 0 && i <= EnumBannerPatternType.AVAILABLE_PATTERNS) {
            this.selectedBannerPatternIndex.set(i);
            this.setupResultSlot();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void slotsChanged(IInventory iinventory) {
        ItemStack itemstack = this.bannerSlot.getItem();
        ItemStack itemstack1 = this.dyeSlot.getItem();
        ItemStack itemstack2 = this.patternSlot.getItem();
        ItemStack itemstack3 = this.resultSlot.getItem();

        if (!itemstack3.isEmpty() && (itemstack.isEmpty() || itemstack1.isEmpty() || this.selectedBannerPatternIndex.get() <= 0 || this.selectedBannerPatternIndex.get() >= EnumBannerPatternType.COUNT - EnumBannerPatternType.PATTERN_ITEM_COUNT && itemstack2.isEmpty())) {
            this.resultSlot.set(ItemStack.EMPTY);
            this.selectedBannerPatternIndex.set(0);
        } else if (!itemstack2.isEmpty() && itemstack2.getItem() instanceof ItemBannerPattern) {
            NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);
            boolean flag = nbttagcompound != null && nbttagcompound.contains("Patterns", 9) && !itemstack.isEmpty() && nbttagcompound.getList("Patterns", 10).size() >= 6;

            if (flag) {
                this.selectedBannerPatternIndex.set(0);
            } else {
                this.selectedBannerPatternIndex.set(((ItemBannerPattern) itemstack2.getItem()).getBannerPattern().ordinal());
            }
        }

        this.setupResultSlot();
        this.broadcastChanges();
    }

    public void registerUpdateListener(Runnable runnable) {
        this.slotUpdateListener = runnable;
    }

    @Override
    public ItemStack quickMoveStack(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.copy();
            if (i == this.resultSlot.index) {
                if (!this.moveItemStackTo(itemstack1, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (i != this.dyeSlot.index && i != this.bannerSlot.index && i != this.patternSlot.index) {
                if (itemstack1.getItem() instanceof ItemBanner) {
                    if (!this.moveItemStackTo(itemstack1, this.bannerSlot.index, this.bannerSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.getItem() instanceof ItemDye) {
                    if (!this.moveItemStackTo(itemstack1, this.dyeSlot.index, this.dyeSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.getItem() instanceof ItemBannerPattern) {
                    if (!this.moveItemStackTo(itemstack1, this.patternSlot.index, this.patternSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 4 && i < 31) {
                    if (!this.moveItemStackTo(itemstack1, 31, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 31 && i < 40 && !this.moveItemStackTo(itemstack1, 4, 31, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 4, 40, false)) {
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
        }

        return itemstack;
    }

    @Override
    public void removed(EntityHuman entityhuman) {
        super.removed(entityhuman);
        this.access.execute((world, blockposition) -> {
            this.clearContainer(entityhuman, this.inputContainer);
        });
    }

    private void setupResultSlot() {
        if (this.selectedBannerPatternIndex.get() > 0) {
            ItemStack itemstack = this.bannerSlot.getItem();
            ItemStack itemstack1 = this.dyeSlot.getItem();
            ItemStack itemstack2 = ItemStack.EMPTY;

            if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
                itemstack2 = itemstack.copy();
                itemstack2.setCount(1);
                EnumBannerPatternType enumbannerpatterntype = EnumBannerPatternType.values()[this.selectedBannerPatternIndex.get()];
                EnumColor enumcolor = ((ItemDye) itemstack1.getItem()).getDyeColor();
                NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack2);
                NBTTagList nbttaglist;

                if (nbttagcompound != null && nbttagcompound.contains("Patterns", 9)) {
                    nbttaglist = nbttagcompound.getList("Patterns", 10);
                } else {
                    nbttaglist = new NBTTagList();
                    if (nbttagcompound == null) {
                        nbttagcompound = new NBTTagCompound();
                    }

                    nbttagcompound.put("Patterns", nbttaglist);
                }

                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.putString("Pattern", enumbannerpatterntype.getHashname());
                nbttagcompound1.putInt("Color", enumcolor.getId());
                nbttaglist.add(nbttagcompound1);
                ItemBlock.setBlockEntityData(itemstack2, TileEntityTypes.BANNER, nbttagcompound);
            }

            if (!ItemStack.matches(itemstack2, this.resultSlot.getItem())) {
                this.resultSlot.set(itemstack2);
            }
        }

    }

    public Slot getBannerSlot() {
        return this.bannerSlot;
    }

    public Slot getDyeSlot() {
        return this.dyeSlot;
    }

    public Slot getPatternSlot() {
        return this.patternSlot;
    }

    public Slot getResultSlot() {
        return this.resultSlot;
    }
}

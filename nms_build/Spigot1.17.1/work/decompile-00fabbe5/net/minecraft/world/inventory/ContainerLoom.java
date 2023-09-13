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
import net.minecraft.world.item.ItemDye;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;

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
        this.selectedBannerPatternIndex = ContainerProperty.a();
        this.slotUpdateListener = () -> {
        };
        this.inputContainer = new InventorySubcontainer(3) {
            @Override
            public void update() {
                super.update();
                ContainerLoom.this.a((IInventory) this);
                ContainerLoom.this.slotUpdateListener.run();
            }
        };
        this.outputContainer = new InventorySubcontainer(1) {
            @Override
            public void update() {
                super.update();
                ContainerLoom.this.slotUpdateListener.run();
            }
        };
        this.access = containeraccess;
        this.bannerSlot = this.a(new Slot(this.inputContainer, 0, 13, 26) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return itemstack.getItem() instanceof ItemBanner;
            }
        });
        this.dyeSlot = this.a(new Slot(this.inputContainer, 1, 33, 26) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return itemstack.getItem() instanceof ItemDye;
            }
        });
        this.patternSlot = this.a(new Slot(this.inputContainer, 2, 23, 45) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return itemstack.getItem() instanceof ItemBannerPattern;
            }
        });
        this.resultSlot = this.a(new Slot(this.outputContainer, 0, 143, 58) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return false;
            }

            @Override
            public void a(EntityHuman entityhuman, ItemStack itemstack) {
                ContainerLoom.this.bannerSlot.a(1);
                ContainerLoom.this.dyeSlot.a(1);
                if (!ContainerLoom.this.bannerSlot.hasItem() || !ContainerLoom.this.dyeSlot.hasItem()) {
                    ContainerLoom.this.selectedBannerPatternIndex.set(0);
                }

                containeraccess.a((world, blockposition) -> {
                    long j = world.getTime();

                    if (ContainerLoom.this.lastSoundTime != j) {
                        world.playSound((EntityHuman) null, blockposition, SoundEffects.UI_LOOM_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        ContainerLoom.this.lastSoundTime = j;
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

        this.a(this.selectedBannerPatternIndex);
    }

    public int l() {
        return this.selectedBannerPatternIndex.get();
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return a(this.access, entityhuman, Blocks.LOOM);
    }

    @Override
    public boolean a(EntityHuman entityhuman, int i) {
        if (i > 0 && i <= EnumBannerPatternType.AVAILABLE_PATTERNS) {
            this.selectedBannerPatternIndex.set(i);
            this.q();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void a(IInventory iinventory) {
        ItemStack itemstack = this.bannerSlot.getItem();
        ItemStack itemstack1 = this.dyeSlot.getItem();
        ItemStack itemstack2 = this.patternSlot.getItem();
        ItemStack itemstack3 = this.resultSlot.getItem();

        if (!itemstack3.isEmpty() && (itemstack.isEmpty() || itemstack1.isEmpty() || this.selectedBannerPatternIndex.get() <= 0 || this.selectedBannerPatternIndex.get() >= EnumBannerPatternType.COUNT - EnumBannerPatternType.PATTERN_ITEM_COUNT && itemstack2.isEmpty())) {
            this.resultSlot.set(ItemStack.EMPTY);
            this.selectedBannerPatternIndex.set(0);
        } else if (!itemstack2.isEmpty() && itemstack2.getItem() instanceof ItemBannerPattern) {
            NBTTagCompound nbttagcompound = itemstack.a("BlockEntityTag");
            boolean flag = nbttagcompound.hasKeyOfType("Patterns", 9) && !itemstack.isEmpty() && nbttagcompound.getList("Patterns", 10).size() >= 6;

            if (flag) {
                this.selectedBannerPatternIndex.set(0);
            } else {
                this.selectedBannerPatternIndex.set(((ItemBannerPattern) itemstack2.getItem()).b().ordinal());
            }
        }

        this.q();
        this.d();
    }

    public void a(Runnable runnable) {
        this.slotUpdateListener = runnable;
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == this.resultSlot.index) {
                if (!this.a(itemstack1, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }

                slot.a(itemstack1, itemstack);
            } else if (i != this.dyeSlot.index && i != this.bannerSlot.index && i != this.patternSlot.index) {
                if (itemstack1.getItem() instanceof ItemBanner) {
                    if (!this.a(itemstack1, this.bannerSlot.index, this.bannerSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.getItem() instanceof ItemDye) {
                    if (!this.a(itemstack1, this.dyeSlot.index, this.dyeSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.getItem() instanceof ItemBannerPattern) {
                    if (!this.a(itemstack1, this.patternSlot.index, this.patternSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 4 && i < 31) {
                    if (!this.a(itemstack1, 31, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 31 && i < 40 && !this.a(itemstack1, 4, 31, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.a(itemstack1, 4, 40, false)) {
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
        }

        return itemstack;
    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.access.a((world, blockposition) -> {
            this.a(entityhuman, this.inputContainer);
        });
    }

    private void q() {
        if (this.selectedBannerPatternIndex.get() > 0) {
            ItemStack itemstack = this.bannerSlot.getItem();
            ItemStack itemstack1 = this.dyeSlot.getItem();
            ItemStack itemstack2 = ItemStack.EMPTY;

            if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
                itemstack2 = itemstack.cloneItemStack();
                itemstack2.setCount(1);
                EnumBannerPatternType enumbannerpatterntype = EnumBannerPatternType.values()[this.selectedBannerPatternIndex.get()];
                EnumColor enumcolor = ((ItemDye) itemstack1.getItem()).d();
                NBTTagCompound nbttagcompound = itemstack2.a("BlockEntityTag");
                NBTTagList nbttaglist;

                if (nbttagcompound.hasKeyOfType("Patterns", 9)) {
                    nbttaglist = nbttagcompound.getList("Patterns", 10);
                } else {
                    nbttaglist = new NBTTagList();
                    nbttagcompound.set("Patterns", nbttaglist);
                }

                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.setString("Pattern", enumbannerpatterntype.b());
                nbttagcompound1.setInt("Color", enumcolor.getColorIndex());
                nbttaglist.add(nbttagcompound1);
            }

            if (!ItemStack.matches(itemstack2, this.resultSlot.getItem())) {
                this.resultSlot.set(itemstack2);
            }
        }

    }

    public Slot m() {
        return this.bannerSlot;
    }

    public Slot n() {
        return this.dyeSlot;
    }

    public Slot o() {
        return this.patternSlot;
    }

    public Slot p() {
        return this.resultSlot;
    }
}

package net.minecraft.world.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.player.AutoRecipeStackManager;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.enchantment.EnchantmentManager;

public class ContainerPlayer extends ContainerRecipeBook<InventoryCrafting> {

    public static final int CONTAINER_ID = 0;
    public static final int RESULT_SLOT = 0;
    public static final int CRAFT_SLOT_START = 1;
    public static final int CRAFT_SLOT_END = 5;
    public static final int ARMOR_SLOT_START = 5;
    public static final int ARMOR_SLOT_END = 9;
    public static final int INV_SLOT_START = 9;
    public static final int INV_SLOT_END = 36;
    public static final int USE_ROW_SLOT_START = 36;
    public static final int USE_ROW_SLOT_END = 45;
    public static final int SHIELD_SLOT = 45;
    public static final MinecraftKey BLOCK_ATLAS = new MinecraftKey("textures/atlas/blocks.png");
    public static final MinecraftKey EMPTY_ARMOR_SLOT_HELMET = new MinecraftKey("item/empty_armor_slot_helmet");
    public static final MinecraftKey EMPTY_ARMOR_SLOT_CHESTPLATE = new MinecraftKey("item/empty_armor_slot_chestplate");
    public static final MinecraftKey EMPTY_ARMOR_SLOT_LEGGINGS = new MinecraftKey("item/empty_armor_slot_leggings");
    public static final MinecraftKey EMPTY_ARMOR_SLOT_BOOTS = new MinecraftKey("item/empty_armor_slot_boots");
    public static final MinecraftKey EMPTY_ARMOR_SLOT_SHIELD = new MinecraftKey("item/empty_armor_slot_shield");
    static final MinecraftKey[] TEXTURE_EMPTY_SLOTS = new MinecraftKey[]{ContainerPlayer.EMPTY_ARMOR_SLOT_BOOTS, ContainerPlayer.EMPTY_ARMOR_SLOT_LEGGINGS, ContainerPlayer.EMPTY_ARMOR_SLOT_CHESTPLATE, ContainerPlayer.EMPTY_ARMOR_SLOT_HELMET};
    private static final EnumItemSlot[] SLOT_IDS = new EnumItemSlot[]{EnumItemSlot.HEAD, EnumItemSlot.CHEST, EnumItemSlot.LEGS, EnumItemSlot.FEET};
    private final InventoryCrafting craftSlots = new InventoryCrafting(this, 2, 2);
    private final InventoryCraftResult resultSlots = new InventoryCraftResult();
    public final boolean active;
    private final EntityHuman owner;

    public ContainerPlayer(PlayerInventory playerinventory, boolean flag, EntityHuman entityhuman) {
        super((Containers) null, 0);
        this.active = flag;
        this.owner = entityhuman;
        this.a((Slot) (new SlotResult(playerinventory.player, this.craftSlots, this.resultSlots, 0, 154, 28)));

        int i;
        int j;

        for (i = 0; i < 2; ++i) {
            for (j = 0; j < 2; ++j) {
                this.a(new Slot(this.craftSlots, j + i * 2, 98 + j * 18, 18 + i * 18));
            }
        }

        for (i = 0; i < 4; ++i) {
            final EnumItemSlot enumitemslot = ContainerPlayer.SLOT_IDS[i];

            this.a(new Slot(playerinventory, 39 - i, 8, 8 + i * 18) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean isAllowed(ItemStack itemstack) {
                    return enumitemslot == EntityInsentient.getEquipmentSlotForItem(itemstack);
                }

                @Override
                public boolean isAllowed(EntityHuman entityhuman1) {
                    ItemStack itemstack = this.getItem();

                    return !itemstack.isEmpty() && !entityhuman1.isCreative() && EnchantmentManager.d(itemstack) ? false : super.isAllowed(entityhuman1);
                }

                @Override
                public Pair<MinecraftKey, MinecraftKey> c() {
                    return Pair.of(ContainerPlayer.BLOCK_ATLAS, ContainerPlayer.TEXTURE_EMPTY_SLOTS[enumitemslot.b()]);
                }
            });
        }

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.a(new Slot(playerinventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.a(new Slot(playerinventory, i, 8 + i * 18, 142));
        }

        this.a(new Slot(playerinventory, 40, 77, 62) {
            @Override
            public Pair<MinecraftKey, MinecraftKey> c() {
                return Pair.of(ContainerPlayer.BLOCK_ATLAS, ContainerPlayer.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
    }

    public static boolean e(int i) {
        return i >= 36 && i < 45 || i == 45;
    }

    @Override
    public void a(AutoRecipeStackManager autorecipestackmanager) {
        this.craftSlots.a(autorecipestackmanager);
    }

    @Override
    public void l() {
        this.resultSlots.clear();
        this.craftSlots.clear();
    }

    @Override
    public boolean a(IRecipe<? super InventoryCrafting> irecipe) {
        return irecipe.a(this.craftSlots, this.owner.level);
    }

    @Override
    public void a(IInventory iinventory) {
        ContainerWorkbench.a(this, this.owner.level, this.owner, this.craftSlots, this.resultSlots);
    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.resultSlots.clear();
        if (!entityhuman.level.isClientSide) {
            this.a(entityhuman, (IInventory) this.craftSlots);
        }
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return true;
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);

            if (i == 0) {
                if (!this.a(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }

                slot.a(itemstack1, itemstack);
            } else if (i >= 1 && i < 5) {
                if (!this.a(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i >= 5 && i < 9) {
                if (!this.a(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (enumitemslot.a() == EnumItemSlot.Function.ARMOR && !((Slot) this.slots.get(8 - enumitemslot.b())).hasItem()) {
                int j = 8 - enumitemslot.b();

                if (!this.a(itemstack1, j, j + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (enumitemslot == EnumItemSlot.OFFHAND && !((Slot) this.slots.get(45)).hasItem()) {
                if (!this.a(itemstack1, 45, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i >= 9 && i < 36) {
                if (!this.a(itemstack1, 36, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i >= 36 && i < 45) {
                if (!this.a(itemstack1, 9, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.a(itemstack1, 9, 45, false)) {
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
        return 5;
    }

    public InventoryCrafting q() {
        return this.craftSlots;
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

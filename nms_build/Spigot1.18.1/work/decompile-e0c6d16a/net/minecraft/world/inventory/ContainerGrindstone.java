package net.minecraft.world.inventory;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3D;

public class ContainerGrindstone extends Container {

    public static final int MAX_NAME_LENGTH = 35;
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    private final IInventory resultSlots;
    final IInventory repairSlots;
    private final ContainerAccess access;

    public ContainerGrindstone(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.NULL);
    }

    public ContainerGrindstone(int i, PlayerInventory playerinventory, final ContainerAccess containeraccess) {
        super(Containers.GRINDSTONE, i);
        this.resultSlots = new InventoryCraftResult();
        this.repairSlots = new InventorySubcontainer(2) {
            @Override
            public void setChanged() {
                super.setChanged();
                ContainerGrindstone.this.slotsChanged(this);
            }
        };
        this.access = containeraccess;
        this.addSlot(new Slot(this.repairSlots, 0, 49, 19) {
            @Override
            public boolean mayPlace(ItemStack itemstack) {
                return itemstack.isDamageableItem() || itemstack.is(Items.ENCHANTED_BOOK) || itemstack.isEnchanted();
            }
        });
        this.addSlot(new Slot(this.repairSlots, 1, 49, 40) {
            @Override
            public boolean mayPlace(ItemStack itemstack) {
                return itemstack.isDamageableItem() || itemstack.is(Items.ENCHANTED_BOOK) || itemstack.isEnchanted();
            }
        });
        this.addSlot(new Slot(this.resultSlots, 2, 129, 34) {
            @Override
            public boolean mayPlace(ItemStack itemstack) {
                return false;
            }

            @Override
            public void onTake(EntityHuman entityhuman, ItemStack itemstack) {
                containeraccess.execute((world, blockposition) -> {
                    if (world instanceof WorldServer) {
                        EntityExperienceOrb.award((WorldServer) world, Vec3D.atCenterOf(blockposition), this.getExperienceAmount(world));
                    }

                    world.levelEvent(1042, blockposition, 0);
                });
                ContainerGrindstone.this.repairSlots.setItem(0, ItemStack.EMPTY);
                ContainerGrindstone.this.repairSlots.setItem(1, ItemStack.EMPTY);
            }

            private int getExperienceAmount(World world) {
                byte b0 = 0;
                int j = b0 + this.getExperienceFromItem(ContainerGrindstone.this.repairSlots.getItem(0));

                j += this.getExperienceFromItem(ContainerGrindstone.this.repairSlots.getItem(1));
                if (j > 0) {
                    int k = (int) Math.ceil((double) j / 2.0D);

                    return k + world.random.nextInt(k);
                } else {
                    return 0;
                }
            }

            private int getExperienceFromItem(ItemStack itemstack) {
                int j = 0;
                Map<Enchantment, Integer> map = EnchantmentManager.getEnchantments(itemstack);
                Iterator iterator = map.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<Enchantment, Integer> entry = (Entry) iterator.next();
                    Enchantment enchantment = (Enchantment) entry.getKey();
                    Integer integer = (Integer) entry.getValue();

                    if (!enchantment.isCurse()) {
                        j += enchantment.getMinCost(integer);
                    }
                }

                return j;
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
    public void slotsChanged(IInventory iinventory) {
        super.slotsChanged(iinventory);
        if (iinventory == this.repairSlots) {
            this.createResult();
        }

    }

    private void createResult() {
        ItemStack itemstack = this.repairSlots.getItem(0);
        ItemStack itemstack1 = this.repairSlots.getItem(1);
        boolean flag = !itemstack.isEmpty() || !itemstack1.isEmpty();
        boolean flag1 = !itemstack.isEmpty() && !itemstack1.isEmpty();

        if (flag) {
            boolean flag2 = !itemstack.isEmpty() && !itemstack.is(Items.ENCHANTED_BOOK) && !itemstack.isEnchanted() || !itemstack1.isEmpty() && !itemstack1.is(Items.ENCHANTED_BOOK) && !itemstack1.isEnchanted();

            if (itemstack.getCount() > 1 || itemstack1.getCount() > 1 || !flag1 && flag2) {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
                this.broadcastChanges();
                return;
            }

            byte b0 = 1;
            int i;
            ItemStack itemstack2;

            if (flag1) {
                if (!itemstack.is(itemstack1.getItem())) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.broadcastChanges();
                    return;
                }

                Item item = itemstack.getItem();
                int j = item.getMaxDamage() - itemstack.getDamageValue();
                int k = item.getMaxDamage() - itemstack1.getDamageValue();
                int l = j + k + item.getMaxDamage() * 5 / 100;

                i = Math.max(item.getMaxDamage() - l, 0);
                itemstack2 = this.mergeEnchants(itemstack, itemstack1);
                if (!itemstack2.isDamageableItem()) {
                    if (!ItemStack.matches(itemstack, itemstack1)) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.broadcastChanges();
                        return;
                    }

                    b0 = 2;
                }
            } else {
                boolean flag3 = !itemstack.isEmpty();

                i = flag3 ? itemstack.getDamageValue() : itemstack1.getDamageValue();
                itemstack2 = flag3 ? itemstack : itemstack1;
            }

            this.resultSlots.setItem(0, this.removeNonCurses(itemstack2, i, b0));
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    private ItemStack mergeEnchants(ItemStack itemstack, ItemStack itemstack1) {
        ItemStack itemstack2 = itemstack.copy();
        Map<Enchantment, Integer> map = EnchantmentManager.getEnchantments(itemstack1);
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<Enchantment, Integer> entry = (Entry) iterator.next();
            Enchantment enchantment = (Enchantment) entry.getKey();

            if (!enchantment.isCurse() || EnchantmentManager.getItemEnchantmentLevel(enchantment, itemstack2) == 0) {
                itemstack2.enchant(enchantment, (Integer) entry.getValue());
            }
        }

        return itemstack2;
    }

    private ItemStack removeNonCurses(ItemStack itemstack, int i, int j) {
        ItemStack itemstack1 = itemstack.copy();

        itemstack1.removeTagKey("Enchantments");
        itemstack1.removeTagKey("StoredEnchantments");
        if (i > 0) {
            itemstack1.setDamageValue(i);
        } else {
            itemstack1.removeTagKey("Damage");
        }

        itemstack1.setCount(j);
        Map<Enchantment, Integer> map = (Map) EnchantmentManager.getEnchantments(itemstack).entrySet().stream().filter((entry) -> {
            return ((Enchantment) entry.getKey()).isCurse();
        }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        EnchantmentManager.setEnchantments(map, itemstack1);
        itemstack1.setRepairCost(0);
        if (itemstack1.is(Items.ENCHANTED_BOOK) && map.size() == 0) {
            itemstack1 = new ItemStack(Items.BOOK);
            if (itemstack.hasCustomHoverName()) {
                itemstack1.setHoverName(itemstack.getHoverName());
            }
        }

        for (int k = 0; k < map.size(); ++k) {
            itemstack1.setRepairCost(ContainerAnvil.calculateIncreasedRepairCost(itemstack1.getBaseRepairCost()));
        }

        return itemstack1;
    }

    @Override
    public void removed(EntityHuman entityhuman) {
        super.removed(entityhuman);
        this.access.execute((world, blockposition) -> {
            this.clearContainer(entityhuman, this.repairSlots);
        });
    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return stillValid(this.access, entityhuman, Blocks.GRINDSTONE);
    }

    @Override
    public ItemStack quickMoveStack(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.copy();
            ItemStack itemstack2 = this.repairSlots.getItem(0);
            ItemStack itemstack3 = this.repairSlots.getItem(1);

            if (i == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (i != 0 && i != 1) {
                if (!itemstack2.isEmpty() && !itemstack3.isEmpty()) {
                    if (i >= 3 && i < 30) {
                        if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (i >= 30 && i < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
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
}

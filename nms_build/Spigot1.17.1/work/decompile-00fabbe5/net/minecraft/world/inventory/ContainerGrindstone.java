package net.minecraft.world.inventory;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.core.BaseBlockPosition;
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
            public void update() {
                super.update();
                ContainerGrindstone.this.a((IInventory) this);
            }
        };
        this.access = containeraccess;
        this.a(new Slot(this.repairSlots, 0, 49, 19) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return itemstack.f() || itemstack.a(Items.ENCHANTED_BOOK) || itemstack.hasEnchantments();
            }
        });
        this.a(new Slot(this.repairSlots, 1, 49, 40) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return itemstack.f() || itemstack.a(Items.ENCHANTED_BOOK) || itemstack.hasEnchantments();
            }
        });
        this.a(new Slot(this.resultSlots, 2, 129, 34) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return false;
            }

            @Override
            public void a(EntityHuman entityhuman, ItemStack itemstack) {
                containeraccess.a((world, blockposition) -> {
                    if (world instanceof WorldServer) {
                        EntityExperienceOrb.a((WorldServer) world, Vec3D.a((BaseBlockPosition) blockposition), this.a(world));
                    }

                    world.triggerEffect(1042, blockposition, 0);
                });
                ContainerGrindstone.this.repairSlots.setItem(0, ItemStack.EMPTY);
                ContainerGrindstone.this.repairSlots.setItem(1, ItemStack.EMPTY);
            }

            private int a(World world) {
                byte b0 = 0;
                int j = b0 + this.f(ContainerGrindstone.this.repairSlots.getItem(0));

                j += this.f(ContainerGrindstone.this.repairSlots.getItem(1));
                if (j > 0) {
                    int k = (int) Math.ceil((double) j / 2.0D);

                    return k + world.random.nextInt(k);
                } else {
                    return 0;
                }
            }

            private int f(ItemStack itemstack) {
                int j = 0;
                Map<Enchantment, Integer> map = EnchantmentManager.a(itemstack);
                Iterator iterator = map.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<Enchantment, Integer> entry = (Entry) iterator.next();
                    Enchantment enchantment = (Enchantment) entry.getKey();
                    Integer integer = (Integer) entry.getValue();

                    if (!enchantment.c()) {
                        j += enchantment.a(integer);
                    }
                }

                return j;
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

    }

    @Override
    public void a(IInventory iinventory) {
        super.a(iinventory);
        if (iinventory == this.repairSlots) {
            this.l();
        }

    }

    private void l() {
        ItemStack itemstack = this.repairSlots.getItem(0);
        ItemStack itemstack1 = this.repairSlots.getItem(1);
        boolean flag = !itemstack.isEmpty() || !itemstack1.isEmpty();
        boolean flag1 = !itemstack.isEmpty() && !itemstack1.isEmpty();

        if (flag) {
            boolean flag2 = !itemstack.isEmpty() && !itemstack.a(Items.ENCHANTED_BOOK) && !itemstack.hasEnchantments() || !itemstack1.isEmpty() && !itemstack1.a(Items.ENCHANTED_BOOK) && !itemstack1.hasEnchantments();

            if (itemstack.getCount() > 1 || itemstack1.getCount() > 1 || !flag1 && flag2) {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
                this.d();
                return;
            }

            byte b0 = 1;
            int i;
            ItemStack itemstack2;

            if (flag1) {
                if (!itemstack.a(itemstack1.getItem())) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.d();
                    return;
                }

                Item item = itemstack.getItem();
                int j = item.getMaxDurability() - itemstack.getDamage();
                int k = item.getMaxDurability() - itemstack1.getDamage();
                int l = j + k + item.getMaxDurability() * 5 / 100;

                i = Math.max(item.getMaxDurability() - l, 0);
                itemstack2 = this.a(itemstack, itemstack1);
                if (!itemstack2.f()) {
                    if (!ItemStack.matches(itemstack, itemstack1)) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.d();
                        return;
                    }

                    b0 = 2;
                }
            } else {
                boolean flag3 = !itemstack.isEmpty();

                i = flag3 ? itemstack.getDamage() : itemstack1.getDamage();
                itemstack2 = flag3 ? itemstack : itemstack1;
            }

            this.resultSlots.setItem(0, this.a(itemstack2, i, b0));
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        }

        this.d();
    }

    private ItemStack a(ItemStack itemstack, ItemStack itemstack1) {
        ItemStack itemstack2 = itemstack.cloneItemStack();
        Map<Enchantment, Integer> map = EnchantmentManager.a(itemstack1);
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<Enchantment, Integer> entry = (Entry) iterator.next();
            Enchantment enchantment = (Enchantment) entry.getKey();

            if (!enchantment.c() || EnchantmentManager.getEnchantmentLevel(enchantment, itemstack2) == 0) {
                itemstack2.addEnchantment(enchantment, (Integer) entry.getValue());
            }
        }

        return itemstack2;
    }

    private ItemStack a(ItemStack itemstack, int i, int j) {
        ItemStack itemstack1 = itemstack.cloneItemStack();

        itemstack1.removeTag("Enchantments");
        itemstack1.removeTag("StoredEnchantments");
        if (i > 0) {
            itemstack1.setDamage(i);
        } else {
            itemstack1.removeTag("Damage");
        }

        itemstack1.setCount(j);
        Map<Enchantment, Integer> map = (Map) EnchantmentManager.a(itemstack).entrySet().stream().filter((entry) -> {
            return ((Enchantment) entry.getKey()).c();
        }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        EnchantmentManager.a(map, itemstack1);
        itemstack1.setRepairCost(0);
        if (itemstack1.a(Items.ENCHANTED_BOOK) && map.size() == 0) {
            itemstack1 = new ItemStack(Items.BOOK);
            if (itemstack.hasName()) {
                itemstack1.a(itemstack.getName());
            }
        }

        for (int k = 0; k < map.size(); ++k) {
            itemstack1.setRepairCost(ContainerAnvil.d(itemstack1.getRepairCost()));
        }

        return itemstack1;
    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.access.a((world, blockposition) -> {
            this.a(entityhuman, this.repairSlots);
        });
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return a(this.access, entityhuman, Blocks.GRINDSTONE);
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            ItemStack itemstack2 = this.repairSlots.getItem(0);
            ItemStack itemstack3 = this.repairSlots.getItem(1);

            if (i == 2) {
                if (!this.a(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.a(itemstack1, itemstack);
            } else if (i != 0 && i != 1) {
                if (!itemstack2.isEmpty() && !itemstack3.isEmpty()) {
                    if (i >= 3 && i < 30) {
                        if (!this.a(itemstack1, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (i >= 30 && i < 39 && !this.a(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.a(itemstack1, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.a(itemstack1, 3, 39, false)) {
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
}

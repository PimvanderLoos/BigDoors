package net.minecraft.world.inventory;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemEnchantedBook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.block.BlockAnvil;
import net.minecraft.world.level.block.state.IBlockData;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerAnvil extends ContainerAnvilAbstract {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean DEBUG_COST = false;
    public static final int MAX_NAME_LENGTH = 50;
    private int repairItemCountCost;
    public String itemName;
    public final ContainerProperty cost;
    private static final int COST_FAIL = 0;
    private static final int COST_BASE = 1;
    private static final int COST_ADDED_BASE = 1;
    private static final int COST_REPAIR_MATERIAL = 1;
    private static final int COST_REPAIR_SACRIFICE = 2;
    private static final int COST_INCOMPATIBLE_PENALTY = 1;
    private static final int COST_RENAME = 1;

    public ContainerAnvil(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.NULL);
    }

    public ContainerAnvil(int i, PlayerInventory playerinventory, ContainerAccess containeraccess) {
        super(Containers.ANVIL, i, playerinventory, containeraccess);
        this.cost = ContainerProperty.a();
        this.a(this.cost);
    }

    @Override
    protected boolean a(IBlockData iblockdata) {
        return iblockdata.a((Tag) TagsBlock.ANVIL);
    }

    @Override
    protected boolean a(EntityHuman entityhuman, boolean flag) {
        return (entityhuman.getAbilities().instabuild || entityhuman.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
    }

    @Override
    protected void a(EntityHuman entityhuman, ItemStack itemstack) {
        if (!entityhuman.getAbilities().instabuild) {
            entityhuman.levelDown(-this.cost.get());
        }

        this.inputSlots.setItem(0, ItemStack.EMPTY);
        if (this.repairItemCountCost > 0) {
            ItemStack itemstack1 = this.inputSlots.getItem(1);

            if (!itemstack1.isEmpty() && itemstack1.getCount() > this.repairItemCountCost) {
                itemstack1.subtract(this.repairItemCountCost);
                this.inputSlots.setItem(1, itemstack1);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }

        this.cost.set(0);
        this.access.a((world, blockposition) -> {
            IBlockData iblockdata = world.getType(blockposition);

            if (!entityhuman.getAbilities().instabuild && iblockdata.a((Tag) TagsBlock.ANVIL) && entityhuman.getRandom().nextFloat() < 0.12F) {
                IBlockData iblockdata1 = BlockAnvil.e(iblockdata);

                if (iblockdata1 == null) {
                    world.a(blockposition, false);
                    world.triggerEffect(1029, blockposition, 0);
                } else {
                    world.setTypeAndData(blockposition, iblockdata1, 2);
                    world.triggerEffect(1030, blockposition, 0);
                }
            } else {
                world.triggerEffect(1030, blockposition, 0);
            }

        });
    }

    @Override
    public void l() {
        ItemStack itemstack = this.inputSlots.getItem(0);

        this.cost.set(1);
        int i = 0;
        byte b0 = 0;
        byte b1 = 0;

        if (itemstack.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
        } else {
            ItemStack itemstack1 = itemstack.cloneItemStack();
            ItemStack itemstack2 = this.inputSlots.getItem(1);
            Map<Enchantment, Integer> map = EnchantmentManager.a(itemstack1);
            int j = b0 + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());

            this.repairItemCountCost = 0;
            if (!itemstack2.isEmpty()) {
                boolean flag = itemstack2.a(Items.ENCHANTED_BOOK) && !ItemEnchantedBook.d(itemstack2).isEmpty();
                int k;
                int l;
                int i1;

                if (itemstack1.f() && itemstack1.getItem().a(itemstack, itemstack2)) {
                    k = Math.min(itemstack1.getDamage(), itemstack1.i() / 4);
                    if (k <= 0) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    for (i1 = 0; k > 0 && i1 < itemstack2.getCount(); ++i1) {
                        l = itemstack1.getDamage() - k;
                        itemstack1.setDamage(l);
                        ++i;
                        k = Math.min(itemstack1.getDamage(), itemstack1.i() / 4);
                    }

                    this.repairItemCountCost = i1;
                } else {
                    if (!flag && (!itemstack1.a(itemstack2.getItem()) || !itemstack1.f())) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    if (itemstack1.f() && !flag) {
                        k = itemstack.i() - itemstack.getDamage();
                        i1 = itemstack2.i() - itemstack2.getDamage();
                        l = i1 + itemstack1.i() * 12 / 100;
                        int j1 = k + l;
                        int k1 = itemstack1.i() - j1;

                        if (k1 < 0) {
                            k1 = 0;
                        }

                        if (k1 < itemstack1.getDamage()) {
                            itemstack1.setDamage(k1);
                            i += 2;
                        }
                    }

                    Map<Enchantment, Integer> map1 = EnchantmentManager.a(itemstack2);
                    boolean flag1 = false;
                    boolean flag2 = false;
                    Iterator iterator = map1.keySet().iterator();

                    while (iterator.hasNext()) {
                        Enchantment enchantment = (Enchantment) iterator.next();

                        if (enchantment != null) {
                            int l1 = (Integer) map.getOrDefault(enchantment, 0);
                            int i2 = (Integer) map1.get(enchantment);

                            i2 = l1 == i2 ? i2 + 1 : Math.max(i2, l1);
                            boolean flag3 = enchantment.canEnchant(itemstack);

                            if (this.player.getAbilities().instabuild || itemstack.a(Items.ENCHANTED_BOOK)) {
                                flag3 = true;
                            }

                            Iterator iterator1 = map.keySet().iterator();

                            while (iterator1.hasNext()) {
                                Enchantment enchantment1 = (Enchantment) iterator1.next();

                                if (enchantment1 != enchantment && !enchantment.isCompatible(enchantment1)) {
                                    flag3 = false;
                                    ++i;
                                }
                            }

                            if (!flag3) {
                                flag2 = true;
                            } else {
                                flag1 = true;
                                if (i2 > enchantment.getMaxLevel()) {
                                    i2 = enchantment.getMaxLevel();
                                }

                                map.put(enchantment, i2);
                                int j2 = 0;

                                switch (enchantment.d()) {
                                    case COMMON:
                                        j2 = 1;
                                        break;
                                    case UNCOMMON:
                                        j2 = 2;
                                        break;
                                    case RARE:
                                        j2 = 4;
                                        break;
                                    case VERY_RARE:
                                        j2 = 8;
                                }

                                if (flag) {
                                    j2 = Math.max(1, j2 / 2);
                                }

                                i += j2 * i2;
                                if (itemstack.getCount() > 1) {
                                    i = 40;
                                }
                            }
                        }
                    }

                    if (flag2 && !flag1) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }
                }
            }

            if (StringUtils.isBlank(this.itemName)) {
                if (itemstack.hasName()) {
                    b1 = 1;
                    i += b1;
                    itemstack1.w();
                }
            } else if (!this.itemName.equals(itemstack.getName().getString())) {
                b1 = 1;
                i += b1;
                itemstack1.a((IChatBaseComponent) (new ChatComponentText(this.itemName)));
            }

            this.cost.set(j + i);
            if (i <= 0) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (b1 == i && b1 > 0 && this.cost.get() >= 40) {
                this.cost.set(39);
            }

            if (this.cost.get() >= 40 && !this.player.getAbilities().instabuild) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (!itemstack1.isEmpty()) {
                int k2 = itemstack1.getRepairCost();

                if (!itemstack2.isEmpty() && k2 < itemstack2.getRepairCost()) {
                    k2 = itemstack2.getRepairCost();
                }

                if (b1 != i || b1 == 0) {
                    k2 = d(k2);
                }

                itemstack1.setRepairCost(k2);
                EnchantmentManager.a(map, itemstack1);
            }

            this.resultSlots.setItem(0, itemstack1);
            this.d();
        }
    }

    public static int d(int i) {
        return i * 2 + 1;
    }

    public void a(String s) {
        this.itemName = s;
        if (this.getSlot(2).hasItem()) {
            ItemStack itemstack = this.getSlot(2).getItem();

            if (StringUtils.isBlank(s)) {
                itemstack.w();
            } else {
                itemstack.a((IChatBaseComponent) (new ChatComponentText(this.itemName)));
            }
        }

        this.l();
    }

    public int m() {
        return this.cost.get();
    }
}

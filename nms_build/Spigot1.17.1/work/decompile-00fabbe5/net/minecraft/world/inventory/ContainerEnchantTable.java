package net.minecraft.world.inventory;

import java.util.List;
import java.util.Random;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemEnchantedBook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.WeightedRandomEnchant;
import net.minecraft.world.level.block.Blocks;

public class ContainerEnchantTable extends Container {

    private final IInventory enchantSlots;
    private final ContainerAccess access;
    private final Random random;
    private final ContainerProperty enchantmentSeed;
    public final int[] costs;
    public final int[] enchantClue;
    public final int[] levelClue;

    public ContainerEnchantTable(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.NULL);
    }

    public ContainerEnchantTable(int i, PlayerInventory playerinventory, ContainerAccess containeraccess) {
        super(Containers.ENCHANTMENT, i);
        this.enchantSlots = new InventorySubcontainer(2) {
            @Override
            public void update() {
                super.update();
                ContainerEnchantTable.this.a((IInventory) this);
            }
        };
        this.random = new Random();
        this.enchantmentSeed = ContainerProperty.a();
        this.costs = new int[3];
        this.enchantClue = new int[]{-1, -1, -1};
        this.levelClue = new int[]{-1, -1, -1};
        this.access = containeraccess;
        this.a(new Slot(this.enchantSlots, 0, 15, 47) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return true;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        this.a(new Slot(this.enchantSlots, 1, 35, 47) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return itemstack.a(Items.LAPIS_LAZULI);
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

        this.a(ContainerProperty.a(this.costs, 0));
        this.a(ContainerProperty.a(this.costs, 1));
        this.a(ContainerProperty.a(this.costs, 2));
        this.a(this.enchantmentSeed).set(playerinventory.player.fr());
        this.a(ContainerProperty.a(this.enchantClue, 0));
        this.a(ContainerProperty.a(this.enchantClue, 1));
        this.a(ContainerProperty.a(this.enchantClue, 2));
        this.a(ContainerProperty.a(this.levelClue, 0));
        this.a(ContainerProperty.a(this.levelClue, 1));
        this.a(ContainerProperty.a(this.levelClue, 2));
    }

    @Override
    public void a(IInventory iinventory) {
        if (iinventory == this.enchantSlots) {
            ItemStack itemstack = iinventory.getItem(0);

            if (!itemstack.isEmpty() && itemstack.canEnchant()) {
                this.access.a((world, blockposition) -> {
                    int i = 0;

                    int j;

                    for (j = -1; j <= 1; ++j) {
                        for (int k = -1; k <= 1; ++k) {
                            if ((j != 0 || k != 0) && world.isEmpty(blockposition.c(k, 0, j)) && world.isEmpty(blockposition.c(k, 1, j))) {
                                if (world.getType(blockposition.c(k * 2, 0, j * 2)).a(Blocks.BOOKSHELF)) {
                                    ++i;
                                }

                                if (world.getType(blockposition.c(k * 2, 1, j * 2)).a(Blocks.BOOKSHELF)) {
                                    ++i;
                                }

                                if (k != 0 && j != 0) {
                                    if (world.getType(blockposition.c(k * 2, 0, j)).a(Blocks.BOOKSHELF)) {
                                        ++i;
                                    }

                                    if (world.getType(blockposition.c(k * 2, 1, j)).a(Blocks.BOOKSHELF)) {
                                        ++i;
                                    }

                                    if (world.getType(blockposition.c(k, 0, j * 2)).a(Blocks.BOOKSHELF)) {
                                        ++i;
                                    }

                                    if (world.getType(blockposition.c(k, 1, j * 2)).a(Blocks.BOOKSHELF)) {
                                        ++i;
                                    }
                                }
                            }
                        }
                    }

                    this.random.setSeed((long) this.enchantmentSeed.get());

                    for (j = 0; j < 3; ++j) {
                        this.costs[j] = EnchantmentManager.a(this.random, j, i, itemstack);
                        this.enchantClue[j] = -1;
                        this.levelClue[j] = -1;
                        if (this.costs[j] < j + 1) {
                            this.costs[j] = 0;
                        }
                    }

                    for (j = 0; j < 3; ++j) {
                        if (this.costs[j] > 0) {
                            List<WeightedRandomEnchant> list = this.a(itemstack, j, this.costs[j]);

                            if (list != null && !list.isEmpty()) {
                                WeightedRandomEnchant weightedrandomenchant = (WeightedRandomEnchant) list.get(this.random.nextInt(list.size()));

                                this.enchantClue[j] = IRegistry.ENCHANTMENT.getId(weightedrandomenchant.enchantment);
                                this.levelClue[j] = weightedrandomenchant.level;
                            }
                        }
                    }

                    this.d();
                });
            } else {
                for (int i = 0; i < 3; ++i) {
                    this.costs[i] = 0;
                    this.enchantClue[i] = -1;
                    this.levelClue[i] = -1;
                }
            }
        }

    }

    @Override
    public boolean a(EntityHuman entityhuman, int i) {
        ItemStack itemstack = this.enchantSlots.getItem(0);
        ItemStack itemstack1 = this.enchantSlots.getItem(1);
        int j = i + 1;

        if ((itemstack1.isEmpty() || itemstack1.getCount() < j) && !entityhuman.getAbilities().instabuild) {
            return false;
        } else if (this.costs[i] > 0 && !itemstack.isEmpty() && (entityhuman.experienceLevel >= j && entityhuman.experienceLevel >= this.costs[i] || entityhuman.getAbilities().instabuild)) {
            this.access.a((world, blockposition) -> {
                ItemStack itemstack2 = itemstack;
                List<WeightedRandomEnchant> list = this.a(itemstack, i, this.costs[i]);

                if (!list.isEmpty()) {
                    entityhuman.enchantDone(itemstack, j);
                    boolean flag = itemstack.a(Items.BOOK);

                    if (flag) {
                        itemstack2 = new ItemStack(Items.ENCHANTED_BOOK);
                        NBTTagCompound nbttagcompound = itemstack.getTag();

                        if (nbttagcompound != null) {
                            itemstack2.setTag(nbttagcompound.clone());
                        }

                        this.enchantSlots.setItem(0, itemstack2);
                    }

                    for (int k = 0; k < list.size(); ++k) {
                        WeightedRandomEnchant weightedrandomenchant = (WeightedRandomEnchant) list.get(k);

                        if (flag) {
                            ItemEnchantedBook.a(itemstack2, weightedrandomenchant);
                        } else {
                            itemstack2.addEnchantment(weightedrandomenchant.enchantment, weightedrandomenchant.level);
                        }
                    }

                    if (!entityhuman.getAbilities().instabuild) {
                        itemstack1.subtract(j);
                        if (itemstack1.isEmpty()) {
                            this.enchantSlots.setItem(1, ItemStack.EMPTY);
                        }
                    }

                    entityhuman.a(StatisticList.ENCHANT_ITEM);
                    if (entityhuman instanceof EntityPlayer) {
                        CriterionTriggers.ENCHANTED_ITEM.a((EntityPlayer) entityhuman, itemstack2, j);
                    }

                    this.enchantSlots.update();
                    this.enchantmentSeed.set(entityhuman.fr());
                    this.a(this.enchantSlots);
                    world.playSound((EntityHuman) null, blockposition, SoundEffects.ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
                }

            });
            return true;
        } else {
            return false;
        }
    }

    private List<WeightedRandomEnchant> a(ItemStack itemstack, int i, int j) {
        this.random.setSeed((long) (this.enchantmentSeed.get() + i));
        List<WeightedRandomEnchant> list = EnchantmentManager.b(this.random, itemstack, j, false);

        if (itemstack.a(Items.BOOK) && list.size() > 1) {
            list.remove(this.random.nextInt(list.size()));
        }

        return list;
    }

    public int l() {
        ItemStack itemstack = this.enchantSlots.getItem(1);

        return itemstack.isEmpty() ? 0 : itemstack.getCount();
    }

    public int m() {
        return this.enchantmentSeed.get();
    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.access.a((world, blockposition) -> {
            this.a(entityhuman, this.enchantSlots);
        });
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return a(this.access, entityhuman, Blocks.ENCHANTING_TABLE);
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 0) {
                if (!this.a(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (i == 1) {
                if (!this.a(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemstack1.a(Items.LAPIS_LAZULI)) {
                if (!this.a(itemstack1, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (((Slot) this.slots.get(0)).hasItem() || !((Slot) this.slots.get(0)).isAllowed(itemstack1)) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemstack2 = itemstack1.cloneItemStack();

                itemstack2.setCount(1);
                itemstack1.subtract(1);
                ((Slot) this.slots.get(0)).set(itemstack2);
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

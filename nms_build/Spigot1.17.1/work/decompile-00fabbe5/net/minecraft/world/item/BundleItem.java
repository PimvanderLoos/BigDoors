package net.minecraft.world.item;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.World;

public class BundleItem extends Item {

    private static final String TAG_ITEMS = "Items";
    public static final int MAX_WEIGHT = 64;
    private static final int BUNDLE_IN_BUNDLE_WEIGHT = 4;
    private static final int BAR_COLOR = MathHelper.f(0.4F, 0.4F, 1.0F);

    public BundleItem(Item.Info item_info) {
        super(item_info);
    }

    public static float d(ItemStack itemstack) {
        return (float) o(itemstack) / 64.0F;
    }

    @Override
    public boolean a(ItemStack itemstack, Slot slot, ClickAction clickaction, EntityHuman entityhuman) {
        if (clickaction != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack itemstack1 = slot.getItem();

            if (itemstack1.isEmpty()) {
                p(itemstack).ifPresent((itemstack2) -> {
                    b(itemstack, slot.e(itemstack2));
                });
            } else if (itemstack1.getItem().P_()) {
                int i = (64 - o(itemstack)) / k(itemstack1);

                b(itemstack, slot.b(itemstack1.getCount(), i, entityhuman));
            }

            return true;
        }
    }

    @Override
    public boolean a(ItemStack itemstack, ItemStack itemstack1, Slot slot, ClickAction clickaction, EntityHuman entityhuman, SlotAccess slotaccess) {
        if (clickaction == ClickAction.SECONDARY && slot.b(entityhuman)) {
            if (itemstack1.isEmpty()) {
                Optional optional = p(itemstack);

                Objects.requireNonNull(slotaccess);
                optional.ifPresent(slotaccess::a);
            } else {
                itemstack1.subtract(b(itemstack, itemstack1));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (a(itemstack, entityhuman)) {
            entityhuman.b(StatisticList.ITEM_USED.b(this));
            return InteractionResultWrapper.a(itemstack, world.isClientSide());
        } else {
            return InteractionResultWrapper.fail(itemstack);
        }
    }

    @Override
    public boolean e(ItemStack itemstack) {
        return o(itemstack) > 0;
    }

    @Override
    public int f(ItemStack itemstack) {
        return Math.min(1 + 12 * o(itemstack) / 64, 13);
    }

    @Override
    public int g(ItemStack itemstack) {
        return BundleItem.BAR_COLOR;
    }

    private static int b(ItemStack itemstack, ItemStack itemstack1) {
        if (!itemstack1.isEmpty() && itemstack1.getItem().P_()) {
            NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

            if (!nbttagcompound.hasKey("Items")) {
                nbttagcompound.set("Items", new NBTTagList());
            }

            int i = o(itemstack);
            int j = k(itemstack1);
            int k = Math.min(itemstack1.getCount(), (64 - i) / j);

            if (k == 0) {
                return 0;
            } else {
                NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);
                Optional<NBTTagCompound> optional = a(itemstack1, nbttaglist);

                if (optional.isPresent()) {
                    NBTTagCompound nbttagcompound1 = (NBTTagCompound) optional.get();
                    ItemStack itemstack2 = ItemStack.a(nbttagcompound1);

                    itemstack2.add(k);
                    itemstack2.save(nbttagcompound1);
                    nbttaglist.remove(nbttagcompound1);
                    nbttaglist.add(0, (NBTBase) nbttagcompound1);
                } else {
                    ItemStack itemstack3 = itemstack1.cloneItemStack();

                    itemstack3.setCount(k);
                    NBTTagCompound nbttagcompound2 = new NBTTagCompound();

                    itemstack3.save(nbttagcompound2);
                    nbttaglist.add(0, (NBTBase) nbttagcompound2);
                }

                return k;
            }
        } else {
            return 0;
        }
    }

    private static Optional<NBTTagCompound> a(ItemStack itemstack, NBTTagList nbttaglist) {
        if (itemstack.a(Items.BUNDLE)) {
            return Optional.empty();
        } else {
            Stream stream = nbttaglist.stream();

            Objects.requireNonNull(NBTTagCompound.class);
            stream = stream.filter(NBTTagCompound.class::isInstance);
            Objects.requireNonNull(NBTTagCompound.class);
            return stream.map(NBTTagCompound.class::cast).filter((nbttagcompound) -> {
                return ItemStack.e(ItemStack.a(nbttagcompound), itemstack);
            }).findFirst();
        }
    }

    private static int k(ItemStack itemstack) {
        if (itemstack.a(Items.BUNDLE)) {
            return 4 + o(itemstack);
        } else {
            if ((itemstack.a(Items.BEEHIVE) || itemstack.a(Items.BEE_NEST)) && itemstack.hasTag()) {
                NBTTagCompound nbttagcompound = itemstack.b("BlockEntityTag");

                if (nbttagcompound != null && !nbttagcompound.getList("Bees", 10).isEmpty()) {
                    return 64;
                }
            }

            return 64 / itemstack.getMaxStackSize();
        }
    }

    private static int o(ItemStack itemstack) {
        return q(itemstack).mapToInt((itemstack1) -> {
            return k(itemstack1) * itemstack1.getCount();
        }).sum();
    }

    private static Optional<ItemStack> p(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

        if (!nbttagcompound.hasKey("Items")) {
            return Optional.empty();
        } else {
            NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

            if (nbttaglist.isEmpty()) {
                return Optional.empty();
            } else {
                boolean flag = false;
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(0);
                ItemStack itemstack1 = ItemStack.a(nbttagcompound1);

                nbttaglist.remove(0);
                if (nbttaglist.isEmpty()) {
                    itemstack.removeTag("Items");
                }

                return Optional.of(itemstack1);
            }
        }
    }

    private static boolean a(ItemStack itemstack, EntityHuman entityhuman) {
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

        if (!nbttagcompound.hasKey("Items")) {
            return false;
        } else {
            if (entityhuman instanceof EntityPlayer) {
                NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                    ItemStack itemstack1 = ItemStack.a(nbttagcompound1);

                    entityhuman.drop(itemstack1, true);
                }
            }

            itemstack.removeTag("Items");
            return true;
        }
    }

    private static Stream<ItemStack> q(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound == null) {
            return Stream.empty();
        } else {
            NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);
            Stream stream = nbttaglist.stream();

            Objects.requireNonNull(NBTTagCompound.class);
            return stream.map(NBTTagCompound.class::cast).map(ItemStack::a);
        }
    }

    @Override
    public Optional<TooltipComponent> h(ItemStack itemstack) {
        NonNullList<ItemStack> nonnulllist = NonNullList.a();
        Stream stream = q(itemstack);

        Objects.requireNonNull(nonnulllist);
        stream.forEach(nonnulllist::add);
        return Optional.of(new BundleTooltip(nonnulllist, o(itemstack)));
    }

    @Override
    public void a(ItemStack itemstack, World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        list.add((new ChatMessage("item.minecraft.bundle.fullness", new Object[]{o(itemstack), 64})).a(EnumChatFormat.GRAY));
    }

    @Override
    public void a(EntityItem entityitem) {
        ItemLiquidUtil.a(entityitem, q(entityitem.getItemStack()));
    }
}

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
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.Entity;
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
    private static final int BAR_COLOR = MathHelper.color(0.4F, 0.4F, 1.0F);

    public BundleItem(Item.Info item_info) {
        super(item_info);
    }

    public static float getFullnessDisplay(ItemStack itemstack) {
        return (float) getContentWeight(itemstack) / 64.0F;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack itemstack, Slot slot, ClickAction clickaction, EntityHuman entityhuman) {
        if (clickaction != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack itemstack1 = slot.getItem();

            if (itemstack1.isEmpty()) {
                this.playRemoveOneSound(entityhuman);
                removeOne(itemstack).ifPresent((itemstack2) -> {
                    add(itemstack, slot.safeInsert(itemstack2));
                });
            } else if (itemstack1.getItem().canFitInsideContainerItems()) {
                int i = (64 - getContentWeight(itemstack)) / getWeight(itemstack1);
                int j = add(itemstack, slot.safeTake(itemstack1.getCount(), i, entityhuman));

                if (j > 0) {
                    this.playInsertSound(entityhuman);
                }
            }

            return true;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack itemstack, ItemStack itemstack1, Slot slot, ClickAction clickaction, EntityHuman entityhuman, SlotAccess slotaccess) {
        if (clickaction == ClickAction.SECONDARY && slot.allowModification(entityhuman)) {
            if (itemstack1.isEmpty()) {
                removeOne(itemstack).ifPresent((itemstack2) -> {
                    this.playRemoveOneSound(entityhuman);
                    slotaccess.set(itemstack2);
                });
            } else {
                int i = add(itemstack, itemstack1);

                if (i > 0) {
                    this.playInsertSound(entityhuman);
                    itemstack1.shrink(i);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (dropContents(itemstack, entityhuman)) {
            this.playDropContentsSound(entityhuman);
            entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
            return InteractionResultWrapper.sidedSuccess(itemstack, world.isClientSide());
        } else {
            return InteractionResultWrapper.fail(itemstack);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack itemstack) {
        return getContentWeight(itemstack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack itemstack) {
        return Math.min(1 + 12 * getContentWeight(itemstack) / 64, 13);
    }

    @Override
    public int getBarColor(ItemStack itemstack) {
        return BundleItem.BAR_COLOR;
    }

    private static int add(ItemStack itemstack, ItemStack itemstack1) {
        if (!itemstack1.isEmpty() && itemstack1.getItem().canFitInsideContainerItems()) {
            NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

            if (!nbttagcompound.contains("Items")) {
                nbttagcompound.put("Items", new NBTTagList());
            }

            int i = getContentWeight(itemstack);
            int j = getWeight(itemstack1);
            int k = Math.min(itemstack1.getCount(), (64 - i) / j);

            if (k == 0) {
                return 0;
            } else {
                NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);
                Optional<NBTTagCompound> optional = getMatchingItem(itemstack1, nbttaglist);

                if (optional.isPresent()) {
                    NBTTagCompound nbttagcompound1 = (NBTTagCompound) optional.get();
                    ItemStack itemstack2 = ItemStack.of(nbttagcompound1);

                    itemstack2.grow(k);
                    itemstack2.save(nbttagcompound1);
                    nbttaglist.remove(nbttagcompound1);
                    nbttaglist.add(0, (NBTBase) nbttagcompound1);
                } else {
                    ItemStack itemstack3 = itemstack1.copy();

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

    private static Optional<NBTTagCompound> getMatchingItem(ItemStack itemstack, NBTTagList nbttaglist) {
        if (itemstack.is(Items.BUNDLE)) {
            return Optional.empty();
        } else {
            Stream stream = nbttaglist.stream();

            Objects.requireNonNull(NBTTagCompound.class);
            stream = stream.filter(NBTTagCompound.class::isInstance);
            Objects.requireNonNull(NBTTagCompound.class);
            return stream.map(NBTTagCompound.class::cast).filter((nbttagcompound) -> {
                return ItemStack.isSameItemSameTags(ItemStack.of(nbttagcompound), itemstack);
            }).findFirst();
        }
    }

    private static int getWeight(ItemStack itemstack) {
        if (itemstack.is(Items.BUNDLE)) {
            return 4 + getContentWeight(itemstack);
        } else {
            if ((itemstack.is(Items.BEEHIVE) || itemstack.is(Items.BEE_NEST)) && itemstack.hasTag()) {
                NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);

                if (nbttagcompound != null && !nbttagcompound.getList("Bees", 10).isEmpty()) {
                    return 64;
                }
            }

            return 64 / itemstack.getMaxStackSize();
        }
    }

    private static int getContentWeight(ItemStack itemstack) {
        return getContents(itemstack).mapToInt((itemstack1) -> {
            return getWeight(itemstack1) * itemstack1.getCount();
        }).sum();
    }

    private static Optional<ItemStack> removeOne(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

        if (!nbttagcompound.contains("Items")) {
            return Optional.empty();
        } else {
            NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

            if (nbttaglist.isEmpty()) {
                return Optional.empty();
            } else {
                boolean flag = false;
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(0);
                ItemStack itemstack1 = ItemStack.of(nbttagcompound1);

                nbttaglist.remove(0);
                if (nbttaglist.isEmpty()) {
                    itemstack.removeTagKey("Items");
                }

                return Optional.of(itemstack1);
            }
        }
    }

    private static boolean dropContents(ItemStack itemstack, EntityHuman entityhuman) {
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

        if (!nbttagcompound.contains("Items")) {
            return false;
        } else {
            if (entityhuman instanceof EntityPlayer) {
                NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                    ItemStack itemstack1 = ItemStack.of(nbttagcompound1);

                    entityhuman.drop(itemstack1, true);
                }
            }

            itemstack.removeTagKey("Items");
            return true;
        }
    }

    private static Stream<ItemStack> getContents(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound == null) {
            return Stream.empty();
        } else {
            NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);
            Stream stream = nbttaglist.stream();

            Objects.requireNonNull(NBTTagCompound.class);
            return stream.map(NBTTagCompound.class::cast).map(ItemStack::of);
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemstack) {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();
        Stream stream = getContents(itemstack);

        Objects.requireNonNull(nonnulllist);
        stream.forEach(nonnulllist::add);
        return Optional.of(new BundleTooltip(nonnulllist, getContentWeight(itemstack)));
    }

    @Override
    public void appendHoverText(ItemStack itemstack, World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        list.add((new ChatMessage("item.minecraft.bundle.fullness", new Object[]{getContentWeight(itemstack), 64})).withStyle(EnumChatFormat.GRAY));
    }

    @Override
    public void onDestroyed(EntityItem entityitem) {
        ItemLiquidUtil.onContainerDestroyed(entityitem, getContents(entityitem.getItem()));
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEffects.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEffects.BUNDLE_INSERT, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEffects.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }
}

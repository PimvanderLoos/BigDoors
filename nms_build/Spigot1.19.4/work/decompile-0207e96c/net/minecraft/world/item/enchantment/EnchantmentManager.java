package net.minecraft.world.item.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom2;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemEnchantedBook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentManager {

    private static final String TAG_ENCH_ID = "id";
    private static final String TAG_ENCH_LEVEL = "lvl";
    private static final float SWIFT_SNEAK_EXTRA_FACTOR = 0.15F;

    public EnchantmentManager() {}

    public static NBTTagCompound storeEnchantment(@Nullable MinecraftKey minecraftkey, int i) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.putString("id", String.valueOf(minecraftkey));
        nbttagcompound.putShort("lvl", (short) i);
        return nbttagcompound;
    }

    public static void setEnchantmentLevel(NBTTagCompound nbttagcompound, int i) {
        nbttagcompound.putShort("lvl", (short) i);
    }

    public static int getEnchantmentLevel(NBTTagCompound nbttagcompound) {
        return MathHelper.clamp(nbttagcompound.getInt("lvl"), 0, 255);
    }

    @Nullable
    public static MinecraftKey getEnchantmentId(NBTTagCompound nbttagcompound) {
        return MinecraftKey.tryParse(nbttagcompound.getString("id"));
    }

    @Nullable
    public static MinecraftKey getEnchantmentId(Enchantment enchantment) {
        return BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
    }

    public static int getItemEnchantmentLevel(Enchantment enchantment, ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return 0;
        } else {
            MinecraftKey minecraftkey = getEnchantmentId(enchantment);
            NBTTagList nbttaglist = itemstack.getEnchantmentTags();

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
                MinecraftKey minecraftkey1 = getEnchantmentId(nbttagcompound);

                if (minecraftkey1 != null && minecraftkey1.equals(minecraftkey)) {
                    return getEnchantmentLevel(nbttagcompound);
                }
            }

            return 0;
        }
    }

    public static Map<Enchantment, Integer> getEnchantments(ItemStack itemstack) {
        NBTTagList nbttaglist = itemstack.is(Items.ENCHANTED_BOOK) ? ItemEnchantedBook.getEnchantments(itemstack) : itemstack.getEnchantmentTags();

        return deserializeEnchantments(nbttaglist);
    }

    public static Map<Enchantment, Integer> deserializeEnchantments(NBTTagList nbttaglist) {
        Map<Enchantment, Integer> map = Maps.newLinkedHashMap();

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);

            BuiltInRegistries.ENCHANTMENT.getOptional(getEnchantmentId(nbttagcompound)).ifPresent((enchantment) -> {
                map.put(enchantment, getEnchantmentLevel(nbttagcompound));
            });
        }

        return map;
    }

    public static void setEnchantments(Map<Enchantment, Integer> map, ItemStack itemstack) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<Enchantment, Integer> entry = (Entry) iterator.next();
            Enchantment enchantment = (Enchantment) entry.getKey();

            if (enchantment != null) {
                int i = (Integer) entry.getValue();

                nbttaglist.add(storeEnchantment(getEnchantmentId(enchantment), i));
                if (itemstack.is(Items.ENCHANTED_BOOK)) {
                    ItemEnchantedBook.addEnchantment(itemstack, new WeightedRandomEnchant(enchantment, i));
                }
            }
        }

        if (nbttaglist.isEmpty()) {
            itemstack.removeTagKey("Enchantments");
        } else if (!itemstack.is(Items.ENCHANTED_BOOK)) {
            itemstack.addTagElement("Enchantments", nbttaglist);
        }

    }

    private static void runIterationOnItem(EnchantmentManager.a enchantmentmanager_a, ItemStack itemstack) {
        if (!itemstack.isEmpty()) {
            NBTTagList nbttaglist = itemstack.getEnchantmentTags();

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);

                BuiltInRegistries.ENCHANTMENT.getOptional(getEnchantmentId(nbttagcompound)).ifPresent((enchantment) -> {
                    enchantmentmanager_a.accept(enchantment, getEnchantmentLevel(nbttagcompound));
                });
            }

        }
    }

    private static void runIterationOnInventory(EnchantmentManager.a enchantmentmanager_a, Iterable<ItemStack> iterable) {
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            runIterationOnItem(enchantmentmanager_a, itemstack);
        }

    }

    public static int getDamageProtection(Iterable<ItemStack> iterable, DamageSource damagesource) {
        MutableInt mutableint = new MutableInt();

        runIterationOnInventory((enchantment, i) -> {
            mutableint.add(enchantment.getDamageProtection(i, damagesource));
        }, iterable);
        return mutableint.intValue();
    }

    public static float getDamageBonus(ItemStack itemstack, EnumMonsterType enummonstertype) {
        MutableFloat mutablefloat = new MutableFloat();

        runIterationOnItem((enchantment, i) -> {
            mutablefloat.add(enchantment.getDamageBonus(i, enummonstertype));
        }, itemstack);
        return mutablefloat.floatValue();
    }

    public static float getSweepingDamageRatio(EntityLiving entityliving) {
        int i = getEnchantmentLevel(Enchantments.SWEEPING_EDGE, entityliving);

        return i > 0 ? EnchantmentSweeping.getSweepingDamageRatio(i) : 0.0F;
    }

    public static void doPostHurtEffects(EntityLiving entityliving, Entity entity) {
        EnchantmentManager.a enchantmentmanager_a = (enchantment, i) -> {
            enchantment.doPostHurt(entityliving, entity, i);
        };

        if (entityliving != null) {
            runIterationOnInventory(enchantmentmanager_a, entityliving.getAllSlots());
        }

        if (entity instanceof EntityHuman) {
            runIterationOnItem(enchantmentmanager_a, entityliving.getMainHandItem());
        }

    }

    public static void doPostDamageEffects(EntityLiving entityliving, Entity entity) {
        EnchantmentManager.a enchantmentmanager_a = (enchantment, i) -> {
            enchantment.doPostAttack(entityliving, entity, i);
        };

        if (entityliving != null) {
            runIterationOnInventory(enchantmentmanager_a, entityliving.getAllSlots());
        }

        if (entityliving instanceof EntityHuman) {
            runIterationOnItem(enchantmentmanager_a, entityliving.getMainHandItem());
        }

    }

    public static int getEnchantmentLevel(Enchantment enchantment, EntityLiving entityliving) {
        Iterable<ItemStack> iterable = enchantment.getSlotItems(entityliving).values();

        if (iterable == null) {
            return 0;
        } else {
            int i = 0;
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                ItemStack itemstack = (ItemStack) iterator.next();
                int j = getItemEnchantmentLevel(enchantment, itemstack);

                if (j > i) {
                    i = j;
                }
            }

            return i;
        }
    }

    public static float getSneakingSpeedBonus(EntityLiving entityliving) {
        return (float) getEnchantmentLevel(Enchantments.SWIFT_SNEAK, entityliving) * 0.15F;
    }

    public static int getKnockbackBonus(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantments.KNOCKBACK, entityliving);
    }

    public static int getFireAspect(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantments.FIRE_ASPECT, entityliving);
    }

    public static int getRespiration(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantments.RESPIRATION, entityliving);
    }

    public static int getDepthStrider(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantments.DEPTH_STRIDER, entityliving);
    }

    public static int getBlockEfficiency(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, entityliving);
    }

    public static int getFishingLuckBonus(ItemStack itemstack) {
        return getItemEnchantmentLevel(Enchantments.FISHING_LUCK, itemstack);
    }

    public static int getFishingSpeedBonus(ItemStack itemstack) {
        return getItemEnchantmentLevel(Enchantments.FISHING_SPEED, itemstack);
    }

    public static int getMobLooting(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantments.MOB_LOOTING, entityliving);
    }

    public static boolean hasAquaAffinity(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantments.AQUA_AFFINITY, entityliving) > 0;
    }

    public static boolean hasFrostWalker(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantments.FROST_WALKER, entityliving) > 0;
    }

    public static boolean hasSoulSpeed(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantments.SOUL_SPEED, entityliving) > 0;
    }

    public static boolean hasBindingCurse(ItemStack itemstack) {
        return getItemEnchantmentLevel(Enchantments.BINDING_CURSE, itemstack) > 0;
    }

    public static boolean hasVanishingCurse(ItemStack itemstack) {
        return getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, itemstack) > 0;
    }

    public static boolean hasSilkTouch(ItemStack itemstack) {
        return getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) > 0;
    }

    public static int getLoyalty(ItemStack itemstack) {
        return getItemEnchantmentLevel(Enchantments.LOYALTY, itemstack);
    }

    public static int getRiptide(ItemStack itemstack) {
        return getItemEnchantmentLevel(Enchantments.RIPTIDE, itemstack);
    }

    public static boolean hasChanneling(ItemStack itemstack) {
        return getItemEnchantmentLevel(Enchantments.CHANNELING, itemstack) > 0;
    }

    @Nullable
    public static Entry<EnumItemSlot, ItemStack> getRandomItemWith(Enchantment enchantment, EntityLiving entityliving) {
        return getRandomItemWith(enchantment, entityliving, (itemstack) -> {
            return true;
        });
    }

    @Nullable
    public static Entry<EnumItemSlot, ItemStack> getRandomItemWith(Enchantment enchantment, EntityLiving entityliving, Predicate<ItemStack> predicate) {
        Map<EnumItemSlot, ItemStack> map = enchantment.getSlotItems(entityliving);

        if (map.isEmpty()) {
            return null;
        } else {
            List<Entry<EnumItemSlot, ItemStack>> list = Lists.newArrayList();
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<EnumItemSlot, ItemStack> entry = (Entry) iterator.next();
                ItemStack itemstack = (ItemStack) entry.getValue();

                if (!itemstack.isEmpty() && getItemEnchantmentLevel(enchantment, itemstack) > 0 && predicate.test(itemstack)) {
                    list.add(entry);
                }
            }

            return list.isEmpty() ? null : (Entry) list.get(entityliving.getRandom().nextInt(list.size()));
        }
    }

    public static int getEnchantmentCost(RandomSource randomsource, int i, int j, ItemStack itemstack) {
        Item item = itemstack.getItem();
        int k = item.getEnchantmentValue();

        if (k <= 0) {
            return 0;
        } else {
            if (j > 15) {
                j = 15;
            }

            int l = randomsource.nextInt(8) + 1 + (j >> 1) + randomsource.nextInt(j + 1);

            return i == 0 ? Math.max(l / 3, 1) : (i == 1 ? l * 2 / 3 + 1 : Math.max(l, j * 2));
        }
    }

    public static ItemStack enchantItem(RandomSource randomsource, ItemStack itemstack, int i, boolean flag) {
        List<WeightedRandomEnchant> list = selectEnchantment(randomsource, itemstack, i, flag);
        boolean flag1 = itemstack.is(Items.BOOK);

        if (flag1) {
            itemstack = new ItemStack(Items.ENCHANTED_BOOK);
        }

        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            WeightedRandomEnchant weightedrandomenchant = (WeightedRandomEnchant) iterator.next();

            if (flag1) {
                ItemEnchantedBook.addEnchantment(itemstack, weightedrandomenchant);
            } else {
                itemstack.enchant(weightedrandomenchant.enchantment, weightedrandomenchant.level);
            }
        }

        return itemstack;
    }

    public static List<WeightedRandomEnchant> selectEnchantment(RandomSource randomsource, ItemStack itemstack, int i, boolean flag) {
        List<WeightedRandomEnchant> list = Lists.newArrayList();
        Item item = itemstack.getItem();
        int j = item.getEnchantmentValue();

        if (j <= 0) {
            return list;
        } else {
            i += 1 + randomsource.nextInt(j / 4 + 1) + randomsource.nextInt(j / 4 + 1);
            float f = (randomsource.nextFloat() + randomsource.nextFloat() - 1.0F) * 0.15F;

            i = MathHelper.clamp(Math.round((float) i + (float) i * f), 1, Integer.MAX_VALUE);
            List<WeightedRandomEnchant> list1 = getAvailableEnchantmentResults(i, itemstack, flag);

            if (!list1.isEmpty()) {
                Optional optional = WeightedRandom2.getRandomItem(randomsource, list1);

                Objects.requireNonNull(list);
                optional.ifPresent(list::add);

                while (randomsource.nextInt(50) <= i) {
                    if (!list.isEmpty()) {
                        filterCompatibleEnchantments(list1, (WeightedRandomEnchant) SystemUtils.lastOf(list));
                    }

                    if (list1.isEmpty()) {
                        break;
                    }

                    optional = WeightedRandom2.getRandomItem(randomsource, list1);
                    Objects.requireNonNull(list);
                    optional.ifPresent(list::add);
                    i /= 2;
                }
            }

            return list;
        }
    }

    public static void filterCompatibleEnchantments(List<WeightedRandomEnchant> list, WeightedRandomEnchant weightedrandomenchant) {
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            if (!weightedrandomenchant.enchantment.isCompatibleWith(((WeightedRandomEnchant) iterator.next()).enchantment)) {
                iterator.remove();
            }
        }

    }

    public static boolean isEnchantmentCompatible(Collection<Enchantment> collection, Enchantment enchantment) {
        Iterator iterator = collection.iterator();

        Enchantment enchantment1;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            enchantment1 = (Enchantment) iterator.next();
        } while (enchantment1.isCompatibleWith(enchantment));

        return false;
    }

    public static List<WeightedRandomEnchant> getAvailableEnchantmentResults(int i, ItemStack itemstack, boolean flag) {
        List<WeightedRandomEnchant> list = Lists.newArrayList();
        Item item = itemstack.getItem();
        boolean flag1 = itemstack.is(Items.BOOK);
        Iterator iterator = BuiltInRegistries.ENCHANTMENT.iterator();

        while (iterator.hasNext()) {
            Enchantment enchantment = (Enchantment) iterator.next();

            if ((!enchantment.isTreasureOnly() || flag) && enchantment.isDiscoverable() && (enchantment.category.canEnchant(item) || flag1)) {
                for (int j = enchantment.getMaxLevel(); j > enchantment.getMinLevel() - 1; --j) {
                    if (i >= enchantment.getMinCost(j) && i <= enchantment.getMaxCost(j)) {
                        list.add(new WeightedRandomEnchant(enchantment, j));
                        break;
                    }
                }
            }
        }

        return list;
    }

    @FunctionalInterface
    private interface a {

        void accept(Enchantment enchantment, int i);
    }
}

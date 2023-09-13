package net.minecraft.world.item.alchemy;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public class PotionUtil {

    public static final String TAG_CUSTOM_POTION_EFFECTS = "CustomPotionEffects";
    public static final String TAG_CUSTOM_POTION_COLOR = "CustomPotionColor";
    public static final String TAG_POTION = "Potion";
    private static final int EMPTY_COLOR = 16253176;
    private static final IChatBaseComponent NO_EFFECT = IChatBaseComponent.translatable("effect.none").withStyle(EnumChatFormat.GRAY);

    public PotionUtil() {}

    public static List<MobEffect> getMobEffects(ItemStack itemstack) {
        return getAllEffects(itemstack.getTag());
    }

    public static List<MobEffect> getAllEffects(PotionRegistry potionregistry, Collection<MobEffect> collection) {
        List<MobEffect> list = Lists.newArrayList();

        list.addAll(potionregistry.getEffects());
        list.addAll(collection);
        return list;
    }

    public static List<MobEffect> getAllEffects(@Nullable NBTTagCompound nbttagcompound) {
        List<MobEffect> list = Lists.newArrayList();

        list.addAll(getPotion(nbttagcompound).getEffects());
        getCustomEffects(nbttagcompound, list);
        return list;
    }

    public static List<MobEffect> getCustomEffects(ItemStack itemstack) {
        return getCustomEffects(itemstack.getTag());
    }

    public static List<MobEffect> getCustomEffects(@Nullable NBTTagCompound nbttagcompound) {
        List<MobEffect> list = Lists.newArrayList();

        getCustomEffects(nbttagcompound, list);
        return list;
    }

    public static void getCustomEffects(@Nullable NBTTagCompound nbttagcompound, List<MobEffect> list) {
        if (nbttagcompound != null && nbttagcompound.contains("CustomPotionEffects", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("CustomPotionEffects", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                MobEffect mobeffect = MobEffect.load(nbttagcompound1);

                if (mobeffect != null) {
                    list.add(mobeffect);
                }
            }
        }

    }

    public static int getColor(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        return nbttagcompound != null && nbttagcompound.contains("CustomPotionColor", 99) ? nbttagcompound.getInt("CustomPotionColor") : (getPotion(itemstack) == Potions.EMPTY ? 16253176 : getColor((Collection) getMobEffects(itemstack)));
    }

    public static int getColor(PotionRegistry potionregistry) {
        return potionregistry == Potions.EMPTY ? 16253176 : getColor((Collection) potionregistry.getEffects());
    }

    public static int getColor(Collection<MobEffect> collection) {
        int i = 3694022;

        if (collection.isEmpty()) {
            return 3694022;
        } else {
            float f = 0.0F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            int j = 0;
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                if (mobeffect.isVisible()) {
                    int k = mobeffect.getEffect().getColor();
                    int l = mobeffect.getAmplifier() + 1;

                    f += (float) (l * (k >> 16 & 255)) / 255.0F;
                    f1 += (float) (l * (k >> 8 & 255)) / 255.0F;
                    f2 += (float) (l * (k >> 0 & 255)) / 255.0F;
                    j += l;
                }
            }

            if (j == 0) {
                return 0;
            } else {
                f = f / (float) j * 255.0F;
                f1 = f1 / (float) j * 255.0F;
                f2 = f2 / (float) j * 255.0F;
                return (int) f << 16 | (int) f1 << 8 | (int) f2;
            }
        }
    }

    public static PotionRegistry getPotion(ItemStack itemstack) {
        return getPotion(itemstack.getTag());
    }

    public static PotionRegistry getPotion(@Nullable NBTTagCompound nbttagcompound) {
        return nbttagcompound == null ? Potions.EMPTY : PotionRegistry.byName(nbttagcompound.getString("Potion"));
    }

    public static ItemStack setPotion(ItemStack itemstack, PotionRegistry potionregistry) {
        MinecraftKey minecraftkey = BuiltInRegistries.POTION.getKey(potionregistry);

        if (potionregistry == Potions.EMPTY) {
            itemstack.removeTagKey("Potion");
        } else {
            itemstack.getOrCreateTag().putString("Potion", minecraftkey.toString());
        }

        return itemstack;
    }

    public static ItemStack setCustomEffects(ItemStack itemstack, Collection<MobEffect> collection) {
        if (collection.isEmpty()) {
            return itemstack;
        } else {
            NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();
            NBTTagList nbttaglist = nbttagcompound.getList("CustomPotionEffects", 9);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                nbttaglist.add(mobeffect.save(new NBTTagCompound()));
            }

            nbttagcompound.put("CustomPotionEffects", nbttaglist);
            return itemstack;
        }
    }

    public static void addPotionTooltip(ItemStack itemstack, List<IChatBaseComponent> list, float f) {
        addPotionTooltip(getMobEffects(itemstack), list, f);
    }

    public static void addPotionTooltip(List<MobEffect> list, List<IChatBaseComponent> list1, float f) {
        List<Pair<AttributeBase, AttributeModifier>> list2 = Lists.newArrayList();
        Iterator iterator;
        IChatMutableComponent ichatmutablecomponent;
        MobEffectList mobeffectlist;

        if (list.isEmpty()) {
            list1.add(PotionUtil.NO_EFFECT);
        } else {
            for (iterator = list.iterator(); iterator.hasNext(); list1.add(ichatmutablecomponent.withStyle(mobeffectlist.getCategory().getTooltipFormatting()))) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                ichatmutablecomponent = IChatBaseComponent.translatable(mobeffect.getDescriptionId());
                mobeffectlist = mobeffect.getEffect();
                Map<AttributeBase, AttributeModifier> map = mobeffectlist.getAttributeModifiers();

                if (!map.isEmpty()) {
                    Iterator iterator1 = map.entrySet().iterator();

                    while (iterator1.hasNext()) {
                        Entry<AttributeBase, AttributeModifier> entry = (Entry) iterator1.next();
                        AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), mobeffectlist.getAttributeModifierValue(mobeffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());

                        list2.add(new Pair((AttributeBase) entry.getKey(), attributemodifier1));
                    }
                }

                if (mobeffect.getAmplifier() > 0) {
                    ichatmutablecomponent = IChatBaseComponent.translatable("potion.withAmplifier", ichatmutablecomponent, IChatBaseComponent.translatable("potion.potency." + mobeffect.getAmplifier()));
                }

                if (!mobeffect.endsWithin(20)) {
                    ichatmutablecomponent = IChatBaseComponent.translatable("potion.withDuration", ichatmutablecomponent, MobEffectUtil.formatDuration(mobeffect, f));
                }
            }
        }

        if (!list2.isEmpty()) {
            list1.add(CommonComponents.EMPTY);
            list1.add(IChatBaseComponent.translatable("potion.whenDrank").withStyle(EnumChatFormat.DARK_PURPLE));
            iterator = list2.iterator();

            while (iterator.hasNext()) {
                Pair<AttributeBase, AttributeModifier> pair = (Pair) iterator.next();
                AttributeModifier attributemodifier2 = (AttributeModifier) pair.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;

                if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    d1 = attributemodifier2.getAmount();
                } else {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D) {
                    list1.add(IChatBaseComponent.translatable("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), IChatBaseComponent.translatable(((AttributeBase) pair.getFirst()).getDescriptionId())).withStyle(EnumChatFormat.BLUE));
                } else if (d0 < 0.0D) {
                    d1 *= -1.0D;
                    list1.add(IChatBaseComponent.translatable("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), IChatBaseComponent.translatable(((AttributeBase) pair.getFirst()).getDescriptionId())).withStyle(EnumChatFormat.RED));
                }
            }
        }

    }
}

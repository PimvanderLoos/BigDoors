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
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
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
    private static final IChatBaseComponent NO_EFFECT = (new ChatMessage("effect.none")).a(EnumChatFormat.GRAY);

    public PotionUtil() {}

    public static List<MobEffect> getEffects(ItemStack itemstack) {
        return a(itemstack.getTag());
    }

    public static List<MobEffect> a(PotionRegistry potionregistry, Collection<MobEffect> collection) {
        List<MobEffect> list = Lists.newArrayList();

        list.addAll(potionregistry.a());
        list.addAll(collection);
        return list;
    }

    public static List<MobEffect> a(@Nullable NBTTagCompound nbttagcompound) {
        List<MobEffect> list = Lists.newArrayList();

        list.addAll(c(nbttagcompound).a());
        a(nbttagcompound, (List) list);
        return list;
    }

    public static List<MobEffect> b(ItemStack itemstack) {
        return b(itemstack.getTag());
    }

    public static List<MobEffect> b(@Nullable NBTTagCompound nbttagcompound) {
        List<MobEffect> list = Lists.newArrayList();

        a(nbttagcompound, (List) list);
        return list;
    }

    public static void a(@Nullable NBTTagCompound nbttagcompound, List<MobEffect> list) {
        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("CustomPotionEffects", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("CustomPotionEffects", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                MobEffect mobeffect = MobEffect.b(nbttagcompound1);

                if (mobeffect != null) {
                    list.add(mobeffect);
                }
            }
        }

    }

    public static int c(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        return nbttagcompound != null && nbttagcompound.hasKeyOfType("CustomPotionColor", 99) ? nbttagcompound.getInt("CustomPotionColor") : (d(itemstack) == Potions.EMPTY ? 16253176 : a((Collection) getEffects(itemstack)));
    }

    public static int a(PotionRegistry potionregistry) {
        return potionregistry == Potions.EMPTY ? 16253176 : a((Collection) potionregistry.a());
    }

    public static int a(Collection<MobEffect> collection) {
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

                if (mobeffect.isShowParticles()) {
                    int k = mobeffect.getMobEffect().getColor();
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

    public static PotionRegistry d(ItemStack itemstack) {
        return c(itemstack.getTag());
    }

    public static PotionRegistry c(@Nullable NBTTagCompound nbttagcompound) {
        return nbttagcompound == null ? Potions.EMPTY : PotionRegistry.a(nbttagcompound.getString("Potion"));
    }

    public static ItemStack a(ItemStack itemstack, PotionRegistry potionregistry) {
        MinecraftKey minecraftkey = IRegistry.POTION.getKey(potionregistry);

        if (potionregistry == Potions.EMPTY) {
            itemstack.removeTag("Potion");
        } else {
            itemstack.getOrCreateTag().setString("Potion", minecraftkey.toString());
        }

        return itemstack;
    }

    public static ItemStack a(ItemStack itemstack, Collection<MobEffect> collection) {
        if (collection.isEmpty()) {
            return itemstack;
        } else {
            NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();
            NBTTagList nbttaglist = nbttagcompound.getList("CustomPotionEffects", 9);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                nbttaglist.add(mobeffect.a(new NBTTagCompound()));
            }

            nbttagcompound.set("CustomPotionEffects", nbttaglist);
            return itemstack;
        }
    }

    public static void a(ItemStack itemstack, List<IChatBaseComponent> list, float f) {
        List<MobEffect> list1 = getEffects(itemstack);
        List<Pair<AttributeBase, AttributeModifier>> list2 = Lists.newArrayList();
        Iterator iterator;
        ChatMessage chatmessage;
        MobEffectList mobeffectlist;

        if (list1.isEmpty()) {
            list.add(PotionUtil.NO_EFFECT);
        } else {
            for (iterator = list1.iterator(); iterator.hasNext(); list.add(chatmessage.a(mobeffectlist.e().a()))) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                chatmessage = new ChatMessage(mobeffect.g());
                mobeffectlist = mobeffect.getMobEffect();
                Map<AttributeBase, AttributeModifier> map = mobeffectlist.g();

                if (!map.isEmpty()) {
                    Iterator iterator1 = map.entrySet().iterator();

                    while (iterator1.hasNext()) {
                        Entry<AttributeBase, AttributeModifier> entry = (Entry) iterator1.next();
                        AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), mobeffectlist.a(mobeffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());

                        list2.add(new Pair((AttributeBase) entry.getKey(), attributemodifier1));
                    }
                }

                if (mobeffect.getAmplifier() > 0) {
                    chatmessage = new ChatMessage("potion.withAmplifier", new Object[]{chatmessage, new ChatMessage("potion.potency." + mobeffect.getAmplifier())});
                }

                if (mobeffect.getDuration() > 20) {
                    chatmessage = new ChatMessage("potion.withDuration", new Object[]{chatmessage, MobEffectUtil.a(mobeffect, f)});
                }
            }
        }

        if (!list2.isEmpty()) {
            list.add(ChatComponentText.EMPTY);
            list.add((new ChatMessage("potion.whenDrank")).a(EnumChatFormat.DARK_PURPLE));
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
                    list.add((new ChatMessage("attribute.modifier.plus." + attributemodifier2.getOperation().a(), new Object[]{ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new ChatMessage(((AttributeBase) pair.getFirst()).getName())})).a(EnumChatFormat.BLUE));
                } else if (d0 < 0.0D) {
                    d1 *= -1.0D;
                    list.add((new ChatMessage("attribute.modifier.take." + attributemodifier2.getOperation().a(), new Object[]{ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new ChatMessage(((AttributeBase) pair.getFirst()).getName())})).a(EnumChatFormat.RED));
                }
            }
        }

    }
}

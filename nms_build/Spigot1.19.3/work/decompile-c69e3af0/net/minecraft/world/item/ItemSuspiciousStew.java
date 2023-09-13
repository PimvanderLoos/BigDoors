package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.level.World;

public class ItemSuspiciousStew extends Item {

    public static final String EFFECTS_TAG = "Effects";
    public static final String EFFECT_ID_TAG = "EffectId";
    public static final String EFFECT_DURATION_TAG = "EffectDuration";
    public static final int DEFAULT_DURATION = 160;

    public ItemSuspiciousStew(Item.Info item_info) {
        super(item_info);
    }

    public static void saveMobEffect(ItemStack itemstack, MobEffectList mobeffectlist, int i) {
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();
        NBTTagList nbttaglist = nbttagcompound.getList("Effects", 9);
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        nbttagcompound1.putInt("EffectId", MobEffectList.getId(mobeffectlist));
        nbttagcompound1.putInt("EffectDuration", i);
        nbttaglist.add(nbttagcompound1);
        nbttagcompound.put("Effects", nbttaglist);
    }

    private static void listPotionEffects(ItemStack itemstack, Consumer<MobEffect> consumer) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null && nbttagcompound.contains("Effects", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Effects", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                int j;

                if (nbttagcompound1.contains("EffectDuration", 3)) {
                    j = nbttagcompound1.getInt("EffectDuration");
                } else {
                    j = 160;
                }

                MobEffectList mobeffectlist = MobEffectList.byId(nbttagcompound1.getInt("EffectId"));

                if (mobeffectlist != null) {
                    consumer.accept(new MobEffect(mobeffectlist, j));
                }
            }
        }

    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        super.appendHoverText(itemstack, world, list, tooltipflag);
        if (tooltipflag.isCreative()) {
            List<MobEffect> list1 = new ArrayList();

            Objects.requireNonNull(list1);
            listPotionEffects(itemstack, list1::add);
            PotionUtil.addPotionTooltip((List) list1, list, 1.0F);
        }

    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, World world, EntityLiving entityliving) {
        ItemStack itemstack1 = super.finishUsingItem(itemstack, world, entityliving);

        Objects.requireNonNull(entityliving);
        listPotionEffects(itemstack1, entityliving::addEffect);
        return entityliving instanceof EntityHuman && ((EntityHuman) entityliving).getAbilities().instabuild ? itemstack1 : new ItemStack(Items.BOWL);
    }
}

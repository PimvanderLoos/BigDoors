package net.minecraft.world.item;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class ItemSuspiciousStew extends Item {

    public ItemSuspiciousStew(Item.Info item_info) {
        super(item_info);
    }

    public static void a(ItemStack itemstack, MobEffectList mobeffectlist, int i) {
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();
        NBTTagList nbttaglist = nbttagcompound.getList("Effects", 9);
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        nbttagcompound1.setByte("EffectId", (byte) MobEffectList.getId(mobeffectlist));
        nbttagcompound1.setInt("EffectDuration", i);
        nbttaglist.add(nbttagcompound1);
        nbttagcompound.set("Effects", nbttaglist);
    }

    @Override
    public ItemStack a(ItemStack itemstack, World world, EntityLiving entityliving) {
        ItemStack itemstack1 = super.a(itemstack, world, entityliving);
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Effects", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Effects", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                int j = 160;
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);

                if (nbttagcompound1.hasKeyOfType("EffectDuration", 3)) {
                    j = nbttagcompound1.getInt("EffectDuration");
                }

                MobEffectList mobeffectlist = MobEffectList.fromId(nbttagcompound1.getByte("EffectId"));

                if (mobeffectlist != null) {
                    entityliving.addEffect(new MobEffect(mobeffectlist, j));
                }
            }
        }

        return entityliving instanceof EntityHuman && ((EntityHuman) entityliving).abilities.canInstantlyBuild ? itemstack1 : new ItemStack(Items.BOWL);
    }
}

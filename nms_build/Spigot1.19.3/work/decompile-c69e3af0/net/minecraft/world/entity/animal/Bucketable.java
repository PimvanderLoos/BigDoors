package net.minecraft.world.entity.animal;

import java.util.Optional;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemLiquidUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public interface Bucketable {

    boolean fromBucket();

    void setFromBucket(boolean flag);

    void saveToBucketTag(ItemStack itemstack);

    void loadFromBucketTag(NBTTagCompound nbttagcompound);

    ItemStack getBucketItemStack();

    SoundEffect getPickupSound();

    /** @deprecated */
    @Deprecated
    static void saveDefaultDataToBucketTag(EntityInsentient entityinsentient, ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

        if (entityinsentient.hasCustomName()) {
            itemstack.setHoverName(entityinsentient.getCustomName());
        }

        if (entityinsentient.isNoAi()) {
            nbttagcompound.putBoolean("NoAI", entityinsentient.isNoAi());
        }

        if (entityinsentient.isSilent()) {
            nbttagcompound.putBoolean("Silent", entityinsentient.isSilent());
        }

        if (entityinsentient.isNoGravity()) {
            nbttagcompound.putBoolean("NoGravity", entityinsentient.isNoGravity());
        }

        if (entityinsentient.hasGlowingTag()) {
            nbttagcompound.putBoolean("Glowing", entityinsentient.hasGlowingTag());
        }

        if (entityinsentient.isInvulnerable()) {
            nbttagcompound.putBoolean("Invulnerable", entityinsentient.isInvulnerable());
        }

        nbttagcompound.putFloat("Health", entityinsentient.getHealth());
    }

    /** @deprecated */
    @Deprecated
    static void loadDefaultDataFromBucketTag(EntityInsentient entityinsentient, NBTTagCompound nbttagcompound) {
        if (nbttagcompound.contains("NoAI")) {
            entityinsentient.setNoAi(nbttagcompound.getBoolean("NoAI"));
        }

        if (nbttagcompound.contains("Silent")) {
            entityinsentient.setSilent(nbttagcompound.getBoolean("Silent"));
        }

        if (nbttagcompound.contains("NoGravity")) {
            entityinsentient.setNoGravity(nbttagcompound.getBoolean("NoGravity"));
        }

        if (nbttagcompound.contains("Glowing")) {
            entityinsentient.setGlowingTag(nbttagcompound.getBoolean("Glowing"));
        }

        if (nbttagcompound.contains("Invulnerable")) {
            entityinsentient.setInvulnerable(nbttagcompound.getBoolean("Invulnerable"));
        }

        if (nbttagcompound.contains("Health", 99)) {
            entityinsentient.setHealth(nbttagcompound.getFloat("Health"));
        }

    }

    static <T extends EntityLiving & Bucketable> Optional<EnumInteractionResult> bucketMobPickup(EntityHuman entityhuman, EnumHand enumhand, T t0) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (itemstack.getItem() == Items.WATER_BUCKET && t0.isAlive()) {
            t0.playSound(((Bucketable) t0).getPickupSound(), 1.0F, 1.0F);
            ItemStack itemstack1 = ((Bucketable) t0).getBucketItemStack();

            ((Bucketable) t0).saveToBucketTag(itemstack1);
            ItemStack itemstack2 = ItemLiquidUtil.createFilledResult(itemstack, entityhuman, itemstack1, false);

            entityhuman.setItemInHand(enumhand, itemstack2);
            World world = t0.level;

            if (!world.isClientSide) {
                CriterionTriggers.FILLED_BUCKET.trigger((EntityPlayer) entityhuman, itemstack1);
            }

            t0.discard();
            return Optional.of(EnumInteractionResult.sidedSuccess(world.isClientSide));
        } else {
            return Optional.empty();
        }
    }
}

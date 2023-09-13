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

    boolean isFromBucket();

    void setFromBucket(boolean flag);

    void setBucketName(ItemStack itemstack);

    void c(NBTTagCompound nbttagcompound);

    ItemStack getBucketItem();

    SoundEffect t();

    @Deprecated
    static void a(EntityInsentient entityinsentient, ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

        if (entityinsentient.hasCustomName()) {
            itemstack.a(entityinsentient.getCustomName());
        }

        if (entityinsentient.isNoAI()) {
            nbttagcompound.setBoolean("NoAI", entityinsentient.isNoAI());
        }

        if (entityinsentient.isSilent()) {
            nbttagcompound.setBoolean("Silent", entityinsentient.isSilent());
        }

        if (entityinsentient.isNoGravity()) {
            nbttagcompound.setBoolean("NoGravity", entityinsentient.isNoGravity());
        }

        if (entityinsentient.hasGlowingTag()) {
            nbttagcompound.setBoolean("Glowing", entityinsentient.hasGlowingTag());
        }

        if (entityinsentient.isInvulnerable()) {
            nbttagcompound.setBoolean("Invulnerable", entityinsentient.isInvulnerable());
        }

        nbttagcompound.setFloat("Health", entityinsentient.getHealth());
    }

    @Deprecated
    static void a(EntityInsentient entityinsentient, NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKey("NoAI")) {
            entityinsentient.setNoAI(nbttagcompound.getBoolean("NoAI"));
        }

        if (nbttagcompound.hasKey("Silent")) {
            entityinsentient.setSilent(nbttagcompound.getBoolean("Silent"));
        }

        if (nbttagcompound.hasKey("NoGravity")) {
            entityinsentient.setNoGravity(nbttagcompound.getBoolean("NoGravity"));
        }

        if (nbttagcompound.hasKey("Glowing")) {
            entityinsentient.setGlowingTag(nbttagcompound.getBoolean("Glowing"));
        }

        if (nbttagcompound.hasKey("Invulnerable")) {
            entityinsentient.setInvulnerable(nbttagcompound.getBoolean("Invulnerable"));
        }

        if (nbttagcompound.hasKeyOfType("Health", 99)) {
            entityinsentient.setHealth(nbttagcompound.getFloat("Health"));
        }

    }

    static <T extends EntityLiving & Bucketable> Optional<EnumInteractionResult> a(EntityHuman entityhuman, EnumHand enumhand, T t0) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.WATER_BUCKET && t0.isAlive()) {
            t0.playSound(((Bucketable) t0).t(), 1.0F, 1.0F);
            ItemStack itemstack1 = ((Bucketable) t0).getBucketItem();

            ((Bucketable) t0).setBucketName(itemstack1);
            ItemStack itemstack2 = ItemLiquidUtil.a(itemstack, entityhuman, itemstack1, false);

            entityhuman.a(enumhand, itemstack2);
            World world = t0.level;

            if (!world.isClientSide) {
                CriterionTriggers.FILLED_BUCKET.a((EntityPlayer) entityhuman, itemstack1);
            }

            t0.die();
            return Optional.of(EnumInteractionResult.a(world.isClientSide));
        } else {
            return Optional.empty();
        }
    }
}

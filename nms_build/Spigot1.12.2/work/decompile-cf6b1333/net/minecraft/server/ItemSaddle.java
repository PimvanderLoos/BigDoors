package net.minecraft.server;

public class ItemSaddle extends Item {

    public ItemSaddle() {
        this.maxStackSize = 1;
        this.b(CreativeModeTab.e);
    }

    public boolean a(ItemStack itemstack, EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        if (entityliving instanceof EntityPig) {
            EntityPig entitypig = (EntityPig) entityliving;

            if (!entitypig.hasSaddle() && !entitypig.isBaby()) {
                entitypig.setSaddle(true);
                entitypig.world.a(entityhuman, entitypig.locX, entitypig.locY, entitypig.locZ, SoundEffects.fr, SoundCategory.NEUTRAL, 0.5F, 1.0F);
                itemstack.subtract(1);
            }

            return true;
        } else {
            return false;
        }
    }
}

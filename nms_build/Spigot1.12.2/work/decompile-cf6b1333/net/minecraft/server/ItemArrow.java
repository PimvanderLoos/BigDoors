package net.minecraft.server;

public class ItemArrow extends Item {

    public ItemArrow() {
        this.b(CreativeModeTab.j);
    }

    public EntityArrow a(World world, ItemStack itemstack, EntityLiving entityliving) {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(world, entityliving);

        entitytippedarrow.a(itemstack);
        return entitytippedarrow;
    }
}

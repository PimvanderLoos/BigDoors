package net.minecraft.server;

import javax.annotation.Nullable;

public class ItemFishBucket extends ItemBucket {

    private final EntityTypes<?> a;

    public ItemFishBucket(EntityTypes<?> entitytypes, FluidType fluidtype, Item.Info item_info) {
        super(fluidtype, item_info);
        this.a = entitytypes;
    }

    public void a(World world, ItemStack itemstack, BlockPosition blockposition) {
        if (!world.isClientSide) {
            this.b(world, itemstack, blockposition);
        }

    }

    protected void a(@Nullable EntityHuman entityhuman, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        generatoraccess.a(entityhuman, blockposition, SoundEffects.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }

    private void b(World world, ItemStack itemstack, BlockPosition blockposition) {
        Entity entity = this.a.a(world, itemstack, (EntityHuman) null, blockposition, true, false);

        if (entity != null) {
            ((EntityFish) entity).setFromBucket(true);
        }

    }
}

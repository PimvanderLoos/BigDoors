package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.animal.EntityFish;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.FluidType;

public class ItemFishBucket extends ItemBucket {

    private final EntityTypes<?> a;

    public ItemFishBucket(EntityTypes<?> entitytypes, FluidType fluidtype, Item.Info item_info) {
        super(fluidtype, item_info);
        this.a = entitytypes;
    }

    @Override
    public void a(World world, ItemStack itemstack, BlockPosition blockposition) {
        if (world instanceof WorldServer) {
            this.a((WorldServer) world, itemstack, blockposition);
        }

    }

    @Override
    protected void a(@Nullable EntityHuman entityhuman, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        generatoraccess.playSound(entityhuman, blockposition, SoundEffects.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }

    private void a(WorldServer worldserver, ItemStack itemstack, BlockPosition blockposition) {
        Entity entity = this.a.spawnCreature(worldserver, itemstack, (EntityHuman) null, blockposition, EnumMobSpawn.BUCKET, true, false);

        if (entity != null) {
            ((EntityFish) entity).setFromBucket(true);
        }

    }
}

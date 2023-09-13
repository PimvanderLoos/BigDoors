package net.minecraft.world.item;

import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityFox;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3D;

public class ItemChorusFruit extends Item {

    public ItemChorusFruit(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, World world, EntityLiving entityliving) {
        ItemStack itemstack1 = super.finishUsingItem(itemstack, world, entityliving);

        if (!world.isClientSide) {
            double d0 = entityliving.getX();
            double d1 = entityliving.getY();
            double d2 = entityliving.getZ();

            for (int i = 0; i < 16; ++i) {
                double d3 = entityliving.getX() + (entityliving.getRandom().nextDouble() - 0.5D) * 16.0D;
                double d4 = MathHelper.clamp(entityliving.getY() + (double) (entityliving.getRandom().nextInt(16) - 8), (double) world.getMinBuildHeight(), (double) (world.getMinBuildHeight() + ((WorldServer) world).getLogicalHeight() - 1));
                double d5 = entityliving.getZ() + (entityliving.getRandom().nextDouble() - 0.5D) * 16.0D;

                if (entityliving.isPassenger()) {
                    entityliving.stopRiding();
                }

                Vec3D vec3d = entityliving.position();

                if (entityliving.randomTeleport(d3, d4, d5, true)) {
                    world.gameEvent(GameEvent.TELEPORT, vec3d, GameEvent.a.of((Entity) entityliving));
                    SoundEffect soundeffect = entityliving instanceof EntityFox ? SoundEffects.FOX_TELEPORT : SoundEffects.CHORUS_FRUIT_TELEPORT;

                    world.playSound((EntityHuman) null, d0, d1, d2, soundeffect, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    entityliving.playSound(soundeffect, 1.0F, 1.0F);
                    break;
                }
            }

            if (entityliving instanceof EntityHuman) {
                ((EntityHuman) entityliving).getCooldowns().addCooldown(this, 20);
            }
        }

        return itemstack1;
    }
}

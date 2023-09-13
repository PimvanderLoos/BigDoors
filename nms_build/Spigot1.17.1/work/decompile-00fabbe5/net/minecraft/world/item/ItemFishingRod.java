package net.minecraft.world.item;

import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityFishingHook;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemFishingRod extends Item implements ItemVanishable {

    public ItemFishingRod(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        int i;

        if (entityhuman.fishing != null) {
            if (!world.isClientSide) {
                i = entityhuman.fishing.a(itemstack);
                itemstack.damage(i, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastItemBreak(enumhand);
                });
            }

            world.playSound((EntityHuman) null, entityhuman.locX(), entityhuman.locY(), entityhuman.locZ(), SoundEffects.FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            world.a((Entity) entityhuman, GameEvent.FISHING_ROD_REEL_IN, (Entity) entityhuman);
        } else {
            world.playSound((EntityHuman) null, entityhuman.locX(), entityhuman.locY(), entityhuman.locZ(), SoundEffects.FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!world.isClientSide) {
                i = EnchantmentManager.c(itemstack);
                int j = EnchantmentManager.b(itemstack);

                world.addEntity(new EntityFishingHook(entityhuman, world, j, i));
            }

            entityhuman.b(StatisticList.ITEM_USED.b(this));
            world.a((Entity) entityhuman, GameEvent.FISHING_ROD_CAST, (Entity) entityhuman);
        }

        return InteractionResultWrapper.a(itemstack, world.isClientSide());
    }

    @Override
    public int c() {
        return 1;
    }
}

package net.minecraft.world.item;

import java.util.function.Predicate;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.World;

public class ItemBow extends ItemProjectileWeapon implements ItemVanishable {

    public static final int MAX_DRAW_DURATION = 20;
    public static final int DEFAULT_RANGE = 15;

    public ItemBow(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public void releaseUsing(ItemStack itemstack, World world, EntityLiving entityliving, int i) {
        if (entityliving instanceof EntityHuman) {
            EntityHuman entityhuman = (EntityHuman) entityliving;
            boolean flag = entityhuman.getAbilities().instabuild || EnchantmentManager.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, itemstack) > 0;
            ItemStack itemstack1 = entityhuman.getProjectile(itemstack);

            if (!itemstack1.isEmpty() || flag) {
                if (itemstack1.isEmpty()) {
                    itemstack1 = new ItemStack(Items.ARROW);
                }

                int j = this.getUseDuration(itemstack) - i;
                float f = getPowerForTime(j);

                if ((double) f >= 0.1D) {
                    boolean flag1 = flag && itemstack1.is(Items.ARROW);

                    if (!world.isClientSide) {
                        ItemArrow itemarrow = (ItemArrow) (itemstack1.getItem() instanceof ItemArrow ? itemstack1.getItem() : Items.ARROW);
                        EntityArrow entityarrow = itemarrow.createArrow(world, itemstack1, entityhuman);

                        entityarrow.shootFromRotation(entityhuman, entityhuman.getXRot(), entityhuman.getYRot(), 0.0F, f * 3.0F, 1.0F);
                        if (f == 1.0F) {
                            entityarrow.setCritArrow(true);
                        }

                        int k = EnchantmentManager.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, itemstack);

                        if (k > 0) {
                            entityarrow.setBaseDamage(entityarrow.getBaseDamage() + (double) k * 0.5D + 0.5D);
                        }

                        int l = EnchantmentManager.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, itemstack);

                        if (l > 0) {
                            entityarrow.setKnockback(l);
                        }

                        if (EnchantmentManager.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, itemstack) > 0) {
                            entityarrow.setSecondsOnFire(100);
                        }

                        itemstack.hurtAndBreak(1, entityhuman, (entityhuman1) -> {
                            entityhuman1.broadcastBreakEvent(entityhuman.getUsedItemHand());
                        });
                        if (flag1 || entityhuman.getAbilities().instabuild && (itemstack1.is(Items.SPECTRAL_ARROW) || itemstack1.is(Items.TIPPED_ARROW))) {
                            entityarrow.pickup = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        }

                        world.addFreshEntity(entityarrow);
                    }

                    world.playSound((EntityHuman) null, entityhuman.getX(), entityhuman.getY(), entityhuman.getZ(), SoundEffects.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !entityhuman.getAbilities().instabuild) {
                        itemstack1.shrink(1);
                        if (itemstack1.isEmpty()) {
                            entityhuman.getInventory().removeItem(itemstack1);
                        }
                    }

                    entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
                }
            }
        }
    }

    public static float getPowerForTime(int i) {
        float f = (float) i / 20.0F;

        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 72000;
    }

    @Override
    public EnumAnimation getUseAnimation(ItemStack itemstack) {
        return EnumAnimation.BOW;
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        boolean flag = !entityhuman.getProjectile(itemstack).isEmpty();

        if (!entityhuman.getAbilities().instabuild && !flag) {
            return InteractionResultWrapper.fail(itemstack);
        } else {
            entityhuman.startUsingItem(enumhand);
            return InteractionResultWrapper.consume(itemstack);
        }
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ItemBow.ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }
}

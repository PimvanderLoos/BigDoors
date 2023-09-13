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
    public void a(ItemStack itemstack, World world, EntityLiving entityliving, int i) {
        if (entityliving instanceof EntityHuman) {
            EntityHuman entityhuman = (EntityHuman) entityliving;
            boolean flag = entityhuman.getAbilities().instabuild || EnchantmentManager.getEnchantmentLevel(Enchantments.INFINITY_ARROWS, itemstack) > 0;
            ItemStack itemstack1 = entityhuman.h(itemstack);

            if (!itemstack1.isEmpty() || flag) {
                if (itemstack1.isEmpty()) {
                    itemstack1 = new ItemStack(Items.ARROW);
                }

                int j = this.b(itemstack) - i;
                float f = a(j);

                if ((double) f >= 0.1D) {
                    boolean flag1 = flag && itemstack1.a(Items.ARROW);

                    if (!world.isClientSide) {
                        ItemArrow itemarrow = (ItemArrow) (itemstack1.getItem() instanceof ItemArrow ? itemstack1.getItem() : Items.ARROW);
                        EntityArrow entityarrow = itemarrow.a(world, itemstack1, (EntityLiving) entityhuman);

                        entityarrow.a(entityhuman, entityhuman.getXRot(), entityhuman.getYRot(), 0.0F, f * 3.0F, 1.0F);
                        if (f == 1.0F) {
                            entityarrow.setCritical(true);
                        }

                        int k = EnchantmentManager.getEnchantmentLevel(Enchantments.POWER_ARROWS, itemstack);

                        if (k > 0) {
                            entityarrow.setDamage(entityarrow.getDamage() + (double) k * 0.5D + 0.5D);
                        }

                        int l = EnchantmentManager.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, itemstack);

                        if (l > 0) {
                            entityarrow.setKnockbackStrength(l);
                        }

                        if (EnchantmentManager.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, itemstack) > 0) {
                            entityarrow.setOnFire(100);
                        }

                        itemstack.damage(1, entityhuman, (entityhuman1) -> {
                            entityhuman1.broadcastItemBreak(entityhuman.getRaisedHand());
                        });
                        if (flag1 || entityhuman.getAbilities().instabuild && (itemstack1.a(Items.SPECTRAL_ARROW) || itemstack1.a(Items.TIPPED_ARROW))) {
                            entityarrow.pickup = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        }

                        world.addEntity(entityarrow);
                    }

                    world.playSound((EntityHuman) null, entityhuman.locX(), entityhuman.locY(), entityhuman.locZ(), SoundEffects.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !entityhuman.getAbilities().instabuild) {
                        itemstack1.subtract(1);
                        if (itemstack1.isEmpty()) {
                            entityhuman.getInventory().g(itemstack1);
                        }
                    }

                    entityhuman.b(StatisticList.ITEM_USED.b(this));
                }
            }
        }
    }

    public static float a(int i) {
        float f = (float) i / 20.0F;

        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public int b(ItemStack itemstack) {
        return 72000;
    }

    @Override
    public EnumAnimation c(ItemStack itemstack) {
        return EnumAnimation.BOW;
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        boolean flag = !entityhuman.h(itemstack).isEmpty();

        if (!entityhuman.getAbilities().instabuild && !flag) {
            return InteractionResultWrapper.fail(itemstack);
        } else {
            entityhuman.c(enumhand);
            return InteractionResultWrapper.consume(itemstack);
        }
    }

    @Override
    public Predicate<ItemStack> b() {
        return ItemBow.ARROW_ONLY;
    }

    @Override
    public int d() {
        return 15;
    }
}

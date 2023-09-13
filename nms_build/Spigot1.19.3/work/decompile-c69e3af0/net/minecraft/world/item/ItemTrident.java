package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityThrownTrident;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class ItemTrident extends Item implements ItemVanishable {

    public static final int THROW_THRESHOLD_TIME = 10;
    public static final float BASE_DAMAGE = 8.0F;
    public static final float SHOOT_POWER = 2.5F;
    private final Multimap<AttributeBase, AttributeModifier> defaultModifiers;

    public ItemTrident(Item.Info item_info) {
        super(item_info);
        Builder<AttributeBase, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(GenericAttributes.ATTACK_DAMAGE, new AttributeModifier(ItemTrident.BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 8.0D, AttributeModifier.Operation.ADDITION));
        builder.put(GenericAttributes.ATTACK_SPEED, new AttributeModifier(ItemTrident.BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.9000000953674316D, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public boolean canAttackBlock(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        return !entityhuman.isCreative();
    }

    @Override
    public EnumAnimation getUseAnimation(ItemStack itemstack) {
        return EnumAnimation.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack itemstack, World world, EntityLiving entityliving, int i) {
        if (entityliving instanceof EntityHuman) {
            EntityHuman entityhuman = (EntityHuman) entityliving;
            int j = this.getUseDuration(itemstack) - i;

            if (j >= 10) {
                int k = EnchantmentManager.getRiptide(itemstack);

                if (k <= 0 || entityhuman.isInWaterOrRain()) {
                    if (!world.isClientSide) {
                        itemstack.hurtAndBreak(1, entityhuman, (entityhuman1) -> {
                            entityhuman1.broadcastBreakEvent(entityliving.getUsedItemHand());
                        });
                        if (k == 0) {
                            EntityThrownTrident entitythrowntrident = new EntityThrownTrident(world, entityhuman, itemstack);

                            entitythrowntrident.shootFromRotation(entityhuman, entityhuman.getXRot(), entityhuman.getYRot(), 0.0F, 2.5F + (float) k * 0.5F, 1.0F);
                            if (entityhuman.getAbilities().instabuild) {
                                entitythrowntrident.pickup = EntityArrow.PickupStatus.CREATIVE_ONLY;
                            }

                            world.addFreshEntity(entitythrowntrident);
                            world.playSound((EntityHuman) null, (Entity) entitythrowntrident, SoundEffects.TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            if (!entityhuman.getAbilities().instabuild) {
                                entityhuman.getInventory().removeItem(itemstack);
                            }
                        }
                    }

                    entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
                    if (k > 0) {
                        float f = entityhuman.getYRot();
                        float f1 = entityhuman.getXRot();
                        float f2 = -MathHelper.sin(f * 0.017453292F) * MathHelper.cos(f1 * 0.017453292F);
                        float f3 = -MathHelper.sin(f1 * 0.017453292F);
                        float f4 = MathHelper.cos(f * 0.017453292F) * MathHelper.cos(f1 * 0.017453292F);
                        float f5 = MathHelper.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
                        float f6 = 3.0F * ((1.0F + (float) k) / 4.0F);

                        f2 *= f6 / f5;
                        f3 *= f6 / f5;
                        f4 *= f6 / f5;
                        entityhuman.push((double) f2, (double) f3, (double) f4);
                        entityhuman.startAutoSpinAttack(20);
                        if (entityhuman.isOnGround()) {
                            float f7 = 1.1999999F;

                            entityhuman.move(EnumMoveType.SELF, new Vec3D(0.0D, 1.1999999284744263D, 0.0D));
                        }

                        SoundEffect soundeffect;

                        if (k >= 3) {
                            soundeffect = SoundEffects.TRIDENT_RIPTIDE_3;
                        } else if (k == 2) {
                            soundeffect = SoundEffects.TRIDENT_RIPTIDE_2;
                        } else {
                            soundeffect = SoundEffects.TRIDENT_RIPTIDE_1;
                        }

                        world.playSound((EntityHuman) null, (Entity) entityhuman, soundeffect, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }

                }
            }
        }
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
            return InteractionResultWrapper.fail(itemstack);
        } else if (EnchantmentManager.getRiptide(itemstack) > 0 && !entityhuman.isInWaterOrRain()) {
            return InteractionResultWrapper.fail(itemstack);
        } else {
            entityhuman.startUsingItem(enumhand);
            return InteractionResultWrapper.consume(itemstack);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
        itemstack.hurtAndBreak(1, entityliving1, (entityliving2) -> {
            entityliving2.broadcastBreakEvent(EnumItemSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack itemstack, World world, IBlockData iblockdata, BlockPosition blockposition, EntityLiving entityliving) {
        if ((double) iblockdata.getDestroySpeed(world, blockposition) != 0.0D) {
            itemstack.hurtAndBreak(2, entityliving, (entityliving1) -> {
                entityliving1.broadcastBreakEvent(EnumItemSlot.MAINHAND);
            });
        }

        return true;
    }

    @Override
    public Multimap<AttributeBase, AttributeModifier> getDefaultAttributeModifiers(EnumItemSlot enumitemslot) {
        return enumitemslot == EnumItemSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(enumitemslot);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}

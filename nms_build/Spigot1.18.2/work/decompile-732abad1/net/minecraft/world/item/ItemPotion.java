package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.IRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemPotion extends Item {

    private static final int DRINK_DURATION = 32;

    public ItemPotion(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return PotionUtil.setPotion(super.getDefaultInstance(), Potions.WATER);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, World world, EntityLiving entityliving) {
        EntityHuman entityhuman = entityliving instanceof EntityHuman ? (EntityHuman) entityliving : null;

        if (entityhuman instanceof EntityPlayer) {
            CriterionTriggers.CONSUME_ITEM.trigger((EntityPlayer) entityhuman, itemstack);
        }

        if (!world.isClientSide) {
            List<MobEffect> list = PotionUtil.getMobEffects(itemstack);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                if (mobeffect.getEffect().isInstantenous()) {
                    mobeffect.getEffect().applyInstantenousEffect(entityhuman, entityhuman, entityliving, mobeffect.getAmplifier(), 1.0D);
                } else {
                    entityliving.addEffect(new MobEffect(mobeffect));
                }
            }
        }

        if (entityhuman != null) {
            entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
            if (!entityhuman.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
        }

        if (entityhuman == null || !entityhuman.getAbilities().instabuild) {
            if (itemstack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (entityhuman != null) {
                entityhuman.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        world.gameEvent(entityliving, GameEvent.DRINKING_FINISH, entityliving.eyeBlockPosition());
        return itemstack;
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 32;
    }

    @Override
    public EnumAnimation getUseAnimation(ItemStack itemstack) {
        return EnumAnimation.DRINK;
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        return ItemLiquidUtil.startUsingInstantly(world, entityhuman, enumhand);
    }

    @Override
    public String getDescriptionId(ItemStack itemstack) {
        return PotionUtil.getPotion(itemstack).getName(this.getDescriptionId() + ".effect.");
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        PotionUtil.addPotionTooltip(itemstack, list, 1.0F);
    }

    @Override
    public boolean isFoil(ItemStack itemstack) {
        return super.isFoil(itemstack) || !PotionUtil.getMobEffects(itemstack).isEmpty();
    }

    @Override
    public void fillItemCategory(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.allowdedIn(creativemodetab)) {
            Iterator iterator = IRegistry.POTION.iterator();

            while (iterator.hasNext()) {
                PotionRegistry potionregistry = (PotionRegistry) iterator.next();

                if (potionregistry != Potions.EMPTY) {
                    nonnulllist.add(PotionUtil.setPotion(new ItemStack(this), potionregistry));
                }
            }
        }

    }
}

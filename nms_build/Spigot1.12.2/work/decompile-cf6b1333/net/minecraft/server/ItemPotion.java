package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public class ItemPotion extends Item {

    public ItemPotion() {
        this.d(1);
        this.b(CreativeModeTab.k);
    }

    public ItemStack a(ItemStack itemstack, World world, EntityLiving entityliving) {
        EntityHuman entityhuman = entityliving instanceof EntityHuman ? (EntityHuman) entityliving : null;

        if (entityhuman == null || !entityhuman.abilities.canInstantlyBuild) {
            itemstack.subtract(1);
        }

        if (entityhuman instanceof EntityPlayer) {
            CriterionTriggers.y.a((EntityPlayer) entityhuman, itemstack);
        }

        if (!world.isClientSide) {
            List list = PotionUtil.getEffects(itemstack);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                if (mobeffect.getMobEffect().isInstant()) {
                    mobeffect.getMobEffect().applyInstantEffect(entityhuman, entityhuman, entityliving, mobeffect.getAmplifier(), 1.0D);
                } else {
                    entityliving.addEffect(new MobEffect(mobeffect));
                }
            }
        }

        if (entityhuman != null) {
            entityhuman.b(StatisticList.b((Item) this));
        }

        if (entityhuman == null || !entityhuman.abilities.canInstantlyBuild) {
            if (itemstack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (entityhuman != null) {
                entityhuman.inventory.pickup(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return itemstack;
    }

    public int e(ItemStack itemstack) {
        return 32;
    }

    public EnumAnimation f(ItemStack itemstack) {
        return EnumAnimation.DRINK;
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        entityhuman.c(enumhand);
        return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, entityhuman.b(enumhand));
    }

    public String b(ItemStack itemstack) {
        return LocaleI18n.get(PotionUtil.d(itemstack).b("potion.effect."));
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.a(creativemodetab)) {
            Iterator iterator = PotionRegistry.a.iterator();

            while (iterator.hasNext()) {
                PotionRegistry potionregistry = (PotionRegistry) iterator.next();

                if (potionregistry != Potions.EMPTY) {
                    nonnulllist.add(PotionUtil.a(new ItemStack(this), potionregistry));
                }
            }
        }

    }
}

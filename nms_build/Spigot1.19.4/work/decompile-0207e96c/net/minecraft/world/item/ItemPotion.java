package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
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

        entityliving.gameEvent(GameEvent.DRINK);
        return itemstack;
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getLevel();
        BlockPosition blockposition = itemactioncontext.getClickedPos();
        EntityHuman entityhuman = itemactioncontext.getPlayer();
        ItemStack itemstack = itemactioncontext.getItemInHand();
        IBlockData iblockdata = world.getBlockState(blockposition);

        if (itemactioncontext.getClickedFace() != EnumDirection.DOWN && iblockdata.is(TagsBlock.CONVERTABLE_TO_MUD) && PotionUtil.getPotion(itemstack) == Potions.WATER) {
            world.playSound((EntityHuman) null, blockposition, SoundEffects.GENERIC_SPLASH, SoundCategory.BLOCKS, 1.0F, 1.0F);
            entityhuman.setItemInHand(itemactioncontext.getHand(), ItemLiquidUtil.createFilledResult(itemstack, entityhuman, new ItemStack(Items.GLASS_BOTTLE)));
            entityhuman.awardStat(StatisticList.ITEM_USED.get(itemstack.getItem()));
            if (!world.isClientSide) {
                WorldServer worldserver = (WorldServer) world;

                for (int i = 0; i < 5; ++i) {
                    worldserver.sendParticles(Particles.SPLASH, (double) blockposition.getX() + world.random.nextDouble(), (double) (blockposition.getY() + 1), (double) blockposition.getZ() + world.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                }
            }

            world.playSound((EntityHuman) null, blockposition, SoundEffects.BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.gameEvent((Entity) null, GameEvent.FLUID_PLACE, blockposition);
            world.setBlockAndUpdate(blockposition, Blocks.MUD.defaultBlockState());
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
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
}

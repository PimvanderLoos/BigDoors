package net.minecraft.world.item;

import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityEnderSignal;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockEnderPortalFrame;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.pattern.ShapeDetector;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class ItemEnderEye extends Item {

    public ItemEnderEye(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getLevel();
        BlockPosition blockposition = itemactioncontext.getClickedPos();
        IBlockData iblockdata = world.getBlockState(blockposition);

        if (iblockdata.is(Blocks.END_PORTAL_FRAME) && !(Boolean) iblockdata.getValue(BlockEnderPortalFrame.HAS_EYE)) {
            if (world.isClientSide) {
                return EnumInteractionResult.SUCCESS;
            } else {
                IBlockData iblockdata1 = (IBlockData) iblockdata.setValue(BlockEnderPortalFrame.HAS_EYE, true);

                Block.pushEntitiesUp(iblockdata, iblockdata1, world, blockposition);
                world.setBlock(blockposition, iblockdata1, 2);
                world.updateNeighbourForOutputSignal(blockposition, Blocks.END_PORTAL_FRAME);
                itemactioncontext.getItemInHand().shrink(1);
                world.levelEvent(1503, blockposition, 0);
                ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = BlockEnderPortalFrame.getOrCreatePortalShape().find(world, blockposition);

                if (shapedetector_shapedetectorcollection != null) {
                    BlockPosition blockposition1 = shapedetector_shapedetectorcollection.getFrontTopLeft().offset(-3, 0, -3);

                    for (int i = 0; i < 3; ++i) {
                        for (int j = 0; j < 3; ++j) {
                            world.setBlock(blockposition1.offset(i, 0, j), Blocks.END_PORTAL.defaultBlockState(), 2);
                        }
                    }

                    world.globalLevelEvent(1038, blockposition1.offset(1, 0, 1), 0);
                }

                return EnumInteractionResult.CONSUME;
            }
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        MovingObjectPositionBlock movingobjectpositionblock = getPlayerPOVHitResult(world, entityhuman, RayTrace.FluidCollisionOption.NONE);

        if (movingobjectpositionblock.getType() == MovingObjectPosition.EnumMovingObjectType.BLOCK && world.getBlockState(((MovingObjectPositionBlock) movingobjectpositionblock).getBlockPos()).is(Blocks.END_PORTAL_FRAME)) {
            return InteractionResultWrapper.pass(itemstack);
        } else {
            entityhuman.startUsingItem(enumhand);
            if (world instanceof WorldServer) {
                BlockPosition blockposition = ((WorldServer) world).getChunkSource().getGenerator().findNearestMapFeature((WorldServer) world, StructureGenerator.STRONGHOLD, entityhuman.blockPosition(), 100, false);

                if (blockposition != null) {
                    EntityEnderSignal entityendersignal = new EntityEnderSignal(world, entityhuman.getX(), entityhuman.getY(0.5D), entityhuman.getZ());

                    entityendersignal.setItem(itemstack);
                    entityendersignal.signalTo(blockposition);
                    world.addFreshEntity(entityendersignal);
                    if (entityhuman instanceof EntityPlayer) {
                        CriterionTriggers.USED_ENDER_EYE.trigger((EntityPlayer) entityhuman, blockposition);
                    }

                    world.playSound((EntityHuman) null, entityhuman.getX(), entityhuman.getY(), entityhuman.getZ(), SoundEffects.ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
                    world.levelEvent((EntityHuman) null, 1003, entityhuman.blockPosition(), 0);
                    if (!entityhuman.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
                    entityhuman.swing(enumhand, true);
                    return InteractionResultWrapper.success(itemstack);
                }
            }

            return InteractionResultWrapper.consume(itemstack);
        }
    }
}

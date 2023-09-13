package net.minecraft.world.item;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.ISourceBlock;
import net.minecraft.core.dispenser.DispenseBehaviorItem;
import net.minecraft.core.dispenser.IDispenseBehavior;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockDispenser;
import net.minecraft.world.level.block.BlockMinecartTrackAbstract;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyTrackPosition;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemMinecart extends Item {

    private static final IDispenseBehavior DISPENSE_ITEM_BEHAVIOR = new DispenseBehaviorItem() {
        private final DispenseBehaviorItem defaultDispenseItemBehavior = new DispenseBehaviorItem();

        @Override
        public ItemStack execute(ISourceBlock isourceblock, ItemStack itemstack) {
            EnumDirection enumdirection = (EnumDirection) isourceblock.getBlockState().getValue(BlockDispenser.FACING);
            WorldServer worldserver = isourceblock.getLevel();
            double d0 = isourceblock.x() + (double) enumdirection.getStepX() * 1.125D;
            double d1 = Math.floor(isourceblock.y()) + (double) enumdirection.getStepY();
            double d2 = isourceblock.z() + (double) enumdirection.getStepZ() * 1.125D;
            BlockPosition blockposition = isourceblock.getPos().relative(enumdirection);
            IBlockData iblockdata = worldserver.getBlockState(blockposition);
            BlockPropertyTrackPosition blockpropertytrackposition = iblockdata.getBlock() instanceof BlockMinecartTrackAbstract ? (BlockPropertyTrackPosition) iblockdata.getValue(((BlockMinecartTrackAbstract) iblockdata.getBlock()).getShapeProperty()) : BlockPropertyTrackPosition.NORTH_SOUTH;
            double d3;

            if (iblockdata.is(TagsBlock.RAILS)) {
                if (blockpropertytrackposition.isAscending()) {
                    d3 = 0.6D;
                } else {
                    d3 = 0.1D;
                }
            } else {
                if (!iblockdata.isAir() || !worldserver.getBlockState(blockposition.below()).is(TagsBlock.RAILS)) {
                    return this.defaultDispenseItemBehavior.dispense(isourceblock, itemstack);
                }

                IBlockData iblockdata1 = worldserver.getBlockState(blockposition.below());
                BlockPropertyTrackPosition blockpropertytrackposition1 = iblockdata1.getBlock() instanceof BlockMinecartTrackAbstract ? (BlockPropertyTrackPosition) iblockdata1.getValue(((BlockMinecartTrackAbstract) iblockdata1.getBlock()).getShapeProperty()) : BlockPropertyTrackPosition.NORTH_SOUTH;

                if (enumdirection != EnumDirection.DOWN && blockpropertytrackposition1.isAscending()) {
                    d3 = -0.4D;
                } else {
                    d3 = -0.9D;
                }
            }

            EntityMinecartAbstract entityminecartabstract = EntityMinecartAbstract.createMinecart(worldserver, d0, d1 + d3, d2, ((ItemMinecart) itemstack.getItem()).type);

            if (itemstack.hasCustomHoverName()) {
                entityminecartabstract.setCustomName(itemstack.getHoverName());
            }

            worldserver.addFreshEntity(entityminecartabstract);
            itemstack.shrink(1);
            return itemstack;
        }

        @Override
        protected void playSound(ISourceBlock isourceblock) {
            isourceblock.getLevel().levelEvent(1000, isourceblock.getPos(), 0);
        }
    };
    final EntityMinecartAbstract.EnumMinecartType type;

    public ItemMinecart(EntityMinecartAbstract.EnumMinecartType entityminecartabstract_enumminecarttype, Item.Info item_info) {
        super(item_info);
        this.type = entityminecartabstract_enumminecarttype;
        BlockDispenser.registerBehavior(this, ItemMinecart.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getLevel();
        BlockPosition blockposition = itemactioncontext.getClickedPos();
        IBlockData iblockdata = world.getBlockState(blockposition);

        if (!iblockdata.is(TagsBlock.RAILS)) {
            return EnumInteractionResult.FAIL;
        } else {
            ItemStack itemstack = itemactioncontext.getItemInHand();

            if (!world.isClientSide) {
                BlockPropertyTrackPosition blockpropertytrackposition = iblockdata.getBlock() instanceof BlockMinecartTrackAbstract ? (BlockPropertyTrackPosition) iblockdata.getValue(((BlockMinecartTrackAbstract) iblockdata.getBlock()).getShapeProperty()) : BlockPropertyTrackPosition.NORTH_SOUTH;
                double d0 = 0.0D;

                if (blockpropertytrackposition.isAscending()) {
                    d0 = 0.5D;
                }

                EntityMinecartAbstract entityminecartabstract = EntityMinecartAbstract.createMinecart(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.0625D + d0, (double) blockposition.getZ() + 0.5D, this.type);

                if (itemstack.hasCustomHoverName()) {
                    entityminecartabstract.setCustomName(itemstack.getHoverName());
                }

                world.addFreshEntity(entityminecartabstract);
                world.gameEvent(GameEvent.ENTITY_PLACE, blockposition, GameEvent.a.of(itemactioncontext.getPlayer(), world.getBlockState(blockposition.below())));
            }

            itemstack.shrink(1);
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        }
    }
}

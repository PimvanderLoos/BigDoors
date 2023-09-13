package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class SolidBucketItem extends ItemBlock implements DispensibleContainerItem {

    private final SoundEffect placeSound;

    public SolidBucketItem(Block block, SoundEffect soundeffect, Item.Info item_info) {
        super(block, item_info);
        this.placeSound = soundeffect;
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        EnumInteractionResult enuminteractionresult = super.a(itemactioncontext);
        EntityHuman entityhuman = itemactioncontext.getEntity();

        if (enuminteractionresult.a() && entityhuman != null && !entityhuman.isCreative()) {
            EnumHand enumhand = itemactioncontext.getHand();

            entityhuman.a(enumhand, Items.BUCKET.createItemStack());
        }

        return enuminteractionresult;
    }

    @Override
    public String getName() {
        return this.p();
    }

    @Override
    protected SoundEffect a(IBlockData iblockdata) {
        return this.placeSound;
    }

    @Override
    public boolean a(@Nullable EntityHuman entityhuman, World world, BlockPosition blockposition, @Nullable MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isValidLocation(blockposition) && world.isEmpty(blockposition)) {
            if (!world.isClientSide) {
                world.setTypeAndData(blockposition, this.getBlock().getBlockData(), 3);
            }

            world.playSound(entityhuman, blockposition, this.placeSound, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }
}

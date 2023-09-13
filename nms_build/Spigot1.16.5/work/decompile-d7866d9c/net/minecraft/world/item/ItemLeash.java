package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.decoration.EntityLeash;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AxisAlignedBB;

public class ItemLeash extends Item {

    public ItemLeash(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        Block block = world.getType(blockposition).getBlock();

        if (block.a((Tag) TagsBlock.FENCES)) {
            EntityHuman entityhuman = itemactioncontext.getEntity();

            if (!world.isClientSide && entityhuman != null) {
                a(entityhuman, world, blockposition);
            }

            return EnumInteractionResult.a(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    public static EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition) {
        EntityLeash entityleash = null;
        boolean flag = false;
        double d0 = 7.0D;
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        List<EntityInsentient> list = world.a(EntityInsentient.class, new AxisAlignedBB((double) i - 7.0D, (double) j - 7.0D, (double) k - 7.0D, (double) i + 7.0D, (double) j + 7.0D, (double) k + 7.0D));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityInsentient entityinsentient = (EntityInsentient) iterator.next();

            if (entityinsentient.getLeashHolder() == entityhuman) {
                if (entityleash == null) {
                    entityleash = EntityLeash.a(world, blockposition);
                }

                entityinsentient.setLeashHolder(entityleash, true);
                flag = true;
            }
        }

        return flag ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS;
    }
}
